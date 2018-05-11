package com.castortech.mdbxjni;

import static org.junit.Assert.assertTrue;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import org.fluttercode.datafactory.impl.DataFactory;

import com.castortech.mdbxjni.TimeUtils.HrsOption;

/**
 * Based on: https://proandroiddev.com/security-best-practices-symmetric-encryption-with-aes-in-java-7616beaaade9
 * and: https://gist.github.com/praseodym/f2499b3e14d872fe5b4a
 * 
 * @author Alain Picard
 *
 */
public class TestEncryptGSM {
	public static final String TRANSFORMATION = "AES/GCM/NoPadding"; //$NON-NLS-1$
  public static final int AES_KEY_SIZE = 128; // in bits
  public static final int GCM_IV_LENGTH = 12; // in bytes
  public static final int GCM_TAG_LENGTH = 16; // in bytes

  private static Cipher cipher;
	
	@SuppressWarnings("nls")
	public static void main(String[] args) throws Exception {
		DataFactory df = new DataFactory();
		
    SecureRandom random = SecureRandom.getInstanceStrong();
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(AES_KEY_SIZE, random);
    SecretKey secretKey = keyGen.generateKey();
    
    cipher = Cipher.getInstance(TRANSFORMATION);

		long startRun = System.nanoTime();
		long totEncrypt = 0;
		long totDecrypt = 0;
		TestEncryptGSM te = new TestEncryptGSM();
//	String plainText = "bzwzyojhecvlwbraajradtzeqxsylubtppi";
//	byte[] plainBytes = plainText.getBytes("UTF-8");

		for (int i = 0; i < 10000; i++) {
			int lgth = i+1;
			String plainText = df.getRandomText(10, 10);
			byte[] plainBytes = plainText.getBytes("UTF-8");

			byte[] iv = new byte[GCM_IV_LENGTH]; //NEVER REUSE THIS IV WITH SAME KEY
			random.nextBytes(iv);
			GCMParameterSpec paramSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv); //128 bit auth tag length
			
			long start = System.nanoTime();
			byte[] cipherMessage = te.encrypt(plainBytes, secretKey, paramSpec);
			long elapsed = TimeUtils.elapsedSinceNanos(start);
			totEncrypt += elapsed;
			System.out.println("Encrypted " + lgth + "/" + cipherMessage.length + " in " + TimeUtils.nanoTimeAsString(elapsed, HrsOption.NEVER));
			
			start = System.nanoTime();
			plainBytes = te.decrypt(cipherMessage, secretKey, paramSpec);
			String decrypted = new String(plainBytes, "UTF-8");
			elapsed = TimeUtils.elapsedSinceNanos(start);
			totDecrypt += elapsed;
			System.out.println("\tDecrypted: " + plainText + " in " + TimeUtils.nanoTimeAsString(elapsed, HrsOption.NEVER));
			assertTrue(plainText.equals(decrypted));
		}
		
		System.out.println();
		System.out.println("Total Run time:" + TimeUtils.elapsedSinceNano(startRun) + 
				",Encrypt:" + TimeUtils.nanoTimeAsString(totEncrypt, HrsOption.NEVER) + 
				",Decrypt:" + TimeUtils.nanoTimeAsString(totDecrypt, HrsOption.NEVER));
	}
	
	private byte[] encrypt(byte[] plainBytes, SecretKey secretKey, AlgorithmParameterSpec paramSpec) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
		return cipher.doFinal(plainBytes);
	}
	
	private byte[] decrypt(byte[] cipherText, SecretKey secretKey, AlgorithmParameterSpec paramSpec) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
		return cipher.doFinal(cipherText);
	}
}
