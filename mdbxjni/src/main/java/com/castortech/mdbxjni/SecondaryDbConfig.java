package com.castortech.mdbxjni;

public class SecondaryDbConfig extends DatabaseConfig implements Cloneable {
	private boolean allowPopulate = false;
	private boolean immutableSecondaryKey = false;
	private SecondaryKeyCreator keyCreator = DefaultSecondaryKeyCreator.getInstance();

	public SecondaryDbConfig() {
	}

	public void setAllowPopulate(final boolean allowPopulate) {
		this.allowPopulate = allowPopulate;
	}

	public boolean getAllowPopulate() {
		return allowPopulate;
	}

	public void setImmutableSecondaryKey(final boolean immutableSecondaryKey) {
		this.immutableSecondaryKey = immutableSecondaryKey;
	}

	public boolean getImmutableSecondaryKey() {
		return immutableSecondaryKey;
	}

	public void setKeyCreator(final SecondaryKeyCreator keyCreator) {
		this.keyCreator = keyCreator;
	}

	public SecondaryKeyCreator getKeyCreator() {
		return keyCreator;
	}
}