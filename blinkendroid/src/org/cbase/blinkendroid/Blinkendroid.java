package org.cbase.blinkendroid;

import java.util.Timer;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.PowerManager.WakeLock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Blinkendroid extends Activity {

	public static final String LOG_TAG	=	"blinkendroids";
	private VibrationListener vibrationListener;
	/*
	 * The views
	 */
	private TextView sensorTextView;
	private TextView counterTextView;
	/*
	 * The buttons
	 */
	private Button vibrateButton;
	private Button exitButton;
	private Button calibrateButton;
	private Button serverButton;
	/*
	 * Our Vibrator
	 */
	private Vibrator vibrator;
	/**
	 * Tells whether the vibrateButton has been clicked
	 */
	private boolean vibrateButtonClicked = false;

	private WakeLock wakeLock;

	private Server server;
	private Handler handler;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Initialize
		wakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
						getString(R.string.app_name));
		wakeLock.acquire();
		calibrateButton = (Button) this.findViewById(R.id.calibrateButton);
		calibrateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(),
						getString(R.string.calibrating), Toast.LENGTH_SHORT)
						.show();
				String imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
						.getDeviceId();
				Toast
						.makeText(getApplicationContext(), imei,
								Toast.LENGTH_LONG).show();
				Log.i(this.getClass().getName(), "Calibrate Button pressed");
			}
		});

		exitButton = (Button) this.findViewById(R.id.exitButton);
		exitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				releaseWakeLock();
				Log.i(this.getClass().getName(), "Exit Button pressed");
				System.exit(0);
			}
		});

		vibrateButton = (Button) this.findViewById(R.id.vibrateButton);
		vibrateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				vibrate();
				Log.i(this.getClass().getName(), "Vibrate Button pressed");
				setButtonClicked(true);
			}
		});

		server	=	new Server(this);		
		serverButton = (Button) this.findViewById(R.id.ServerButton);
		serverButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(server.running){
					server.end();
					serverButton.setText(R.string.serverbuttonstart);
				}else{
					server.start();
					serverButton.setText(R.string.serverbuttonstop);}
			}
		});
		
		counterTextView = (TextView) this.findViewById(R.id.CounterTextView);
		sensorTextView = (TextView) this.findViewById(R.id.SensorsTextView);
		// End initialize

		getVibration();
	}

	@Override
	protected void onDestroy() {
		Log.i(this.getClass().getName(), "onDestroy()");
		releaseWakeLock();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.i(this.getClass().getName(), "onPause()");
		releaseWakeLock();
		super.onPause();
	}

	@Override
	protected void onStop() {
		releaseWakeLock();
		Log.i(this.getClass().getName(), "onStop()");
		super.onStop();
	}

	@Override
	protected void onRestart() {
		wakeLock.acquire();
		Log.i(this.getClass().getName(), "onRestart()");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		wakeLock.acquire();
		Log.i(this.getClass().getName(), "onResume()");
		super.onResume();
	}

	/**
	 * Vibrates the device.
	 */
	public void vibrate() {
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sm.unregisterListener(getVibrationListener());
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

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
	 * 
	 * @param durationMS
	 *            The time in milliseconds
	 */
	public void vibrate(long durationMS) {
		vibrator.vibrate(durationMS);
	}

	/**
	 * Gets a {@link VibrationListener}
	 * 
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
	 * @param buttonClicked
	 *            the buttonClicked to set
	 */
	public void setButtonClicked(boolean buttonClicked) {
		this.vibrateButtonClicked = buttonClicked;
	}

	/**
	 * @return the buttonClicked
	 */
	public boolean isVibrateButtonClicked() {
		return vibrateButtonClicked;
	}

	private void releaseWakeLock() {
		if (wakeLock.isHeld())
			wakeLock.release();
	}

	public Handler getHandler() {
		if(null==handler)
			handler= new Handler();
		return handler;
	}
}
