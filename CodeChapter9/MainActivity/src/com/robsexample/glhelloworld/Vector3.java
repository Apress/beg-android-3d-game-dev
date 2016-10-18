package com.robsexample.glhelloworld;

import android.util.FloatMath;
import java.lang.Math;
import java.util.Random;



class Vector3
{
	 public float x;
     public float y;
     public float z;
    
     // Constructors
     public Vector3(float _x, float _y, float _z)
     {
    	 x = _x;
    	 y = _y;
    	 z = _z;
     }
     
     // Constructor taking another Vector3 object as input
     public Vector3(Vector3 src)
     {
    	 x = src.x;
    	 y = src.y;
    	 z = src.z;
     }
     
     // Conversion
     float[] AsFloatArray()
     {
    	 float[] farray = new float[3];
    	 
    	 farray[0] = x;
    	 farray[1] = y;
    	 farray[2] = z;
    	 
    	 return farray;
     }
      
      //////////////////// Vector Operators ////////////////////////////////////
    
      void Multiply(float v)
      {
    	  x *= v;
    	  y *= v;
    	  z *= v;
      }
      
      void Add (Vector3 vec)
      {
    	  x = x + vec.x;
    	  y = y + vec.y;
    	  z = z + vec.z;
      }
      
      void Subtract(Vector3 vec)
      {
    	  x = x - vec.x;
    	  y = y - vec.y;
    	  z = z - vec.z;
      }
       
      void Divide (Vector3 vec)
      {
    	  //debug_assert(vec.x != 0.0f, "divide by zero error");
    	  //debug_assert(vec.y != 0.0f, "divide by zero error");
    	  //debug_assert(vec.z != 0.0f, "divide by zero error");

    	  x /= vec.x;
    	  y /= vec.y;
    	  z /= vec.z;
      }

      void Divide (float v)
      {
    	  //debug_assert(v != 0.0f, "divide by zero error");

    	  x /= v;
    	  y /= v;
    	  z /= v;
      }
      
      ///////////// Static Vector Operations /////////////////////////////////// 
   
      static Vector3 Add(Vector3 vec1, Vector3 vec2)
      {
    	  Vector3 result = new Vector3(0,0,0);
    	  
    	  result.x = vec1.x + vec2.x;
    	  result.y = vec1.y + vec2.y;
    	  result.z = vec1.z + vec2.z;
    	  
    	  return result;
      }
     
      static Vector3 Subtract(Vector3 vec1 , Vector3 vec2)
      {
    	  Vector3 result = new Vector3(1,0,0);
    	  
    	  result.x = vec1.x - vec2.x;
    	  result.y = vec1.y - vec2.y;
    	  result.z = vec1.z - vec2.z;
    	  
    	  return result;
      }
      
      static Vector3 Multiply(float scalar, Vector3 vec1)
      {
    	  Vector3 result = new Vector3(0,0,0);
    	  
    	  result.x = scalar * vec1.x;
    	  result.y = scalar * vec1.y;
    	  result.z = scalar * vec1.z;
    	  
    	  return result;
      }
      
      /////////////////////////////////////////////////////////////////////////
      
     void Set(float _x, float _y, float _z)
     {
	      x = _x;
	      y = _y;
	      z = _z;
     }
      
     void Normalize()
     {
	      float l = Length();

	      x = x/l;
	      y = y/l;
	      z = z/l;
     }

     float Length()
     {
	     return FloatMath.sqrt(x*x + y*y + z*z);
    	 //return java.lang.Math.sqrt(x*x + y*y + z*z);
     }

     static Vector3 CrossProduct(Vector3 a, Vector3 b)
     {
    	 Vector3 result = new Vector3(0,0,0);
    	 
    	 result.x= (a.y*b.z) - (a.z*b.y);
    	 result.y= (a.z*b.x) - (a.x*b.z);
    	 result.z= (a.x*b.y) - (a.y*b.x);
    	 
    	 return result;
     }
     
     // dot product
     float DotProduct(Vector3 vec)
     {
	     return (x * vec.x) + (y * vec.y) + (z * vec.z);
     }
     
      
     void Negate()
     {
	      x = -x;
	      y = -y;
	      z = -z;
     }
     
     // Additional Functions
     void Clear()
     {
	      x = 0.0f;
	      y = 0.0f;
	      z = 0.0f;
     }
     
     void GenerateRandomVector(float MinValue, float MaxValue)
     {
	      Random tempRandom = new Random();
	
	      // 1. Generate Random Vector Range MinValue - MaxValue
	      x = MinValue + ((MaxValue-MinValue) * tempRandom.nextFloat());
	      y = MinValue + ((MaxValue-MinValue) * tempRandom.nextFloat());
	      z = MinValue + ((MaxValue-MinValue) * tempRandom.nextFloat());
     }	
     
     //////////////////////
     String GetVectorString()
     {
   	  return ("(" + x + "," + y + "," + z + ")");
     }
     
       
}
