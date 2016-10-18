package com.robsexample.glhelloworld;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MotionEvent;

import android.opengl.GLSurfaceView;
import android.content.Context;

public class MainActivity extends Activity {
	
	private MyGLSurfaceView m_GLView;
	
	static String SAVE_GAME_HANDLE = "CurrentGame";
	

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

    	// Create a MyGLSurfaceView instance and set it
    	// as the ContentView for this Activity
    	m_GLView = new MyGLSurfaceView(this);
    	setContentView(m_GLView); 
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
    	m_GLView.onPause();
    	
    	// Save State
    	//m_GLView.CustomGLRenderer.SaveGameState();
    	m_GLView.CustomGLRenderer.SaveGameState(SAVE_GAME_HANDLE);
    	
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
    	m_GLView.onResume();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
	

}

/////////////////////////////////////////////////////////////////////////////////////////

class MyGLSurfaceView extends GLSurfaceView 
{
	public MyGLRenderer CustomGLRenderer = null;
	
	private float m_PreviousX = 0;
	private float m_PreviousY = 0;
	    
	private float m_dx = 0;
	private float m_dy = 0;
	    
	private float m_Startx = 0;
	private float m_Starty = 0;
	    

    public MyGLSurfaceView(Context context) 
    {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        //setRenderer(new MyGLRenderer(context));
        
        CustomGLRenderer = new MyGLRenderer(context);
        setRenderer(CustomGLRenderer);
        
        
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();
        
        switch (e.getAction()) 
        {
        	case MotionEvent.ACTION_DOWN:
        		m_Startx = x;
        		m_Starty = y;
        	break;
        	
        	case MotionEvent.ACTION_UP:
        		CustomGLRenderer.ProcessTouch(m_Startx, m_Starty, x, y);
        		break;
        
            case MotionEvent.ACTION_MOVE:
                m_dx = x - m_PreviousX;
                m_dy = y - m_PreviousY;

                CustomGLRenderer.CameraMoved(m_dy, m_dx); 
                break;
        }

        m_PreviousX = x;
        m_PreviousY = y;
        return true;
    }
    
    
    
    
    
    
    
}



