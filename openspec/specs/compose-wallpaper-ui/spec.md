# compose-wallpaper-ui Specification

## Purpose

Provide a modern Material 3 interface for browsing themes, previewing the wallpaper live, and applying it as the system wallpaper.

## Requirements

### Requirement: Material 3 interface

The system SHALL present a clean, modern interface using Jetpack Compose and Material 3, with dark/dynamic theming support.

#### Scenario: App launches
- **WHEN** the user opens the Wave app
- **THEN** a Material 3 interface is displayed with theme selector and preview

### Requirement: Live preview

The system SHALL embed a GLSurfaceView inside Compose using AndroidView, allowing real-time interaction with waves before applying.

#### Scenario: Preview responds to touch
- **WHEN** the user touches the preview area
- **THEN** waves respond identically to the actual wallpaper

### Requirement: Theme selector

The system SHALL display all 16 themes in a grid/carousel with thumbnail previews and names, allowing selection.

#### Scenario: Select theme
- **WHEN** the user taps a theme thumbnail
- **THEN** the preview updates to show that theme

### Requirement: Apply wallpaper

The system SHALL provide a button that opens the system wallpaper picker (`WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER`) to apply the selected live wallpaper.

#### Scenario: Apply button
- **WHEN** the user taps "Apply"
- **THEN** the system wallpaper picker opens with Wave pre-selected

### Requirement: Theme persistence in UI

The system SHALL save the selected theme and restore it when the app reopens.

#### Scenario: Restore selection
- **WHEN** the app is reopened
- **THEN** the previously selected theme is highlighted
