package com.castortech.mdbxjni;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.spec.IvParameterSpec;

/**
 * see: https://stackoverflow.com/questions/37077758/aes-256-encryption-decryption-without-iv
 * 
 * @author Alain Picard
 *
 */
public class IvProvider {
	private static final int IV_CNT = Short.MAX_VALUE * 2; //64k keys
  private static final int IV_LENGTH = 16; // in bytes
  private static final byte[][] ivs = new byte[IV_CNT][];

  private static boolean loaded = false;
  
	public IvProvider(File ivFile) throws Exception {
		super();
		init(ivFile);
	}
	
	private void init(File ivFile) throws Exception {
		synchronized (this) {
			if (loaded) {
				return;
			}

			loaded = true;
		}

		if (ivFile.exists()) {
			FileInputStream fis = new FileInputStream(ivFile);
			DataInputStream dis = new DataInputStream(fis);
	
			try {
				int i = 0;
				while (dis.available() > 0) {
		  		byte[] iv = new byte[IV_LENGTH];
		  		dis.read(iv);
		  		ivs[i++] = iv;
				}
			}
			finally {
				if (dis != null) {
					dis.close();
				}
			}
		}
		
		if (ivs[0] == null) {
	    System.out.println("Generating IVS"); //$NON-NLS-1$
			FileOutputStream fos = new FileOutputStream(ivFile);
			DataOutputStream dos = new DataOutputStream(fos);
			SecureRandom secureRandom = SecureRandom.getInstanceStrong();

			try {
		    for (int i = 0; i < IV_CNT; i++) {
		  		byte[] iv = new byte[IV_LENGTH];
		  		if (i > 0)  //0 is reserved for no IV for unencrypted data
		  			secureRandom.nextBytes(iv);
		      ivs[i] = iv;
		      dos.write(iv);
		    }
			}
			finally {
				if (dos != null) {
			    dos.flush();
			    dos.close();
				}
			}
		}
	}

	public int getRandomIvKey() {
		return ThreadLocalRandom.current().nextInt(IV_CNT -1) + 1;  //shift to never return 0
	}
	
	public IvParameterSpec getParamSpec(int ivKey) {
		assert ivKey > 0 && ivKey < IV_CNT;
		IvParameterSpec paramSpec = new IvParameterSpec(ivs[ivKey]);
		return paramSpec;
	}
}