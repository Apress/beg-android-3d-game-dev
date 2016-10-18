package com.robsexample.glhelloworld;

import java.util.Random;
import android.util.Log;

import android.content.Context;



public class GamePlayController 
{
private Context m_Context;
	
	// Random Number Generator
	private Random m_RandNumber = new Random();
	
	//private AirVehicleFleet m_AirVehicleFleet;
	private ArenaObjectSet 	m_ArenaObjectsSet;
	private TankFleet 		m_TankFleet;
	private GravityGridEx	m_Grid;
	
	// Arena Objects
	private float DROP_HEIGHT = 13;
	private long m_TimeDeltaAddArenaObject = 1000 * 15; // Number milliseconds between adding new Arena Objects
	private long m_TimeLastArenaObjectAdded = 0;
	private int m_MinArenaObjectsOnPlayField = 1;
	private float m_MaxSpeedArenaObjects = 0.1f;
	
	// Tank Objects
	private long m_TimeDeltaAddTank = 1000 * 25; // Number milliseconds between adding new Tanks
	private long m_TimeLastTankOnGrid = 0;
	private int m_MaxTanksOnPlayField = 2;
 
	private int m_NumberTankRoutes = 0;
	private int m_TankRouteIndex = 0;
	private Route[] m_TankRoutes = null;
		
	// AirVehicles
	private long m_TimeDeltaAddAirVehicle = 1000 * 60; // Number milliseconds between adding new Arena Objects
	private long m_PatrolTimeDuration = 1000 * 80; // Time that Air Vehicle has to patrol before attacking Target
	private long m_TimeLastAirVehicleOnGrid = 0;
	private int  m_MaxAirVehiclesOnPlayField = 1;
 
	private int m_AirVehicleRouteIndex = 0;
	private int m_NumberAirVehicleRoutes = 0;
	private Route[] m_AirVehicleRoutes = null;
	
	
	
	// Enemy Object Sets already initialized and contain sets of 
	// different enemies available for use on the play field.
	GamePlayController(Context iContext,
					   //AirVehicleFleet AirVehicleFleet,
					   ArenaObjectSet  ArenaObjectsSet,
					   TankFleet 	   TankFleet,
					   GravityGridEx   Grid,
					   
					   int NumberTankRoutes,
					   Route[] TankRoutes//,
					   
					   //int NumberAirVehicleRoutes,
					   //Route[] AirVehicleRoutes
					   )
	{
		m_Context = iContext;
		
		//m_AirVehicleFleet = AirVehicleFleet;
		m_ArenaObjectsSet = ArenaObjectsSet;
		m_TankFleet = TankFleet;
		m_Grid = Grid;
	
		m_NumberTankRoutes = NumberTankRoutes;
		m_TankRoutes = TankRoutes;
		
		//m_NumberAirVehicleRoutes = NumberAirVehicleRoutes;
		//m_AirVehicleRoutes = AirVehicleRoutes;	
	}

	void ResetController()
	{	
		m_TimeLastTankOnGrid = 0;
		m_TimeLastAirVehicleOnGrid = 0; 
	}
	
	
	/*
	
	void SaveControllerState(String Handle)
	{
		SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
		SharedPreferences.Editor editor = settings.edit();
		   
		// Arena Objects
		editor.putLong("m_TimeLastArenaObjectAdded", m_TimeLastArenaObjectAdded);		
		
		// Tank Objects
		editor.putLong("m_TimeLastTankAdded", m_TimeLastTankAdded);
		editor.putInt("m_TankRouteIndex", m_TankRouteIndex);
		
		// AirVehicles
		editor.putLong("m_TimeLastAirVehicleAdded", m_TimeLastAirVehicleAdded);
		editor.putInt("m_AirVehicleRouteIndex", m_AirVehicleRouteIndex);
		
		// Commit the edits!
		editor.commit();
	}
	
	
	void LoadControllerState(String Handle)
	{
		SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);	
	}
	
	*/
	
	
	
	// Generate Navigation Data
	Vector3 GenerateRandomGridLocation()
	{
		Vector3 Location = new Vector3(0,0,0);
		
		// Get Random X
		float MinX = m_Grid.GetXMinBoundary();
		float MaxX = m_Grid.GetXMaxBoundary();
		float DiffX = MaxX - MinX;
		float RandomXOffset = DiffX * m_RandNumber.nextFloat(); // DiffX * (Number from 0-1);
		float PosX = MinX + RandomXOffset;
		
		// Get Random Z
		float MinZ = m_Grid.GetZMinBoundary();
		float MaxZ = m_Grid.GetZMaxBoundary();
		float DiffZ = MaxZ - MinZ;
		float RandomZOffset = DiffZ * m_RandNumber.nextFloat(); // DiffX * (Number from 0-1);
		float PosZ = MinZ + RandomZOffset;
		
		// Y is 0 for Ground Level for Playfield
		float PosY = DROP_HEIGHT; 
			
		// Set Random Location
		Location.Set(PosX, PosY, PosZ);
	
		return Location;
	}
		
	Vector3 GenerateGridLocationRestricted(Vector3 Max, Vector3 Min)
	{
		Vector3 ClampedLocation = new Vector3(0,0,0);
		Vector3 OriginalLocation = null;
		
		OriginalLocation = GenerateRandomGridLocation();
		
		ClampedLocation.x = Math.min(OriginalLocation.x, Max.x);
		ClampedLocation.y = Math.min(OriginalLocation.y, Max.y);
		ClampedLocation.z = Math.min(OriginalLocation.z, Max.z);
	
		ClampedLocation.x = Math.max(ClampedLocation.x, Min.x);
		ClampedLocation.y = Math.max(ClampedLocation.y, Min.y);
		ClampedLocation.z = Math.max(ClampedLocation.z, Min.z);
		
		return ClampedLocation;
	}

	Vector3 GenerateRandomVelocityArenaObjects()
	{
		Vector3 Velocity = new Vector3(0,0,0);
		
		// X 
		float VelX = m_MaxSpeedArenaObjects * m_RandNumber.nextFloat();
		//float VelY = m_MaxSpeedArenaObjects * m_RandNumber.nextFloat();
		float VelZ = m_MaxSpeedArenaObjects * m_RandNumber.nextFloat();
		
		Velocity.Set(VelX, 0, VelZ);
		
		return Velocity;
	}
	
	// Add New Objects to Playfield
	boolean AddNewArenaObject()
	{
		boolean result = false;
		
    	ArenaObject3d AO =  m_ArenaObjectsSet.GetRandomAvailableArenaObject();  //GetAvailableArenaObject();
    	if (AO != null)
    	{
    		// Respawn
    		AO.SetVisibility(true);
			AO.GetObjectStats().SetHealth(100);
			
			//Vector3 Position = GenerateRandomGridLocation();
			Vector3 Max = new Vector3(m_Grid.GetXMaxBoundary(), DROP_HEIGHT, -5.0f);
			Vector3 Min = new Vector3(m_Grid.GetXMinBoundary(), DROP_HEIGHT, m_Grid.GetZMinBoundary());
			
			Vector3 Position = GenerateGridLocationRestricted(Max, Min);
			
			AO.m_Orientation.GetPosition().Set(Position.x, Position.y, Position.z);
			result = true;
    	}
    	
    	return result;
	}

	// Air Vehicle
	VehicleCommand CreateAirVehiclePatrolCommand(int NumberWayPoints, Vector3[] WayPoints)
	{
		VehicleCommand PatrolCommand = null;
		
    	AIVehicleCommand Command = AIVehicleCommand.Patrol;
		AIVehicleObjectsAffected ObjectsAffected = AIVehicleObjectsAffected.WayPoints;
		
		int	NumberObjectsAffected = 0;
		
		int	DeltaAmount = 0;
		int	DeltaIncrement = 0;
		
		int	MaxValue = 0;
		int	MinValue = 0;
		
		Vector3 Target = null;
    	PatrolCommand = new VehicleCommand(m_Context,
    									   Command,
				   						   ObjectsAffected,
				   						   NumberObjectsAffected,
				   						   DeltaAmount,
				   						   DeltaIncrement,
				   						   MaxValue,
				   						   MinValue,
				   						   NumberWayPoints,
				   						   WayPoints,
				   						   Target,
				   						   null
				   						   );
    	
		return PatrolCommand;
	}
	
	/*
	VehicleCommand CreateAirVehicleAttackCommand(AIVehicleObjectsAffected ObjectsAffected, // Weapons type
			 									 int DeltaAmountMinFireDistance,
			 									 int NumberRoundsToFire,
			 									 int NumberTargets,
			 									 Vector3[] Targets,
			 									 int EvasiveManeuversUpDown)
	{
		VehicleCommand AttackCommand;

		AIVehicleCommand Command = AIVehicleCommand.AttackTarget;
		int NumberObjectsAffected = 0;

		int DeltaAmount = DeltaAmountMinFireDistance;
		int DeltaIncrement = EvasiveManeuversUpDown;

		int MaxValue = 0;
		int MinValue = NumberRoundsToFire;


		// Create Evasive Maneuvers command
		AttackCommand = new VehicleCommand(m_Context,
										   Command,
										   ObjectsAffected,
										   NumberObjectsAffected,
										   DeltaAmount,
										   DeltaIncrement,
										   MaxValue,
										   MinValue,
										   NumberTargets,
										   Targets, // Waypoints become targets
										   null,
										   null);

		return AttackCommand;
	}

	void SetAirVehiclePatrol(AirVehicle Ship)
	{
		// Set Tank Route Index to cycle through all available routes
		m_AirVehicleRouteIndex++;
		if (m_AirVehicleRouteIndex >= m_NumberAirVehicleRoutes)
		{
			m_AirVehicleRouteIndex = 0;
		}
		
		// Set Patrol Order
		Route SelectedRoute = m_AirVehicleRoutes[m_AirVehicleRouteIndex];
		Vector3[] WayPoints = SelectedRoute.GetWayPoints();
		int NumberWayPoints = SelectedRoute.GetNumberWayPoints();
	
		VehicleCommand Command = CreateAirVehiclePatrolCommand(NumberWayPoints, WayPoints);
	
		Ship.GetPilot().SetOrder(Command);
	}
	
	void SetAirVehicleAttack(AirVehicle Ship)
	{
		AIVehicleObjectsAffected ObjectsAffected = AIVehicleObjectsAffected.PrimaryWeapon; // Weapons type
		int DeltaAmountMinFireDistance = 5;
		int NumberRoundsToFire=3;
		int NumberTargets = 1;
		Vector3[] Targets = new Vector3[NumberTargets];
		Targets[0] = new Vector3(0,0,0);
		int EvasiveManeuversUpDown = 1;

		VehicleCommand AttackCommand = CreateAirVehicleAttackCommand(ObjectsAffected, // Weapons type
						   											 DeltaAmountMinFireDistance,
						   											 NumberRoundsToFire,
						   											 NumberTargets,
						   											 Targets,
						   											 EvasiveManeuversUpDown);

		Ship.GetPilot().SetOrder(AttackCommand);
	}
	
	void TestAirFleetReadyForAttack(long CurrentTime)
	{
		// If air vehicle has been on patrol for at least a certain time then set
		// a new order to attack target
		
		int MaxShips = m_AirVehicleFleet.GetMaxAirVehicles();
		AirVehicle[] ActiveShips = new AirVehicle[MaxShips];
		
		int NumberActiveShips = m_AirVehicleFleet.GetActiveAirVehicles(ActiveShips);
		
		for (int i = 0; i < NumberActiveShips; i++)
		{
			AirVehicle Ship = ActiveShips[i];
			AIVehicleCommand CommandExecuting = Ship.GetPilot().GetCommandExecuting();
			long TimeStartCommand = Ship.GetPilot().GetTimeStartCommand();
			long TimeElapsed = CurrentTime - TimeStartCommand;
			
			if ((CommandExecuting == AIVehicleCommand.Patrol) && (TimeElapsed > m_PatrolTimeDuration))
			{
				// Set ship to attack target
				SetAirVehicleAttack(Ship);
			}
		}
	}
	
	boolean AddNewAirVehicle()
	{
		boolean result = false;
		AirVehicle Ship = m_AirVehicleFleet.GetAvailableAirVehicle();	
		if (Ship != null)
		{
			Ship.SetVisibility(true);
			Ship.GetPilot().ResetPilot();
			Ship.GetObjectStats().SetHealth(100);
			
			// Set Random Position
			Vector3 Position = GenerateRandomGridLocation();
			Ship.m_Orientation.GetPosition().Set(Position.x, Position.y, Position.z);
			
			// Set Order to Patrol
			SetAirVehiclePatrol(Ship);
			
			result = true;
		}
		
		return result;
	}
	*/
	
	// Tank
	VehicleCommand CreatePatrolAttackTankCommand(AIVehicleObjectsAffected ObjectsAffected,
				 								 int NumberWayPoints,
				 								 Vector3[] WayPoints, 	
				 								 Vector3 Target,
				 								 Object3d TargetObj,
				 								 int NumberRoundToFire,
				 								 int FiringDelay)
	{
		VehicleCommand TankCommand = null;
		AIVehicleCommand Command = AIVehicleCommand.Patrol;

		int	NumberObjectsAffected = 0;	
		int	DeltaAmount = NumberRoundToFire;
		int	DeltaIncrement = FiringDelay;

		int	MaxValue = 0;
		int	MinValue = 0;

		TankCommand = new VehicleCommand(m_Context,
										 Command,
										 ObjectsAffected,
										 NumberObjectsAffected,  
										 DeltaAmount,
										 DeltaIncrement,
										 MaxValue,
										 MinValue,
										 NumberWayPoints,
										 WayPoints,
										 Target,
										 TargetObj);

		return TankCommand;
	}
	
	void SetTankOrder(Tank TankVehicle)
	{
		// Set Tank Route Index to cycle through all available routes
		m_TankRouteIndex++;
		if (m_TankRouteIndex >= m_NumberTankRoutes)
		{
			m_TankRouteIndex = 0;
		}
		
		// Set Patrol Order
		Route SelectedRoute = m_TankRoutes[m_TankRouteIndex];
		Vector3[] WayPoints = SelectedRoute.GetWayPoints();
		int NumberWayPoints = SelectedRoute.GetNumberWayPoints();
		
		AIVehicleObjectsAffected ObjectsAffected = AIVehicleObjectsAffected.PrimaryWeapon;
		Vector3 Target = new Vector3(0,0,0);
		Object3d TargetObj = null;
		int NumberRoundToFire = 3;
		int FiringDelay = 5000;
		
		VehicleCommand Command = CreatePatrolAttackTankCommand(ObjectsAffected,
															   NumberWayPoints,
															   WayPoints, 	
															   Target,
															   TargetObj,
															   NumberRoundToFire,
															   FiringDelay);
		
		TankVehicle.GetDriver().SetOrder(Command);
	}

	boolean AddNewTank()
	{
		boolean result = false;
		
		Tank TankVehicle = m_TankFleet.GetAvailableTank();
    	if (TankVehicle != null)
    	{
    		TankVehicle.Reset();
    		TankVehicle.GetMainBody().GetObjectStats().SetHealth(100);
    		
    		// Set Position
    		//Vector3 Position = GenerateRandomGridLocation();	
    		Vector3 Max = new Vector3(m_Grid.GetXMaxBoundary(), DROP_HEIGHT, -5.0f);
			Vector3 Min = new Vector3(m_Grid.GetXMinBoundary(), DROP_HEIGHT, m_Grid.GetZMinBoundary());
		
			Vector3 Position = GenerateGridLocationRestricted(Max, Min);
    		TankVehicle.GetMainBody().m_Orientation.GetPosition().Set(Position.x, Position.y, Position.z);
    		
    		// Set Command
    		SetTankOrder(TankVehicle);
    		
    		result = true;
    	}
    	
    	return result;
	}
	

	// Update Enemy 
	void UpdateArenaObjects(long CurrentTime)
	{
		// Check to see if need to add in more Arena Objects
		int NumberObjects = m_ArenaObjectsSet.NumberActiveArenaObjects();
		
		if (NumberObjects < m_MinArenaObjectsOnPlayField)
		{
			// Add another object to meet minimum
			boolean result = AddNewArenaObject();
			if (result == true)
			{
				m_TimeLastArenaObjectAdded = System.currentTimeMillis();
			}
		}
		else
		{
			// Check to see if enough time has elapsed to add in another object.
			long ElapsedTime = CurrentTime - m_TimeLastArenaObjectAdded;
			if (ElapsedTime >= m_TimeDeltaAddArenaObject)
			{
				// Add New Arena Object
				boolean result = AddNewArenaObject();
				if (result == true)
				{
					m_TimeLastArenaObjectAdded = System.currentTimeMillis();
				}
			}
		}
		
	}
	
	/*
	void UpdateAirVehicles(long CurrentTime)
	{
		int NumberAirVehicles = m_AirVehicleFleet.NumberActiveVehicles();
		
		if (NumberAirVehicles > 0)
		{
			m_TimeLastAirVehicleOnGrid = System.currentTimeMillis();
		}
	
		long ElapsedTime = CurrentTime - m_TimeLastAirVehicleOnGrid;
		if ((NumberAirVehicles < m_MaxAirVehiclesOnPlayField)&&
			(ElapsedTime > m_TimeDeltaAddAirVehicle))
		{
			// Add New Air Vehicle
			boolean result = AddNewAirVehicle();
		}
		
		TestAirFleetReadyForAttack(CurrentTime);
	}
	*/
		
	
	void UpdateTanks(long CurrentTime)
	{
		int NumberTanks = m_TankFleet.NumberActiveVehicles();
		
		//Log.e("GAMECONTROLLER","NUMBER TANKS = " + NumberTanks);
		
		//if (NumberTanks > 0)
		//{
		//	m_TimeLastTankOnGrid = System.currentTimeMillis();
		//}

		long ElapsedTime = CurrentTime - m_TimeLastTankOnGrid;
			
		if ((NumberTanks < m_MaxTanksOnPlayField)&&
			(ElapsedTime > m_TimeDeltaAddTank))
		{
			// Add New Tank
			boolean result = AddNewTank();
			if (result == true)
			{
				m_TimeLastTankOnGrid = System.currentTimeMillis();
			}
		}
	}
	
	// Update Controller
	void UpdateController(long CurrentTime)
	{
		UpdateArenaObjects(CurrentTime);
		UpdateTanks(CurrentTime);
		//UpdateAirVehicles(CurrentTime);
	}
	
	

}
