package org.cbase.blinkendroid.listener;


import org.cbase.blinkendroid.Blinkendroid;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class VibrationListener implements SensorEventListener {

    private long lastTimeOverThreshold = 0;
    private int counter = 0;
    private float data_x, data_y, data_z, acc_m;
    private Blinkendroid blinkendroid;
    
    public VibrationListener(Blinkendroid bd) {
	blinkendroid = bd;
    }

    public void onSensorChanged(SensorEvent event) {
	data_x = event.values[SensorManager.DATA_X];
	data_y = event.values[SensorManager.DATA_Y];
	data_z = event.values[SensorManager.DATA_Z];
	acc_m = (float) Math.sqrt(data_x * data_x + data_y * data_y + data_z * data_z);

	blinkendroid.getSensorTextView().setText(Float.toString(acc_m));
	if (acc_m < 9) {
	    if (lastTimeOverThreshold == 0) {
		lastTimeOverThreshold = System.currentTimeMillis();
	    } else if (System.currentTimeMillis() - lastTimeOverThreshold >= 550) {
		lastTimeOverThreshold = System.currentTimeMillis();
		counter++;
		blinkendroid.getCounterTextView().setText(""+counter);
	    }
	}
	if (counter == 3) {
	    counter = 0;
	    blinkendroid.vibrate();
	    blinkendroid.getCounterTextView().setText(""+counter);
	}
	if (System.currentTimeMillis() - lastTimeOverThreshold >= 3000) {
	    counter = 0;
	    blinkendroid.getCounterTextView().setText(""+counter);
	}

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	// TODO Auto-generated method stub
    }

    public void reset() {
	counter = 0;
	lastTimeOverThreshold = 0;
    }
}
