package com.robsexample.glhelloworld;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.InputStream;
import java.nio.IntBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;


public class Shader {

	private Context m_Context;
	private int	m_FragmentShader;
	private int	m_VertexShader;	
	private int	m_ShaderProgram;
		
	
	/*
	int GetShaderProgram()
	{
		return m_ShaderProgram;
	}
	*/
	
	
	StringBuffer ReadInShader(int ResourceId)
	{
		StringBuffer TempBuffer = new StringBuffer();
		
		InputStream inputStream = m_Context.getResources().openRawResource(ResourceId);
		
		/*
		 InputStreamReader
		 
		A class for turning a byte stream into a character stream. Data read from the source input stream 
		is converted into characters by either a default or a provided character converter. The default encoding
		is taken from the "file.encoding" system property. InputStreamReader contains a buffer of bytes read from
		the source stream and converts these into characters as needed. The buffer size is 8K.
		
		InputStreamReader(InputStream in)
		Constructs a new InputStreamReader on the InputStream in.
		*/
		
		/*
        Wraps an existing Reader and buffers the input. Expensive interaction with the underlying reader is 
        minimized, since most (smaller) requests can be satisfied by accessing the buffer alone. The drawback 
        is that some extra space is required to hold the buffer and that copying takes place when filling that 
        buffer, but this is usually outweighed by the performance benefits.

        A typical application pattern for the class looks like this:

        BufferedReader buf = new BufferedReader(new FileReader("file.java"));
        
        BufferedReader(Reader in)
        Constructs a new BufferedReader, providing in with a buffer of 8192 characters.
	    */
		
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        	
		try {
		     String read = in.readLine();
		     while (read != null) {
			     TempBuffer.append(read + "\n");
			     read = in.readLine();
		     }
		} catch (Exception e) {
			 //Log.e(String tag, String msg, Throwable tr)
			 //Send a ERROR log message and log the exception.
			 Log.e("ERROR - SHADER READ ERROR", "Error in ReadInShader(): " + e.getLocalizedMessage());
		} 
		
		
		//d(String tag, String msg)
		//Send a DEBUG log message.
		//Log.d("DEBUG - SHADER SOURCE: ", in.toString());
		
		
		//ShaderBuffer.deleteCharAt(vs.length() - 1);
		return TempBuffer;
	}
	
	void InitFragmentShader(int ResourceId)
	{
		StringBuffer tempBuffer = ReadInShader(ResourceId);
		
		m_FragmentShader= GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		GLES20.glShaderSource(m_FragmentShader,tempBuffer.toString());
		GLES20.glCompileShader(m_FragmentShader);
		
		//public static void glGetShaderiv (int shader, int pname, IntBuffer params)
		IntBuffer CompileErrorStatus = IntBuffer.allocate(1);
		GLES20.glGetShaderiv(m_FragmentShader, GLES20.GL_COMPILE_STATUS, CompileErrorStatus);
		if (CompileErrorStatus.get(0) == 0) {
			 Log.e("ERROR - FRAGMENT SHADER ", "Could not compile Fragment shader file =  " + String.valueOf(ResourceId));
			 Log.e("ERROR - FRAGMENT SHADER ", GLES20.glGetShaderInfoLog(m_FragmentShader));
			 GLES20.glDeleteShader(m_FragmentShader);
			 m_FragmentShader = 0;
		}
		else
		{
		     GLES20.glAttachShader(m_ShaderProgram,m_FragmentShader);
		     Log.d("DEBUG - FRAGMENT SHADER ATTACHED ", "In InitFragmentShader()");
		}
	}
	
	void InitVertexShader(int ResourceId)
	{
        StringBuffer tempBuffer = ReadInShader(ResourceId);
		
        m_VertexShader= GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		GLES20.glShaderSource(m_VertexShader,tempBuffer.toString());
		GLES20.glCompileShader(m_VertexShader);
		
		//public static void glGetShaderiv (int shader, int pname, IntBuffer params)
		IntBuffer CompileErrorStatus = IntBuffer.allocate(1);
		GLES20.glGetShaderiv(m_VertexShader, GLES20.GL_COMPILE_STATUS, CompileErrorStatus);
		if (CompileErrorStatus.get(0) == 0) {
			 Log.e("ERROR - VERTEX SHADER ", "Could not compile Vertex shader!! " + String.valueOf(ResourceId));
			 Log.e("ERROR - VERTEX SHADER ", GLES20.glGetShaderInfoLog(m_VertexShader));
			 GLES20.glDeleteShader(m_VertexShader);
			 m_VertexShader = 0;
		}
		else
		{
		     GLES20.glAttachShader(m_ShaderProgram,m_VertexShader);
		     Log.d("DEBUG - VERTEX SHADER ATTACHED ", "In InitVertexShader()");
		}
	}
	
	void InitShaderProgram(int VSResourceId, int FSResourceId)
	{
		m_ShaderProgram = GLES20.glCreateProgram();
	   
		InitVertexShader(VSResourceId);
		InitFragmentShader(FSResourceId);
		GLES20.glLinkProgram(m_ShaderProgram);
	
		String DebugInfo = GLES20.glGetProgramInfoLog(m_ShaderProgram);
		Log.d("DEBUG - SHADER LINK INFO ", DebugInfo);
	
		//String	ShaderInfo =  GLES20.glGetShaderInfoLog(m_ShaderProgram);
		//Log.d("DEBUG - SHADER COMPILE INFO ", ShaderInfo);
		
		  
		// Check for Link errors
		/*
		IntBuffer ErrorStatus = IntBuffer.allocate(1);
		GLES20.glGetShaderiv(m_VertexShader, GLES20.GL_COMPILE_STATUS, ErrorStatus);
		if (ErrorStatus.get(0) == 0) {
			 Log.e("ERROR - VERTEX SHADER ", "Could not compile Vertex shader!! ");
			 Log.e("ERROR - VERTEX SHADER ", GLES20.glGetShaderInfoLog(m_VertexShader));
			 GLES20.glDeleteShader(m_VertexShader);
			 m_VertexShader = 0;
		}
		else
		{
		     GLES20.glAttachShader(m_ShaderProgram,m_VertexShader);
		     Log.d("DEBUG - VERTEX SHADER ATTACHED ", "In InitVertexShader()");
		}
		*/
		
	}

	public Shader(Context context, int VSResourceId, int FSResourceId)
    {
	     // Shader Variables
	     m_FragmentShader	=	0;
	     m_VertexShader		=	0;
	     m_ShaderProgram	=	0;
	     
	     m_Context = context;
	     InitShaderProgram(VSResourceId, FSResourceId);
    }


    void ActivateShader()
    {
	     GLES20.glUseProgram(m_ShaderProgram);
    }

    void DeActivateShader()
    {
    	// Revert back to Fixed Function Rendering Pipeline
    	GLES20.glUseProgram(0);
    }

	
	
	
	
	/*
    int GetShaderUniformVariableLocation(String variable)
    {
	     return (GLES20.glGetUniformLocation(m_ShaderProgram,variable));	
    }
    */
	
   
	
    int GetShaderVertexAttributeVariableLocation(String variable)
    {
    	return (GLES20.glGetAttribLocation(m_ShaderProgram, variable));
    }
    
    void SetShaderUniformVariableValue(String variable, float value)
    {
	     int loc = GLES20.glGetUniformLocation(m_ShaderProgram,variable);

	    // Log.d("SHADER " , "SetShaderUniformVariableValue loc value = " + loc);
	     GLES20.glUniform1f(loc, value);
    }
    
    void SetShaderUniformVariableValue(String variable, Vector3 value)
    {
    	int loc = GLES20.glGetUniformLocation(m_ShaderProgram,variable);

    	GLES20.glUniform3f(loc, value.x, value.y, value.z);
    }
    
    void SetShaderUniformVariableValue(String variable, float[] value)
    {
    	int loc = GLES20.glGetUniformLocation(m_ShaderProgram,variable);

    	GLES20.glUniform3f(loc, value[0], value[1], value[2]);
    }
    
    void SetShaderVariableValueFloatMatrix4Array(String variable, int count, boolean transpose, float[] value, int offset)
    {
    	int loc = GLES20.glGetUniformLocation(m_ShaderProgram,variable);
    	//public static void glUniformMatrix4fv (int location, int count, boolean transpose, float[] value, int offset)
    	GLES20.glUniformMatrix4fv (loc, count, transpose, value, offset);
    }
    
    
    // For Drone Grid 
    
    
    void SetShaderUniformVariableValueInt(String variable, int value)
    {
	     int loc = GLES20.glGetUniformLocation(m_ShaderProgram,variable);

	     if (loc < 0)
	     {
	    	// Log.e("SHADER ERROR - LOC", "Loc = " + loc);
	     }
	     else
	     {
	    	// Log.d("SHADER " , "SetShaderUniformVariableValueInt loc value = " + loc);
	     }
	     
	     GLES20.glUniform1i(loc, value);
    }
    
    
    
    
    void SetShaderVariableValueFloatVector1Array(String variable, int count, float[] value, int offset)
    {
    	int loc = GLES20.glGetUniformLocation(m_ShaderProgram,variable);
	
    	// Android GLES20 declaration
    	//public static void glUniform1fv (int location, int count, float[] v, int offset)
    	GLES20.glUniform1fv(loc, count, value, offset);
    }
    
    
    
    

    void SetShaderVariableValueFloatVector3Array(String variable, int count, float[] value, int offset)
    {
    	int loc = GLES20.glGetUniformLocation(m_ShaderProgram,variable);

    	// Android GLES20 declaration
    	// public static void glUniform3fv (int location, int count, float[] v, int offset)
    	GLES20.glUniform3fv(loc, count, value, offset);
    }
    
    
    
    
    /*
    
    void SetShaderVariableValueFloatMatrix3Array(String variable, int count, boolean transpose, float[] value, int offset)
    {
    	int loc = GLES20.glGetUniformLocation(m_ShaderProgram,variable);
    	//public static void glUniformMatrix4fv (int location, int count, boolean transpose, float[] value, int offset)
    	GLES20.glUniformMatrix3fv (loc, count, transpose, value, offset);
    }
    
    */
    
    
    
}
