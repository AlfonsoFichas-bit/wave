  
  gl_Position = vec4(ld, 0.5, 1.0);
  ld.y += baseMotion(ld, force);
  ld.y += curvePush(ld, 0.0);
  

