package com.robsexample.glhelloworld;





import android.content.Context;
import android.util.Log;




public class Weapon extends Object3d
{
	private int MAX_DEFAULTAMMO = 20; //10;
	private Ammunition[] m_WeaponClip = new Ammunition[MAX_DEFAULTAMMO];
	private long m_TimeLastFired = 0;
	private long m_TimeReadyToFire = 0;
	private long m_FireDelay = 500; // weapon fires once every 300 ms 
	
	// Ammunition particle Trails
	//private float m_ForceAvgParticle = 4;					   
	//private int   m_NumberParticlesToLaunch = 15;
	//private int   m_MaxLaunchDelay = 500;
	
	
	Weapon(Context iContext, 
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


	void TurnOnOffSFX(boolean value)
	{
		for (int i = 0; i < MAX_DEFAULTAMMO; i++)
		{
			m_WeaponClip[i].SetSFXOnOff(value);
		}
	}
	
	int GetMaxAmmunition()
	{
		return MAX_DEFAULTAMMO;	
	}
	
	
	/*
	
	void SetForceAvgParticle(float value)
	{
		m_ForceAvgParticle = value;					   
	}
	
	void SetNumberParticlesToLaunch(int value)
	{
		m_NumberParticlesToLaunch = value;
	}
	
	void SetMaxLaunchDelay(int value)
	{
		m_MaxLaunchDelay = value;
	}
*/
	
	
	
	
	
	
	void ResetWeapon()
	{
		// Reset All the Ammunition in the Weapon's Magazine
		for (int i = 0; i < MAX_DEFAULTAMMO; i++)
		{
			m_WeaponClip[i].Reset();
		}
	}

	void LoadAmmunition(Ammunition Ammo, int AmmoSlot)
	{
		if (AmmoSlot >= MAX_DEFAULTAMMO)
		{
			AmmoSlot = MAX_DEFAULTAMMO - 1;
		}
		
		m_WeaponClip[AmmoSlot] = Ammo;
	}
	
	int FindReadyAmmo()
	{
		for (int i = 0; i < MAX_DEFAULTAMMO; i++)
		{
			// If Ammo is not Fired and Ammo's dust trail is gone
			if ((m_WeaponClip[i].IsFired() == false) //&&					
			    //(m_WeaponClip[i].AllAmmoTrailsFinished() == true)
			    )
			{
				return i;
			}
		}

		return -1; // No More Ammo Available
	}

	Ammunition GetAmmo(int AmmoSlot)
	{
		if (AmmoSlot >= MAX_DEFAULTAMMO )
		{
			return null;
		}
		
		return m_WeaponClip[AmmoSlot];
	}
	
	boolean Fire(Vector3 Direction, Vector3 WeaponPosition)
	{
		boolean WeaponFired = false;
		
		// 0. Test if this weapon is ready to fire
		long CurrentTime = System.currentTimeMillis();
		if (CurrentTime < m_TimeReadyToFire)
		{
			return false;
		}
			
		// 1. Find Ammo That is not spent
		int AmmoSlot = FindReadyAmmo();

		// 2. If Ammo Found then Fire Ammunition
		if (AmmoSlot >= 0) 
		{
			WeaponFired = true;
			m_WeaponClip[AmmoSlot].Fire(Direction,WeaponPosition,GetObjectPhysics().GetVelocity());
		
			// Play SFX if available
			m_WeaponClip[AmmoSlot].PlayFiringSFX();
			
			
			// Create Particle trail for ammo
			/*
			Vector3 MainDirectionVector = new Vector3(-Direction.x, -Direction.y, -Direction.z);						   
			int NumberEmitters = m_WeaponClip[AmmoSlot].GetNumberPolyEmitters();
			for (int i = 0; i < NumberEmitters; i++)
			{
				m_WeaponClip[AmmoSlot].EmitAmmunitionParticles(m_ForceAvgParticle, 
														   	   MainDirectionVector, 
														   	   CurrentTime, 
														   	   m_NumberParticlesToLaunch, 
														   	   m_MaxLaunchDelay, 
														   	   i);
			}
			*/
			
		}
		else
		{
			Log.e("AMMUNITION ", "AMMUNITION NOT FOUND");
			WeaponFired = false;
		}
	
		// 3. Firing Delay
		m_TimeLastFired = System.currentTimeMillis();
		m_TimeReadyToFire = m_TimeLastFired + m_FireDelay;
		
		return WeaponFired; //true; // Weapon Fired
	}
	
	Object3d CheckAmmoCollision(Object3d obj)
	{
		Object3d ObjectCollided = null;
		
		for (int i = 0; i < MAX_DEFAULTAMMO; i++)
		{
			// If Ammunition is fired then Update Ammunition and Emit More AmmoDust Trail particles
			if (m_WeaponClip[i].IsFired() == true)
			{
				//Check Collision
				Physics.CollisionStatus result = m_WeaponClip[i].CheckCollision(obj);
				if ((result == Physics.CollisionStatus.COLLISION) ||
					(result == Physics.CollisionStatus.PENETRATING_COLLISION))
				{
					ObjectCollided = m_WeaponClip[i];
				}
			}
		}
		
		return ObjectCollided;
	}
	
	/*
	int GetActiveAmmo(Object3d[] ActiveAmmo)
	{
		// Put all active fired ammunition in ActiveAmmo array
		// and return the number of fired ammunition
		int AmmoNumber = 0;
		for (int i = 0; i < MAX_DEFAULTAMMO; i++)
		{
			if (m_WeaponClip[i].IsFired() == true)
			{
				ActiveAmmo[AmmoNumber] = m_WeaponClip[i];
				AmmoNumber++;
			}
		}
		return AmmoNumber;
	}
	*/
	
	/*
	int GetActiveAmmo(Object3d[] ActiveAmmo, float[] SpotLights)
	{
		// Put all active fired ammunition in ActiveAmmo array
		// and return the number of fired ammunition
		int AmmoNumber = 0;
		int SpotLightIndex = 0;
		for (int i = 0; i < MAX_DEFAULTAMMO; i++)
		{
			if (m_WeaponClip[i].IsFired() == true)
			{
				ActiveAmmo[AmmoNumber] = m_WeaponClip[i];
				
				
				float[] Color = m_WeaponClip[i].GetGridSpotLightColor();
				SpotLights[SpotLightIndex]	= Color[0];
				SpotLights[SpotLightIndex+1]= Color[1];
				SpotLights[SpotLightIndex+2]= Color[2];
				
				SpotLightIndex = SpotLightIndex + 3;
				
				AmmoNumber++;
			}
		}
		return AmmoNumber;
	}
	*/

	/*
	int GetActiveAmmo(int StartIndex, Object3d[] ActiveAmmo, float[] SpotLights)
	{
		// Put all active fired ammunition in ActiveAmmo array
		// and return the number of fired ammunition
		int AmmoNumber = StartIndex;
		int SpotLightIndex = StartIndex * 3; // Account for r,g,b components
		for (int i = 0; i < MAX_DEFAULTAMMO; i++)
		{
			if (m_WeaponClip[i].IsFired() == true)
			{
				ActiveAmmo[AmmoNumber] = m_WeaponClip[i];
				
				
				float[] Color = m_WeaponClip[i].GetGridSpotLightColor();
				SpotLights[SpotLightIndex]	= Color[0];
				SpotLights[SpotLightIndex+1]= Color[1];
				SpotLights[SpotLightIndex+2]= Color[2];
				
				SpotLightIndex = SpotLightIndex + 3;
				
				AmmoNumber++;
			}
		}
		return (AmmoNumber - StartIndex);
	}
	*/
	
	int GetActiveAmmo(int StartIndex, Object3d[] ActiveAmmo)
	{
		// Put all active fired ammunition in ActiveAmmo array
		// and return the number of fired ammunition
		int AmmoNumber = StartIndex;
		for (int i = 0; i < MAX_DEFAULTAMMO; i++)
		{
			if (m_WeaponClip[i].IsFired() == true)
			{
				ActiveAmmo[AmmoNumber] = m_WeaponClip[i];	
				AmmoNumber++;
			}
		}
		return (AmmoNumber - StartIndex);
	}
	
	void RenderWeapon(Camera Cam, PointLight light, boolean DebugOn)
	{
		// 1. Render Each Fired Ammunition in Weapon
		for (int i = 0; i < MAX_DEFAULTAMMO; i++)
		{
			// If Ammunition is fired then Update Ammunition and Emit More AmmoDust Trail particles
			if (m_WeaponClip[i].IsFired() == true)
			{
				m_WeaponClip[i].RenderAmmunition(Cam, light, DebugOn);	
			}
		}
	}

	void UpdateWeapon()
	{
		// 1. Update Each Ammunition in Weapon
		for (int i = 0; i < MAX_DEFAULTAMMO; i++)
		{
			// If Ammunition is fired then Update Ammunition and Emit More AmmoDust Trail particles
			if (m_WeaponClip[i].IsFired() == true)
			{
				// Add Spin to Ammunition
				m_WeaponClip[i].GetObjectPhysics().ApplyRotationalForce(30, 1);
				m_WeaponClip[i].UpdateAmmunition();

				// 2. Check if Ammunition is spent
				float	AmmoRange		=	m_WeaponClip[i].GetAmmunitionRange();
				Vector3	AmmoCurrentPos	=	m_WeaponClip[i].m_Orientation.GetPosition();
				Vector3	AmmoInitPos		=	m_WeaponClip[i].GetAmmunitionStartPosition();
				Vector3	DistanceVector	=	Vector3.Subtract(AmmoCurrentPos, AmmoInitPos);

				float DistanceMag = DistanceVector.Length();

				if (DistanceMag > AmmoRange)
				{
					// Ammo is Spent so Reset Ammunition to ready to use status.
					m_WeaponClip[i].Reset();
				}
			}
		}
	}


	
}
