package com.robsexample.glhelloworld;



import android.content.Context;


public class PolyParticleEx extends Object3d
{	
	/*
	// With Textures and Normals
	static float[] PolyParticleVertices = 
	{
		// Triangle Shape
		// Left Side            u  v   nx, ny, nz
		-0.5f, -0.5f, -0.5f,    0, 1,  -1, -1, -1,	// v0 =  left, bottom, back
		-0.5f, -0.5f,  0.5f,    1, 1,  -1, -1,  1,	// v1 =  left, bottom, front
		 0.0f,  0.6f,  0.0f, 0.5f, 0,   0,  1,  0,	// v2 =  top point
	};
	*/
	
	static float[] PolyParticleVertices = 
	{
		// Triangle Shape
		// Left Side              nx, ny, nz
		 0.0f,  0.0f, -0.5f,      0, 0,  -1,	// v0 =  bottom, back
		 0.0f,  0.0f,  0.5f,      0, 0,  1,	// v1 =  bottom, front
		 0.0f,  0.5f,  0.0f,      0, 1,  0,	// v2 =  top point
	};
		
		
	
	private Vector3	m_Color = new Vector3(0,0,0);
	private long	m_TimeStamp;	// Time in milliseconds that Particle is created
	private float	m_TimeDelay;	// LifeSpan of Particle in milliseconds
	private boolean	m_Locked;		// true if set to launch or in use, false if available for use
	private boolean	m_Active;       // Onscreen = Render particle if Active

	// Color Fading Variables
	private float	m_ColorBrightness;
	private float	m_FadeDelta;
	private Vector3	m_OriginalColor = new Vector3(0,0,0);
	
	public PolyParticleEx(Context iContext, 
			 			Mesh iMesh, 
			 			MeshEx iMeshEx, 
			 			Texture[] iTextures, 
			 			Material iMaterial, 
			 			Shader iShader//, 
			 			//Shader LocalAxisShader
			 			)
	{
		super(iContext, 
			iMesh, 
			iMeshEx, 
			iTextures, 
			iMaterial, 
			iShader//, 
			//LocalAxisShader
			);
		
		
		
		m_Color.Clear();
		m_TimeStamp	= 0;
		m_TimeDelay	= 1000;
		m_Locked	= false;
		m_Active	= false;  

		m_ColorBrightness	= 1;
		m_OriginalColor.Clear();
		m_FadeDelta			= 0.0000f;
	}
	
	Vector3	GetColor()					
	{
		return m_Color;
	}
	
	long GetTimeStamp()				
	{
		return m_TimeStamp;
	}
	
	float GetTimeDelay()				
	{
		return m_TimeDelay;
	}
	
	boolean	GetLockedStatus()			
	{
		return m_Locked;
	}
	
	boolean	GetActiveStatus()			
	{
		return m_Active;
	} 
		
	void SetFadeDelta(float value)					
	{
		m_FadeDelta	= value;
	}
	
	void SetColor(Vector3 value)		
	{
		m_Color.x			= value.x;
		m_Color.y			= value.y;
		m_Color.z			= value.z;
		
		GetMaterial().SetAmbient(value.x, value.y, value.z);
		GetMaterial().SetDiffuse(value.x, value.y, value.z);
		GetMaterial().SetEmissive(value.x, value.y, value.z);
		//GetMaterial().SetSpecular(value.x, value.y, value.z);
	}
	
	void SetTimeStamp(long value)		
	{
		m_TimeStamp= value;
	}

	void SetTimeDelay(float value)		
	{
		m_TimeDelay	= value;
	}
		
	void SetLockedStatus(boolean value)		
	{
		m_Locked	= value;
	}
	
	void SetActiveStatus(boolean value)		
	{
		m_Active	= value;

		// Reset Brightness Level for Random Color Animation Particles
		m_ColorBrightness= 1;
	}       
	  
	void Destroy()
	{
		GetObjectPhysics().GetVelocity().Clear();
			
		m_Locked = false;	// Particle is now free to be used again by the Particle Manager.
		m_Active = false;	// Do not draw on screen
		m_TimeStamp = 0;
		
		// Restore Particle to Original Color
		//Create(m_OriginalColor);
		m_Color.x = m_OriginalColor.x;
		m_Color.y = m_OriginalColor.y;
		m_Color.z = m_OriginalColor.z;	
	}

	void Reset()
	{
		m_TimeStamp = 0;
		m_Locked = false;		// true if set to launch or in use, false if available for use
		m_Active = false;  
		
		GetObjectPhysics().GetVelocity().Set(0, 0, 0);
	}
		
	void Create(Vector3 Color)
	{
		m_Color.x			= Color.x;
		m_Color.y			= Color.y;
		m_Color.z			= Color.z;
		
		m_OriginalColor.x = m_Color.x;
		m_OriginalColor.y = m_Color.y;
		m_OriginalColor.z = m_Color.z;
	}

	float FindDistanceToPoint(Vector3 point)
	{
		Vector3 d = new Vector3(point.x, point.y, point.z);
		Vector3 pos = m_Orientation.GetPosition();

		d.Subtract(pos);
	
		return (d.Length());
	}

	void LockParticle(float	  Force, 
					  Vector3 DirectionNormalized, 
					  long	  CurrentTime)
	{
		// 1. Setup particle for use
		m_Active = false;
		m_Locked = true;
	
		// 2. Apply Initial Force 
		Vector3 FVector = new Vector3(DirectionNormalized.x, DirectionNormalized.y, DirectionNormalized.z);
		FVector.Multiply(Force);
		GetObjectPhysics().ApplyTranslationalForce(FVector);

		// 3. Apply Time
		m_TimeStamp = CurrentTime;	

		// 4. Calculate Color for Fade
		m_Color.x	= m_OriginalColor.x;
		m_Color.y	= m_OriginalColor.y;
		m_Color.z	= m_OriginalColor.z;
	}

	void FadeColor(Vector3 ColorIn)
	{
		// Fade Color to Black.

		// Adjust Brightness Level Down from full brightness = 1 to no brightness = 0;
		m_ColorBrightness -= m_FadeDelta;

		if (m_ColorBrightness < 0)
		{
			m_ColorBrightness = 0;
		}
	
		// 1. Adjust Color so that everything is at the same Brightness Level
		ColorIn.x *= m_ColorBrightness;
		ColorIn.y *= m_ColorBrightness;
		ColorIn.z *= m_ColorBrightness;
	}

	void FadeColor(long ElapsedTime)
	{
		FadeColor(m_Color);
		SetColor(m_Color);
	}

	void UpdateParticle(long current_time)
	{
		// If particle is Active (on the screen)
		if (m_Active)
		{			
			// Update Particle Physics and position
			GetObjectPhysics().ApplyRotationalForce(40, 1);
			GetObjectPhysics().UpdatePhysicsObject(m_Orientation);
			
			long TimePassed = current_time - m_TimeStamp;
			if (TimePassed > m_TimeDelay)
			{
				// Destroy Particle
				Destroy();
			}
			else
			{
				FadeColor(TimePassed);
			}
		}
	}
	
	void Render(Camera Cam, PointLight light)
	{
		//DrawObject(Cam, light, false);
		DrawObject(Cam, light);
		
	}
}
