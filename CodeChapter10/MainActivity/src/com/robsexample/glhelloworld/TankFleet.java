package com.robsexample.glhelloworld;


import android.content.Context;
import android.content.SharedPreferences;

public class TankFleet 
{
	
	private Context m_Context;
	private int MAX_TANKS = 5;
	
	private Tank[] m_TankFleet = new Tank[MAX_TANKS];
	private boolean[] m_InService = new boolean[MAX_TANKS];
	
	private float m_VehicleExplosionMinVelocity = 0.02f;
	private float m_VehicleExplosionMaxVelocity = 0.4f;
	
	private Vector3 m_OffScreen = new Vector3(0,50000,0);
	
	TankFleet(Context iContext)
	{
		m_Context = iContext;
		Init();
	}

	void SaveSet(String Handle)
	{
		SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
		SharedPreferences.Editor editor = settings.edit();
		     
		for (int i = 0; i < MAX_TANKS; i++)
		{
			// Active Status
			String ActiveHandle = Handle + "InService" + i; 
			editor.putBoolean(ActiveHandle, m_InService[i]);
		
			if (m_TankFleet[i] != null)
			{
				String ObjectHandle = Handle + "TankFleet" + i;
				m_TankFleet[i].SaveTankState(ObjectHandle);
			}
		}
		
		// Commit the edits!
		editor.commit();
	}
	
	void LoadSet(String Handle)
	{
		  // Restore preferences
	      SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
	
	      for (int i = 0; i < MAX_TANKS; i++)
	      {
			// Active Status
			String ActiveHandle = Handle + "InService" + i; 
			m_InService[i] = settings.getBoolean(ActiveHandle, false);
			
			if (m_TankFleet[i] != null)
			{
				String ObjectHandle = Handle + "TankFleet" + i;
				m_TankFleet[i].LoadTankState(ObjectHandle);
			}
	      }      
	}
	
	void ResetSet()
	{
		// Sets all objects to inactive and invisible
		for (int i = 0; i < MAX_TANKS; i++)
		{
			if (m_TankFleet[i] != null)
			{
				m_InService[i] = false;
				m_TankFleet[i].GetMainBody().SetVisibility(false);
				m_TankFleet[i].GetTurret().SetVisibility(false);
				m_TankFleet[i].Reset();
			}
		}
	}
	
	void Init()
	{
		for (int i = 0; i < MAX_TANKS; i++)
		{
			m_TankFleet[i] = null;
			m_InService[i] = false;
		}
	}
	
	int NumberActiveVehicles()
	{
		int NumberActiveVehicles = 0;
		
		for (int i = 0; i < MAX_TANKS; i++)
		{
			if (m_InService[i] == true)
			{
				NumberActiveVehicles++;
			}
		}
	
		return NumberActiveVehicles;	
	}

	Tank GetAvailableTank()
	{
		Tank temp = null;
	
		for (int i = 0; i < MAX_TANKS; i++)
		{
			if (m_TankFleet[i] != null)
			{
				if (m_InService[i] == false)
				{
					m_TankFleet[i].GetMainBody().SetVisibility(true);
					m_TankFleet[i].GetTurret().SetVisibility(true);
					m_InService[i] = true;
					return m_TankFleet[i];
				}
			}
		}
		
		return temp;
	}

	boolean AddNewTankVehicle(Tank Vehicle)
	{
		boolean result = false;
		
		for (int i = 0; i < MAX_TANKS; i++)
		{
			if (m_TankFleet[i] == null)
			{
				Vehicle.GetMainBody().SetVisibility(false);
				Vehicle.GetTurret().SetVisibility(false);
				m_TankFleet[i] = Vehicle;
				//m_InService[i] = true;
				return true;
			}
		}
		
		return result;
	}
	
	// SFX ON/OFF
	void SetSoundOnOff(boolean Value)
	{
		for (int i = 0; i < MAX_TANKS; i++)
		{
			if (m_TankFleet[i] != null)
			{
				m_TankFleet[i].GetMainBody().SetSFXOnOff(Value);
				m_TankFleet[i].GetTurret().SetSFXOnOff(Value);
				
				int NumberWeapons = m_TankFleet[i].GetNumberWeapons();
				for (int j = 0; j < NumberWeapons; j++)
				{
					m_TankFleet[i].GetWeapon(j).TurnOnOffSFX(Value);
				}
			}
		}
	}
	
	// Check Collisions with Weapon
	int ProcessCollisionsWeapon(Weapon iWeapon)
	{
		int TotalKillValue = 0;
		
		for (int i = 0; i < MAX_TANKS; i++)
		{
			if ((m_TankFleet[i] != null) && (m_InService[i] == true))
			{
				Object3d CollisionObj = iWeapon.CheckAmmoCollision(m_TankFleet[i].GetMainBody());
		    	if (CollisionObj != null)
		    	{
		    		CollisionObj.ApplyLinearImpulse(m_TankFleet[i].GetMainBody());
		    		SphericalPolygonExplosion Exp = m_TankFleet[i].GetMainBody().GetExplosion(0);
		    		Exp.StartExplosion(m_TankFleet[i].GetMainBody().m_Orientation.GetPosition(),
		    						   m_VehicleExplosionMaxVelocity, 
		    						   m_VehicleExplosionMinVelocity);
		    		m_TankFleet[i].PlayExplosionSFX();
		    		
		    		// Process Damage
		    		m_TankFleet[i].GetMainBody().TakeDamage(CollisionObj);
		    		int Health = m_TankFleet[i].GetMainBody().GetObjectStats().GetHealth();
		    		if (Health <= 0)
		    		{
		    			// Tank Killed
		    			int KillValue = m_TankFleet[i].GetMainBody().GetObjectStats().GetKillValue();
		    			TotalKillValue = TotalKillValue + KillValue;
		    			m_InService[i] = false;
		    			m_TankFleet[i].GetMainBody().SetVisibility(false);
		    			m_TankFleet[i].GetTurret().SetVisibility(false);
		    			
		    			// Move Killed Tank to Off Screen area 
		    			m_TankFleet[i].GetMainBody().m_Orientation.GetPosition().Set(m_OffScreen.x, m_OffScreen.y, m_OffScreen.z);
		    			m_TankFleet[i].GetTurret().m_Orientation.GetPosition().Set(m_OffScreen.x, m_OffScreen.y, m_OffScreen.z);;
		    		}
		    		
		    	}	
			}
		}

		return TotalKillValue;
		
	}

	// Add in all the Air vehicles in the fleet to the gravity grid
	void AddTankFleetToGravityGrid(GravityGridEx iGrid)
	{
		Object3d[] Masses = new Object3d[50];
		int NumberMasses = 0;
		
		for (int i = 0; i < MAX_TANKS; i++)
		{
			if ((m_TankFleet[i] != null) && (m_InService[i] == true))
			{
				// Add Mass of AirVehicle to grid
				iGrid.AddMass(m_TankFleet[i].GetMainBody()); 	
				
		    	// Add Weapons Fire from AirVehicle to grid
				int NumberWeapons = m_TankFleet[i].GetNumberWeapons();
				for (int j = 0; j < NumberWeapons; j++)
				{
					NumberMasses =  m_TankFleet[i].GetWeapon(j).GetActiveAmmo(0, Masses);
					iGrid.AddMasses(NumberMasses, Masses);
				}
			}
		}
	}
	
	
	Tank GetTankVehicle(String ID)
	{
		Tank temp = null;
		
		for (int i = 0; i < MAX_TANKS; i++)
		{
			if (m_TankFleet[i] != null)
			{
				if ((m_InService[i]== true) && (m_TankFleet[i].GetVehicleID() == ID))
				{
					return m_TankFleet[i];
				}
			}
		}
		
		return temp;
	}

	boolean ProcessWeaponAmmoCollisionObject(Object3d Obj)
	{
		Object3d CollisionObj = null;
		boolean hitresult = false;
		
		for (int i = 0; i < MAX_TANKS; i++)
		{
			if ((m_TankFleet[i] != null) && (m_InService[i] == true))
			{
				int NumberWeapons = m_TankFleet[i].GetNumberWeapons();
				
				for (int j=0; j < NumberWeapons; j++)
				{
					CollisionObj = m_TankFleet[i].GetWeapon(j).CheckAmmoCollision(Obj);
					if (CollisionObj != null)
					{
						hitresult = true;
						CollisionObj.ApplyLinearImpulse(Obj);
		    		
						//Process Damage
						Obj.TakeDamage(CollisionObj);
						
						// Obj Explosion
			    		SphericalPolygonExplosion Exp = Obj.GetExplosion(0);
			    		if (Exp != null)
			    		{
			    			Exp.StartExplosion(Obj.m_Orientation.GetPosition(),
			    						   	   m_VehicleExplosionMaxVelocity, 
			    						   	   m_VehicleExplosionMinVelocity);
			    			//Obj.PlayExplosionSFX();
			    		}
			    		
						//SphericalPolygonExplosion Exp = m_AirFleet[i].GetExplosion(0);
			    		//Exp.StartExplosion(m_ArenaObjectSet[i].m_Orientation.GetPosition(),
			    		//				   m_ExplosionMaxVelocity, 
			    		//				   m_ExplosionMinVelocity);
					}
				}
			}
		}
		
		return hitresult;
	}
	
	void RenderTankFleet(Camera Cam, PointLight Light, boolean DebugOn)
	{
		for (int i = 0; i < MAX_TANKS; i++)
		{
			if (m_TankFleet[i] != null)
			{
				m_TankFleet[i].RenderVehicle(Cam, Light, DebugOn);
			}
		}
	}
	
	void UpdateTankFleet()
	{
		for (int i = 0; i < MAX_TANKS; i++)
		{
			if (m_TankFleet[i] != null)
			{
				m_TankFleet[i].UpdateVehicle();
			}
		}
		
	}
	
	
	
	

}
