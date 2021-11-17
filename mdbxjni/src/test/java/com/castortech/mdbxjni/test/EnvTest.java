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

package com.castortech.mdbxjni.test;

import junit.framework.TestCase;
import com.castortech.mdbxjni.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import static com.castortech.mdbxjni.Constants.FIRST;
import static com.castortech.mdbxjni.Constants.NEXT;
import static com.castortech.mdbxjni.Constants.*;

/**
 * Unit tests for the LMDB API.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class EnvTest extends TestCase {
	static {
		Setup.setLibraryPaths();
	}

	public static void assertEquals(byte[] arg1, byte[] arg2) {
		assertTrue(Arrays.equals(arg1, arg2));
	}

	static File getTestDirectory(String name) throws IOException {
		File rc = new File(new File("test-data"), name);
		rc.mkdirs();
		return rc;
	}

	@SuppressWarnings("nls")
	@Test
	public void testCRUD() throws IOException {
		String path = getTestDirectory(getName()).getCanonicalPath();

		Global global = new Global();

		RamInfo sysRamInfo = global.getSysRamInfo();
		System.out.println("System RamInfo:" + sysRamInfo);

		long minPgSize = global.getMinPageSize();
		long maxPgSize = global.getMaxPageSize();
		System.out.println("Page size MinMax:" + minPgSize + '/' + maxPgSize);

		long pgSize = 4096;
		long minDbSize = global.getMinDbSize(pgSize);
		long maxDbSize = global.getMaxDbSize(pgSize);
		long maxTxnSize = global.getMaxTxnSize(pgSize);
		long maxKeySize = global.getMaxKeySize(pgSize, Constants.DBDEFAULTS);
		long maxValSize = global.getMaxValSize(pgSize, Constants.DBDEFAULTS);
		System.out.println("4k page size, Db MinMax:" + minDbSize + '/' + maxDbSize +
				", max txn:" + maxTxnSize + ", max key:" + maxKeySize + ", max val:" + maxValSize);

		Env env = new Env();
		System.out.println("path:" + path);
		env.open(path);
		Database db = env.openDatabase("foo");

		byte[] origVal = bytes("green");
		assertNull(db.put(bytes("Tampa"), origVal));

		byte[] oldVal = db.replace(bytes("Tampa"), bytes("orange"));
		assertEquals(origVal, oldVal);

		assertNull(db.put(bytes("London"), bytes("red")));

		assertNull(db.put(bytes("New York"), bytes("gray")));
		assertNull(db.put(bytes("New York"), bytes("blue")));
		assertEquals(db.put(bytes("New York"), bytes("silver"), NOOVERWRITE), bytes("blue"));

		assertEquals(db.get(bytes("Tampa")), bytes("orange"));
		assertEquals(db.get(bytes("London")), bytes("red"));
		assertEquals(db.get(bytes("New York")), bytes("blue"));

		Transaction tx = env.createTransaction();
		Cursor cursor = db.openCursor(tx);

		FlagState flagState = db.getFlagsState(tx);
		System.out.println("Db FlagState:" + flagState);

		//only for dupsort db
//		int depthMask = db.getDupsortDepthMask(tx);
//		System.out.println("Db depthMask:" + depthMask);

		int sequence = db.getSequence(tx, 1);
		System.out.println("Db sequence:" + sequence);

		EnvInfo envInfo = tx.envInfo();
		System.out.println("Tx env info:" + envInfo);

		Stat stat = tx.stat();
		System.out.println("Tx stat:" + stat);

		// Lets verify cursoring works..
		LinkedList<String> keys = new LinkedList<String>();
		LinkedList<String> values = new LinkedList<String>();

		for (Entry entry = cursor.get(FIRST); entry != null; entry = cursor.get(NEXT)) {
			keys.add(string(entry.getKey()));
			values.add(string(entry.getValue()));
		}
		CommitLatency commitWithLatency = tx.commitWithLatency();
		assertEquals(Arrays.asList(new String[] { "London", "New York", "Tampa" }), keys);
		assertEquals(Arrays.asList(new String[] { "red", "blue", "orange" }), values);

		assertTrue(db.delete(bytes("New York")));
		assertNull(db.get(bytes("New York")));

		// We should not be able to delete it again.
		assertFalse(db.delete(bytes("New York")));

		// put /w readonly transaction should fail.
		tx = env.createTransaction(true);
		try {
			TxnInfo txnInfo = tx.info(false);
			System.out.println("TxInfo:" + txnInfo);
			db.put(tx, bytes("New York"), bytes("silver"));
			fail("Expected LMDBException");
		}
		catch (MDBXException e) {
			assertEquals(MDBXException.Status.EACCES.getStatusCode(), e.getErrorCode());
		}

		db.close();
		env.close();
	}
}