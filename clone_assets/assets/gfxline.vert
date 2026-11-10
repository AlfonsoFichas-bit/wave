



  linewidth = mix(curveWidthRange.x, curveWidthRange.y, (sin(precalc_curveParams.z*ld.x)*0.5+0.5)*linedata.x);
  ld.y *= linewidth*precalc_ratio;



  ld.y += baseMotion(ld, force);
  ld.y += curvePush(ld, precalc_radlc);



  ld.y +=(precalc_car.x*linedata.x + precalc_car.y)
         *sin(force*precalc_curveParams.y+
              (ld.x*precalc_curveParams.x+precalc_randval.w))
         *precalc_ratio;

  gl_Position = vec4(ld.x,-ld.y, 0.5, 1.0);


  l1n = sin(ld.x+precalc_randval.w*76.0+l3.x*precalc_randval.z)*0.5+0.5;

  loc = clamp(distance(vec2(0.3,-0.3), ld.xy*vec2(1.3,lsByx)),0.0,1.0);
  
  vec2 l = vec2(min(ild.y+1.0,1.0), max(0.0, ild.y-1.0));
  len = abs(ild.y);
  
  c1 = mix(mix(lc1, lc2, l.x), lc3, l.y);
  c2 = mix(mix(lc4, lc5, l.x), lc6, l.y);
