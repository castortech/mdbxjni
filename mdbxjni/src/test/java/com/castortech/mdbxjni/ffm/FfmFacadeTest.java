package com.castortech.mdbxjni.ffm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Exercises the FFM facade without calling the generated header class directly.
 */
public class FfmFacadeTest {
	static {
		MdbxLibrary.load();
	}

	@Test
	public void putWalkAndStat() throws Exception {
		Path dir = Files.createTempDirectory("mdbx-ffm-facade");
		Path db = dir.resolve("facade.mdbx");
		FfmEnv env = FfmEnv.create();
		try {
			env.setGeometry(-1, -1, 64L * 1024 * 1024, -1, -1, -1);
			env.setMaxDbs(4);
			env.open(db, FfmEnv.nosubdir());
			assertEquals(4, env.getMaxDbs());
			assertTrue(env.mapSize() > 0);

			try (FfmTransaction txn = env.begin()) {
				FfmDatabase database = txn.openDatabase("items", FfmEnv.createDatabase());
				database.put(bytes("a"), bytes("1"));
				database.put(bytes("c"), bytes("3"));
				try (FfmCursor cursor = database.openCursor()) {
					cursor.put(bytes("b"), bytes("2"));
					assertEquals(List.of("a", "b", "c"), keys(cursor));
					byte[][] found = cursor.find(FfmCursor.set(), bytes("b"));
					assertArrayEquals(bytes("2"), found[1]);
					cursor.delete();
				}
				assertTrue(database.delete(bytes("c")));
				assertNull(database.get(bytes("c")));
				assertEquals(1, database.entries());
				txn.commit();
			}
			env.sync();
		}
		finally {
			env.close();
			Mdbx.deleteTree(dir);
		}
	}

	private static List<String> keys(FfmCursor cursor) {
		List<String> keys = new ArrayList<>();
		for (byte[][] row = cursor.get(FfmCursor.first()); row != null; row = cursor.get(FfmCursor.next())) {
			keys.add(new String(row[0], StandardCharsets.UTF_8));
		}
		return keys;
	}

	private static byte[] bytes(String text) {
		return text.getBytes(StandardCharsets.UTF_8);
	}
}
