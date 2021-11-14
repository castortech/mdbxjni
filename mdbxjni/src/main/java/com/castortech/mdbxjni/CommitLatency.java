package com.castortech.mdbxjni;

/**
 * Latency of commit stages in 1/65536 of seconds units.
 */
public class CommitLatency extends JNI.MDBX_commit_latency {
	CommitLatency(JNI.MDBX_commit_latency rc) {
		preparation = rc.preparation;
		gc = rc.gc;
		audit = rc.audit;
		write = rc.write;
		sync = rc.sync;
		ending = rc.ending;
		whole = rc.whole;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "Stat{" +
			"preparation=" + preparation +
			", gc=" + gc +
			", audit=" + audit +
			", write=" + write +
			", sync=" + sync +
			", ending=" + ending +
			", whole=" + whole +
			'}';
	}
}