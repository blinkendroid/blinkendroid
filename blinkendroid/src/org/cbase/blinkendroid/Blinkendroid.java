package org.cbase.blinkendroid;

import java.util.Timer;

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
    /*
     * The views 
     */
    private TextView sensorTextView;
    private TextView counterTextView;
    /*
     * The buttons
     */
    private Button vibrate;
    private Button exit;
    /*
     * Our Vibrator
     */
    private Vibrator vibrator;
    /*
     * Tells whether the button has been clicked
     */
    private boolean buttonClicked = false;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	
	exit = (Button) this.findViewById(R.id.exit);
	exit.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		System.exit(0);
	    }
	});
	
	vibrate = (Button) this.findViewById(R.id.vibrate);
	vibrate.setOnClickListener(new OnClickListener() {
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
	
	TaskPool tp = new TaskPool(this);
	Timer t = new Timer();
	t.schedule(tp.createVibrationTask(500), 0);
	t.schedule(tp.createVibrationTask(500), 1000);
	t.schedule(tp.createVibrationTask(500), 2000);
	t.schedule(tp.createReconnectTask(), 2600);
	sensorTextView.setBackgroundColor(Color.RED);
    }
    /**
     * Gets the vibration from another source.
     */
    public void getVibration() {
	SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
	sm.registerListener(getVibrationListener(), sm
		.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		SensorManager.SENSOR_DELAY_GAME);
    }
    /**
     * Vibrates for a given time
     * @param durationMS The time in milliseconds
     */
    public void vibrate(long durationMS) {
	vibrator.vibrate(durationMS);
    }
    /**
     * Gets a {@link VibrationListener}
     * @return see above
     */
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
}
