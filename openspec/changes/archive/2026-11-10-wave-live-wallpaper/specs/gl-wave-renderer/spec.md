# Spec Delta

## Purpose

Render an animated wave background using OpenGL ES 2.0 with shaders, noise texture, and theme-driven colors at 60/120 FPS with minimal battery usage.

## ADDED Requirements

### Requirement: OpenGL ES 2.0 rendering

The system SHALL render the wallpaper using OpenGL ES 2.0 with a fullscreen background shader and line/wave shaders, compositing layers on the GPU.

#### Scenario: Background renders
- **WHEN** the wallpaper surface is created
- **THEN** the background shader fills the screen with animated colors

#### Scenario: Waves render
- **WHEN** the render loop executes
- **THEN** sinusoidal wave lines are drawn over the background using the line shader

### Requirement: Noise texture

The system SHALL load and apply a stochastic noise texture to modulate the wave animation, creating organic movement.

#### Scenario: Noise applied
- **WHEN** the renderer initializes
- **THEN** the noise texture is loaded into GPU memory and sampled by the shaders

### Requirement: Theme-driven colors

The system SHALL apply colors from the active theme (background colors bgc1-4, line colors lc1-6) to the shaders, allowing visual customization.

#### Scenario: Theme change updates colors
- **WHEN** the user selects a different theme
- **THEN** the background and wave colors update to match the theme

### Requirement: Touch responsiveness

The system SHALL respond to touch events by altering wave parameters dynamically, with smooth return to baseline after touch ends.

#### Scenario: Touch distorts waves
- **WHEN** the user touches the screen
- **THEN** waves near the touch point distort and animate

#### Scenario: Smooth return
- **WHEN** the user lifts their finger
- **THEN** waves gradually return to their normal pattern

### Requirement: Framerate control

The system SHALL cap rendering at 30 FPS maximum and support on-demand rendering, reporting sleep recommendations to the system.

#### Scenario: Sleep recommendation
- **WHEN** the system queries for sleep recommendation
- **THEN** the renderer returns an appropriate value based on current activity
PY