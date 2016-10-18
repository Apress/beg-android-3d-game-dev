package com.robsexample.glhelloworld;


import android.content.Context;
import android.content.SharedPreferences;



enum AIVehicleCommand
{	
	None,
	Patrol,
	//AttackTarget,
	//EvasiveManeuversSpiral
};

enum AIVehicleObjectsAffected
{		
	None,
	WayPoints,
	PrimaryWeapon,
	SecondaryWeapon
};


public class VehicleCommand 
{
	private Context m_Context;
	
	
	private AIVehicleCommand m_Command;	
	private AIVehicleObjectsAffected m_ObjectsAffected;
	private int	m_NumberObjectsAffected;
	
	private float	m_DeltaAmount;
	private float	m_DeltaIncrement;
	
	private float	m_MaxValue;
	private float	m_MinValue;
	
	static int MAX_WAYPOINTS = 50;
	private int m_NumberWayPoints = 0;
	private int m_CurrentWayPointIndex = 0;
	private Vector3[] m_WayPoints = new Vector3[MAX_WAYPOINTS];
	
	private Vector3 m_Target;
	private Object3d m_TargetObject;

	
	VehicleCommand(Context 					iContext,
				   AIVehicleCommand			Command,
				   AIVehicleObjectsAffected	ObjectsAffected,
				   int						NumberObjectsAffected,
				   float					DeltaAmount,
				   float					DeltaIncrement,
				   float					MaxValue,
				   float					MinValue,
				   int 						NumberWayPoints,
				   Vector3[]				WayPoints,
				   Vector3 					Target,
				   Object3d					TargetObject
				   )
	{
		m_Context 				=	iContext;
		m_Command				=	Command;
		m_ObjectsAffected		=	ObjectsAffected;
		m_NumberObjectsAffected	=	NumberObjectsAffected;
		m_DeltaAmount			=	DeltaAmount;
		m_DeltaIncrement		=   DeltaIncrement;
		m_MaxValue				=	MaxValue;
		m_MinValue				=	MinValue;
		
		m_NumberWayPoints 		=  	NumberWayPoints;
		m_WayPoints				= 	WayPoints;
		m_Target 				=	Target;
		m_TargetObject			=   TargetObject;
	}
	
	void SaveState(String Handle)
	{
		SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
		SharedPreferences.Editor editor = settings.edit();
		     
		// Command
		String CommandHandle = Handle + "Command";
		String CommandStr = m_Command + "";
		editor.putString(CommandHandle, CommandStr);
		
		// Objects Effected
		String TypeEffectedHandle = Handle + "TypeObjectsEffected";
		String TypeEffectedStr = m_ObjectsAffected + "";
		editor.putString(TypeEffectedHandle,TypeEffectedStr);
				
		// Number Objects Affected
		String ObjectsAffectedHandle = Handle + "ObjectsAffected";
		editor.putInt(ObjectsAffectedHandle, m_NumberObjectsAffected);
		
		// DeltaAmount
		String AmountHandle = Handle + "DeltaAmount";
		editor.putFloat(AmountHandle, m_DeltaAmount);
		
		// Delta Increment
		String IncrementHandle = Handle + "DeltaIncrement"; 
		editor.putFloat(IncrementHandle, m_DeltaIncrement);

		// Max Value
		String MaxValueHandle = Handle + "MaxValue"; 
		editor.putFloat(MaxValueHandle, m_MaxValue);
		
		// Min Value
		String MinValueHandle = Handle + "MinValue"; 
		editor.putFloat(MinValueHandle, m_MinValue);
		
		// Number Way Points
		String NumberWayPointsHandle = Handle + "NumberWayPoints"; 
		editor.putInt(NumberWayPointsHandle, m_NumberWayPoints);
		
		// Current WayPoint Index
		String WayPointIndexHandle = Handle + "WayPointIndex"; 
		editor.putInt(WayPointIndexHandle, m_CurrentWayPointIndex);
	
		// Waypoints
		//Log.e("VEHICLECOMMAND" , "Saved m_NumberWayPoints = " + m_NumberWayPoints);
		for (int i = 0; i < m_NumberWayPoints; i++) 
		{
			String WayPointXHandle = Handle + "WayPointX" + i; 
			editor.putFloat(WayPointXHandle, m_WayPoints[i].x);
			
			String WayPointYHandle = Handle + "WayPointY" + i; 
			editor.putFloat(WayPointYHandle, m_WayPoints[i].y);
			
			String WayPointZHandle = Handle + "WayPointZ" + i; 
			editor.putFloat(WayPointZHandle, m_WayPoints[i].z);
		}
		
		// Target
		if (m_Target != null)
		{
			String TargetXHandle = Handle + "TargetX"; 
			editor.putFloat(TargetXHandle, m_Target.x);
		
			String TargetYHandle = Handle + "TargetY"; 
			editor.putFloat(TargetYHandle, m_Target.y);
		
			String TargetZHandle = Handle + "TargetZ"; 
			editor.putFloat(TargetZHandle, m_Target.z);
		}
		
		// Save Target Object Location
		if (m_TargetObject != null)
		{
			String TargetObjXHandle = Handle + "TargetObjX"; 
			editor.putFloat(TargetObjXHandle, m_TargetObject.m_Orientation.GetPosition().x);
		
			String TargetObjYHandle = Handle + "TargetObjY"; 
			editor.putFloat(TargetObjYHandle, m_TargetObject.m_Orientation.GetPosition().y);
		
			String TargetObjZHandle = Handle + "TargetObjZ"; 
			editor.putFloat(TargetObjZHandle, m_TargetObject.m_Orientation.GetPosition().z);
		}
		
		// Commit the edits!
		editor.commit();
	}

	static AIVehicleCommand MatchCommand(String CommandStr)
	{
		AIVehicleCommand Command = AIVehicleCommand.None;
		
		if (CommandStr.equalsIgnoreCase("None"))
		{
			Command = AIVehicleCommand.None;
		}
		else
		if (CommandStr.equalsIgnoreCase("Patrol"))
		{
			Command = AIVehicleCommand.Patrol;
		}
		
		/*
		else
		if (CommandStr.equalsIgnoreCase("AttackTarget"))
		{
			Command = AIVehicleCommand.AttackTarget;
		}
		else
		if (CommandStr.equalsIgnoreCase("EvasiveManeuversSpiral"))
		{
			Command = AIVehicleCommand.EvasiveManeuversSpiral;
		}
		*/
		
		
		return Command;
	}
	
	static AIVehicleObjectsAffected MatchObjectsAffected(String ObjectsAffectedStr)
	{
		AIVehicleObjectsAffected ObjectsAffected = AIVehicleObjectsAffected.None;
		
		if (ObjectsAffectedStr.equalsIgnoreCase("None"))
		{
			ObjectsAffected = AIVehicleObjectsAffected.None;
		}
		else
		if (ObjectsAffectedStr.equalsIgnoreCase("WayPoints"))
		{
			ObjectsAffected = AIVehicleObjectsAffected.WayPoints;
		}
		else
		if (ObjectsAffectedStr.equalsIgnoreCase("PrimaryWeapon"))
		{
			ObjectsAffected = AIVehicleObjectsAffected.PrimaryWeapon;
		}
		else
		if (ObjectsAffectedStr.equalsIgnoreCase("SecondaryWeapon"))
		{
			ObjectsAffected = AIVehicleObjectsAffected.SecondaryWeapon;
		}
		
		return ObjectsAffected;
	}
	
	void LoadState(String Handle)
	{
	    SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
		
	    // Command
	 	String CommandHandle = Handle + "Command";
	 	String CommandStr = settings.getString(CommandHandle, "None");
	 	m_Command =  MatchCommand(CommandStr);
	 	
	 	// Objects Effected
	 	String TypeEffectedHandle = Handle + "TypeObjectsEffected";
	 	String TypeEffectedStr = settings.getString(TypeEffectedHandle, "None");
	 	m_ObjectsAffected = MatchObjectsAffected(TypeEffectedStr);
	 		 				
	 	// Number Objects Affected
	 	String ObjectsAffectedHandle = Handle + "ObjectsAffected";
	 	m_NumberObjectsAffected = settings.getInt(ObjectsAffectedHandle, 0);
	 	
	 	// DeltaAmount
	 	String AmountHandle = Handle + "DeltaAmount";
	 	m_DeltaAmount = settings.getFloat(AmountHandle, 0);
	 	
	 	// Delta Increment
	 	String IncrementHandle = Handle + "DeltaIncrement"; 
	 	m_DeltaIncrement = settings.getFloat(IncrementHandle, 0);
	 	
	 	// Max Value
	 	String MaxValueHandle = Handle + "MaxValue"; 
	 	m_MaxValue = settings.getFloat(MaxValueHandle, 0);
	 	
	 	// Min Value
	 	String MinValueHandle = Handle + "MinValue"; 	
	 	m_MinValue = settings.getFloat(MinValueHandle, 0);
	 	
	 	// Number Way Points
	 	String NumberWayPointsHandle = Handle + "NumberWayPoints"; 
	 	m_NumberWayPoints = settings.getInt(NumberWayPointsHandle, 0);
	
	 	// Current WayPoint Index
	 	String WayPointIndexHandle = Handle + "WayPointIndex";  	
	 	m_CurrentWayPointIndex = settings.getInt(WayPointIndexHandle, 0);
	 	 	
	 	// Way Points
	 	//Log.e("VEHICLECOMMAND" , "Loaded m_NumberWayPoints = " + m_NumberWayPoints);
	 	
	 	// Create new WayPoint Array
	 	m_WayPoints = new Vector3[m_NumberWayPoints];
	 	
	 	for (int i = 0; i < m_NumberWayPoints; i++)
	 	{
	 		//Log.e("VEHICLECOMMAND", "INDEX = " + i);
	 		m_WayPoints[i] = new Vector3(0,0,0);
	 		
	 		String WayPointXHandle = Handle + "WayPointX" + i; 
	 		m_WayPoints[i].x = settings.getFloat(WayPointXHandle, 0);
	 		
	 		String WayPointYHandle = Handle + "WayPointY" + i; 
	 		m_WayPoints[i].y = settings.getFloat(WayPointYHandle, 0);
	 		
	 		String WayPointZHandle = Handle + "WayPointZ" + i; 		
	 		m_WayPoints[i].z = settings.getFloat(WayPointZHandle, 0);
	 	}
	 	
	 	// Target 
	 	m_Target = new Vector3(0,0,0);
	 	
	 	String TargetXHandle = Handle + "TargetX"; 
	 	m_Target.x = settings.getFloat(TargetXHandle, 0);
	 		
	 	String TargetYHandle = Handle + "TargetY"; 	
	 	m_Target.y = settings.getFloat(TargetYHandle, 0);
	 	
	 	String TargetZHandle = Handle + "TargetZ"; 
	 	m_Target.z = settings.getFloat(TargetZHandle, 0);
	 	
	 	// Target Object
		Mesh iMesh =null;
		MeshEx iMeshEx= null;
		Texture[] iTextures= null; 
		Material iMaterial =null;
		Shader iShader =null;
		Shader LocalAxisShader= null;
		
		m_TargetObject = new Object3d(m_Context, 
									  iMesh, 
									  iMeshEx, 
									  iTextures, 
									  iMaterial, 
									  iShader//, 
									  //LocalAxisShader
									  );
	 	
	 
	 	// Save Target Object Location
	 	String TargetObjXHandle = Handle + "TargetObjX"; 	 	
	 	float Objx = settings.getFloat(TargetObjXHandle, 0);
	 	
	 	String TargetObjYHandle = Handle + "TargetObjY"; 
	 	float Objy = settings.getFloat(TargetObjYHandle, 0);
	 
	 	String TargetObjZHandle = Handle + "TargetObjZ";  		
	 	float Objz = settings.getFloat(TargetObjZHandle, 0);
	 		
	    m_TargetObject.m_Orientation.GetPosition().Set(Objx, Objy, Objz);
	}
	
	AIVehicleCommand		 GetCommand()				{return	m_Command;}
	AIVehicleObjectsAffected GetObjectsAffected()		{return m_ObjectsAffected;}
	int						 GetNumberObjectsAffected()	{return m_NumberObjectsAffected;}
	float					 GetDeltaAmount()			{return m_DeltaAmount;}
	float					 GetDeltaIncrement()		{return m_DeltaIncrement;}
	float					 GetMaxValue()				{return m_MaxValue;}
	float					 GetMinValue()				{return m_MinValue;}
	
	Vector3[]				 GetWayPoints()				{return m_WayPoints;}
	int						 GetNumberWayPoints()		{return m_NumberWayPoints;}
	Vector3 				 GetCurrentWayPoint()		{return m_WayPoints[m_CurrentWayPointIndex];}
	Vector3					 GetTarget()				{return m_Target;}
	Object3d				 GetTargetObject()			{return m_TargetObject;}
	
	void IncrementWayPointIndex()
	{
		int NextWayPointIndex = m_CurrentWayPointIndex + 1;
		if (NextWayPointIndex < m_NumberWayPoints)
		{
			m_CurrentWayPointIndex = NextWayPointIndex;
		}
		else
		{
			// Loop Waypoints
			m_CurrentWayPointIndex = 0;
		}
	}
	
	void ClearCommand()				
	{
		m_Command 			= AIVehicleCommand.None;
		m_ObjectsAffected 	= AIVehicleObjectsAffected.None;
	}

	/*
	String GetCommandString()
	{
		String result = "";

		result += "Command = ";
		result += m_Command;
		
		result += ", ObjectsAffected = ";
		result += m_ObjectsAffected;
		result += ", NumberObjectsAffected = ";
		result += m_NumberObjectsAffected;
		
		result += ",DeltaAmount = ";
		result += m_DeltaAmount;
		result += ",DeltaIncrement = ";
		result += m_DeltaIncrement;
		
		result += ",MaxValue = ";
		result += m_MaxValue;
		result += ",MinValue = ";
		result += m_MinValue;
		
		result += ",Number WayPoints = ";
		result += m_NumberWayPoints;
		
		result += ", CurrentWayPointNumber = ";
		result += m_CurrentWayPointIndex;
		
		result += ", CurrentWayPoint = ";
		result += m_WayPoints[m_CurrentWayPointIndex].GetVectorString();

		return result;		
	}
	*/
	


}
