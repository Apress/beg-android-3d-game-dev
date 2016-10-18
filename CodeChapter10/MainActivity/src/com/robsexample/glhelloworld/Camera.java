package com.robsexample.glhelloworld;

import android.content.Context;
import android.opengl.Matrix;


public class Camera 
{	
    private float[] m_ProjectionMatrix 	= new float[16];
    private float[] m_ViewMatrix 		= new float[16];
    
    // Viewing Frustrum
    private float m_Projleft 	=	0;
	private float m_Projright	=	0; 
	private float m_Projbottom	=	0; 
	private float m_Projtop		=	0;
	private float m_Projnear	=	0; 
	private	float m_Projfar		=	0;
    
	// Camera Location and Orientation
    private Vector3 m_Eye 	= new Vector3(0,0,0);
    private Vector3 m_Center= new Vector3(0,0,0);
    private Vector3 m_Up 	= new Vector3(0,0,0);
   
    private Orientation m_Orientation = null;
   

    Camera(Context context, 
    	   //Shader iShader,
    	   Vector3 Eye,	Vector3 Center, Vector3 Up,
    	   float Projleft, float Projright, 
    	   float Projbottom, float Projtop, 
    	   float Projnear, float Projfar)
    {	
    	m_Orientation = new Orientation(context);
    	
    	// Set Camera Projection
    	
    	SetCameraProjection(Projleft, 		
			         		Projright, 
			         		Projbottom, 
			         		Projtop, 
			         		Projnear, 
			         		Projfar);	
    	
    	/*
    	SetCameraProjectionOrtho(Projleft, 		
         		Projright, 
         		Projbottom, 
         		Projtop, 
         		Projnear, 
         		Projfar);	

    	*/
    	
  
    	// Set Orientation
    	m_Orientation.GetForward().Set(Center.x, Center.y, Center.z);
    	m_Orientation.GetUp().Set(Up.x, Up.y, Up.z);
    	m_Orientation.GetPosition().Set(Eye.x, Eye.y, Eye.z);
    	
    	// Calculate Right Local Vector
    	Vector3 CameraRight = Vector3.CrossProduct(Center, Up);
        CameraRight.Normalize();
        m_Orientation.SetRight(CameraRight);
    }
  
    Orientation GetOrientation()
    {
    	return m_Orientation;
    }
   
    /*
    void SetCameraProjectionOrtho(float Projleft, 		
		         float Projright, 
		         float Projbottom, 
		         float Projtop, 
		         float Projnear, 
		         float Projfar)
    {
    	m_Projleft	=	Projleft;
    	m_Projright	=	Projright; 	
    	m_Projbottom=	Projbottom; 
    	m_Projtop	=	Projtop;
    	m_Projnear	=	Projnear; 
    	m_Projfar	=	Projfar;
    	
    	//(m, mOffset, left, right, bottom, top, near, far)
    	Matrix.orthoM(m_ProjectionMatrix, 0, 
    				  m_Projleft, m_Projright, 
    				  m_Projbottom, m_Projtop, 
    				  m_Projnear, m_Projfar);
    }
    */
    
    
    void SetCameraProjection(float Projleft, 		
     	   			         float Projright, 
     	   			         float Projbottom, 
     	   			         float Projtop, 
     	   			         float Projnear, 
     	   			         float Projfar)
    {
    	m_Projleft	=	Projleft;
    	m_Projright	=	Projright; 
    	m_Projbottom=	Projbottom; 
    	m_Projtop	=	Projtop;
    	m_Projnear	=	Projnear; 
    	m_Projfar	=	Projfar;
        Matrix.frustumM(m_ProjectionMatrix, 0, 
        				m_Projleft, m_Projright, 
        				m_Projbottom, m_Projtop, 
        				m_Projnear, m_Projfar);
    }
    
    void SetCameraView(Vector3 Eye,	
     	   		       Vector3 Center,
     	   		       Vector3 Up)
    {
    	// public static void setLookAtM (float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ)	
        // Create Matrix
        Matrix.setLookAtM(m_ViewMatrix,0,
        				  Eye.x, Eye.y, Eye.z,
        				  Center.x, Center.y, Center.z,
        				  Up.x, Up.y, Up.z);
    }
	
    // Camera Vectors
    Vector3 GetCameraEye()
    {
    	return m_Eye;
    }
    
    Vector3 GetCameraLookAtCenter()
    {
    	return m_Center;
    }
    
    Vector3 GetCameraUp()
    {
    	return m_Up;
    }
    
    // Camera Frustrum
    float GetProjLeft()
    {
    	return m_Projleft;
    }
    
   	float GetProjRight()
   	{
   		return m_Projright; 
   	}
   	
   	float GetProjBottom()
   	{
   		return m_Projbottom; 
   	}
   	
   	float GetProjTop()
   	{
   		return m_Projtop;
   	}
   	
   	float GetProjNear()
   	{
   		return m_Projnear; 
   	}
   	
   	float GetProjFar()
   	{
   		return m_Projfar;		
   	}
   	
   	// Camera Dimensions
   	float GetCameraViewportWidth()
   	{
   		return (Math.abs(m_Projleft-m_Projright));
   	}
   	
   	float GetCameraViewportHeight()
   	{
   		return (Math.abs(m_Projtop - m_Projbottom));
   	}
   	
   	float GetCameraViewportDepth()
   	{
   		return (Math.abs(m_Projfar - m_Projnear));
   	}
   	
   	// Camera Matrices
    float[] GetProjectionMatrix()
    {
    	return m_ProjectionMatrix;
    }
    
    float[] GetViewMatrix()
    {
    	return m_ViewMatrix;
    }
    
    // Calculate Camera Vectors
    void CalculateLookAtVector()
    {
    	// LookatVector = m_Projfar * FowardCameraUnitVecWorldCoords
    	m_Center.Set(m_Orientation.GetForwardWorldCoords().x,
    				 m_Orientation.GetForwardWorldCoords().y, 
    				 m_Orientation.GetForwardWorldCoords().z);
    	
    	//m_Center.Multiply(m_Projfar);
        m_Center.Multiply(5);
    
        m_Center = Vector3.Add(m_Orientation.GetPosition(), m_Center);
    }
    
    void CalculateUpVector()
    {
    	m_Up.Set(m_Orientation.GetUpWorldCoords().x,
				 m_Orientation.GetUpWorldCoords().y, 
				 m_Orientation.GetUpWorldCoords().z);	
    }
    
    void CalculatePosition()
    {
    	m_Eye.Set(m_Orientation.GetPosition().x, 
    			  m_Orientation.GetPosition().y, 
    			  m_Orientation.GetPosition().z);
    }
   
    // Update Camera View
    void UpdateCamera()
    {
    	CalculateLookAtVector();
    	CalculateUpVector();
    	CalculatePosition();
    
    	SetCameraView(m_Eye, m_Center, m_Up);
    }
    
    
    
    void LoadCameraState(String Handle)
    {
    	m_Orientation.LoadState(Handle);
    }
    
    void SaveCameraState(String Handle)
    {	
		m_Orientation.SaveState(Handle);		
    }
   
}
