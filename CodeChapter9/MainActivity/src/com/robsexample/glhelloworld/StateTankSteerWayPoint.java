package com.robsexample.glhelloworld;


import android.util.Log;



public class StateTankSteerWayPoint extends StateTank
{
	// WayPoint
	private Vector3 m_WayPoint 		 = new Vector3(0,0,0);
	private float	m_WayPointRadius = 0;
	private Vector3 m_LastWayPoint   = new Vector3(5000,5000,5000);
	
	
	// Weapon/Target Firing
	private float m_TargetAngleTolerance = Physics.PI/16.0f;
	private Vector3 m_Target;
	private Object3d m_TargetObj;
	
	private AIVehicleObjectsAffected m_WeaponType; 
	private float m_RoundsToFire = 0;
	private int m_NumberRoundsFired = 0;
	
	private long m_TimeIntervalBetweenFiring = 0;
	private long m_StartTimeFiring = 0;
	
	private boolean m_FireWeapon = false;
	
	

    StateTankSteerWayPoint(FSM_StatesTank ID, Driver Parent)
    {
    	super(ID, Parent);
    }
    
    void Init()
    {
    	
    }
    
    void Enter()
	{	
    	// Weapon is not firing when state is entered initially
    	m_NumberRoundsFired = 0;
    	m_FireWeapon = false;
    	
    	// Get WayPoint Data
    	m_WayPoint 		 = GetParent().GetWayPoint();
		m_WayPointRadius = GetParent().GetWayPointRadius();
		
		// Get Targeting and firing parameters
		m_Target = GetParent().GetCurrentOrder().GetTarget();
		m_TargetObj = GetParent().GetCurrentOrder().GetTargetObject();
		
		m_WeaponType = GetParent().GetCurrentOrder().GetObjectsAffected(); 	
		m_RoundsToFire = GetParent().GetCurrentOrder().GetDeltaAmount();
		m_TimeIntervalBetweenFiring = (long)GetParent().GetCurrentOrder().GetDeltaIncrement();
		
		// Tell the Pilot class what command is actually being executed in the FSM
		GetParent().SetCommandExecuting(AIVehicleCommand.Patrol);
	
		Log.e("STATETANKSTEERWAYPOINT" , "ENTERING STATE_TANKSTEERWAYPOINT, TANK CURRENTWAYPOINT = " + m_WayPoint.GetVectorString());
	}
	
    void Exit()
    {	
    	// Update Current Waypoint to next WayPoint
    	GetParent().IncrementNextWayPoint();
    	
    	//Log.e("STEERINGWAYPOINT", "EXITING STATESTEERWAYPOINT , NEXT WAYPOINT =  " + GetParent().GetWayPoint().GetVectorString());
    }

    void TurnTurretTowardTarget(Vector3 Target)
    {
    	// 1. Find vector from front of vehicle to target
    	Vector3 ForwardXZPlane = new Vector3(0,0,0); 	
    	ForwardXZPlane.x = GetParent().GetAIVehicle().GetTurret().m_Orientation.GetForwardWorldCoords().x;
    	ForwardXZPlane.z = GetParent().GetAIVehicle().GetTurret().m_Orientation.GetForwardWorldCoords().z;

    	Vector3 TurretPosition = new Vector3(0,0,0);
    	TurretPosition.x = GetParent().GetAIVehicle().GetTurret().m_Orientation.GetPosition().x;
    	TurretPosition.z = GetParent().GetAIVehicle().GetTurret().m_Orientation.GetPosition().z;

    	Vector3 WayPointXZPlane = new Vector3(Target.x, 0, Target.z);
    	Vector3 TurretToTarget = Vector3.Subtract(WayPointXZPlane, TurretPosition);

    	// 2. Normalize Vectors for Dot Product operation
    	ForwardXZPlane.Normalize();
    	TurretToTarget.Normalize();

    	// P.Q = P*Q*cos(theta)
    	// P.Q/P*Q = cos(theta)
    	// acos(P.Q/P*Q) = theta;

    	// 3. Get current theta
    	double Theta = Math.acos(ForwardXZPlane.DotProduct(TurretToTarget));
    		
    	// 4. Get Theta if boat is turned to left by PI/16
    	Orientation NewO = new Orientation(GetParent().GetAIVehicle().GetTurret().m_Orientation);
    	Vector3 Up = NewO.GetUp();
    	NewO.SetRotationAxis(Up);
    	NewO.AddRotation(Physics.PI/16);
    			
    	Vector3 NewForwardXZ = NewO.GetForwardWorldCoords();
    	NewForwardXZ.y = 0;
    	NewForwardXZ.Normalize();
    			
    	double Theta2 = Math.acos(NewForwardXZ.DotProduct(TurretToTarget));
    		
   
    	// Check if angle within tolerance for firing
    	//float Diff = Math.abs((float)(Theta2-Theta));
    	float Diff = Math.abs((float)(Theta));
    	if (!m_FireWeapon)
    	{
    		if (Diff <= m_TargetAngleTolerance)
    		{
    			m_FireWeapon = true;
    			m_StartTimeFiring = System.currentTimeMillis();
    			//Log.e("TANK" , "m_FIREWEAPON = TRUE");
    		}
    	}
     	
    	
    	
    	
    	// 5. Set Steering
    	if (Theta2 > Theta)	
    	{
    		GetParent().GetTurretSteering().SetSteeringHorizontal(HorizontalSteeringValues.Right, 1);
    		//Log.e("STATESTEERING" ,"Target = " + Target.GetVectorString()+ ", Turret STEERING RIGHT");		
    	}
    	else  
    	if (Theta2 < Theta)
    	{	
    		GetParent().GetTurretSteering().SetSteeringHorizontal(HorizontalSteeringValues.Left, 1);
    		//Log.e("STATESTEERING" , "Target = " + Target.GetVectorString()+ " ,Turret STEERING LEFT");
    	}
    	else
    	{		
    		GetParent().GetTurretSteering().SetSteeringHorizontal(HorizontalSteeringValues.None,0);
    		//Log.e("STATESTEERING" , "Target = " + Target.GetVectorString()+ ", Turret STEERING STRAIGHT");
    	}
    		
    	
    }
    
    void FireTurretWeapon()
    {
    	Vector3 TurretPos = GetParent().GetAIVehicle().GetTurret().m_Orientation.GetPosition();
		//Vector3 Direction = Vector3.Subtract(m_Target, TurretPos);
		Vector3 Direction = GetParent().GetAIVehicle().GetTurret().m_Orientation.GetForwardWorldCoords(); 
		boolean IsFired = false;
		
		if (m_WeaponType == AIVehicleObjectsAffected.PrimaryWeapon)
		{
			IsFired = GetParent().GetAIVehicle().FireWeapon(0, Direction);
		}
		else
		if (m_WeaponType == AIVehicleObjectsAffected.SecondaryWeapon)
		{
			IsFired = GetParent().GetAIVehicle().FireWeapon(1, Direction);
		}
		
		if (IsFired)
		{
			m_NumberRoundsFired++;
		}
    	
    }
    
    void SteerVehicleToWayPointHorizontal(Vector3 WayPoint)
    {
    	// 1. Find vector from front of vehicle to target
		Vector3 ForwardXZPlane = new Vector3(0,0,0); 	
		ForwardXZPlane.x = GetParent().GetAIVehicle().GetMainBody().m_Orientation.GetForwardWorldCoords().x;
		ForwardXZPlane.z = GetParent().GetAIVehicle().GetMainBody().m_Orientation.GetForwardWorldCoords().z;

		Vector3 VehiclePosition = new Vector3(0,0,0);
		VehiclePosition.x = GetParent().GetAIVehicle().GetMainBody().m_Orientation.GetPosition().x;
		VehiclePosition.z = GetParent().GetAIVehicle().GetMainBody().m_Orientation.GetPosition().z;

		Vector3 WayPointXZPlane = new Vector3(WayPoint.x, 0, WayPoint.z);
		Vector3 VehicleToWayPoint = Vector3.Subtract(WayPointXZPlane, VehiclePosition);

		// 2. Normalize Vectors for Dot Product operation
		ForwardXZPlane.Normalize();
		VehicleToWayPoint.Normalize();

		// P.Q = P*Q*cos(theta)
		// P.Q/P*Q = cos(theta)
		// acos(P.Q/P*Q) = theta;

		// 3. Get current theta
	    double Theta = Math.acos(ForwardXZPlane.DotProduct(VehicleToWayPoint));
	
		// 4. Get Theta if boat is turned to left by PI/16
		Orientation NewO = new Orientation(GetParent().GetAIVehicle().GetMainBody().m_Orientation);
		Vector3 Up = NewO.GetUp();
		NewO.SetRotationAxis(Up);
		NewO.AddRotation(Physics.PI/16);
		
		Vector3 NewForwardXZ = NewO.GetForwardWorldCoords();
		NewForwardXZ.y = 0;
		NewForwardXZ.Normalize();
		
		double Theta2 = Math.acos(NewForwardXZ.DotProduct(VehicleToWayPoint));
	
		// 5. Set Steering
		if (Theta2 > Theta)	
		{
			GetParent().GetAISteering().SetSteeringHorizontal(HorizontalSteeringValues.Right, 1);
			//Log.e("STATESTEERING" , "STEERING RIGHT");		
		}
		else  
		if (Theta2 < Theta)
		{	
			GetParent().GetAISteering().SetSteeringHorizontal(HorizontalSteeringValues.Left, 1);
			//Log.e("STATESTEERING" , "STEERING LEFT");
		}
		else
		{		
			GetParent().GetAISteering().SetSteeringHorizontal(HorizontalSteeringValues.None,0);
			//Log.e("STATESTEERING" , "STEERING STRAIGHT");
		}
	
    }
 
    void SteerVehicleWaypointSpeed(Vector3 WayPoint)
    {
    	// If vehicle is close to waypoint then slow down vehicle 
    	// else accelerate vehicle
    	Tank AIVehicle = GetParent().GetAIVehicle();

    	Vector3 VehiclePos = AIVehicle.GetMainBody().m_Orientation.GetPosition();
    	Vector3 DistanceVecLastWayPoint = Vector3.Subtract(VehiclePos,m_LastWayPoint);
    	Vector3 DistanceVecCurrentWayPoint = Vector3.Subtract(VehiclePos, m_WayPoint); 
    	
    	float TurnArea = GetParent().GetTurnArea();
    	
    	float DLastWayPoint = DistanceVecLastWayPoint.Length();
    	float DCurrentWayPoint = DistanceVecCurrentWayPoint.Length();
    	
    	if ((DLastWayPoint <= TurnArea) || (DCurrentWayPoint <= TurnArea))
    	{
    		// Decrease speed
    		GetParent().GetAISteering().SetSteeringSpeed(SpeedSteeringValues.Deccelerate, 0.04f, 0.03f, 0.005f);
    		GetParent().GetAISteering().SetTurnDelta(3.0f);
    		//Log.e("STATESTEERING" , "STEERING DECCELERATE");
    	}
    	else
    	{
    		GetParent().GetAISteering().SetSteeringSpeed(SpeedSteeringValues.Accelerate, 0.04f, 0.03f, 0.005f);
    		//Log.e("STATESTEERING" , "STEERING ACCELERATE");
    	}		
    }
    
	void SteerVehicleToWayPoint(Vector3 WayPoint)
	{
		SteerVehicleToWayPointHorizontal(WayPoint); 	// ok
		SteerVehicleWaypointSpeed(WayPoint);			//	
	}
	
    void Update()
    {
    	// Steer Main Tank Body to Waypoint
    	SteerVehicleToWayPoint(m_WayPoint);
    	
    	// Turn Tank Turret towards target and fire
    	
    	if (m_Target != null)
    	{
    		TurnTurretTowardTarget(m_Target);
    	}
    	else
    	if (m_TargetObj != null)
    	{
    		TurnTurretTowardTarget(m_TargetObj.m_Orientation.GetPosition());
    	}
    	else
    	{
    		Log.e("STATETANKSTEERWAYPOINT" , "NO TARGET FOR TANK TO SHOOT AT!!!!");
    	}
    	
    	if (m_FireWeapon)
    	{	
    		if (m_NumberRoundsFired >= m_RoundsToFire)
    		{
    			m_NumberRoundsFired = 0;
    			m_FireWeapon = false;
    		}
    		else
    		{
    			// Find Time Elapsed Between firing sequences
    			long ElapsedTime = System.currentTimeMillis() - m_StartTimeFiring;
    			if (ElapsedTime > m_TimeIntervalBetweenFiring)
    			{
    				FireTurretWeapon();
    			}
    		}
    		
    	}
    	
    
    
    }
    
    FSM_StatesTank  CheckTransitions()
    {
    	Object3d AIVehicle = GetParent().GetAIVehicle().GetMainBody();

    	Vector3 VehiclePos = AIVehicle.m_Orientation.GetPosition();
    	Vector3 Distance   = Vector3.Subtract(VehiclePos,m_WayPoint);
    	float   D          = Distance.Length();
    	
    	if (D <= m_WayPointRadius)
    	{
    		Log.e("STATE_TANKSTEERWAYPOINT" , "TANK WayPoint Reached, WayPoint = " + m_WayPoint.GetVectorString());
    		m_LastWayPoint.Set(m_WayPoint.x, m_WayPoint.y, m_WayPoint.z);
    		
    		return FSM_StatesTank.FSM_STATE_PROCESS_COMMAND;
    	}
    	else
    	{
    		return FSM_StatesTank.FSM_STATE_STEER_WAYPOINT;
    	}
    }
	
	
}
