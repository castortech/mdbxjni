package com.castortech.mdbxjni.ffm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.castortech.mdbxjni.JNI;
import com.castortech.mdbxjni.JNI.MDBX_build_info;
import com.castortech.mdbxjni.JNI.MDBX_cache_entry;
import com.castortech.mdbxjni.JNI.MDBX_cache_result;
import com.castortech.mdbxjni.JNI.MDBX_canary;
import com.castortech.mdbxjni.JNI.MDBX_commit_latency;
import com.castortech.mdbxjni.JNI.MDBX_defrag_result;
import com.castortech.mdbxjni.JNI.MDBX_gc_info;
import com.castortech.mdbxjni.JNI.MDBX_envinfo;
import com.castortech.mdbxjni.JNI.MDBX_stat;
import com.castortech.mdbxjni.JNI.MDBX_txn_info;
import com.castortech.mdbxjni.JNI.MDBX_val;
import com.castortech.mdbxjni.JNI.MDBX_version_info;
import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/** FFM implementations of the calls the existing Java API still makes by name. */
@SuppressWarnings("nls")
public final class FfmDispatch {
	private FfmDispatch() {
	}

	public static void loadConstants() {
		MdbxLibrary.load();
		for (Field field : JNI.class.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			String name = field.getName();
			if (!name.startsWith("MDBX_")) {
				continue;
			}
			try {
				Method getter = mdbx_h.class.getMethod(name);
				Object value = getter.invoke(null);
				field.setAccessible(true);
				if (field.getType() == int.class) {
					field.setInt(null, ((Number) value).intValue());
				}
				else if (field.getType() == long.class) {
					field.setLong(null, ((Number) value).longValue());
				}
			}
			catch (NoSuchMethodException missing) {
				// Constant is not in the 0.14 surface.
			}
			catch (ReflectiveOperationException failure) {
				throw new ExceptionInInitializerError(failure);
			}
		}
		try {
			Field stat = JNI.class.getDeclaredField("SIZEOF_STAT");
			stat.setAccessible(true);
			stat.setInt(null, (int) com.castortech.mdbxjni.ffm.gen.MDBX_stat.sizeof());
			Field info = JNI.class.getDeclaredField("SIZEOF_ENVINFO");
			info.setAccessible(true);
			info.setInt(null, (int) com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.sizeof());
			Field version = JNI.class.getDeclaredField("SIZEOF_VERSIONINFO");
			version.setAccessible(true);
			version.setInt(null, (int) com.castortech.mdbxjni.ffm.gen.MDBX_version_info.sizeof());
			Field build = JNI.class.getDeclaredField("SIZEOF_BUILDINFO");
			build.setAccessible(true);
			build.setInt(null, (int) com.castortech.mdbxjni.ffm.gen.MDBX_build_info.sizeof());
		}
		catch (ReflectiveOperationException failure) {
			throw new ExceptionInInitializerError(failure);
		}
		applyPlatformErrorCodes();
	}

	/**
	 * The errno and Win32 aliases in {@code mdbx.h}. jextract records the Windows
	 * numbers. These are the values libmdbx actually returns on each OS.
	 */
	private static void applyPlatformErrorCodes() {
		String os = System.getProperty("os.name", "").toLowerCase();
		if (os.contains("win")) {
			assignErrorCodes(38, 87, 5, 14, 6009, 50, 29, 1, 1223, 80, 2, 4352, 1131);
		}
		else if (os.contains("mac")) {
			// Darwin has no ENODATA and no EREMOTEIO, so mdbx.h uses 9919 and ENOTBLK.
			assignErrorCodes(9919, 22, 13, 12, 30, 45, 5, 1, 4, 17, 2, 15, 11);
		}
		else {
			assignErrorCodes(61, 22, 13, 12, 30, 95, 5, 1, 4, 17, 2, 121, 35);
		}
	}

	private static void assignErrorCodes(int enodata, int einval, int eaccess, int enomem, int erofs, int enosys,
			int eio, int eperm, int eintr, int eexist, int enofile, int eremote, int edeadlk) {
		JNI.MDBX_ENODATA = enodata;
		JNI.MDBX_EINVAL = einval;
		JNI.MDBX_EACCESS = eaccess;
		JNI.MDBX_ENOMEM = enomem;
		JNI.MDBX_EROFS = erofs;
		JNI.MDBX_ENOSYS = enosys;
		JNI.MDBX_EIO = eio;
		JNI.MDBX_EPERM = eperm;
		JNI.MDBX_EINTR = eintr;
		JNI.MDBX_EEXIST = eexist;
		JNI.MDBX_ENOFILE = enofile;
		JNI.MDBX_EREMOTE = eremote;
		JNI.MDBX_EDEADLK = edeadlk;
	}

	public static void init() {
		throw new UnsupportedOperationException("init");
	}

	public static int errno() {
		return FfmCalls.errno();
	}

	public static long strerror(int errnum) {
		return FfmCalls.strerror(errnum);
	}

	public static int strlen(long s) {
		return FfmCalls.strlen(s);
	}

	public static long malloc(long size) {
		return FfmCalls.malloc(size);
	}

	public static void free(long self) {
		FfmCalls.free(self);
	}

	public static void buffer_copy(byte[] src, long srcPos, long dest, long destPos, long length) {
		FfmCalls.buffer_copy(src, srcPos, dest, destPos, length);
	}

	public static void buffer_copy(long src, long srcPos, byte[] dest, long destPos, long length) {
		FfmCalls.buffer_copy(src, srcPos, dest, destPos, length);
	}

	public static int mdbx_version() {
		throw new UnsupportedOperationException("mdbx_version");
	}

	public static void readVersion(MDBX_version_info info) {
		FfmCalls.readVersion(info);
	}

	public static void readBuild(MDBX_build_info info) {
		FfmCalls.readBuild(info);
	}

	public static void map_val(long in, MDBX_val out) {
		FfmCalls.map_val(in, out);
	}

	public static int get_mdbx_build_info(long arg, long bytes) {
		throw new UnsupportedOperationException("get_mdbx_build_info");
	}

	public static int mdbx_build() {
		throw new UnsupportedOperationException("mdbx_build");
	}

	public static long map_printf(long buf, int size, long fmt, long args) {
		return FfmCalls.map_printf(buf, size, fmt, args);
	}

	public static void debug_func(int logLevel, long function, long line, long fmt, long args) {
		throw new UnsupportedOperationException("debug_func");
	}

	public static int mdbx_setup_debug(int log_level, int debug_flags, long logger) {
		return FfmCalls.mdbx_setup_debug(log_level, debug_flags, logger);
	}

	public static int mdbx_setup_debug_nofmt(int log_level, int debug_flags, long logger, long logger_buffer, long logger_buffer_size) {
		throw new UnsupportedOperationException("mdbx_setup_debug_nofmt");
	}

	public static long mdbx_strerror(int errnum) {
		return FfmCalls.mdbx_strerror(errnum);
	}

	public static long mdbx_strerror_r(int errnum, long buf, long buflen) {
		throw new UnsupportedOperationException("mdbx_strerror_r");
	}

	public static long mdbx_strerror_ANSI2OEM(int errnum) {
		throw new UnsupportedOperationException("mdbx_strerror_ANSI2OEM");
	}

	public static long mdbx_strerror_r_ANSI2OEM(int errnum, long buf, long buflen) {
		throw new UnsupportedOperationException("mdbx_strerror_r_ANSI2OEM");
	}

	public static int mdbx_env_set_hsr(long env, long hsr_callback) {
		throw new UnsupportedOperationException("mdbx_env_set_hsr");
	}

	public static int mdbx_env_create(long[] penv) {
		return FfmCalls.mdbx_env_create(penv);
	}

	public static int mdbx_env_open(long env, String pathname, int flags, int mode) {
		return FfmCalls.mdbx_env_open(env, pathname, flags, mode);
	}

	public static int mdbx_env_openW(long env, String pathname, int flags, int mode) {
		return FfmCalls.mdbx_env_openW(env, pathname, flags, mode);
	}

	public static int mdbx_env_open_for_recovery(long env, String pathname, int target_meta, int writeable) {
		throw new UnsupportedOperationException("mdbx_env_open_for_recovery");
	}

	public static int mdbx_env_open_for_recoveryW(long env, String pathname, int target_meta, int writeable) {
		throw new UnsupportedOperationException("mdbx_env_open_for_recoveryW");
	}

	public static int mdbx_env_turn_for_recovery(long env, int target_meta) {
		throw new UnsupportedOperationException("mdbx_env_turn_for_recovery");
	}

	public static int mdbx_preopen_snapinfo(String pathname, MDBX_envinfo info, long bytes) {
		throw new UnsupportedOperationException("mdbx_preopen_snapinfo");
	}

	public static int mdbx_preopen_snapinfoW(String pathname, MDBX_envinfo info, long bytes) {
		throw new UnsupportedOperationException("mdbx_preopen_snapinfoW");
	}

	public static int mdbx_env_delete(String pathname, int mode) {
		throw new UnsupportedOperationException("mdbx_env_delete");
	}

	public static int mdbx_env_deleteW(String pathname, int mode) {
		throw new UnsupportedOperationException("mdbx_env_deleteW");
	}

	public static int mdbx_env_copy(long env, String dest, int flags) {
		return FfmCalls.mdbx_env_copy(env, dest, flags);
	}

	public static int mdbx_env_copyW(long env, String dest, int flags) {
		throw new UnsupportedOperationException("mdbx_env_copyW");
	}

	public static int mdbx_txn_copy2pathname(long txn, String dest, int flags) {
		throw new UnsupportedOperationException("mdbx_txn_copy2pathname");
	}

	public static int mdbx_txn_copy2pathnameW(long txn, String dest, int flags) {
		throw new UnsupportedOperationException("mdbx_txn_copy2pathnameW");
	}

	public static int mdbx_env_copy2fd(long env, long fd, int flags) {
		throw new UnsupportedOperationException("mdbx_env_copy2fd");
	}

	public static int mdbx_txn_copy2fd(long txn, long fd, int flags) {
		throw new UnsupportedOperationException("mdbx_txn_copy2fd");
	}

	public static int mdbx_env_stat(long env, MDBX_stat stat, long bytes) {
		return FfmCalls.mdbx_env_stat(env, stat, bytes);
	}

	public static int mdbx_env_stat_ex(long env, long txn, MDBX_stat stat, long bytes) {
		return FfmCalls.mdbx_env_stat_ex(env, txn, stat, bytes);
	}

	public static int mdbx_env_info(long env, MDBX_envinfo info, long bytes) {
		return FfmCalls.mdbx_env_info(env, info, bytes);
	}

	public static int mdbx_env_info_ex(long env, long txn, MDBX_envinfo info, long bytes) {
		return FfmCalls.mdbx_env_info_ex(env, txn, info, bytes);
	}

	public static int mdbx_env_close(long env) {
		return FfmCalls.mdbx_env_close(env);
	}

	public static int mdbx_env_close_ex(long env, int dont_sync) {
		throw new UnsupportedOperationException("mdbx_env_close_ex");
	}

	public static int mdbx_env_resurrect_after_fork(long env) {
		throw new UnsupportedOperationException("mdbx_env_resurrect_after_fork");
	}

	public static int mdbx_env_sync(long env) {
		return FfmCalls.mdbx_env_sync(env);
	}

	public static int mdbx_env_sync_ex(long env, int force, int nonblock) {
		return FfmCalls.mdbx_env_sync_ex(env, force, nonblock);
	}

	public static int mdbx_env_sync_poll(long env) {
		return FfmCalls.mdbx_env_sync_poll(env);
	}

	public static int mdbx_env_set_flags(long env, int flags, int onoff) {
		return FfmCalls.mdbx_env_set_flags(env, flags, onoff);
	}

	public static int mdbx_env_get_flags(long env, long[] flags) {
		return FfmCalls.mdbx_env_get_flags(env, flags);
	}

	public static int mdbx_env_get_option(long env, int option, long[] pvalue) {
		return FfmCalls.mdbx_env_get_option(env, option, pvalue);
	}

	public static int mdbx_env_set_option(long env, int option, long value) {
		return FfmCalls.mdbx_env_set_option(env, option, value);
	}

	public static int mdbx_env_get_path(long env, long[] dest) {
		throw new UnsupportedOperationException("mdbx_env_get_path");
	}

	public static int mdbx_env_get_pathW(long env, long[] dest) {
		throw new UnsupportedOperationException("mdbx_env_get_pathW");
	}

	public static int mdbx_env_get_fd(long env, long[] fd) {
		throw new UnsupportedOperationException("mdbx_env_get_fd");
	}

	public static int mdbx_env_set_geometry(long env, long size_lower, long size_now, long size_upper, long growth_step, long shrink_threshold, long pagesize) {
		return FfmCalls.mdbx_env_set_geometry(env, size_lower, size_now, size_upper, growth_step, shrink_threshold, pagesize);
	}

	public static int mdbx_env_set_mapsize(long env, long size) {
		return FfmCalls.mdbx_env_set_mapsize(env, size);
	}

	public static int mdbx_env_get_maxreaders(long env, long[] readers) {
		return FfmCalls.mdbx_env_get_maxreaders(env, readers);
	}

	public static int mdbx_env_set_maxreaders(long env, int readers) {
		return FfmCalls.mdbx_env_set_maxreaders(env, readers);
	}

	public static int mdbx_env_get_maxdbs(long env, long[] flags) {
		return FfmCalls.mdbx_env_get_maxdbs(env, flags);
	}

	public static int mdbx_env_set_maxdbs(long env, long dbs) {
		return FfmCalls.mdbx_env_set_maxdbs(env, dbs);
	}

	public static int mdbx_env_get_maxkeysize(long env) {
		return FfmCalls.mdbx_env_get_maxkeysize(env);
	}

	public static int mdbx_env_get_maxkeysize_ex(long env, long flags) {
		return FfmCalls.mdbx_env_get_maxkeysize_ex(env, flags);
	}

	public static int mdbx_env_get_maxvalsize_ex(long env, long flags) {
		return FfmCalls.mdbx_env_get_maxvalsize_ex(env, flags);
	}

	public static int mdbx_env_get_pairsize4page_max(long env, long flags) {
		throw new UnsupportedOperationException("mdbx_env_get_pairsize4page_max");
	}

	public static int mdbx_env_get_valsize4page_max(long env, long flags) {
		throw new UnsupportedOperationException("mdbx_env_get_valsize4page_max");
	}

	public static int mdbx_env_get_syncbytes(long env, long[] threshold) {
		throw new UnsupportedOperationException("mdbx_env_get_syncbytes");
	}

	public static int mdbx_env_set_syncbytes(long env, long bytes) {
		throw new UnsupportedOperationException("mdbx_env_set_syncbytes");
	}

	public static int mdbx_env_get_syncperiod(long env, long[] period_seconds_16dot16) {
		throw new UnsupportedOperationException("mdbx_env_get_syncperiod");
	}

	public static int mdbx_env_set_syncperiod(long env, long seconds_16dot16) {
		throw new UnsupportedOperationException("mdbx_env_set_syncperiod");
	}

	public static long mdbx_env_get_userctx(long env) {
		return FfmCalls.mdbx_env_get_userctx(env);
	}

	public static int mdbx_env_set_userctx(long env, long ctx) {
		return FfmCalls.mdbx_env_set_userctx(env, ctx);
	}

	public static int mdbx_env_warmup(long env, long txn, long flags, long timeout_seconds_16dot16) {
		throw new UnsupportedOperationException("mdbx_env_warmup");
	}

	public static int mdbx_txn_begin(long env, long parent, long flags, long[] txn) {
		return FfmCalls.mdbx_txn_begin(env, parent, flags, txn);
	}

	public static int mdbx_txn_begin_ex(long env, long parent, long flags, long[] txn, long ctx) {
		return FfmCalls.mdbx_txn_begin_ex(env, parent, flags, txn, ctx);
	}

	public static int mdbx_txn_break(long txn) {
		return FfmCalls.mdbx_txn_break(txn);
	}

	public static long mdbx_txn_env(long txn) {
		throw new UnsupportedOperationException("mdbx_txn_env");
	}

	public static int mdbx_txn_flags(long txn) {
		return FfmCalls.mdbx_txn_flags(txn);
	}

	public static long mdbx_txn_id(long txn) {
		return FfmCalls.mdbx_txn_id(txn);
	}

	public static int mdbx_txn_commit(long txn) {
		return FfmCalls.mdbx_txn_commit(txn);
	}

	public static int mdbx_txn_commit_ex(long txn, MDBX_commit_latency latency) {
		return FfmCalls.mdbx_txn_commit_ex(txn, latency);
	}

	public static void mdbx_txn_abort(long txn) {
		FfmCalls.mdbx_txn_abort(txn);
	}

	public static void mdbx_txn_reset(long txn) {
		FfmCalls.mdbx_txn_reset(txn);
	}

	public static int mdbx_txn_renew(long txn) {
		return FfmCalls.mdbx_txn_renew(txn);
	}

	public static int mdbx_txn_park(long txn, int autounpark) {
		throw new UnsupportedOperationException("mdbx_txn_park");
	}

	public static int mdbx_txn_unpark(long txn, int restart_if_ousted) {
		throw new UnsupportedOperationException("mdbx_txn_unpark");
	}

	public static int mdbx_txn_release_all_cursors_ex(long txn, int unbind, long count) {
		return FfmCalls.mdbx_txn_release_all_cursors_ex(txn, unbind, count);
	}

	public static int mdbx_txn_release_all_cursors(long txn, int unbind) {
		return FfmCalls.mdbx_txn_release_all_cursors(txn, unbind);
	}

	public static int mdbx_txn_info(long txn, MDBX_txn_info info, int scanRlt) {
		return FfmCalls.mdbx_txn_info(txn, info, scanRlt);
	}

	public static int mdbx_txn_set_userctx(long txn, long ctx) {
		return FfmCalls.mdbx_txn_set_userctx(txn, ctx);
	}

	public static long mdbx_txn_get_userctx(long txn) {
		return FfmCalls.mdbx_txn_get_userctx(txn);
	}

	public static int mdbx_txn_lock(long env, int dont_wait) {
		throw new UnsupportedOperationException("mdbx_txn_lock");
	}

	public static int mdbx_txn_unlock(long env) {
		throw new UnsupportedOperationException("mdbx_txn_unlock");
	}

	public static int mdbx_dbi_open(long txn, String name, int flags, long[] dbi) {
		return FfmCalls.mdbx_dbi_open(txn, name, flags, dbi);
	}

	public static int mdbx_dbi_open_ex(long txn, String name, int flags, long[] dbi, long keycmp, long datacmp) {
		return FfmCalls.mdbx_dbi_open_ex(txn, name, flags, dbi, keycmp, datacmp);
	}

	public static int mdbx_dbi_stat(long txn, long dbi, MDBX_stat stat, long bytes) {
		return FfmCalls.mdbx_dbi_stat(txn, dbi, stat, bytes);
	}

	public static int mdbx_dbi_flags(long txn, long dbi, long[] flags) {
		return FfmCalls.mdbx_dbi_flags(txn, dbi, flags);
	}

	public static int mdbx_dbi_flags_ex(long txn, long dbi, long[] flags, long[] state) {
		return FfmCalls.mdbx_dbi_flags_ex(txn, dbi, flags, state);
	}

	public static void mdbx_dbi_close(long env, long dbi) {
		FfmCalls.mdbx_dbi_close(env, dbi);
	}

	public static int mdbx_dbi_dupsort_depthmask(long txn, long dbi, long[] mask) {
		return FfmCalls.mdbx_dbi_dupsort_depthmask(txn, dbi, mask);
	}

	public static int mdbx_dbi_sequence(long txn, long dbi, long[] result, long increment) {
		return FfmCalls.mdbx_dbi_sequence(txn, dbi, result, increment);
	}

	public static int mdbx_dbi_rename(long txn, long dbi, String name) {
		return FfmCalls.mdbx_dbi_rename(txn, dbi, name);
	}

	public static int mdbx_dbi_rename2(long txn, long dbi, MDBX_val name) {
		return FfmCalls.mdbx_dbi_rename2(txn, dbi, name);
	}

	public static int mdbx_get(long txn, long dbi, MDBX_val key, MDBX_val data) {
		return FfmCalls.mdbx_get(txn, dbi, key, data);
	}

	public static int mdbx_get_ex(long txn, long dbi, MDBX_val key, MDBX_val data, long[] values_count) {
		return FfmCalls.mdbx_get_ex(txn, dbi, key, data, values_count);
	}

	public static int mdbx_get_attr(long txn, long dbi, MDBX_val key, MDBX_val data, long[] pattr) {
		throw new UnsupportedOperationException("mdbx_get_attr");
	}

	public static int mdbx_get_equal_or_great(long txn, long dbi, MDBX_val key, MDBX_val data) {
		return FfmCalls.mdbx_get_equal_or_great(txn, dbi, key, data);
	}

	public static int mdbx_put(long txn, long dbi, MDBX_val key, MDBX_val data, int flags) {
		return FfmCalls.mdbx_put(txn, dbi, key, data, flags);
	}

	public static int mdbx_put_multiple(long txn, long dbi, MDBX_val key, MDBX_val data1, MDBX_val data2, int flags) {
		return FfmCalls.mdbx_put_multiple(txn, dbi, key, data1, data2, flags);
	}

	public static int mdbx_put_attr(long txn, long dbi, MDBX_val key, MDBX_val data, long[] attr, int flags) {
		throw new UnsupportedOperationException("mdbx_put_attr");
	}

	public static int mdbx_replace(long txn, long dbi, MDBX_val key, MDBX_val new_data, MDBX_val old_data, int flags) {
		return FfmCalls.mdbx_replace(txn, dbi, key, new_data, old_data, flags);
	}

	public static int mdbx_del(long txn, long dbi, MDBX_val key, MDBX_val data) {
		return FfmCalls.mdbx_del(txn, dbi, key, data);
	}

	public static int mdbx_drop(long txn, long dbi, int del) {
		return FfmCalls.mdbx_drop(txn, dbi, del);
	}

	public static int mdbx_canary_get(long txn, MDBX_canary canary) {
		throw new UnsupportedOperationException("mdbx_canary_get");
	}

	public static int mdbx_canary_put(long txn, MDBX_canary canary) {
		throw new UnsupportedOperationException("mdbx_canary_put");
	}

	public static int mdbx_cursor_open(long txn, long dbi, long[] cursor) {
		return FfmCalls.mdbx_cursor_open(txn, dbi, cursor);
	}

	public static void mdbx_cursor_close(long cursor) {
		FfmCalls.mdbx_cursor_close(cursor);
	}

	public static int mdbx_cursor_close2(long cursor) {
		throw new UnsupportedOperationException("mdbx_cursor_close2");
	}

	public static int mdbx_cursor_renew(long txn, long cursor) {
		return FfmCalls.mdbx_cursor_renew(txn, cursor);
	}

	public static int mdbx_cursor_bind(long txn, long cursor, long dbi) {
		return FfmCalls.mdbx_cursor_bind(txn, cursor, dbi);
	}

	public static int mdbx_cursor_unbind(long cursor) {
		throw new UnsupportedOperationException("mdbx_cursor_unbind");
	}

	public static int mdbx_cursor_reset(long cursor) {
		throw new UnsupportedOperationException("mdbx_cursor_reset");
	}

	public static int mdbx_cursor_copy(long src, long dest) {
		throw new UnsupportedOperationException("mdbx_cursor_copy");
	}

	public static int mdbx_cursor_compare(long left, long right, int ignore_multival) {
		throw new UnsupportedOperationException("mdbx_cursor_compare");
	}

	public static int mdbx_cursor_get(long cursor, MDBX_val key, MDBX_val data, int op) {
		return FfmCalls.mdbx_cursor_get(cursor, key, data, op);
	}

	public static int mdbx_cursor_get_batch(long cursor, long[] count, MDBX_val pairs, long limit, int op) {
		throw new UnsupportedOperationException("mdbx_cursor_get_batch");
	}

	public static int mdbx_cursor_ignord(long cursor) {
		throw new UnsupportedOperationException("mdbx_cursor_ignord");
	}

	public static int mdbx_cursor_ignor2d(long cursor) {
		throw new UnsupportedOperationException("mdbx_cursor_ignor2d");
	}

	public static int mdbx_cursor_scan(long cursor, long predicate, long context, int start_op, int turn_op, long arg) {
		throw new UnsupportedOperationException("mdbx_cursor_scan");
	}

	public static int mdbx_cursor_scan_from(long cursor, long predicate, long context, int from_op, MDBX_val from_key, MDBX_val from_value, int turn_op, long arg) {
		throw new UnsupportedOperationException("mdbx_cursor_scan_from");
	}

	public static int mdbx_cursor_put(long cursor, MDBX_val key, MDBX_val data, int flags) {
		return FfmCalls.mdbx_cursor_put(cursor, key, data, flags);
	}

	public static int mdbx_cursor_get_attr(long cursor, MDBX_val key, MDBX_val data, long[] pattr, int op) {
		throw new UnsupportedOperationException("mdbx_cursor_get_attr");
	}

	public static int mdbx_cursor_put_attr(long cursor, MDBX_val key, MDBX_val data, long attr, int flags) {
		throw new UnsupportedOperationException("mdbx_cursor_put_attr");
	}

	public static long mdbx_cursor_txn(long cursor) {
		throw new UnsupportedOperationException("mdbx_cursor_txn");
	}

	public static long mdbx_cursor_dbi(long cursor) {
		throw new UnsupportedOperationException("mdbx_cursor_dbi");
	}

	public static int mdbx_cursor_del(long cursor, int flags) {
		return FfmCalls.mdbx_cursor_del(cursor, flags);
	}

	public static int mdbx_cursor_count(long cursor, long[] count) {
		return FfmCalls.mdbx_cursor_count(cursor, count);
	}

	public static int mdbx_cursor_count_ex(long cursor, long[] count, MDBX_stat stat, long bytes) {
		throw new UnsupportedOperationException("mdbx_cursor_count_ex");
	}

	public static int mdbx_cursor_eof(long cursor) {
		throw new UnsupportedOperationException("mdbx_cursor_eof");
	}

	public static int mdbx_cursor_on_first(long cursor) {
		throw new UnsupportedOperationException("mdbx_cursor_on_first");
	}

	public static int mdbx_cursor_on_first_dup(long cursor) {
		throw new UnsupportedOperationException("mdbx_cursor_on_first_dup");
	}

	public static int mdbx_cursor_on_last_dup(long cursor) {
		throw new UnsupportedOperationException("mdbx_cursor_on_last_dup");
	}

	public static int mdbx_cursor_on_last(long cursor) {
		throw new UnsupportedOperationException("mdbx_cursor_on_last");
	}

	public static long mdbx_cursor_create(long ctx) {
		return FfmCalls.mdbx_cursor_create(ctx);
	}

	public static long mdbx_cursor_get_userctx(long cursor) {
		throw new UnsupportedOperationException("mdbx_cursor_get_userctx");
	}

	public static int mdbx_cursor_set_userctx(long cursor, long ctx) {
		throw new UnsupportedOperationException("mdbx_cursor_set_userctx");
	}

	public static int mdbx_cmp(long txn, long dbi, MDBX_val a, MDBX_val b) {
		throw new UnsupportedOperationException("mdbx_cmp");
	}

	public static int mdbx_dcmp(long txn, long dbi, MDBX_val a, MDBX_val b) {
		throw new UnsupportedOperationException("mdbx_dcmp");
	}

	public static long mdbx_get_keycmp(int flags) {
		throw new UnsupportedOperationException("mdbx_get_keycmp");
	}

	public static long mdbx_get_datacmp(int flags) {
		throw new UnsupportedOperationException("mdbx_get_datacmp");
	}

	public static long mdbx_key_from_jsonInteger(long json_integer) {
		throw new UnsupportedOperationException("mdbx_key_from_jsonInteger");
	}

	public static long mdbx_key_from_double(double ieee754_64bit) {
		throw new UnsupportedOperationException("mdbx_key_from_double");
	}

	public static long mdbx_key_from_ptrdouble(long[] ieee754_64bit) {
		throw new UnsupportedOperationException("mdbx_key_from_ptrdouble");
	}

	public static int mdbx_key_from_float(float ieee754_32bit) {
		throw new UnsupportedOperationException("mdbx_key_from_float");
	}

	public static int mdbx_key_from_ptrfloat(long[] ieee754_32bit) {
		throw new UnsupportedOperationException("mdbx_key_from_ptrfloat");
	}

	public static long mdbx_key_from_int64(long i64) {
		throw new UnsupportedOperationException("mdbx_key_from_int64");
	}

	public static int mdbx_key_from_int32(int i32) {
		throw new UnsupportedOperationException("mdbx_key_from_int32");
	}

	public static long mdbx_jsonInteger_from_key(MDBX_val val) {
		throw new UnsupportedOperationException("mdbx_jsonInteger_from_key");
	}

	public static double mdbx_double_from_key(MDBX_val val) {
		throw new UnsupportedOperationException("mdbx_double_from_key");
	}

	public static float mdbx_float_from_key(MDBX_val val) {
		throw new UnsupportedOperationException("mdbx_float_from_key");
	}

	public static int mdbx_int32_from_key(MDBX_val val) {
		throw new UnsupportedOperationException("mdbx_int32_from_key");
	}

	public static long mdbx_int64_from_key(MDBX_val val) {
		throw new UnsupportedOperationException("mdbx_int64_from_key");
	}

	public static long mdbx_limits_dbsize_min(long pagesize) {
		return FfmCalls.mdbx_limits_dbsize_min(pagesize);
	}

	public static long mdbx_limits_dbsize_max(long pagesize) {
		return FfmCalls.mdbx_limits_dbsize_max(pagesize);
	}

	public static long mdbx_limits_keysize_min(long flags) {
		return FfmCalls.mdbx_limits_keysize_min(flags);
	}

	public static long mdbx_limits_keysize_max(long pagesize, long flags) {
		return FfmCalls.mdbx_limits_keysize_max(pagesize, flags);
	}

	public static long mdbx_limits_pgsize_min() {
		return FfmCalls.mdbx_limits_pgsize_min();
	}

	public static long mdbx_limits_pgsize_max() {
		return FfmCalls.mdbx_limits_pgsize_max();
	}

	public static long mdbx_limits_txnsize_max(long pagesize) {
		return FfmCalls.mdbx_limits_txnsize_max(pagesize);
	}

	public static long mdbx_limits_valsize_min(long flags) {
		return FfmCalls.mdbx_limits_valsize_min(flags);
	}

	public static long mdbx_limits_valsize_max(long pagesize, long flags) {
		return FfmCalls.mdbx_limits_valsize_max(pagesize, flags);
	}

	public static long mdbx_limits_pairsize4page_max(long pagesize, long flags) {
		return FfmCalls.mdbx_limits_pairsize4page_max(pagesize, flags);
	}

	public static long mdbx_limits_valsize4page_max(long pagesize, long flags) {
		return FfmCalls.mdbx_limits_valsize4page_max(pagesize, flags);
	}

	public static int mdbx_reader_check(long env, int[] dead) {
		throw new UnsupportedOperationException("mdbx_reader_check");
	}

	public static int mdbx_reader_list(long env, long func, long ctx) {
		throw new UnsupportedOperationException("mdbx_reader_list");
	}

	public static int mdbx_estimate_distance(long first, long last, long[] distance_items) {
		throw new UnsupportedOperationException("mdbx_estimate_distance");
	}

	public static int mdbx_estimate_move(long cursor, MDBX_val key, MDBX_val data, long move_op, long[] distance_items) {
		throw new UnsupportedOperationException("mdbx_estimate_move");
	}

	public static int mdbx_estimate_range(long txn, long dbi, MDBX_val begin_key, MDBX_val begin_data, MDBX_val end_key, MDBX_val end_data, long[] distance_items) {
		throw new UnsupportedOperationException("mdbx_estimate_range");
	}

	public static long mdbx_default_pagesize() {
		throw new UnsupportedOperationException("mdbx_default_pagesize");
	}

	public static int mdbx_is_dirty(long txn, long ptr) {
		throw new UnsupportedOperationException("mdbx_is_dirty");
	}

	public static int mdbx_get_sysraminfo(long[] page_size, long[] total_pages, long[] avail_pages) {
		return FfmCalls.mdbx_get_sysraminfo(page_size, total_pages, avail_pages);
	}

	public static int mdbx_set_compare(long txn, long dbi, long cmp) {
		throw new UnsupportedOperationException("mdbx_set_compare");
	}

	public static int mdbx_set_dupsort(long txn, long dbi, long cmp) {
		throw new UnsupportedOperationException("mdbx_set_dupsort");
	}

	public static void mdbx_assert_fail(String msg, String func, int line) {
		FfmCalls.mdbx_assert_fail(msg, func, line);
	}

	public static void mdbx_set_panic(long panicFunc) {
		FfmCalls.mdbx_set_panic(panicFunc);
	}

	public static long mdbx_dump_val(MDBX_val key, long buf, long bufsize) {
		return FfmCalls.mdbx_dump_val(key, buf, bufsize);
	}

	public static long mdbx_liberr2str(int errnum) {
		return FfmCalls.mdbx_liberr2str(errnum);
	}

	public static int mdbx_is_readahead_reasonable(long volume, long redundancy) {
		return FfmCalls.mdbx_is_readahead_reasonable(volume, redundancy);
	}

	public static int mdbx_thread_register(long env) {
		return FfmCalls.mdbx_thread_register(env);
	}

	public static int mdbx_thread_unregister(long env) {
		return FfmCalls.mdbx_thread_unregister(env);
	}

	public static long mdbx_env_get_hsr(long env) {
		return FfmCalls.mdbx_env_get_hsr(env);
	}

	public static long mdbx_ratio2digits(long numerator, long denominator, int precision, long buffer, long bufferSize) {
		return FfmCalls.mdbx_ratio2digits(numerator, denominator, precision, buffer, bufferSize);
	}

	public static long mdbx_ratio2percents(long value, long whole, long buffer, long bufferSize) {
		return FfmCalls.mdbx_ratio2percents(value, whole, buffer, bufferSize);
	}

	public static int mdbx_gc_info(long txn, MDBX_gc_info info, long bytes, long iterFunc, long iterCtx) {
		return FfmCalls.mdbx_gc_info(txn, info, bytes, iterFunc, iterCtx);
	}

	public static int mdbx_env_defrag(long env, long defragAtLeast, long timeAtLeastDot16, long defragEnough,
			long timeLimitDot16, long acceptableBacklash, long preferredBatch, long progressCallback, long ctx,
			MDBX_defrag_result result) {
		return FfmCalls.mdbx_env_defrag(env, defragAtLeast, timeAtLeastDot16, defragEnough, timeLimitDot16,
				acceptableBacklash, preferredBatch, progressCallback, ctx, result);
	}

	public static int mdbx_txn_clone(long origin, long[] inOutClone, long context) {
		return FfmCalls.mdbx_txn_clone(origin, inOutClone, context);
	}

	public static int mdbx_txn_checkpoint(long txn, long weakeningDurability, MDBX_commit_latency latency) {
		return FfmCalls.mdbx_txn_checkpoint(txn, weakeningDurability, latency);
	}

	public static int mdbx_txn_commit_embark_read(long[] ptxn, MDBX_commit_latency latency) {
		return FfmCalls.mdbx_txn_commit_embark_read(ptxn, latency);
	}

	public static int mdbx_txn_amend(long readTxn, long[] ptrWriteTxn, long flags, long context) {
		return FfmCalls.mdbx_txn_amend(readTxn, ptrWriteTxn, flags, context);
	}

	public static int mdbx_txn_rollback(long txn) {
		return FfmCalls.mdbx_txn_rollback(txn);
	}

	public static int mdbx_txn_abort_ex(long txn, MDBX_commit_latency latency) {
		return FfmCalls.mdbx_txn_abort_ex(txn, latency);
	}

	public static int mdbx_txn_refresh(long txn) {
		return FfmCalls.mdbx_txn_refresh(txn);
	}

	public static int mdbx_dbi_open2(long txn, MDBX_val name, int flags, long[] dbi) {
		return FfmCalls.mdbx_dbi_open2(txn, name, flags, dbi);
	}

	public static int mdbx_dbi_open_ex2(long txn, MDBX_val name, int flags, long[] dbi, long keycmp, long datacmp) {
		return FfmCalls.mdbx_dbi_open_ex2(txn, name, flags, dbi, keycmp, datacmp);
	}

	public static int mdbx_enumerate_tables(long txn, long func, long ctx) {
		return FfmCalls.mdbx_enumerate_tables(txn, func, ctx);
	}

	public static void mdbx_cache_init(MDBX_cache_entry entry) {
		FfmCalls.mdbx_cache_init(entry);
	}

	public static MDBX_cache_result mdbx_cache_get(long txn, long dbi, MDBX_val key, MDBX_val data, MDBX_cache_entry entry) {
		return FfmCalls.mdbx_cache_get(txn, dbi, key, data, entry);
	}

	public static MDBX_cache_result mdbx_cache_get_SingleThreaded(long txn, long dbi, MDBX_val key, MDBX_val data, MDBX_cache_entry entry) {
		return FfmCalls.mdbx_cache_get_SingleThreaded(txn, dbi, key, data, entry);
	}

	public static int mdbx_replace_ex(long txn, long dbi, MDBX_val key, MDBX_val newData, MDBX_val oldData, int flags,
			long preserver, long preserverContext) {
		return FfmCalls.mdbx_replace_ex(txn, dbi, key, newData, oldData, flags, preserver, preserverContext);
	}

	public static int mdbx_cursor_delete_range(long begin, long end, int endIncluding, long[] numberOfAffected) {
		return FfmCalls.mdbx_cursor_delete_range(begin, end, endIncluding, numberOfAffected);
	}

	public static int mdbx_cursor_distance(long first, long last, long[] distance, int deepness) {
		return FfmCalls.mdbx_cursor_distance(first, last, distance, deepness);
	}

	public static int mdbx_cursor_scroll(long cursor, long amount, int deepness) {
		return FfmCalls.mdbx_cursor_scroll(cursor, amount, deepness);
	}

	public static int mdbx_cursor_distribute(long first, long last, long[] cursors, int deepness) {
		return FfmCalls.mdbx_cursor_distribute(first, last, cursors, deepness);
	}

	public static int mdbx_cursor_bunch_delete(long cursor, int action, long[] numberOfAffected) {
		return FfmCalls.mdbx_cursor_bunch_delete(cursor, action, numberOfAffected);
	}

}
