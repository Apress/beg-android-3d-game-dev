// Fragment Shader
precision mediump float;

uniform sampler2D sTexture;

uniform vec3 uLightAmbient;
uniform vec3 uLightDiffuse;
uniform vec3 uLightSpecular;

uniform vec3 uMatEmissive;
uniform vec3 uMatAmbient;
uniform vec3 uMatDiffuse;
uniform vec3 uMatSpecular;
uniform float uMatAlpha;

varying vec2 vTextureCoord;
varying float vDiffuse;
varying float vSpecular;

void main() 
{
	vec4 color = texture2D(sTexture, vTextureCoord);
	
	vec3 EmissiveTerm = uMatEmissive;
	vec3 AmbientTerm  = uMatAmbient * uLightAmbient;
	vec3 DiffuseTerm  = vDiffuse * uLightDiffuse * uMatDiffuse;
	vec3 SpecularTerm = vSpecular * uLightSpecular * uMatSpecular;

	// Calculate final color based on texture color, diffuse color, specular color and ambient color
	vec4 tempColor = color * (vec4(DiffuseTerm,1) + vec4(SpecularTerm,1) + vec4(AmbientTerm,1) + vec4(EmissiveTerm,1));
	gl_FragColor = vec4(tempColor.r,tempColor.g, tempColor.b, uMatAlpha);
}
    