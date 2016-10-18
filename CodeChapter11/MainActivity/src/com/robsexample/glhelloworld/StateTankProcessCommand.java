package com.robsexample.glhelloworld;


import android.util.Log;


public class StateTankProcessCommand extends StateTank
{
	FSM_StatesTank m_NextState = FSM_StatesTank.FSM_STATE_PROCESS_COMMAND;
	
	
	StateTankProcessCommand(FSM_StatesTank ID, Driver Parent)
	{
		super(ID, Parent); 	
	}
    
	void Init()     
	{
	
	}
	
	void Enter()   	
	{
		Log.e("STATETANKPROCESSCOMMAND", "TANK ENTERED STATEPROCESSCOMMAND!!!!");
		
	}
	
	void Exit()		
	{
		
	}
	
	void ProcessAIVehicleCommand()
	{
		VehicleCommand CurrentOrder = GetParent().GetCurrentOrder();
		
		if (CurrentOrder == null)
		{
			Log.e("STATETANKPROCESSCOMMAND", "ERROR, CurrentOrder is NULL");
			return;
		}
		
		if (CurrentOrder.GetCommand() == AIVehicleCommand.None)
		{
			Log.e("STATETANKPROCESSCOMMAND", "ERROR, CurrentOrder is None");
			return;
		}

		AIVehicleCommand Command = CurrentOrder.GetCommand();

		// Process Commands
		if (Command == AIVehicleCommand.Patrol)
		{
			m_NextState = FSM_StatesTank.FSM_STATE_STEER_WAYPOINT;
		}
		//else
		//if (Command == AIVehicleCommand.AttackTarget)
		//{
		//	m_NextState = FSM_StatesTank.FSM_STATE_ATTACK_STEER_TARGET;
		//}
		else
		{
			m_NextState = FSM_StatesTank.FSM_STATE_PROCESS_COMMAND;
		}
		
		Log.e("STATETankPROCESSCOMMAND", "STATETankPROCESSCOMMAND, Command = " + Command);
	}
	
	void Update()   
	{
		
	}
	
	FSM_StatesTank  CheckTransitions() 
	{
		ProcessAIVehicleCommand();
		
		return m_NextState;
	}
	 
	
	
	
	
}
