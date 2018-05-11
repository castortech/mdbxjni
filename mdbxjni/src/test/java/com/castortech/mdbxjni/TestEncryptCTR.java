package com.castortech.mdbxjni;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Random;

import org.fluttercode.datafactory.impl.DataFactory;

import com.castortech.mdbxjni.TimeUtils.HrsOption;

/**
 * Based on: https://proandroiddev.com/security-best-practices-symmetric-encryption-with-aes-in-java-7616beaaade9
 * and: https://gist.github.com/praseodym/f2499b3e14d872fe5b4a
 * 
 * @author Alain Picard
 *
 */
public class TestEncryptCTR {
	private static final int ITERATIONS = 10000;
  private static final byte[][] data = new byte[ITERATIONS][];
	
	public TestEncryptCTR() throws Exception {
		super();
	}
	
	@SuppressWarnings("nls")
	public static void main(String[] args) throws Exception {
		IvProvider ivProvider = new IvProvider(new File("ivs.dat"));
		SecretProvider secretProvider = new SecretProvider();
		EncryptUtils encrypt = new EncryptUtils(secretProvider, ivProvider);
    
  	Random rand = new Random();
		DataFactory df = new DataFactory();
		df.randomize(rand.nextInt());

		long startRun = System.nanoTime();
		long totEncrypt = 0;
		long totDecrypt = 0;
//		String plainText = "This is a top secret message";
//		byte[] plainBytes = plainText.getBytes("UTF-8");

		for (int i = 0; i < ITERATIONS; i++) {
			int lgth = i+1;
			String plainText = df.getRandomText(10, 50);
			byte[] plainBytes = plainText.getBytes("UTF-8");
			
			int clientId = secretProvider.getRandomClientId();
//			System.out.println("client:" + clientId);
			long start = System.nanoTime();

			ByteBuffer cipherBuff = encrypt.encryptWithInfo(plainBytes, clientId);
			long elapsed = TimeUtils.elapsedSinceNanos(start);
			totEncrypt += elapsed;
			System.out.println("Encrypted " + lgth + "/" + cipherBuff.capacity() + " in " + TimeUtils.nanoTimeAsString(elapsed, HrsOption.NEVER));
			data[i] = cipherBuff.array();
		}
		
		for (byte[] datum : data) {
			long start = System.nanoTime();
			ByteBuffer decryptedBuff = encrypt.decryptWithInfo(datum);
			String decrypted = new String(decryptedBuff.array(), "UTF-8");
			long elapsed = TimeUtils.elapsedSinceNanos(start);
			totDecrypt += elapsed;
			System.out.println("\tDecrypted: " + decrypted + " in " + TimeUtils.nanoTimeAsString(elapsed, HrsOption.NEVER));
		};
		
		System.out.println();
		System.out.println("Total Run time:" + TimeUtils.elapsedSinceNano(startRun) + 
				",Encrypt:" + TimeUtils.nanoTimeAsString(totEncrypt, HrsOption.NEVER) + 
				",Decrypt:" + TimeUtils.nanoTimeAsString(totDecrypt, HrsOption.NEVER));
	}
}
