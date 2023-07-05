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
 * Creates and instantiates an unsigned 8-bit integer object
 *
 * @author Sun Microsystems, Inc.
 * @version 1.1 03/01/01
 * @since WBEM 1.0
 */
public class UnsignedInt8 extends Number {

	final static long serialVersionUID = 200;

	/**
	 * Initialize value fo this 8-bit integer object to short.
	 *
	 * @serial
	 */
	private Short value;

	/**
	 * The maximum value of this short.
	 *
	 * @serial
	 */
	public final static short MAX_VALUE = 0xff;

	/**
	 * The minimum value of this short.
	 *
	 * @serial
	 */
	public final static short MIN_VALUE = 0;

	/**
	 * Constructor creates an unsigned 8-bit integer object for the specified short value. Only the bottom 8
	 * bits are considered.
	 *
	 * @param a
	 *          the short to be represented as an unsigned 8-bit integer object
	 *
	 */
	public UnsignedInt8(short a) {
		if ((a < MIN_VALUE) || (a > MAX_VALUE)) {
			throw new NumberFormatException();
		}
		value = new Short(a);
	}

	/**
	 * Constructor creates an unsigned 8-bit integer object for the specified array of bytes. Only the bottom 8
	 * bits are considered.
	 */
	public UnsignedInt8(byte[] bval) throws NumberFormatException {
		this(new BigInteger(bval).shortValue());
	}

	/**
	 * Constructor creates an unsigned 8-bit integer object for the specified string. Only the bottom 8 bits are
	 * considered.
	 *
	 * @param a
	 *          the string to be represented as an unsigned 8-bit integer
	 *
	 */
	public UnsignedInt8(String a) throws NumberFormatException {
		Short temp = new Short(a);
		short shortValue = temp.shortValue();
		if ((shortValue < MIN_VALUE) || (shortValue > MAX_VALUE)) {
			throw new NumberFormatException();
		}
		value = new Short(shortValue);
	}

	/**
	 * Returns the value of this unsigned 8-bit integer object as a byte
	 *
	 * @return byte the byte value of this unsigned 8-bit integer object
	 *
	 */
	@Override
	public byte byteValue() {
		return value.byteValue();
	}

	/**
	 * Returns the value of this unsigned 8-bit integer object as a short
	 *
	 * @return short value of this unsigned 8-bit integer object as a short
	 *
	 */
	@Override
	public short shortValue() {
		return value.shortValue();
	}

	/**
	 * Returns the value of this unsigned 8-bit integer object as an int
	 *
	 * @return int value of this unsigned 8-bit integer object as an int
	 *
	 */
	@Override
	public int intValue() {
		return value.intValue();
	}

	/**
	 * Returns the value of this unsigned 8-bit integer object as a long
	 *
	 * @return long value of this unsigned 8-bit integer object as a long
	 *
	 */
	@Override
	public long longValue() {
		return value.longValue();
	}

	/**
	 * Returns the value of this unsigned 8-bit integer object as a float
	 *
	 * @return float value of this unsigned 8-bit integer object as a float
	 *
	 */
	@Override
	public float floatValue() {
		return value.floatValue();
	}

	/**
	 * Returns the value of this unsigned 8-bit integer object as a double
	 *
	 * @return double value of this unsigned 8-bit integer object as a double
	 *
	 */
	@Override
	public double doubleValue() {
		return value.doubleValue();
	}

	/**
	 * Returns the text representation of this unsigned 8-bit integer object
	 *
	 * @return String text representation of this unsigned 8-bit integer
	 *
	 */
	@Override
	public String toString() {
		return value.toString();
	}

	/**
	 * Computes the hash code for this unsigned 8-bit integer object
	 *
	 * @return int the integer representing the hash code for this unsigned 8-bit integer object
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * Compares this unsigned 8-bit integer object with the specified object for equality
	 *
	 * @return boolean true if the specified object is an unsigned 8-bit integer object. Otherwise, false.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof UnsignedInt8)) {
			return false;
		}
		return (((UnsignedInt8)o).value.equals(value));
	}
}
