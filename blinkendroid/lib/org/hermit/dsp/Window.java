
/**
 * dsp: various digital signal processing algorithms
 * <br>Copyright 2009 Ian Cameron Smith
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


package org.hermit.dsp;


/**
 * A windowing function for a discrete signal.  This is used to
 * pre-process a signal prior to FFT, in order to improve the frequency
 * response, essentially by eliminating the discontinuities at the ends
 * of a block of samples.
 */
public final class Window {

    // ******************************************************************** //
    // Public Constants.
    // ******************************************************************** //

    /**
     * Definitions of the available window functions.
     */
    public enum Function {
        /** A simple rectangular window function.  This is equivalent to
         * doing no windowing. */
        RECTANGULAR,
        
        /** The Blackman-Harris window function. */
        BLACKMAN_HARRIS,
        
        /** The Gauss window function. */
        GAUSS,
        
        /** The Weedon-Gauss window function. */
        WEEDON_GAUSS,
    }
    
    
    // ******************************************************************** //
    // Constructor.
    // ******************************************************************** //

    /**
     * Create a window function for a given sample size.  This preallocates
     * resources appropriate to that block size.
     * 
     * @param   size        The number of samples in a block that we will
     *                      be asked to transform.
     */
    public Window(int size) {
        this(size, DEFAULT_FUNC);
    }

    
    /**
     * Create a window function for a given sample size.  This preallocates
     * resources appropriate to that block size.
     * 
     * @param   size        The number of samples in a block that we will
     *                      be asked to transform.
     * @param   function    The window function to use.  Function.RECTANGULAR
     *                      effectively means no transformation.
     */
    public Window(int size, Function function) {
        blockSize = size;

        // Create the window function as an array, so we do the
        // calculations once only.  For RECTANGULAR, leave the kernel as
        // null, signalling no transformation.
        kernel = function == Function.RECTANGULAR ? null : new double[size];

        switch (function) {
        case RECTANGULAR:
            // Nothing to do.
            break;
        case BLACKMAN_HARRIS:
            makeBlackmanHarris(kernel, size);
            break;
        case GAUSS:
            makeGauss(kernel, size);
            break;
        case WEEDON_GAUSS:
            makeWeedonGauss(kernel, size);
            break;
        }
    }

    
    // ******************************************************************** //
    // Window Functions.
    // ******************************************************************** //

    private void makeBlackmanHarris(double[] buf, int len) {
        final double n = (double) (len - 1);
        for (int i = 0; i < len; ++i) {
            final double f = Math.PI * (double) i / n;
            buf[i] = BH_A0 -
                     BH_A1 * Math.cos(2.0 * f) +
                     BH_A2 * Math.cos(4.0 * f) -
                     BH_A3 * Math.cos(6.0 * f);
        }
    }
    
    
    private void makeGauss(double[] buf, int len) {
        final double k = (double) (len - 1) / 2;
        
        for (int i = 0; i < len; ++i) {
            final double d = (i - k) / (0.4 * k);
            buf[i] = Math.exp(-0.5 * d * d);
        }
    }
    

    private void makeWeedonGauss(double[] buf, int len) {
        final double k = (-250.0 * 0.4605) / (double) (len * len);
        final double d = (double) len / 2.0;
        
        for (int i = 0; i < len; ++i) {
            final double n = (double) i - d;
            buf[i] = Math.exp(n * n * k);
        }
    }
    

    // ******************************************************************** //
    // Data Transformation.
    // ******************************************************************** //

    /**
     * Apply the window function to a given data block.  The data in
     * the provided buffer will be multiplied by the window function.
     * 
     * @param   input       The input data buffer.  This data will be
     *                      transformed in-place by the window function.
     * @throws  IllegalArgumentException    Invalid data size.
     */
    public final void transform(double[] input) {
        transform(input, 0, input.length);
    }
    

    /**
     * Apply the window function to a given data block.  The data in
     * the provided buffer will be multiplied by the window function.
     * 
     * @param   input       The input data buffer.  This data will be
     *                      transformed in-place by the window function.
     * @param   off         Offset in the buffer at which the data to
     *                      be transformed starts.
     * @param   count       Number of samples in the data to be
     *                      transformed.  Must be the same as the size
     *                      parameter that was given to the constructor.
     * @throws  IllegalArgumentException    Invalid data size.
     */
    public final void transform(double[] input, int off, int count) {
        if (count != blockSize)
            throw new IllegalArgumentException("bad input count in Window:" +
                                               " constructed for " + blockSize +
                                               "; given " + input.length);
        if (kernel != null)
            for (int i = 0; i < blockSize; i++)
                input[off + i] *= kernel[i];
    }
    
    
    // ******************************************************************** //
    // Private Constants.
    // ******************************************************************** //

    // Default window function.
    private static final Function DEFAULT_FUNC = Function.BLACKMAN_HARRIS;
    
    // Blackman-Harris coefficients.  These sum to 1.0.
    private static final double BH_A0 = 0.35875;
    private static final double BH_A1 = 0.48829;
    private static final double BH_A2 = 0.14128;
    private static final double BH_A3 = 0.01168;

        
    // ******************************************************************** //
    // Private Data.
    // ******************************************************************** //

    // The size of an input data block.
    private final int blockSize;
    
    // The window function, as a pre-computed array of multiplication factors.
    // If null, do no transformation -- this is a unity rectangular window.
    private final double[] kernel;

}

