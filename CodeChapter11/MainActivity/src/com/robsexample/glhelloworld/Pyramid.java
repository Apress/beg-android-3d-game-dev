package com.robsexample.glhelloworld;

import android.content.Context;

public class Pyramid extends Object3d
{
	 static float[] PyramidVertices = 
	 {
		    	// Triangle Shape
		    	// Left Side            u  v   nx, ny, nz
		    	-1.5f, -1.5f, -1.5f,    0, 1,  -1, -1, -1,	// v0 =  left, bottom, back
		    	-1.5f, -1.5f,  1.5f,    1, 1,  -1, -1,  1,	// v1 =  left, bottom, front
		    	 0.0f,  3.2f,  0.0f, 0.5f, 0,   0,  1,  0,	// v2 =  top point

		    	// Right Side
		    	 1.5f, -1.5f, 1.5f,    0, 1,  	1,-1, 1,	// v3 = right, bottom, front	
		    	 1.5f, -1.5f,-1.5f,    1, 1,  	1,-1,-1,	// v4 = right, bottom, back  	
		    	 0.0f, 3.2f, 0.0f,  0.5f, 0,  	0, 1, 0,	// v2 =  top point
		    	
		    	// Front 
		    	-1.5f, -1.5f, 1.5f,     1, 1, 	-1,-1, 1,	// v1 =  left, bottom, front
		    	 1.5f, -1.5f, 1.5f,     0, 1,  	 1,-1, 1,	// v3 = right, bottom, front
		    	 0.0f,  3.2f, 0.0f,  0.5f, 0,  	 0, 1, 0,	// v2 =  top point
		    	
		    	// Back
		    	-1.5f, -1.5f, -1.5f,  0, 1,		-1, -1,-1,	// v0 =  left, bottom, back
		    	1.5f, -1.5f,-1.5f,    1, 1,  	 1, -1,-1,	// v4 = right, bottom, back
		    	0.0f, 3.2f, 0.0f,  0.5f, 0,  	 0,  1, 0,	// v2 =  top point
		    
		    	// Bottom
		    	-1.5f, -1.5f, -1.5f,  0, 0,	   -1, -1, -1,	// v0 =  left, bottom, back
		    	1.5f, -1.5f,-1.5f,    0, 1,  	1, -1, -1,	// v4 = right, bottom, back
		    	1.5f, -1.5f, 1.5f,    1, 1,  	1, -1,  1,	// v3 = right, bottom, front
		    	
		    	// Bottom 2
		    	-1.5f, -1.5f, -1.5f,   0, 0,	-1,-1,-1,	// v0 =  left, bottom, back
		        -1.5f, -1.5f, 1.5f,    1, 0, 	-1,-1, 1,	// v1 =  left, bottom, front
		    	 1.5f, -1.5f, 1.5f,    1, 1,  	 1,-1, 1	// v3 = right, bottom, front    
	 };

	 
	Pyramid(Context iContext, 
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
