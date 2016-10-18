package com.robsexample.glhelloworld;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;



public class ArenaObjectSet 
{

	private Context m_Context;
	private int MAX_ARENA_OBJECTS = 25;
	private int m_NumberArenaObjects = 0;
	
	private ArenaObject3d[] m_ArenaObjectSet = new ArenaObject3d[MAX_ARENA_OBJECTS];
	private boolean[] m_Active = new boolean[MAX_ARENA_OBJECTS];
	
	private float m_ExplosionMinVelocity = 0.02f;
	private float m_ExplosionMaxVelocity = 0.4f;
	
	//private Vector3 m_Force = new Vector3(0,25,0);
	
	ArenaObjectSet(Context iContext)
	{
		m_Context = iContext;
		Init();
	}
	
	void SaveSet(String Handle)
	{
		SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
		SharedPreferences.Editor editor = settings.edit();
		     
		for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
		{
			// Active Status
			String ActiveHandle = Handle + "Active" + i; 
			editor.putBoolean(ActiveHandle, m_Active[i]);
		
			if (m_ArenaObjectSet[i] != null)
			{
				String ArenaObjectHandle = Handle + "ArenaObject" + i;
				m_ArenaObjectSet[i].SaveObjectState(ArenaObjectHandle);
			}
		}
		
		// Commit the edits!
		editor.commit();
	}
	
	void LoadSet(String Handle)
	{
		  // Restore preferences
	      SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
	
	      for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
	      {
			// Active Status
			String ActiveHandle = Handle + "Active" + i; 
			m_Active[i] = settings.getBoolean(ActiveHandle, false);
			
			if (m_ArenaObjectSet[i] != null)
			{
				String ArenaObjectHandle = Handle + "ArenaObject" + i;
				m_ArenaObjectSet[i].LoadObjectState(ArenaObjectHandle);
			}
	      }      
	}
	
	void ResetSet()
	{
		// Sets all objects to inactive and invisible
		for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
		{
			if (m_ArenaObjectSet[i] != null)
			{
				m_Active[i] = false;
				m_ArenaObjectSet[i].SetVisibility(false);
			}
		}
	}

	void Init()
	{
		for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
		{
			m_ArenaObjectSet[i] = null;
			m_Active[i] = false;
		}
	}
	
	int NumberActiveArenaObjects()
	{
		int NumberActiveVehicles = 0;
		
		for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
		{
			if (m_Active[i] == true)
			{
				NumberActiveVehicles++;
			}
		}
	
		return NumberActiveVehicles;	
	}

	int TotalNumberArenaObjects()
	{
		return m_NumberArenaObjects;
	}
	
	ArenaObject3d GetArenaObject(int Index)
	{
		ArenaObject3d Obj = null;
		
		if ((Index < 0) || (Index >= m_NumberArenaObjects))
		{
			return null;
		}
		
		Obj = m_ArenaObjectSet[Index];
	
		return Obj;
	}

	ArenaObject3d GetAvailableArenaObject()
	{
		ArenaObject3d temp = null;
	
		for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
		{
			if (m_ArenaObjectSet[i] != null)
			{
				if (m_Active[i] == false)
				{
					m_ArenaObjectSet[i].SetVisibility(true);
					m_Active[i] = true;
					return m_ArenaObjectSet[i];
				}
			}
		}
		
		return temp;
	}

	ArenaObject3d GetRandomAvailableArenaObject()
	{
		ArenaObject3d Obj = null;
		
		Random RandomNumber = new Random();
		int RandomIndex = 0;
		
		int AvailableObjectsIndex = 0;
		int[] AvailableObjects = new int[MAX_ARENA_OBJECTS];
				
		// Build list of available objects
		for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
		{
			if (m_ArenaObjectSet[i] != null)
			{
				if (m_Active[i] == false)
				{
					AvailableObjects[AvailableObjectsIndex] = i;
					AvailableObjectsIndex++;
				}
			}
		}
		
		// If there are Available Objects then choose one at random from the list of available objects
		if (AvailableObjectsIndex > 0)
		{
			// Find Random Object from array of available objects
			RandomIndex = RandomNumber.nextInt(AvailableObjectsIndex);
		
			int ObjIndex = AvailableObjects[RandomIndex]; 
			Obj =  GetArenaObject(ObjIndex);
		
			if (Obj != null)
			{
				Obj.SetVisibility(true);
				m_Active[ObjIndex] = true;
			}
			else
			{
				Log.e("ARENAOBJECTSSET", "Random Arena OBJECT = NULL ERROR!!!! ");
			}
		
		}
		
		return Obj;
	}
	
	boolean AddNewArenaObject(ArenaObject3d ArenaObj)
	{
		boolean result = false;
		
		for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
		{
			if (m_ArenaObjectSet[i] == null)
			{
				ArenaObj.SetVisibility(false);
				m_ArenaObjectSet[i] = ArenaObj;
				//m_Active[i] = true;
				m_NumberArenaObjects++;
				return true;
			}
		}
		
		return result;
	}
	
	// SFX ON/OFF
	void SetSoundOnOff(boolean Value)
	{
		for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
		{
			if (m_ArenaObjectSet[i] != null)
			{
				m_ArenaObjectSet[i].SetSFXOnOff(Value);
			}
		}
	}
	
	// Check Collisions with Weapon
	int ProcessCollisionsWeapon(Weapon iWeapon)
	{
		int TotalKillValue = 0;
		
		for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
		{
			if ((m_ArenaObjectSet[i] != null) && (m_Active[i] == true))
			{
				Object3d CollisionObj = iWeapon.CheckAmmoCollision(m_ArenaObjectSet[i]);
		    	if (CollisionObj != null)
		    	{
		    		CollisionObj.ApplyLinearImpulse(m_ArenaObjectSet[i]);
		    		SphericalPolygonExplosion Exp = m_ArenaObjectSet[i].GetExplosion(0);
		    		Exp.StartExplosion(m_ArenaObjectSet[i].m_Orientation.GetPosition(),
		    						   m_ExplosionMaxVelocity, 
		    						   m_ExplosionMinVelocity);
		    		m_ArenaObjectSet[i].PlayExplosionSFX();
		    		
		    		// Process Damage
		    		m_ArenaObjectSet[i].TakeDamage(CollisionObj);
		    		int Health = m_ArenaObjectSet[i].GetObjectStats().GetHealth();
		    		if (Health <= 0)
		    		{
		    			int KillValue = m_ArenaObjectSet[i].GetObjectStats().GetKillValue();
		    			TotalKillValue = TotalKillValue + KillValue;
		    			m_Active[i] = false;
		    			m_ArenaObjectSet[i].SetVisibility(false);
		    		}
		    		
		    	}	
			}
		}

		return TotalKillValue;
		
	}

	// Add in all the Air vehicles in the fleet to the gravity grid
	void AddArenaObjectsToGravityGrid(GravityGridEx iGrid)
	{
		for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
		{
			if ((m_ArenaObjectSet[i] != null) && (m_Active[i] == true))
			{
				// Add Mass of AirVehicle to grid
				iGrid.AddMass(m_ArenaObjectSet[i]); 	
			}
		}
	}
	
	ArenaObject3d GetArenaObject(String ID)
	{
		ArenaObject3d temp = null;
		
		for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
		{
			if (m_ArenaObjectSet[i] != null)
			{
				if ((m_Active[i]== true) && (m_ArenaObjectSet[i].GetArenaObjectID() == ID))
				{
					return m_ArenaObjectSet[i];
				}
			}
		}
		
		return temp;
	}
	
	int ProcessCollisionWithObject(Object3d Obj)
	{
		int TotalKillValue = 0;
		
		for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
		{
			if ((m_ArenaObjectSet[i] != null) && (m_Active[i] == true))
			{
				Physics.CollisionStatus result = Obj.CheckCollision(m_ArenaObjectSet[i]);
				if ((result == Physics.CollisionStatus.COLLISION) ||
					(result == Physics.CollisionStatus.PENETRATING_COLLISION))
				{	
					// Process Collision
					Obj.ApplyLinearImpulse(m_ArenaObjectSet[i]);
					
					
					//m_ArenaObjectSet[i].GetObjectPhysics().ApplyTranslationalForce(m_Force);
				
					// Arena Object Explosion
		    		SphericalPolygonExplosion Exp = m_ArenaObjectSet[i].GetExplosion(0);
		    		if (Exp != null)
		    		{
		    			Exp.StartExplosion(m_ArenaObjectSet[i].m_Orientation.GetPosition(),
		    						   	   m_ExplosionMaxVelocity, 
		    						   	   m_ExplosionMinVelocity);
		    			m_ArenaObjectSet[i].PlayExplosionSFX();
		    		}
		    		
		    		// Pyramid Explosion
		    		Exp = Obj.GetExplosion(0);
		    		if (Exp != null)
		    		{
		    			Exp.StartExplosion(Obj.m_Orientation.GetPosition(),
		    						   	   m_ExplosionMaxVelocity, 
		    						   	   m_ExplosionMinVelocity);
		    			//Obj.PlaySound(0);
		    		}
		    		
		    		
		    		
		    		
		    		// Process Damage
		    		Obj.TakeDamage(m_ArenaObjectSet[i]);
		    		
		    		m_ArenaObjectSet[i].TakeDamage(Obj);
		    		int Health = m_ArenaObjectSet[i].GetObjectStats().GetHealth();
		    		if (Health <= 0)
		    		{
		    			int KillValue = m_ArenaObjectSet[i].GetObjectStats().GetKillValue();
		    			TotalKillValue = TotalKillValue + KillValue;
		    			m_Active[i] = false;
		    			m_ArenaObjectSet[i].SetVisibility(false);
		    		}
		    		
		    	
		    	
				}
			}
		}

		return TotalKillValue;
		
	}
	
	void RenderArenaObjects(Camera Cam, PointLight Light, boolean DebugOn)
	{
		for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
		{
			if (m_ArenaObjectSet[i] != null)
			{
				m_ArenaObjectSet[i].RenderArenaObject(Cam, Light);
			}
		}
	}
	
	void UpdateArenaObjects()
	{
		for (int i = 0; i < MAX_ARENA_OBJECTS; i++)
		{
			if (m_ArenaObjectSet[i] != null)
			{
				m_ArenaObjectSet[i].UpdateArenaObject();
			}
		}
		
	}
	
	
	
	
}
