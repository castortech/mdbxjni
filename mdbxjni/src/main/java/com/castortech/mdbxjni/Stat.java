package com.castortech.mdbxjni;

/**
 * Statistics about the LMDB environment.
 */
public class Stat extends JNI.MDBX_stat {
	Stat(JNI.MDBX_stat rc) {
		ms_branch_pages = rc.ms_branch_pages;
		ms_leaf_pages = rc.ms_leaf_pages;
		ms_depth = rc.ms_depth;
		ms_entries = rc.ms_entries;
		ms_overflow_pages = rc.ms_overflow_pages;
		ms_psize = rc.ms_psize;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "Stat{" +
			"psize=" + ms_psize +
			", depth=" + ms_depth +
			", branchPages=" + ms_branch_pages +
			", leafPages=" + ms_leaf_pages +
			", overflowPages=" + ms_overflow_pages +
			", entries=" + ms_entries +
			'}';
	}
}