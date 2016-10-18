package com.robsexample.glhelloworld;


import android.content.Context;
import android.media.SoundPool;





public class ArenaObject3d extends Object3d
{
	private String m_ArenaObjectID = "None";
	
	private float m_XMaxBoundary = 1;
	private float m_XMinBoundary = 0;
	
	private float m_ZMaxBoundary = 1;
	private float m_ZMinBoundary = 0;
	
	private int m_HitGroundSFXIndex = -1;
	private int m_ExplosionSFXIndex = -1;

	
	ArenaObject3d(Context iContext, 
			 	  Mesh iMesh, 
			 	  MeshEx iMeshEx, 
			 	  Texture[] iTextures, 
			 	  Material iMaterial, 
			 	  Shader iShader, 
			 	  //Shader LocalAxisShader,
			 
			 	  float XMaxBoundary,
			 	  float XMinBoundary,
			 	  float ZMaxBoundary,
			 	  float ZMinBoundary)
	{
		super(iContext, 
			  iMesh, 
			  iMeshEx, 
			  iTextures, 
			  iMaterial, 
			  iShader//, 
			  //LocalAxisShader
			  );
		
		m_XMaxBoundary =  XMaxBoundary;
	    m_XMinBoundary =  XMinBoundary;
	    m_ZMaxBoundary =  ZMaxBoundary;
		m_ZMinBoundary =  ZMinBoundary;
	}
	
	/*
	ArenaObject3d(Context iContext, 
			 	  int MeshResourceID,
			 	  int NumberTextures,
			 	  int[] TextureResourceID, 
			 	  Material iMaterial, 
			 	  int VertexShaderResourceID, int FragmentShaderResourceID, 
			 	  int LocalAxisVertexShaderResourceID,  int LocalAxisFragmentShaderResourceID,
			 	  
			 	  float XMaxBoundary,
			 	  float XMinBoundary,
			 	  float ZMaxBoundary,
			 	  float ZMinBoundary)
	{
	
		super(iContext, 
			  MeshResourceID,
			  NumberTextures,
			  TextureResourceID, 
			  iMaterial, 
			  VertexShaderResourceID, FragmentShaderResourceID, 
			  LocalAxisVertexShaderResourceID, LocalAxisFragmentShaderResourceID);
		
		m_XMaxBoundary =  XMaxBoundary;
	    m_XMinBoundary =  XMinBoundary;
	    m_ZMaxBoundary =  ZMaxBoundary;
		m_ZMinBoundary =  ZMinBoundary;
	}
	
	*/
	
	// ID
	String GetArenaObjectID()
	{
		return m_ArenaObjectID;
	}
		
	void SetArenaObjectID(String ID)
	{
		m_ArenaObjectID = ID;
	}
	
	
	
	void CreateExplosionSFX(SoundPool Pool, int ResourceID)
	{
		m_ExplosionSFXIndex = AddSound(Pool, ResourceID);
	}
	
	void PlayExplosionSFX()
	{
		if (m_ExplosionSFXIndex >= 0)
		{
			PlaySound(m_ExplosionSFXIndex);
		}
	}
	
	void CreateHitGroundSFX(SoundPool Pool, int ResourceID)
	{
		m_HitGroundSFXIndex = AddSound(Pool, ResourceID);
	}
	
	void PlayHitGoundSFX()
	{
		if (m_HitGroundSFXIndex >= 0)
		{
			PlaySound(m_HitGroundSFXIndex);
		}
	}
	
	void RenderArenaObject(Camera Cam, PointLight light)
	{
		// Shake Camera if Object hits ground
		boolean ShakeCamera = GetObjectPhysics().GetHitGroundStatus();
		if (ShakeCamera)
		{
			//Cam.StartShake(GetObjectPhysics().GetShockWaveMagnitude(), GetObjectPhysics().GetShockWaveDuration(), System.currentTimeMillis());
			GetObjectPhysics().ClearHitGroundStatus();
			PlayHitGoundSFX();
		}
		
		//DrawObject(Cam, light, DebugOn);
		DrawObject(Cam, light);
	}
	
	void UpdateArenaObject()
	{
		if (IsVisible() == true)
		{
			// Check Bounds for Z
			if (m_Orientation.GetPosition().z >= m_ZMaxBoundary)
			{
				Vector3 v = GetObjectPhysics().GetVelocity();
				if (v.z > 0)
				{
					v.z = -v.z;
				}
			}
			else
				if (m_Orientation.GetPosition().z <= m_ZMinBoundary)
				{
					Vector3 v = GetObjectPhysics().GetVelocity();
					if (v.z < 0)
					{
						v.z = -v.z;
					}
				}
	        
			// Check bounds for X
			if (m_Orientation.GetPosition().x >= m_XMaxBoundary)
			{
				Vector3 v = GetObjectPhysics().GetVelocity();
				if (v.x > 0)
				{
					v.x = -v.x;
				}
			}
			if (m_Orientation.GetPosition().x <= m_XMinBoundary)
			{
				Vector3 v = GetObjectPhysics().GetVelocity();
				if (v.x < 0)
				{
					v.x = -v.x;
				}
			}
	   
		}
	
	   	// Update Physics for this object    
	    UpdateObject3d();

	}
	
	
	
}
