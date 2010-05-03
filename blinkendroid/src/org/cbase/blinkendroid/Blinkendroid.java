package org.cbase.blinkendroid;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Timer;

import org.hermit.dsp.FFTTransformer;
import org.hermit.dsp.SignalPower;
import org.hermit.dsp.Window;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Blinkendroid extends Activity {

	 public static final String LOG_TAG = "blinkendroids";
    
    /*
	 * Our Vibrator
	 */
    private Vibrator vibrator;
    private VibrationListener vibrationListener;
    
    /**
     * Communication
     */
    
    private Server server;
	private Handler handler;
	
	/*
	 * The views
	 */
    
    private TextView sensorTextView;
	private TextView counterTextView;
	private TextView listenTextView;
	
	/*
	 * The buttons
	 */
	private Button vibrateButton;
	private Button exitButton;
	private Button calibrateButton;
	private Button serverButton;
	private Button switchToMainButton;
	
	/**
	 * Tells whether the vibrateButton has been clicked
	 */
	private boolean buttonClicked = false;
	private boolean vibrateButtonClicked = false;
    
	
	private WakeLock wakeLock;
	
	/**
	 * Audio members
	 */
	 private AudioReader audioReader;
    private boolean listening = false;
    protected Handler listenHandler;
    final int sampleRate = 8000;
	final int inputBlockSize = 256;
    private FFTTransformer spectrumAnalyser;
	int counter=0;
	
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
		
		switchToMainButton = (Button) this
				.findViewById(R.id.switchToMainButton);
		switchToMainButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switchToDebugView();
			}
		});
		
		
		handler = new Handler();
		
		initAudio();

		// End initialize

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
	 * Audio capture initialization
	 */
	public void initAudio() {
		
		listenTextView = (TextView) this.findViewById(R.id.TextView03);
		Button bListen = (Button) this.findViewById(R.id.ListenButton);
		listenHandler = new Handler();
		this.audioReader = new AudioReader();	
		this.spectrumAnalyser = new FFTTransformer(inputBlockSize, Window.Function.BLACKMAN_HARRIS);
		FrequencyView.spectrumData = new float[inputBlockSize / 2];
		Arrays.fill(FrequencyView.spectrumData, 0);
	
		final AudioReader.Listener audioReadListener = new AudioReader.Listener() {
			
			public void onReadComplete(short[] buffer) {
				counter++;
				FrequencyView.buffer=buffer;
				//ich nehm erstmal nur jeden 4ten
				if(counter%4==0)
					return;
				double currentPower = SignalPower.calculatePowerDb(buffer, 0, buffer.length);

				FrequencyView.power=(float)currentPower;
				
				spectrumAnalyser.setInput(buffer, buffer.length - inputBlockSize, inputBlockSize);
				spectrumAnalyser.transform();
				spectrumAnalyser.getResults(FrequencyView.spectrumData);
				 
				//final String out = writeBuffer(buffer);
				final String out = currentPower + "db";
//				Log.d(Blinkendroid.LOG_TAG, Arrays.toString(FrequencyView.spectrumData));
				listenHandler.post(new Runnable() {
					public void run() {
						listenTextView.setText(out);
					}
				});
			}
			
		};
		
		bListen.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Button me = (Button)v;
				if (!listening) {
					listening = true;
					me.setText("Stop");
					audioReader.startReader(sampleRate, inputBlockSize, audioReadListener);
				} else {
					audioReader.stopReader();
					listening = false;
					me.setText("Listen");
				}
				
			}
			
		});
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

	public Handler getHandler() {

		return handler;
	}

	private void releaseWakeLock() {
		if (wakeLock.isHeld())
			wakeLock.release();
	}

	/**
	 * Initialization of objects for the debug view.
	 */
	private void switchToDebugView() {
		setContentView(R.layout.debug);
		calibrateButton = (Button) this.findViewById(R.id.calibrateButton);
		calibrateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(),
						getString(R.string.calibrating), Toast.LENGTH_SHORT)
						.show();
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

		server = new Server(this);
		serverButton = (Button) this.findViewById(R.id.ServerButton);
		serverButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (server.isRunning()) {
					server.end();
					serverButton.setText(R.string.serverbuttonstart);
				} else {
					server.start();
					serverButton.setText(R.string.serverbuttonstop);
				}
			}
		});

		counterTextView = (TextView) this.findViewById(R.id.CounterTextView);
		sensorTextView = (TextView) this.findViewById(R.id.SensorsTextView);

		((EditText) Blinkendroid.this.findViewById(R.id.ServerIPEditText))
				.setText(getLocalIpAddress());
		Button sendButton = (Button) this.findViewById(R.id.SendButton);
		sendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String ip = ((EditText) Blinkendroid.this
						.findViewById(R.id.ServerIPEditText)).getEditableText()
						.toString();
				String chat = ((EditText) Blinkendroid.this
						.findViewById(R.id.ChatEditText)).getEditableText()
						.toString();
				new Client(Blinkendroid.this, ip, "IMEI: " + getImei()).start();
			}
		});
		// End initialize

		getVibration();
	}

	/**
	 * Gets the IPAdress of the local device
	 * @return The IPAdress or null if none was found
	 */
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(LOG_TAG, ex.toString());
		}
		return null;
	}
	
	/**
	 * Gets the IMEI of the running device
	 * @return the IMEI
	 */
	public String getImei() {
		return ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
		.getDeviceId();
	}
	
	/**
	 * Gets the location of a device on the x ordinate in a matrix
	 * @return
	 */
	public int getLocationX() {
	    //TODO implement setting of the location (backend and gui)
	    return 1;
	}
	
	/**
	 * Gets the location of a device on the y ordinate in a matrix
	 * @return
	 */
	public int getLocationY() {
	    //TODO implement setting of the location (backend and gui)
	    return 1;
	}
	
	
}
