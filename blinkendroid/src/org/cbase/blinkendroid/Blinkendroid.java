package org.cbase.blinkendroid;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Blinkendroid extends Activity {

    private SensorEventListener sensorEventListener;
    private TextView sensorTextView;
    private TextView counterTextView;
    private Button vibrate;
    private Vibrator vibrator;
    private boolean buttonClicked = false;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	// Get the button and set the listener
	vibrate = (Button) this.findViewById(R.id.vibrate);
	vibrate.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		vibrate();
		buttonClicked = true;
	    }
	});
	
	sensorTextView = (TextView) this.findViewById(R.id.TextView01);
	counterTextView = (TextView) this.findViewById(R.id.TextView02);
	getVibration();
    }

    /**
     * Vibrates the device.
     */
    public void vibrate() {
	vibrator  = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	Timer t = new Timer();
	t.schedule(new VibrationTask(500), 0);
	t.schedule(new VibrationTask(500), 1000);
	t.schedule(new VibrationTask(500), 2000);
    }
    /**
     * Gets the vibration from another source.
     */
    public void getVibration() {
	SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
	sm.registerListener(getVibrationListener(), sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sm.SENSOR_DELAY_GAME);
    }
    
    private void vibrationDuration(long durationMS) {
	vibrator.vibrate(durationMS);
    }

    private SensorEventListener getVibrationListener() {
	if (sensorEventListener == null)
	    sensorEventListener = new SensorEventListener() {
	    	private float zOld = 0;
	    	private long lastTimeOverThreshold = 0;
	    	private int counter = 0;
		@Override
		public void onSensorChanged(SensorEvent event) {
		    sensorTextView.setText(" dx " + (event.values[2] - zOld));
		    if (Math.abs(event.values[2] - zOld)  > 0.3) {
			if (lastTimeOverThreshold == 0) {
			    lastTimeOverThreshold = System.currentTimeMillis();
			    counterTextView.setText("" + counter);
			} else if (System.currentTimeMillis() - lastTimeOverThreshold >= 750) {
			    lastTimeOverThreshold = System.currentTimeMillis();
			    counter++;
			    counterTextView.setText("" + counter);
			}
		    }
		    if (counter == 3) {
			counter = 0;
			buttonClicked = !buttonClicked;
			if (buttonClicked == true) {
			    vibrate();
			}
		    }
		    zOld = event.values[2];
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		    // TODO Auto-generated method stub

		}
	    };
	    return sensorEventListener;
    }
    
    private class VibrationTask extends TimerTask {
	private long vibrationDuration;
	
	public VibrationTask(long vibrationDuration) {
	    this.vibrationDuration = vibrationDuration;
	}
	@Override
	public void run() {
	    vibrationDuration(vibrationDuration);
	}
	
    }
}
