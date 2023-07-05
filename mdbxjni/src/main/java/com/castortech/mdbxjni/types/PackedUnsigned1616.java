package com.castortech.mdbxjni.types;

public final class PackedUnsigned1616 {
	private static final int RIGHT = 0xFFFF;

	private final int field;

	public PackedUnsigned1616(int field) {
		this.field = field;
	}

	public PackedUnsigned1616(int left, int right) {
		field = (left << 16) | (right & RIGHT);
	}

	public int getLeft() {
		return field >>> 16; // >>> operator 0-fills from left
	}

	public int getRight() {
		return field & RIGHT;
	}

	public int getPackedValue() {
		return field;
	}
}