  float l1n = length(l1);
  gl_FragColor = mix(lc1,lc2, sin(precalc_randval.w*300.0)*0.5+0.5);//clamp(abs(ld.x*1.0),0.0,1.0));
  gl_FragColor *= clamp(l1n/(0.1+l1n),0.0,1.0);
  gl_FragColor *= clamp((lc-ra)/(10.0+lc-ra), 0.0, 1.0);
