package com.castortech.mdbxjni;

public class EnvOption {
	private final int option;
	private final long value;

	public EnvOption(int option, long value) {
		this.option = option;
		this.value = value;
	}

	public int getOption() {
		return option;
	}

	public long getValue() {
		return value;
	}
}