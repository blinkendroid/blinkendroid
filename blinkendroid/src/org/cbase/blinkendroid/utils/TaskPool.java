package org.cbase.blinkendroid.utils;

import java.util.TimerTask;

import org.cbase.blinkendroid.Blinkendroid;

import android.graphics.Color;

/**
 * A pool of tasks for vibration listening, running and reconnecting.
 */
public class TaskPool {
    /**
     * see {@link ReconnectTask}.
     */
    private ReconnectTask reconnectTask;
    /**
     * The running {@link Blinkendroid}.
     */
    private Blinkendroid blinkendroid;
    /**
     * Creates a {@link TaskPool} for a {@link Blinkendroid}.
     * @param bd The Blinkendroid that the TaskPool is created for.
     */
    public TaskPool(Blinkendroid bd) {
	blinkendroid = bd;
    }

    /**
     * A Task to set the vibration duration.
     */
    public class VibrationTask extends TimerTask {
	/**
	 * The vibration duration in milliseconds.
	 */
	private long vibrationDuration;

	/**
	 * Creates a {@link VibrationTask}
	 * 
	 * @param vibrationDuration
	 *            The vibration duration in milliseconds.
	 */
	public VibrationTask(long vibrationDuration) {
	    this.vibrationDuration = vibrationDuration;
	}
	@Override
	public void run() {
	    blinkendroid.vibrate(vibrationDuration);
	}
    }
    /**
     * A Task to reconnect the vibration listeners.
     */
    public class ReconnectTask extends TimerTask {
	/**
	 * Creates a {@link ReconnectTask}.
	 */
	public ReconnectTask() {
	}
	@Override
	public void run() {
	    blinkendroid.getVibration();
	    blinkendroid.getSensorTextView().setBackgroundColor(Color.BLACK);
	}
    }
    /**
     * Creates a {@link VibrationTask}.
     * @param vibrationDuration The vibration's duration in milliseconds.
     * @return The created {@link VibrationTask}.
     */
    public VibrationTask createVibrationTask(long vibrationDuration) {
	return new VibrationTask(vibrationDuration);
    }
    /**
     * Creates a {@link ReconnectTask}.
     * @return The created {@link ReconnectTask}.
     */
    public ReconnectTask createReconnectTask() {
	if (reconnectTask == null) {
	    reconnectTask = new ReconnectTask();
	}
	return reconnectTask;
    }
}