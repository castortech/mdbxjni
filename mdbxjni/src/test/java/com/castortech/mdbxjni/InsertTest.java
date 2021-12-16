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
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;

import com.google.common.primitives.UnsignedBytes;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

/**
 * Unit tests for the MDBX API.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@SuppressWarnings("nls")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InsertTest {
	private static int I_CNT = 1000;
	private static int J_CNT = 100;

	static {
		Setup.setLibraryPaths();
	}

	Env env;
	Database db;

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	@Before
	public void before() throws Exception {
		String path = tmp.newFolder().getCanonicalPath();
		System.out.println("Using path:" + path);

		EnvConfig envConfig = new EnvConfig();
		envConfig.setLifoReclaim(true);

		//lazy
//		envConfig.setNoSync(true);
//		envConfig.setNoMetaSync(true);


//		envConfig.setSafeNoSync(true);
//		envConfig.setWriteMap(true);

			envConfig.setMapSize(2000L * 1024 * 1024);	//2gb
			envConfig.setMapGrowth(1000L * 1024 * 1024);	//1gb

		//nosync
//		envConfig.setWriteMap(true);
//		envConfig.setUtterlyNoSync(true);
//		envConfig.setNoMetaSync(true);

//	envConfig.setMapAsync(true);

		env = new Env();
		env.setMaxDbs(2);
		Env.pushMemoryPool(1024*512);
		env.open(path, envConfig);
//		db = env.openDatabase("primary");
//		db = env.openDatabase("primary", new KeyComparator(), null);
		db = env.openDatabase("primary", UnsignedBytes.lexicographicalComparator(), null);

//		SecondaryDbConfig secConfig = new SecondaryDbConfig();
//		secConfig.setCreate(true);
//		secConfig.setDupSort(true);
//		secDb = env.openSecondaryDatabase(db, "secondary", secConfig);

//		//warm up routine
//		longSequential_100Bytes_intern();
	}

	@After
	public void after() {
		db.close();
		Env.popMemoryPool();
		env.close();
	}

	public void doTests() {
//		doTest1_UUID();
//		doTest1_LongSequential();
//		doTest2();
//		doTest3();
//		doTest4();
//		doTest5();
//		doTest6();
//		doTest7();
	}

	//standard puts
	@Test
	public void A_uuidRandom_100Bytes() {
		System.out.println("Starting uuidRandom_100Bytes");
		for (int i=0; i < I_CNT; i++) {
//			long start = System.nanoTime();
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					UUID uuid = UUID.randomUUID();
///					System.out.println("\t putting " + uuid);
					db.put(tx, UuidAdapter.getBytesFromUUID(uuid), new byte[100]);
				}

				CommitLatency latency = tx.commitWithLatency();
//				System.out.println("Latency:" + latency);
				assertTrue(true);
			}
//			System.out.println("Completed " + i + " in " + TimeUtils.elapsedSinceNano(start));
		}
	}

	@Test
	public void B_longSequential_100Bytes() {
		System.out.println("Starting longSequential_100Bytes");
		longSequential_100Bytes_intern();
	}

	public void longSequential_100Bytes_intern() {
		AtomicLong along = new AtomicLong();

		for (int i=0; i < I_CNT; i++) {
//			long start = System.nanoTime();
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					long l = along.getAndIncrement();
					db.put(tx, longToBytes(l), new byte[100]);
				}

				CommitLatency latency = tx.commitWithLatency();
//				System.out.println("Latency:" + latency);
				assertTrue(true);
			}
//			System.out.println("Completed " + i + " in " + TimeUtils.elapsedSinceNano(start));
		}
	}

//	@Test
	public void longRandom_100Bytes() {
		System.out.println("Starting longRandom_100Bytes");
		for (int i=0; i < I_CNT; i++) {
//			long start = System.nanoTime();
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					long l = new Random().nextLong();
					db.put(tx, longToBytes(l), new byte[100]);
				}

				tx.commit();
				assertTrue(true);
			}
//			System.out.println("Completed " + i + " in " + TimeUtils.elapsedSinceNano(start));
		}
	}

//	@Test
	public void longSequential_4kBytes() {
		System.out.println("Starting longSequential_4kBytes");
		AtomicLong along = new AtomicLong();

		for (int i=0; i < I_CNT; i++) {
//			long start = System.nanoTime();
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					long l = along.getAndIncrement();
					db.put(tx, longToBytes(l), new byte[4096]);
				}

				tx.commit();
				assertTrue(true);
			}
//			System.out.println("Completed " + i + " in " + TimeUtils.elapsedSinceNano(start));
		}
	}

	@Test
	public void C_longSequential_3kBytes() {
		System.out.println("Starting longSequential_3kBytes");
		AtomicLong along = new AtomicLong();

		for (int i=0; i < I_CNT; i++) {
//			long start = System.nanoTime();
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					long l = along.getAndIncrement();
					db.put(tx, longToBytes(l), new byte[3072]);
				}

				tx.commit();
				assertTrue(true);
			}
//			System.out.println("Completed " + i + " in " + TimeUtils.elapsedSinceNano(start));
		}
	}

//	@Test
	public void longRandom_4kBytes() {
		System.out.println("Starting longRandom_4kBytes");
		for (int i=0; i < I_CNT; i++) {
//			long start = System.nanoTime();
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					long l = new Random().nextLong();
					db.put(tx, longToBytes(l), new byte[4096]);
				}

				tx.commit();
				assertTrue(true);
			}
//			System.out.println("Completed " + i + " in " + TimeUtils.elapsedSinceNano(start));
		}
	}

//	@Test
	public void longRandom_3kBytes() {
		System.out.println("Starting longRandom_3kBytes");
		for (int i=0; i < I_CNT; i++) {
//			long start = System.nanoTime();
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					long l = new Random().nextLong();
					db.put(tx, longToBytes(l), new byte[3072]);
				}

				tx.commit();
				assertTrue(true);
			}
//			System.out.println("Completed " + i + " in " + TimeUtils.elapsedSinceNano(start));
		}
	}

	@Test
	public void D_longSequential_3kBytes_withUpdate() {
		System.out.println("Starting longSequential_3kBytes_withUpdate");
		AtomicLong along = new AtomicLong();
		Random random = new Random();
		long start = System.nanoTime();

		for (int i=0; i < I_CNT/2; i++) {
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				Map<Long, byte[]> entries = new HashMap<>();
				for (int j= 0; j < J_CNT; j++) {
					long l = along.getAndIncrement();
					byte[] b = new byte[3072];
					random.nextBytes(b);
					db.put(tx, longToBytes(l), b);
					entries.put(l, b);
				}

				tx.commit();
				entries.entrySet().stream().forEach(entry -> {
					assertArrayEquals(entry.getValue(), entry.getValue());
//					assertArrayEquals(db.get(longToBytes(entry.getKey())), entry.getValue());
				});
			}
		}
		System.out.println("Completed inserts in " + TimeUtils.elapsedSinceNano(start));

		along = new AtomicLong();
		start = System.nanoTime();
		for (int i=0; i < I_CNT/2; i++) {
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					long l = along.getAndIncrement();
					byte[] b = new byte[3072];
					random.nextBytes(b);
					db.put(tx, longToBytes(l), b);
				}

				tx.commit();
				assertTrue(true);
			}
		}
		System.out.println("Completed updates in " + TimeUtils.elapsedSinceNano(start));
	}

	@Test
	public void E_longSequential_3kBytes_withGet() {
		System.out.println("Starting longSequential_3kBytes_withGet");
		AtomicLong along = new AtomicLong();
		Random random = new Random();

		for (int i=0; i < I_CNT; i++) {
			try (Transaction tx = env.createWriteTransaction()) {
				Map<Long, byte[]> entries = new HashMap<>();
				for (int j= 0; j < J_CNT; j++) {
					long l = along.getAndIncrement();
					byte[] b = new byte[3072];
					random.nextBytes(b);
					db.put(tx, longToBytes(l), b);
					entries.put(l, b);
				}

				tx.commit();

				entries.entrySet().stream().forEach(entry -> {
					assertArrayEquals(db.get(longToBytes(entry.getKey())), entry.getValue());
				});
			}
		}
	}

	@Test
	public void F_longSequential_3kBytes_withGetParallel() {
		System.out.println("Starting longSequential_3kBytes_withGet");
		AtomicLong along = new AtomicLong();
		Random random = new Random();

		for (int i=0; i < I_CNT; i++) {
			try (Transaction tx = env.createWriteTransaction()) {
				Map<Long, byte[]> entries = new HashMap<>();
				for (int j= 0; j < J_CNT; j++) {
					long l = along.getAndIncrement();
					byte[] b = new byte[3072];
					random.nextBytes(b);
					db.put(tx, longToBytes(l), b);
					entries.put(l, b);
				}

				tx.commit();

				entries.entrySet().parallelStream().forEach(entry -> {
					assertArrayEquals(db.get(longToBytes(entry.getKey())), entry.getValue());
				});
			}
		}
	}

//	@Test
	public void longSequential_3kBytes_withGetInSingleTransact() {
		System.out.println("Starting longSequential_3kBytes_withGet");
		AtomicLong along = new AtomicLong();
		Random random = new Random();

		for (int i=0; i < I_CNT; i++) {
			try (Transaction tx = env.createWriteTransaction()) {
				Map<Long, byte[]> entries = new HashMap<>();
				for (int j= 0; j < J_CNT; j++) {
					long l = along.getAndIncrement();
					byte[] b = new byte[3072];
					random.nextBytes(b);
					db.put(tx, longToBytes(l), b);
					entries.put(l, b);
				}

				tx.commit();

				try (Transaction tx2 = env.createReadTransaction()) {
					entries.entrySet().stream().forEach(entry -> {
						assertArrayEquals(db.get(tx2, longToBytes(entry.getKey())), entry.getValue());
					});
				}
			}
		}
	}

//	@Test
	public void longRandom_3kBytes_withGetInSingleTransact() {
		System.out.println("Starting longRandom_3kBytes_withGet");
		Random random = new Random();

		for (int i=0; i < I_CNT; i++) {
			try (Transaction tx = env.createWriteTransaction()) {
				Map<Long, byte[]> entries = new HashMap<>();
				for (int j= 0; j < J_CNT; j++) {
					long l = new Random().nextLong();
					byte[] b = new byte[3072];
					random.nextBytes(b);
					db.put(tx, longToBytes(l), b);
					entries.put(l, b);
				}

				tx.commit();

				try (Transaction tx2 = env.createReadTransaction()) {
					entries.entrySet().stream().forEach(entry -> {
						assertArrayEquals(db.get(tx2, longToBytes(entry.getKey())), entry.getValue());
					});
				}
			}
		}
	}

//	@Test
	public void longSequential_Resize() {
		System.out.println("Starting longSequential_Resize");
		AtomicLong along = new AtomicLong();
		long totSize = 0;

		for (int i=0; i < I_CNT; i++) {
			long start = System.nanoTime();
			int sz = 0;
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					long l = along.getAndIncrement();
					sz = 2 + (i*100) + (j*1);
					db.put(tx, longToBytes(l), new byte[sz]);
					totSize += 8;
					totSize += sz;
				}

				tx.commit();
				assertTrue(true);
			}
			System.out.println("Completed " + i + " in " + TimeUtils.elapsedSinceNano(start) + ", last Size:" + sz);
		}
		System.out.println("Completed with totSize " + totSize);
	}

//	@Test
	public void UpdateResize() {
		System.out.println("Starting UpdateResize");

		long l = 200L;
		byte[] longToBytes = longToBytes(l);

		for (int i=0; i < I_CNT; i++) {
			long start = System.nanoTime();
			int sz = 0;
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					sz = 32 + (i*800) + (j*8);
					db.put(tx, longToBytes, new byte[sz]);
				}

				tx.commit();
				assertTrue(true);
			}
			System.out.println("Completed " + i + " in " + TimeUtils.elapsedSinceNano(start) + ", last Size:" + sz);
		}
	}


//	// standard put & get back
//	private void doTest2() {
//		try (Transaction tx = env.createWriteTransaction()) {
//			try (Cursor cursor = db.openCursor(tx)) {
//				cursor.put(bytes("New York"), bytes("gray"), 0);
//			}
//			tx.commit();
//
//			assertArrayEquals(db.get(bytes("New York")), bytes("gray"));
//		}
//	}
//
//	//change value (secondary needs to update as well)
//	private void doTest3() {
//		try (Transaction tx = env.createWriteTransaction()) {
//			try (Cursor cursor = db.openCursor(tx)) {
//				cursor.put(bytes("New York"), bytes("green"), 0);
//			}
//			tx.commit();
//
//			assertArrayEquals(db.get(bytes("New York")), bytes("green"));
//			assertNull(secDb.get(bytes("gray")));
//		}
//	}
//
//	// try to change with flag
//	private void doTest4() {
//		try (Transaction tx = env.createWriteTransaction()) {
//			try (Cursor cursor = db.openCursor(tx)) {
//				cursor.put(bytes("New York"), bytes("silver"), NOOVERWRITE);
//			}
//			tx.commit();
//
//			assertArrayEquals(db.get(bytes("New York")), bytes("green"));
//		}
//	}
//
//	// various gets
//	private void doTest5() {
//		// standard get
//		assertArrayEquals(db.get(bytes("Tampa")), bytes("green"));
//
//		// secondary get of unique
//		assertArrayEquals(secDb.get(bytes("red")), bytes("London"));
//
//		// secondary get of multiple (return last value)
//		assertArrayEquals(secDb.get(bytes("green")), bytes("New York"));
//
//		// secondary cursor to get multiple
//		try (Transaction tx = env.createReadTransaction(); Cursor cursor = secDb.openCursor(tx)) {
//			LinkedList<String> values = new LinkedList<>();
//
//			byte[] key = bytes("green");
//			Entry entry = cursor.get(CursorOp.SET, key);
//
//			while (entry != null) {
//				values.add(string(entry.getValue()));
//				entry = cursor.get(CursorOp.NEXT_DUP, key, entry.getValue());
//			}
//
//			assertEquals(Arrays.asList(new String[] { "New York", "Tampa" }), values);
//		}
//
//		// standard get
//		assertArrayEquals(db.get(bytes("London")), bytes("red"));
//		assertArrayEquals(db.get(bytes("New York")), bytes("green"));
//
//		// standard cursor db scan
//		try (Transaction tx = env.createReadTransaction(); Cursor cursor = db.openCursor(tx)) {
//			// Lets verify cursoring works..
//			List<String> keys = new LinkedList<>();
//			List<String> values = new LinkedList<>();
//			for (Entry entry = cursor.get(FIRST); entry != null; entry = cursor.get(NEXT)) {
//				keys.add(string(entry.getKey()));
//				values.add(string(entry.getValue()));
//			}
//			assertEquals(Arrays.asList(new String[] { "London", "New York", "Tampa" }), keys);
//			assertEquals(Arrays.asList(new String[] { "red", "green", "green" }), values);
//		}
//
//		// secondary cursor db scan
//		try (Transaction tx = env.createReadTransaction(); Cursor cursor = secDb.openCursor(tx)) {
//			// Lets verify cursoring works..
//			Set<String> keys = new HashSet<>();
//			Set<String> values = new HashSet<>();
//			for (Entry entry = cursor.get(FIRST); entry != null; entry = cursor.get(NEXT)) {
//				keys.add(string(entry.getKey()));
//				values.add(string(entry.getValue()));
//			}
//			assertEquals(new HashSet<String>(Arrays.asList(new String[] { "red", "green" })), keys);
//			assertEquals(new HashSet<String>(Arrays.asList(new String[] { "London", "New York", "Tampa" })), values);
//		}
//	}
//
//	// standard delete
//	private void doTest6() {
//		try (Transaction tx = env.createWriteTransaction()) {
//			try (Cursor cursor = db.openCursor(tx)) {
//				Entry entry = cursor.get(CursorOp.SET, bytes("New York"));
//				assertNotNull(entry);
//				cursor.delete();
//			}
//			tx.commit();
//
//			assertNull(db.get(bytes("New York")));
//			// secondary get after delete of one entry should return remaining
//			assertArrayEquals(secDb.get(bytes("green")), bytes("Tampa"));
//
//			// We should not be able to delete it again.
//			assertFalse(db.delete(bytes("New York")));
//		}
//	}
//
//	// standard delete and test of now empty secondary
//	private void doTest7() {
//		try (Transaction tx = env.createWriteTransaction()) {
//			try (Cursor cursor = db.openCursor(tx)) {
//				Entry entry = cursor.get(CursorOp.SET, bytes("Tampa"));
//				assertNotNull(entry);
//				cursor.delete();
//			}
//			tx.commit();
//
//			assertNull(secDb.get(bytes("green")));
//		}
//	}

	public byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(x);
		return buffer.array();
	}
}
