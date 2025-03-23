package com.castortech.mdbxjni.types;

import java.nio.ByteBuffer;

/**
 * Utility class supporting byte manipulation
 * @author Alain Picard
 */
public class ByteUtil {
	private static final String HexStringForLongMaxValue = Long.toHexString(Long.MAX_VALUE);
	private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

	private ByteUtil() { }

	/**
	 * Converts a byte into an hex string using default prefix of 0x
	 * @param b byte to convert
	 * @return hex string for byte
	 */
	public static String hexStringForByte(byte b) {
		return hexStringForByte(b, "0x"); //$NON-NLS-1$
	}

	/**
	 * Converts a byte into an hex string
	 * @param b byte to convert
	 * @param prefix to add to converted byte
	 * @return hex string for byte
	 */
	public static String hexStringForByte(byte b, String prefix) {
		StringBuilder sb = new StringBuilder();
		int i = b;
		if (i < 0) {
			i += 256;
		}
		String raw = Integer.toHexString(i);
		int padding = 2 - raw.length();
		if (prefix != null) {
			sb.append(prefix);
		}

		while (padding-- > 0) {
			sb.append("0"); //$NON-NLS-1$
		}

		sb.append(raw);

		return sb.toString();
	}

	/**
	 * Converts a byte array into an hex string
	 * @param bytes bytes to convert
	 * @param bytePrefix prefix to add to converted bytes
	 * @param byteSeparator separator to insert between bytes if not null
	 * @return hex string for bytes
	 */
	public static String hexStringForBytes(byte[] bytes, String bytePrefix, String byteSeparator) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexStringForByte(bytes[i], bytePrefix));
			if (byteSeparator != null && i != bytes.length - 1) {
				sb.append(byteSeparator);
			}
		}
		return sb.toString();
	}

	/**
	 * Converts an hex string to a byte. Note that there is minimal validation performed
	 * @param hexString string to convert
	 * @return byte represented by the string
	 */
	public static byte hexToByte(String hexString) {
		int firstDigit = toDigit(hexString.charAt(0));
		int secondDigit = toDigit(hexString.charAt(1));
		return (byte) ((firstDigit << 4) + secondDigit);
	}

	private static int toDigit(char hexChar) {
		int digit = Character.digit(hexChar, 16);
		if (digit == -1) {
			throw new IllegalArgumentException("Invalid Hexadecimal Character: " + hexChar); //$NON-NLS-1$
		}
		return digit;
	}

	/**
	 * Converts an hex string to a byte array. Note that there is minimal validation performed
	 * @param hexString string to convert
	 * @param bytePrefix prefix contained in hexString
	 * @param byteSeparator separator between bytes contained in hexString
	 * @return byte array from converstion
	 */
	public static byte[] bytesForHexString(String hexString, String bytePrefix, String byteSeparator) {
		if (byteSeparator != null) { //add a trailing separator to make it easier to split things
			hexString += byteSeparator;
		}
		int prefixLgth = bytePrefix == null ? 0 : bytePrefix.length();
		int byteChars = 2 + prefixLgth + (byteSeparator == null ? 0 : byteSeparator.length());
		int byteCnt = hexString.length() / byteChars;
		byte[] bytes = new byte[byteCnt];

		for (int i = 0; i < byteCnt; i ++) {
			int startOffset = (i * byteChars) + prefixLgth;
			bytes[i] = hexToByte(hexString.substring(startOffset, startOffset + 2));
		}
		return bytes;
	}

	/**
	 * Converts a long value into an hex string
	 * @param lValue long value
	 * @return hex string for long value
	 */
	public static String hexStringForLong(long lValue) {
		String raw = Long.toHexString(lValue);
		if (raw.length() == HexStringForLongMaxValue.length()) {
			return "0x" + raw; //$NON-NLS-1$
		}
		StringBuilder sb = new StringBuilder();
		int padding = HexStringForLongMaxValue.length() - raw.length();
		sb.append("0x"); //$NON-NLS-1$
		while (padding-- > 0) {
			sb.append("0"); //$NON-NLS-1$
		}

		sb.append(raw);

		return sb.toString();
	}

	// make this obsolete. Use UnsignedInt* instead
	private static long unsignedByte(byte b) {
		if (b >= 0) {
			return b;
		}
		return 256 + (long)b;
	}

	/**
	 * Convert a little endian byte array (4 bytes) representing a long
	 * @param buf byte array
	 * @return long from byte array
	 */
	public static long longForByteArray(byte[] buf) {
		return unsignedByte(buf[0]) + unsignedByte(buf[1]) * 256 + unsignedByte(buf[2]) * 65536
				+ unsignedByte(buf[3]) * 16777216;
	}

	/**
	 * Convert a little endian byte array (4 bytes) representing a long
	 * @param buf byte array
	 * @param offset offset from which to start extracting bytes
	 * @return long from byte array
	 */
	public static long longForByteArray(byte[] buf, int offset) {
		return unsignedByte(buf[offset + 0]) + unsignedByte(buf[offset + 1]) * 256
				+ unsignedByte(buf[offset + 2]) * 65536 + unsignedByte(buf[offset + 3]) * 16777216;
	}

	/**
	 * Convert a long into a little endian byte array (4 bytes)
	 * @param l long to convert
	 * @return byte array representing long
	 */
	public static byte[] byteArrayForLong(long l) {
//		byte[] buf = new byte[4];
//		buf[0] = (byte)(l & 0x000000FF);
//		buf[1] = (byte)((l & 0x0000FF00) >> 8);
//		buf[2] = (byte)((l & 0x00FF0000) >> 16);
//		buf[3] = (byte)((l & 0xFF000000) >> 24);

		buffer.putLong(0, l);
		byte[] bs = buffer.array();
		return bs;
	}

	/**
	 * Convert a long into an unsigned byte array
	 * @param l long value
	 * @return byte array representing unsigned int.
	 */
	public static byte[] unsignedIntByteArray(long l) {
		byte[] buf = new byte[4];
		buf[0] = (byte)(l >>> 24);
		buf[1] = (byte)(l >>> 16);
		buf[2] = (byte)(l >>> 8);
		buf[3] = (byte)l;
		return buf;
	}

	/**
	 * Convert between little and big endian and vice versa
	 * @param bytes byte array to reverse
	 * @return reversed bytes
	 */
	public static byte[] reverseByteOrderInPlace(byte[] bytes) {
		for (int index = 0, count = bytes.length / 2; index < count; index++) {
			byte temp = bytes[index];
			int otherIndex = bytes.length - 1 - index;
			bytes[index] = bytes[otherIndex];
			bytes[otherIndex] = temp;
		}
		return bytes;
	}

	/**
	 * Reverse the bytes in the passed in array specified range
	 * @param bytes byte array to reverse
	 * @param offset starting offset
	 * @param length length to convert
	 */
	public static void reverseBytes(byte[] bytes, int offset, int length) {
		for (int index = 0, count = length / 2; index < count; index++) {
			int thisIndex = offset + index;
			int otherIndex = offset + length - 1 - index;

			byte temp = bytes[thisIndex];
			bytes[thisIndex] = bytes[otherIndex];
			bytes[otherIndex] = temp;
		}
	}
}