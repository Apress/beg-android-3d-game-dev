// Vertex Shader 
uniform vec3 uWorldLightPos;
uniform vec3 uEyePosition;

uniform float uLightShininess;

uniform mat4 uMVPMatrix; 	// ModelViewProj Matrix
uniform mat4 uMVMatrix;		// ModelView Matrix
uniform mat4 uModelMatrix;	// Model Matrix
uniform mat4 uViewMatrix; 	// View Matrix

attribute vec3 aPosition;
attribute vec2 aTextureCoord;
attribute vec3 aNormal;
uniform mat4 NormalMatrix; // Normal Matrix

varying vec2 vTextureCoord;
varying float vDiffuse;
varying float vSpecular;

void main() 
{
	// Put Vertex Normal Into Eye Coords
	vec3 EcNormal = normalize(vec3(NormalMatrix * vec4(aNormal,1)));
	//vec3 EcNormal = normalize(vec3(uMVMatrix * vec4(aNormal,0)));
	
	// Calculate Diffuse Lighting for vertex
	// maximum of ( N dot L, 0)
	// Normal Vertex Vector in Eye Coords dot LightVector from Vertex to Light Source Position in Eye Coords
	vec3 WcVertexPos = vec3(uModelMatrix * vec4(aPosition,1));
    vec3 WcLightDir = uWorldLightPos - WcVertexPos; 
    vec3 EcLightDir = normalize(vec3(uViewMatrix * vec4(WcLightDir,1)));
    vDiffuse = max(dot(EcLightDir, EcNormal), 0.0);
	
	// Calculate Specular Term
	// S = LightVector + EyeVector
	// N = Vertex Normal
	// max (S dot N, 0) ^ Shininess 
	// 
	vec3 EyeDir = uEyePosition - WcVertexPos;
	vec3 S  = WcLightDir + EyeDir;
	vec3 EcS = normalize(vec3(uViewMatrix * vec4(S,1)));
	vSpecular = pow(max(dot(EcS, EcNormal), 0.0), uLightShininess);
	
    gl_Position = uMVPMatrix * vec4(aPosition,1);
    vTextureCoord = aTextureCoord;   
}
