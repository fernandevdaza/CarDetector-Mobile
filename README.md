# Car Detector Mobile

Car Detector Mobile is an Android application built with Jetpack Compose that uses AI to detect and identify car models from images. The app allows users to capture or upload photos of vehicles and receive detailed information about the car's brand, model, and year.

## Features

- 🚗 **Car Detection**: Upload or capture photos of vehicles to identify brand, model, and year
- 📸 **Camera Integration**: Take photos directly within the app using your device's camera
- 🗺️ **Location Tracking**: Automatically captures GPS coordinates (latitude/longitude) with each detection
- 📜 **Detection History**: View past detections stored locally with Room Database
- 👤 **User Authentication**: Secure login and registration system
- 📊 **User Profile Management**: View and update user information
- 🌓 **Dark Mode Support**: Toggle between light and dark themes
- 🗺️ **Map Visualization**: View detection locations on an interactive map using OpenStreetMap

## Tech Stack


### Android
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit for native Android
- **Material Design 3**: Latest Material Design components

### Architecture & Libraries
- **MVVM Architecture**: ViewModel-based architecture pattern
- **Room Database**: Local data persistence for detection history
- **Retrofit**: HTTP client for API communication
- **OkHttp**: HTTP & HTTP/2 client
- **Gson**: JSON serialization/deserialization
- **Coroutines**: Asynchronous programming
- **Navigation Component**: In-app navigation
- **Coil**: Image loading library
- **OSMDroid**: OpenStreetMap Android library for map visualization
- **Dependency Injection**: Custom AppContainer for dependency management

## Prerequisites

- Android Studio Ladybug or later
- Android SDK 36 (compileSdk)
- Minimum Android SDK 24 (Android 7.0 Nougat)
- JDK 11 or later
- Gradle 8.x

## Installation

1. Clone the repository:
```bash
git clone https://github.com/fernandevdaza/CarDetector-Mobile.git
cd CarDetector-Mobile
```

2. Open the project in Android Studio

3. Sync Gradle files by clicking "Sync Now" when prompted

4. Configure the backend API endpoint in `RetrofitClient.kt` if needed

5. Build and run the application on an emulator or physical device

## Permissions

The app requires the following permissions:

- **INTERNET**: Network communication with backend API
- **ACCESS_NETWORK_STATE**: Check network connectivity
- **CAMERA**: Capture photos of vehicles
- **ACCESS_FINE_LOCATION**: Get precise GPS coordinates
- **ACCESS_COARSE_LOCATION**: Get approximate GPS coordinates
- **READ_MEDIA_IMAGES**: Access photos from gallery (Android 13+)
- **READ_EXTERNAL_STORAGE**: Access photos from storage (Android 12 and below)
- **ACCESS_MEDIA_LOCATION**: Extract location metadata from images

## Usage

### First Time Setup
1. Launch the app and go through the onboarding screens
2. Register a new account or log in with existing credentials
3. Grant necessary permissions when prompted

### Detecting a Car
1. Navigate to the Detection screen
2. Choose to either:
   - Take a new photo with the camera
   - Select an existing photo from your gallery
3. The app will process the image and return detection results including:
   - Car brand
   - Model name
   - Year
   - Location (if available)

### Viewing History
1. Navigate to the History/Activity screen
2. Browse through past detections
3. Click on any detection to view details

### Profile Management
1. Navigate to the Profile screen
2. View your account information
3. Update profile details if needed
4. Toggle dark mode
5. Log out when needed

## Project Structure

```
app/src/main/java/com/example/cardetectormobile/
├── MainActivity.kt                    # Main entry point
├── data/
│   ├── local/                        # Local data storage
│   │   ├── dao/                      # Room DAOs
│   │   ├── entity/                   # Room entities
│   │   ├── DetectionDatabase.kt      # Database configuration
│   │   └── SessionManager.kt         # Session & preferences management
│   ├── model/                        # Data models
│   │   ├── AuthModels.kt
│   │   └── DetectionModels.kt
│   └── network/                      # Network layer
│       ├── ApiService.kt             # API endpoints
│       ├── RetrofitClient.kt         # Retrofit configuration
│       └── TokenAuthenticator.kt     # Token refresh handler
├── di/
│   └── AppContainer.kt               # Dependency injection container
├── domain/
│   └── repository/                   # Repository interfaces
├── ui/
│   ├── components/                   # Reusable UI components
│   ├── navigation/                   # Navigation configuration
│   ├── screens/                      # App screens
│   ├── theme/                        # App theming
│   └── viewmodel/                    # ViewModels
└── utils/                            # Utility classes
```

## API Endpoints

The app communicates with a backend API for the following operations:

- **POST /auth/token**: User login
- **POST /auth/**: User registration
- **PUT /auth/me**: Update user data
- **POST /auth/refresh**: Refresh authentication token
- **DELETE /auth/me**: Delete user account
- **POST /inference/car-with-image**: Vehicle detection from image

## Configuration

### Backend API
Update the base URL in `RetrofitClient.kt` to point to your backend server:

```kotlin
private const val BASE_URL = "your-api-endpoint-here"
```

## Building for Production

To build a release APK:

```bash
./gradlew assembleRelease
```

The APK will be generated in `app/build/outputs/apk/release/`

## Development

### Running Tests
```bash
./gradlew test                    # Run unit tests
./gradlew connectedAndroidTest    # Run instrumentation tests
```

### Code Style
The project follows Kotlin coding conventions and uses Android best practices.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is available under the [MIT License](LICENSE)


---

**Note**: Make sure to configure the backend API endpoint before running the application. The app requires a compatible backend server to function properly.
