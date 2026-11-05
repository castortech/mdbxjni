package com.castortech.mdbxjni.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * Transaction opened through the FFM binding.
 */
public final class FfmTransaction implements AutoCloseable {
	private MemorySegment txn;
	private boolean finished;

	FfmTransaction(MemorySegment txn) {
		this.txn = txn;
	}

	public FfmDatabase openDatabase(String name, int flags) {
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment dbName = name == null ? MemorySegment.NULL : Mdbx.utf8(arena, name);
			MemorySegment slot = Mdbx.outInt(arena);
			Mdbx.check(mdbx_h.mdbx_dbi_open(txn, dbName, flags, slot));
			return new FfmDatabase(this, Mdbx.readInt(slot));
		}
	}

	public void commit() {
		Mdbx.check(mdbx_h.mdbx_txn_commit_ex(txn, MemorySegment.NULL));
		finished = true;
		txn = MemorySegment.NULL;
	}

	public void abort() {
		if (finished || txn == null || txn.address() == 0) {
			return;
		}
		Mdbx.check(mdbx_h.mdbx_txn_abort_ex(txn, MemorySegment.NULL));
		finished = true;
		txn = MemorySegment.NULL;
	}

	@Override
	public void close() {
		abort();
	}

	MemorySegment segment() {
		return txn;
	}
}
