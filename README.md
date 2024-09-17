# Modern Movie App

## Overview

This Android app interacts with The Movie Database (TMDB) API. Every line of code has been generated
or suggested by AI using a large language model.

## Current state

the codebase evolves as the AI generates more and more code.

![p5.png](docs%2Fassets%2Fp5.png)

## Setup

1. Get a TMDB API key from [themoviedb.org](https://www.themoviedb.org/).
2. Add to `gradle.properties`:
   ```
   TMDB_API_KEY="your_api_key_here"
   ```
3. Sync project with Gradle files.

## Key Features

1. Browse movies by different categories (popular, top-rated, etc.)
2. Search for movies
3. View detailed movie information
4. Mark movies as favorites
5. Create custom movie lists (authenticated users only)
6. Switch between grid and list views
7. Apply filters (genre, release year, minimum rating)
8. Toggle between light and dark themes

## Architecture

The app follows the MVVM (Model-View-ViewModel) architecture:

- Model: Represented by the `Movie` and `MovieResponse` data classes
- View: Compose UI components in various screen files
- ViewModel: `MovieViewModel` manages the app's state and business logic

## Data Flow

1. The `MovieViewModel` fetches data from the `MovieRepository`
2. The repository makes API calls using the `ApiService`
3. Data is then exposed to the UI components via StateFlows in the ViewModel
4. UI components observe these flows and recompose when the data changes

## Authentication Flow

1. User initiates authentication from the ListCreationScreen
2. App obtains a request token from the TMDB API
3. User approves the request token via a web browser
4. App exchanges the approved token for a session ID
5. Session ID is stored for future authenticated requests

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: Retrofit with OkHttp
- **JSON Parsing**: Gson
- **Image Loading**: Coil
- **Asynchronous Programming**: Kotlin Coroutines
- **Navigation**: Jetpack Navigation Compose
- **Dependency Injection**: Manual (no DI framework used yet)
- **UI Components**: Material Design 3
- **Build System**: Gradle with Kotlin DSL
- **Minimum SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)