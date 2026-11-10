    float loc = clamp((ld.y)*10.0+0.5,0.0,1.0);
    float po = min(length(ild.xy*vec2(1.0, lsB.y/lsB.x)*0.5), 1.0);
    vec2 ildScaled = vec2((ild.x-1.0)*0.5*noiseTextureScale.x, (ild.y-1.0)*0.5*noiseTextureScale.y);
    gl_FragColor =  mix(mix(bgc3,bgc4,po), mix(bgc2,bgc1,po), loc);
    gl_FragColor.rgb += texture2D(noise, ildScaled).rgb-0.75;
