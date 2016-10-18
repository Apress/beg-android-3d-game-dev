package com.robsexample.glhelloworld;




public class HighScoreEntry implements Comparable<HighScoreEntry>
{
	private boolean m_ItemValid;
	private String 	m_Initials;
	private int		m_Score;
	
	HighScoreEntry(String 	Initials,
				   int 		Score)
	{
		m_Initials = Initials;
		m_Score	   = Score;
	}
	
	boolean IsValid()
	{
		return m_ItemValid;
	}
	
	void SetName(String Name)
	{
		m_Initials = Name;
	}
	
	void SetScore(int Score)
	{
		m_Score = Score;
	}
	
	String 	GetInitials()
	{
		return m_Initials;
	}
	
	int	GetScore()
	{
		return m_Score;
	}
	
	void SetItemValidState(boolean state)
	{
		m_ItemValid = state;
	}
	
	public int compareTo(HighScoreEntry Another)
	{
		/*
		Returns
		a negative integer if this instance is less than another; a positive integer if this 
		instance is greater than another; 0 if this instance has the same order as another.
		*/
		int result = 0;
		 
		if (m_Score > Another.m_Score)
		{
			result = -1;
		}
		else
		if (m_Score < Another.m_Score)
		{
			result = 1;
		}
		 
		return result;
	}
	
	
}
