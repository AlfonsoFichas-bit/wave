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
// Replaced by: flow_fragment.glsl
//
//

precision mediump float;

uniform vec3 u_PrimaryColor;
uniform vec3 u_SecondaryColor;

varying vec4 vPosition;
varying vec4 vDx;
varying vec4 vDy;

varying float col1;

void main() {
    // Substitute the alpha with a value proportional to the area to the two
    // neighbor positions, this will make the flow less dense where the
    // transformed grid is flatter
    float normalAlpha = (1.1 - (length(cross(vDx.xyz, vDy.xyz))));
    float alpha = normalAlpha * (vPosition.y + 0.2);
    alpha = pow(alpha, 2.3);

    gl_FragColor = vec4(u_PrimaryColor + col1 * u_SecondaryColor, alpha);
}

