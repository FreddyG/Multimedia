package assignment1.android;

import uvamult.assignment1.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;

public class CameraActivity extends Activity {
    /** A handle to the View that handles camera stuff. */
    private CameraView mCameraView;
    // added
    private SeekBar mSeekBar;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        
        // get handles to the CameraView from XML
        mCameraView = (CameraView) findViewById(R.id.camerafield);
        // added
        mSeekBar = (SeekBar) findViewById(R.id.seekBar1);
        
        mCameraView.setActivity(this);
    }
}