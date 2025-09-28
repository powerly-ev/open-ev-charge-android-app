# 📐 Application Architecture — Open Powerly Android

This document provides a detailed overview of the architecture for the **`open-powerly-android`** application — part of the **Powerly** open-source EV charging ecosystem.  
It is designed to ensure the app remains **scalable**, **modular**, **testable**, and **maintainable** as it evolves.

---

## 🏛️ 1. Architectural Overview

The project follows the **MVVM (Model–View–ViewModel)** pattern, strongly inspired by **Clean Architecture** principles. It uses a **Single Activity Architecture**, where a single host `Activity` manages the app’s navigation and lifecycle, while individual screens are implemented as **Jetpack Compose destinations**.

### 🧱 Layered Architecture

- **UI Layer (View):**
    - Composable screens responsible for rendering data and handling user interaction.
    - They delegate business logic and state management to ViewModels.

- **ViewModel Layer:**
    - Holds and exposes UI state using `StateFlow`.
    - Handles UI events and communicates with Use Cases or Repositories.

- **Domain Layer (Recommended):**
    - Encapsulates core business logic in **Use Cases** (a.k.a. Interactors).
    - Decouples ViewModels from data sources and enforces clear business rules.

- **Data Layer:**
    - Composed of **Repositories** and **Data Sources** (network, database, shared preferences).
    - Provides a unified API to the domain layer, abstracting away implementation details.

This layered approach ensures each component has a single responsibility, improves testability, and allows the project to scale without breaking existing functionality.

---

## 🧩 2. Modules & Structure

The application is modularized into several Gradle modules, each responsible for a well-defined part of the system.

### 📱 App Module

- **`app`** – The entry point of the application.
    - Hosts the main Activity and orchestrates navigation between feature modules.
    - Provides dependency graph initialization and global configuration.

---

### 🌟 Feature Modules

Feature modules encapsulate user-facing functionality. Each is self-contained and follows MVVM internally:

- `feature.main` – Main app flow
- `feature.main.home` – Home and map-based charger discovery
- `feature.main.account` – Account management and profile
- `feature.main.orders` – Charging history and billing
- `feature.main.scan` – QR scanning for chargers
- `feature.payment` – Payments and Stripe integration
- `feature.power-source` – Power source management
- `feature.power-source.charge` – Session control and charging logic
- `feature.user` – User authentication and onboarding

🧪 Each feature can evolve independently, minimizing coupling and enabling parallel development.

---

### ⚙️ Core Modules

Core modules provide shared functionality and infrastructure used across features:

- **`core.data`** – Repository implementations, data source contracts, and domain models.
- **`core.database`** – Room database setup, entities, and DAOs.
- **`core.network`** – Retrofit setup, DTOs, and API service definitions.
- **`core.model`** – Shared data models.

---

### 🧰 Common Modules

These modules house utilities, shared resources, reusable UI components, and testing infrastructure:

- `common.lib` – Extension functions, constants, and utility classes
- `common.resources` – Shared themes, strings, drawables, and dimensions
- `common.ui` – Reusable Jetpack Compose components
- `common.testing` – Test utilities, base test classes, and custom rules

---

### ⚡ Benchmark Module

- **`benchmark`** – Contains performance benchmarks and profiling tools for critical app flows.

---

## 🪄 3. Dependency Management

- **Dependency Injection:**
    - [Koin](https://insert-koin.io/) powers dependency injection. Modules declare their dependencies, which are injected into ViewModels, Repositories, and Use Cases.

- **Gradle Module Dependencies:**
    - Feature modules depend only on `core` and `common` modules — **never on each other**, to maintain loose coupling.
    - The `app` module ties everything together.

---

## 🛠️ 4. Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture Components:**
    - ViewModel, StateFlow, Room, Navigation Component
- **Networking:** [Retrofit](https://square.github.io/retrofit/)
- **Async:** Kotlin Coroutines
- **DI:** [Koin](https://insert-koin.io/)
- **Image Loading:** [Coil](https://coil-kt.github.io/coil/) 

**Testing:**

- Unit Tests – [JUnit5](https://junit.org/junit5/), [MockK](https://mockk.io/), Mockito
- UI Tests – [Espresso](https://developer.android.com/training/testing/espresso), Compose Test APIs
---

## 🧪 5. Build Variants & Product Flavors

The app leverages Gradle’s variant system to support multiple build configurations.

### Build Types

- **`debug`** – Development builds with debug symbols and verbose logging.
- **`release`** – Production builds with code shrinker (R8/ProGuard), signing, and optimizations.

### Product Flavors

- **`default`** – Base build without Firebase dependencies.
- **`gms`** – Google Mobile Services-enabled build:
    - Firebase Analytics
    - Firebase Cloud Messaging
    - Crashlytics

⚠️ `gms` builds require a `google-services.json` in `app/src/gms/`.

**Resulting variants:**
- `defaultDebug`
- `gmsDebug`
- `defaultRelease`
- `gmsRelease`
- `gmsPreRelease` (production API + debuggable)

---

## 🏗️ 6. Build Logic (`build-logic`)

The `build-logic` module centralizes Gradle configurations, plugin definitions, and dependency versions. This approach:

- Keeps module build files DRY
- Improves maintainability
- Ensures type safety using Kotlin DSL
- Provides shared convention plugins for:
    - Android application & library
    - Kotlin JVM
    - Jetpack Compose setup
    - Dependency management
    - Feature and core module conventions

---

## 🚀 7. CI/CD with Fastlane

[Fastlane](https://fastlane.tools/) powers automation for build, test, and release pipelines:

- **Build Automation:** Generates debug and release builds across flavors.
- **Testing:** Runs unit, instrumentation, and UI tests automatically.
- **Code Signing:** Manages signing keys and provisioning.
- **Distribution:**
---

## 🧭 8. Future-Proofing & Best Practices

- Emphasize **feature modularization** to enable on-demand delivery and parallel development.
- Adopt **domain-driven design (DDD)** where appropriate to further isolate business logic.
- Use **Gradle version catalogs** for centralized dependency management.

---

## 🧾 Final Note

This architecture document is a **living resource** — update it as the project evolves.  
Its purpose is to guide contributors and ensure the project stays clean, scalable, and future-ready as it powers the future of electric mobility.

