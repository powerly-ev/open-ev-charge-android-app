# EV Charging App (Android Kotlin)

Welcome to the **EV Charging App**, an open-source Android application developed using Kotlin and Jetpack components for discovering and managing electric vehicle (EV) charging stations. This application is white-label ready, allowing businesses to brand, customize, and configure backend services (e.g., Firebase) for deployment on Google Play.

## Features
- **Charger Locator**: Interactive map displaying nearby EV charging stations.
- **User Authentication**: Sign in/out functionality with email via Firebase Authentication.
- **Station Details**: Real-time charger availability, charger type, and pricing information.
- **White-Label Ready**: Easily customizable UI components, colors, and branding elements.

## Prerequisites
- **Android Studio**: Version Flamingo (2023.2.1) or later.
- **Android SDK**: Minimum SDK version 23 (Android 6.0).
- **Firebase Account**: Required for authentication and analytics.
- **Gradle**: Project dependency management (included with Android Studio).

## Getting Started

### 1. Clone the Repository
```bash
git clone [<your-repo-url>](https://github.com/powerly-ev/open-ev-charge-android-app.git)
cd <your-repo-name>
```

### 2. Open Project in Android Studio
- Launch Android Studio.
- Select "Open an Existing Project."
- Navigate to your cloned project directory and open it.

### 3. Configure Firebase
1. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com).
2. Download the `google-services.json` file.
3. Place it in the `app/` directory of your Android project.
4. Enable Firebase services (e.g., Authentication, Firestore) via Firebase console.
5. Ensure Firebase dependencies are added in your project's `build.gradle` files:

```gradle
// Project-level build.gradle
classpath 'com.google.gms:google-services:4.4.2'

// Module-level build.gradle
plugins {
    id 'com.google.gms.google-services'
}
```

### 4. White-Labeling
Customize the app for your business by:
- **Branding**: Replace logos and icons in the `app/src/main/res` folder.
- **Colors**: Update `colors.xml` in `app/src/main/res/values`.
- **Text**: Modify `strings.xml` in `app/src/main/res/values`.
- **Package ID**: Update `applicationId` in `app/build.gradle` to match your Google Play account.

### 5. Build and Run
- Connect a physical Android device or configure an emulator.
- Press the "Run" button (Shift + F10) to build and deploy the app.

## Contributing
We encourage contributions! Follow these steps:
1. Fork the repository.
2. Create a new feature branch (`git checkout -b feature/<your-feature>`).
3. Commit your changes clearly following the [commit guidelines](#commit-message-guidelines).
4. Push to your fork (`git push origin feature/<your-feature>`).
5. Submit a pull request.

### Commit Message Guidelines
Format commits using `<type>(<scope>): <description>`. Examples:
- `feat(map): add filtering of chargers by type`
- `fix(firebase): handle authentication exceptions properly`

## White-Labeling Guide
Businesses can leverage this app as a foundation for branded EV charging solutions:
- **Firebase Setup**: Provide your `google-services.json`.
- **API Keys**: Set your third-party APIs (e.g., Google Maps API) in `local.properties`.
- **Google Play**: Update your signing keys and deploy under your Google Play account.
- **Analytics**: Use Firebase Analytics or integrate another analytics provider.

## License Information
This project is distributed under a [dual-license model](LICENSE):
1. **Apache 2.0 License** (open-source usage):
   - Use, modify, distribute freely under Apache 2.0.
   - Mandatory removal of original branding (name, logo, API keys) when forking or modifying.
   - Read the complete [Apache 2.0 License](LICENSE).

2. **Enterprise Commercial License** (business and premium usage):
   - Premium features, advanced analytics, priority support require a paid subscription.
   - Explicit approval required for commercial backend use.
   - For Enterprise License inquiries, contact [sales@ypowerly.app](mailto:sales@powerly.app).

## Support
For queries or issues:
- Open a GitHub issue.
- Reach out to maintainers at dev@powerly.app.

