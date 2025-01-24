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
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.castortech.mdbxjni.JNIIntern.MDBX_cursor;

/**
 * A cursor handle.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Cursor extends NativeObject implements AutoCloseable {
	private static final Logger log = LoggerFactory.getLogger(Cursor.class);
	private final Env env;
	private Transaction tx;
	private Database db;

	Cursor(Env env, long self, Transaction tx, Database db) {
		super(self);
		this.env = env;
		this.tx = tx;
		this.db = db;
		if (log.isTraceEnabled())
			log.trace("Opened cursor {}", this); //$NON-NLS-1$
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
			if (log.isTraceEnabled())
				log.trace("Calling cursor close for {}", this); //$NON-NLS-1$

			if (env.usePooledCursors()) {
				try {
					env.getCursorPool().release(this);
				}
				catch (Exception e) {
					log.error("Failed to release cursor", e); //$NON-NLS-1$
				}
			}
			else {
				mdbxClose();  //well let's really close it
			}
		}
	}

	/**
	 * Close the cursor directly with MDBX. Normally should be using and working with {@link #close()} instead.
	 * This method is for release from the pool.
	 */
	public void mdbxClose() {
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
	 * <p>
	 * The cursor may be associated with a new transaction, and referencing a new or the same database handle as
	 * it was created with. This may be done whether the previous transaction is live or dead.
	 * </p>
	 *
	 * <p>
	 * Using of the mdbx_cursor_renew() is equivalent to calling mdbx_cursor_bind() with the DBI handle that
	 * previously the cursor was used with.
	 * </p>
	 *
	 * <p>
	 * <b>Note</b> In contrast to LMDB, the MDBX allow any cursor to be re-used by using mdbx_cursor_renew(), to
	 * avoid unnecessary malloc/free overhead until it freed by mdbx_cursor_close().
	 * </p>
	 *
	 * @param tx
	 *          transaction handle
	 */
	public void renew(Transaction tx) {
		this.tx = tx;
		if (log.isTraceEnabled())
			log.trace("Calling cursor renew for {}", this); //$NON-NLS-1$
		checkErrorCode(env, tx, mdbx_cursor_renew(tx.pointer(), pointer()));
	}

	/**
	 * <p>
	 * Bind cursor to specified transaction and DBI handle.
	 * </p>
	 *
	 * <p>
	 * Using of this method is equivalent to calling renew but with specifying an arbitrary database.
	 * </p>
	 *
	 * <p>
	 * A cursor may be associated with a new transaction, and referencing a new or the same database as
	 * it was created with. This may be done whether the previous transaction is live or dead.
	 * </p>
	 *
	 * <p>
	 * <b>Note:</b> In contrast to LMDB, MDBX requires that any opened cursors can be reused and must be freed
	 * explicitly, regardless ones was opened in a read-only or write transaction. The REASON for this is
	 * eliminates ambiguity which helps to avoid errors such as: use-after-free, double-free, i.e. memory
	 * corruption and segfaults.
	 * </p>
	 *
	 * @param db
	 *          Database to bind to
	 * @param tx
	 *          Transaction to bind to
	 */
	public void bind(Database db, Transaction tx) {
		this.db = db;
		this.tx = tx;
		if (log.isTraceEnabled())
			log.trace("Calling cursor bind for {}", this); //$NON-NLS-1$
		checkErrorCode(env, tx, mdbx_cursor_bind(tx.pointer(), pointer(), db.pointer()));
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
	 * @return Entry representing the key value pair
	 */
	public Entry get(CursorOp op) {
		checkArgNotNull(op, "op"); //$NON-NLS-1$

		Value key = new Value();
		Value value = new Value();
		if (log.isTraceEnabled())
			log.trace("Calling cursor get op for {}", this); //$NON-NLS-1$
		int rc = mdbx_cursor_get(pointer(), key, value, op.getValue());
		if (rc == MDBX_NOTFOUND) {
			return null;
		}
		checkErrorCode(env, tx, rc);
		return new Entry(key.toByteArray(), value.toByteArray());
	}

	public Entry get(CursorOp op, byte[] key) {
		checkArgNotNull(op, "op"); //$NON-NLS-1$
		NativeBuffer keyBuffer = NativeBuffer.create(key);
		try {
			Value keyValue = new Value(keyBuffer);
			Value value = new Value();
			if (log.isTraceEnabled())
				log.trace("Calling cursor get op/key for {}", this); //$NON-NLS-1$
			int rc = mdbx_cursor_get(pointer(), keyValue, value, op.getValue());
			if (rc == MDBX_NOTFOUND) {
				return null;
			}
			checkErrorCode(env, tx, rc);
			return new Entry(keyValue.toByteArray(), value.toByteArray());
		}
		finally {
			if (keyBuffer != null)
				keyBuffer.delete();
		}
	}

	public Entry get(CursorOp op, byte[] key, byte[] value) {
		return get(op, key, value, x -> true);
	}

	public Entry get(CursorOp op, byte[] key, byte[] value, Predicate<byte[]> matchPredicate) {
		checkArgNotNull(op, "op"); //$NON-NLS-1$
		NativeBuffer keyBuffer = NativeBuffer.create(key);
		NativeBuffer valBuffer = NativeBuffer.create(value);

		try {
			Value keyValue = keyBuffer != null ? new Value(keyBuffer) : new Value();
			Value valValue = valBuffer != null ? new Value(valBuffer) : new Value();
			if (log.isTraceEnabled())
				log.trace("Calling cursor get op/key/val for {}", this); //$NON-NLS-1$
			int rc = mdbx_cursor_get(pointer(), keyValue, valValue, op.getValue());
			if (rc == MDBX_NOTFOUND) {
				return null;
			}
			checkErrorCode(env, tx, rc);

			if (!matchPredicate.test(valValue.toByteArray())) {
				return null;
			}
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
		return get(op, key, value, null, null);
	}

	public OperationStatus get(CursorOp op, DatabaseEntry key, DatabaseEntry value,
			Predicate<DatabaseEntry> keyMatchPredicate, Predicate<DatabaseEntry> valMatchPredicate) {
		checkArgNotNull(op, "op"); //$NON-NLS-1$
		NativeBuffer keyBuffer = NativeBuffer.create(key.getData());
		NativeBuffer valBuffer = NativeBuffer.create(value.getData());

		try {
			Value keyValue = keyBuffer != null ? new Value(keyBuffer) : new Value();
			Value valValue = valBuffer != null ? new Value(valBuffer) : new Value();
			if (log.isTraceEnabled())
				log.trace("Calling cursor get op:{}/de key:{}/val:{} for {}", op, key, value, this); //$NON-NLS-1$
			int rc = mdbx_cursor_get(pointer(), keyValue, valValue, op.getValue());
			if (rc == MDBX_NOTFOUND) {
				return OperationStatus.NOTFOUND;
			}
			checkErrorCode(env, tx, rc);

			key.setData(keyValue.toByteArray());
			if (keyMatchPredicate !=null && !keyMatchPredicate.test(key)) {
				return OperationStatus.NOTFOUND;
			}

			value.setData(valValue.toByteArray());
			if (valMatchPredicate !=null && !valMatchPredicate.test(value)) {
				return OperationStatus.NOTFOUND;
			}
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
	 *          <li>{@link com.castortech.mdbxjni.Constants#CURRENT} - replace the item at the current cursor
	 *          position. The \b key parameter must still be provided, and must match it. If using sorted
	 *          duplicates ({@link com.castortech.mdbxjni.Constants#DUPSORT}) the data item must still sort into
	 *          the same place. This is intended to be used when the new data is the same size as the old.
	 *          Otherwise it will simply perform a delete of the old record followed by an insert.
	 *          <li>{@link com.castortech.mdbxjni.Constants#NODUPDATA} - enter the new key/data pair only if it
	 *          does not already appear in the database. This flag may only be specified if the database was
	 *          opened with {@link com.castortech.mdbxjni.Constants#DUPSORT}. The function will return
	 *          {@link com.castortech.mdbxjni.MDBXException.Status#KEYEXIST} if the key/data pair already appears in
	 *          the database.
	 *          <li>{@link com.castortech.mdbxjni.Constants#NOOVERWRITE} - enter the new key/data pair only if
	 *          the key does not already appear in the database. The function will return
	 *          {@link com.castortech.mdbxjni.MDBXException.Status#KEYEXIST} if the key already appears in the
	 *          database, even if the database supports duplicates
	 *          ({@link com.castortech.mdbxjni.MDBXException.Status#KEYEXIST}).
	 *          <li>{@link com.castortech.mdbxjni.Constants#RESERVE} - reserve space for data of the given size,
	 *          but don't copy the given data. Instead, return a pointer to the reserved space, which the caller
	 *          can fill in later. This saves an extra memcpy if the data is being generated later.
	 *          <li>{@link com.castortech.mdbxjni.Constants#APPEND} - append the given key/data pair to the end
	 *          of the database. No key comparisons are performed. This option allows fast bulk loading when
	 *          keys are already known to be in the correct order. Loading unsorted keys with this flag will
	 *          cause data corruption.
	 *          <li>{@link com.castortech.mdbxjni.Constants#APPENDDUP} - as above, but for sorted dup data.
	 *          <li>{@link com.castortech.mdbxjni.Constants#MULTIPLE} - store multiple contiguous data elements
	 *          in a single request. This flag may only be specified if the database was opened with
	 *          {@link com.castortech.mdbxjni.Constants#DUPFIXED}. The \b data argument must be an array of two
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

		if (log.isTraceEnabled())
			log.trace("Calling cursor put key/val/flags for {}", this); //$NON-NLS-1$
		int rc = mdbx_cursor_put(pointer(), keySlice, valueSlice, flags);

		if (((flags & MDBX_NOOVERWRITE) != 0 || (flags & MDBX_NODUPDATA) != 0) && rc == MDBX_KEYEXIST) {
			// Return the existing value if it was a dup insert attempt.
			return valueSlice.toByteArray();
		}
		else {
			// If the put failed, throw an exception..
			checkErrorCode(env, tx, rc);

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

		if (log.isTraceEnabled())
			log.trace("Calling cursor del for {}", this); //$NON-NLS-1$
		int rc = mdbx_cursor_del(pointer(), 0);
		checkErrorCode(env, tx, rc);

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
	 * May only be called if the database was opened with {@link com.castortech.mdbxjni.Constants#DUPSORT}.
	 */
	public void deleteIncludingDups() {
		if (log.isTraceEnabled())
			log.trace("Calling cursor del w/dupes for {}", this); //$NON-NLS-1$
		checkErrorCode(env, tx, mdbx_cursor_del(pointer(), MDBX_NODUPDATA));
	}

	/**
	 * <p>
	 * Return count of duplicates for current key.
	 * </p>
	 *
	 * This call is only valid on databases that support sorted duplicate data items
	 * {@link com.castortech.mdbxjni.Constants#DUPSORT}.
	 *
	 * @return count of duplicates for current key
	 */
	public long count() {
		long[] rc = new long[1];
		if (log.isTraceEnabled())
			log.trace("Calling cursor count for {}", this); //$NON-NLS-1$
		checkErrorCode(env, tx, mdbx_cursor_count(pointer(), rc));
		return rc[0];
	}

	/**
	 * Special version of count that starts with a positioned cursor and will iterate to count duplicates for
	 * the current key
	 *
	 * @return count of scanned records. Note that if cursor was initialized via a cursor op that returns data,
	 *         the count will have to be incremented to account for 1st value.
	 */
	public long count(CursorOp op, DatabaseEntry key, DatabaseEntry value,
			Predicate<DatabaseEntry> valMatchPredicate) {
		long cnt = 0;

		while (true) {
			OperationStatus status = get(op, key, value, null, valMatchPredicate);
			if (status != OperationStatus.SUCCESS) {
				break;
			}
			cnt++;
		}
		return cnt;
	}

	public Database getDatabase() {
		return db;
	}

	public Transaction getTransaction() {
		return tx;
	}

	public MDBX_cursor internCursor() {
		MDBX_cursor rc = new MDBX_cursor();
		JNIIntern.ptr_2_cursor(pointer(), rc, JNIIntern.SIZEOF_CURSOR);
		return rc;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[Id:");
		sb.append(System.identityHashCode(this));
		sb.append(", db:");
		sb.append(getDatabase());
		sb.append(", txn:");
		sb.append(getTransaction());
		return sb.toString();
	}
}