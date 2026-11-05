package com.castortech.mdbxjni.ffm;

import static org.junit.Assert.assertArrayEquals;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * Opens a temporary environment through the jextract binding and reads back one value.
 */
public class FfmSmokeTest {
	static {
		MdbxLibrary.load();
	}

	@Test
	public void putAndGet() throws Exception {
		Path dir = Files.createTempDirectory("mdbx-ffm");
		Path db = dir.resolve("smoke.mdbx");
		byte[] keyBytes = "alpha".getBytes(StandardCharsets.UTF_8);
		byte[] valueBytes = "beta".getBytes(StandardCharsets.UTF_8);

		try (Arena arena = Arena.ofConfined()) {
			MemorySegment penv = Mdbx.outPointer(arena);
			Mdbx.check(mdbx_h.mdbx_env_create(penv));
			MemorySegment env = Mdbx.pointer(penv);

			MemorySegment txn = MemorySegment.NULL;
			boolean committed = false;
			try {
				Mdbx.check(mdbx_h.mdbx_env_open(env, Mdbx.utf8(arena, db.toString()), mdbx_h.MDBX_NOSUBDIR(), (short)0644));

				MemorySegment ptxn = Mdbx.outPointer(arena);
				Mdbx.check(mdbx_h.mdbx_txn_begin_ex(env, MemorySegment.NULL, 0, ptxn, MemorySegment.NULL));
				txn = Mdbx.pointer(ptxn);

				MemorySegment dbiSlot = Mdbx.outInt(arena);
				Mdbx.check(mdbx_h.mdbx_dbi_open(txn, MemorySegment.NULL, mdbx_h.MDBX_DB_DEFAULTS(), dbiSlot));
				int dbi = Mdbx.readInt(dbiSlot);

				MemorySegment key = Mdbx.val(arena, keyBytes);
				MemorySegment value = Mdbx.val(arena, valueBytes);
				Mdbx.check(mdbx_h.mdbx_put(txn, dbi, key, value, mdbx_h.MDBX_UPSERT()));

				MemorySegment found = Mdbx.emptyVal(arena);
				Mdbx.check(mdbx_h.mdbx_get(txn, dbi, key, found));
				assertArrayEquals(valueBytes, Mdbx.bytes(found));

				Mdbx.check(mdbx_h.mdbx_txn_commit_ex(txn, MemorySegment.NULL));
				committed = true;
				txn = MemorySegment.NULL;
			}
			finally {
				if (!committed && txn != null && txn.address() != 0) {
					mdbx_h.mdbx_txn_abort_ex(txn, MemorySegment.NULL);
				}
				Mdbx.check(mdbx_h.mdbx_env_close_ex(env, false));
			}
		}
		finally {
			Mdbx.deleteTree(dir);
		}
	}
}
