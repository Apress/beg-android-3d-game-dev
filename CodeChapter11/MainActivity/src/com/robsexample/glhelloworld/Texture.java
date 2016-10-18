package com.robsexample.glhelloworld;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

import android.content.Context;
import android.opengl.GLES10;

public class Texture 
{
	private Context m_Context;
	private int m_TextureId;
	Bitmap m_Bitmap;

	public Texture(Context context, int ResourceId)
	{
		// Create new Texture resource from ResourceId
		m_Context = context;
		InitTexture(ResourceId);
		
		// Setup Default Texture Parameters
    	SetTextureWRAP_MIN_FILTER(GLES20.GL_NEAREST);
    	SetTextureWRAP_MAG_FILTER(GLES20.GL_LINEAR);
    	SetTextureWRAP_S(GLES20.GL_CLAMP_TO_EDGE);
    	SetTextureWRAP_T(GLES20.GL_CLAMP_TO_EDGE);
	}
	
	Bitmap GetTextureBitMap()
	{
		return m_Bitmap;
	}
	
	void LoadTexture(int ResourceId)
	{
		 InputStream is = m_Context.getResources()
		            .openRawResource(ResourceId);
		      	      
		 try 
		 {
		      m_Bitmap = BitmapFactory.decodeStream(is);
		 }
		 finally
		 {
		      try 
		      {
		           is.close();
		      } 
		      catch(IOException e) 
		      {
		    	  Log.e("ERROR - Texture ERROR", "Error in LoadTexture()! ");    
		    	  // Ignore.
		      }
		 }	
	}
	
	boolean InitTexture(int ResourceId)
	{		
		/*
		 * Android 
		 * public static void glGenTextures (int n, int[] textures, int offset)
		 * Returns n currently unused names for texture objects in the array textures. 
		 */
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
	
		/*
		 * Android 
		 * public static void glBindTexture (int target, int texture)
		 */
		m_TextureId = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_TextureId);
	
		// Loads in Texture from Resource File
		LoadTexture(ResourceId);
	
		/*
		 * Android 
		 * static void	 texImage2D(int target, int level, Bitmap bitmap, int border)
		 * A version of texImage2D that determines the internalFormat and type automatically.
		 */
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, m_Bitmap, 0);
	
		return true;
	}

	void ActivateTexture()
	{
		// Activate Texture
		if (m_TextureId != 0)
		{
			GLES20.glBindTexture (GLES20.GL_TEXTURE_2D, m_TextureId);	
			//Log.d("DEBUG - Texture", "Texture Activated! ");
		}
		else 
		{
			Log.e("ERROR - Texture ERROR- m_TextureId = 0", "Error in ActivateTexture()! ");
		}
	}
	
	public void CheckGLError(String glOperation) 
	{
		int error;
	    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) 
	    {
	    	Log.e("class Texture :", glOperation + " IN CHECKGLERROR() : glError " + GLU.gluErrorString(error));
	        throw new RuntimeException(glOperation + ": glError " + error);
	    }
	}
	
	void SetTextureWRAP_S(int value)
	{
		GLES20.glTexParameteri (GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,value);
	}

	void SetTextureWRAP_T(int value)
	{
		GLES20.glTexParameteri (GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,value);
	}

	void SetTextureWRAP_MAG_FILTER(int value)
	{
		GLES20.glTexParameteri (GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,value);
	}

	void SetTextureWRAP_MIN_FILTER(int value)
	{
		GLES20.glTexParameteri (GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, value);
	}

	static void SetActiveTextureUnit(int UnitNumber)
	{		
		//UnitNumber = GL_TEXTURE0 - GL_TEXTURE31
		GLES20.glActiveTexture(UnitNumber);
	}
	
	/*
	void SetTextureEnvMODE(float value)
	{
		//void glTexEnv{if}(GLenum target, GLenum pname, TYPEparam);
		//void glTexEnv{if}v(GLenum target, GLenum pname, TYPE *param);
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//Sets the current texturing function. target must be GL_TEXTURE_ENV. If pname is GL_TEXTURE_ENV_MODE, param can be
		//GL_DECAL, GL_REPLACE, GL_MODULATE, or GL_BLEND, to specify how texture values are to be combined with the color
		//values of the fragment being processed. If pname is GL_TEXTURE_ENV_COLOR, param is an array of four floating-point values
		//representing R, G, B, and A components. These values are used only if the GL_BLEND texture function has been specified 
		//as well.
		//static void	 glTexEnvf(int target, int pname, float param)
		GLES10.glTexEnvf (GLES10.GL_TEXTURE_ENV, GLES10.GL_TEXTURE_ENV_MODE, value);
	}
	*/
	
	
	
	
	
	
	
	
	
	// BILLBOARD
	
	void CopySubTextureToTexture(int Level, int XOffset, int YOffset, Bitmap BitmapImage)
	{
		// Copies the texture in BitmapImage to the bitmap associated with this Texture object
		/*
		public static void texSubImage2D (int target, int level, int xoffset, int yoffset, Bitmap bitmap)

		Added in API level 1
		Calls glTexSubImage2D() on the current OpenGL context. If no context is current the 
		behavior is the same as calling glTexSubImage2D() with no current context, that is, eglGetError() 
		will return the appropriate error. Unlike glTexSubImage2D() bitmap cannot be null and will raise 
		an exception in that case. All other parameters are identical to those used for glTexSubImage2D(). 
				
				NOTE: this method doesn't change GL_UNPACK_ALIGNMENT, you must make sure to set it properly 
				according to the supplied bitmap. Whether or not bitmap can have non power of two dimensions 
				depends on the current OpenGL context. Always check glGetError() some time after calling 
				this method, just like when using OpenGL directly.
		*/
		
		ActivateTexture();
		GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, Level, XOffset, YOffset, BitmapImage);
		CheckGLError("GLUtils.texSubImage2D"); 
	}
	
	
	
	
	
	
	
	
	
}


