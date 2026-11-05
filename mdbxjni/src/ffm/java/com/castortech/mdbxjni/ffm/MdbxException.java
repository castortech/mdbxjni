package com.castortech.mdbxjni.ffm;

/**
 * Error reported by a libmdbx call made through the FFM binding.
 */
public class MdbxException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final int code;

	public MdbxException(int code, String message) {
		super(message + " (" + code + ")");
		this.code = code;
	}

	public int code() {
		return code;
	}
}
