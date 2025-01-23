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

import org.fusesource.hawtjni.runtime.*;

import static org.fusesource.hawtjni.runtime.ClassFlag.STRUCT;
import static org.fusesource.hawtjni.runtime.ClassFlag.TYPEDEF;
import static org.fusesource.hawtjni.runtime.FieldFlag.CONSTANT;
import static org.fusesource.hawtjni.runtime.MethodFlag.CONSTANT_GETTER;
import static org.fusesource.hawtjni.runtime.MethodFlag.CONSTANT_INITIALIZER;
import static org.fusesource.hawtjni.runtime.ArgFlag.*;

/**
 * This class holds all the native constant, structure and function mappings.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@SuppressWarnings( {"java:S1444", "java:S3008", "java:S101", "java:S116", "java:S117", "squid:S00100" })
@JniClass
public class JNI {
	public static final Library DB_LIB;
	public static final Library JNI_LIB;

	static {
		//Needed to avoid java.lang.UnsatisfiedLinkError: Can't find dependent libraries
		//The sha1 strategy on windows will prohibit the JNI lib to find the DB lib.
		System.setProperty("hawtjni.strategy", "temp"); //$NON-NLS-1$ //$NON-NLS-2$

		DB_LIB = new Library("mdbx", JNI.class); //$NON-NLS-1$
		JNI_LIB = new Library("mdbxjni", JNI.class); //$NON-NLS-1$

		JNI.DB_LIB.load();
		JNI.JNI_LIB.load();
		init();
	}

	@JniMethod(flags = {CONSTANT_INITIALIZER})
	private static final native void init();

	///////////////////////////////////////////////////////////////////////
	//
	// Posix APIs:
	//
	///////////////////////////////////////////////////////////////////////

	@JniMethod(flags={CONSTANT_GETTER})
	public static final native int errno();

	@JniMethod(cast = "char *")
	public static final native long strerror(int errnum);

	public static final native int strlen(@JniArg(cast = "const char *")long s);

	@JniMethod(cast = "void *")
	public static final native long malloc(@JniArg(cast = "size_t") long size);

	public static final native void free(@JniArg(cast = "void *") long self);

	///////////////////////////////////////////////////////////////////////
	//
	// Additional Helpers
	//
	///////////////////////////////////////////////////////////////////////
	public static final native void buffer_copy (
			@JniArg(cast = "const void *", flags={NO_OUT, CRITICAL}) byte[] src,
			@JniArg(cast = "size_t") long srcPos,
			@JniArg(cast = "void *") long dest,
			@JniArg(cast = "size_t") long destPos,
			@JniArg(cast = "size_t") long length);

	public static final native void buffer_copy (
			@JniArg(cast = "const void *") long src,
			@JniArg(cast = "size_t") long srcPos,
			@JniArg(cast = "void *", flags={NO_IN, CRITICAL}) byte[] dest,
			@JniArg(cast = "size_t") long destPos,
			@JniArg(cast = "size_t") long length);

	///////////////////////////////////////////////////////////////////////
	//
	// The mdbx API
	//
	///////////////////////////////////////////////////////////////////////

	//====================================================//
	// Version Info (no enum)
	//====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_VERSION_MAJOR;
	@JniField(flags = { CONSTANT })
	public static int MDBX_VERSION_MINOR;

	@JniClass(flags = STRUCT)
	public static class MDBX_version_info {
		@JniField(cast = "uint16_t")
		public int major;
		@JniField(cast = "uint16_t")
		public int minor;
		@JniField(cast = "uint16_t")
		public int patch;
		@JniField(cast = "uint16_t")
		public int tweak;
		@JniField(cast = "char *")
		public char[] semver_prerelease;

		@JniField(accessor="git.datetime", cast = "char *")
		public char[] git_datetime;
		@JniField(accessor="git.tree", cast = "char *")
		public char[] git_tree;
		@JniField(accessor="git.commit", cast = "char *")
		public char[] git_commit;
		@JniField(accessor="git.describe", cast = "char *")
		public char[] git_describe;

		@JniField(accessor="sourcery", cast = "char *")
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

	@JniField(accessor="sizeof(struct MDBX_version_info)", flags={CONSTANT})
	public static int SIZEOF_VERSIONINFO;


//	@JniMethod(flags={CONSTANT_GETTER})
//	public static final native int mdbx_version();

//	@JniField(flags = { CONSTANT })
//	public static int mdbx_version;
//	public static MDBX_version_info mdbx_version;

	//====================================================//
	// Build Info
	//====================================================//
	@JniClass(flags = STRUCT)
	public static class MDBX_build_info {
		@JniField(cast = "char *")
		public char[] datetime;
		@JniField(cast = "char *")
		public char[] target;
		@JniField(cast = "char *")
		public char[] options;
		@JniField(cast = "char *")
		public char[] compiler;
		@JniField(cast = "char *")
		public char[] flags;
		@JniField(cast = "char *")
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

	@JniField(accessor="sizeof(struct MDBX_build_info)", flags={CONSTANT})
	public static int SIZEOF_BUILDINFO;

	//====================================================//
	// Transaction Info
	//====================================================//
	@JniClass(flags = {STRUCT, TYPEDEF})
	public static class MDBX_txn_info {
		@JniField(cast = "uint64_t")
		public long txn_id;
		@JniField(cast = "uint64_t")
		public long txn_reader_lag;
		@JniField(cast = "uint64_t")
		public long txn_space_used;
		@JniField(cast = "uint64_t")
		public long txn_space_limit_soft;
		@JniField(cast = "uint64_t")
		public long txn_space_limit_hard;
		@JniField(cast = "uint64_t")
		public long txn_space_retired;
		@JniField(cast = "uint64_t")
		public long txn_space_leftover;
		@JniField(cast = "uint64_t")
		public long txn_space_dirty;

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
					'}';
		}
	}

	//====================================================//
	// Commit Latency
	//====================================================//
	@JniClass(flags = STRUCT)
	public static class MDBX_commit_latency {
		/**
		 * Duration of preparation (commit child transactions, update sub-databases records and cursors
		 * destroying).
		 */
		@JniField(cast = "uint32_t")
		public int preparation;

		/**
		 * Duration of GC update by wall clock
		 */
		@JniField(cast = "uint32_t")
		public int gc_wallclock;

		/**
		 * Duration of internal audit if enabled.
		 */
		@JniField(cast = "uint32_t")
		public int audit;

		/**
		 * Duration of writing dirty/modified data pages.
		 */
		@JniField(cast = "uint32_t")
		public int write;

		/**
		 * Duration of syncing written data to the dist/storage.
		 */
		@JniField(cast = "uint32_t")
		public int sync;

		/**
		 * Duration of transaction ending (releasing resources).
		 */
		@JniField(cast = "uint32_t")
		public int ending;

		/**
		 * The total duration of a commit
		 */
		@JniField(cast = "uint32_t")
		public int whole;

		/**
		 * Duration of GC update by wall clock
		 */
		@JniField(cast = "uint32_t")
		public int gc_cputime;

		/**
		 * Number of GC update iterations, more than 1 if there were repetitions/restarts.
		 */
		@JniField(accessor="gc_prof.wloops", cast = "uint32_t")
		public int gc_prof_wloops;

		/**
		 * Number of iterations to merge GC records.
		 */
		@JniField(accessor="gc_prof.coalescences", cast = "uint32_t")
		public int gc_prof_coalescences;

		/**
		 * The number of times previous good/resilient commit points were killed when running in
		 * MDBX_UTTERLY_NOSYNC mode.
		 */
		@JniField(accessor="gc_prof.wipes", cast = "uint32_t")
		public int gc_prof_wipes;

		/**
		 * The number of forced commits to disk to avoid database growth when working outside of the
		 * MDBX_UTTERLY_NOSYNC mode.
		 */
		@JniField(accessor="gc_prof.flushes", cast = "uint32_t")
		public int gc_prof_flushes;

		/**
		 * The number of calls to the Handle-Slow-Readers mechanism to avoid database increment. See also
		 * MDBX_hsr_func
		 */
		@JniField(accessor="gc_prof.kicks", cast = "uint32_t")
		public int gc_prof_kicks;

		/**
		 * Slow path execution count GC for user data.
		 */
		@JniField(accessor="gc_prof.work_counter", cast = "uint32_t")
		public int gc_prof_work_counter;

		/**
		 * Wall clock time spent reading and searching within the GC for user data.
		 */
		@JniField(accessor="gc_prof.work_rtime_monotonic", cast = "uint32_t")
		public int gc_prof_work_rtime_monotonic;

		/**
		 * CPU time in user mode spent preparing pages fetched from the GC for user data, including paging from disk.
		 */
		@JniField(accessor="gc_prof.work_xtime_cpu", cast = "uint32_t")
		public int gc_prof_work_xtime_cpu;

		/**
		 * The number of search iterations inside the GC when allocating pages for user data.
		 */
		@JniField(accessor="gc_prof.work_rsteps", cast = "uint32_t")
		public int gc_prof_work_rsteps;

		/**
		 * Number of requests to allocate sequences of pages for user data.
		 */
		@JniField(accessor="gc_prof.work_xpages", cast = "uint32_t")
		public int gc_prof_work_xpages;

		/**
		 * The number of page faults within the GC when allocating and preparing pages for user data.
		 */
		@JniField(accessor="gc_prof.work_majflt", cast = "uint32_t")
		public int gc_prof_work_majflt;

		/**
		 * GC slow path execution count for purposes of maintaining and updating the GC itself.
		 */
		@JniField(accessor="gc_prof.self_counter", cast = "uint32_t")
		public int gc_prof_self_counter;

		/**
		 * Wall clock time spent reading and searching within the GC for purposes of maintaining and updating the
		 * GC itself.
		 */
		@JniField(accessor="gc_prof.self_rtime_monotonic", cast = "uint32_t")
		public int gc_prof_self_rtime_monotonic;

		/**
		 * CPU time in user mode spent preparing pages to be retrieved from the GC for the purposes of maintaining
		 * and updating the GC itself, including paging from disk.
		 */
		@JniField(accessor="gc_prof.self_rtime_monotonic", cast = "uint32_t")
		public int gc_prof_self_xtime_cpu;

		/**
		 * The number of iterations of searching within the GC when allocating pages for the purpose of
		 * maintaining and updating the GC itself.
		 */
		@JniField(accessor="gc_prof.self_rsteps", cast = "uint32_t")
		public int gc_prof_self_rsteps;

		/**
		 * Number of requests to allocate sequences of pages for user data.
		 */
		@JniField(accessor="gc_prof.self_xpages", cast = "uint32_t")
		public int gc_prof_self_xpages;

		/**
		 * The number of page faults within the GC when allocating and preparing pages for the GC itself.
		 */
		@JniField(accessor="gc_prof.self_xpages", cast = "uint32_t")
		public int gc_prof_self_majflt;

		/**
		 * For disassembling with pnl_merge()
		 */
		@JniField(accessor="gc_prof.pnl_merge_work.time", cast = "uint32_t")
		public int gc_prof_pnl_merge_work_time;

		@JniField(accessor="gc_prof.pnl_merge_work.volume", cast = "uint64_t")
		public int gc_prof_pnl_merge_work_volume;

		@JniField(accessor="gc_prof.pnl_merge_work.calls", cast = "uint32_t")
		public int gc_prof_pnl_merge_work_calls;

		@JniField(accessor="gc_prof.pnl_merge_self.time", cast = "uint32_t")
		public int gc_prof_pnl_merge_self_time;

		@JniField(accessor="gc_prof.pnl_merge_self.volume", cast = "uint64_t")
		public int gc_prof_pnl_merge_self_volume;

		@JniField(accessor="gc_prof.pnl_merge_self.calls", cast = "uint32_t")
		public int gc_prof_pnl_merge_self_calls;

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
					"}";
		}
	}

	//====================================================//
	// Canary
	//====================================================//
	@JniClass(flags = STRUCT)
	public static class MDBX_canary {
		@JniField(cast = "uint64_t")
		public long x;
		@JniField(cast = "uint64_t")
		public long y;
		@JniField(cast = "uint64_t")
		public long z;
		@JniField(cast = "uint64_t")
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_ENV_DEFAULTS;

	/**
	 * Extra validation of DB structure and pages content.
	 *
	 * The `MDBX_VALIDATION` enabled the simple safe/careful mode for working with damaged or untrusted DB.
	 * However, a notable performance degradation should be expected.
	 */
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_LIFORECLAIM;

	/**
	 * Debugging option, fill/perturb released pages.
	 */
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_SAFE_NOSYNC;

	/**
	 * \deprecated Please use \ref MDBX_SAFE_NOSYNC instead of `MDBX_MAPASYNC`.
	 *
	 * Since version 0.9.x the `MDBX_MAPASYNC` is deprecated and has the same effect as \ref MDBX_SAFE_NOSYNC
	 * with \ref MDBX_WRITEMAP. This just API simplification is for convenience and clarity.
	 */
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_UTTERLY_NOSYNC;

	//====================================================//
	// MDBX_constants (MDBX_constants)
	//====================================================//
	/** The hard limit for DBI handles*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_MAX_DBI; //

	/** The maximum size of a data item.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_MAXDATASIZE;

	/** The minimal database page size in bytes.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_MIN_PAGESIZE;

	/** The maximal database page size in bytes. */
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_ENV_JUST_DELETE;

	/**
	 * \brief Make sure that the environment is not being used by other processes, or return an error otherwise.
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_ENV_ENSURE_UNUSED;

	/**
	 * \brief Wait until other processes closes the environment before deletion.
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_ENV_WAIT_FOR_UNUSED;

	//====================================================//
	// MDBX environment options (MDBX_option_t)
	//====================================================//
	/** Controls the maximum number of named databases for the environment.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_max_db;

	/** Defines the maximum number of threads/reader slots for all processes interacting with the database. */
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_max_readers;

	/**
	 * Controls interprocess/shared threshold to force flush the data buffers to disk, if MDBX_SAFE_NOSYNC is
	 * used.
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_sync_bytes;

	/**
	 * Controls interprocess/shared relative period since the last unsteady commit to force flush the data
	 * buffers to disk, if MDBX_SAFE_NOSYNC is used.
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_sync_period;

	/**
	 * Controls the in-process limit to grow a list of reclaimed/recycled page's numbers for finding a sequence
	 * of contiguous pages for large data items.
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_rp_augment_limit;

	/** Controls the in-process limit to grow a cache of dirty pages for reuse in the current transaction.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_loose_limit;

	/** Controls the in-process limit of a pre-allocated memory items for dirty pages.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_dp_reserve_limit;

	/** Controls the in-process limit of dirty pages for a write transaction.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_txn_dp_limit;

	/**
	 * Controls the in-process initial allocation size for dirty pages list of a write transaction. Default is
	 * 1024.
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_txn_dp_initial;

	/** Controls the in-process how maximal part of the dirty pages may be spilled when necessary.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_spill_max_denominator;

	/** Controls the in-process how minimal part of the dirty pages should be spilled when necessary.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_spill_min_denominator;

	/**
	 * Controls the in-process how much of the parent transaction dirty pages will be spilled while start each
	 * child transaction.
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_spill_parent4child_denominator;

	/** Controls the in-process threshold of semi-empty pages merge.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_merge_threshold_16dot16_percent;

	/**
	 * Controls the choosing between use write-through disk writes and usual ones with followed flush by the
	 * `fdatasync()` syscall..
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_writethrough_threshold;

	/**
	 * Controls prevention of page-faults of reclaimed and allocated pages in the MDBX_WRITEMAP mode by clearing
	 * ones through file handle before touching
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_prefault_write_enable;

	/**
	 * Controls the in-process spending time limit of searching consecutive pages inside GC. \see
	 * MDBX_opt_rp_augment_limit
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_gc_time_limit;

	/**
	 * Controls the choice between striving for uniformity of page filling or reducing the number of changed and
	 * written pages.
	 *
	 * \see MDBX_opt_merge_threshold_16dot16_percent
	 */
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_subpage_limit;

	/**
	 * Specifies in % the minimum amount of free space on the main page, in the absence of which nested pages
	 * are moved to a separate tree.
	 *
	 * min 0, max 100% (65535), default = 0
	 */
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_subpage_reserve_prereq;

	/**
	 * Specifies the limit in % for reserving space on nested pages.
	 *
	 * min 0, max 100% (65535), default = 4.2% (2753)
	 */
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_warmup_default;

	/**
	 * Peeking all pages of allocated portion of the database to force ones to be loaded into memory. However,
	 * the pages are just peeks sequentially, so unused pages that are in GC will be loaded in the same way as
	 * those that contain payload.
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_warmup_force;

	/**
	 * Using system calls to peeks pages instead of directly accessing ones, which at the cost of additional
	 * overhead avoids killing the current process by OOM-killer in a lack of memory condition. \note Has effect
	 * only on POSIX (non-Windows) systems with conjunction to \ref MDBX_warmup_force option.
	 */
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_warmup_lock;

	/**
	 * Alters corresponding current resource limits to be enough for lock pages by \ref MDBX_warmup_lock.
	 * However, this option should be used in simpler applications since takes into account only current size of
	 * this environment disregarding all other factors. For real-world database application you will need
	 * full-fledged management of resources and their limits with respective engineering.
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_warmup_touchlimit;

	/** Release the lock that was performed before by \ref MDBX_warmup_lock. */
	@JniField(flags = { CONSTANT })
	public static int MDBX_warmup_release;

	//====================================================//
	// MDBX check flags (MDBX_chk_flags_t)
	// Flags/options for checking database integrity.
	// Note: This API is not yet fixed, in future versions there may be minor improvements and changes.
	// see mdbx_env_chk()
	//====================================================//
	/** Default verification mode, including read-only mode.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_CHK_DEFAULTS;

	/** Checking in read-write mode, with lock acquisition and suspension of writing transactions. */
	@JniField(flags = { CONSTANT })
	public static int MDBX_CHK_READWRITE;

	/** Skip page tree traversal.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_CHK_SKIP_BTREE_TRAVERSAL;

	/** Skip scanning key-value records. */
	@JniField(flags = { CONSTANT })
	public static int MDBX_CHK_SKIP_KV_TRAVERSAL;

	/**
	 * Ignore key and record order. Note: Required when checking legacy DBs created using custom (user-defined)
	 * key or value comparison functions.
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_CHK_IGNORE_ORDER;

	//====================================================//
	// MDBX check severity (MDBX_chk_severity_t)
	// Logging/detail levels of information provided via database integrity check callbacks.
	// see mdbx_env_chk()
	//====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_severity_prio_shift;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_severity_kind_mask;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_fatal;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_error;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_warning;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_notice;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_result;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_resolution;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_processing;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_info;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_verbose;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_details;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_extra;

	//====================================================//
	// MDBX check stages (MDBX_chk_stage_t)
	// Check stages reported via callbacks when checking database integrity.
	// see mdbx_env_chk()
	//====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_none;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_init;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_lock;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_meta;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_tree;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_gc;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_space;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_maindb;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_tables;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_conclude;
	@JniField(flags = { CONSTANT })
	public static int MDBX_chk_unlock;
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_LOG_FATAL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LOG_ERROR;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LOG_WARN;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LOG_NOTICE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LOG_VERBOSE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LOG_DEBUG;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LOG_TRACE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LOG_EXTRA;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LOG_DONTCHANGE;

	//====================================================//
	// MDBX debug flags (MDBX_debug_flags_t)
	// Runtime debug flags
	//====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBG_NONE;

	/** Enable assertion checks.
   * \note Always enabled for builds with `MDBX_FORCE_ASSERTIONS` option,
   * otherwise requires build with \ref MDBX_DEBUG > 0
   */
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBG_ASSERT;

	/** Enable pages usage audit at commit transactions.
   * \note Requires build with \ref MDBX_DEBUG > 0
   */
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBG_AUDIT;

	/** Enable small random delays in critical points.
   * \note Requires build with \ref MDBX_DEBUG > 0
   */
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBG_JITTER;

	/** Include or not meta-pages in coredump files.
   * \note May affect performance in \ref MDBX_WRITEMAP mode
   */
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBG_DUMP;

	/** Allow multi-opening environment(s) */
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBG_LEGACY_MULTIOPEN;

	/** Allow read and write transactions overlapping for the same thread. */
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBG_LEGACY_OVERLAP;

	/** Don't auto-upgrade format signature.
   * \note However a new write transactions will use and store
   * the last signature regardless this flag
   */
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBG_DONT_UPGRADE;

	/** for mdbx_setup_debug() only: Don't change current settings */
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBG_DONTCHANGE;

	// ====================================================//
	// Database Flags (MDBX_db_flags_t)
	// ====================================================//
	/** Default (flag == 0). */
	@JniField(flags = { CONSTANT })
	public static int MDBX_DB_DEFAULTS;

	/** Use reverse string comparison for keys. */
	@JniField(flags = { CONSTANT })
	public static int MDBX_REVERSEKEY;

	/** Use sorted duplicates, i.e. allow multi-values for a keys. */
	@JniField(flags = { CONSTANT })
	public static int MDBX_DUPSORT;

	/**
	 * Numeric keys in native byte order either uint32_t or uint64_t. The keys must all be of the same size and
	 * must be aligned while passing as arguments.
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_INTEGERKEY;

	/** With MDBX_DUPSORT; sorted dup items have fixed size. The data values must all be of the same size. */
	@JniField(flags = { CONSTANT })
	public static int MDBX_DUPFIXED;

	/**
	 * With MDBX_DUPSORT and with MDBX_DUPFIXED; dups are fixed size like MDBX_INTEGERKEY -style integers. The
	 * data values must all be of the same size and must be aligned while passing as arguments.
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_INTEGERDUP;

	/** With MDBX_DUPSORT; use reverse string comparison for data values. */
	@JniField(flags = { CONSTANT })
	public static int MDBX_REVERSEDUP;

	/** Create DB if not already existing. */
	@JniField(flags = { CONSTANT })
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_DB_ACCEDE;

	// ====================================================//
	// Transaction Flags (MDBX_txn_flags_t)
	// ====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_READWRITE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_RDONLY;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_RDONLY_PREPARE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_TRY;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_NOMETASYNC;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_NOSYNC;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_INVALID;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_FINISHED;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_ERROR;

	/**
	 * Transaction must write, even if dirty list is empty. \note This is a transaction state flag. Returned
	 * from \ref mdbx_txn_flags() but can't be used with \ref mdbx_txn_begin().
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_DIRTY;

	/**
	 * Transaction or a parent has spilled pages. \note This is a transaction state flag. Returned from \ref
	 * mdbx_txn_flags() but can't be used with \ref mdbx_txn_begin().
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_SPILLS;

	/**
	 * Transaction has a nested child transaction. \note This is a transaction state flag. Returned from \ref
	 * mdbx_txn_flags() but can't be used with \ref mdbx_txn_begin().
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_HAS_CHILD;

	/**
	 * Transaction is parked by mdbx_txn_park(). \note This is a transaction state flag. Returned from
	 * mdbx_txn_flags() but can't be used with mdbx_txn_begin().
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_PARKED;

	/**
	 * Transaction is parked by mdbx_txn_park() with `autounpark=true`, and therefore it can be used without
	 * explicitly calling mdbx_txn_unpark() first. \note This is a transaction state flag. Returned from
	 * mdbx_txn_flags() but can't be used with mdbx_txn_begin().
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_AUTOUNPARK;

	/**
	 * The transaction was blocked using the mdbx_txn_park() function, and then ousted by a write
	 * transaction because this transaction was interfered with garbage recycling. \note This is a transaction
	 * state flag. Returned from mdbx_txn_flags() but can't be used with mdbx_txn_begin().
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_OUSTED;

	/**
	 * Most operations on the transaction are currently illegal. \note This is a transaction state flag.
	 * Returned from mdbx_txn_flags() but can't be used with mdbx_txn_begin().
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_BLOCKED;

	// ====================================================//
	// DBI State Flags (MDBX_dbi_state_t)
	// ====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBI_DIRTY; //DB was written in this txn
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBI_STALE; //Named-DB record is older than txnID
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBI_FRESH; //Named-DB handle opened in this txn
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBI_CREAT; //Named-DB handle created in this txn

	// ====================================================//
	// Copy Flags (MDBX_copy_flags_t)
	// ====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_CP_DEFAULTS;

	/** Copy with compactification: Omit free space from copy and renumber all pages sequentially */
	@JniField(flags = { CONSTANT })
	public static int MDBX_CP_COMPACT; //

	/** Force to make resizeable copy, i.e. dynamic size instead of fixed */
	@JniField(flags = { CONSTANT })
	public static int MDBX_CP_FORCE_DYNAMIC_SIZE;

	/** Don't explicitly flush the written data to an output media */
	@JniField(flags = { CONSTANT })
	public static int MDBX_CP_DONT_FLUSH;

	/** Use read transaction parking during copying MVCC-snapshot \see mdbx_txn_park() */
	@JniField(flags = { CONSTANT })
	public static int MDBX_CP_THROTTLE_MVCC;

	/** Abort/dispose passed transaction after copy mdbx_txn_copy2fd() \see mdbx_txn_copy2pathname() */
	@JniField(flags = { CONSTANT })
	public static int MDBX_CP_DISPOSE_TXN;

	/**
	 * Enable renew/restart read transaction in case it use outdated MVCC shapshot, otherwise the
	 * MDBX_MVCC_RETARDED will be returned \see mdbx_txn_copy2fd() \see mdbx_txn_copy2pathname()
	 */
	@JniField(flags = { CONSTANT })
	public static int MDBX_CP_RENEW_TXN;

	// ====================================================//
	// Write Flags (MDBX_put_flags_t)
	// ====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_UPSERT;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NOOVERWRITE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NODUPDATA;
	@JniField(flags = { CONSTANT })
	public static int MDBX_CURRENT;
	@JniField(flags = { CONSTANT })
	public static int MDBX_ALLDUPS;
	@JniField(flags = { CONSTANT })
	public static int MDBX_RESERVE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_APPEND;
	@JniField(flags = { CONSTANT })
	public static int MDBX_APPENDDUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_MULTIPLE;

	// ====================================================//
	// Cursor Flags (MDBX_cursor_op)
	// ====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_FIRST;
	@JniField(flags = { CONSTANT })
	public static int MDBX_FIRST_DUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_GET_BOTH;
	@JniField(flags = { CONSTANT })
	public static int MDBX_GET_BOTH_RANGE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_GET_CURRENT;
	@JniField(flags = { CONSTANT })
	public static int MDBX_GET_MULTIPLE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LAST;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LAST_DUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NEXT;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NEXT_DUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NEXT_MULTIPLE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NEXT_NODUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PREV;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PREV_DUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PREV_NODUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_SET;
	@JniField(flags = { CONSTANT })
	public static int MDBX_SET_KEY;
	@JniField(flags = { CONSTANT })
	public static int MDBX_SET_RANGE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PREV_MULTIPLE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_SET_LOWERBOUND;
	@JniField(flags = { CONSTANT })
	public static int MDBX_SET_UPPERBOUND;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_KEY_LESSER_THAN;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_KEY_LESSER_OR_EQUAL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_KEY_EQUAL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_KEY_GREATER_OR_EQUAL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_KEY_GREATER_THAN;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_EXACT_KEY_VALUE_LESSER_THAN;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_EXACT_KEY_VALUE_LESSER_OR_EQUAL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_EXACT_KEY_VALUE_EQUAL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_EXACT_KEY_VALUE_GREATER_OR_EQUAL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_EXACT_KEY_VALUE_GREATER_THAN;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_PAIR_LESSER_THAN;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_PAIR_LESSER_OR_EQUAL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_PAIR_EQUAL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_PAIR_GREATER_OR_EQUAL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TO_PAIR_GREATER_THAN;

	// ====================================================//
	// Return Codes (no enum)
	// ====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_ENODATA;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EINVAL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EACCESS;
	@JniField(flags = { CONSTANT })
	public static int MDBX_ENOMEM;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EROFS;
	@JniField(flags = { CONSTANT })
	public static int MDBX_ENOSYS;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EIO;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EPERM;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EINTR;
	@JniField(flags = { CONSTANT })
	public static int MDBX_ENOFILE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EREMOTE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EDEADLK;
	@JniField(flags = { CONSTANT })
	public static int MDBX_SUCCESS;
	@JniField(flags = { CONSTANT })
	public static int MDBX_RESULT_FALSE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_RESULT_TRUE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_KEYEXIST;
	@JniField(flags = { CONSTANT })
	public static int MDBX_FIRST_LMDB_ERRCODE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NOTFOUND;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PAGE_NOTFOUND;
	@JniField(flags = { CONSTANT })
	public static int MDBX_CORRUPTED;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PANIC;
	@JniField(flags = { CONSTANT })
	public static int MDBX_VERSION_MISMATCH;
	@JniField(flags = { CONSTANT })
	public static int MDBX_INVALID;
	@JniField(flags = { CONSTANT })
	public static int MDBX_MAP_FULL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBS_FULL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_READERS_FULL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_FULL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_CURSOR_FULL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PAGE_FULL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_UNABLE_EXTEND_MAPSIZE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_INCOMPATIBLE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_BAD_RSLOT;
	@JniField(flags = { CONSTANT })
	public static int MDBX_BAD_TXN;
	@JniField(flags = { CONSTANT })
	public static int MDBX_BAD_VALSIZE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_BAD_DBI;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PROBLEM;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LAST_LMDB_ERRCODE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_BUSY;
	@JniField(flags = { CONSTANT })
	public static int MDBX_FIRST_ADDED_ERRCODE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EMULTIVAL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EBADSIGN;
	@JniField(flags = { CONSTANT })
	public static int MDBX_WANNA_RECOVERY;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EKEYMISMATCH;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TOO_LARGE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_THREAD_MISMATCH;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_OVERLAPPING;
	@JniField(flags = { CONSTANT })
	public static int MDBX_BACKLOG_DEPLETED;
	@JniField(flags = { CONSTANT })
	public static int MDBX_DUPLICATED_CLK;
	@JniField(flags = { CONSTANT })
	public static int MDBX_DANGLING_DBI;
	@JniField(flags = { CONSTANT })
	public static int MDBX_OUSTED;
	@JniField(flags = { CONSTANT })
	public static int MDBX_MVCC_RETARDED;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LAST_ADDED_ERRCODE;

//	@JniClass(flags = {STRUCT})
//	public static class MiGeo {
//		@JniField(cast = "uint64_t")
//		public long lower;		/* lower limit for datafile size */
//		@JniField(cast = "uint64_t")
//		public long upper;  	/* upper limit for datafile size */
//		@JniField(cast = "uint64_t")
//		public long current; 	/* current datafile size */
//		@JniField(cast = "uint64_t")
//		public long shrink;  	/* shrink treshold for datafile */
//		@JniField(cast = "uint64_t")
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
	@JniClass(flags = {STRUCT, TYPEDEF})
	public static class MDBX_envinfo {
//		public MiGeo mi_geo = new MiGeo();
		@JniField(accessor="mi_geo.lower", cast = "uint64_t")
		public long mi_geo_lower;		/* lower limit for datafile size */
		@JniField(accessor="mi_geo.upper", cast = "uint64_t")
		public long mi_geo_upper;		/* upper limit for datafile size */
		@JniField(accessor="mi_geo.current", cast = "uint64_t")
		public long mi_geo_current;		/* current datafile size */
		@JniField(accessor="mi_geo.shrink", cast = "uint64_t")
		public long mi_geo_shrink;		/* shrink treshold for datafile */
		@JniField(accessor="mi_geo.grow", cast = "uint64_t")
		public long mi_geo_grow;			/* growth step for datafile */

		@JniField(cast = "uint64_t")
		public long mi_mapsize;							/* Size of the data memory map */
		@JniField(cast = "uint64_t")
		public long mi_last_pgno;						/* ID of the last used page */
		@JniField(cast = "uint64_t")
		public long mi_recent_txnid;					/* ID of the last committed transaction */
		@JniField(cast = "uint64_t")
		public long mi_latter_reader_txnid; 	/* ID of the last reader transaction */
		@JniField(cast = "uint64_t")
		public long mi_self_latter_reader_txnid; /* ID of the last reader transaction of caller process */

		@JniField(accessor="mi_meta_txnid[0]", cast = "uint64_t")
		public long mi_meta0_txnid;
		@JniField(accessor="mi_meta_txnid[1]", cast = "uint64_t")
		public long mi_meta1_txnid;
		@JniField(accessor="mi_meta_txnid[1]", cast = "uint64_t")
		public long mi_meta2_txnid;

		@JniField(accessor="mi_meta_sign[0]", cast = "uint64_t")
		public long mi_meta0_sign;
		@JniField(accessor="mi_meta_sign[1]", cast = "uint64_t")
		public long mi_meta1_sign;
		@JniField(accessor="mi_meta_sign[2]", cast = "uint64_t")
		public long mi_meta2_sign;

		@JniField(cast = "uint32_t")
		public long mi_maxreaders; /* max reader slots in the environment */
		@JniField(cast = "uint32_t")
		public long mi_numreaders; /* max reader slots used in the environment */
		@JniField(cast = "uint32_t")
		public long mi_dxb_pagesize; /* database pagesize */
		@JniField(cast = "uint32_t")
		public long mi_sys_pagesize; /* system pagesize */
		@JniField(accessor="mi_bootid.current.x", cast = "uint64_t")
		public long mi_bootid_current_x;
		@JniField(accessor="mi_bootid.current.y", cast = "uint64_t")
		public long mi_bootid_current_y;

		@JniField(accessor="mi_bootid.meta[0].x", cast = "uint64_t")
		public long mi_bootid_meta0_x;
		@JniField(accessor="mi_bootid.meta[0].y", cast = "uint64_t")
		public long mi_bootid_meta0_y;
		@JniField(accessor="mi_bootid.meta[1].x", cast = "uint64_t")
		public long mi_bootid_meta1_x;
		@JniField(accessor="mi_bootid.meta[1].y", cast = "uint64_t")
		public long mi_bootid_meta1_y;
		@JniField(accessor="mi_bootid.meta[2].x", cast = "uint64_t")
		public long mi_bootid_meta2_x;
		@JniField(accessor="mi_bootid.meta[2].y", cast = "uint64_t")
		public long mi_bootid_meta2_y;

		@JniField(cast = "uint64_t")
		public long mi_unsync_volume;
		@JniField(cast = "uint64_t")
		public long mi_autosync_threshold;
		@JniField(cast = "uint32_t")
		public long mi_since_sync_seconds16dot16;
		@JniField(cast = "uint32_t")
		public long mi_autosync_period_seconds16dot16;
		@JniField(cast = "uint32_t")
		public long mi_since_reader_check_seconds16dot16;
		@JniField(cast = "uint32_t")
		public long mi_mode;
		@JniField(accessor="mi_pgop_stat.newly", cast = "uint64_t")
		public long mi_pgop_stat_newly;
		@JniField(accessor="mi_pgop_stat.cow", cast = "uint64_t")
		public long mi_pgop_stat_cow;
		@JniField(accessor="mi_pgop_stat.clone", cast = "uint64_t")
		public long mi_pgop_stat_clone;
		@JniField(accessor="mi_pgop_stat.split", cast = "uint64_t")
		public long mi_pgop_stat_split;
		@JniField(accessor="mi_pgop_stat.merge", cast = "uint64_t")
		public long mi_pgop_stat_merge;
		@JniField(accessor="mi_pgop_stat.spill", cast = "uint64_t")
		public long mi_pgop_stat_spill;
		@JniField(accessor="mi_pgop_stat.unspill", cast = "uint64_t")
		public long mi_pgop_stat_unspill;
		@JniField(accessor="mi_pgop_stat.wops", cast = "uint64_t")
		public long mi_pgop_stat_wops;
		@JniField(accessor="mi_pgop_stat.prefault", cast = "uint64_t")
		public long mi_pgop_stat_prefault;
		@JniField(accessor="mi_pgop_stat.mincore", cast = "uint64_t")
		public long mi_pgop_stat_mincore;
		@JniField(accessor="mi_pgop_stat.msync", cast = "uint64_t")
		public long mi_pgop_stat_msync;
		@JniField(accessor="mi_pgop_stat.fsync", cast = "uint64_t")
		public long mi_pgop_stat_fsync;
		@JniField(accessor="mi_dxbid.x", cast = "uint64_t")
		public long mi_dxbid_x;
		@JniField(accessor="mi_dxbid.y", cast = "uint64_t")
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
	@JniClass(flags = {STRUCT, TYPEDEF})
	public static class MDBX_stat {
		@JniField(cast = "uint32_t")
		public long ms_psize;
		@JniField(cast = "uint32_t")
		public long ms_depth;
		@JniField(cast = "uint64_t")
		public long ms_branch_pages;
		@JniField(cast = "uint64_t")
		public long ms_leaf_pages;
		@JniField(cast = "uint64_t")
		public long ms_overflow_pages;
		@JniField(cast = "uint64_t")
		public long ms_entries;
		@JniField(cast = "uint64_t")
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

	@JniField(accessor="sizeof(struct MDBX_envinfo)", flags={CONSTANT})
	public static int SIZEOF_ENVINFO;

	@JniField(accessor="sizeof(struct MDBX_stat)", flags={CONSTANT})
	public static int SIZEOF_STAT;

	//====================================================//
	// Global Extern constant
	//====================================================//
//	@JniField(accessor = "const struct MDBX_build_info", flags = { CONSTANT })
//	public static MDBX_build_info mdbx_build;

//	@JniField(accessor="mdbx_version", flags = { CONSTANT })
//			@JniField(cast = "const struct MDBX_version_info", flags = { CONSTANT })
//			public static MDBX_version_info mdbx_version;


	// ====================================================//
	// Value classes
	// ====================================================//
	@JniClass(flags = { STRUCT, TYPEDEF })
	public static class MDBX_val {
		@JniField(cast = "void *")
		public long iov_base;
		@JniField(cast = "size_t")
		public long iov_len;
	}

	@JniMethod
	public static final native void map_val(
			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) long in,
			@JniArg(cast = "MDBX_val *", flags={NO_IN}) MDBX_val out);

//	@JniMethod
//	public static final native int get_mdbx_build_info(
//			@JniArg(cast = "void *", flags = {NO_IN}) long arg,
//			@JniArg(cast = "size_t") long bytes);

//	@JniMethod
//	public static final native int mdbx_build();

	// ====================================================//
	// Debug methods
	// ====================================================//
	@JniMethod(cast = "char *")
	public static final native long map_printf(
			@JniArg(cast = "char *", flags={NO_OUT}) long buf,
			@JniArg(cast = "unsigned") int size,
			@JniArg(cast = "const char *", flags={NO_OUT}) long fmt,
			@JniArg(cast = "void *", flags={NO_OUT}) long args);

//	@JniMethod
//	public static final native void debug_func(
//			@JniArg(cast = "unsigned", flags={NO_OUT}) int logLevel,
//			@JniArg(cast = "const char *", flags={NO_OUT}) long function,
//			@JniArg(cast = "unsigned", flags={NO_OUT}) long line,
//			@JniArg(cast = "const char *", flags={NO_OUT}) long fmt,
//			@JniArg(cast = "const char *", flags={NO_OUT}) long args);

	/** Setup global log-level, debug options and debug logger.
	 * \returns The previously `debug_flags` in the 0-15 bits and `log_level` in the 16-31 bits.
	 */
	@JniMethod
	public static final native int mdbx_setup_debug(
			@JniArg(cast = "MDBX_log_level_t") int log_level,
			@JniArg(cast = "MDBX_debug_flags_t") int debug_flags,
			@JniArg(cast = "void(*)(MDBX_log_level_t, const char *, int, const char *, va_list)",
					flags = ArgFlag.POINTER_ARG) long logger);

	@JniMethod
	public static final native int mdbx_setup_debug_nofmt(
			@JniArg(cast = "MDBX_log_level_t") int log_level,
			@JniArg(cast = "MDBX_debug_flags_t") int debug_flags,
			@JniArg(cast = "void(*)(MDBX_log_level_t, const char *, int, const char *, unsigned)",
					flags = ArgFlag.POINTER_ARG) long logger,
			@JniArg(cast = "char *") long logger_buffer,
			@JniArg(cast = "size_t") long logger_buffer_size);

	// ====================================================//
	// Error methods
	// ====================================================//
	@JniMethod(cast = "char *")
	public static final native long mdbx_strerror(int errnum);

	@JniMethod(cast = "char *")
	public static final native long mdbx_strerror_r(int errnum,
			@JniArg(cast = "char *") long buf,
			@JniArg(cast = "size_t") long buflen);

	@JniMethod(conditional="defined(_WIN32) || defined(_WIN64)", cast = "char *")
	public static final native long mdbx_strerror_ANSI2OEM(int errnum);

	@JniMethod(conditional="defined(_WIN32) || defined(_WIN64)", cast = "char *")
	public static final native long mdbx_strerror_r_ANSI2OEM(int errnum,
			@JniArg(cast = "char *") long buf,
			@JniArg(cast = "size_t") long buflen);

//	@JniMethod
//	public static final native int mdbx_env_set_hsr(
//			@JniArg(cast = "MDBX_env *") long env,
//
//			MDBX_hsr_func *hsr_callback)

	//====================================================//
	// ENV methods
	//====================================================//
	@JniMethod
	public static final native int mdbx_env_create(
			@JniArg(cast = "MDBX_env **", flags={NO_IN}) long[] penv);

	@JniMethod
	public static final native int mdbx_env_open(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "const char *") String pathname,
			@JniArg(cast = "unsigned") int flags,
			@JniArg(cast = "mdbx_mode_t") int mode);

	@JniMethod(conditional="defined(_WIN32) || defined(_WIN64)")
	public static final native int mdbx_env_openW(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "const wchar_t *") String pathname,
			@JniArg(cast = "unsigned") int flags,
			@JniArg(cast = "mdbx_mode_t") int mode);

	/**
	 * Open an environment instance using specific meta-page
	 * for checking and recovery.
	 *
	 * This function mostly of internal API for `mdbx_chk` utility and subject to
	 * change at any time. Do not use this function to avoid shooting your own
	 * leg(s).
	 *
	 * \note On Windows the \ref mdbx_env_open_for_recoveryW() is recommended
	 * to use.
	 */
	@JniMethod
	public static final native int mdbx_env_open_for_recovery(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "const char *") String pathname,
			@JniArg(cast = "unsigned") int target_meta,
			@JniArg(cast = "int") int writeable);  //bool

	@JniMethod(conditional="defined(_WIN32) || defined(_WIN64)")
	public static final native int mdbx_env_open_for_recoveryW(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "const char *") String pathname,
			@JniArg(cast = "unsigned") int target_meta,
			@JniArg(cast = "int") int writeable);  //bool

	/**
	 * Turn database to the specified meta-page.
	 *
	 * This function mostly of internal API for `mdbx_chk` utility and subject to change at any time. Do not use
	 * this function to avoid shooting your own leg(s).
	 */
	@JniMethod
	public static final native int mdbx_env_turn_for_recovery(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned") int target_meta);


	/**
	 * Gets basic information about the database without opening it.
	 *
	 * The purpose of the function is to get basic information without opening the database and mapping the data
	 * to memory (which can be quite an expensive operation for the OS kernel). The information obtained in this
	 * way can be useful for adjusting the options for working with the database before opening it, as well as
	 * in file manager scripts and other auxiliary utilities.
	 *
	 * @todo Add the ability to set a callback to the API for revising the options for working with the database
	 * during its opening (while holding locks).
	 *
	 * @param [in]
	 *          pathname Path to the directory or file of the database.
	 * @param [out]
	 *          info Pointer to the \ref MDBX_envinfo structure for getting information.
	 * @param [in]
	 *          bytes The current size of the \ref MDBX_envinfo structure, this value is used to ensure
	 *          compatibility with the ABI.
	 *
	 * @note Only some fields of the \ref MDBX_envinfo structure are filled, the values of which can be obtained
	 *       without mapping the DB files into memory and without capturing locks: DB page size, DB geometry,
	 *       size of allocated space (number of the last allocated page), number of the last transaction and
	 *       boot-id.
	 *
	 * @warning The information obtained is a snapshot for the duration of the function execution and can be
	 *          changed at any time by the process working with the DB. In particular, there are no obstacles to
	 *          another process deleting the DB and creating it again with a different page size and/or changing
	 *          any other parameters.
	 *
	 * @return Non-zero error code value, or 0 if successful.
	 */
	@JniMethod
	public static final native int mdbx_preopen_snapinfo(
			@JniArg(cast = "const char *") String pathname,
			@JniArg(cast = "MDBX_envinfo *", flags = {NO_IN}) MDBX_envinfo info,
			@JniArg(cast = "size_t") long bytes);

	@JniMethod(conditional="defined(_WIN32) || defined(_WIN64)")
	public static final native int mdbx_preopen_snapinfoW(
			@JniArg(cast = "const char *") String pathname,
			@JniArg(cast = "MDBX_envinfo *", flags = {NO_IN}) MDBX_envinfo info,
			@JniArg(cast = "size_t") long bytes);

	/**
	 * \brief Delete the environment's files in a proper and multiprocess-safe way.
	 * \ingroup c_extra
	 *
	 * \note On Windows the \ref mdbx_env_deleteW() is recommended to use.
	 *
	 * \param [in] pathname  The pathname for the database or the directory in which the database files reside.
	 *
	 * \param [in] mode      Specifies deletion mode for the environment. This parameter must be set to one of
	 *                       the constants described above in the \ref MDBX_env_delete_mode_t section.
	 *
	 * \note The \ref MDBX_ENV_JUST_DELETE not supported on Windows since system is unable to delete a
	 * memory-mapped file.
	 *
	 * \returns A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_RESULT_TRUE  No corresponding files or directories were found, so no deletion was performed.
	 */
	@JniMethod
	public static final native int mdbx_env_delete(
			@JniArg(cast = "const char *") String pathname,
			@JniArg(cast = "unsigned") int mode);

	@JniMethod(conditional="defined(_WIN32) || defined(_WIN64)")
	public static final native int mdbx_env_deleteW(
			@JniArg(cast = "const wchar_t *") String pathname,
			@JniArg(cast = "unsigned") int mode);

	@JniMethod
	public static final native int mdbx_env_copy(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "const char *") String dest,
			@JniArg(cast = "MDBX_copy_flags_t") int flags);

	@JniMethod(conditional="defined(_WIN32) || defined(_WIN64)")
	public static final native int mdbx_env_copyW(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "const wchar_t *") String dest,
			@JniArg(cast = "MDBX_copy_flags_t") int flags);

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
	 * \param [in] txn A transaction handle returned by \ref mdbx_txn_begin(). \param [in] dest The pathname of
	 * a file in which the copy will reside. This file must not be already exist, but parent directory must be
	 * writable. \param [in] flags Specifies options for this operation. This parameter must be bitwise OR'ing
	 * together any of the constants described here:
	 *
	 * - \ref MDBX_CP_DEFAULTS Perform copy as-is without compaction, etc.
	 *
	 * - \ref MDBX_CP_COMPACT Perform compaction while copying: omit free pages and sequentially renumber all
	 * pages in output. This option consumes little bit more CPU for processing, but may running quickly than
	 * the default, on account skipping free pages.
	 *
	 * - \ref MDBX_CP_FORCE_DYNAMIC_SIZE Force to make resizable copy, i.e. dynamic size instead of fixed.
	 *
	 * \returns A non-zero error value on failure and 0 on success.
	 */
	@JniMethod
	public static final native int mdbx_txn_copy2pathname(
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn,
			@JniArg(cast = "const char *") String dest,
			@JniArg(cast = "MDBX_copy_flags_t") int flags);

	@JniMethod(conditional="defined(_WIN32) || defined(_WIN64)")
	public static final native int mdbx_txn_copy2pathnameW(
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn,
			@JniArg(cast = "const wchar_t *") String dest,
			@JniArg(cast = "MDBX_copy_flags_t") int flags);

	@JniMethod
	public static final native int mdbx_env_copy2fd(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "mdbx_filehandle_t") long fd,
			@JniArg(cast = "MDBX_copy_flags_t") int flags);

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
	 * \param [in] txn A transaction handle returned by \ref mdbx_txn_begin(). \param [in] fd The file
	 * descriptor to write the copy to. It must have already been opened for Write access. \param [in] flags
	 * Special options for this operation. \see mdbx_env_copy()
	 *
	 * \returns A non-zero error value on failure and 0 on success.
	 */
	@JniMethod
	public static final native int mdbx_txn_copy2fd(
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn,
			@JniArg(cast = "mdbx_filehandle_t") long fd,
			@JniArg(cast = "MDBX_copy_flags_t") int flags);

	@JniMethod
	public static final native int mdbx_env_stat(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "MDBX_stat *", flags = {NO_IN}) MDBX_stat stat,
			@JniArg(cast = "size_t") long bytes);

	@JniMethod
	public static final native int mdbx_env_stat_ex(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn,
			@JniArg(cast = "MDBX_stat *", flags = {NO_IN}) MDBX_stat stat,
			@JniArg(cast = "size_t") long bytes);

	@JniMethod
	public static final native int mdbx_env_info(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "MDBX_envinfo *", flags = {NO_IN}) MDBX_envinfo info,
			@JniArg(cast = "size_t") long bytes);

	@JniMethod
	public static final native int mdbx_env_info_ex(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn,
			@JniArg(cast = "MDBX_envinfo *", flags = {NO_IN}) MDBX_envinfo info,
			@JniArg(cast = "size_t") long bytes);

	@JniMethod
	public static final native int mdbx_env_close(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env);

	@JniMethod
	public static final native int mdbx_env_close_ex(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			int dont_sync);

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
	 * \param [in,out] env The environment instance created by the \ref mdbx_env_create() function.
	 *
	 * \returns A non-zero error code value, or 0 on success. Some possible errors are:
	 *
	 * \retval MDBX_BUSY The parent process opened the DB in \ref MDBX_EXCLUSIVE mode.
	 *
	 * \retval MDBX_EBADSIGN If the signature of the object instance is damaged, or if \ref
	 * mdbx_env_resurrect_after_fork() is called simultaneously from different threads.
	 *
	 * \retval MDBX_PANIC A critical error occurred while restoring the environment instance, or such an error
	 * already existed before the function was called.
	 */
	@JniMethod(conditional="!(defined(_WIN32) || defined(_WIN64))")
	public static final native int mdbx_env_resurrect_after_fork(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env);

	@JniMethod
	public static final native int mdbx_env_sync(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env);

	@JniMethod
	public static final native int mdbx_env_sync_ex(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			int force,  //_Bool
			int nonblock);   //_Bool

	@JniMethod
	public static final native int mdbx_env_sync_poll(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env);

	@JniMethod
	public static final native int mdbx_env_set_flags(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned") int flags,
			@JniArg(cast = "int") int onoff);

	@JniMethod
	public static final native int mdbx_env_get_flags(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned *", flags = {NO_IN}) long[] flags);

	@JniMethod
	public static final native int mdbx_env_get_option(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned") int option,
			@JniArg(cast = "uint64_t *", flags = {NO_IN}) long[] pvalue);

	@JniMethod
	public static final native int mdbx_env_set_option(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned") int option,
			@JniArg(cast = "uint64_t") long value);

	@JniMethod
	public static final native int mdbx_env_get_path(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "const char **", flags={NO_IN}) long[] dest);

	@JniMethod(conditional="defined(_WIN32) || defined(_WIN64)")
	public static final native int mdbx_env_get_pathW(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "const wchar_t **", flags={NO_IN}) long[] dest);

	@JniMethod
	public static final native int mdbx_env_get_fd(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "mdbx_filehandle_t *", flags = {NO_IN}) long[] fd);

	@JniMethod
	public static final native int mdbx_env_set_geometry(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "intptr_t") long size_lower,
			@JniArg(cast = "intptr_t") long size_now,
			@JniArg(cast = "intptr_t") long size_upper,
			@JniArg(cast = "intptr_t") long growth_step,
			@JniArg(cast = "intptr_t") long shrink_threshold,
			@JniArg(cast = "intptr_t") long pagesize);

	@JniMethod
	public static final native int mdbx_env_set_mapsize(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "size_t") long size);

	@JniMethod
	public static final native int mdbx_env_get_maxreaders(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned *", flags = {NO_IN}) long[] readers);

	@JniMethod
	public static final native int mdbx_env_set_maxreaders(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned") int readers);

	@JniMethod
	public static final native int mdbx_env_get_maxdbs(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned *") long[] flags);

	@JniMethod
	public static final native int mdbx_env_set_maxdbs(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "uint32_t") long dbs);

	/** \deprecated Please use \ref mdbx_env_get_maxkeysize_ex() and/or \ref mdbx_env_get_maxvalsize_ex()
	 * \ingroup c_statinfo
	 */
	@Deprecated
	@JniMethod
	public static final native int mdbx_env_get_maxkeysize(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env);

	@JniMethod
	public static final native int mdbx_env_get_maxkeysize_ex(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned") long flags);

	@JniMethod
	public static final native int mdbx_env_get_maxvalsize_ex(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned") long flags);

	/** \brief Returns maximal size of key-value pair to fit in a single page for specified table flags.
	 * \ingroup c_statinfo
	 *
	 * \param [in] env    An environment handle returned by \ref mdbx_env_create().
	 * \param [in] flags  Table options (\ref MDBX_DUPSORT, \ref MDBX_INTEGERKEY and so on).
	 * \see db_flags
	 *
	 * \returns The maximum size of a data can write, or -1 if something is wrong.
	 */
	@JniMethod
	public static final native int mdbx_env_get_pairsize4page_max(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned") long flags);

	/** \brief Returns maximal data size in bytes to fit in a leaf-page or single large/overflow-page for
	 * specified table flags.
	 * \ingroup c_statinfo
	 *
	 * \param [in] env    An environment handle returned by \ref mdbx_env_create().
	 * \param [in] flags  Table options (\ref MDBX_DUPSORT, \ref MDBX_INTEGERKEY and so on).
	 * \see db_flags
	 *
	 * \returns The maximum size of a data can write, or -1 if something is wrong.
	 */
	@JniMethod
	public static final native int mdbx_env_get_valsize4page_max(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned") long flags);

	@JniMethod
	public static final native int mdbx_env_get_syncbytes(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "size_t *", flags = {NO_IN}) long[] threshold);

	@JniMethod
	public static final native int mdbx_env_set_syncbytes(
			@JniArg(cast = "MDBX_env *") long env,
			@JniArg(cast = "size_t") long bytes);

	@JniMethod
	public static final native int mdbx_env_get_syncperiod(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned *", flags = {NO_IN}) long[] period_seconds_16dot16);

	@JniMethod
	public static final native int mdbx_env_set_syncperiod(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned") long seconds_16dot16);

	@JniMethod(cast = "void *")
	public static final native long mdbx_env_get_userctx(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env);

	@JniMethod
	public static final native int mdbx_env_set_userctx(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "void *", flags = {NO_OUT}) long ctx);

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
	 * \param [in] env An environment handle returned by \ref mdbx_env_create(). \param [in] txn A transaction
	 * handle returned by \ref mdbx_txn_begin(). \param [in] flags The \ref warmup_flags, bitwise OR'ed
	 * together.
	 *
	 * \param [in] timeout_seconds_16dot16 Optional timeout which checking only during explicitly peeking
	 * database pages for loading ones if the \ref MDBX_warmup_force option was specified.
	 *
	 * \returns A non-zero error value on failure and 0 on success. Some possible errors are:
	 *
	 * \retval MDBX_ENOSYS The system does not support requested operation(s).
	 *
	 * \retval MDBX_RESULT_TRUE The specified timeout is reached during load data into memory.
	 */
	@JniMethod
	public static final native int mdbx_env_warmup(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn,
			@JniArg(cast = "unsigned") long flags,
			@JniArg(cast = "unsigned") long timeout_seconds_16dot16);

	//====================================================//
	// Debug methods
	//====================================================//
	//TODO: MDBX_assert_func

	//TODO: mdbx_env_set_assert

	//====================================================//
	// TXN methods
	//====================================================//
	@JniMethod
	public static final native int mdbx_txn_begin(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long parent,
			@JniArg(cast = "unsigned") long flags,
			@JniArg(cast = "MDBX_txn **", flags={NO_IN}) long[] txn);

	@JniMethod
	public static final native int mdbx_txn_begin_ex(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long parent,
			@JniArg(cast = "unsigned") long flags,
			@JniArg(cast = "MDBX_txn **", flags={NO_IN}) long[] txn,
			@JniArg(cast = "void *") long ctx);

	/**
	 * Marks transaction as broken.
	 *
	 * Function keeps the transaction handle and corresponding locks, but makes impossible to perform any
	 * operations within a broken transaction. Broken transaction must then be aborted explicitly later.
	 *
	 * @param txn
	 * @return
	 */
	@JniMethod
	public static final native int mdbx_txn_break(
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn);

	@JniMethod(cast = "MDBX_env *")
	public static final native long mdbx_txn_env(
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn);

	@JniMethod
	public static final native int mdbx_txn_flags(
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn);

	@JniMethod(cast = "uint64_t")
	public static final native long mdbx_txn_id(
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn);

	@JniMethod
	public static final native int mdbx_txn_commit(
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn);

	@JniMethod
	public static final native int mdbx_txn_commit_ex(
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn,
			@JniArg(cast = "MDBX_commit_latency *") MDBX_commit_latency latency);

	@JniMethod
	public static final native void mdbx_txn_abort(
			@JniArg(cast = "MDBX_txn *") long txn);

	@JniMethod
	public static final native void mdbx_txn_reset(
			@JniArg(cast = "MDBX_txn *") long txn);

	@JniMethod
	public static final native int mdbx_txn_renew(
			@JniArg(cast = "MDBX_txn *") long txn);

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
	 * @param [in]
	 *          txn Read transaction started by \ref mdbx_txn_begin().
	 * @param [in]
	 *          autounpark Allows you to enable automatic unparking/recovery of a transaction when calling API
	 *          functions that involve reading data.
	 * @return Non-zero error code value, or 0 on success.
	 *
	 * @see mdbx_txn_unpark()
	 * @see mdbx_txn_flags()
	 * @see mdbx_env_set_hsr()
	 * @see <a href="intro.html#long-lived-read">Long-lived read transactions</a>
	 */
	@JniMethod
	public static final native int mdbx_txn_park(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "int") int autounpark);  //Bool

	/**
	 * Unparks a previously parked read transaction.
	 *
	 * The function attempts to recover a previously parked transaction. If the parked transaction was ousted to
	 * recycle old MVCC snapshots, then depending on the `restart_if_ousted` argument, it is either restarted
	 * similarly to \ref mdbx_txn_renew(), or the transaction is reset and the error code \ref MDBX_OUSTED is
	 * returned.
	 *
	 * @param [in]
	 *          txn A read transaction started by \ref mdbx_txn_begin() and then parked by \ref mdbx_txn_park.
	 *
	 * @param [in]
	 *          restart_if_ousted Allows an immediate restart of the transaction if it was ousted.
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
	 * @see mdbx_txn_park()
	 * @see mdbx_txn_flags()
	 * @see <a href="intro.html#long-lived-read">Long-lived read transactions</a>
	 */
	@JniMethod
	public static final native int mdbx_txn_unpark(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "int") int restart_if_ousted);  //Bool

	/**
	 * Unbind or closes all cursors of a given transaction.
	 *
	 * Unbinds either closes all cursors associated (opened or renewed) with
	 * a given transaction in a bulk with minimal overhead.
	 *
	 * @param [in] txn      A transaction handle returned by \ref mdbx_txn_begin().
	 * @param [in] unbind   If non-zero, unbinds cursors and leaves ones reusable.
	 *                      Otherwise close and dispose cursors.
	 *
	 * @return A negative error value on failure or the number of closed cursors
	 *          on success, some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned
	 *                               by current thread.
	 * \retval MDBX_BAD_TXN          Given transaction is invalid or has
	 *                               a child/nested transaction transaction.
	 *
	 * @see mdbx_cursor_unbind()
	 * @see mdbx_cursor_close()
	 */
	@JniMethod
	public static final native int mdbx_txn_release_all_cursors(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "int") int unbind
	);

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
	@JniMethod
	public static final native int mdbx_txn_info(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "MDBX_txn_info *", flags = {NO_IN}) MDBX_txn_info info,
			@JniArg(cast = "int") int scanRlt);  //_Bool

	/** \brief Sets application information associated (a context pointer) with the transaction.
	 * \ingroup c_transactions
	 * \see mdbx_txn_get_userctx()
	 *
	 * \param [in] txn  An transaction handle returned by \ref mdbx_txn_begin_ex() or \ref mdbx_txn_begin().
	 * \param [in] ctx  An arbitrary pointer for whatever the application needs.
	 *
	 * \returns A non-zero error value on failure and 0 on success.
	 */
	@JniMethod
	public static final native int mdbx_txn_set_userctx(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "void *") long ctx);

	/**
	 * \brief Returns an application information (a context pointer) associated with the transaction.
	 * \ingroup c_transactions
	 * \see mdbx_txn_set_userctx()
	 *
	 * \param [in] txn  An transaction handle returned by \ref mdbx_txn_begin_ex() or \ref mdbx_txn_begin().
	 * \returns The pointer which was passed via the `context` parameter of `mdbx_txn_begin_ex()` or set by
	 *          \ref mdbx_txn_set_userctx(), or `NULL` if something wrong.
	 */
	@JniMethod(cast = "void *")
	public static final native long mdbx_txn_get_userctx(
			@JniArg(cast = "MDBX_txn *") long txn);

	/**
	 * \brief Acquires write-transaction lock.
	 * Provided for custom and/or complex locking scenarios.
	 * \returns A non-zero error value on failure and 0 on success.
	 */
	@JniMethod
	public static final native int mdbx_txn_lock(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "int") int dont_wait);  //_Bool

	/**
	 * \brief Releases write-transaction lock.
	 * Provided for custom and/or complex locking scenarios.
	 * \returns A non-zero error value on failure and 0 on success.
	 */
	@JniMethod
	public static final native int mdbx_txn_unlock(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env);

	//====================================================//
	// DBI methods
	//====================================================//
	@JniMethod
	public static final native int mdbx_dbi_open(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "const char *") String name,
			@JniArg(cast = "unsigned") int flags,
			@JniArg(cast = "uint32_t *") long[] dbi);

	@JniMethod
	public static final native int mdbx_dbi_open_ex(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "const char *") String name,
			@JniArg(cast = "unsigned") int flags,
			@JniArg(cast = "uint32_t *") long[] dbi,
			@JniArg(cast = "int(*)(const MDBX_val *, const MDBX_val *)", flags = ArgFlag.POINTER_ARG) long keycmp,
			@JniArg(cast = "int(*)(const MDBX_val *, const MDBX_val *)", flags = ArgFlag.POINTER_ARG) long datacmp);

	@JniMethod
	public static final native int mdbx_dbi_stat(
			@JniArg(cast = "const MDBX_txn *") long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_stat *", flags = {NO_IN}) MDBX_stat stat,
			@JniArg(cast = "size_t") long bytes);

	@JniMethod
	public static final native int mdbx_dbi_flags(
			@JniArg(cast = "const MDBX_txn *") long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "unsigned *") long[] flags);

	@JniMethod
	public static final native int mdbx_dbi_flags_ex(
			@JniArg(cast = "const MDBX_txn *") long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "unsigned *") long[] flags,
			@JniArg(cast = "unsigned *") long[] state);

	@JniMethod
	public static final native void mdbx_dbi_close(
			@JniArg(cast = "MDBX_env *") long env,
			@JniArg(cast = "uint32_t") long dbi);

	@JniMethod
	public static final native int mdbx_dbi_dupsort_depthmask(
			@JniArg(cast = "const MDBX_txn *") long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "uint32_t *", flags = {NO_IN}) long[] mask);

	@JniMethod
	public static final native int mdbx_dbi_sequence(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "uint64_t *", flags = {NO_IN}) long[] result,
			@JniArg(cast = "uint64_t") long increment);

	/**
	 * Rename a table by DBI handle.
	 *
	 * Rename a user-defined subDB associated with the passed DBI handle.
	 *
	 * @param [in,out]
	 *          txn A write transaction started by \ref mdbx_txn_begin().
	 * @param [in]
	 *          dbi A table handle (named user subDB) opened by \ref mdbx_dbi_open().
	 * @param [in]
	 *          name A new name to rename.
	 *
	 * @return A non-zero error code value, or 0 on success.
	 */
	@JniMethod
	public static final native int mdbx_dbi_rename(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "const char *") String name);

	@JniMethod
	public static final native int mdbx_dbi_rename2(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_val *", flags={NO_IN}) MDBX_val name);

	/**
	 * Callback function for enumerating user-defined named tables.
	 *
	 * @param [in]
	 *          ctx Pointer to context passed by the same parameter in \ref mdbx_enumerate_subdb().
	 * @param [in]
	 *          txn Transaction.
	 * @param [in]
	 *          name Table name.
	 * @param [in]
	 *          flags \ref MDBX_db_flags_t.
	 * @param [in]
	 *          stat Basic information \ref MDBX_stat about the table.
	 * @param [in]
	 *          dbi Non-zero DBI handle, if one was opened for this table. Or 0 if no such open handle was
	 *          found.
	 *
	 * @return Zero on success and enumeration continues; if another value is returned, it will be immediately
	 *         returned to the caller without continuing enumeration.
	 *
	 * @see mdbx_enumerate_tables()
	 */
//	typedef int(MDBX_table_enum_func)(void *ctx, const MDBX_txn *txn, const MDBX_val *name, MDBX_db_flags_t flags,
//      const struct MDBX_stat *stat, MDBX_dbi dbi) MDBX_CXX17_NOEXCEPT;

/**
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
	@JniMethod
	public static final native int mdbx_get(
			@JniArg(cast = "const MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
			@JniArg(cast = "MDBX_val *") MDBX_val data);

	@JniMethod
	public static final native int mdbx_get_ex(
			@JniArg(cast = "const MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_val *") MDBX_val key,
			@JniArg(cast = "MDBX_val *") MDBX_val data,
			@JniArg(cast = "size_t *") long[] values_count);

//	@JniMethod
//	public static final native int mdbx_get_attr(
//			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
//			@JniArg(cast = "uint32_t") long dbi,
//			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
//			@JniArg(cast = "MDBX_val *") MDBX_val data,
//			@JniArg(cast = "uint_fast64_t *") long[] pattr);

	@JniMethod
	public static final native int mdbx_get_equal_or_great(
			@JniArg(cast = "const MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_val *") MDBX_val key,
			@JniArg(cast = "MDBX_val *") MDBX_val data);

	@JniMethod
	public static final native int mdbx_put(
			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
			@JniArg(cast = "MDBX_val *") MDBX_val data,
			@JniArg(cast = "unsigned") int flags);

	@JniMethod
	public static final native int mdbx_put_multiple(
			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
			@JniArg(cast = "MDBX_val *") MDBX_val data1,
			@JniArg(cast = "MDBX_val *") MDBX_val data2,
			@JniArg(cast = "unsigned") int flags);

//	@JniMethod
//	public static final native int mdbx_put_attr(
//			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
//			@JniArg(cast = "uint32_t") long dbi,
//			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
//			@JniArg(cast = "MDBX_val *") MDBX_val data,
//			@JniArg(cast = "uint_fast64_t *") long[] attr,
//			@JniArg(cast = "unsigned") int flags);

	@JniMethod
	public static final native int mdbx_replace(
			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val new_data,
			@JniArg(cast = "MDBX_val *") MDBX_val old_data,
			@JniArg(cast = "unsigned") int flags);

	@JniMethod
	public static final native int mdbx_del(
			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val data);

	@JniMethod
	public static final native int mdbx_drop(
			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			int del);

	@JniMethod
	public static final native int mdbx_canary_get(
			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "MDBX_canary *") MDBX_canary canary);

	@JniMethod
	public static final native int mdbx_canary_put(
			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "MDBX_canary *", flags={NO_OUT}) MDBX_canary canary);


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
	 * @param txn
	 * @param dbi
	 * @param cursor
	 * @return
	 */
	@JniMethod
	public static final native int mdbx_cursor_open(
			@JniArg(cast = "const MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_cursor **", flags={NO_IN}) long[] cursor);

	@JniMethod
	public static final native void mdbx_cursor_close(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

	@JniMethod
	public static final native int mdbx_cursor_renew(
			@JniArg(cast = "const MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

	@JniMethod
	public static final native int mdbx_cursor_bind(
			@JniArg(cast = "const MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor,
			@JniArg(cast = "uint32_t") long dbi);

	/**
	 * Unbind cursor from a transaction.
	 *
	 * Unbound cursor is disassociated with any transactions but still holds the original DBI-handle internally.
	 * Thus it could be renewed with any running transaction or closed.
	 *
	 * \note In contrast to LMDB, the MDBX required that any opened cursors can be reused and must be freed
	 * explicitly, regardless ones was opened in a read-only or write transaction. The REASON for this is
	 * eliminates ambiguity which helps to avoid errors such as: use-after-free, double-free, i.e. memory
	 * corruption and segfaults.
	 *
	 * @param [in]
	 *          cursor A cursor handle returned by \ref mdbx_cursor_open().
	 *
	 *          \returns A non-zero error value on failure and 0 on success.
	 *
	 * @see mdbx_cursor_renew()
	 * @see mdbx_cursor_bind()
	 * @see mdbx_cursor_close()
	 * @see mdbx_cursor_reset()
	 */
	@JniMethod
	public static final native int mdbx_cursor_unbind(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

	/**
	 * Resets the cursor state.
	 *
	 * Resetting the cursor causes it to become unset and prevents it from performing relative positioning
	 * operations, retrieving, or modifying data until it is set to a position independent of the current one.
	 * This allows the application to prevent further operations without first positioning the cursor.
	 *
	 * @param [in]
	 *          cursor A pointer to the cursor.
	 *
	 * @return The result of the scan operation, or an error code.
	 */
	@JniMethod
	public static final native int mdbx_cursor_reset(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

	@JniMethod
	public static final native int mdbx_cursor_copy(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long src,
			@JniArg(cast = "MDBX_cursor *") long dest);

	/**
	 * Compares cursor positions.
	 *
	 * The function is intended to compare the positions of two initialized/installed cursors associated with
	 * the same transaction and one table (DBI descriptor). If the cursors are associated with different
	 * transactions, or with different tables, or one of them is not initialized, the result of the comparison
	 * is undefined (the behavior may be changed in future versions).
	 *
	 * @param [in]
	 *          left Left cursor for comparing positions.
	 * @param [in]
	 *          right Right cursor for comparing positions.
	 * @param [in]
	 *          ignore_multival Boolean flag that affects the result only when comparing cursors for tables with
	 *          multi-values, i.e. with the \ref MDBX_DUPSORT flag. If `true`, cursor positions are compared
	 *          only by keys, without taking into account positioning among multi-values. Otherwise, if `false`,
	 *          if the positions by keys match, the positions by multi-values ​​are also compared.
	 *
	 *          \retval A signed value in the semantics of the `<=>` operator (less than zero, zero, or greater
	 *          than zero) as a result of comparing cursor positions.
	 */
	@JniMethod
	public static final native int mdbx_cursor_compare(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long left,
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long right,
			@JniArg(cast = "bool") boolean ignore_multival);

	@JniMethod
	public static final native int mdbx_cursor_get(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor,
			@JniArg(cast = "MDBX_val *") MDBX_val key,  //in,out
			@JniArg(cast = "MDBX_val *") MDBX_val data, //in,out
			@JniArg(cast = "MDBX_cursor_op", flags={NO_OUT}) int op);

	@JniMethod
	public static final native int mdbx_cursor_get_batch(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor,
			@JniArg(cast = "size_t *") long[] count,
			@JniArg(cast = "MDBX_val *") MDBX_val pairs, //in,out
			@JniArg(cast = "size_t") long limit,
			@JniArg(cast = "MDBX_cursor_op", flags={NO_OUT}) int op);

	/**
	 * Utility function for use in utilities.
	 *
	 * When using user-defined comparison functions (aka custom comparison functions), checking the order of
	 * keys may lead to incorrect results and returning the error \ref MDBX_CORRUPTED.
	 *
	 * This function disables the control of the order of keys on pages when reading database pages for this
	 * cursor, and thus allows reading data in the absence/unavailability of the used comparison functions.
	 *
	 * @see avoid_custom_comparators
	 *
	 * @return The result of the scanning operation, or an error code.
	 */
	@JniMethod
	public static final native int mdbx_cursor_ignord(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

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
//	@JniMethod
//	public static final native int mdbx_cursor_ignor2d(
//			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);
//	typedef int(MDBX_predicate_func)(void *context, MDBX_val *key, MDBX_val *value,
//	                                 void *arg) MDBX_CXX17_NOEXCEPT;

/**
 * Scans a table using the predicate passed in, with reduced overhead.
 *
 * Implements functionality similar to the `std::find_if<>()` pattern using a cursor and a user-supplied
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
 * @param [in,out]
 *          cursor A cursor to perform a scan operation, associated with an active transaction and a DBI
 *          handle to the table. For example, a cursor created by \ref mdbx_cursor_open().
 * @param [in]
 *          predicate A predicate function for evaluating iterable key-value pairs, see \ref
 *          MDBX_predicate_func for details.
 * @param [in,out]
 *          context A pointer to a context with the information needed to evaluate , which is entirely
 *          prepared and controlled by you.
 * @param [in]
 *          start_op A start operation for positioning the cursor, see \ref MDBX_cursor_op for details. To
 *          scan without changing the starting position of the cursor, use \ref MDBX_GET_CURRENT. Valid values
 *          ​​are \ref MDBX_FIRST, \ref MDBX_FIRST_DUP, \ref MDBX_LAST, \ref MDBX_LAST_DUP, \ref
 *          MDBX_GET_CURRENT, and \ref MDBX_GET_MULTIPLE.
 * @param [in]
 *          turn_op The cursor positioning operation for moving to the next element. Valid values ​​are \ref
 *          MDBX_NEXT, \ref MDBX_NEXT_DUP, \ref MDBX_NEXT_NODUP, \ref MDBX_PREV, \ref MDBX_PREV_DUP, \ref
 *          MDBX_PREV_NODUP, and \ref MDBX_NEXT_MULTIPLE and \ref MDBX_PREV_MULTIPLE.
 * @param [in,out]
 *          arg An additional argument to the predictive function, which is entirely prepared and controlled
 *          by you.
 *
 *          \note When using \ref MDBX_GET_MULTIPLE, \ref MDBX_NEXT_MULTIPLE or \ref MDBX_PREV_MULTIPLE, be
 *          careful about the batch specifics of passing values ​​through predictive function parameters.
 *
 * @returns The result of the scan operation, or an error code. *
 * @retval MDBX_RESULT_TRUE if a key-value pair for which the predictive function returned \ref
 *         MDBX_RESULT_TRUE is found.
 * @retval MDBX_RESULT_FALSE if a matching key-value pair is NOT found, the search has reached the end of the
 *         data or there is no data to search.
 * @retval ELSE any value other than \ref MDBX_RESULT_TRUE and \ref MDBX_RESULT_FALSE is a course positioning
 *         error code, or a user-defined search stop code or error condition. *
 * @see MDBX_predicate_func
 * @see mdbx_cursor_scan_from
 */
	@JniMethod
	public static final native int mdbx_cursor_scan(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor,
			@JniArg(cast = "int(*)(void *, const MDBX_val *, const MDBX_val *, void *)", flags = ArgFlag.POINTER_ARG) long predicate,
			@JniArg(cast="void *") long context,
			@JniArg(cast = "MDBX_cursor_op", flags={NO_OUT}) int start_op,
			@JniArg(cast = "MDBX_cursor_op", flags={NO_OUT}) int turn_op,
			@JniArg(cast="void *") long arg);
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
 * 	<li>the evaluation function returns \ref MDBX_RESULT_TRUE, signaling that further scanning should be
 * 	stopped;</li>
 * 	<li>the evaluation function will return a value different from \ref MDBX_RESULT_FALSE and \ref
 * 	MDBX_RESULT_TRUE indicating an error.</li>
 * </ul>
 *
 * @param [in,out]
 *          cursor The cursor to perform the scan operation, associated with the active transaction and the
 *          DBI handle of the table. For example, a cursor created by \ref mdbx_cursor_open().
 * @param [in]
 *          predicate A predicate function for evaluating iterable key-value pairs, see \ref
 *          MDBX_predicate_func for details.
 * @param [in,out]
 *          context A pointer to a context with the information needed for the evaluation, which is completely
 *          prepared and controlled by you.
 * @param [in]
 *          from_op The operation of positioning the cursor to the starting position, see \ref MDBX_cursor_op
 *          for details. Valid values ​​\ref MDBX_GET_BOTH, * \ref MDBX_GET_BOTH_RANGE, \ref MDBX_SET_KEY, *
 *          \ref MDBX_SET_LOWERBOUND, \ref MDBX_SET_UPPERBOUND, * \ref MDBX_TO_KEY_LESSER_THAN, * \ref
 *          MDBX_TO_KEY_LESSER_OR_E QUAL, * \ref MDBX_TO_KEY_EQUAL, * \ref MDBX_TO_KEY_GREATER_OR_EQUAL, *
 *          \ref MDBX_TO_KEY_GREATER_THAN, * \ref MDBX_TO_EXACT_KEY_VALUE_LESSER_THAN, * \ref
 *          MDBX_TO_EXACT_KEY_VALUE_LESSER_OR_EQUAL, *\ref MDBX_TO_EXACT_KEY_VALUE_EQUAL, \ref
 *          MDBX_TO_EXACT_KEY_VALUE_GREATER_OR_EQUAL, \ref MDBX_TO_EXACT_KEY_VALUE_GREATER_THAN, \ref
 *          MDBX_TO_PAIR_LESSER_THAN, \ref MDBX_TO_PAIR_LESSER_OR_EQUAL, \ref MDBX_TO_PAIR_EQUAL, \ref
 *          MDBX_TO_PAIR_GREATER_OR_EQUAL, \ref MDBX_TO_PAIR_GREATER_THAN, and \ref MDBX_GET_MULTIPLE.
 * @param [in,out]
 *          from_key Pointer to the key used for both the initial positioning and subsequent iterations of the
 *          transition. * @param [in,out] from_value Pointer to the value used for both the initial
 *          positioning and subsequent iterations of the transition.
 * @param [in]
 *          turn_op Operation of positioning the cursor for the transition to the next element. Allowed values
 *          \ref MDBX_NEXT, \ref MDBX_NEXT_DUP, \ref MDBX_NEXT_NODUP, \ref MDBX_PREV, \ref MDBX_PREV_DUP, \ref
 *          MDBX_PREV_NODUP, and \ref MDBX_NEXT_MULTIPLE and \ref MDBX_PREV_MULTIPLE.
 * @param [in,out]
 *          arg An additional argument to the predicate function, which is entirely prepared and controlled by
 *          you.
 *
 *          \note When using \ref MDBX_GET_MULTIPLE, \ref MDBX_NEXT_MULTIPLE or \ref MDBX_PREV_MULTIPLE, be
 *          careful about the packet specifics of passing values ​​through the parameters of the predictive
 *          function.
 *
 * @return The result of the scan operation, or an error code.
 *
 * @retval MDBX_RESULT_TRUE if a key-value pair for which the predictive function returned \ref
 *         MDBX_RESULT_TRUE was found.
 * @retval MDBX_RESULT_FALSE if a matching key-value pair was NOT found, the search reached the end of data,
 *         or there was no data to search.
 * @retval ELSE any value other than \ref MDBX_RESULT_TRUE and \ref MDBX_RESULT_FALSE is a course positioning
 *         error code, or a user-defined search stop code or an error condition. *
 * @see MDBX_predicate_func
 * @see mdbx_cursor_scan
 */
	@JniMethod
	public static final native int mdbx_cursor_scan_from(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor,
			@JniArg(cast = "int(*)(void *, const MDBX_val *, const MDBX_val *, void *)", flags = ArgFlag.POINTER_ARG) long predicate,
			@JniArg(cast="void *") long context,
			@JniArg(cast = "MDBX_cursor_op", flags={NO_OUT}) int from_op,
			@JniArg(cast = "MDBX_val *") MDBX_val from_key,
			@JniArg(cast = "MDBX_val *") MDBX_val from_value,
			@JniArg(cast = "MDBX_cursor_op", flags={NO_OUT}) int turn_op,
			@JniArg(cast="void *") long arg);
//	LIBMDBX_API int mdbx_cursor_scan_from(MDBX_cursor *cursor,
//	                                      MDBX_predicate_func *predicate,
//	                                      void *context,
//	                                      MDBX_cursor_op from_op,
//	                                      MDBX_val *from_key,
//	                                      MDBX_val *from_value,
//	                                      MDBX_cursor_op turn_op,
//	                                      void *arg);

	@JniMethod
	public static final native int mdbx_cursor_put(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor,
			@JniArg(cast = "MDBX_val *") MDBX_val key, //in,out
			@JniArg(cast = "MDBX_val *") MDBX_val data, //in,out
			@JniArg(cast = "unsigned") int flags);

//@JniMethod
//public static final native int mdbx_cursor_get_attr(
//		@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor,
//		@JniArg(cast = "MDBX_val *") MDBX_val key,
//		@JniArg(cast = "MDBX_val *") MDBX_val data,
//		@JniArg(cast = "uint_fast64_t *") long[] pattr,
//		@JniArg(cast = "MDBX_cursor_op", flags={NO_OUT}) int op);

//	@JniMethod
//	public static final native int mdbx_cursor_put_attr(
//			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor,
//			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
//			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val data,
//			@JniArg(cast = "uint_fast64_t") long attr,
//			@JniArg(cast = "unsigned") int flags);

	@JniMethod(cast = "MDBX_txn *")
	public static final native long mdbx_cursor_txn(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

	@JniMethod(cast = "uint32_t")
	public static final native long mdbx_cursor_dbi(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

	@JniMethod
	public static final native int mdbx_cursor_del(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor,
			@JniArg(cast = "unsigned") int flags);

	@JniMethod
	public static final native int mdbx_cursor_count(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor,
			@JniArg(cast = "size_t *") long[] count);

	/** \brief Return count values (aka duplicates) and nested b-tree statistics for current key.
	 * \ingroup c_crud
	 *
	 * \see mdbx_dbi_stat
	 * \see mdbx_dbi_dupsort_depthmask
	 * \see mdbx_cursor_count
	 *
	 * This call is valid for all tables, but reasonable only for that support sorted duplicate data items
	 * \ref MDBX_DUPSORT.
	 *
	 * \param [in] cursor    A cursor handle returned by \ref mdbx_cursor_open().
	 * \param [out] count    Address where the count will be stored.
	 * \param [out] stat     The address of an \ref MDBX_stat structure where the statistics of a nested b-tree
	 *                       will be copied.
	 * \param [in] bytes     The size of \ref MDBX_stat.
	 *
	 * \returns A non-zero error value on failure and 0 on success, some possible errors are:
	 * \retval MDBX_THREAD_MISMATCH  Given transaction is not owned by current thread.
	 * \retval MDBX_EINVAL   Cursor is not initialized, or an invalid parameter was specified.
	 */
	@JniMethod
	public static final native int mdbx_cursor_count_ex(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor,
			@JniArg(cast = "size_t *") long[] count,
			@JniArg(cast = "MDBX_stat *") MDBX_stat stat,
			@JniArg(cast = "size_t") long bytes);

	@JniMethod
	public static final native int mdbx_cursor_eof(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

	/**
	 * Determines whether the cursor is pointed to the first key-value pair or not.
	 *
	 * @param [in]
	 *          cursor A cursor handle returned by \ref mdbx_cursor_open().
	 *
	 * @return A MDBX_RESULT_TRUE or MDBX_RESULT_FALSE value, otherwise the error code.
	 * @retval MDBX_RESULT_TRUE Cursor positioned to the first key-value pair
	 * @retval MDBX_RESULT_FALSE Cursor NOT positioned to the first key-value pair \retval Otherwise the error
	 *         code
	 */
	@JniMethod
	public static final native int mdbx_cursor_on_first(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

	/**
	 * Determines whether the cursor is on the first or only multi-value matching the key.
	 *
	 * @param [in]
	 *          cursor The cursor created by \ref mdbx_cursor_open().
	 * @return The value of \ref MDBX_RESULT_TRUE, or \ref MDBX_RESULT_FALSE, otherwise an error code.
	 * @retval MDBX_RESULT_TRUE the cursor is positioned on the first or only multi-value matching the key.
	 * @retval MDBX_RESULT_FALSE the cursor is NOT positioned on the first or only multi-value matching the key.
	 * @retval OTHERWISE the error code.
	 */
	@JniMethod
	public static final native int mdbx_cursor_on_first_dup(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

	/**
	 * Determines whether the cursor is on the last or only multi-value matching the key.
	 *
	 * @param [in]
	 *          cursor The cursor created by \ref mdbx_cursor_open(). \returns The value of \ref
	 *          MDBX_RESULT_TRUE, or \ref MDBX_RESULT_FALSE, otherwise an error code.
	 * @retval MDBX_RESULT_TRUE the cursor is positioned on the last or only multi-value matching the key.
	 * @retval MDBX_RESULT_FALSE the cursor is NOT positioned on the last or only multi-value matching the key.
	 * @retval OTHERWISE the error code.
	 */
	@JniMethod
	public static final native int mdbx_cursor_on_last_dup(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

	@JniMethod
	public static final native int mdbx_cursor_on_last(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

	@JniMethod(cast = "MDBX_cursor *")
	public static final native long mdbx_cursor_create(
			@JniArg(cast = "void *") long ctx);

	@JniMethod(cast = "void *")
	public static final native long mdbx_cursor_get_userctx(
			@JniArg(cast = "MDBX_cursor *", flags = {NO_OUT}) long env);

	@JniMethod
	public static final native int mdbx_cursor_set_userctx(
			@JniArg(cast = "MDBX_cursor *", flags = {NO_OUT}) long env,
			@JniArg(cast = "void *") long ctx);

	//====================================================//
	// Compare methods (and K2V and V2K functions)
	//====================================================//
	@JniMethod
	public static final native int mdbx_cmp(
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_val *", flags = {NO_OUT}) MDBX_val a,
			@JniArg(cast = "MDBX_val *", flags = {NO_OUT}) MDBX_val b);

	@JniMethod
	public static final native int mdbx_dcmp(
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_val *", flags = {NO_OUT}) MDBX_val a,
			@JniArg(cast = "MDBX_val *", flags = {NO_OUT}) MDBX_val b);

	@JniMethod(cast = "int *")
	public static final native long mdbx_get_keycmp(
			@JniArg(cast = "unsigned") int flags);

	@JniMethod(cast = "int *")
	public static final native long mdbx_get_datacmp(
			@JniArg(cast = "unsigned") int flags);

	@JniMethod(cast = "uint64_t")
	public static final native long mdbx_key_from_jsonInteger(
			@JniArg(cast = "const int64_t") long json_integer);

	@JniMethod(cast = "uint64_t")
	public static final native long mdbx_key_from_double(
			@JniArg(cast = "const double") double ieee754_64bit);

	@JniMethod(cast = "uint64_t")
	public static final native long mdbx_key_from_ptrdouble(
			@JniArg(cast = "const double *const") long[] ieee754_64bit);

	@JniMethod(cast = "uint32_t")
	public static final native int mdbx_key_from_float(
			@JniArg(cast = "const float") float ieee754_32bit);

	@JniMethod(cast = "uint32_t")
	public static final native int mdbx_key_from_ptrfloat(
			@JniArg(cast = "const float *const") long[] ieee754_32bit);

	@JniMethod(cast = "uint64_t")
	public static final native long mdbx_key_from_int64(
			@JniArg(cast = "const int64_t") long i64);

	@JniMethod(cast = "uint32_t")
	public static final native int mdbx_key_from_int32(
			@JniArg(cast = "const int32_t") int i32);

	@JniMethod(cast = "int64_t")
	public static final native long mdbx_jsonInteger_from_key(
			@JniArg(flags={BY_VALUE}) MDBX_val val);

	@JniMethod
	public static final native double mdbx_double_from_key(
			@JniArg(flags={BY_VALUE}) MDBX_val val);

	@JniMethod
	public static final native float mdbx_float_from_key(
			@JniArg(flags={BY_VALUE}) MDBX_val val);

	@JniMethod(cast = "int32_t")
	public static final native int mdbx_int32_from_key(
			@JniArg(flags={BY_VALUE}) MDBX_val val);

	@JniMethod(cast = "int64_t")
	public static final native long mdbx_int64_from_key(
			@JniArg(flags={BY_VALUE}) MDBX_val val);

	//====================================================//
	// Limits methods
	//====================================================//
	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_dbsize_min(
			@JniArg(cast = "intptr_t") long pagesize);

	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_dbsize_max(
			@JniArg(cast = "intptr_t") long pagesize);

	/** \brief Returns minimal key size in bytes for given table flags.
	 * \ingroup c_statinfo
	 * \see db_flags
	 */
	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_keysize_min(
			@JniArg(cast = "unsigned") long flags);

	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_keysize_max(
			@JniArg(cast = "intptr_t") long pagesize,
			@JniArg(cast = "unsigned") long flags);

	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_pgsize_min();

	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_pgsize_max();

	/** \brief Returns maximal write transaction size (i.e. limit for summary volume
	 * of dirty pages) in bytes for given page size, or -1 if pagesize is invalid.
	 * \ingroup c_statinfo
	 */
	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_txnsize_max(
			@JniArg(cast = "intptr_t") long pagesize);

	/** \brief Returns minimal data size in bytes for given table flags.
	 * \ingroup c_statinfo
	 * \see db_flags
	 */
	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_valsize_min(
			@JniArg(cast = "unsigned") long flags);

	/** \brief Returns maximal data size in bytes for given page size
	 * and table flags, or -1 if pagesize is invalid.
	 * \ingroup c_statinfo
	 * \see db_flags
	 */
	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_valsize_max(
			@JniArg(cast = "intptr_t") long pagesize,
			@JniArg(cast = "unsigned") long flags);

	/** \brief Returns maximal size of key-value pair to fit in a single page with
	 * the given size and table flags, or -1 if pagesize is invalid.
	 * \ingroup c_statinfo
	 * \see db_flags
	 */
	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_pairsize4page_max(
			@JniArg(cast = "intptr_t") long pagesize,
			@JniArg(cast = "unsigned") long flags);

	/** \brief Returns maximal data size in bytes to fit in a leaf-page or
	 * single large/overflow-page with the given page size and table flags,
	 * or -1 if pagesize is invalid.
	 * \ingroup c_statinfo
	 * \see db_flags
	 */
	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_valsize4page_max(
			@JniArg(cast = "intptr_t") long pagesize,
			@JniArg(cast = "unsigned") long flags);

	//====================================================//
	// Reader methods
	//====================================================//
	@JniMethod
	public static final native int mdbx_reader_check(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "int *") int[] dead);

	@JniMethod
	public static final native int mdbx_reader_list(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "int(*)(void *, int, int, mdbx_pid_t, mdbx_tid_t, uint64_t, uint64_t, size_t, size_t)", flags = ArgFlag.POINTER_ARG) long func,
			@JniArg(cast = "void *") long ctx);

	//====================================================//
	// Range Estimation methods
	//====================================================//
	@JniMethod
	public static final native int mdbx_estimate_distance(
			@JniArg(cast = "const MDBX_cursor *", flags={NO_OUT}) long first,
			@JniArg(cast = "const MDBX_cursor *", flags={NO_OUT}) long last,
			@JniArg(cast = "ptrdiff_t *") long[] distance_items);

	@JniMethod
	public static final native int mdbx_estimate_move(
			@JniArg(cast = "const MDBX_cursor *", flags={NO_OUT}) long cursor,
			@JniArg(cast = "MDBX_val *") MDBX_val key,
			@JniArg(cast = "MDBX_val *") MDBX_val data,
			@JniArg(cast = "unsigned") long move_op,
			@JniArg(cast = "ptrdiff_t *") long[] distance_items);

	@JniMethod
	public static final native int mdbx_estimate_range(
			@JniArg(cast = "const MDBX_txn *", flags = {NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "const MDBX_val *") MDBX_val begin_key,
			@JniArg(cast = "const MDBX_val *") MDBX_val begin_data,
			@JniArg(cast = "const MDBX_val *") MDBX_val end_key,
			@JniArg(cast = "const MDBX_val *") MDBX_val end_data,
			@JniArg(cast = "ptrdiff_t *") long[] distance_items);

	//====================================================//
	// Extra operation methods
	//====================================================//
	//TODO: MDBX_msg_func

	//TODO: mdbx_reader_list

	@JniMethod(cast = "size_t")
	public static final native long mdbx_default_pagesize();


	//TODO: mdbx_dkey

	//TODO: MDBX_oom_func

	//TODO: mdbx_env_set_oomfunc

	//TODO: MDBX_oom_func

	//TODO: debug and page visit functions

	@JniMethod
	public static final native int mdbx_is_dirty(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "const void *") long ptr);

	/**
	 * Returns basic information about system RAM. This function provides a portable way to get information
	 * about available RAM and can be useful in that it returns the same information that libmdbx uses
	 * internally to adjust various options and control readahead.
	 *
	 * @param page_size
	 * @param total_pages
	 * @param avail_pages
	 * @return
	 */
	@JniMethod
	public static final native int mdbx_get_sysraminfo(
			@JniArg(cast = "intptr_t *") long[] page_size,
			@JniArg(cast = "intptr_t *") long[] total_pages,
			@JniArg(cast = "intptr_t *") long[] avail_pages);

//  @JniMethod
//  public static final native int mdbx_set_compare(
//  		@JniArg(cast = "MDBX_txn *") long txn,
//  		@JniArg(cast = "uint32_t") long dbi,
//  		@JniArg(cast = "MDBX_cmp_func *") long cmp);
//
//  @JniMethod
//  public static final native int mdbx_set_dupsort(
//  		@JniArg(cast = "MDBX_txn *") long txn,
//  		@JniArg(cast = "uint32_t") long dbi,
//  		@JniArg(cast = "MDBX_cmp_func *") long cmp);

}