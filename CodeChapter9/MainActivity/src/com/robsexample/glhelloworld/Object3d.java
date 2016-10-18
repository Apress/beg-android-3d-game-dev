package com.robsexample.glhelloworld;


import android.media.SoundPool;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLU;


public class Object3d 
{
	Orientation	m_Orientation = null;
	
	private Context		m_Context	= null;
	private Mesh 		m_Mesh 		= null;
	private MeshEx		m_MeshEx	= null;
	private Texture[] 	m_Textures 	= null;
	private Material 	m_Material 	= null;
	private Shader		m_Shader	= null;
	
	private float[] m_MVPMatrix = new float[16];
	private float[] m_ModelMatrix = new float[16];
	private float[] m_ModelViewMatrix = new float[16];
	private float[] m_NormalMatrix = new float[16];
	private float[] m_NormalMatrixInvert = new float[16];
	
	private int m_PositionHandle;   
	private int m_TextureHandle;
	private int m_NormalHandle;
	   
	private int m_ActiveTexture = -1;
	
	// Texture Animation Control
	private boolean m_AnimateTextures 	= false;
	private int 	m_StartTexAnimNum 	= 0;
	private int 	m_StopTexAnimNum 	= 0;
	private float 	m_TexAnimDelay 		= 0; // in seconds
	private float 	m_TargetTime 		= 0;
	private float 	m_Counter 			= 0;
	
	
	// Physics
	private Physics m_Physics;
	
	// Gravity Grid
	private Vector3 m_GridSpotLightColor = new Vector3(0,1,0);
		

	// Is Visible
	private boolean m_Visible = true;
	
	
	// Sound
	private int MAX_SOUNDS = 5;
	private int m_NumberSounds = 0;
	private Sound[] m_SoundEffects = new Sound[MAX_SOUNDS];
	private boolean[] m_SoundEffectsOn = new boolean[MAX_SOUNDS];
		

	// HUD
	// Blend 
	private boolean m_Blend = false;
		
		
	// Persistent Data
	private Stats m_ObjectStats;
	
	
	// Explosions
	private int MAX_EXPLOSIONS = 3;
	private int m_NumberExplosions = 0;
	private SphericalPolygonExplosion[] m_Explosions = new SphericalPolygonExplosion[MAX_EXPLOSIONS];
	
	
	
	
	
	// For User Interface
	private float[] m_ProjMatrix = new float[16];
	
	
	
	Object3d(Context iContext, 
			 Mesh iMesh, 
			 MeshEx iMeshEx, 
			 Texture[] iTextures, 
			 Material iMaterial, 
			 Shader iShader)
	{	
		m_Context	= iContext;
		m_Mesh 		= iMesh;
		m_MeshEx	= iMeshEx;
		m_Textures 	= iTextures;
		m_Material 	= iMaterial;
		m_Shader	= iShader;
		
		if (m_Textures == null)
		{
			m_ActiveTexture = -1;
		}
		else
		{
			m_ActiveTexture = 0;
		}
		
		m_Orientation = new Orientation(m_Context);	
		
		
		
		// Physics
		m_Physics = new Physics(iContext);
		
		
		// Initialize Object Stats
		m_ObjectStats = new Stats(iContext);
		
	}
	
	
	
	
	
	
	 float[] MapObjectCoordsToWindowCoords(int[] View, int ViewOffset, Vector3 ObjOffset)
	 {
		 //Map object coordinates into window coordinates. gluProject transforms the specified object 
		 //coordinates into window coordinates using model, proj, and view. The result is stored in win.
		 float[] WindowCoords = new float[3]; 
		 int result = 0;
		
		 // Android gluProject declaration
		 //public static int gluProject (float objX, float objY, float objZ, 
		 //								 float[] model, int modelOffset, 
		 //								 float[] project, int projectOffset, 
		 //								 int[] view, int viewOffset, 
		 //								 float[] win, int winOffset)
		 result = GLU.gluProject (ObjOffset.x, 
				 				  ObjOffset.y, 
				 				  ObjOffset.z, 
				 				  m_ModelViewMatrix, 0, 
				 				  m_ProjMatrix, 0, 
				 				  View, ViewOffset, 
								  WindowCoords, 0);
		 CheckGLError("gluProject"); 
			 
		 if (result == GLES20.GL_FALSE)
		 {
			 Log.e("class Object3d::MapObjectCoordsToWindowCoords()", "ERROR = GLU.gluProject failed!!!");
			 Log.e("View = ", View[0] + "," + View[1] + ", " + View[2] + ", " + View[3]);
		 }
		 else
		 {
			 //Log.d("DEBUG - In GetWindowCoordsObject-> GLUPROJECT", "WindowCoords(x,y,z) = " + 
		     //       WindowCoords[0] + "," + WindowCoords[1]+ "," + WindowCoords[2]);	 
		 }
		 return WindowCoords;
	 }
	 
	 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	Context GetContext()
	{
		return m_Context;
	}
	
	
	
	// Explosions
		SphericalPolygonExplosion GetExplosion(int index)
		{
			if (index >= MAX_EXPLOSIONS)
			{
				return null;
			}
			
			return m_Explosions[index];
		}
		
		int AddExplosion(SphericalPolygonExplosion Explosion)
		{
			if (m_NumberExplosions >= MAX_EXPLOSIONS)
			{
				return -1;
			}
			
			m_NumberExplosions++;
			
			int index = m_NumberExplosions - 1;
			m_Explosions[index] = Explosion;
			
			return index;
		}
		
		void RenderExplosions(Camera Cam, PointLight light)
		{
			for (int i = 0; i < m_NumberExplosions; i++)
			{
				m_Explosions[i].RenderExplosion(Cam, light);
			}
		}
		
		void UpdateExplosions()
		{
			for (int i = 0; i < m_NumberExplosions; i++)
			{		
				m_Explosions[i].UpdateExplosion();
			}
		}
		
		void ExplodeObject(float MaxVelocity, float MinVelocity)
		{
			for (int i = 0; i < m_NumberExplosions; i++)
			{
				m_Explosions[i].StartExplosion(m_Orientation.GetPosition(), MaxVelocity, MinVelocity);
			}
		}
		
	
	
	// Stats
	Stats GetObjectStats()
	{
		return m_ObjectStats;
	}
	
	void TakeDamage(Object3d DamageObj)
	{
		int DamageAmount = DamageObj.GetObjectStats().GetDamageValue();
		int Health = m_ObjectStats.GetHealth();
		
		Health = Health - DamageAmount;
		
		// Health can never be negative
		if (Health < 0)
		{
			Health = 0;
		}
		m_ObjectStats.SetHealth(Health);
	}
	
	
	
	// Collision
	Physics.CollisionStatus CheckCollision(Object3d object)
	{
		Physics.CollisionStatus result = m_Physics.CheckForCollisionSphereBounding(this, object);
		
		return result;
	}
	
	void ApplyLinearImpulse(Object3d object)
	{
		m_Physics.ApplyLinearImpulse(this, object);
	}
	
	
	////////////////////////// Persistent State ///////////////////////////////////////
	// Save and Load States of an Object using Shared Preferences.
		void SaveObjectState(String Handle)
		{
			SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
			SharedPreferences.Editor editor = settings.edit();
			      
			// Visible
			editor.putBoolean("Visible", m_Visible);
			             
			// Commit the edits!
			editor.commit();
			
			// Save orientation
			String OrientationHandle = Handle + "Orientation";
			m_Orientation.SaveState(OrientationHandle);
			
			// Save Physics
			String PhysicsHandle = Handle + "Physics";
			m_Physics.SaveState(PhysicsHandle);
				
			// Save Stats
			String StatsHandle = Handle + "Stats";
			m_ObjectStats.SaveStats(StatsHandle);
		}

		void LoadObjectState(String Handle)
		{
			SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
				
			// Visible
			m_Visible = settings.getBoolean("Visible",true);
			     
			// Orientation
			String OrientationHandle = Handle + "Orientation";
			m_Orientation.LoadState(OrientationHandle);
			
			// Physics
			String PhysicsHandle = Handle + "Physics";
			m_Physics.LoadState(PhysicsHandle);
			
			// Stats
			String StatsHandle = Handle + "Stats";
			m_ObjectStats.LoadStats(StatsHandle);
		}
		

	public void CheckGLError(String glOperation) 
	{
		int error;
	    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) 
	    {
	    	Log.e("class Object3d :", glOperation + " IN CHECKGLERROR() : glError " + GLU.gluErrorString(error));
	        throw new RuntimeException(glOperation + ": glError " + error);
	    }
	}
	    

	// Sound Effects
	int AddSound(Sound iSound)
	{
		int Index = m_NumberSounds;
		
		if (m_NumberSounds >= MAX_SOUNDS)
		{
			return -1;
		}
		
		m_SoundEffects[Index] = iSound;
		m_NumberSounds++;
		
		return Index;
	}
	
	void SetSFXOnOff(boolean Value)
	{
		for (int i = 0; i < m_NumberSounds;i++)
		{
			m_SoundEffectsOn[i] = Value;
		}
	}

	int AddSound(SoundPool Pool, int ResourceID)
	{
		int SoundIndex = -1;
		
		//Sound SFX = new Sound(m_Context, ResourceID);
		Sound SFX = new Sound(m_Context, Pool, ResourceID);
		
		SoundIndex = AddSound(SFX);
		
		return SoundIndex;
	}
	
	void PlaySound(int SoundIndex)
	{
		if ((SoundIndex < m_NumberSounds) && 
			(m_SoundEffectsOn[SoundIndex]))
		{
			// Play Sound
			m_SoundEffects[SoundIndex].PlaySound();
		}
		else
		{
			Log.e("OBJECT3D", "ERROR IN PLAYING SOUND, SOUNDINDEX = " + SoundIndex);
		}
	}
	

	// HUD
	Material GetMaterial()
	{
		return m_Material;
	}
	
	// Blend
	void SetBlend(boolean value)
	{
			m_Blend = value;
	}
	
	
	// Visibility
	void SetVisibility(boolean value)
	{
		m_Visible = value;
	}
	
	boolean IsVisible()
	{
		return m_Visible;
	}
	
	
	// Texture
	
	// For Player Power Pyramid
	void SetAnimateTextures(boolean iAnimate, float delay, int StartTexture, int StopTexture)
	{
		m_AnimateTextures = iAnimate;
		m_StartTexAnimNum = StartTexture;
		m_StopTexAnimNum = StopTexture;
		m_TexAnimDelay = delay;
		m_Counter = SystemClock.uptimeMillis() * 1/1000.0f; // Convert to seconds
		m_TargetTime = m_Counter + m_TexAnimDelay;
	}
	
	boolean SetTexture(int TextureNumber)
	{
		if (TextureNumber < m_Textures.length)
		{
			m_ActiveTexture = TextureNumber;
			return true;
		}
		
		return false;
	}
	
	Texture GetTexture(int TextureNumber)
	{
		if (TextureNumber < m_Textures.length)
		{
			return m_Textures[TextureNumber];
		}
		else
		{
			return null;
		}
	}
	
	
	// Grid 
	void SetGridSpotLightColor(Vector3 Color)
	{
		m_GridSpotLightColor.Set(Color.x, Color.y, Color.z);
	}
	
	float[] GetGridSpotLightColor()
	{
		return m_GridSpotLightColor.AsFloatArray();
	}
	
	
	// Collision
	 	float GetRadius()
	 	{
	 		if (m_Mesh != null)
	 		{
	 			return m_Mesh.GetRadius();
	 		}
			
			if (m_MeshEx != null)
			{
				return m_MeshEx.GetRadius();
			}
			return -1;
		}
		
		float GetScaledRadius()
		{
			float LargestScaleFactor = 0;
			float ScaledRadius = 0;
			float RawRadius = GetRadius();
			
			Vector3 ObjectScale = m_Orientation.GetScale();
			
			if (ObjectScale.x > LargestScaleFactor)
			{
				LargestScaleFactor = ObjectScale.x;
			}
			
			if (ObjectScale.y > LargestScaleFactor)
			{
				LargestScaleFactor = ObjectScale.y;
			}
			
			if (ObjectScale.z > LargestScaleFactor)
			{
				LargestScaleFactor = ObjectScale.z;
			}
			
			
			ScaledRadius = RawRadius * LargestScaleFactor;
			
			return ScaledRadius;
		}
		
	
	
	
	
	// Physics
	Physics GetObjectPhysics()
	{
		return m_Physics;
	}
	
	void UpdateObjectPhysics()
	{
		m_Physics.UpdatePhysicsObject(m_Orientation);
	}
	
	void UpdateObject3d()
	{
		if (m_Visible)
		{
			// Update Object3d Physics
			UpdateObjectPhysics();
		}
		
		// Update Object3d Particle Emitters
		//UpdatePolyParticleEmitter();
		
		// Update Explosions associated with this object
		UpdateExplosions();
	}
	
	// Vehicles
	void UpdateObject3dToHeading(Vector3 Heading)
	{
		if (m_Visible)
		{
			// Update Object3d Physics
			m_Physics.UpdatePhysicsObjectHeading(Heading, m_Orientation);
		}
		
		// Update Object3d Particle Emitters
		//UpdatePolyParticleEmitter();
		
		// Update Explosions associated with this object
		UpdateExplosions();
	}
	

	void ActivateTexture()
	{
		if (m_ActiveTexture >= 0)
		{	 
			// Activate Texture Unit
			Texture.SetActiveTextureUnit(GLES20.GL_TEXTURE0);
			CheckGLError("glActiveTexture - SetActiveTexture Unit Failed");  
			 
			if ((m_AnimateTextures)&& (m_Counter >= m_TargetTime))
			{
				m_ActiveTexture++;
				if (m_ActiveTexture > m_StopTexAnimNum)
				{
					m_ActiveTexture = m_StartTexAnimNum; 
				}
				m_TargetTime = m_Counter + m_TexAnimDelay;
			}
			m_Counter = SystemClock.uptimeMillis() * 1/1000.0f; // Convert to seconds
		
			
			// Activates texture for this object
			m_Textures[m_ActiveTexture].ActivateTexture();
		}
	}
	 
	void GetVertexAttribInfo()
	{ 	
		// Set up Vertex Position Data
        m_PositionHandle = m_Shader.GetShaderVertexAttributeVariableLocation("aPosition");
        CheckGLError("glGetAttribLocation - aPosition");     
        
        // Set up Vertex Texture Data
        m_TextureHandle = m_Shader.GetShaderVertexAttributeVariableLocation("aTextureCoord");
        CheckGLError("glGetAttribLocation - aTextureCoord");     
        
        // Get Handle to Normal Vertex Attribute Variable
    	m_NormalHandle = m_Shader.GetShaderVertexAttributeVariableLocation("aNormal");	
    	CheckGLError("glGetAttribLocation - aNormal");     
	}

	
	 void SetLighting(Camera Cam, 
			 		  PointLight light, 
				      float[] ModelMatrix, 
				      float[] ViewMatrix, 
				      float[] ModelViewMatrix,
				      float[] NormalMatrix)
	 {
		 float[] AmbientColor		= light.GetAmbientColor();
		 float[] DiffuseColor 		= light.GetDiffuseColor();
		 float[] SpecularColor 		= light.GetSpecularColor();
		 float	SpecularShininess 	= light.GetSpecularShininess();

		 Vector3 EyePos = Cam.GetCameraEye();

		 m_Shader.SetShaderUniformVariableValue("uLightAmbient", AmbientColor);	
		 m_Shader.SetShaderUniformVariableValue("uLightDiffuse", DiffuseColor);   	
		 m_Shader.SetShaderUniformVariableValue("uLightSpecular", SpecularColor);    	
		 m_Shader.SetShaderUniformVariableValue("uLightShininess", SpecularShininess);  	
		 m_Shader.SetShaderUniformVariableValue("uWorldLightPos", light.GetPosition());  	
		 
		 m_Shader.SetShaderUniformVariableValue("uEyePosition", EyePos);

		 m_Shader.SetShaderVariableValueFloatMatrix4Array("NormalMatrix", 1, false, NormalMatrix, 0);	
		 m_Shader.SetShaderVariableValueFloatMatrix4Array("uModelMatrix", 1, false, ModelMatrix, 0);	
		 m_Shader.SetShaderVariableValueFloatMatrix4Array("uViewMatrix", 1, false, ViewMatrix, 0);	
		 m_Shader.SetShaderVariableValueFloatMatrix4Array("uModelViewMatrix", 1, false, ModelViewMatrix, 0);	 
 
		 // Set object lighting Material Properties in shader
		 if (m_Material != null)
		 {
			 m_Shader.SetShaderUniformVariableValue("uMatEmissive", m_Material.GetEmissive());
			 m_Shader.SetShaderUniformVariableValue("uMatAmbient", m_Material.GetAmbient());
			 m_Shader.SetShaderUniformVariableValue("uMatDiffuse", m_Material.GetDiffuse());
			 m_Shader.SetShaderUniformVariableValue("uMatSpecular", m_Material.GetSpecular());
			 m_Shader.SetShaderUniformVariableValue("uMatAlpha", m_Material.GetAlpha());
		 }
	 }
	  
	 void GenerateMatrices(Camera  	Cam, 
			 			   Vector3 	iPosition,
			 			   Vector3  iRotationAxis,
			 			   Vector3 	iScale)
	 {
		 // Initialize Model Matrix
	     Matrix.setIdentityM(m_ModelMatrix, 0);  
		 
		 m_Orientation.SetPosition(iPosition);
	     m_Orientation.SetRotationAxis(iRotationAxis);
	     m_Orientation.SetScale(iScale);
	    
	     
	     // Rotate then Translate object
	     m_ModelMatrix = m_Orientation.UpdateOrientation();
	     
	     // Translate then Rotate object
	     //m_ModelMatrix = m_Orientation.UpdateOrientationTranslateRotate();
		   
	     
	     
	          
	     // Create Model View Matrix
	     Matrix.multiplyMM(m_ModelViewMatrix, 0, Cam.GetViewMatrix(), 0, m_ModelMatrix, 0);
	             
	     // Create Normal Matrix for lighting	      
	     Matrix.multiplyMM(m_NormalMatrix, 0, Cam.GetViewMatrix(), 0, m_ModelMatrix, 0);
	     Matrix.invertM(m_NormalMatrixInvert, 0, m_NormalMatrix, 0);
	     Matrix.transposeM(m_NormalMatrix, 0, m_NormalMatrixInvert, 0);
	       
	     // Create Model View Projection Matrix
	     Matrix.multiplyMM(m_MVPMatrix, 0, Cam.GetProjectionMatrix(), 0, m_ModelViewMatrix, 0);  
	     
	     
	     
	     
	     
	     
	     // Save a Copy of the Projection Matrix
	     m_ProjMatrix =  Cam.GetProjectionMatrix().clone();  
	 }
	 
	void DrawObject(Camera 		Cam,
					PointLight 	light,
					Vector3 	iPosition,
		 			Vector3  	iRotationAxis,
		 			Vector3 	iScale)
	{
		// Activate and set up the Shader and Draw Object's mesh
	   
		// Generate Needed Matrices for Object
		GenerateMatrices(Cam, 
	 			   		 iPosition,
	 			   		 iRotationAxis,
	 			   		 iScale);
		
	    // Add program to OpenGL environment
		m_Shader.ActivateShader(); 
	    	
	    // Get Vertex Attribute Info in preparation for drawing the mesh
		GetVertexAttribInfo();
	    
	    // Sets up the lighting parameters for this object
	    SetLighting(Cam, light, m_ModelMatrix, Cam.GetViewMatrix(), m_ModelViewMatrix, m_NormalMatrix);
	              
	    // Apply the projection and view transformation matrix to the shader
	    m_Shader.SetShaderVariableValueFloatMatrix4Array("uMVPMatrix", 1, false, m_MVPMatrix, 0); 
	                
	    // Activates texture for this object
	    ActivateTexture();
	    
	    // Enable Hidden surface removal
	    GLES20.glEnable(GLES20.GL_DEPTH_TEST);	
	     
	    
	    // Draw Mesh for this Object
	    if (m_Mesh != null)
	    {
	    	m_Mesh.DrawMesh(m_PositionHandle, m_TextureHandle, m_NormalHandle);
	    }
	    else	    
	    if (m_MeshEx != null)
	    {
	    	m_MeshEx.DrawMesh(m_PositionHandle, m_TextureHandle, m_NormalHandle);
	    } 
	    else
	    {
	    	Log.d("class Object3d :", "No MESH in Object3d");
	    }
	  	  
	}

	void DrawObject(Camera Cam, PointLight light)
	{
		
		RenderExplosions(Cam,light);
		
		
		// HUD
		if (m_Blend)
		{
			GLES20.glEnable(GLES20.GL_BLEND);
	    	//GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	    	GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
		}
		
		
		// Glow Animation - Player's Power Pyramid
		if (m_Material.DoGlowAnimation())
		{
			m_Material.UpdateGlowAnimation();
		}
		
		
		
		if (m_Visible)
		{
			DrawObject(Cam,
					   light,
					   m_Orientation.GetPosition(),
					   m_Orientation.GetRotationAxis(),
					   m_Orientation.GetScale());
		}
		
		
		
		
		
		
		// HUD
		if (m_Blend)
		{
			GLES20.glDisable(GLES20.GL_BLEND);
		}
		
		
		
		
		
		
	}
	
}
