package com.robsexample.glhelloworld;


import android.content.SharedPreferences;
import android.util.Log;




public class Driver 
{
	private VehicleCommand	 m_CurrentOrder 	= null; // Order to be executed in the FSM
	private VehicleCommand	 m_LastOrder 		= null;
	private AIVehicleCommand m_CommandExecuting = null; // Command that is currently being executed in the Finite State Machine
	
	private FSMDriver m_FiniteStateMachine = null;
	
	private Steering m_AISteer = new Steering();
	private Steering m_TurretSteering = new Steering();
	
	private float	m_TurnArea = 2.0f; // Area near waypoint where ai vehicle slows down for turning toward next waypoint
	private Vector3	m_WayPoint = new Vector3(0,0,0); // Holds Current Waypoint
	private float	m_WayPointRadius = 1.0f; 		 // Radius of CheckPoint/Evasion WayPoint
	
	private Tank m_AITank = null; // Controlled Object
	
	
	
	Driver(Tank Vehicle)
	{
		// Set Vehicle that is to be controlled
		m_AITank = Vehicle;
		
		//construct the state machine and add the necessary states
	    m_FiniteStateMachine = new FSMDriver();

		StateTankSteerWayPoint SteerWayPoint = new StateTankSteerWayPoint(FSM_StatesTank.FSM_STATE_STEER_WAYPOINT, this);
		StateTankProcessCommand ProcessCommand = new StateTankProcessCommand(FSM_StatesTank.FSM_STATE_PROCESS_COMMAND,this);
	      		
		m_FiniteStateMachine.AddState(SteerWayPoint);
		m_FiniteStateMachine.AddState(ProcessCommand);
		
		m_FiniteStateMachine.SetDefaultState(ProcessCommand);
		m_FiniteStateMachine.Reset();
	}
	
	void SaveDriverState(String Handle)
	{
		SharedPreferences settings = m_AITank.GetMainBody().GetContext().getSharedPreferences(Handle, 0);
		SharedPreferences.Editor editor = settings.edit();
		     
		// Turn Area
		String TurnAreaKey = Handle + "TurnArea";
		editor.putFloat(TurnAreaKey, m_TurnArea);
		
		// WayPoint
		String WayPointXKey = Handle + "WayPointX";
		editor.putFloat(WayPointXKey, m_WayPoint.x);
		
		String WayPointYKey = Handle + "WayPointY";
		editor.putFloat(WayPointYKey, m_WayPoint.y);
		
		String WayPointZKey = Handle + "WayPointZ";
		editor.putFloat(WayPointZKey, m_WayPoint.z);
		
		// WayPoint Radius
		String RadiusKey = Handle + "Radius";
		editor.putFloat(RadiusKey, m_WayPointRadius);
	
		// Commit the edits!
		editor.commit();
		
		
		// Current Order
		if (m_CurrentOrder != null)
		{
			String OrderHandle = Handle + "Order";
			m_CurrentOrder.SaveState(OrderHandle);
		}
		
	}
	
	void LoadDriverState(String Handle)
	{
		SharedPreferences settings = m_AITank.GetMainBody().GetContext().getSharedPreferences(Handle, 0);
	
		// Turn Area
		String TurnAreaKey = Handle + "TurnArea";
		m_TurnArea = settings.getFloat(TurnAreaKey, 4.0f);
		
		// WayPoint
		String WayPointXKey = Handle + "WayPointX";	
		m_WayPoint.x = settings.getFloat(WayPointXKey, 0);
			
		String WayPointYKey = Handle + "WayPointY";		
		m_WayPoint.y = settings.getFloat(WayPointYKey, 0);
		
		String WayPointZKey = Handle + "WayPointZ";	
		m_WayPoint.z = settings.getFloat(WayPointZKey, 0);
		
		// WayPoint Radius
		String RadiusKey = Handle + "Radius";
		m_WayPointRadius = settings.getFloat(RadiusKey, 1.5f);	
		
		
		// Current Order
		if (m_CurrentOrder != null)
		{
			String OrderHandle = Handle + "Order";
			m_CurrentOrder.LoadState(OrderHandle);
		}	
	}
	
	Tank		GetAIVehicle()      {return m_AITank;}	
	Steering 	GetAISteering()	    {return m_AISteer;}
	Vector3		GetWayPoint()       {return m_WayPoint;}
	float       GetWayPointRadius() {return m_WayPointRadius;}
	float    	GetTurnArea()		{return m_TurnArea;}
	
	VehicleCommand	 GetCurrentOrder()		{return m_CurrentOrder;}
	VehicleCommand	 GetLastOrder()			{return m_LastOrder;}
	AIVehicleCommand GetCommandExecuting() 	{return m_CommandExecuting;} 	
	Steering 		 GetTurretSteering() 	{return m_TurretSteering;}

	
	void DriverReset()
	{
		m_FiniteStateMachine.Reset();
	}
	
	void SetCommandExecuting(AIVehicleCommand Command)
	{
		m_CommandExecuting = Command;
	}
	
	void IncrementNextWayPoint()
	{
		AIVehicleCommand Command = m_CurrentOrder.GetCommand();
		
		if (Command ==  AIVehicleCommand.Patrol)
		{
			m_CurrentOrder.IncrementWayPointIndex();
			m_WayPoint = m_CurrentOrder.GetCurrentWayPoint();
		}	
	}
	
	void SetOrder(VehicleCommand Command)
	{
		m_LastOrder = m_CurrentOrder;
		m_CurrentOrder = Command;
		
		if ((m_CurrentOrder.GetCommand() == AIVehicleCommand.Patrol) 
			//|| (m_CurrentOrder.GetCommand() == AIVehicleCommand.AttackTarget)
		    )
		{
			// Set Inital WayPoint
			Vector3[] WayPoints = m_CurrentOrder.GetWayPoints();
			int NumberWayPoints = m_CurrentOrder.GetNumberWayPoints();
			m_WayPoint = WayPoints[0];
			
			for (int i = 0; i < NumberWayPoints; i++)
			{
				Log.e("DRIVER" , "DRIVER WAYPOINT[" + i + "] = " + WayPoints[i].GetVectorString());
			}
		}
	}
	
	void Update()
	{
		// Clear AISteering
		m_AISteer.ClearSteering();
		
		// Update FSM Machine
		m_FiniteStateMachine.UpdateMachine();
		
		//Log.e("PILOT","PILOT MACHINE STATE = " + m_FiniteStateMachine.GetCurrentState());
	}
	

	
	
}
