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
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests for the MDBX API.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@SuppressWarnings("nls")
public class ReLayoutDbs {
	private static String path;
	private static long mapMax = 12000L * 1024 * 1024;

	static {
		Setup.setLibraryPaths(Setup.RELEASE_MODE);
		Optional.ofNullable(System.getProperty("db.path")).ifPresent(val -> path = val);

		try {
			Optional.ofNullable(System.getProperty("map.max"))
					.ifPresent(val -> mapMax = Long.parseLong(val));
		}
		catch (Exception e) {
			System.err.println("Exception resolving map max, using default");// TODO: handle exception
		}
	}

	private Env env;

	private byte[] nullHdl = new byte[16];

	@Before
	public void before() throws Exception {
		if (path == null || path.isEmpty()) {
			throw new IllegalStateException("Path must be set via db.path");
		}
		System.out.println("Using path:" + path);
		Arrays.fill(nullHdl, (byte) 0x00);

		openEnv();
	}

	private void openEnv() {
		EnvConfig envConfig = new EnvConfig();
//		envConfig.setDELifoReclaim(true);
//
		envConfig.setMapSize(4000L * 1024 * 1024);	//4gb
		envConfig.setMapGrowth(1000L * 1024 * 1024);	//1gb
		envConfig.setMapUpper(mapMax); //12gb or set value via map.max

		env = new Env();
		env.setMaxDbs(100);
		Env.pushMemoryPool(1024*512);
		env.open(path, envConfig);
	}

	@After
	public void after() {
		Env.popMemoryPool();
		env.close();
	}

	@Test
	public void testRelayoutDbs() throws Exception {
		renameDbs();
		reloadIncidence();
		reloadData();
	}

	private void renameDbs() throws Exception {
		try (Transaction tx = env.createWriteTransaction()) {
			List<String> databases = env.listDatabases(tx);

			databases.stream()
					.sorted()
					.filter(dbName -> dbName.equals("datadb") || dbName.equals("incidencedb"))
					.forEach(dbName -> {
						Database db = env.openDatabase(tx, dbName, 0);
						try {
							db.rename(tx, dbName + "_bak");
						}
						finally {
							db.close();
						}
			});
		}

		System.out.println("After renaming DBs");
		try (Transaction tx = env.createWriteTransaction()) {
			List<String> databases = env.listDatabases(tx);

			databases.stream()
					.sorted()
					.forEach(dbName -> {
						System.out.println("found:" + dbName);
			});
		}
	}

	private void reloadIncidence() throws Exception {
		DatabaseConfig dbConfig;
		Database bakDb = env.openDatabase("incidencedb_bak", 0);
		dbConfig = bakDb.getConfig().cloneConfig();

		try (Transaction tx = env.createWriteTransaction()) {
			Database oldDb = env.openDatabase(tx, "incidencedb_bak", 0);

			dbConfig.setCreate(true);
			dbConfig.setDupSort(true);
			Database incidenceDb = env.openDatabase(tx, "incidencedb", dbConfig);

			Cursor cursor = oldDb.openCursor(tx);
			int cnt = 0;

			for (Entry entry = cursor.get(CursorOp.FIRST); entry != null; entry = cursor.get(CursorOp.NEXT)) {
				byte[] key = entry.getKey();
				byte[] value = entry.getValue();

				byte[] newVal = new byte[32];
				System.arraycopy(nullHdl, 0, newVal, 0, 16);
				System.arraycopy(value, 0, newVal, 16, 16);
				incidenceDb.put(tx, key, newVal, Constants.NODUPDATA);
				if (cnt++ % 100  == 0)
					System.out.print('.');
			}
		}
		try (Transaction tx = env.createTransaction()) {
			System.out.println(TestUtils.getStats(env, tx, Collections.emptyMap()));
		}
	}

	private void reloadData() throws Exception {
		Database bakDb = env.openDatabase("datadb_bak", 0);
		DatabaseConfig dbConfig = bakDb.getConfig().cloneConfig();

		try (Transaction tx = env.createWriteTransaction()) {
			Database oldDb = env.openDatabase(tx, "datadb_bak", 0);

			dbConfig.setCreate(true);
			dbConfig.setDupSort(true);
			dbConfig.setDupFixed(true);
			Database dataDb = env.openDatabase(tx, "datadb", dbConfig);

			Cursor cursor = oldDb.openCursor(tx);
			int z = 0;

			for (Entry entry = cursor.get(CursorOp.FIRST); entry != null; entry = cursor.get(CursorOp.NEXT)) {
				byte[] key = entry.getKey();
				byte[] value = entry.getValue();

				int valCnt = value.length / 16; //how many handles do we start with
				int valLgth = 16 + 4;
				List<byte[]> links = new ArrayList<>(valCnt);

				//create our handles ordered
				for (int i = 0; i < valCnt; i++) {
					byte[] hdl = new byte[16];
					System.arraycopy(value, i *16, hdl, 0, 16);
					links.add(hdl);
				}

				//now create the new sequenced array
				int trailingNullCnt = trailingNullCnt(links);
				byte[] buffer = new byte[links.size() * valLgth];
				int cnt = 0;

				for (int i = 0; i < links.size(); i++) {
					byte[] handle = links.get(i);
					//skip any null handle except trailing not to loose original size, no need to store them
					if (handle == null) {
						continue;
					}
					if (!Arrays.equals(handle, nullHdl) || i >= links.size() - trailingNullCnt) {
						System.arraycopy(writeUnsignedInt(i), 0, buffer, cnt * valLgth, 4);
						System.arraycopy(handle, 0, buffer, (cnt * valLgth) + 4, 16);
						cnt++;
					}
				}

				valCnt = cnt;
				byte[] valArr = new byte[cnt * valLgth];
				System.arraycopy(buffer, 0, valArr, 0, cnt * valLgth);

				dataDb.put(tx, key, valArr, Constants.MULTIPLE, valCnt);
				if (z++ % 100  == 0)
					System.out.print('.');
			}
		}

		try (Transaction tx = env.createTransaction()) {
			System.out.println(TestUtils.getStats(env, tx, Collections.emptyMap()));
		}
	}

	public int trailingNullCnt(List<byte[]> links) {
		int cnt = 0;
		for (int i = links.size() -1; i >= 0; i--) {
			if (Arrays.equals(links.get(i), nullHdl)) {
				cnt++;
			}
			else {
				break;
			}
		}
		return cnt;
	}

	public byte[] writeUnsignedInt(long val) {
		byte[] out = new byte[4];
		out[0] = (byte)(val >>> 24);
		out[1] = (byte)(val >>> 16);
		out[2] = (byte)(val >>> 8);
		out[3] = (byte)val;
		return out;
	}
}