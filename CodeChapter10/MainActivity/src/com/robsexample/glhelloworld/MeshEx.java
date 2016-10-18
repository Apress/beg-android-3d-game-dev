package com.robsexample.glhelloworld;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ShortBuffer;






enum MeshType
{
	Triangles,
	Lines
}

public class MeshEx 
{
	// Use Indexed Drawing method
	private FloatBuffer m_VertexBuffer 		= null; // Holds mesh vertex data
	private ShortBuffer m_DrawListBuffer 	= null; // Holds index values into the vertex buffer that indicate 
													// the order in which to draw the vertices.
	
	private int m_CoordsPerVertex = 3;
	private static final int FLOAT_SIZE_BYTES = 4;
	private int m_VertexCount = 0;
	private int m_MeshVerticesDataStrideBytes = m_CoordsPerVertex * FLOAT_SIZE_BYTES;
	private int m_MeshVerticesDataPosOffset = 0;
	private int m_MeshVerticesDataUVOffset = -1;
	private int m_MeshVerticesDataNormalOffset = -1; 
	
	private boolean m_MeshHasUV = false;
	private boolean m_MeshHasNormals = false;
	
	
	
	
	// Collision
	private Vector3 m_Size 			= new Vector3(0,0,0);
	private float	m_Radius 		= 0;
	private float	m_RadiusAverage = 0;
	
	// Mesh Type
	private MeshType m_MeshType;
	

	
	
	public MeshEx(int CoordsPerVertex, 
				int MeshVerticesDataPosOffset, 
				int MeshVerticesUVOffset , 
				int MeshVerticesNormalOffset,
				float[] Vertices,
				short[] DrawOrder
				)
	{
		m_CoordsPerVertex = CoordsPerVertex;
		m_MeshVerticesDataStrideBytes	= m_CoordsPerVertex * FLOAT_SIZE_BYTES;
		m_MeshVerticesDataPosOffset 	= MeshVerticesDataPosOffset;
		m_MeshVerticesDataUVOffset 		= MeshVerticesUVOffset ; 
		m_MeshVerticesDataNormalOffset 	= MeshVerticesNormalOffset;
		
		if (m_MeshVerticesDataUVOffset >= 0)
		{
			m_MeshHasUV = true;
		}
		
		if (m_MeshVerticesDataNormalOffset >=0)
		{
			m_MeshHasNormals = true;
		}
		
		// Allocate Vertex Buffer
		ByteBuffer bb = ByteBuffer.allocateDirect(
				    // (# of coordinate values * 4 bytes per float)
					Vertices.length * FLOAT_SIZE_BYTES);
	    bb.order(ByteOrder.nativeOrder());
	    m_VertexBuffer = bb.asFloatBuffer();
		
	    if (Vertices != null)
	    {
	    	m_VertexBuffer.put(Vertices);
	    	m_VertexBuffer.position(0);
	    	    	
	    	m_VertexCount = Vertices.length / m_CoordsPerVertex;
	    }
	    
	   // Initialize DrawList Buffer  
	   m_DrawListBuffer = ShortBuffer.wrap(DrawOrder);
	   
	   // Set Default Mesh type to Triangles
	   m_MeshType = MeshType.Triangles;
	  
	   // Calculate Bounding Sphere
	   CalculateRadius();
	}
	
	// MeshType
	void SetMeshType(MeshType Type)
	{
		m_MeshType = Type;
	}
	
	MeshType GetMeshType()
	{
		return m_MeshType;
	}
	
	// Collision 
	float GetRadius()
	{
		return m_Radius;	
	}
	
	float GetRadiusAverage()
	{
		return m_RadiusAverage;
	}
	
	void CalculateRadius()
	{
		float XMin = 100000000;
		float YMin = 100000000;
		float ZMin = 100000000;
		
		float XMax = -100000000;
		float YMax = -100000000;
		float ZMax = -100000000;
		
		int ElementPos = m_MeshVerticesDataPosOffset;
		
		// Loop through all vertices and find min and max values of x,y,z
		for (int i = 0; i < m_VertexCount; i++)
		{	
			float x = m_VertexBuffer.get(ElementPos);
			float y = m_VertexBuffer.get(ElementPos+1);
			float z = m_VertexBuffer.get(ElementPos+2);
					
			// Test for Min 
			if (x < XMin)
			{
				XMin = x;
			}
			
			if (y < YMin)
			{
				YMin = y;
			}
			
			if (z < ZMin)
			{
				ZMin = z;
			}
			
			// Test for Max 
			if (x > XMax)
			{
				XMax = x;
			}
			
			if (y > YMax)
			{
				YMax = y;
			}
			
			if (z > ZMax)
			{
				ZMax = z;
			}
			ElementPos = ElementPos + m_CoordsPerVertex;
		}
		
		// Calculate Size of Mesh in the x,y,z directions
		m_Size.x = Math.abs(XMax - XMin);
		m_Size.y = Math.abs(YMax - YMin);
		m_Size.z = Math.abs(ZMax - ZMin);
		
		
		// Calculate Radius
		float LargestSize = -1;
		if (m_Size.x > LargestSize)
		{
			LargestSize = m_Size.x;
		}
		
		if (m_Size.y > LargestSize)
		{
			LargestSize = m_Size.y;
		}
		
		if (m_Size.z > LargestSize)
		{
			LargestSize = m_Size.z;
		}
		
		m_Radius = LargestSize/2.0f;
		
		// Calculate Average Radius;
		m_RadiusAverage = (m_Size.x + m_Size.y + m_Size.z) / 3.0f;
		m_RadiusAverage = m_RadiusAverage/2.0f;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	void SetUpMeshArrays(int PosHandle, int TexHandle, int NormalHandle)
	{
		//glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int offset)
		//glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr)
		
		// Set up stream to position variable in shader
		m_VertexBuffer.position(m_MeshVerticesDataPosOffset);
	    GLES20.glVertexAttribPointer(PosHandle, 
	    							3, 
	    							GLES20.GL_FLOAT, 
	    							false,
	    							m_MeshVerticesDataStrideBytes, 
	    							m_VertexBuffer);
	       
	    GLES20.glEnableVertexAttribArray(PosHandle);
	     
	    
	    if (m_MeshHasUV)
	    {
	    	// Set up Vertex Texture Data stream to shader  
	    	m_VertexBuffer.position(m_MeshVerticesDataUVOffset);
	    	GLES20.glVertexAttribPointer(TexHandle, 
	    								2, 
	    								GLES20.GL_FLOAT, 
	    								false,
	    								m_MeshVerticesDataStrideBytes, 
	    								m_VertexBuffer);
	    	GLES20.glEnableVertexAttribArray(TexHandle);
	    }
	   
	    if (m_MeshHasNormals)
	    {
	    	
	    	// Set up Vertex Texture Data stream to shader
	    	m_VertexBuffer.position(m_MeshVerticesDataNormalOffset);
	    	GLES20.glVertexAttribPointer(NormalHandle, 
	    								3, 
	    								GLES20.GL_FLOAT, 
	    								false,
	    								m_MeshVerticesDataStrideBytes, 
	    								m_VertexBuffer);
	    	GLES20.glEnableVertexAttribArray(NormalHandle);
	    } 
	    
	          
	    
	}
	
	public static void CheckGLError(String glOperation) 
	{
		int error;
	    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
	    	Log.e("ERROR IN MESHEX", glOperation + " IN CHECKGLERROR() : glError - " + error);
	        throw new RuntimeException(glOperation + ": glError " + error);
	    }
	}
	
	void DrawMesh(int PosHandle, int TexHandle, int NormalHandle)
	{
		SetUpMeshArrays(PosHandle, TexHandle, NormalHandle);
		
	   
		/*
		//glDrawElements (int mode, int count, int type, int offset)
		//glDrawElements (int mode, int count, int type, Buffer indices)
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, 
							  m_DrawListBuffer.capacity(),
							  GLES20.GL_UNSIGNED_SHORT, 
							  m_DrawListBuffer);
		
		*/
		
		
	    // Draw the either Triangle Mesh or line mesh
		if (m_MeshType == MeshType.Triangles)
		{
			//glDrawElements (int mode, int count, int type, int offset)
			//glDrawElements (int mode, int count, int type, Buffer indices)
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, 
								  m_DrawListBuffer.capacity(),
								  GLES20.GL_UNSIGNED_SHORT, 
								  m_DrawListBuffer);
		}
		else
		if (m_MeshType == MeshType.Lines)
		{
			GLES20.glDrawElements(GLES20.GL_LINES, 
								  m_DrawListBuffer.capacity(),
								  GLES20.GL_UNSIGNED_SHORT, 
								  m_DrawListBuffer);
		}
	        
		
		
		
		
		
	
		
	    // Disable vertex array
	    GLES20.glDisableVertexAttribArray(PosHandle);
	    CheckGLError("glDisableVertexAttribArray ERROR - PosHandle");
	    
	    if (m_MeshHasUV)
	    {
	    	GLES20.glDisableVertexAttribArray(TexHandle);	
	    	CheckGLError("glDisableVertexAttribArray ERROR - TexHandle");
	    }
	    if (m_MeshHasNormals)
	    {
	    	GLES20.glDisableVertexAttribArray(NormalHandle);	
	    	CheckGLError("glDisableVertexAttribArray ERROR - NormalHandle");
	    }
	}
	
}
