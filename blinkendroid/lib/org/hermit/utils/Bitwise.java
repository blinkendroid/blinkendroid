
/**
 * utils: general utility functions.
 * <br>Copyright 2004-2009 Ian Cameron Smith
 *
 * <p>This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation (see COPYING).
 *
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package org.hermit.utils;


/**
 * A set of bit-twiddling utilities.
 *
 * @author	Ian Cameron Smith
 */
public class Bitwise
{

	// ******************************************************************** //
	// Constructors.
	// ******************************************************************** //

	/**
	 * This class is not constructible.
	 */
	private Bitwise() {
	}


    // ******************************************************************** //
    // Number Properties.
    // ******************************************************************** //
    
    /**
     * Returns true if the argument is power of 2.
     * 
     * @param   n       The number to test.
     * @return          true if the argument is power of 2.
     */
    public static final boolean isPowerOf2(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }


	// ******************************************************************** //
	// Bit Order Reversal.
	// ******************************************************************** //

    /**
     * Reverse the lowest n bits of j.  This function is useful in the
     * Cooley–Tukey FFT algorithm, for example.
     * 
     * @param   j       Number to be reversed.
     * @param   n       Number of low-order bits of j which are significant
     *                  and to be reversed.
     * @return          The lowest n bits of the input value j, reversed.
     *                  The higher-order bits will be zero.
     */
	public static final int bitrev(int j, int n) {
        int r = 0;
        for (int i = 0; i < n; ++i, j >>= 1)
            r = (r << 1) | (j & 0x0001);
        return r;
    }

}

