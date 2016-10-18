package com.robsexample.glhelloworld;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;



public class Mesh 
{

	private FloatBuffer m_VertexBuffer = null;

	private int m_CoordsPerVertex = 3;
	private static final int FLOAT_SIZE_BYTES = 4;
	private int m_VertexCount = 0;
	private int m_MeshVerticesDataStrideBytes = m_CoordsPerVertex * FLOAT_SIZE_BYTES;
	private int m_MeshVerticesDataPosOffset = 0;
	private int m_MeshVerticesDataUVOffset = -1;
	private int m_MeshVerticesDataNormalOffset = -1; 
	
	private boolean m_MeshHasUV = false;
	private boolean m_MeshHasNormals = false;
	
	private Vector3 m_Size 			= new Vector3(0,0,0);
	private float	m_Radius 		= 0;
	private float	m_RadiusAverage = 0;
	
	public Mesh(int CoordsPerVertex, 
				int MeshVerticesDataPosOffset, 
				int MeshVerticesUVOffset , 
				int MeshVerticesNormalOffset,
				float[] Vertices)
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
	    CalculateRadius();
	}
	
	Vector3 GetSize()
	{
		return m_Size;
	}
	
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
	
	void DrawMesh(int PosHandle, int TexHandle, int NormalHandle)
	{
		SetUpMeshArrays(PosHandle, TexHandle, NormalHandle);
		
	    // Draw the triangle
	    //glDrawArrays (int mode, int first, int count)
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, m_VertexCount);
	    
		//GLES20.glDrawArrays(GLES20.GL_POINTS, 0, m_VertexCount);
	    
	    

	    // Disable vertex array
	    GLES20.glDisableVertexAttribArray(PosHandle);
	    
	    if (m_MeshHasUV)
	    {
	    	GLES20.glDisableVertexAttribArray(TexHandle);	
	    }
	    if (m_MeshHasNormals)
	    {
	    	GLES20.glDisableVertexAttribArray(NormalHandle);	
	    }
	}
	
	
	
	
	
	
}
