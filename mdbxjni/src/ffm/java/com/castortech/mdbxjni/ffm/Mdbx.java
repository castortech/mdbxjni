package com.castortech.mdbxjni.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.castortech.mdbxjni.ffm.gen.iovec;
import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * Small helpers around the generated binding. Pointer fields are written with
 * plain {@link ValueLayout#ADDRESS} because the generated {@code iovec} setter
 * expects an unbounded target layout.
 */
public final class Mdbx {
	private Mdbx() {
	}

	public static void check(int rc) {
		// mdbx_env_sync_ex returns MDBX_RESULT_TRUE when nothing is waiting to be flushed.
		if (rc != mdbx_h.MDBX_SUCCESS() && rc != mdbx_h.MDBX_RESULT_TRUE()) {
			throw new MdbxException(rc, message(rc));
		}
	}

	public static boolean isNotFound(int rc) {
		return rc == mdbx_h.MDBX_NOTFOUND();
	}

	public static MemorySegment outPointer(Arena arena) {
		return arena.allocate(mdbx_h.C_POINTER);
	}

	public static MemorySegment pointer(MemorySegment slot) {
		return slot.get(ValueLayout.ADDRESS, 0);
	}

	public static MemorySegment outInt(Arena arena) {
		return arena.allocate(mdbx_h.C_INT);
	}

	public static int readInt(MemorySegment slot) {
		return slot.get(mdbx_h.C_INT, 0);
	}

	public static MemorySegment outLong(Arena arena) {
		return arena.allocate(mdbx_h.C_LONG_LONG);
	}

	public static long readLong(MemorySegment slot) {
		return slot.get(mdbx_h.C_LONG_LONG, 0);
	}

	public static MemorySegment val(Arena arena, byte[] data) {
		MemorySegment storage = arena.allocateFrom(ValueLayout.JAVA_BYTE, data);
		MemorySegment val = emptyVal(arena);
		val.set(ValueLayout.ADDRESS, iovec.iov_base$offset(), storage);
		val.set(ValueLayout.JAVA_LONG, iovec.iov_len$offset(), data.length);
		return val;
	}

	public static MemorySegment emptyVal(Arena arena) {
		return iovec.allocate(arena);
	}

	public static byte[] bytes(MemorySegment val) {
		long len = val.get(ValueLayout.JAVA_LONG, iovec.iov_len$offset());
		if (len < 0 || len > Integer.MAX_VALUE) {
			throw new MdbxException(0, "MDBX_val length is out of range: " + len);
		}
		if (len == 0) {
			return new byte[0];
		}
		MemorySegment base = val.get(ValueLayout.ADDRESS, iovec.iov_base$offset());
		return MemorySegment.ofAddress(base.address()).reinterpret(len).toArray(ValueLayout.JAVA_BYTE);
	}

	public static MemorySegment utf8(Arena arena, String text) {
		return arena.allocateFrom(text);
	}

	public static MemorySegment wide(Arena arena, String text) {
		char[] chars = (text + '\0').toCharArray();
		MemorySegment segment = arena.allocate(ValueLayout.JAVA_CHAR, chars.length);
		segment.copyFrom(MemorySegment.ofArray(chars));
		return segment;
	}

	public static void deleteTree(Path dir) throws java.io.IOException {
		if (Files.notExists(dir)) {
			return;
		}
		try (Stream<Path> paths = Files.walk(dir)) {
			for (Path path : paths.sorted((left, right) -> right.compareTo(left)).toList()) {
				Files.deleteIfExists(path);
			}
		}
	}

	private static String message(int rc) {
		try {
			MemorySegment text = mdbx_h.mdbx_strerror(rc).reinterpret(256);
			String message = text.getString(0, StandardCharsets.UTF_8);
			if (message != null && !message.isEmpty()) {
				return message;
			}
		}
		catch (RuntimeException ignored) {
			// Fall through to the numeric code.
		}
		return Integer.toString(rc);
	}
}
