/*********************************************************************
 *  ____                      _____      _                           *
 * / ___|  ___  _ __  _   _  | ____|_ __(_) ___ ___ ___  ___  _ __   *
 * \___ \ / _ \| '_ \| | | | |  _| | '__| |/ __/ __/ __|/ _ \| '_ \  *
 *  ___) | (_) | | | | |_| | | |___| |  | | (__\__ \__ \ (_) | | | | *
 * |____/ \___/|_| |_|\__, | |_____|_|  |_|\___|___/___/\___/|_| |_| *
 *                    |___/                                          *
 *                                                                   *
 *********************************************************************
 * Copyright 2011 Sony Ericsson Mobile Communications AB.            *
 * All rights, including trade secret rights, reserved.              *
 *********************************************************************/

//
//
// NOTE! This shader is now obsolete. This code remains only if we are not allowed
// to deliver the new shader code to the Fuji project since they might not dare to
// take in updated functionality.
//
// Replaced by: flow_vertex.glsl
//
//

attribute vec4 aPosition;

uniform mat4 uMVPMatrix;

uniform sampler2D sTexture0;
uniform sampler2D sTexture1;

uniform vec3 u_Time_NoiseScale_Color;
float u_NoiseScale;
float u_Time;

uniform vec4 u_NoisePos01;

varying vec4 vPosition;
varying vec4 vDx;
varying vec4 vDy;
varying float col1;

/*
 * The noise is read from the noise texture that contains offset data in three
 * dimensions.  There are two noise positions that are moved around the noise
 * texture and sent to the shader.  The two sampled noise samples, will both
 * be weighed in together to offset the vertex position.
 */
vec4 addNoise(in vec4 pos)
{
    float x = pos.x;
    float y = pos.y;

    // Read the first noise sample, the texture coordinates for the first
    // noise position is stored in u_NoisePos01.xy
    vec2 texPos0 = pos.xy * 0.1 + u_NoisePos01.xy;
    vec4 noise0 = (texture2D(sTexture1, texPos0) - 0.5) / u_NoiseScale;

    // Read the second noise sample, the texture coordinates for the second
    // noise position is stored in u_NoisePos01.zw
    vec2 texPos1 = pos.xy * 0.17 + u_NoisePos01.zw;
    vec4 noise1 = (texture2D(sTexture1, texPos1) - 0.5) / u_NoiseScale;

    // Add the two noise contributions to the vertex position. Since the
    // untransformed grid is stored in the plane z = 0, we explicitly add 0.0
    // to indicate that the z coordinate doesn't need to be added
    return vec4(x + noise0.x + noise1.x, y + noise0.y  + noise1.y, 0.0 + noise0.z + noise1.z, 1.0);
}

/*
 * Calculate the distance to another position in the grid. The result will be
 * an approximation of the distance across the defined interval over the
 * transformed crinkled grid. The exact distance is unknown since we will have
 * different values in u_NoisePos01 next time.
 */
vec4 delta(in vec4 pos, in float dx, in float dy, in vec4 newPos)
{
    return uMVPMatrix * addNoise(pos + vec4(dx, dy, 0.0, 0.0)) * vec4(0.7, 1.3, 0.7, 1.0) - newPos;
}

void main() {

    u_NoiseScale = u_Time_NoiseScale_Color.y;
    u_Time = u_Time_NoiseScale_Color.x;
    // This will offset (in three dimensions) the position of the vertex in
    // the grid, the result is that the grid plane is transformed into a
    // crinkled irregular shape
    gl_Position = uMVPMatrix * addNoise(aPosition) * vec4(0.7, 1.3, 0.7, 1.0);

    // Calculate approximate distances to two close positions in the
    // transformed
    vDx = delta(aPosition, 0.045, 0.0, gl_Position);
    vDy = delta(aPosition, 0.0, 0.045, gl_Position);

    vPosition = aPosition;

    // This texture is used to weigh in a variance of the flow color (the
    // secondary color), indicated by alpha entries
    col1 = texture2D(sTexture0, vPosition.xy).a;
}
