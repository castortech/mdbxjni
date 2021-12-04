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

package com.castortech.mdbxjni;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static com.castortech.mdbxjni.Constants.*;

/**
 * Unit tests for the MDBX API.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class SecondaryCursorTest {
	static {
		Setup.setLibraryPaths();
	}

	Env env;
	Database db;
	SecondaryDatabase secDb;

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	@Before
	public void before() throws IOException {
		String path = tmp.newFolder().getCanonicalPath();
		env = new Env();
		env.setMaxDbs(2);
		env.open(path);
		Env.pushMemoryPool(1024);
		db = env.openDatabase("primary");

		SecondaryDbConfig secConfig = new SecondaryDbConfig();
		secConfig.setCreate(true);
		secConfig.setDupSort(true);
		secDb = env.openSecondaryDatabase(db, "secondary", secConfig);
	}

	@After
	public void after() {
		db.close();
		Env.popMemoryPool();
		env.close();
	}

	@Test
	public void doTests() {
		doTest1();
		doTest2();
		doTest3();
		doTest4();
		doTest5();
		doTest6();
		doTest7();
	}

	//standard puts
	private void doTest1() {
		try (Transaction tx = env.createWriteTransaction()) {
			try (Cursor cursor = db.openCursor(tx)) {
				cursor.put(bytes("Tampa"), bytes("green"), 0);
				cursor.put(bytes("London"), bytes("red"), 0);
			}
			tx.commit();

			// secondary get after put
			assertArrayEquals(secDb.get(bytes("red")), bytes("London"));
		}
	}

	// standard put & get back
	private void doTest2() {
		try (Transaction tx = env.createWriteTransaction()) {
			try (Cursor cursor = db.openCursor(tx)) {
				cursor.put(bytes("New York"), bytes("gray"), 0);
			}
			tx.commit();

			assertArrayEquals(db.get(bytes("New York")), bytes("gray"));
		}
	}

	//change value (secondary needs to update as well)
	private void doTest3() {
		try (Transaction tx = env.createWriteTransaction()) {
			try (Cursor cursor = db.openCursor(tx)) {
				cursor.put(bytes("New York"), bytes("green"), 0);
			}
			tx.commit();

			assertArrayEquals(db.get(bytes("New York")), bytes("green"));
			assertNull(secDb.get(bytes("gray")));
		}
	}

	// try to change with flag
	private void doTest4() {
		try (Transaction tx = env.createWriteTransaction()) {
			try (Cursor cursor = db.openCursor(tx)) {
				cursor.put(bytes("New York"), bytes("silver"), NOOVERWRITE);
			}
			tx.commit();

			assertArrayEquals(db.get(bytes("New York")), bytes("green"));
		}
	}

	// various gets
	private void doTest5() {
		// standard get
		assertArrayEquals(db.get(bytes("Tampa")), bytes("green"));

		// secondary get of unique
		assertArrayEquals(secDb.get(bytes("red")), bytes("London"));

		// secondary get of multiple (return last value)
		assertArrayEquals(secDb.get(bytes("green")), bytes("New York"));

		// secondary cursor to get multiple
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

		// standard get
		assertArrayEquals(db.get(bytes("London")), bytes("red"));
		assertArrayEquals(db.get(bytes("New York")), bytes("green"));

		// standard cursor db scan
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

		// secondary cursor db scan
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
	}

	// standard delete
	private void doTest6() {
		try (Transaction tx = env.createWriteTransaction()) {
			try (Cursor cursor = db.openCursor(tx)) {
				Entry entry = cursor.get(CursorOp.SET, bytes("New York"));
				assertNotNull(entry);
				cursor.delete();
			}
			tx.commit();

			assertNull(db.get(bytes("New York")));
			// secondary get after delete of one entry should return remaining
			assertArrayEquals(secDb.get(bytes("green")), bytes("Tampa"));

			// We should not be able to delete it again.
			assertFalse(db.delete(bytes("New York")));
		}
	}

	// standard delete and test of now empty secondary
	private void doTest7() {
		try (Transaction tx = env.createWriteTransaction()) {
			try (Cursor cursor = db.openCursor(tx)) {
				Entry entry = cursor.get(CursorOp.SET, bytes("Tampa"));
				assertNotNull(entry);
				cursor.delete();
			}
			tx.commit();

			assertNull(secDb.get(bytes("green")));
		}
	}
}
