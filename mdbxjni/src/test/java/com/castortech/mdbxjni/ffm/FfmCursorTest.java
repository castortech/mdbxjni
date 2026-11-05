package com.castortech.mdbxjni.ffm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * Walks three keys with a cursor and deletes one.
 */
public class FfmCursorTest {
	static {
		MdbxLibrary.load();
	}

	@Test
	public void walkAndDelete() throws Exception {
		Path dir = Files.createTempDirectory("mdbx-ffm-cursor");
		Path db = dir.resolve("cursor.mdbx");
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment env = open(arena, db);
			MemorySegment txn = MemorySegment.NULL;
			MemorySegment cursor = MemorySegment.NULL;
			boolean committed = false;
			try {
				txn = begin(arena, env);
				int dbi = openMain(arena, txn);
				put(arena, txn, dbi, "a", "1");
				put(arena, txn, dbi, "c", "3");

				MemorySegment pcursor = Mdbx.outPointer(arena);
				Mdbx.check(mdbx_h.mdbx_cursor_open(txn, dbi, pcursor));
				cursor = Mdbx.pointer(pcursor);
				Mdbx.check(mdbx_h.mdbx_cursor_put(cursor, Mdbx.val(arena, bytes("b")), Mdbx.val(arena, bytes("2")),
						mdbx_h.MDBX_UPSERT()));

				MemorySegment positioned = Mdbx.emptyVal(arena);
				Mdbx.check(mdbx_h.mdbx_cursor_get(cursor, positioned, Mdbx.emptyVal(arena), mdbx_h.MDBX_FIRST()));
				assertEquals(mdbx_h.MDBX_RESULT_TRUE(), mdbx_h.mdbx_cursor_on_first(cursor));
				assertEquals(mdbx_h.MDBX_RESULT_FALSE(), mdbx_h.mdbx_cursor_eof(cursor));

				assertEquals(List.of("a", "b", "c"), keys(arena, cursor));

				MemorySegment key = Mdbx.val(arena, bytes("b"));
				MemorySegment data = Mdbx.emptyVal(arena);
				Mdbx.check(mdbx_h.mdbx_cursor_get(cursor, key, data, mdbx_h.MDBX_SET()));
				Mdbx.check(mdbx_h.mdbx_cursor_del(cursor, 0));
				Mdbx.check(mdbx_h.mdbx_del(txn, dbi, Mdbx.val(arena, bytes("c")), MemorySegment.NULL));

				assertEquals(List.of("a"), keys(arena, cursor));
				MemorySegment found = Mdbx.emptyVal(arena);
				Mdbx.check(mdbx_h.mdbx_get(txn, dbi, Mdbx.val(arena, bytes("a")), found));
				assertArrayEquals(bytes("1"), Mdbx.bytes(found));

				Mdbx.check(mdbx_h.mdbx_txn_commit_ex(txn, MemorySegment.NULL));
				committed = true;
			}
			finally {
				if (cursor != null && cursor.address() != 0) {
					mdbx_h.mdbx_cursor_close(cursor);
				}
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

	private static List<String> keys(Arena arena, MemorySegment cursor) {
		List<String> keys = new ArrayList<>();
		MemorySegment key = Mdbx.emptyVal(arena);
		MemorySegment data = Mdbx.emptyVal(arena);
		int rc = mdbx_h.mdbx_cursor_get(cursor, key, data, mdbx_h.MDBX_FIRST());
		while (!Mdbx.isNotFound(rc)) {
			Mdbx.check(rc);
			keys.add(new String(Mdbx.bytes(key), StandardCharsets.UTF_8));
			rc = mdbx_h.mdbx_cursor_get(cursor, key, data, mdbx_h.MDBX_NEXT());
		}
		return keys;
	}

	private static MemorySegment open(Arena arena, Path db) {
		MemorySegment slot = Mdbx.outPointer(arena);
		Mdbx.check(mdbx_h.mdbx_env_create(slot));
		MemorySegment env = Mdbx.pointer(slot);
		Mdbx.check(mdbx_h.mdbx_env_open(env, Mdbx.utf8(arena, db.toString()), mdbx_h.MDBX_NOSUBDIR(), (short)0644));
		return env;
	}

	private static MemorySegment begin(Arena arena, MemorySegment env) {
		MemorySegment slot = Mdbx.outPointer(arena);
		Mdbx.check(mdbx_h.mdbx_txn_begin_ex(env, MemorySegment.NULL, 0, slot, MemorySegment.NULL));
		return Mdbx.pointer(slot);
	}

	private static int openMain(Arena arena, MemorySegment txn) {
		MemorySegment slot = Mdbx.outInt(arena);
		Mdbx.check(mdbx_h.mdbx_dbi_open(txn, MemorySegment.NULL, mdbx_h.MDBX_DB_DEFAULTS(), slot));
		return Mdbx.readInt(slot);
	}

	private static void put(Arena arena, MemorySegment txn, int dbi, String key, String value) {
		Mdbx.check(mdbx_h.mdbx_put(txn, dbi, Mdbx.val(arena, bytes(key)), Mdbx.val(arena, bytes(value)),
				mdbx_h.MDBX_UPSERT()));
	}

	private static byte[] bytes(String text) {
		return text.getBytes(StandardCharsets.UTF_8);
	}
}
