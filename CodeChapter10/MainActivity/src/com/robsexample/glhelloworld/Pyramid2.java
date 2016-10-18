package com.robsexample.glhelloworld;


import android.content.Context;




public class Pyramid2  extends Object3d
{

	 static float[] Pyramid2Vertices = 
		 {
		 		// Triangle Shape
		 		// Left Side            u  v   nx, ny, nz
			    -0.5f, -0.5f, -0.5f,    0, 1,  -1, -1, -1,	// v0 =  left, bottom, back
			     0.0f, -0.5f,  0.5f,    1, 1,   0,  0,  1,	// v1 =  left, bottom, front
			     0.0f,  0.5f, -0.5f, 0.5f, 0,   0,  1,  0,	// v2 =  top point

			     
			    // Right Side
			    0.5f, -0.5f, -0.5f,    0, 1,  	1,-1,-1,	// v3 = right, bottom, back	
			    0.0f, -0.5f,  0.5f,    1, 1,  	0, 0, 1,	// v4 = right, bottom, front 	
			    0.0f,  0.5f, -0.5f,  0.5f, 0,  	0, 1, 0,	// v2 =  top point
			    	
	
			    // Back
			    -0.5f, -0.5f, -0.5f,    0, 1,  -1, -1, -1,  // v0 =  left, bottom, back
			     0.5f, -0.5f, -0.5f,    1, 1,  	1,-1,-1,	// v3 = right, bottom, back	
			     0.0f,  0.5f, -0.5f,  0.5f, 0,  0, 1, 0,    // v2 =  top point
			    
			
			    // Bottom
			   -0.5f, -0.5f,  -0.5f,     0, 1,     -1, -1, -1,  // v0 =  left, bottom, back
			    0.5f, -0.5f,  -0.5f,     1, 1,  	1,-1,-1,	// v3 = right, bottom, back	
			    0.0f, -0.5f,   0.5f,  0.5f, 0,  	0, 0, 1,	// v4 = right, bottom, front 
		 };

	
	
	Pyramid2(Context iContext, 
			 Mesh iMesh, 
			 MeshEx iMeshEx, 
			 Texture[] iTextures, 
			 Material iMaterial, 
			 Shader iShader, 
			 Shader LocalAxisShader)
	{
		super(iContext, 
			  iMesh, 
			  iMeshEx, 
			  iTextures, 
			  iMaterial, 
			  iShader//, 
			  //LocalAxisShader
			  );
	
	}
	
	
}
