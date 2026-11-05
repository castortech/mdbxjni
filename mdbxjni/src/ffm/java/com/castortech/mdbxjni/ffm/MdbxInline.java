package com.castortech.mdbxjni.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import com.castortech.mdbxjni.ffm.gen.MDBX_cache_entry;
import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * Names that libmdbx 0.14 turned into {@code static inline} wrappers. Each one
 * calls the exported function. The comments are the opening paragraph from mdbx.h.
 */
@SuppressWarnings({"squid:S100"})
public final class MdbxInline {
	private MdbxInline() {
	}

	/**
	 * The shortcut to calling mdbx_env_close_ex() with the dont_sync=false argument.
	 */
	public static int mdbx_env_close(MemorySegment env) {
		return mdbx_h.mdbx_env_close_ex(env, false);
	}

	/**
	 * Create a transaction for use with the environment.
	 * The transaction handle may be discarded using mdbx_txn_abort() or mdbx_txn_commit().
	 */
	public static int mdbx_txn_begin(MemorySegment env, MemorySegment parent, int flags, MemorySegment txn) {
		return mdbx_h.mdbx_txn_begin_ex(env, parent, flags, txn, MemorySegment.NULL);
	}

	/**
	 * Commits all the operations of the transaction into the database.
	 */
	public static int mdbx_txn_commit(MemorySegment txn) {
		return mdbx_h.mdbx_txn_commit_ex(txn, MemorySegment.NULL);
	}

	/**
	 * Abandons all the operations of the transaction instead of saving ones.
	 * The transaction handle is freed.
	 */
	public static int mdbx_txn_abort(MemorySegment txn) {
		return mdbx_h.mdbx_txn_abort_ex(txn, MemorySegment.NULL);
	}

	/**
	 * Return statistics about the MDBX environment.
	 * Legacy mdbx_env_stat() correspond to calling mdbx_env_stat_ex() with the null txn argument.
	 */
	public static int mdbx_env_stat(MemorySegment env, MemorySegment stat, long bytes) {
		return mdbx_h.mdbx_env_stat_ex(env, MemorySegment.NULL, stat, bytes);
	}

	/**
	 * Return information about the MDBX environment.
	 * Legacy mdbx_env_info() correspond to calling mdbx_env_info_ex() with the null txn argument.
	 */
	public static int mdbx_env_info(MemorySegment env, MemorySegment info, long bytes) {
		return mdbx_h.mdbx_env_info_ex(env, MemorySegment.NULL, info, bytes);
	}

	/**
	 * The shortcut to calling mdbx_env_sync_ex() with the force=true and nonblock=false arguments.
	 */
	public static int mdbx_env_sync(MemorySegment env) {
		return mdbx_h.mdbx_env_sync_ex(env, true, false);
	}

	/**
	 * The shortcut to calling mdbx_env_sync_ex() with the force=false and nonblock=true arguments.
	 */
	public static int mdbx_env_sync_poll(MemorySegment env) {
		return mdbx_h.mdbx_env_sync_ex(env, false, true);
	}

	/**
	 * Please use mdbx_env_set_geometry() instead.
	 * Sets the lower, current, and upper map size to the same value.
	 */
	public static int mdbx_env_set_mapsize(MemorySegment env, long size) {
		return mdbx_h.mdbx_env_set_geometry(env, size, size, size, -1, -1, -1);
	}

	/**
	 * Returns the minimal database page size in bytes.
	 */
	public static int mdbx_limits_pgsize_min() {
		return mdbx_h.MDBX_MIN_PAGESIZE();
	}

	/**
	 * Returns the maximal database page size in bytes.
	 */
	public static int mdbx_limits_pgsize_max() {
		return mdbx_h.MDBX_MAX_PAGESIZE();
	}

	/**
	 * Set the maximum number of threads/reader slots for all processes interacts with the database.
	 */
	public static int mdbx_env_set_maxreaders(MemorySegment env, int readers) {
		return mdbx_h.mdbx_env_set_option(env, mdbx_h.MDBX_opt_max_readers(), readers);
	}

	/**
	 * Get the maximum number of threads/reader slots for the environment.
	 */
	public static int mdbx_env_get_maxreaders(MemorySegment env, MemorySegment readers) {
		return readUnsigned(env, mdbx_h.MDBX_opt_max_readers(), readers);
	}

	/**
	 * Set the maximum number of named tables for the environment.
	 */
	public static int mdbx_env_set_maxdbs(MemorySegment env, int dbs) {
		return mdbx_h.mdbx_env_set_option(env, mdbx_h.MDBX_opt_max_db(), dbs);
	}

	/**
	 * Get the maximum number of named tables for the environment.
	 */
	public static int mdbx_env_get_maxdbs(MemorySegment env, MemorySegment dbs) {
		return readUnsigned(env, mdbx_h.MDBX_opt_max_db(), dbs);
	}

	/**
	 * Sets threshold to force flush the data buffers to disk, even any of MDBX_SAFE_NOSYNC flag in the environment.
	 */
	public static int mdbx_env_set_syncbytes(MemorySegment env, long threshold) {
		return mdbx_h.mdbx_env_set_option(env, mdbx_h.MDBX_opt_sync_bytes(), threshold);
	}

	/**
	 * Get threshold to force flush the data buffers to disk, even any of MDBX_SAFE_NOSYNC flag in the environment.
	 */
	public static int mdbx_env_get_syncbytes(MemorySegment env, MemorySegment threshold) {
		return readSize(env, mdbx_h.MDBX_opt_sync_bytes(), threshold);
	}

	/**
	 * Sets relative period since the last unsteady commit to force flush the data buffers to disk,
	 * even of MDBX_SAFE_NOSYNC flag in the environment.
	 */
	public static int mdbx_env_set_syncperiod(MemorySegment env, int seconds16dot16) {
		return mdbx_h.mdbx_env_set_option(env, mdbx_h.MDBX_opt_sync_period(), seconds16dot16 & 0xffffffffL);
	}

	/**
	 * Get relative period since the last unsteady commit to force flush the data buffers to disk,
	 * even of MDBX_SAFE_NOSYNC flag in the environment.
	 */
	public static int mdbx_env_get_syncperiod(MemorySegment env, MemorySegment period) {
		return readUnsigned(env, mdbx_h.MDBX_opt_sync_period(), period);
	}

	/**
	 * The shortcut to calling mdbx_dbi_flags_ex() with state=NULL for discarding it result.
	 */
	public static int mdbx_dbi_flags(MemorySegment txn, int dbi, MemorySegment flags) {
		try (Arena arena = Arena.ofConfined()) {
			return mdbx_h.mdbx_dbi_flags_ex(txn, dbi, flags, arena.allocate(ValueLayout.JAVA_INT));
		}
	}

	/**
	 * Unbind or closes all cursors of a given transaction and of all its parent transactions if ones are.
	 */
	public static int mdbx_txn_release_all_cursors(MemorySegment txn, boolean unbind) {
		return mdbx_h.mdbx_txn_release_all_cursors_ex(txn, unbind, MemorySegment.NULL);
	}

	/**
	 * Value-to-key conversion so a signed 64-bit integer sorts in numeric order without a custom comparator.
	 */
	public static long mdbx_key_from_int64(long value) {
		return 0x8000000000000000L + value;
	}

	/**
	 * Value-to-key conversion so a signed 32-bit integer sorts in numeric order without a custom comparator.
	 */
	public static int mdbx_key_from_int32(int value) {
		return 0x80000000 + value;
	}

	/**
	 * Initializes the cache entry before the first use.
	 */
	public static void mdbx_cache_init(MemorySegment entry) {
		MDBX_cache_entry.trunk_txnid(entry, 0);
		MDBX_cache_entry.last_confirmed_txnid(entry, 0);
		MDBX_cache_entry.offset(entry, 0);
		MDBX_cache_entry.length(entry, 0);
	}

	private static int readUnsigned(MemorySegment env, int option, MemorySegment out) {
		return readOption(env, option, out, ValueLayout.JAVA_INT);
	}

	private static int readSize(MemorySegment env, int option, MemorySegment out) {
		return readOption(env, option, out, ValueLayout.JAVA_LONG);
	}

	private static int readOption(MemorySegment env, int option, MemorySegment out, ValueLayout layout) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment proxy = arena.allocate(ValueLayout.JAVA_LONG);
			int rc = mdbx_h.mdbx_env_get_option(env, option, proxy);
			if (rc == mdbx_h.MDBX_SUCCESS() && out != null && out.address() != 0) {
				long value = proxy.get(ValueLayout.JAVA_LONG, 0);
				if (layout == ValueLayout.JAVA_INT) {
					out.set(ValueLayout.JAVA_INT, 0, (int)value);
				}
				else {
					out.set(ValueLayout.JAVA_LONG, 0, value);
				}
			}
			return rc;
		}
	}
}
