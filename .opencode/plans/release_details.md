# Release Details Screen

## Purpose
Displays full details of a record release including artist, album, year, genres, styles, cover art, and share functionality.

## Data Model
- **API**: `/releases/{id}` from https://api.discogs.com/markets/release
- **Domain**: `ReleaseDetailsWithFavorite` class
- **State**: `ReleaseDetailsScreenState`

## UI Components
- **Scaffold**: Wraps the entire screen with a top bar
- **TopBar**: Back arrow and "Release Details" title
- **CoverArtSection**: LazyRow for cover images
- **TracklistSection**: Expandable track list with LazyColumn
- **MarketplaceSection**: Prices and condition from marketplace

## Navigation
- Accessed from a `Composable` that accepts `releaseId: String`
- Passes `releaseId` to NavHost
- Supports deep linking via `https://api.discogs.com/release/{id}`

## Key Logic
- `ReleaseDetailsViewModel` handles data loading, favorites, and state
- `ReleasesUseCase` is called immediately in the `init` block
- Favorite status is toggled via `onToggleFavorite` callback
- Share flow uses native share intent (not yet implemented)

## Notes
- The release details screen follows the same architectural pattern as releases, search, and favorites modules
- Uses **Hilt** for DI, **Ktor** for API, **Coil** for images, **Compose** for UI
- Room and Coroutines for caching and async operations
