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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.castortech.mdbxjni.JNI.MDBX_txn_info;

import static com.castortech.mdbxjni.JNI.*;
import static com.castortech.mdbxjni.Util.checkErrorCode;

/**
 * A transaction handle.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Transaction extends NativeObject implements Closeable {
	private static final Logger log = LoggerFactory.getLogger(Transaction.class);

	private final boolean readOnly;
	private final long threadId;
	private final Env env;

	/**
	 * Special user controlled object that can be added to a transaction to capture various information or
	 * metrics.
	 *
	 * There is no format and how to use and handle is left to the user.
	 */
	private Object logData;

	Transaction(Env env, long self, boolean readOnly) {
		super(self);
		threadId = Thread.currentThread().getId();
		this.env = env;
		this.readOnly = readOnly;
	}

	/**
	 * Transaction are associated with a specific thread and will throw an MDBX_THREAD_MISMATCH if used with the
	 * wrong thread. This method provides visibility into the creating thread.
	 *
	 * @return the id of the thread that created this transaction
	 */
	public long getThreadId() {
		return threadId;
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
		checkErrorCode(env, this, mdbx_txn_renew(pointer()));
	}


	public int releaseCursors() {
		return mdbx_txn_release_all_cursors(pointer());
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
			checkErrorCode(env, this, mdbx_txn_commit(self));

			if (env.usePooledCursors()) {
				try {
					env.getCursorPool().closeTransaction(this);
				}
				catch (Exception e) {
					log.error("Exception occurred", e); //$NON-NLS-1$
				}
			}
			self = 0;
		}
	}

	public CommitLatency commitWithLatency() {
		if (self != 0) {
			MDBX_commit_latency rc = new MDBX_commit_latency();
			checkErrorCode(env, this, mdbx_txn_commit_ex(self, rc));
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
	 * and also locking overhead if {@link com.castortech.mdbxjni.Constants#NOTLS} is in use. The reader table
	 * lock is released, but the table slot stays tied to its thread or #MDB_txn. Use mdb_txn_abort() to discard
	 * a reset handle, and to free its lock table slot if {@link com.castortech.mdbxjni.Constants#NOTLS} is in
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

	public void broken() {
		checkErrorCode(env, this, mdbx_txn_break(pointer()));
	}

	public TxnInfo info(boolean scanRlt) {
		MDBX_txn_info rc = new MDBX_txn_info();
		checkErrorCode(env, this, mdbx_txn_info(pointer(), rc, scanRlt ? 1 : 0));
		return new TxnInfo(rc);
	}

	public int getFlags() {
		return mdbx_txn_flags(pointer());
	}

	/**
	 * @return pointer to user context
	 */
	public long getUserContext() {
		return mdbx_txn_get_userctx(pointer());
	}

	/**
	 * Sets the user context to the supplied context native object
	 * @param ctx
	 */
	public void setUserContext(NativeObject ctx) {
		if (ctx != null) {
			mdbx_txn_set_userctx(pointer(), ctx.pointer());
		}
	}

	/** @see #logData */
	public Object getLogData() {
		return logData;
	}

	/** @see #logData */
	public void setLogData(Object logData) {
		this.logData = logData;
	}

	@Override
	public void close() {
		if (readOnly)
			abort();
		commit();
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		if (!isAllocated()) {
			return "Transaction [Id=Freed]";
		}

		try {
			return "Transaction [ThreadId=" + getThreadId() + ", Id=" + getId() + "]";
		}
		catch (MDBXException e) {
			return "Transaction [Id=Freed]";
		}
	}
}