package com.robsexample.glhelloworld;



import android.content.Context;
import android.content.SharedPreferences;

public class Physics 
{
	static float PI			= (float)(3.14159265358979323846264338327950288419716939937511);
	static float TWO_PI		= (float)(2.0*PI);
	static float HALF_PI	= (float)(PI/2.0);
	static float QUARTER_PI	= (float)(PI/4.0);
	
	private Context m_Context;
	
	// Newtonian Physics 
	// Linear
	private Vector3	m_Velocity 			= new Vector3(0,0,0);	
	private Vector3	m_Acceleration 		= new Vector3(0,0,0);			
	private Vector3	m_MaxVelocity 		= new Vector3(1.25f, 1.25f, 1.25f);	//new Vector3(1, 0.25f, 1);	
	private Vector3	m_MaxAcceleration	= new Vector3(1.0f,1.0f,1.0f); 		//new Vector3(5.0f,1.0f,5.0f);	
	
	// Angular
	private float	m_AngularVelocity 		= 0;	
	private float	m_AngularAcceleration 	= 0;
	private float	m_MaxAngularVelocity 	= 4 * PI;	
	private float	m_MaxAngularAcceleration= HALF_PI;
	
	// Gravity
	private boolean m_ApplyGravity 	= false;
	private float	m_Gravity 		= 0.010f;	
	private float 	m_GroundLevel 	= 0;
	private float	m_Mass 			= 100.0f;		
	private float	m_MassEffectiveRadius = 10; //15; // Radius for mass effect on gravity grid

	// Collision Variables
	private float	COEFFICIENTOFRESTITUTION= 0.5f;
	private float 	COLLISIONTOLERANCE		= 0.1f;
	
	// Hit Ground?
	private boolean m_JustHitGround = false;
	
	enum CollisionStatus
	{
		COLLISION,	
		NOCOLLISION,				
		PENETRATING,				
		PENETRATING_COLLISION
	}
	
	private float	m_CollisionTolerance = COLLISIONTOLERANCE;
	private float 	m_CoefficientOfRestitution = COEFFICIENTOFRESTITUTION;
	private Vector3	m_CollisionNormal;
	private Vector3	m_RelativeVelocity;
	
	
	
	
	
	// Vehicles 
	private float 	m_MaxSpeed = 0.20f;
	
	
	
	
	Physics(Context context)
	{
		m_Context = context;
	}
	
	
	///////////////////////// Persistent State ////////////////////////////////////////
	// Save/Load Object State
		void SaveState(String handle)
		{
			SharedPreferences settings = m_Context.getSharedPreferences(handle, 0);
			SharedPreferences.Editor editor = settings.edit();
			      
			// Linear Velocity
			editor.putFloat("velx", m_Velocity.x);
			editor.putFloat("vely", m_Velocity.y);
			editor.putFloat("velz", m_Velocity.z);
				
			// Linear Acceleration
			editor.putFloat("ax", m_Acceleration.x);
			editor.putFloat("ay", m_Acceleration.y);
			editor.putFloat("az", m_Acceleration.z);
			
			// Angular velocity
			editor.putFloat("angularvel", m_AngularVelocity);
			
			// Angular Acceleration
			editor.putFloat("angularaccel", m_AngularAcceleration);
			
			// Commit the edits!
			editor.commit();    
		}
		
		
		void LoadState(String handle)
		{
			// Restore preferences
		    SharedPreferences settings = m_Context.getSharedPreferences(handle, 0);
		
			// Linear Velocity
		    float velx = settings.getFloat("velx", 0);
		    float vely = settings.getFloat("vely", 0);
		    float velz = settings.getFloat("velz", 0);
			m_Velocity.Set(velx, vely, velz);	
		    
			// Linear Acceleration
		    float ax = settings.getFloat("ax", 0);
		    float ay = settings.getFloat("ay", 0);
		    float az = settings.getFloat("az", 0);
			m_Acceleration.Set(ax, ay, az);
		    
			// Angular velocity
		    m_AngularVelocity = settings.getFloat("angularvel", 0);
			
			// Angular Acceleration
		    m_AngularAcceleration = settings.getFloat("angularaccel",0);	
		}
		
		
	
	
	
	
	
	
	
	
	
	void ResetState()
	{
		// Linear Velocity
		m_Velocity.Clear();
			
		// Linear Acceleration
		m_Acceleration.Clear();
		
		// Angular velocity
		m_AngularVelocity = 0;
		
		// Angular Acceleration
		m_AngularAcceleration = 0;		
	}
	
	//************ Get Functions*************************
	void SetGroundLevel(float value)
	{
		m_GroundLevel = value;
	}
	
	boolean GetHitGroundStatus()
	{
		return m_JustHitGround;
	}
	
	void ClearHitGroundStatus()
	{
		m_JustHitGround = false;
	}
	
	void SetGravityLevel(float value)
	{
		m_Gravity = value;
	}
	
	void SetGravity(boolean value)
	{
		m_ApplyGravity = value;
	}
	
	Vector3 GetVelocity()
	{
		return m_Velocity;
	}
		
	float GetAngularVelocity()
	{
		return m_AngularVelocity;
	}

	Vector3 GetMaxVelocity()
	{
		return m_MaxVelocity;
	}

	float GetAngularAcceleration()
	{
		return m_AngularAcceleration;
	}

	Vector3 GetLinearAcceleration()
	{
		return m_Acceleration;
	}

	float GetGravity()
	{
		return m_Gravity;
	}

	float GetMass()
	{
		return m_Mass;
	}
	
	float GetMassEffectiveRadius()
	{
		return m_MassEffectiveRadius;
	}
	
	//************Set Functions**************************
	
	
	void SetMaxSpeed(float Speed)
	{
		m_MaxSpeed = Speed;
	}
	
	
	
	
	void SetMaximumVelocity(Vector3 v)
	{
		m_MaxVelocity = v;
	}

	void SetMaximumAngularVelocity(float v)
	{
		m_MaxAngularVelocity = v;
	}

	void SetMaximumAcceleration(Vector3 a)
	{
		m_MaxAcceleration  = a;
	}

	void SetMaximumAngularAcceleration(float a)
	{
		m_MaxAngularAcceleration = a;
	}

	void SetVelocity(Vector3 v)
	{
		m_Velocity = v;
	}
	
	void SetAcceleration(Vector3 a)
	{
		m_Acceleration = a;
	}
	
	void SetAngularVelocity(float v)
	{
		m_AngularVelocity = v;
	}
	
	void SetMass(float m)
	{
		m_Mass = m;
	}
	
	void SetMassEffectiveRadius(float value)
	{
		m_MassEffectiveRadius = value;
	}
	
	//*************Apply Force Functinos*********************************
	void ApplyTranslationalForce(Vector3 Force)
	{ // Apply a force to the object
		// F = ma
		// F/m = a
		// 1. Calculate translational acceleration on object due to new force and add this 
		// to the current acceleration for this object.
		Vector3 a = new Vector3(Force);
		if (m_Mass != 0)
		{
			a.Divide(m_Mass);
		}
		m_Acceleration.Add(a);
	}

	void ApplyRotationalForce(float Force, float r)
	{
		// 1. Torque = r X F;
		//    T = I * AngularAcceleration;
		//    T/I = AngularAccleration;
		//   
		//    I = mr^2 = approximate with hoop inertia with r = 1 so that I = mass;
		float Torque = r * Force;
		
		float aangular = 0;
		float I = m_Mass;
		
		if (I != 0)
		{
			aangular  = Torque/I;
		}
		
		m_AngularAcceleration += aangular;
	}
	
	float UpdateValueWithinLimit(float value, float increment, float limit)
	{
		float retvalue = 0;
		
		// Increments the value by the increment if the result 
		// is within +- limit value
		float tempv = value + increment;
		if (tempv > limit)
		{
			retvalue = limit;
		} 
		else if (tempv < -limit)
		{
			retvalue = -limit;
		} 
		else 
		{
			retvalue += increment; // angular velocity
		}
		
		return retvalue;
	}

	float TestSetLimitValue(float value, float limit)
	{
		float retvalue = value;
		
		// If value is greater than limit then set value = limit
		// If value is less than -limit then set value = -limit
		if (value > limit)
		{
			retvalue = limit;
		} 
		else if (value < -limit)
		{
			retvalue = -limit;
		}
		
		return retvalue;
	}
	
	void ApplyGravityToObject()
	{ 	
		// Apply gravity to object - Assume standard OpenGL axis orientation of positive y being up 
		m_Acceleration.y = m_Acceleration.y - m_Gravity;
	}
	
	//****************************************Update Physics Object*******************************************************
	void UpdatePhysicsObject(Orientation orientation)
	{	
		// 0. Apply Gravity if needed
		if (m_ApplyGravity)
		{
			ApplyGravityToObject();
		}
		
		// 1. Update Linear Velocity	
		/////////////////////////////////////////////////////////////////////////////
		m_Acceleration.x  = TestSetLimitValue(m_Acceleration.x, m_MaxAcceleration.x);
		m_Acceleration.y  = TestSetLimitValue(m_Acceleration.y, m_MaxAcceleration.y);
		m_Acceleration.z  = TestSetLimitValue(m_Acceleration.z, m_MaxAcceleration.z);
		
		m_Velocity.Add(m_Acceleration);
		m_Velocity.x = TestSetLimitValue(m_Velocity.x, m_MaxVelocity.x);
		m_Velocity.y = TestSetLimitValue(m_Velocity.y, m_MaxVelocity.y);
		m_Velocity.z = TestSetLimitValue(m_Velocity.z, m_MaxVelocity.z);
		
		// 2. Update Angular Velocity
		/////////////////////////////////////////////////////////////////////////////////
		m_AngularAcceleration = TestSetLimitValue(m_AngularAcceleration, m_MaxAngularAcceleration);
		
		m_AngularVelocity += m_AngularAcceleration;
		m_AngularVelocity = TestSetLimitValue(m_AngularVelocity,m_MaxAngularVelocity);

		// 3. Reset Forces acting on Object 
		//    Rebuild forces acting on object for each update
		////////////////////////////////////////////////////////////////////////////////
		m_Acceleration.Clear();
		m_AngularAcceleration = 0;

		//4. Update Object Linear Position 
		////////////////////////////////////////////////////////////////////////////////
		Vector3 pos = orientation.GetPosition();
		pos.Add(m_Velocity);
		
		//Log.e("PHYSICS-UpdatePhysicsObject" , "Velocity =  " + m_Velocity.GetVectorString());
		
		// Check for object hitting ground if gravity is on.
		if (m_ApplyGravity)
		{
			if ((pos.y < m_GroundLevel)&& (m_Velocity.y < 0)) 
			{
				if (Math.abs(m_Velocity.y) > Math.abs(m_Gravity))
				{
					m_JustHitGround = true;
				}	
				pos.y = m_GroundLevel;
				m_Velocity.y = 0;
			}
		}

		//5. Update Object Angular Position
		////////////////////////////////////////////////////////////////////////////////
		// Add Rotation to Rotation Matrix
		orientation.AddRotation(m_AngularVelocity);
		
		//AddRotationToObject(orientation, m_AngularVelocity);
		
	}
	
	//***********************Update Physics Object Along Heading****************************************
	
	//***********************Update Physics Object Along Heading****************************************
		void UpdatePhysicsObjectHeading(Vector3 Heading, Orientation orientation)
		{	
			// Adjust for Gravity
			if (m_ApplyGravity)
			{
				ApplyGravityToObject();
			}
					
			// 1. Update Linear Velocity	
			/////////////////////////////////////////////////////////////////////////////
			m_Acceleration.x = TestSetLimitValue(m_Acceleration.x, m_MaxAcceleration.x);
			m_Acceleration.y = TestSetLimitValue(m_Acceleration.y, m_MaxAcceleration.y);
			m_Acceleration.z = TestSetLimitValue(m_Acceleration.z, m_MaxAcceleration.z);
			
			m_Velocity.Add(m_Acceleration);
			m_Velocity.x = TestSetLimitValue(m_Velocity.x, m_MaxVelocity.x);
			m_Velocity.y = TestSetLimitValue(m_Velocity.y, m_MaxVelocity.y);
			m_Velocity.z = TestSetLimitValue(m_Velocity.z, m_MaxVelocity.z);
			
			// 2. Update Angular Velocity
			/////////////////////////////////////////////////////////////////////////////////
			m_AngularAcceleration = TestSetLimitValue(m_AngularAcceleration, m_MaxAngularAcceleration);

			m_AngularVelocity += m_AngularAcceleration;
			m_AngularVelocity = TestSetLimitValue(m_AngularVelocity, m_MaxAngularVelocity);

			// 3. Reset Forces acting on Object 
			//    Rebuild forces acting on object for each update
			////////////////////////////////////////////////////////////////////////////////
			m_Acceleration.Clear();
			m_AngularAcceleration = 0;

			// 4. Adjust Velocity so that all the velocity is redirected along the heading.
			//////////////////////////////////////////////////////////////////////////////////
			float VelocityMagnitude	= m_Velocity.Length();
			
			if (VelocityMagnitude > m_MaxSpeed)
			{
				VelocityMagnitude = m_MaxSpeed;
			}
			
			Vector3 NewVelocity = new Vector3(Heading);
			NewVelocity.Normalize();
			NewVelocity.Multiply(VelocityMagnitude);
			
			
			Vector3 OldVelocity = new Vector3(m_Velocity);
			
			if (m_ApplyGravity)
			{
				m_Velocity.Set(NewVelocity.x, OldVelocity.y, NewVelocity.z);
			}
			else
			{
				m_Velocity.Set(NewVelocity.x, NewVelocity.y, NewVelocity.z);
			}
			
			
			//5. Update Object Linear Position 
			////////////////////////////////////////////////////////////////////////////////
			Vector3 pos = orientation.GetPosition();
			pos.Add(m_Velocity);
			orientation.SetPosition(pos);
			
			
			// Check for object hitting ground if gravity is on.
			if (m_ApplyGravity)
			{
				if ((pos.y < m_GroundLevel)&& (m_Velocity.y < 0)) 
				{
					if (Math.abs(m_Velocity.y) > Math.abs(m_Gravity))
					{
						m_JustHitGround = true;
					}
					pos.y = m_GroundLevel;
					m_Velocity.y = 0;	
				}
			}
			

			//6. Update Object Angular Position
			////////////////////////////////////////////////////////////////////////////////		
			// Add Rotation to Rotation Matrix
			orientation.AddRotation(m_AngularVelocity);
		}
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	CollisionStatus	CheckForCollisionSphereBounding(Object3d body1, Object3d body2)
	{
		float	ImpactRadiusSum 		= 0; 
		float 	RelativeVelocityNormal 	= 0; 
		float	CollisionDistance 		= 0;	
		Vector3	Body1Velocity;
		Vector3 Body2Velocity;
		CollisionStatus	retval;

		// 1. Calculate Separation
		//ImpactRadiusSum = body1.GetRadius() + body2.GetRadius();	
		ImpactRadiusSum = body1.GetScaledRadius() + body2.GetScaledRadius();	
	
		Vector3 Position1 = body1.m_Orientation.GetPosition();
		Vector3 Position2 =	body2.m_Orientation.GetPosition();
	
		Vector3 DistanceVec = Vector3.Subtract(Position1, Position2);
		CollisionDistance = DistanceVec.Length() - ImpactRadiusSum; 


		// 2. Set Collision Normal Vector		
		DistanceVec.Normalize();
		m_CollisionNormal = DistanceVec;
	

		// 3. Calculate Relative Normal Velocity:
		Body1Velocity = body1.GetObjectPhysics().GetVelocity();
		Body2Velocity = body2.GetObjectPhysics().GetVelocity();
	
		m_RelativeVelocity = Vector3.Subtract(Body1Velocity , Body2Velocity);
		RelativeVelocityNormal = m_RelativeVelocity.DotProduct(m_CollisionNormal);


		// 4. Test for collision 
		if((Math.abs(CollisionDistance) <= m_CollisionTolerance) && (RelativeVelocityNormal < 0.0))
		{
			retval = CollisionStatus.COLLISION;
		} 
		else 	
		if ((CollisionDistance < -m_CollisionTolerance) && (RelativeVelocityNormal < 0.0))
		{
			retval = CollisionStatus.PENETRATING_COLLISION;
		}
		else 	
		if (CollisionDistance < -m_CollisionTolerance) 
		{
			retval = CollisionStatus.PENETRATING;
		} 
		else
		{
			retval = CollisionStatus.NOCOLLISION;
		}
		
		return retval;
	}
	
	void ApplyLinearImpulse(Object3d body1, Object3d body2)
	{
		float m_Impulse;	

		// 1. Calculate the impulse along the line of action of the Collision Normal
		m_Impulse = (-(1+m_CoefficientOfRestitution) * (m_RelativeVelocity.DotProduct(m_CollisionNormal))) / 
					((1/body1.GetObjectPhysics().GetMass() + 1/body2.GetObjectPhysics().GetMass()));
		
		// 2. Apply Translational Force to bodies
		// f = ma;
		// f/m = a;
		Vector3 Force1 =  Vector3.Multiply( m_Impulse, m_CollisionNormal);
		Vector3 Force2 =  Vector3.Multiply(-m_Impulse, m_CollisionNormal);
		
		body1.GetObjectPhysics().ApplyTranslationalForce(Force1);
		body2.GetObjectPhysics().ApplyTranslationalForce(Force2);
	}

}
