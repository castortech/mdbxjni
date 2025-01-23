package com.castortech.mdbxjni;

import static com.castortech.mdbxjni.JNI.*;
import static com.castortech.mdbxjni.Util.checkErrorCode;

public class Global {

	public long getMinPageSize() {
		return mdbx_limits_pgsize_min();
	}

	public long getMaxPageSize() {
		return mdbx_limits_pgsize_max();
	}

	public long getMinDbSize(long pageSize) {
		return mdbx_limits_dbsize_min(pageSize);
	}

	public long getMaxDbSize(long pageSize) {
		return mdbx_limits_dbsize_max(pageSize);
	}

	public long getMaxTxnSize(long pageSize) {
		return mdbx_limits_txnsize_max(pageSize);
	}

	public long getMinKeySize(int flags) {
		return mdbx_limits_keysize_min(flags);
	}

	public long getMaxKeySize(long pageSize, int flags) {
		return mdbx_limits_keysize_max(pageSize, flags);
	}

	public long getMinValSize(int flags) {
		return mdbx_limits_valsize_min(flags);
	}

	public long getMaxValSize(long pageSize, int flags) {
		return mdbx_limits_valsize_max(pageSize, flags);
	}

	public RamInfo getSysRamInfo() {
		long[] pgSz = new long[1];
		long[] totPg = new long[1];
		long[] avPg = new long[1];
		checkErrorCode(null, mdbx_get_sysraminfo(pgSz, totPg, avPg));
		return new RamInfo(pgSz[0], totPg[0], avPg[0]);
	}
}