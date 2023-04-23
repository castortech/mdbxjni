package com.castortech.mdbxjni;

import java.util.Comparator;

public class DatabaseConfig implements Cloneable {
	/** @see JNI#MDBX_REVERSEKEY */
	private boolean reverseKey = false;
	/** @see JNI#MDBX_DUPSORT */
	private boolean dupSort = false;
	/** @see JNI#MDBX_DUPFIXED */
	private boolean dupFixed = false;
	/** @see JNI#MDBX_INTEGERKEY */
	private boolean integerKey = false;
	/** @see JNI#MDBX_INTEGERDUP */
	private boolean integerDup = false;
	/** @see JNI#MDBX_REVERSEDUP */
	private boolean reverseDup = false;
	/** @see JNI#MDBX_CREATE */
	private boolean create = false;
	/** @see JNI#MDBX_ACCEDE */
	private boolean accede = false;

	private Comparator<byte[]> keyComparator;
	private Comparator<byte[]> dataComparator;

	public DatabaseConfig() {
	}

	public DatabaseConfig(int flags) {
		if ((flags & Constants.REVERSEKEY) == Constants.REVERSEKEY) {
			setReverseKey(true);
		}

		if ((flags & Constants.REVERSEDUP) == Constants.REVERSEDUP) {
			setReverseDup(true);
		}

		if ((flags & Constants.DUPSORT) == Constants.DUPSORT) {
			setDupSort(true);
		}

		if ((flags & Constants.DUPFIXED) == Constants.DUPFIXED) {
			setDupFixed(true);
		}

		if ((flags & Constants.INTEGERKEY) == Constants.INTEGERKEY) {
			setIntegerKey(true);
		}

		if ((flags & Constants.INTEGERDUP) == Constants.INTEGERDUP) {
			setIntegerDup(true);
		}

		if ((flags & Constants.CREATE) == Constants.CREATE) {
			setCreate(true);
		}

		if ((flags & EnvFlags.ACCEDE) == EnvFlags.ACCEDE) {
			setAccede(true);
		}
	}

	/** @see JNI#MDBX_REVERSEKEY */
	public boolean isReverseKey() {
		return reverseKey;
	}

	public void setReverseKey(boolean reverseKey) {
		this.reverseKey = reverseKey;
	}

	/** @see JNI#MDBX_DUPSORT */
	public boolean isDupSort() {
		return dupSort;
	}

	public void setDupSort(boolean dupSort) {
		this.dupSort = dupSort;
	}

	/** @see JNI#MDBX_DUPFIXED */
	public boolean isDupFixed() {
		return dupFixed;
	}

	public void setDupFixed(boolean dupFixed) {
		this.dupFixed = dupFixed;
	}

	/** @see JNI#MDBX_INTEGERKEY */
	public boolean isIntegerKey() {
		return integerKey;
	}

	public void setIntegerKey(boolean integerKey) {
		this.integerKey = integerKey;
	}

	/** @see JNI#MDBX_INTEGERDUP */
	public boolean isIntegerDup() {
		return integerDup;
	}

	public void setIntegerDup(boolean integerDup) {
		this.integerDup = integerDup;
	}

	/** @see JNI#MDBX_REVERSEDUP */
	public boolean isReverseDup() {
		return reverseDup;
	}

	public void setReverseDup(boolean reverseDup) {
		this.reverseDup = reverseDup;
	}

	/** @see JNI#MDBX_CREATE */
	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	/** @see JNI#MDBX_ACCEDE */
	public boolean isAccede() {
		return accede;
	}

	public void setAccede(boolean accede) {
		this.accede = accede;
	}

	public Comparator<byte[]> getKeyComparator() {
		return keyComparator;
	}

	public void setKeyComparator(Comparator<byte[]> keyComparator) {
		this.keyComparator = keyComparator;
	}

	public Comparator<byte[]> getDataComparator() {
		return dataComparator;
	}

	public void setDataComparator(Comparator<byte[]> dataComparator) {
		this.dataComparator = dataComparator;
	}

	/**
	 * Returns a copy of this configuration object.
	 */
	public DatabaseConfig cloneConfig() {
		try {
			return (DatabaseConfig)super.clone();
		}
		catch (CloneNotSupportedException willNeverOccur) {
			return null;
		}
	}
}