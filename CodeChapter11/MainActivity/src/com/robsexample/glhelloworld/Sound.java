package com.robsexample.glhelloworld;


import android.content.Context;
import android.util.Log;
import android.media.SoundPool;


public class Sound 
{
	private SoundPool m_SoundPool;
	private int m_SoundIndex = -1;
	
	float 	m_LeftVolume 	= 1; 
	float 	m_RightVolume 	= 1; 
	int  	m_Priority 		= 1;
	int 	m_Loop 			= 0; 
	float 	m_Rate 			= 1;
	
	
	Sound(Context iContext, SoundPool Pool, int ResourceID)
	{
		m_SoundPool = Pool;
		m_SoundIndex = m_SoundPool.load(iContext, ResourceID, 1);
	}
	
	void PlaySound()
	{
		//public final int play (int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
		
		/*
		 * 	soundID		a soundID returned by the load() function
			leftVolume	left volume value (range = 0.0 to 1.0)
			rightVolume	right volume value (range = 0.0 to 1.0)
			priority	stream priority (0 = lowest priority)
			loop		loop mode (0 = no loop, -1 = loop forever)
			rate		playback rate (1.0 = normal playback, range 0.5 to 2.0)

		 * 
		 */
		m_SoundPool.play(m_SoundIndex, m_LeftVolume, m_RightVolume, m_Priority, m_Loop, m_Rate);
	}
	
	SoundPool GetSoundPool()
	{
		return m_SoundPool;
	}	
}
