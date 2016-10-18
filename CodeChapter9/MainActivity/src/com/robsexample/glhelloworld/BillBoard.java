package com.robsexample.glhelloworld;




import android.content.Context;
import android.util.Log;


public class BillBoard extends Cube
{
	BillBoard(Context iContext, 
			 Mesh iMesh, 
			 MeshEx iMeshEx, 
			 Texture[] iTextures, 
			 Material iMaterial, 
			 Shader iShader 
			 //Shader LocalAxisShader
			 )
	{
		 super(iContext, 
			   iMesh, 
			   iMeshEx, 
			   iTextures, 
			   iMaterial, 
			   iShader 
			   //LocalAxisShader
			   );
	
		 Vector3 Scale = new Vector3(1.0f,1.0f,0.1f);
		 m_Orientation.SetScale(Scale);
		 
	}
	
	void SetBillBoardTowardCamera(Camera Cam)
	{
		// 1. Get Front Vector of Billboard Object projected on xz plane
		Vector3 ForwardVecProj = new Vector3(m_Orientation.GetForwardWorldCoords().x, 
										     0, 
										     m_Orientation.GetForwardWorldCoords().z);
		
		// 2. Get The BillBoard Position projected on xz plane
		Vector3 BillBoardPositionProj = new Vector3(m_Orientation.GetPosition().x, 
												    0,
												    m_Orientation.GetPosition().z);
		
		// 3. Get Position of Camera on 2d XZ Plane
		Vector3 CameraPositionProj = new Vector3(Cam.GetCameraEye().x, 0, Cam.GetCameraEye().z);

		// 4. Calculate Vector from Billboard to Camera
		Vector3 Bill2CameraVecProj = Vector3.Subtract(CameraPositionProj , BillBoardPositionProj);
		Bill2CameraVecProj.Normalize();

		// 5. Find Angle between forward of Billboard object and camera
	
		// P = forwardxy
		// Q = Vec_Bill_Camera
		// P and Q are normalized Vectors
		// P.Q = P*Q*cos(theta)
		// P.Q/P*Q = cos(theta)
		// acos(P.Q/P*Q) = theta;

		// P.Q > 0 then angle between vectors is less than 90 deg
		// P.Q < 0 then angle between vectors is greater than 90 deg.
		// P.Q = 0 then angle between vector is exactly 90 degs.
		
		// Get current theta
		// returns 0-PI radians
		float Theta = (float)Math.acos(ForwardVecProj.DotProduct(Bill2CameraVecProj));
		float DegreeTheta = Theta * 180.0f/Physics.PI;
		
				
		// 6. Cross Product to form rotation axis
		Vector3 RotAxis = Vector3.CrossProduct(ForwardVecProj, Bill2CameraVecProj);
		
		
		// 7. Rotate BillBoard Toward Camera
		// cos in radians
		if ((Math.cos(Theta) < 0.9999) && (Math.cos(Theta) > -0.9999))
		{
			m_Orientation.SetRotationAxis(RotAxis);	
			m_Orientation.AddRotation(DegreeTheta);
		
			//Log.e("ROTATION BILLBOARD ", "ForwardVecProj = " + ForwardVecProj.GetVectorString()+ ", " +
			//		"Bill2CameraVecProj = " + Bill2CameraVecProj.GetVectorString() + ", " +
			//		"Rotation Axis = " + RotAxis.GetVectorString() + "," + 
			//		"Theta = " + Theta);
		} 
		else
		{
			//Log.e( "BILLBOARD", "No Cylindrical Rotation!! , Theta = " + Theta);
		}
		
		
	
		/*
		
		// Spherical BillBoarding
		Vector3 RightProj = m_Orientation.GetRightWorldCoords();
		//RightProj.y = 0;
		
		//Log.e("BILLBOARD " , "RIGHT WORLD COORDS = " + Right.GetVectorString());
		
		Vector3 Bill2Camera = new Vector3(Cam.GetCameraEye().x - m_Orientation.GetPosition().x,
										  Cam.GetCameraEye().y - 0,
										  Cam.GetCameraEye().z - m_Orientation.GetPosition().z);
		Bill2Camera.Normalize();
		
		Vector3 Bill2CameraProj = new Vector3(Cam.GetCameraEye().x - m_Orientation.GetPosition().x,
				  							  0,
				  							  Cam.GetCameraEye().z - m_Orientation.GetPosition().z);
		Bill2CameraProj.Normalize();
		
		
		Theta = (float)Math.acos(Bill2Camera.DotProduct(Bill2CameraProj));
		DegreeTheta = Theta * 180.0f/Physics.PI;
		
		// 7. Rotate BillBoard Toward Camera
		// cos in radians
		//if ((angleCosine < 0.99990) && (angleCosine > -0.9999))
		//	if (objToCam[1] < 0)
		//		glRotatef(acos(angleCosine)*180/3.14,1,0,0);	
		//	else
		//		glRotatef(acos(angleCosine)*180/3.14,-1,0,0);	
		
		
		
		if ((Math.cos(Theta) < 0.9999) && (Math.cos(Theta) > -0.9999))
		{
			m_Orientation.SetRotationAxis(RightProj);
			
			if (Bill2Camera.y > 0)
			{
				m_Orientation.AddRotation(Theta);
			}
			else
			{
				m_Orientation.AddRotation(-Theta);
			}
			//Log.e("ROTATION BILLBOARD ", "ForwardVecProj = " + ForwardVecProj.GetVectorString()+ ", " +
			//		"Bill2CameraVecProj = " + Bill2CameraVecProj.GetVectorString() + ", " +
			//		"Rotation Axis = " + RotAxis.GetVectorString() + "," + 
			//		"Theta = " + Theta);
		} 
		*/
		
		
	}

	void UpdateObject3d(Camera Cam)
	{
		super.UpdateObject3d();
		SetBillBoardTowardCamera(Cam);
	}
	
	
}
