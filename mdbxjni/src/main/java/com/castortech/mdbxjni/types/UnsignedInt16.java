package com.castortech.mdbxjni.types;

/*
 *EXHIBIT A - Sun Industry Standards Source License
 *
 *"The contents of this file are subject to the Sun Industry
 *Standards Source License Version 1.2 (the "License");
 *You may not use this file except in compliance with the
 *License. You may obtain a copy of the
 *License at http://wbemservices.sourceforge.net/license.html
 *
 *Software distributed under the License is distributed on
 *an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either
 *express or implied. See the License for the specific
 *language governing rights and limitations under the License.
 *
 *The Original Code is WBEM Services.
 *
 *The Initial Developer of the Original Code is:
 *Sun Microsystems, Inc.
 *
 *Portions created by: Sun Microsystems, Inc.
 *are Copyright � 2001 Sun Microsystems, Inc.
 *
 *All Rights Reserved.
 *
 *Contributor(s): _______________________________________
 */

import java.math.BigInteger;

/**
 *
 * Creates and instantiates an unsigned 16-bit integer object
 *
 * @author Sun Microsystems, Inc.
 * @version 1.1 03/01/01
 * @since WBEM 1.0
 */
public class UnsignedInt16 extends Number {

	static final long serialVersionUID = 200;

	private Integer value;

	/**
	 * the maximum value this int can have
	 */
	public static final int MAX_VALUE = 0xffff;

	/**
	 * the minimum value this int can have
	 */
	public static final int MIN_VALUE = 0;

	public static boolean IsLittleEndian = false;
	public static boolean IsBigEndian = true;

	/**
	 * Constructor creates an unsigned 16-bit integer object for the specified int value. Only the bottom 16
	 * bits are considered.
	 *
	 * @param a
	 *          the integer to be represented as an unsigned 16-bit integer object
	 *
	 */
	public UnsignedInt16(int a) {
		if ((a < MIN_VALUE) || (a > MAX_VALUE)) {
			throw new NumberFormatException();
		}
		value = Integer.valueOf(a);
	}

	/**
	 * Constructor creates an unsigned 16-bit integer object for the specified array of bytes. Only the bottom
	 * 16 bits are considered.
	 */
	public UnsignedInt16(byte[] bval) throws NumberFormatException {
		this(new BigInteger(bval).intValue());
	}

	/**
	 * Constructor creates an unsigned 16-bit integer object for the specified array of bytes. Only the bottom
	 * 16 bits are considered.
	 */
	public UnsignedInt16(byte[] bval, boolean isBigEndian) throws NumberFormatException {
		if (!isBigEndian) {
			ByteUtil.reverseByteOrderInPlace(bval);
		}
		int a = new BigInteger(bval).intValue();
		if ((a < MIN_VALUE) || (a > MAX_VALUE)) {
			throw new NumberFormatException();
		}
		value = Integer.valueOf(a);
	}

	/**
	 * Constructor creates an unsigned 16-bit integer object for the specified string. Only the bottom 16 bits
	 * are considered.
	 *
	 * @param a
	 *          the string to be represented as an unsigned 16-bit integer
	 *
	 */
	public UnsignedInt16(String a) throws NumberFormatException {
		Integer temp = Integer.valueOf(a);
		int iValue = temp.intValue();
		if ((iValue < MIN_VALUE) || (iValue > MAX_VALUE)) {
			throw new NumberFormatException();
		}
		value = Integer.valueOf(iValue);
	}

	/**
	 * Returns the value of this unsigned 16-bit integer object as a byte This method returns the least
	 * significant 8 bits.
	 *
	 * @return byte the byte value of this unsigned 16-bit integer object
	 *
	 */
	@Override
	public byte byteValue() {
		return value.byteValue();
	}

	/**
	 * Returns the value of this unsigned 16-bit integer object as a short
	 *
	 * @return short value of this unsigned 16-bit integer object as a short
	 *
	 */
	@Override
	public short shortValue() {
		return value.shortValue();
	}

	/**
	 * Returns the value of this unsigned 16-bit integer object as an int
	 *
	 * @return int value of this unsigned 16-bit integer object as an int
	 *
	 */
	@Override
	public int intValue() {
		return value.intValue();
	}

	// little endian
	public byte[] byteArrayValue() {
		int i = intValue();
		byte[] buf = new byte[2];
		buf[0] = (byte)(i & 0x00FF);
		buf[1] = (byte)((i & 0xFF00) >> 8);
		return buf;
	}

	/**
	 * Returns the value of this unsigned 16-bit integer object as a long
	 *
	 * @return long value of this unsigned 16-bit integer object as a long
	 *
	 */
	@Override
	public long longValue() {
		return value.longValue();
	}

	/**
	 * Returns the value of this unsigned 16-bit integer object as a float
	 *
	 * @return float value of this unsigned 16-bit integer object as a float
	 *
	 */
	@Override
	public float floatValue() {
		return value.floatValue();
	}

	/**
	 * Returns the value of this unsigned 16-bit integer object as a double
	 *
	 * @return double value of this unsigned 16-bit integer object as a double
	 *
	 */
	@Override
	public double doubleValue() {
		return value.doubleValue();
	}

	/**
	 * Returns the text representation of this unsigned 16-bit integer object
	 *
	 * @return String text representation of this unsigned 16-bit integer
	 *
	 */
	@Override
	public String toString() {
		return value.toString();
	}

	/**
	 * Computes the hash code for this unsigned 16-bit integer object
	 *
	 * @return int the integer representing the hash code for this unsigned 16-bit integer object
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * Compares this unsigned 16-bit integer object with the specified object for equality
	 *
	 * @return boolean true if the specified object is an unsigned 16-bit integer . Otherwise, false.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof UnsignedInt16)) {
			return false;
		}
		return (((UnsignedInt16)o).value.equals(value));
	}
}