package com.castortech.mdbxjni;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static com.castortech.mdbxjni.Bytes.fromLong;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class DatabaseTest {
  static {
    Setup.setLmdbLibraryPath();
  }

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  Env env;
  Database db;

  @Before
  public void before() throws IOException {
    String path = tmp.newFolder().getCanonicalPath();
    env = new Env();
    env.setMapSize(16 * 4096);
    env.open(path);
    db = env.openDatabase();
  }

  @After
  public void after() {
    db.close();
    env.close();
  }

  /**
   * Should trigger MDB_MAP_FULL if entries wasn't deleted.
   */
  @Test
  public void testCleanupFullDb() {
    for (int i = 0; i < 100; i++) {
      // twice the size of page size
      db.put(fromLong(i), new byte[2 * 4096]);
      db.delete(fromLong(i));
    }
  }

  @Test
  public void testDrop() {
    byte[] bytes = {1,2,3};
    db.put(bytes, bytes);
    byte[] value = db.get(bytes);
    assertArrayEquals(value, bytes);
    // empty
    db.drop(false);
    value = db.get(bytes);
    assertNull(value);
    db.put(bytes, bytes);
    db.drop(true);
    try {
      db.get(bytes);
      fail("db has been closed");
    } catch (MDBXException e) {

    }
  }

  @Test
  public void testStat() {
    db.put(new byte[]{1}, new byte[]{1});
    db.put(new byte[]{2}, new byte[]{1});
    Stat stat = db.stat();
    System.out.println(stat);
    assertThat(stat.ms_entries, is(2L));
    assertThat(stat.ms_psize, is(not(0L)));
    assertThat(stat.ms_overflow_pages, is(0L));
    assertThat(stat.ms_depth, is(1L));
    assertThat(stat.ms_leaf_pages, is(1L));
  }

//  @Test
//  public void testDeleteBuffer() {
//    db.put(new byte[]{1}, new byte[]{1});
//    DirectBuffer key = new DirectBuffer(ByteBuffer.allocateDirect(1));
//    key.putByte(0, (byte) 1);
//    db.delete(key);
//    assertNull(db.get(new byte[]{1}));
//  }
}
