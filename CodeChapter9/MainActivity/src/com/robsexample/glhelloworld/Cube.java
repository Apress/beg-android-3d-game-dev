package com.robsexample.glhelloworld;


import android.content.Context;

public class Cube extends Object3d 
{
	static float CubeData[] = 
	{ 
		// x,     y,    z,   u,       v		nx,  ny, nz
	    -0.5f,  0.5f, 0.5f, 0.0f,   0.0f,   -1,  1, 1, 	// front top left      	0
	    -0.5f, -0.5f, 0.5f, 0.0f,   1.0f,   -1, -1, 1,	// front bottom left   	1
	     0.5f, -0.5f, 0.5f, 1.0f,   1.0f,    1, -1, 1, 	// front bottom right	2
	     0.5f,  0.5f, 0.5f, 1.0f,   0.0f,    1,  1, 1,  // front top right		3
	         	
	    -0.5f,  0.5f, -0.5f, 0.0f,   0.0f,  -1,  1, -1, // back top left		4
	    -0.5f, -0.5f, -0.5f, 0.0f,   1.0f,  -1, -1, -1,	// back bottom left		5
	     0.5f, -0.5f, -0.5f, 1.0f,   1.0f,   1, -1, -1, // back bottom right	6
	     0.5f,  0.5f, -0.5f, 1.0f,   0.0f,   1,  1, -1  // back top right    	7
	}; 
	
	
	static float CubeData4Sided[] = 
		{ 
			// x,     y,    z,   u,       v		nx,  ny, nz
		    -0.5f,  0.5f, 0.5f, 0.0f,   0.0f,   -1,  1, 1, 	// front top left      	0
		    -0.5f, -0.5f, 0.5f, 0.0f,   1.0f,   -1, -1, 1,	// front bottom left   	1
		     0.5f, -0.5f, 0.5f, 1.0f,   1.0f,    1, -1, 1, 	// front bottom right	2
		     0.5f,  0.5f, 0.5f, 1.0f,   0.0f,    1,  1, 1,  // front top right		3
		         	
		    -0.5f,  0.5f, -0.5f, 1.0f,   0.0f,  -1,  1, -1, // back top left		4
		    -0.5f, -0.5f, -0.5f, 1.0f,   1.0f,  -1, -1, -1,	// back bottom left		5
		     0.5f, -0.5f, -0.5f, 0.0f,   1.0f,   1, -1, -1, // back bottom right	6
		     0.5f,  0.5f, -0.5f, 0.0f,   0.0f,   1,  1, -1  // back top right    	7
	}; 
		
	
	
	static float CubeDataNoTexture[] = 
		{ 
			// x,     y,    z,   	nx,  ny, nz
		    -0.5f,  0.5f, 0.5f,    -1,  1, 1, 	// front top left      	0
		    -0.5f, -0.5f, 0.5f,    -1, -1, 1,	// front bottom left   	1
		     0.5f, -0.5f, 0.5f,     1, -1, 1, 	// front bottom right	2
		     0.5f,  0.5f, 0.5f,     1,  1, 1,   // front top right		3
		         	
		    -0.5f,  0.5f, -0.5f,   -1,  1, -1,  // back top left		4
		    -0.5f, -0.5f, -0.5f,   -1, -1, -1,	// back bottom left		5
		     0.5f, -0.5f, -0.5f,    1, -1, -1,  // back bottom right	6
		     0.5f,  0.5f, -0.5f,    1,  1, -1   // back top right    	7
		}; 
		
	static final short CubeDrawOrder[] = 
	{ 
			0, 3, 1, 3, 2, 1,	// Front panel
	    	4, 7, 5, 7, 6, 5,   // Back panel
	    	4, 0, 5, 0, 1, 5,	// Side
	    	7, 3, 6, 3, 2, 6,	// Side
	    	4, 7, 0, 7, 3, 0,	// Top
	    	5, 6, 1, 6, 2, 1    // Bottom
	}; // order to draw vertices

	Cube(Context iContext, 
		 Mesh iMesh, 
		 MeshEx iMeshEx, 
		 Texture[] iTextures, 
		 Material iMaterial, 
		 Shader iShader)
	{	
		super(iContext, 
			  iMesh, 
			  iMeshEx, 
			  iTextures, 
			  iMaterial, 
			  iShader);		
	}
}
