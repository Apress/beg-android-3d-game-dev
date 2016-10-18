package com.robsexample.glhelloworld;

import android.content.Context;



public class MenuItem extends BillBoard
{
	MenuItem(Context iContext, 
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
	}

	
	 float[] GetObject3dWindowCoords(int ViewPortWidth,
				 					 int ViewPortHeight,
				 					 Vector3 ObjOffset)
	 {
		 float[] WindowCoords;
		 int[] View = new int[4];

		 View[0] = 0;
		 View[1] = 0;
		 View[2] = ViewPortWidth;
		 View[3] = ViewPortHeight;

		 WindowCoords = MapObjectCoordsToWindowCoords(View, 0, ObjOffset);

		 // Flip Y starting point so that 0 is at top of window
		 WindowCoords[1] = ViewPortHeight - WindowCoords[1];

		 //Log.d("DEBUG - In ProcessTouch() - VIEWPORT WIDTH,HEIGHT - ", ": " + View[2] + ", " + View[3]);
		 //Log.d("DEBUG - In ProcessTouch() - Object Window Coords (x,y,z) = ", WindowCoords[0] + ", " + WindowCoords[1] + "," + WindowCoords[2]);

		 return WindowCoords;
	 }	 

	 boolean Touched(float TouchX, float TouchY,
			 		 int ViewPortWidth,
			 	     int ViewPortHeight)
	 {
		 boolean result = false;

		 float Radius = GetRadius();
		 Vector3 ObjCoordsUpperLeft  = new Vector3(-Radius,  Radius, 0);
		 Vector3 ObjCoordsUpperRight = new Vector3( Radius,  Radius, 0);
		 Vector3 ObjCoordsLowerLeft  = new Vector3(-Radius, -Radius, 0);

		 float[] UpperLeft  = GetObject3dWindowCoords(ViewPortWidth, ViewPortHeight, ObjCoordsUpperLeft);
		 float[] UpperRight = GetObject3dWindowCoords(ViewPortWidth, ViewPortHeight, ObjCoordsUpperRight);
		 float[] LowerLeft  = GetObject3dWindowCoords(ViewPortWidth, ViewPortHeight, ObjCoordsLowerLeft);

		 if ((TouchX >= UpperLeft[0]) && (TouchX <= UpperRight[0]) &&
		     (TouchY >= UpperLeft[1]) && (TouchY <= LowerLeft[1]))
		 {
			 result = true;
		 }

		 return result;
	 } 
}
