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

import com.castortech.mdbxjni.ffm.FfmDispatch;

/**
 * This class holds all the native constant, structure and function mappings.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@SuppressWarnings( {"java:S1444", "java:S3008", "java:S101", "java:S116", "java:S117", "squid:S00100" })

public class JNI {
	static {
		FfmDispatch.loadConstants();
	}


	private static final void init() {
		FfmDispatch.init();
	}

	///////////////////////////////////////////////////////////////////////
	//
	// Posix APIs:
	//
	///////////////////////////////////////////////////////////////////////

	/**
	 * Provide access to standard C errno function
	 * @return error number
	 */

	public static final int errno() {
		return FfmDispatch.errno();
	}

	/**
	 * Provide access to standard C strerror function
	 * @param errnum error number
	 * @return char* to the error message string
	 */

	public static final long strerror(int errnum) {
		return FfmDispatch.strerror(errnum);
	}

	/**
	 * Provide access to standard C strlen function
	 * @param s char* to the error message string
	 * @return length of string error message
	 */
	public static final int strlen(long s) {
		return FfmDispatch.strlen(s);
	}

	/**
	 * Provide access to standard C malloc function
	 * @param size size to allocate
	 * @return void pointer
	 */

	public static final long malloc( long size) {
		return FfmDispatch.malloc(size);
	}

	/**
	 * Provide access to standard C free function
	 * @param self void pointer from previous malloc
	 */
	public static final void free( long self) {
		FfmDispatch.free(self);
	}

	///////////////////////////////////////////////////////////////////////
	//
	// Additional Helpers
	//
	///////////////////////////////////////////////////////////////////////
	public static final void buffer_copy ( byte[] src, long srcPos, long dest, long destPos, long length) {
		FfmDispatch.buffer_copy(src, srcPos, dest, destPos, length);
	}

	public static final void buffer_copy ( long src, long srcPos, byte[] dest, long destPos, long length) {
		FfmDispatch.buffer_copy(src, srcPos, dest, destPos, length);
	}

	///////////////////////////////////////////////////////////////////////
	//
	// The mdbx API
	//
	///////////////////////////////////////////////////////////////////////

	//====================================================//
	// Version Info (no enum)
	//====================================================//

	public static int MDBX_VERSION_MAJOR;

	public static int MDBX_VERSION_MINOR;


	public static class MDBX_version_info {

		public int major;

		public int minor;

		public int patch;

		public int tweak;

		public char[] semver_prerelease;


		public char[] git_datetime;

		public char[] git_tree;

		public char[] git_commit;

		public char[] git_describe;


		public char[] sourcery;

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
					"major=" + major +
					", minor=" + minor +
					", patch=" + patch +
					", tweak=" + tweak +
					", semver prerelease=" + new String(semver_prerelease) +
					", git datetime=" + new String(git_datetime) +
					", git tree=" + new String(git_tree) +
					", git commit=" + new String(git_commit) +
					", git describe=" + new String(git_describe) +
					", sourcery=" + new String(sourcery) +
					'}';
		}

		@SuppressWarnings("nls")
		public String getVersionString() {
			return "" + major + '.' + minor + '.' + patch + '.' + tweak;
		}
	}

	public static void readVersion(MDBX_version_info info) {
		FfmDispatch.readVersion(info);
	}

	public static void readBuild(MDBX_build_info info) {
		FfmDispatch.readBuild(info);
	}


	public static int SIZEOF_VERSIONINFO;


//
//	public static final int mdbx_version() {
//		return FfmDispatch.mdbx_version();
//	}

//
//	public static int mdbx_version;
//	public static MDBX_version_info mdbx_version;

	//====================================================//
	// Build Info
	//====================================================//

	public static class MDBX_build_info {

		public char[] datetime;

		public char[] target;

		public char[] options;

		public char[] compiler;

		public char[] flags;

		public char[] metadata;

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
					"datetime=" + new String(datetime) +
					", target=" + new String(target) +
					", options=" + new String(options) +
					", compiler=" + new String(compiler) +
					", flags=" + new String(flags) +
					", metadata=" + new String(metadata) +
					'}';
		}
	}


	public static int SIZEOF_BUILDINFO;

	//====================================================//
	// Transaction Info
	//====================================================//

	public static class MDBX_txn_info {

		public long txn_id;

		public long txn_reader_lag;

		public long txn_space_used;

		public long txn_space_limit_soft;

		public long txn_space_limit_hard;

		public long txn_space_retired;

		public long txn_space_leftover;

		public long txn_space_dirty;

		/** Page-get count when the library was built with MDBX_ENABLE_PGET_STAT. */
		public long txn_pget;

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
					"txn_id=" + txn_id +
					", txn_reader_lag=" + txn_reader_lag +
					", txn_space_used=" + txn_space_used +
					", txn_space_limit_soft=" + txn_space_limit_soft +
					", txn_space_limit_hard=" + txn_space_limit_hard +
					", txn_space_retired=" + txn_space_retired +
					", txn_space_leftover=" + txn_space_leftover +
					", txn_space_dirty=" + txn_space_dirty +
					", txn_pget=" + txn_pget +
					'}';
		}
	}

	//====================================================//
	// Commit Latency
	//====================================================//

	public static class MDBX_commit_latency {
		/**
		 * Duration of preparation (commit child transactions, update sub-databases records and cursors
		 * destroying).
		 */

		public int preparation;

		/**
		 * Duration of GC update by wall clock
		 */

		public int gc_wallclock;

		/**
		 * Duration of internal audit if enabled.
		 */

		public int audit;

		/**
		 * Duration of writing dirty/modified data pages.
		 */

		public int write;

		/**
		 * Duration of syncing written data to the dist/storage.
		 */

		public int sync;

		/**
		 * Duration of transaction ending (releasing resources).
		 */

		public int ending;

		/**
		 * The total duration of a commit
		 */

		public int whole;

		/**
		 * Duration of GC update by wall clock
		 */

		public int gc_cputime;

		/**
		 * Number of GC update iterations, more than 1 if there were repetitions/restarts.
		 */

		public int gc_prof_wloops;

		/**
		 * Number of iterations to merge GC records.
		 */

		public int gc_prof_coalescences;

		/**
		 * The number of times previous good/resilient commit points were killed when running in
		 * MDBX_UTTERLY_NOSYNC mode.
		 */

		public int gc_prof_wipes;

		/**
		 * The number of forced commits to disk to avoid database growth when working outside of the
		 * MDBX_UTTERLY_NOSYNC mode.
		 */

		public int gc_prof_flushes;

		/**
		 * The number of calls to the Handle-Slow-Readers mechanism to avoid database increment. See also
		 * MDBX_hsr_func
		 */

		public int gc_prof_kicks;

		/**
		 * Slow path execution count GC for user data.
		 */

		public int gc_prof_work_counter;

		/**
		 * Wall clock time spent reading and searching within the GC for user data.
		 */

		public int gc_prof_work_rtime_monotonic;

		/**
		 * CPU time in user mode spent preparing pages fetched from the GC for user data, including paging from disk.
		 */

		public int gc_prof_work_xtime_cpu;

		/**
		 * The number of search iterations inside the GC when allocating pages for user data.
		 */

		public int gc_prof_work_rsteps;

		/**
		 * Number of requests to allocate sequences of pages for user data.
		 */

		public int gc_prof_work_xpages;

		/**
		 * The number of page faults within the GC when allocating and preparing pages for user data.
		 */

		public int gc_prof_work_majflt;

		/**
		 * GC slow path execution count for purposes of maintaining and updating the GC itself.
		 */

		public int gc_prof_self_counter;

		/**
		 * Wall clock time spent reading and searching within the GC for purposes of maintaining and updating the
		 * GC itself.
		 */

		public int gc_prof_self_rtime_monotonic;

		/**
		 * CPU time in user mode spent preparing pages to be retrieved from the GC for the purposes of maintaining
		 * and updating the GC itself, including paging from disk.
		 */

		public int gc_prof_self_xtime_cpu;

		/**
		 * The number of iterations of searching within the GC when allocating pages for the purpose of
		 * maintaining and updating the GC itself.
		 */

		public int gc_prof_self_rsteps;

		/**
		 * Number of requests to allocate sequences of pages for user data.
		 */

		public int gc_prof_self_xpages;

		/**
		 * The number of page faults within the GC when allocating and preparing pages for the GC itself.
		 */

		public int gc_prof_self_majflt;

		/**
		 * For disassembling with pnl_merge()
		 */

		public int gc_prof_pnl_merge_work_time;


		public long gc_prof_pnl_merge_work_volume;


		public int gc_prof_pnl_merge_work_calls;


		public int gc_prof_pnl_merge_self_time;


		public long gc_prof_pnl_merge_self_volume;


		public int gc_prof_pnl_merge_self_calls;

		/**
		 * The maximum observed difference between the latest and oldest read MVCC-snapshots.
		 */

		public int gc_prof_max_reader_lag;

		/**
		 * The maximum noticed number of pages withheld from reclaim due to reading old MVCC-snapshots.
		 */

		public int gc_prof_max_retained_pages;

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
					"preparation=" + preparation +
					", gc_wallclock=" + gc_wallclock +
					", audit=" + audit +
					", write=" + write +
					", sync=" + sync +
					", ending=" + ending +
					", whole=" + whole +
					", gc_cputime=" + gc_cputime +
					", gc_prof_wloops=" + gc_prof_wloops +
					", gc_prof_coalescences=" + gc_prof_coalescences +
					", gc_prof_wipes=" + gc_prof_wipes +
					", gc_prof_flushes=" + gc_prof_flushes +
					", gc_prof_kicks=" + gc_prof_kicks +
					", gc_prof_work_counter=" + gc_prof_work_counter +
					", gc_prof_work_rtime_monotonic=" + gc_prof_work_rtime_monotonic +
					", gc_prof_work_xtime_cpu=" + gc_prof_work_xtime_cpu +
					", gc_prof_work_rsteps=" + gc_prof_work_rsteps +
					", gc_prof_work_xpages=" + gc_prof_work_xpages +
					", gc_prof_work_majflt=" + gc_prof_work_majflt +
					", gc_prof_self_counter=" + gc_prof_self_counter +
					", gc_prof_self_rtime_monotonic=" + gc_prof_self_rtime_monotonic +
					", gc_prof_self_xtime_cpu=" + gc_prof_self_xtime_cpu +
					", gc_prof_self_rsteps=" + gc_prof_self_rsteps +
					", gc_prof_self_xpages=" + gc_prof_self_xpages +
					", gc_prof_self_majflt=" + gc_prof_self_majflt +
					", gc_prof_pnl_merge_work_time=" + gc_prof_pnl_merge_work_time +
					", gc_prof_pnl_merge_work_volume=" + gc_prof_pnl_merge_work_volume +
					", gc_prof_pnl_merge_work_calls=" + gc_prof_pnl_merge_work_calls +
					", gc_prof_pnl_merge_self_time=" + gc_prof_pnl_merge_self_time +
					", gc_prof_pnl_merge_self_volume=" + gc_prof_pnl_merge_self_volume +
					", gc_prof_pnl_merge_self_calls=" + gc_prof_pnl_merge_self_calls +
					", gc_prof_max_reader_lag=" + gc_prof_max_reader_lag +
					", gc_prof_max_retained_pages=" + gc_prof_max_retained_pages +
					"}";
		}
	}

	//====================================================//
	// Canary
	//====================================================//

	public static class MDBX_canary {

		public long x;

		public long y;

		public long z;

		public long v;

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
					"x=" + x +
					", y=" + y +
					", z=" + z +
					", v=" + v +
					'}';
		}
	}

	//====================================================//
	// Environment Flags (MDBX_env_flags_t)
	//====================================================//
	/**
	 * \brief Environment flags
	 * \ingroup c_opening \anchor env_flags
	 * \see mdbx_env_open()
	 * \see mdbx_env_set_flags()
	 */

	public static int MDBX_ENV_DEFAULTS;

	/**
	 * Extra validation of DB structure and pages content.
	 *
	 * The `MDBX_VALIDATION` enabled the simple safe/careful mode for working with damaged or untrusted DB.
	 * However, a notable performance degradation should be expected.
	 */

	public static int MDBX_VALIDATION;

	/**
	 * No environment directory.
	 *
	 * By default, MDBX creates its environment in a directory whose pathname is given in path, and creates its
	 * data and lock files under that directory. With this option, path is used as-is for the database main data
	 * file. The database lock file is the path with "-lck" appended.
	 *
	 * - with `MDBX_NOSUBDIR` = in a filesystem we have the pair of MDBX-files which names derived from given
	 * pathname by appending predefined suffixes.
	 *
	 * - without `MDBX_NOSUBDIR` = in a filesystem we have the MDBX-directory with given pathname, within that a
	 * pair of MDBX-files with predefined names.
	 *
	 * This flag affects only at new environment creating by \ref mdbx_env_open(), otherwise at opening an
	 * existing environment libmdbx will choice this automatically.
	 */

	public static int MDBX_NOSUBDIR;

	/**
	 * Read only mode.
	 *
	 * Open the environment in read-only mode. No write operations will be allowed. MDBX will still modify the
	 * lock file - except on read-only filesystems, where MDBX does not use locks.
	 *
	 * - with `MDBX_RDONLY` = open environment in read-only mode. MDBX supports pure read-only mode (i.e.
	 * without opening LCK-file) only when environment directory and/or both files are not writable (and the
	 * LCK-file may be missing). In such case allowing file(s) to be placed on a network read-only share.
	 *
	 * - without `MDBX_RDONLY` = open environment in read-write mode.
	 *
	 * This flag affects only at environment opening but can't be changed after.
	 */

	public static int MDBX_RDONLY;

	/**
	 * Open environment in exclusive/monopolistic mode.
	 *
	 * `MDBX_EXCLUSIVE` flag can be used as a replacement for `MDB_NOLOCK`, which don't supported by MDBX. In
	 * this way, you can get the minimal overhead, but with the correct multi-process and multi-thread locking.
	 *
	 * - with `MDBX_EXCLUSIVE` = open environment in exclusive/monopolistic mode or return \ref MDBX_BUSY if
	 * environment already used by other process. The main feature of the exclusive mode is the ability to open
	 * the environment placed on a network share.
	 *
	 * - without `MDBX_EXCLUSIVE` = open environment in cooperative mode, i.e. for multi-process
	 * access/interaction/cooperation. The main requirements of the cooperative mode are:
	 *
	 * 1. data files MUST be placed in the LOCAL file system, but NOT on a network share. 2. environment MUST be
	 * opened only by LOCAL processes, but NOT over a network. 3. OS kernel (i.e. file system and memory mapping
	 * implementation) and all processes that open the given environment MUST be running in the physically
	 * single RAM with cache-coherency. The only exception for cache-consistency requirement is Linux on MIPS
	 * architecture, but this case has not been tested for a long time).
	 *
	 * This flag affects only at environment opening but can't be changed after.
	 */

	public static int MDBX_EXCLUSIVE;

	/**
	 * Using database/environment which already opened by another process(es).
	 *
	 * The `MDBX_ACCEDE` flag is useful to avoid \ref MDBX_INCOMPATIBLE error while opening the
	 * database/environment which is already used by another process(es) with unknown mode/flags. In such cases,
	 * if there is a difference in the specified flags (\ref MDBX_NOMETASYNC, \ref MDBX_SAFE_NOSYNC, \ref
	 * MDBX_UTTERLY_NOSYNC, \ref MDBX_LIFORECLAIM and \ref MDBX_NORDAHEAD), instead of returning an error, the
	 * database will be opened in a compatibility with the already used mode.
	 *
	 * `MDBX_ACCEDE` has no effect if the current process is the only one either opening the DB in read-only
	 * mode or other process(es) uses the DB in read-only mode.
	 */

	public static int MDBX_ACCEDE;

	/**
	 * Map data into memory with write permission.
	 *
	 * Use a writeable memory map unless \ref MDBX_RDONLY is set. This uses fewer mallocs and requires much less
	 * work for tracking database pages, but loses protection from application bugs like wild pointer writes and
	 * other bad updates into the database. This may be slightly faster for DBs that fit entirely in RAM, but is
	 * slower for DBs larger than RAM. Also adds the possibility for stray application writes thru pointers to
	 * silently corrupt the database.
	 *
	 * - with `MDBX_WRITEMAP` = all data will be mapped into memory in the read-write mode. This offers a
	 * significant performance benefit, since the data will be modified directly in mapped memory and then
	 * flushed to disk by single system call, without any memory management nor copying.
	 *
	 * - without `MDBX_WRITEMAP` = data will be mapped into memory in the read-only mode. This requires stocking
	 * all modified database pages in memory and then writing them to disk through file operations.
	 *
	 * \warning On the other hand, `MDBX_WRITEMAP` adds the possibility for stray application writes thru
	 * pointers to silently corrupt the database.
	 *
	 * \note The `MDBX_WRITEMAP` mode is incompatible with nested transactions, since this is unreasonable. I.e.
	 * nested transactions requires mallocation of database pages and more work for tracking ones, which neuters
	 * a performance boost caused by the `MDBX_WRITEMAP` mode.
	 *
	 * This flag affects only at environment opening but can't be changed after.
	 */

	public static int MDBX_WRITEMAP;

	/**
	 * Decouples transactions from threads as much as possible.
	 *
	 * This option is intended for applications that multiplex multiple user-defined lightweight threads of
	 * execution across separate operating system threads, such as in the GoLang and Rust runtimes. Such
	 * applications are also encouraged to serialize write transactions on a single operating system thread,
	 * since MDBX write locks use basic system synchronization primitives and know nothing about user threads
	 * and/or lightweight runtime threads. At a minimum, it is mandatory to ensure that each write transaction
	 * completes strictly on the same operating system thread where it was started.
	 *
	 * \note Starting with v0.13, the `MDBX_NOSTICKYTHREADS` option completely replaces the \ref MDBX_NOTLS
	 * option.
	 *
	 * Using `MDBX_NOSTICKYTHREADS` makes transactions un associated with the threads of execution that created
	 * them. Therefore, the API functions do not check whether the transaction matches the current thread of
	 * execution. Most functions that work with transactions and cursors can be called from any thread of
	 * execution. However, it also becomes impossible to detect errors in the simultaneous use of transactions
	 * and/or cursors in different threads.
	 *
	 * Using `MDBX_NOSTICKYTHREADS` also limits the ability to change the size of the database, since you lose
	 * the ability to track the threads working with the database and suspend them while the database is being
	 * unmapped in RAM. In particular, for this reason, on Windows, it is not possible to reduce the size of the
	 * database file until the last process working with it closes the database or until the database is
	 * subsequently opened in read-write mode.
	 *
	 * \warning Regardless of \ref MDBX_NOSTICKYTHREADS and \ref MDBX_NOTLS, it is not allowed to use API
	 * objects from different threads of execution at the same time! It is entirely your responsibility to
	 * ensure that all measures are taken to prevent the simultaneous use of API objects from different threads
	 * of execution!
	 *
	 * \warning Write transactions can only be completed in the same thread of execution where they were
	 * started. This restriction follows from the requirement of most operating systems that a captured
	 * synchronization primitive (mutex, semaphore, critical section) must be released only by the thread that
	 * captured it.
	 *
	 * \warning Creating a cursor in the context of a transaction, binding a cursor to a transaction, unbinding
	 * a cursor from a transaction, and closing a cursor bound to a transaction are operations that use both the
	 * cursor itself and the corresponding transaction. Similarly, completing or aborting a transaction is an
	 * operation that uses both the transaction itself and all cursors bound to it. To avoid damage to internal
	 * data structures, unpredictable behavior, database corruption, and data loss, you should not allow any
	 * cursor or transactions to be used simultaneously from different threads. * Reader transactions stop using
	 * TLS (Thread Local Storage) when using `MDBX_NOSTICKYTHREADS`, and MVCC snapshot lock slots in the reader
	 * table are bound only to transactions. Termination of any threads does not release MVCC snapshot locks
	 * until explicitly terminated by the transactions or until the corresponding process as a whole terminates.
	 *
	 * For write transactions, no check is performed to ensure that the current thread of execution matches the
	 * thread that created the transaction. However, committing or aborting write transactions must be performed
	 * strictly in the thread that started the transaction, since these operations are associated with the
	 * acquisition and release of synchronization primitives (mutexes, critical sections), for which most
	 * operating systems require release only by the thread that captured the resource.
	 *
	 * This flag takes effect when the environment is opened and cannot be changed afterward.*
	 */

	public static int MDBX_NOSTICKYTHREADS;

	/**
	 * Don't do readahead.
	 *
	 * Turn off readahead. Most operating systems perform readahead on read requests by default. This option
	 * turns it off if the OS supports it. Turning it off may help random read performance when the DB is larger
	 * than RAM and system RAM is full.
	 *
	 * By default libmdbx dynamically enables/disables readahead depending on the actual database size and
	 * currently available memory. On the other hand, such automation has some limitation, i.e. could be
	 * performed only when DB size changing but can't tracks and reacts changing a free RAM availability, since
	 * it changes independently and asynchronously.
	 *
	 * \note The mdbx_is_readahead_reasonable() function allows to quickly find out whether to use readahead or
	 * not based on the size of the data and the amount of available memory.
	 *
	 * This flag affects only at environment opening and can't be changed after.
	 */

	public static int MDBX_NORDAHEAD;

	/**
	 * Don't initialize malloc'ed memory before writing to datafile.
	 *
	 * Don't initialize malloc'ed memory before writing to unused spaces in the data file. By default, memory
	 * for pages written to the data file is obtained using malloc. While these pages may be reused in
	 * subsequent transactions, freshly malloc'ed pages will be initialized to zeroes before use. This avoids
	 * persisting leftover data from other code (that used the heap and subsequently freed the memory) into the
	 * data file.
	 *
	 * Note that many other system libraries may allocate and free memory from the heap for arbitrary uses.
	 * E.g., stdio may use the heap for file I/O buffers. This initialization step has a modest performance cost
	 * so some applications may want to disable it using this flag. This option can be a problem for
	 * applications which handle sensitive data like passwords, and it makes memory checkers like Valgrind
	 * noisy. This flag is not needed with \ref MDBX_WRITEMAP, which writes directly to the mmap instead of
	 * using malloc for pages. The initialization is also skipped if \ref MDBX_RESERVE is used; the caller is
	 * expected to overwrite all of the memory that was reserved in that case.
	 *
	 * This flag may be changed at any time using `mdbx_env_set_flags()`.
	 */

	public static int MDBX_NOMEMINIT;

	/**
	 * Aims to coalesce a Garbage Collection items.
	 *
   * \deprecated Always enabled since v0.12 and deprecated since v0.13.
   *
	 * Note Always enabled since v0.12
	 *
	 * With MDBX_COALESCE flag MDBX will aims to coalesce items while recycling a Garbage Collection.
	 * Technically, when possible short lists of pages will be combined into longer ones, but to fit on one
	 * database page. As a result, there will be fewer items in Garbage Collection and a page lists are longer,
	 * which slightly increases the likelihood of returning pages to Unallocated space and reducing the database
	 * file.
	 *
	 * This flag may be changed at any time using mdbx_env_set_flags().
	 */

	@Deprecated
	public static int MDBX_COALESCE;

	/**
	 * LIFO policy for recycling a Garbage Collection items.
	 *
	 * MDBX_LIFORECLAIM flag turns on LIFO policy for recycling a Garbage Collection items, instead of FIFO by
	 * default. On systems with a disk write-back cache, this can significantly increase write performance, up
	 * to several times in a best case scenario.
	 *
	 * LIFO recycling policy means that for reuse pages will be taken which became unused the lastest (i.e. just
	 * now or most recently). Therefore the loop of database pages circulation becomes as short as possible. In
	 * other words, the number of pages, that are overwritten in memory and on disk during a series of write
	 * transactions, will be as small as possible. Thus creates ideal conditions for the efficient operation of
	 * the disk write-back cache.
	 *
	 * MDBX_LIFORECLAIM is compatible with all no-sync flags, but gives NO noticeable impact in combination with
	 * MDBX_SAFE_NOSYNC or MDBX_UTTERLY_NOSYNC. Because MDBX will reused pages only before the last "steady"
	 * MVCC-snapshot, i.e. the loop length of database pages circulation will be mostly defined by frequency of
	 * calling mdbx_env_sync() rather than LIFO and FIFO difference.
	 *
	 * This flag may be changed at any time using mdbx_env_set_flags().
	 */

	public static int MDBX_LIFORECLAIM;

	/**
	 * Debugging option, fill/perturb released pages.
	 */

	public static int MDBX_PAGEPERTURB;

	/* SYNC MODES ****************************************************************/
	/**
	 * \defgroup sync_modes SYNC MODES
	 *
	 * \attention Using any combination of \ref MDBX_SAFE_NOSYNC, \ref MDBX_NOMETASYNC and especially \ref
	 * MDBX_UTTERLY_NOSYNC is always a deal to reduce durability for gain write performance. You must know
	 * exactly what you are doing and what risks you are taking!
	 *
	 * \note for LMDB users: \ref MDBX_SAFE_NOSYNC is NOT similar to LMDB_NOSYNC, but \ref MDBX_UTTERLY_NOSYNC
	 * is exactly match LMDB_NOSYNC. See details below.
	 *
	 * THE SCENE: - The DAT-file contains several MVCC-snapshots of B-tree at same time, each of those B-tree
	 * has its own root page. - Each of meta pages at the beginning of the DAT file contains a pointer to the
	 * root page of B-tree which is the result of the particular transaction, and a number of this transaction.
	 * - For data durability, MDBX must first write all MVCC-snapshot data pages and ensure that are written to
	 * the disk, then update a meta page with the new transaction number and a pointer to the corresponding new
	 * root page, and flush any buffers yet again. - Thus during commit a I/O buffers should be flushed to the
	 * disk twice; i.e. fdatasync(), FlushFileBuffers() or similar syscall should be called twice for each
	 * commit. This is very expensive for performance, but guaranteed durability even on unexpected system
	 * failure or power outage. Of course, provided that the operating system and the underlying hardware (e.g.
	 * disk) work correctly.
	 *
	 * TRADE-OFF: By skipping some stages described above, you can significantly benefit in speed, while
	 * partially or completely losing in the guarantee of data durability and/or consistency in the event of
	 * system or power failure. Moreover, if for any reason disk write order is not preserved, then at moment of
	 * a system crash, a meta-page with a pointer to the new B-tree may be written to disk, while the itself
	 * B-tree not yet. In that case, the database will be corrupted!
	 *
	 * \see MDBX_SYNC_DURABLE \see MDBX_NOMETASYNC \see MDBX_SAFE_NOSYNC \see MDBX_UTTERLY_NOSYNC
	 *
	 * @{
	 */

	/**
	 * Default robust and durable sync mode.
	 *
	 * Metadata is written and flushed to disk after a data is written and flushed, which guarantees the
	 * integrity of the database in the event of a crash at any time.
	 *
	 * \attention Please do not use other modes until you have studied all the details and are sure. Otherwise,
	 * you may lose your users' data, as happens in [Miranda NG](https://www.miranda-ng.org/) messenger.
	 */

	public static int MDBX_SYNC_DURABLE;

	/**
	 * Don't sync the meta-page after commit.
	 *
	 * Flush system buffers to disk only once per transaction commit, omit the metadata flush. Defer that until
	 * the system flushes files to disk, or next non-\ref MDBX_RDONLY commit or \ref mdbx_env_sync(). Depending
	 * on the platform and hardware, with \ref MDBX_NOMETASYNC you may get a doubling of write performance.
	 *
	 * This trade-off maintains database integrity, but a system crash may undo the last committed transaction.
	 * I.e. it preserves the ACI (atomicity, consistency, isolation) but not D (durability) database property.
	 *
	 * `MDBX_NOMETASYNC` flag may be changed at any time using \ref mdbx_env_set_flags() or by passing to \ref
	 * mdbx_txn_begin() for particular write transaction. \see sync_modes
	 */

	public static int MDBX_NOMETASYNC;

	/**
	 * Don't sync anything but keep previous steady commits.
	 *
	 * Like \ref MDBX_UTTERLY_NOSYNC the `MDBX_SAFE_NOSYNC` flag disable similarly flush system buffers to disk
	 * when committing a transaction. But there is a huge difference in how are recycled the MVCC snapshots
	 * corresponding to previous "steady" transactions (see below).
	 *
	 * With \ref MDBX_WRITEMAP the `MDBX_SAFE_NOSYNC` instructs MDBX to use asynchronous mmap-flushes to disk.
	 * Asynchronous mmap-flushes means that actually all writes will scheduled and performed by operation system
	 * on it own manner, i.e. unordered. MDBX itself just notify operating system that it would be nice to write
	 * data to disk, but no more.
	 *
	 * Depending on the platform and hardware, with `MDBX_SAFE_NOSYNC` you may get a multiple increase of write
	 * performance, even 10 times or more.
	 *
	 * In contrast to \ref MDBX_UTTERLY_NOSYNC mode, with `MDBX_SAFE_NOSYNC` flag MDBX will keeps untouched
	 * pages within B-tree of the last transaction "steady" which was synced to disk completely. This has big
	 * implications for both data durability and (unfortunately) performance: - a system crash can't corrupt the
	 * database, but you will lose the last transactions; because MDBX will rollback to last steady commit since
	 * it kept explicitly. - the last steady transaction makes an effect similar to "long-lived" read
	 * transaction (see above in the \ref restrictions section) since prevents reuse of pages freed by newer
	 * write transactions, thus the any data changes will be placed in newly allocated pages. - to avoid rapid
	 * database growth, the system will sync data and issue a steady commit-point to resume reuse pages, each
	 * time there is insufficient space and before increasing the size of the file on disk.
	 *
	 * In other words, with `MDBX_SAFE_NOSYNC` flag MDBX insures you from the whole database corruption, at the
	 * cost increasing database size and/or number of disk IOPs. So, `MDBX_SAFE_NOSYNC` flag could be used with
	 * \ref mdbx_env_sync() as alternatively for batch committing or nested transaction (in some cases). As
	 * well, auto-sync feature exposed by \ref mdbx_env_set_syncbytes() and \ref mdbx_env_set_syncperiod()
	 * functions could be very useful with `MDBX_SAFE_NOSYNC` flag.
	 *
	 * The number and volume of disk IOPs with MDBX_SAFE_NOSYNC flag will exactly the as without any no-sync
	 * flags. However, you should expect a larger process's [work set](https://bit.ly/2kA2tFX) and significantly
	 * worse a [locality of reference](https://bit.ly/2mbYq2J), due to the more intensive allocation of
	 * previously unused pages and increase the size of the database.
	 *
	 * `MDBX_SAFE_NOSYNC` flag may be changed at any time using \ref mdbx_env_set_flags() or by passing to \ref
	 * mdbx_txn_begin() for particular write transaction.
	 */

	public static int MDBX_SAFE_NOSYNC;

	/**
	 * \deprecated Please use \ref MDBX_SAFE_NOSYNC instead of `MDBX_MAPASYNC`.
	 *
	 * Since version 0.9.x the `MDBX_MAPASYNC` is deprecated and has the same effect as \ref MDBX_SAFE_NOSYNC
	 * with \ref MDBX_WRITEMAP. This just API simplification is for convenience and clarity.
	 */

	public static int MDBX_MAPASYNC;

	/**
	 * Don't sync anything and wipe previous steady commits.
	 *
	 * Don't flush system buffers to disk when committing a transaction. This optimization means a system crash
	 * can corrupt the database, if buffers are not yet flushed to disk. Depending on the platform and hardware,
	 * with `MDBX_UTTERLY_NOSYNC` you may get a multiple increase of write performance, even 100 times or more.
	 *
	 * If the filesystem preserves write order (which is rare and never provided unless explicitly noted) and
	 * the \ref MDBX_WRITEMAP and \ref MDBX_LIFORECLAIM flags are not used, then a system crash can't corrupt
	 * the database, but you can lose the last transactions, if at least one buffer is not yet flushed to disk.
	 * The risk is governed by how often the system flushes dirty buffers to disk and how often \ref
	 * mdbx_env_sync() is called. So, transactions exhibit ACI (atomicity, consistency, isolation) properties
	 * and only lose `D` (durability). I.e. database integrity is maintained, but a system crash may undo the
	 * final transactions.
	 *
	 * Otherwise, if the filesystem not preserves write order (which is typically) or \ref MDBX_WRITEMAP or \ref
	 * MDBX_LIFORECLAIM flags are used, you should expect the corrupted database after a system crash.
	 *
	 * So, most important thing about `MDBX_UTTERLY_NOSYNC`: - a system crash immediately after commit the write
	 * transaction high likely lead to database corruption. - successful completion of mdbx_env_sync(force =
	 * true) after one or more committed transactions guarantees consistency and durability. - BUT by committing
	 * two or more transactions you back database into a weak state, in which a system crash may lead to
	 * database corruption! In case single transaction after mdbx_env_sync, you may lose transaction itself, but
	 * not a whole database.
	 *
	 * Nevertheless, `MDBX_UTTERLY_NOSYNC` provides "weak" durability in case of an application crash (but no
	 * durability on system failure), and therefore may be very useful in scenarios where data durability is not
	 * required over a system failure (e.g for short-lived data), or if you can take such risk.
	 *
	 * `MDBX_UTTERLY_NOSYNC` flag may be changed at any time using \ref mdbx_env_set_flags(), but don't has
	 * effect if passed to \ref mdbx_txn_begin() for particular write transaction. \see sync_modes
	 */

	public static int MDBX_UTTERLY_NOSYNC;

	//====================================================//
	// MDBX_constants (MDBX_constants)
	//====================================================//
	/** The hard limit for DBI handles*/

	public static int MDBX_MAX_DBI; //

	/** The maximum size of a data item.*/

	public static int MDBX_MAXDATASIZE;

	/** The minimal database page size in bytes.*/

	public static int MDBX_MIN_PAGESIZE;

	/** The maximal database page size in bytes. */

	public static int MDBX_MAX_PAGESIZE;

	//====================================================//
	// MDBX Delete mode options (MDBX_env_delete_mode_t)
	//====================================================//
	/** \brief Deletion modes for \ref mdbx_env_delete().
	 * \ingroup c_extra
	 * \see mdbx_env_delete()
	 */

	/**
	 * \brief Just delete the environment's files and directory if any. \note On POSIX systems, processes
	 * already working with the database will continue to work without interference until it close the
	 * environment. \note On Windows, the behavior of `MDBX_ENV_JUST_DELETE` is different because the system
	 * does not support deleting files that are currently memory mapped.
	 */

	public static int MDBX_ENV_JUST_DELETE;

	/**
	 * \brief Make sure that the environment is not being used by other processes, or return an error otherwise.
	 */

	public static int MDBX_ENV_ENSURE_UNUSED;

	/**
	 * \brief Wait until other processes closes the environment before deletion.
	 */

	public static int MDBX_ENV_WAIT_FOR_UNUSED;

	//====================================================//
	// MDBX environment options (MDBX_option_t)
	//====================================================//
	/** Controls the maximum number of named databases for the environment.*/

	public static int MDBX_opt_max_db;

	/** Defines the maximum number of threads/reader slots for all processes interacting with the database. */

	public static int MDBX_opt_max_readers;

	/**
	 * Controls interprocess/shared threshold to force flush the data buffers to disk, if MDBX_SAFE_NOSYNC is
	 * used.
	 */

	public static int MDBX_opt_sync_bytes;

	/**
	 * Controls interprocess/shared relative period since the last unsteady commit to force flush the data
	 * buffers to disk, if MDBX_SAFE_NOSYNC is used.
	 */

	public static int MDBX_opt_sync_period;

	/**
	 * Controls the in-process limit to grow a list of reclaimed/recycled page's numbers for finding a sequence
	 * of contiguous pages for large data items.
	 */

	public static int MDBX_opt_rp_augment_limit;

	/** Controls the in-process limit to grow a cache of dirty pages for reuse in the current transaction.*/

	public static int MDBX_opt_loose_limit;

	/** Controls the in-process limit of a pre-allocated memory items for dirty pages.*/

	public static int MDBX_opt_dp_reserve_limit;

	/** Controls the in-process limit of dirty pages for a write transaction.*/

	public static int MDBX_opt_txn_dp_limit;

	/**
	 * Controls the in-process initial allocation size for dirty pages list of a write transaction. Default is
	 * 1024.
	 */

	public static int MDBX_opt_txn_dp_initial;

	/** Controls the in-process how maximal part of the dirty pages may be spilled when necessary.*/

	public static int MDBX_opt_spill_max_denominator;

	/** Controls the in-process how minimal part of the dirty pages should be spilled when necessary.*/

	public static int MDBX_opt_spill_min_denominator;

	/**
	 * Controls the in-process how much of the parent transaction dirty pages will be spilled while start each
	 * child transaction.
	 */

	public static int MDBX_opt_spill_parent4child_denominator;

	/** Controls the in-process threshold of semi-empty pages merge.*/

	public static int MDBX_opt_merge_threshold_16dot16_percent;

	/**
	 * Controls the choosing between use write-through disk writes and usual ones with followed flush by the
	 * `fdatasync()` syscall..
	 */

	public static int MDBX_opt_writethrough_threshold;

	/**
	 * Controls prevention of page-faults of reclaimed and allocated pages in the MDBX_WRITEMAP mode by clearing
	 * ones through file handle before touching
	 */

	public static int MDBX_opt_prefault_write_enable;

	/**
	 * Controls the in-process spending time limit of searching consecutive pages inside GC. \see
	 * MDBX_opt_rp_augment_limit
	 */

	public static int MDBX_opt_gc_time_limit;

	/**
	 * Controls the choice between striving for uniformity of page filling or reducing the number of changed and
	 * written pages.
	 *
	 * \see MDBX_opt_merge_threshold_16dot16_percent
	 */

	public static int MDBX_opt_prefer_waf_insteadof_balance;

	/**
	 * Specifies in % the maximum size of nested pages used to store a small number of multi-values ​​associated
	 * with a single key.
	 *
	 * Using nested pages, instead of moving values ​​to separate pages of the nested tree, allows you to reduce
	 * the amount of unused space and thereby increase the density of data placement.
	 *
	 * However, as the size of nested pages increases, more leaf pages of the main tree are required, which also
	 * increases the height of the main tree. In addition, changing data on nested pages requires additional
	 * copies, so the cost can be higher in many scenarios.
	 *
	 * min 12.5% (8192), max 100% (65535), default = 100%
	 */

	public static int MDBX_opt_subpage_limit;

	/**
	 * Specifies in % the minimum amount of free space on the main page, in the absence of which nested pages
	 * are moved to a separate tree.
	 *
	 * min 0, max 100% (65535), default = 0
	 */

	public static int MDBX_opt_subpage_room_threshold;

	/**
	 * Specifies in % the minimum amount of free space on the main page, if present, a space reservation is made
	 * in the nested page.
	 *
	 * If there is not enough free space on the main page, the nested page will be of the minimum size. In turn,
	 * if there is no reserve in the nested page, each addition of elements to it will require rebuilding the
	 * main page with the transfer of all data nodes.
	 *
	 * Therefore, reserving space is usually beneficial in scenarios with intensive addition of short
	 * multi-values, for example, when indexing. But it reduces the density of data placement, and accordingly
	 * increases the volume of the database and I/O operations.
	 *
	 * min 0, max 100% (65535), default = 42% (27525)
	 */

	public static int MDBX_opt_subpage_reserve_prereq;

	/**
	 * Specifies the limit in % for reserving space on nested pages.
	 *
	 * min 0, max 100% (65535), default = 4.2% (2753)
	 */

	public static int MDBX_opt_subpage_reserve_limit;

	//====================================================//
	// MDBX warmup options (MDBX_warmup_flags_t)
	//====================================================//
	/** \brief Warming up options
	 * \ingroup c_settings
	 * \anchor warmup_flags
	 * \see mdbx_env_warmup()
	 */

	/**
	 * By default \ref mdbx_env_warmup() just ask OS kernel to asynchronously prefetch database pages.
	 */

	public static int MDBX_warmup_default;

	/**
	 * Peeking all pages of allocated portion of the database to force ones to be loaded into memory. However,
	 * the pages are just peeks sequentially, so unused pages that are in GC will be loaded in the same way as
	 * those that contain payload.
	 */

	public static int MDBX_warmup_force;

	/**
	 * Using system calls to peeks pages instead of directly accessing ones, which at the cost of additional
	 * overhead avoids killing the current process by OOM-killer in a lack of memory condition. \note Has effect
	 * only on POSIX (non-Windows) systems with conjunction to \ref MDBX_warmup_force option.
	 */

	public static int MDBX_warmup_oomsafe;

	/**
	 * Try to lock database pages in memory by `mlock()` on POSIX-systems or `VirtualLock()` on Windows. Please
	 * refer to description of these functions for reasonability of such locking and the information of effects,
	 * including the system as a whole.
	 *
	 * Such locking in memory requires that the corresponding resource limits (e.g. `RLIMIT_RSS`,
	 * `RLIMIT_MEMLOCK` or process working set size) and the availability of system RAM are sufficiently high.
	 *
	 * On successful, all currently allocated pages, both unused in GC and containing payload, will be locked in
	 * memory until the environment closes, or explicitly unblocked by using \ref MDBX_warmup_release, or the
	 * database geometry will changed, including its auto-shrinking.
	 */

	public static int MDBX_warmup_lock;

	/**
	 * Alters corresponding current resource limits to be enough for lock pages by \ref MDBX_warmup_lock.
	 * However, this option should be used in simpler applications since takes into account only current size of
	 * this environment disregarding all other factors. For real-world database application you will need
	 * full-fledged management of resources and their limits with respective engineering.
	 */

	public static int MDBX_warmup_touchlimit;

	/** Release the lock that was performed before by \ref MDBX_warmup_lock. */

	public static int MDBX_warmup_release;

	//====================================================//
	// MDBX check flags (MDBX_chk_flags_t)
	// Flags/options for checking database integrity.
	// Note: This API is not yet fixed, in future versions there may be minor improvements and changes.
	// see mdbx_env_chk()
	//====================================================//
	/** Default verification mode, including read-only mode.*/

	public static int MDBX_CHK_DEFAULTS;

	/** Checking in read-write mode, with lock acquisition and suspension of writing transactions. */

	public static int MDBX_CHK_READWRITE;

	/** Skip page tree traversal.*/

	public static int MDBX_CHK_SKIP_BTREE_TRAVERSAL;

	/** Skip scanning key-value records. */

	public static int MDBX_CHK_SKIP_KV_TRAVERSAL;

	/**
	 * Ignore key and record order. Note: Required when checking legacy DBs created using custom (user-defined)
	 * key or value comparison functions.
	 */

	public static int MDBX_CHK_IGNORE_ORDER;

	//====================================================//
	// MDBX check severity (MDBX_chk_severity_t)
	// Logging/detail levels of information provided via database integrity check callbacks.
	// see mdbx_env_chk()
	//====================================================//

	public static int MDBX_chk_severity_prio_shift;

	public static int MDBX_chk_severity_kind_mask;

	public static int MDBX_chk_fatal;

	public static int MDBX_chk_error;

	public static int MDBX_chk_warning;

	public static int MDBX_chk_notice;

	public static int MDBX_chk_result;

	public static int MDBX_chk_resolution;

	public static int MDBX_chk_processing;

	public static int MDBX_chk_info;

	public static int MDBX_chk_verbose;

	public static int MDBX_chk_details;

	public static int MDBX_chk_extra;

	//====================================================//
	// MDBX check stages (MDBX_chk_stage_t)
	// Check stages reported via callbacks when checking database integrity.
	// see mdbx_env_chk()
	//====================================================//

	public static int MDBX_chk_none;

	public static int MDBX_chk_init;

	public static int MDBX_chk_lock;

	public static int MDBX_chk_meta;

	public static int MDBX_chk_tree;

	public static int MDBX_chk_gc;

	public static int MDBX_chk_space;

	public static int MDBX_chk_maindb;

	public static int MDBX_chk_tables;

	public static int MDBX_chk_conclude;

	public static int MDBX_chk_unlock;

	public static int MDBX_chk_finalize;

	//====================================================//
	// MDBX Check Context (MDBX_chk_context_t)
	//====================================================//
//	typedef struct MDBX_chk_context {
//	  struct MDBX_chk_internal *internal;
//	  MDBX_env *env;
//	  MDBX_txn *txn;
//	  MDBX_chk_scope_t *scope;
//	  uint8_t scope_nesting;
//	  struct {
//	    size_t total_payload_bytes;
//	    size_t table_total, table_processed;
//	    size_t total_unused_bytes, unused_pages;
//	    size_t processed_pages, reclaimable_pages, gc_pages, alloc_pages, backed_pages;
//	    size_t problems_meta, tree_problems, gc_tree_problems, kv_tree_problems, problems_gc, problems_kv, total_problems;
//	    uint64_t steady_txnid, recent_txnid;
//	    /** Указатель на массив размером table_total с указателями на экземпляры
//	     * структур MDBX_chk_table_t с информацией о всех таблицах ключ-значение,
//	     * включая MainDB и GC/FreeDB. */
//	    const MDBX_chk_table_t *const *tables;
//	  } result;
//	} MDBX_chk_context_t;

	//====================================================//
	// MDBX Check Line (MDBX_chk_line_t)
	//====================================================//
	/**
	 *  \brief Virtual string of the report generated when checking the integrity of the database data.
	 *  \see mdbx_env_chk()
	*/

	//====================================================//
	// MDBX Log Level (MDBX_log_level_t)
	//====================================================//

	public static int MDBX_LOG_FATAL;

	public static int MDBX_LOG_ERROR;

	public static int MDBX_LOG_WARN;

	public static int MDBX_LOG_NOTICE;

	public static int MDBX_LOG_VERBOSE;

	public static int MDBX_LOG_DEBUG;

	public static int MDBX_LOG_TRACE;

	public static int MDBX_LOG_EXTRA;

	public static int MDBX_LOG_DONTCHANGE;

	//====================================================//
	// MDBX debug flags (MDBX_debug_flags_t)
	// Runtime debug flags
	//====================================================//

	public static int MDBX_DBG_NONE;

	/** Enable assertion checks.
   * \note Always enabled for builds with `MDBX_FORCE_ASSERTIONS` option,
   * otherwise requires build with \ref MDBX_DEBUG > 0
   */

	public static int MDBX_DBG_ASSERT;

	/** Enable pages usage audit at commit transactions.
   * \note Requires build with \ref MDBX_DEBUG > 0
   */

	public static int MDBX_DBG_AUDIT;

	/** Enable small random delays in critical points.
   * \note Requires build with \ref MDBX_DEBUG > 0
   */

	public static int MDBX_DBG_JITTER;

	/** Include or not meta-pages in coredump files.
   * \note May affect performance in \ref MDBX_WRITEMAP mode
   */

	public static int MDBX_DBG_DUMP;

	/** Allow multi-opening environment(s) */

	public static int MDBX_DBG_LEGACY_MULTIOPEN;

	/** Allow read and write transactions overlapping for the same thread. */

	public static int MDBX_DBG_LEGACY_OVERLAP;

	/** Don't auto-upgrade format signature.
   * \note However a new write transactions will use and store
   * the last signature regardless this flag
   */

	public static int MDBX_DBG_DONT_UPGRADE;

	/** for mdbx_setup_debug() only: Don't change current settings */

	public static int MDBX_DBG_DONTCHANGE;

	// ====================================================//
	// Database Flags (MDBX_db_flags_t)
	// ====================================================//
	/** Variable length unique keys with usual byte-by-byte string comparison. */

	public static int MDBX_DB_DEFAULTS;

	/** Use reverse string comparison for keys. */

	public static int MDBX_REVERSEKEY;

	/** Use sorted duplicates, i.e. allow multi-values for a keys. */

	public static int MDBX_DUPSORT;

	/**
	 * Numeric keys in native byte order either uint32_t or uint64_t. The keys must all be of the same size and
	 * must be aligned while passing as arguments.
	 */

	public static int MDBX_INTEGERKEY;

	/** With MDBX_DUPSORT; sorted dup items have fixed size. The data values must all be of the same size. */

	public static int MDBX_DUPFIXED;

	/**
	 * With MDBX_DUPSORT and with MDBX_DUPFIXED; dups are fixed size like MDBX_INTEGERKEY -style integers. The
	 * data values must all be of the same size and must be aligned while passing as arguments.
	 */

	public static int MDBX_INTEGERDUP;

	/** With MDBX_DUPSORT; use reverse string comparison for data values. */

	public static int MDBX_REVERSEDUP;

	/** Create DB if not already existing. */

	public static int MDBX_CREATE;

	/**
	 * Opens an existing sub-database created with unknown flags.
	 *
	 * The MDBX_DB_ACCEDE flag is intend to open a existing sub-database which was created with unknown flags
	 * (MDBX_REVERSEKEY, MDBX_DUPSORT, MDBX_INTEGERKEY, MDBX_DUPFIXED, MDBX_INTEGERDUP and MDBX_REVERSEDUP).
	 *
	 * In such cases, instead of returning the MDBX_INCOMPATIBLE error, the sub-database will be opened with
	 * flags which it was created, and then an application could determine the actual flags by mdbx_dbi_flags().
	 */

	public static int MDBX_DB_ACCEDE;

	// ====================================================//
	// Transaction Flags (MDBX_txn_flags_t)
	// ====================================================//

	public static int MDBX_TXN_READWRITE;

	public static int MDBX_TXN_RDONLY;

	public static int MDBX_TXN_RDONLY_PREPARE;

	public static int MDBX_TXN_TRY;

	public static int MDBX_TXN_NOMETASYNC;

	public static int MDBX_TXN_NOSYNC;

	public static int MDBX_TXN_INVALID;

	public static int MDBX_TXN_FINISHED;

	public static int MDBX_TXN_ERROR;

	/**
	 * Transaction must write, even if dirty list is empty. \note This is a transaction state flag. Returned
	 * from \ref mdbx_txn_flags() but can't be used with \ref mdbx_txn_begin().
	 */

	public static int MDBX_TXN_DIRTY;

	/**
	 * Transaction or a parent has spilled pages. \note This is a transaction state flag. Returned from \ref
	 * mdbx_txn_flags() but can't be used with \ref mdbx_txn_begin().
	 */

	public static int MDBX_TXN_SPILLS;

	/**
	 * Transaction has a nested child transaction. \note This is a transaction state flag. Returned from \ref
	 * mdbx_txn_flags() but can't be used with \ref mdbx_txn_begin().
	 */

	public static int MDBX_TXN_HAS_CHILD;

	/**
	 * Transaction is parked by mdbx_txn_park(). \note This is a transaction state flag. Returned from
	 * mdbx_txn_flags() but can't be used with mdbx_txn_begin().
	 */

	public static int MDBX_TXN_PARKED;

	/**
	 * Transaction is parked by mdbx_txn_park() with `autounpark=true`, and therefore it can be used without
	 * explicitly calling mdbx_txn_unpark() first. \note This is a transaction state flag. Returned from
	 * mdbx_txn_flags() but can't be used with mdbx_txn_begin().
	 */

	public static int MDBX_TXN_AUTOUNPARK;

	/**
	 * The transaction was blocked using the mdbx_txn_park() function, and then ousted by a write
	 * transaction because this transaction was interfered with garbage recycling. \note This is a transaction
	 * state flag. Returned from mdbx_txn_flags() but can't be used with mdbx_txn_begin().
	 */

	public static int MDBX_TXN_OUSTED;

	/**
	 * Most operations on the transaction are currently illegal. \note This is a transaction state flag.
	 * Returned from mdbx_txn_flags() but can't be used with mdbx_txn_begin().
	 */

	public static int MDBX_TXN_BLOCKED;

	// ====================================================//
	// DBI State Flags (MDBX_dbi_state_t)
	// ====================================================//

	public static int MDBX_DBI_DIRTY; //DB was written in this txn

	public static int MDBX_DBI_STALE; //Named-DB record is older than txnID

	public static int MDBX_DBI_FRESH; //Named-DB handle opened in this txn

	public static int MDBX_DBI_CREAT; //Named-DB handle created in this txn

	// ====================================================//
	// Copy Flags (MDBX_copy_flags_t)
	// ====================================================//

	public static int MDBX_CP_DEFAULTS;

	/** Copy with compactification: Omit free space from copy and renumber all pages sequentially */

	public static int MDBX_CP_COMPACT; //

	/** Force to make resizeable copy, i.e. dynamic size instead of fixed */

	public static int MDBX_CP_FORCE_DYNAMIC_SIZE;

	/** Don't explicitly flush the written data to an output media */

	public static int MDBX_CP_DONT_FLUSH;

	/** Use read transaction parking during copying MVCC-snapshot \see mdbx_txn_park() */

	public static int MDBX_CP_THROTTLE_MVCC;

	/** Abort/dispose passed transaction after copy mdbx_txn_copy2fd() \see mdbx_txn_copy2pathname() */

	public static int MDBX_CP_DISPOSE_TXN;

	/**
	 * Enable renew/restart read transaction in case it use outdated MVCC shapshot, otherwise the
	 * MDBX_MVCC_RETARDED will be returned \see mdbx_txn_copy2fd() \see mdbx_txn_copy2pathname()
	 */

	public static int MDBX_CP_RENEW_TXN;

	// ====================================================//
	// Write Flags (MDBX_put_flags_t)
	// ====================================================//
	/** Upsertion by default (without any other flags) */

	public static int MDBX_UPSERT;

	/** For insertion: Don't write if the key already exists. */

	public static int MDBX_NOOVERWRITE;

	/** Has effect only for \ref MDBX_DUPSORT tables.
	 * For upsertion: don't write if the key-value pair already exist.
	 */

	public static int MDBX_NODUPDATA;

	/** For upsertion: overwrite the current key/data pair.
	 * MDBX allows this flag for \ref mdbx_put() for explicit overwrite/update
	 * without insertion.
	 * For deletion: remove only single entry at the current cursor position.
	 */

	public static int MDBX_CURRENT;

	/**
	 * Has effect only for \ref MDBX_DUPSORT tables. For deletion: remove all multi-values (aka duplicates) for
	 * given key. For upsertion: replace all multi-values for given key with a new one.
	 */

	public static int MDBX_ALLDUPS;

	/** For upsertion: Just reserve space for data, don't copy it.
	 * Return a pointer to the reserved space.
	 */

	public static int MDBX_RESERVE;

	/** Data is being appended.
	 * Don't split full pages, continue on a new instead.
	 */

	public static int MDBX_APPEND;

	/** Has effect only for \ref MDBX_DUPSORT tables.
	 * Duplicate data is being appended.
	 * Don't split full pages, continue on a new instead.
	 */

	public static int MDBX_APPENDDUP;

	/** Only for \ref MDBX_DUPFIXED.
	 * Store multiple data items in one call.
	 */

	public static int MDBX_MULTIPLE;

	// ====================================================//
	// Cursor Flags (MDBX_cursor_op)
	// ====================================================//

	public static int MDBX_FIRST;

	public static int MDBX_FIRST_DUP;

	public static int MDBX_GET_BOTH;

	public static int MDBX_GET_BOTH_RANGE;

	public static int MDBX_GET_CURRENT;

	public static int MDBX_GET_MULTIPLE;

	public static int MDBX_LAST;

	public static int MDBX_LAST_DUP;

	public static int MDBX_NEXT;

	public static int MDBX_NEXT_DUP;

	public static int MDBX_NEXT_MULTIPLE;

	public static int MDBX_NEXT_NODUP;

	public static int MDBX_PREV;

	public static int MDBX_PREV_DUP;

	public static int MDBX_PREV_NODUP;

	public static int MDBX_SET;

	public static int MDBX_SET_KEY;

	public static int MDBX_SET_RANGE;

	public static int MDBX_PREV_MULTIPLE;

	public static int MDBX_SET_LOWERBOUND;

	public static int MDBX_SET_UPPERBOUND;

	public static int MDBX_TO_KEY_LESSER_THAN;

	public static int MDBX_TO_KEY_LESSER_OR_EQUAL;

	public static int MDBX_TO_KEY_EQUAL;

	public static int MDBX_TO_KEY_GREATER_OR_EQUAL;

	public static int MDBX_TO_KEY_GREATER_THAN;

	public static int MDBX_TO_EXACT_KEY_VALUE_LESSER_THAN;

	public static int MDBX_TO_EXACT_KEY_VALUE_LESSER_OR_EQUAL;

	public static int MDBX_TO_EXACT_KEY_VALUE_EQUAL;

	public static int MDBX_TO_EXACT_KEY_VALUE_GREATER_OR_EQUAL;

	public static int MDBX_TO_EXACT_KEY_VALUE_GREATER_THAN;

	public static int MDBX_TO_PAIR_LESSER_THAN;

	public static int MDBX_TO_PAIR_LESSER_OR_EQUAL;

	public static int MDBX_TO_PAIR_EQUAL;

	public static int MDBX_TO_PAIR_GREATER_OR_EQUAL;

	public static int MDBX_TO_PAIR_GREATER_THAN;

	public static int MDBX_SEEK_AND_GET_MULTIPLE;

	// ====================================================//
	// Return Codes (no enum)
	// ====================================================//

	public static int MDBX_ENODATA;

	public static int MDBX_EINVAL;

	public static int MDBX_EACCESS;

	public static int MDBX_ENOMEM;

	public static int MDBX_EROFS;

	public static int MDBX_ENOSYS;

	public static int MDBX_EIO;

	public static int MDBX_EPERM;

	public static int MDBX_EINTR;

	public static int MDBX_EEXIST;

	public static int MDBX_ENOFILE;

	public static int MDBX_EREMOTE;

	public static int MDBX_EDEADLK;

	public static int MDBX_SUCCESS;

	public static int MDBX_RESULT_FALSE;

	public static int MDBX_RESULT_TRUE;

	public static int MDBX_KEYEXIST;

	public static int MDBX_FIRST_LMDB_ERRCODE;

	public static int MDBX_NOTFOUND;

	public static int MDBX_PAGE_NOTFOUND;

	public static int MDBX_CORRUPTED;

	public static int MDBX_PANIC;

	public static int MDBX_VERSION_MISMATCH;

	public static int MDBX_INVALID;

	public static int MDBX_MAP_FULL;

	public static int MDBX_DBS_FULL;

	public static int MDBX_READERS_FULL;

	public static int MDBX_TXN_FULL;

	public static int MDBX_CURSOR_FULL;

	public static int MDBX_PAGE_FULL;

	public static int MDBX_UNABLE_EXTEND_MAPSIZE;

	public static int MDBX_INCOMPATIBLE;

	public static int MDBX_BAD_RSLOT;

	public static int MDBX_BAD_TXN;

	public static int MDBX_BAD_VALSIZE;

	public static int MDBX_BAD_DBI;

	public static int MDBX_PROBLEM;

	public static int MDBX_LAST_LMDB_ERRCODE;

	public static int MDBX_BUSY;

	public static int MDBX_FIRST_ADDED_ERRCODE;

	public static int MDBX_EMULTIVAL;

	public static int MDBX_EBADSIGN;

	public static int MDBX_WANNA_RECOVERY;

	public static int MDBX_EKEYMISMATCH;

	public static int MDBX_TOO_LARGE;

	public static int MDBX_THREAD_MISMATCH;

	public static int MDBX_TXN_OVERLAPPING;

	public static int MDBX_BACKLOG_DEPLETED;

	public static int MDBX_DUPLICATED_CLK;

	public static int MDBX_DANGLING_DBI;

	public static int MDBX_OUSTED;

	public static int MDBX_MVCC_RETARDED;

	public static int MDBX_LAST_ADDED_ERRCODE;

//
//	public static class MiGeo {
//
//		public long lower;		/* lower limit for datafile size */
//
//		public long upper;  	/* upper limit for datafile size */
//
//		public long current; 	/* current datafile size */
//
//		public long shrink;  	/* shrink treshold for datafile */
//
//		public long grow;    	/* growth step for datafile */
//
//		@SuppressWarnings("nls")
//		@Override
//		public String toString() {
//			return "{" +
//					"lower=" + lower +
//					", upper=" + upper +
//					", current=" + current +
//					", shrink=" + shrink +
//					", grow=" + grow +
//					'}';
//		}
//	}

	// ====================================================//
	// Environment Info
	// ====================================================//

	public static class MDBX_envinfo {
//		public MiGeo mi_geo = new MiGeo();

		public long mi_geo_lower;		/* lower limit for datafile size */

		public long mi_geo_upper;		/* upper limit for datafile size */

		public long mi_geo_current;		/* current datafile size */

		public long mi_geo_shrink;		/* shrink treshold for datafile */

		public long mi_geo_grow;			/* growth step for datafile */

		public long mi_mapsize;							/* Size of the data memory map */

		public long mi_dxb_fsize;						/* Current database file size */

		public long mi_dxb_fallocated;					/* Space allocated for the database file */

		public long mi_last_pgno;						/* ID of the last used page */

		public long mi_recent_txnid;					/* ID of the last committed transaction */

		public long mi_latter_reader_txnid; 	/* ID of the last reader transaction */

		public long mi_self_latter_reader_txnid; /* ID of the last reader transaction of caller process */

		public long mi_meta0_txnid;

		public long mi_meta1_txnid;

		public long mi_meta2_txnid;

		public long mi_meta0_sign;

		public long mi_meta1_sign;

		public long mi_meta2_sign;


		public long mi_maxreaders; /* max reader slots in the environment */

		public long mi_numreaders; /* max reader slots used in the environment */

		public long mi_dxb_pagesize; /* database pagesize */

		public long mi_sys_pagesize; /* system pagesize */

		public long mi_sys_upcblk; /* system unified page-cache block size */

		public long mi_sys_ioblk; /* filesystem I/O block size */

		public long mi_bootid_current_x;

		public long mi_bootid_current_y;

		public long mi_bootid_meta0_x;

		public long mi_bootid_meta0_y;

		public long mi_bootid_meta1_x;

		public long mi_bootid_meta1_y;

		public long mi_bootid_meta2_x;

		public long mi_bootid_meta2_y;


		public long mi_unsync_volume;

		public long mi_autosync_threshold;

		public long mi_since_sync_seconds16dot16;

		public long mi_autosync_period_seconds16dot16;

		public long mi_since_reader_check_seconds16dot16;

		public long mi_mode;

		public long mi_pgop_stat_newly;

		public long mi_pgop_stat_cow;

		public long mi_pgop_stat_clone;

		public long mi_pgop_stat_split;

		public long mi_pgop_stat_merge;

		public long mi_pgop_stat_spill;

		public long mi_pgop_stat_unspill;

		public long mi_pgop_stat_wops;

		public long mi_pgop_stat_prefault;

		public long mi_pgop_stat_mincore;

		public long mi_pgop_stat_msync;

		public long mi_pgop_stat_fsync;

		public long mi_dxbid_x;

		public long mi_dxbid_y;

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" + //$NON-NLS-1$
					"mi_geo_lower=" + mi_geo_lower +
					", mi_geo_upper=" + mi_geo_upper +
					", mi_geo_current=" + mi_geo_current +
					", mi_geo_shrink=" + mi_geo_shrink +
					", mi_geo_grow=" + mi_geo_grow +
					", mi_mapsize=" + mi_mapsize +
					", mi_dxb_fsize=" + mi_dxb_fsize +
					", mi_dxb_fallocated=" + mi_dxb_fallocated +
					", mi_last_pgno=" + mi_last_pgno +
					", mi_recent_txnid=" + mi_recent_txnid +
					", mi_latter_reader_txnid=" + mi_latter_reader_txnid +
					", mi_self_latter_reader_txnid=" + mi_self_latter_reader_txnid +
					", mi_meta0_txnid=" + mi_meta0_txnid +
					", mi_meta0_sign=" + mi_meta0_sign +
					", mi_meta1_txnid=" + mi_meta1_txnid +
					", mi_meta1_sign=" + mi_meta1_sign +
					", mi_meta2_txnid=" + mi_meta2_txnid +
					", mi_meta2_sign=" + mi_meta2_sign +

					", mi_maxreaders=" + mi_maxreaders +
					", mi_numreaders=" + mi_numreaders +
					", mi_dxb_pagesize=" + mi_dxb_pagesize +
					", mi_sys_pagesize=" + mi_sys_pagesize +
					", mi_sys_upcblk=" + mi_sys_upcblk +
					", mi_sys_ioblk=" + mi_sys_ioblk +
					", mi_bootid_current_x=" + mi_bootid_current_x +
					", mi_bootid_current_y=" + mi_bootid_current_y +

					", mi_bootid_meta0_x=" + mi_bootid_meta0_x +
					", mi_bootid_meta0_y=" + mi_bootid_meta0_y +
					", mi_bootid_meta1_x=" + mi_bootid_meta1_x +
					", mi_bootid_meta1_y=" + mi_bootid_meta1_y +
					", mi_bootid_meta2_x=" + mi_bootid_meta2_x +
					", mi_bootid_meta2_y=" + mi_bootid_meta2_y +

					", mi_unsync_volume=" + mi_unsync_volume +
					", mi_autosync_threshold=" + mi_autosync_threshold +
					", mi_since_sync_seconds16dot16=" + mi_since_sync_seconds16dot16 +
					", mi_autosync_period_seconds16dot16=" + mi_autosync_period_seconds16dot16 +
					", mi_since_reader_check_seconds16dot16=" + mi_since_reader_check_seconds16dot16 +
					", mi_mode=" + mi_mode +
					", mi_pgop_stat_newly=" + mi_pgop_stat_newly +
					", mi_pgop_stat_cow=" + mi_pgop_stat_cow +
					", mi_pgop_stat_clone=" + mi_pgop_stat_clone +
					", mi_pgop_stat_split=" + mi_pgop_stat_split +
					", mi_pgop_stat_merge=" + mi_pgop_stat_merge +
					", mi_pgop_stat_spill=" + mi_pgop_stat_spill +
					", mi_pgop_stat_unspill=" + mi_pgop_stat_unspill +
					", mi_pgop_stat_wops=" + mi_pgop_stat_wops +
					", mi_pgop_stat_prefault=" + mi_pgop_stat_prefault +
					", mi_pgop_stat_mincore=" + mi_pgop_stat_mincore +
					", mi_pgop_stat_msync=" + mi_pgop_stat_msync +
					", mi_pgop_stat_fsync=" + mi_pgop_stat_fsync +
					", mi_dxbid_x=" + mi_dxbid_x +
					", mi_dxbid_y=" + mi_dxbid_y +
					'}';
		}
	}

	// ====================================================//
	// Stats Info
	// ====================================================//

	public static class MDBX_stat {
		public long ms_psize;
		public long ms_depth;
		public long ms_branch_pages;
		public long ms_leaf_pages;
		public long ms_overflow_pages;
		public long ms_entries;
		public long ms_mod_txnid;

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
					"ms_branch_pages=" + ms_branch_pages +
					", ms_psize=" + ms_psize +
					", ms_depth=" + ms_depth +
					", ms_leaf_pages=" + ms_leaf_pages +
					", ms_overflow_pages=" + ms_overflow_pages +
					", ms_entries=" + ms_entries +
					", ms_mod_txnid=" + ms_mod_txnid +
					'}';
		}
	}


	public static int SIZEOF_ENVINFO;

	public static int SIZEOF_STAT;

	//====================================================//
	// Global Extern constant
	//====================================================//
//
//	public static MDBX_build_info mdbx_build;

//
//
//			public static MDBX_version_info mdbx_version;


	// ====================================================//
	// Value classes
	// ====================================================//

	public static class MDBX_val {
		public long iov_base;
		public long iov_len;
	}

	/** In/out cache entry for {@code mdbx_cache_get}. */
	public static class MDBX_cache_entry {
		public long trunk_txnid;
		public long last_confirmed_txnid;
		public long offset;
		public int length;
	}

	/** Pair returned by value from {@code mdbx_cache_get}. */
	public static class MDBX_cache_result {
		public int errcode;
		public int status;
	}

	/** Histogram carried inside {@link MDBX_gc_info}. */
	public static class MDBX_chk_histogram {
		public long amount;
		public long count;
		public long le1_amount;
		public long le1_count;
		public long[] range_begin = new long[9];
		public long[] range_end = new long[9];
		public long[] range_amount = new long[9];
		public long[] range_count = new long[9];
	}

	/** Summary filled by {@code mdbx_gc_info}. */
	public static class MDBX_gc_info {
		public long pages_total;
		public long pages_backed;
		public long pages_allocated;
		public long pages_gc;
		public long gc_reclaimable_pages;
		public MDBX_chk_histogram gc_reclaimable_span_histogram = new MDBX_chk_histogram();
		public MDBX_chk_histogram gc_reclaimable_pgno_distribution = new MDBX_chk_histogram();
		public long max_reader_lag;
		public long max_retained_pages;
	}

	/** Counters filled by {@code mdbx_env_defrag}. */
	public static class MDBX_defrag_result {
		public long pages_shrinked;
		public long pages_moved;
		public long pages_scheduled;
		public long pages_retained;
		public long pages_left;
		public long pages_whole;
		public long obstructed_pgno;
		public long obstructed_span;
		public long obstructed_txnid;
		public long obstructor_tid;
		public long obstructor_pid;
		public long rough_estimation_cycle_progress_permille;
		public long cycles;
		public long stopping_reasons;
		public long spent_time_dot16;
	}

	public static final void map_val( long in, MDBX_val out) {
		FfmDispatch.map_val(in, out);
	}

//
//	public static final int get_mdbx_build_info(long arg, long bytes) {
//		return FfmDispatch.get_mdbx_build_info(arg, bytes);
//	}

//
//	public static final int mdbx_build() {
//		return FfmDispatch.mdbx_build();
//	}

	// ====================================================//
	// Debug methods
	// ====================================================//

	public static final long map_printf( long buf, int size, long fmt, long args) {
		return FfmDispatch.map_printf(buf, size, fmt, args);
	}

//
//	public static final void debug_func(int logLevel, long function, long line, long fmt, long args) {
//		FfmDispatch.debug_func(logLevel, function, line, fmt, args);
//	}

	/**
	 * Setup global log-level, debug options and debug logger.
	 * @param log_level log level
	 * @param debug_flags debug flags
	 * @param logger logger
	 * @return The previously `debug_flags` in the 0-15 bits and `log_level` in the 16-31 bits.
	 */

	public static final int mdbx_setup_debug( int log_level, int debug_flags, long logger) {
		return FfmDispatch.mdbx_setup_debug(log_level, debug_flags, logger);
	}

	/**
	 * Setup global log-level, debug options and debug logger.
	 * @param log_level log level
	 * @param debug_flags debug flags
	 * @param logger logger
	 * @param logger_buffer logger buffer
	 * @param logger_buffer_size buffer size
	 * @return The previously `debug_flags` in the 0-15 bits and `log_level` in the 16-31 bits.
	 */

	public static final int mdbx_setup_debug_nofmt( int log_level, int debug_flags, long logger, long logger_buffer, long logger_buffer_size) {
		return FfmDispatch.mdbx_setup_debug_nofmt(log_level, debug_flags, logger, logger_buffer, logger_buffer_size);
	}

	// ====================================================//
	// Error methods
	// ====================================================//
	/**
	 * Return a string describing a given error code.
	 * \ingroup c_err
	 *
	 * This function is a superset of the ANSI C X3.159-1989 (ANSI C) `strerror()`
	 * function. If the error code is greater than or equal to 0, then the string
	 * returned by the system function `strerror()` is returned. If the error code
	 * is less than 0, an error string corresponding to the MDBX library error is
	 * returned. See errors for a list of MDBX-specific error codes.
	 *
	 * `mdbx_strerror()` is NOT thread-safe because may share common internal buffer
	 * for system messages. The returned string must NOT be modified by the
	 * application, but MAY be modified by a subsequent call to
	 * \ref mdbx_strerror(), `strerror()` and other related functions.
	 * \see mdbx_strerror_r()
	 *
	 * @param errnum  [in] The error code.
	 *
	 * @return "error message" The description of the error.
	 */

	public static final long mdbx_strerror(int errnum) {
		return FfmDispatch.mdbx_strerror(errnum);
	}

	/**
	 * Return a string describing a given error code.
	 * \ingroup c_err
	 *
	 * This function is a superset of the ANSI C X3.159-1989 (ANSI C) `strerror()`
	 * function. If the error code is greater than or equal to 0, then the string
	 * returned by the system function `strerror()` is returned. If the error code
	 * is less than 0, an error string corresponding to the MDBX library error is
	 * returned. See errors for a list of MDBX-specific error codes.
	 *
	 * `mdbx_strerror_r()` is thread-safe since uses user-supplied buffer where
	 * appropriate. The returned string must NOT be modified by the application,
	 * since it may be pointer to internal constant string. However, there is no
	 * restriction if the returned string points to the supplied buffer.
	 * \see mdbx_strerror()
	 *
	 * mdbx_liberr2str() returns string describing only MDBX error numbers but NULL
	 * for non-MDBX error codes. This function is thread-safe since return pointer
	 * to constant non-localized strings.
	 *
	 * @param errnum  [in] The error code.
	 * @param buf [in,out] Buffer to store the error message.
	 * @param buflen [in] The size of buffer to store the message.
	 *
	 * @return "error message" The description of the error.
	 */

	public static final long mdbx_strerror_r(int errnum, long buf, long buflen) {
		return FfmDispatch.mdbx_strerror_r(errnum, buf, buflen);
	}

	/**
	 * Bit of Windows' madness. The similar to \ref mdbx_strerror() but returns Windows error-messages in the
	 * OEM-encoding for console utilities.
	 * \ingroup c_err
	 *
	 * @see JNI#mdbx_strerror_r_ANSI2OEM(int, long, long)
	 *
	 * @param errnum Error number
	 * @return "error message" The description of the error.
	 */

	public static final long mdbx_strerror_ANSI2OEM(int errnum) {
		return FfmDispatch.mdbx_strerror_ANSI2OEM(errnum);
	}

	/**
	 * Bit of Windows' madness. The similar to \ref mdbx_strerror_r() but returns Windows error-messages in the
	 * OEM-encoding for console utilities.
	 * \ingroup c_err
	 * @see JNI#mdbx_strerror_r_ANSI2OEM(int, long, long)

	 * @param errnum Error number
	 * @param buf buffer
	 * @param buflen buffer length
	 * @return "error message" The description of the error.
	 */

	public static final long mdbx_strerror_r_ANSI2OEM(int errnum, long buf, long buflen) {
		return FfmDispatch.mdbx_strerror_r_ANSI2OEM(errnum, buf, buflen);
	}

	/**
	 * Sets a Handle-Slow-Readers callback to resolve database full/overflow
	 * issue due to a reader(s) which prevents the old data from being recycled.
	 * \ingroup c_err
	 *
	 * The callback will only be triggered when the database is full due to a
	 * reader(s) prevents the old data from being recycled.
	 *
	 * see JNI#MDBX_hsr_func
	 * see JNI#mdbx_env_get_hsr()
	 * @see JNI#mdbx_txn_park(long, int)
	 * @see <a href="intro.html#long-lived-read">Long-lived read transactions</a>
	 *
	 * @param env             [in] An environment handle returned by \ref mdbx_env_create().
	 * @param hsr_callback    [in] A \ref MDBX_hsr_func function or NULL to disable.
	 *
	 * @return A non-zero error value on failure and 0 on success.
	 * */

	public static final int mdbx_env_set_hsr( long env, long hsr_callback) {
		return FfmDispatch.mdbx_env_set_hsr(env, hsr_callback);
	}

	//====================================================//
	// ENV methods
	//====================================================//

	public static final int mdbx_env_create( long[] penv) {
		return FfmDispatch.mdbx_env_create(penv);
	}

	/**
	 * Open an environment instance. \ingroup c_opening
	 *
	 * Indifferently this function will fails or not, the \ref mdbx_env_close() must be called later to discard
	 * the \ref MDBX_env handle and release associated resources.
	 *
	 * \note On Windows the \ref mdbx_env_openW() is recommended to use.
	 *
	 * @param env [in] An environment handle returned by \ref mdbx_env_create()
	 *
	 * @param pathname [in] The pathname for the database or the directory in which the database files reside.
	 * In the case of directory it must already exist and be writable.
	 *
	 * @param flags [in] Specifies options for this environment. This parameter must be bitwise OR'ing together
	 * any constants described above in the \ref env_flags and \ref sync_modes sections.
	 *
	 * Flags set by mdbx_env_set_flags() are also used: - \ref MDBX_ENV_DEFAULTS, \ref MDBX_NOSUBDIR, \ref
	 * MDBX_RDONLY, \ref MDBX_EXCLUSIVE, \ref MDBX_WRITEMAP, \ref MDBX_NOSTICKYTHREADS, \ref MDBX_NORDAHEAD,
	 * \ref MDBX_NOMEMINIT, \ref MDBX_COALESCE, \ref MDBX_LIFORECLAIM. See \ref env_flags section.
	 *
	 * - \ref MDBX_SYNC_DURABLE, \ref MDBX_NOMETASYNC, \ref MDBX_SAFE_NOSYNC, \ref MDBX_UTTERLY_NOSYNC. See \ref
	 * sync_modes section.
	 *
	 * \note `MDB_NOLOCK` flag don't supported by MDBX, try use \ref MDBX_EXCLUSIVE as a replacement.
	 *
	 * \note MDBX don't allow to mix processes with different \ref MDBX_SAFE_NOSYNC flags on the same
	 * environment. In such case \ref MDBX_INCOMPATIBLE will be returned.
	 *
	 * If the database is already exist and parameters specified early by \ref mdbx_env_set_geometry() are
	 * incompatible (i.e. for instance, different page size) then \ref mdbx_env_open() will return \ref
	 * MDBX_INCOMPATIBLE error.
	 *
	 * @param mode [in] The UNIX permissions to set on created files. Zero value means to open existing, but do
	 * not create.
	 *
	 * @return A non-zero error value on failure and 0 on success, some possible errors are: \retval
	 * MDBX_VERSION_MISMATCH The version of the MDBX library doesn't match the version that created the database
	 * environment. \retval MDBX_INVALID The environment file headers are corrupted. \retval MDBX_ENOENT The
	 * directory specified by the path parameter doesn't exist. \retval MDBX_EACCES The user didn't have
	 * permission to access the environment files. \retval MDBX_BUSY The \ref MDBX_EXCLUSIVE flag was specified
	 * and the environment is in use by another process, or the current process tries to open environment more
	 * than once. \retval MDBX_INCOMPATIBLE Environment is already opened by another process, but with different
	 * set of \ref MDBX_SAFE_NOSYNC, \ref MDBX_UTTERLY_NOSYNC flags. Or if the database is already exist and
	 * parameters specified early by \ref mdbx_env_set_geometry() are incompatible (i.e. different pagesize,
	 * etc).
	 *
	 * \retval MDBX_WANNA_RECOVERY The \ref MDBX_RDONLY flag was specified but read-write access is required to
	 * rollback inconsistent state after a system crash.
	 *
	 * \retval MDBX_TOO_LARGE Database is too large for this process, i.e. 32-bit process tries to open >4Gb
	 * database.
	 */

	public static final int mdbx_env_open( long env, String pathname, int flags, int mode) {
		return FfmDispatch.mdbx_env_open(env, pathname, flags, mode);
	}

	/**
	 * Open environment version for Windows
	 * \note Available only on Windows.
	 * @see #mdbx_env_open(long, String, int, int)
	 *
	 * @param env environment
	 * @param pathname path name
	 * @param flags environment flags
	 * @param mode mode
	 * @return pointer to environment
	 */

	public static final int mdbx_env_openW( long env, String pathname, int flags, int mode) {
		return FfmDispatch.mdbx_env_openW(env, pathname, flags, mode);
	}

	/**
	 * Open an environment instance using specific meta-page for checking and recovery.
	 *
	 * This function mostly of internal API for `mdbx_chk` utility and subject to
	 * change at any time. Do not use this function to avoid shooting your own
	 * leg(s).
	 *
	 * \note On Windows the \ref mdbx_env_open_for_recoveryW() is recommended to use.

	 * @param env environment
	 * @param pathname path name
	 * @param target_meta target meta
	 * @param writeable writable boolean
	 * @return pointer to environment
	 */

	public static final int mdbx_env_open_for_recovery( long env, String pathname, int target_meta, int writeable) {
		return FfmDispatch.mdbx_env_open_for_recovery(env, pathname, target_meta, writeable);
	}  //bool

	/**
	 * \copydoc mdbx_env_open_for_recovery()
	 * \ingroup c_extra
	 * \note Available only on Windows.
	 * @see #mdbx_env_open_for_recovery(long, String, int, int)
	 *
	 * @param env environment
	 * @param pathname path name
	 * @param target_meta target meta
	 * @param writeable writable boolean
	 * @return pointer to environment
	 */

	public static final int mdbx_env_open_for_recoveryW( long env, String pathname, int target_meta, int writeable) {
		return FfmDispatch.mdbx_env_open_for_recoveryW(env, pathname, target_meta, writeable);
	}  //bool

	/**
	 * Turn database to the specified meta-page.
	 *
	 * This function mostly of internal API for `mdbx_chk` utility and subject to change at any time. Do not use
	 * this function to avoid shooting your own leg(s).

	 * @param env environment
	 * @param target_meta target meta
	 * @return error code if any
	 */

	public static final int mdbx_env_turn_for_recovery( long env, int target_meta) {
		return FfmDispatch.mdbx_env_turn_for_recovery(env, target_meta);
	}

	/**
	 * Gets basic information about the database without opening it.
	 *
	 * The purpose of the function is to get basic information without opening the database and mapping the data
	 * to memory (which can be quite an expensive operation for the OS kernel). The information obtained in this
	 * way can be useful for adjusting the options for working with the database before opening it, as well as
	 * in file manager scripts and other auxiliary utilities.
	 *
	 * <b>Todo</b> Add the ability to set a callback to the API for revising the options for working with the database
	 * during its opening (while holding locks).
	 *
	 * @param pathname [in]
	 *        Path to the directory or file of the database.
	 * @param info [out]
	 *        Pointer to the \ref MDBX_envinfo structure for getting information.
	 * @param bytes [in]
	 *        The current size of the \ref MDBX_envinfo structure, this value is used to ensure
	 *        compatibility with the ABI.
	 *
	 * <b>Note</b> Only some fields of the \ref MDBX_envinfo structure are filled, the values of which can be obtained
	 *       without mapping the DB files into memory and without capturing locks: DB page size, DB geometry,
	 *       size of allocated space (number of the last allocated page), number of the last transaction and
	 *       boot-id.
	 *
	 * <b>warning</b> The information obtained is a snapshot for the duration of the function execution and can be
	 *          changed at any time by the process working with the DB. In particular, there are no obstacles to
	 *          another process deleting the DB and creating it again with a different page size and/or changing
	 *          any other parameters.
	 *
	 * @return Non-zero error code value, or 0 if successful.
	 */

	public static final int mdbx_preopen_snapinfo( String pathname, MDBX_envinfo info, long bytes) {
		return FfmDispatch.mdbx_preopen_snapinfo(pathname, info, bytes);
	}


	public static final int mdbx_preopen_snapinfoW( String pathname, MDBX_envinfo info, long bytes) {
		return FfmDispatch.mdbx_preopen_snapinfoW(pathname, info, bytes);
	}

	/**
	 * Delete the environment's files in a proper and multiprocess-safe way.
	 * \ingroup c_extra
	 *
	 * \note On Windows the \ref mdbx_env_deleteW() is recommended to use.
	 *
	 * @param pathname  [in] The pathname for the database or the directory in which the database files reside.
	 *
	 * @param mode      [in] Specifies deletion mode for the environment. This parameter must be set to one of
	 *                  the constants described above in the \ref MDBX_env_delete_mode_t section.
	 *
	 * \note The \ref MDBX_ENV_JUST_DELETE not supported on Windows since system is unable to delete a
	 * memory-mapped file.
	 *
	 * @return A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_RESULT_TRUE  No corresponding files or directories were found, so no deletion was performed.
	 */

	public static final int mdbx_env_delete( String pathname, int mode) {
		return FfmDispatch.mdbx_env_delete(pathname, mode);
	}

	/**
	 * 	 Delete the environment's files in a proper and multiprocess-safe way (Windows version)
	 * \ingroup c_extra
	 * \note Available only on Windows.
	 * @see #mdbx_env_delete(String, int)
	 *
	 * @param pathname path name
	 * @param mode mode
	 * @return error code or 0 on success
	 */

	public static final int mdbx_env_deleteW( String pathname, int mode) {
		return FfmDispatch.mdbx_env_deleteW(pathname, mode);
	}


	public static final int mdbx_env_copy( long env, String dest, int flags) {
		return FfmDispatch.mdbx_env_copy(env, dest, flags);
	}


	public static final int mdbx_env_copyW( long env, String dest, int flags) {
		return FfmDispatch.mdbx_env_copyW(env, dest, flags);
	}

	/**
	 * Copy an MDBX environment by given read transaction to the specified path, with options.
	 *
	 * This function may be used to make a backup of an existing environment. No lockfile is created, since it
	 * gets recreated at need. \note This call can trigger significant file size growth if run in parallel with
	 * write transactions, because it employs a read-only transaction. See long-lived transactions under \ref
	 * restrictions section.
	 *
	 * \note On Windows the mdbx_txn_copy2pathnameW() is recommended to use.
	 *
	 * @param txn [in] 	A transaction handle returned by \ref mdbx_txn_begin().
	 * @param dest [in] The pathname of a file in which the copy will reside. This file must not be already
	 * 									exist, but parent directory must be writable.
	 * @param flags [in] Specifies options for this operation. This parameter must be bitwise OR'ing
	 * 									 together any of the constants described here:
	 *
	 * - \ref MDBX_CP_DEFAULTS Perform copy as-is without compaction, etc.
	 *
	 * - \ref MDBX_CP_COMPACT Perform compaction while copying: omit free pages and sequentially renumber all
	 * pages in output. This option consumes little bit more CPU for processing, but may running quickly than
	 * the default, on account skipping free pages.
	 *
	 * - \ref MDBX_CP_FORCE_DYNAMIC_SIZE Force to make resizable copy, i.e. dynamic size instead of fixed.
	 *
	 * @return A non-zero error value on failure and 0 on success.
	 */

	public static final int mdbx_txn_copy2pathname( long txn, String dest, int flags) {
		return FfmDispatch.mdbx_txn_copy2pathname(txn, dest, flags);
	}


	public static final int mdbx_txn_copy2pathnameW( long txn, String dest, int flags) {
		return FfmDispatch.mdbx_txn_copy2pathnameW(txn, dest, flags);
	}


	public static final int mdbx_env_copy2fd( long env, long fd, int flags) {
		return FfmDispatch.mdbx_env_copy2fd(env, fd, flags);
	}

	/**
	 * Copy an environment by given read transaction to the specified file descriptor, with options.
	 *
	 * This function may be used to make a backup of an existing environment. No lockfile is created, since it
	 * gets recreated at need.
	 *
	 * \note This call can trigger significant file size growth if run in parallel with write transactions,
	 * because it employs a read-only transaction. See long-lived transactions under \ref restrictions section.
	 *
	 * \note Fails if the environment has suffered a page leak and the destination file descriptor is associated
	 * with a pipe, socket, or FIFO.
	 *
	 * @param txn [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param fd [in] The file descriptor to write the copy to. It must have already been opened for Write
	 * 								access.
	 * @param flags [in] Special options for this operation. \see mdbx_env_copy()
	 *
	 * @return A non-zero error value on failure and 0 on success.
	 */

	public static final int mdbx_txn_copy2fd( long txn, long fd, int flags) {
		return FfmDispatch.mdbx_txn_copy2fd(txn, fd, flags);
	}


	public static final int mdbx_env_stat( long env, MDBX_stat stat, long bytes) {
		return FfmDispatch.mdbx_env_stat(env, stat, bytes);
	}


	public static final int mdbx_env_stat_ex( long env, long txn, MDBX_stat stat, long bytes) {
		return FfmDispatch.mdbx_env_stat_ex(env, txn, stat, bytes);
	}


	public static final int mdbx_env_info( long env, MDBX_envinfo info, long bytes) {
		return FfmDispatch.mdbx_env_info(env, info, bytes);
	}


	public static final int mdbx_env_info_ex( long env, long txn, MDBX_envinfo info, long bytes) {
		return FfmDispatch.mdbx_env_info_ex(env, txn, info, bytes);
	}


	public static final int mdbx_env_close( long env) {
		return FfmDispatch.mdbx_env_close(env);
	}


	public static final int mdbx_env_close_ex( long env, int dont_sync) {
		return FfmDispatch.mdbx_env_close_ex(env, dont_sync);
	}

	/**
	 * \brief Restores the environment instance in the child process after forking the parent process via
	 * `fork()` and related system calls. \ingroup c_extra
	 *
	 * Without calling \ref mdbx_env_resurrect_after_fork(), it is not possible to use the open environment
	 * instance in the child process, including all transactions running at the time of the fork.
	 *
	 * The actions performed by the function can be considered as reopening the DB in the child process, while
	 * preserving the specified options and addresses of already created instances of objects associated with
	 * the API.
	 *
	 * \note The function is not available in Windows family OS due to the lack of process forking functionality
	 * in the operating system API.
	 *
	 * Forking does not affect the state of the MDBX environment in the parent process. All transactions that
	 * were in the parent process at the time of the fork will continue to execute without interference in the
	 * parent process after the fork. But in the child process, all corresponding transactions will inevitably
	 * cease to be valid, and attempting to use them will result in an error being returned or `SIGSEGV` being
	 * sent.
	 *
	 * Using an instance of the environment in a child process is not possible before calling \ref
	 * mdbx_env_resurrect_after_fork(), since as a result of forking, the PID of the process changes, the value
	 * of which is used to organize the joint work with the database, including tracking the processes/threads
	 * executing read transactions associated with the corresponding data snapshots. All transactions active at
	 * the time of forking cannot continue in the child process, since they do not own any locks or any data
	 * snapshot and do not prevent it from being processed during garbage collection. * The \ref
	 * mdbx_env_resurrect_after_fork() function restores the passed environment instance in the child process
	 * after forking, namely: updates the system identifiers used, reopens file descriptors, acquires the
	 * necessary locks associated with the LCK and DXB files of the database, restores the memory mappings of
	 * the database page, reader tables, and service/auxiliary data to memory. However, transactions inherited
	 * from the parent process are not restored, receiving write and read transactions are processed
	 * differently:
	 *
	 * - A write transaction, if any at the time of forking, is aborted in the child process with the release of
	 * resources associated with it, including all nested transactions.
	 *
	 * - Read transactions, if any in the parent process, are logically aborted in the child process, but
	 * without releasing resources. Therefore, it is necessary to ensure that \ref mdbx_txn_abort() is called
	 * for each such reader transaction in the child process, or accept the resource leak until the child
	 * process terminates.
	 *
	 * The reason for not releasing the resources of reader transactions is that historically MDBX does not
	 * maintain any common list of reader instances, since this is not required for normal operation, but
	 * requires the use of atomic operations or additional synchronization objects when creating/destroying \ref
	 * MDBX_txn instances.
	 *
	 * Calling \ref mdbx_env_resurrect_after_fork() without forking, not in a child process, or repeated calls
	 * do not result in any actions or changes.
	 *
	 * @param env [in,out] The environment instance created by the \ref mdbx_env_create() function.
	 *
	 * @return A non-zero error code value, or 0 on success. Some possible errors are:
	 *
	 * \retval MDBX_BUSY The parent process opened the DB in \ref MDBX_EXCLUSIVE mode.
	 *
	 * \retval MDBX_EBADSIGN If the signature of the object instance is damaged, or if \ref
	 * mdbx_env_resurrect_after_fork() is called simultaneously from different threads.
	 *
	 * \retval MDBX_PANIC A critical error occurred while restoring the environment instance, or such an error
	 * already existed before the function was called.
	 */

	public static final int mdbx_env_resurrect_after_fork( long env) {
		return FfmDispatch.mdbx_env_resurrect_after_fork(env);
	}


	public static final int mdbx_env_sync( long env) {
		return FfmDispatch.mdbx_env_sync(env);
	}


	public static final int mdbx_env_sync_ex( long env, int force, int nonblock) {
		return FfmDispatch.mdbx_env_sync_ex(env, force, nonblock);
	}


	public static final int mdbx_env_sync_poll( long env) {
		return FfmDispatch.mdbx_env_sync_poll(env);
	}


	public static final int mdbx_env_set_flags( long env, int flags, int onoff) {
		return FfmDispatch.mdbx_env_set_flags(env, flags, onoff);
	}


	public static final int mdbx_env_get_flags( long env, long[] flags) {
		return FfmDispatch.mdbx_env_get_flags(env, flags);
	}


	public static final int mdbx_env_get_option( long env, int option, long[] pvalue) {
		return FfmDispatch.mdbx_env_get_option(env, option, pvalue);
	}


	public static final int mdbx_env_set_option( long env, int option, long value) {
		return FfmDispatch.mdbx_env_set_option(env, option, value);
	}


	public static final int mdbx_env_get_path( long env, long[] dest) {
		return FfmDispatch.mdbx_env_get_path(env, dest);
	}


	public static final int mdbx_env_get_pathW( long env, long[] dest) {
		return FfmDispatch.mdbx_env_get_pathW(env, dest);
	}


	public static final int mdbx_env_get_fd( long env, long[] fd) {
		return FfmDispatch.mdbx_env_get_fd(env, fd);
	}


	public static final int mdbx_env_set_geometry( long env, long size_lower, long size_now, long size_upper, long growth_step, long shrink_threshold, long pagesize) {
		return FfmDispatch.mdbx_env_set_geometry(env, size_lower, size_now, size_upper, growth_step, shrink_threshold, pagesize);
	}


	public static final int mdbx_env_set_mapsize( long env, long size) {
		return FfmDispatch.mdbx_env_set_mapsize(env, size);
	}


	public static final int mdbx_env_get_maxreaders( long env, long[] readers) {
		return FfmDispatch.mdbx_env_get_maxreaders(env, readers);
	}


	public static final int mdbx_env_set_maxreaders( long env, int readers) {
		return FfmDispatch.mdbx_env_set_maxreaders(env, readers);
	}


	public static final int mdbx_env_get_maxdbs( long env, long[] flags) {
		return FfmDispatch.mdbx_env_get_maxdbs(env, flags);
	}


	public static final int mdbx_env_set_maxdbs( long env, long dbs) {
		return FfmDispatch.mdbx_env_set_maxdbs(env, dbs);
	}

	/** \deprecated Please use \ref mdbx_env_get_maxkeysize_ex() and/or \ref mdbx_env_get_maxvalsize_ex()
	 * \ingroup c_statinfo
	 *
	 * @param env    [in] An environment handle returned by \ref mdbx_env_create().
	 *
	 * @return The maximum size of a key can write, or -1 if something is wrong.
	 */
	@Deprecated

	public static final int mdbx_env_get_maxkeysize( long env) {
		return FfmDispatch.mdbx_env_get_maxkeysize(env);
	}

	/**
	 * Returns the maximum size of keys can put.
	 * \ingroup c_statinfo
	 *
	 * @param env    [in] An environment handle returned by \ref mdbx_env_create().
	 * @param flags  [in] Table options (\ref MDBX_DUPSORT, \ref MDBX_INTEGERKEY and so on). \see db_flags
	 *
	 * @return The maximum size of a key can write, or -1 if something is wrong.
	 */

	public static final int mdbx_env_get_maxkeysize_ex( long env, long flags) {
		return FfmDispatch.mdbx_env_get_maxkeysize_ex(env, flags);
	}


	public static final int mdbx_env_get_maxvalsize_ex( long env, long flags) {
		return FfmDispatch.mdbx_env_get_maxvalsize_ex(env, flags);
	}

	/**
	 * Returns maximal size of key-value pair to fit in a single page for specified table flags.
	 * \ingroup c_statinfo
	 *
	 * @param env    [in] An environment handle returned by \ref mdbx_env_create().
	 * @param flags  [in] Table options (\ref MDBX_DUPSORT, \ref MDBX_INTEGERKEY and so on). \see db_flags
	 *
	 * @return The maximum size of a data can write, or -1 if something is wrong.
	 */

	public static final int mdbx_env_get_pairsize4page_max( long env, long flags) {
		return FfmDispatch.mdbx_env_get_pairsize4page_max(env, flags);
	}

	/** \brief Returns maximal data size in bytes to fit in a leaf-page or single large/overflow-page for
	 * specified table flags.
	 * \ingroup c_statinfo
	 *
	 * @param env    [in] An environment handle returned by \ref mdbx_env_create().
	 * @param flags  [in] Table options (\ref MDBX_DUPSORT, \ref MDBX_INTEGERKEY and so on). \see db_flags
	 *
	 * @return The maximum size of a data can write, or -1 if something is wrong.
	 */

	public static final int mdbx_env_get_valsize4page_max( long env, long flags) {
		return FfmDispatch.mdbx_env_get_valsize4page_max(env, flags);
	}


	public static final int mdbx_env_get_syncbytes( long env, long[] threshold) {
		return FfmDispatch.mdbx_env_get_syncbytes(env, threshold);
	}


	public static final int mdbx_env_set_syncbytes( long env, long bytes) {
		return FfmDispatch.mdbx_env_set_syncbytes(env, bytes);
	}


	public static final int mdbx_env_get_syncperiod( long env, long[] period_seconds_16dot16) {
		return FfmDispatch.mdbx_env_get_syncperiod(env, period_seconds_16dot16);
	}


	public static final int mdbx_env_set_syncperiod( long env, long seconds_16dot16) {
		return FfmDispatch.mdbx_env_set_syncperiod(env, seconds_16dot16);
	}


	public static final long mdbx_env_get_userctx( long env) {
		return FfmDispatch.mdbx_env_get_userctx(env);
	}


	public static final int mdbx_env_set_userctx( long env, long ctx) {
		return FfmDispatch.mdbx_env_set_userctx(env, ctx);
	}

	/**
	 * \brief Warms up the database by loading pages into memory, optionally lock ones. \ingroup c_settings
	 *
	 * Depending on the specified flags, notifies OS kernel about following access, force loads the database
	 * pages, including locks ones in memory or releases such a lock. However, the function does not analyze the
	 * b-tree nor the GC. Therefore an unused pages that are in GC handled (i.e. will be loaded) in the same way
	 * as those that contain payload.
	 *
	 * At least one of `env` or `txn` argument must be non-null.
	 *
	 * @param env [in] An environment handle returned by \ref mdbx_env_create().
	 * @param txn [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param flags [in] The \ref warmup_flags, bitwise OR'ed together.
	 * @param timeout_seconds_16dot16 [in] Optional timeout which checking only during explicitly peeking
	 * database pages for loading ones if the \ref MDBX_warmup_force option was specified.
	 *
	 * @return A non-zero error value on failure and 0 on success. Some possible errors are:
	 *
	 * \retval MDBX_ENOSYS The system does not support requested operation(s).
	 *
	 * \retval MDBX_RESULT_TRUE The specified timeout is reached during load data into memory.
	 */

	public static final int mdbx_env_warmup( long env, long txn, long flags, long timeout_seconds_16dot16) {
		return FfmDispatch.mdbx_env_warmup(env, txn, flags, timeout_seconds_16dot16);
	}

	//====================================================//
	// Debug methods
	//====================================================//
	//TODO: MDBX_assert_func

	//TODO: mdbx_env_set_assert

	//====================================================//
	// TXN methods
	//====================================================//

	public static final int mdbx_txn_begin( long env, long parent, long flags, long[] txn) {
		return FfmDispatch.mdbx_txn_begin(env, parent, flags, txn);
	}


	public static final int mdbx_txn_begin_ex( long env, long parent, long flags, long[] txn, long ctx) {
		return FfmDispatch.mdbx_txn_begin_ex(env, parent, flags, txn, ctx);
	}

	/**
	 * Marks transaction as broken.
	 *
	 * Function keeps the transaction handle and corresponding locks, but makes impossible to perform any
	 * operations within a broken transaction. Broken transaction must then be aborted explicitly later.
	 *
	 * @param txn [in] A transaction handle returned by {@link #mdbx_txn_begin(long, long, long, long[])}.
	 *
	 * @return A non-zero error value on failure and 0 on success. *
	 *
	 * @see #mdbx_txn_abort(long)
	 * @see #mdbx_txn_reset(long)
	 * @see #mdbx_txn_commit(long)
	 */

	public static final int mdbx_txn_break( long txn) {
		return FfmDispatch.mdbx_txn_break(txn);
	}


	public static final long mdbx_txn_env( long txn) {
		return FfmDispatch.mdbx_txn_env(txn);
	}


	public static final int mdbx_txn_flags( long txn) {
		return FfmDispatch.mdbx_txn_flags(txn);
	}


	public static final long mdbx_txn_id( long txn) {
		return FfmDispatch.mdbx_txn_id(txn);
	}


	public static final int mdbx_txn_commit( long txn) {
		return FfmDispatch.mdbx_txn_commit(txn);
	}


	public static final int mdbx_txn_commit_ex( long txn, MDBX_commit_latency latency) {
		return FfmDispatch.mdbx_txn_commit_ex(txn, latency);
	}


	public static final void mdbx_txn_abort( long txn) {
		FfmDispatch.mdbx_txn_abort(txn);
	}


	public static final void mdbx_txn_reset( long txn) {
		FfmDispatch.mdbx_txn_reset(txn);
	}


	public static final int mdbx_txn_renew( long txn) {
		return FfmDispatch.mdbx_txn_renew(txn);
	}

	/**
	 * Puts the reading transaction in the "parked" state.
	 *
	 * Running reading transactions do not allow recycling of old MVCC data snapshots, starting with the oldest
	 * used/read version and all subsequent ones. A parked transaction can be evicted by a write transaction if
	 * it interferes with garbage recycling (old MVCC data snapshots). And if evicting does not occur, then
	 * recovery (transition to the working state and continuation of execution) of the reading transaction will
	 * be significantly cheaper. Thus, parking transactions allows you to prevent the negative consequences
	 * associated with stopping garbage recycling, while keeping overhead costs to a minimum.
	 *
	 * To continue execution (reading and/or using data), a parked transaction must be restored using \ref
	 * mdbx_txn_unpark(). For ease of use and to prevent unnecessary API calls, the `autounpark` parameter
	 * provides the ability to automatically "unpark" when using a parked transaction in API functions that
	 * involve reading data.
	 *
	 * \warning Before restoring/unparking a transaction, regardless of the `autounpark` argument, dereferencing
	 * pointers to previously obtained data within a parked transaction is not allowed, since the MVCC snapshot
	 * in which this data is located is not held and can be recycled at any time.
	 *
	 * A parked transaction without "unparking" can be aborted, reset or restarted at any time via \ref
	 * mdbx_txn_abort(), \ref mdbx_txn_reset() and \ref mdbx_txn_renew(), respectively.
	 *
	 * @param txn [in]
	 *          Read transaction started by \ref mdbx_txn_begin().
	 * @param autounpark [in]
	 *          Allows you to enable automatic unparking/recovery of a transaction when calling API
	 *          functions that involve reading data.
	 * @return Non-zero error code value, or 0 on success.
	 *
	 * @see #mdbx_txn_unpark(long, int)
	 * @see #mdbx_txn_flags(long)
	 * @see #mdbx_env_set_hsr(long, long)
	 * @see <a href="intro.html#long-lived-read">Long-lived read transactions</a>
	 */

	public static final int mdbx_txn_park( long txn, int autounpark) {
		return FfmDispatch.mdbx_txn_park(txn, autounpark);
	}  //Bool

	/**
	 * Unparks a previously parked read transaction.
	 *
	 * The function attempts to recover a previously parked transaction. If the parked transaction was ousted to
	 * recycle old MVCC snapshots, then depending on the `restart_if_ousted` argument, it is either restarted
	 * similarly to \ref mdbx_txn_renew(), or the transaction is reset and the error code \ref MDBX_OUSTED is
	 * returned.
	 *
	 * @param txn [in]
	 *          A read transaction started by \ref mdbx_txn_begin() and then parked by \ref mdbx_txn_park.
	 *
	 * @param restart_if_ousted [in]
	 *          Allows an immediate restart of the transaction if it was ousted.
	 *
	 * @return A non-zero error code, or 0 on success. Some specific result codes:
	 *
	 * \retval MDBX_SUCCESS A parked transaction has been successfully recovered, or it was not parked.
	 *
	 * \retval MDBX_OUSTED A reader transaction was preempted by a writer to recycle old MVCC snapshots,
	 *         and the `restart_if_ousted` argument was `false`. The transaction is reset to a state similar to
	 *         that after calling \ref mdbx_txn_reset(), but the instance (handle) is not deallocated and can be
	 *         reused via \ref mdbx_txn_renew(), or deallocated via \ref mdbx_txn_abort().
	 *
	 * \retval MDBX_RESULT_TRUE The reading transaction was ousted, but is now restarted to read another
	 *         (latest) MVCC snapshot, because restart_if_ousted` was set `true`.
	 *
	 * \retval MDBX_BAD_TXN The transaction has already committed, or was not started.
	 *
	 * @see #mdbx_txn_park(long, int)
	 * @see #mdbx_txn_flags(long)
	 * @see <a href="intro.html#long-lived-read">Long-lived read transactions</a>
	 */

	public static final int mdbx_txn_unpark( long txn, int restart_if_ousted) {
		return FfmDispatch.mdbx_txn_unpark(txn, restart_if_ousted);
	}  //Bool

	/** Unbind or closes all cursors of a given transaction and of all its parent transactions if ones are.
	 * \ingroup c_cursors
	 *
	 * Unbinds either closes all cursors associated (opened, renewed or binded) with
	 * the given transaction in a bulk with minimal overhead.
	 *
	 * @see JNI#mdbx_cursor_unbind(long)
	 * @see JNI#mdbx_cursor_close(long)
	 *
	 * @param txn        [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param unbind     [in] If non-zero, unbinds cursors and leaves ones reusable.
	 *                        Otherwise close and dispose cursors.
	 * @param count  [in,out] An optional pointer to return the number of cursors processed by the requested
	 * operation.
	 *
	 * @return A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned
	 *                               by current thread.
	 * \retval MDBX_BAD_TXN          Given transaction is invalid or has
	 *                               a child/nested transaction transaction.
	 */

	public static final int mdbx_txn_release_all_cursors_ex( long txn, int unbind, long count ) {
		return FfmDispatch.mdbx_txn_release_all_cursors_ex(txn, unbind, count);
	}

	/**
	 * Unbind or closes all cursors of a given transaction and of all its parent transactions if ones are.
	 *
	 * Unbinds either closes all cursors associated (opened or renewed) with the given transaction in a bulk
	 * with minimal overhead.
	 *
	 * @param txn [in]      A transaction handle returned by \ref mdbx_txn_begin().
	 * @param unbind [in]   If non-zero, unbinds cursors and leaves ones reusable.
	 *                      Otherwise close and dispose cursors.
	 *
	 * @return A negative error value on failure or the number of closed cursors
	 *          on success, some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned by current thread.
	 * \retval MDBX_BAD_TXN          Given transaction is invalid or has a child/nested transaction transaction.
	 *
	 * @see #mdbx_cursor_unbind(long)
	 * @see #mdbx_cursor_close(long)
	 */

	public static final int mdbx_txn_release_all_cursors( long txn, int unbind ) {
		return FfmDispatch.mdbx_txn_release_all_cursors(txn, unbind);
	}

	/**
	 * Return information about the MDBX transaction.
	 *
	 * @param txn
	 *          A transaction handle returned by mdbx_txn_begin()
	 * @param info
	 *          The address of an MDBX_txn_info structure where the information will be copied.
	 * @param scanRlt
	 *          The boolean flag controls the scan of the read lock table to provide complete information. Such
	 *          scan is relatively expensive and you can avoid it if corresponding fields are not needed. See
	 *          description of MDBX_txn_info.
	 * @return A non-zero error value on failure and 0 on success.
	 */

	public static final int mdbx_txn_info( long txn, MDBX_txn_info info, int scanRlt) {
		return FfmDispatch.mdbx_txn_info(txn, info, scanRlt);
	}  //_Bool

	/**
	 * Sets application information associated (a context pointer) with the transaction.
	 * \ingroup c_transactions
	 * @see #mdbx_txn_get_userctx(long)
	 *
	 * @param txn  [in] An transaction handle returned by \ref mdbx_txn_begin_ex() or \ref mdbx_txn_begin().
	 * @param ctx  [in] An arbitrary pointer for whatever the application needs.
	 *
	 * @return A non-zero error value on failure and 0 on success.
	 */

	public static final int mdbx_txn_set_userctx( long txn, long ctx) {
		return FfmDispatch.mdbx_txn_set_userctx(txn, ctx);
	}

	/**
	 * Returns an application information (a context pointer) associated with the transaction.
	 * \ingroup c_transactions
	 * @see #mdbx_txn_set_userctx(long, long)
	 *
	 * @param txn  [in] An transaction handle returned by \ref mdbx_txn_begin_ex() or \ref mdbx_txn_begin().
	 * @return The pointer which was passed via the `context` parameter of `mdbx_txn_begin_ex()` or set by
	 *          \ref mdbx_txn_set_userctx(), or `NULL` if something wrong.
	 */

	public static final long mdbx_txn_get_userctx( long txn) {
		return FfmDispatch.mdbx_txn_get_userctx(txn);
	}

	/**
	 * \brief Acquires write-transaction lock.
	 * Provided for custom and/or complex locking scenarios.
	 *
	 * @param env environment
	 * @param dont_wait Don't wait boolean
	 * @return A non-zero error value on failure and 0 on success.
	 */

	public static final int mdbx_txn_lock( long env, int dont_wait) {
		return FfmDispatch.mdbx_txn_lock(env, dont_wait);
	}  //_Bool

	/**
	 * \brief Releases write-transaction lock.
	 * Provided for custom and/or complex locking scenarios.
	 *
	 * @param env environment
	 * @return A non-zero error value on failure and 0 on success.
	 */

	public static final int mdbx_txn_unlock( long env) {
		return FfmDispatch.mdbx_txn_unlock(env);
	}

	//====================================================//
	// DBI methods
	//====================================================//

	public static final int mdbx_dbi_open( long txn, String name, int flags, long[] dbi) {
		return FfmDispatch.mdbx_dbi_open(txn, name, flags, dbi);
	}


	public static final int mdbx_dbi_open_ex( long txn, String name, int flags, long[] dbi, long keycmp, long datacmp) {
		return FfmDispatch.mdbx_dbi_open_ex(txn, name, flags, dbi, keycmp, datacmp);
	}


	public static final int mdbx_dbi_stat( long txn, long dbi, MDBX_stat stat, long bytes) {
		return FfmDispatch.mdbx_dbi_stat(txn, dbi, stat, bytes);
	}


	public static final int mdbx_dbi_flags( long txn, long dbi, long[] flags) {
		return FfmDispatch.mdbx_dbi_flags(txn, dbi, flags);
	}


	public static final int mdbx_dbi_flags_ex( long txn, long dbi, long[] flags, long[] state) {
		return FfmDispatch.mdbx_dbi_flags_ex(txn, dbi, flags, state);
	}


	public static final void mdbx_dbi_close( long env, long dbi) {
		FfmDispatch.mdbx_dbi_close(env, dbi);
	}


	public static final int mdbx_dbi_dupsort_depthmask( long txn, long dbi, long[] mask) {
		return FfmDispatch.mdbx_dbi_dupsort_depthmask(txn, dbi, mask);
	}


	public static final int mdbx_dbi_sequence( long txn, long dbi, long[] result, long increment) {
		return FfmDispatch.mdbx_dbi_sequence(txn, dbi, result, increment);
	}

	/**
	 * Rename a table by DBI handle.
	 *
	 * Rename a user-defined subDB associated with the passed DBI handle.
	 *
	 * @param txn [in,out]
	 *          A write transaction started by \ref mdbx_txn_begin().
	 * @param dbi [in]
	 *          A table handle (named user subDB) opened by \ref mdbx_dbi_open().
	 * @param name [in]
	 *          A new name to rename.
	 *
	 * @return A non-zero error code value, or 0 on success.
	 */

	public static final int mdbx_dbi_rename( long txn, long dbi, String name) {
		return FfmDispatch.mdbx_dbi_rename(txn, dbi, name);
	}


	public static final int mdbx_dbi_rename2( long txn, long dbi, MDBX_val name) {
		return FfmDispatch.mdbx_dbi_rename2(txn, dbi, name);
	}

	/*
	 * Callback function for enumerating user-defined named tables.
	 *
	 * @param ctx [in] Pointer to context passed by the same parameter in \ref mdbx_enumerate_subdb().
	 * @param txn [in] Transaction.
	 * @param name [in] Table name.
	 * @param flags [in] \ref MDBX_db_flags_t.
	 * @param stat [in] Basic information \ref MDBX_stat about the table.
	 * @param dbi [in] Non-zero DBI handle, if one was opened for this table. Or 0 if no such open handle was
	 *          found.
	 *
	 * @return Zero on success and enumeration continues; if another value is returned, it will be immediately
	 *         returned to the caller without continuing enumeration.
	 *
	 * @see mdbx_enumerate_tables()
	 */
//	typedef int(MDBX_table_enum_func)(void *ctx, const MDBX_txn *txn, const MDBX_val *name, MDBX_db_flags_t flags,
//      const struct MDBX_stat *stat, MDBX_dbi dbi) MDBX_CXX17_NOEXCEPT;

/*
 * Enumerates user-defined named tables.
 *
 * Enumerates user-defined named tables, calling the user-specified visitor function for each named table.
 * Enumeration continues until there are no more named tables, or until the user-defined function returns a
 * nonzero result, which will be immediately returned as the result.
 *
 * @param [in]
 *          txn The transaction started by \ref mdbx_txn_begin().
 * @param [in]
 *          func A pointer to a user-defined function with signature \ref MDBX_subdb_enum_func, which will be
 *          called for each table.
 * @param [in]
 *          ctx A pointer to some context that will be passed to the `func()` function as is.
 *
 * @return A nonzero error code value, or 0 on success.
 *
 * @see MDBX_subdb_enum_func
 */
//	LIBMDBX_API int mdbx_enumerate_tables(const MDBX_txn *txn, MDBX_table_enum_func *func, void *ctx);

	//====================================================//
	// CRUD methods
	//====================================================//
	/**
	 * Get items from a table.
	 * \ingroup c_crud
	 *
	 * This function retrieves key/data pairs from the table. The address and length of the data associated with
	 * the specified key are returned in the structure to which data refers.
	 * If the table supports duplicate keys (\ref MDBX_DUPSORT) then the first data item for the key will be
	 * returned. Retrieval of other items requires the use of \ref mdbx_cursor_get().
	 *
	 * \note The memory pointed to by the returned values is owned by the table. The caller MUST not dispose of
	 * the memory, and MUST not modify it in any way regardless in a read-only nor read-write transactions!
	 * For case a table opened without the \ref MDBX_WRITEMAP modification attempts likely will cause a
	 * `SIGSEGV`. However, when a table opened with the \ref MDBX_WRITEMAP or in case values returned inside
	 * read-write transaction are located on a "dirty" (modified and pending to commit) pages, such modification
	 * will silently accepted and likely will lead to DB and/or data corruption.
	 *
	 * \note Values returned from the table are valid only until a subsequent update operation, or the end of
	 * the transaction.
	 *
	 * @param txn       [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param dbi       [in] A table handle returned by \ref mdbx_dbi_open().
	 * @param key       [in] The key to search for in the table.
	 * @param data  [in,out] The data corresponding to the key.
	 *
	 * @return A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned by current thread.
	 * \retval MDBX_NOTFOUND  The key was not in the table.
	 * \retval MDBX_EINVAL    An invalid parameter was specified.
	 */

	public static final int mdbx_get( long txn, long dbi, MDBX_val key, MDBX_val data) {
		return FfmDispatch.mdbx_get(txn, dbi, key, data);
	}

	/**
	 * Get items from a table and optionally number of data items for a given key.
	 *
	 * \ingroup c_crud
	 *
	 * Briefly this function does the same as \ref mdbx_get() with a few
	 * differences:
	 *  1. If values_count is NOT NULL, then returns the count
	 *     of multi-values/duplicates for a given key.
	 *  2. Updates BOTH the key and the data for pointing to the actual key-value
	 *     pair inside the table.
	 *
	 * @param txn           [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param dbi           [in] A table handle returned by \ref mdbx_dbi_open().
	 *
	 * @param key       [in,out] The key to search for in the table.
	 * @param data      [in,out] The data corresponding to the key.
	 * @param values_count [out] The optional address to return number of values associated with given key:
	 *                            = 0 - in case \ref MDBX_NOTFOUND error;
	 *                            = 1 - exactly for tables WITHOUT \ref MDBX_DUPSORT;
	 *                            >= 1 for tables WITH \ref MDBX_DUPSORT.
	 *
	 * @return A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned by current thread.
	 * \retval MDBX_NOTFOUND  The key was not in the table.
	 * \retval MDBX_EINVAL    An invalid parameter was specified.
	 */

	public static final int mdbx_get_ex( long txn, long dbi, MDBX_val key, MDBX_val data, long[] values_count) {
		return FfmDispatch.mdbx_get_ex(txn, dbi, key, data, values_count);
	}

//
//	public static final int mdbx_get_attr(long txn, long dbi, MDBX_val key, MDBX_val data, long[] pattr) {
//		return FfmDispatch.mdbx_get_attr(txn, dbi, key, data, pattr);
//	}

	/**
	 * Get equal or great item from a table.
	 * \ingroup c_crud
	 *
	 * Briefly this function does the same as \ref mdbx_get() with a few differences:
	 * 1. Return equal or great (due comparison function) key-value pair, but not only exactly matching with the
	 * 		key.
	 * 2. On success return \ref MDBX_SUCCESS if key found exactly, and \ref MDBX_RESULT_TRUE otherwise.
	 * 		Moreover, for tables with \ref MDBX_DUPSORT flag the data argument also will be used to match over
	 *    multi-value/duplicates, and \ref MDBX_SUCCESS will be returned only when BOTH the key and the data
	 *    match exactly.
	 * 3. Updates BOTH the key and the data for pointing to the actual key-value pair inside the table.
	 *
	 * @param txn           [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param dbi           [in] A table handle returned by \ref mdbx_dbi_open().
	 * @param key       [in,out] The key to search for in the table.
	 * @param data      [in,out] The data corresponding to the key.
	 *
	 * @return A non-zero error value on failure and \ref MDBX_RESULT_FALSE or \ref MDBX_RESULT_TRUE on success
	 * 				 (as described above). Some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned
	 *                               by current thread.
	 * \retval MDBX_NOTFOUND      The key was not in the table.
	 * \retval MDBX_EINVAL        An invalid parameter was specified. */

	public static final int mdbx_get_equal_or_great( long txn, long dbi, MDBX_val key, MDBX_val data) {
		return FfmDispatch.mdbx_get_equal_or_great(txn, dbi, key, data);
	}

	/**
	 * Store items into a table.
	 * \ingroup c_crud
	 *
	 * This function stores key/data pairs in the table. The default behavior is to enter the new key/data pair,
	 * replacing any previously existing key if duplicates are disallowed, or adding a duplicate data item if
	 * duplicates are allowed (see \ref MDBX_DUPSORT).
	 *
	 * @param txn        [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param dbi        [in] A table handle returned by \ref mdbx_dbi_open().
	 * @param key        [in] The key to store in the table.
	 * @param data   [in,out] The data to store.
	 * @param flags      [in] Special options for this operation. This parameter must be set to 0 or by bitwise
	 *                        OR'ing together one or more of the values described here:
	 *   - \ref MDBX_NODUPDATA
	 *      Enter the new key-value pair only if it does not already appear
	 *      in the table. This flag may only be specified if the table
	 *      was opened with \ref MDBX_DUPSORT. The function will return
	 *      \ref MDBX_KEYEXIST if the key/data pair already appears in the table.
	 *
	 *  - \ref MDBX_NOOVERWRITE
	 *      Enter the new key/data pair only if the key does not already appear
	 *      in the table. The function will return \ref MDBX_KEYEXIST if the key
	 *      already appears in the table, even if the table supports
	 *      duplicates (see \ref  MDBX_DUPSORT). The data parameter will be set
	 *      to point to the existing item.
	 *
	 *  - \ref MDBX_CURRENT
	 *      Update an single existing entry, but not add new ones. The function will
	 *      return \ref MDBX_NOTFOUND if the given key not exist in the table.
	 *      In case multi-values for the given key, with combination of
	 *      the \ref MDBX_ALLDUPS will replace all multi-values,
	 *      otherwise return the \ref MDBX_EMULTIVAL.
	 *
	 *  - \ref MDBX_RESERVE
	 *      Reserve space for data of the given size, but don't copy the given
	 *      data. Instead, return a pointer to the reserved space, which the
	 *      caller can fill in later - before the next update operation or the
	 *      transaction ends. This saves an extra memcpy if the data is being
	 *      generated later. MDBX does nothing else with this memory, the caller
	 *      is expected to modify all of the space requested. This flag must not
	 *      be specified if the table was opened with \ref MDBX_DUPSORT.
	 *
	 *  - \ref MDBX_APPEND
	 *      Append the given key/data pair to the end of the table. This option
	 *      allows fast bulk loading when keys are already known to be in the
	 *      correct order. Loading unsorted keys with this flag will cause
	 *      a \ref MDBX_EKEYMISMATCH error.
	 *
	 *  - \ref MDBX_APPENDDUP
	 *      As above, but for sorted dup data.
	 *
	 *  - \ref MDBX_MULTIPLE
	 *      Store multiple contiguous data elements in a single request. This flag
	 *      may only be specified if the table was opened with
	 *      \ref MDBX_DUPFIXED. With combination the \ref MDBX_ALLDUPS
	 *      will replace all multi-values.
	 *      The data argument must be an array of two \ref MDBX_val. The `iov_len`
	 *      of the first \ref MDBX_val must be the size of a single data element.
	 *      The `iov_base` of the first \ref MDBX_val must point to the beginning
	 *      of the array of contiguous data elements which must be properly aligned
	 *      in case of table with \ref MDBX_INTEGERDUP flag.
	 *      The `iov_len` of the second \ref MDBX_val must be the count of the
	 *      number of data elements to store. On return this field will be set to
	 *      the count of the number of elements actually written. The `iov_base` of
	 *      the second \ref MDBX_val is unused.
	 *
	 * \see \ref c_crud_hints "Quick reference for Insert/Update/Delete operations"
	 *
	 * @return A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned by current thread.
	 * \retval MDBX_KEYEXIST  The key/value pair already exists in the table.
	 * \retval MDBX_MAP_FULL  The database is full, see \ref mdbx_env_set_mapsize().
	 * \retval MDBX_TXN_FULL  The transaction has too many dirty pages.
	 * \retval MDBX_EACCES    An attempt was made to write in a read-only transaction.
	 * \retval MDBX_EINVAL    An invalid parameter was specified.
	 */

	public static final int mdbx_put( long txn, long dbi, MDBX_val key, MDBX_val data, int flags) {
		return FfmDispatch.mdbx_put(txn, dbi, key, data, flags);
	}

	/**
	 * Store multiple items into a table.
	 *
	 * This function stores multiple key/data pairs in the table. It is a utility function that call
	 * {@link #mdbx_put(long, long, MDBX_val, MDBX_val, int)} and needs to have the multiple flag. It expects
	 * the data to be formatted according the mdbx_put documentation.
	 *
	 * @param txn        [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param dbi        [in] A table handle returned by \ref mdbx_dbi_open().
	 * @param key        [in] The key to store in the table.
	 * @param data1  [in,out] First data element (see doc).
	 * @param data2  [in,out] Second data element (see doc)
	 * @param flags      [in] Special options for this operation. This parameter must be set to 0 or by bitwise
	 *                        OR'ing together one or more of the values described here:
	 *
	 * @return A non-zero error value on failure and 0 on success.
	 *
	 * @see #mdbx_put(long, long, MDBX_val, MDBX_val, int)
	 */

	public static final int mdbx_put_multiple( long txn, long dbi, MDBX_val key, MDBX_val data1, MDBX_val data2, int flags) {
		return FfmDispatch.mdbx_put_multiple(txn, dbi, key, data1, data2, flags);
	}

//
//	public static final int mdbx_put_attr(long txn, long dbi, MDBX_val key, MDBX_val data, long[] attr, int flags) {
//		return FfmDispatch.mdbx_put_attr(txn, dbi, key, data, attr, flags);
//	}

	/**
	 * Replace items in a table.
	 * \ingroup c_crud
	 *
	 * This function allows to update or delete an existing value at the same time as the previous value is
	 * retrieved. If the argument new_data equal is NULL zero, the removal is performed, otherwise the
	 * update/insert.
	 *
	 * The current value may be in an already changed (aka dirty) page. In this case, the page will be
	 * overwritten during the update, and the old value will be lost. Therefore, an additional buffer must be
	 * passed via old_data argument initially to copy the old value. If the buffer passed in is too small, the
	 * function will return \ref MDBX_RESULT_TRUE by setting iov_len field pointed by old_data argument to the
	 * appropriate value, without performing any changes.
	 *
	 * For tables with non-unique keys (i.e. with \ref MDBX_DUPSORT flag), another use case is also possible,
	 * when by old_data argument selects a specific item from multi-value/duplicates with the same key for
	 * deletion or update. To select this scenario in flags should simultaneously specify \ref MDBX_CURRENT and
	 * \ref MDBX_NOOVERWRITE. This combination is chosen because it makes no sense, and thus allows you to
	 * identify the request of such a scenario.
	 *
	 * @param txn           [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param dbi           [in] A table handle returned by \ref mdbx_dbi_open().
	 * @param key           [in] The key to store in the table.
	 * @param new_data      [in] The data to store, if NULL then deletion will be performed.
	 * @param old_data  [in,out] The buffer for retrieve previous value as describe above.
	 * @param flags         [in] Special options for this operation. This parameter must be set to 0 or by
	 *                           bitwise OR'ing together one or more of the values described in \ref mdbx_put()
	 *                           description above, and additionally (\ref MDBX_CURRENT | \ref MDBX_NOOVERWRITE)
	 *                           combination for selection particular item from multi-value/duplicates.
	 *
	 * \see \ref c_crud_hints "Quick reference for Insert/Update/Delete operations"
	 *
	 * @return A non-zero error value on failure and 0 on success.
	 */

	public static final int mdbx_replace( long txn, long dbi, MDBX_val key, MDBX_val new_data, MDBX_val old_data, int flags) {
		return FfmDispatch.mdbx_replace(txn, dbi, key, new_data, old_data, flags);
	}

	/**
	 * Delete items from a table.
	 * \ingroup c_crud
	 *
	 * This function removes key/data pairs from the table.
	 *
	 * \note The data parameter is NOT ignored regardless the table does support sorted duplicate data items or
	 * not. If the data parameter is non-NULL only the matching data item will be deleted. Otherwise, if data
	 * parameter is NULL, any/all value(s) for specified key will be deleted.
	 *
	 * This function will return \ref MDBX_NOTFOUND if the specified key/data pair is not in the table.
	 *
	 * \see \ref c_crud_hints "Quick reference for Insert/Update/Delete operations"
	 *
	 * @param txn   [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param dbi   [in] A table handle returned by \ref mdbx_dbi_open().
	 * @param key   [in] The key to delete from the table.
	 * @param data  [in] The data to delete.
	 *
	 * @return A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_EACCES   An attempt was made to write in a read-only transaction.
	 * \retval MDBX_EINVAL   An invalid parameter was specified.
	 */

	public static final int mdbx_del( long txn, long dbi, MDBX_val key, MDBX_val data) {
		return FfmDispatch.mdbx_del(txn, dbi, key, data);
	}

	/**
	 * Empty or delete and close a table.
	 * \ingroup c_crud
	 *
	 * @see #mdbx_dbi_close(long, long)
	 * @see #mdbx_dbi_open(long, String, int, long[])
	 *
	 * @param txn  [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param dbi  [in] A table handle returned by \ref mdbx_dbi_open().
	 * @param del  [in] `false` to empty the DB, `true` to delete it from the environment and close the DB
	 *                  handle.
	 *
	 * @return A non-zero error value on failure and 0 on success.
	 */

	public static final int mdbx_drop( long txn, long dbi, int del) {
		return FfmDispatch.mdbx_drop(txn, dbi, del);
	}

	/**
	 * Returns fours integers markers (aka "canary") associated with the environment.
	 * \ingroup c_crud
	 *
	 * \see mdbx_canary_put()
	 *
	 * @param txn     [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param canary  [in] The address of an \ref MDBX_canary structure where the information will be copied.
	 *
	 * @return A non-zero error value on failure and 0 on success.
	 */

	public static final int mdbx_canary_get( long txn, MDBX_canary canary) {
		return FfmDispatch.mdbx_canary_get(txn, canary);
	}

	/**
	 * Set integers markers (aka "canary") associated with the environment.
	 * \ingroup c_crud
	 * @see #mdbx_canary_get(long, MDBX_canary)
	 *
	 * @param txn     [in] A transaction handle returned by \ref mdbx_txn_begin()
	 * @param canary  [in] A optional pointer to \ref MDBX_canary structure for `x`, `y` and `z` values from.
	 *            - If canary is NOT NULL then the `x`, `y` and `z` values will be updated from given canary
	 *              argument, but the 'v' be always set to the current transaction number if at least one `x`,
	 *              `y` or `z` values have changed (i.e. if `x`, `y` and `z` have the same values as currently
	 *              present then nothing will be changes or updated).
	 *            - if canary is NULL then the `v` value will be explicitly update to the current transaction
	 *              number without changes `x`, `y` nor `z`.
	 *
	 * @return A non-zero error value on failure and 0 on success.
	 */

	public static final int mdbx_canary_put( long txn, MDBX_canary canary) {
		return FfmDispatch.mdbx_canary_put(txn, canary);
	}


	//====================================================//
	// Cursor methods
	//====================================================//
	/**
	 * <p>
	 * Create a cursor handle for the specified transaction and DBI handle.
	 * </p>
	 *
	 * <p>
	 * Using of the mdbx_cursor_open() is equivalent to calling mdbx_cursor_create() and then mdbx_cursor_bind()
	 * functions.
	 * </p>
	 *
	 * <p>
	 * A cursor cannot be used when its database handle is closed. Nor when its transaction has ended, except
	 * with mdbx_cursor_bind() and mdbx_cursor_renew(). Also it can be discarded with mdbx_cursor_close().
	 * </p>
	 *
	 * <p>
	 * A cursor must be closed explicitly always, before or after its transaction ends. It can be reused with
	 * mdbx_cursor_bind() or mdbx_cursor_renew() before finally closing it.
	 * </p>
	 *
	 * <p>
	 * <b>Note</b> In contrast to LMDB, the MDBX required that any opened cursors can be reused and must be
	 * freed explicitly, regardless ones was opened in a read-only or write transaction. The REASON for this is
	 * eliminates ambiguity which helps to avoid errors such as: use-after-free, double-free, i.e. memory
	 * corruption and segfaults.
	 * </p>
	 *
	 * @param txn      [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param dbi      [in] A table handle returned by \ref mdbx_dbi_open().
	 * @param cursor  [out] Address where the new \ref MDBX_cursor handle will be stored.
	 *
	 * @return A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned
	 *                               by current thread.
	 * \retval MDBX_EINVAL  An invalid parameter was specified. 	 *
	 */

	public static final int mdbx_cursor_open( long txn, long dbi, long[] cursor) {
		return FfmDispatch.mdbx_cursor_open(txn, dbi, cursor);
	}

	/**
	 * Closes a cursor handle without returning error code.
	 * \ingroup c_cursors
	 *
	 * The cursor handle will be freed and must not be used again after this call, but its transaction may still
	 * be live.
	 *
	 * This function returns `void` but panic in case of error. Use \ref mdbx_cursor_close2() if you need to
	 * receive an error code instead of an app crash.
	 *
	 * @see mdbx_cursor_close2
	 *
	 * \note In contrast to LMDB, the MDBX required that any opened cursors can be
	 * reused and must be freed explicitly, regardless ones was opened in a
	 * read-only or write transaction. The REASON for this is eliminates ambiguity
	 * which helps to avoid errors such as: use-after-free, double-free, i.e.
	 * memory corruption and segfaults.
	 *
	 * @param cursor  [in] A cursor handle returned by \ref mdbx_cursor_open() or \ref mdbx_cursor_create().
	 */

	public static final void mdbx_cursor_close( long cursor) {
		FfmDispatch.mdbx_cursor_close(cursor);
	}

	/**
	 * Closes a cursor handle with returning error code.
	 * \ingroup c_cursors
	 *
	 * The cursor handle will be freed and must not be used again after this call, but its transaction may still
	 * be live.
	 *
	 * @see JNI#mdbx_cursor_close(long)
	 *
	 * \note In contrast to LMDB, the MDBX required that any opened cursors can be reused and must be freed
	 * explicitly, regardless ones was opened in a read-only or write transaction. The REASON for this is
	 * eliminates ambiguity which helps to avoid errors such as: use-after-free, double-free, i.e. memory
	 * corruption and segfaults.
	 *
	 * @param cursor  [in] A cursor handle returned by \ref mdbx_cursor_open() or \ref mdbx_cursor_create().
	 * @return A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned by current thread.
	 * \retval MDBX_EINVAL  An invalid parameter was specified.
	 */

	public static final int mdbx_cursor_close2( long cursor) {
		return FfmDispatch.mdbx_cursor_close2(cursor);
	}

	/**
	 * Renew a cursor handle for use within the given transaction.
	 * \ingroup c_cursors
	 *
	 * A cursor may be associated with a new transaction whether the previous transaction is running or finished.
	 *
	 * Using of the `mdbx_cursor_renew()` is equivalent to calling \ref mdbx_cursor_bind() with the DBI-handle
	 * that previously the cursor was used with.
	 *
	 * \note In contrast to LMDB, the MDBX allow any cursor to be re-used by using \ref mdbx_cursor_renew(), to
	 * avoid unnecessary malloc/free overhead until it freed by \ref mdbx_cursor_close().
	 *
	 * @param txn      [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param cursor   [in] A cursor handle returned by \ref mdbx_cursor_open().
	 *
	 * @return A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned by current thread.
	 * \retval MDBX_EINVAL  An invalid parameter was specified.
	 * \retval MDBX_BAD_DBI The cursor was not bound to a DBI-handle or such a handle became invalid.
	 */

	public static final int mdbx_cursor_renew( long txn, long cursor) {
		return FfmDispatch.mdbx_cursor_renew(txn, cursor);
	}

	/**
	 * Bind cursor to specified transaction and DBI-handle.
	 * \ingroup c_cursors
	 *
	 * Using of the `mdbx_cursor_bind()` is equivalent to calling \ref mdbx_cursor_renew() but with specifying
	 * an arbitrary DBI-handle.
	 *
	 * A cursor may be associated with a new transaction, and referencing a new or the same table handle as it
	 * was created with. This may be done whether the previous transaction is live or dead.
	 *
	 * If the transaction is nested, then the cursor should not be used in its parent transaction. Otherwise it
	 * is no way to restore state if this nested transaction will be aborted, nor impossible to define the
	 * expected behavior.
	 *
	 * \note In contrast to LMDB, the MDBX required that any opened cursors can be reused and must be freed
	 * explicitly, regardless ones was opened in a read-only or write transaction. The REASON for this is
	 * eliminates ambiguity which helps to avoid errors such as: use-after-free, double-free, i.e. memory
	 * corruption and segfaults.
	 *
	 * @param txn      [in] A transaction handle returned by \ref mdbx_txn_begin().
	 * @param dbi      [in] A table handle returned by \ref mdbx_dbi_open().
	 * @param cursor   [in] A cursor handle returned by \ref mdbx_cursor_create().
	 *
	 * @return A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned by current thread.
	 * \retval MDBX_EINVAL  An invalid parameter was specified.
	 */

	public static final int mdbx_cursor_bind( long txn, long cursor, long dbi) {
		return FfmDispatch.mdbx_cursor_bind(txn, cursor, dbi);
	}

	/**
	 * Unbind cursor from a transaction.
	 *
	 * Unbound cursor is disassociated with any transactions but still holds the original DBI-handle internally.
	 * Thus it could be renewed with any running transaction or closed.
	 *
	 * If the transaction is nested, then the cursor should not be used in its parent transaction. Otherwise it
	 * is no way to restore state if this nested transaction will be aborted, nor impossible to define the
	 * expected behavior.
	 *
	 * \note In contrast to LMDB, the MDBX required that any opened cursors can be reused and must be freed
	 * explicitly, regardless ones was opened in a read-only or write transaction. The REASON for this is
	 * eliminates ambiguity which helps to avoid errors such as: use-after-free, double-free, i.e. memory
	 * corruption and segfaults.
	 *
	 * @param cursor [in]
	 *          A cursor handle returned by \ref mdbx_cursor_open().
	 *
	 * @return A non-zero error value on failure and 0 on success.
	 *
	 * @see #mdbx_cursor_renew(long, long)
	 * @see #mdbx_cursor_bind(long, long, long)
	 * @see #mdbx_cursor_close(long)
	 * @see #mdbx_cursor_reset(long)
	 */

	public static final int mdbx_cursor_unbind( long cursor) {
		return FfmDispatch.mdbx_cursor_unbind(cursor);
	}

	/**
	 * Resets the cursor state.
	 *
	 * Resetting the cursor causes it to become unset and prevents it from performing relative positioning
	 * operations, retrieving, or modifying data until it is set to a position independent of the current one.
	 * This allows the application to prevent further operations without first positioning the cursor.
	 *
	 * @param cursor [in]
	 *          A pointer to the cursor.
	 *
	 * @return The result of the scan operation, or an error code.
	 */

	public static final int mdbx_cursor_reset( long cursor) {
		return FfmDispatch.mdbx_cursor_reset(cursor);
	}

	/**
	 * Copy cursor position and state.
	 * \ingroup c_cursors
	 *
	 * @param src       [in] A source cursor handle returned by \ref mdbx_cursor_create() or
	 *                       \ref mdbx_cursor_open().
	 * @param dest  [in,out] A destination cursor handle returned by \ref mdbx_cursor_create() or
	 *                       \ref mdbx_cursor_open().
	 *
	 * @return A non-zero error value on failure and 0 on success.
	 */

	public static final int mdbx_cursor_copy( long src, long dest) {
		return FfmDispatch.mdbx_cursor_copy(src, dest);
	}

	/**
	 * Compares cursor positions.
	 *
	 * The function is intended to compare the positions of two initialized/installed cursors associated with
	 * the same transaction and one table (DBI descriptor). If the cursors are associated with different
	 * transactions, or with different tables, or one of them is not initialized, the result of the comparison
	 * is undefined (the behavior may be changed in future versions).
	 *
	 * @param left [in]  Left cursor for comparing positions.
	 * @param right [in] Right cursor for comparing positions.
	 * @param ignore_multival [in] Boolean flag that affects the result only when comparing cursors for tables
	 * 					with multi-values, i.e. with the \ref MDBX_DUPSORT flag. If `true`, cursor positions are
	 * 					compared only by keys, without taking into account positioning among multi-values. Otherwise, if
	 * 					`false`, if the positions by keys match, the positions by multi-values ​​are also compared.
	 *
	 * @return A signed value in the semantics of the `&lt;=&gt;` operator (less than zero, zero, or greater
	 *          than zero) as a result of comparing cursor positions.
	 */

	public static final int mdbx_cursor_compare( long left, long right, int ignore_multival) {
		return FfmDispatch.mdbx_cursor_compare(left, right, ignore_multival);
	}  //bool

	/**
	 * Retrieve by cursor.
	 * \ingroup c_crud
	 *
	 * This function retrieves key/data pairs from the table. The address and length of the key are returned in
	 * the object to which key refers (except for the case of the \ref MDBX_SET option, in which the key object
	 * is unchanged), and the address and length of the data are returned in the object to which data refers.
	 *
	 * @see #mdbx_get(long, long, MDBX_val, MDBX_val)
	 *
	 * \note The memory pointed to by the returned values is owned by the database. The caller MUST not dispose
	 * of the memory, and MUST not modify it in any way regardless in a read-only nor read-write transactions!
	 * For case a database opened without the \ref MDBX_WRITEMAP modification attempts likely will cause a
	 * `SIGSEGV`. However, when a database opened with the \ref MDBX_WRITEMAP or in case values returned inside
	 * read-write transaction are located on a "dirty" (modified and pending to commit) pages, such modification
	 * will silently accepted and likely will lead to DB and/or data corruption.
	 *
	 * @param cursor    [in] A cursor handle returned by \ref mdbx_cursor_open().
	 * @param key   [in,out] The key for a retrieved item.
	 * @param data  [in,out] The data of a retrieved item.
	 * @param op        [in] A cursor operation \ref MDBX_cursor_op.
	 *
	 * @return A non-zero error value on failure and 0 on success,
	 *          some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned
	 *                               by current thread.
	 * \retval MDBX_NOTFOUND  No matching key found.
	 * \retval MDBX_EINVAL    An invalid parameter was specified.
	 */

	public static final int mdbx_cursor_get( long cursor, MDBX_val key, MDBX_val data, int op) {
		return FfmDispatch.mdbx_cursor_get(cursor, key, data, op);
	}

	/**
	 * Retrieve multiple non-dupsort key/value pairs by cursor.
	 * \ingroup c_crud
	 *
	 * This function retrieves multiple key/data pairs from the table without \ref MDBX_DUPSORT option. For
	 * `MDBX_DUPSORT` tables please use \ref MDBX_GET_MULTIPLE and \ref MDBX_NEXT_MULTIPLE.
	 *
	 * The number of key and value items is returned in the `size_t count` refers. The addresses and lengths of
	 * the keys and values are returned in the array to which `pairs` refers.
	 *
	 * @see #mdbx_cursor_get(long, MDBX_val, MDBX_val, int)
	 *
	 * \note The memory pointed to by the returned values is owned by the database. The caller MUST not dispose
	 * of the memory, and MUST not modify it in any way regardless in a read-only nor read-write transactions!
	 * For case a database opened without the \ref MDBX_WRITEMAP modification attempts likely will cause a
	 * `SIGSEGV`. However, when a database opened with the \ref MDBX_WRITEMAP or in case values returned inside
	 * read-write transaction are located on a "dirty" (modified and pending to commit) pages, such modification
	 * will silently accepted and likely will lead to DB and/or data corruption.
	 *
	 * @param cursor     [in] A cursor handle returned by \ref mdbx_cursor_open().
	 * @param count     [out] The number of key and value item returned, on success it always be the even
	 *                        because the key-value pairs are returned.
	 * @param pairs  [in,out] A pointer to the array of key value pairs.
	 * @param limit      [in] The size of pairs buffer as the number of items, but not a pairs.
	 * @param op         [in] A cursor operation \ref MDBX_cursor_op (only \ref MDBX_FIRST and \ref MDBX_NEXT
	 *                       are supported).
	 *
	 * @return A non-zero error value on failure and 0 on success,
	 *          some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned
	 *                               by current thread.
	 * \retval MDBX_NOTFOUND         No any key-value pairs are available.
	 * \retval MDBX_ENODATA          The cursor is already at the end of data.
	 * \retval MDBX_RESULT_TRUE      The returned chunk is the last one,
	 *                               and there are no pairs left.
	 * \retval MDBX_EINVAL           An invalid parameter was specified.
	 */

	public static final int mdbx_cursor_get_batch( long cursor, long[] count, MDBX_val pairs, long limit, int op) {
		return FfmDispatch.mdbx_cursor_get_batch(cursor, count, pairs, limit, op);
	}

	/**
	 * Utility function for use in utilities.
	 *
	 * When using user-defined comparison functions (aka custom comparison functions), checking the order of
	 * keys may lead to incorrect results and returning the error \ref MDBX_CORRUPTED.
	 *
	 * This function disables the control of the order of keys on pages when reading database pages for this
	 * cursor, and thus allows reading data in the absence/unavailability of the used comparison functions.
	 *
	 * check avoid_custom_comparators
	 *
	 * @param cursor cursor pointer
	 * @return The result of the scanning operation, or an error code.
	 */

	public static final int mdbx_cursor_ignord( long cursor) {
		return FfmDispatch.mdbx_cursor_ignord(cursor);
	}

	/**
	 * The type of the predicative callback functions used \ref mdbx_cursor_scan() and \ref
	 * mdbx_cursor_scan_from() to probe key-value pairs.
	 *
	 * @param [in,out]
	 *          context Pointer to the context with the information needed to evaluate , which is completely
	 *          prepared and controlled by you.
	 * @param [in]
	 *          key Key to evaluate by the user-defined function.
	 * @param [in]
	 *          value Value to evaluate by the user-defined function.
	 * @param [in,out]
	 *          arg Additional argument to the predicative function, which is completely prepared and controlled
	 *          by you.
	 *
	 * @return The result of checking whether the passed key-value pair matches the target. Otherwise, an error
	 *         code that aborts the scan and is returned unchanged as the result of the \ref mdbx_cursor_scan()
	 *         or \ref mdbx_cursor_scan_from() functions.
	 *
	 * @retval MDBX_RESULT_TRUE if the passed key-value pair matches the searched for and the scan should be
	 *         terminated.
	 * @retval MDBX_RESULT_FALSE if the passed key-value pair does NOT match the searched for and the scan
	 *         should continue.
	 * @retval ELSE any value other than \ref MDBX_RESULT_TRUE and \ref MDBX_RESULT_FALSE is considered an error
	 *         indicator and is returned unchanged as the scan result.
	 *
	 * @see mdbx_cursor_scan()
	 * @see mdbx_cursor_scan_from()
	 */
//
//	public static final int mdbx_cursor_ignor2d(long cursor) {
//		return FfmDispatch.mdbx_cursor_ignor2d(cursor);
//	}
//	typedef int(MDBX_predicate_func)(void *context, MDBX_val *key, MDBX_val *value, void *arg) MDBX_CXX17_NOEXCEPT;

/**
 * Scans a table using the predicate passed in, with reduced overhead.
 *
 * Implements functionality similar to the `std::find_if&lt;&gt;()` pattern using a cursor and a user-supplied
 * predicate function, while saving on the associated overhead, including not performing some of the checks
 * inside the record iteration loop and potentially reducing the number of DSO cross-border calls.
 *
 * The function takes a cursor, which must be bound to some transaction and a DBI table handle (a named user
 * subDB), and performs an initial cursor positioning determined by the `start_op` argument. It then evaluates
 * each key-value pair using the `predicate` predicate function you provide, and then, if necessary, moves to
 * the next element using the `turn_op` operation, until one of four events occurs: - the end of data is
 * reached; - an error will occur while positioning the cursor; - the evaluation function will return \ref
 * MDBX_RESULT_TRUE, signaling the need to stop further scanning; - the evaluation function will return a
 * value different from \ref MDBX_RESULT_FALSE and \ref MDBX_RESULT_TRUE, signaling an error.
 *
 * @param cursor [in,out]
 *          A cursor to perform a scan operation, associated with an active transaction and a DBI
 *          handle to the table. For example, a cursor created by \ref mdbx_cursor_open().
 * @param predicate [in]
 *          A predicate function for evaluating iterable key-value pairs, see \ref
 *          MDBX_predicate_func for details.
 * @param context [in,out]
 *          A pointer to a context with the information needed to evaluate , which is entirely
 *          prepared and controlled by you.
 * @param start_op [in]
 *          A start operation for positioning the cursor, see \ref MDBX_cursor_op for details. To
 *          scan without changing the starting position of the cursor, use \ref MDBX_GET_CURRENT. Valid values
 *          ​​are \ref MDBX_FIRST, \ref MDBX_FIRST_DUP, \ref MDBX_LAST, \ref MDBX_LAST_DUP, \ref
 *          MDBX_GET_CURRENT, and \ref MDBX_GET_MULTIPLE.
 * @param turn_op [in]
 *          The cursor positioning operation for moving to the next element. Valid values ​​are \ref
 *          MDBX_NEXT, \ref MDBX_NEXT_DUP, \ref MDBX_NEXT_NODUP, \ref MDBX_PREV, \ref MDBX_PREV_DUP, \ref
 *          MDBX_PREV_NODUP, and \ref MDBX_NEXT_MULTIPLE and \ref MDBX_PREV_MULTIPLE.
 * @param arg [in,out]
 *          An additional argument to the predictive function, which is entirely prepared and controlled
 *          by you.
 *
 *          \note When using \ref MDBX_GET_MULTIPLE, \ref MDBX_NEXT_MULTIPLE or \ref MDBX_PREV_MULTIPLE, be
 *          careful about the batch specifics of passing values ​​through predictive function parameters.
 *
 * @return The result of the scan operation, or an error code. *
 * retval MDBX_RESULT_TRUE if a key-value pair for which the predictive function returned \ref
 *         MDBX_RESULT_TRUE is found.
 * retval MDBX_RESULT_FALSE if a matching key-value pair is NOT found, the search has reached the end of the
 *         data or there is no data to search.
 * retval ELSE any value other than \ref MDBX_RESULT_TRUE and \ref MDBX_RESULT_FALSE is a course positioning
 *         error code, or a user-defined search stop code or error condition. *
 * see MDBX_predicate_func
 * @see #mdbx_cursor_scan_from(long, long, long, int, MDBX_val, MDBX_val, int, long)
 */

	public static final int mdbx_cursor_scan( long cursor, long predicate, long context, int start_op, int turn_op, long arg) {
		return FfmDispatch.mdbx_cursor_scan(cursor, predicate, context, start_op, turn_op, arg);
	}
//	LIBMDBX_API int mdbx_cursor_scan(MDBX_cursor *cursor,
//	                                 MDBX_predicate_func *predicate, void *context,
//	                                 MDBX_cursor_op start_op,
//	                                 MDBX_cursor_op turn_op, void *arg);

/**
 * Scans the table using the passed predicate, starting with the passed key-value pair, with reduced overhead.
 *
 * The function takes a cursor, which must be bound to some transaction and a DBI handle to a table (a named
 * user subDB), and performs an initial cursor positioning determined by the `from_op` argument. and the
 * `from_key` and `from_value` arguments. Next, it evaluates each key-value pair using the `predicate`
 * predicate function you provide, and then, if necessary, moves on to the next element using the `turn_op`
 * operation, until one of four things happens:
 * <ul>
 * 	<li>the end of data is reached;</li>
 * 	<li>an error occurs while positioning the cursor;</li>
 * 	<li>the evaluation function returns {@link #MDBX_RESULT_TRUE}, signaling that further scanning should be
 * 	stopped;</li>
 * 	<li>the evaluation function will return a value different from \ref MDBX_RESULT_FALSE and
 * {@link #MDBX_RESULT_TRUE} indicating an error.</li>
 * </ul>
 *
 * @param cursor [in,out]
 *          The cursor to perform the scan operation, associated with the active transaction and the
 *          DBI handle of the table. For example, a cursor created by {@link #mdbx_cursor_open(long, long, long[])}.
 * @param predicate [in]
 *          A predicate function for evaluating iterable key-value pairs, see MDBX_predicate_func
 *          for details.
 * @param context [in,out]
 *          A pointer to a context with the information needed for the evaluation, which is completely
 *          prepared and controlled by you.
 * @param from_op [in]
 *          The operation of positioning the cursor to the starting position, see MDBX_cursor_op
 *          for details. Valid values ​​{@link JNI#MDBX_GET_BOTH}, * \ref MDBX_GET_BOTH_RANGE, \ref MDBX_SET_KEY, *
 *          {@link JNI#MDBX_SET_LOWERBOUND}, \ref MDBX_SET_UPPERBOUND, * \ref MDBX_TO_KEY_LESSER_THAN, * \ref
 *          MDBX_TO_KEY_LESSER_OR_E QUAL, * \ref MDBX_TO_KEY_EQUAL, * \ref MDBX_TO_KEY_GREATER_OR_EQUAL, *
 *          \ref MDBX_TO_KEY_GREATER_THAN, * \ref MDBX_TO_EXACT_KEY_VALUE_LESSER_THAN, * \ref
 *          MDBX_TO_EXACT_KEY_VALUE_LESSER_OR_EQUAL, *\ref MDBX_TO_EXACT_KEY_VALUE_EQUAL, \ref
 *          MDBX_TO_EXACT_KEY_VALUE_GREATER_OR_EQUAL, \ref MDBX_TO_EXACT_KEY_VALUE_GREATER_THAN, \ref
 *          MDBX_TO_PAIR_LESSER_THAN, {@link JNI#MDBX_TO_PAIR_LESSER_OR_EQUAL}, {@link JNI#MDBX_TO_PAIR_EQUAL}, \ref
 *          MDBX_TO_PAIR_GREATER_OR_EQUAL, {@link JNI#MDBX_TO_PAIR_GREATER_THAN}, and {@link JNI#MDBX_GET_MULTIPLE}
 * @param from_key [in,out]
 *          Pointer to the key used for both the initial positioning and subsequent iterations of the
 *          transition. * @param [in,out] from_value Pointer to the value used for both the initial
 *          positioning and subsequent iterations of the transition
 * @param from_value [in,out]
 *          Pointer to a value used for both the initial positioning and subsequent iterations of the transition.
 * @param turn_op [in]
 *          Operation of positioning the cursor for the transition to the next element. Allowed values
 *          \ref MDBX_NEXT, \ref MDBX_NEXT_DUP, \ref MDBX_NEXT_NODUP, \ref MDBX_PREV, \ref MDBX_PREV_DUP, \ref
 *          MDBX_PREV_NODUP, and \ref MDBX_NEXT_MULTIPLE and \ref MDBX_PREV_MULTIPLE.
 * @param arg [in,out]
 *          An additional argument to the predicate function, which is entirely prepared and controlled by
 *          you.
 *
 *          \note When using \ref MDBX_GET_MULTIPLE, \ref MDBX_NEXT_MULTIPLE or \ref MDBX_PREV_MULTIPLE, be
 *          careful about the packet specifics of passing values ​​through the parameters of the predictive
 *          function.
 *
 * @return The result of the scan operation, or an error code.
 *
 * retval MDBX_RESULT_TRUE if a key-value pair for which the predictive function returned \ref
 *         MDBX_RESULT_TRUE was found.
 * retval MDBX_RESULT_FALSE if a matching key-value pair was NOT found, the search reached the end of data,
 *         or there was no data to search.
 * retval ELSE any value other than \ref MDBX_RESULT_TRUE and \ref MDBX_RESULT_FALSE is a course positioning
 *         error code, or a user-defined search stop code or an error condition. *
 * see #MDBX_predicate_func
 * @see #mdbx_cursor_scan(long, long, long, int, int, long)
 */

	public static final int mdbx_cursor_scan_from( long cursor, long predicate, long context, int from_op, MDBX_val from_key, MDBX_val from_value, int turn_op, long arg) {
		return FfmDispatch.mdbx_cursor_scan_from(cursor, predicate, context, from_op, from_key, from_value, turn_op, arg);
	}
//	LIBMDBX_API int mdbx_cursor_scan_from(MDBX_cursor *cursor,
//	                                      MDBX_predicate_func *predicate,
//	                                      void *context,
//	                                      MDBX_cursor_op from_op,
//	                                      MDBX_val *from_key,
//	                                      MDBX_val *from_value,
//	                                      MDBX_cursor_op turn_op,
//	                                      void *arg);


	public static final int mdbx_cursor_put( long cursor, MDBX_val key, MDBX_val data, int flags) {
		return FfmDispatch.mdbx_cursor_put(cursor, key, data, flags);
	}

//
//public static final int mdbx_cursor_get_attr(long cursor, MDBX_val key, MDBX_val data, long[] pattr, int op) {
//		return FfmDispatch.mdbx_cursor_get_attr(cursor, key, data, pattr, op);
//	}

//
//	public static final int mdbx_cursor_put_attr(long cursor, MDBX_val key, MDBX_val data, long attr, int flags) {
//		return FfmDispatch.mdbx_cursor_put_attr(cursor, key, data, attr, flags);
//	}

	/**
	 * Return the cursor's transaction handle.
	 * \ingroup c_cursors
	 *
	 * @param cursor [in] A cursor handle returned by \ref mdbx_cursor_open().
	 * @return cursor transaction handle
	 */

	public static final long mdbx_cursor_txn( long cursor) {
		return FfmDispatch.mdbx_cursor_txn(cursor);
	}

	/**
	 * Return the cursor's table handle.
	 * \ingroup c_cursors
	 *
	 * @param cursor  [in] A cursor handle returned by \ref mdbx_cursor_open().
	 * @return cursor table handle
	 */

	public static final long mdbx_cursor_dbi( long cursor) {
		return FfmDispatch.mdbx_cursor_dbi(cursor);
	}

	/**
	 * Delete current key/data pair.
	 * \ingroup c_crud
	 *
	 * This function deletes the key/data pair to which the cursor refers. This does not invalidate the cursor,
	 * so operations such as \ref MDBX_NEXT can still be used on it. Both \ref MDBX_NEXT and
	 * \ref MDBX_GET_CURRENT will return the same record after this operation.
	 *
	 * @param cursor  [in] A cursor handle returned by mdbx_cursor_open().
	 * @param flags   [in] Options for this operation. This parameter must be set to one of the values described
	 *                     here.
	 *  - \ref MDBX_CURRENT Delete only single entry at current cursor position.
	 *  - \ref MDBX_ALLDUPS or \ref MDBX_NODUPDATA (supported for compatibility)
	 *      Delete all of the data items for the current key. This flag has effect
	 *      only for table(s) was created with \ref MDBX_DUPSORT.
	 *
	 * \see \ref c_crud_hints "Quick reference for Insert/Update/Delete operations"
	 *
	 * @return A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned by current thread.
	 * \retval MDBX_MAP_FULL      The database is full, see \ref mdbx_env_set_mapsize().
	 * \retval MDBX_TXN_FULL      The transaction has too many dirty pages.
	 * \retval MDBX_EACCES        An attempt was made to write in a read-only transaction.
	 * \retval MDBX_EINVAL        An invalid parameter was specified.
	 */

	public static final int mdbx_cursor_del( long cursor, int flags) {
		return FfmDispatch.mdbx_cursor_del(cursor, flags);
	}

	/**
	 * Return count values (aka duplicates) for current key.
	 * \ingroup c_crud
	 *
	 * @see #mdbx_cursor_count_ex(long, long[], MDBX_stat, long)
	 *
	 * This call is valid for all tables, but reasonable only for that support sorted duplicate data items
	 * \ref MDBX_DUPSORT.
	 *
	 * @param cursor    [in] A cursor handle returned by \ref mdbx_cursor_open().
	 * @param count    [out] Address where the count will be stored.
	 *
	 * @return A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned by current thread.
	 * \retval MDBX_EINVAL   Cursor is not initialized, or an invalid parameter was specified.
	 */

	public static final int mdbx_cursor_count( long cursor, long[] count) {
		return FfmDispatch.mdbx_cursor_count(cursor, count);
	}

	/**
	 * Return count values (aka duplicates) and nested b-tree statistics for current key.
	 * \ingroup c_crud
	 *
	 * @see #mdbx_dbi_stat(long, long, MDBX_stat, long)
	 * @see #mdbx_dbi_dupsort_depthmask(long, long, long[])
	 * @see #mdbx_cursor_count(long, long[])
	 *
	 * This call is valid for all tables, but reasonable only for that support sorted duplicate data items
	 * \ref MDBX_DUPSORT.
	 *
	 * @param cursor    [in] A cursor handle returned by \ref mdbx_cursor_open().
	 * @param count    [out] Address where the count will be stored.
	 * @param stat     [out] The address of an \ref MDBX_stat structure where the statistics of a nested b-tree
	 *                       will be copied.
	 * @param bytes     [in] The size of \ref MDBX_stat.
	 *
	 * @return A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned by current thread.
	 * \retval MDBX_EINVAL   Cursor is not initialized, or an invalid parameter was specified.
	 */

	public static final int mdbx_cursor_count_ex( long cursor, long[] count, MDBX_stat stat, long bytes) {
		return FfmDispatch.mdbx_cursor_count_ex(cursor, count, stat, bytes);
	}

	/**
	 * Determines whether the cursor is pointed to a key-value pair or not, i.e. was not positioned or points to
	 * the end of data.
	 * \ingroup c_cursors
	 *
	 * @param cursor    [in] A cursor handle returned by \ref mdbx_cursor_open().
	 *
	 * @return A \ref MDBX_RESULT_TRUE or \ref MDBX_RESULT_FALSE value, otherwise the error code.
	 * \retval MDBX_RESULT_TRUE    No more data available or cursor not positioned
	 * \retval MDBX_RESULT_FALSE   A data is available
	 * \retval Otherwise the error code */

	public static final int mdbx_cursor_eof( long cursor) {
		return FfmDispatch.mdbx_cursor_eof(cursor);
	}

	/**
	 * Determines whether the cursor is pointed to the first key-value pair or not.
	 *
	 * @param cursor [in]
	 *          A cursor handle returned by \ref mdbx_cursor_open().
	 *
	 * @return A MDBX_RESULT_TRUE or MDBX_RESULT_FALSE value, otherwise the error code.
	 * retval MDBX_RESULT_TRUE Cursor positioned to the first key-value pair
	 * retval MDBX_RESULT_FALSE Cursor NOT positioned to the first key-value pair \retval Otherwise the error
	 *         code
	 */

	public static final int mdbx_cursor_on_first( long cursor) {
		return FfmDispatch.mdbx_cursor_on_first(cursor);
	}

	/**
	 * Determines whether the cursor is on the first or only multi-value matching the key.
	 *
	 * @param cursor [in]
	 *          The cursor created by \ref mdbx_cursor_open().
	 * @return The value of \ref MDBX_RESULT_TRUE, or \ref MDBX_RESULT_FALSE, otherwise an error code.
	 * retval MDBX_RESULT_TRUE the cursor is positioned on the first or only multi-value matching the key.
	 * retval MDBX_RESULT_FALSE the cursor is NOT positioned on the first or only multi-value matching the key.
	 * retval OTHERWISE the error code.
	 */

	public static final int mdbx_cursor_on_first_dup( long cursor) {
		return FfmDispatch.mdbx_cursor_on_first_dup(cursor);
	}

	/**
	 * Determines whether the cursor is on the last or only multi-value matching the key.
	 *
	 * @param cursor [in]
	 *          The cursor created by \ref mdbx_cursor_open().
	 * @return The value of \ref MDBX_RESULT_TRUE, or \ref MDBX_RESULT_FALSE, otherwise an error code.
	 * retval MDBX_RESULT_TRUE the cursor is positioned on the last or only multi-value matching the key.
	 * retval MDBX_RESULT_FALSE the cursor is NOT positioned on the last or only multi-value matching the key.
	 * retval OTHERWISE the error code.
	 */

	public static final int mdbx_cursor_on_last_dup( long cursor) {
		return FfmDispatch.mdbx_cursor_on_last_dup(cursor);
	}

	/**
	 * Determines whether the cursor is pointed to the last key-value pair or not.
	 * \ingroup c_cursors
	 *
	 * @param cursor    [in] A cursor handle returned by \ref mdbx_cursor_open().
	 *
	 * @return A \ref MDBX_RESULT_TRUE or \ref MDBX_RESULT_FALSE value, otherwise the error code.
	 * \retval MDBX_RESULT_TRUE   Cursor positioned to the last key-value pair
	 * \retval MDBX_RESULT_FALSE  Cursor NOT positioned to the last key-value pair
	 * \retval Otherwise the error code
	 */

	public static final int mdbx_cursor_on_last( long cursor) {
		return FfmDispatch.mdbx_cursor_on_last(cursor);
	}

	/**
	 * Create a cursor handle but not bind it to transaction nor DBI-handle.
	 * \ingroup c_cursors
	 *
	 * A cursor cannot be used when its table handle is closed. Nor when its transaction has ended, except with
	 * \ref mdbx_cursor_bind() and \ref mdbx_cursor_renew(). Also it can be discarded with
	 * \ref mdbx_cursor_close().
	 *
	 * A cursor must be closed explicitly always, before or after its transaction ends. It can be reused with
	 * \ref mdbx_cursor_bind() or \ref mdbx_cursor_renew() before finally closing it.
	 *
	 * \note In contrast to LMDB, the MDBX required that any opened cursors can be reused and must be freed
	 * explicitly, regardless ones was opened in a read-only or write transaction. The REASON for this is
	 * eliminates ambiguity which helps to avoid errors such as: use-after-free, double-free, i.e. memory
	 * corruption and segfaults.
	 *
	 * @param ctx [in] A pointer to application context to be associated with created cursor and could be
	 *                 retrieved by \ref mdbx_cursor_get_userctx() until cursor closed.
	 *
	 * @return Created cursor handle or NULL in case out of memory.
	 */

	public static final long mdbx_cursor_create( long ctx) {
		return FfmDispatch.mdbx_cursor_create(ctx);
	}

	/**
	 * Get the application information associated with the MDBX_cursor.
	 * \ingroup c_cursors
	 * @see #mdbx_cursor_set_userctx(long, long)
	 *
	 * @param cursor  [in] An cursor handle returned by \ref mdbx_cursor_create() or \ref mdbx_cursor_open().
	 * @return The pointer which was passed via the `context` parameter of `mdbx_cursor_create()` or set by
	 *         \ref mdbx_cursor_set_userctx(), or `NULL` if something wrong. */

	public static final long mdbx_cursor_get_userctx( long cursor) {
		return FfmDispatch.mdbx_cursor_get_userctx(cursor);
	}

	/**
	 * Set application information associated with the cursor.
	 * \ingroup c_cursors
	 *
	 * @see #mdbx_cursor_get_userctx(long)
	 *
	 * @param cursor  [in] An cursor handle returned by \ref mdbx_cursor_create() or \ref mdbx_cursor_open().
	 * @param ctx     [in] An arbitrary pointer for whatever the application needs.
	 *
	 * @return A non-zero error value on failure and 0 on success.
	 */

	public static final int mdbx_cursor_set_userctx( long cursor, long ctx) {
		return FfmDispatch.mdbx_cursor_set_userctx(cursor, ctx);
	}

	//====================================================//
	// Compare methods (and K2V and V2K functions)
	//====================================================//

	public static final int mdbx_cmp( long txn, long dbi, MDBX_val a, MDBX_val b) {
		return FfmDispatch.mdbx_cmp(txn, dbi, a, b);
	}


	public static final int mdbx_dcmp( long txn, long dbi, MDBX_val a, MDBX_val b) {
		return FfmDispatch.mdbx_dcmp(txn, dbi, a, b);
	}


	public static final long mdbx_get_keycmp( int flags) {
		return FfmDispatch.mdbx_get_keycmp(flags);
	}


	public static final long mdbx_get_datacmp( int flags) {
		return FfmDispatch.mdbx_get_datacmp(flags);
	}


	public static final long mdbx_key_from_jsonInteger( long json_integer) {
		return FfmDispatch.mdbx_key_from_jsonInteger(json_integer);
	}


	public static final long mdbx_key_from_double( double ieee754_64bit) {
		return FfmDispatch.mdbx_key_from_double(ieee754_64bit);
	}


	public static final long mdbx_key_from_ptrdouble( long[] ieee754_64bit) {
		return FfmDispatch.mdbx_key_from_ptrdouble(ieee754_64bit);
	}


	public static final int mdbx_key_from_float( float ieee754_32bit) {
		return FfmDispatch.mdbx_key_from_float(ieee754_32bit);
	}


	public static final int mdbx_key_from_ptrfloat( long[] ieee754_32bit) {
		return FfmDispatch.mdbx_key_from_ptrfloat(ieee754_32bit);
	}


	public static final long mdbx_key_from_int64( long i64) {
		return FfmDispatch.mdbx_key_from_int64(i64);
	}


	public static final int mdbx_key_from_int32( int i32) {
		return FfmDispatch.mdbx_key_from_int32(i32);
	}


	public static final long mdbx_jsonInteger_from_key( MDBX_val val) {
		return FfmDispatch.mdbx_jsonInteger_from_key(val);
	}


	public static final double mdbx_double_from_key( MDBX_val val) {
		return FfmDispatch.mdbx_double_from_key(val);
	}


	public static final float mdbx_float_from_key( MDBX_val val) {
		return FfmDispatch.mdbx_float_from_key(val);
	}


	public static final int mdbx_int32_from_key( MDBX_val val) {
		return FfmDispatch.mdbx_int32_from_key(val);
	}


	public static final long mdbx_int64_from_key( MDBX_val val) {
		return FfmDispatch.mdbx_int64_from_key(val);
	}

	//====================================================//
	// Limits methods
	//====================================================//

	public static final long mdbx_limits_dbsize_min( long pagesize) {
		return FfmDispatch.mdbx_limits_dbsize_min(pagesize);
	}


	public static final long mdbx_limits_dbsize_max( long pagesize) {
		return FfmDispatch.mdbx_limits_dbsize_max(pagesize);
	}

	/**
	 * Returns minimal key size in bytes for given table flags.
	 * \ingroup c_statinfo
	 * \see #db_flags
	 * @param flags flags
	 * @return size in bytes
	 */

	public static final long mdbx_limits_keysize_min( long flags) {
		return FfmDispatch.mdbx_limits_keysize_min(flags);
	}


	public static final long mdbx_limits_keysize_max( long pagesize, long flags) {
		return FfmDispatch.mdbx_limits_keysize_max(pagesize, flags);
	}


	public static final long mdbx_limits_pgsize_min() {
		return FfmDispatch.mdbx_limits_pgsize_min();
	}


	public static final long mdbx_limits_pgsize_max() {
		return FfmDispatch.mdbx_limits_pgsize_max();
	}

	/**
	 * Returns maximal write transaction size (i.e. limit for summary volume of dirty pages) in bytes for given
	 * page size, or -1 if pagesize is invalid.
	 * \ingroup c_statinfo
	 * @param pagesize page size
	 * @return maximum write transaction size in bytes
	 */

	public static final long mdbx_limits_txnsize_max( long pagesize) {
		return FfmDispatch.mdbx_limits_txnsize_max(pagesize);
	}

	/**
	 * brief Returns minimal data size in bytes for given table flags.
	 * \ingroup c_statinfo
	 * \see db_flags
	 * @param flags flags
	 * @return size in bytes
	 */

	public static final long mdbx_limits_valsize_min( long flags) {
		return FfmDispatch.mdbx_limits_valsize_min(flags);
	}

	/**
	 * Returns maximal data size in bytes for given page size and table flags, or -1 if pagesize is invalid.
	 * \ingroup c_statinfo
	 * \see db_flags
	 * @param pagesize page size
	 * @param flags flags
	 * @return size in bytes
	 */

	public static final long mdbx_limits_valsize_max( long pagesize, long flags) {
		return FfmDispatch.mdbx_limits_valsize_max(pagesize, flags);
	}

	/**
	 * Returns maximal size of key-value pair to fit in a single page with the given size and table flags, or -1
	 * if pagesize is invalid.
	 * \ingroup c_statinfo
	 * \see db_flags
	 * @param pagesize page size
	 * @param flags flags
	 * @return size in bytes
	 */

	public static final long mdbx_limits_pairsize4page_max( long pagesize, long flags) {
		return FfmDispatch.mdbx_limits_pairsize4page_max(pagesize, flags);
	}

	/**
	 * Returns maximal data size in bytes to fit in a leaf-page or single large/overflow-page with the given
	 * page size and table flags, or -1 if pagesize is invalid.
	 * \ingroup c_statinfo
	 * \see db_flags
	 * @param pagesize page size
	 * @param flags flags
	 * @return size in bytes
	 */

	public static final long mdbx_limits_valsize4page_max( long pagesize, long flags) {
		return FfmDispatch.mdbx_limits_valsize4page_max(pagesize, flags);
	}

	//====================================================//
	// Reader methods
	//====================================================//

	public static final int mdbx_reader_check( long env, int[] dead) {
		return FfmDispatch.mdbx_reader_check(env, dead);
	}


	public static final int mdbx_reader_list( long env, long func, long ctx) {
		return FfmDispatch.mdbx_reader_list(env, func, ctx);
	}

	//====================================================//
	// Range Estimation methods
	//====================================================//

	public static final int mdbx_estimate_distance( long first, long last, long[] distance_items) {
		return FfmDispatch.mdbx_estimate_distance(first, last, distance_items);
	}


	public static final int mdbx_estimate_move( long cursor, MDBX_val key, MDBX_val data, long move_op, long[] distance_items) {
		return FfmDispatch.mdbx_estimate_move(cursor, key, data, move_op, distance_items);
	}


	public static final int mdbx_estimate_range( long txn, long dbi, MDBX_val begin_key, MDBX_val begin_data, MDBX_val end_key, MDBX_val end_data, long[] distance_items) {
		return FfmDispatch.mdbx_estimate_range(txn, dbi, begin_key, begin_data, end_key, end_data, distance_items);
	}

	//====================================================//
	// Extra operation methods
	//====================================================//
	//TODO: MDBX_msg_func

	//TODO: mdbx_reader_list


	public static final long mdbx_default_pagesize() {
		return FfmDispatch.mdbx_default_pagesize();
	}


	//TODO: mdbx_dkey

	//TODO: MDBX_oom_func

	//TODO: mdbx_env_set_oomfunc

	//TODO: MDBX_oom_func

	//TODO: debug and page visit functions


	public static final int mdbx_is_dirty( long txn, long ptr) {
		return FfmDispatch.mdbx_is_dirty(txn, ptr);
	}

	/**
	 * Returns basic information about system RAM. This function provides a portable way to get information
	 * about available RAM and can be useful in that it returns the same information that libmdbx uses
	 * internally to adjust various options and control readahead.
	 *
 	 * @param page_size     [out] Optional address where the system page size will be stored.
	 * @param total_pages   [out] Optional address where the number of total RAM pages will be stored.
	 * @param avail_pages   [out] Optional address where the number of available/free RAM pages will be stored.
	 *
	 * @return A non-zero error value on failure and 0 on success.
	 */

	public static final int mdbx_get_sysraminfo( long[] page_size, long[] total_pages, long[] avail_pages) {
		return FfmDispatch.mdbx_get_sysraminfo(page_size, total_pages, avail_pages);
	}

	public static final void mdbx_assert_fail(String msg, String func, int line) {
		FfmDispatch.mdbx_assert_fail(msg, func, line);
	}

	public static final void mdbx_set_panic(long panicFunc) {
		FfmDispatch.mdbx_set_panic(panicFunc);
	}

	public static final long mdbx_dump_val(MDBX_val key, long buf, long bufsize) {
		return FfmDispatch.mdbx_dump_val(key, buf, bufsize);
	}

	public static final long mdbx_liberr2str(int errnum) {
		return FfmDispatch.mdbx_liberr2str(errnum);
	}

	public static final int mdbx_is_readahead_reasonable(long volume, long redundancy) {
		return FfmDispatch.mdbx_is_readahead_reasonable(volume, redundancy);
	}

	public static final int mdbx_thread_register(long env) {
		return FfmDispatch.mdbx_thread_register(env);
	}

	public static final int mdbx_thread_unregister(long env) {
		return FfmDispatch.mdbx_thread_unregister(env);
	}

	public static final long mdbx_env_get_hsr(long env) {
		return FfmDispatch.mdbx_env_get_hsr(env);
	}

	public static final long mdbx_ratio2digits(long numerator, long denominator, int precision, long buffer, long bufferSize) {
		return FfmDispatch.mdbx_ratio2digits(numerator, denominator, precision, buffer, bufferSize);
	}

	public static final long mdbx_ratio2percents(long value, long whole, long buffer, long bufferSize) {
		return FfmDispatch.mdbx_ratio2percents(value, whole, buffer, bufferSize);
	}

	public static final int mdbx_gc_info(long txn, MDBX_gc_info info, long bytes, long iterFunc, long iterCtx) {
		return FfmDispatch.mdbx_gc_info(txn, info, bytes, iterFunc, iterCtx);
	}

	public static final int mdbx_env_defrag(long env, long defragAtLeast, long timeAtLeastDot16, long defragEnough,
			long timeLimitDot16, long acceptableBacklash, long preferredBatch, long progressCallback, long ctx,
			MDBX_defrag_result result) {
		return FfmDispatch.mdbx_env_defrag(env, defragAtLeast, timeAtLeastDot16, defragEnough, timeLimitDot16,
				acceptableBacklash, preferredBatch, progressCallback, ctx, result);
	}

	public static final int mdbx_txn_clone(long origin, long[] inOutClone, long context) {
		return FfmDispatch.mdbx_txn_clone(origin, inOutClone, context);
	}

	public static final int mdbx_txn_checkpoint(long txn, long weakeningDurability, MDBX_commit_latency latency) {
		return FfmDispatch.mdbx_txn_checkpoint(txn, weakeningDurability, latency);
	}

	public static final int mdbx_txn_commit_embark_read(long[] ptxn, MDBX_commit_latency latency) {
		return FfmDispatch.mdbx_txn_commit_embark_read(ptxn, latency);
	}

	public static final int mdbx_txn_amend(long readTxn, long[] ptrWriteTxn, long flags, long context) {
		return FfmDispatch.mdbx_txn_amend(readTxn, ptrWriteTxn, flags, context);
	}

	public static final int mdbx_txn_rollback(long txn) {
		return FfmDispatch.mdbx_txn_rollback(txn);
	}

	public static final int mdbx_txn_abort_ex(long txn, MDBX_commit_latency latency) {
		return FfmDispatch.mdbx_txn_abort_ex(txn, latency);
	}

	public static final int mdbx_txn_refresh(long txn) {
		return FfmDispatch.mdbx_txn_refresh(txn);
	}

	public static final int mdbx_dbi_open2(long txn, MDBX_val name, int flags, long[] dbi) {
		return FfmDispatch.mdbx_dbi_open2(txn, name, flags, dbi);
	}

	public static final int mdbx_dbi_open_ex2(long txn, MDBX_val name, int flags, long[] dbi, long keycmp, long datacmp) {
		return FfmDispatch.mdbx_dbi_open_ex2(txn, name, flags, dbi, keycmp, datacmp);
	}

	public static final int mdbx_enumerate_tables(long txn, long func, long ctx) {
		return FfmDispatch.mdbx_enumerate_tables(txn, func, ctx);
	}

	public static final void mdbx_cache_init(MDBX_cache_entry entry) {
		FfmDispatch.mdbx_cache_init(entry);
	}

	public static final MDBX_cache_result mdbx_cache_get(long txn, long dbi, MDBX_val key, MDBX_val data, MDBX_cache_entry entry) {
		return FfmDispatch.mdbx_cache_get(txn, dbi, key, data, entry);
	}

	public static final MDBX_cache_result mdbx_cache_get_SingleThreaded(long txn, long dbi, MDBX_val key, MDBX_val data, MDBX_cache_entry entry) {
		return FfmDispatch.mdbx_cache_get_SingleThreaded(txn, dbi, key, data, entry);
	}

	public static final int mdbx_replace_ex(long txn, long dbi, MDBX_val key, MDBX_val newData, MDBX_val oldData, int flags, long preserver, long preserverContext) {
		return FfmDispatch.mdbx_replace_ex(txn, dbi, key, newData, oldData, flags, preserver, preserverContext);
	}

	public static final int mdbx_cursor_delete_range(long begin, long end, int endIncluding, long[] numberOfAffected) {
		return FfmDispatch.mdbx_cursor_delete_range(begin, end, endIncluding, numberOfAffected);
	}

	public static final int mdbx_cursor_distance(long first, long last, long[] distance, int deepness) {
		return FfmDispatch.mdbx_cursor_distance(first, last, distance, deepness);
	}

	public static final int mdbx_cursor_scroll(long cursor, long amount, int deepness) {
		return FfmDispatch.mdbx_cursor_scroll(cursor, amount, deepness);
	}

	public static final int mdbx_cursor_distribute(long first, long last, long[] cursors, int deepness) {
		return FfmDispatch.mdbx_cursor_distribute(first, last, cursors, deepness);
	}

	public static final int mdbx_cursor_bunch_delete(long cursor, int action, long[] numberOfAffected) {
		return FfmDispatch.mdbx_cursor_bunch_delete(cursor, action, numberOfAffected);
	}

//
//  public static final int mdbx_set_compare(long txn, long dbi, long cmp) {
//		return FfmDispatch.mdbx_set_compare(txn, dbi, cmp);
//	}
//
//
//  public static final int mdbx_set_dupsort(long txn, long dbi, long cmp) {
//		return FfmDispatch.mdbx_set_dupsort(txn, dbi, cmp);
//	}

}