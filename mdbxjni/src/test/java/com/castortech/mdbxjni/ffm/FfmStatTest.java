package com.castortech.mdbxjni.ffm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.castortech.mdbxjni.ffm.gen.MDBX_envinfo;
import com.castortech.mdbxjni.ffm.gen.MDBX_stat;
import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * Reads {@code ms_entries} and the current map size through the generated layouts.
 */
public class FfmStatTest {
	static {
		MdbxLibrary.load();
	}

	@Test
	public void entriesAndMapSize() throws Exception {
		Path dir = Files.createTempDirectory("mdbx-ffm-stat");
		Path db = dir.resolve("stat.mdbx");
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outPointer(arena);
			Mdbx.check(mdbx_h.mdbx_env_create(slot));
			MemorySegment env = Mdbx.pointer(slot);
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
				for (String key : new String[] { "a", "b", "c" }) {
					Mdbx.check(mdbx_h.mdbx_put(txn, dbi, Mdbx.val(arena, key.getBytes(StandardCharsets.UTF_8)),
							Mdbx.val(arena, key.getBytes(StandardCharsets.UTF_8)), mdbx_h.MDBX_UPSERT()));
				}

				MemorySegment stat = MDBX_stat.allocate(arena);
				Mdbx.check(mdbx_h.mdbx_dbi_stat(txn, dbi, stat, MDBX_stat.sizeof()));
				assertEquals(3, MDBX_stat.ms_entries(stat));

				MemorySegment envStat = MDBX_stat.allocate(arena);
				Mdbx.check(mdbx_h.mdbx_env_stat_ex(env, txn, envStat, MDBX_stat.sizeof()));
				assertEquals(3, MDBX_stat.ms_entries(envStat));

				MemorySegment info = MDBX_envinfo.allocate(arena);
				Mdbx.check(mdbx_h.mdbx_env_info_ex(env, txn, info, MDBX_envinfo.sizeof()));
				assertTrue(MDBX_envinfo.mi_geo.current(MDBX_envinfo.mi_geo(info)) > 0);

				Mdbx.check(mdbx_h.mdbx_txn_commit_ex(txn, MemorySegment.NULL));
				committed = true;
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
