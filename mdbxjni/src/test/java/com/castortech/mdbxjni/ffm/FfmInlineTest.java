package com.castortech.mdbxjni.ffm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.castortech.mdbxjni.ffm.gen.MDBX_cache_entry;
import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * Checks the Java wrappers for libmdbx functions that are inline in 0.14.
 */
public class FfmInlineTest {
	static {
		MdbxLibrary.load();
	}

	@Test
	public void keyOrderAndMaxDbs() throws Exception {
		assertEquals(0x8000000000000000L, MdbxInline.mdbx_key_from_int64(0));
		assertEquals(0x80000000, MdbxInline.mdbx_key_from_int32(0));
		assertTrue(Long.compareUnsigned(MdbxInline.mdbx_key_from_int64(-1), MdbxInline.mdbx_key_from_int64(1)) < 0);
		assertEquals(mdbx_h.MDBX_MIN_PAGESIZE(), MdbxInline.mdbx_limits_pgsize_min());
		assertEquals(mdbx_h.MDBX_MAX_PAGESIZE(), MdbxInline.mdbx_limits_pgsize_max());

		try (Arena cacheArena = Arena.ofConfined()) {
			MemorySegment entry = MDBX_cache_entry.allocate(cacheArena);
			MDBX_cache_entry.trunk_txnid(entry, 9);
			MDBX_cache_entry.last_confirmed_txnid(entry, 8);
			MDBX_cache_entry.offset(entry, 7);
			MDBX_cache_entry.length(entry, 6);
			MdbxInline.mdbx_cache_init(entry);
			assertEquals(0L, MDBX_cache_entry.trunk_txnid(entry));
			assertEquals(0L, MDBX_cache_entry.last_confirmed_txnid(entry));
			assertEquals(0L, MDBX_cache_entry.offset(entry));
			assertEquals(0, MDBX_cache_entry.length(entry));
		}

		Path dir = Files.createTempDirectory("mdbx-ffm-inline");
		Path db = dir.resolve("inline.mdbx");
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outPointer(arena);
			Mdbx.check(mdbx_h.mdbx_env_create(slot));
			MemorySegment env = Mdbx.pointer(slot);
			try {
				Mdbx.check(MdbxInline.mdbx_env_set_maxdbs(env, 4));
				MemorySegment dbs = Mdbx.outInt(arena);
				Mdbx.check(MdbxInline.mdbx_env_get_maxdbs(env, dbs));
				assertEquals(4, Mdbx.readInt(dbs));
				Mdbx.check(MdbxInline.mdbx_env_set_mapsize(env, 8L * 1024 * 1024));
				Mdbx.check(mdbx_h.mdbx_env_open(env, Mdbx.utf8(arena, db.toString()), mdbx_h.MDBX_NOSUBDIR(), (short)0644));
				Mdbx.check(MdbxInline.mdbx_env_sync(env));
				int polled = MdbxInline.mdbx_env_sync_poll(env);
				assertTrue(polled == mdbx_h.MDBX_SUCCESS() || polled == mdbx_h.MDBX_RESULT_TRUE());
			}
			finally {
				Mdbx.check(MdbxInline.mdbx_env_close(env));
			}
		}
		finally {
			Mdbx.deleteTree(dir);
		}
	}
}
