package com.robsexample.glhelloworld;

//import com.robsexample.glhelloworld.Camera;
//import robs.gldemo.robsgl20tutorial.MeshEx;
//import robs.gldemo.robsgl20tutorial.MeshType;
//import robs.gldemo.robsgl20tutorial.Object3d;
//import robs.gldemo.robsgl20tutorial.Shader;
//import robs.gldemo.robsgl20tutorial.Vector3;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

public class GravityGridEx 
{
	private Context m_Context;
	
	// New Grid using indexed vertices.
	private MeshEx m_LineMeshGrid;
	
	private int m_CoordsPerVertex = 3; 
	private int m_MeshVerticesDataPosOffset = 0; 
	private int m_MeshVerticesUVOffset = -1; 
	private int m_MeshVerticesNormalOffset = -1;
	private float[] m_Vertices;
	private short[] m_DrawOrder;
	
	// Masses on Gravity Grid
	private int m_NumberMasses = 0; 
	private int MassesIndex = 0;
	private int MAX_MASSES = 30;
	
	private float[] m_MassValues 		  = new float[MAX_MASSES];  
	private float[] m_MassLocations 	  = new float[MAX_MASSES*3]; 
	private float[] m_MassEffectiveRadius = new float[MAX_MASSES]; 
	private float[] m_MassSpotLightColor  = new float[MAX_MASSES*3]; // 3 r,g,b values per mass
	
	private Shader m_Shader;
	private int m_PositionHandle; 
	private Vector3 m_GridColor;

	private float[] m_MVPMatrix = new float[16];
	
	
	// Grid Boundaries
	private float m_XMinBoundary;
	private float m_XMaxBoundary;
		
	private float m_ZMinBoundary;
	private float m_ZMaxBoundary;
	
	// Creates a grid of lines on the XZ plane at GridHeight height
	// of size GridSizeZ by GridSizeX in number of grid points
	GravityGridEx(Context iContext,
				  Vector3 GridColor,
				  float GridHeight,
				  float GridStartZValue, 
				  float GridStartXValue, 
				  float GridSpacing, 
				  int GridSizeZ,
				  int GridSizeX,
				  Shader iShader)
	{	
		m_Context = iContext;
		m_Shader = iShader;
		m_GridColor = GridColor;
		
		
		 // Set Grid Boundaries
		 float NumberCellsX = GridSizeX - 1;
		 float NumberCellsZ = GridSizeZ - 1;
		 
		 m_XMinBoundary = GridStartXValue;
		 m_XMaxBoundary = GridStartXValue + (NumberCellsX * GridSpacing);
			
		 m_ZMinBoundary = GridStartZValue;
		 m_ZMaxBoundary = GridStartZValue + (NumberCellsZ * GridSpacing);
			
		 
		 
		
		
		
		
		int NumberVertices = GridSizeZ * GridSizeX;
		int TotalNumberCoords = m_CoordsPerVertex * NumberVertices;
		
		Log.e("GRAVITYGRIDEX" , "TotalNumberCoords = " + TotalNumberCoords);
		m_Vertices = new float[TotalNumberCoords];
		
		// Create Vertices for Grid
		int index = 0;
		for (float z = 0; z < GridSizeZ; z++)
		{
			for (float x = 0; x < GridSizeX; x++)
			{
				// Determine World Position of Vertex
				float xpos = GridStartXValue + (x * GridSpacing);
				float zpos = GridStartZValue + (z * GridSpacing);
				
				if (index >= TotalNumberCoords)
				{
					Log.e("GRAVITYGRIDEX" , "Array Out of Bounds ERRROR, Index >= TotalNumberCoords");
				}
				// Assign Vertex to array
				m_Vertices[index] 	 = xpos; 		//x coord
				m_Vertices[index + 1]= GridHeight;  // y coord
				m_Vertices[index + 2]= zpos;		// z coord
							
				// Increment index counter for next vertex
				index = index + 3;
			}
		}
		
		// Create DrawList for Grid
		int DrawListEntriesX = (GridSizeX-1) * 2;
		int TotalDrawListEntriesX = GridSizeZ * DrawListEntriesX;
		
		int DrawListEntriesZ = (GridSizeZ-1) * 2;
		int TotalDrawListEntriesZ = GridSizeX * DrawListEntriesZ;
		
		int TotalDrawListEntries = TotalDrawListEntriesX + TotalDrawListEntriesZ;
		
		Log.e("GRAVITYGRIDEX" , "TotalDrawListEntries = " + TotalDrawListEntries);
		m_DrawOrder = new short[TotalDrawListEntries];
		
		index = 0;
		for (int z = 0; z < GridSizeZ; z++)
		{
			// Create Draw List for Horizontal Lines
			for (int x = 0; x < (GridSizeX-1);x++)
			{
				if (index >= TotalDrawListEntries)
				{
					Log.e("GRAVITYGRIDEX" , "Array Out of Bounds ERRROR- Horizontal, Index >= TotalDrawListEntries");
				}
				
				
				int CurrentVertexIndex = (z*GridSizeX) + x;
				m_DrawOrder[index] 	  = (short)CurrentVertexIndex;
				m_DrawOrder[index + 1]= (short)(CurrentVertexIndex + 1);
				
				index = index + 2;	
			}
		}
		
		for (int z = 0; z < (GridSizeZ-1); z++)
		{
			// Create Draw List for Vertical Lines
			for (int x = 0; x < (GridSizeX);x++)
			{
				if (index >= TotalDrawListEntries)
				{
					Log.e("GRAVITYGRIDEX" , "Array Out of Bounds ERRROR-Vertical, Index >= TotalDrawListEntries");
				}
				
				
				int CurrentVertexIndex = (z*GridSizeX) + x;
				int VertexIndexBelowCurrent = CurrentVertexIndex + GridSizeX;
				
				m_DrawOrder[index] 	  = (short)CurrentVertexIndex;
				m_DrawOrder[index + 1]= (short)VertexIndexBelowCurrent;
				
				index = index + 2;
			}
		}
		
		
		// Create Mesh
		m_LineMeshGrid = new MeshEx(m_CoordsPerVertex, 
				 					m_MeshVerticesDataPosOffset, 
				 					m_MeshVerticesUVOffset, 
				 					m_MeshVerticesNormalOffset,
				 					m_Vertices,
				 					m_DrawOrder);
		

		m_LineMeshGrid.SetMeshType(MeshType.Lines);
		
		
		
		// Clear Value of Masses
		ClearMasses();
	}
	
	int GetMaxMasses()
	{
		return MAX_MASSES;
	}
	
	int GetNumberMassesOnGrid()
	{
		return m_NumberMasses;
	}
		
	float GetXMinBoundary()
	{
		return m_XMinBoundary;
	}
	
	float GetXMaxBoundary() 
	{
		return m_XMaxBoundary;
	}
		
	float GetZMinBoundary()
	{
		return m_ZMinBoundary;
	}
	
	float GetZMaxBoundary()
	{
		return m_ZMaxBoundary;
	}

	/*
	boolean SetMassSpotLightColor(int NumberComponents, float[] Color)
	{	
		if(NumberComponents > MAX_MASSES*3)
		{
			Log.e("GRAVITY GRID" , "SetMassSpotLightColor(), To many values in Color array ");
			return false;
		}
		
		for (int i = 0 ; i < NumberComponents; i++)
		{
			m_MassSpotLightColor[i]= Color[i];	
		}
		
		return true;
	}
	*/
	
	

	void ClearMasses()
	{
		//int MassLocationIndex = 0;
		
		for (int i = 0; i < MAX_MASSES; i++)
		{
			m_MassValues[i] = 0;
		}
	}
	
	void ResetGrid()
	{
		// Clears Grid of All Masses
		MassesIndex = 0;
		m_NumberMasses = 0;
		ClearMasses();
	}
	
	/*
	void SetMasses(int NumberMasses, Object3d[] Masses)
	{
		int MassLocationIndex = 0;
		
		m_NumberMasses = NumberMasses; 
		
		ClearMasses();
		
		for (int i = 0; i < m_NumberMasses; i++)
		{
			m_MassValues[i] = Masses[i].GetObjectPhysics().GetMass();  
			
			m_MassLocations[MassLocationIndex]		= Masses[i].m_Orientation.GetPosition().x; 
			m_MassLocations[MassLocationIndex + 1]	= Masses[i].m_Orientation.GetPosition().y; 
			m_MassLocations[MassLocationIndex + 2]	= Masses[i].m_Orientation.GetPosition().z; 
			MassLocationIndex = MassLocationIndex + 3;
			
			m_MassEffectiveRadius[i] = Masses[i].GetObjectPhysics().GetMassEffectiveRadius();
			
		}
	}
	*/
	
	
	boolean AddMass(Object3d Mass)
	{
		boolean result = true;
		
		int MassLocationIndex 		= MassesIndex * 3; // each mass has 3 components x,y,z
		int SpotLightLocationIndex 	= MassesIndex * 3; // each spotlight has 3 components r,g,b
		
		if (m_NumberMasses >= MAX_MASSES)
		{
			result = false;
			return result;
		}
	
		float[] Color;
		
		
		// Add Value of the Mass
		//Log.e("GRAVITY GRID EX", "MassesIndex = " + MassesIndex);
		m_MassValues[MassesIndex] = Mass.GetObjectPhysics().GetMass();  
		
		// Add the x,y,z location of the Mass
		m_MassLocations[MassLocationIndex]		= Mass.m_Orientation.GetPosition().x; 
		m_MassLocations[MassLocationIndex + 1]	= Mass.m_Orientation.GetPosition().y; 
		m_MassLocations[MassLocationIndex + 2]	= Mass.m_Orientation.GetPosition().z; 
		MassLocationIndex = MassLocationIndex + 3;
			
		// Add the Radius of the Spotlight for the Mass
		m_MassEffectiveRadius[MassesIndex] = Mass.GetObjectPhysics().GetMassEffectiveRadius();
		
		// Add the SpotLight Color for the mass
		Color = Mass.GetGridSpotLightColor();
		m_MassSpotLightColor[SpotLightLocationIndex] 	 = Color[0];
		m_MassSpotLightColor[SpotLightLocationIndex + 1] = Color[1];
		m_MassSpotLightColor[SpotLightLocationIndex + 2] = Color[2];
		SpotLightLocationIndex = SpotLightLocationIndex + 3;
		
		
		MassesIndex++;
		m_NumberMasses++;
		
		
		return result;
	}
	
	boolean AddMasses(int iNumberMasses, Object3d[] Masses)
	{
		boolean result = true;
		
		int MassLocationIndex 		= MassesIndex * 3; // each mass has 3 components x,y,z
		int SpotLightLocationIndex 	= MassesIndex * 3; // each spotlight has 3 components r,g,b
		
		float[] Color;
		for (int i = 0; i < iNumberMasses; i++)
		{
			if (m_NumberMasses >= MAX_MASSES)
			{
				return false;
			}
			
			// Add Value of the Mass
			m_MassValues[MassesIndex] = Masses[i].GetObjectPhysics().GetMass();  
			
			// Add the x,y,z location of the Mass
			m_MassLocations[MassLocationIndex]		= Masses[i].m_Orientation.GetPosition().x; 
			m_MassLocations[MassLocationIndex + 1]	= Masses[i].m_Orientation.GetPosition().y; 
			m_MassLocations[MassLocationIndex + 2]	= Masses[i].m_Orientation.GetPosition().z; 
			MassLocationIndex = MassLocationIndex + 3;
			
			// Add the Radius of the Spotlight for the Mass
			m_MassEffectiveRadius[MassesIndex] = Masses[i].GetObjectPhysics().GetMassEffectiveRadius();
		
			// Add the SpotLight Color for the mass
			Color = Masses[i].GetGridSpotLightColor();
			m_MassSpotLightColor[SpotLightLocationIndex] 	 = Color[0];
			m_MassSpotLightColor[SpotLightLocationIndex + 1] = Color[1];
			m_MassSpotLightColor[SpotLightLocationIndex + 2] = Color[2];
			SpotLightLocationIndex = SpotLightLocationIndex + 3;
			
			
			MassesIndex++;
			m_NumberMasses++;
		}
		
		return result;
	}
	
	void SetUpShader()
	{
	    // Add program to OpenGL environment
	    m_Shader.ActivateShader();

	    // get handle to vertex shader's vPosition member
	    m_PositionHandle = m_Shader.GetShaderVertexAttributeVariableLocation("aPosition");
	        
	    // Set Gravity Line Variables
	    //m_Shader.SetShaderUniformVariableValue("time", CalcGridHeightOffset());
	    m_Shader.SetShaderUniformVariableValueInt("NumberMasses", m_NumberMasses);
		   
	    m_Shader.SetShaderVariableValueFloatVector1Array("MassValues", MAX_MASSES, m_MassValues, 0);
	    m_Shader.SetShaderVariableValueFloatVector3Array("MassLocations", MAX_MASSES, m_MassLocations, 0);
	    m_Shader.SetShaderVariableValueFloatVector1Array("MassEffectiveRadius", MAX_MASSES, 
	        										     m_MassEffectiveRadius, 0);
	    
	    m_Shader.SetShaderVariableValueFloatVector3Array("SpotLightColor",MAX_MASSES, m_MassSpotLightColor, 0);
	    
	    // Set Color of Line
	    m_Shader.SetShaderUniformVariableValue("vColor", m_GridColor);
	        
	    // Set View Proj Matrix
	    m_Shader.SetShaderVariableValueFloatMatrix4Array("uMVPMatrix", 1, false, m_MVPMatrix, 0);
	}
	
	void GenerateMatrices(Camera Cam)
	{
		Matrix.multiplyMM(m_MVPMatrix, 0, Cam.GetProjectionMatrix(), 0, Cam.GetViewMatrix(), 0);
	}
	
	void DrawGrid(Camera Cam)
	{
		// Set up Shader
		GenerateMatrices(Cam);
		SetUpShader();
		
		// Draw Mesh
		m_LineMeshGrid.DrawMesh(m_PositionHandle, -1, -1);
	}
	
	
}
