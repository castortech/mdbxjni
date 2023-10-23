package com.castortech.mdbxjni;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class StaggeredTransactionTest {
	static {
		Setup.setLibraryPaths();
	}

	final ReentrantLock COMMIT_LOCK = new ReentrantLock(true);

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	Env env;
	Database db;
	byte[] data = new byte[]{1, 2, 3};

	@Before
	public void before() throws IOException {
		String path = tmp.newFolder().getCanonicalPath();
		env = new Env(path);
		db = env.openDatabase();
	}

	@After
	public void after() {
		db.close();
		env.close();
	}

	@Test
	public void testStaggered() {
		int threads = 2;
		ExecutorService service = Executors.newFixedThreadPool(threads);
		CountDownLatch latch = new CountDownLatch(1);

		Collection<Future<Void>> futures = new ArrayList<>(threads);

		futures.add(service.submit(() -> {
			latch.await();

			try (Transaction tx = env.createWriteTransaction()) {
				System.out.println("T1 started transaction");
				COMMIT_LOCK.lock();
				System.out.println("T1 acquired lock");
				db.put(tx, data, data);
				tx.commit();
			}
			finally {
				Thread.sleep(2);
				COMMIT_LOCK.unlock();
				System.out.println("T1 released lock");
			}
		 	return null;
		}));

		futures.add(service.submit(() -> {
			latch.await();

			try {
				Transaction tx = env.createWriteTransaction();
				System.out.println("T2 started transaction");
	//		Thread.sleep(10);
				COMMIT_LOCK.lock();
				System.out.println("T2 acquired lock");
				tx.abort();
				System.out.println("T2 aborted");
			}
			finally {
				COMMIT_LOCK.unlock();
				System.out.println("T2 released lock");
			}

			try (Transaction tx = env.createWriteTransaction()) {
				System.out.println("T2 restarted transaction");
				assertArrayEquals(data, db.get(tx, data));
				System.out.println("T2 read data back");
	// 		Thread.sleep(10);
				COMMIT_LOCK.lock();
				System.out.println("T2 reacquired lock");
			}
			finally {
				COMMIT_LOCK.unlock();
				System.out.println("T2 released lock");
			}

			return null;
		}));

		latch.countDown();

		for (Future<Void> f : futures) {
			try {
				f.get();
//	 		Thread.sleep(2);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
}