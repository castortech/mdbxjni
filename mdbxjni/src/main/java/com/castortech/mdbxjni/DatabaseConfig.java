package com.castortech.mdbxjni;

import java.util.Comparator;

public class DatabaseConfig implements Cloneable {
	private boolean reverseKey = false;
	private boolean dupSort = false;
	private boolean dupFixed = false;
	private boolean integerKey = false;
	private boolean integerDup = false;
	private boolean reverseDup = false;
	private boolean create = false;
	private Comparator<byte[]> comparator;
	
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
	}
	
	public boolean isReverseKey() {
		return reverseKey;
	}
	
	public void setReverseKey(boolean reverseKey) {
		this.reverseKey = reverseKey;
	}
	
	public boolean isDupSort() {
		return dupSort;
	}
	
	public void setDupSort(boolean dupSort) {
		this.dupSort = dupSort;
	}
	
	public boolean isDupFixed() {
		return dupFixed;
	}
	
	public void setDupFixed(boolean dupFixed) {
		this.dupFixed = dupFixed;
	}
	
	public boolean isIntegerKey() {
		return integerKey;
	}
	
	public void setIntegerKey(boolean integerKey) {
		this.integerKey = integerKey;
	}
	
	public boolean isIntegerDup() {
		return integerDup;
	}
	
	public void setIntegerDup(boolean integerDup) {
		this.integerDup = integerDup;
	}
	
	public boolean isReverseDup() {
		return reverseDup;
	}
	
	public void setReverseDup(boolean reverseDup) {
		this.reverseDup = reverseDup;
	}
	
	public boolean isCreate() {
		return create;
	}
	
	public void setCreate(boolean create) {
		this.create = create;
	}
	
	public Comparator<byte[]> getComparator() {
		return null;
	}
	
	public void setComparator(Comparator<byte[]> comparator) {
		this.comparator = comparator;
		throw new IllegalStateException("Setting of custom comparator is presently not supported");
	}
	
    /**
     * Returns a copy of this configuration object.
     */
    public DatabaseConfig cloneConfig() {
        try {
            return (DatabaseConfig) super.clone();
        } 
        catch (CloneNotSupportedException willNeverOccur) {
            return null;
        }
    }
}
