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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.castortech.mdbxjni.types.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Unit tests for the MDBX API.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@SuppressWarnings("nls")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InsertTestDupeKey {
	private static final Logger log = LoggerFactory.getLogger(InsertTestDupeKey.class);

	private static int I_CNT = 1;
	private static int J_CNT = 1000000;

//	private static int I_CNT = 1000;
//	private static int J_CNT = 100;

	private static long pageSize = -1;

	static {
		Setup.setLibraryPaths();
		try {
			Optional.ofNullable(System.getProperty("pg.size"))
					.ifPresent(val -> pageSize = Long.parseLong(val));
		}
		catch (Exception e) {
			System.err.println("Exception resolving page size, using default");// TODO: handle exception
		}
		try {
			Optional.ofNullable(System.getProperty("cnt.i"))
					.ifPresent(val -> I_CNT = Integer.parseInt(val));
		}
		catch (Exception e) {
			System.err.println("Exception resolving I_CNT, using default");// TODO: handle exception
		}
		try {
			Optional.ofNullable(System.getProperty("cnt.j"))
					.ifPresent(val -> J_CNT = Integer.parseInt(val));
		}
		catch (Exception e) {
			System.err.println("Exception resolving J_CNT, using default");// TODO: handle exception
		}
	}

	Env env;
	Database db;

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();
	private OperationStatus operationStatus;

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
		envConfig.setPageSize(pageSize);

		//nosync
//		envConfig.setWriteMap(true);
//		envConfig.setUtterlyNoSync(true);
//		envConfig.setNoMetaSync(true);

//	envConfig.setMapAsync(true);

		env = new Env();
		env.setMaxDbs(2);
		Env.pushMemoryPool(1024*512);
		env.open(path, envConfig);


		DebugState debugState = env.setupDebug(MdbxLogLevel.EXTRA, JNI.MDBX_DBG_AUDIT | JNI.MDBX_DBG_ASSERT);
		System.out.println("DebugState:" + debugState);


//		db = env.openDatabase("primary");
//		db = env.openDatabase("primary", new KeyComparator(), null);
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setDupSort(true);
		dbConfig.setDupFixed(true);
		dbConfig.setCreate(true);
//		dbConfig.setKeyComparator(UnsignedBytes.lexicographicalComparator());
		db = env.openDatabase("primary", dbConfig);

//		SecondaryDbConfig secConfig = new SecondaryDbConfig();
//		secConfig.setCreate(true);
//		secConfig.setDupSort(true);
//		secDb = env.openSecondaryDatabase(db, "secondary", secConfig);

		//warm up routine
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
//	@Test
	public void A1_uuidRandom_100Bytes() {
		long start = System.nanoTime();
		System.out.println("Starting uuidRandom_100Bytes ungrouped");
		for (int i=0; i < I_CNT; i++) {
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					UUID uuid = UUID.randomUUID();
///					System.out.println("\t putting " + uuid);

					for (int k= 0; k < 6; k++) {  //6 = 96 bytes
						UUID valid = UUID.randomUUID();
						db.put(tx, UuidAdapter.getBytesFromUUID(uuid), UuidAdapter.getBytesFromUUID(valid),
								Constants.NODUPDATA);
					}
					log.info("\t Putting, j:{}, uuid:{}", j, uuid);
				}

				tx.commit();

//				CommitLatency latency = tx.commitWithLatency();
//				System.out.println("Latency:" + latency);
				assertTrue(true);
			}
		}
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));
		System.out.println(TestUtils.getStats(env, env.createReadTransaction(), Collections.emptyMap()));
	}

	@Test
	public void A2_uuidRandom_100Bytes() {
		long start = System.nanoTime();
		UUID getUuid = null;
		System.out.println("Starting uuidRandom_100Bytes grouped");
		for (int i=0; i < I_CNT; i++) {
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				EnvInfo envInfo = env.info(tx);
				System.out.println("Tx env info, at txn start:" + envInfo);
				Random random = new Random();

				for (int j= 0; j < J_CNT; j++) {
					UUID uuid = UUID.randomUUID();

					if (j == 22) {
						getUuid = uuid;
					}

					int valCnt = random.nextInt(10) + 1;
					byte[] vals = new byte[valCnt*20];

					for (int k= 0; k < valCnt; k++) {  //6 = 96 bytes
						byte[] buf = new byte[20];

						System.arraycopy(ByteUtil.unsignedIntByteArray(k), 0, buf, 0, 4);

						UUID valid = UUID.randomUUID();
						System.arraycopy(UuidAdapter.getBytesFromUUID(valid), 0, buf, 4, 16);

						System.arraycopy(buf, 0, vals, k *20, 20);
					}
					log.info("\t Putting, j:{}, uuid:{}, valCnt:{}", j, uuid, valCnt);
					db.put(tx, UuidAdapter.getBytesFromUUID(uuid), vals, Constants.MULTIPLE, valCnt);
				}

				envInfo = env.info(tx);
				System.out.println("Tx env info, at pre-commit:" + envInfo);

//				tx.commit();

				CommitLatency latency = tx.commitWithLatency();
				System.out.println("Latency:" + latency);

				envInfo = env.info();
				System.out.println("Tx env info, after commit:" + envInfo);


				assertTrue(true);
			}
		}
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));

//		try (Transaction tx = env.createReadTransaction(); Cursor cursor = db.openCursor(tx)) {
//			System.out.println(TestUtils.getStats(env, tx, Collections.emptyMap()));
//			LinkedList<UUID> values = new LinkedList<>();
//
//			byte[] key = UuidAdapter.getBytesFromUUID(getUuid);
//			Entry entry = cursor.get(CursorOp.SET, key);
//
//			while (entry != null) {
//				values.add(UuidAdapter.getUUIDFromBytes(entry.getValue()));
//				entry = cursor.get(CursorOp.GET_MULTIPLE, key, entry.getValue());
//			}
//
//			System.out.println("\t got values:" + values);
//		}
	}

//	@Test
	public void B_longSequential_100Bytes() {
		long start = System.nanoTime();
		System.out.println("Starting longSequential_100Bytes");
		longSequential_100Bytes_intern();
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));
		System.out.println(TestUtils.getStats(env, env.createReadTransaction(), Collections.emptyMap()));
	}

	public void longSequential_100Bytes_intern() {
		AtomicLong along = new AtomicLong();

		for (int i=0; i < I_CNT; i++) {
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					long l = along.getAndIncrement();
					for (int k= 0; k < 6; k++) {  //6 = 96 bytes
						UUID uuid = UUID.randomUUID();
						db.put(tx, longToBytes(l), UuidAdapter.getBytesFromUUID(uuid), Constants.NODUPDATA);
					}
				}

				tx.commit();

//				CommitLatency latency = tx.commitWithLatency();
//				System.out.println("Latency:" + latency);
				assertTrue(true);
			}
//			System.out.println("Completed " + i + " in " + TimeUtils.elapsedSinceNano(start));
		}
	}

//	@Test
	public void C1_longRandom_100Bytes() {
		long start = System.nanoTime();
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
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));
		System.out.println(TestUtils.getStats(env, env.createReadTransaction(), Collections.emptyMap()));
	}

//	@Test
	public void C2_longRandom_200Bytes() {
		long start = System.nanoTime();
		System.out.println("Starting longRandom_200Bytes ungrouped");
		long getLong = 0;

		for (int i=0; i < I_CNT; i++) {
//			long start = System.nanoTime();
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					long l = new Random().nextLong();

					if (j == 22) {
						getLong = l;
					}

					for (int k= 0; k < 1; k++) {  //25 = 200 bytes
						long val = new Random().nextLong();
						db.put(tx, longToBytes(l), longToBytes(val), Constants.NODUPDATA);
					}
				}

				tx.commit();
				assertTrue(true);
			}
//			System.out.println("Completed " + i + " in " + TimeUtils.elapsedSinceNano(start));
		}
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));

		try (Transaction tx = env.createReadTransaction(); Cursor cursor = db.openCursor(tx)) {
			System.out.println(TestUtils.getStats(env, tx, Collections.emptyMap()));
			LinkedList<Long> values = new LinkedList<>();

			byte[] key = longToBytes(getLong);
//			Entry entry = cursor.get(CursorOp.SET, key);
			Entry entry = cursor.get(CursorOp.GET_MULTIPLE, key);
			if (entry != null) {
				entry = cursor.get(CursorOp.GET_MULTIPLE, key, entry.getValue());

				while (entry != null) {
					byte[] entryVal = entry.getValue();
					int lgth = entryVal.length / 8;
					for (int i = 0; i < lgth; i++) {
						values.add(bytesToLong(Arrays.copyOfRange(entryVal, i * 8, (i * 8) + 8)));
					}
					entry = cursor.get(CursorOp.NEXT_MULTIPLE, key, entry.getValue());
				}
			}

			System.out.println("\t got values:" + values);
		}
	}

//	@Test
	public void D_longSequential_4kBytes() {
		long start = System.nanoTime();
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
		}
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));
	}

//	@Test
	public void E1_uuidRandom_4kBytes() {
		long start = System.nanoTime();
		System.out.println("Starting uuidRandom_4kBytes");
		UUID getUuid = null;

		for (int i=0; i < I_CNT; i++) {
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					UUID uuid = UUID.randomUUID();

					if (j == 22) {
						getUuid = uuid;
					}

					byte[] vals = new byte[256*16];
					for (int k= 0; k < 256; k++) {  //256 = 4096 bytes
						UUID valid = UUID.randomUUID();
						System.arraycopy(UuidAdapter.getBytesFromUUID(valid), 0, vals, k *16, 16);
					}
					db.put(tx, UuidAdapter.getBytesFromUUID(uuid), vals, Constants.MULTIPLE, 256);
				}

				tx.commit();

//				CommitLatency latency = tx.commitWithLatency();
//				System.out.println("Latency:" + latency);
				assertTrue(true);
			}
		}
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));
		try (Transaction tx = env.createReadTransaction(); Cursor cursor = db.openCursor(tx)) {
			System.out.println(TestUtils.getStats(env, tx, Collections.emptyMap()));
			LinkedList<UUID> values = new LinkedList<>();

			byte[] key = UuidAdapter.getBytesFromUUID(getUuid);
			Entry entry = cursor.get(CursorOp.GET_MULTIPLE, key);

			while (entry != null) {
				byte[] entryVal = entry.getValue();
				int lgth = entryVal.length / 16;
				for (int i = 0; i < lgth; i++) {
					values.add(UuidAdapter.getUUIDFromBytes(ByteBuffer.wrap(entryVal, i * 16, 16).slice().array()));
				}
				entry = cursor.get(CursorOp.NEXT_MULTIPLE, key, entry.getValue());
			}

			System.out.println("\tGot " + values.size() + " values:" + values);
		}
	}

//	@Test
	public void E2_uuidRandom_4kBytes() {
		long start = System.nanoTime();
		System.out.println("Starting uuidRandom_4kBytes");
		for (int i=0; i < I_CNT; i++) {
//			System.out.println("start trans " + i);
			try (Transaction tx = env.createWriteTransaction()) {
				for (int j= 0; j < J_CNT; j++) {
					UUID uuid = UUID.randomUUID();
///					System.out.println("\t putting " + uuid);
					byte[] vals = new byte[256*16];

					for (int k= 0; k < 256; k++) {  //256 = 4096 bytes
						UUID valid = UUID.randomUUID();
						System.arraycopy(UuidAdapter.getBytesFromUUID(valid), 0, vals, k *16, 16);
					}
					db.put(tx, UuidAdapter.getBytesFromUUID(uuid), vals, Constants.MULTIPLE, 256);
				}

				tx.commit();

//				CommitLatency latency = tx.commitWithLatency();
//				System.out.println("Latency:" + latency);
				assertTrue(true);
			}
		}
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));
		System.out.println(TestUtils.getStats(env, env.createReadTransaction(), Collections.emptyMap()));
	}

//	@Test
	public void E_longSequential_3kBytes() {
		long start = System.nanoTime();
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
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));
	}

//	@Test
	public void F_longRandom_4kBytes() {
		long start = System.nanoTime();
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
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));
	}

//	@Test
	public void G_longRandom_3kBytes() {
		long start = System.nanoTime();
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
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));
	}

//	@Test
	public void H_longSequential_3kBytes_withUpdate() {
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

//	@Test
	public void I_longSequential_3kBytes_withGet() {
		long start = System.nanoTime();
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
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));
	}

//	@Test
	public void J_longSequential_3kBytes_withGetParallel() {
		long start = System.nanoTime();
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
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));
	}

//	@Test
	public void K_longSequential_3kBytes_withGetInSingleTransact() {
		long start = System.nanoTime();
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
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));
	}

//	@Test
	public void L_longRandom_3kBytes_withGetInSingleTransact() {
		long start = System.nanoTime();
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
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));
	}

//	@Test
	public void M_longSequential_Resize() {
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
	public void N_UpdateResize() {
		long start = System.nanoTime();
		System.out.println("Starting UpdateResize");

		long l = 200L;
		byte[] longToBytes = longToBytes(l);

		for (int i=0; i < I_CNT; i++) {
//			long start = System.nanoTime();
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
//			System.out.println("Completed " + i + " in " + TimeUtils.elapsedSinceNano(start) + ", last Size:" + sz);
		}
		System.out.println("Completed " + I_CNT + " in " + TimeUtils.elapsedSinceNano(start));
	}

//	@Test
	public void multiple_put_replace() {
		long start = System.nanoTime();
		System.out.println("Starting multiple_put_replace");
		long getLong = 0;

		try (Transaction tx = env.createWriteTransaction()) {
			for (int j= 0; j < J_CNT; j++) {
				long l = new Random().nextLong();

				if (j == 22) {
					getLong = l;
				}

				for (int k= 0; k < 5; k++) {  //25 = 200 bytes
					long val = k;
					db.put(tx, longToBytes(l), longToBytes(val), Constants.NODUPDATA);
				}
			}

			tx.commit();
			assertTrue(true);
		}

		{
			Transaction tx = env.createReadTransaction();
			Cursor cursor = db.openCursor(tx);
			System.out.println(TestUtils.getStats(env, tx, Collections.emptyMap()));
			LinkedList<Long> values = new LinkedList<>();

			byte[] key = longToBytes(getLong);
			Entry entry = cursor.get(CursorOp.GET_MULTIPLE, key);
			if (entry != null) {
				while (entry != null) {
					byte[] entryVal = entry.getValue();
					int lgth = entryVal.length / 8;
					for (int i = 0; i < lgth; i++) {
						values.add(bytesToLong(Arrays.copyOfRange(entryVal, i * 8, (i * 8) + 8)));
					}
					entry = cursor.get(CursorOp.NEXT_MULTIPLE, key, entry.getValue());
				}
			}

			System.out.println("\t got values:" + values);
			cursor.close();
			tx.commit();
		}

		try (Transaction tx = env.createWriteTransaction()) {
			byte[] vals = new byte[5*8];
			for (int k= 0; k < 5; k++) {  //25 = 200 bytes
				long val = k + 5;
				System.arraycopy(longToBytes(val), 0, vals, k *8, 8);
			}

			db.put(tx, longToBytes(getLong), vals, Constants.MULTIPLE | Constants.CURRENT | Constants.ALLDUPS, 5);

			tx.commit();
			assertTrue(true);
		}

		try (Transaction tx = env.createReadTransaction(); Cursor cursor = db.openCursor(tx)) {
			System.out.println(TestUtils.getStats(env, tx, Collections.emptyMap()));
			LinkedList<Long> values = new LinkedList<>();

			byte[] key = longToBytes(getLong);
			Entry entry = cursor.get(CursorOp.GET_MULTIPLE, key);
			if (entry != null) {
				while (entry != null) {
					byte[] entryVal = entry.getValue();
					int lgth = entryVal.length / 8;
					for (int i = 0; i < lgth; i++) {
						values.add(bytesToLong(Arrays.copyOfRange(entryVal, i * 8, (i * 8) + 8)));
					}
					entry = cursor.get(CursorOp.NEXT_MULTIPLE, key, entry.getValue());
				}
			}

			System.out.println("\t got values:" + values);
		}
	}

//	@Test
	public void testCursorPutGetRange() {
		UUID typeId = UUID.randomUUID();

		try (Transaction tx = env.createWriteTransaction()) {
			for (int j= 0; j < J_CNT; j++) {
				byte[] vals = new byte[25*32];  //25 x 2 id
				for (int k = 0; k < 50; k++) {
					UUID valid = UUID.randomUUID();
					if (j == 8 && k % 2 == 0)
						valid = typeId;
					System.arraycopy(UuidAdapter.getBytesFromUUID(valid), 0, vals, k *16, 16);
				}
				db.put(tx, Bytes.fromLong(j), vals, Constants.MULTIPLE, 25);
			}
			tx.commit();
		}

		Transaction read = env.createReadTransaction();

		try (Cursor cursor = db.openCursor(read)) {
			Entry entry = cursor.get(CursorOp.GET_BOTH_RANGE, Bytes.fromLong(8),
					Arrays.copyOf(UuidAdapter.getBytesFromUUID(typeId), 32));
			LinkedList<Pair<UUID, UUID>> values = new LinkedList<>();

			if (entry != null) {
				while (entry != null) {
					UUID part1 = UuidAdapter.getUUIDFromBytes(Arrays.copyOf(entry.getValue(), 16));
					if (!Objects.equals(part1, typeId)) {
						break;
					}
					byte[] entryVal = entry.getValue();
					int lgth = entryVal.length / 32;
					for (int i = 0; i < lgth; i++) {
						values.add(new Pair<>(part1,
								UuidAdapter.getUUIDFromBytes(Arrays.copyOfRange(entry.getValue(), 16, 32))));
					}
					entry = cursor.get(CursorOp.NEXT_DUP, Bytes.fromLong(8), entry.getValue());
				}
			}

			System.out.println("Got " + values.size() + " values:\n" + values.stream()
					.map(val -> val.getA().toString() + " -> " + val.getB().toString())
					.collect(Collectors.joining("\n")));
		}

		try (Cursor cursor = db.openCursor(read)) {
			DatabaseEntry key = new DatabaseEntry(Bytes.fromLong(8));
			byte[] uuidBA = UuidAdapter.getBytesFromUUID(typeId);
			DatabaseEntry value = new DatabaseEntry(Arrays.copyOf(uuidBA, 32));
			operationStatus = cursor.get(CursorOp.GET_BOTH_RANGE, key, value);
			if (operationStatus == OperationStatus.SUCCESS) {
				long cnt = cursor.count(CursorOp.NEXT_DUP, key, value,
						val -> Arrays.equals(Arrays.copyOf(val.getData(), 16), uuidBA));
				System.out.println("count is:" + cnt + " so total is:" + ++cnt);
			}
		}
//		assertArrayEquals(db.get(Bytes.fromLong(1)), Bytes.fromLong(1));
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

	public long bytesToLong(byte[] ba) {
		return ByteBuffer.wrap(ba).getLong();
	}

}