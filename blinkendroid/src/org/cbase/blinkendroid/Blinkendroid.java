package org.cbase.blinkendroid;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Blinkendroid extends Activity {

    private VibrationListener vibrationListener;
    private TextView sensorTextView;
    private TextView counterTextView;
    private Button vibrate;
    private Button exit;
    private Vibrator vibrator;
    private boolean buttonClicked = false;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	// Get the button and set the listener
	exit = (Button) this.findViewById(R.id.exit);
	exit.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		System.exit(0);
	    }
	});
	
	vibrate = (Button) this.findViewById(R.id.vibrate);
	vibrate.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		vibrate();
		setButtonClicked(true);
	    }
	});
	
	counterTextView = (TextView) this.findViewById(R.id.CounterTextView);
	sensorTextView = (TextView) this.findViewById(R.id.SensorsTextView);
	getVibration();
    }

    /**
     * Vibrates the device.
     */
    public void vibrate() {
	SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
	sm.unregisterListener(getVibrationListener());
	vibrator  = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	Timer t = new Timer();
	t.schedule(new VibrationTask(500), 0);
	t.schedule(new VibrationTask(500), 1000);
	t.schedule(new VibrationTask(500), 2000);
	t.schedule(new ReconnectTask(), 2500);
	sensorTextView.setBackgroundColor(Color.RED);
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

    private VibrationListener getVibrationListener() {
	if (vibrationListener == null) {
	    vibrationListener = new VibrationListener(this);
	}
	    return vibrationListener;
    }

    /**
     * @return the counterTextView
     */
    public TextView getCounterTextView() {
	return counterTextView;
    }

    /**
     * @return the sensorTextView
     */
    public TextView getSensorTextView() {
	return sensorTextView;
    }

    /**
     * @param buttonClicked the buttonClicked to set
     */
    public void setButtonClicked(boolean buttonClicked) {
	this.buttonClicked = buttonClicked;
    }

    /**
     * @return the buttonClicked
     */
    public boolean isButtonClicked() {
	return buttonClicked;
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
    private class ReconnectTask extends TimerTask {
	
	public ReconnectTask() {
	}
	@Override

	public void run() {
	getVibration();
	sensorTextView.setBackgroundColor(Color.BLACK);

	}
	
    }
}
