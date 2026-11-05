package com.castortech.mdbxjni.ffm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.castortech.mdbxjni.JNI;
import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * Calls generated downcalls that were missing from the JNI facade.
 */
public class FfmMissingCallTest {
	static {
		MdbxLibrary.load();
	}

	@Test
	public void liberrReadaheadAndCacheGet() throws Exception {
		long text = JNI.mdbx_liberr2str(mdbx_h.MDBX_NOTFOUND());
		assertTrue(text != 0);
		assertTrue(FfmCalls.strlen(text) > 0);

		int ahead = JNI.mdbx_is_readahead_reasonable(1024L * 1024L, 0);
		assertTrue(ahead == mdbx_h.MDBX_SUCCESS() || ahead == mdbx_h.MDBX_RESULT_TRUE()
				|| ahead == mdbx_h.MDBX_RESULT_FALSE());

		Path dir = Files.createTempDirectory("mdbx-ffm-missing");
		Path db = dir.resolve("missing.mdbx");
		long[] env = new long[1];
		assertEquals(mdbx_h.MDBX_SUCCESS(), JNI.mdbx_env_create(env));
		long keyPtr = 0;
		long dataPtr = 0;
		try {
			assertEquals(mdbx_h.MDBX_SUCCESS(), JNI.mdbx_env_set_mapsize(env[0], 8L * 1024 * 1024));
			assertEquals(mdbx_h.MDBX_SUCCESS(), JNI.mdbx_env_open(env[0], db.toString(), mdbx_h.MDBX_NOSUBDIR(), 0644));
			long[] txn = new long[1];
			assertEquals(mdbx_h.MDBX_SUCCESS(), JNI.mdbx_txn_begin(env[0], 0, 0, txn));
			try {
				long[] dbi = new long[1];
				assertEquals(mdbx_h.MDBX_SUCCESS(), JNI.mdbx_dbi_open(txn[0], null, 0, dbi));
				byte[] key = "k".getBytes(StandardCharsets.UTF_8);
				byte[] data = "v".getBytes(StandardCharsets.UTF_8);
				keyPtr = FfmCalls.malloc(key.length);
				dataPtr = FfmCalls.malloc(data.length);
				FfmCalls.buffer_copy(key, 0, keyPtr, 0, key.length);
				FfmCalls.buffer_copy(data, 0, dataPtr, 0, data.length);
				JNI.MDBX_val keyVal = val(keyPtr, key.length);
				JNI.MDBX_val dataVal = val(dataPtr, data.length);
				assertEquals(mdbx_h.MDBX_SUCCESS(), JNI.mdbx_put(txn[0], dbi[0], keyVal, dataVal, 0));

				JNI.MDBX_cache_entry entry = new JNI.MDBX_cache_entry();
				entry.trunk_txnid = 1;
				JNI.mdbx_cache_init(entry);
				assertEquals(0L, entry.trunk_txnid);
				assertEquals(0, entry.length);

				keyVal = val(keyPtr, key.length);
				dataVal = val(dataPtr, data.length);
				JNI.MDBX_cache_result result = JNI.mdbx_cache_get(txn[0], dbi[0], keyVal, dataVal, entry);
				assertEquals(result.errcode, mdbx_h.MDBX_SUCCESS(), result.errcode);
				assertEquals(data.length, dataVal.iov_len);
			}
			finally {
				JNI.mdbx_txn_abort(txn[0]);
			}
		}
		finally {
			FfmCalls.free(keyPtr);
			FfmCalls.free(dataPtr);
			JNI.mdbx_env_close(env[0]);
			Mdbx.deleteTree(dir);
		}
	}

	private static JNI.MDBX_val val(long address, int length) {
		JNI.MDBX_val value = new JNI.MDBX_val();
		value.iov_base = address;
		value.iov_len = length;
		return value;
	}
}
