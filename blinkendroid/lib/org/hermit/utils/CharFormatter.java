
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
 * Utilities for quickly formatting numbers into character buffers, without
 * memory allocations.  These routines are much faster than using
 * String.format, and can be used to avoid GC.
 *
 * @author	Ian Cameron Smith
 */
public class CharFormatter
{

	// ******************************************************************** //
	// Private Constructors.
	// ******************************************************************** //

	/**
	 * No instances of this class.
	 */
	private CharFormatter() {
	}

	
	// ************************************************************************ //
	// Static Formatting Utilities.
	// ************************************************************************ //

    /**
     * Fill a field of a given width with spaces.
     * 
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
     * @param   field       Width of the field to blank.  -1 means use
     *                      all available space.
     * @throws  ArrayIndexOutOfBoundsException  Buffer is too small.
     */
    public static final void blank(char[] buf, int off, int field)
    {
        if (field < 0)
            field = buf.length - off;
        if (field < 0)
            field = 0;
        if (off + field > buf.length)
            throw new ArrayIndexOutOfBoundsException("Buffer [" + buf.length +
                                                     "] too small for " +
                                                     off + "+" + field);
        
        for (int i = 0; i < field; ++i)
            buf[off + i] = ' ';
    }


    /**
     * Format a string left-aligned into a fixed-width field.  MUCH faster
     * than String.format.
     * 
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
     * @param   val         The value to format.
     * @param   field       Width of the field to format in.  -1 means use
     *                      all available space.
     * @throws  ArrayIndexOutOfBoundsException  Buffer is too small.
     */
    public static final void formatString(char[] buf, int off, String val,
                                          int field)
    {
        formatString(buf, off, val, field, false);
    }


    /**
     * Format a string into a fixed-width field.  MUCH faster
     * than String.format.
     * 
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
     * @param   val         The value to format.
     * @param   field       Width of the field to format in.  -1 means use
     *                      all available space.
     * @param   right       Iff true, format the text right-aligned; else
     *                      left-aligned.
     * @throws  ArrayIndexOutOfBoundsException  Buffer is too small.
     */
    public static final void formatString(char[] buf, int off, String val,
                                          int field, boolean right)
    {
        if (field < 0)
            field = buf.length - off;
        if (field < 0)
            field = 0;
        if (off + field > buf.length)
            throw new ArrayIndexOutOfBoundsException("Buffer [" + buf.length +
                                                     "] too small for " +
                                                     off + "+" + field);
        if (val == null || val.length() == 0) {
            blank(buf, off, field);
            return;
        }
        
        int strlen = val.length();
        int len = val.length() < field ? val.length() : field;
        int pad = field - len;
        if (!right)
            val.getChars(0, len, buf, off);
        else
            val.getChars(strlen - len, strlen, buf, off + pad);
        
        int pads = !right ? off + len : off;
        for (int i = 0; i < pad; ++i)
            buf[pads + i] = ' ';
    }


    /**
     * Format a single character into a fixed-width field.  The remainder
     * is filled with spaces.  MUCH faster than String.format.
     * 
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
     * @param   val         The value to format.
     * @param   field       Width of the field to format in.  -1 means use
     *                      all available space.
     * @param   right       Iff true, format the text right-aligned; else
     *                      left-aligned.
     * @throws  ArrayIndexOutOfBoundsException  Buffer is too small.
     */
    public static final void formatChar(char[] buf, int off, char val,
                                        int field, boolean right)
    {
        if (field < 0)
            field = buf.length - off;
        if (field < 0)
            field = 0;
        if (off + field > buf.length)
            throw new ArrayIndexOutOfBoundsException("Buffer [" + buf.length +
                                                     "] too small for " +
                                                     off + "+" + field);
        if (val == 0 || field < 1) {
            blank(buf, off, field);
            return;
        }
        
        int pad = field - 1;
        if (!right)
            buf[off] = val;
        else
            buf[off + pad] = val;
        
        int pads = !right ? off + 1 : off;
        for (int i = 0; i < pad; ++i)
            buf[pads + i] = ' ';
    }


    /**
     * Format an integer right-aligned into a fixed-width field.  MUCH faster
     * than String.format.
     * 
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
     * @param   val         The value to format.
     * @param   field       Width of the field to format in.  -1 means use
     *                      all available space.  The value will be padded
     *                      on the left with spaces if smaller than the field.
     * @param   signed      Iff true, add a sign character, space for
     *                      positive, '-' for negative.  This takes
     *                      up one place in the given field width.
     * @throws  ArrayIndexOutOfBoundsException  Buffer is too small.
     * @throws  IllegalArgumentException        Negative value given and
     *                                          signed == false.
     */
    public static final void formatInt(char[] buf, int off, int val,
                                       int field, boolean signed)
    {
        formatInt(buf, off, val, field, signed, false);
    }


    /**
     * Format an integer right-aligned into a fixed-width field.  MUCH faster
     * than String.format.
     * 
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
     * @param   val         The value to format.
     * @param   field       Width of the field to format in.  -1 means use
     *                      all available space.
     * @param   signed      Iff true, add a sign character, space for
     *                      positive, '-' for negative.  This takes
     *                      up one place in the given field width.
     * @param   lz          Iff true, pad with leading zeros; otherwise,
     *                      pad on the left with spaces.
     * @throws  ArrayIndexOutOfBoundsException  Buffer is too small.
     * @throws  IllegalArgumentException        Negative value given and
     *                                          signed == false.
     */
    public static final void formatInt(char[] buf, int off, int val,
                                       int field, boolean signed, boolean lz)
    {
        if (field < 0)
            field = buf.length - off;
        if (field < 0)
            field = 0;
        if (off + field > buf.length)
            throw new ArrayIndexOutOfBoundsException("Buffer [" + buf.length +
                                                     "] too small for " +
                                                     off + "+" + field);
        
        // If the value is negative but field is unsigned, just put in an
        // error indicator.
        if (!signed && val < 0) {
            formatChar(buf, off, '-', field, true);
            return;
        }
        
        int sign = val >= 0 ? 1 : -1;
        val *= sign;
        char schar = signed ? (sign < 0 ? '-' : ' ') : 0;
        try {
            formatInt(buf, off, val, field, schar, lz);
        } catch (OverflowException e) {
            formatChar(buf, off, '+', field, true);
        }
    }


    /**
     * Format an integer left-aligned into a fixed-width field.  MUCH faster
     * than String.format.
     * 
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
     * @param   val         The value to format.
     * @param   field       Width of the field to format in.  -1 means use
     *                      all available space.  If the value is smaller
     *                      than the field, pads on the right with space.
     * @param   signed      Iff true, add a sign character, space for
     *                      positive, '-' for negative.  This takes
     *                      up one place in the given field width, so positive
     *                      values will have a space on the left.  Iff false,
     *                      no space is taken for the sign; values must
     *                      be positive, and will begin in the first position.
     * @throws  ArrayIndexOutOfBoundsException  Buffer is too small.
     * @throws  IllegalArgumentException        Negative value given and
     *                                          signed == false.
     */
    public static final void formatIntLeft(char[] buf, int off, int val,
                                           int field, boolean signed)
    {
        if (field < 0)
            field = buf.length - off;
        if (field < 0)
            field = 0;
        if (off + field > buf.length)
            throw new ArrayIndexOutOfBoundsException("Buffer [" + buf.length +
                                                     "] too small for " +
                                                     off + "+" + field);
        
        // If the value is negative but field is unsigned, just put in an
        // error indicator.
        if (!signed && val < 0) {
            formatChar(buf, off, '-', field, false);
            return;
        }
        
        int sign = val >= 0 ? 1 : -1;
        val *= sign;
        char schar = signed ? (sign < 0 ? '-' : ' ') : 0;
        try {
            formatIntLeft(buf, off, val, field, schar);
        } catch (OverflowException e) {
            formatChar(buf, off, '+', field, false);
        }
    }


    /**
     * Format an unsigned integer into a fixed-width field in hexadecimal.
     * MUCH faster than String.format.
     * 
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
     * @param   val         The value to format.
     * @param   field       Width of the field to format in.  -1 means use
     *                      all available space.
     * @throws  ArrayIndexOutOfBoundsException  Buffer is too small.
     */
    public static final void formatHex(char[] buf, int off, int val, int field)
    {
        if (field < 0)
            field = buf.length - off;
        if (field < 0)
            field = 0;
        if (off + field > buf.length)
            throw new ArrayIndexOutOfBoundsException("Buffer [" + buf.length +
                                                     "] too small for " +
                                                     off + "+" + field);
        if (field < 1)
            throw new IllegalArgumentException("Field <" + field + "> too small");
   
        for (int i = off + field - 1; i >= off; --i) {
            int d = val % 16;
            buf[i] = d < 10 ? (char) ('0' + d) : (char) ('a' + d - 10);
            val /= 16;
        }
        
        // We should have used up all the value.  If not, then the value
        // doesn't fit; just put in an error indicator.
        if (val != 0)
            formatChar(buf, off, '+', field, true);
    }


    /**
     * Format a floating-point value into a fixed-width field, with sign.
     * MUCH faster than String.format.
     * 
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
     * @param   val         The value to format.
     * @param   field       Width of the field to format in.  -1 means use
     *                      all available space.
     * @param   frac        Number of digits after the decimal.
     * @throws  ArrayIndexOutOfBoundsException  Buffer is too small.
     * @throws  IllegalArgumentException        Negative value given and
     *                                          signed == false.
     */
    public static final void formatFloat(char[] buf, int off, double val,
                                         int field, int frac)
    {
        formatFloat(buf, off, val, field, frac, true);
    }


    /**
     * Format a floating-point value into a fixed-width field.  MUCH faster
     * than String.format.
     * 
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
     * @param   val         The value to format.
     * @param   field       Width of the field to format in.  -1 means use
     *                      all available space.
     * @param   frac        Number of digits after the decimal.
     * @param   signed      Iff true, add a sign character, space for
     *                      positive, '-' for negative.  This takes
     *                      up one place in the given field width.
     * @throws  ArrayIndexOutOfBoundsException  Buffer is too small.
     * @throws  IllegalArgumentException        Negative value given and
     *                                          signed == false.
     */
    public static final void formatFloat(char[] buf, int off, double val,
                                         int field, int frac, boolean signed)
    {
        if (field < 0)
            field = buf.length - off;
        if (field < 0)
            field = 0;
        if (off + field > buf.length)
            throw new ArrayIndexOutOfBoundsException("Buffer [" + buf.length +
                                                     "] too small for " +
                                                     off + "+" + field);

        // If the value is negative but field is unsigned, just put in an
        // error indicator.
        if (!signed && val < 0) {
            formatChar(buf, off, '-', field, true);
            return;
        }

        int intDigits = field - frac - 1;
        int sign = val >= 0 ? 1 : -1;
        val *= sign;
        char schar = signed ? (sign < 0 ? '-' : ' ') : 0;
        int intPart = (int) val;
        double fracPart = val - intPart;
        for (int i = 0; i < frac; ++i)
            fracPart *= 10;

        try {
            formatInt(buf, off, intPart, intDigits, schar, false);
            buf[off + intDigits] = '.';
            formatInt(buf, off + intDigits + 1, (int) fracPart, frac, (char) 0, true);
        } catch (OverflowException e) {
            formatChar(buf, off, '+', field, true);
        }
    }


    /**
     * Internal integer formatter.
     * 
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
     * @param   val         The value to format.  Must not be negative.
     * @param   field       Width of the field to format in.  Must not be
     *                      negative.
     * @param   schar       Iff not zero, add this sign character.  This takes
     *                      up one place in the given field width.
     * @param   leadZero    Iff true, pad on the left with leading zeros
     *                      instead of spaces.
     * @throws  IllegalArgumentException    Field width is too small.
     * @throws  OverflowException     Overflow: the value is too big to be
     *                      formatted into the field.
     */
    private static final void formatInt(char[] buf, int off, int val,
                                        int field, char schar, boolean leadZero)
        throws IllegalArgumentException, OverflowException
    {
        int intDigits = field - (schar != 0 ? 1 : 0);
        if (intDigits < 1)
            throw new IllegalArgumentException("Field <" + field + "> too small");
   
        int last = 0;
        for (int i = off + field - 1; i >= off + field - intDigits; --i) {
            if (val == 0 && !leadZero && i < off + field - 1) {
                buf[i] = ' ';
            } else {
                buf[i] = val == 0 ? '0' : (char) ('0' + val % 10);
                val /= 10;
                last = i;
            }
        }
        
        // We should have used up all the value.  If not, then the value
        // doesn't fit; just put in an error indicator.
        if (val != 0) {
            formatChar(buf, off, '+', field, true);
            throw new OverflowException();
        }

        if (schar != 0) {
            buf[off] = ' ';
            buf[last - 1] = schar;
        }
    }


    /**
     * Internal integer formatter for left-aligned values.
     * 
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
     * @param   val         The value to format.  Must not be negative.
     * @param   field       Width of the field to format in.  Must not be
     *                      negative.  If the number is narrower than the
     *                      field, it will be right-padded with space.
     * @param   schar       Iff not zero, add this sign character.  This takes
     *                      up one place in the given field width.
     * @throws  IllegalArgumentException    Field width is too small.
     * @throws  OverflowException     Overflow: the value is too big to be
     *                      formatted into the field.
     */
    private static final void formatIntLeft(char[] buf, int off, int val,
                                            int field, char schar)
        throws IllegalArgumentException, OverflowException
    {
        // Check that we have space for the sign and at least 1 digit.
        int intDigits = field - (schar != 0 ? 1 : 0);
        if (intDigits < 1)
            throw new IllegalArgumentException("Field <" + field + "> too small");
   
        // Count the digits in the value.
        int valDigits = 1;
        int v = val / 10;
        while (v > 0) {
            v /= 10;
            ++valDigits;
        }
        
        // If the value doesn't fit, just put in an error indicator and bail.
        if (intDigits < valDigits) {
            formatChar(buf, off, '+', field, false);
            throw new OverflowException();
        }

        // First, put in the sign if any.
        int index = off;
        if (schar != 0)
            buf[index++] = schar;

        // Now the digits.
        for (int i = index + valDigits - 1; i >= index; --i) {
            buf[i] = val == 0 ? '0' : (char) ('0' + val % 10);
            val /= 10;
        }
        index += valDigits;
        
        // Now pad out with spaces.
        while (index < off + field)
                buf[index++] = ' ';
    }


	/**
	 * Format an angle for user display in degrees and minutes.
	 * Negative angles are formatted with a "-" sign, as in
	 * "-171°15.165'".  Place the result in the supplied buffer.
     *
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
	 * @param	angle		The angle to format.
     * @return              Number of characters written.
     * @throws  ArrayIndexOutOfBoundsException  Buffer is too small.
	 */
	public static int formatDegMin(char[] buf, int off, double angle)
    {
		return formatDegMin(buf, off, angle, ' ', '-', false);
	}


	/**
	 * Format an angle as a string in the format
	 * "W171°15.165'".  Place the result in the supplied buffer.
	 * 
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
     * @param   angle       The angle to format.
	 * @param	pos			Sign character to use if positive.
	 * @param	neg			Sign character to use if negative.
     * @param   space       If true, leave a space after the sign and degrees.
     *                      Otherwise pack them.
     * @return              Number of characters written.
     * @throws  ArrayIndexOutOfBoundsException  Buffer is too small.
	 */
	public static int formatDegMin(char[] buf, int off, double angle,
									char pos, char neg, boolean space)
	{
        if (off + (space ? 14 : 12) > buf.length)
            throw new ArrayIndexOutOfBoundsException("Buffer [" + buf.length +
                                                     "] too small for " +
                                                     off + "+" + (space ? 14 : 12));

        int p = off;
	    
		if (angle < 0) {
			buf[p++] = neg;
			angle = -angle;
		} else
            buf[p++] = pos;
		if (space)
            buf[p++] = ' ';
		
		int deg = (int) angle;
		int min = (int) (angle * 60.0 % 60.0);
		int frac = (int) (angle * 60000.0 % 1000.0);
		
		try {
		    formatInt(buf, p, deg, 3, (char) 0, false);
		    p += 3;
		    buf[p++] = '°';
		    if (space)
		        buf[p++] = ' ';

		    formatInt(buf, p, min, 2, (char) 0, true);
		    p += 2;
		    buf[p++] = '.';

		    formatInt(buf, p, frac, 3, (char) 0, true);
		    p += 3;
		    buf[p++] = '\'';
		} catch (OverflowException e) {
		    formatChar(buf, p, '+', 1, true);
		}

        return p - off;
	}
	

	/**
	 * Format a latitude and longitude for user display in degrees and
	 * minutes.  Place the result in the supplied buffer.
	 *
     * @param   buf         Buffer to place the result in.
     * @param   off         Offset within buf to start writing at.
	 * @param	lat			The latitude.
	 * @param	lon			The longitude.
     * @param   space       If true, leave a space after the sign and degrees.
     *                      Otherwise pack them.
	 * @return				Number of characters written.
     * @throws  ArrayIndexOutOfBoundsException  Buffer is too small.
	 */
	public static int formatLatLon(char[] buf, int off,
	                               double lat, double lon, boolean space)
	{
        if (off + (space ? 29 : 25) > buf.length)
            throw new ArrayIndexOutOfBoundsException("Buffer [" + buf.length +
                                                     "] too small for " +
                                                     off + "+" + (space ? 29 : 25));

        int p = off;
        
		p += formatDegMin(buf, off, lat, 'N', 'S', space);
		buf[p++] = ' ';
		p += formatDegMin(buf, off, lon, 'E', 'W', space);
        
        return p - off;
	}

	
    // ******************************************************************** //
    // Private Classes.
    // ******************************************************************** //
	
	private static final class OverflowException extends Exception {
        private static final long serialVersionUID = -6009530000597939453L;
	}

}

