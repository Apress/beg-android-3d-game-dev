package com.robsexample.glhelloworld;


import android.content.Context;
import android.media.SoundPool;


public class Ammunition extends Object3d
{
	private boolean	m_FireStatus = false;
	private boolean	m_AmmunitionSpent = false;	
	private float	m_AmmunitionRange = 50; //100.0f;
	private Vector3	m_AmmunitionStartPosition = new Vector3(0,0,0);
	private float	m_AmmoSpeed = 0.5f;

	private int m_FireSFXIndex = -1;

	Ammunition(Context iContext, 
			   Mesh iMesh, 
			   MeshEx iMeshEx, 
			   Texture[] iTextures, 
			   Material iMaterial, 
			   Shader iShader, 
			   //Shader LocalAxisShader,
			   
			   float AmmunitionRange,
			   float AmmunitionSpeed)
	{
		 super(iContext, 
			   iMesh, 
			   iMeshEx, 
			   iTextures, 
			   iMaterial, 
			   iShader//, 
			   //LocalAxisShader
			   );
		 
		 
		 m_AmmunitionRange = AmmunitionRange;
		 m_AmmoSpeed = AmmunitionSpeed;
		 
		 GetObjectPhysics().SetMass(1.0f);
	}
	
	void CreateFiringSFX(SoundPool Pool, int ResourceID)
	{
		m_FireSFXIndex = AddSound(Pool, ResourceID);
	}
	
	void PlayFiringSFX()
	{
		if (m_FireSFXIndex >= 0)
		{
			PlaySound(m_FireSFXIndex);
		}
	}
	
	float GetAmmunitionRange()
	{
		return m_AmmunitionRange;
	}

	void SetAmmunitionRange(float range)
	{
		m_AmmunitionRange = range;
	}

	boolean IsSpent()
	{
		return	m_AmmunitionSpent;
	}
	
	boolean IsFired()
	{
		return m_FireStatus;
	}

	void SetSpent(boolean value)
	{
		m_AmmunitionSpent = value;
	}

	void SetFired(boolean fired)
	{
		m_FireStatus = fired;
	}

	void Reset()
	{
		m_FireStatus = false;
		m_AmmunitionSpent = false;
	
		GetObjectPhysics().GetVelocity().Set(0, 0, 0);
		
		// Reset Emitters
		/*
		int NumEmitters = GetNumberPolyEmitters();
		ParticlePolyEmitter Emitter = null;
		
		for (int i = 0; i < NumEmitters; i++)
		{
			Emitter = GetPolyEmitter(i);
			Emitter.ResetParticleEmitter();
		}
		*/
		
	}

	Vector3 GetAmmunitionStartPosition()
	{
		return m_AmmunitionStartPosition;
	}

	void  SetAmmunitionStartPosition(Vector3 pos)
	{
		m_AmmunitionStartPosition = pos;
	}	

	void Fire(Vector3 Direction, 
			  Vector3 AmmoPosition, 
			  Vector3 OffSetVelocity)
	{		
		// 1. Set Fire Status to true
		m_FireStatus = true;

		// 2. Set direction and speed of Ammunition	
		// Velocity of Ammo
		Vector3 DirectionAmmo = new Vector3(Direction.x, Direction.y, Direction.z);
		DirectionAmmo.Normalize();
		
		Vector3 VelocityAmmo = Vector3.Multiply(m_AmmoSpeed, DirectionAmmo);
		
		// Velocity of Object with Weapon that has fired Ammo
		// Total Velocity
		Vector3 VelocityTotal = Vector3.Add(OffSetVelocity , VelocityAmmo);

		GetObjectPhysics().SetVelocity(VelocityTotal);
		m_Orientation.GetPosition().Set(AmmoPosition.x, AmmoPosition.y, AmmoPosition.z);

		// 3. Set Ammunition Initial World Position
		m_AmmunitionStartPosition.Set(AmmoPosition.x, AmmoPosition.y, AmmoPosition.z);
	}

	/*
	boolean IsAmmoTrailFinished(int EmitterNumber)
	{
		ParticlePolyEmitter Emitter = null;
		
		Emitter = GetPolyEmitter(EmitterNumber);
		
		if (Emitter == null)
		{
			return true;
		}
		
		int NumLockedParticles = Emitter.GetNumberLockedParticles();

		if (NumLockedParticles == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	*/
	
	/*
	boolean AllAmmoTrailsFinished()
	{
		boolean AllFinished = true;
		int NumberEmitters = GetNumberPolyEmitters();
		for (int i = 0; i < NumberEmitters; i++)
		{
			boolean result = IsAmmoTrailFinished(i);
			if (result == false)
			{
				AllFinished = false;
			}
		}
		
		return AllFinished;
	}
	*/
	
	/*
	void EmitAmmunitionParticles(float ForceAvg,
			  					 Vector3 MainDirectionVector,
			  					 long CurrentTime,						   
			  					 int NumberParticlesToLaunch,
			  					 int MaxLaunchDelay,
			  					 int EmitterNumber)
	{
		// Emit A Particle Cluster from Ammunition
		ParticlePolyEmitter Emitter = null;
				
		Emitter = GetPolyEmitter(EmitterNumber);
		if (Emitter != null)
		{
			//Emitter.EmitParticleCluster(ForceAvg, MainDirectionVector, CurrentTime, NumberParticlesToLaunch, MaxLaunchDelay);
			Emitter.EmitParticlesAlongVector(NumberParticlesToLaunch, ForceAvg, MaxLaunchDelay, MainDirectionVector, CurrentTime);
		
		}
	}
	*/
	
	void RenderAmmunition(Camera Cam, PointLight light, boolean DebugOn)
	{
		//DrawObject(Cam, light, DebugOn);
		DrawObject(Cam, light);
	}
	
	void UpdateAmmunition()
	{
		// 1. Update Ammunition Physics, Position, Rotation
		UpdateObject3d();
	}
	
	
	
	
}
