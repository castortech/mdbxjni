package com.castortech.mdbxjni;

import java.util.Comparator;

public class EncryptedKeyComparator implements Comparator<byte[]> {
	EncryptUtils encrypt;
	
	public EncryptedKeyComparator(EncryptUtils encrypt) throws Exception {
		super();
    this.encrypt = encrypt;
	}

	@Override
	public int compare(byte[] v1, byte[] v2) {
		return compareUnsignedBytes(v1, v2);
	}

	private int compareUnsignedBytes(byte[] key1, byte[] key2) {
		if (key1.length < 4 || key2.length < 4) {
			throw new RuntimeException("Encrypted data is a minimum of 4 bytes");
		}
		
		//compare the client id part of the keys
		int comp = compareUnsignedBytes(key1, 2, 2, key2, 2, 2);
		
		if (comp == 0) {
			try {
				byte[] k1 = encrypt.decryptWithInfo(key1).array();
				byte[] k2 = encrypt.decryptWithInfo(key2).array();
				comp = compareUnsignedBytes(k1, 0, k1.length, k2, 0, k2.length);
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Exception decrypting key data", e);
			}
		}
		
		return comp;
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
