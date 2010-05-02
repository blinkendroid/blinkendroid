package org.cbase.blinkendroid;

import java.util.TimerTask;

import android.graphics.Color;

public class TaskPool {
    
    private ReconnectTask reconnectTask;
    
    private Blinkendroid blinkendroid;
    public TaskPool(Blinkendroid bd) {
	blinkendroid = bd;
    }
    
    public class VibrationTask extends TimerTask {
	private long vibrationDuration;
	
	public VibrationTask(long vibrationDuration) {
	    this.vibrationDuration = vibrationDuration;
	}
	@Override
	public void run() {
	    blinkendroid.vibrate(vibrationDuration);
	}
	
    }
    public class ReconnectTask extends TimerTask {
	
	public ReconnectTask() {
	}
	@Override

	public void run() {
	blinkendroid.getVibration();
	blinkendroid.getSensorTextView().setBackgroundColor(Color.BLACK);

	}
    }
    
    public VibrationTask createVibrationTask(long vibrationDuration) {
	return new VibrationTask(vibrationDuration);
    }

    public ReconnectTask createReconnectTask() {
	if (reconnectTask == null) {
	    reconnectTask = new ReconnectTask();
	}
	return reconnectTask;
    }
}
