uniform float iOvertoneVolume;

#define t iGlobalTime
mat2 m(float a){float c=cos(a), s=sin(a);return mat2(c,-s,s,c);}
float map(vec3 p){
  p.xz*= m(t*0.4);p.xy*= m(t*0.3);
  vec3 q = p*2.+t*1.;
  return length(p+vec3(sin(t*0.7)))*log(length(p)+1.) + sin(q.x+sin(q.z+sin(q.y)))*0.5 - 1.;
}

void ether() {
  vec2 p = gl_FragCoord.xy / iResolution.y - vec2(.9,.5);
  vec3 cl = vec3(0.);
  float d = 2.5;
  for(int i=0; i<=5; i++) {
    vec3 p = vec3(0,0,5.) + normalize(vec3(p, -1.))*d;
    float rz = map(p);
    float f =  clamp((rz - map(p+.1))*0.5, -.1, 1. );
    vec3 l = vec3(0.1,0.3,.4) + vec3(5., 2.5, 3.)*f;
    cl = cl*l + (1.-smoothstep(0., 2.5, rz))*.7*l;
    d += min(rz, 1.);
  }

  gl_FragColor = vec4(cl, 1.);
}

void electric()
{
  float freqs[4];
  freqs[0] = texture2D(iChannel0, vec2( 0.2, 0.25 ) ).x;
  freqs[1] = texture2D(iChannel0, vec2( 0.5, 0.25 ) ).x;
  freqs[2] = texture2D(iChannel0, vec2( 0.25, 0.25 ) ).x;
  freqs[3] = texture2D(iChannel0, vec2( 0.40, 0.25 ) ).x;

  vec2 uv = gl_FragCoord.xy / iResolution.xy;

  vec2 p = uv*2.0 - 1.0;
  p *= 100.0;

  vec2 sfunc = vec2(p.x, p.y + freqs[0]*100*sin(uv.x*5.0-iGlobalTime*5.0 + cos(iGlobalTime*3.0) ) + freqs[1]*200.0*cos(uv.x*23.0+iGlobalTime*7.0));
  // vec2 sfunc = vec2(p.x, p.y + 55*sin(uv.x*10.0-iGlobalTime*1.0 + cos(iGlobalTime*7.0) ) + 2.0*cos(uv.x*25.0+iGlobalTime*4.0));

  sfunc.y *= uv.x*1.0+0.05;
  //sfunc.y /= uv.x+freqs[3];
  sfunc.y *= 2.0 - uv.x*2.0+0.05;
  sfunc.y /= freqs[0] + 0.1;

  vec3 c = vec3(abs(sfunc.y));
  c = pow(c, vec3(-0.5));
  c *= vec3(0.16, 0.63, 0.60);

  gl_FragColor = vec4(c,1.0);
}

void main()
{
  electric();
  // ether();
}
