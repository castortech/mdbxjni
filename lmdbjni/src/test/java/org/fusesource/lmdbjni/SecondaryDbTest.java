/**
 * Copyright (C) 2013, RedHat, Inc.
 *
 *    http://www.redhat.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.lmdbjni;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.fusesource.lmdbjni.Constants.*;

/**
 * Unit tests for the LMDB API.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class SecondaryDbTest {
	static {
		Setup.setLmdbLibraryPath();
	}

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	@Rule
	public TemporaryFolder backup = new TemporaryFolder();

	@Test
	public void testCRUD() throws Exception {
		String path = tmp.newFolder().getCanonicalPath();
		try (Env env = new Env()) {
			env.setMaxDbs(2);
			env.open(path);
			Env.pushMemoryPool(10);
			Env.pushMemoryPool(10);
			Env.popMemoryPool();
			Env.popMemoryPool();
			
			SecondaryDbConfig secConfig = new SecondaryDbConfig();
            secConfig.setCreate(true);
            secConfig.setDupSort(true);
            
			try (Database db = env.openDatabase("primary"); 
					SecondaryDatabase secDb = env.openSecondaryDatabase(db, "secondary", secConfig)) {
				doTest(env, db, secDb);
			}
		}
	}

	@Test
	public void testBackup() throws Exception {
		String path = tmp.newFolder().getCanonicalPath();
		String backupPath = backup.newFolder().getCanonicalPath();
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
				assertThat((int) value[0], is(1));
			}
		}
	}

	@Test
	public void testBackupCompact() throws Exception {
		String path = tmp.newFolder().getCanonicalPath();
		String backupPath = backup.newFolder().getCanonicalPath();
		try (Env env = new Env()) {
			env.open(path);
			try (Database db = env.openDatabase()) {
				db.put(new byte[] { 1 }, new byte[] { 1 });
				// env.copyCompact(backupPath);
				env.copy(backupPath);
			}
		}
		try (Env env = new Env()) {
			env.open(backupPath);
			try (Database db = env.openDatabase()) {
				byte[] value = db.get(new byte[] { 1 });
				assertThat((int) value[0], is(1));
			}
		}
	}

	@Test
	public void testEnvInfo() throws Exception {
		String path = tmp.newFolder().getCanonicalPath();
		try (Env env = new Env()) {
			env.addFlags(CREATE);
			env.open(path);
			env.setMapSize(1048576L);
			try (Database db = env.openDatabase()) {
				db.put(new byte[] { 1 }, new byte[] { 1 });
				EnvInfo info = env.info();
				assertNotNull(info);
			}
		}
	}

	@Test
	public void testStat() throws Exception {
		String path = tmp.newFolder().getCanonicalPath();
		try (Env env = new Env()) {
			env.open(path);
			env.setMapSize(1048576L);
			try (Database db = env.openDatabase()) {
				db.put(new byte[] { 1 }, new byte[] { 1 });
				db.put(new byte[] { 2 }, new byte[] { 1 });
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
			assertThat(env.getMaxKeySize(), is(511L));
		}
	}

	private void doTest(Env env, Database db, SecondaryDatabase secDb) {
		//standard puts
		assertNull(db.put(bytes("Tampa"), bytes("green")));
		assertNull(db.put(bytes("London"), bytes("red")));

		//secondary get after put
		assertArrayEquals(secDb.get(bytes("red")), bytes("London"));
		
		//standard put & get back
		assertNull(db.put(bytes("New York"), bytes("gray")));
		assertArrayEquals(db.get(bytes("New York")), bytes("gray"));
		
		//change value (secondary needs to update as well)
		assertNull(db.put(bytes("New York"), bytes("green")));
		assertArrayEquals(db.get(bytes("New York")), bytes("green"));
		assertNull(secDb.get(bytes("gray")));
		
		//try to change with flag
		assertArrayEquals(db.put(bytes("New York"), bytes("silver"), NOOVERWRITE), bytes("green"));

		//standard get
		assertArrayEquals(db.get(bytes("Tampa")), bytes("green"));

		//secondary get of unique
		assertArrayEquals(secDb.get(bytes("red")), bytes("London"));
		
		//secondary get of multiple (return last value)
		assertArrayEquals(secDb.get(bytes("green")), bytes("New York"));

		//secondary cursor to get multiple
		try (Transaction tx = env.createReadTransaction(); Cursor cursor = secDb.openCursor(tx)) {
			LinkedList<String> values = new LinkedList<>();
			
			byte[] key = bytes("green");
			Entry entry = cursor.get(CursorOp.SET, key);
            
           	while (entry != null) {
				values.add(string(entry.getValue()));
            	entry = cursor.get(CursorOp.NEXT_DUP, key, entry.getValue());
            }				

			assertEquals(Arrays.asList(new String[] { "New York", "Tampa" }), values);
		}
		
		//standard get
		assertArrayEquals(db.get(bytes("London")), bytes("red"));
		assertArrayEquals(db.get(bytes("New York")), bytes("green"));

		//standard cursor db scan
		try (Transaction tx = env.createReadTransaction(); Cursor cursor = db.openCursor(tx)) {
			// Lets verify cursoring works..
			List<String> keys = new LinkedList<>();
			List<String> values = new LinkedList<>();
			for (Entry entry = cursor.get(FIRST); entry != null; entry = cursor.get(NEXT)) {
				keys.add(string(entry.getKey()));
				values.add(string(entry.getValue()));
			}
			assertEquals(Arrays.asList(new String[] { "London", "New York", "Tampa" }), keys);
			assertEquals(Arrays.asList(new String[] { "red", "green", "green" }), values);
		}
		
		//secondary cursor db scan
		try (Transaction tx = env.createReadTransaction(); Cursor cursor = secDb.openCursor(tx)) {
			// Lets verify cursoring works..
			Set<String> keys = new HashSet<>();
			Set<String> values = new HashSet<>();
			for (Entry entry = cursor.get(FIRST); entry != null; entry = cursor.get(NEXT)) {
				keys.add(string(entry.getKey()));
				values.add(string(entry.getValue()));
			}
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { "red", "green" })), keys);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { "London", "New York", "Tampa" })), values);
		}

		assertNull(db.put(bytes("Yonkers"), bytes("white")));

		//standard delete
		assertTrue(db.delete(bytes("New York")));
		assertNull(db.get(bytes("New York")));
		//secondary get after delete of one entry should return remaining
		assertArrayEquals(secDb.get(bytes("green")), bytes("Tampa"));

		// We should not be able to delete it again.
		assertFalse(db.delete(bytes("New York")));
		
		//standard delete and test of now empty secondary
		assertTrue(db.delete(bytes("Tampa")));
		assertNull(secDb.get(bytes("green")));
		
		// put /w readonly transaction should fail.
		try (Transaction tx = env.createReadTransaction()) {
			db.put(tx, bytes("New York"), bytes("silver"));
			fail("Expected LMDBException");
		} catch (LMDBException e) {
			assertTrue(e.getErrorCode() > 0);
		}
		env.sync(true);
	}
}
