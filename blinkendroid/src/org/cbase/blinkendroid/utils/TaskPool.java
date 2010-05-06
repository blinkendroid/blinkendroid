/*
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

package org.cbase.blinkendroid.utils;

import java.util.TimerTask;

import org.cbase.blinkendroid.Blinkendroid;
import org.cbase.blinkendroid.OldBlinkendroid;

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
    private OldBlinkendroid blinkendroid;
    /**
     * Creates a {@link TaskPool} for a {@link Blinkendroid}.
     * @param bd The Blinkendroid that the TaskPool is created for.
     */
    public TaskPool(OldBlinkendroid bd) {
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