# Ghaima (غيمة) - Weather App

Ghaima is a comprehensive Android weather application that provides real-time weather updates, forecasts, and alerts. Built with modern Android development practices, it offers a seamless and intuitive user experience for monitoring weather conditions across various locations.

## Features

- **Real-time Weather:** Get current weather conditions for your current location or any selected city.
- **Detailed Forecasts:** View hourly and daily forecasts to plan your day and week.
- **Map Integration:** Select locations directly from an interactive map.
- **Saved Locations:** Manage a list of favorite locations for quick access.
- **Weather Alerts & Alarms:** Stay informed with timely notifications and custom weather-based alarms.
- **Customizable Settings:** Adjust units (Celsius/Fahrenheit), language, and notification preferences.
- **Onboarding:** A smooth introduction to the app's core features.

## Architecture

The project follows the **MVVM (Model-View-ViewModel)** architectural pattern and is organized into layers to ensure scalability, maintainability, and testability:

- **Presentation Layer:** Uses Jetpack Compose for building the UI and ViewModels to manage state and handle user interactions.
- **Data Layer:** Handles data retrieval from both local (Room) and remote (Retrofit) sources. It includes repositories that abstract the data source from the rest of the app.
- **Dependency Injection:** Koin is used as the DI framework to manage component dependencies.
- **Background Tasks:** WorkManager is utilized for periodic weather updates and alert management.

## Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Dependency Injection:** [Koin](https://insert-koin.io/)
- **Networking:** [Retrofit](https://square.github.io/retrofit/) & [Gson](https://github.com/google/gson)
- **Local Database:** [Room](https://developer.android.com/training/data-storage/room)
- **Image/Animation:** [Lottie](https://github.com/airbnb/lottie-android)
- **Location & Maps:** Google Play Services Location & Google Maps Compose
- **Concurrency:** Kotlin Coroutines & Flow
- **Navigation:** Compose Navigation

## Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/Ghaima.git
   ```

2. **API Keys:**
   The app requires API keys from OpenWeather and Google Maps.
   - Create a `local.properties` file in the root directory.
   - Add your keys:
     ```properties
     OPEN_WEATHER_API_KEY=your_open_weather_api_key
     MAPS_API_KEY=your_google_maps_api_key
     ```

3. **Build the project:**
   Open the project in Android Studio and sync the Gradle files.

## Testing

The project includes unit tests for ViewModels and data sources using:
- JUnit 4
- MockK
- Kotlinx Coroutines Test
- AndroidX Test libraries

---
*Developed as part of the ITI Android Kotlin Course.*
