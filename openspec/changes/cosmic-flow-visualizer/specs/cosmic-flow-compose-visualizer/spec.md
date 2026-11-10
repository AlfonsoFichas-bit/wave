# Spec Delta: cosmic-flow-compose-visualizer

## Purpose

Provides a modular Jetpack Compose component and showcase user interface for hosting the 3D Cosmic Flow visualizer, selecting themes, and modulating visual flow.

## ADDED Requirements

### Requirement: Embeddable Jetpack Compose Visualizer Component
The system SHALL provide a reusable `@Composable` component (`CosmicFlowVisualizer`) that hosts the OpenGL 3D engine in Compose without layout flicker or memory leaks.

#### Scenario: Visualizer embedded in UI
- **WHEN** `CosmicFlowVisualizer` is included in a Composable screen layout
- **THEN** it renders the continuous 3D Cosmic Flow animation smoothly within its assigned bounds.

### Requirement: Color Theme Selection UI
The system SHALL provide an interactive Compose interface allowing users to preview and select any of the 7 Cosmic Flow color themes.

#### Scenario: User selects a theme in Compose
- **WHEN** the user taps a color theme chip or thumbnail
- **THEN** the active palette changes in the visualizer and the selection persists in user preferences.

### Requirement: Interactive Gesture and Audio Input Hooks
The component SHALL provide callback parameters for touch interaction, drag gestures, and music playback state (BPM and volume amplitude).

#### Scenario: Music playback parameters updated
- **WHEN** new BPM or amplitude values are passed into the Composable
- **THEN** the visualizer dynamically adjusts animation speed and wave intensity to match the music tempo.
