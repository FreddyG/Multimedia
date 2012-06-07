/*
 * What?
 */

package assignment1.android;


import java.io.IOException;
import java.util.List;

import uvamult.assignment1.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import assignment1.DrawCamera;


/**
 * Your face. Or something. Whatever...
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    /** Handle to the application context, used to e.g. fetch Drawables. */
    private Context mContext;
    private SurfaceHolder mHolder;
    private Activity activity;
    
    private SurfaceView cameraShow;
    
    //This variable is responsible for getting and setting the camera settings  
    private Parameters parameters;  
    //this variable stores the camera preview size  
    private Size previewSize;
    
    private int creationPhase = 0;
    
    int framerate = 10;
    
    private LinearLayout buttonBar;
    
	private DrawCamera drawControl;
    
    /*
     * Camera get
     */
    private Camera camera;
    
    public Camera getCamera() {
    	return camera;
    }
    
    public void openCamera() {
    	camera = Camera.open();
    }

    
    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mContext = context;

        // register our interest in hearing about changes to our surface
        mHolder = getHolder();
        mHolder.addCallback(this);
        
        setFocusable(true); // make sure we get key events (unneeded?)
    	setKeepScreenOn(true); // Prevent phone from sleeping
    }

    
    /* Key events are removed. Screen interaction is the dealio now. */

    
    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    	
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
    	setKeepScreenOn(true);
		if (++creationPhase == 2) finalInitialize();
    }
    	

    private void finalInitialize() {
    	
		// Create our DrawCamera object
    	drawControl = new DrawCamera();
    	drawControl.setup(this);
    	
    	try {

    		openCamera();

            ///initialize the variables  
            parameters = camera.getParameters();   
    		camera.setPreviewDisplay(cameraShow.getHolder());
    		parameters.setPreviewFrameRate(10); // Not all phones listen to this
    		List<Size> sizes = parameters.getSupportedPreviewSizes();
    		
    		// Find the preview resolution closest to our canvas width
    		int goalh = getWidth();
    		int goalw = getHeight();
    		float lowestDistance = Float.MAX_VALUE; 
    		// Grab the one closest to our preview size
    		Size current = sizes.get(0);
    		for (Size c : sizes) {
    			float distance = (float)Math.sqrt((c.width-goalw)*(c.width-goalw)+(c.height-goalh)*(c.height-goalh));
    			if (distance < lowestDistance) {
    				lowestDistance = distance;
    				current = c;
    			}
    		}
    		
    		
    		
    		parameters.setPreviewSize(current.width, current.height);
    		
    		camera.setParameters(parameters);
            previewSize = camera.getParameters().getPreviewSize(); 
            Log.d("preview","preview w = " + previewSize.width + " h = " + previewSize.height);
    		camera.setDisplayOrientation(90);
    		camera.setPreviewCallback(new PreviewFramer());

    		camera.startPreview();
    		//setVisibility(INVISIBLE);
    	} catch (RuntimeException e) {
    		// Camera does not exist?
    		Log.e("CameraView", "Camera not available?", e);
    	} catch (IOException e) {
    		Log.e("CameraView", "Can't start preview", e);
    	}
	}


	/*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
    	
    }
    
    
    // Will probably be used eventually
    public void addButton(String text, View.OnClickListener action) {
    	Button make = new Button(mContext);
    	//make.setHeight(90);
    	make.setText(text);
    	make.setOnClickListener(action);
    	buttonBar.addView(make);
    }
    
    
	public void setActivity(CameraActivity cameraTest) {
		
		// TODO Auto-generated method stub
		activity = cameraTest;
		
		buttonBar = (LinearLayout)activity.findViewById(R.id.controlbar);
		
        cameraShow = (SurfaceView) activity.findViewById(R.id.surfaceView1);
        cameraShow.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraShow.getHolder().addCallback(new CameraSurfaceCallBack());
        
	}
    
	
	class CameraSurfaceCallBack implements SurfaceHolder.Callback {
		
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			// Nothing
		}

		public void surfaceCreated(SurfaceHolder holder) {
			if (++creationPhase == 2) finalInitialize();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {
				camera.setPreviewCallback(null);
				camera.stopPreview();
	        	camera.release();
	        	camera = null;
			}
		}
		
	}
	
	class PreviewFramer implements Camera.PreviewCallback {
		
		public void onPreviewFrame(byte[] data, Camera camera) {
	        //Log.d("Camera", "Got a camera frame");

	        Canvas c = null;

	        if(mHolder == null) {//mHolder == null){
	            return;
	        }

        	if (creationPhase == 2) {
        		try {
        			// First grab and process the camera data using DrawCamera
        			drawControl.imageSize = previewSize;//camera.getParameters().getPreviewSize();
        			drawControl.imageReceived(data);
        			
        			synchronized (mHolder) {
        				c = mHolder.lockCanvas(null);
        				drawControl.draw(c);
        			}

        		} finally {
        			// do this in a finally so that if an exception is thrown
        			// during the above, we don't leave the Surface in an
        			// inconsistent state
        			if (c != null) {
        				mHolder.unlockCanvasAndPost(c);
        			}
        		}
        	}
	    }
		
	}
	
}
