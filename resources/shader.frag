#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;
uniform float u_Channel0;

vec4 electric()
{
  float freqs[4];
  freqs[0] = texture2D(u_Channel0, vec2( 0.2, 0.25 ) ).x;
  freqs[1] = texture2D(u_Channel0, vec2( 0.5, 0.25 ) ).x;
  freqs[2] = texture2D(u_Channel0, vec2( 0.25, 0.25 ) ).x;
  freqs[3] = texture2D(u_Channel0, vec2( 0.40, 0.25 ) ).x;

  vec2 uv = gl_FragCoord.xy / u_Resolution.xy;

  vec2 p = uv*2.0 - 1.0;
  p *= 100.0;

  vec2 sfunc = vec2(p.x, p.y + freqs[0]*100*sin(uv.x*5.0-u_time*5.0 + cos(u_time*3.0) ) + freqs[1]*200.0*cos(uv.x*23.0+u_time*7.0));

  sfunc.y = sfunc.y * (uv.x*1.0+0.05);
  sfunc.y = sfunc.y * (2.0 - uv.x * 2.0+0.05);
  sfunc.y = sfunc.y / (freqs[0] + 0.1);

  vec3 c = pow(vec3(abs(sfunc.y)), vec3(-0.5)) * vec3(0.16, 0.63, 0.60);

  return vec4(c,1.0);
}


void main() {
  vec2 st = gl_FragCoord.xy/u_resolution.xy;
  vec3 color = vec3(1.0, 1.0, 0.0);
  gl_FragColor = vec4(color, 1.0);
}

