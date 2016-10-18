package com.robsexample.glhelloworld;



import android.media.SoundPool;




public class Tank 
{
	
private String m_VehicleID = "None";
	
	private Driver m_Driver;
	private Object3d m_MainBody;
	private Object3d m_Turret;
	
	private Vector3 m_Heading = new Vector3(0,0,0);
	
	private int MAX_WEAPONS = 5;
	private int m_NumberWeapons = 0;
	private Weapon[] m_Weapons = new Weapon[MAX_WEAPONS];

	private Vector3 m_TurretOffset = new Vector3(0,0,0);
	
	private int m_HitGroundSFXIndex = -1;
	private int m_ExplosionSFXIndex = -1;
	
	
	
	
	Tank(Object3d MainBody, Object3d Turret, Vector3 TurretOffset)
	{
		m_MainBody = MainBody;
		m_Turret = Turret;
		m_TurretOffset = TurretOffset;
		
		// Create new Pilot for this vehicle
		m_Driver = new Driver(this);
	}
	
	void SaveTankState(String Handle)
	{
		//SharedPreferences settings = m_MainBody.GetContext().getSharedPreferences(Handle, 0);
		//SharedPreferences.Editor editor = settings.edit();
		  
		// Commit the edits!
	    //editor.commit();
	    
	    
		// Main Body 
		String MainBodyHandle = Handle + "MainBody";
		m_MainBody.SaveObjectState(MainBodyHandle);
	
		// Turret
		String TurretHandle = Handle + "Turret";
		m_Turret.SaveObjectState(TurretHandle);
		
		// Driver
		String DriverHandle = Handle + "Driver";
	    m_Driver.SaveDriverState(DriverHandle);
	}
	
	void LoadTankState(String Handle)
	{
		//SharedPreferences settings = m_MainBody.GetContext().getSharedPreferences(Handle, 0);
			
		//Driver
		String DriverHandle = Handle + "Driver";
		m_Driver.LoadDriverState(DriverHandle);
		
		// Main Body
		String MainBodyHandle = Handle + "MainBody";
		m_MainBody.LoadObjectState(MainBodyHandle);
	
		// Turret
		String TurretHandle = Handle + "Turret";
		m_Turret.LoadObjectState(TurretHandle);
		
		
	}
	
	void Reset()
	{
		// Reset Driver
		if (m_Driver != null)
		{
			m_Driver.DriverReset();
		}
		
		// Reset Weapons
		for (int i = 0; i < m_NumberWeapons; i++)
		{
			Weapon TempWeapon = m_Weapons[i];
			TempWeapon.ResetWeapon();
		}
	}
	
	// Vehicle ID
	String GetVehicleID()
	{
		return m_VehicleID;
	}
		
	void SetVehicleID(String ID)
	{
		m_VehicleID = ID;
	}

	void CreateHitGroundSFX(SoundPool Pool, int ResourceID)
	{
		m_HitGroundSFXIndex = m_MainBody.AddSound(Pool, ResourceID);
	}
	
	void PlayHitGoundSFX()
	{
		if (m_HitGroundSFXIndex >= 0)
		{
			m_MainBody.PlaySound(m_HitGroundSFXIndex);
		}
	}
	
	void CreateExplosionSFX(SoundPool Pool, int ResourceID)
	{
		m_ExplosionSFXIndex = m_MainBody.AddSound(Pool, ResourceID);
	}
	
	void PlayExplosionSFX()
	{
		if (m_ExplosionSFXIndex >= 0)
		{
			m_MainBody.PlaySound(m_ExplosionSFXIndex);
		}
	}
	
	Driver GetDriver()
	{
		return m_Driver;
	}
	
	Object3d GetMainBody()
	{
		return m_MainBody;
	}
	
	Object3d GetTurret()
	{
		return m_Turret;
	}
	
	Weapon GetWeapon(int WeaponNumber)
	{
		if (WeaponNumber < m_NumberWeapons)
		{
			return m_Weapons[WeaponNumber];
		}
		else
		{
			return null;
		}
	}
	
	int GetNumberWeapons()
	{
		return m_NumberWeapons;
	}
	
	boolean AddWeapon(Weapon iWeapon)
	{
		boolean result = false;
		
		if (m_NumberWeapons < MAX_WEAPONS)
		{
			m_Weapons[m_NumberWeapons] = iWeapon;
			m_NumberWeapons++;
			result = true;
		}
		
		return result;
	}

	boolean FireWeapon(int WeaponNumber, Vector3 Direction)
	{
		boolean result = false;
		
		if (WeaponNumber < m_NumberWeapons)
		{
			result = m_Weapons[WeaponNumber].Fire(Direction, m_Turret.m_Orientation.GetPosition());
		}

		return result;		
	}
		
	void RenderVehicle(Camera Cam, PointLight Light, boolean DebugOn)
	{	
		// Render Vehicle
		m_MainBody.DrawObject(Cam, Light);
		m_Turret.DrawObject(Cam, Light);
		
		// Render Vehicles Weapons and Ammunition if any
		for (int i = 0 ; i < m_NumberWeapons; i++)
		{
			m_Weapons[i].RenderWeapon(Cam, Light, DebugOn);
		}
		
		// Shake Camera if Tank hits ground
		boolean ShakeCamera = m_MainBody.GetObjectPhysics().GetHitGroundStatus();
		if (ShakeCamera)
		{
			//Cam.StartShake(m_MainBody.GetObjectPhysics().GetShockWaveMagnitude(), 
			//		       m_MainBody.GetObjectPhysics().GetShockWaveDuration(), 
			//		       System.currentTimeMillis());
			
			m_MainBody.GetObjectPhysics().ClearHitGroundStatus();
			PlayHitGoundSFX();
		}
	}

	void TurnTank(float TurnDelta)
	{
		Vector3 Axis = new Vector3(0,1,0);
		m_MainBody.m_Orientation.SetRotationAxis(Axis);
		m_MainBody.m_Orientation.AddRotation(TurnDelta);
	}
	
	void ProcessSteering()
	{
		Steering DriverSteering = m_Driver.GetAISteering();
		
		HorizontalSteeringValues 	HorizontalTurn 	= DriverSteering.GetHorizontalSteering();
		SpeedSteeringValues 		Acceleration 	= DriverSteering.GetSpeedSteering();
		
		float TurnDelta  = DriverSteering.GetTurnDelta();
		float MaxSpeed 	 = DriverSteering.GetMaxSpeed();
		float MinSpeed 	 = DriverSteering.GetMinSpeed();
		float SpeedDelta = DriverSteering.GetSpeedDelta();
		
		
		// Process Tank Steering
		
		// Process Right/Left Turn
		if (HorizontalTurn == HorizontalSteeringValues.Left)
		{	
			TurnTank(TurnDelta);
		}
		else if (HorizontalTurn == HorizontalSteeringValues.Right)
		{
			TurnTank(-TurnDelta);		
		}
		
		// Process Acceleration
		if (Acceleration == SpeedSteeringValues.Accelerate)
		{
			m_MainBody.GetObjectPhysics().SetMaxSpeed(MaxSpeed);
					
			Vector3 Force = new Vector3(0,0,30.0f);
		    m_MainBody.GetObjectPhysics().ApplyTranslationalForce(Force);
		    //Log.e("TANK" , "ACCELERATE");
		}
		else
		if (Acceleration == SpeedSteeringValues.Deccelerate)
		{
			float Speed = m_MainBody.GetObjectPhysics().GetVelocity().Length();
			if (Speed > MinSpeed)
			{
				float NewSpeed = Speed - SpeedDelta;
				m_MainBody.GetObjectPhysics().SetMaxSpeed(NewSpeed);
				//Log.e("TANK" , "DECELLERATE");
			}
		}
		
	}

	void TurnTurret(float TurnDelta)
	{
		Vector3 Axis = new Vector3(0,1,0);
		m_Turret.m_Orientation.SetRotationAxis(Axis);
		m_Turret.m_Orientation.AddRotation(TurnDelta);
	}
	
	void ProcessTurret()
	{
		Steering TurretSteering = m_Driver.GetTurretSteering();
		
		HorizontalSteeringValues HorizontalTurn = TurretSteering.GetHorizontalSteering();
		
		float TurnDelta  = TurretSteering.GetTurnDelta();
		
		// Process Right/Left Turn
		if (HorizontalTurn == HorizontalSteeringValues.Left)
		{	
			//Log.e("STATESTEERING" ,"Turret STEERING LEFT");	
			TurnTurret(TurnDelta);
		}
		else if (HorizontalTurn == HorizontalSteeringValues.Right)
		{
			//Log.e("STATESTEERING" ,"Turret STEERING RIGHT");	
			TurnTurret(-TurnDelta);		
		}
		
	}

	void UpdateVehicle()
	{
		if (m_MainBody.IsVisible())
		{
			// Update AIPilot
			m_Driver.Update();
		
			// Update Right/Left and Up/Down Rotation of Vehicle based on AIPilot's Steering
			ProcessSteering();
			
			// Process Turret Steering
			ProcessTurret();
		}
		
		// Update Vehicle Physics, Position, Rotation, and attached emitters and explosions
		m_Heading = m_MainBody.m_Orientation.GetForwardWorldCoords();
		m_MainBody.UpdateObject3dToHeading(m_Heading);
		
		
		if (m_MainBody.IsVisible())
		{
			// Tie Turret to Main Body
			Vector3 Pos = m_MainBody.m_Orientation.GetPosition();
			Vector3 ZOffset = Vector3.Multiply(m_TurretOffset.z, m_MainBody.m_Orientation.GetForwardWorldCoords());
			Vector3 XOffset = Vector3.Multiply(m_TurretOffset.x, m_MainBody.m_Orientation.GetRightWorldCoords());
			Vector3 YOffset = Vector3.Multiply(m_TurretOffset.y, m_MainBody.m_Orientation.GetUpWorldCoords());
		
			Vector3 OffsetPos = new Vector3(Pos);
			OffsetPos.Add(XOffset);
			OffsetPos.Add(YOffset);
			OffsetPos.Add(ZOffset);
		
			m_Turret.m_Orientation.GetPosition().Set(OffsetPos.x, OffsetPos.y,OffsetPos.z);
		}
		
		// Update Weapons and Ammunition
		for (int i = 0 ; i < m_NumberWeapons; i++)
		{
			m_Weapons[i].UpdateWeapon();
		}
	}
	
	
	

}
