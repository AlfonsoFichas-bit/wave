# Spec Delta: cosmic-flow-engine

## Purpose

Provides a pure Kotlin and OpenGL ES 2.0 3D renderer implementing the reverse-engineered Cosmic Flow motion algorithms, 3D perspective camera matrix, and classic color presets.

## ADDED Requirements

### Requirement: 3D Grid Mesh and Model-View-Projection Matrix
The engine SHALL generate a 3D vertex mesh grid and apply a dynamic Model-View-Projection matrix (`uMVPMatrix`) to render the Cosmic Flow surface with depth, perspective tilt, and smooth rotation.

#### Scenario: Mesh generation and perspective setup
- **WHEN** the OpenGL surface is created or resized
- **THEN** the engine initializes the grid vertex buffer and builds the perspective projection and camera view matrices.

### Requirement: Cosmic Flow Harmonic Shader Computation
The engine SHALL execute vertex and fragment shaders computing multi-frequency harmonic wave turbulence modulated by moving noise coordinates.

#### Scenario: Real-time vertex perturbation
- **WHEN** a frame is drawn with advancing time
- **THEN** vertex positions are deformed in 3D space using the harmonic wave functions (`wave`, `addNoiseForAxis`, and noise texture lookup).

### Requirement: Seven Classic Cosmic Flow Palettes
The engine SHALL support the 7 original Sony Xperia Cosmic Flow color schemes: Amethyst, Turquoise, Emerald, Sapphire, Gold, Silk, and Ruby.

#### Scenario: Switching color theme
- **WHEN** a theme palette is selected by the user or client
- **THEN** the background and ribbon color uniforms update immediately and smoothly without reallocating OpenGL programs.

### Requirement: Interactive Gesture and Audio Modulation
The engine SHALL accept horizontal parallax offsets, touch input vectors, and playback modulation inputs (such as BPM or audio amplitude).

#### Scenario: BPM or touch modulation
- **WHEN** audio beat pulses or touch interactions occur
- **THEN** the engine modulates wave speed and turbulence amplitude proportionately in real time.
