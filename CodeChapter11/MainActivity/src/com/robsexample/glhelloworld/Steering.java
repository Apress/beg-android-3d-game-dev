package com.robsexample.glhelloworld;


enum HorizontalSteeringValues
{
	None,
	Right,
	Left
}

enum VerticalSteeringValues
{
	None,
	Up,
	Down
}

enum SpeedSteeringValues
{
	None,
	Accelerate,
	Deccelerate
}

public class Steering 
{
	private HorizontalSteeringValues m_HoriontalSteering; 
	private VerticalSteeringValues 	 m_VerticalSteering;
	private SpeedSteeringValues 	 m_SpeedSteering;
	
	private float m_MaxPitch = 45; // degrees
	private float m_TurnDelta = 1; // degrees

	private float m_MaxSpeed = 0.1f;
	private float m_MinSpeed = 0.05f;
	private float m_SpeedDelta = 0.01f;
	

	Steering()
	{
		ClearSteering();
	}
	
	void SetTurnDelta(float delta)
	{
		m_TurnDelta = delta;
	}
	
	void ClearSteering()
	{
		m_HoriontalSteering = HorizontalSteeringValues.None; 
		m_VerticalSteering 	= VerticalSteeringValues.None;
		m_SpeedSteering 	= SpeedSteeringValues.None;
	}
	
	void SetSteeringHorizontal(HorizontalSteeringValues Horizontal, float TurnDelta)
	{
		m_HoriontalSteering = Horizontal;	
		m_TurnDelta = TurnDelta;
	}
	
	void SetSteeringVertical(VerticalSteeringValues Vertical, float MaxPitch)
	{
		m_VerticalSteering = Vertical;	
		m_MaxPitch = MaxPitch;
	}
	
	void SetSteeringSpeed(SpeedSteeringValues Speed, float MaxSpeed, float MinSpeed, float SpeedDelta)
	{
		m_SpeedSteering = Speed;	
		m_MaxSpeed = MaxSpeed;
		m_MinSpeed = MinSpeed;
		m_SpeedDelta = SpeedDelta;
	}

	HorizontalSteeringValues GetHorizontalSteering()
	{
		return m_HoriontalSteering; 
	}
	
	VerticalSteeringValues GetVerticalSteering()
	{
		return m_VerticalSteering;
	}
	
	SpeedSteeringValues GetSpeedSteering()
	{
		return m_SpeedSteering;
	}
	
	float GetMaxPitch()
	{
		return m_MaxPitch;
	}
	
	float GetTurnDelta()
	{
		return m_TurnDelta;
	}

	float GetMaxSpeed()
	{
		return m_MaxSpeed;
	}
	
	float GetMinSpeed()
	{
		return m_MinSpeed;
	}
	
	float GetSpeedDelta()
	{
		return m_SpeedDelta;
	}
	
}
