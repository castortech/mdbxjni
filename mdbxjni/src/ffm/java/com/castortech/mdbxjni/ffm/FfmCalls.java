package com.castortech.mdbxjni.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;

import com.castortech.mdbxjni.JNI.MDBX_build_info;
import com.castortech.mdbxjni.JNI.MDBX_cache_entry;
import com.castortech.mdbxjni.JNI.MDBX_cache_result;
import com.castortech.mdbxjni.JNI.MDBX_chk_histogram;
import com.castortech.mdbxjni.JNI.MDBX_commit_latency;
import com.castortech.mdbxjni.JNI.MDBX_defrag_result;
import com.castortech.mdbxjni.JNI.MDBX_envinfo;
import com.castortech.mdbxjni.JNI.MDBX_gc_info;
import com.castortech.mdbxjni.JNI.MDBX_stat;
import com.castortech.mdbxjni.JNI.MDBX_txn_info;
import com.castortech.mdbxjni.JNI.MDBX_val;
import com.castortech.mdbxjni.JNI.MDBX_version_info;
import com.castortech.mdbxjni.ffm.gen.iovec;
import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * The calls the existing Java API still makes, expressed with the published
 * libmdbx surface. Pointers stay {@code long} on the Java side of that API.
 */
public final class FfmCalls {
	private static final MethodHandle MALLOC = downcall("malloc",
			FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG));
	private static final MethodHandle FREE = downcall("free",
			FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
	private static final MethodHandle STRERROR = downcall("strerror",
			FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
	private static final MethodHandle ERRNO = errnoHandle();

	private FfmCalls() {
	}

	public static void readVersion(MDBX_version_info info) {
		MemorySegment segment = global("mdbx_version", com.castortech.mdbxjni.ffm.gen.MDBX_version_info.sizeof());
		info.major = Short.toUnsignedInt(com.castortech.mdbxjni.ffm.gen.MDBX_version_info.major(segment));
		info.minor = Short.toUnsignedInt(com.castortech.mdbxjni.ffm.gen.MDBX_version_info.minor(segment));
		info.patch = Short.toUnsignedInt(com.castortech.mdbxjni.ffm.gen.MDBX_version_info.patch(segment));
		info.tweak = Short.toUnsignedInt(com.castortech.mdbxjni.ffm.gen.MDBX_version_info.tweak(segment));
		info.semver_prerelease = chars(com.castortech.mdbxjni.ffm.gen.MDBX_version_info.semver_prerelease(segment));
		MemorySegment git = com.castortech.mdbxjni.ffm.gen.MDBX_version_info.git(segment);
		info.git_datetime = chars(com.castortech.mdbxjni.ffm.gen.MDBX_version_info.git.datetime(git));
		info.git_tree = chars(com.castortech.mdbxjni.ffm.gen.MDBX_version_info.git.tree(git));
		info.git_commit = chars(com.castortech.mdbxjni.ffm.gen.MDBX_version_info.git.commit(git));
		info.git_describe = chars(com.castortech.mdbxjni.ffm.gen.MDBX_version_info.git.describe(git));
		info.sourcery = chars(com.castortech.mdbxjni.ffm.gen.MDBX_version_info.sourcery(segment));
	}

	public static void readBuild(MDBX_build_info info) {
		MemorySegment segment = global("mdbx_build", com.castortech.mdbxjni.ffm.gen.MDBX_build_info.sizeof());
		info.datetime = chars(com.castortech.mdbxjni.ffm.gen.MDBX_build_info.datetime(segment));
		info.target = chars(com.castortech.mdbxjni.ffm.gen.MDBX_build_info.target(segment));
		info.options = chars(com.castortech.mdbxjni.ffm.gen.MDBX_build_info.options(segment));
		info.compiler = chars(com.castortech.mdbxjni.ffm.gen.MDBX_build_info.compiler(segment));
		info.flags = chars(com.castortech.mdbxjni.ffm.gen.MDBX_build_info.flags(segment));
		info.metadata = chars(com.castortech.mdbxjni.ffm.gen.MDBX_build_info.metadata(segment));
	}

	public static int errno() {
		if (ERRNO == null) {
			return 0;
		}
		try {
			MemorySegment slot = (MemorySegment) ERRNO.invokeExact();
			return slot.reinterpret(4).get(ValueLayout.JAVA_INT, 0);
		}
		catch (Throwable failure) {
			throw new IllegalStateException(failure);
		}
	}

	public static long strerror(int errnum) {
		try {
			return ((MemorySegment) STRERROR.invokeExact(errnum)).address();
		}
		catch (Throwable failure) {
			throw new IllegalStateException(failure);
		}
	}

	public static int strlen(long address) {
		if (address == 0) {
			return 0;
		}
		MemorySegment text = MemorySegment.ofAddress(address).reinterpret(Integer.MAX_VALUE);
		int length = 0;
		while (text.get(ValueLayout.JAVA_BYTE, length) != 0) {
			length++;
		}
		return length;
	}

	public static long malloc(long size) {
		try {
			return ((MemorySegment) MALLOC.invokeExact(size)).address();
		}
		catch (Throwable failure) {
			throw new IllegalStateException(failure);
		}
	}

	public static void free(long address) {
		if (address == 0) {
			return;
		}
		try {
			FREE.invokeExact(MemorySegment.ofAddress(address));
		}
		catch (Throwable failure) {
			throw new IllegalStateException(failure);
		}
	}

	public static void buffer_copy(byte[] source, long sourcePos, long dest, long destPos, long length) {
		if (length == 0) {
			return;
		}
		MemorySegment.ofAddress(dest + destPos).reinterpret(length)
				.copyFrom(MemorySegment.ofArray(source).asSlice(sourcePos, length));
	}

	public static void buffer_copy(long source, long sourcePos, byte[] dest, long destPos, long length) {
		if (length == 0) {
			return;
		}
		MemorySegment.ofArray(dest).asSlice(destPos, length)
				.copyFrom(MemorySegment.ofAddress(source + sourcePos).reinterpret(length));
	}

	public static void map_val(long in, MDBX_val out) {
		if (in == 0 || out == null) {
			return;
		}
		MemorySegment val = MemorySegment.ofAddress(in).reinterpret(iovec.sizeof());
		out.iov_base = val.get(ValueLayout.ADDRESS, iovec.iov_base$offset()).address();
		out.iov_len = val.get(ValueLayout.JAVA_LONG, iovec.iov_len$offset());
	}

	public static long map_printf(long buf, int size, long fmt, long args) {
		if (buf == 0 || size <= 0) {
			return buf;
		}
		int available = size - 1;
		int length = fmt == 0 ? 0 : Math.min(strlen(fmt), available);
		if (length > 0) {
			MemorySegment.ofAddress(buf).reinterpret(length)
					.copyFrom(MemorySegment.ofAddress(fmt).reinterpret(length));
		}
		MemorySegment.ofAddress(buf + length).reinterpret(1).set(ValueLayout.JAVA_BYTE, 0, (byte) 0);
		return buf;
	}

	public static long mdbx_strerror(int errnum) {
		return mdbx_h.mdbx_strerror(errnum).address();
	}

	public static int mdbx_setup_debug(int logLevel, int debugFlags, long logger) {
		return mdbx_h.mdbx_setup_debug_nofmt(logLevel, debugFlags, MemorySegment.NULL, MemorySegment.NULL, 0);
	}

	public static int mdbx_env_create(long[] penv) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outPointer(arena);
			int rc = mdbx_h.mdbx_env_create(slot);
			writePointer(penv, slot);
			return rc;
		}
	}

	public static int mdbx_env_open(long env, String pathname, int flags, int mode) {
		try (Arena arena = Arena.ofConfined()) {
			return mdbx_h.mdbx_env_open(ptr(env), text(arena, pathname), flags, (short) mode);
		}
	}

	public static int mdbx_env_openW(long env, String pathname, int flags, int mode) {
		try (Arena arena = Arena.ofConfined()) {
			return mdbx_h.mdbx_env_openW(ptr(env), wide(arena, pathname), flags, (short) mode);
		}
	}

	public static int mdbx_env_close(long env) {
		return MdbxInline.mdbx_env_close(ptr(env));
	}

	public static int mdbx_env_copy(long env, String dest, int flags) {
		try (Arena arena = Arena.ofConfined()) {
			return mdbx_h.mdbx_env_copy(ptr(env), text(arena, dest), flags);
		}
	}

	public static int mdbx_env_sync(long env) {
		return MdbxInline.mdbx_env_sync(ptr(env));
	}

	public static int mdbx_env_sync_ex(long env, int force, int nonblock) {
		return mdbx_h.mdbx_env_sync_ex(ptr(env), force != 0, nonblock != 0);
	}

	public static int mdbx_env_sync_poll(long env) {
		return mdbx_h.mdbx_env_sync_ex(ptr(env), false, true);
	}

	public static int mdbx_env_set_mapsize(long env, long size) {
		return MdbxInline.mdbx_env_set_mapsize(ptr(env), size);
	}

	public static int mdbx_env_set_geometry(long env, long lower, long now, long upper, long growth, long shrink,
			long pagesize) {
		return mdbx_h.mdbx_env_set_geometry(ptr(env), lower, now, upper, growth, shrink, pagesize);
	}

	public static int mdbx_env_get_maxdbs(long env, long[] dbs) {
		return readOption(env, mdbx_h.MDBX_opt_max_db(), dbs);
	}

	public static int mdbx_env_set_maxdbs(long env, long dbs) {
		return MdbxInline.mdbx_env_set_maxdbs(ptr(env), (int) dbs);
	}

	public static int mdbx_env_get_maxreaders(long env, long[] readers) {
		return readOption(env, mdbx_h.MDBX_opt_max_readers(), readers);
	}

	public static int mdbx_env_set_maxreaders(long env, int readers) {
		return MdbxInline.mdbx_env_set_maxreaders(ptr(env), readers);
	}

	public static int mdbx_env_get_maxkeysize(long env) {
		return mdbx_h.mdbx_env_get_maxkeysize(ptr(env));
	}

	public static int mdbx_env_get_maxkeysize_ex(long env, long flags) {
		return mdbx_h.mdbx_env_get_maxkeysize_ex(ptr(env), (int) flags);
	}

	public static int mdbx_env_get_maxvalsize_ex(long env, long flags) {
		return mdbx_h.mdbx_env_get_maxvalsize_ex(ptr(env), (int) flags);
	}

	public static int mdbx_env_get_flags(long env, long[] flags) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outInt(arena);
			int rc = mdbx_h.mdbx_env_get_flags(ptr(env), slot);
			writeUnsigned(flags, slot);
			return rc;
		}
	}

	public static int mdbx_env_set_flags(long env, int flags, int onoff) {
		return mdbx_h.mdbx_env_set_flags(ptr(env), flags, onoff != 0);
	}

	public static long mdbx_env_get_userctx(long env) {
		return mdbx_h.mdbx_env_get_userctx(ptr(env)).address();
	}

	public static int mdbx_env_set_userctx(long env, long ctx) {
		return mdbx_h.mdbx_env_set_userctx(ptr(env), ptr(ctx));
	}

	public static int mdbx_env_set_option(long env, int option, long value) {
		return mdbx_h.mdbx_env_set_option(ptr(env), option, value);
	}

	public static int mdbx_env_get_option(long env, int option, long[] value) {
		return readOption(env, option, value);
	}

	public static int mdbx_env_info(long env, MDBX_envinfo info, long bytes) {
		return mdbx_env_info_ex(env, 0, info, bytes);
	}

	public static int mdbx_env_info_ex(long env, long txn, MDBX_envinfo info, long bytes) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment segment = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.allocate(arena);
			int rc = mdbx_h.mdbx_env_info_ex(ptr(env), ptr(txn), segment, segment.byteSize());
			if (rc == mdbx_h.MDBX_SUCCESS() && info != null) {
				copyEnvInfo(segment, info);
			}
			return rc;
		}
	}

	public static int mdbx_env_stat(long env, MDBX_stat stat, long bytes) {
		return mdbx_env_stat_ex(env, 0, stat, bytes);
	}

	public static int mdbx_env_stat_ex(long env, long txn, MDBX_stat stat, long bytes) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment segment = com.castortech.mdbxjni.ffm.gen.MDBX_stat.allocate(arena);
			int rc = mdbx_h.mdbx_env_stat_ex(ptr(env), ptr(txn), segment, segment.byteSize());
			if (rc == mdbx_h.MDBX_SUCCESS()) {
				copyStat(segment, stat);
			}
			return rc;
		}
	}

	public static int mdbx_txn_begin(long env, long parent, long flags, long[] txn) {
		return mdbx_txn_begin_ex(env, parent, flags, txn, 0);
	}

	public static int mdbx_txn_begin_ex(long env, long parent, long flags, long[] txn, long ctx) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outPointer(arena);
			int rc = mdbx_h.mdbx_txn_begin_ex(ptr(env), ptr(parent), (int) flags, slot, ptr(ctx));
			writePointer(txn, slot);
			return rc;
		}
	}

	public static int mdbx_txn_commit(long txn) {
		return MdbxInline.mdbx_txn_commit(ptr(txn));
	}

	public static int mdbx_txn_commit_ex(long txn, MDBX_commit_latency latency) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment segment = latency == null ? MemorySegment.NULL
					: com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.allocate(arena);
			int rc = mdbx_h.mdbx_txn_commit_ex(ptr(txn), segment);
			if (rc == mdbx_h.MDBX_SUCCESS() && latency != null) {
				copyLatency(segment, latency);
			}
			return rc;
		}
	}

	public static void mdbx_txn_abort(long txn) {
		MdbxInline.mdbx_txn_abort(ptr(txn));
	}

	public static void mdbx_txn_reset(long txn) {
		mdbx_h.mdbx_txn_reset(ptr(txn));
	}

	public static int mdbx_txn_renew(long txn) {
		return mdbx_h.mdbx_txn_renew(ptr(txn));
	}

	public static int mdbx_txn_break(long txn) {
		return mdbx_h.mdbx_txn_break(ptr(txn));
	}

	public static long mdbx_txn_id(long txn) {
		return mdbx_h.mdbx_txn_id(ptr(txn));
	}

	public static int mdbx_txn_flags(long txn) {
		return mdbx_h.mdbx_txn_flags(ptr(txn));
	}

	public static long mdbx_txn_get_userctx(long txn) {
		return mdbx_h.mdbx_txn_get_userctx(ptr(txn)).address();
	}

	public static int mdbx_txn_set_userctx(long txn, long ctx) {
		return mdbx_h.mdbx_txn_set_userctx(ptr(txn), ptr(ctx));
	}

	public static int mdbx_txn_info(long txn, MDBX_txn_info info, int scanRlt) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment segment = com.castortech.mdbxjni.ffm.gen.MDBX_txn_info.allocate(arena);
			int rc = mdbx_h.mdbx_txn_info(ptr(txn), segment, scanRlt != 0);
			if (rc == mdbx_h.MDBX_SUCCESS() && info != null) {
				copyTxnInfo(segment, info);
			}
			return rc;
		}
	}

	public static int mdbx_txn_release_all_cursors(long txn, int unbind) {
		return mdbx_h.mdbx_txn_release_all_cursors_ex(ptr(txn), unbind != 0, MemorySegment.NULL);
	}

	public static int mdbx_txn_release_all_cursors_ex(long txn, int unbind, long count) {
		return mdbx_txn_release_all_cursors(txn, unbind);
	}

	public static int mdbx_dbi_open(long txn, String name, int flags, long[] dbi) {
		return mdbx_dbi_open_ex(txn, name, flags, dbi, 0, 0);
	}

	public static int mdbx_dbi_open_ex(long txn, String name, int flags, long[] dbi, long keycmp, long datacmp) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outInt(arena);
			int rc = mdbx_h.mdbx_dbi_open_ex(ptr(txn), text(arena, name), flags, slot, ptr(keycmp), ptr(datacmp));
			writeUnsigned(dbi, slot);
			return rc;
		}
	}

	public static void mdbx_dbi_close(long env, long dbi) {
		mdbx_h.mdbx_dbi_close(ptr(env), (int) dbi);
	}

	public static int mdbx_dbi_stat(long txn, long dbi, MDBX_stat stat, long bytes) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment segment = com.castortech.mdbxjni.ffm.gen.MDBX_stat.allocate(arena);
			int rc = mdbx_h.mdbx_dbi_stat(ptr(txn), (int) dbi, segment, segment.byteSize());
			if (rc == mdbx_h.MDBX_SUCCESS()) {
				copyStat(segment, stat);
			}
			return rc;
		}
	}

	public static int mdbx_dbi_flags(long txn, long dbi, long[] flags) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outInt(arena);
			int rc = MdbxInline.mdbx_dbi_flags(ptr(txn), (int) dbi, slot);
			writeUnsigned(flags, slot);
			return rc;
		}
	}

	public static int mdbx_dbi_flags_ex(long txn, long dbi, long[] flags, long[] state) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment flagSlot = Mdbx.outInt(arena);
			MemorySegment stateSlot = Mdbx.outInt(arena);
			int rc = mdbx_h.mdbx_dbi_flags_ex(ptr(txn), (int) dbi, flagSlot, stateSlot);
			writeUnsigned(flags, flagSlot);
			writeUnsigned(state, stateSlot);
			return rc;
		}
	}

	public static int mdbx_dbi_dupsort_depthmask(long txn, long dbi, long[] mask) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outInt(arena);
			int rc = mdbx_h.mdbx_dbi_dupsort_depthmask(ptr(txn), (int) dbi, slot);
			writeUnsigned(mask, slot);
			return rc;
		}
	}

	public static int mdbx_dbi_sequence(long txn, long dbi, long[] result, long increment) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outLong(arena);
			int rc = mdbx_h.mdbx_dbi_sequence(ptr(txn), (int) dbi, slot, increment);
			writeLong(result, slot);
			return rc;
		}
	}

	public static int mdbx_dbi_rename(long txn, long dbi, String name) {
		try (Arena arena = Arena.ofConfined()) {
			return mdbx_h.mdbx_dbi_rename(ptr(txn), (int) dbi, text(arena, name));
		}
	}

	public static int mdbx_dbi_rename2(long txn, long dbi, MDBX_val name) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment val = val(arena, name);
			int rc = mdbx_h.mdbx_dbi_rename2(ptr(txn), (int) dbi, val);
			readVal(val, name);
			return rc;
		}
	}

	public static int mdbx_drop(long txn, long dbi, int del) {
		return mdbx_h.mdbx_drop(ptr(txn), (int) dbi, del != 0);
	}

	public static int mdbx_get(long txn, long dbi, MDBX_val key, MDBX_val data) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment keyVal = val(arena, key);
			MemorySegment dataVal = val(arena, data);
			int rc = mdbx_h.mdbx_get(ptr(txn), (int) dbi, keyVal, dataVal);
			readVal(keyVal, key);
			readVal(dataVal, data);
			return rc;
		}
	}

	public static int mdbx_get_ex(long txn, long dbi, MDBX_val key, MDBX_val data, long[] valuesCount) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment keyVal = val(arena, key);
			MemorySegment dataVal = val(arena, data);
			MemorySegment count = Mdbx.outLong(arena);
			int rc = mdbx_h.mdbx_get_ex(ptr(txn), (int) dbi, keyVal, dataVal, count);
			readVal(keyVal, key);
			readVal(dataVal, data);
			writeLong(valuesCount, count);
			return rc;
		}
	}

	public static int mdbx_get_equal_or_great(long txn, long dbi, MDBX_val key, MDBX_val data) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment keyVal = val(arena, key);
			MemorySegment dataVal = val(arena, data);
			int rc = mdbx_h.mdbx_get_equal_or_great(ptr(txn), (int) dbi, keyVal, dataVal);
			readVal(keyVal, key);
			readVal(dataVal, data);
			return rc;
		}
	}

	public static int mdbx_put(long txn, long dbi, MDBX_val key, MDBX_val data, int flags) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment keyVal = val(arena, key);
			MemorySegment dataVal = val(arena, data);
			int rc = mdbx_h.mdbx_put(ptr(txn), (int) dbi, keyVal, dataVal, flags);
			readVal(keyVal, key);
			readVal(dataVal, data);
			return rc;
		}
	}

	public static int mdbx_put_multiple(long txn, long dbi, MDBX_val key, MDBX_val data1, MDBX_val data2, int flags) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment keyVal = val(arena, key);
			MemorySegment pair = arena.allocate(iovec.layout(), 2);
			writeVal(pair, 0, data1);
			writeVal(pair, iovec.sizeof(), data2);
			int rc = mdbx_h.mdbx_put(ptr(txn), (int) dbi, keyVal, pair, flags);
			readVal(keyVal, key);
			readVal(pair, data1);
			readVal(pair.asSlice(iovec.sizeof()), data2);
			return rc;
		}
	}

	public static int mdbx_replace(long txn, long dbi, MDBX_val key, MDBX_val newData, MDBX_val oldData, int flags) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment keyVal = val(arena, key);
			MemorySegment newVal = val(arena, newData);
			MemorySegment oldVal = val(arena, oldData);
			int rc = mdbx_h.mdbx_replace(ptr(txn), (int) dbi, keyVal, newVal, oldVal, flags);
			readVal(keyVal, key);
			readVal(newVal, newData);
			readVal(oldVal, oldData);
			return rc;
		}
	}

	public static int mdbx_del(long txn, long dbi, MDBX_val key, MDBX_val data) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment keyVal = val(arena, key);
			MemorySegment dataVal = val(arena, data);
			int rc = mdbx_h.mdbx_del(ptr(txn), (int) dbi, keyVal, dataVal);
			readVal(keyVal, key);
			readVal(dataVal, data);
			return rc;
		}
	}

	public static int mdbx_cursor_open(long txn, long dbi, long[] cursor) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outPointer(arena);
			int rc = mdbx_h.mdbx_cursor_open(ptr(txn), (int) dbi, slot);
			writePointer(cursor, slot);
			return rc;
		}
	}

	public static void mdbx_cursor_close(long cursor) {
		mdbx_h.mdbx_cursor_close(ptr(cursor));
	}

	public static int mdbx_cursor_renew(long txn, long cursor) {
		return mdbx_h.mdbx_cursor_renew(ptr(txn), ptr(cursor));
	}

	public static int mdbx_cursor_bind(long txn, long cursor, long dbi) {
		return mdbx_h.mdbx_cursor_bind(ptr(txn), ptr(cursor), (int) dbi);
	}

	public static long mdbx_cursor_create(long ctx) {
		return mdbx_h.mdbx_cursor_create(ptr(ctx)).address();
	}

	public static int mdbx_cursor_get(long cursor, MDBX_val key, MDBX_val data, int op) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment keyVal = val(arena, key);
			MemorySegment dataVal = val(arena, data);
			int rc = mdbx_h.mdbx_cursor_get(ptr(cursor), keyVal, dataVal, op);
			readVal(keyVal, key);
			readVal(dataVal, data);
			return rc;
		}
	}

	public static int mdbx_cursor_put(long cursor, MDBX_val key, MDBX_val data, int flags) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment keyVal = val(arena, key);
			MemorySegment dataVal = val(arena, data);
			int rc = mdbx_h.mdbx_cursor_put(ptr(cursor), keyVal, dataVal, flags);
			readVal(keyVal, key);
			readVal(dataVal, data);
			return rc;
		}
	}

	public static int mdbx_cursor_del(long cursor, int flags) {
		return mdbx_h.mdbx_cursor_del(ptr(cursor), flags);
	}

	public static int mdbx_cursor_count(long cursor, long[] count) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outLong(arena);
			int rc = mdbx_h.mdbx_cursor_count(ptr(cursor), slot);
			writeLong(count, slot);
			return rc;
		}
	}

	public static long mdbx_limits_pgsize_min() {
		return mdbx_h.MDBX_MIN_PAGESIZE();
	}

	public static long mdbx_limits_pgsize_max() {
		return mdbx_h.MDBX_MAX_PAGESIZE();
	}

	public static long mdbx_limits_dbsize_min(long pagesize) {
		return mdbx_h.mdbx_limits_dbsize_min(pagesize);
	}

	public static long mdbx_limits_dbsize_max(long pagesize) {
		return mdbx_h.mdbx_limits_dbsize_max(pagesize);
	}

	public static long mdbx_limits_txnsize_max(long pagesize) {
		return mdbx_h.mdbx_limits_txnsize_max(pagesize);
	}

	public static long mdbx_limits_keysize_min(long flags) {
		return mdbx_h.mdbx_limits_keysize_min((int) flags);
	}

	public static long mdbx_limits_keysize_max(long pagesize, long flags) {
		return mdbx_h.mdbx_limits_keysize_max(pagesize, (int) flags);
	}

	public static long mdbx_limits_valsize_min(long flags) {
		return mdbx_h.mdbx_limits_valsize_min((int) flags);
	}

	public static long mdbx_limits_valsize_max(long pagesize, long flags) {
		return mdbx_h.mdbx_limits_valsize_max(pagesize, (int) flags);
	}

	public static long mdbx_limits_pairsize4page_max(long pagesize, long flags) {
		return mdbx_h.mdbx_limits_pairsize4page_max(pagesize, (int) flags);
	}

	public static long mdbx_limits_valsize4page_max(long pagesize, long flags) {
		return mdbx_h.mdbx_limits_valsize4page_max(pagesize, (int) flags);
	}

	public static int mdbx_get_sysraminfo(long[] pageSize, long[] totalPages, long[] availPages) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment page = Mdbx.outLong(arena);
			MemorySegment total = Mdbx.outLong(arena);
			MemorySegment avail = Mdbx.outLong(arena);
			int rc = mdbx_h.mdbx_get_sysraminfo(page, total, avail);
			writeLong(pageSize, page);
			writeLong(totalPages, total);
			writeLong(availPages, avail);
			return rc;
		}
	}

	public static void mdbx_assert_fail(String msg, String func, int line) {
		try (Arena arena = Arena.ofConfined()) {
			mdbx_h.mdbx_assert_fail(text(arena, msg), text(arena, func), line);
		}
	}

	public static void mdbx_set_panic(long panicFunc) {
		mdbx_h.mdbx_set_panic(ptr(panicFunc));
	}

	public static long mdbx_dump_val(MDBX_val key, long buf, long bufsize) {
		try (Arena arena = Arena.ofConfined()) {
			return mdbx_h.mdbx_dump_val(val(arena, key), ptr(buf), bufsize).address();
		}
	}

	public static long mdbx_liberr2str(int errnum) {
		return mdbx_h.mdbx_liberr2str(errnum).address();
	}

	public static int mdbx_is_readahead_reasonable(long volume, long redundancy) {
		return mdbx_h.mdbx_is_readahead_reasonable(volume, redundancy);
	}

	public static int mdbx_thread_register(long env) {
		return mdbx_h.mdbx_thread_register(ptr(env));
	}

	public static int mdbx_thread_unregister(long env) {
		return mdbx_h.mdbx_thread_unregister(ptr(env));
	}

	public static long mdbx_env_get_hsr(long env) {
		return mdbx_h.mdbx_env_get_hsr(ptr(env)).address();
	}

	public static long mdbx_ratio2digits(long numerator, long denominator, int precision, long buffer, long bufferSize) {
		return mdbx_h.mdbx_ratio2digits(numerator, denominator, precision, ptr(buffer), bufferSize).address();
	}

	public static long mdbx_ratio2percents(long value, long whole, long buffer, long bufferSize) {
		return mdbx_h.mdbx_ratio2percents(value, whole, ptr(buffer), bufferSize).address();
	}

	public static int mdbx_gc_info(long txn, MDBX_gc_info info, long bytes, long iterFunc, long iterCtx) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment segment = info == null ? MemorySegment.NULL
					: com.castortech.mdbxjni.ffm.gen.MDBX_gc_info.allocate(arena);
			long size = segment.address() == 0 ? 0 : bounded(bytes, segment.byteSize());
			int rc = mdbx_h.mdbx_gc_info(ptr(txn), segment, size, ptr(iterFunc), ptr(iterCtx));
			if (rc == mdbx_h.MDBX_SUCCESS() && info != null) {
				copyGcInfo(segment, info);
			}
			return rc;
		}
	}

	public static int mdbx_env_defrag(long env, long defragAtLeast, long timeAtLeastDot16, long defragEnough,
			long timeLimitDot16, long acceptableBacklash, long preferredBatch, long progressCallback, long ctx,
			MDBX_defrag_result result) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment segment = result == null ? MemorySegment.NULL
					: com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.allocate(arena);
			int rc = mdbx_h.mdbx_env_defrag(ptr(env), defragAtLeast, timeAtLeastDot16, defragEnough, timeLimitDot16,
					acceptableBacklash, preferredBatch, ptr(progressCallback), ptr(ctx), segment);
			if (result != null && segment.address() != 0) {
				copyDefrag(segment, result);
			}
			return rc;
		}
	}

	public static int mdbx_txn_clone(long origin, long[] inOutClone, long context) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = incomingPointer(arena, inOutClone);
			int rc = mdbx_h.mdbx_txn_clone(ptr(origin), slot, ptr(context));
			writePointer(inOutClone, slot);
			return rc;
		}
	}

	public static int mdbx_txn_checkpoint(long txn, long weakeningDurability, MDBX_commit_latency latency) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment segment = latency == null ? MemorySegment.NULL
					: com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.allocate(arena);
			int rc = mdbx_h.mdbx_txn_checkpoint(ptr(txn), (int) weakeningDurability, segment);
			if (rc == mdbx_h.MDBX_SUCCESS() && latency != null) {
				copyLatency(segment, latency);
			}
			return rc;
		}
	}

	public static int mdbx_txn_commit_embark_read(long[] ptxn, MDBX_commit_latency latency) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = incomingPointer(arena, ptxn);
			MemorySegment segment = latency == null ? MemorySegment.NULL
					: com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.allocate(arena);
			int rc = mdbx_h.mdbx_txn_commit_embark_read(slot, segment);
			writePointer(ptxn, slot);
			if (rc == mdbx_h.MDBX_SUCCESS() && latency != null) {
				copyLatency(segment, latency);
			}
			return rc;
		}
	}

	public static int mdbx_txn_amend(long readTxn, long[] ptrWriteTxn, long flags, long context) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = incomingPointer(arena, ptrWriteTxn);
			int rc = mdbx_h.mdbx_txn_amend(ptr(readTxn), slot, (int) flags, ptr(context));
			writePointer(ptrWriteTxn, slot);
			return rc;
		}
	}

	public static int mdbx_txn_rollback(long txn) {
		return mdbx_h.mdbx_txn_rollback(ptr(txn));
	}

	public static int mdbx_txn_abort_ex(long txn, MDBX_commit_latency latency) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment segment = latency == null ? MemorySegment.NULL
					: com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.allocate(arena);
			int rc = mdbx_h.mdbx_txn_abort_ex(ptr(txn), segment);
			if (rc == mdbx_h.MDBX_SUCCESS() && latency != null) {
				copyLatency(segment, latency);
			}
			return rc;
		}
	}

	public static int mdbx_txn_refresh(long txn) {
		return mdbx_h.mdbx_txn_refresh(ptr(txn));
	}

	public static int mdbx_dbi_open2(long txn, MDBX_val name, int flags, long[] dbi) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outInt(arena);
			int rc = mdbx_h.mdbx_dbi_open2(ptr(txn), val(arena, name), flags, slot);
			writeUnsigned(dbi, slot);
			return rc;
		}
	}

	public static int mdbx_dbi_open_ex2(long txn, MDBX_val name, int flags, long[] dbi, long keycmp, long datacmp) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outInt(arena);
			int rc = mdbx_h.mdbx_dbi_open_ex2(ptr(txn), val(arena, name), flags, slot, ptr(keycmp), ptr(datacmp));
			writeUnsigned(dbi, slot);
			return rc;
		}
	}

	public static int mdbx_enumerate_tables(long txn, long func, long ctx) {
		return mdbx_h.mdbx_enumerate_tables(ptr(txn), ptr(func), ptr(ctx));
	}

	public static void mdbx_cache_init(MDBX_cache_entry entry) {
		if (entry == null) {
			return;
		}
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment segment = com.castortech.mdbxjni.ffm.gen.MDBX_cache_entry.allocate(arena);
			writeCacheEntry(segment, entry);
			MdbxInline.mdbx_cache_init(segment);
			readCacheEntry(segment, entry);
		}
	}

	public static MDBX_cache_result mdbx_cache_get(long txn, long dbi, MDBX_val key, MDBX_val data, MDBX_cache_entry entry) {
		return cacheGet(false, txn, dbi, key, data, entry);
	}

	public static MDBX_cache_result mdbx_cache_get_SingleThreaded(long txn, long dbi, MDBX_val key, MDBX_val data,
			MDBX_cache_entry entry) {
		return cacheGet(true, txn, dbi, key, data, entry);
	}

	public static int mdbx_replace_ex(long txn, long dbi, MDBX_val key, MDBX_val newData, MDBX_val oldData, int flags,
			long preserver, long preserverContext) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment keyVal = val(arena, key);
			MemorySegment newVal = val(arena, newData);
			MemorySegment oldVal = val(arena, oldData);
			int rc = mdbx_h.mdbx_replace_ex(ptr(txn), (int) dbi, keyVal, newVal, oldVal, flags, ptr(preserver),
					ptr(preserverContext));
			readVal(keyVal, key);
			readVal(newVal, newData);
			readVal(oldVal, oldData);
			return rc;
		}
	}

	public static int mdbx_cursor_delete_range(long begin, long end, int endIncluding, long[] numberOfAffected) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outLong(arena);
			int rc = mdbx_h.mdbx_cursor_delete_range(ptr(begin), ptr(end), endIncluding != 0, slot);
			writeLong(numberOfAffected, slot);
			return rc;
		}
	}

	public static int mdbx_cursor_distance(long first, long last, long[] distance, int deepness) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outLong(arena);
			int rc = mdbx_h.mdbx_cursor_distance(ptr(first), ptr(last), slot, deepness);
			writeLong(distance, slot);
			return rc;
		}
	}

	public static int mdbx_cursor_scroll(long cursor, long amount, int deepness) {
		return mdbx_h.mdbx_cursor_scroll(ptr(cursor), amount, deepness);
	}

	public static int mdbx_cursor_distribute(long first, long last, long[] cursors, int deepness) {
		try (Arena arena = Arena.ofConfined()) {
			int count = cursors == null ? 0 : cursors.length;
			MemorySegment array = count == 0 ? MemorySegment.NULL : arena.allocate(ValueLayout.ADDRESS, count);
			for (int i = 0; i < count; i++) {
				array.setAtIndex(ValueLayout.ADDRESS, i, ptr(cursors[i]));
			}
			int rc = mdbx_h.mdbx_cursor_distribute(ptr(first), ptr(last), array, count, deepness);
			for (int i = 0; i < count; i++) {
				cursors[i] = array.getAtIndex(ValueLayout.ADDRESS, i).address();
			}
			return rc;
		}
	}

	public static int mdbx_cursor_bunch_delete(long cursor, int action, long[] numberOfAffected) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outLong(arena);
			int rc = mdbx_h.mdbx_cursor_bunch_delete(ptr(cursor), action, slot);
			writeLong(numberOfAffected, slot);
			return rc;
		}
	}

	private static MDBX_cache_result cacheGet(boolean singleThreaded, long txn, long dbi, MDBX_val key, MDBX_val data,
			MDBX_cache_entry entry) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment keyVal = val(arena, key);
			MemorySegment dataVal = val(arena, data);
			MemorySegment entrySeg = entry == null ? MemorySegment.NULL
					: com.castortech.mdbxjni.ffm.gen.MDBX_cache_entry.allocate(arena);
			if (entry != null) {
				writeCacheEntry(entrySeg, entry);
			}
			MemorySegment result = singleThreaded
					? mdbx_h.mdbx_cache_get_SingleThreaded(arena, ptr(txn), (int) dbi, keyVal, dataVal, entrySeg)
					: mdbx_h.mdbx_cache_get(arena, ptr(txn), (int) dbi, keyVal, dataVal, entrySeg);
			readVal(keyVal, key);
			readVal(dataVal, data);
			if (entry != null) {
				readCacheEntry(entrySeg, entry);
			}
			MDBX_cache_result out = new MDBX_cache_result();
			out.errcode = com.castortech.mdbxjni.ffm.gen.MDBX_cache_result.errcode(result);
			out.status = com.castortech.mdbxjni.ffm.gen.MDBX_cache_result.status(result);
			return out;
		}
	}

	private static void writeCacheEntry(MemorySegment segment, MDBX_cache_entry entry) {
		com.castortech.mdbxjni.ffm.gen.MDBX_cache_entry.trunk_txnid(segment, entry.trunk_txnid);
		com.castortech.mdbxjni.ffm.gen.MDBX_cache_entry.last_confirmed_txnid(segment, entry.last_confirmed_txnid);
		com.castortech.mdbxjni.ffm.gen.MDBX_cache_entry.offset(segment, entry.offset);
		com.castortech.mdbxjni.ffm.gen.MDBX_cache_entry.length(segment, entry.length);
	}

	private static void readCacheEntry(MemorySegment segment, MDBX_cache_entry entry) {
		entry.trunk_txnid = com.castortech.mdbxjni.ffm.gen.MDBX_cache_entry.trunk_txnid(segment);
		entry.last_confirmed_txnid = com.castortech.mdbxjni.ffm.gen.MDBX_cache_entry.last_confirmed_txnid(segment);
		entry.offset = com.castortech.mdbxjni.ffm.gen.MDBX_cache_entry.offset(segment);
		entry.length = com.castortech.mdbxjni.ffm.gen.MDBX_cache_entry.length(segment);
	}

	private static void copyGcInfo(MemorySegment segment, MDBX_gc_info info) {
		info.pages_total = com.castortech.mdbxjni.ffm.gen.MDBX_gc_info.pages_total(segment);
		info.pages_backed = com.castortech.mdbxjni.ffm.gen.MDBX_gc_info.pages_backed(segment);
		info.pages_allocated = com.castortech.mdbxjni.ffm.gen.MDBX_gc_info.pages_allocated(segment);
		info.pages_gc = com.castortech.mdbxjni.ffm.gen.MDBX_gc_info.pages_gc(segment);
		MemorySegment reclaim = segment.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_gc_info.gc_reclaimable$offset(),
				com.castortech.mdbxjni.ffm.gen.MDBX_gc_info.gc_reclaimable.sizeof());
		info.gc_reclaimable_pages = com.castortech.mdbxjni.ffm.gen.MDBX_gc_info.gc_reclaimable.pages(reclaim);
		if (info.gc_reclaimable_span_histogram == null) {
			info.gc_reclaimable_span_histogram = new MDBX_chk_histogram();
		}
		if (info.gc_reclaimable_pgno_distribution == null) {
			info.gc_reclaimable_pgno_distribution = new MDBX_chk_histogram();
		}
		copyHistogram(reclaim.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_gc_info.gc_reclaimable.span_histogram$offset(),
				com.castortech.mdbxjni.ffm.gen.MDBX_chk_histogram.sizeof()), info.gc_reclaimable_span_histogram);
		copyHistogram(reclaim.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_gc_info.gc_reclaimable.pgno_distribution$offset(),
				com.castortech.mdbxjni.ffm.gen.MDBX_chk_histogram.sizeof()), info.gc_reclaimable_pgno_distribution);
		info.max_reader_lag = com.castortech.mdbxjni.ffm.gen.MDBX_gc_info.max_reader_lag(segment);
		info.max_retained_pages = com.castortech.mdbxjni.ffm.gen.MDBX_gc_info.max_retained_pages(segment);
	}

	private static void copyHistogram(MemorySegment histogram, MDBX_chk_histogram out) {
		out.amount = com.castortech.mdbxjni.ffm.gen.MDBX_chk_histogram.amount(histogram);
		out.count = com.castortech.mdbxjni.ffm.gen.MDBX_chk_histogram.count(histogram);
		out.le1_amount = com.castortech.mdbxjni.ffm.gen.MDBX_chk_histogram.le1_amount(histogram);
		out.le1_count = com.castortech.mdbxjni.ffm.gen.MDBX_chk_histogram.le1_count(histogram);
		long stride = com.castortech.mdbxjni.ffm.gen.MDBX_chk_histogram.ranges.sizeof();
		MemorySegment ranges = histogram.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_chk_histogram.ranges$offset(), stride * 9);
		out.range_begin = sized(out.range_begin);
		out.range_end = sized(out.range_end);
		out.range_amount = sized(out.range_amount);
		out.range_count = sized(out.range_count);
		for (int i = 0; i < 9; i++) {
			MemorySegment range = ranges.asSlice(i * stride, stride);
			out.range_begin[i] = com.castortech.mdbxjni.ffm.gen.MDBX_chk_histogram.ranges.begin(range);
			out.range_end[i] = com.castortech.mdbxjni.ffm.gen.MDBX_chk_histogram.ranges.end(range);
			out.range_amount[i] = com.castortech.mdbxjni.ffm.gen.MDBX_chk_histogram.ranges.amount(range);
			out.range_count[i] = com.castortech.mdbxjni.ffm.gen.MDBX_chk_histogram.ranges.count(range);
		}
	}

	private static long[] sized(long[] values) {
		return values != null && values.length >= 9 ? values : new long[9];
	}

	private static void copyDefrag(MemorySegment segment, MDBX_defrag_result result) {
		result.pages_shrinked = com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.pages_shrinked(segment);
		result.pages_moved = com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.pages_moved(segment);
		result.pages_scheduled = com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.pages_scheduled(segment);
		result.pages_retained = com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.pages_retained(segment);
		result.pages_left = com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.pages_left(segment);
		result.pages_whole = com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.pages_whole(segment);
		result.obstructed_pgno = com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.obstructed_pgno(segment);
		result.obstructed_span = com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.obstructed_span(segment);
		result.obstructed_txnid = com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.obstructed_txnid(segment);
		result.obstructor_tid = Integer.toUnsignedLong(com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.obstructor_tid(segment));
		result.obstructor_pid = Integer.toUnsignedLong(com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.obstructor_pid(segment));
		result.rough_estimation_cycle_progress_permille = Integer.toUnsignedLong(
				com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.rough_estimation_cycle_progress_permille(segment));
		result.cycles = Integer.toUnsignedLong(com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.cycles(segment));
		result.stopping_reasons = Integer.toUnsignedLong(com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.stopping_reasons(segment));
		result.spent_time_dot16 = com.castortech.mdbxjni.ffm.gen.MDBX_defrag_result.spent_time_dot16(segment);
	}

	private static MemorySegment incomingPointer(Arena arena, long[] slot) {
		MemorySegment out = Mdbx.outPointer(arena);
		long incoming = slot != null && slot.length > 0 ? slot[0] : 0;
		out.set(ValueLayout.ADDRESS, 0, ptr(incoming));
		return out;
	}

	private static long bounded(long requested, long available) {
		if (requested <= 0 || requested > available) {
			return available;
		}
		return requested;
	}

	private static void copyStat(MemorySegment segment, MDBX_stat stat) {
		if (stat == null) {
			return;
		}
		stat.ms_psize = Integer.toUnsignedLong(com.castortech.mdbxjni.ffm.gen.MDBX_stat.ms_psize(segment));
		stat.ms_depth = Integer.toUnsignedLong(com.castortech.mdbxjni.ffm.gen.MDBX_stat.ms_depth(segment));
		stat.ms_branch_pages = com.castortech.mdbxjni.ffm.gen.MDBX_stat.ms_branch_pages(segment);
		stat.ms_leaf_pages = com.castortech.mdbxjni.ffm.gen.MDBX_stat.ms_leaf_pages(segment);
		stat.ms_overflow_pages = com.castortech.mdbxjni.ffm.gen.MDBX_stat.ms_overflow_pages(segment);
		stat.ms_entries = com.castortech.mdbxjni.ffm.gen.MDBX_stat.ms_entries(segment);
		stat.ms_mod_txnid = com.castortech.mdbxjni.ffm.gen.MDBX_stat.ms_mod_txnid(segment);
	}

	private static void copyTxnInfo(MemorySegment segment, MDBX_txn_info info) {
		info.txn_id = com.castortech.mdbxjni.ffm.gen.MDBX_txn_info.txn_id(segment);
		info.txn_reader_lag = com.castortech.mdbxjni.ffm.gen.MDBX_txn_info.txn_reader_lag(segment);
		info.txn_space_used = com.castortech.mdbxjni.ffm.gen.MDBX_txn_info.txn_space_used(segment);
		info.txn_space_limit_soft = com.castortech.mdbxjni.ffm.gen.MDBX_txn_info.txn_space_limit_soft(segment);
		info.txn_space_limit_hard = com.castortech.mdbxjni.ffm.gen.MDBX_txn_info.txn_space_limit_hard(segment);
		info.txn_space_retired = com.castortech.mdbxjni.ffm.gen.MDBX_txn_info.txn_space_retired(segment);
		info.txn_space_leftover = com.castortech.mdbxjni.ffm.gen.MDBX_txn_info.txn_space_leftover(segment);
		info.txn_space_dirty = com.castortech.mdbxjni.ffm.gen.MDBX_txn_info.txn_space_dirty(segment);
		info.txn_pget = com.castortech.mdbxjni.ffm.gen.MDBX_txn_info.txn_pget(segment);
	}

	private static void copyLatency(MemorySegment segment, MDBX_commit_latency latency) {
		latency.preparation = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.preparation(segment);
		latency.gc_wallclock = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_wallclock(segment);
		latency.audit = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.audit(segment);
		latency.write = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.write(segment);
		latency.sync = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.sync(segment);
		latency.ending = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.ending(segment);
		latency.whole = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.whole(segment);
		latency.gc_cputime = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_cputime(segment);
		MemorySegment prof = segment.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof$offset(),
				com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.sizeof());
		latency.gc_prof_wloops = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.wloops(prof);
		latency.gc_prof_coalescences = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.coalescences(prof);
		latency.gc_prof_wipes = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.wipes(prof);
		latency.gc_prof_flushes = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.flushes(prof);
		latency.gc_prof_kicks = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.kicks(prof);
		latency.gc_prof_work_counter = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.work_counter(prof);
		latency.gc_prof_work_rtime_monotonic = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.work_rtime_monotonic(prof);
		latency.gc_prof_work_xtime_cpu = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.work_xtime_cpu(prof);
		latency.gc_prof_work_rsteps = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.work_rsteps(prof);
		latency.gc_prof_work_xpages = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.work_xpages(prof);
		latency.gc_prof_work_majflt = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.work_majflt(prof);
		latency.gc_prof_self_counter = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.self_counter(prof);
		latency.gc_prof_self_rtime_monotonic = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.self_rtime_monotonic(prof);
		latency.gc_prof_self_xtime_cpu = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.self_xtime_cpu(prof);
		latency.gc_prof_self_rsteps = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.self_rsteps(prof);
		latency.gc_prof_self_xpages = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.self_xpages(prof);
		latency.gc_prof_self_majflt = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.self_majflt(prof);
		MemorySegment work = prof.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.pnl_merge_work$offset(),
				com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.pnl_merge_work.layout().byteSize());
		MemorySegment self = prof.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.pnl_merge_self$offset(),
				com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.pnl_merge_work.layout().byteSize());
		latency.gc_prof_pnl_merge_work_time = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.pnl_merge_work.time(work);
		latency.gc_prof_pnl_merge_work_volume = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.pnl_merge_work.volume(work);
		latency.gc_prof_pnl_merge_work_calls = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.pnl_merge_work.calls(work);
		latency.gc_prof_pnl_merge_self_time = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.pnl_merge_work.time(self);
		latency.gc_prof_pnl_merge_self_volume = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.pnl_merge_work.volume(self);
		latency.gc_prof_pnl_merge_self_calls = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.pnl_merge_work.calls(self);
		latency.gc_prof_max_reader_lag = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.max_reader_lag(prof);
		latency.gc_prof_max_retained_pages = com.castortech.mdbxjni.ffm.gen.MDBX_commit_latency.gc_prof.max_retained_pages(prof);
	}

	private static void copyEnvInfo(MemorySegment segment, MDBX_envinfo info) {
		MemorySegment geo = segment.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_geo$offset(),
				com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_geo.sizeof());
		info.mi_geo_lower = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_geo.lower(geo);
		info.mi_geo_upper = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_geo.upper(geo);
		info.mi_geo_current = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_geo.current(geo);
		info.mi_geo_shrink = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_geo.shrink(geo);
		info.mi_geo_grow = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_geo.grow(geo);
		info.mi_mapsize = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_mapsize(segment);
		info.mi_dxb_fsize = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_dxb_fsize(segment);
		info.mi_dxb_fallocated = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_dxb_fallocated(segment);
		info.mi_last_pgno = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_last_pgno(segment);
		info.mi_recent_txnid = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_recent_txnid(segment);
		info.mi_latter_reader_txnid = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_latter_reader_txnid(segment);
		info.mi_self_latter_reader_txnid = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_self_latter_reader_txnid(segment);
		MemorySegment txnid = segment.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_meta_txnid$offset(), 24);
		info.mi_meta0_txnid = txnid.get(ValueLayout.JAVA_LONG, 0);
		info.mi_meta1_txnid = txnid.get(ValueLayout.JAVA_LONG, 8);
		info.mi_meta2_txnid = txnid.get(ValueLayout.JAVA_LONG, 16);
		MemorySegment sign = segment.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_meta_sign$offset(), 24);
		info.mi_meta0_sign = sign.get(ValueLayout.JAVA_LONG, 0);
		info.mi_meta1_sign = sign.get(ValueLayout.JAVA_LONG, 8);
		info.mi_meta2_sign = sign.get(ValueLayout.JAVA_LONG, 16);
		info.mi_maxreaders = Integer.toUnsignedLong(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_maxreaders(segment));
		info.mi_numreaders = Integer.toUnsignedLong(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_numreaders(segment));
		info.mi_dxb_pagesize = Integer.toUnsignedLong(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_dxb_pagesize(segment));
		info.mi_sys_pagesize = Integer.toUnsignedLong(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_sys_pagesize(segment));
		info.mi_sys_upcblk = Integer.toUnsignedLong(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_sys_upcblk(segment));
		info.mi_sys_ioblk = Integer.toUnsignedLong(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_sys_ioblk(segment));
		MemorySegment boot = segment.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_bootid$offset(),
				com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_bootid.sizeof());
		MemorySegment current = boot.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_bootid.current$offset(),
				com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_bootid.current.sizeof());
		info.mi_bootid_current_x = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_bootid.current.x(current);
		info.mi_bootid_current_y = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_bootid.current.y(current);
		MemorySegment meta = boot.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_bootid.meta$offset(),
				com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_bootid.current.sizeof() * 3);
		info.mi_bootid_meta0_x = meta.get(ValueLayout.JAVA_LONG, 0);
		info.mi_bootid_meta0_y = meta.get(ValueLayout.JAVA_LONG, 8);
		info.mi_bootid_meta1_x = meta.get(ValueLayout.JAVA_LONG, 16);
		info.mi_bootid_meta1_y = meta.get(ValueLayout.JAVA_LONG, 24);
		info.mi_bootid_meta2_x = meta.get(ValueLayout.JAVA_LONG, 32);
		info.mi_bootid_meta2_y = meta.get(ValueLayout.JAVA_LONG, 40);
		info.mi_unsync_volume = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_unsync_volume(segment);
		info.mi_autosync_threshold = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_autosync_threshold(segment);
		info.mi_since_sync_seconds16dot16 = Integer.toUnsignedLong(
				com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_since_sync_seconds16dot16(segment));
		info.mi_autosync_period_seconds16dot16 = Integer.toUnsignedLong(
				com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_autosync_period_seconds16dot16(segment));
		info.mi_since_reader_check_seconds16dot16 = Integer.toUnsignedLong(
				com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_since_reader_check_seconds16dot16(segment));
		info.mi_mode = Integer.toUnsignedLong(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_mode(segment));
		MemorySegment pgop = segment.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_pgop_stat$offset(),
				com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_pgop_stat.sizeof());
		info.mi_pgop_stat_newly = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_pgop_stat.newly(pgop);
		info.mi_pgop_stat_cow = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_pgop_stat.cow(pgop);
		info.mi_pgop_stat_clone = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_pgop_stat.clone(pgop);
		info.mi_pgop_stat_split = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_pgop_stat.split(pgop);
		info.mi_pgop_stat_merge = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_pgop_stat.merge(pgop);
		info.mi_pgop_stat_spill = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_pgop_stat.spill(pgop);
		info.mi_pgop_stat_unspill = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_pgop_stat.unspill(pgop);
		info.mi_pgop_stat_wops = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_pgop_stat.wops(pgop);
		info.mi_pgop_stat_prefault = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_pgop_stat.prefault(pgop);
		info.mi_pgop_stat_mincore = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_pgop_stat.mincore(pgop);
		info.mi_pgop_stat_msync = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_pgop_stat.msync(pgop);
		info.mi_pgop_stat_fsync = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_pgop_stat.fsync(pgop);
		MemorySegment dxbid = segment.asSlice(com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_dxbid$offset(),
				com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_dxbid.sizeof());
		info.mi_dxbid_x = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_dxbid.x(dxbid);
		info.mi_dxbid_y = com.castortech.mdbxjni.ffm.gen.MDBX_envinfo.mi_dxbid.y(dxbid);
	}

	private static int readOption(long env, int option, long[] value) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outLong(arena);
			int rc = mdbx_h.mdbx_env_get_option(ptr(env), option, slot);
			writeLong(value, slot);
			return rc;
		}
	}

	private static MemorySegment val(Arena arena, MDBX_val value) {
		if (value == null) {
			return MemorySegment.NULL;
		}
		MemorySegment segment = iovec.allocate(arena);
		writeVal(segment, 0, value);
		return segment;
	}

	private static void writeVal(MemorySegment segment, long offset, MDBX_val value) {
		long base = value == null ? 0 : value.iov_base;
		long length = value == null ? 0 : value.iov_len;
		segment.set(ValueLayout.ADDRESS, offset + iovec.iov_base$offset(), MemorySegment.ofAddress(base));
		segment.set(ValueLayout.JAVA_LONG, offset + iovec.iov_len$offset(), length);
	}

	private static void readVal(MemorySegment segment, MDBX_val value) {
		if (value == null || segment == null || segment.address() == 0) {
			return;
		}
		value.iov_base = segment.get(ValueLayout.ADDRESS, iovec.iov_base$offset()).address();
		value.iov_len = segment.get(ValueLayout.JAVA_LONG, iovec.iov_len$offset());
	}

	private static MemorySegment global(String name, long bytes) {
		MdbxLibrary.load();
		MemorySegment symbol = SymbolLookup.loaderLookup().find(name)
				.orElseThrow(() -> new IllegalStateException("libmdbx symbol was not found: " + name));
		return symbol.reinterpret(bytes);
	}

	private static char[] chars(MemorySegment pointer) {
		if (pointer == null || pointer.address() == 0) {
			return new char[0];
		}
		String text = MemorySegment.ofAddress(pointer.address()).reinterpret(Integer.MAX_VALUE)
				.getString(0, StandardCharsets.UTF_8);
		return text.toCharArray();
	}

	private static MemorySegment ptr(long address) {
		return address == 0 ? MemorySegment.NULL : MemorySegment.ofAddress(address);
	}

	private static MemorySegment text(Arena arena, String value) {
		return value == null ? MemorySegment.NULL : Mdbx.utf8(arena, value);
	}

	private static MemorySegment wide(Arena arena, String value) {
		return value == null ? MemorySegment.NULL : Mdbx.wide(arena, value);
	}

	private static void writePointer(long[] slot, MemorySegment out) {
		if (slot != null && slot.length > 0) {
			slot[0] = Mdbx.pointer(out).address();
		}
	}

	private static void writeUnsigned(long[] slot, MemorySegment out) {
		if (slot != null && slot.length > 0) {
			slot[0] = Integer.toUnsignedLong(Mdbx.readInt(out));
		}
	}

	private static void writeLong(long[] slot, MemorySegment out) {
		if (slot != null && slot.length > 0) {
			slot[0] = Mdbx.readLong(out);
		}
	}

	private static MethodHandle downcall(String name, FunctionDescriptor descriptor) {
		MemorySegment symbol = Linker.nativeLinker().defaultLookup().find(name)
				.orElseThrow(() -> new IllegalStateException("C runtime symbol was not found: " + name));
		return Linker.nativeLinker().downcallHandle(symbol, descriptor);
	}

	private static MethodHandle errnoHandle() {
		SymbolLookup lookup = Linker.nativeLinker().defaultLookup();
		MemorySegment symbol = lookup.find("_errno").or(() -> lookup.find("__errno_location")).orElse(null);
		if (symbol == null) {
			return null;
		}
		return Linker.nativeLinker().downcallHandle(symbol, FunctionDescriptor.of(ValueLayout.ADDRESS));
	}
}
