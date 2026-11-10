# Spec Delta

## Purpose

Parse and manage theme files that define colors and wave parameters, enabling 16 distinct visual themes plus smooth transitions.

## ADDED Requirements

### Requirement: Theme file parsing

The system SHALL parse `.theme` files containing key-value pairs for colors (bgc1-4, lc1-6), wave ranges (amplitude, frequency, width, speed, offset, bend), noise scale, and line count.

#### Scenario: Parse valid theme
- **WHEN** a valid `.theme` file is loaded
- **THEN** all parameters are extracted into a typed `WaveTheme` object

#### Scenario: Missing parameters
- **WHEN** a theme file is missing some parameters
- **THEN** safe default values are used for missing entries

### Requirement: Sixteen built-in themes

The system SHALL include 16 pre-defined themes (lwp.0 through lwp.15) with distinct color palettes matching the original Sony Xperia themes.

#### Scenario: Theme selection
- **WHEN** the user selects theme index N (0-15)
- **THEN** the corresponding theme parameters are applied

### Requirement: Default theme

The system SHALL provide a default theme (lwp.-2) used when no theme is selected or on first launch.

#### Scenario: First launch
- **WHEN** the app is launched for the first time
- **THEN** the default theme is displayed

### Requirement: Theme transitions

The system SHALL support smooth color transitions between themes using the transition definitions in lwp.-1.theme.

#### Scenario: Transition on theme change
- **WHEN** the user switches from one theme to another
- **THEN** colors interpolate smoothly over a short duration

### Requirement: Theme persistence

The system SHALL persist the selected theme index in SharedPreferences and restore it on app restart.

#### Scenario: Restore on restart
- **WHEN** the app is restarted
- **THEN** the previously selected theme is applied
PY