package org.fusesource.lmdbjni;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class TransactionTest {
  static {
    Setup.setLmdbLibraryPath();
  }

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

//  @Test
//  public void testGetId() throws Exception {
//    final AtomicLong tx1 = new AtomicLong();
//    final AtomicLong tx2 = new AtomicLong();
//    try (Transaction tx = env.createReadTransaction()) {
//      tx1.set(tx.getId());
//    }
//    db.put(new byte[] {1}, new byte[]{2});
//    try (Transaction tx = env.createReadTransaction()) {
//      tx2.set(tx.getId());
//    }
//    // should not see the same snapshot
//    assertThat(tx1.get(), is(not(tx2.get())));
//  }

  @Test
  public void testCommit() {
    try (Transaction tx = env.createWriteTransaction()) {
      db.put(tx, data, data);
      tx.commit();
    }
    assertArrayEquals(data, db.get(data));

    try (Transaction tx = env.createWriteTransaction()) {
      db.delete(tx, data);
      tx.commit();
    }
    assertNull(db.get(data));
  }

  @Test
  public void testAbort() {
    try (Transaction tx = env.createWriteTransaction()) {
      db.put(tx, data, data);
    }
    assertNull(db.get(data));

    db.put(data, data);

    try (Transaction tx = env.createWriteTransaction()) {
      db.delete(tx, data);
    }
    assertArrayEquals(data, db.get(data));
  }

  @Test
  public void testResetRenew() {
    db.put(data, data);
    try (Transaction tx = env.createReadTransaction()) {
      assertArrayEquals(data, db.get(tx, data));
      tx.reset();
      try {
        db.get(tx, data);
        fail("should fail since transaction was reset");
      } catch (LMDBException e) {
        // ok
      }
      tx.renew();
      assertArrayEquals(data, db.get(tx, data));
    }
  }

}
