package com.castortech.mdbxjni.ffm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * Sets geometry and the named-database limit, then opens with the Windows path.
 */
public class FfmEnvConfigTest {
	private static final long UPPER = 64L * 1024 * 1024;

	static {
		MdbxLibrary.load();
	}

	@Test
	public void namedDatabase() throws Exception {
		Path dir = Files.createTempDirectory("mdbx-ffm-env");
		Path db = dir.resolve("env.mdbx");
		Path copy = dir.resolve("copy.mdbx");
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outPointer(arena);
			Mdbx.check(mdbx_h.mdbx_env_create(slot));
			MemorySegment env = Mdbx.pointer(slot);
			MemorySegment txn = MemorySegment.NULL;
			boolean committed = false;
			try {
				Mdbx.check(mdbx_h.mdbx_env_set_geometry(env, -1, -1, UPPER, -1, -1, -1));
				Mdbx.check(mdbx_h.mdbx_env_set_option(env, mdbx_h.MDBX_opt_max_db(), 4));
				Mdbx.check(mdbx_h.mdbx_env_set_option(env, mdbx_h.MDBX_opt_max_readers(), 126));
				Mdbx.check(mdbx_h.mdbx_env_openW(env, Mdbx.wide(arena, db.toString()), mdbx_h.MDBX_NOSUBDIR(), (short)0644));
				Mdbx.check(mdbx_h.mdbx_env_set_flags(env, mdbx_h.MDBX_NOMEMINIT(), true));

				int flags = readInt(arena, env);
				assertTrue((flags & mdbx_h.MDBX_NOSUBDIR()) != 0);
				assertTrue((flags & mdbx_h.MDBX_NOMEMINIT()) != 0);
				assertEquals(4, readOption(arena, env, mdbx_h.MDBX_opt_max_db()));
				assertTrue(readOption(arena, env, mdbx_h.MDBX_opt_max_readers()) >= 126);
				assertTrue(mdbx_h.mdbx_env_get_maxkeysize(env) > 0);
				assertTrue(mdbx_h.mdbx_env_get_maxkeysize_ex(env, mdbx_h.MDBX_DB_DEFAULTS()) > 0);
				assertTrue(mdbx_h.mdbx_env_get_maxvalsize_ex(env, mdbx_h.MDBX_DB_DEFAULTS()) > 0);

				MemorySegment ptxn = Mdbx.outPointer(arena);
				Mdbx.check(mdbx_h.mdbx_txn_begin_ex(env, MemorySegment.NULL, 0, ptxn, MemorySegment.NULL));
				txn = Mdbx.pointer(ptxn);

				MemorySegment dbiSlot = Mdbx.outInt(arena);
				Mdbx.check(mdbx_h.mdbx_dbi_open(txn, Mdbx.utf8(arena, "items"), mdbx_h.MDBX_CREATE(), dbiSlot));
				int dbi = Mdbx.readInt(dbiSlot);
				byte[] key = "k".getBytes(StandardCharsets.UTF_8);
				byte[] value = "v".getBytes(StandardCharsets.UTF_8);
				Mdbx.check(mdbx_h.mdbx_put(txn, dbi, Mdbx.val(arena, key), Mdbx.val(arena, value), mdbx_h.MDBX_UPSERT()));

				MemorySegment found = Mdbx.emptyVal(arena);
				Mdbx.check(mdbx_h.mdbx_get(txn, dbi, Mdbx.val(arena, key), found));
				assertArrayEquals(value, Mdbx.bytes(found));

				Mdbx.check(mdbx_h.mdbx_txn_commit_ex(txn, MemorySegment.NULL));
				committed = true;
				txn = MemorySegment.NULL;

				Mdbx.check(mdbx_h.mdbx_env_sync_ex(env, true, false));
				Mdbx.check(mdbx_h.mdbx_env_copy(env, Mdbx.utf8(arena, copy.toString()), mdbx_h.MDBX_CP_DEFAULTS()));
				assertTrue(Files.isRegularFile(copy));
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

	private static int readInt(Arena arena, MemorySegment env) {
		MemorySegment slot = Mdbx.outInt(arena);
		Mdbx.check(mdbx_h.mdbx_env_get_flags(env, slot));
		return Mdbx.readInt(slot);
	}

	private static long readOption(Arena arena, MemorySegment env, int option) {
		MemorySegment slot = Mdbx.outLong(arena);
		Mdbx.check(mdbx_h.mdbx_env_get_option(env, option, slot));
		return Mdbx.readLong(slot);
	}
}
