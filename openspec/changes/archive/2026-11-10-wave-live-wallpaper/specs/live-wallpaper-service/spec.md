# Spec Delta

## Purpose

Provide a modern Android live wallpaper service that renders animated waves on the home screen with touch interaction and energy-efficient lifecycle management.

## ADDED Requirements

### Requirement: Wallpaper service registration

The system SHALL register a `WallpaperService` that Android recognizes as a live wallpaper provider, exposing it through the standard wallpaper picker.

#### Scenario: Service appears in wallpaper picker
- **WHEN** the user opens the system wallpaper selector
- **THEN** "Wave" appears as an available live wallpaper option

### Requirement: Engine lifecycle management

The system SHALL create and manage a wallpaper engine that handles surface creation, visibility changes, and destruction, pausing rendering when the wallpaper is not visible.

#### Scenario: Pause on invisibility
- **WHEN** the user switches to a full-screen app or the screen turns off
- **THEN** rendering pauses and resumes when the wallpaper becomes visible again

#### Scenario: Clean destruction
- **WHEN** the wallpaper is removed or the service destroyed
- **THEN** all GL resources are released without memory leaks

### Requirement: Touch event propagation

The system SHALL propagate touch events (down, move, up) from the launcher to the renderer, and support accelerate commands for lockscreen integration.

#### Scenario: Touch moves waves
- **WHEN** the user touches and drags on the home screen
- **THEN** the wave animation responds to the touch position

#### Scenario: Accelerate command
- **WHEN** an accelerate command with value <= 10000 is received
- **THEN** the renderer accelerates the animation accordingly

### Requirement: Energy efficiency

The system SHALL limit rendering to 30 FPS maximum and use on-demand rendering when idle, reducing battery consumption.

#### Scenario: Idle rendering
- **WHEN** no touch or motion is occurring
- **THEN** the system renders at most 30 frames per second

#### Scenario: Continuous during interaction
- **WHEN** the user is actively touching the screen
- **THEN** rendering continues smoothly without frame drops
PY