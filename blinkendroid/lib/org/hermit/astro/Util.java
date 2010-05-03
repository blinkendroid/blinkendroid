
/**
 * astro: astronomical functions, utilities and data
 * <br>Copyright 2009 Ian Cameron Smith
 * 
 * <p>References:
 * <dl>
 * <dt>PAC</dt>
 * <dd>"Practical Astronomy with your Calculator", by Peter Duffett-Smith,
 * ISBN-10: 0521356997.</dd>
 * <dt>ESAA</dt>
 * <dd>"Explanatory Supplement to the Astronomical Almanac", edited
 * by Kenneth Seidelmann, ISBN-13: 978-1-891389-45-0.</dd>
 * <dt>AA</dt>
 * <dd>"Astronomical Algorithms", by Jean Meeus, ISBN-10: 0-943396-61-1.</dd>
 * </dl>
 * The primary reference for this version of the software is AA.
 * 
 * <p>Note that the formulae have been converted to work in radians, to
 * make it easier to work with java.lang.Math.
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


package org.hermit.astro;


/**
 * This class contains an implementation of Meeus' interpolation methods.
 *
 * @author	Ian Cameron Smith
 */
public class Util
	implements AstroConstants
{
		
	// ******************************************************************** //
    // Constructors.
    // ******************************************************************** //

	/**
	 * No instances allowed.
	 */
	private Util() {
	}


    // ******************************************************************** //
	// Interpolation Utilities.
	// ******************************************************************** //

	/**
	 * Interpolate in a table to find a good interpolated value.
	 *
	 * From AA chapter 3.
	 * 
	 * @param	args		The arguments of the table.  Each element is
	 * 						an index in the table (e.g. a time).
	 * @param	values		The values of the table.  Each element is
	 * 						the value for the corresponding argument in args[].
	 * @param	index		The argument for which we want the
	 * 						interpolated value.
	 * @return				The value of the table at index, interpolated
	 * 						from the point values.
	 */
	public static double interpolate(double[] args, double[] values, double index) {
		int tablen = args.length;
		if (tablen != values.length)
			throw new IllegalArgumentException("Both tables for interpolate()" +
											   " must be the same length.");
		if (tablen != 3)
			throw new IllegalArgumentException("interpolate() can only handle" +
											   " tables of length 3.  Sorry.");
		
		// Compute the differences between subsequent elements.
		// (This part is generic on the table size.)
		double[][] diffTable = new double[tablen - 1][];
		for (int col = 0; col < tablen - 1; ++col)
			diffTable[col] = makeDiffs(col == 0 ? values : diffTable[col - 1]);
		
		// Calculate the interpolating factor.
		double n = index - args[1];
		
		// And interpolate.
		double sum = diffTable[0][0] + diffTable[0][1] + n * diffTable[1][0];
		return values[1] + n / 2 * sum;
	}
	
	
	/**
	 * Make a table of differences for the given table.
	 * 
	 * @param values
	 * @return
	 */
	private static double[] makeDiffs(double[] values) {
		int n = values.length - 1;
		double[] diffs = new double[n];
		for (int i = 0; i < n; ++i)
			diffs[i] = values[i + 1] - values[i];
		return diffs;
	}

}

