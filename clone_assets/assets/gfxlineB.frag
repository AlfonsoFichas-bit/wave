
  float cwo = (linewidth-curveWidthRange.x)/(curveWidthRange.y-curveWidthRange.x);
  float l64 = max(len*100.0 + (-98.0), 0.0);
  gl_FragColor = mix(c1, c2, l1n);
  gl_FragColor.rgba = gl_FragColor.rgba + cwo*(gl_FragColor.rgba*2.0+0.5)*clamp(l64*(1.0-loc*0.65),0.0,1.0);
  gl_FragColor.a *= l64*-0.5+1.0;

