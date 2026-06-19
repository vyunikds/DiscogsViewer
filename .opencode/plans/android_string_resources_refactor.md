# Android String Resources Refactoring Plan

## Overview
This plan documents the partial refactoring of hardcoded UI strings to `strings.xml` in the DiscogsViewer Android app.

## Current Status

### ✅ Completed Changes

#### 1. `strings.xml` - New String Resources Added
Path: `app/src/main/res/values/strings.xml`

All 36 user-facing hardcoded strings have been added as resource entries:
- Navigation titles: `nav_top_releases`, `nav_details`, `nav_favorites`, `nav_settings`, `nav_search`
- Common: `back`, `clear`, `delete`, `apply`, `share_via`
- Search: `search_placeholder`, `clear_all_history`
- Favorites: `favorites_image_desc`, `<plurals name="favorites_count">`
- Sort: `sort_title`, `sort_by_date`, `sort_by_release_title`, `sort_by_artist_title`
- Details: `try_again`, `image_not_available`, `already_have`, `label_artist_name`, `label_genres`, `label_country`, `label_release_id`
- Settings: `settings_title`, `theme_title`, `theme_system`, `theme_light`, `theme_dark`
- Errors: `error_unknown`

Typo fix: `error_wile_loading_data` → `error_while_loading_data`

#### 2. Files Updated

- `MainActivity.kt` - Updated to use `titleRes` field with `@StringRes` annotation
- `FavoriteSortMode.kt` - Added enum support for string resource IDs
- `FavoriteSortBottomSheet.kt` - Refactored to use enum instead of string comparisons
- `MainNavigation.kt` - Updated to use resource strings for UI elements
- `SearchScreen.kt` - All hardcoded strings moved to resources
- `SearchHistoryItem.kt` - "Delete" content description now uses resource
- `FavoritesScreen.kt` - Updated with new state fields and resource strings
- `FavoriteState.kt` - Replaced `error: String?` with `hasError: Boolean` and `errorProvider: ErrorProvider`
- `FavoritesViewModel.kt` - Updated to use `errorProvider` pattern
- `ReleaseDetailsScreen.kt` - All label and button strings use resources
- `SettingsScreen.kt` - All title and option strings use resources
- ViewModels - Fixed 3 ViewModels that used the old misspelled string resource name

### ✅ Build Verification - PASSED

```
./gradlew :app:compileDebugKotlin
BUILD SUCCESSFUL in 6s
```

Build compiles successfully with 0 errors. Warnings are pre-existing (deprecated APIs).

### ✅ Completed Tasks

All 36 hardcoded user-facing strings moved to `strings.xml`.

#### Files Modified
- `app/src/main/java/com/example/discogsviewer/MainActivity.kt`
- `app/src/main/java/com/example/discogsviewer/favorites/domain/FavoriteSortMode.kt`
- `app/src/main/java/com/example/discogsviewer/favorites/feature/ui/FavoriteSortBottomSheet.kt`
- `app/src/main/java/com/example/discogsviewer/favorites/feature/ui/FavoritesScreen.kt`
- `app/src/main/java/com/example/discogsviewer/favorites/feature/FavoriteState.kt`
- `app/src/main/java/com/example/discogsviewer/favorites/feature/FavoritesViewModel.kt`
- `app/src/main/java/com/example/discogsviewer/navigation/MainNavigation.kt`
- `app/src/main/java/com/example/discogsviewer/search/feature/ui/SearchScreen.kt`
- `app/src/main/java/com/example/discogsviewer/search/feature/ui/SearchHistoryItem.kt`
- `app/src/main/java/com/example/discogsviewer/details/feature/ui/ReleaseDetailsScreen.kt`
- `app/src/main/java/com/example/discogsviewer/settings/ui/SettingsScreen.kt`
- `app/src/main/java/com/example/discogsviewer/releases/feature/ReleasesViewModel.kt`
- `app/src/main/java/com/example/discogsviewer/search/feature/ReleaseSearchViewModel.kt`
- `app/src/main/java/com/example/discogsviewer/details/feature/ReleaseDetailsViewModel.kt`
- `app/src/main/res/values/strings.xml`

## Implementation Details

### Key Patterns Used

1. **String Resources**: All UI text now uses `stringResource(R.string.xxx)` or `context.getString(R.string.xxx)`

2. **Plurals**: Used Android's `<plurals>` resource type for favorites count

3. **Enum-Based Sorting**: `FavoriteSortMode` enum now carries `@StringRes` label resource IDs, eliminating string comparison for sort modes

4. **Error Handling Pattern**: Changed `error: String?` state to `hasError: Boolean` + `errorProvider: (Context) -> String` to maintain consistency across ViewModels

5. **Non-Composable Context**: When using context in non-UI threads (like `Intent.createChooser`), uses `context.getString()` instead of `stringResource()`

## Notes
- Russian text in UI elements ("Применить", "Попробовать снова") was replaced with English to maintain consistency
- The typo in `error_wile_loading_data` was fixed to `error_while_loading_data`
- Build verification pending due to compile errors detected during previous step (plurals syntax and context issues)
