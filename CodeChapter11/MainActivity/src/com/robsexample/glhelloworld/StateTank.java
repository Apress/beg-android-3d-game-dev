package com.robsexample.glhelloworld;


enum FSM_StatesTank
{
	FSM_STATE_NONE,
	FSM_STATE_STEER_WAYPOINT,
	
	//FSM_STATE_ATTACK_STEER_TARGET,
	//FSM_STATE_ATTACK_FIRE_WEAPON_TARGET,
	
	FSM_STATE_PROCESS_COMMAND,
};

public class StateTank
{
	private Driver m_Parent;
	private FSM_StatesTank m_StateID;

	StateTank(FSM_StatesTank ID, Driver Parent)
	{
		m_StateID = ID;
		m_Parent = Parent;	
	}
	
	void Init()     {}
	void Enter()   	{}
	void Exit()		{}
	void Update()   {}
	
	FSM_StatesTank  CheckTransitions() 
	{
		return FSM_StatesTank.FSM_STATE_NONE;
	}

	Driver GetParent()
	{
		return m_Parent;
	}

	FSM_StatesTank GetStateID()
	{
		return m_StateID;
	}
	
}
