package com.castortech.mdbxjni;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SecretProvider {
	private static final int SECRET_CNT = 256; //Short.MAX_VALUE * 2; //64k potential clients
  private static final SecretKey[] secrets = new SecretKey[SECRET_CNT];
  public static final int AES_KEY_SIZE = 128; // in bits
  
  private static SecureRandom secureRandom;

  private boolean encryptGlobal = false;
  
	public SecretProvider() throws Exception {
		this(false);
	}
	
	public SecretProvider(boolean encryptGlobal) throws Exception {
		super();
		this.encryptGlobal = encryptGlobal;
		init();
	}
	
	private void init() throws Exception {
		if (secureRandom != null) {
			return;
		}
		secureRandom = SecureRandom.getInstanceStrong();
    
		//TODO: Remove when moving to production
    System.out.println("Generating secrets"); //$NON-NLS-1$
    for (int i = 1; i < SECRET_CNT; i++) {  //0 is reserved for global data, not client owned and often not encrypted
      KeyGenerator keyGen = KeyGenerator.getInstance("AES"); //$NON-NLS-1$
      keyGen.init(AES_KEY_SIZE, secureRandom);
      SecretKey secretKey = keyGen.generateKey();
      secrets[i] = secretKey;
    }
	}

	/**
	 * Method to only be used for testing
	 * @return
	 */
	public int getRandomClientId() {
		if (encryptGlobal)
			return ThreadLocalRandom.current().nextInt(SECRET_CNT);
		
		return ThreadLocalRandom.current().nextInt(SECRET_CNT-1) + 1;
	}

	public SecretKey getSecretKey(int clientId) {
		assert clientId > (encryptGlobal ? -1 : 0) && clientId < SECRET_CNT;
		SecretKey secretKey = secrets[clientId];
		return secretKey;
	}
}