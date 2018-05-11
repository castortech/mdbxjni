package com.castortech.mdbxjni;

import java.util.Comparator;

public class KeyComparator implements Comparator<byte[]> {
	public KeyComparator() throws Exception {
		super();
	}

	@Override
	public int compare(byte[] v1, byte[] v2) {
		return compareUnsignedBytes(v1, v2);
	}

	private int compareUnsignedBytes(byte[] key1, byte[] key2) {
		return compareUnsignedBytes(key1, 0, key1.length, key2, 0, key2.length);
	}

	/**
	 * Compare using a default unsigned byte comparison.
	 */
	private static int compareUnsignedBytes(byte[] key1, int off1, int len1, byte[] key2, int off2, int len2) {
		int limit = Math.min(len1, len2);

		for (int i = 0; i < limit; i++) {
			byte b1 = key1[i + off1];
			byte b2 = key2[i + off2];
			if (b1 == b2) {
				continue;
			}
			else {
				/*
				 * Remember, bytes are signed, so convert to shorts so that we effectively do an unsigned byte
				 * comparison.
				 */
				return (b1 & 0xff) - (b2 & 0xff);
			}
		}

		return len1 - len2;
	}
}
