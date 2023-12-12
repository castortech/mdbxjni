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

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.castortech.mdbxjni.pool.CursorKey;
import static com.castortech.mdbxjni.JNI.*;
import static com.castortech.mdbxjni.Util.checkArgNotNull;
import static com.castortech.mdbxjni.Util.checkErrorCode;
import static com.castortech.mdbxjni.Util.checkSize;

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Database extends NativeObject implements Closeable {
	private static final Logger log = LoggerFactory.getLogger(Database.class);
	private final Env env;
	private final String name;
	private List<SecondaryDatabase> secondaries = null;

	/*package*/Database(Env env, long self, String name) {
		super(self);
		this.env = env;
		this.name = name;
	}

	/**
	 * <p>
	 * Close a database handle. Normally unnecessary.
	 * </p>
	 *
	 * Use with care:
	 *
	 * This call is not mutex protected. Handles should only be closed by a single
	 * thread, and only if no other threads are going to reference the database
	 * handle or one of its cursors any further. Do not close a handle if an
	 * existing transaction has modified its database. Doing so can cause
	 * misbehavior from database corruption to errors like
	 * {@link com.castortech.mdbxjni.MDBXException.Status#BAD_VALSIZE} (since the DB name
	 * is gone).
	 */
	@Override
	public void close() {
		if (self != 0) {
			if (log.isTraceEnabled())
				log.trace("Calling db close for {}", this); //$NON-NLS-1$
			mdbx_dbi_close(env.pointer(), self);
			self = 0;
		}
	}

	/**
	 * @return Statistics for a database.
	 */
	public Stat stat() {
		Transaction tx = env.createTransaction();
		try {
			return new Stat(stat(tx));
		}
		finally {
			tx.commit();
		}
	}

	public Stat stat(Transaction tx) {
		checkArgNotNull(tx, "tx"); //$NON-NLS-1$
		MDBX_stat rc = new MDBX_stat();
		if (log.isTraceEnabled())
			log.trace("Calling db stats for {}", this); //$NON-NLS-1$
		mdbx_dbi_stat(tx.pointer(), pointer(), rc, JNI.SIZEOF_STAT);
		return new Stat(rc);
	}

	/**
	 * @see com.castortech.mdbxjni.Database#drop(Transaction, boolean)
	 */
	public void drop(boolean delete) {
		Transaction tx = env.createTransaction();
		try {
			drop(tx, delete);
		}
		finally {
			tx.commit();
		}
	}

	/**
	 * <p>
	 * Empty or delete+close a database.
	 * </p>
	 *
	 * @param tx
	 *            transaction handle
	 * @param delete
	 *            false to empty the DB, true to delete it from the environment and
	 *            close the DB handle.
	 */
	public void drop(Transaction tx, boolean delete) {
		checkArgNotNull(tx, "tx"); //$NON-NLS-1$
		if (log.isTraceEnabled())
			log.trace("Calling db drop for {}", this); //$NON-NLS-1$
		mdbx_drop(tx.pointer(), pointer(), delete ? 1 : 0);
		if (delete) {
			self = 0;
		}
	}

	public String getName() {
		return name;
	}

	public List<SecondaryDatabase> getSecondaries() {
		return secondaries;
	}

	/**
	 * @see com.castortech.mdbxjni.Database#get(Transaction, byte[])
	 */
	public byte[] get(byte[] key) {
		checkArgNotNull(key, "key"); //$NON-NLS-1$
		Transaction tx = env.createTransaction();
		try {
			return get(tx, key);
		}
		finally {
			tx.commit();
		}
	}

	public EntryCount getEx(byte[] key) {
		checkArgNotNull(key, "key"); //$NON-NLS-1$
		Transaction tx = env.createTransaction();
		try {
			return getEx(tx, key);
		}
		finally {
			tx.commit();
		}
	}

	public Entry getEqOrGE(byte[] key) {
		checkArgNotNull(key, "key"); //$NON-NLS-1$
		Transaction tx = env.createTransaction();
		try {
			return getEqOrGE(tx, key);
		}
		finally {
			tx.commit();
		}
	}

	/**
	 * <p>
	 * Get items from a database.
	 * </p>
	 *
	 * This function retrieves key/data pairs from the database. The address and
	 * length of the data associated with the specified \b key are returned in the
	 * structure to which \b data refers. If the database supports duplicate keys
	 * ({@link com.castortech.mdbxjni.Constants#DUPSORT}) then the first data item
	 * for the key will be returned. Retrieval of other items requires the use of
	 * #mdb_cursor_get().
	 *
	 * @param tx
	 *            transaction handle
	 * @param key
	 *            The key to search for in the database
	 * @return The data corresponding to the key or null if not found
	 */
	@SuppressWarnings("nls")
	public byte[] get(Transaction tx, byte[] key) {
		checkArgNotNull(key, "key");
		if (tx == null) {
			return get(key);
		}

		checkArgNotNull(tx, "tx");
		NativeBuffer keyBuffer = NativeBuffer.create(key);
		try {
			return get(tx, keyBuffer);
		}
		finally {
			keyBuffer.delete();
		}
	}

	@SuppressWarnings("nls")
	public EntryCount getEx(Transaction tx, byte[] key) {
		checkArgNotNull(key, "key");
		if (tx == null) {
			return getEx(key);
		}

		checkArgNotNull(tx, "tx");
		NativeBuffer keyBuffer = NativeBuffer.create(key);
		try {
			return getEx(tx, keyBuffer);
		}
		finally {
			keyBuffer.delete();
		}
	}

	/**
	 * <p>
	 * Get items from a database.
	 * </p>
	 * Get equal or greater item from a database. This is equivalent to using a cursor with MDBX_SET_LOWERBOUND.
	 * <br />
	 * Briefly this function does the same as mdbx_get() with a few differences: <br />
	 *
	 * <ol>
	 * <li>Return equal or great (due comparison function) key-value pair, but not only exactly matching with the
	 * key.</li>
	 * <li>On success return MDBX_SUCCESS if key found exactly, and MDBX_RESULT_TRUE otherwise. Moreover, for
	 * databases with MDBX_DUPSORT flag the data argument also will be used to match over multi-value/duplicates,
	 * and MDBX_SUCCESS will be returned only when BOTH the key and the data match exactly.</li>
	 * <li>Updates BOTH the key and the data for pointing to the actual key-value pair inside the database.
	 * <p>
	 * </li>
	 * </ol>
	 *
	 * @param tx
	 *            transaction handle
	 * @param key
	 *            The key to search for in the database
	 * @return Entry representing the key value pair
	 */
	@SuppressWarnings("nls")
	public Entry getEqOrGE(Transaction tx, byte[] key) {
		checkArgNotNull(key, "key");
		if (tx == null) {
			return getEqOrGE(key);
		}

		checkArgNotNull(tx, "tx");
		NativeBuffer keyBuffer = NativeBuffer.create(key);
		try {
			return getEqOrGE(tx, keyBuffer);
		}
		finally {
			keyBuffer.delete();
		}
	}

	private byte[] get(Transaction tx, NativeBuffer keyBuffer) {
		return get(tx, new Value(keyBuffer));
	}

	private EntryCount getEx(Transaction tx, NativeBuffer keyBuffer) {
		return getEx(tx, new Value(keyBuffer));
	}

	private Entry getEqOrGE(Transaction tx, NativeBuffer keyBuffer) {
		return getEqOrGE(tx, new Value(keyBuffer));
	}

	/* package */byte[] get(Transaction tx, Value key) {
		Value value = new Value();
		if (log.isTraceEnabled())
			log.trace("Calling db get for {}", this); //$NON-NLS-1$
		int rc = mdbx_get(tx.pointer(), pointer(), key, value);
		if (rc == MDBX_NOTFOUND) {
			return null;
		}
		checkErrorCode(env, tx, rc);
		return value.toByteArray();
	}

	/* package */EntryCount getEx(Transaction tx, Value key) {
		Value value = new Value();
		long[] valCnt = new long[1];

		if (log.isTraceEnabled())
			log.trace("Calling db getex for {}", this); //$NON-NLS-1$
		int rc = mdbx_get_ex(tx.pointer(), pointer(), key, value, valCnt);
		if (rc == MDBX_NOTFOUND) {
			return null;
		}
		checkErrorCode(env, tx, rc);
		return new EntryCount(key.toByteArray(), value.toByteArray(), valCnt[0]);
	}

	/* package */Entry getEqOrGE(Transaction tx, Value key) {
		Value value = new Value();
		if (log.isTraceEnabled())
			log.trace("Calling db get eq or great for {}", this); //$NON-NLS-1$
		int rc = mdbx_get_equal_or_great(tx.pointer(), pointer(), key, value);
		if (rc == MDBX_NOTFOUND) {
			return null;
		}
		checkErrorCode(env, tx, rc);
		return new Entry(key.toByteArray(), value.toByteArray());
	}

	/**
	 * @see com.castortech.mdbxjni.Database#put(Transaction, byte[], byte[], int)
	 */
	public byte[] put(byte[] key, byte[] value) {
		return put(key, value, 0);
	}

	/**
	 * @see com.castortech.mdbxjni.Database#put(Transaction, byte[], byte[], int)
	 */
	public byte[] put(byte[] key, byte[] value, int flags) {
		checkArgNotNull(key, "key"); //$NON-NLS-1$
		Transaction tx = env.createTransaction();
		try {
			return put(tx, key, value, flags);
		}
		finally {
			tx.commit();
		}
	}

	/**
	 * @see com.castortech.mdbxjni.Database#put(Transaction, byte[], byte[], int)
	 */
	public byte[] put(Transaction tx, byte[] key, byte[] value) {
		return put(tx, key, value, 0);
	}

	/**
	 * <p>
	 * Store items into a database.
	 * </p>
	 *
	 * This function stores key/data pairs in the database. The default behavior is
	 * to enter the new key/data pair, replacing any previously existing key if
	 * duplicates are disallowed, or adding a duplicate data item if duplicates are
	 * allowed ({@link com.castortech.mdbxjni.Constants#DUPSORT}).
	 *
	 * @param tx
	 *            transaction handle
	 * @param key
	 *            The key to store in the database
	 * @param value
	 *            The value to store in the database
	 * @param flags
	 *            Special options for this operation. This parameter must be set to
	 *            0 or by bitwise OR'ing together one or more of the values
	 *            described here.
	 *            <ul>
	 *            <li>{@link com.castortech.mdbxjni.Constants#NODUPDATA} - enter the
	 *            new key/data pair only if it does not already appear in the
	 *            database. This flag may only be specified if the database was
	 *            opened with {@link com.castortech.mdbxjni.Constants#DUPSORT}. The
	 *            function will return #MDB_KEYEXIST if the key/data pair already
	 *            appears in the database.
	 *            <li{@link com.castortech.mdbxjni.Constants#NOOVERWRITE} - enter
	 *            the new key/data pair only if the key does not already appear in
	 *            the database. The function will return
	 *            {@link com.castortech.mdbxjni.MDBXException.Status#KEYEXIST} if the key
	 *            already appears in the database, even if the database supports
	 *            duplicates ({@link com.castortech.mdbxjni.Constants#DUPSORT}). The
	 *            \b data parameter will be set to point to the existing item.
	 *            <li>{@link com.castortech.mdbxjni.Constants#RESERVE} - reserve
	 *            space for data of the given size, but don't copy the given data.
	 *            Instead, return a pointer to the reserved space, which the caller
	 *            can fill in later - before the next update operation or the
	 *            transaction ends. This saves an extra memcpy if the data is being
	 *            generated later. MDBX does nothing else with this memory, the
	 *            caller is expected to modify all of the space requested.
	 *            <li>{@link com.castortech.mdbxjni.Constants#APPEND} - append the
	 *            given key/data pair to the end of the database. No key comparisons
	 *            are performed. This option allows fast bulk loading when keys are
	 *            already known to be in the correct order. Loading unsorted keys
	 *            with this flag will cause data corruption.
	 *            <li>{@link com.castortech.mdbxjni.Constants#APPENDDUP} - as above,
	 *            but for sorted dup data.
	 *            </ul>
	 *
	 * @return the existing value if it was a dup insert attempt.
	 */
	@SuppressWarnings("nls")
	public byte[] put(Transaction tx, byte[] key, byte[] value, int flags) {
		checkArgNotNull(key, "key");
		checkArgNotNull(value, "value");

		if (tx == null) {
			return put(key, value, flags);
		}

		checkArgNotNull(tx, "tx");
		NativeBuffer keyBuffer = NativeBuffer.create(key);
		try {
			NativeBuffer valueBuffer = NativeBuffer.create(value);
			try {
				return put(tx, keyBuffer, valueBuffer, flags);
			}
			finally {
				valueBuffer.delete();
			}
		}
		finally {
			keyBuffer.delete();
		}
	}

	@SuppressWarnings("nls")
	public byte[] put(Transaction tx, byte[] key, byte[] value, int flags, long valueCnt) {
		if (valueCnt > 0) {  //indicates a put multiple for dupsort/dupfixed
			int flags2 = getFlags(tx);
			if ((flags2 & MDBX_DUPSORT) == 0 || (flags2 & MDBX_DUPFIXED) == 0) {
				String msg = "Put multiple only applies to DUPSORT with DUPFIXED";
				throw new MDBXException(msg, JNI.MDBX_PROBLEM);
			}

			if (tx == null) {
				String msg = "Put multiple only supported with non null tx argument";
				throw new MDBXException(msg, JNI.MDBX_BAD_TXN);
			}

			if (value.length % valueCnt != 0) {
				String msg = "Value length must be an exact multiple of valueSize";
				throw new MDBXException(msg, JNI.MDBX_PROBLEM);
			}

			checkArgNotNull(tx, "tx");
			long valueSize = value.length / valueCnt;
			NativeBuffer keyBuffer = NativeBuffer.create(key);
			try {
				NativeBuffer valueBuffer = NativeBuffer.create(value);
//				NativeBuffer valArray = NativeBuffer.create(32);
//				valArray.write(0, ByteUtil.byteArrayForLong(valueBuffer.pointer()), 0, 8);
//				valArray.write(8, ByteUtil.byteArrayForLong(valueSize), 0, 8);
//				valArray.write(16, ByteUtil.byteArrayForLong(0L), 0, 8);
//				valArray.write(24, ByteUtil.byteArrayForLong(valueCnt), 0, 8);

				Value value1 = new Value(valueBuffer.pointer(), valueSize);
				Value value2 = new Value(0L, valueCnt);

				try {
					long now = System.currentTimeMillis();
					byte[] rc = put(tx, new Value(keyBuffer), value1, value2, flags);
//					if (log.isDebugEnabled())
//						log.debug("Db put:{}, ms:{}", key, System.currentTimeMillis() - now);
					return rc;
				}
				finally {
					valueBuffer.delete();
				}
			}
			finally {
				keyBuffer.delete();
			}
		}
		return null;
	}

	private byte[] put(Transaction tx, NativeBuffer keyBuffer, NativeBuffer valueBuffer, int flags) {
		return put(tx, new Value(keyBuffer), new Value(valueBuffer), flags);
	}

	private byte[] put(Transaction tx, Value keySlice, Value valueSlice, int flags) {
		checkSize(env, keySlice);
		if ((flags & MDBX_DUPSORT) != 0) {
			checkSize(env, valueSlice);
		}

		boolean hasSec = getSecondaries() != null;
		Set<Value> valueSlices = new HashSet<>();

		//do we already have an entry under this key, if so secondary will need to be replaced (delete + put)
		if (hasSec && (flags & MDBX_NOOVERWRITE) == 0 && (flags & MDBX_NODUPDATA) == 0) {
			try (Cursor cursor = openCursor(tx)) {
				byte[] key = keySlice.toByteArray();
				Entry entry = cursor.get(CursorOp.SET, key);

				if (entry != null) {
					NativeBuffer valueBuffer = NativeBuffer.create(entry.getValue());
					valueSlices.add(Value.create(valueBuffer));
				}
			}
		}

		if (log.isTraceEnabled())
			log.trace("Calling db put for {}", this); //$NON-NLS-1$
		int rc = mdbx_put(tx.pointer(), pointer(), keySlice, valueSlice, flags);
		if (((flags & MDBX_NOOVERWRITE) != 0 || (flags & MDBX_NODUPDATA) != 0) && rc == MDBX_KEYEXIST) {
			// Return the existing value if it was a dup insert attempt.
			return valueSlice.toByteArray();
		}
		else {
			// If the put failed, throw an exception..
			if (rc != 0) {
				throw new MDBXException("put failed", rc); //$NON-NLS-1$
			}

			if (!valueSlices.isEmpty()) {
				deleteSecondaries(tx, keySlice, valueSlices);
			}

			putSecondaries(tx, keySlice, valueSlice);

			return null;
		}
	}

	private byte[] put(Transaction tx, Value keySlice, Value value1Slice, Value value2Slice, int flags) {
		checkSize(env, keySlice);
		if ((flags & MDBX_DUPSORT) != 0) {
			checkSize(env, value1Slice);
		}

		if (log.isTraceEnabled())
			log.trace("Calling db put for {}", this); //$NON-NLS-1$
		int rc = mdbx_put_multiple(tx.pointer(), pointer(), keySlice, value1Slice, value2Slice, flags);
		if (((flags & MDBX_NOOVERWRITE) != 0 || (flags & MDBX_NODUPDATA) != 0) && rc == MDBX_KEYEXIST) {
			// Return the existing value if it was a dup insert attempt.
			return value1Slice.toByteArray();
		}
		else {
			// If the put failed, throw an exception..
			if (rc != 0) {
				throw new MDBXException("put failed", rc); //$NON-NLS-1$
			}
			return null;
		}
	}

	protected void putSecondaries(Transaction tx, Value keySlice, Value valueSlice) {
		if (secondaries != null) {
			for (SecondaryDatabase secDb : secondaries) {
				SecondaryDbConfig secConfig = (SecondaryDbConfig)secDb.getConfig();
				byte[] pKey = keySlice.toByteArray();
				byte[] secKey = secConfig.getKeyCreator().createSecondaryKey(secDb, pKey, valueSlice.toByteArray());
				secDb.internalPut(tx, secKey, pKey);
			}
		}
	}

	/**
	 * @see com.castortech.mdbxjni.Database#replace(Transaction, byte[], byte[], int)
	 */
	public byte[] replace(byte[] key, byte[] value) {
		return replace(key, value, 0);
	}

	/**
	 * @see com.castortech.mdbxjni.Database#replace(Transaction, byte[], byte[], int)
	 */
	public byte[] replace(byte[] key, byte[] value, int flags) {
		checkArgNotNull(key, "key"); //$NON-NLS-1$
		Transaction tx = env.createTransaction();
		try {
			return replace(tx, key, value, flags);
		}
		finally {
			tx.commit();
		}
	}

	/**
	 * @see com.castortech.mdbxjni.Database#replace(Transaction, byte[], byte[], int)
	 */
	public byte[] replace(Transaction tx, byte[] key, byte[] value) {
		return replace(tx, key, value, 0);
	}

	@SuppressWarnings("nls")
	public byte[] replace(Transaction tx, byte[] key, byte[] value, int flags) {
		checkArgNotNull(key, "key");
		checkArgNotNull(value, "value");

		if (tx == null) {
			return replace(key, value, flags);
		}

		checkArgNotNull(tx, "tx");
		NativeBuffer keyBuffer = NativeBuffer.create(key);
		try {
			NativeBuffer valueBuffer = NativeBuffer.create(value);
			try {
				return replace(tx, keyBuffer, valueBuffer, flags);
			}
			finally {
				valueBuffer.delete();
			}
		}
		finally {
			keyBuffer.delete();
		}
	}

	private byte[] replace(Transaction tx, NativeBuffer keyBuffer, NativeBuffer valueBuffer, int flags) {
		return replace(tx, new Value(keyBuffer), new Value(valueBuffer), flags);
	}

	private byte[] replace(Transaction tx, Value keySlice, Value valueSlice, int flags) {
		checkSize(env, keySlice);
		if ((flags & MDBX_DUPSORT) != 0) {
			checkSize(env, valueSlice);
		}

		boolean hasSec = getSecondaries() != null;
		Set<Value> valueSlices = new HashSet<>();

		//do we already have an entry under this key, if so secondary will need to be replaced (delete + put)
		if (hasSec && (flags & MDBX_NOOVERWRITE) == 0 && (flags & MDBX_NODUPDATA) == 0) {
			try (Cursor cursor = openCursor(tx)) {
				byte[] key = keySlice.toByteArray();
				Entry entry = cursor.get(CursorOp.SET, key);

				if (entry != null) {
					NativeBuffer valueBuffer = NativeBuffer.create(entry.getValue());
					valueSlices.add(Value.create(valueBuffer));
				}
			}
		}

		//allocate buffer with 50% larger than new value which should be enough to avoid retry
		NativeBuffer oldValBuffer = NativeBuffer.create((long)(valueSlice.iov_len * 1.5));
		try {
			Value oldValSlice = new Value(oldValBuffer);
			boolean didResize = false;
			int rc = 0;

			while (true) {
				if (log.isTraceEnabled())
					log.trace("Calling db replace for {}", this); //$NON-NLS-1$
				rc = mdbx_replace(tx.pointer(), pointer(), keySlice, valueSlice, oldValSlice, flags);
				if (rc == MDBX_RESULT_TRUE) {  //we have a dirty old value that couldn't fit in old value buffer
					if (didResize) {  //been here already, bail out.
						throw new MDBXException("put failed, falling into loop", rc); //$NON-NLS-1$
					}
					didResize = true;
					oldValBuffer.delete();  //release existing buffer
					oldValBuffer = NativeBuffer.create(oldValSlice.iov_len);
				}
				else {
					break;
				}
			}

			// If the put failed, throw an exception..
			if (rc != 0) {
				throw new MDBXException("put failed", rc); //$NON-NLS-1$
			}

			if (!valueSlices.isEmpty()) {
				deleteSecondaries(tx, keySlice, valueSlices);
			}

			putSecondaries(tx, keySlice, valueSlice);

			return oldValSlice.toByteArray();
		}
		finally {
			oldValBuffer.delete();
		}
	}


	/**
	 * @see com.castortech.mdbxjni.Database#delete(Transaction, byte[], byte[])
	 */
	public boolean delete(byte[] key) {
		return delete(key, null);
	}

	/**
	 * @see com.castortech.mdbxjni.Database#delete(Transaction, byte[], byte[])
	 */
	public boolean delete(byte[] key, byte[] value) {
		checkArgNotNull(key, "key"); //$NON-NLS-1$
		Transaction tx = env.createTransaction();
		try {
			return delete(tx, key, value);
		}
		finally {
			tx.commit();
		}
	}

	/**
	 * @see com.castortech.mdbxjni.Database#delete(Transaction, byte[], byte[])
	 */
	public boolean delete(Transaction tx, byte[] key) {
		return delete(tx, key, null);
	}

	/**
	 * <p>
	 * Removes key/data pairs from the database.
	 * </p>
	 * If the database does not support sorted duplicate data items
	 * ({@link com.castortech.mdbxjni.Constants#DUPSORT}) the value parameter is
	 * ignored. If the database supports sorted duplicates and the data parameter is
	 * NULL, all of the duplicate data items for the key will be deleted. Otherwise,
	 * if the data parameter is non-NULL only the matching data item will be
	 * deleted. This function will return false if the specified key/data pair is
	 * not in the database.
	 *
	 * @param tx
	 *            Transaction handle.
	 * @param key
	 *            The key to delete from the database.
	 * @param value
	 *            The value to delete from the database
	 * @return true if the key/value was deleted.
	 */
	@SuppressWarnings("nls")
	public boolean delete(Transaction tx, byte[] key, byte[] value) {
		checkArgNotNull(tx, "tx");
		checkArgNotNull(key, "key");

		if (tx == null) {
			return delete(key, value);
		}

		checkArgNotNull(tx, "tx");
		NativeBuffer keyBuffer = NativeBuffer.create(key);
		try {
			NativeBuffer valueBuffer = NativeBuffer.create(value);
			try {
				return delete(tx, keyBuffer, valueBuffer);
			}
			finally {
				if (valueBuffer != null) {
					valueBuffer.delete();
				}
			}
		}
		finally {
			keyBuffer.delete();
		}
	}

	private boolean delete(Transaction tx, NativeBuffer keyBuffer, NativeBuffer valueBuffer) {
		return delete(tx, new Value(keyBuffer), Value.create(valueBuffer));
	}

	private boolean delete(Transaction tx, Value keySlice, Value valueSlice) {
		checkSize(env, keySlice);

		boolean hasSec = getSecondaries() != null;
		Set<Value> valueSlices = new HashSet<>();

		//here we just have a key w/o value, so all values must be deleted.
		//for secondaries where the value is the key, all such values must be retrieved and deleted
		if (valueSlice == null && hasSec) {
			try (Cursor cursor = openCursor(tx)) {
				byte[] key = keySlice.toByteArray();
				Entry entry = cursor.get(CursorOp.SET, key);

				while (entry != null) {
					NativeBuffer valueBuffer = NativeBuffer.create(entry.getValue());
					valueSlices.add(Value.create(valueBuffer));
					if (getConfig(tx).isDupSort()) {
						entry = cursor.get(CursorOp.NEXT_DUP, key, entry.getValue());
					}
					else {
						entry = null;
					}
				}
			}
		}
		else {
			valueSlices.add(valueSlice);
		}

		if (log.isTraceEnabled())
			log.trace("Calling db del for {}", this); //$NON-NLS-1$
		int rc = mdbx_del(tx.pointer(), pointer(), keySlice, valueSlice);
		if (rc == MDBX_NOTFOUND) {
			return false;
		}
		checkErrorCode(env, tx, rc);
		deleteSecondaries(tx, keySlice, valueSlices);

		return true;
	}

	protected void deleteSecondaries(Transaction tx, Value keySlice, Set<Value> valueSlices) {
		if (secondaries != null) {
			for (SecondaryDatabase secDb : secondaries) {
				SecondaryDbConfig secConfig = (SecondaryDbConfig) secDb.getConfig();
				byte[] pKey = keySlice.toByteArray();
				for (Value valueSlice : valueSlices) {
					byte[] secKey = secConfig.getKeyCreator().createSecondaryKey(secDb, pKey, valueSlice.toByteArray());
					secDb.delete(tx, secKey, pKey);
				}
			}
		}
	}

	/**
	 * <p>
	 * Create a cursor handle.
	 * </p>
	 *
	 * <p>
	 * For more info see {@link JNI#mdbx_cursor_open(long, long, long[])}
	 * </p>
	 *
	 * @param tx
	 *          transaction handle
	 * @return cursor handle
	 */
	public Cursor openCursor(Transaction tx) {
		try {
			checkArgNotNull(tx, "tx"); //$NON-NLS-1$

			if (env.usePooledCursors()) {
				return env.getCursorPool().borrow(new CursorKey(env, this, tx));
			}

			long[] cursor = new long[1];
			if (log.isTraceEnabled())
				log.trace("Calling cursor open for {}", this); //$NON-NLS-1$
			checkErrorCode(env, tx, mdbx_cursor_open(tx.pointer(), pointer(), cursor));
			return new Cursor(env, cursor[0], tx, this);
		}
		catch (Exception e) {
			String msg = "Failed opening cursor for " + this; //$NON-NLS-1$
			throw new MDBXException(msg, e);
		}
	}

	public SecondaryCursor openSecondaryCursor(Transaction tx) {
		try {
			checkArgNotNull(tx, "tx"); //$NON-NLS-1$

			if (env.usePooledCursors()) {
				return env.getCursorPool().borrowSecondary(new CursorKey(env, this, tx));
			}

			long[] cursor = new long[1];
			if (log.isTraceEnabled())
				log.trace("Calling sec cursor open for {}", this); //$NON-NLS-1$
			checkErrorCode(env, tx, mdbx_cursor_open(tx.pointer(), pointer(), cursor));
			return new SecondaryCursor(env, cursor[0], tx, this);
		}
		catch (Exception e) {
			String msg = "Failed opening secondary cursor for " + this; //$NON-NLS-1$
			throw new MDBXException(msg, e);
		}
	}

	public int getFlags() {
		Transaction tx = env.createTransaction();
		try {
			return getFlags(tx);
		}
		finally {
			tx.commit();
		}
	}

	public int getFlags(Transaction tx) {
		long[] flags = new long[1];
		if (log.isTraceEnabled())
			log.trace("Calling db flags for {}", this); //$NON-NLS-1$
		checkErrorCode(env, tx, mdbx_dbi_flags(tx.pointer(), pointer(), flags));
		return (int)flags[0];
	}

	public FlagState getFlagsState(Transaction tx) {
		long[] flags = new long[1];
		long[] state = new long[1];
		if (log.isTraceEnabled())
			log.trace("Calling db flags ex for {}", this); //$NON-NLS-1$
		checkErrorCode(env, tx, mdbx_dbi_flags_ex(tx.pointer(), pointer(), flags, state));
		return new FlagState((int)flags[0], (int)state[0]);
	}

	public int getDupsortDepthMask(Transaction tx) {
		long[] mask = new long[1];
		if (log.isTraceEnabled())
			log.trace("Calling db dupsort for {}", this); //$NON-NLS-1$
		checkErrorCode(env, tx, mdbx_dbi_dupsort_depthmask(tx.pointer(), pointer(), mask));
		return (int)mask[0];
	}

	public int getSequence(Transaction tx, long increment) {
		long[] res = new long[1];
		if (log.isTraceEnabled())
			log.trace("Calling db sequence for {}", this); //$NON-NLS-1$
		checkErrorCode(env, tx, mdbx_dbi_sequence(tx.pointer(), pointer(), res, increment));
		return (int)res[0];
	}

	public int rename(Transaction tx, String name) {
		long[] res = new long[1];
		checkErrorCode(env, tx, mdbx_dbi_rename(tx.pointer(), pointer(), name));
		return (int)res[0];
	}

	@SuppressWarnings("nls")
	public int rename2(Transaction tx, byte[] name) {
		checkArgNotNull(tx, "tx");
		checkArgNotNull(name, "name");

		NativeBuffer nameBuffer = NativeBuffer.create(name);
		long[] res = new long[1];
		checkErrorCode(env, tx, mdbx_dbi_rename2(tx.pointer(), pointer(), new Value(nameBuffer)));
		return (int)res[0];
	}

	public DatabaseConfig getConfig() {
		Transaction tx = env.createTransaction();
		try {
			return getConfig(tx);
		}
		finally {
			tx.commit();
		}
	}

	public DatabaseConfig getConfig(Transaction tx) {
		int flags = getFlags(tx);
		return new DatabaseConfig(flags);
	}

	/* package */void associate(Transaction tx, SecondaryDatabase secondary) {
		if (secondaries == null) {
			secondaries = new ArrayList<>();
		}

		secondaries.add(secondary);
	}
	// int associateFlags = 0;
	// associateFlags |= config.getAllowPopulate() ? Constants.CREATE : 0;
	// if (config.getImmutableSecondaryKey())
	// associateFlags |= Constants.IMMUTABLE_KEY;
	//
	// config.getKeyCreator()
	//
	// }

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "Database [id=" + pointer() + ", name=" + name + "]";
	}
}