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
import java.util.Comparator;

import org.fusesource.hawtjni.runtime.Callback;
import com.castortech.mdbxjni.JNI.MDBX_envinfo;
import com.castortech.mdbxjni.JNI.MDBX_stat;
import static com.castortech.mdbxjni.JNI.*;
import static com.castortech.mdbxjni.Util.*;

/**
 * An environment handle.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @author <a href="http://castortech.com">Alain Picard</a>
 */
public class Env extends NativeObject implements Closeable {
	public static String version() {
		return "" + JNI.MDBX_VERSION_MAJOR + JNI.MDBX_VERSION_MINOR; //$NON-NLS-1$
	}

	private Callback keyCmpCallback = null;
	private Callback dataCmpCallback = null;
	private Comparator<byte[]> keyComparator;
	private Comparator<byte[]> dataComparator;

	/**
	 * Create an environment handle and open it at the same time with default
	 * values.
	 *
	 * @param path
	 *            directory in which the database files reside. This directory must
	 *            already exist and be writable.
	 * @see org.fusesource.lmdbjni.Env#open(String, int, int)
	 */
	public Env(String path) {
		super(create());
		setMaxDbs(1);
		open(path);
	}

	public Env() {
		super(create());
		setMaxDbs(1);
	}

	private static long create() {
		long env_ptr[] = new long[1];
		checkErrorCode(mdbx_env_create(env_ptr));
		return env_ptr[0];
	}

	/**
	 * @see org.fusesource.lmdbjni.Env#open(String, int, int)
	 */
	public void open(String path) {
		open(path, 0);
	}

	/**
	 * @see org.fusesource.lmdbjni.Env#open(String, int, int)
	 */
	public void open(String path, int flags) {
		open(path, flags, 0644);
	}

	/**
	 * <p>
	 * Open the environment.
	 * </p>
	 *
	 * If this function fails, #mdb_env_close() must be called to discard the
	 * #MDB_env handle.
	 *
	 * @param path
	 *            The directory in which the database files reside. This directory
	 *            must already exist and be writable.
	 * @param flags
	 *            Special options for this environment. This parameter must be set
	 *            to 0 or by bitwise OR'ing together one or more of the values
	 *            described here. Flags set by mdb_env_set_flags() are also used.
	 *            <ul>
	 *            <li>{@link org.fusesource.lmdbjni.Constants#FIXEDMAP} use a fixed
	 *            address for the mmap region. This flag must be specified when
	 *            creating the environment, and is stored persistently in the
	 *            environment. If successful, the memory map will always reside at
	 *            the same virtual address and pointers used to reference data items
	 *            in the database will be constant across multiple invocations. This
	 *            option may not always work, depending on how the operating system
	 *            has allocated memory to shared libraries and other uses. The
	 *            feature is highly experimental.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#NOSUBDIR} By default,
	 *            LMDB creates its environment in a directory whose pathname is
	 *            given in \b path, and creates its data and lock files under that
	 *            directory. With this option, \b path is used as-is for the
	 *            database main data file. The database lock file is the \b path
	 *            with "-lock" appended.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#RDONLY} Open the
	 *            environment in read-only mode. No write operations will be
	 *            allowed. LMDB will still modify the lock file - except on
	 *            read-only filesystems, where LMDB does not use locks.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#WRITEMAP} Use a
	 *            writeable memory map unless MDB_RDONLY is set. This is faster and
	 *            uses fewer mallocs, but loses protection from application bugs
	 *            like wild pointer writes and other bad updates into the database.
	 *            Incompatible with nested transactions. Processes with and without
	 *            MDB_WRITEMAP on the same environment do not cooperate well.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#NOMETASYNC} Flush
	 *            system buffers to disk only once per transaction, omit the
	 *            metadata flush. Defer that until the system flushes files to disk,
	 *            or next non-MDB_RDONLY commit or #mdb_env_sync(). This
	 *            optimization maintains database integrity, but a system crash may
	 *            undo the last committed transaction. I.e. it preserves the ACI
	 *            (atomicity, consistency, isolation) but not D (durability)
	 *            database property. This flag may be changed at any time using
	 *            #mdb_env_set_flags().
	 *            <li>{@link org.fusesource.lmdbjni.Constants#NOSYNC} Don't flush
	 *            system buffers to disk when committing a transaction. This
	 *            optimization means a system crash can corrupt the database or lose
	 *            the last transactions if buffers are not yet flushed to disk. The
	 *            risk is governed by how often the system flushes dirty buffers to
	 *            disk and how often #mdb_env_sync() is called. However, if the
	 *            filesystem preserves write order and the #MDB_WRITEMAP flag is not
	 *            used, transactions exhibit ACI (atomicity, consistency, isolation)
	 *            properties and only lose D (durability). I.e. database integrity
	 *            is maintained, but a system crash may undo the final transactions.
	 *            Note that (#MDB_NOSYNC | #MDB_WRITEMAP) leaves the system with no
	 *            hint for when to write transactions to disk, unless
	 *            #mdb_env_sync() is called. (#MDB_MAPASYNC | #MDB_WRITEMAP) may be
	 *            preferable. This flag may be changed at any time using
	 *            #mdb_env_set_flags().
	 *            <li>{@link org.fusesource.lmdbjni.Constants#MAPASYNC} When using
	 *            #MDB_WRITEMAP, use asynchronous flushes to disk. As with
	 *            #MDB_NOSYNC, a system crash can then corrupt the database or lose
	 *            the last transactions. Calling #mdb_env_sync() ensures on-disk
	 *            database integrity until next commit. This flag may be changed at
	 *            any time using #mdb_env_set_flags().
	 *            <li>{@link org.fusesource.lmdbjni.Constants#NOTLS} Don't use
	 *            Thread-Local Storage. Tie reader locktable slots to #MDB_txn
	 *            objects instead of to threads. I.e. #mdb_txn_reset() keeps the
	 *            slot reseved for the #MDB_txn object. A thread may use parallel
	 *            read-only transactions. A read-only transaction may span threads
	 *            if the user synchronizes its use. Applications that multiplex many
	 *            user threads over individual OS threads need this option. Such an
	 *            application must also serialize the write transactions in an OS
	 *            thread, since LMDB's write locking is unaware of the user threads.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#NOLOCK} Don't do any
	 *            locking. If concurrent access is anticipated, the caller must
	 *            manage all concurrency itself. For proper operation the caller
	 *            must enforce single-writer semantics, and must ensure that no
	 *            readers are using old transactions while a writer is active. The
	 *            simplest approach is to use an exclusive lock so that no readers
	 *            may be active at all when a writer begins.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#NORDAHEAD} Turn off
	 *            readahead. Most operating systems perform readahead on read
	 *            requests by default. This option turns it off if the OS supports
	 *            it. Turning it off may help random read performance when the DB is
	 *            larger than RAM and system RAM is full. The option is not
	 *            implemented on Windows.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#NOMEMINIT} Don't
	 *            initialize malloc'd memory before writing to unused spaces in the
	 *            data file. By default, memory for pages written to the data file
	 *            is obtained using malloc. While these pages may be reused in
	 *            subsequent transactions, freshly malloc'd pages will be
	 *            initialized to zeroes before use. This avoids persisting leftover
	 *            data from other code (that used the heap and subsequently freed
	 *            the memory) into the data file. Note that many other system
	 *            libraries may allocate and free memory from the heap for arbitrary
	 *            uses. E.g., stdio may use the heap for file I/O buffers. This
	 *            initialization step has a modest performance cost so some
	 *            applications may want to disable it using this flag. This option
	 *            can be a problem for applications which handle sensitive data like
	 *            passwords, and it makes memory checkers like Valgrind noisy. This
	 *            flag is not needed with #MDB_WRITEMAP, which writes directly to
	 *            the mmap instead of using malloc for pages. The initialization is
	 *            also skipped if #MDB_RESERVE is used; the caller is expected to
	 *            overwrite all of the memory that was reserved in that case. This
	 *            flag may be changed at any time using #mdb_env_set_flags().
	 *            </ul>
	 * @param mode
	 *            The UNIX permissions to set on created files. This parameter is
	 *            ignored on Windows.
	 */
	public void open(String path, int flags, int mode) {
		int rc = mdbx_env_open(pointer(), path, flags, mode);
		if (rc != 0) {
			close();
		}
		checkErrorCode(rc);
	}

	public void open(String path, EnvConfig config) {
		checkArgNotNull(config, "config"); //$NON-NLS-1$
		int flags = 0;

		if (config.isNoSubDir()) {
			flags |= Constants.NOSUBDIR;
		}

		if (config.isNoSync()) {
			flags |= Constants.NOSYNC;
		}
		
		if (config.isReadOnly()) {
			flags |= Constants.RDONLY;
		}

		if (config.isNoMetaSync()) {
			flags |= Constants.NOMETASYNC;
		}

		if (config.isWriteMap()) {
			flags |= Constants.WRITEMAP;
		}
		
		if (config.isMapAsync()) {
			flags |= Constants.MAPASYNC;
		}
		
		if (config.isNoTLS()) {
			flags |= Constants.NOTLS;
		}
		
		if (config.isNoReadAhead()) {
			flags |= Constants.NORDAHEAD;
		}

		if (config.isNoMemInit()) {
			flags |= Constants.NOMEMINIT;
		}

		if (config.isCoalesce()) {
			flags |= Constants.COALESCE;
		}

		if (config.isLifoReclaim()) {
			flags |= Constants.LIFORECLAIM;
		}

		if (config.isUtterlyNoSync()) {
			flags |= Constants.UTTERLY_NOSYNC;
		}

		if (config.isPagePerturb()) {
			flags |= Constants.PAGEPERTURB;
		}

		if (config.getMaxDbs() != -1) {
			setMaxDbs(config.getMaxDbs());
		}

		if (config.getMaxReaders() != -1) {
			setMaxReaders(config.getMaxReaders());
		}

		if (config.getMapSize() != -1) {
			setMapSize(config.getMapSize());
		}

		int rc = mdbx_env_open(pointer(), path, flags, config.getMode());
		if (rc != 0) {
			close();
		}
		checkErrorCode(rc);
	}

	@Override
	public void close() {
		if (self != 0) {
			mdbx_env_close(self);
			self = 0;
		}
		
		if (keyCmpCallback != null) {
			keyCmpCallback.dispose();
			keyCmpCallback = null;
		}
		if (dataCmpCallback != null) {
			dataCmpCallback.dispose();
			dataCmpCallback = null;
		}
	}

	/**
	 * <p>
	 * Copy an LMDB environment to the specified path.
	 * </p>
	 * This function may be used to make a backup of an existing environment. No
	 * lockfile is created, since it gets recreated at need. This call can trigger
	 * significant file size growth if run in parallel with write transactions,
	 * because it employs a read-only transaction.
	 *
	 * @param path
	 *            The directory in which the copy will reside. This directory must
	 *            already exist and be writable but must otherwise be empty.
	 */
	public void copy(String path) {
		copy(path, 0);
	}

	public void copy(String path, int flags) {
		checkArgNotNull(path, "path"); //$NON-NLS-1$
		checkErrorCode(mdbx_env_copy(pointer(), path, flags));
	}

	/**
	 * <p>
	 * Flush the data buffers to disk.
	 * </p>
	 * Data is always written to disk when #mdb_txn_commit() is called, but the
	 * operating system may keep it buffered. LMDB always flushes the OS buffers
	 * upon commit as well, unless the environment was opened with
	 * {@link org.fusesource.lmdbjni.Constants#NOSYNC} or in part
	 * {@link org.fusesource.lmdbjni.Constants#NOMETASYNC}
	 *
	 * @param force
	 *            force a synchronous flush. Otherwise if the environment has the
	 *            {@link org.fusesource.lmdbjni.Constants#NOSYNC} flag set the
	 *            flushes will be omitted, and with
	 *            {@link org.fusesource.lmdbjni.Constants#MAPASYNC} they will be
	 *            asynchronous.
	 */
	public void sync(boolean force) {
		checkErrorCode(mdbx_env_sync(pointer(), force ? 1 : 0));
	}

	/**
	 * <p>
	 * Set the size of the memory map to use for this environment.
	 * </p>
	 *
	 * The size should be a multiple of the OS page size. The default is 10485760
	 * bytes. The size of the memory map is also the maximum size of the database.
	 * The value should be chosen as large as possible, to accommodate future growth
	 * of the database. This function should be called after #mdb_env_create() and
	 * before #mdb_env_open(). It may be called at later times if no transactions
	 * are active in this process. Note that the library does not check for this
	 * condition, the caller must ensure it explicitly.
	 *
	 * The new size takes effect immediately for the current process but will not be
	 * persisted to any others until a write transaction has been committed by the
	 * current process. Also, only mapsize increases are persisted into the
	 * environment.
	 *
	 * If the mapsize is increased by another process, and data has grown beyond the
	 * range of the current mapsize, #mdb_txn_begin() will return
	 * {@link org.fusesource.MDBXException.LMDBException#MAP_RESIZED}. This function may
	 * be called with a size of zero to adopt the new size.
	 *
	 * Any attempt to set a size smaller than the space already consumed by the
	 * environment will be silently changed to the current size of the used space.
	 *
	 * @param size
	 *            The size in bytes
	 */
	public void setMapSize(long size) {
		checkErrorCode(mdbx_env_set_mapsize(pointer(), size));
	}

	/**
	 * <p>
	 * Set the maximum number of named databases for the environment.
	 * </p>
	 *
	 *
	 * This function is only needed if multiple databases will be used in the
	 * environment. Simpler applications that use the environment as a single
	 * unnamed database can ignore this option. This function may only be called
	 * after #mdb_env_create() and before #mdb_env_open().
	 *
	 * @param size
	 *            The maximum number of databases
	 */
	public void setMaxDbs(long size) {
		checkErrorCode(mdbx_env_set_maxdbs(pointer(), size));
	}

	public long getMaxReaders() {
		long rc[] = new long[1];
		checkErrorCode(mdbx_env_get_maxreaders(pointer(), rc));
		return rc[0];
	}

	/**
	 * <p>
	 * Set the maximum number of threads/reader slots for the environment.
	 * </p>
	 *
	 * This defines the number of slots in the lock table that is used to track
	 * readers in the the environment. The default is 126. Starting a read-only
	 * transaction normally ties a lock table slot to the current thread until the
	 * environment closes or the thread exits. If
	 * {@link org.fusesource.lmdbjni.Constants#NOTLS} is in use, #mdb_txn_begin()
	 * instead ties the slot to the MDB_txn object until it or the #MDB_env object
	 * is destroyed. This function may only be called after #mdb_env_create() and
	 * before #mdb_env_open().
	 *
	 * @param size
	 *            The maximum number of reader lock table slots
	 */
	public void setMaxReaders(long size) {
		checkErrorCode(mdbx_env_set_maxreaders(pointer(), size));
	}

	public long getMaxKeySize() {
		return mdbx_env_get_maxkeysize(pointer());
	}

	public int getFlags() {
		long[] flags = new long[1];
		checkErrorCode(mdbx_env_get_flags(pointer(), flags));
		return (int) flags[0];
	}

	public void addFlags(int flags) {
		checkErrorCode(mdbx_env_set_flags(pointer(), flags, 1));
	}

	public void removeFlags(int flags) {
		checkErrorCode(mdbx_env_set_flags(pointer(), flags, 0));
	}

	/**
	 * @return Information about the LMDB environment.
	 */
	public EnvInfo info() {
		MDBX_envinfo rc = new MDBX_envinfo();
		mdbx_env_info(pointer(), rc, JNI.SIZEOF_ENVINFO);
		return new EnvInfo(rc);
	}

	/**
	 * @return Statistics about the LMDB environment.
	 */
	public Stat stat() {
		MDBX_stat rc = new MDBX_stat();
		mdbx_env_stat(pointer(), rc, JNI.SIZEOF_STAT);
		return new Stat(rc);
	}

	public float percentageFull() {
		Stat stat2 = stat();
		EnvInfo info2 = info();
		long nbr_pages = info2.getMapSize() / stat2.ms_psize;
		float percent_used = (info2.getLastPgNo() / (float) nbr_pages) * 100;
		return percent_used;
	}

	public Transaction createTransaction() {
		return createTransaction(null, false);
	}

	public Transaction createTransaction(boolean readOnly) {
		return createTransaction(null, readOnly);
	}

	/**
	 * @see org.fusesource.lmdbjni.Env#createTransaction(Transaction, boolean)
	 */
	public Transaction createReadTransaction() {
		return createTransaction(null, true);
	}

	/**
	 * @see org.fusesource.lmdbjni.Env#createTransaction(Transaction, boolean)
	 */
	public Transaction createWriteTransaction() {
		return createTransaction(null, false);
	}

	/**
	 * @see org.fusesource.lmdbjni.Env#createTransaction(Transaction, boolean)
	 */
	public Transaction createTransaction(Transaction parent) {
		return createTransaction(parent, false);
	}

	/**
	 * <p>
	 * Create a transaction for use with the environment.
	 * </p>
	 * <p/>
	 * The transaction handle may be discarded using #mdb_txn_abort() or
	 * #mdb_txn_commit().
	 *
	 * A transaction and its cursors must only be used by a single thread, and a
	 * thread may only have a single transaction at a time. If MDB_NOTLS is in use,
	 * this does not apply to read-only transactions. Cursors may not span
	 * transactions.
	 *
	 * @param parent
	 *            If this parameter is non-NULL, the new transaction will be a
	 *            nested transaction, with the transaction indicated by \b parent as
	 *            its parent. Transactions may be nested to any level. A parent
	 *            transaction and its cursors may not issue any other operations
	 *            than mdb_txn_commit and mdb_txn_abort while it has active child
	 *            transactions.
	 * @param readOnly
	 *            This transaction will not perform any write operations.
	 * @param parent
	 *            If this parameter is non-NULL, the new transaction will be a
	 *            nested transaction, with the transaction indicated by \b parent as
	 *            its parent. Transactions may be nested to any level. A parent
	 *            transaction and its cursors may not issue any other operations
	 *            than mdb_txn_commit and mdb_txn_abort while it has active child
	 *            transactions.
	 * @return transaction handle
	 * @note A transaction and its cursors must only be used by a single thread, and
	 *       a thread may only have a single transaction at a time. If
	 *       {@link org.fusesource.lmdbjni.Constants#NOTLS} is in use, this does not
	 *       apply to read-only transactions.
	 * @note Cursors may not span transactions.
	 */
	public Transaction createTransaction(Transaction parent, boolean readOnly) {
		long txpointer[] = new long[1];
		// System.err.println("JNI creating transaction, parent: " + (parent == null ?
		// null :parent.self) + ",id:" + self);

		checkErrorCode(mdbx_txn_begin(pointer(), 
				parent == null ? 0 : parent.pointer(), readOnly ? MDBX_RDONLY : 0, txpointer));
		return new Transaction(this, txpointer[0]);
	}

	/**
	 * <p>
	 * Open a database in the environment.
	 * </p>
	 * <p/>
	 * A database handle denotes the name and parameters of a database,
	 * independently of whether such a database exists. The database handle may be
	 * discarded by calling #mdb_dbi_close(). The old database handle is returned if
	 * the database was already open. The handle may only be closed once. The
	 * database handle will be private to the current transaction until the
	 * transaction is successfully committed. If the transaction is aborted the
	 * handle will be closed automatically. After a successful commit the handle
	 * will reside in the shared environment, and may be used by other transactions.
	 * This function must not be called from multiple concurrent transactions. A
	 * transaction that uses this function must finish (either commit or abort)
	 * before any other transaction may use this function.
	 * <p/>
	 * To use named databases (with name != NULL), #mdb_env_set_maxdbs() must be
	 * called before opening the environment. Database names are kept as keys in the
	 * unnamed database.
	 *
	 * @param tx
	 *            A transaction handle.
	 * @param name
	 *            The name of the database to open. If only a single database is
	 *            needed in the environment, this value may be NULL.
	 * @param flags
	 *            Special options for this database. This parameter must be set to 0
	 *            or by bitwise OR'ing together one or more of the values described
	 *            here.
	 *            <ul>
	 *            <li>{@link org.fusesource.lmdbjni.Constants#REVERSEKEY} Keys are
	 *            strings to be compared in reverse order, from the end of the
	 *            strings to the beginning. By default, Keys are treated as strings
	 *            and compared from beginning to end.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#DUPSORT} Duplicate
	 *            keys may be used in the database. (Or, from another perspective,
	 *            keys may have multiple data items, stored in sorted order.) By
	 *            default keys must be unique and may have only a single data item.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#INTEGERKEY} Keys are
	 *            binary integers in native byte order. Setting this option requires
	 *            all keys to be the same size, typically sizeof(int) or
	 *            sizeof(size_t).
	 *            <li>{@link org.fusesource.lmdbjni.Constants#DUPFIXED} This flag
	 *            may only be used in combination with
	 *            {@link org.fusesource.lmdbjni.Constants#DUPSORT}. This option
	 *            tells the library that the data items for this database are all
	 *            the same size, which allows further optimizations in storage and
	 *            retrieval. When all data items are the same size, the
	 *            #MDB_GET_MULTIPLE and #MDB_NEXT_MULTIPLE cursor operations may be
	 *            used to retrieve multiple items at once.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#INTEGERDUP} This
	 *            option specifies that duplicate data items are also integers, and
	 *            should be sorted as such.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#REVERSEDUP} This
	 *            option specifies that duplicate data items should be compared as
	 *            strings in reverse order.
	 *            <li>{@link org.fusesource.lmdbjni.Constants#CREATE} Create the
	 *            named database if it doesn't exist. This option is not allowed in
	 *            a read-only transaction or a read-only environment.
	 * @return A database handle.
	 */
	public Database openDatabase(Transaction tx, String name, int flags) {
		if (tx == null) {
			return openDatabase(name, flags);
		}

		checkArgNotNull(tx, "tx"); //$NON-NLS-1$
		// checkArgNotNull(name, "name");
		long dbi[] = new long[1];
		checkErrorCode(mdbx_dbi_open(tx.pointer(), name, flags, dbi));
		return new Database(this, dbi[0], name);
	}

	public Database openDatabase(Transaction tx, String name, DatabaseConfig config) {
		checkArgNotNull(config, "config"); //$NON-NLS-1$

		if (tx == null) {
			return openDatabase(name, config);
		}

		checkArgNotNull(tx, "tx"); //$NON-NLS-1$
		int flags = setFlags(config);
		
		if (config.getKeyComparator() != null || config.getDataComparator() != null) {
			return openDatabase(tx, name, flags, config.getKeyComparator(), config.getDataComparator());
		}
		
		return openDatabase(tx, name, flags);
	}

	/**
	 * @see org.fusesource.lmdbjni.Env#open(String, int, int)
	 */
	public Database openDatabase() {
		return openDatabase(null, Constants.CREATE);
	}

	public Database openDatabase(Comparator<byte[]> keyComp, Comparator<byte[]> dataComp) {
		return openDatabase(null, Constants.CREATE, keyComp, dataComp);
	}

	/**
	 * @see org.fusesource.lmdbjni.Env#open(String, int, int)
	 */
	public Database openDatabase(String name) {
		return openDatabase(name, Constants.CREATE);
	}

	public Database openDatabase(String name, Comparator<byte[]> keyComp, Comparator<byte[]> dataComp) {
		return openDatabase(name, Constants.CREATE, keyComp, dataComp);
	}

	/**
	 * @see org.fusesource.lmdbjni.Env#open(String, int, int)
	 */
	public Database openDatabase(String name, int flags) {
		// checkArgNotNull(name, "name");
		Transaction tx = createTransaction();
		try {
			return openDatabase(tx, name, flags);
		} 
		finally {
			tx.commit();
		}
	}
	
	public Database openDatabase(String name, int flags, Comparator<byte[]> keyComp,  
			Comparator<byte[]> dataComp) {
		// checkArgNotNull(name, "name");
		Transaction tx = createTransaction();
		try {
			return openDatabase(tx, name, flags, keyComp, dataComp);
		} 
		finally {
			tx.commit();
		}
	}

	public Database openDatabase(String name, DatabaseConfig config) {
		Transaction tx = createTransaction();
		try {
			return openDatabase(tx, name, config);
		} 
		finally {
			tx.commit();
		}
	}

	public Database openDatabase(Transaction tx, String name, int flags, Comparator<byte[]> keyComp, 
			Comparator<byte[]> dataComp) {
		if (tx == null) {
			return openDatabase(name, flags, keyComp, dataComp);
		}

		long keyCmpAddr = 0L;
		long dataCmpAddr = 0L;
		
		checkArgNotNull(tx, "tx"); //$NON-NLS-1$
		// checkArgNotNull(name, "name");
		long dbi[] = new long[1];
		
		if (keyComp != null) {
			keyCmpCallback = new Callback(this, "compareKey", 2); //$NON-NLS-1$
			keyCmpAddr = keyCmpCallback.getAddress();
			keyComparator =  keyComp;
		}
		
		if (dataComp != null) {
			dataCmpCallback = new Callback(dataComp.getClass(), "compareData", 2); //$NON-NLS-1$
			dataCmpAddr = dataCmpCallback.getAddress();
			dataComparator =  dataComp;
		}
		
		checkErrorCode(mdbx_dbi_open_ex(tx.pointer(), name, flags, dbi, keyCmpAddr, dataCmpAddr));
		return new Database(this, dbi[0], name);
	}
	
	public long compareKey(long o1, long o2) {
		Value v1 = new Value();
		map_val(o1, v1);

		Value v2 = new Value();
		map_val(o2, v2);

		byte[] key1 = v1.toByteArray();
		byte[] key2 = v2.toByteArray();
		
		return keyComparator.compare(key1, key2);
	}
	
	public long compareData(long o1, long o2) {
		Value v1 = new Value();
		map_val(o1, v1);

		Value v2 = new Value();
		map_val(o2, v2);

		byte[] key1 = v1.toByteArray();
		byte[] key2 = v2.toByteArray();
		
		return dataComparator.compare(key1, key2);
	}

	/**
	 * @see org.fusesource.lmdbjni.Env#open(String, int, int)
	 */
	public SecondaryDatabase openSecondaryDatabase(Database primary, String name) {
		return openSecondaryDatabase(primary, name, Constants.CREATE);
	}

	public SecondaryDatabase openSecondaryDatabase(Database primary, String name, int flags) {
		Transaction tx = createTransaction();
		try {
			return openSecondaryDatabase(tx, primary, name, flags);
		} 
		finally {
			tx.commit();
		}
	}

	public SecondaryDatabase openSecondaryDatabase(Transaction tx, Database primary, String name, int flags) {
		if (tx == null) {
			return openSecondaryDatabase(primary, name, flags);
		}

		checkArgNotNull(tx, "tx"); //$NON-NLS-1$
		checkArgNotNull(primary, "primary"); //$NON-NLS-1$
		// checkArgNotNull(name, "name");
		long dbi[] = new long[1];
		checkErrorCode(mdbx_dbi_open(tx.pointer(), name, flags, dbi));
		SecondaryDbConfig config = new SecondaryDbConfig();
		SecondaryDatabase secDb = new SecondaryDatabase(this, primary, dbi[0], name, config);

		if (associateDbs(tx, primary, secDb)) {
			return secDb;
		} 
		else {
			throw new MDBXException("Error associating databases");
		}
	}

	public SecondaryDatabase openSecondaryDatabase(Database primary, String name, SecondaryDbConfig config) {
		Transaction tx = createTransaction();
		try {
			return openSecondaryDatabase(tx, primary, name, config);
		} 
		finally {
			tx.commit();
		}
	}

	public SecondaryDatabase openSecondaryDatabase(Transaction tx, Database primary, String name,
			SecondaryDbConfig config) {
		if (tx == null) {
			return openSecondaryDatabase(primary, name, config);
		}

		checkArgNotNull(tx, "tx"); //$NON-NLS-1$
		checkArgNotNull(primary, "primary"); //$NON-NLS-1$
		checkArgNotNull(config, "config"); //$NON-NLS-1$

		int flags = setFlags(config);
		long dbi[] = new long[1];
		checkErrorCode(mdbx_dbi_open(tx.pointer(), name, flags, dbi));
		SecondaryDatabase secDb = new SecondaryDatabase(this, primary, dbi[0], name, config);

		if (associateDbs(tx, primary, secDb)) {
			return secDb;
		} 
		else {
			throw new MDBXException("Error associating databases");
		}
	}

	private boolean associateDbs(Transaction tx, Database primary, SecondaryDatabase secondary) {
		boolean succeeded = false;
		try {
			primary.associate(tx, secondary);
			succeeded = true;
		} 
		finally {
			if (!succeeded)
				try {
					primary.close();
				} 
				catch (Throwable t) {
					// Ignore it -- there is already an exception in flight.
				}
		}
		return succeeded;
	}

	public static void pushMemoryPool(int size) {
		NativeBuffer.pushMemoryPool(size);
	}

	public static void popMemoryPool() {
		NativeBuffer.popMemoryPool();
	}

	private int setFlags(DatabaseConfig config) {
		int flags = 0;

		if (config.isReverseKey()) {
			flags |= Constants.REVERSEKEY;
		}

		if (config.isReverseDup()) {
			flags |= Constants.REVERSEDUP;
		}

		if (config.isDupSort()) {
			flags |= Constants.DUPSORT;
		}

		if (config.isDupFixed()) {
			flags |= Constants.DUPFIXED;
		}

		if (config.isIntegerKey()) {
			flags |= Constants.INTEGERKEY;
		}

		if (config.isIntegerDup()) {
			flags |= Constants.INTEGERDUP;
		}

		if (config.isCreate()) {
			flags |= Constants.CREATE;
		}

		return flags;
	}
}