package com.castortech.mdbxjni.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import com.castortech.mdbxjni.ffm.gen.MDBX_stat;
import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * Named or main database opened through the FFM binding.
 */
public final class FfmDatabase {
	private final FfmTransaction transaction;
	private final int dbi;

	FfmDatabase(FfmTransaction transaction, int dbi) {
		this.transaction = transaction;
		this.dbi = dbi;
	}

	public void put(byte[] key, byte[] value) {
		try (Arena arena = Arena.ofConfined()) {
			Mdbx.check(mdbx_h.mdbx_put(transaction.segment(), dbi, Mdbx.val(arena, key), Mdbx.val(arena, value),
					mdbx_h.MDBX_UPSERT()));
		}
	}

	public byte[] get(byte[] key) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment found = Mdbx.emptyVal(arena);
			int rc = mdbx_h.mdbx_get(transaction.segment(), dbi, Mdbx.val(arena, key), found);
			if (Mdbx.isNotFound(rc)) {
				return null;
			}
			Mdbx.check(rc);
			return Mdbx.bytes(found);
		}
	}

	public boolean delete(byte[] key) {
		try (Arena arena = Arena.ofConfined()) {
			int rc = mdbx_h.mdbx_del(transaction.segment(), dbi, Mdbx.val(arena, key), MemorySegment.NULL);
			if (Mdbx.isNotFound(rc)) {
				return false;
			}
			Mdbx.check(rc);
			return true;
		}
	}

	public long entries() {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment stat = MDBX_stat.allocate(arena);
			Mdbx.check(mdbx_h.mdbx_dbi_stat(transaction.segment(), dbi, stat, MDBX_stat.sizeof()));
			return MDBX_stat.ms_entries(stat);
		}
	}

	public FfmCursor openCursor() {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outPointer(arena);
			Mdbx.check(mdbx_h.mdbx_cursor_open(transaction.segment(), dbi, slot));
			return new FfmCursor(Mdbx.pointer(slot));
		}
	}

	int handle() {
		return dbi;
	}
}
