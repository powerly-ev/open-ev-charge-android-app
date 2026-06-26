# Application Architecture — Open Powerly Android

This document describes the architecture of **`open-powerly-android`**, the Android client of the **Powerly** open-source EV charging ecosystem. The architecture is designed to keep the application **scalable**, **modular**, **testable**, and **maintainable** as it grows.

---

## 1. Architectural Overview

The project follows the **MVVM (Model–View–ViewModel)** pattern, guided by **Clean Architecture** principles and a clear separation of concerns. It uses a **single-activity architecture**: one host `Activity` owns navigation and lifecycle, while every screen is a **Jetpack Compose** destination.

### Layered Architecture

| Layer | Responsibility |
| --- | --- |
| **UI (View)** | Composable screens that render state and forward user interactions. They contain no business logic and delegate to ViewModels. |
| **ViewModel** | Holds and exposes UI state via `StateFlow`, handles UI events, and coordinates use cases and repositories. |
| **Domain** | Pure business logic expressed as use cases and domain entities. Independent of any framework or data source. |
| **Data** | Repository implementations backed by data sources (network, database, preferences). Exposes a clean API to the layers above and hides implementation details. |

Each layer has a single responsibility, depends only on the layer beneath it, and can be tested in isolation.

---

## 2. Module Structure

The application is split into Gradle modules, each owning a well-defined part of the system. Feature modules depend only on `core` and `common` — never on one another — and the `app` module wires everything together.

```
open-powerly-android
├── app                     # Single Activity host, navigation graph, DI bootstrap
│
├── feature
│   ├── splash              # Launch & initial routing
│   ├── user               # Authentication and onboarding
│   ├── payment            # Payments and Stripe integration
│   ├── vehicles           # Vehicle management
│   ├── main               # Bottom-navigation shell
│   │   ├── home           # Map-based charger discovery
│   │   ├── scan           # QR scanning for chargers
│   │   ├── orders         # Charging history & billing
│   │   └── account        # Account and profile management
│   └── power-source
│       ├── details        # Power-source / charger details
│       └── charging       # Charging session control
│
├── core
│   ├── domain             # Use cases and domain entities (business rules)
│   ├── data               # Repository implementations & data-source contracts
│   ├── network            # Ktor setup, DTOs, API services
│   ├── database           # Room database, entities, and DAOs
│   ├── managers           # Cross-cutting managers (storage, device, app state)
│   └── analytics          # Analytics abstraction
│       └── impl           # Concrete analytics implementation (Firebase)
│
├── common
│   ├── navigation         # Shared navigation routes & contracts
│   ├── ui                 # Reusable Jetpack Compose components
│   ├── resources          # Themes, strings, drawables, dimensions
│   └── testing            # Test utilities, fixtures, and base classes
│
├── build-logic            # Gradle convention plugins (see §6)
└── benchmark              # Macrobenchmark & profiling for critical flows
```

### App Module

- **`app`** — the entry point. Hosts the main `Activity`, owns the top-level navigation graph, initializes the dependency graph, and holds global configuration.

### Feature Modules

Feature modules encapsulate user-facing functionality and apply MVVM internally. Because they are isolated from each other, features can evolve independently and be developed in parallel.

### Core Modules

Core modules provide the business and infrastructure foundation shared across features — the domain layer (`core:domain`), data layer (`core:data`), and the platform integrations they rely on (`network`, `database`, `managers`, `analytics`).

### Common Modules

Common modules host the shared presentation and tooling layer: navigation contracts, reusable UI components, design resources, and testing infrastructure.

### Benchmark Module

- **`benchmark`** — Macrobenchmarks and profiling for performance-critical app flows (e.g. startup and navigation).

---

## 3. Dependency Management

- **Dependency Injection** — [Koin](https://insert-koin.io/) provides the DI graph. Modules declare their own definitions, which are injected into ViewModels, repositories, and use cases.
- **Module Dependencies** — Feature modules depend only on `core` and `common` modules, never on each other, preserving loose coupling. The `app` module composes the full graph.
- **Version Catalog** — Dependencies and versions are centralized in a Gradle [version catalog](https://docs.gradle.org/current/userguide/version_catalogs.html) (`gradle/libs.versions.toml`).

---

## 4. Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture Components:** ViewModel, StateFlow, Room, Navigation
- **Networking:** Ktor
- **Asynchrony:** Kotlin Coroutines & Flow
- **Dependency Injection:** [Koin](https://insert-koin.io/)
- **Image Loading:** [Coil](https://coil-kt.github.io/coil/)

**Testing:**

- Unit — [JUnit 5](https://junit.org/junit5/), [MockK](https://mockk.io/), [Turbine](https://github.com/cashapp/turbine)
- UI — [Espresso](https://developer.android.com/training/testing/espresso), Compose Test APIs
- E2E — on-device journeys with fake repositories, installed via a custom instrumentation runner
- QA — [Maestro](https://maestro.mobile.dev/) black-box flows

### Running the Test Suites

Tasks are flavor-qualified (`default` / `gms` — see §5). Replace `Default` with `Gms` to target the GMS flavor.

```bash
# Unit tests (JVM, no device) — all modules
./gradlew testDefaultDebugUnitTest

# Instrumented + Compose UI + on-device E2E (needs a device/emulator)
./gradlew connectedDefaultDebugAndroidTest
```

**Maestro QA flows** live in `.maestro/` and run against the installed app (`appId: com.powerly.open.test`), independent of Gradle. Install the [Maestro CLI](https://maestro.mobile.dev/getting-started/installing-maestro), then with a device/emulator connected and the app installed:

```bash
# Run the whole suite in the configured order (smoke → login → balance → vehicles → profile)
maestro test .maestro/

# Run a single flow
maestro test .maestro/login.yaml
```

---

## 5. Build Variants & Product Flavors

The app uses Gradle's variant system to support multiple build configurations.

### Build Types

- **`debug`** — Development builds with debug symbols and verbose logging.
- **`release`** — Production builds with code shrinking (R8/ProGuard), signing, and optimizations.

### Product Flavors

Flavors are defined on the `contentType` dimension:

- **`default`** — Base build without Firebase / Google services.
- **`gms`** — Google Mobile Services build, adding Firebase Analytics, Cloud Messaging, and Crashlytics.

> `gms` builds require a `google-services.json` under `app/src/gms/`.

**Resulting variants:**

- `defaultDebug`
- `gmsDebug`
- `defaultRelease`
- `gmsRelease`
- `gmsPreRelease` — production API, debuggable

---

## 6. Build Logic (`build-logic`)

The included `build-logic` build centralizes Gradle configuration through Kotlin DSL **convention plugins**, keeping module build scripts DRY and type-safe. It provides shared conventions for:

- Android application & library modules
- Kotlin JVM modules
- Jetpack Compose setup
- Product flavors (see §5)
- Feature and core module conventions

---

## 7. CI/CD with Fastlane

[Fastlane](https://fastlane.tools/) automates the build, test, and release pipelines:

- **Build Automation** — Generates debug and release builds across flavors.
- **Testing** — Runs unit, instrumentation, and UI tests.
- **Code Signing** — Manages signing keys and provisioning.
- **Distribution** — Publishes builds to the configured distribution channels.

---

## 8. Future-Proofing & Best Practices

- Keep features **modularized** to enable parallel development and on-demand delivery.
- Keep business logic in **`core:domain`**, isolated from frameworks and data sources.
- Centralize dependencies through the **Gradle version catalog**.
- Treat module boundaries as contracts — depend on abstractions, not implementations.

---

## Final Note

This document is a **living resource** — keep it in sync as the project evolves. Its purpose is to guide contributors and keep the codebase clean, scalable, and ready to power the future of electric mobility.
