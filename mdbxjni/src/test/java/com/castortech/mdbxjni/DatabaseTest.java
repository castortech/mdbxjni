package com.castortech.mdbxjni;

import org.fluttercode.datafactory.impl.DataFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Random;

import static com.castortech.mdbxjni.Bytes.fromLong;
import static com.castortech.mdbxjni.Constants.bytes;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class DatabaseTest {
  private SecretProvider secretProvider;
  private IvProvider ivProvider;

	static {
    Setup.setLmdbLibraryPath();
  }

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  Env env;
  Database db;
  EncryptUtils encrypt;

  @Before
  public void before() throws Exception {
    String path = tmp.newFolder().getCanonicalPath();
    env = new Env();
    env.setMapSize(16 * 4096);
    env.open(path);
    ivProvider = new IvProvider(new File("ivs.dat"));
    secretProvider = new SecretProvider();
    encrypt = new EncryptUtils(secretProvider, ivProvider);
    db = env.openDatabase(new EncryptedKeyComparator(encrypt), null);
  }

  @After
  public void after() {
    db.close();
    env.close();
  }

  /**
   * Should trigger MDB_MAP_FULL if entries wasn't deleted.
   */
  //@Test
  public void testCleanupFullDb() {
    for (int i = 0; i < 100; i++) {
      // twice the size of page size
      db.put(fromLong(i), new byte[2 * 4096]);
      db.delete(fromLong(i));
    }
  }

  //@Test
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

  //@Test
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
  public void testStat2() throws Exception {
  	db.put(getEncrypted(22, bytes("Tampa")), bytes("green"));
  	db.put(getEncrypted(11, bytes("London")), bytes("red"));
  	db.put(getEncrypted(22, bytes("New-York")), bytes("red"));
    Stat stat = db.stat();
    System.out.println(stat);
    assertThat(stat.ms_entries, is(3L));
    assertThat(stat.ms_psize, is(not(0L)));
    assertThat(stat.ms_overflow_pages, is(0L));
    assertThat(stat.ms_depth, is(1L));
    assertThat(stat.ms_leaf_pages, is(1L));
  }
  
	// standard put & get back
  @Test
	public void doTest2() throws Exception {
  	Random rand = new Random();
		DataFactory df = new DataFactory();
		df.randomize(rand.nextInt());
		int cId = 0;
		String key = null;
		String val = null;
		
		try (Transaction tx = env.createWriteTransaction()) {
			try (Cursor cursor = db.openCursor(tx)) {
//				cursor.put(bytes("New York"), bytes("gray"), 0);

				for (int i = 0; i < 100; i++) {
					String cityText = df.getCity();
					String wordText = df.getRandomWord(20);
					int clientId = secretProvider.getRandomClientId();
					System.out.println("Put " + i + " with " + cityText + "," + wordText + "," + clientId);

					if (i == 62) {
						cId = clientId;
						key = cityText;
						val = wordText;
					}
					
					cursor.put(getEncrypted(clientId, bytes(cityText)), bytes(wordText), 0);
				}
				
				
//				cursor.put(getEncrypted(22, bytes("Tampa")), bytes("green"), 0);
//				cursor.put(getEncrypted(22, bytes("Tampa")), bytes("blue"), 0);
//				cursor.put(getEncrypted(11, bytes("London")), bytes("red"), 0);
//				cursor.put(getEncrypted(22, bytes("New-York")), bytes("gray"), 0);
			}
			tx.commit();

			byte[] expected = db.get(getEncrypted(cId, bytes(key)));
			
			assertArrayEquals(expected, bytes(val));

//			assertArrayEquals(db.get(getEncrypted(22, bytes("New-York"))), bytes("gray"));
//			assertArrayEquals(db.get(getEncrypted(22, bytes("Tampa"))), bytes("blue"));
		}
	}
  
  private byte[] getEncrypted(int clientId, byte[] plainBytes) throws Exception {
		ByteBuffer cipherBuff = encrypt.encryptWithInfo(plainBytes, clientId);
		return cipherBuff.array();
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
