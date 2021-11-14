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

import static com.castortech.mdbxjni.JNI.*;
import static com.castortech.mdbxjni.Util.checkErrorCode;

/**
 * A transaction handle.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Transaction extends NativeObject implements Closeable {
	private final Env env;

	Transaction(Env env, long self) {
		super(self);
		this.env = env;
	}

	/**
	 * <p>
	 * Return the transaction's ID.
	 * </p>
	 *
	 * This returns the identifier associated with this transaction. For a read-only transaction, this
	 * corresponds to the snapshot being read; concurrent readers will frequently have the same transaction ID.
	 *
	 * @return A transaction ID, valid if input is an active transaction.
	 */
	public long getId() {
		return mdbx_txn_id(pointer());
	}

	/**
	 * <p>
	 * Renew a read-only transaction.
	 * </p>
	 *
	 * This acquires a new reader lock for a transaction handle that had been released by #mdb_txn_reset(). It
	 * must be called before a reset transaction may be used again.
	 */
	public void renew() {
		checkErrorCode(env, mdbx_txn_renew(pointer()));
	}

	/**
	 * <p>
	 * Commit all the operations of a transaction into the database.
	 * </p>
	 *
	 * The transaction handle is freed. It and its cursors must not be used again after this call, except with
	 * #mdb_cursor_renew().
	 *
	 * @note Earlier documentation incorrectly said all cursors would be freed. Only write-transactions free
	 *       cursors.
	 *
	 */
	public void commit() {
		if (self != 0) {
			checkErrorCode(env, mdbx_txn_commit(self));
			self = 0;
		}
	}

	public CommitLatency commitWithLatency() {
		if (self != 0) {
			MDBX_commit_latency rc = new MDBX_commit_latency();
			checkErrorCode(env, mdbx_txn_commit_ex(self, rc));
			self = 0;
			return new CommitLatency(rc);
		}
		return null;
	}

	/**
	 * <p>
	 * Reset a read-only transaction.
	 * </p>
	 *
	 * Abort the transaction like #mdb_txn_abort(), but keep the transaction handle. #mdb_txn_renew() may reuse
	 * the handle. This saves allocation overhead if the process will start a new read-only transaction soon,
	 * and also locking overhead if {@link org.fusesource.lmdbjni.Constants#NOTLS} is in use. The reader table
	 * lock is released, but the table slot stays tied to its thread or #MDB_txn. Use mdb_txn_abort() to discard
	 * a reset handle, and to free its lock table slot if {@link org.fusesource.lmdbjni.Constants#NOTLS} is in
	 * use. Cursors opened within the transaction must not be used again after this call, except with
	 * #mdb_cursor_renew(). Reader locks generally don't interfere with writers, but they keep old versions of
	 * database pages allocated. Thus they prevent the old pages from being reused when writers commit new data,
	 * and so under heavy load the database size may grow much more rapidly than otherwise.
	 */
	public void reset() {
		checkAllocated();
		mdbx_txn_reset(pointer());
	}

	/**
	 * <p>
	 * Abandon all the operations of the transaction instead of saving them.
	 * </p>
	 *
	 * The transaction handle is freed. It and its cursors must not be used again after this call, except with
	 * #mdb_cursor_renew().
	 *
	 * @note Earlier documentation incorrectly said all cursors would be freed. Only write-transactions free
	 *       cursors.
	 */
	public void abort() {
		if (self != 0) {
			mdbx_txn_abort(self);
			self = 0;
		}
	}

	@Override
	public void close() {
		abort();
	}
}