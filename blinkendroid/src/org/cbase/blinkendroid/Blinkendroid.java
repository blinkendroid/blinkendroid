package org.cbase.blinkendroid;

import java.util.Arrays;
import java.util.logging.Logger;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Blinkendroid extends Activity {

    private SensorEventListener sensorEventListener;
    private TextView textfeld;
    private Button vibrate;
    
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
	    }
	});
	
	textfeld = (TextView) this.findViewById(R.id.TextView01);
	getVibration();
    }

    /**
     * Vibrates the device.
     */
    public void vibrate() {
	Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	vibrator.vibrate(2000l);
    }
    /**
     * Gets the vibration from another source.
     */
    public void getVibration() {
	SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
	sm.registerListener(getVibrationListener(), sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 1);
    }

    private SensorEventListener getVibrationListener() {
	if (sensorEventListener == null)
	    sensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
		    textfeld.setText(Arrays.toString(event.values));
		    Log.i("Vibrate", Arrays.toString(event.values));
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		    // TODO Auto-generated method stub

		}
	    };
	    return sensorEventListener;
    }
}
