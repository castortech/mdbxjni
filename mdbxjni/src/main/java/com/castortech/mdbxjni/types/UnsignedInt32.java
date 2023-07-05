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
 *
 * Creates and instantiates an unsigned 32-bit integer object. The CIMDataType class uses this class to
 * instantiate valid CIM data types.
 *
 *
 * @author Sun Microsystems, Inc.
 * @version 1.1 03/01/01
 * @since WBEM 1.0
 */
public class UnsignedInt32 extends Number {
	static final long serialVersionUID = 200;

	private Long value;

	/**
	 * the maximum value this long can have
	 */
	public static final long MAX_VALUE = 0xffffffffL;

	/**
	 * the minimum value this long can have
	 */
	public static final long MIN_VALUE = 0;

	/**
	 * Constructor creates an unsigned 32-bit integer object for the specified array of bytes. Only the bottom
	 * 32 bits are considered.
	 */
	public UnsignedInt32(byte[] bval) throws NumberFormatException {
		long a = new BigInteger(bval).longValue();

		if ((a < MIN_VALUE) || (a > MAX_VALUE)) {
			throw new NumberFormatException();
		}

		value = new Long(a);
	}

	public static boolean IsLittleEndian = false;
	public static boolean IsBigEndian = true;

	/**
	 * Constructor creates an unsigned 32-bit integer object for the specified array of bytes. Only the bottom
	 * 32 bits are considered.
	 */
	public UnsignedInt32(byte[] bval, boolean isBigEndian) throws NumberFormatException {
		if (!isBigEndian) {
			ByteUtil.reverseByteOrderInPlace(bval);
		}

		long a = new BigInteger(bval).longValue();

		if ((a < MIN_VALUE) || (a > MAX_VALUE)) {
			throw new NumberFormatException();
		}

		value = new Long(a);
	}

	/**
	 * Constructor creates an unsigned 32-bit integer object for the specified long value. Only the bottom 32
	 * bits are considered.
	 */
	public UnsignedInt32(long a) {
		if ((a < MIN_VALUE) || (a > MAX_VALUE)) {
			throw new NumberFormatException();
		}

		value = new Long(a);
	}

	/**
	 * Constructor creates an unsigned 32-bit integer object for the specified string. Only the bottom 32 bits
	 * are considered.
	 */
	public UnsignedInt32(String a) throws NumberFormatException {
		Long temp = new Long(a);
		long longValue = temp.longValue();

		if ((longValue < MIN_VALUE) || (longValue > MAX_VALUE)) {
			throw new NumberFormatException();
		}

		value = new Long(longValue);
	}

	/**
	 * Returns the value of this unsigned 32-bit integer object as a byte. This method returns the least
	 * significant 8 bits.
	 *
	 * @return byte the byte value of this unsigned 32-bit integer object
	 *
	 */
	@Override
	public byte byteValue() {
		return value.byteValue();
	}

	/**
	 * Returns the value of this unsigned 32-bit integer object as a short This method returns the least
	 * significant 16 bits.
	 *
	 * @return short value of this unsigned 32-bit integer object as a short
	 *
	 */
	@Override
	public short shortValue() {
		return value.shortValue();
	}

	/**
	 * Returns the value of this unsigned 32-bit integer object as an int This method returns the least
	 * significant 32 bits.
	 *
	 * @return int value of this unsigned 32-bit integer object as an int
	 *
	 */
	@Override
	public int intValue() {
		return value.intValue();
	}

	/**
	 * Returns the value of this unsigned 32-bit integer object as a long This method returns the least
	 * significant 64 bits.
	 *
	 * @return long value of this unsigned 32-bit integer object as a long
	 *
	 */
	@Override
	public long longValue() {
		return value.longValue();
	}

	// little endian
	public byte[] byteArrayValue() {
		long l = longValue();
		byte[] buf = new byte[4];
		buf[0] = (byte)((l & 0x000000FF));
		buf[1] = (byte)((l & 0x0000FF00) >> 8);
		buf[2] = (byte)((l & 0x00FF0000) >> 16);
		buf[3] = (byte)((l & 0xFF000000) >> 24);
		return buf;
	}

	/**
	 * Returns the value of this unsigned 32-bit integer object as a float
	 *
	 * @return float value of this unsigned 32-bit integer object as a float
	 *
	 */
	@Override
	public float floatValue() {
		return value.floatValue();
	}

	/**
	 * Returns the value of this unsigned 32-bit integer object as a double
	 *
	 * @return double value of this unsigned 32-bit integer object as a double
	 *
	 */
	@Override
	public double doubleValue() {
		return value.doubleValue();
	}

	/**
	 * Returns the text representation of this unsigned 32-bit integer object
	 *
	 * @return String text representation of this unsigned 32-bit integer
	 *
	 */
	@Override
	public String toString() {
		return value.toString();
	}

	/**
	 * Computes the hash code for this unsigned 32-bit integer object
	 *
	 * @return int the integer representing the hash code for this unsigned 32-bit integer
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * Compares this unsigned 32-bit integer object with the specified object for equality
	 *
	 * @return boolean true if the specified object is an unsigned 32-bit integer. Otherwise, false.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof UnsignedInt32)) {
			return false;
		}
		return (((UnsignedInt32)o).value.equals(value));
	}
}