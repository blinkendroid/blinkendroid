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

package org.cbase.blinkendroid.audio;

/**
 * org.hermit.android.instrument: graphical instruments for Android.
 * <br>Copyright 2009 Ian Cameron Smith
 * 
 * <p>These classes provide input and display functions for creating on-screen
 * instruments of various kinds in Android apps.
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation (see COPYING).
 * 
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import org.hermit.dsp.FFTTransformer;
import org.hermit.dsp.SignalPower;
import org.hermit.dsp.Window;

import android.os.Handler;

/**
 * An {@link Instrument} which analyses an audio stream in various ways.
 * <p>
 * To use this class, your application must have permission RECORD_AUDIO.
 */
public class AudioAnalyser {

    /** The desired sampling rate for this analyser, in samples/sec. */
    private int sampleRate = 8000;
    /** Audio input block size, in samples. */
    private int inputBlockSize = 256;
    /** The selected windowing function. */
    private Window.Function windowFunction = Window.Function.BLACKMAN_HARRIS;
    /** The desired decimation rate for this analyser. Only 1 in 
     * sampleDecimate blocks will actually be processed.
     */
    private int sampleDecimate = 1;
    /** The desired histogram averaging window. 1 means no averaging. */
    private int historyLen = 4;
    /** Our audio input device. */
    private final AudioReader audioReader;
    /** Fourier Transform calculator we use for calculating the spectrum. */
    private FFTTransformer spectrumAnalyser;
    /** Buffered audio data, and sequence number of the latest block. */
    private short[] audioData;
    private long audioSequence = 0;
    /** Sequence number of the last block we processed. */
    private long audioProcessed = 0;
    /** Analysed audio spectrum data; index into the history data */
    private float[] spectrumData;
    /** history data for each frequency in the spectrum */
    private float[][] spectrumHist;
    /** and buffer for peak frequencies */
    private int spectrumIndex;
    //TODO remove unread variables?
    /** Current signal power level, in dB relative to max. input power. */
    private double currentPower = 0f;
    /** Temp. buffer for calculated bias and range. */
    private float[] biasRange = null;
    /** the handler that will call back to our main activity */
    private Handler handler;
    /**
     * Create an AudioAnalyser
     */
    public AudioAnalyser() {

	audioReader = new AudioReader();

	spectrumAnalyser = new FFTTransformer(inputBlockSize, windowFunction);

	// Allocate the spectrum data.
	spectrumData = new float[inputBlockSize / 2];
	spectrumHist = new float[inputBlockSize / 2][historyLen];
	spectrumIndex = 0;

	biasRange = new float[2];
    }
    /**
     * Set the sample rate for this instrument.
     * 
     * @param rate
     *            The desired rate, in samples/sec.
     */
    public void setSampleRate(int rate) {
	sampleRate = rate;
    }
    /**
     * Set the input block size for this instrument.
     * 
     * @param size
     *            The desired block size, in samples. Typical values would be
     *            256, 512, or 1024. Larger block sizes will mean more work to
     *            analyse the spectrum.
     */
    public void setBlockSize(int size) {
	inputBlockSize = size;
	spectrumAnalyser = new FFTTransformer(inputBlockSize, windowFunction);
	// Allocate the spectrum data.
	spectrumData = new float[inputBlockSize / 2];
	spectrumHist = new float[inputBlockSize / 2][historyLen];
    }
    /**
     * Set the spectrum analyser windowing function for this instrument.
     * @param func
     *            The desired windowing function.
     *            Window.Function.BLACKMAN_HARRIS is a good option.
     *            Window.Function.RECTANGULAR turns off windowing.
     */
    public void setWindowFunc(Window.Function func) {
	windowFunction = func;
	spectrumAnalyser.setWindowFunc(func);
    }
    /**
     * Set the decimation rate for this instrument.
     * @param rate
     *            The desired decimation. Only 1 in rate blocks will actually be
     *            processed.
     */
    public void setDecimation(int rate) {
	sampleDecimate = rate;
    }
    /**
     * Set the histogram averaging window for this instrument.
     * @param len
     *            The averaging interval. 1 means no averaging.
     */
    public void setAverageLen(int len) {
	historyLen = len;
	// Set up the history buffer.
	spectrumHist = new float[inputBlockSize / 2][historyLen];
	spectrumIndex = 0;
    }
    
    public void setHandler(Handler h) {
	this.handler = h;
    }
    /**
     * We are starting the main run; start measurements.
     */
    public void measureStart() {
	audioProcessed = audioSequence = 0;
	audioReader.startReader(sampleRate, inputBlockSize * sampleDecimate,
		new AudioReader.Listener() {
		    @Override
		    public final void onReadComplete(short[] buffer) {
			receiveAudio(buffer);

		    }
		});
    }
    /**
     * We are stopping / pausing the run; stop measurements.
     */
    public void measureStop() {
	audioReader.stopReader();
    }
    /*
     * public PowerGauge getPowerGauge(SurfaceRunner surface) { if (powerGauge
     * != null) throw new RuntimeException("Already have a PowerGauge" +
     * " for this AudioAnalyser"); powerGauge = new PowerGauge(surface); return
     * powerGauge; }
     */

    // ******************************************************************** //
    // Audio Processing.
    // ******************************************************************** //

    /**
     * Handle audio input. This is called on the thread of the audio reader.
     * 
     * @param buffer
     *            Audio data that was just read.
     */
    private final void receiveAudio(short[] buffer) {
	// Lock to protect updates to these local variables. See run().
	synchronized (this) {
	    audioData = buffer;
	    ++audioSequence;
	}
    }

    // ******************************************************************** //
    // Main Loop.
    // ******************************************************************** //

    /**
     * Update the state of the instrument for the current frame. This method
     * must be invoked from the doUpdate() method of the application's
     * {@link SurfaceRunner}.
     * 
     * <p>
     * Since this is called frequently, we first check whether new audio data
     * has actually arrived.
     * 
     * @param now
     *            Nominal time of the current frame in ms.
     */
    public final void doUpdate(long now) {
	short[] buffer = null;
	synchronized (this) {
	    if (audioData != null && audioSequence > audioProcessed) {
		audioProcessed = audioSequence;
		buffer = audioData;
	    }
	}

	// If we got data, process it without the lock.
	if (buffer != null)
	    processAudio(buffer);
    }

    /**
     * Handle audio input. This is called on the thread of the parent surface.
     * 
     * @param buffer
     *            Audio data that was just read.
     */
    private final void processAudio(short[] buffer) {
	// Process the buffer. While reading it, it needs to be locked.
	synchronized (buffer) {
	    // Calculate the power now, while we have the input
	    // buffer; this is pretty cheap.
	    final int len = buffer.length;
	    currentPower = SignalPower.calculatePowerDb(buffer, 0, len);

	    // If we have a spectrum analyser, set up the FFT input data.
	    spectrumAnalyser.setInput(buffer, len - inputBlockSize,
		    inputBlockSize);

	    // Tell the reader we're done with the buffer.
	    buffer.notify();
	}

	// If we have a spectrum analyser, perform the FFT.
	// Do the (expensive) transformation.
	// The transformer has its own state, no need to lock here.

	spectrumAnalyser.transform();

	// Get the FFT output and draw the spectrum.
	if (historyLen <= 1)
	    spectrumAnalyser.getResults(spectrumData);
	else
	    spectrumIndex = spectrumAnalyser.getResults(spectrumData,
		    spectrumHist, spectrumIndex);
	// do something with spectrumData
    }
}
