package com.robsexample.glhelloworld;

import android.content.Context;

public class PointLight
{
	private float[]	m_light_ambient = new float[3];
	private float[]	m_light_diffuse = new float[3];
	private float[]	m_light_specular = new float[3];
	private float m_specular_shininess = 5;

	private Vector3 m_Position;
	
	public PointLight(Context context)
	{
		m_light_ambient[0] = 1.0f;
		m_light_ambient[1] = 1.0f;
		m_light_ambient[2] = 1.0f;
			
		m_light_diffuse[0]	=	1.0f;
		m_light_diffuse[1]	=	1.0f;
		m_light_diffuse[2]	=	1.0f;

		m_light_specular[0]	=	1.0f;
		m_light_specular[1]	=	1.0f;
		m_light_specular[2]	=	1.0f;
		
		m_Position = new Vector3(0,0,0);	
	}
	
	void SetPosition(float x, float y, float z)
	{
		m_Position.x = x;
		m_Position.y = y;
		m_Position.z = z;
	}
	
	void SetPosition(Vector3 Pos)
	{
		m_Position.x = Pos.x;
		m_Position.y = Pos.y;
		m_Position.z = Pos.z;
	}
	
	Vector3 GetPosition()
	{
		return m_Position;
	}
	
	void SetAmbientColor(float[] ambient)
	{
		m_light_ambient[0] = ambient[0];
		m_light_ambient[1] = ambient[1];
		m_light_ambient[2] = ambient[2];
	}
	
	void SetDiffuseColor(float[] diffuse)
	{
		m_light_diffuse[0]	=	diffuse[0];
		m_light_diffuse[1]	=	diffuse[1];
		m_light_diffuse[2]	=	diffuse[2];
	}

	void SetSpecularColor(float[] spec)
	{
		m_light_specular[0]	=	spec[0];
		m_light_specular[1]	=	spec[1];
		m_light_specular[2]	=	spec[2];
	}
	
	float[] GetAmbientColor()
	{
		return m_light_ambient;
	}
	
	float[] GetDiffuseColor()
	{
		return m_light_diffuse;
	}
	
	float[] GetSpecularColor()
	{
		return m_light_specular;
	}
	
	float GetSpecularShininess()
	{
		return m_specular_shininess;
	}
	
}

