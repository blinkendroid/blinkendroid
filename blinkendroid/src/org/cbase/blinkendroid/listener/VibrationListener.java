/*
 * Copyright 2010 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cbase.blinkendroid.listener;


import org.cbase.blinkendroid.OldBlinkendroid;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class VibrationListener implements SensorEventListener {

    private long lastTimeOverThreshold = 0;
    private int counter = 0;
    private float data_x, data_y, data_z, acc_m;
    private OldBlinkendroid blinkendroid;
    
    public VibrationListener(OldBlinkendroid bd) {
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
