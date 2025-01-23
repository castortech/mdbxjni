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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.castortech.mdbxjni.JNIIntern.MDBX_cursor;
import com.google.common.primitives.UnsignedBytes;

import java.util.LinkedList;
import java.util.stream.Collectors;

import static com.castortech.mdbxjni.Constants.*;

/**
 * Unit tests for the MDBX API.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@SuppressWarnings("nls")
public class DatabaseRenameTest {
	static {
		Setup.setLibraryPaths(Setup.RELEASE_MODE);
	}

	private Env env;
	private Database db;
	private Database secDb;
	private OperationStatus operationStatus;
	private String path;

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	@Before
	public void before() throws Exception {
		path = tmp.newFolder().getCanonicalPath();
		System.out.println("Using path:" + path);

		openEnv();

		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setDupSort(true);
		dbConfig.setDupFixed(true);
		dbConfig.setCreate(true);
		dbConfig.setKeyComparator(UnsignedBytes.lexicographicalComparator());
		db = env.openDatabase("primary", dbConfig);

		SecondaryDbConfig secConfig = new SecondaryDbConfig();
		secConfig.setCreate(true);
		secConfig.setDupSort(true);
		secDb = env.openSecondaryDatabase(db, "secondary", secConfig);
	}

	private void openEnv() {
		EnvConfig envConfig = new EnvConfig();
		envConfig.setLifoReclaim(true);

		envConfig.setMapSize(2000L * 1024 * 1024);	//2gb
		envConfig.setMapGrowth(1000L * 1024 * 1024);	//1gb

		env = new Env();
		env.setMaxDbs(5);
		Env.pushMemoryPool(1024*512);
		env.open(path, envConfig);
	}

	@After
	public void after() {
		db.close();
		Env.popMemoryPool();
		env.close();
	}

	@Test
	public void testRenameDb() throws Exception {
		testMainDb2();
	}

	private void testMainDb() {
		Database db = env.openDatabase("foo");
		db.put(bytes("foo"), bytes("bar"));
		db.close();

		db = env.openDatabase("bar");
		db.close();

		listDbs("Original list");

		after();
		openEnv();

		try (Transaction tx = env.createWriteTransaction()) {
			Database fooDb = env.openDatabase(tx, "foo", 0);
			fooDb.rename(tx, "foobar");
		}

		listDbs("After renaming foo to foobar");
		after();
		openEnv();

		db = env.openDatabase("foobar");
		byte[] data = db.get(bytes("foo"));
		String val = string(data);
		System.out.println("found:" + val);

		after();
		openEnv();

		listDbs("After re-open");

		db = env.openDatabase("foobar");
		data = db.get(bytes("foo"));
		val = string(data);
		System.out.println("found:" + val);

		after();
		openEnv();


//		byte[] data = bytes("bar");
//		try (Transaction tx = env.createWriteTransaction()) {
//			env.getMainDb().delete(tx, data);
//		}
	}

	private void testMainDb2() {
		Database db = env.openDatabase("foo");
		System.out.println("Adding record foo -> bar to foo db");
		db.put(bytes("foo"), bytes("bar"));
		db.close();

		db = env.openDatabase("bar");
		db.close();

		listDbs("Original DB list");

		try (Transaction tx = env.createWriteTransaction()) {
			Database fooDb = env.openDatabase(tx, "foo", 0);
			fooDb.rename(tx, "foobar");
		}

		listDbs("After renaming DB foo to foobar");

		db = env.openDatabase("foobar");

		System.out.println("Retrieving foo record from foobar db");
		byte[] data = db.get(bytes("foo"));
		String val = string(data);
		System.out.println("found:" + val);

		after();
		openEnv();

		listDbs("After re-open");

		db = env.openDatabase("foobar");
		data = db.get(bytes("foo"));
		val = string(data);
		System.out.println("found:" + val);
	}

	private void listDbs(String label) {
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
			System.out.println(label);
			System.out.print('\t');
			System.out.println(keys.stream().collect(Collectors.joining("\n\t")));
		}
	}
}