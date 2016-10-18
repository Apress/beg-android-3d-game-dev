package com.robsexample.glhelloworld;



public class Material 
{
	private float[] m_Emissive	= new float[3];
	private float[] m_Ambient 	= new float[3];
	private float[]	m_Diffuse 	= new float[3];
	private float[]	m_Specular 	= new float[3];

	private float m_Specular_Shininess = 5.0f;
	private float m_Alpha = 1.0f;
	
	
	// Glow Animation
	private boolean m_GlowAnimation = false;
	private Vector3 m_EmissiveMax = new Vector3(1,1,1);
	private Vector3 m_EmissiveMin = new Vector3(0,0,0);
	private Vector3 m_EmissiveDelta = new Vector3(0.1f, 0.1f, 0.1f);
		

	
	public Material()
	{
		m_Emissive[0] = 0;
		m_Emissive[1] = 0;
		m_Emissive[2] = 0;
		
		m_Ambient[0] = 1;
		m_Ambient[1] = 1;
		m_Ambient[2] = 1;
		
		m_Diffuse[0] = 1;
		m_Diffuse[1] = 1;
		m_Diffuse[2] = 1;
				
		m_Specular[0]= 1;
		m_Specular[1]= 1;
		m_Specular[2]= 1;

		m_Specular_Shininess = 5;
		m_Alpha = 1;
	}
	
	public Material(float[] iEmissive, 
					float[] iAmbient, 
					float[] iDiffuse, 
					float[] iSpecular,
					float iSpecShininess, 
					float iAlpha)
	{
		
		m_Emissive[0] = iEmissive[0];
		m_Emissive[1] = iEmissive[1];
		m_Emissive[2] = iEmissive[2];
		
		m_Ambient[0] = iAmbient[0];
		m_Ambient[1] = iAmbient[1];
		m_Ambient[2] = iAmbient[2];
		
		m_Diffuse[0] = iDiffuse[0];
		m_Diffuse[1] = iDiffuse[1];
		m_Diffuse[2] = iDiffuse[2];
				
		m_Specular[0]= iSpecular[0];
		m_Specular[1]= iSpecular[1];
		m_Specular[2]= iSpecular[2];

		m_Specular_Shininess = iSpecShininess;
		m_Alpha = iAlpha;
		
	}
	
	void SetEmissive(float r, float g, float b)
	{
		// values are 0 through 1 inclusive
		m_Emissive[0] = r;
		m_Emissive[1] = g;
		m_Emissive[2] = b;
	}
	
	void SetAmbient(float r, float g, float b)
	{
		// values are 0 through 1 inclusive
		m_Ambient[0] = r;
		m_Ambient[1] = g;
		m_Ambient[2] = b;
	}
	
	void SetDiffuse(float r, float g, float b)
	{
		// values are 0 through 1 inclusive
		m_Diffuse[0] = r;
		m_Diffuse[1] = g;
		m_Diffuse[2] = b;
	}
	
	void SetSpecular(float r, float g, float b)
	{
		// values are 0 through 1 inclusive
		m_Specular[0] = r;
		m_Specular[1] = g;
		m_Specular[2] = b;
	}
	
	void SetSpecularShininess(float value)
	{
		m_Specular_Shininess = value;
	}
	
	void SetAlpha(float value)
	{
		m_Alpha = value;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	float[] GetEmissive()
	{
		return m_Emissive;
	}
	
	float[] GetAmbient () 
	{
		return m_Ambient;
	}
	
	float[]	GetDiffuse()
	{
		return m_Diffuse;
	}
		
	float[]	GetSpecular() 
	{
		return m_Specular;
	}
	
	float GetSpecularShininess()
	{
		return m_Specular_Shininess;
	}
		
	float GetAlpha()
	{
		return m_Alpha;
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	 void SetGlowAnimation(boolean value)
	 {
		 m_GlowAnimation = value;
	 }
	 
	 boolean DoGlowAnimation()
	 {
		 return m_GlowAnimation;
	 }
	 
	 Vector3 GetEmissiveMax()
	 {
		 return m_EmissiveMax;
	 }
	
	 Vector3 GetEmissiveMin()
	 {
		 return m_EmissiveMin;
	 }
	 
	 Vector3 GetEmissiveDelta()
	 {
		 return m_EmissiveDelta;
	 }
		
	void UpdateGlowAnimation()
	{	
		// Update Scale of object based on scale delta
		m_Emissive[0] = m_Emissive[0] + m_EmissiveDelta.x;
		m_Emissive[1] = m_Emissive[1] + m_EmissiveDelta.y;
		m_Emissive[2] = m_Emissive[2] + m_EmissiveDelta.z;
		
		
		// Check and set bounds for x,y,z scales if needed and reverse delta if those bounds are exceeded.
		// red
		if (m_Emissive[0] > m_EmissiveMax.x)
		{
			m_Emissive[0] = m_EmissiveMax.x;
			m_EmissiveDelta.x = -m_EmissiveDelta.x;
		}
		else
		if (m_Emissive[0] < m_EmissiveMin.x)
		{
			m_Emissive[0] = m_EmissiveMin.x;
			m_EmissiveDelta.x = -m_EmissiveDelta.x;
		}
		
		// green
		if (m_Emissive[1] > m_EmissiveMax.y)
		{
			m_Emissive[1] = m_EmissiveMax.y;
			m_EmissiveDelta.y = -m_EmissiveDelta.y;
		}
		else
		if (m_Emissive[1] < m_EmissiveMin.y)
		{
			m_Emissive[1] = m_EmissiveMin.y;
			m_EmissiveDelta.y = -m_EmissiveDelta.y;
		}
		
		// blue
		if (m_Emissive[2] > m_EmissiveMax.z)
		{
			m_Emissive[2] = m_EmissiveMax.z;
			m_EmissiveDelta.z = -m_EmissiveDelta.z;
		}
		else
		if (m_Emissive[2] < m_EmissiveMin.z)
		{
			m_Emissive[2] = m_EmissiveMin.z;
			m_EmissiveDelta.z = -m_EmissiveDelta.z;
		}
		
		
		//m_Diffuse[0] = m_Emissive[0];
		//m_Diffuse[1] = m_Emissive[1];
		//m_Diffuse[2] = m_Emissive[2];
		
		//m_Specular[0] = m_Specular[0] * m_Emissive[0];
		
		
	}
	
	
	
	
	
	
	
	
	
}
