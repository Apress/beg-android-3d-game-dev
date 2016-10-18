package com.robsexample.glhelloworld;

import android.content.Context;
import android.content.SharedPreferences;

public class Stats 
{
	private Context m_Context;
	private int m_Health = 100;
	private int m_KillValue = 50;
	private int m_DamageValue = 25;
		
	Stats(Context iContext)
	{
		m_Context = iContext;
	}
	
	void SaveStats(String Handle)
	{
		SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
		SharedPreferences.Editor editor = settings.edit();
		      
		// Health
		String HealthHandle = Handle + "Health";
		editor.putInt(HealthHandle, m_Health);
		             
		// Commit the edits!
		editor.commit();
	}
	
	void LoadStats(String Handle)
	{
		// Restore preferences
	    SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
	
	    // Health
	    String HealthHandle = Handle + "Health";
	    m_Health = settings.getInt(HealthHandle, 100);
	}

	int GetDamageValue()
	{
		return m_DamageValue;
	}
	
	int GetHealth()
	{
		return m_Health;
	}
	
	int GetKillValue()
	{
		return m_KillValue;
	}
	
	void SetDamageValue(int value)
	{
		m_DamageValue = value;
	}
	
	void SetHealth(int health)
	{
		m_Health = health;
	}
	
	void SetKillValue(int value)
	{
		m_KillValue = value;
	}
	
}
