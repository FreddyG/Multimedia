package assignment1;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.View;
import assignment1.android.CameraView;

public class DrawCamera {
	
	/*
	 * This class is the only file that needs changes to show
	 * the histogram of the green values.
	 */
	
	public int[] rgb;			// the array of integers
	public Size imageSize;
	public Paint p;
	
	
	public void imageReceived(byte[] data) {
		// Allocate the image as an array of integers if needed.
		// Then, decode the raw image data in YUV420SP format into a red-green-blue array (rgb array)
		// Note that per pixel the RGB values are packed into an integer. See the methods r(), g() and b().
		
		if (rgb == null) rgb = new int[imageSize.width*imageSize.height];
		decodeYUV420SP(rgb, data);
		
		// below you should calculate the histogram of the image
		// the histogram should be class member so you can access 
		// it in other methods of this class.
		// ....
	}
	
	private void axisDraw(Canvas c, float len) {
		// utility function to draw the coordinate axis in a canvas with length 'len'
		// the x axis is drawn in red, the y axis in green
		p.setColor(combine(255,0,0));
		c.drawLine(0f, 0f, len, 0f, p);
		p.setColor(combine(0,255,0));
		c.drawLine(0f, 0f, 0f, len, p);
		
	}
	
	public void draw(Canvas c) {
		int w = c.getWidth();
		int h = c.getHeight();
		
		// here you should draw the histogram. The number of bins should be user selectable.
		// note that at this point the canvas origin is in the top left corner of the surface 
		// just below the preview window.
		
		// you could translate translate an reflect the coordinate system to make
		// into a standard coordinate system.
		
		// please not that the canvas height is larger then what can be seen on the screen. 
		// For hints/tips why that is the case..... please mail Rein vd Boomgaard
		
		// instead of drawing the histogram below we draw the origin, put some text in it
		// and draw a line.
		
		axisDraw(c,100f);
		c.drawColor(Color.GRAY);
		p.setColor(combine(255, 0, 0));
		c.drawText("canvas width = "+c.getWidth(), 15, 45, p);
		c.drawText("canvas heightt = "+c.getHeight(), 15, 60, p);
		
		c.drawLine(0.0f,0.0f,(float)c.getWidth()-5, c.getHeight(),p);
	
		Log.d("DEBUG", "canvas w = " + c.getWidth() + " h = " + c.getHeight()); // this writes to the LogCat that can be read on your PC in
																				// the phone is connected
	}
	
	/*
	 * Setup your environment
	 */
	public void setup(CameraView view) {
		// Example: add a button to the bottom bar
		view.addButton("Debug Message", new View.OnClickListener() {
			public void onClick(View arg) {
				// Using Log, you can print debug messages to Eclipse
				Log.d("DrawCamera", "Debug message");
			}
		});
		
		// You can add your own buttons here
		
	}
	
	
	
	/*
	 * Below are some convenience methods,
	 * like grabbing colors and decoding.
	 */
    
	// Extract the red element from the given color
    private int r(int rgb) {
    	return (rgb & 0xff0000) >> 16;
    }

	// Extract the green element from the given color
    private int g(int rgb) {
    	return (rgb & 0x00ff00) >> 8;
    }

	// Extract the blue element from the given color
    private int b(int rgb) {
    	return (rgb & 0x0000ff);
    }
    
    // Combine red, green and blue into a single color int
    private int combine(int r, int g, int b) {
    	 return 0xff000000 | (r << 16) | (g << 8) | b;
    }
    
    
    /*
     * Draw the rgb image onto the canvas
     */
    private void drawImage(Canvas c) {
    	c.save();
    	
		//c.scale(c.getWidth(), c.getHeight()); // Note: turn to 1x1
		//c.rotate(90, 0.5f, 0.5f); // Note: rotate around half
		//c.scale(1.0f/imageSize.width, 1.0f/imageSize.height); // Note: scale to image sizes 
		
		//c.translate(10f,5f);
		axisDraw(c, 100f);
		
		c.drawBitmap(rgb, 0, imageSize.width, 0f, 0f, imageSize.width, imageSize.height, true, null);
		c.restore();
    }
    
    
    
    /*
     * Decode the incoming data (YUV format) to a red-green-blue format
     */
	private void decodeYUV420SP(int[] rgb, byte[] yuv420sp) {
		final int width = imageSize.width;
		final int height = imageSize.height;
    	final int frameSize = width * height;
    	
    	for (int j = 0, yp = 0; j < height; j++) {
    		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
    		for (int i = 0; i < width; i++, yp++) {
    			int y = (0xff & ((int) yuv420sp[yp])) - 16;
    			if (y < 0) y = 0;
    			if ((i & 1) == 0) {
    				v = (0xff & yuv420sp[uvp++]) - 128;
    				u = (0xff & yuv420sp[uvp++]) - 128;
    			}
    			
    			int y1192 = 1192 * y;
    			int r = (y1192 + 1634 * v);
    			int g = (y1192 - 833 * v - 400 * u);
    			int b = (y1192 + 2066 * u);
    			
    			if (r < 0) r = 0; else if (r > 262143) r = 262143;
    			if (g < 0) g = 0; else if (g > 262143) g = 262143;
    			if (b < 0) b = 0; else if (b > 262143) b = 262143;
    			
    			rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
    		}
    	}
    }

    
	public DrawCamera() {
		p = new Paint();
	}
}
