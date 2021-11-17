package com.castortech.mdbxjni;

public class FlagState {
	private final int flag;
	private final int state;

	public FlagState(int flag, int state) {
		super();
		this.flag = flag;
		this.state = state;
	}

	public int getFlag() {
		return flag;
	}

	public int getState() {
		return state;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "FlagState [" +
				"flag=" + flag +
				", state=" + state +
				"]";
	}
}