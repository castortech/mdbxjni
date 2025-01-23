/**
 * Copyright (C) 2013, RedHat, Inc.
 *
 *		http://www.redhat.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.castortech.mdbxjni;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.castortech.mdbxjni.JNIIntern.MDBX_cursor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static com.castortech.mdbxjni.JNI.*;
import static org.junit.Assert.*;
import static com.castortech.mdbxjni.Constants.*;

/**
 * Unit tests for the MDBX API.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@SuppressWarnings("nls")
public class EnvTest {
	static {
		Setup.setLibraryPaths();
	}

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	@Rule
	public TemporaryFolder backup = new TemporaryFolder();

	@Test
	public void testCRUD() throws Exception {
		String path = tmp.newFolder().getCanonicalPath();
		System.out.println("testCRUD Using path:" + path);

		try (Env env = new Env()) {
			env.setMaxDbs(5);
			env.open(path);
			Env.pushMemoryPool(10);
			Env.pushMemoryPool(10);
			Env.popMemoryPool();
			Env.popMemoryPool();

			try (Database db = env.openDatabase()) {
				doTest(env, db);
			}

			testMainDb(env);
		}
	}

	@Test
	public void testBackup() throws Exception {
		String path = tmp.newFolder().getCanonicalPath();
		System.out.println("testBackup Using path:" + path);
		String backupPath = backup.newFolder().getCanonicalPath() + "/mdbx.dat";
		try (Env env = new Env()) {
			env.open(path);
			try (Database db = env.openDatabase()) {
				db.put(new byte[] { 1 }, new byte[] { 1 });
				env.copy(backupPath);
			}
		}
		try (Env env = new Env()) {
			env.open(backupPath);
			try (Database db = env.openDatabase()) {
				byte[] value = db.get(new byte[] { 1 });
				assertThat((int)value[0], is(1));
			}
		}
	}

	@Test
	public void testBackupCompact() throws Exception {
		String path = tmp.newFolder().getCanonicalPath();
		System.out.println("testBackupCompact Using path:" + path);
		String backupPath = backup.newFolder().getCanonicalPath() + "/mdbx.dat";

		try (Env env = new Env()) {
			env.open(path);
			try (Database db = env.openDatabase()) {
				db.put(new byte[]{1}, new byte[]{1});
			}
		}

		try (Env env = new Env()) {
			env.open(path);
			env.copy(backupPath, JNI.MDBX_CP_COMPACT);
		}
		try (Env env = new Env()) {
			env.open(backupPath);
			try (Database db = env.openDatabase()) {
				byte[] value = db.get(new byte[]{1});
				assertThat((int) value[0], is(1));
			}
		}
	}

	@Test
	public void testEnvVersion() throws Exception {
		String path = tmp.newFolder().getCanonicalPath();
		try (Env env = new Env()) {
			Env.version();
			env.open(path);
		}
	}

	@Test
	public void testEnvInfo() throws Exception {
		String path = tmp.newFolder().getCanonicalPath();
		System.out.println("testEnvInfo Using path:" + path);

//		try (Env env = new Env()) {
//			env.setMaxDbs(5);
////			env.addFlags(CREATE);
//			env.setGeometry(1048576L, -1L, 4000L * 1024 * 1024, 1048576L, -1L, -1L);
//			env.open(path);
//
//			Database d1 = env.openDatabase("db1");
//			d1.close();
//		}
//
//		try (Env env = new Env()) {
//			env.setGeometry(1048576L, -1L, 4000L * 1024 * 1024, 1048576L, -1L, 32768);
//			env.open(path);
//			Database d2 = env.openDatabase("db2");
//			d2.close();
//		}

//			env.setMapSize(1048576L);

		try (Env env = new Env()) {
			env.setMaxDbs(5);
			env.setGeometry(1048576L, -1L, 4000L * 1024 * 1024, 1048576L, -1L, 32768);
			env.open(path);
			StringBuilder sb = new StringBuilder();
			float percentFull;
			sb.append("Env Version:");
			sb.append(Env.version());

			MDBX_stat stat = env.stat();
			sb.append(" Stats:");
			sb.append(stat.toString());
			sb.append('\n');

			EnvInfo info = env.info();
			sb.append("Info:");
			sb.append(info.toString());
			sb.append('\n');

			try (Database db = env.openDatabase("db1")) {
//			try (Database db = env.openDatabase(null, "db1", new DatabaseConfig())) {
				db.put(new byte[]{1}, new byte[]{1});
				info = env.info();
				assertNotNull(info);

				stat = db.stat();
				sb.append("Db1 Stats:");
				sb.append(stat.toString());
				sb.append('\n');
			}

			env.setGeometry(1048576L, -1L, 4000L * 1024 * 1024, 1048576L, -1L, -1L);
			try (Database db = env.openDatabase("db2")) {
//			try (Database db = env.openDatabase(null, "db2", new DatabaseConfig())) {
				db.put(new byte[]{1}, new byte[]{1});
				info = env.info();
				assertNotNull(info);

				stat = db.stat();
				sb.append("Db1 Stats:");
				sb.append(stat.toString());
				sb.append('\n');
			}

			System.out.println(sb.toString());
		}
	}

	@Test
	public void testStat() throws Exception {
		String path = tmp.newFolder().getCanonicalPath();
		try (Env env = new Env()) {
			env.open(path);
			env.setMapSize(1048576L);
			try (Database db = env.openDatabase()) {
				db.put(new byte[]{1}, new byte[]{1});
				db.put(new byte[]{2}, new byte[]{1});
				Stat stat = env.stat();
				System.out.println(stat);
				assertThat(stat.ms_entries, is(2L));
				assertThat(stat.ms_psize, is(not(0L)));
				assertThat(stat.ms_overflow_pages, is(0L));
				assertThat(stat.ms_depth, is(1L));
				assertThat(stat.ms_leaf_pages, is(1L));
			}
		}
	}

	@Test
	public void testMaxKeySize() throws Exception {
		String path = tmp.newFolder().getCanonicalPath();
		try (Env env = new Env()) {
			env.open(path);
			assertThat(env.getMaxKeySize(), is(1980L));
		}
	}

	@Test
	public void testSetupDebug() throws Exception {
		String path = tmp.newFolder().getCanonicalPath();
		try (Env env = new Env()) {
			env.open(path);
			DebugState debugState = env.setupDebug(MdbxLogLevel.TRACE, MDBX_DBG_AUDIT | MDBX_DBG_ASSERT);
			System.out.println("DebugState:" + debugState);
			assertThat(env.getMaxKeySize(), is(1980L));

			debugState = env.setupDebug(MdbxLogLevel.ERROR, MDBX_DBG_DONT_UPGRADE);
			System.out.println("DebugState:" + debugState);

			debugState = env.setupDebug(MdbxLogLevel.FATAL, MDBX_DBG_AUDIT);
			System.out.println("DebugState:" + debugState);
		}
	}

	private void testMainDb(Env env) {
		Database db = env.openDatabase("foo");
		db = env.openDatabase("bar");

		try (Transaction tx = env.createWriteTransaction(); Cursor cursor = env.getMainDb().openCursor(tx)) {
			LinkedList<String> keys = new LinkedList<>();
			MDBX_cursor mdbx_cursor = cursor.internCursor();
			CursorState cursorState = new CursorState(mdbx_cursor.flags);

			while (true) {
				byte[] key = new byte[1];
				Entry entry = cursor.get(NEXT_NODUP, key, null);
				mdbx_cursor = cursor.internCursor();
				cursorState = new CursorState(mdbx_cursor.flags);
				if (entry == null) {
					break;
				}
				String keyVal = string(entry.getKey());
//				if (keyVal.equals("bar")) {
//					cursor.delete();
//				}

				keys.add(keyVal);
			}
			System.out.println(keys.stream().collect(Collectors.joining("\n")));
		}

//		byte[] data = bytes("bar");
//		try (Transaction tx = env.createWriteTransaction()) {
//			env.getMainDb().delete(tx, data);
//		}
	}

	private void doTest(Env env, Database db) {
		assertNull(db.put(bytes("Tampa"), bytes("green")));
		assertNull(db.put(bytes("London"), bytes("red")));

		assertNull(db.put(bytes("New York"), bytes("gray")));
		assertNull(db.put(bytes("New York"), bytes("blue")));
		assertArrayEquals(db.put(bytes("New York"), bytes("silver"), NOOVERWRITE), bytes("blue"));

		assertArrayEquals(db.get(bytes("Tampa")), bytes("green"));
		assertArrayEquals(db.get(bytes("London")), bytes("red"));
		assertArrayEquals(db.get(bytes("New York")), bytes("blue"));

		try (Transaction tx = env.createReadTransaction(); Cursor cursor = db.openCursor(tx)) {
			// Lets verify cursoring works..
			LinkedList<String> keys = new LinkedList<>();
			LinkedList<String> values = new LinkedList<>();
			for (Entry entry = cursor.get(FIRST); entry != null; entry = cursor.get(NEXT)) {
				keys.add(string(entry.getKey()));
				values.add(string(entry.getValue()));
			}
			assertEquals(Arrays.asList(new String[]{"London", "New York", "Tampa"}), keys);
			assertEquals(Arrays.asList(new String[]{"red", "blue", "green"}), values);
		}

		assertTrue(db.delete(bytes("New York")));
		assertNull(db.get(bytes("New York")));

		// We should not be able to delete it again.
		assertFalse(db.delete(bytes("New York")));

		// put /w readonly transaction should fail.
		try (Transaction tx = env.createReadTransaction()) {
			db.put(tx, bytes("New York"), bytes("silver"));
			fail("Expected MDBXException");
		}
		catch (MDBXException e) {
			assertTrue(e.getErrorCode() > 0);
		}
		env.sync(true);
	}
}