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

import static com.castortech.mdbxjni.JNI.*;
import static com.castortech.mdbxjni.Util.checkArgNotNull;
import static com.castortech.mdbxjni.Util.checkErrorCode;

import java.util.HashSet;
import java.util.Set;

/**
 * A cursor handle.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Cursor extends NativeObject implements AutoCloseable {
	private final Env env;
	private final Transaction tx;
	private final Database db;

	Cursor(Env env, long self, Transaction tx, Database db) {
		super(self);
		this.env = env;
		this.tx = tx;
		this.db = db;
	}

	/**
	 * <p>
	 * Close a cursor handle.
	 * </p>
	 *
	 * The cursor handle will be freed and must not be used again after this call. Its transaction must still be
	 * live if it is a write-transaction.
	 */
	@Override
	public void close() {
		if (self != 0) {
			mdbx_cursor_close(self);
			self = 0;
		}
	}

	/**
	 * <p>
	 * Renew a cursor handle.
	 * </p>
	 *
	 * A cursor is associated with a specific transaction and database. Cursors that are only used in read-only
	 * transactions may be re-used, to avoid unnecessary malloc/free overhead. The cursor may be associated with
	 * a new read-only transaction, and referencing the same database handle as it was created with. This may be
	 * done whether the previous transaction is live or dead.
	 *
	 * @param tx
	 *          transaction handle
	 */
	public void renew(Transaction tx) {
		checkErrorCode(mdbx_cursor_renew(tx.pointer(), pointer()));
	}

	/**
	 * <p>
	 * Retrieve by cursor.
	 * </p>
	 * This function retrieves key/data pairs from the database. The address and length of the key are returned
	 * in the object to which \b key refers (except for the case of the #MDB_SET option, in which the \b key
	 * object is unchanged), and the address and length of the data are returned in the object to which \b data
	 *
	 * @param op
	 *          A cursor operation #MDB_cursor_op
	 * @return
	 */
	public Entry get(CursorOp op) {
		checkArgNotNull(op, "op"); //$NON-NLS-1$

		Value key = new Value();
		Value value = new Value();
		int rc = mdbx_cursor_get(pointer(), key, value, op.getValue());
		if (rc == MDBX_NOTFOUND) {
			return null;
		}
		checkErrorCode(rc);
		return new Entry(key.toByteArray(), value.toByteArray());
	}

	public Entry get(CursorOp op, byte[] key) {
		checkArgNotNull(op, "op"); //$NON-NLS-1$
		NativeBuffer keyBuffer = NativeBuffer.create(key);
		try {
			Value keyValue = new Value(keyBuffer);
			Value value = new Value();
			int rc = mdbx_cursor_get(pointer(), keyValue, value, op.getValue());
			if (rc == MDBX_NOTFOUND) {
				return null;
			}
			checkErrorCode(rc);
			return new Entry(keyValue.toByteArray(), value.toByteArray());
		}
		finally {
			if (keyBuffer != null)
				keyBuffer.delete();
		}
	}

	public Entry get(CursorOp op, byte[] key, byte[] value) {
		checkArgNotNull(op, "op"); //$NON-NLS-1$
		NativeBuffer keyBuffer = NativeBuffer.create(key);
		NativeBuffer valBuffer = NativeBuffer.create(value);

		try {
			Value keyValue = keyBuffer != null ? new Value(keyBuffer) : new Value();
			Value valValue = valBuffer != null ? new Value(valBuffer) : new Value();
			int rc = mdbx_cursor_get(pointer(), keyValue, valValue, op.getValue());
			if (rc == MDBX_NOTFOUND) {
				return null;
			}
			checkErrorCode(rc);
			return new Entry(keyValue.toByteArray(), valValue.toByteArray());
		}
		finally {
			if (keyBuffer != null)
				keyBuffer.delete();
			if (valBuffer != null)
				valBuffer.delete();
		}
	}

	public OperationStatus get(CursorOp op, DatabaseEntry key, DatabaseEntry value) {
		checkArgNotNull(op, "op"); //$NON-NLS-1$
		NativeBuffer keyBuffer = NativeBuffer.create(key.getData());
		NativeBuffer valBuffer = NativeBuffer.create(value.getData());

		try {
			Value keyValue = keyBuffer != null ? new Value(keyBuffer) : new Value();
			Value valValue = valBuffer != null ? new Value(valBuffer) : new Value();
			int rc = mdbx_cursor_get(pointer(), keyValue, valValue, op.getValue());
			if (rc == MDBX_NOTFOUND) {
				return OperationStatus.NOTFOUND;
			}
			checkErrorCode(rc);
			key.setData(keyValue.toByteArray());
			value.setData(valValue.toByteArray());
			return OperationStatus.SUCCESS;
		}
		finally {
			if (keyBuffer != null)
				keyBuffer.delete();
			if (valBuffer != null)
				valBuffer.delete();
		}
	}

	/**
	 * <p>
	 * Store by cursor.
	 * </p>
	 *
	 * @param key
	 *          The key operated on.
	 * @param value
	 *          The data operated on.
	 * @param flags
	 *          Options for this operation. This parameter must be set to 0 or one of the values described here.
	 *          <ul>
	 *          <li>{@link org.fusesource.lmdbjni.Constants#CURRENT} - replace the item at the current cursor
	 *          position. The \b key parameter must still be provided, and must match it. If using sorted
	 *          duplicates ({@link org.fusesource.lmdbjni.Constants#DUPSORT}) the data item must still sort into
	 *          the same place. This is intended to be used when the new data is the same size as the old.
	 *          Otherwise it will simply perform a delete of the old record followed by an insert.
	 *          <li>{@link org.fusesource.lmdbjni.Constants#NODUPDATA} - enter the new key/data pair only if it
	 *          does not already appear in the database. This flag may only be specified if the database was
	 *          opened with {@link org.fusesource.lmdbjni.Constants#DUPSORT}. The function will return
	 *          {@link org.fusesource.MDBXException.LMDBException#KEYEXIST} if the key/data pair already appears in
	 *          the database.
	 *          <li>{@link org.fusesource.lmdbjni.Constants#NOOVERWRITE} - enter the new key/data pair only if
	 *          the key does not already appear in the database. The function will return
	 *          {@link org.fusesource.MDBXException.LMDBException#KEYEXIST} if the key already appears in the
	 *          database, even if the database supports duplicates
	 *          ({@link org.fusesource.MDBXException.LMDBException#KEYEXIST}).
	 *          <li>{@link org.fusesource.lmdbjni.Constants#RESERVE} - reserve space for data of the given size,
	 *          but don't copy the given data. Instead, return a pointer to the reserved space, which the caller
	 *          can fill in later. This saves an extra memcpy if the data is being generated later.
	 *          <li>{@link org.fusesource.lmdbjni.Constants#APPEND} - append the given key/data pair to the end
	 *          of the database. No key comparisons are performed. This option allows fast bulk loading when
	 *          keys are already known to be in the correct order. Loading unsorted keys with this flag will
	 *          cause data corruption.
	 *          <li>{@link org.fusesource.lmdbjni.Constants#APPENDDUP} - as above, but for sorted dup data.
	 *          <li>{@link org.fusesource.lmdbjni.Constants#MULTIPLE} - store multiple contiguous data elements
	 *          in a single request. This flag may only be specified if the database was opened with
	 *          {@link org.fusesource.lmdbjni.Constants#DUPFIXED}. The \b data argument must be an array of two
	 *          MDB_vals. The mv_size of the first MDB_val must be the size of a single data element. The
	 *          mv_data of the first MDB_val must point to the beginning of the array of contiguous data
	 *          elements. The mv_size of the second MDB_val must be the count of the number of data elements to
	 *          store. On return this field will be set to the count of the number of elements actually written.
	 *          The mv_data of the second MDB_val is unused.
	 *          </ul>
	 * @return the value that was stored
	 */
	public byte[] put(byte[] key, byte[] value, int flags) {
		checkArgNotNull(key, "key"); //$NON-NLS-1$
		checkArgNotNull(value, "value"); //$NON-NLS-1$
		NativeBuffer keyBuffer = NativeBuffer.create(key);

		try {
			NativeBuffer valBuffer = NativeBuffer.create(value);
			try {
				return put(keyBuffer, valBuffer, flags);
			}
			finally {
				if (valBuffer != null)
					valBuffer.delete();
			}
		}
		finally {
			if (keyBuffer != null)
				keyBuffer.delete();
		}
	}

	private byte[] put(NativeBuffer keyBuffer, NativeBuffer valueBuffer, int flags) {
		return put(new Value(keyBuffer), new Value(valueBuffer), flags);
	}

	private byte[] put(Value keySlice, Value valueSlice, int flags) {
		boolean hasSec = db.getSecondaries() != null;
		Set<Value> valueSlices = new HashSet<>();

		if (hasSec && (flags & MDBX_NOOVERWRITE) == 0 && (flags & MDBX_NODUPDATA) == 0) {
			try (Cursor cursor = db.openCursor(tx)) {
				byte[] key = keySlice.toByteArray();
				Entry entry = cursor.get(CursorOp.SET, key);

				if (entry != null) {
					NativeBuffer valueBuffer = NativeBuffer.create(entry.getValue());
					valueSlices.add(Value.create(valueBuffer));
				}
			}
		}

		int rc = mdbx_cursor_put(pointer(), keySlice, valueSlice, flags);

		if (((flags & MDBX_NOOVERWRITE) != 0 || (flags & MDBX_NODUPDATA) != 0) && rc == MDBX_KEYEXIST) {
			// Return the existing value if it was a dup insert attempt.
			return valueSlice.toByteArray();
		}
		else {
			// If the put failed, throw an exception..
			checkErrorCode(rc);

			if (!valueSlices.isEmpty()) {
				db.deleteSecondaries(tx, keySlice, valueSlices);
			}

			db.putSecondaries(tx, keySlice, valueSlice);
			return valueSlice.toByteArray();
		}
	}

	/**
	 * <p>
	 * Delete current key/data pair.
	 * </p>
	 *
	 * This function deletes the key/data pair to which the cursor refers.
	 */
	public void delete() {
		boolean hasSec = db.getSecondaries() != null;
		Entry entry = null;

		if (hasSec) {
			entry = get(CursorOp.GET_CURRENT);
		}

		int rc = mdbx_cursor_del(pointer(), 0);
		checkErrorCode(rc);

		if (hasSec) {
			for (SecondaryDatabase secDb : db.getSecondaries()) {
				SecondaryDbConfig secConfig = (SecondaryDbConfig)secDb.getConfig();
				byte[] pKey = entry.getKey();
				byte[] secKey = secConfig.getKeyCreator().createSecondaryKey(secDb, pKey, entry.getValue());
				secDb.delete(tx, secKey, pKey);
			}
		}
	}

	/**
	 * <p>
	 * Delete current key/data pair.
	 * </p>
	 *
	 * This function deletes all of the data items for the current key.
	 *
	 * May only be called if the database was opened with {@link org.fusesource.lmdbjni.Constants#DUPSORT}.
	 */
	public void deleteIncludingDups() {
		checkErrorCode(mdbx_cursor_del(pointer(), MDBX_NODUPDATA));
	}

	/**
	 * <p>
	 * Return count of duplicates for current key.
	 * </p>
	 *
	 * This call is only valid on databases that support sorted duplicate data items
	 * {@link org.fusesource.lmdbjni.Constants#DUPSORT}.
	 *
	 * @return count of duplicates for current key
	 */
	public long count() {
		long rc[] = new long[1];
		checkErrorCode(mdbx_cursor_count(pointer(), rc));
		return rc[0];
	}

	public Database getDatabase() {
		// long dbi = mdb_cursor_dbi(pointer());
		return db;
	}

	public Transaction getTransaction() {
		// long txn = mdb_cursor_txn(pointer());
		return tx;
	}
}