package com.castortech.mdbxjni;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertArrayEquals;

public class CursorTest {
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
    env = new Env(path);
    db = env.openDatabase();
  }

  @After
  public void after() {
    db.close();
    env.close();
  }

  @Test
  public void testCursorPutGet() {
    try (Transaction tx = env.createWriteTransaction()) {
      try (Cursor cursor = db.openCursor(tx)) {
        cursor.put(Bytes.fromLong(1), Bytes.fromLong(1), 0);
      }
      tx.commit();
    }
    assertArrayEquals(db.get(Bytes.fromLong(1)), Bytes.fromLong(1));
  }

  @Test
  public void testCursorRenew() {
    Transaction read = env.createReadTransaction();
    try (Cursor readCursor = db.openCursor(read)) {
      
      try (Transaction write = env.createWriteTransaction()) {
        try (Cursor cursor = db.openCursor(write)) {
          cursor.put(Bytes.fromLong(1), Bytes.fromLong(1), 0);
        }
        write.commit();
      }
      assertNull(readCursor.get(CursorOp.FIRST));
      read.abort();
      read = env.createReadTransaction();
      readCursor.renew(read);
      assertArrayEquals(readCursor.get(CursorOp.FIRST).getKey(), Bytes.fromLong(1));
    }
    read.abort();
  }
}
