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

	private static final int CURSOR_STACK = 32;

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

//	@JniClass(flags = {STRUCT})
//	public static class git {
//		public String datetime;
//		public String tree;
//		public String commit;
//		public String describe;
//
//		@SuppressWarnings("nls")
//		@Override
//		public String toString() {
//			return "{" +
//					"datetime=" + datetime +
//					", tree=" + tree +
//					", commit=" + commit +
//					", describe=" + describe +
//					'}';
//		}
//	}

	@JniClass(flags = STRUCT)
	public static class MDBX_version_info {
		@JniField(cast = "uint8_t")
		public short major;
		@JniField(cast = "uint8_t")
		public short minor;
		@JniField(cast = "uint16_t")
		public int release;
		@JniField(cast = "uint32_t")
		public int revision;

		@JniField(accessor="git.datetime", cast = "char *")
		public static char[] git_datetime;
		@JniField(accessor="git.tree", cast = "char *")
		public static char[] git_tree;
		@JniField(accessor="git.commit", cast = "char *")
		public static char[] git_commit;
		@JniField(accessor="git.describe", cast = "char *")
		public static char[] git_describe;
		@JniField(accessor="sourcery", cast = "char *")
		public static char[] sourcery;

//		@JniField(cast = "mdbx_version_info.git *")
//		public git git = new git();

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
					"major=" + major +
					", minor=" + minor +
					", release=" + release +
					", revision=" + revision +
//					", git=" + git +
					'}';
		}

		@SuppressWarnings("nls")
		public String getVersionString() {
			return "" + major + '.' + minor + '.' + release + '.' + revision;
		}
	}

//	@JniField(flags = { CONSTANT })
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

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
					"datetime=" + new String(datetime) +
					", target=" + new String(target) +
					", options=" + new String(options) +
					", compiler=" + new String(compiler) +
					", flags=" + new String(flags) +
					'}';
		}
	}

//	@JniField(flags = { CONSTANT })
//	public static MDBX_build_info mdbx_build;

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
		@JniField(cast = "uint32_t")
		public int preparation; // Duration of preparation (commit child transactions, update sub-databases records and cursors destroying).
		@JniField(cast = "uint32_t")
		public int gc_wallclock; //Duration of GC update by wall clock
		@JniField(cast = "uint32_t")
		public int audit; //Duration of internal audit if enabled.
		@JniField(cast = "uint32_t")
		public int write; //Duration of writing dirty/modified data pages.
		@JniField(cast = "uint32_t")
		public int sync; //Duration of syncing written data to the dist/storage.
		@JniField(cast = "uint32_t")
		public int ending; //Duration of transaction ending (releasing resources).
		@JniField(cast = "uint32_t")
		public int whole; //The total duration of a commit
		@JniField(cast = "uint32_t")
		public int gc_cputime; //Duration of GC update by wall clock

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
					'}';
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_ENV_DEFAULTS;
	@JniField(flags = { CONSTANT })
	public static int MDBX_VALIDATION;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NOSUBDIR;
	@JniField(flags = { CONSTANT })
	public static int MDBX_RDONLY;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EXCLUSIVE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_ACCEDE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_WRITEMAP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NOTLS;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NORDAHEAD;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NOMEMINIT;
	@JniField(flags = { CONSTANT })
	public static int MDBX_COALESCE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LIFORECLAIM;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PAGEPERTURB;
	@JniField(flags = { CONSTANT })
	public static int MDBX_SYNC_DURABLE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NOMETASYNC;
	@JniField(flags = { CONSTANT })
	public static int MDBX_SAFE_NOSYNC;
	@JniField(flags = { CONSTANT })
	public static int MDBX_MAPASYNC;
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_ENV_JUST_DELETE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_ENV_ENSURE_UNUSED;
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

	/** Controls the in-process initial allocation size for dirty pages list of a write transaction. Default is 1024.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_txn_dp_initial;

	/** Controls the in-process how maximal part of the dirty pages may be spilled when necessary.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_spill_max_denominator;

	/** Controls the in-process how minimal part of the dirty pages should be spilled when necessary.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_spill_min_denominator;

	/** Controls the in-process how much of the parent transaction dirty pages will be spilled while start each child transaction.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_spill_parent4child_denominator;

	/** Controls the in-process threshold of semi-empty pages merge.*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_merge_threshold_16dot16_percent;

	/** Controls the choosing between use write-through disk writes and usual ones with followed flush by the `fdatasync()` syscall..*/
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_writethrough_threshold;

	/** Controls prevention of page-faults of reclaimed and allocated pages in the MDBX_WRITEMAP mode by clearing ones through file handle before touching */
	@JniField(flags = { CONSTANT })
	public static int MDBX_opt_prefault_write_enable;

	//====================================================//
	// MDBX warmup options (MDBX_warmup_flags_t)
	//====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_warmup_default;
	@JniField(flags = { CONSTANT })
	public static int MDBX_warmup_force;
	@JniField(flags = { CONSTANT })
	public static int MDBX_warmup_oomsafe;
	@JniField(flags = { CONSTANT })
	public static int MDBX_warmup_lock;
	@JniField(flags = { CONSTANT })
	public static int MDBX_warmup_touchlimit;
	@JniField(flags = { CONSTANT })
	public static int MDBX_warmup_release;

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

	/** Opens an existing sub-database created with unknown flags.*/
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_DIRTY;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_SPILLS;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_HAS_CHILD;
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
	@JniField(flags = { CONSTANT })
	public static int MDBX_CP_COMPACT; //Copy with compactification: Omit free space from copy and renumber all pages sequentially
	@JniField(flags = { CONSTANT })
	public static int MDBX_CP_FORCE_DYNAMIC_SIZE; //Force to make resizeable copy, i.e. dynamic size instead of fixed

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
		public long mi_geo_upper;  	/* upper limit for datafile size */
		@JniField(accessor="mi_geo.current", cast = "uint64_t")
		public long mi_geo_current; 	/* current datafile size */
		@JniField(accessor="mi_geo.shrink", cast = "uint64_t")
		public long mi_geo_shrink;  	/* shrink treshold for datafile */
		@JniField(accessor="mi_geo.grow", cast = "uint64_t")
		public long mi_geo_grow;    	/* growth step for datafile */

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
		@JniField(cast = "uint64_t")
		public long mi_meta0_txnid;
		@JniField(cast = "uint64_t")
		public long mi_meta0_sign;
		@JniField(cast = "uint64_t")
		public long mi_meta1_txnid;
		@JniField(cast = "uint64_t")
		public long mi_meta1_sign;
		@JniField(cast = "uint64_t")
		public long mi_meta2_txnid;
		@JniField(cast = "uint64_t")
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
		@JniField(accessor="mi_bootid.meta0.x", cast = "uint64_t")
		public long mi_bootid_meta0_x;
		@JniField(accessor="mi_bootid.meta0.y", cast = "uint64_t")
		public long mi_bootid_meta0_y;
		@JniField(accessor="mi_bootid.meta1.x", cast = "uint64_t")
		public long mi_bootid_meta1_x;
		@JniField(accessor="mi_bootid.meta1.y", cast = "uint64_t")
		public long mi_bootid_meta1_y;
		@JniField(accessor="mi_bootid.meta2.x", cast = "uint64_t")
		public long mi_bootid_meta2_x;
		@JniField(accessor="mi_bootid.meta2.y", cast = "uint64_t")
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

//@JniField(accessor="mdbx_version", flags = { CONSTANT })
//		@JniField(cast = "const struct MDBX_version_info", flags = { CONSTANT })
//		public static MDBX_version_info mdbx_version;


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

	// ====================================================//
	// Debug methods
	// ====================================================//
	@JniMethod(cast = "char *")
	public static final native long map_printf(
			@JniArg(cast = "char *", flags={NO_OUT}) long buf,
			@JniArg(cast = "unsigned") int size,
			@JniArg(cast = "const char *", flags={NO_OUT}) long fmt,
			@JniArg(cast = "va_list", flags={NO_OUT}) long args);

//	@JniMethod
//	public static final native void debug_func(
//			@JniArg(cast = "unsigned", flags={NO_OUT}) int logLevel,
//			@JniArg(cast = "const char *", flags={NO_OUT}) long function,
//			@JniArg(cast = "unsigned", flags={NO_OUT}) long line,
//			@JniArg(cast = "const char *", flags={NO_OUT}) long fmt,
//			@JniArg(cast = "const char *", flags={NO_OUT}) long args);

	@JniMethod
	public static final native int mdbx_setup_debug(
			@JniArg(cast = "MDBX_log_level_t") int log_level,
			@JniArg(cast = "MDBX_debug_flags_t") int debug_flags,
			@JniArg(cast = "void(*)(MDBX_log_level_t, const char *, unsigned int, const char *, va_list)",
					flags = ArgFlag.POINTER_ARG) long logger);

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

	@JniMethod(conditional="defined(_WIN32) || defined(_WIN64)")
	public static final native int mdbx_env_delete(
			@JniArg(cast = "const char *") String pathname,
			@JniArg(cast = "unsigned") int mode);

	@JniMethod
	public static final native int mdbx_env_deleteW(
			@JniArg(cast = "const wchar_t *") String pathname,
			@JniArg(cast = "unsigned") int mode);

	@JniMethod
	public static final native int mdbx_env_copy(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "const char *") String dest,
			@JniArg(cast = "unsigned") int flags);

	@JniMethod(conditional="defined(_WIN32) || defined(_WIN64)")
	public static final native int mdbx_env_copyW(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "const wchar_t *") String dest,
			@JniArg(cast = "unsigned") int flags);

	@JniMethod
	public static final native int mdbx_env_copy2fd(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "mdbx_filehandle_t") long fd,
			@JniArg(cast = "unsigned int") int flags);

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

	@JniMethod
	public static final native int mdbx_env_get_pairsize4page_max(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "unsigned") long flags);

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

	@JniMethod
	public static final native int mdbx_env_warmup(
			@JniArg(cast = "MDBX_env *", flags = {NO_OUT}) long env,
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long parent,
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

	@JniMethod
	public static final native int mdbx_txn_release_all_cursors(
			@JniArg(cast = "MDBX_txn *") long txn);

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

	@JniMethod
	public static final native int mdbx_txn_set_userctx(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "void *") long ctx);

	@JniMethod(cast = "void *")
	public static final native long mdbx_txn_get_userctx(
			@JniArg(cast = "MDBX_txn *") long txn);

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
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_stat *", flags = {NO_IN}) MDBX_stat stat,
			@JniArg(cast = "size_t") long bytes);

	@JniMethod
	public static final native int mdbx_dbi_flags(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "unsigned *") long[] flags);

	@JniMethod
	public static final native int mdbx_dbi_flags_ex(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "unsigned *") long[] flags,
			@JniArg(cast = "unsigned *") long[] state);

	@JniMethod
	public static final native void mdbx_dbi_close(
			@JniArg(cast = "MDBX_env *") long env,
			@JniArg(cast = "uint32_t") long dbi);

	@JniMethod
	public static final native int mdbx_dbi_dupsort_depthmask(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "uint32_t *", flags = {NO_IN}) long[] mask);

	@JniMethod
	public static final native int mdbx_dbi_sequence(
			@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "uint64_t *", flags = {NO_IN}) long[] result,
			@JniArg(cast = "uint64_t") long increment);

	//====================================================//
	// CRUD methods
	//====================================================//
	@JniMethod
	public static final native int mdbx_get(
			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
			@JniArg(cast = "MDBX_val *") MDBX_val data);

	@JniMethod
	public static final native int mdbx_get_ex(
			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
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
			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
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
	@JniMethod
	public static final native int mdbx_cursor_open(
			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_cursor **", flags={NO_IN}) long[] cursor);

	@JniMethod
	public static final native void mdbx_cursor_close(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

	@JniMethod
	public static final native int mdbx_cursor_renew(
			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

	@JniMethod
	public static final native int mdbx_cursor_bind(
			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor,
			@JniArg(cast = "uint32_t") long dbi);

	@JniMethod
	public static final native int mdbx_cursor_copy(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long src,
			@JniArg(cast = "MDBX_cursor *") long dest);

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
			@JniArg(cast = "size_t *") long[] countp);

	@JniMethod
	public static final native int mdbx_cursor_eof(
			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor);

	@JniMethod
	public static final native int mdbx_cursor_on_first(
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

	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_keysize_max(
			@JniArg(cast = "intptr_t") long pagesize,
			@JniArg(cast = "unsigned") long flags);

	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_pgsize_min();

	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_pgsize_max();

	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_txnsize_max(
			@JniArg(cast = "intptr_t") long pagesize);

	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_valsize_max(
			@JniArg(cast = "intptr_t") long pagesize,
			@JniArg(cast = "unsigned") long flags);

	@JniMethod(cast = "intptr_t")
	public static final native long mdbx_limits_pairsize4page_max(
			@JniArg(cast = "intptr_t") long pagesize,
			@JniArg(cast = "unsigned") long flags);

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
			@JniArg(cast = "MDBX_txn *", flags = {NO_OUT}) long txn,
			@JniArg(cast = "uint32_t") long dbi,
			@JniArg(cast = "MDBX_val *") MDBX_val begin_key,
			@JniArg(cast = "MDBX_val *") MDBX_val begin_data,
			@JniArg(cast = "MDBX_val *") MDBX_val end_key,
			@JniArg(cast = "MDBX_val *") MDBX_val end_data,
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