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
 * Creates and instantiates an unsigned 64-bit integer object. The CIMDataType class uses this class to
 * instantiate valid CIM data types.
 *
 *
 * @author Sun Microsystems, Inc.
 * @version 1.1 03/01/01
 * @since WBEM 1.0
 */
public class UnsignedInt64 extends Number implements Comparable<Object> {

	final static long serialVersionUID = 200;

	/**
	 * the maximum value this BigInteger can have
	 */
	public final static BigInteger MAX_VALUE = new BigInteger("18446744073709551615"); //$NON-NLS-1$

	/**
	 * the minimum value this BigInteger can have
	 */
	public final static BigInteger MIN_VALUE = new BigInteger("0"); //$NON-NLS-1$

	/**
	 * Big integer to delegate to. This class stores its value in the bigInt.
	 */
	private BigInteger bigInt;

	/**
	 * Constructor creates an unsigned 64-bit integer object for the specified string. Only the bottom 64 bits
	 * are considered.
	 */
	public UnsignedInt64(String sval) throws NumberFormatException {
		bigInt = new BigInteger(sval);

		if ((bigInt.compareTo(MIN_VALUE) < 0) || (bigInt.compareTo(MAX_VALUE) > 0)) {
			throw new NumberFormatException();
		}
	}

	/**
	 * Constructor creates an unsigned 64-bit integer object for the specified array of bytes. Only the bottom
	 * 64 bits are considered.
	 */
	public UnsignedInt64(byte[] bval) throws NumberFormatException {
		bigInt = new BigInteger(bval);

		if ((bigInt.compareTo(MIN_VALUE) < 0) || (bigInt.compareTo(MAX_VALUE) > 0)) {
			throw new NumberFormatException();
		}
	}

	/**
	 * Constructor creates an unsigned 64-bit integer object for the specified BigInteger. Only the bottom 64
	 * bits are considered.
	 */
	public UnsignedInt64(BigInteger input) {
		bigInt = new BigInteger(input.toString());
		if ((bigInt.compareTo(MIN_VALUE) < 0) || (bigInt.compareTo(MAX_VALUE) > 0)) {
			throw new NumberFormatException();
		}
	}

	/**
	 * Compares this unsigned 64-bit integer object with the specified object for equality
	 *
	 * @return boolean true if the specified object is an unsigned 64-bit integer object. Otherwise, false.
	 *
	 */
	@Override
	public boolean equals(Object o) {
		try {
			UnsignedInt64 u = (UnsignedInt64)o;
			return u.bigInt.equals(bigInt);
		}
		catch (ClassCastException ce) {
			// This was not an UnsignedInt64, so equals should fail.
			return false;
		}
	}

	public BigInteger bigIntValue() {
		return bigInt;
	}

	/**
	 * Returns the value of the specified number as an <code>int</code>. This may involve rounding.
	 *
	 * @return the numeric value represented by this object after conversion to type <code>int</code>.
	 */
	@Override
	public int intValue() {
		return bigInt.intValue();
	}

	/**
	 * Returns the value of the specified number as a <code>long</code>. This may involve rounding.
	 *
	 * @return the numeric value represented by this object after conversion to type <code>long</code>.
	 */
	@Override
	public long longValue() {
		return bigInt.longValue();
	}

	/**
	 * Returns the value of the specified number as a <code>double</code>. This may involve rounding.
	 *
	 * @return the numeric value represented by this object after conversion to type <code>double</code>.
	 */
	@Override
	public double doubleValue() {
		return bigInt.doubleValue();
	}

	/**
	 * Returns the value of the specified number as a <code>float</code>. This may involve rounding.
	 *
	 * @return the numeric value represented by this object after conversion to type <code>float</code>.
	 */
	@Override
	public float floatValue() {
		return bigInt.floatValue();
	}

	/**
	 * Compares this UnsignedInt64 with the specified UnsignedInt64. This method is provided in preference to
	 * individual methods for each of the six boolean comparison operators (&lt;, ==, &gt;, &gt;=, !=, &lt;=).
	 * The suggested idiom for performing these comparisons is: <tt>(x.compareTo(y)</tt> &lt;<i>op</i>&gt;
	 * <tt>0)</tt>, where &lt;<i>op</i>&gt; is one of the six comparison operators.
	 *
	 * @param val
	 *          Object to which this UnsignedInt64 is to be compared. Throws a ClassCastException if the input
	 *          object is not an UnsignedInt64.
	 * @return -1, 0 or 1 as this UnsignedInt64 is numerically less than, equal to, or greater than <tt>val</tt>
	 *         .
	 */
	@Override
	public int compareTo(Object val) {
		return bigInt.compareTo(((UnsignedInt64)val).bigInt);
	}

	/**
	 * Returns the text representation of this unsigned 64-bit integer object
	 *
	 * @return String text representation of this unsigned 64-bit integer
	 *
	 */
	@Override
	public String toString() {
		return bigInt.toString();
	}

	/**
	 * Returns the hash code for this UnsignedInt64.
	 *
	 * @return hash code for this UnsignedInt64.
	 */
	@Override
	public int hashCode() {
		return bigInt.hashCode();
	}
}
