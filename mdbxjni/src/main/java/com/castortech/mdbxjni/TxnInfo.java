package com.castortech.mdbxjni;

public class TxnInfo {
	private final long id;
	private final long readerLag;
	private final long spaceUsed;
	private final long spaceLimitSoft;
	private final long spaceLimitHard;
	private final long spaceRetired;
	private final long spaceLeftover;
	private final long spaceDirty;
	private final long pageGets;

	TxnInfo(JNI.MDBX_txn_info rc) {
		id = rc.txn_id;
		readerLag = rc.txn_reader_lag;
		spaceUsed = rc.txn_space_used;
		spaceLimitSoft = rc.txn_space_limit_soft;
		spaceLimitHard = rc.txn_space_limit_hard;
		spaceRetired = rc.txn_space_retired;
		spaceLeftover = rc.txn_space_leftover;
		spaceDirty = rc.txn_space_dirty;
		pageGets = rc.txn_pget;
	}

	public long getId() {
		return id;
	}

	public long getReaderLag() {
		return readerLag;
	}

	public long getSpaceUsed() {
		return spaceUsed;
	}

	public long getSpaceLimitSoft() {
		return spaceLimitSoft;
	}

	public long getSpaceLimitHard() {
		return spaceLimitHard;
	}

	public long getSpaceRetired() {
		return spaceRetired;
	}

	public long getSpaceLeftover() {
		return spaceLeftover;
	}

	public long getSpaceDirty() {
		return spaceDirty;
	}

	/**
	 * @return Page-get count. Zero unless libmdbx was built with page-get statistics.
	 */
	public long getPageGets() {
		return pageGets;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "TxnInfo [" +
				"id=" + id +
				", readerLag=" + readerLag +
				", spaceUsed=" + spaceUsed +
				", spaceLimitSoft=" + spaceLimitSoft +
				", spaceLimitHard=" + spaceLimitHard +
				", spaceRetired=" + spaceRetired +
				", spaceLeftover=" + spaceLeftover +
				", spaceDirty=" + spaceDirty +
				", pageGets=" + pageGets +
				"]";
	}
}