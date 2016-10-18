package com.robsexample.glhelloworld;



public class FSMDriver 
{
	private int MAX_STATES = 20;
	private int m_NumberStates = 0;
	protected StateTank[] m_States = new StateTank[MAX_STATES];
	
	protected StateTank m_CurrentState = null;
	protected StateTank m_DefaultState = null;
	protected StateTank m_GoalState = null;
	
	protected FSM_StatesTank m_GoalID;

	FSMDriver()
	{

	}
	void UpdateMachine()
	{
		if(m_NumberStates == 0)
		{
			return;
		}
		
		if(m_CurrentState == null)
		{
			m_CurrentState = m_DefaultState;
		}
		
		if(m_CurrentState == null)
		{
			return;
		}

		FSM_StatesTank OldStateID = m_CurrentState.GetStateID();
		
		m_GoalID = m_CurrentState.CheckTransitions();
		
		if(m_GoalID != OldStateID)
		{
			if(TransitionState(m_GoalID))
			{
				m_CurrentState.Exit();
				m_CurrentState = m_GoalState;
				m_CurrentState.Enter();
			}
		}
		m_CurrentState.Update();		
	}
	 
	boolean AddState(StateTank State)
	{
		boolean result = false;
		
		if (m_NumberStates < MAX_STATES)
		{
			m_States[m_NumberStates] = State;
			m_NumberStates++;
			result = true;
		}
		
		return result;
	}
	
	void SetDefaultState(StateTank State) 
	{
		m_DefaultState = State;
	}
	 
	void SetGoalID(FSM_StatesTank Goal) 
	{
		m_GoalID = Goal;
	}
	 
	boolean TransitionState(FSM_StatesTank Goal)
	{		
		if(m_NumberStates == 0)
		{
			return false;
		}
		
		for(int i = 0; i < m_NumberStates;i++)
		{
			if(m_States[i].GetStateID() == Goal)
			{
				m_GoalState = m_States[i];
				return true;
			}
		}
		return false;
	}
	
	void Reset()
	{
		if(m_CurrentState != null)
		{
			m_CurrentState.Exit();
		}
		
		m_CurrentState = m_DefaultState;

		for(int i = 0;i < m_NumberStates;i++)
		{
			m_States[i].Init();
		}
		
	    if(m_CurrentState != null)
	    {
	        m_CurrentState.Enter();
	    }
	}

	StateTank GetCurrentState()
	{
		return m_CurrentState;
	}
	
	
	
	
	
	
}
