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
import java.util.Comparator;
import java.util.List;
import org.fusesource.hawtjni.runtime.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.castortech.mdbxjni.JNI.MDBX_envinfo;
import com.castortech.mdbxjni.JNI.MDBX_stat;
import com.castortech.mdbxjni.pool.CursorPool;
import com.castortech.mdbxjni.pool.CursorPoolConfig;
import com.castortech.mdbxjni.pool.CursorPoolImpl;

import static com.castortech.mdbxjni.Constants.NEXT_NODUP;
import static com.castortech.mdbxjni.Constants.string;
import static com.castortech.mdbxjni.JNI.*;
import static com.castortech.mdbxjni.Util.*;

/**
 * An environment handle.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @author <a href="http://castortech.com">Alain Picard</a>
 */
public class Env extends NativeObject implements Closeable {
	private static final Logger log = LoggerFactory.getLogger(Env.class);

	public static final boolean IS_WINDOWS = isWindows();

	@SuppressWarnings("nls")
	static private boolean isWindows() {
		String name = System.getProperty("os.name").toLowerCase().trim();
		return name.startsWith("win");
	}

	private static final String MAIN_DB = "MAIN_DB"; //$NON-NLS-1$

	private Callback keyCmpCallback = null;
	private Callback dataCmpCallback = null;
	private Callback loggerCallback = null;
	private Comparator<byte[]> keyComparator;
	private Comparator<byte[]> dataComparator;
	private Database mainDb;  //represents the main DB used by MDBX to maintain the list of databases
	private CursorPool cursorPool;

	/**
	 * Create an environment handle and open it at the same time with default
	 * values.
	 *
	 * @param path
	 *            directory in which the database files reside. This directory must
	 *            already exist and be writable.
	 * @see com.castortech.mdbxjni.Env#open(String, int, int)
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

	public static String version() {
		return "" + JNI.MDBX_VERSION_MAJOR + '.' + JNI.MDBX_VERSION_MINOR; //$NON-NLS-1$
	}

	public static VersionInfo versionInfo() {
		MDBX_version_info rc = new MDBX_version_info();

		NativeBuffer buffer = NativeBuffer.create(JNI.SIZEOF_VERSIONINFO);
//		mdbx_build();
		return new VersionInfo(rc);
	}

	public static BuildInfo buildInfo() {
		MDBX_build_info rc = new MDBX_build_info();

		NativeBuffer buffer = NativeBuffer.create(JNI.SIZEOF_BUILDINFO);
//		mdbx_build();
		return new BuildInfo(rc);
	}


//	printf("mdbx_copy version %d.%d.%d.%d\n"
//      " - source: %s %s, commit %s, tree %s\n"
//      " - anchor: %s\n"
//      " - build: %s for %s by %s\n"
//      " - flags: %s\n"
//      " - options: %s\n",
//      mdbx_version.major, mdbx_version.minor, mdbx_version.release,
//      mdbx_version.revision, mdbx_version.git.describe,
//      mdbx_version.git.datetime, mdbx_version.git.commit,
//      mdbx_version.git.tree, mdbx_sourcery_anchor, mdbx_build.datetime,
//      mdbx_build.target, mdbx_build.compiler, mdbx_build.flags,
//      mdbx_build.options);

	private static long create() {
		long[] envPtr = new long[1];
		checkErrorCode(null, mdbx_env_create(envPtr));
		return envPtr[0];
	}

	/**
	 * @see com.castortech.mdbxjni.Env#open(String, int, int)
	 */
	public void open(String path) {
		open(path, 0);
	}

	/**
	 * @see com.castortech.mdbxjni.Env#open(String, int, int)
	 */
	public void open(String path, int flags) {
		open(path, flags, 0644);
	}

	/**
	 * @see JNI#mdbx_env_open(long, String, int, int)
	 */
	public void open(String path, int flags, int mode) {
		int rc;
		if (IS_WINDOWS) {
			rc = mdbx_env_openW(pointer(), path, flags, mode);
		}
		else {
			rc = mdbx_env_open(pointer(), path, flags, mode);
		}

		if (rc != 0) {
			close();
		}
		else {
			mainDb = new Database(this, 1L, MAIN_DB);
		}
		checkErrorCode(this, rc);
	}

	public void open(String path, EnvConfig config) {
		checkArgNotNull(config, "config"); //$NON-NLS-1$
		int flags = 0;

		if (config.isValidation()) {
			flags |= EnvFlags.VALIDATION;
		}

		if (config.isNoSubDir()) {
			flags |= EnvFlags.NOSUBDIR;
		}

		if (config.isReadOnly()) {
			flags |= EnvFlags.RDONLY;
		}

		if (config.isExclusive()) {
			flags |= EnvFlags.EXCLUSIVE;
		}

		if (config.isAccede()) {
			flags |= EnvFlags.ACCEDE;
		}

		if (config.isWriteMap()) {
			flags |= EnvFlags.WRITEMAP;
		}

		if (config.isNoStickyThreads()) {
			flags |= EnvFlags.NOSTICKYTHREADS;
		}

		if (config.isNoReadAhead()) {
			flags |= EnvFlags.NORDAHEAD;
		}

		if (config.isNoMemInit()) {
			flags |= EnvFlags.NOMEMINIT;
		}

		if (config.isCoalesce()) {
			flags |= EnvFlags.COALESCE;
		}

		if (config.isLifoReclaim()) {
			flags |= EnvFlags.LIFORECLAIM;
		}

		if (config.isPagePerturb()) {
			flags |= EnvFlags.PAGEPERTURB;
		}

		if (config.isSyncDurable()) {
			flags |= EnvFlags.SYNCDURABLE;
		}

		if (config.isNoMetaSync()) {
			flags |= EnvFlags.NOMETASYNC;
		}

		if (config.isSafeNoSync()) {
			flags |= EnvFlags.SAFENOSYNC;
		}

		if (config.isMapAsync()) {
			flags |= EnvFlags.MAPASYNC;
		}

		if (config.isUtterlyNoSync()) {
			flags |= EnvFlags.UTTERLY_NOSYNC;
		}

		if (config.getMaxDbs() != -1) {
			setMaxDbs(config.getMaxDbs());
		}

		if (config.getMaxReaders() != -1) {
			setMaxReaders(config.getMaxReaders());
		}

		if (!config.getOptions().isEmpty()) {
			setOptions(config.getOptions());
		}

		setGeometry(config.getMapLower(), config.getMapSize(), config.getMapUpper(), config.getMapGrowth(),
				config.getMapShrink(), config.getPageSize());

		int rc;
		if (IS_WINDOWS) {
			rc = mdbx_env_openW(pointer(), path, flags, config.getMode());
		}
		else {
			rc = mdbx_env_open(pointer(), path, flags, config.getMode());
		}

		if (rc != 0) {
			close();
		}
		else {
			mainDb = new Database(this, 1L, MAIN_DB);
		}

		CursorPoolConfig poolConfig = new CursorPoolConfig();
		if (config.isUsePooledCursors()) {
			poolConfig.setTimeBetweenEvictionRuns(config.getPooledCursorTimeBetweenEvictionRuns());
			poolConfig.setMaxIdlePerKey(config.getPooledCursorMaxIdle());
			poolConfig.setSoftMinEvictableIdleTime(config.getPooledCursorMinEvictableIdleTime());
			poolConfig.setCloseMaxWaitSeconds(config.getPooledCloseMaxWaitSeconds());
			cursorPool = new CursorPoolImpl(poolConfig, this);
		}
		checkErrorCode(this, rc);
	}

	@Override
	public void close() {
		if (self != 0) {
			if (getCursorPool() != null) {
				getCursorPool().close();
				cursorPool = null;
			}

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
	 * Copy an MDBX environment to the specified path.
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
		copy(path, JNI.MDBX_CP_DEFAULTS);
	}

	public void copy(String path, int flags) {
		checkArgNotNull(path, "path"); //$NON-NLS-1$
		checkErrorCode(this, mdbx_env_copy(pointer(), path, flags));
	}

	/**
	 * <p>
	 * Flush the data buffers to disk.
	 * </p>
	 * Data is always written to disk when #mdb_txn_commit() is called, but the
	 * operating system may keep it buffered. MDBX always flushes the OS buffers
	 * upon commit as well, unless the environment was opened with
	 * {@link EnvFlags#SAFENOSYNC} or in part {@link EnvFlags#NOMETASYNC}
	 */
	public void sync() {
		checkErrorCode(this, mdbx_env_sync(pointer()));
	}

	/**
	 * Kept for backward compatibility with older versions
	 * @param force
	 *            force a synchronous flush. Otherwise if the environment has the
	 *            {@link EnvFlags#SAFENOSYNC} flag set the flushes will be omitted, and with
	 *            {@link EnvFlags#MAPASYNC} they will be asynchronous.
	 */
	public void sync(boolean force) {
		checkErrorCode(this, mdbx_env_sync_ex(pointer(), force ? 1 : 0, 0));
	}

	public void sync(boolean force, boolean nonblock) {
		checkErrorCode(this, mdbx_env_sync_ex(pointer(), force ? 1 : 0, nonblock ? 1 : 0));
	}

	public void syncPoll() {
		checkErrorCode(this, mdbx_env_sync_poll(pointer()));
	}

	/**
	 * \deprecated Please use \ref mdbx_env_set_geometry() instead.
   */
	@Deprecated
	public void setMapSize(long size) {
		checkErrorCode(this, mdbx_env_set_mapsize(pointer(), size));
	}

	/**
	 * \brief Set all size-related parameters of environment, including page size and the min/max size of the
	 * memory map. \ingroup c_settings
	 *
	 * In contrast to LMDB, the MDBX provide automatic size management of an database according the given
	 * parameters, including shrinking and resizing on the fly. From user point of view all of these just
	 * working. Nevertheless, it is reasonable to know some details in order to make optimal decisions when
	 * choosing parameters.
	 *
	 * \see mdbx_env_info_ex()
	 *
	 * Both \ref mdbx_env_set_geometry() and legacy \ref mdbx_env_set_mapsize() are inapplicable to read-only
	 * opened environment.
	 *
	 * Both \ref mdbx_env_set_geometry() and legacy \ref mdbx_env_set_mapsize() could be called either before or
	 * after \ref mdbx_env_open(), either within the write transaction running by current thread or not:
	 *
	 * - In case \ref mdbx_env_set_geometry() or legacy \ref mdbx_env_set_mapsize() was called BEFORE \ref
	 * mdbx_env_open(), i.e. for closed environment, then the specified parameters will be used for new database
	 * creation, or will be applied during opening if database exists and no other process using it.
	 *
	 * If the database is already exist, opened with \ref MDBX_EXCLUSIVE or not used by any other process, and
	 * parameters specified by \ref mdbx_env_set_geometry() are incompatible (i.e. for instance, different page
	 * size) then \ref mdbx_env_open() will return \ref MDBX_INCOMPATIBLE error.
	 *
	 * In another way, if database will opened read-only or will used by other process during calling \ref
	 * mdbx_env_open() that specified parameters will silently discarded (open the database with \ref
	 * MDBX_EXCLUSIVE flag to avoid this).
	 *
	 * - In case \ref mdbx_env_set_geometry() or legacy \ref mdbx_env_set_mapsize() was called after \ref
	 * mdbx_env_open() WITHIN the write transaction running by current thread, then specified parameters will be
	 * applied as a part of write transaction, i.e. will not be completely visible to any others processes until
	 * the current write transaction has been committed by the current process. However, if transaction will be
	 * aborted, then the database file will be reverted to the previous size not immediately, but when a next
	 * transaction will be committed or when the database will be opened next time.
	 *
	 * - In case \ref mdbx_env_set_geometry() or legacy \ref mdbx_env_set_mapsize() was called after \ref
	 * mdbx_env_open() but OUTSIDE a write transaction, then MDBX will execute internal pseudo-transaction to
	 * apply new parameters (but only if anything has been changed), and changes be visible to any others
	 * processes immediately after successful completion of function.
	 *
	 * Essentially a concept of "automatic size management" is simple and useful: - There are the lower and
	 * upper bounds of the database file size; - There is the growth step by which the database file will be
	 * increased, in case of lack of space; - There is the threshold for unused space, beyond which the database
	 * file will be shrunk; - The size of the memory map is also the maximum size of the database; - MDBX will
	 * automatically manage both the size of the database and the size of memory map, according to the given
	 * parameters.
	 *
	 * So, there some considerations about choosing these parameters: - The lower bound allows you to prevent
	 * database shrinking below certain reasonable size to avoid unnecessary resizing costs. - The upper bound
	 * allows you to prevent database growth above certain reasonable size. Besides, the upper bound defines the
	 * linear address space reservation in each process that opens the database. Therefore changing the upper
	 * bound is costly and may be required reopening environment in case of \ref MDBX_UNABLE_EXTEND_MAPSIZE
	 * errors, and so on. Therefore, this value should be chosen reasonable large, to accommodate future growth
	 * of the database. - The growth step must be greater than zero to allow the database to grow, but also
	 * reasonable not too small, since increasing the size by little steps will result a large overhead. - The
	 * shrink threshold must be greater than zero to allow the database to shrink but also reasonable not too
	 * small (to avoid extra overhead) and not less than growth step to avoid up-and-down flouncing. - The
	 * current size (i.e. `size_now` argument) is an auxiliary parameter for simulation legacy \ref
	 * mdbx_env_set_mapsize() and as workaround Windows issues (see below).
	 *
	 * Unfortunately, Windows has is a several issue with resizing of memory-mapped file: - Windows unable
	 * shrinking a memory-mapped file (i.e memory-mapped section) in any way except unmapping file entirely and
	 * then map again. Moreover, it is impossible in any way when a memory-mapped file is used more than one
	 * process. - Windows does not provide the usual API to augment a memory-mapped file (i.e. a memory-mapped
	 * partition), but only by using "Native API" in an undocumented way.
	 *
	 * MDBX bypasses all Windows issues, but at a cost: - Ability to resize database on the fly requires an
	 * additional lock and release `SlimReadWriteLock` during each read-only transaction. - During resize all
	 * in-process threads should be paused and then resumed. - Shrinking of database file is performed only when
	 * it used by single process, i.e. when a database closes by the last process or opened by the first. =
	 * Therefore, the size_now argument may be useful to set database size by the first process which open a
	 * database, and thus avoid expensive remapping further.
	 *
	 * For create a new database with particular parameters, including the page size, \ref
	 * mdbx_env_set_geometry() should be called after \ref mdbx_env_create() and before \ref mdbx_env_open().
	 * Once the database is created, the page size cannot be changed. If you do not specify all or some of the
	 * parameters, the corresponding default values will be used. For instance, the default for database size is
	 * 10485760 bytes.
	 *
	 * If the mapsize is increased by another process, MDBX silently and transparently adopt these changes at
	 * next transaction start. However, \ref mdbx_txn_begin() will return \ref MDBX_UNABLE_EXTEND_MAPSIZE if new
	 * mapping size could not be applied for current process (for instance if address space is busy). Therefore,
	 * in the case of \ref MDBX_UNABLE_EXTEND_MAPSIZE error you need close and reopen the environment to resolve
	 * error.
	 *
	 * \note Actual values may be different than your have specified because of rounding to specified database
	 * page size, the system page size and/or the size of the system virtual memory management unit. You can get
	 * actual values by \ref mdbx_env_info_ex() or see by using the tool `mdbx_chk` with the `-v` option.
	 *
	 * Legacy \ref mdbx_env_set_mapsize() correspond to calling \ref mdbx_env_set_geometry() with the arguments
	 * `size_lower`, `size_now`, `size_upper` equal to the `size` and `-1` (i.e. default) for all other
	 * parameters.
	 *
	 * \param [in] env An environment handle returned by \ref mdbx_env_create()
	 *
	 * \param [in] size_lower The lower bound of database size in bytes. Zero value means "minimal acceptable",
	 * and negative means "keep current or use default".
	 *
	 * \param [in] size_now The size in bytes to setup the database size for now. Zero value means "minimal
	 * acceptable", and negative means "keep current or use default". So, it is recommended always pass -1 in
	 * this argument except some special cases.
	 *
	 * \param [in] size_upper The upper bound of database size in bytes. Zero value means "minimal acceptable",
	 * and negative means "keep current or use default". It is recommended to avoid change upper bound while
	 * database is used by other processes or threaded (i.e. just pass -1 in this argument except absolutely
	 * necessary). Otherwise you must be ready for \ref MDBX_UNABLE_EXTEND_MAPSIZE error(s), unexpected pauses
	 * during remapping and/or system errors like "address busy", and so on. In other words, there is no way to
	 * handle a growth of the upper bound robustly because there may be a lack of appropriate system resources
	 * (which are extremely volatile in a multi-process multi-threaded environment).
	 *
	 * \param [in] growth_step The growth step in bytes, must be greater than zero to allow the database to
	 * grow. Negative value means "keep current or use default".
	 *
	 * \param [in] shrink_threshold The shrink threshold in bytes, must be greater than zero to allow the
	 * database to shrink and greater than growth_step to avoid shrinking right after grow. Negative value means
	 * "keep current or use default". Default is 2*growth_step.
	 *
	 * \param [in] pagesize The database page size for new database creation or -1 otherwise. Once the database
	 * is created, the page size cannot be changed. Must be power of 2 in the range between \ref
	 * MDBX_MIN_PAGESIZE and \ref MDBX_MAX_PAGESIZE. Zero value means "minimal acceptable", and negative means
	 * "keep current or use default".
	 *
	 * \returns A non-zero error value on failure and 0 on success, some possible errors are: \retval
	 * MDBX_EINVAL An invalid parameter was specified, or the environment has an active write transaction.
	 * \retval MDBX_EPERM Two specific cases for Windows: 1) Shrinking was disabled before via geometry settings
	 * and now it enabled, but there are reading threads that don't use the additional `SRWL` (which is required
	 * to avoid Windows issues). 2) Temporary close memory mapped is required to change geometry, but there read
	 * transaction(s) is running and no corresponding thread(s) could be suspended since the \ref
	 * MDBX_NOSTICKYTHREADS mode is used. \retval MDBX_EACCESS The environment opened in read-only. \retval
	 * MDBX_MAP_FULL Specified size smaller than the space already consumed by the environment. \retval
	 * MDBX_TOO_LARGE Specified size is too large, i.e. too many pages for given size, or a 32-bit process
	 * requests too much bytes for the 32-bit address space.
	 */
	public void setGeometry(long lower, long now, long upper, long growthStep, long shrinkThreshold,
			long pageSize) {
		checkErrorCode(this, mdbx_env_set_geometry(pointer(), lower, now, upper, growthStep, shrinkThreshold,
				pageSize));
	}

	public long getMaxDbs() {
		long[] rc = new long[1];
		checkErrorCode(this, mdbx_env_get_maxdbs(pointer(), rc));
		return rc[0];
	}

	public Database getMainDb() {
		return mainDb;
	}

	public CursorPool getCursorPool() {
		return cursorPool;
	}

	public boolean usePooledCursors() {
		return cursorPool != null;
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
		checkErrorCode(this, mdbx_env_set_maxdbs(pointer(), size));
	}

	public long getMaxReaders() {
		long[] rc = new long[1];
		checkErrorCode(this, mdbx_env_get_maxreaders(pointer(), rc));
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
	 * {@link EnvFlags#NOSTICKYTHREADS} is in use, #mdb_txn_begin()
	 * instead ties the slot to the MDB_txn object until it or the #MDB_env object
	 * is destroyed. This function may only be called after #mdb_env_create() and
	 * before #mdb_env_open().
	 *
	 * @param size
	 *            The maximum number of reader lock table slots
	 */
	public void setMaxReaders(int size) {
		checkErrorCode(this, mdbx_env_set_maxreaders(pointer(), size));
	}

	/**
	 * @deprecated Use {@link Env#getMaxKeySize(int)} or {@link Env#getMaxValSize(int)} instead.
	 */
	@Deprecated
	public long getMaxKeySize() {
		return mdbx_env_get_maxkeysize(pointer());
	}

	public long getMaxKeySize(int flags) {
		return mdbx_env_get_maxkeysize_ex(pointer(), flags);
	}

	public long getMaxValSize(int flags) {
		return mdbx_env_get_maxvalsize_ex(pointer(), flags);
	}

	public int getFlags() {
		long[] flags = new long[1];
		checkErrorCode(this, mdbx_env_get_flags(pointer(), flags));
		return (int) flags[0];
	}

	public void addFlags(int flags) {
		checkErrorCode(this, mdbx_env_set_flags(pointer(), flags, 1));
	}

	public void removeFlags(int flags) {
		checkErrorCode(this, mdbx_env_set_flags(pointer(), flags, 0));
	}

	/**
	 * @return pointer to user context
	 */
	public long getUserContext() {
		return mdbx_env_get_userctx(pointer());
	}

	/**
	 * Sets the user context to the supplied context native object
	 * @param ctx
	 */
	public void setUserContext(NativeObject ctx) {
		if (ctx != null) {
			mdbx_env_set_userctx(pointer(), ctx.pointer());
		}
	}

	public void setOptions(List<EnvOption> options) {
		for (EnvOption option : options) {
			checkErrorCode(this, mdbx_env_set_option(pointer(), option.getOption(), option.getValue()));
		}
	}

	/**
	 * @return Information about the MDBX environment.
	 *
	 * @deprecated Use {@link Env#info(Transaction)} instead.
	 */
	@Deprecated
	public EnvInfo info() {
		MDBX_envinfo rc = new MDBX_envinfo();
		mdbx_env_info(pointer(), rc, JNI.SIZEOF_ENVINFO);
		return new EnvInfo(rc);
	}

	/**
	 * Version of the method that runs within a transaction. Replaces previous version from {@link Env}
	 *
	 * @return Information about the MDBX environment.
	 */
	public EnvInfo info(Transaction txn) {
		MDBX_envinfo rc = new MDBX_envinfo();
		mdbx_env_info_ex(pointer(), txn.pointer(), rc, JNI.SIZEOF_ENVINFO);
		return new EnvInfo(rc);
	}

	/**
	 * @return Statistics about the MDBX environment.
	 *
	 * @deprecated Use {@link Env#stat(Transaction)} instead.
	 */
	@Deprecated
	public Stat stat() {
		MDBX_stat rc = new MDBX_stat();
		mdbx_env_stat(pointer(), rc, JNI.SIZEOF_STAT);
		return new Stat(rc);
	}

	/**
	 * Version of the method that runs within a transaction. Replaces previous version from {@link Env}
	 *
	 * @return Statistics about the MDBX environment.
	 */
	public Stat stat(Transaction txn) {
		MDBX_stat rc = new MDBX_stat();
		mdbx_env_stat_ex(pointer(), txn.pointer(), rc, JNI.SIZEOF_STAT);
		return new Stat(rc);
	}

	public String getPoolStats() {
		if (usePooledCursors()) {
			return cursorPool.getStats();
		}
		return null;
	}

	/**
	 * @return Percent full for the whole Db environment.
	 *
	 * @deprecated Use version with {@link Transaction} argument instead. This one can at times return erratic
	 *             results.
	 */
	@Deprecated
	public float percentageFull() {
		Stat stat2 = stat();
		EnvInfo info2 = info();

		if (stat2.ms_psize == 0) {
			return 0.0f;
		}

		long nbrPages = info2.getMapSize() / stat2.ms_psize;
		return (info2.getLastPgNo() / (float)nbrPages) * 100;
	}

	public float percentageFull(Transaction txn) {
		Stat stat2 = stat(txn);
		EnvInfo info2 = info(txn);

		if (stat2.ms_psize == 0) {
			return 0.0f;
		}

		long nbrPages = info2.getMapSize() / stat2.ms_psize;
		return (info2.getLastPgNo() / (float)nbrPages) * 100;
	}

	public Cursor createCursor() {
		try {
			return new Cursor(this, createMdbxCursor(), null, null);
		}
		catch (MDBXException e) {
			String msg = "Failed creating cursor for " + this; //$NON-NLS-1$
			throw new MDBXException(msg, e);
		}
	}

	public Cursor createSecondaryCursor() {
		try {
			return new SecondaryCursor(this, createMdbxCursor(), null, null);
		}
		catch (MDBXException e) {
			String msg = "Failed creating secondary cursor for " + this; //$NON-NLS-1$
			throw new MDBXException(msg, e);
		}
	}

	private long createMdbxCursor() {
		if (log.isTraceEnabled())
			log.trace("Calling cursor open for {}", this); //$NON-NLS-1$
		long cursorPtr = mdbx_cursor_create(0);
		if (cursorPtr == 0) {
			throw new MDBXException("Create cursor returned null"); //$NON-NLS-1$
		}
		return cursorPtr;
	}

	public Transaction createTransaction() {
		return createTransaction(null, false);
	}

	public Transaction createTransaction(boolean readOnly) {
		return createTransaction(null, readOnly);
	}

	/**
	 * @see com.castortech.mdbxjni.Env#createTransaction(Transaction, boolean)
	 */
	public Transaction createReadTransaction() {
		return createTransaction(null, true);
	}

	/**
	 * @see com.castortech.mdbxjni.Env#createTransaction(Transaction, boolean)
	 */
	public Transaction createWriteTransaction() {
		return createTransaction(null, false);
	}

	/**
	 * @see com.castortech.mdbxjni.Env#createTransaction(Transaction, boolean)
	 */
	public Transaction createTransaction(Transaction parent) {
		return createTransaction(parent, false);
	}

	/**
	 * <p>
	 * Create a transaction for use with the environment.
	 * </p>
	 * <p>
	 * The transaction handle may be discarded using #mdb_txn_abort() or
	 * #mdb_txn_commit().
	 * </p>
	 * <p>
	 * A transaction and its cursors must only be used by a single thread, and a
	 * thread may only have a single transaction at a time. If MDB_NOTLS is in use,
	 * this does not apply to read-only transactions. Cursors may not span
	 * transactions.
	 * </p>
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
	 * @return transaction handle
	 * <b>Note</b> A transaction and its cursors must only be used by a single thread, and
	 *       a thread may only have a single transaction at a time. If {@link EnvFlags#NOSTICKYTHREADS} is in
	 *       use, this does not apply to read-only transactions.
	 * <b>Note</b> Cursors may not span transactions.
	 */
	public Transaction createTransaction(Transaction parent, boolean readOnly) {
		long[] txpointer = new long[1];
		checkErrorCode(this, mdbx_txn_begin(pointer(),
				parent == null ? 0 : parent.pointer(), readOnly ? MDBX_RDONLY : 0, txpointer));
		return new Transaction(this, txpointer[0], readOnly);
	}

	public Transaction createTransaction(Transaction parent, boolean readOnly, NativeObject ctx) {
		long[] txpointer = new long[1];
		checkErrorCode(this, mdbx_txn_begin_ex(pointer(),
				parent == null ? 0 : parent.pointer(), readOnly ? MDBX_RDONLY : 0, txpointer, ctx.pointer()));
		return new Transaction(this, txpointer[0], readOnly);
	}

	/**
	 * <p>
	 * Open a database in the environment.
	 * </p>
	 * <p>
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
	 * </p>
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
	 *            <li>{@link com.castortech.mdbxjni.Constants#REVERSEKEY} Keys are
	 *            strings to be compared in reverse order, from the end of the
	 *            strings to the beginning. By default, Keys are treated as strings
	 *            and compared from beginning to end.
	 *            <li>{@link com.castortech.mdbxjni.Constants#DUPSORT} Duplicate
	 *            keys may be used in the database. (Or, from another perspective,
	 *            keys may have multiple data items, stored in sorted order.) By
	 *            default keys must be unique and may have only a single data item.
	 *            <li>{@link com.castortech.mdbxjni.Constants#INTEGERKEY} Keys are
	 *            binary integers in native byte order. Setting this option requires
	 *            all keys to be the same size, typically sizeof(int) or
	 *            sizeof(size_t).
	 *            <li>{@link com.castortech.mdbxjni.Constants#DUPFIXED} This flag
	 *            may only be used in combination with
	 *            {@link com.castortech.mdbxjni.Constants#DUPSORT}. This option
	 *            tells the library that the data items for this database are all
	 *            the same size, which allows further optimizations in storage and
	 *            retrieval. When all data items are the same size, the
	 *            #MDB_GET_MULTIPLE and #MDB_NEXT_MULTIPLE cursor operations may be
	 *            used to retrieve multiple items at once.
	 *            <li>{@link com.castortech.mdbxjni.Constants#INTEGERDUP} This
	 *            option specifies that duplicate data items are also integers, and
	 *            should be sorted as such.
	 *            <li>{@link com.castortech.mdbxjni.Constants#REVERSEDUP} This
	 *            option specifies that duplicate data items should be compared as
	 *            strings in reverse order.
	 *            <li>{@link com.castortech.mdbxjni.Constants#CREATE} Create the
	 *            named database if it doesn't exist. This option is not allowed in
	 *            a read-only transaction or a read-only environment.
	 *            </ul>
	 * @return A database handle.
	 */
	public Database openDatabase(Transaction tx, String name, int flags) {
		if (tx == null) {
			return openDatabase(name, flags);
		}

		checkArgNotNull(tx, "tx"); //$NON-NLS-1$
		// checkArgNotNull(name, "name");
		long[] dbi = new long[1];
		checkErrorCode(this, tx, mdbx_dbi_open(tx.pointer(), name, flags, dbi));
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
	 * @see com.castortech.mdbxjni.Env#open(String, int, int)
	 */
	public Database openDatabase() {
		return openDatabase(null, Constants.CREATE);
	}

	public Database openDatabase(Comparator<byte[]> keyComp, Comparator<byte[]> dataComp) {
		return openDatabase(null, Constants.CREATE, keyComp, dataComp);
	}

	/**
	 * @see com.castortech.mdbxjni.Env#open(String, int, int)
	 */
	public Database openDatabase(String name) {
		return openDatabase(name, Constants.CREATE);
	}

	public Database openDatabase(String name, Comparator<byte[]> keyComp, Comparator<byte[]> dataComp) {
		return openDatabase(name, Constants.CREATE, keyComp, dataComp);
	}

	/**
	 * @see com.castortech.mdbxjni.Env#open(String, int, int)
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
		long[] dbi = new long[1];

		if (keyComp != null) {
			keyCmpCallback = new Callback(this, "compareKey", 2); //$NON-NLS-1$
			keyCmpAddr = keyCmpCallback.getAddress();
			keyComparator = keyComp;
		}

		if (dataComp != null) {
			dataCmpCallback = new Callback(dataComp.getClass(), "compareData", 2); //$NON-NLS-1$
			dataCmpAddr = dataCmpCallback.getAddress();
			dataComparator = dataComp;
		}

		checkErrorCode(this, tx, mdbx_dbi_open_ex(tx.pointer(), name, flags, dbi, keyCmpAddr, dataCmpAddr));
		return new Database(this, dbi[0], name);
	}

	public long compareKey(long o1, long o2) {  //NOUCD: Called via Callback constructor in openDatabase
		Value v1 = new Value();
		map_val(o1, v1);

		Value v2 = new Value();
		map_val(o2, v2);

		byte[] key1 = v1.toByteArray();
		byte[] key2 = v2.toByteArray();

		return keyComparator.compare(key1, key2);
	}

	public long compareData(long o1, long o2) {  //NOUCD: Called via Callback constructor in openDatabase
		Value v1 = new Value();
		map_val(o1, v1);

		Value v2 = new Value();
		map_val(o2, v2);

		byte[] key1 = v1.toByteArray();
		byte[] key2 = v2.toByteArray();

		return dataComparator.compare(key1, key2);
	}

	/**
	 * @see com.castortech.mdbxjni.Env#open(String, int, int)
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
		long[] dbi = new long[1];
		checkErrorCode(this, tx, mdbx_dbi_open(tx.pointer(), name, flags, dbi));
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
		long[] dbi = new long[1];
		checkErrorCode(this, tx, mdbx_dbi_open(tx.pointer(), name, flags, dbi));
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
				catch (Exception e) {
					// Ignore it -- there is already an exception in flight.
				}
		}
		return succeeded;
	}

	public List<String> listDatabases(Transaction tx) {
		List<String> dbNames = new ArrayList<>();

		try (Cursor cursor = getMainDb().openCursor(tx)) {
			while (true) {
				byte[] key = new byte[1];
				Entry entry = cursor.get(NEXT_NODUP, key, null);
				if (entry == null) {
					break;
				}
				String keyVal = string(entry.getKey());
				dbNames.add(keyVal);
			}
		}
		return dbNames;
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

		if (config.isDupSort()) {
			flags |= Constants.DUPSORT;
		}

		if (config.isIntegerKey()) {
			flags |= Constants.INTEGERKEY;
		}

		if (config.isDupFixed()) {
			flags |= Constants.DUPFIXED;
		}

		if (config.isIntegerDup()) {
			flags |= Constants.INTEGERDUP;
		}

		if (config.isReverseDup()) {
			flags |= Constants.REVERSEDUP;
		}

		if (config.isCreate()) {
			flags |= Constants.CREATE;
		}

		if (config.isAccede()) {
			flags |= Constants.DBACCEDE;
		}

		return flags;
	}

	public DebugState setupDebug(MdbxLogLevel logLevel, int debugFlags) {
		long logDebugAddr = 0L;

		if (loggerCallback == null) {
			loggerCallback = new Callback(this, "logDebug", 5); //$NON-NLS-1$
			logDebugAddr = loggerCallback.getAddress();
		}

		int rc = mdbx_setup_debug(logLevel.getValue(), debugFlags, logDebugAddr);
		return new DebugState(rc);
	}


	/**
	 * Method callback for MDBX issued log entries
	 * @param level
	 * @param fctnNamePtr
	 * @param line
	 * @param formatPtr
	 * @param argsPtr
	 * @return
	 */
	public long logDebug(long level, long fctnNamePtr, long line, long formatPtr, long argsPtr) {  //NOUCD: Called via Callback constructor in openDatabase
		String fctnName = Util.string(fctnNamePtr);
		NativeBuffer buffer = NativeBuffer.create(1024);
		map_printf(buffer.self, 1024, formatPtr, argsPtr);
		String msg = Util.string(buffer.self);
		buffer.delete();

		if (msg != null && msg.contains("MDBX_NOTFOUND")) { //$NON-NLS-1$
			return 0;
		}

		MdbxLogLevel.log((int)level, log, "Function:{}, line:{}, msg:{}", fctnName, line, msg); //$NON-NLS-1$
		return 0;
	}
}