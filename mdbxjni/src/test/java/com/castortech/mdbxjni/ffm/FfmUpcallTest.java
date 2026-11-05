package com.castortech.mdbxjni.ffm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.castortech.mdbxjni.ffm.gen.iovec;
import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * Callbacks libmdbx invokes. Both comparators are bound to one Java instance.
 */
public class FfmUpcallTest {
	static {
		MdbxLibrary.load();
	}

	@Test
	public void comparators() throws Exception {
		Order order = new Order();
		Path dir = Files.createTempDirectory("mdbx-ffm-cmp");
		Path db = dir.resolve("cmp.mdbx");
		try (Arena arena = Arena.ofConfined()) {
			MethodHandle keyHandle = MethodHandles.lookup().findVirtual(Order.class, "compareKey",
					MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class)).bindTo(order);
			MethodHandle dataHandle = MethodHandles.lookup().findVirtual(Order.class, "compareData",
					MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class)).bindTo(order);
			FunctionDescriptor compare = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
			MemorySegment keyCmp = Linker.nativeLinker().upcallStub(keyHandle, compare, arena);
			MemorySegment dataCmp = Linker.nativeLinker().upcallStub(dataHandle, compare, arena);
			MemorySegment env = open(arena, db);
			MemorySegment txn = MemorySegment.NULL;
			MemorySegment cursor = MemorySegment.NULL;
			boolean committed = false;
			try {
				txn = begin(arena, env);
				MemorySegment dbiSlot = Mdbx.outInt(arena);
				int flags = mdbx_h.MDBX_CREATE() | mdbx_h.MDBX_DUPSORT();
				Mdbx.check(mdbx_h.mdbx_dbi_open_ex(txn, Mdbx.utf8(arena, "ordered"), flags, dbiSlot, keyCmp, dataCmp));
				int dbi = Mdbx.readInt(dbiSlot);
				put(arena, txn, dbi, "a", "1");
				put(arena, txn, dbi, "a", "2");
				put(arena, txn, dbi, "b", "1");
				put(arena, txn, dbi, "c", "1");

				MemorySegment pcursor = Mdbx.outPointer(arena);
				Mdbx.check(mdbx_h.mdbx_cursor_open(txn, dbi, pcursor));
				cursor = Mdbx.pointer(pcursor);
				assertEquals(List.of("c", "b", "a"), keys(arena, cursor));
				assertTrue(order.keyCalls > 0);
				assertTrue(order.dataCalls > 0);

				Mdbx.check(mdbx_h.mdbx_txn_commit_ex(txn, MemorySegment.NULL));
				committed = true;
			}
			finally {
				closeCursor(cursor);
				if (!committed && txn != null && txn.address() != 0) {
					mdbx_h.mdbx_txn_abort_ex(txn, MemorySegment.NULL);
				}
				Mdbx.check(mdbx_h.mdbx_env_close_ex(env, false));
			}
		}
		finally {
			Mdbx.deleteTree(dir);
		}
	}

	@Test
	public void logger() throws Exception {
		List<String> messages = new ArrayList<>();
		Path dir = Files.createTempDirectory("mdbx-ffm-log");
		Path db = dir.resolve("log.mdbx");
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment buffer = arena.allocate(512);
			MethodHandle logHandle = MethodHandles.lookup().findVirtual(LogSink.class, "accept",
					MethodType.methodType(void.class, int.class, MemorySegment.class, int.class, MemorySegment.class, int.class))
					.bindTo(new LogSink(messages));
			FunctionDescriptor logDesc = FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
					ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT);
			MemorySegment logger = Linker.nativeLinker().upcallStub(logHandle, logDesc, arena);
			int installed = mdbx_h.mdbx_setup_debug_nofmt(mdbx_h.MDBX_LOG_NOTICE(), mdbx_h.MDBX_DBG_DONTCHANGE(),
					logger, buffer, buffer.byteSize());
			assertTrue(installed >= 0);
			try {
				MemorySegment env = open(arena, db);
				Mdbx.check(mdbx_h.mdbx_env_close_ex(env, false));
				assertFalse(messages.isEmpty());
			}
			finally {
				mdbx_h.mdbx_setup_debug_nofmt(mdbx_h.MDBX_LOG_NOTICE(), mdbx_h.MDBX_DBG_DONTCHANGE(),
						MemorySegment.NULL, MemorySegment.NULL, 0);
			}
		}
		finally {
			Mdbx.deleteTree(dir);
		}
	}

	@Test
	public void readerList() throws Exception {
		List<Integer> pids = new ArrayList<>();
		Path dir = Files.createTempDirectory("mdbx-ffm-readers");
		Path db = dir.resolve("readers.mdbx");
		try (Arena arena = Arena.ofConfined()) {
			MethodHandle readerHandle = MethodHandles.lookup().findVirtual(ReaderSink.class, "accept",
					MethodType.methodType(int.class, MemorySegment.class, int.class, int.class, int.class, int.class,
							long.class, long.class, long.class, long.class))
					.bindTo(new ReaderSink(pids));
			FunctionDescriptor readerDesc = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
					ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
					ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG);
			MemorySegment func = Linker.nativeLinker().upcallStub(readerHandle, readerDesc, arena);
			MemorySegment env = open(arena, db);
			MemorySegment txn = MemorySegment.NULL;
			boolean committed = false;
			try {
				MemorySegment ptxn = Mdbx.outPointer(arena);
				Mdbx.check(mdbx_h.mdbx_txn_begin_ex(env, MemorySegment.NULL, mdbx_h.MDBX_TXN_RDONLY(), ptxn,
						MemorySegment.NULL));
				txn = Mdbx.pointer(ptxn);
				int rc = mdbx_h.mdbx_reader_list(env, func, MemorySegment.NULL);
				assertTrue(rc == mdbx_h.MDBX_SUCCESS() || rc == mdbx_h.MDBX_RESULT_TRUE());
				int self = (int)ProcessHandle.current().pid();
				assertTrue(pids.contains(self));
				Mdbx.check(mdbx_h.mdbx_txn_commit_ex(txn, MemorySegment.NULL));
				committed = true;
			}
			finally {
				if (!committed && txn != null && txn.address() != 0) {
					mdbx_h.mdbx_txn_abort_ex(txn, MemorySegment.NULL);
				}
				Mdbx.check(mdbx_h.mdbx_env_close_ex(env, false));
			}
		}
		finally {
			Mdbx.deleteTree(dir);
		}
	}

	@Test
	public void cursorScan() throws Exception {
		Path dir = Files.createTempDirectory("mdbx-ffm-scan");
		Path db = dir.resolve("scan.mdbx");
		try (Arena arena = Arena.ofConfined()) {
			AtomicInteger seen = new AtomicInteger();
			MethodHandle predicateHandle = MethodHandles.lookup().findVirtual(Scan.class, "accept",
					MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class, MemorySegment.class,
							MemorySegment.class))
					.bindTo(new Scan(seen));
			FunctionDescriptor predicateDesc = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
					ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
			MemorySegment predicate = Linker.nativeLinker().upcallStub(predicateHandle, predicateDesc, arena);
			MemorySegment env = open(arena, db);
			MemorySegment txn = MemorySegment.NULL;
			MemorySegment cursor = MemorySegment.NULL;
			boolean committed = false;
			try {
				txn = begin(arena, env);
				int dbi = openMain(arena, txn);
				put(arena, txn, dbi, "a", "1");
				put(arena, txn, dbi, "b", "2");
				put(arena, txn, dbi, "c", "3");
				MemorySegment pcursor = Mdbx.outPointer(arena);
				Mdbx.check(mdbx_h.mdbx_cursor_open(txn, dbi, pcursor));
				cursor = Mdbx.pointer(pcursor);
				int rc = mdbx_h.mdbx_cursor_scan(cursor, predicate, MemorySegment.NULL, mdbx_h.MDBX_FIRST(),
						mdbx_h.MDBX_NEXT(), MemorySegment.NULL);
				assertEquals(mdbx_h.MDBX_RESULT_TRUE(), rc);
				assertTrue(seen.get() >= 2);
				Mdbx.check(mdbx_h.mdbx_txn_commit_ex(txn, MemorySegment.NULL));
				committed = true;
			}
			finally {
				closeCursor(cursor);
				if (!committed && txn != null && txn.address() != 0) {
					mdbx_h.mdbx_txn_abort_ex(txn, MemorySegment.NULL);
				}
				Mdbx.check(mdbx_h.mdbx_env_close_ex(env, false));
			}
		}
		finally {
			Mdbx.deleteTree(dir);
		}
	}

	@Test
	public void slowReaderCallback() throws Exception {
		Path dir = Files.createTempDirectory("mdbx-ffm-hsr");
		Path db = dir.resolve("hsr.mdbx");
		try (Arena arena = Arena.ofConfined()) {
			MethodHandle hsrHandle = MethodHandles.lookup().findStatic(FfmUpcallTest.class, "ignoreSlowReader",
					MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class, int.class, int.class,
							long.class, int.class, long.class, int.class));
			FunctionDescriptor hsrDesc = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
					ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG,
					ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT);
			MemorySegment stub = Linker.nativeLinker().upcallStub(hsrHandle, hsrDesc, arena);
			MemorySegment env = open(arena, db);
			try {
				Mdbx.check(mdbx_h.mdbx_env_set_hsr(env, stub));
				MemorySegment installed = mdbx_h.mdbx_env_get_hsr(env);
				assertEquals(stub.address(), installed.address());
			}
			finally {
				Mdbx.check(mdbx_h.mdbx_env_close_ex(env, false));
			}
		}
		finally {
			Mdbx.deleteTree(dir);
		}
	}

	private static List<String> keys(Arena arena, MemorySegment cursor) {
		List<String> keys = new ArrayList<>();
		MemorySegment key = Mdbx.emptyVal(arena);
		MemorySegment data = Mdbx.emptyVal(arena);
		int rc = mdbx_h.mdbx_cursor_get(cursor, key, data, mdbx_h.MDBX_FIRST());
		while (!Mdbx.isNotFound(rc)) {
			Mdbx.check(rc);
			keys.add(new String(Mdbx.bytes(key), StandardCharsets.UTF_8));
			rc = mdbx_h.mdbx_cursor_get(cursor, key, data, mdbx_h.MDBX_NEXT_NODUP());
		}
		return keys;
	}

	private static MemorySegment struct(MemorySegment pointer) {
		return MemorySegment.ofAddress(pointer.address()).reinterpret(iovec.sizeof());
	}

	private static MemorySegment open(Arena arena, Path db) {
		MemorySegment slot = Mdbx.outPointer(arena);
		Mdbx.check(mdbx_h.mdbx_env_create(slot));
		MemorySegment env = Mdbx.pointer(slot);
		Mdbx.check(mdbx_h.mdbx_env_set_option(env, mdbx_h.MDBX_opt_max_db(), 4));
		Mdbx.check(mdbx_h.mdbx_env_open(env, Mdbx.utf8(arena, db.toString()), mdbx_h.MDBX_NOSUBDIR(), (short)0644));
		return env;
	}

	private static MemorySegment begin(Arena arena, MemorySegment env) {
		MemorySegment slot = Mdbx.outPointer(arena);
		Mdbx.check(mdbx_h.mdbx_txn_begin_ex(env, MemorySegment.NULL, 0, slot, MemorySegment.NULL));
		return Mdbx.pointer(slot);
	}

	private static int openMain(Arena arena, MemorySegment txn) {
		MemorySegment slot = Mdbx.outInt(arena);
		Mdbx.check(mdbx_h.mdbx_dbi_open(txn, MemorySegment.NULL, mdbx_h.MDBX_DB_DEFAULTS(), slot));
		return Mdbx.readInt(slot);
	}

	private static void put(Arena arena, MemorySegment txn, int dbi, String key, String value) {
		Mdbx.check(mdbx_h.mdbx_put(txn, dbi, Mdbx.val(arena, key.getBytes(StandardCharsets.UTF_8)),
				Mdbx.val(arena, value.getBytes(StandardCharsets.UTF_8)), mdbx_h.MDBX_UPSERT()));
	}

	private static void closeCursor(MemorySegment cursor) {
		if (cursor != null && cursor.address() != 0) {
			mdbx_h.mdbx_cursor_close(cursor);
		}
	}

	private static int ignoreSlowReader(MemorySegment env, MemorySegment txn, int pid, int tid, long laggard, int gap,
			long space, int retry) {
		return 0;
	}

	public static final class LogSink {
		private final List<String> messages;

		private LogSink(List<String> messages) {
			this.messages = messages;
		}

		public void accept(int level, MemorySegment function, int line, MemorySegment msg, int length) {
			try {
				if (msg == null || msg.address() == 0 || length <= 0) {
					return;
				}
				byte[] raw = MemorySegment.ofAddress(msg.address()).reinterpret(length).toArray(ValueLayout.JAVA_BYTE);
				String text = new String(raw, StandardCharsets.UTF_8).trim();
				if (!text.isEmpty()) {
					messages.add(text);
				}
			}
			catch (RuntimeException ignored) {
				// An exception leaving an upcall aborts the JVM.
			}
		}
	}

	public static final class ReaderSink {
		private final List<Integer> pids;

		private ReaderSink(List<Integer> pids) {
			this.pids = pids;
		}

		public int accept(MemorySegment ctx, int num, int slot, int pid, int thread, long txnid, long lag,
				long bytesUsed, long bytesRetained) {
			pids.add(pid);
			return 0;
		}
	}

	public static final class Scan {
		private final AtomicInteger seen;

		private Scan(AtomicInteger seen) {
			this.seen = seen;
		}

		public int accept(MemorySegment context, MemorySegment key, MemorySegment value, MemorySegment arg) {
			try {
				seen.incrementAndGet();
				byte[] bytes = Mdbx.bytes(struct(key));
				if (Arrays.equals(bytes, "b".getBytes(StandardCharsets.UTF_8))) {
					return mdbx_h.MDBX_RESULT_TRUE();
				}
			}
			catch (RuntimeException ignored) {
				return mdbx_h.MDBX_RESULT_TRUE();
			}
			return mdbx_h.MDBX_RESULT_FALSE();
		}
	}

	public static final class Order {
		private int keyCalls;
		private int dataCalls;

		public int compareKey(MemorySegment left, MemorySegment right) {
			keyCalls++;
			return reversed(left, right);
		}

		public int compareData(MemorySegment left, MemorySegment right) {
			dataCalls++;
			return reversed(left, right);
		}

		private static int reversed(MemorySegment left, MemorySegment right) {
			try {
				return -Arrays.compareUnsigned(Mdbx.bytes(struct(left)), Mdbx.bytes(struct(right)));
			}
			catch (RuntimeException failure) {
				return 0;
			}
		}
	}
}
