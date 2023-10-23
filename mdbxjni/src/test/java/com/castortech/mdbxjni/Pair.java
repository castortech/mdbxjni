package com.castortech.mdbxjni;

import java.io.Serializable;

/**
 * Client code may freely create instances of this class.
 *
 * @see IPair
 *      for contract details (including immutability).
 *
 * @author Oliver Wong (owong@castortech.com)
 * @noextend This class is not intended to be subclassed by clients.
 */
public class Pair<A, B> implements IPair<A, B>, Serializable {
	private static final long serialVersionUID = 4587144988967892149L;

	public static <T, U> Pair<T, U> create(T a, U b) {
		return new Pair<>(a, b);
	}

	public A a;
	public B b;

	public Pair() {
	}

	public Pair(final A a, final B b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public final A getA() {
		return a;
	}

	@Override
	public final B getB() {
		return b;
	}

	/**
	 * @deprecated reserved for use by the serializer.
	 */
	@Deprecated
	public void setA(A a) {
		this.a = a;
	}

	/**
	 * @deprecated reserved for use by the serializer.
	 */
	@Deprecated
	public void setB(B b) {
		this.b = b;
	}

	@SuppressWarnings("nls")
	@Override
	public final String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("A<");

		if (getA() != null) {
			sb.append(getA().getClass().getSimpleName());
			sb.append(">:");
			sb.append(getA().toString());
		}
		else {
			sb.append("null>");
		}

		sb.append(",B<");

		if (getB() != null) {
			sb.append(getB().getClass().getSimpleName());
			sb.append(">:");
			sb.append(getB().toString());
		}
		else {
			sb.append("null>"); //$NON-NLS-1$
		}

		return sb.toString();
	}

	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof IPair<?, ?>) {
			return equals((IPair<?, ?>)obj);
		}
		return false;
	}

	@Override
	public final boolean equals(final IPair<?, ?> that) {
		/*
		 * Note: If ever we need a new class for which the above contract is true, we should create a new class
		 * hierarchy, rather modify this contract.
		 */
		if (that == null) {
			return false;
		}
		if (getA() == null) {
			if (that.getA() != null) {
				return false;
			}
		}
		else {
			if (!getA().equals(that.getA())) {
				return false;
			}
		}

		if (getB() == null) {
			if (that.getB() != null) {
				return false;
			}
		}
		else {
			if (!getB().equals(that.getB())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public final int hashCode() {
		final int hashcodeA, hashcodeB;
		hashcodeA = getA() == null ? 0 : getA().hashCode();
		hashcodeB = getB() == null ? 0 : getB().hashCode();
		return hashcodeA ^ hashcodeB;
	}
}