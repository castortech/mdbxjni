package com.castortech.mdbxjni;

import java.nio.ByteBuffer;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

/**
 * Based on: https://proandroiddev.com/security-best-practices-symmetric-encryption-with-aes-in-java-7616beaaade9
 * and: https://gist.github.com/praseodym/f2499b3e14d872fe5b4a
 * 
 * @author Alain Picard
 *
 */
public class EncryptUtils {
	private static final String TRANSFORMATION = "AES/CTR/NoPadding"; //$NON-NLS-1$

  private static Cipher cipher;
  private final SecretProvider secretProvider;
  private final IvProvider ivProvider;
	
	public EncryptUtils(SecretProvider secretProvider, IvProvider ivProvider) throws Exception {
		super();
		this.secretProvider = secretProvider;
		this.ivProvider = ivProvider;
		init();
	}
	
	private void init() throws Exception {
		if (cipher != null) {
			return;
		}
		
    cipher = Cipher.getInstance(TRANSFORMATION);
	}
	
	public ByteBuffer encryptWithInfo(byte[] plainBytes, int clientId) throws Exception {
		return encryptWithInfo(plainBytes, clientId, ivProvider.getRandomIvKey()); 
	}
	
	public ByteBuffer encryptWithInfo(byte[] plainBytes, int clientId, int ivKey) throws Exception {
		SecretKey secretKey = getSecret(clientId);
		AlgorithmParameterSpec paramSpec = getParamSpec(ivKey);
		ByteBuffer msgBuffer = ByteBuffer.allocate(plainBytes.length + 4);
		writeUnsignedShort(msgBuffer, ivKey);
		writeUnsignedShort(msgBuffer, clientId);
		encrypt(ByteBuffer.wrap(plainBytes), msgBuffer, secretKey, paramSpec);
		
		return msgBuffer; 
	}
	
	public ByteBuffer decryptWithInfo(byte[] msgBytes) throws Exception {
		ByteBuffer buffer = ByteBuffer.wrap(msgBytes);
		int ivKey = readUnsignedShort(buffer);
		AlgorithmParameterSpec paramSpec = getParamSpec(ivKey);
		int clientId = readUnsignedShort(buffer);
		SecretKey secretKey = getSecret(clientId);
		ByteBuffer plainBuff = ByteBuffer.allocate(msgBytes.length - 4);
    decrypt(buffer, plainBuff, secretKey, paramSpec);
		
    return plainBuff;
	}
	
	public ByteBuffer decrypt(byte[] msgBytes, int clientId, int ivKey) throws Exception {
		ByteBuffer buffer = ByteBuffer.wrap(msgBytes);
		AlgorithmParameterSpec paramSpec = getParamSpec(ivKey);
		SecretKey secretKey = getSecret(clientId);
		ByteBuffer plainBuff = ByteBuffer.allocate(msgBytes.length);
    decrypt(buffer, plainBuff, secretKey, paramSpec);
		
    return plainBuff;
	}

	private void encrypt(ByteBuffer plainBuff, ByteBuffer cipherBuff, SecretKey secretKey, 
			AlgorithmParameterSpec paramSpec) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
		cipher.doFinal(plainBuff, cipherBuff);
	}
	
	private void decrypt(ByteBuffer cipherBuff, ByteBuffer plainBuff, SecretKey secretKey, 
			AlgorithmParameterSpec paramSpec) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
		cipher.doFinal(cipherBuff, plainBuff);
	}
	
	private void writeUnsignedShort(ByteBuffer buff, int val) {
		buff.put((byte)(val >>> 8));
		buff.put((byte)val);
	}
	
	private int unsigned(byte b) {
	 return b & 0xff;
	}

	private int readUnsignedShort(ByteBuffer buff) throws IndexOutOfBoundsException {
		int c1 = unsigned(buff.get());
		int c2 = unsigned(buff.get());
		if ((c1 | c2) < 0) {
			throw new IndexOutOfBoundsException();
		}
		return ((c1 << 8) | c2);
	}

	private SecretKey getSecret(int clientId) {
		SecretKey secretKey = secretProvider.getSecretKey(clientId);
		
		if (secretKey == null) {
			throw new IllegalStateException("Secret Key not found for client:" + clientId);
		}
		return secretKey;
	}

	private AlgorithmParameterSpec getParamSpec(int ivKey) {
		AlgorithmParameterSpec paramSpec = ivProvider.getParamSpec(ivKey);
		
		if (paramSpec == null) {
			throw new IllegalStateException("IV Param spec not found for ivKey:" + ivKey);
		}
		return paramSpec;
	}

//	private byte[] encrypt(byte[] plainBytes, SecretKey secretKey, AlgorithmParameterSpec paramSpec)
//			throws Exception {
//		cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
//		return cipher.doFinal(plainBytes);
//	}
//
//	private byte[] decrypt(byte[] cipherText, SecretKey secretKey, AlgorithmParameterSpec paramSpec)
//			throws Exception {
//		cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
//		return cipher.doFinal(cipherText);
//	}
}
