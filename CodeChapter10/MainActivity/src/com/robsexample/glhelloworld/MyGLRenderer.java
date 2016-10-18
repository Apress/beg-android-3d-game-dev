package com.robsexample.glhelloworld;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;
import android.content.Context;
import android.content.SharedPreferences;



enum GameState
{
	MainMenu,
	ActiveGamePlay,
	HighScoreTable,
	HighScoreEntry,
	GameOverScreen
}

public class MyGLRenderer implements GLSurfaceView.Renderer 
{
	private Context m_Context;
	
	private PointLight m_PointLight;
	private Camera m_Camera;
	
	private int m_ViewPortWidth;
    private int m_ViewPortHeight;
	
	
	// Gravity Grid
    private GravityGridEx	m_Grid;
	
    ///////////////////////////////////////////////////////////////////
    
    // SFX
 	private SoundPool 	m_SoundPool;
 	private int 		m_SoundIndex1;
 	private int 		m_SoundIndex2;
 	private boolean 	m_SFXOn = true;
    
  	// HUD
 	// Character Set
    private Texture[] m_CharacterSetTextures = new Texture[BillBoardCharacterSet.MAX_CHARACTERS];
    private BillBoardCharacterSet m_CharacterSet = null;
   
	// HUD
	private Texture 	m_HUDTexture 	= null;
    private BillBoard 	m_HUDComposite 	= null;	
    private HUD 		m_HUD 			= null;
	
    private int m_Health = 10;
    private int m_Score = 193;
    
  
    // Creating the Player
    private volatile boolean m_ScreenTouched = false;
    private float m_TouchX = 0;
    private float m_TouchY = 0;
    
    // Power Pyramid
    private PowerPyramid m_Pyramid;
    private Texture m_TexPyramid1;
    private Texture m_TexPyramid2;
      
    // Camera Movement
    private boolean m_CameraMoved = false;
	private float 	m_DeltaXAxisRotation = 0;
	private float 	m_DeltaYAxisRotation = 0;
    
	// Camera Max Angles
	private float m_MaxCameraAngle =  90.0f;
	private float m_MinCameraAngle = -90.0f;
	
    // Player's Weapon and Ammunition;
    private Weapon 		m_Weapon 	= null;  
    private Sound		m_PlayerWeaponSFX = null;
     
    
    // User Interface
    private GameState m_GameState = GameState.MainMenu;
    
    //Menu
  	private MainMenu m_MainMenu;
  	private HighScoreEntryMenu m_HighScoreEntryMenu;
  	
	// HighScore Table
	private HighScoreTable m_HighScoreTable;
	
  	private boolean m_CanContinue = false;
  	
  	 
  	// Game Play Controller
 	private GamePlayController m_GamePlayController;
 
 	// Enemy Objects
	private String ARENA_OBJECTS_HANDLE = "ArenaObjectsSet";
	private String AIR_VEHICLE_HANDLE = "AirFleet";
	private String TANK_FLEET_HANDLE = "TankFleet";
	
    //private AirVehicleFleet m_AirVehicleFleet;
    private ArenaObjectSet 	m_ArenaObjectsSet;
    private TankFleet 		m_TankFleet;
  	
    
    
    // Frame Rate Management Variables
    //private float		k_SecondsPerTick 	= 	0.10f 	* 1000.0f/1.0f; // milliseconds 10 frames /sec
    private float		k_SecondsPerTick 	= 	0.05f 	* 1000.0f/1.0f; // milliseconds 20 frames /sec
    //private float 	k_SecondsPerTick	=	0.0333f * 1000.0f/1.0f; // milliseconds 30 frames /sec
    //private float 	k_SecondsPerTick	=	0.0250f * 1000.0f/1.0f; // milliseconds 40 frames /sec
    //private float 	k_SecondsPerTick	=	0.0166f * 1000.0f/1.0f; // milliseconds 60 frames /sec
   
    private long 	m_ElapsedTime		=	0;
    private long	m_CurrentTime		=	0;
    private long	m_UpdateTimeCount	=	0;
    private boolean	m_TimeInit			=	false;
    
    
    
	// Game Over
	private BillBoard m_GameOverBillBoard;
	private long m_GameOverPauseTime = 1000;
	private long m_GameOverStartTime;
	
    
 
    ///////////////////////////////////////////////////////////////////
 	
	
    

	public MyGLRenderer(Context context) 
	{
	   m_Context = context; 
	}
	
	
	// Game Over
		void CreateGameOverBillBoard(Context iContext)
		{
			// Put Game over Billboard in front of camera
	        int TextureResourceID = R.drawable.gameover;
	       
	        Vector3 Position= new Vector3(0,0,0);
	        Vector3 Scale 	= new Vector3(1 , 0.5f, 0.5f);
	    
			m_GameOverBillBoard = CreateInitBillBoard(iContext,
				                                      TextureResourceID,
				                                      Position,
				                                      Scale);
		}
		
		void UpdateGameOverBillBoard()
		{
			Vector3 TempVec = new Vector3(0,0,0);
			float DistanceToBillBoard = 5;
	        
	    	TempVec.Set(m_Camera.GetOrientation().GetForwardWorldCoords().x,
	    			    m_Camera.GetOrientation().GetForwardWorldCoords().y, 
	    			    m_Camera.GetOrientation().GetForwardWorldCoords().z);
	    	TempVec.Multiply(DistanceToBillBoard);

	    	Vector3 Position = Vector3.Add(m_Camera.GetOrientation().GetPosition(), TempVec);
			
			m_GameOverBillBoard.m_Orientation.SetPosition(Position);
		}

	
	
	
	
	
	void TurnSFXOnOff(boolean value)
	{
		// Tanks
		m_TankFleet.SetSoundOnOff(value);
		
		// Ships
		//m_AirVehicleFleet.SetSoundOnOff(value);
		
		// Arena Objects
		m_ArenaObjectsSet.SetSoundOnOff(value);
		
		// Pyramid
		m_Pyramid.SetSFXOnOff(value);
	}
	
	 ArenaObject3d CreateArenaObjectCube1(Context iContext)
	 {
		 ArenaObject3d Cube1 = null;
		 
		 //Create Cube Shader
		 Shader Shader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight);	// ok
    	         
		 //MeshEx(int CoordsPerVertex, 
		 //		int MeshVerticesDataPosOffset, 
		 //		int MeshVerticesUVOffset , 
		 //		int MeshVerticesNormalOffset,
		 //		float[] Vertices,
		 //		short[] DrawOrder
		 //MeshEx CubeMesh = new MeshEx(8,0,3,5,Cube.CubeData, Cube.CubeDrawOrder);
		 MeshEx CubeMesh = new MeshEx(8,0,3,5,Cube.CubeData4Sided, Cube.CubeDrawOrder);
	        	 
		 // Create Material for this object
		 Material Material1 = new Material();
		 //Material1.SetEmissive(0.0f, 0, 0.25f);
    
		 // Create Texture
		 Texture TexAndroid = new Texture(iContext,R.drawable.ic_launcher);		
        
		 Texture[] CubeTex = new Texture[1];
		 CubeTex[0] = TexAndroid;
           
		 float XMaxBoundary = m_Grid.GetXMaxBoundary();
	 	 float XMinBoundary = m_Grid.GetXMinBoundary();
	 	 float ZMaxBoundary = m_Grid.GetZMaxBoundary();
	 	 float ZMinBoundary = m_Grid.GetZMinBoundary();
		 
		 Cube1 = new ArenaObject3d(iContext, 
		 		   null,
		 		   CubeMesh, 
				   CubeTex, 
				   Material1, 
				   Shader,
		       
				   XMaxBoundary,
				   XMinBoundary,
				   ZMaxBoundary,
				   ZMinBoundary);
		 
		 // Set Intial Position and Orientation
		 Vector3 Axis = new Vector3(0,1,0);
		 Vector3 Position = new Vector3(-1.0f, 2.0f, -1.0f);
		 Vector3 Scale = new Vector3(1.0f,1.0f,1.0f);
        
		 Cube1.m_Orientation.SetPosition(Position);
		 Cube1.m_Orientation.SetRotationAxis(Axis);
		 Cube1.m_Orientation.SetScale(Scale);
		 
		 // Gravity
		 Cube1.GetObjectPhysics().SetGravity(true);
		 
		 // Set Gravity Grid Parameters
		 Vector3 GridColor = new Vector3(1,0,0);
		 Cube1.SetGridSpotLightColor(GridColor);
		 Cube1.GetObjectPhysics().SetMassEffectiveRadius(6);
		 
		 //Create Explosion
		 int		NumberParticles	= 20; 
		 Vector3	Color			= new Vector3(1,1,0);
		 long		ParticleLifeSpan= 2000;
		 boolean	RandomColors	= false;
		 boolean	ColorAnimation	= false;
		 float		FadeDelta		= 0.005f;
		 Vector3	ParticleSize	= new Vector3(0.5f,0.5f,0.5f);
			  
	    SphericalPolygonExplosion Explosion = CreateExplosion(iContext,
					  										NumberParticles,
					  										Color,
					  										ParticleSize,
					  										ParticleLifeSpan,
					  										RandomColors,
					  										ColorAnimation, 
					  										FadeDelta);
	 
		 Cube1.AddExplosion(Explosion);
		 
		 // Add Sound Effects
		 int HitGroundSFX = R.raw.explosion2;
		 int ExplosionSFX = R.raw.explosion1;
	       
		 //SFX
		 Cube1.CreateHitGroundSFX(m_SoundPool, HitGroundSFX);
	     Cube1.CreateExplosionSFX(m_SoundPool, ExplosionSFX);
	     Cube1.SetSFXOnOff(true);
		 
		 // Set Initial Velocity
		 Vector3 Force = new Vector3(10,0,5);
		 Vector3 AxisRotation = new Vector3(1,1,1);
		 float MaxVelocity = 0.1f;
		 
		 Cube1.GetObjectPhysics().ApplyTranslationalForce(Force);
		 Cube1.m_Orientation.SetRotationAxis(AxisRotation);
		 Cube1.GetObjectPhysics().GetMaxVelocity().Set(MaxVelocity, 1, MaxVelocity);
		 
		 return Cube1;	
	 }
    
	 ArenaObject3d CreateArenaObjectCube2(Context iContext)
	 {
		 ArenaObject3d Cube1 = null;
		 
		 //Create Cube Shader
		 Shader Shader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight);	// ok
    	         
		 //MeshEx(int CoordsPerVertex, 
		 //		int MeshVerticesDataPosOffset, 
		 //		int MeshVerticesUVOffset , 
		 //		int MeshVerticesNormalOffset,
		 //		float[] Vertices,
		 //		short[] DrawOrder
		 //MeshEx CubeMesh = new MeshEx(8,0,3,5,Cube.CubeData, Cube.CubeDrawOrder);
		 MeshEx CubeMesh = new MeshEx(8,0,3,5,Cube.CubeData4Sided, Cube.CubeDrawOrder);
	        	 
		 // Create Material for this object
		 Material Material1 = new Material();
		 //Material1.SetEmissive(0.0f, 0, 0.25f);
    
		 // Create Texture
		 Texture TexAndroid = new Texture(iContext,R.drawable.health);		
        
		 Texture[] CubeTex = new Texture[1];
		 CubeTex[0] = TexAndroid;
           
		 float XMaxBoundary = m_Grid.GetXMaxBoundary();
	 	 float XMinBoundary = m_Grid.GetXMinBoundary();
	 	 float ZMaxBoundary = m_Grid.GetZMaxBoundary();
	 	 float ZMinBoundary = m_Grid.GetZMinBoundary();
		 
		 Cube1 = new ArenaObject3d(iContext, 
		 		   null,
		 		   CubeMesh, 
				   CubeTex, 
				   Material1, 
				   Shader,
		       
				   XMaxBoundary,
				   XMinBoundary,
				   ZMaxBoundary,
				   ZMinBoundary);
		 
		 // Set Intial Position and Orientation
		 Vector3 Axis = new Vector3(0,1,0);
		 Vector3 Position = new Vector3(-1.0f, 2.0f, -1.0f);
		 Vector3 Scale = new Vector3(1.0f,1.0f,1.0f);
        
		 Cube1.m_Orientation.SetPosition(Position);
		 Cube1.m_Orientation.SetRotationAxis(Axis);
		 Cube1.m_Orientation.SetScale(Scale);
		 
		 // Gravity
		 Cube1.GetObjectPhysics().SetGravity(true);
		 
		 // Set Gravity Grid Parameters
		 Vector3 GridColor = new Vector3(1,0,0);
		 Cube1.SetGridSpotLightColor(GridColor);
		 Cube1.GetObjectPhysics().SetMassEffectiveRadius(6);
		 
		 //Create Explosion
		 int		NumberParticles	= 20; 
		 Vector3	Color			= new Vector3(1,1,0);
		 long		ParticleLifeSpan= 2000;
		 boolean	RandomColors	= false;
		 boolean	ColorAnimation	= true;
		 float		FadeDelta		= 0.005f;
		 Vector3	ParticleSize	= new Vector3(0.5f,0.5f,0.5f);
			  
	    SphericalPolygonExplosion Explosion = CreateExplosion(iContext,
					  										NumberParticles,
					  										Color,
					  										ParticleSize,
					  										ParticleLifeSpan,
					  										RandomColors,
					  										ColorAnimation, 
					  										FadeDelta);
	 
		 Cube1.AddExplosion(Explosion);
		 
		 // Add Sound Effects
		 int HitGroundSFX = R.raw.explosion2;
		 int ExplosionSFX = R.raw.explosion1;
	       
		 //SFX
		 Cube1.CreateHitGroundSFX(m_SoundPool, HitGroundSFX);
	     Cube1.CreateExplosionSFX(m_SoundPool, ExplosionSFX);
	     Cube1.SetSFXOnOff(true);
		 
		 // Set Initial Velocity
		 Vector3 Force = new Vector3(10,0,5);
		 Vector3 AxisRotation = new Vector3(1,1,1);
		 float MaxVelocity = 0.1f;
		 
		 Cube1.GetObjectPhysics().ApplyTranslationalForce(Force);
		 Cube1.m_Orientation.SetRotationAxis(AxisRotation);
		 Cube1.GetObjectPhysics().GetMaxVelocity().Set(MaxVelocity, 1, MaxVelocity);
		 
		 return Cube1;	
	 }
    
	 
	
	 
	 
	 ArenaObject3d CreateArenaObjectCube3(Context iContext)
	 {
		 ArenaObject3d Cube1 = null;
		 
		 //Create Cube Shader
		 Shader Shader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight);	// ok
    	         
		 //MeshEx(int CoordsPerVertex, 
		 //		int MeshVerticesDataPosOffset, 
		 //		int MeshVerticesUVOffset , 
		 //		int MeshVerticesNormalOffset,
		 //		float[] Vertices,
		 //		short[] DrawOrder
		 //MeshEx CubeMesh = new MeshEx(8,0,3,5,Cube.CubeData, Cube.CubeDrawOrder);
		 MeshEx CubeMesh = new MeshEx(8,0,3,5,Cube.CubeData4Sided, Cube.CubeDrawOrder);
	        	 
		 // Create Material for this object
		 Material Material1 = new Material();
		 //Material1.SetEmissive(0.0f, 0, 0.25f);
    
		 // Create Texture
		 Texture TexAndroid = new Texture(iContext,R.drawable.charset1);		
        
		 Texture[] CubeTex = new Texture[1];
		 CubeTex[0] = TexAndroid;
           
		 float XMaxBoundary = m_Grid.GetXMaxBoundary();
	 	 float XMinBoundary = m_Grid.GetXMinBoundary();
	 	 float ZMaxBoundary = m_Grid.GetZMaxBoundary();
	 	 float ZMinBoundary = m_Grid.GetZMinBoundary();
		 
		 Cube1 = new ArenaObject3d(iContext, 
		 		   null,
		 		   CubeMesh, 
				   CubeTex, 
				   Material1, 
				   Shader,
		       
				   XMaxBoundary,
				   XMinBoundary,
				   ZMaxBoundary,
				   ZMinBoundary);
		 
		 // Set Intial Position and Orientation
		 Vector3 Axis = new Vector3(0,1,0);
		 Vector3 Position = new Vector3(-1.0f, 2.0f, -1.0f);
		 Vector3 Scale = new Vector3(1.0f,1.0f,1.0f);
        
		 Cube1.m_Orientation.SetPosition(Position);
		 Cube1.m_Orientation.SetRotationAxis(Axis);
		 Cube1.m_Orientation.SetScale(Scale);
		 
		 // Gravity
		 Cube1.GetObjectPhysics().SetGravity(true);
		 
		 // Set Gravity Grid Parameters
		 Vector3 GridColor = new Vector3(1,0,0);
		 Cube1.SetGridSpotLightColor(GridColor);
		 Cube1.GetObjectPhysics().SetMassEffectiveRadius(6);
		 
		 //Create Explosion
		 int		NumberParticles	= 20; 
		 Vector3	Color			= new Vector3(1,1,0);
		 long		ParticleLifeSpan= 2000;
		 boolean	RandomColors	= false;
		 boolean	ColorAnimation	= true;
		 float		FadeDelta		= 0.005f;
		 Vector3	ParticleSize	= new Vector3(0.5f,0.5f,0.5f);
			  
	    SphericalPolygonExplosion Explosion = CreateExplosion(iContext,
					  										NumberParticles,
					  										Color,
					  										ParticleSize,
					  										ParticleLifeSpan,
					  										RandomColors,
					  										ColorAnimation, 
					  										FadeDelta);
	 
		 Cube1.AddExplosion(Explosion);
		 
		 // Add Sound Effects
		 int HitGroundSFX = R.raw.explosion2;
		 int ExplosionSFX = R.raw.explosion1;
	       
		 //SFX
		 Cube1.CreateHitGroundSFX(m_SoundPool, HitGroundSFX);
	     Cube1.CreateExplosionSFX(m_SoundPool, ExplosionSFX);
	     Cube1.SetSFXOnOff(true);
		 
		 // Set Initial Velocity
		 Vector3 Force = new Vector3(10,0,5);
		 Vector3 AxisRotation = new Vector3(1,1,1);
		 float MaxVelocity = 0.1f;
		 
		 Cube1.GetObjectPhysics().ApplyTranslationalForce(Force);
		 Cube1.m_Orientation.SetRotationAxis(AxisRotation);
		 Cube1.GetObjectPhysics().GetMaxVelocity().Set(MaxVelocity, 1, MaxVelocity);
		 
		 return Cube1;	
	 }
    
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	
	
	 void CreateArenaObjectsSet(Context iContext)
	    {
	    	m_ArenaObjectsSet = new ArenaObjectSet(iContext);
			
	    	// Cube
	    	//Vector3 Position = new Vector3(-5, 7, 0);
			//Vector3 Scale =  new Vector3(0.5f,0.5f,0.5f);
			//Vector3 TForce = new Vector3(-0.05f, 0.0f, 0.04f);
			//float RotationalForce = 5000;
			//float Mass = 3;
			//Vector3 GridColor = new Vector3(0.0f, 0.0f, 0.7f);  
	        //float MassEffectiveRadius = 3.5f;
	        float MaxVelocity = 0.1f;
	        
	        //Material iMaterial = new Material();
	        //iMaterial.SetDiffuse(0.5f, 0, 0);
			//iMaterial.SetSpecular(0, 0, 0);
			//iMaterial.SetGlowAnimation(true);
			//iMaterial.GetEmissiveMax().Set(0.5f, 0, 0);
			//iMaterial.GetEmissiveMin().Set(0.0f, 0, 0);
	        
	     
	    	ArenaObject3d Obj = CreateArenaObjectCube1(iContext);
	    	
			// ID
			Obj.SetArenaObjectID("cube1");	
			Obj.GetObjectStats().SetDamageValue(10);
			Obj.GetObjectPhysics().GetMaxVelocity().Set(MaxVelocity, 1, MaxVelocity);
			
			// Add new Object
			boolean result = m_ArenaObjectsSet.AddNewArenaObject(Obj);
			
	    	///////////////////////////////////////////////
			
			Obj = CreateArenaObjectCube2(iContext);
	    	
			// ID
			Obj.SetArenaObjectID("cube2");	
			Obj.GetObjectStats().SetDamageValue(10);
			Obj.GetObjectPhysics().GetMaxVelocity().Set(MaxVelocity, 1, MaxVelocity);
			
			// Add new Object
			result = m_ArenaObjectsSet.AddNewArenaObject(Obj);
			
	    	///////////////////////////////////////////////
	    }
	
	
	
	
	 Weapon CreateTankWeaponType2(Context iContext)
	 	{
	 		Weapon TankWeapon = null;
	 		
	 		Vector3 AmmoColor = new Vector3(0,0,1);
	         int AmmoSoundResourceID= R.raw.clustoid; //R.raw.weapon2;
	         boolean GravityOn= false;
	         float GravityValue=0;
	          
	         float AmmunitionRange = 100;
	         float AmmunitionSpeed = 0.5f;
	         
	         Vector3 ScaleAmmo = new Vector3(0.2f,0.2f,0.2f);
	 	
	        int AmmoDamageValue = 2;
	         
	 		TankWeapon = CreateWeapon(iContext, 
	 	            				  AmmoColor,
	 	            				  AmmoSoundResourceID,
	 	            				  GravityOn,
	 	            				  GravityValue,
	 	            				  AmmunitionRange,
	 	            				  AmmunitionSpeed,
	 	            				  ScaleAmmo,
	 	            				  AmmoDamageValue);
	 	
	 		return TankWeapon;
	 	}
	
	
	 Tank CreateTankType2(Context iContext)
	    {
	    	//Weapon
	    	Weapon TankWeapon = CreateTankWeaponType2(iContext);
	    	
	    	// MainBody
	    	
	    	// Material
	    	Material MainBodyMaterial = new Material();
	    	MainBodyMaterial.SetEmissive(0.0f, 0.4f, 0.0f);
	    				
	    	// Texture
	        Texture TexTankMainBody = new Texture(iContext,R.drawable.ship1);	
			int NumberMainBodyTextures = 1;
			Texture[] MainBodyTexture = new Texture[NumberMainBodyTextures];
			MainBodyTexture[0] = TexTankMainBody;
			boolean AnimateMainBodyTex = false;
			float MainBodyAnimationDelay = 0;
			
			// Mesh
			Mesh MainBodyMesh = new Mesh(8,0,3,5,Pyramid2.Pyramid2Vertices);
			MeshEx MainBodyMeshEx= null;
			
			// Turret
			
			//Material
			Material TurretMaterial=new Material();
	    	TurretMaterial.SetEmissive(0.4f, 0.0f, 0.0f);
	    	
	    	// Texture
	    	Texture TexTankTurret = new Texture(iContext,R.drawable.ship1);	
			int NumberTurretTextures = 1;
			Texture[] TurretTexture = new Texture[NumberTurretTextures];
			TurretTexture[0] = TexTankTurret;		
			boolean AnimateTurretTex = false;
			float TurretAnimationDelay = 0;
			
			// Mesh
			Mesh TurretMesh= new Mesh(8,0,3,5,Pyramid2.Pyramid2Vertices);
			MeshEx TurretMeshEx = null;
			
			// Turret Offset
			Vector3 TurretOffset = new Vector3(0, 0.2f, -0.3f);
			
			// Shaders
			Shader iShader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight);	// ok
			//Shader iLocalAxisShader =new Shader(iContext, R.raw.vslocalaxis, R.raw.fslocalaxis);	  
		      
			// Initilization	
			Vector3 Position = new Vector3(3.0f, 7.0f, 4.0f);
	        Vector3 ScaleTurret = new Vector3(1.5f/2.0f, 0.5f/2.0f, 1.3f/2.0f);
			Vector3 ScaleMainBody = new Vector3(1, 0.5f/2.0f, 1);
	        
			float GroundLevel = 0.0f;
			Vector3 GridColor= new Vector3(0.0f,0.0f,1.0f);
			float MassEffectiveRadius = 7.0f;
			int HitGroundSFX =R.raw.explosion2;
			int ExplosionSFX=R.raw.explosion1;
	    	
	    	
	    
	    	// Create Explosion
	    	int		NumberParticles	= 20; 
	   		Vector3	Color			= new Vector3(0,0,1);
	   		long	ParticleLifeSpan= 3000;
	   		boolean	RandomColors	= false;
	   		boolean	ColorAnimation	= true;
	   		float	FadeDelta		= 0.001f;
	   		Vector3	ParticleSize	= new Vector3(0.5f,0.5f,0.5f);
	   		
	    	SphericalPolygonExplosion Explosion = CreateExplosion(iContext,
					  											  NumberParticles,
					  											  Color,
					  											  ParticleSize,
					  											  ParticleLifeSpan,
					  											  RandomColors,
					  											  ColorAnimation, 
					  											  FadeDelta);
	    
	    	Tank TankType1 = CreateInitTank(iContext,
	    									TankWeapon,
										    MainBodyMaterial,
										    NumberMainBodyTextures,
										    MainBodyTexture,
										    AnimateMainBodyTex,
										    MainBodyAnimationDelay,
										    MainBodyMesh, MainBodyMeshEx,
										    TurretMaterial,
										    NumberTurretTextures,
										    TurretTexture,
										    AnimateTurretTex,
										    TurretAnimationDelay,
										    TurretMesh,TurretMeshEx,
										    TurretOffset, 
										    iShader, //iLocalAxisShader,
										    Explosion,
						
										    Position,
										    ScaleMainBody,
										    ScaleTurret,
										    GroundLevel,
										    GridColor,
										    MassEffectiveRadius,
										    HitGroundSFX, 
										    ExplosionSFX);
	    	
	    	
	    	
	    	return TankType1;
	    }
	    
	
	 int GenerateTankWayPoints2(Vector3[] WayPoints)
	  	{
	  		int NumberWayPoints = 2;
	     
	  		WayPoints[0] = new Vector3( -10, 0, 0); 
	  		WayPoints[1] = new Vector3( -5, 0, 10); 
	  		//WayPoints[2] = new Vector3(-10, 0,-10); 
	  		//WayPoints[3] = new Vector3(-5, 0, 10);  
	  		
	  		return NumberWayPoints;
	  	}
	 
	 
	 void CreateTankFleet(Context iContext)
	    {	
	    	m_TankFleet = new TankFleet(iContext);
			
			Tank TankVehicle = CreateTankType1(iContext);
			
			// Set Material
			TankVehicle.GetMainBody().GetMaterial().SetEmissive(0.0f, 0.5f, 0f);
			TankVehicle.GetTurret().GetMaterial().SetEmissive(0.5f, 0, 0.0f);
		
			// Tank ID
			TankVehicle.SetVehicleID("tank1");
			
			// Set Patrol Order
			int MAX_WAYPOINTS = 10;
			Vector3[] WayPoints = new Vector3[MAX_WAYPOINTS];
			int NumberWayPoints = GenerateTankWayPoints(WayPoints);
			AIVehicleObjectsAffected ObjectsAffected = AIVehicleObjectsAffected.PrimaryWeapon;
			Vector3 Target = new Vector3(0,0,0);
			Object3d TargetObj = null;
			int NumberRoundToFire = 2;
			int FiringDelay = 5000;
			
			VehicleCommand Command = CreatePatrolAttackTankCommand(ObjectsAffected,
																   NumberWayPoints,
																   WayPoints, 	
																   Target,
																   TargetObj,
																   NumberRoundToFire,
																   FiringDelay);
			
			TankVehicle.GetDriver().SetOrder(Command);
			
			boolean result = m_TankFleet.AddNewTankVehicle(TankVehicle);
			
			if (result == false)
			{
				Log.e("RENDERER", "NO, TANK FAILED TO BE ADDED TO FLEET!!!!!!!!!");
			}
			else
			{
				Log.e("RENDERER", "YES, TANK SUCCESSFULLY ADDED TO FLEET!!!!!!!!!");
			}
	    	
			
			/////////////////////////////////////////////////////
			TankVehicle = CreateTankType2(iContext);
			
			// Set Material
			TankVehicle.GetMainBody().GetMaterial().SetEmissive(0, 0.5f, 0.5f);
			TankVehicle.GetTurret().GetMaterial().SetEmissive(0.5f, 0, 0.5f);
			
			// Ship ID
			TankVehicle.SetVehicleID("tank2");
			
			// Set Patrol Order
		
			WayPoints = new Vector3[MAX_WAYPOINTS];
			NumberWayPoints = GenerateTankWayPoints2(WayPoints);

			Target = new Vector3(0,0,0);
			TargetObj = null;
			NumberRoundToFire = 3;
			FiringDelay = 3000;
			
			
			Command = CreatePatrolAttackTankCommand(ObjectsAffected,
													NumberWayPoints,
													WayPoints, 	
													Target,
													TargetObj,
													NumberRoundToFire,
													FiringDelay);
			
			TankVehicle.GetDriver().SetOrder(Command);
			
			result = m_TankFleet.AddNewTankVehicle(TankVehicle);
			
			if (result == false)
			{
				Log.e("RENDERER", "NO, TANK FAILED TO BE ADDED TO FLEET!!!!!!!!!");
			}
			else
			{
				Log.e("RENDERER", "YES, TANK SUCCESSFULLY ADDED TO FLEET!!!!!!!!!");
			}
	   
	    }
	    
	
	
	
	

	 // Create Tank Routes for Controller
   Route CreateTankRoute1()
   {
   	// Around Pyramid
   	Route TankRoute = null;
   	int NumberWayPoints = 4;
   	
   	Vector3[] WayPoints = new Vector3[NumberWayPoints];
		WayPoints[0] = new Vector3(  7, 0, -10); 
		WayPoints[1] = new Vector3( -7, 0, -10); 
		WayPoints[2] = new Vector3( -7, 0, 5); 
		WayPoints[3] = new Vector3(  7, 0, 5);  
		
		TankRoute = new Route(NumberWayPoints, WayPoints);
   	
   	return TankRoute;
   }
   
   Route CreateTankRoute2()
   {
   	// Side to side behind pyramid
   	Route TankRoute = null;
   	int NumberWayPoints = 2;
   	
   	Vector3[] WayPoints = new Vector3[NumberWayPoints];
		WayPoints[0] = new Vector3(  4, 0, -2); 
		WayPoints[1] = new Vector3( -4, 0, -2);  
		
		TankRoute = new Route(NumberWayPoints, WayPoints);
   	
   	return TankRoute;
   }
   
   Route CreateTankRoute3()
   {
   	// Side to Side in front of pyramid
   	Route TankRoute = null;
   	int NumberWayPoints = 2;
   	
   	Vector3[] WayPoints = new Vector3[NumberWayPoints];
		WayPoints[0] = new Vector3(  4, 0, 2); 
		WayPoints[1] = new Vector3( -4, 0, 2);  
		
		TankRoute = new Route(NumberWayPoints, WayPoints);
   	
   	return TankRoute;
   }
   
   Route CreateTankRoute4()
   {
   	// Diagonal Left Side
   	Route TankRoute = null;
   	int NumberWayPoints = 2;
   	
   	Vector3[] WayPoints = new Vector3[NumberWayPoints];
		WayPoints[0] = new Vector3( -4, 0,  2); 
		WayPoints[1] = new Vector3( -10, 0, -2); 
		
		TankRoute = new Route(NumberWayPoints, WayPoints);
   	
   	return TankRoute;
   }
   
   Route CreateTankRoute5()
   {
   	// Diagonal Right Side
   	Route TankRoute = null;
   	int NumberWayPoints = 2;
   	
   	Vector3[] WayPoints = new Vector3[NumberWayPoints];
		WayPoints[0] = new Vector3(  4, 0, -2); 
		WayPoints[1] = new Vector3(  10, 0,  2); 
		
		TankRoute = new Route(NumberWayPoints, WayPoints);
   	
   	return TankRoute;
   }
  
   Route CreateTankRoute6()
   {
   	// Around Pyramid
   	Route TankRoute = null;
   	int NumberWayPoints = 4;
   	
   	Vector3[] WayPoints = new Vector3[NumberWayPoints];
		WayPoints[0] = new Vector3( -7, 0, -10); 
		WayPoints[1] = new Vector3(  7, 0, -10); 
		WayPoints[2] = new Vector3(  7, 0, 5); 
		WayPoints[3] = new Vector3( -7, 0, 5);  
		
		TankRoute = new Route(NumberWayPoints, WayPoints);
   	
   	return TankRoute;
   }
   
   int CreateTankRoutes(Route[] TankRoutes)
   {
   	int NumberRoutes = 6;	
 
   	TankRoutes[0] = CreateTankRoute1();
     	TankRoutes[1] = CreateTankRoute2();
     	TankRoutes[2] = CreateTankRoute3();
     	TankRoutes[3] = CreateTankRoute4();
     	TankRoutes[4] = CreateTankRoute5();
    	TankRoutes[5] = CreateTankRoute6();
   	
     	
   	return NumberRoutes;
   }
   
	
	
	
	
	   // Game Player Controller
		void CreateGamePlayController(Context iContext)
		{
			int MaxNumberRoutes = 10;
			
			// Tanks
			int NumberTankRoutes = 0;
			Route[] TankRoutes = new Route[MaxNumberRoutes];
			NumberTankRoutes = CreateTankRoutes(TankRoutes);
			
			// Air Vehicles
			//int NumberAirVehicleRoutes = 0;
			//Route[] AirVehicleRoutes = new Route[MaxNumberRoutes];
			//NumberAirVehicleRoutes = CreateAirVehicleRoutes(AirVehicleRoutes);
		
			
			m_GamePlayController = new GamePlayController(iContext,
					   									  //m_AirVehicleFleet,
					   									  m_ArenaObjectsSet,
					   									  m_TankFleet,
					   									  m_Grid,
					   									  NumberTankRoutes,
					   									  TankRoutes//,
					   									  //NumberAirVehicleRoutes,
					   									  //AirVehicleRoutes
					   									  );
			
			
		}
	
	
	
	
	
	
	
	
	// High Score Table
    BillBoard CreateInitBillBoard(Context iContext,
    						      int TextureResourceID,
    						      Vector3 Position,
    						      Vector3 Scale)
    {
    	BillBoard NewBillBoard = null;
    	
    	Texture BillBoardTexture = new Texture(iContext, TextureResourceID);
		
		//Create Shader
    	Shader Shader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight);	// ok
    	   
        // Create Debug Local Axis Shader
        //Shader LocalAxisShader = m_LocalAxisShader; // new Shader(iContext, R.raw.vslocalaxis, R.raw.fslocalaxis);	  
        
        //MeshEx(int CoordsPerVertex, 
		//		int MeshVerticesDataPosOffset, 
		//		int MeshVerticesUVOffset , 
		//		int MeshVerticesNormalOffset,
		//		float[] Vertices,
		//		short[] DrawOrder
        MeshEx Mesh = new MeshEx(8,0,3,5,Cube.CubeData, Cube.CubeDrawOrder);
        
        // Create Material for this object
        Material Material1 = new Material();
        //Material1.SetEmissive(1.0f, 1.0f, 1.0f);
        
        // Create Texture for BillBoard
        Texture[] Tex = new Texture[1];
        Tex[0] = BillBoardTexture;
       
        // Create new BillBoard
        NewBillBoard = new BillBoard(iContext, 
									 null, 
									 Mesh, 
									 Tex, 
									 Material1, 
									 Shader//, 
									 //LocalAxisShader
									 );
          
        // Set Intial Position and Orientation 
        NewBillBoard.m_Orientation.SetPosition(Position);
        NewBillBoard.m_Orientation.SetScale(Scale);
        
        NewBillBoard.GetObjectPhysics().SetGravity(false); 	
		
       
    	return NewBillBoard;
    }
    
    void CreateHighScoreTable(Context iContext)
    {
    	int TextureResourceID = R.drawable.background;
	    Vector3 Position = new Vector3(0.5f, 1, 4);
	    Vector3 Scale = new Vector3(4.5f,5,1);
    	
    	BillBoard HighScoreTableImage = CreateInitBillBoard(iContext,
    			 											TextureResourceID,
    			 											Position,
    			 											Scale);
   
    	m_HighScoreTable = new HighScoreTable(iContext,
    										  m_CharacterSet,
    										  HighScoreTableImage);
    	
   
    	/////////////////////////////////////////////////////////////////
    	
    	/*
    	String 	Initials= "rob1";
		int 	Score = 111;
    	HighScoreEntry Entry1 = new HighScoreEntry(Initials,Score);
    	boolean result  = m_HighScoreTable.AddItem(Entry1);
    	
    	Initials = "rob2";
		Score = 222;
    	HighScoreEntry Entry2 = new HighScoreEntry(Initials,Score);
    	result  = m_HighScoreTable.AddItem(Entry2);
    	
    	Initials = "rob3";
		Score = 333;
    	HighScoreEntry Entry3 = new HighScoreEntry(Initials,Score);
    	result  = m_HighScoreTable.AddItem(Entry3);
    
    	Initials = "rob4";
		Score = 444;
    	HighScoreEntry Entry4 = new HighScoreEntry(Initials,Score);
    	result  = m_HighScoreTable.AddItem(Entry4);
    	
    	Initials = "rob5";
		Score = 555;
    	HighScoreEntry Entry5 = new HighScoreEntry(Initials,Score);
    	result  = m_HighScoreTable.AddItem(Entry5);
    	
    	Initials = "rob6";
		Score = 666;
    	HighScoreEntry Entry6 = new HighScoreEntry(Initials,Score);
    	result  = m_HighScoreTable.AddItem(Entry6);
    	
    	Initials = "rob7";
		Score = 777;
    	HighScoreEntry Entry7 = new HighScoreEntry(Initials,Score);
    	result  = m_HighScoreTable.AddItem(Entry7);
    	
    	Initials = "rob8";
		Score = 888;
    	HighScoreEntry Entry8 = new HighScoreEntry(Initials,Score);
    	result  = m_HighScoreTable.AddItem(Entry8);
    	
    	
    	Initials = "rob9";
		Score = 999;
    	HighScoreEntry Entry9 = new HighScoreEntry(Initials,Score);
    	result  = m_HighScoreTable.AddItem(Entry9);
    	
    	
    	Initials = "rob10";
		Score = 1000;
    	HighScoreEntry Entry10 = new HighScoreEntry(Initials,Score);
    	result  = m_HighScoreTable.AddItem(Entry10);
    	*/
    	
    	/*
    	Initials = "rob11";
		Score = 1100;
    	HighScoreEntry Entry11 = new HighScoreEntry(Initials,Score);
    	result  = m_HighScoreTable.AddItem(Entry11);
    	*/
    	
    	/*
    	if (result == false)
    	{
    		Log.e("RENDERER", "ADDITEM ENTRY1 FAILED TO HIGH SCORE TABLE");
    	}
    	*/
    	
    	
    	
    	/////////////////////////////////////////////////////////////////
    	
    	
    }
    
    void CreateHighScoreEntryMenu(Context iContext)
    {
    	
    	// Create High Score Entry Menu Billboard
    	int TextureResourceID = R.drawable.backgroundentrymenu;
	    Vector3 Position = new Vector3(0.0f, 1, 4);
	    Vector3 Scale = new Vector3(4.5f,5,1);
    	
    	BillBoard HighScoreEntryMenuImage = CreateInitBillBoard(iContext,
    			 											    TextureResourceID,
    			 											    Position,
    			 											    Scale);
		
    	
    	
    	// Create Menu Buttons
    	Shader ObjectShader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight);	// ok
        //Shader LocalAxisShader = new Shader(iContext, R.raw.vslocalaxis, R.raw.fslocalaxis);	
            
        MeshEx MenuItemMeshEx = new MeshEx(8,0,3,5,Cube.CubeData, Cube.CubeDrawOrder);
        Mesh MenuItemMesh = null;
        
        // Create Material for this object
        Material Material1 = new Material();
        Material1.SetEmissive(0.3f, 0.3f, 0.3f);
     
        // Create Texture
        int NumberTextures = 1;
        Texture TexNextButton = new Texture(iContext,R.drawable.nextbutton);		
        
        
        Texture[] Tex = new Texture[NumberTextures];
        Tex[0] = TexNextButton;
       
        boolean AnimateTextures = false; 
        float TimeDelay = 0.0f;
        
        Position = new Vector3(-1.0f, 1.3f, 4.25f);
        Scale = new Vector3(1.4f,1.0f,1.0f);
    
		// Next Character Button
		MenuItem NextCharacterButton = CreateMenuItem(iContext,    						
			    						  		  	  MenuItemMesh,
			    						  		  	  MenuItemMeshEx,
			    						  		  	  Material1,
			    						  		  	  NumberTextures,
			    						  		  	  Tex,
			    						  		  	  AnimateTextures,
			    						  		  	  TimeDelay,
			    						  		  	  Position,
			    						  		  	  Scale,
			    						  		  	  ObjectShader//, 
			    						  		  	  //LocalAxisShader
			    						  		  	  );
    		
		// Previous Character Button
    	Position = new Vector3(0.5f, 1.3f, 4.25f);
    	Texture TexPreviousGameButton = new Texture(iContext,R.drawable.previousbutton);	
    	Tex = new Texture[NumberTextures];
        Tex[0] = TexPreviousGameButton;
          
    	MenuItem PreviousCharacterButton = CreateMenuItem(iContext,    						
		  		  									      MenuItemMesh,
		  		  									      MenuItemMeshEx,
		  		  									      Material1,
		  		  									      NumberTextures,
		  		  									      Tex,
		  		  									      AnimateTextures,
		  		  									      TimeDelay,
		  		  									      Position,
		  		  									      Scale,
		  		  									      ObjectShader//, 
		  		  									      //LocalAxisShader
		  		  									      );

    	// Enter Button
    	Position = new Vector3(0.0f, 0.0f, 4.25f);
    	Texture TexEnterButton = new Texture(iContext,R.drawable.enterbutton);	
    	Tex = new Texture[NumberTextures];
        Tex[0] = TexEnterButton;
        Scale = new Vector3(3.0f,1.0f,1.0f);
        
        
    	MenuItem EnterButton = CreateMenuItem(iContext,    						
		  		  							  MenuItemMesh,
		  		  							  MenuItemMeshEx,
		  		  							  Material1,
		  		  							  NumberTextures,
		  		  							  Tex,
		  		  							  AnimateTextures,
		  		  							  TimeDelay,
		  		  							  Position,
		  		  							  Scale,
		  		  							  ObjectShader//, 
		  		  							  //LocalAxisShader
		  		  							  );

    	int StartingEntryXPos = 168;
		int StartingEntryYPos = 100;
		
    	m_HighScoreEntryMenu = new HighScoreEntryMenu(NextCharacterButton,
				   									  PreviousCharacterButton,
				   									  EnterButton,
				   									  m_CharacterSet,
				   									  HighScoreEntryMenuImage,
				   									  StartingEntryXPos,
				   									  StartingEntryYPos);
    	
    }
    
	
	
	 // Menu
    MenuItem CreateMenuItem(Context iContext,    						
    					    Mesh MenuItemMesh,
    					    MeshEx MenuItemMeshEx,
    					    Material Material1,
    					    int NumberTextures,
    					    Texture[] Tex,
    					    boolean AnimateTextures,
    					    float TimeDelay,
    					    Vector3 Position,
    					    Vector3 Scale,
    					    Shader ObjectShader//,
    					    
    					    //Shader LocalAxisShader
    					    )
    {
    	MenuItem NewMenuItem = null;
    
        NewMenuItem = new MenuItem(iContext, 
        						   MenuItemMesh,
        						   MenuItemMeshEx, 
        						   Tex, 
        						   Material1, 
        						   ObjectShader//, 
        						   //LocalAxisShader
        						   );
        NewMenuItem.SetAnimateTextures(AnimateTextures, TimeDelay, 0, NumberTextures-1);
       
        NewMenuItem.m_Orientation.SetPosition(Position);
        NewMenuItem.m_Orientation.SetScale(Scale);
        NewMenuItem.GetObjectPhysics().SetGravity(false);
    
    	return NewMenuItem;
    }
    
	
	  void CreateMainMenu(Context iContext)
	    {
	    	// Create New Game Button
	    	Shader ObjectShader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight);	// ok
	        //Shader LocalAxisShader = new Shader(iContext, R.raw.vslocalaxis, R.raw.fslocalaxis);	
	            
	        MeshEx MenuItemMeshEx = new MeshEx(8,0,3,5,Cube.CubeData, Cube.CubeDrawOrder);
	        Mesh MenuItemMesh = null;
	        
	        // Create Material for this object
	        Material Material1 = new Material();
	     
	        // Create Texture
	        int NumberTextures = 1;
	        Texture TexNewGameButton = new Texture(iContext,R.drawable.newgamebutton);		
	        
	        Texture[] Tex = new Texture[NumberTextures];
	        Tex[0] = TexNewGameButton;
	       
	        boolean AnimateTextures = false; 
	        float TimeDelay = 0.0f;
	        
	        Vector3 Position = new Vector3(0.0f, 2.5f, 4.25f);
	        Vector3 Scale = new Vector3(3.0f,1.0f,1.0f);
	    
	    	MenuItem NewGameMenuItem = CreateMenuItem(iContext,    						
				    						  		  MenuItemMesh,
				    						  		  MenuItemMeshEx,
				    						  		  Material1,
				    						  		  NumberTextures,
				    						  		  Tex,
				    						  		  AnimateTextures,
				    						  		  TimeDelay,
				    						  		  Position,
				    						  		  Scale,
				    						  		  ObjectShader//, 
				    						  		  //LocalAxisShader
				    						  		  );
	    	
	    	
	    	
	    	// Continue Game  
	    	Position = new Vector3(0.0f, 1.3f, 4.25f);
	    	Texture TexContinueGameButton = new Texture(iContext,R.drawable.continuegamebutton);	
	    	Tex = new Texture[NumberTextures];
	        Tex[0] = TexContinueGameButton;
	          
	    	MenuItem ContinueGameMenuItem = CreateMenuItem(iContext,    						
			  		  									   MenuItemMesh,
			  		  									   MenuItemMeshEx,
			  		  									   Material1,
			  		  									   NumberTextures,
			  		  									   Tex,
			  		  									   AnimateTextures,
			  		  									   TimeDelay,
			  		  									   Position,
			  		  									   Scale,
			  		  									   ObjectShader//,
			  		  									   
			  		  									   //LocalAxisShader
			  		  									   );


	    	// View High Scores  
	    	Position = new Vector3(0.0f, 0.0f, 4.25f);
	    	Texture TexHighScoresButton = new Texture(iContext,R.drawable.highscoresbutton);
	    	Tex = new Texture[NumberTextures];
	        Tex[0] = TexHighScoresButton;
	          
	    	MenuItem HighScoreMenuItem = CreateMenuItem(iContext,    						
			  		  									MenuItemMesh,
			  		  									MenuItemMeshEx,
			  		  									Material1,
			  		  									NumberTextures,
			  		  									Tex,
			  		  									AnimateTextures,
			  		  									TimeDelay,
			  		  									Position,
			  		  									Scale,
			  		  									ObjectShader//, 
			  		  									//LocalAxisShader
			  		  									);


	    	
	    	// CopyRight Notice  
	    	Position = new Vector3(0.0f, -1.3f, 4.25f);
	    	Texture TexCopyrightButton = new Texture(iContext,R.drawable.copyright);
	    	Tex = new Texture[NumberTextures];
	        Tex[0] = TexCopyrightButton;
	        Material Material2 = new Material();
	        Material2.SetEmissive(0.3f, 0.3f, 0.3f);
	          
	    	MenuItem CopyrightMenuItem = CreateMenuItem(iContext,    						
			  		  									MenuItemMesh,
			  		  									MenuItemMeshEx,
			  		  									Material2,
			  		  									NumberTextures,
			  		  									Tex,
			  		  									AnimateTextures,
			  		  									TimeDelay,
			  		  									Position,
			  		  									Scale,
			  		  									ObjectShader//, 
			  		  									//LocalAxisShader
			  		  									);

	    	
	   
	    	m_MainMenu = new MainMenu(NewGameMenuItem,
	    							  ContinueGameMenuItem,
	    							  HighScoreMenuItem,
	    							  CopyrightMenuItem);
	        
	    	
	    }
	    
	Weapon CreateWeapon(Context iContext, 
            			Vector3 Color,
            			int SoundResourceID,
            			boolean GravityOn,
            			float GravityValue,
            			float AmmunitionRange,
            			float AmmunitionSpeed,
            			Vector3 ScaleAmmo,
            			int AmmoDamageValue)
	{
		Weapon NewWeapon = null;

		//Create Cube Shader
		Shader Shader = new Shader(iContext, R.raw.vsonelightnotexture, R.raw.fsonelightnotexture);	// ok
   
		// Create Debug Local Axis Shader
		//Shader LocalAxisShader = m_LocalAxisShader; //new Shader(iContext, R.raw.vslocalaxis, R.raw.fslocalaxis);	  

		//MeshEx(int CoordsPerVertex, 
		//		int MeshVerticesDataPosOffset, 
		//		int MeshVerticesUVOffset , 
		//		int MeshVerticesNormalOffset,
		//		float[] Vertices,
		//		short[] DrawOrder
		//MeshEx CubeMesh = new MeshEx(8,0,3,5,Cube.CubeData, Cube.CubeDrawOrder);
		MeshEx CubeMesh = new MeshEx(6,0,-1,3,Cube.CubeDataNoTexture, Cube.CubeDrawOrder);

		// Create Material for this object
		Material Material1 = new Material();
		Material1.SetEmissive(Color.x, Color.y, Color.z);

		// Create Weapon
		NewWeapon = new Weapon(iContext, 
							null, 
							null, 
							null, 
							Material1, 
							Shader//, 
							//LocalAxisShader
							);


		for (int i = 0; i < NewWeapon.GetMaxAmmunition(); i++)
		{
			Ammunition TestAmmo = new Ammunition(iContext, 
							         null, 
							         CubeMesh, 
							         null, 
							         Material1, 
							         Shader, 
							         //LocalAxisShader,
			
							         AmmunitionRange,
							         AmmunitionSpeed);
       
			// Set Intial Position and Orientation
			Vector3 Axis = new Vector3(1,0,1);
			//Vector3 Scale = new Vector3(0.2f,0.2f,0.2f);
   
			TestAmmo.m_Orientation.SetRotationAxis(Axis);
			TestAmmo.m_Orientation.SetScale(ScaleAmmo);
			TestAmmo.GetObjectPhysics().SetGravity(GravityOn);	   
			TestAmmo.GetObjectPhysics().SetGravityLevel(GravityValue);
        
			TestAmmo.SetGridSpotLightColor(Color);
   
			// SFX
			TestAmmo.CreateFiringSFX(m_SoundPool, SoundResourceID);
			TestAmmo.SetSFXOnOff(true);
			
			// Damage Amount
			TestAmmo.GetObjectStats().SetDamageValue(AmmoDamageValue);

			NewWeapon.LoadAmmunition(TestAmmo, i);
		}

		return NewWeapon;
	}
	
	  Weapon CreateTankWeaponType1(Context iContext)
		{
			Weapon TankWeapon = null;
			
			Vector3 AmmoColor = new Vector3(1,1,0);
	        int AmmoSoundResourceID= R.raw.clustoid; //R.raw.weapon2; 
	        boolean GravityOn= false;
	        float GravityValue=0;
	         
	        float AmmunitionRange = 100;
	        float AmmunitionSpeed = 0.5f;
	        
	        Vector3 ScaleAmmo = new Vector3(0.2f,0.2f,0.2f);
		
	        int AmmoDamageValue = 2;
	        
			TankWeapon = CreateWeapon(iContext, 
		            				  AmmoColor,
		            				  AmmoSoundResourceID,
		            				  GravityOn,
		            				  GravityValue,
		            				  AmmunitionRange,
		            				  AmmunitionSpeed,
		            				  ScaleAmmo, 
		            				  AmmoDamageValue);
		
			return TankWeapon;
		}
	
	  
	  
	   Tank CreateTank(Context iContext,
				Weapon TankWeapon,
				Material MainBodyMaterial,
				int NumberMainBodyTextures,
				Texture[] MainBodyTexture,
				boolean AnimateMainBodyTex,
				float MainBodyAnimationDelay,
				Mesh MainBodyMesh, MeshEx MainBodyMeshEx,
				Material TurretMaterial,
				int NumberTurretTextures,
				Texture[] TurretTexture,
				boolean AnimateTurretTex,
				float TurretAnimationDelay,
				Mesh TurretMesh, MeshEx TurretMeshEx,
				Vector3 TurretOffset, 
				Shader iShader, 
				//Shader iLocalAxisShader,
				SphericalPolygonExplosion Explosion)
	   {
		   Tank NewTank = null;


		   // Create Main Tank body
		   Object3d TankMainBody = new Object3d(iContext, 
   									 MainBodyMesh, 
   									 MainBodyMeshEx, 
   									 MainBodyTexture, 
   									 MainBodyMaterial, 
   					    			 iShader//, 
   					    			 //iLocalAxisShader
   					    			 );   
		   TankMainBody.SetAnimateTextures(AnimateMainBodyTex, MainBodyAnimationDelay, 0, NumberMainBodyTextures-1);
 
   

		   // Create Tank Turret
		   Object3d TankTurret = new Object3d(iContext, 
				   							TurretMesh, 
				   							TurretMeshEx, 
				   							TurretTexture, 
				   							TurretMaterial, 
				   							iShader//, 
				   							//iLocalAxisShader
			 							   	);   
		   TankTurret.SetAnimateTextures(AnimateTurretTex, TurretAnimationDelay, 0, NumberTurretTextures-1);
   
   
		   // Create new Tank
		   NewTank = new Tank(TankMainBody, TankTurret, TurretOffset);

		   // Add Weapon to Tank
		   NewTank.AddWeapon(TankWeapon);
   
		   // Add Explosion to Tank
		   NewTank.GetMainBody().AddExplosion(Explosion); 

		   return NewTank;
	   }
	  
	  
	  
	  
	  
	  
	  
	  Tank CreateInitTank(Context iContext,
				Weapon TankWeapon,
				Material MainBodyMaterial,
				int NumberMainBodyTextures,
				Texture[] MainBodyTexture,
				boolean AnimateMainBodyTex,
				float MainBodyAnimationDelay,
				Mesh MainBodyMesh, MeshEx MainBodyMeshEx,
				Material TurretMaterial,
				int NumberTurretTextures,
				Texture[] TurretTexture,
				boolean AnimateTurretTex,
				float TurretAnimationDelay,
				Mesh TurretMesh, MeshEx TurretMeshEx,
				Vector3 TurretOffset, 
				Shader iShader, 
				//Shader iLocalAxisShader,
				SphericalPolygonExplosion Explosion,
				
				Vector3 Position,
				Vector3 ScaleMainBody,
				Vector3 ScaleTurret,
				float GroundLevel,
				Vector3 GridColor,
				float MassEffectiveRadius,
				int HitGroundSFX, 
				int ExplosionSFX)
{
		  Tank NewTank = null;

		  // Create new Tank
		  NewTank = CreateTank(iContext,
				  			TankWeapon,
				  			MainBodyMaterial,
				  			NumberMainBodyTextures,
				  			MainBodyTexture,
				  			AnimateMainBodyTex,
				  			MainBodyAnimationDelay,
				  			MainBodyMesh, MainBodyMeshEx,
				  			TurretMaterial,
				  			NumberTurretTextures,
				  			TurretTexture,
				  			AnimateTurretTex,
				  			TurretAnimationDelay,
				  			TurretMesh, TurretMeshEx,
				  			TurretOffset, 
				  			iShader, 
				  			//iLocalAxisShader,
				  			Explosion);

		  // Initialize Tank

		  // Set Initial Position and Orientation
		  Vector3 Axis  = new Vector3(0,1,0);
		  NewTank.GetMainBody().m_Orientation.SetPosition(Position);
		  NewTank.GetMainBody().m_Orientation.SetRotationAxis(Axis);
		  NewTank.GetMainBody().m_Orientation.SetScale(ScaleMainBody);

		  // Initialize Tank Turret
		  NewTank.GetTurret().m_Orientation.SetScale(ScaleTurret);

		  // Physics
		  NewTank.GetMainBody().GetObjectPhysics().SetGravity(true);
		  NewTank.GetMainBody().GetObjectPhysics().SetGroundLevel(GroundLevel);

		  // Grid
		  NewTank.GetMainBody().SetGridSpotLightColor(GridColor);
		  NewTank.GetMainBody().GetObjectPhysics().SetMassEffectiveRadius(MassEffectiveRadius);

		  //SFX
		  NewTank.CreateHitGroundSFX(m_SoundPool, HitGroundSFX);
		  NewTank.CreateExplosionSFX(m_SoundPool, ExplosionSFX);


		  return NewTank;
	}

	  
	
	 
	  
	 Tank CreateTankType1(Context iContext)
	    {
	    	//Weapon
	    	Weapon TankWeapon = CreateTankWeaponType1(iContext);
	    	
	    	// MainBody
	    	
	    	// Material
	    	Material MainBodyMaterial = new Material();
	    	MainBodyMaterial.SetEmissive(0.0f, 0.4f, 0.0f);
	    	//MainBodyMaterial.SetGlowAnimation(true);
	    	//MainBodyMaterial.GetEmissiveMax().Set(0, 0.5f, 0);
	    	//MainBodyMaterial.GetEmissiveMin().Set(0, 0, 0);
	    	
	    	
	    	// Texture
	        Texture TexTankMainBody = new Texture(iContext,R.drawable.ship1);	
			int NumberMainBodyTextures = 1;
			Texture[] MainBodyTexture = new Texture[NumberMainBodyTextures];
			MainBodyTexture[0] = TexTankMainBody;
			boolean AnimateMainBodyTex = false;
			float MainBodyAnimationDelay = 0;
			
			// Mesh
			Mesh MainBodyMesh = new Mesh(8,0,3,5,Pyramid2.Pyramid2Vertices);
			MeshEx MainBodyMeshEx= null;
			
			// Turret
			
			//Material
			Material TurretMaterial=new Material();
	    	TurretMaterial.SetEmissive(0.4f, 0.0f, 0.0f);
	    	//TurretMaterial.SetGlowAnimation(true);
	    	//TurretMaterial.GetEmissiveMax().Set(0.5f, 0.0f, 0);
	    	///TurretMaterial.GetEmissiveMin().Set(0, 0, 0);
	    	
	    	
	    	// Texture
	    	Texture TexTankTurret = new Texture(iContext,R.drawable.ship1);	
			int NumberTurretTextures = 1;
			Texture[] TurretTexture = new Texture[NumberTurretTextures];
			TurretTexture[0] = TexTankTurret;		
			boolean AnimateTurretTex = false;
			float TurretAnimationDelay = 0;
			
			// Mesh
			Mesh TurretMesh= new Mesh(8,0,3,5,Pyramid2.Pyramid2Vertices);
			MeshEx TurretMeshEx = null;
			
			// Turret Offset
			Vector3 TurretOffset = new Vector3(0, 0.2f, -0.3f);
			
			// Shaders
			Shader iShader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight);	// ok
			//Shader iLocalAxisShader = new Shader(iContext, R.raw.vslocalaxis, R.raw.fslocalaxis);	  
		      
			// Initilization	
			Vector3 Position = new Vector3(-2.0f, 7.0f, 2.0f);
	        Vector3 ScaleTurret = new Vector3(1.5f/2.0f, 0.5f/2.0f, 1.3f/2.0f);
			Vector3 ScaleMainBody = new Vector3(1, 0.5f/2.0f, 1);
	        
			float GroundLevel = 0.0f;
			Vector3 GridColor= new Vector3(0.0f,1.0f,0.0f);
			float MassEffectiveRadius = 7.0f;
			int HitGroundSFX =R.raw.explosion2;
			int ExplosionSFX=R.raw.explosion1;
	    	
	    	
	    
	    	// Create Explosion
	    	int		NumberParticles	= 20; 
	   		Vector3	Color			= new Vector3(0,0,1);
	   		long	ParticleLifeSpan= 3000;
	   		boolean	RandomColors	= false;
	   		boolean	ColorAnimation	= true;
	   		float	FadeDelta		= 0.001f;
	   		Vector3	ParticleSize	= new Vector3(0.5f,0.5f,0.5f);
	   		
	    	SphericalPolygonExplosion Explosion = CreateExplosion(iContext,
					  											  NumberParticles,
					  											  Color,
					  											  ParticleSize,
					  											  ParticleLifeSpan,
					  											  RandomColors,
					  											  ColorAnimation, 
					  											  FadeDelta);
	    
	    	Tank TankType1 = CreateInitTank(iContext,
	    									TankWeapon,
										    MainBodyMaterial,
										    NumberMainBodyTextures,
										    MainBodyTexture,
										    AnimateMainBodyTex,
										    MainBodyAnimationDelay,
										    MainBodyMesh, MainBodyMeshEx,
										    TurretMaterial,
										    NumberTurretTextures,
										    TurretTexture,
										    AnimateTurretTex,
										    TurretAnimationDelay,
										    TurretMesh,TurretMeshEx,
										    TurretOffset, 
										    iShader, 
										    //iLocalAxisShader,
										    Explosion,
						
										    Position,
										    ScaleMainBody,
										    ScaleTurret,
										    GroundLevel,
										    GridColor,
										    MassEffectiveRadius,
										    HitGroundSFX, 
										    ExplosionSFX);
	    	
	    	
	    	
	    	TankType1.GetMainBody().SetSFXOnOff(true);
			TankType1.GetTurret().SetSFXOnOff(true);
	    	
	    	return TankType1;
	    }
	   
	
	 
	 int GenerateTankWayPoints(Vector3[] WayPoints)
		{
			int NumberWayPoints = 4;
	   
			WayPoints[0] = new Vector3( 5, 0, 10); 
			WayPoints[1] = new Vector3( 10, 0,-10); 
			WayPoints[2] = new Vector3(-10, 0,-10); 
			WayPoints[3] = new Vector3(-5, 0, 10);  
			
			return NumberWayPoints;
		}
	 
	 
	 // Tank
	    VehicleCommand CreatePatrolAttackTankCommand(AIVehicleObjectsAffected ObjectsAffected,
	    											 int NumberWayPoints,
	    											 Vector3[] WayPoints, 	
	    											 Vector3 Target,
	    											 Object3d TargetObj,
	    											 int NumberRoundToFire,
	    											 int FiringDelay)
	    {
	    	VehicleCommand TankCommand = null;
	    	
	    	AIVehicleCommand Command = AIVehicleCommand.Patrol;
	   
			int	NumberObjectsAffected = 0;	
			int	DeltaAmount = NumberRoundToFire;
			int	DeltaIncrement = FiringDelay;
			
			int	MaxValue = 0;
			int	MinValue = 0;
			
	    	TankCommand = new VehicleCommand(m_Context,
	    									 Command,
					   						 ObjectsAffected,
					   						 NumberObjectsAffected,  
					   						 DeltaAmount,
					   						 DeltaIncrement,
					   						 MaxValue,
					   						 MinValue,
					   						 NumberWayPoints,
					   						 WayPoints,
					   						 Target,
					   						 TargetObj
					   						 );
	    	
	    	return TankCommand;
	    }
	    
	 

	 /*
	 void CreateTanks()
	 {
		 
		 	m_Tank= CreateTankType1(m_Context);
			
	
		 	
			// Set Material
			m_Tank.GetMainBody().GetMaterial().SetEmissive(0.0f, 0.5f, 0f);
			m_Tank.GetTurret().GetMaterial().SetEmissive(0.5f, 0, 0.0f);
		
				
			// Tank ID
			m_Tank.SetVehicleID("tank1");
			
			// Set Patrol Order
			int MAX_WAYPOINTS = 10;
			Vector3[] WayPoints = new Vector3[MAX_WAYPOINTS];
			int NumberWayPoints = GenerateTankWayPoints(WayPoints);
			AIVehicleObjectsAffected ObjectsAffected = AIVehicleObjectsAffected.PrimaryWeapon;
			Vector3 Target = new Vector3(0,0,0);
			Object3d TargetObj = null;
			int NumberRoundToFire = 3;
			int FiringDelay = 5000;
			
			VehicleCommand Command = CreatePatrolAttackTankCommand(ObjectsAffected,
																   NumberWayPoints,
																   WayPoints, 	
																   Target,
																   TargetObj,
																   NumberRoundToFire,
																   FiringDelay);
			
			m_Tank.GetDriver().SetOrder(Command);
		 
		 
		 
	 }
	*/
	    
	
	 public void PyramidCreateTexture(Context context)
	 {   
		 m_TexPyramid1 = new Texture(context,R.drawable.pyramid1);		
		 m_TexPyramid2 = new Texture(context,R.drawable.pyramid2);			
	 }
	    
	
	// Create Player's Pyramid
	  void CreatePyramid(Context iContext)
	    {
	    	//Create Cube Shader
	    	Shader Shader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight);	// ok
	    	   
	        // Create Debug Local Axis Shader
	        //Shader LocalAxisShader = m_LocalAxisShader; // new Shader(iContext, R.raw.vslocalaxis, R.raw.fslocalaxis);	
	          	
	    	//Mesh(int CoordsPerVertex, 
	    	//		int MeshVerticesDataPosOffset, 
	    	//		int MeshVerticesUVOffset , 
	    	//		int MeshVerticesNormalOffset,
	    	//		float[] Vertices)      
	    	Mesh PyramidMesh = new Mesh(8,0,3,5,Pyramid.PyramidVertices);
	    	
	    	// Create Material for this object
	        Material Material1 = new Material();
	        Material1.SetEmissive(0.0f, 0.0f, 0.5f);
	        
	        Material1.SetGlowAnimation(true);
	        Material1.GetEmissiveMax().Set(0.45f, 0.45f, 0.25f);
	        Material1.GetEmissiveMin().Set(0, 0, 0);
	        
	        // Create Texture
	        PyramidCreateTexture(iContext);
	        Texture[] PyramidTex = new Texture[2];
	        PyramidTex[0] = m_TexPyramid1;
	        PyramidTex[1] = m_TexPyramid2;
	           
	        //Context iContext, 
			// Mesh iMesh, 
			// MeshEx iMeshEx, 
			// Texture[] iTextures, 
			// Material iMaterial, 
			// Shader iShader, 
			// Shader LocalAxisShader
	        m_Pyramid = new PowerPyramid(iContext, 
	        					    	 PyramidMesh, 
	        					    	 null, 
	        					    	 PyramidTex, 
	        					    	 Material1, 
	        					    	 Shader//, 
	        					    	 //LocalAxisShader
	        					    	 );   
	       
	        /*
	   		int MeshResourceID = 
	   		int NumberTextures=
	   		int[] TextureResourceID= 
	   		Material iMaterial= 
	   		int VertexShaderResourceID= 
	   		int FragmentShaderResourceID= 
	   		int LocalAxisVertexShaderResourceID=  
	   	    int LocalAxisFragmentShaderResourceID=
	        
	        m_Pyramid = new Pyramid(iContext, 
	   			 MeshResourceID,
	   			 NumberTextures,
	   			 TextureResourceID, 
	   			 iMaterial, 
	   			 VertexShaderResourceID, FragmentShaderResourceID, 
	   			 LocalAxisVertexShaderResourceID, LocalAxisFragmentShaderResourceID);
	        
	        */
	      
	        m_Pyramid.SetAnimateTextures(true, 0.3f, 0, 1);
	         
	        // Set Initial Position and Orientation
	        Vector3 Axis  = new Vector3(0,1,0);
	        //Vector3 Position = new Vector3(1.5f, -1.0f, -10.0f);
	        Vector3 Position = new Vector3(0.0f, 0.0f, 0.0f);
	        Vector3 Scale = new Vector3(0.25f,0.30f,0.25f);
	        
	        m_Pyramid.m_Orientation.SetPosition(Position);
	        m_Pyramid.m_Orientation.SetRotationAxis(Axis);
	        m_Pyramid.m_Orientation.SetScale(Scale);
	        m_Pyramid.m_Orientation.AddRotation(45);
	        
	        
	        m_Pyramid.GetObjectPhysics().SetGravity(false);
	        
	        Vector3 ColorGrid = new Vector3(1.0f, 0.0f, 0.5f);
	        m_Pyramid.SetGridSpotLightColor(ColorGrid);
	        m_Pyramid.GetObjectPhysics().SetMassEffectiveRadius(7);
	        
	        m_Pyramid.GetObjectPhysics().SetMass(2000);
	        
	        //SFX
	        m_Pyramid.CreateExplosionSFX(m_SoundPool, R.raw.explosion2);
	        m_Pyramid.SetSFXOnOff(true);
	        
	        //m_PyramidParticleEmitter = m_Pyramid.AddPolyParticleEmitter(m_PolyEmitter2);
	        //Log.d("DEBUG - m_PYRAMID RADIUS = ", "PYRAMID RADIUS = " + m_Pyramid.GetRadius());
	        
	     
	        // Create Explosion
	        int		NumberParticles	= 20; 
	 		Vector3	Color			= new Vector3(1,1,0);
	 		long	ParticleLifeSpan= 2000;
	 		boolean	RandomColors	= false;
	 		boolean	ColorAnimation	= true;
	 		float	FadeDelta		= 0.01f;
	 		Vector3	ParticleSize	= new Vector3(0.5f,0.5f,0.5f);
	 		   	
	    	//Mesh(int CoordsPerVertex, 
	    	//		int MeshVerticesDataPosOffset, 
	    	//		int MeshVerticesUVOffset , 
	    	//		int MeshVerticesNormalOffset,
	    	//		float[] Vertices)      
	    	//Mesh PolyParticleMesh = new Mesh(8,0,3,5,PolyParticleEx.PolyParticleVertices);
	        // No textures
	        Mesh PolyParticleMesh = new Mesh(6,0,-1,3,PolyParticleEx.PolyParticleVertices);
	    	
	    	
	    	// Create Material for this object
	        Material Material2 = new Material();
	        //Material2.SetEmissive(0.3f, 0.0f, 0.0f);
	        Material2.SetSpecular(0, 0, 0);
	        //Material2.SetDiffuse(1, 1, 0);
	        //Material2.SetAlpha(0.5f);
	 		
	        //Create Cube Shader
	        Shader Shader2 = new Shader(iContext, R.raw.vsonelightnotexture, R.raw.fsonelightnotexture);	// ok  
	        
	        SphericalPolygonExplosion explosion = new SphericalPolygonExplosion(NumberParticles, 
	        																    Color,
	        																    ParticleLifeSpan,
	        																    RandomColors, 
	        																    ColorAnimation,
	        																    FadeDelta,
	        																    ParticleSize,
	        																    
	        																    m_Context, 
	        														 			PolyParticleMesh, 
	        														 			null, 
	        														 			null, 
	        														 			Material2, 
	        														 			Shader2//, 
	        														 			//LocalAxisShader
	        														 			);
	       m_Pyramid.AddExplosion(explosion);    
	    }
	
	  /*
	  void ProcessCollsionsArenaObjects()
	  {
			float ExplosionMinVelocity = 0.02f;
	    	float ExplosionMaxVelocity = 0.4f;
		
	    	if (!m_Cube.IsVisible())
	    	{
	    		return;
	    	}
	    	
	    	// Check Collisons between Cube and Player's Ammunition
	    	Object3d CollisionObj = m_Weapon.CheckAmmoCollision(m_Cube);
	    	if (CollisionObj != null)
	    	{
	    		CollisionObj.ApplyLinearImpulse(m_Cube);
	    		
	    		m_Cube.ExplodeObject(ExplosionMaxVelocity, ExplosionMinVelocity);
	    		m_Cube.PlayExplosionSFX();
	    		
	    		// Process Damage
	    		m_Cube.TakeDamage(CollisionObj);
	    		int Health = m_Cube.GetObjectStats().GetHealth();
	    		if (Health <= 0)
	    		{
	    			int KillValue = m_Cube.GetObjectStats().GetKillValue();
	    			m_Score = m_Score + KillValue;
	    			
	    			m_Cube.SetVisibility(false);
	    		}
	    	}
	    		
	    	//Check Collision between Pyramid and Cube
			Physics.CollisionStatus result = m_Pyramid.CheckCollision(m_Cube);
			if ((result == Physics.CollisionStatus.COLLISION) ||
				(result == Physics.CollisionStatus.PENETRATING_COLLISION))
			{
				m_Pyramid.ExplodeObject(ExplosionMaxVelocity, ExplosionMinVelocity);	
				m_Pyramid.PlayExplosionSFX();			
				m_Pyramid.ApplyLinearImpulse(m_Cube);
				
				// Set Pyramid Velocity and Acceleration to 0
				m_Pyramid.GetObjectPhysics().ResetState();
				
				m_Pyramid.TakeDamage(m_Cube);
			}
		  
	  }
	   */
	  
	  /*
	  void ProcessTankCollisions()
	  {
			float ExplosionMinVelocity = 0.02f;
	    	float ExplosionMaxVelocity = 0.4f;
		
	    	if (!m_Tank.GetMainBody().IsVisible())
	    	{
	    		return;
	    	}
	    	
	    	
	    	
	    	// Check Collisons between Tank and Player's Ammunition
	    	Object3d CollisionObj = m_Weapon.CheckAmmoCollision(m_Tank.GetMainBody());
	    	if (CollisionObj != null)
	    	{
	    		CollisionObj.ApplyLinearImpulse(m_Tank.GetMainBody());
	    		
	    		m_Tank.GetMainBody().ExplodeObject(ExplosionMaxVelocity, ExplosionMinVelocity);
	    		m_Tank.PlayExplosionSFX();
	    		
	    		// Process Damage
	    		m_Tank.GetMainBody().TakeDamage(CollisionObj);
	    		int Health = m_Tank.GetMainBody().GetObjectStats().GetHealth();
	    		if (Health <= 0)
	    		{
	    			int KillValue = m_Tank.GetMainBody().GetObjectStats().GetKillValue();
	    			m_Score = m_Score + KillValue;
	    			
	    			m_Tank.GetMainBody().SetVisibility(false);
	    			m_Tank.GetTurret().SetVisibility(false);
	    		}
	    	}
	    	
	    	
	    	// Tank Weapons and Pyramid
	    	int NumberWeapons = m_Tank.GetNumberWeapons();
			
			for (int j=0; j < NumberWeapons; j++)
			{
				CollisionObj = m_Tank.GetWeapon(j).CheckAmmoCollision(m_Pyramid);
				if (CollisionObj != null)
				{
					//hitresult = true;
					CollisionObj.ApplyLinearImpulse(m_Pyramid);
	    		
					//Process Damage
					m_Pyramid.TakeDamage(CollisionObj);
					
					// Obj Explosion
					m_Pyramid.ExplodeObject(ExplosionMaxVelocity, ExplosionMinVelocity);
					m_Pyramid.PlayExplosionSFX();
					
					// Set Pyramid Velocity and Acceleration to 0
					m_Pyramid.GetObjectPhysics().ResetState();
				}
			}
		  
	  }
	  */
	  
	  
	  
	  void ProcessTankFleetCollisions()
	  { 
				
			// Tank Fleet Collision
	    	m_Score = m_Score + m_TankFleet.ProcessCollisionsWeapon(m_Weapon);
	    	
	    	// Tank Ammo Collisions with Pyramid
	    	boolean HitResult = m_TankFleet.ProcessWeaponAmmoCollisionObject(m_Pyramid);
	    	if (HitResult == true)
	    	{
	    		m_Pyramid.PlayExplosionSFX();
	    	}
	    	
	    	// Reset Pyramid Physics 
	    	m_Pyramid.GetObjectPhysics().GetLinearAcceleration().Set(0, 0, 0);
	    	
	    
	  }
	  
	  void ProcessArenaObjectSetCollisions()
	  {
		  // Arena Objects
		  m_Score = m_Score + m_ArenaObjectsSet.ProcessCollisionsWeapon(m_Weapon);
	    
		  // Process Arena Object collisions with Center Power Pyramid
		  m_Score = m_Score + m_ArenaObjectsSet.ProcessCollisionWithObject(m_Pyramid);
	    	
		  // Reset Pyramid Physics 
		  m_Pyramid.GetObjectPhysics().GetLinearAcceleration().Set(0, 0, 0);
	  }
	  
	  
	// Creating the Player's Weapon and Ammo
	void ProcessCollisions()
	{
		ProcessArenaObjectSetCollisions();
		ProcessTankFleetCollisions();
	}
	
	
	 void CreateWeapon(Context iContext)
	    {
	    	//Create Cube Shader
	    	Shader Shader = new Shader(iContext, R.raw.vsonelightnotexture, R.raw.fsonelightnotexture);	// ok
	    	   
	        // Create Debug Local Axis Shader
	        //Shader LocalAxisShader = m_LocalAxisShader; //new Shader(iContext, R.raw.vslocalaxis, R.raw.fslocalaxis);	
	        
	        // Create 
	        //Orientation Orientation = new Orientation(iContext,LocalAxisShader);   
	        
	        //MeshEx(int CoordsPerVertex, 
			//		int MeshVerticesDataPosOffset, 
			//		int MeshVerticesUVOffset , 
			//		int MeshVerticesNormalOffset,
			//		float[] Vertices,
			//		short[] DrawOrder
	        //MeshEx CubeMesh = new MeshEx(8,0,3,5,Cube.CubeData, Cube.CubeDrawOrder);
	        MeshEx CubeMesh = new MeshEx(6,0,-1,3,Cube.CubeDataNoTexture, Cube.CubeDrawOrder);
	        
	        
	        // Create Material for this object
	        Material Material1 = new Material();
	        Material1.SetEmissive(0.0f, 1.0f, 0.0f);
	        //Material1.SetAlpha(0.5f);
	    
	       
	        // Create Texture
	        //CubeCreateTexture(iContext);
	        
	        //Texture[] CubeTex = new Texture[2];
	        //CubeTex[0] = m_TexRedAndroid;
	        //CubeTex[1] = m_TexGreenAndroid;
	        
	        
	        // Create Weapon
	        m_Weapon = new Weapon(iContext, 
	 							null, 
	 							null, 
	 							null, 
	 							Material1, 
	 							Shader//, 
	 							//LocalAxisShader
	 							);
	        
	       float AmmunitionRange = 100;
	       float AmmunitionSpeed = 0.5f;
	   
	       
	       for (int i = 0; i < m_Weapon.GetMaxAmmunition(); i++)
	  	   {
	    	   Ammunition Ammo = new Ammunition(iContext, 
	        							null, 
	        							CubeMesh, 
	        							null, //CubeTex, 
	        							Material1, 
	        							Shader, 
	        							//LocalAxisShader,
	        			
	        							AmmunitionRange,
	        							AmmunitionSpeed);
	        
	    	   //m_TestAmmo.SetAnimateTextures(true, 0.3f, 0, 1);
	        
	    	   // Set Intial Position and Orientation
	    	   Vector3 Axis = new Vector3(1,0,1);
	    	   Vector3 Scale = new Vector3(0.3f,0.3f,0.3f);
	        
	    	   Ammo.m_Orientation.SetRotationAxis(Axis);
	    	   Ammo.m_Orientation.SetScale(Scale);
	        
	    	   Ammo.GetObjectPhysics().SetGravity(false);	   
	    	   Ammo.GetObjectPhysics().SetGravityLevel(0.003f);
	    	   
	    	   Vector3 GridColor = new Vector3(1,0f,0);
	    	   Ammo.SetGridSpotLightColor(GridColor);
	    	   Ammo.GetObjectPhysics().SetMassEffectiveRadius(10);
	    	   Ammo.GetObjectPhysics().SetMass(100);
	    	   
	    	   Ammo.GetObjectStats().SetDamageValue(25);
	    	   
	    	   
	    	   
	    	   /*
	    	   
	    	   // Create Particle Emitter.
	    	   Mesh iMesh = new Mesh(6,0,-1,3,PolyParticleEx.PolyParticleVertices); 
	           MeshEx iMeshEx = null; 
	           Texture[] iTextures = null; 
	           Material iMaterial = new Material(); 
	           Shader iShader = new Shader(iContext, R.raw.vsonelightnotexture, R.raw.fsonelightnotexture); 

	           float 	EmissionRadius = 5;
	           int		NumberParticles = 500;
	           float 	ActiveDistance = 200;
	           Vector3 	ColorParticles = new Vector3(0,1,0);
	           float 	ParticleLifeSpan = 1000;
	      	
	           Vector3 RotationAxis = new Vector3(1,1,1);
	           Vector3  Scale2 = new Vector3(0.1f,0.1f,0.1f);
	           float MaxAngularVel = 4 * Physics.PI;
	           float Gravity = 0.00020f;
	           float GroundLevel = -4;
	           float FadeDelta = 0.02f;
	           
	           iMaterial.SetSpecular(0, 0, 0);
	           iMaterial.SetAlpha(0.5f);
	       
	           ParticlePolyEmitter PolyEmitter = new ParticlePolyEmitter(iContext, // PolyParticleEx Info
	          		 								iMesh, 
	          		 								iMeshEx, 
	          		 								iTextures, 
	          		 								iMaterial, 
	          		 								iShader, 
	          		 								LocalAxisShader, 

	          		 								NumberParticles,
	          		 								ActiveDistance,
	          		 								ColorParticles,
	          		 								ParticleLifeSpan,
	          		 								
	          		 							    RotationAxis,
	          		 								Scale2,
	          		 								MaxAngularVel,
	          		 								Gravity,
	          		 								GroundLevel,
	          		 								FadeDelta);
	         
	    	   m_TestAmmo.AddPolyParticleEmitter(PolyEmitter);
	    	  */
	    	   
	    	   m_Weapon.LoadAmmunition(Ammo, i);
	  	   } 
	    }
	    
	
	
	
	// Creating the Player's Camera View
	
	 void ProcessTouch(float Startx, float Starty, float x, float y)
	 {
	    	// If app is not initialized then do not process screen touches.
	    	//if (m_AppInitialized == false)
	    //	{
	    	//	return;
	    	//}
	    	
	    	Vector3 DiffVec = new Vector3(Startx - x, Starty - y, 0);
	    	
	    	float length = DiffVec.Length();
	    	
	    	if (length < 10)
	    	{
	    		// Player weapon has been fired
	    		m_ScreenTouched = true;
	    		m_TouchX = x;
	    		m_TouchY = y;
	    		//m_TouchRadius = Radius;
	    	}
	}
	    
	    void CameraMoved(float DeltaXAxisRotation , float DeltaYAxisRotation)
	    {
	    	m_CameraMoved = true;
	    	
	    	float ScaleFactor = 3;
	    	m_DeltaXAxisRotation = DeltaXAxisRotation/ScaleFactor;
	    	m_DeltaYAxisRotation = DeltaYAxisRotation/ScaleFactor;
	    }
	    
	    void ProcessCameraMove()
	    {
	    	if ((m_GameState == GameState.MainMenu) || 
	    	    (m_GameState == GameState.HighScoreTable)||
	    	    (m_GameState == GameState.HighScoreEntry))
	    	{
	    		return;
	    	}
	    	
	    	Vector3 Axis = new Vector3(0,1,0);
	    
	    	// Test Limits
	    	float CameraRotation = m_Camera.GetOrientation().GetRotationAngle();
	    	float NextRotationAngle = CameraRotation + m_DeltaYAxisRotation;
	    	if (NextRotationAngle > m_MaxCameraAngle)
	    	{
	    		m_DeltaYAxisRotation = m_MaxCameraAngle - CameraRotation;
	    	}
	    	else
	    	if (NextRotationAngle < m_MinCameraAngle)
	    	{
	    		m_DeltaYAxisRotation = m_MinCameraAngle - CameraRotation;
	    	}
	    	
	        // Camera Test
	    	// Rotate Camera Around Y Axis
	        m_Camera.GetOrientation().SetRotationAxis(Axis);
	        m_Camera.GetOrientation().AddRotation(m_DeltaYAxisRotation);
	               
	    	m_CameraMoved = false;
	    }
	   
	

	 float[] MapWindowCoordsToWorldCoords(int[] View, float WinX, float WinY, float WinZ)
	 {
    	 // Set modelview matrix to just camera view to get world coordinates
    	
		 // Map window coordinates to object coordinates. gluUnProject maps the specified 
		 // window coordinates into object coordinates using model, proj, and view. The result is 
		 // stored in obj.
		 // view	the current view, {x, y, width, height}
		 float[] ObjectCoords = new float[4];
		 float realy = View[3] - WinY;
		 int result = 0;
				 	 
		 //public static int gluUnProject (float winX, float winY, float winZ, 
		 //								   float[] model, int modelOffset, 
		 //								   float[] project, int projectOffset, 
		 //								   int[] view, int viewOffset, 
		 //								   float[] obj, int objOffset)
		 result = GLU.gluUnProject (WinX, realy, WinZ, 
				 					m_Camera.GetViewMatrix(), 0, 
				 					m_Camera.GetProjectionMatrix(), 0, 
				 					View, 0, 
				 					ObjectCoords, 0);
		 
		 if (result == GLES20.GL_FALSE)
		 {
			 Log.e("class Object3d :", "ERROR = GLU.gluUnProject failed!!!");
			 Log.e("View = ", View[0] + "," + View[1] + ", " + View[2] + ", " + View[3]);
		 }
		 else
		 {
			 //Log.d("DEBUG - In MapWindowCoordsToObjectCoords-> GLUnUPROJECT", "World Coords(x,y,z,w) = " + 
			 //		 ObjectCoords[0] + "," + ObjectCoords[1]+ "," + ObjectCoords[2] + "," + ObjectCoords[3] );	 
		 }
		 
		 return ObjectCoords;
	 }
    
	
	 void CreatePlayerWeaponSound(Context iContext)
	 {
		 m_PlayerWeaponSFX = new Sound(iContext, m_SoundPool, R.raw.playershoot2);	
	 }
	    
	 void PlayPlayerWeaponSound()
	 {
		 if (m_SFXOn)
		 {
			 m_PlayerWeaponSFX.PlaySound();
		 }
	 }
	 
	 // High Score 
	  void CreateHighScoreEntry(String Initials, int Score)
	    {  	
	    	HighScoreEntry Entry = new HighScoreEntry(Initials,Score);
	    	m_HighScoreTable.AddItem(Entry);
	    }
	 
	 void ResetCamera()
	 {
		 m_Camera.GetOrientation().SetRotationAngle(0);
		 m_Camera.GetOrientation().ResetRotation();
	 }
	 
	 
	 void ResetGame()
	    {
	    	// Game is now reset so player can continue 
	    	m_CanContinue = true;
	    	
	    	// Resets Player's Weapon
	    	m_Weapon.ResetWeapon();
	    	
	    	// Resets the game to the beginning state
	    	ResetCamera();
	    	
	    	// Resets Player's Score to 0
	    	m_Score = 0;
	    	
	    	// Resets Power Pyramid's Health to 0
	    	m_Pyramid.GetObjectStats().SetHealth(100);
	    	
	    	// Reset Enemy Objects
	    	m_ArenaObjectsSet.ResetSet();
	    	//m_AirVehicleFleet.ResetSet();
	    	m_TankFleet.ResetSet();
	    	
	    	// Reset Game Controller
	    	m_GamePlayController.ResetController();
	   
	    }
	 
	 
	 
	 
	 void SaveGameState(String Handle)
	    {
	    	// Only save game state when game is active and being played not at
	    	// menu or high score table etc.
	    	if (m_GameState != GameState.ActiveGamePlay)
	    	{
	    		return;
	    	}
	    	
	    	// Save Player's Score
	    	SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
	   	    SharedPreferences.Editor editor = settings.edit();
	   	   
	   	    
	   	    // Player's Score
	   	    editor.putInt("Score", m_Score);
	   	   
	   	    // Player's Health
	   	    editor.putInt("Health", m_Pyramid.GetObjectStats().GetHealth());
	   		
	   	  
	   	    // Can Continue Game
	   	    editor.putBoolean("CanContinue", m_CanContinue);
	   	   
	   	    // Health Display Status
	   	    //editor.putBoolean("m_BonusGiven", m_BonusGiven);
	   	    //editor.putLong("m_TimeHealthBonusDisplayStart", m_TimeHealthBonusDisplayStart);  	    
	   	    //editor.putBoolean("m_DisplayHealthBonus", m_DisplayHealthBonus);
	   	    
	   	    // Commit the edits!
	   	    editor.commit();
	    	
	   	    // Camera
	   	    m_Camera.SaveCameraState("Camera");
	   	    
	   	    // Arena Objects Set
	   	    m_ArenaObjectsSet.SaveSet(ARENA_OBJECTS_HANDLE);
	   	    //m_Cube.SaveObjectState("m_Cube");
	   	    
	   	    // AirFleet
	   	    //m_AirVehicleFleet.SaveSet(AIR_VEHICLE_HANDLE);
	   	    
	    	// Tank Fleet
	   	    m_TankFleet.SaveSet(TANK_FLEET_HANDLE);
	   	    
	   	    //m_Tank.SaveTankState("m_Tank");
	    }
	 
	 
	 
	 void LoadContinueStatus(String Handle)
	 {
		 SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
	    		
		 m_CanContinue = settings.getBoolean("CanContinue", false);
	 }
	
	 
	 void LoadGameState(String Handle)
	    {
	    	// Load game state of last game that was inturrupted during play
	    
	    	// Restore preferences
		    SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
		
		    // Load In Player Score
		    m_Score = settings.getInt("Score", 0);
	    	
		    // Load in Player's Health 
		    int Health = settings.getInt("Health", 100);
		    m_Pyramid.GetObjectStats().SetHealth(Health);
		    
		    // Can Continue 
		    m_CanContinue = settings.getBoolean("CanContinue", false);
		   
		    // Health Display Status
		    //m_BonusGiven = settings.getBoolean("m_BonusGiven", false);
		    //m_TimeHealthBonusDisplayStart = settings.getLong("m_TimeHealthBonusDisplayStart", 0);  
		    //m_DisplayHealthBonus = settings.getBoolean("m_DisplayHealthBonus", false);
		    
		    // Camera
		    m_Camera.LoadCameraState("Camera");
		    
		    // Arena Objects Set
	   	    m_ArenaObjectsSet.LoadSet(ARENA_OBJECTS_HANDLE);
		    //m_Cube.LoadObjectState("m_Cube");
	   	    
	   	    // AirFleet
	   	    //m_AirVehicleFleet.LoadSet(AIR_VEHICLE_HANDLE);
	   	    
	   	    // Tank Fleet
	   	    m_TankFleet.LoadSet(TANK_FLEET_HANDLE);
	   	    
		    //m_Tank.LoadTankState("m_Tank");
	    }
	 
	 
	 boolean IsNewHighScore()
	    {
	    	boolean result = false;
	    	
	    	int LowestScore = m_HighScoreTable.GetLowestScore();
			int MaxScores = m_HighScoreTable.MaxNumberHighScores();
			int NumberValidScores = m_HighScoreTable.NumberValidHighScores();
		
			boolean SlotAvailable = false;
			
			if (NumberValidScores < MaxScores)
			{
				SlotAvailable = true;
			}
		
			if ((m_Score > LowestScore) || 
				((m_Score > 0) && SlotAvailable))
			{
				result = true;
			}
			
			Log.e("RENDERER", "LowestScore = " + LowestScore);
			Log.e("RENDERER", "SlotAvailable = " + SlotAvailable);
		
			return result;
	    }
	 
	  void SaveContinueStatus(String Handle)
	    {
	    	SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
	   	    SharedPreferences.Editor editor = settings.edit();
	   	   
	   	    editor.putBoolean("CanContinue", m_CanContinue);
	   	    
	   	    // Commit the edits!
	   	    editor.commit();
	    }
	 
	 
	 
	void CheckTouch()
	{
    	// Main Menu
    	if (m_GameState == GameState.MainMenu)
    	{
    		// Reset camera to face main menu
    		MenuStates result = m_MainMenu.GetMainMenuStatus(m_TouchX, m_TouchY, m_ViewPortWidth, m_ViewPortHeight);
    	
    		Log.e("CHECKTOUCH", "TOUCHED MAIN MENU!!!!!! ");
    		if (result == MenuStates.NewGame)
    		{
    			Log.e("CHECKTOUCH", "NEW GAME MENU ITEM SELECTED!!!!!! ");
    			ResetGame();
    			m_GameState = GameState.ActiveGamePlay;
    		}
    		else
    		if (result == MenuStates.ContinueCurrentGame)
    		{
    			Log.e("CHECKTOUCH", "CONTINUE GAME MENU ITEM SELECTED!!!!!! ");
    			
    			
    			LoadContinueStatus(MainActivity.SAVE_GAME_HANDLE);
    			if (m_CanContinue)
    			{
    				LoadGameState(MainActivity.SAVE_GAME_HANDLE);
    			}
    			else
    			{
    				ResetGame();
    			}
    			
    			
    			m_GameState = GameState.ActiveGamePlay;
    		}
    		else
    		if (result == MenuStates.HighScoreTable)
    		{
    			Log.e("CHECKTOUCH", "HIGH SCORE MENU ITEM SELECTED!!!!!! ");
    			m_GameState = GameState.HighScoreTable;
    		}
    		else
    		if (result == MenuStates.Copyright)
        	{
        		Log.e("CHECKTOUCH", "CopyRight MENU ITEM SELECTED!!!!!! ");
        		m_GameState = GameState.HighScoreEntry;
        	}	
    		
    	
    		return;
    	}
    	else
    	if (m_GameState == GameState.HighScoreTable)
    	{
    		m_GameState = GameState.MainMenu;
    		return;
    	}
    	else
    	if (m_GameState == GameState.HighScoreEntry)
    	{
    		
    		
    		// If User presses finished button from High Score Entry Menu 
    		EntryMenuStates result = m_HighScoreEntryMenu.GetEntryMenuStatus(m_TouchX, m_TouchY, m_ViewPortWidth, m_ViewPortHeight);
        	
    		if (result == EntryMenuStates.NextCharacterPressed)
    		{
    			Log.e("CHECKTOUCH", "Next MENU ITEM SELECTED!!!!!! ");
    			m_HighScoreEntryMenu.ProcessNextMenuSelection();
    		}
    		else
    		if (result == EntryMenuStates.PreviousCharacterPressed)
        	{
        		Log.e("CHECKTOUCH", "Previous MENU ITEM SELECTED!!!!!! ");
        		m_HighScoreEntryMenu.ProcessPreviousMenuSelection();
        	}
    		else
    		if (result == EntryMenuStates.Enter)
    		{
    			Log.e("CHECKTOUCH", "ENTER MENU ITEM SELECTED!!!!!! ");
    			m_HighScoreEntryMenu.ProcessEnterMenuSelection();
    			
    			if (m_HighScoreEntryMenu.IsEntryFinished())
    			{
    				char[] Initials = m_HighScoreEntryMenu.GetEntry();
    				String StrInitials = new String(Initials);
    				
    				CreateHighScoreEntry(StrInitials, m_Score);
    				
    				m_GameState = GameState.HighScoreTable;
    				m_HighScoreEntryMenu.ResetMenu();
    			}
    		}
    		
    		
    	
    		return;
    	}
    	else
    	if (m_GameState == GameState.GameOverScreen)
    	{
    		long CurTime = System.currentTimeMillis();
    		long Delay = CurTime - m_GameOverStartTime;
    		
    		if (Delay < m_GameOverPauseTime)
    		{
    			return;
    		}
    		
    		// Test for High Score
    		if (IsNewHighScore())
    		{
    			// Go to High Score Entry Screen
    			m_GameState = GameState.HighScoreEntry;
    		}
    		else
    		{
    			m_GameState = GameState.MainMenu;
    		}
    		ResetCamera();
    		
    		// Can not continue further since game is now over
    		m_CanContinue = false;
			SaveContinueStatus(MainActivity.SAVE_GAME_HANDLE);
			
	
    		
    	
    		return;
    	}
    	

		
		// Player Weapon Firing
    	int[] View = new int[4];
    	
    	View[0] = 0;
    	View[1] = 0;
    	View[2] = m_ViewPortWidth;
    	View[3] = m_ViewPortHeight;
    	
    	float[] WorldCoords = MapWindowCoordsToWorldCoords(View, m_TouchX, m_TouchY, 1); // 1 = far clipping plane
    	Vector3 TargetLocation = new Vector3(WorldCoords[0]/WorldCoords[3], 
    										 WorldCoords[1]/WorldCoords[3], 
    										 WorldCoords[2]/WorldCoords[3]);
    	Vector3 WeaponLocation = m_Camera.GetCameraEye();
    	
    	Vector3 Direction = Vector3.Subtract(TargetLocation, WeaponLocation);
    	if ((Direction.x == 0) && (Direction.y == 0) && (Direction.z == 0))
    	{
    		return;
    	}
    	if (m_Weapon.Fire(Direction, WeaponLocation) == true)
    	{
    		// WeaponFired
    		PlayPlayerWeaponSound();
    	}
		
	}
	
	// Persistent State
	/*
	void SaveCubes()
	{
		//m_Cube.SaveObjectState("Cube1Data");
		//m_Cube2.SaveObjectState("Cube2Data");	
	}
	*/
	
	/*
	void LoadCubes()
	{
		//m_Cube.LoadObjectState("Cube1Data");
		//m_Cube2.LoadObjectState("Cube2Data");	
	}
	*/
	
	/*
	void LoadGameState()
	{	
		// Restore preferences
		SharedPreferences settings = m_Context.getSharedPreferences("gamestate", 0);
		
		int StatePreviouslySaved = settings.getInt("previouslysaved", 0);
		
		if (StatePreviouslySaved != 0)
		{
			// Load in previously saved state
			m_Score = settings.getInt("score", 0);
			m_Health = settings.getInt("health", 100);
			
			LoadCubes();
		}
	}
	
	void SaveGameState()
	{
		// We need an Editor object to make preference changes.
	    SharedPreferences settings = m_Context.getSharedPreferences("gamestate", 0);
	    SharedPreferences.Editor editor = settings.edit();
	      
	    editor.putInt("score", m_Score);
	    editor.putInt("health", m_Health);
		
	    SaveCubes();
	    editor.putInt("previouslysaved", 1);
	    
	    
	    // Commit the edits!
	    editor.commit();
	}
	*/
	
	
	
	// HUD
	void CreateCharacterSetTextures(Context iContext)
	{
		// Numeric
	    m_CharacterSetTextures[0] = new Texture(iContext, R.drawable.charset1); 
	    m_CharacterSetTextures[1] = new Texture(iContext, R.drawable.charset2);
	    m_CharacterSetTextures[2] = new Texture(iContext, R.drawable.charset3);
	    m_CharacterSetTextures[3] = new Texture(iContext, R.drawable.charset4);
	    m_CharacterSetTextures[4] = new Texture(iContext, R.drawable.charset5);
	    m_CharacterSetTextures[5] = new Texture(iContext, R.drawable.charset6);
	    m_CharacterSetTextures[6] = new Texture(iContext, R.drawable.charset7);
	    m_CharacterSetTextures[7] = new Texture(iContext, R.drawable.charset8);
	    m_CharacterSetTextures[8] = new Texture(iContext, R.drawable.charset9);
	    m_CharacterSetTextures[9] = new Texture(iContext, R.drawable.charset0);
	 
	    // Alphabet
	    m_CharacterSetTextures[10] = new Texture(iContext, R.drawable.charseta);
	    m_CharacterSetTextures[11] = new Texture(iContext, R.drawable.charsetb);
	    m_CharacterSetTextures[12] = new Texture(iContext, R.drawable.charsetc);
	    m_CharacterSetTextures[13] = new Texture(iContext, R.drawable.charsetd);
	    m_CharacterSetTextures[14] = new Texture(iContext, R.drawable.charsete);
	    m_CharacterSetTextures[15] = new Texture(iContext, R.drawable.charsetf);
	    m_CharacterSetTextures[16] = new Texture(iContext, R.drawable.charsetg);
	    m_CharacterSetTextures[17] = new Texture(iContext, R.drawable.charseth);
	    m_CharacterSetTextures[18] = new Texture(iContext, R.drawable.charseti);
	    m_CharacterSetTextures[19] = new Texture(iContext, R.drawable.charsetj);
	    m_CharacterSetTextures[20] = new Texture(iContext, R.drawable.charsetk);
	    m_CharacterSetTextures[21] = new Texture(iContext, R.drawable.charsetl);
	    m_CharacterSetTextures[22] = new Texture(iContext, R.drawable.charsetm);
	    m_CharacterSetTextures[23] = new Texture(iContext, R.drawable.charsetn);
	    m_CharacterSetTextures[24] = new Texture(iContext, R.drawable.charseto);
	    m_CharacterSetTextures[25] = new Texture(iContext, R.drawable.charsetp);
	    m_CharacterSetTextures[26] = new Texture(iContext, R.drawable.charsetq);
	    m_CharacterSetTextures[27] = new Texture(iContext, R.drawable.charsetr);
	    m_CharacterSetTextures[28] = new Texture(iContext, R.drawable.charsets);
	    m_CharacterSetTextures[29] = new Texture(iContext, R.drawable.charsett);
	    m_CharacterSetTextures[30] = new Texture(iContext, R.drawable.charsetu);
	    m_CharacterSetTextures[31] = new Texture(iContext, R.drawable.charsetv);
	    m_CharacterSetTextures[32] = new Texture(iContext, R.drawable.charsetw);
	    m_CharacterSetTextures[33] = new Texture(iContext, R.drawable.charsetx);
	    m_CharacterSetTextures[34] = new Texture(iContext, R.drawable.charsety);
	    m_CharacterSetTextures[35] = new Texture(iContext, R.drawable.charsetz);
	    
	    // Debug Symbols
	    m_CharacterSetTextures[36] = new Texture(iContext, R.drawable.charsetcolon);
	    m_CharacterSetTextures[37] = new Texture(iContext, R.drawable.charsetsemicolon);
	    m_CharacterSetTextures[38] = new Texture(iContext, R.drawable.charsetcomma);
	    m_CharacterSetTextures[39] = new Texture(iContext, R.drawable.charsetequals);
	    m_CharacterSetTextures[40] = new Texture(iContext, R.drawable.charsetleftparen);
	    m_CharacterSetTextures[41] = new Texture(iContext, R.drawable.charsetrightparen);
	    	
	    m_CharacterSetTextures[42] = new Texture(iContext, R.drawable.charsetdot);
	    
	}
	
	
	  void SetUpHUDComposite(Context iContext)
		{
			m_HUDTexture = new Texture(iContext, R.drawable.hud);
			
			// Set up HUD Composite BillBoard
			//Create Shader
	    	//Shader Shader = m_OneLightTexShader; //new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight);	// ok	  
	    	Shader Shader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight);	// ok
	    	//Shader Shader = new Shader(m_Context, R.raw.vsonelight, R.raw.fsonelightnodiffuse);	// ok
	    	
	    	
	    	
	        // Create Debug Local Axis Shader
	        //Shader LocalAxisShader = m_LocalAxisShader; // new Shader(iContext, R.raw.vslocalaxis, R.raw.fslocalaxis);	  
	        
	        //MeshEx(int CoordsPerVertex, 
			//		int MeshVerticesDataPosOffset, 
			//		int MeshVerticesUVOffset , 
			//		int MeshVerticesNormalOffset,
			//		float[] Vertices,
			//		short[] DrawOrder
	        MeshEx Mesh = new MeshEx(8,0,3,5,Cube.CubeData, Cube.CubeDrawOrder);
	        
	        // Create Material for this object
	        Material Material1 = new Material();
	        Material1.SetEmissive(1.0f, 1.0f, 1.0f);
	        //Material1.SetAlpha(0.5f);
	   
	        
	        Texture[] Tex = new Texture[1];
	        Tex[0] = m_HUDTexture;
	        //CubeTex[1] = m_TexGreenAndroid;
	          
	        
	        
	        m_HUDComposite = new BillBoard(iContext, 
					null, 
					Mesh, 
					Tex, 
					Material1, 
					Shader//, 
					//LocalAxisShader
					);
	           
	        // Set Intial Position and Orientation
	        //Vector3 Axis = new Vector3(1,1,1);
	        Vector3 Position = new Vector3(0.0f, 3.0f, 0.0f);
	        Vector3 Scale = new Vector3(1.0f,0.1f,0.01f);
	        
	        m_HUDComposite.m_Orientation.SetPosition(Position);
	        //m_HUDComposite.m_Orientation.SetRotationAxis(Axis);
	        m_HUDComposite.m_Orientation.SetScale(Scale);
	        
	        m_HUDComposite.GetObjectPhysics().SetGravity(false); 	
			
	        // Set black portion of HUD to transparent
	        m_HUDComposite.GetMaterial().SetAlpha(1.0f);
	        m_HUDComposite.SetBlend(true);
		}
	   
	 void CreateCharacterSet(Context iContext)
	    {
	    	//Create Shader
	    	//Shader Shader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight);	// ok
	    	Shader Shader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelightnodiffuse);	// ok
	  	  
	    	
	        // Create Debug Local Axis Shader
	        //Shader LocalAxisShader = m_LocalAxisShader; //new Shader(iContext, R.raw.vslocalaxis, R.raw.fslocalaxis);	  
	        
	        //MeshEx(int CoordsPerVertex, 
			//		int MeshVerticesDataPosOffset, 
			//		int MeshVerticesUVOffset , 
			//		int MeshVerticesNormalOffset,
			//		float[] Vertices,
			//		short[] DrawOrder
	        MeshEx Mesh = new MeshEx(8,0,3,5,Cube.CubeData, Cube.CubeDrawOrder);
	        
	        // Create Material for this object
	        Material Material1 = new Material();
	        Material1.SetEmissive(1.0f, 1.0f, 1.0f);
	        //Material1.SetAlpha(0.5f);
	    
	       
	        // Create Texture
	        CreateCharacterSetTextures(iContext);
	        
	        
	        
	        // Setup HUD
	        SetUpHUDComposite(iContext);
	        
	        m_CharacterSet = new BillBoardCharacterSet();
	        
	        int NumberCharacters = 43; 
	        char[] Characters = new char[BillBoardCharacterSet.MAX_CHARACTERS];
	        Characters[0] = '1';
	        Characters[1] = '2';
	        Characters[2] = '3';
	        Characters[3] = '4';
	        Characters[4] = '5';
	        Characters[5] = '6';
	        Characters[6] = '7';
	        Characters[7] = '8';
	        Characters[8] = '9';
	        Characters[9] = '0';
	        
	        // AlphaBets
	        Characters[10] = 'a';
	        Characters[11] = 'b';
	        Characters[12] = 'c';
	        Characters[13] = 'd';
	        Characters[14] = 'e';
	        Characters[15] = 'f';
	        Characters[16] = 'g';
	        Characters[17] = 'h';
	        Characters[18] = 'i';
	        Characters[19] = 'j';
	        Characters[20] = 'k';
	        Characters[21] = 'l';
	        Characters[22] = 'm';
	        Characters[23] = 'n';
	        Characters[24] = 'o';
	        Characters[25] = 'p';
	        Characters[26] = 'q';
	        Characters[27] = 'r';
	        Characters[28] = 's';
	        Characters[29] = 't';
	        Characters[30] = 'u';
	        Characters[31] = 'v';
	        Characters[32] = 'w';
	        Characters[33] = 'x';
	        Characters[34] = 'y';
	        Characters[35] = 'z';
	        
	        // Debug 
	        Characters[36] = ':';
	        Characters[37] = ';';
	        Characters[38] = ',';
	        Characters[39] = '=';
	        Characters[40] = '(';
	        Characters[41] = ')';
	        Characters[42] = '.';
	        
	        
	     
	        for (int i = 0; i < NumberCharacters; i++)
	        {
	        	Texture[] Tex = new Texture[1];
	        	Tex[0] = m_CharacterSetTextures[i];
	 
	        	BillBoardFont Font = new BillBoardFont(iContext, 
	        										   null, 
	        										   Mesh, 
	        										   Tex, 
	        										   Material1, 
	        										   Shader, 
	        										   //LocalAxisShader,
	        										   Characters[i]);
	        	
	        	Font.GetObjectPhysics().SetGravity(false); 	 	
	        	m_CharacterSet.AddToCharacterSet(Font);
	        }
	           
	    }
	   
	
	 
	  void CreateHealthItem()
	    {
	    	Texture HUDTexture = new Texture(m_Context, R.drawable.hud);
			
			// Set up HUD Composite BillBoard
			//Create Shader
	    	Shader Shader = new Shader(m_Context, R.raw.vsonelight, R.raw.fsonelightnodiffuse);	// ok
	    	   
	        // Create Debug Local Axis Shader
	        //Shader LocalAxisShader = m_LocalAxisShader; //new Shader(m_Context, R.raw.vslocalaxis, R.raw.fslocalaxis);	  
	        
	        //MeshEx(int CoordsPerVertex, 
			//		int MeshVerticesDataPosOffset, 
			//		int MeshVerticesUVOffset , 
			//		int MeshVerticesNormalOffset,
			//		float[] Vertices,
			//		short[] DrawOrder
	        MeshEx Mesh = new MeshEx(8,0,3,5,Cube.CubeData, Cube.CubeDrawOrder);
	        
	        // Create Material for this object
	        Material Material1 = new Material();
	        Material1.SetEmissive(1.0f, 1.0f, 1.0f);
	        //Material1.SetAlpha(0.5f);
	   
	        
	        Texture[] Tex = new Texture[1];
	        Tex[0] = HUDTexture;
	     
	   
	        BillBoard HUDHealthComposite = new BillBoard(m_Context, 
													null, 
													Mesh, 
													Tex, 
													Material1, 
													Shader//, 
													//LocalAxisShader
													);
	      
	        Vector3 Scale = new Vector3(1.0f,0.1f,0.01f);
	        HUDHealthComposite.m_Orientation.SetScale(Scale);
	        
	        HUDHealthComposite.GetObjectPhysics().SetGravity(false); 	
			
	        
	        // Set Black portion of HUD to transparent
	        HUDHealthComposite.GetMaterial().SetAlpha(1.0f);
	        HUDHealthComposite.SetBlend(true);
	        
	        
	    	// Create Health HUD
	        Texture HealthTexture = new Texture(m_Context, R.drawable.health);
	        Vector3 ScreenPosition = new Vector3(0.8f, 
	            								 m_Camera.GetCameraViewportHeight()/2, 
	            								 0.5f);  
	        
	       
	        HUDItem HUDHealth = new HUDItem("health", 	
	        								m_Health, 	
	        								ScreenPosition,
	        								m_CharacterSet,
	        								HealthTexture,
	        								HUDHealthComposite);
	    	
	    	if (m_HUD.AddHUDItem(HUDHealth) == false)
	    	{
	    		Log.e("ADDHUDITEM" , "CANNOT ADD IN NEW HUD HEALTH ITEM");
	    	}
	    }
	 
	 
	
	 void CreateHUD()
	    {
	        // Create HUD
	        m_HUD = new HUD(m_Context);
	        
	        // Create Score HUD
	        Vector3 ScreenPosition = new Vector3(-m_Camera.GetCameraViewportWidth()/2 + 0.3f, 
	            								  m_Camera.GetCameraViewportHeight()/2, 
	            								  0.5f); 
	      
	        
	        // Create Score Item for HUD
	        HUDItem HUDScore = new HUDItem("score", 	
	        								0, 	
	        								ScreenPosition,
	        								m_CharacterSet,
	        								null,
	        								m_HUDComposite);
	    	
	    	if (m_HUD.AddHUDItem(HUDScore) == false)
	    	{
	    		Log.e("ADDHUDITEM" , "CANNOT ADD IN NEW HUD ITEM");
	    	}
	    
	    	CreateHealthItem();
	    	//CreateHealthBonusItem();
	    }
	
	
	// Update HUD
 	void UpdateHUD()
 	{
 		m_Health = m_Pyramid.GetObjectStats().GetHealth();
 		
 		m_HUD.UpdateHUDItemNumericalValue("health", m_Health); 	
 		m_HUD.UpdateHUDItemNumericalValue("score",m_Score);
 	}
	 
	
	//SFX
	
	// Sound Pool
	void CreateSoundPool()
	{
		/*
		* 
		* public SoundPool (int maxStreams, int streamType, int srcQuality)

		Added in API level 1
		Constructor. Constructs a SoundPool object with the following characteristics:

		Parameters
		maxStreams	the maximum number of simultaneous streams for this SoundPool object
		streamType	the audio stream type as described in AudioManager For example, game applications will normally use STREAM_MUSIC.
		srcQuality	the sample-rate converter quality. Currently has no effect. Use 0 for the default.
				
		Returns
		a SoundPool object, or null if creation failed
		* 
		* 
		* 
		*/
			
		int maxStreams = 10; 
		int streamType = AudioManager.STREAM_MUSIC; 
		int srcQuality = 0;
			
		m_SoundPool = new SoundPool(maxStreams, streamType, srcQuality);
			 
		if (m_SoundPool == null)
		{
			Log.e("RENDERER " , "m_SoundPool creation failure!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
	}
	
	 void CreateGrid(Context iContext)
	 {
		 Vector3 GridColor		= new Vector3(0,0.0f,0.3f);
		 float 	GridHeight		= -0.5f;
		 float 	GridStartZValue	= -15; //-20;
		 float 	GridStartXValue = -15;
		 float 	GridSpacing		= 1.0f;
			
		 //int 	GridSize		= 10;
		 int GridSizeZ = 33;  // grid vertex points in the z direction
		 int GridSizeX = 33;  // grid vertex point in the x direction
		
		 //Shader 	iShader			= new Shader(iContext, R.raw.vsonelightnotexture, R.raw.fsonelightnotexture);	
		 Shader 	iShader			= new Shader(iContext, R.raw.vsgrid, R.raw.fslocalaxis);	
		    
		 m_Grid = new GravityGridEx(iContext,
					  				GridColor,
					  				GridHeight,
					  				GridStartZValue, 
					  				GridStartXValue, 
					  				GridSpacing, 
					  				GridSizeZ,
					  				GridSizeX,
					  				iShader);
	 }
	    
	 void SetupLights()
	 {
		 // Set Light Characteristics
	     Vector3 LightPosition = new Vector3(0,125,125);
	     
	     float[] AmbientColor = new float [3];
	     AmbientColor[0] = 0.0f;
	     AmbientColor[1] = 0.0f;
	     AmbientColor[2] = 0.0f;  
	        
	     float[] DiffuseColor = new float[3];
	     DiffuseColor[0] = 1.0f;
	     DiffuseColor[1] = 1.0f;
	     DiffuseColor[2] = 1.0f;
	        
	     float[] SpecularColor = new float[3];
	     SpecularColor[0] = 1.0f;
	     SpecularColor[1] = 1.0f;
	     SpecularColor[2] = 1.0f;
	          
	     m_PointLight.SetPosition(LightPosition);
	     m_PointLight.SetAmbientColor(AmbientColor);
	     m_PointLight.SetDiffuseColor(DiffuseColor);
	     m_PointLight.SetSpecularColor(SpecularColor);
	 }
	   
	 void SetupCamera()
	 {	
		// Set Camera View
		 Vector3 Eye = new Vector3(0,1,13);
	     Vector3 Center = new Vector3(0,0,-1);
	     Vector3 Up = new Vector3(0,1,0);  
	        
	     float ratio = (float) m_ViewPortWidth / m_ViewPortHeight;
	     float Projleft	= -ratio;
	     float Projright = ratio;
	     float Projbottom= -1;
	     float Projtop	= 1;
	     float Projnear	= 3;
	     float Projfar	= 50; //100;
	    	
	     m_Camera = new Camera(m_Context,
	        				   Eye,
	        				   Center,
	        				   Up,
	        				   Projleft, Projright, 
	        				   Projbottom,Projtop, 
	        				   Projnear, Projfar);
	  }
	 
	 SphericalPolygonExplosion CreateExplosion(Context iContext,
			  								   int NumberParticles,
			  								   Vector3 Color,
			  								   Vector3 ParticleSize,
			  								   long ParticleLifeSpan,
			  								   boolean RandomColors,
			  								   boolean ColorAnimation, 
			  								   float FadeDelta)
	 {
		 SphericalPolygonExplosion Explosion = null;

		 // No textures
		 Mesh PolyParticleMesh = new Mesh(6,0,-1,3,PolyParticleEx.PolyParticleVertices);

		 // Create Material for this object
		 Material Material2 = new Material();
		 Material2.SetSpecular(0, 0, 0);

		 //Shader LocalAxisShader = m_LocalAxisShader; //new Shader(iContext, R.raw.vslocalaxis, R.raw.fslocalaxis);	 
		 Shader Shader2 = new Shader(iContext, R.raw.vsonelightnotexture, R.raw.fsonelightnotexture);	

		 Explosion = new SphericalPolygonExplosion(NumberParticles, 
				 								   Color,
				 								   ParticleLifeSpan,
				 								   RandomColors, 
				 								   ColorAnimation,
				 								   FadeDelta,
				 								   ParticleSize,
										    
				 								   iContext, 
				 								   PolyParticleMesh, 
				 								   null, 
				 								   null, 
				 								   Material2, 
				 								   Shader2);
		 return Explosion;
	 }
	 
	 
	 void UpdateGravityGrid()
	 {	
		  int NumberMasses = 0;
	      Object3d[] Masses = new Object3d[50];
	    	
		 
		  // Clear Masses from Grid from Previous Update
		  m_Grid.ResetGrid();
	    	
		  // Arena Objects
		  m_ArenaObjectsSet.AddArenaObjectsToGravityGrid(m_Grid);
	    
		  // Tank
		  m_TankFleet.AddTankFleetToGravityGrid(m_Grid);
		  
		  
		  // Add Player Weapon Rounds
		  NumberMasses = m_Weapon.GetActiveAmmo(0, Masses);
		  m_Grid.AddMasses(NumberMasses, Masses);
		  
		  
		  // Add Player Pyramid
		  m_Grid.AddMass(m_Pyramid);
		  
	  }
	 
    	@Override
    	public void onSurfaceCreated(GL10 unused, EGLConfig config) 
    	{
    		m_PointLight = new PointLight(m_Context);
    		SetupLights();
    		
    		
    		// Create SFX
    		CreateSoundPool();
    		
    		// Create a new gravity grid
    		CreateGrid(m_Context);
    		
    		// Create HUD
    		// Get Width and Height of surface
        	m_ViewPortHeight = m_Context.getResources().getDisplayMetrics().heightPixels;
        	m_ViewPortWidth = m_Context.getResources().getDisplayMetrics().widthPixels;
        
    		SetupCamera();
    		
    		// Create Character Set
            CreateCharacterSet(m_Context);
            
    		CreateHUD();
    		
    		// Create Weapon
    		CreateWeapon(m_Context);
    		CreatePlayerWeaponSound(m_Context);
    		
    		// Create Player's Graphic
    		CreatePyramid(m_Context);
    		
    		//Main Menu
            CreateMainMenu(m_Context);  
            m_GameState = GameState.MainMenu;
           
            // Create High Score Table
            CreateHighScoreTable(m_Context);
            
            // Create High Score Entry Menu
            CreateHighScoreEntryMenu(m_Context);
            
            // Create ArenaObjectSet
            CreateArenaObjectsSet(m_Context);
             
            // Create Tank Fleet
            CreateTankFleet(m_Context);
          
            // Create Game Play Controller
            CreateGamePlayController(m_Context);
            
            // Create Game Over BillBoard
            CreateGameOverBillBoard(m_Context);
           
    	}

    	@Override
    	public void onSurfaceChanged(GL10 unused, int width, int height) 
    	{
    		// Ignore the passed-in GL10 interface, and use the GLES20
            // class's static methods instead.
            GLES20.glViewport(0, 0, width, height);
    		//GLES20.glViewport(0, height, width/2, height/2);
            
            m_ViewPortWidth = width;
            m_ViewPortHeight = height;
            
            SetupCamera();
    	}

    	
    	
    	void UpdateScene()
    	{
    		m_Camera.UpdateCamera();
    		
    		// Main Menu
   		 	if (m_GameState == GameState.MainMenu)
   		 	{
   		 		m_MainMenu.UpdateMenu(m_Camera);
   		 		return;
   		 	}
   	    	
   		 	// High Score Table
   		 	if (m_GameState == GameState.HighScoreTable)
   		 	{
   		 		m_HighScoreTable.UpdateHighScoreTable(m_Camera);
   		 		return;
   		 	}
   		 
   		 	// High Score Entry
   		 	if (m_GameState == GameState.HighScoreEntry)
   		 	{
   		 		// Update HighScore Entry Table
   		 		m_HighScoreEntryMenu.UpdateHighScoreEntryMenu(m_Camera);
   		 		return;
   		 	}
   		 	
   		 	// Game Over Screen
   	    	if (m_GameState == GameState.GameOverScreen)
   	    	{
   	    		// Update Game Over Screen Here
   	    		UpdateGameOverBillBoard();
   	    		m_GameOverBillBoard.UpdateObject3d(m_Camera);
   	    			
   	    		return;
   	    	}
   		 	
   	    	// Check if Game has ended and go to  
   	    	if (m_Pyramid.GetObjectStats().GetHealth() <= 0)
   	    	{
   	    		m_GameState = GameState.GameOverScreen;	
   	    		m_GameOverStartTime = System.currentTimeMillis();	
   	    		
   	    		// Game is over cannnot continue current game.
   	    		m_CanContinue = false;
   	    	}
   	    	
   	    	
   		 
   		 	// Process the Collisons in the Game
   		 	ProcessCollisions();
   	     
   		 	////////////////////////// Update Objects
   		 	// Arena Objects
   		 	m_ArenaObjectsSet.UpdateArenaObjects();
    	    
   		 	// Tank Objects
   		 	m_TankFleet.UpdateTankFleet();
   		  
   		 	////////////////////////// Update and Draw Grid
   		 	UpdateGravityGrid();
   	         	    
   		 	// Player's Pyramid
   		 	m_Pyramid.UpdateObject3d();
   	
   		 	// Player's Weapon
   		 	m_Weapon.UpdateWeapon();
   	 
   		 	///////////////////////// HUD
   		 	// Update HUD
   		 	UpdateHUD();
   		 	m_HUD.UpdateHUD(m_Camera); 	
   	     
   		 	// Update Game Play Controller
   		 	m_GamePlayController.UpdateController(System.currentTimeMillis());
    	}
    	
    	
    	
    	
    	
    	void RenderScene()
    	{
    		// Main Menu
   		 	if (m_GameState == GameState.MainMenu)
   		 	{
   		 		m_MainMenu.RenderMenu(m_Camera, m_PointLight, false);
   		 		return;
   		 	}
   	    	
   		 	// High Score Table
   		 	if (m_GameState == GameState.HighScoreTable)
   		 	{
   		 		m_HighScoreTable.RenderHighScoreTable(m_Camera, m_PointLight, false);
   		 		return;
   		 	}
   		 
   		 	// High Score Entry
   		 	if (m_GameState == GameState.HighScoreEntry)
   		 	{
   		 		m_HighScoreEntryMenu.RenderHighScoreEntryMenu(m_Camera, m_PointLight, false);
   		 		return;
   		 	}
   		 
   			// Game Over Screen
   	    	if (m_GameState == GameState.GameOverScreen)
   	    	{
   	    		// Update Game Over Screen Here
   	    		m_GameOverBillBoard.DrawObject(m_Camera, m_PointLight);
   	    	}
   		 	
   		 	
   		 	//////////////////////////// Draw Objects 
   		 	m_ArenaObjectsSet.RenderArenaObjects(m_Camera, m_PointLight,false);  	  
   		 	m_TankFleet.RenderTankFleet(m_Camera, m_PointLight,false);
   		 
   		 	////////////////////////// Update and Draw Grid
   		 	m_Grid.DrawGrid(m_Camera);
   	     	    
   		 	// Player's Pyramid
   		 	m_Pyramid.DrawObject(m_Camera, m_PointLight);
   
   		 	// Player's Weapon
   		 	m_Weapon.RenderWeapon(m_Camera, m_PointLight, false);
   	     
   		 	///////////////////////// HUD
   
   		 	// Render HUD 
   		 	m_HUD.RenderHUD(m_Camera, m_PointLight);   
    	}
    	
    	
    	void CalculateFrameUpdateElapsedTime()
        {
        	long Oldtime;
        	
        	// Elapsed Time Since Last in this function
        	if (!m_TimeInit)
        	{
        		m_ElapsedTime = 0;
        		m_CurrentTime = System.currentTimeMillis();
        		m_TimeInit = true;
        	}
        	else
        	{
        		Oldtime = m_CurrentTime;
        		m_CurrentTime = System.currentTimeMillis();
        		m_ElapsedTime = m_CurrentTime - Oldtime;
        	}
        }
        
    	
        void FrameMove()
        {	
        	m_UpdateTimeCount += m_ElapsedTime;

        	if (m_UpdateTimeCount > k_SecondsPerTick)
        	{
        		while(m_UpdateTimeCount > k_SecondsPerTick)
        		{
        			// Update Camera Position
       			 	if (m_CameraMoved)
       			 	{
       			 		ProcessCameraMove();
       			 	}
       			
        			// update the scene
        			UpdateScene();
        			
        			m_UpdateTimeCount -= k_SecondsPerTick;
        		}
        	}
        }

    	
    	@Override
    	public void onDrawFrame(GL10 unused) 
    	{
    		 GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    		 GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
    	      
  
    		  // UPDATE SFX
    	      if (m_SFXOn)
    	      {
    	    	  TurnSFXOnOff(true);
    	      }
    	      else
    	      {
    	    	  TurnSFXOnOff(false);
    	      }
    	     	      
    	      // Did user touch screen
    	      if (m_ScreenTouched)
    	      {	
    	    	  // Process Screen Touch      	
    	    	  CheckTouch();
    	    	  m_ScreenTouched = false;
    	      }
    		 	
    	      
    	      CalculateFrameUpdateElapsedTime();
    	      FrameMove();  
    	      RenderScene(); 
    	}
}

