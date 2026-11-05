package com.castortech.mdbxjni.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.file.Path;

import com.castortech.mdbxjni.ffm.gen.MDBX_envinfo;
import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * Environment opened through the FFM binding. The public {@code Env} class uses the same surface.
 */
public final class FfmEnv implements AutoCloseable {
	private MemorySegment env;

	private FfmEnv(MemorySegment env) {
		this.env = env;
	}

	public static int nosubdir() {
		return mdbx_h.MDBX_NOSUBDIR();
	}

	public static int createDatabase() {
		return mdbx_h.MDBX_CREATE();
	}

	public static FfmEnv create() {
		MdbxLibrary.load();
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outPointer(arena);
			Mdbx.check(mdbx_h.mdbx_env_create(slot));
			return new FfmEnv(Mdbx.pointer(slot));
		}
	}

	public void setGeometry(long lower, long now, long upper, long growthStep, long shrinkThreshold, long pageSize) {
		Mdbx.check(mdbx_h.mdbx_env_set_geometry(env, lower, now, upper, growthStep, shrinkThreshold, pageSize));
	}

	/** {@code mdbx_env_set_maxdbs} is inline in 0.14 and calls {@code mdbx_env_set_option}. */
	public void setMaxDbs(int count) {
		Mdbx.check(mdbx_h.mdbx_env_set_option(env, mdbx_h.MDBX_opt_max_db(), count));
	}

	public long getMaxDbs() {
		return option(mdbx_h.MDBX_opt_max_db());
	}

	/** {@code mdbx_env_set_maxreaders} is inline in 0.14 and calls {@code mdbx_env_set_option}. */
	public void setMaxReaders(int count) {
		Mdbx.check(mdbx_h.mdbx_env_set_option(env, mdbx_h.MDBX_opt_max_readers(), count));
	}

	public long getMaxReaders() {
		return option(mdbx_h.MDBX_opt_max_readers());
	}

	public void open(Path path, int flags) {
		try (Arena arena = Arena.ofConfined()) {
			int rc = windows()
					? mdbx_h.mdbx_env_openW(env, Mdbx.wide(arena, path.toString()), flags, (short)0644)
					: mdbx_h.mdbx_env_open(env, Mdbx.utf8(arena, path.toString()), flags, (short)0644);
			Mdbx.check(rc);
		}
	}

	public int getFlags() {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outInt(arena);
			Mdbx.check(mdbx_h.mdbx_env_get_flags(env, slot));
			return Mdbx.readInt(slot);
		}
	}

	public void setFlags(int flags, boolean on) {
		Mdbx.check(mdbx_h.mdbx_env_set_flags(env, flags, on));
	}

	public int getMaxKeySize() {
		return mdbx_h.mdbx_env_get_maxkeysize(env);
	}

	public int getMaxKeySize(int dbFlags) {
		return mdbx_h.mdbx_env_get_maxkeysize_ex(env, dbFlags);
	}

	public int getMaxValueSize(int dbFlags) {
		return mdbx_h.mdbx_env_get_maxvalsize_ex(env, dbFlags);
	}

	public void sync() {
		Mdbx.check(mdbx_h.mdbx_env_sync_ex(env, true, false));
	}

	public void copyTo(Path dest) {
		try (Arena arena = Arena.ofConfined()) {
			Mdbx.check(mdbx_h.mdbx_env_copy(env, Mdbx.utf8(arena, dest.toString()), mdbx_h.MDBX_CP_DEFAULTS()));
		}
	}

	public long mapSize() {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment info = MDBX_envinfo.allocate(arena);
			Mdbx.check(mdbx_h.mdbx_env_info_ex(env, MemorySegment.NULL, info, MDBX_envinfo.sizeof()));
			return MDBX_envinfo.mi_geo.current(MDBX_envinfo.mi_geo(info));
		}
	}

	public FfmTransaction begin() {
		return begin(0);
	}

	public FfmTransaction begin(int flags) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outPointer(arena);
			Mdbx.check(mdbx_h.mdbx_txn_begin_ex(env, MemorySegment.NULL, flags, slot, MemorySegment.NULL));
			return new FfmTransaction(Mdbx.pointer(slot));
		}
	}

	@Override
	public void close() {
		if (env == null || env.address() == 0) {
			return;
		}
		Mdbx.check(mdbx_h.mdbx_env_close_ex(env, false));
		env = MemorySegment.NULL;
	}

	private long option(int option) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment slot = Mdbx.outLong(arena);
			Mdbx.check(mdbx_h.mdbx_env_get_option(env, option, slot));
			return Mdbx.readLong(slot);
		}
	}

	private static boolean windows() {
		return System.getProperty("os.name", "").toLowerCase().contains("win");
	}
}
