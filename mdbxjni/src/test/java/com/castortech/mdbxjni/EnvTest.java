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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Arrays;
import java.util.LinkedList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static com.castortech.mdbxjni.Constants.*;

/**
 * Unit tests for the LMDB API.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class EnvTest {
  static {
    Setup.setLibraryPaths();
  }

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  @Rule
  public TemporaryFolder backup = new TemporaryFolder();

  @Test
  public void testCRUD() throws Exception {
    String path = tmp.newFolder().getCanonicalPath();
    try (Env env = new Env()) {
      env.open(path);
      Env.pushMemoryPool(10);
      Env.pushMemoryPool(10);
      Env.popMemoryPool();
      Env.popMemoryPool();
      try (Database db = env.openDatabase()) {
        doTest(env, db);
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
        db.put(new byte[]{1}, new byte[]{1});
        env.copy(backupPath);
      }
    }
    try (Env env = new Env()) {
      env.open(backupPath);
      try (Database db = env.openDatabase()) {
        byte[] value = db.get(new byte[]{1});
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
        db.put(new byte[]{1}, new byte[]{1});
//        env.copyCompact(backupPath);
        env.copy(backupPath);
      }
    }
    try (Env env = new Env()) {
      env.open(backupPath);
      try (Database db = env.openDatabase()) {
        byte[] value = db.get(new byte[]{1});
        assertThat((int) value[0], is(1));
      }
    }
  }

  @Test
  public void testEnvInfo() throws Exception {
    String path = tmp.newFolder().getCanonicalPath();
    try (Env env = new Env()) {
//      env.addFlags(CREATE);
      env.open(path);
      env.setMapSize(1048576L);
      try (Database db = env.openDatabase()) {
        db.put(new byte[]{1}, new byte[]{1});
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
        db.put(new byte[]{1}, new byte[]{1});
        db.put(new byte[]{2}, new byte[]{1});
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
      assertThat(env.getMaxKeySize(), is(1980L));
    }
  }

  private void doTest(Env env, Database db) {

    assertNull(db.put(bytes("Tampa"), bytes("green")));
    assertNull(db.put(bytes("London"), bytes("red")));

    assertNull(db.put(bytes("New York"), bytes("gray")));
    assertNull(db.put(bytes("New York"), bytes("blue")));
    assertArrayEquals(db.put(bytes("New York"), bytes("silver"), NOOVERWRITE), bytes("blue"));

    assertArrayEquals(db.get(bytes("Tampa")), bytes("green"));
    assertArrayEquals(db.get(bytes("London")), bytes("red"));
    assertArrayEquals(db.get(bytes("New York")), bytes("blue"));


    try (Transaction tx = env.createReadTransaction(); Cursor cursor = db.openCursor(tx)) {
      // Lets verify cursoring works..
      LinkedList<String> keys = new LinkedList<>();
      LinkedList<String> values = new LinkedList<>();
      for (Entry entry = cursor.get(FIRST); entry != null; entry = cursor.get(NEXT)) {
        keys.add(string(entry.getKey()));
        values.add(string(entry.getValue()));
      }
      assertEquals(Arrays.asList(new String[]{"London", "New York", "Tampa"}), keys);
      assertEquals(Arrays.asList(new String[]{"red", "blue", "green"}), values);
    }

    assertTrue(db.delete(bytes("New York")));
    assertNull(db.get(bytes("New York")));

    // We should not be able to delete it again.
    assertFalse(db.delete(bytes("New York")));

    // put /w readonly transaction should fail.
    try (Transaction tx = env.createReadTransaction()) {
      db.put(tx, bytes("New York"), bytes("silver"));
      fail("Expected LMDBException");
    } 
    catch (MDBXException e) {
      assertTrue(e.getErrorCode() > 0);
    }
    env.sync(true);
  }
}
