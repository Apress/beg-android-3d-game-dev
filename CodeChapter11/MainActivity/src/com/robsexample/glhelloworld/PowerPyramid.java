package com.robsexample.glhelloworld;


import android.content.Context;
import android.media.SoundPool;



public class PowerPyramid extends Object3d 
{
	private int m_ExplosionSFXIndex = -1;
	
	PowerPyramid(Context iContext, 
			 	 Mesh iMesh, 
			 	 MeshEx iMeshEx, 
			 	 Texture[] iTextures, 
			 	 Material iMaterial, 
			 	 Shader iShader//, 
			 	 //Shader LocalAxisShader
			 	 )
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
	
	
	
	// Sound Effects
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
	
	
	
}
