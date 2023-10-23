package com.castortech.mdbxjni;

/**
 * Represents an pair of objects. This class is immutable.
 *
 * @param <A>
 *          the type of the first object.
 * @param <B>
 *          the type of the second object.
 * @author Oliver Wong (owong@castortech.com)
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IPair<A, B> {
	/**
	 * Returns the first object.
	 *
	 * @return the first object.
	 */
	public A getA();

	/**
	 * Returns the second object.
	 *
	 * @return the second object.
	 */
	public B getB();

	/**
	 * This method should follow the same contract as {@link #equals(Pair)}.
	 *
	 * @param obj
	 *          the object being compared against.
	 * @return a value consistent with the contract in {@link #equals(Pair)}.
	 */
	@Override
	public boolean equals(final Object obj);

	/**
	 * <p>
	 * Returns true if this instance of AbstractPair is considered equal to "that" instance of AbstractPair,
	 * false otherwise. The contract is as follows:
	 * </p>
	 *
	 * <ul>
	 * <li>if the that == null, then this method returns false.</li>
	 * <li>if this.getA() == null and that.getA() != null, then this method returns false.</li>
	 * <li>if this.getA() != null and this.getA().equals(that.getA()) == false, then this method returns false.</li>
	 * <li>if this.getB() == null and that.getB() != null, then this method returns false.</li>
	 * <li>if this.getB() != null and this.getB().equals(that.getB()) == false, then this method returns false.</li>
	 * <li>In all other cases, this method returns true.
	 * </ul>
	 *
	 * @param that
	 *          the other instance for AbstractPair to compare against.
	 * @return true if this instance of AbstractPair is considered equal to "that" instance of AbstractPair,
	 *         false otherwise.
	 */
	public boolean equals(final IPair<?, ?> that);
}