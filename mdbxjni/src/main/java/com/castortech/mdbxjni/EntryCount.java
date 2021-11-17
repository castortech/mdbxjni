package com.castortech.mdbxjni;

public class EntryCount extends Entry {
	private final long valuesCount;

	public EntryCount(byte[] key, byte[] value, long valuesCount) {
		super(key, value);
		this.valuesCount = valuesCount;
	}

	public long getValuesCount() {
		return valuesCount;
	}
}