package com.castortech.mdbxjni.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * Cursor opened through the FFM binding.
 */
public final class FfmCursor implements AutoCloseable {
	public static int first() {
		return mdbx_h.MDBX_FIRST();
	}

	public static int next() {
		return mdbx_h.MDBX_NEXT();
	}

	public static int set() {
		return mdbx_h.MDBX_SET();
	}

	private MemorySegment cursor;

	FfmCursor(MemorySegment cursor) {
		this.cursor = cursor;
	}

	public byte[][] get(int op) {
		try (Arena arena = Arena.ofConfined()) {
			return position(Mdbx.emptyVal(arena), Mdbx.emptyVal(arena), op);
		}
	}

	public byte[][] find(int op, byte[] key) {
		try (Arena arena = Arena.ofConfined()) {
			return position(Mdbx.val(arena, key), Mdbx.emptyVal(arena), op);
		}
	}

	public void put(byte[] key, byte[] value) {
		try (Arena arena = Arena.ofConfined()) {
			Mdbx.check(mdbx_h.mdbx_cursor_put(cursor, Mdbx.val(arena, key), Mdbx.val(arena, value), mdbx_h.MDBX_UPSERT()));
		}
	}

	public void delete() {
		Mdbx.check(mdbx_h.mdbx_cursor_del(cursor, 0));
	}

	@Override
	public void close() {
		if (cursor != null && cursor.address() != 0) {
			mdbx_h.mdbx_cursor_close(cursor);
			cursor = MemorySegment.NULL;
		}
	}

	private byte[][] position(MemorySegment key, MemorySegment data, int op) {
		int rc = mdbx_h.mdbx_cursor_get(cursor, key, data, op);
		if (Mdbx.isNotFound(rc)) {
			return null;
		}
		Mdbx.check(rc);
		return new byte[][] { Mdbx.bytes(key), Mdbx.bytes(data) };
	}
}
