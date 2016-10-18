package com.robsexample.glhelloworld;


public class Route 
{
	private int m_NumberWayPoints = 0;
	private Vector3[] m_WayPoints = null;

	Route(int NumberWayPoints, Vector3[] WayPoints)
	{
		m_NumberWayPoints = NumberWayPoints;
		m_WayPoints = WayPoints;
	}
	
	Vector3[] GetWayPoints()
	{
		return m_WayPoints;
	}
	
	int GetNumberWayPoints()
	{
		return m_NumberWayPoints;
	}
	
	float DistanceToFirstWayPoint(Vector3 Point)
	{
		float Length = -1;
		Vector3 DistVec = new Vector3(0,0,0);
		
		if (m_NumberWayPoints > 0)
		{
			DistVec = Vector3.Subtract(Point, m_WayPoints[0]);
			Length = DistVec.Length();
		}
	
		return Length;
	}
	
	


}
