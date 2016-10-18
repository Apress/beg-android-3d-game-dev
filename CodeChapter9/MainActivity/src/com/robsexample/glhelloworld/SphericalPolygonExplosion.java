package com.robsexample.glhelloworld;

import java.util.Random;


import android.content.Context;




public class SphericalPolygonExplosion 
{
private int MAX_POLYGONS = 1000;
	
	private PolyParticleEx[] m_Particles 			= new PolyParticleEx[MAX_POLYGONS];
	private Vector3[]		 m_ExplosionDirection 	= new Vector3[MAX_POLYGONS];

	int		m_NumberParticles;
	Vector3	m_ParticleColor;
	Vector3	m_ParticleSize;
	long	m_ParticleLifeSpan;
	Vector3	m_ExplosionCenter;
	boolean	m_RandomColors;				// true if Particles set to have Random colors
	boolean	m_ParticleColorAnimation;	// true if Particles change colors during explosion
	boolean	m_ExplosionActive;
	
	private Random m_RandNumber = new Random();
	
	Vector3 GenerateRandomColor()
	{
		Vector3 Color = new Vector3(0,0,0);

		// 1. Generate Random RGB Colors in Range of 0-1;
		Color.x = m_RandNumber.nextFloat();
		Color.y = m_RandNumber.nextFloat();
		Color.z = m_RandNumber.nextFloat();

		return Color;
	}
	
	float GenerateRandomRotation(float MaxValue)
	{
		float Rotation;

		// 1. Generate Random Rotation in Range of 0-1 * MaxValue;
		Rotation = MaxValue * m_RandNumber.nextFloat();

		return Rotation;
	}
	
	Vector3 GenerateRandomRotationAxis()
	{
		Vector3 RotationAxis = new Vector3(0,0,0);

		// 1. Generate Random Rotation in Range of 0-1 * MaxValue;
		RotationAxis.x = m_RandNumber.nextFloat();
		RotationAxis.y = m_RandNumber.nextFloat();
		RotationAxis.z = m_RandNumber.nextFloat();

		RotationAxis.Normalize();
		
		return RotationAxis;
	}

	SphericalPolygonExplosion(int		NumberParticles, 
					   		  Vector3	Color,
					   		  long		ParticleLifeSpan,
					   		  boolean	RandomColors, 
					   		  boolean	ColorAnimation,
					   		  float		FadeDelta,
					   		  Vector3	ParticleSize,
					   		  
					   		  // Polygon Particle info
					   		  Context	iContext, 
					   		  Mesh 		iMesh, 
					   		  MeshEx 	iMeshEx, 
					   		  Texture[] iTextures, 
					   		  Material 	iMaterial, 
					   		  Shader 	iShader//, 
					   		  //Shader 	LocalAxisShader
					   		  )
	{
		m_NumberParticles = NumberParticles;
		m_ParticleColor = new Vector3(Color.x, Color.y, Color.z);
		m_ParticleLifeSpan = ParticleLifeSpan;
		m_RandomColors = RandomColors;				// true if Particles set to have Random colors
		m_ParticleColorAnimation = ColorAnimation;
		m_ExplosionActive = false;
		m_ParticleSize = new Vector3(ParticleSize.x, ParticleSize.y, ParticleSize.z);
		
		if (NumberParticles > MAX_POLYGONS) 
		{
			m_NumberParticles = MAX_POLYGONS; //SetNumberParticles(MAX_POLYGONS);
		}

		// For each new Particle
		for (int i = 0; i < m_NumberParticles; i++)
		{
			int signx = 1;
			int signy = 1;
			int signz = 1;

			if (m_RandNumber.nextFloat() > 0.5f)
			{
				signx = -1;
			}
			if (m_RandNumber.nextFloat() > 0.5f)
			{	
				signy = -1;
			}
			if (m_RandNumber.nextFloat() > 0.5f)
			{
				signz = -1;
			}

			// Find point within range of m_MinRadius and m_MaxRadius
			float randomx = (float)signx * m_RandNumber.nextFloat();
			float randomy = (float)signy * m_RandNumber.nextFloat();
			float randomz = (float)signz * m_RandNumber.nextFloat();	
	
			// Generate random x,y,z coords
			Vector3	direction = new Vector3(0,0,0);
			direction.x = randomx;
			direction.y = randomy;
			direction.z = randomz;
			direction.Normalize();
		
			// Set Particle Explosion Direction Array
			m_ExplosionDirection[i]	=	direction;
		
			// Create New Particle
			m_Particles[i] = new PolyParticleEx(iContext, 
		 										iMesh, 
		 										iMeshEx, 
		 										iTextures, 
		 										iMaterial, 
		 										iShader//, 
		 										//LocalAxisShader
		 										);
				
			// Set Particle Array Information
			if (RandomColors)
			{
				m_Particles[i].SetColor(GenerateRandomColor());
			}
			else
			{
				//m_Particles[i].SetColor(m_ParticleColor);
				m_Particles[i].Create(m_ParticleColor);
			}
		
			m_Particles[i].SetTimeDelay(ParticleLifeSpan);
			m_Particles[i].SetFadeDelta(FadeDelta);
			
			// Generate Random Rotations
			Vector3 Axis = GenerateRandomRotationAxis();
			m_Particles[i].m_Orientation.SetRotationAxis(Axis);
			
			float rot = GenerateRandomRotation(360);
			m_Particles[i].m_Orientation.SetRotationAngle(rot);
			
			
			
			
			// Set Max Angular Velocity
			//m_Particles[i].GetObjectPhysics().SetMaximumAngularVelocity(0);
			
			
		}
	}
	
	Vector3 GetRandomParticleVelocity(int	ParticleNumber, 
									  float	MaxVelocity, 
									  float	MinVelocity)
	{
		Vector3 ExplosionDirection 	= m_ExplosionDirection[ParticleNumber];
		Vector3 ParticleVelocity	= new Vector3(ExplosionDirection.x, ExplosionDirection.y, ExplosionDirection.z);
		
		float RandomVelocityMagnitude = MinVelocity + (MaxVelocity - MinVelocity)* m_RandNumber.nextFloat(); 
		ParticleVelocity.Multiply(RandomVelocityMagnitude);

		return ParticleVelocity; 
	}

	void StartExplosion(Vector3	Position,
						float	MaxVelocity, 
						float	MinVelocity)
	{
		// 1. Set Position of Particles 
		m_ExplosionActive = true;									
	
		
		for (int i = 0; i < m_NumberParticles; i++)
		{
			m_Particles[i].SetActiveStatus(true);
			m_Particles[i].SetTimeStamp(System.currentTimeMillis());
			
			m_ExplosionCenter = new Vector3(Position.x, Position.y, Position.z);
			m_Particles[i].m_Orientation.SetPosition(m_ExplosionCenter);
			m_Particles[i].GetObjectPhysics().SetVelocity(GetRandomParticleVelocity(i,MaxVelocity,MinVelocity));			
			m_Particles[i].m_Orientation.SetScale(m_ParticleSize);	
			
			if (m_RandomColors)
			{
				m_Particles[i].SetColor(GenerateRandomColor());
			}
			else
			{
				m_Particles[i].SetColor(m_ParticleColor);
			}
			
			m_Particles[i].SetTimeDelay(m_ParticleLifeSpan);
			
			//Log.d("DEBUG - STARTEXPLOSION ", "Color = " + m_Particles[i].GetColor().GetVectorString());
		}
	}

	void RenderExplosion(Camera Cam, PointLight light)
	{
		// Render Explosion 
		for (int i = 0; i < m_NumberParticles; i++)
		{	
			if (m_Particles[i].GetActiveStatus() == true)
			{
				m_Particles[i].Render(Cam, light);
			}
		}
	}

	void UpdateExplosion()
	{		
		if (!m_ExplosionActive)
		{
			return;
		}

		boolean ExplosionFinished = true;
		for (int i = 0; i < m_NumberParticles; i++)
		{		
			// If all Particles are not active then explosion is finished.
			if (m_Particles[i].GetActiveStatus() == true)
			{
				// If Color Animation is on then set particle to random color
				if(m_ParticleColorAnimation)
				{
					m_Particles[i].SetColor(GenerateRandomColor());
				}

				// For each particle update particle
				m_Particles[i].UpdateParticle(System.currentTimeMillis());
				
			    //Log.d("DEBUG - UPDATEEXPLOSION ", "Position = " + m_Particles[i].m_Orientation.GetPosition().GetVectorString());
				//Log.d("DEBUG - UPDATEEXPLOSION ", "Color = " + m_Particles[i].GetColor().GetVectorString());
			
				ExplosionFinished = false;
			}
		}
		if (ExplosionFinished)
		{
			m_ExplosionActive = false;
		}
	}
	
	
	

}
