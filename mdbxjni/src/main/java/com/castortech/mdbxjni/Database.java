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

import static com.castortech.mdbxjni.JNI.*;
import static com.castortech.mdbxjni.Util.checkArgNotNull;
import static com.castortech.mdbxjni.Util.checkErrorCode;
import static com.castortech.mdbxjni.Util.checkSize;

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Database extends NativeObject implements Closeable {
	private final Env env;
	private final String name;
	private List<SecondaryDatabase> secondaries = null;

	Database(Env env, long self, String name) {
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
	 * {@link org.fusesource.MDBXException.LMDBException#BAD_VALSIZE} (since the DB name
	 * is gone).
	 */
	@Override
	public void close() {
		if (self != 0) {
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
		mdbx_dbi_stat(tx.pointer(), pointer(), rc, JNI.SIZEOF_STAT);
		return new Stat(rc);
	}

	/**
	 * @see org.fusesource.lmdbjni.Database#drop(Transaction, boolean)
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
	 * @see org.fusesource.lmdbjni.Database#get(Transaction, byte[])
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

	/**
	 * <p>
	 * Get items from a database.
	 * </p>
	 *
	 * This function retrieves key/data pairs from the database. The address and
	 * length of the data associated with the specified \b key are returned in the
	 * structure to which \b data refers. If the database supports duplicate keys
	 * ({@link org.fusesource.lmdbjni.Constants#DUPSORT}) then the first data item
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

	private byte[] get(Transaction tx, NativeBuffer keyBuffer) {
		return get(tx, new Value(keyBuffer));
	}

	/* package */byte[] get(Transaction tx, Value key) {
		Value value = new Value();
		int rc = mdbx_get(tx.pointer(), pointer(), key, value);
		if (rc == MDBX_NOTFOUND) {
			return null;
		}
		checkErrorCode(env, rc);
		return value.toByteArray();
	}

	/**
	 * @see org.fusesource.lmdbjni.Database#put(Transaction, byte[], byte[], int)
	 */
	/**
	 * @see org.fusesource.lmdbjni.Database#put(Transaction, byte[], byte[], int)
	 */
	public byte[] put(byte[] key, byte[] value) {
		return put(key, value, 0);
	}

	/**
	 * @see org.fusesource.lmdbjni.Database#put(Transaction, byte[], byte[], int)
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
	 * @see org.fusesource.lmdbjni.Database#put(Transaction, byte[], byte[], int)
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
	 * allowed ({@link org.fusesource.lmdbjni.Constants#DUPSORT}).
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
	 *            <li>{@link org.fusesource.lmdbjni.Constants#NODUPDATA} - enter the
	 *            new key/data pair only if it does not already appear in the
	 *            database. This flag may only be specified if the database was
	 *            opened with {@link org.fusesource.lmdbjni.Constants#DUPSORT}. The
	 *            function will return #MDB_KEYEXIST if the key/data pair already
	 *            appears in the database.
	 *            <li{@link org.fusesource.lmdbjni.Constants#NOOVERWRITE} - enter
	 *            the new key/data pair only if the key does not already appear in
	 *            the database. The function will return
	 *            {@link org.fusesource.MDBXException.LMDBException#KEYEXIST} if the key
	 *            already appears in the database, even if the database supports
	 *            duplicates ({@link org.fusesource.lmdbjni.Constants#DUPSORT}). The
	 *            \b data parameter will be set to point to the existing item.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#RESERVE} - reserve
	 *            space for data of the given size, but don't copy the given data.
	 *            Instead, return a pointer to the reserved space, which the caller
	 *            can fill in later - before the next update operation or the
	 *            transaction ends. This saves an extra memcpy if the data is being
	 *            generated later. LMDB does nothing else with this memory, the
	 *            caller is expected to modify all of the space requested.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#APPEND} - append the
	 *            given key/data pair to the end of the database. No key comparisons
	 *            are performed. This option allows fast bulk loading when keys are
	 *            already known to be in the correct order. Loading unsorted keys
	 *            with this flag will cause data corruption.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#APPENDDUP} - as above,
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

	protected void putSecondaries(Transaction tx, Value keySlice, Value valueSlice) {
		if (secondaries != null) {
			for (SecondaryDatabase secDb : secondaries) {
				SecondaryDbConfig secConfig = (SecondaryDbConfig) secDb.getConfig();
				byte[] pKey = keySlice.toByteArray();
				byte[] secKey = secConfig.getKeyCreator().createSecondaryKey(secDb, pKey, valueSlice.toByteArray());
				secDb.internalPut(tx, secKey, pKey);
			}
		}
	}

	/**
	 * @see org.fusesource.lmdbjni.Database#delete(Transaction, byte[], byte[])
	 */
	public boolean delete(byte[] key) {
		return delete(key, null);
	}

	/**
	 * @see org.fusesource.lmdbjni.Database#delete(Transaction, byte[], byte[])
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
	 * @see org.fusesource.lmdbjni.Database#delete(Transaction, byte[], byte[])
	 */
	public boolean delete(Transaction tx, byte[] key) {
		return delete(tx, key, null);
	}

	/**
	 * <p>
	 * Removes key/data pairs from the database.
	 * </p>
	 * If the database does not support sorted duplicate data items
	 * ({@link org.fusesource.lmdbjni.Constants#DUPSORT}) the value parameter is
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

		if (valueSlice == null && hasSec) {
			try (Cursor cursor = openCursor(tx)) {
				byte[] key = keySlice.toByteArray();
				Entry entry = cursor.get(CursorOp.SET, key);

				while (entry != null) {
					NativeBuffer valueBuffer = NativeBuffer.create(entry.getValue());
					// System.out.println(string(entry.getValue()));
					valueSlices.add(Value.create(valueBuffer));
					if (getConfig(tx).isDupSort()) {
						entry = cursor.get(CursorOp.NEXT_DUP, key, entry.getValue());
					} else {
						entry = null;
					}
				}
			}
		}
		else {
			valueSlices.add(valueSlice);
		}

		int rc = mdbx_del(tx.pointer(), pointer(), keySlice, valueSlice);
		if (rc == MDBX_NOTFOUND) {
			return false;
		}
		checkErrorCode(env, rc);
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
	 * A cursor is associated with a specific transaction and database. A cursor
	 * cannot be used when its database handle is closed. Nor when its transaction
	 * has ended, except with #mdb_cursor_renew(). It can be discarded with
	 * #mdb_cursor_close(). A cursor in a write-transaction can be closed before its
	 * transaction ends, and will otherwise be closed when its transaction ends. A
	 * cursor in a read-only transaction must be closed explicitly, before or after
	 * its transaction ends. It can be reused with #mdb_cursor_renew() before
	 * finally closing it.
	 *
	 * @note Earlier documentation said that cursors in every transaction were
	 *       closed when the transaction committed or aborted.
	 *
	 * @param tx
	 *            transaction handle
	 * @return Address where the new #MDB_cursor handle will be stored
	 * @return cursor handle
	 */
	public Cursor openCursor(Transaction tx) {
		checkArgNotNull(tx, "tx"); //$NON-NLS-1$

		long cursor[] = new long[1];
		checkErrorCode(env, mdbx_cursor_open(tx.pointer(), pointer(), cursor));
		return new Cursor(env, cursor[0], tx, this);
	}

	public SecondaryCursor openSecondaryCursor(Transaction tx) {
		checkArgNotNull(tx, "tx"); //$NON-NLS-1$

		long cursor[] = new long[1];
		checkErrorCode(env, mdbx_cursor_open(tx.pointer(), pointer(), cursor));
		return new SecondaryCursor(env, cursor[0], tx, this);
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
		checkErrorCode(env, mdbx_dbi_flags(tx.pointer(), pointer(), flags));
		return (int) flags[0];
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
}