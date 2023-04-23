package com.castortech.mdbxjni;

/**
 * Latency of commit stages in 1/65536 of seconds units.
 */
public class CommitLatency extends JNI.MDBX_commit_latency {
	CommitLatency(JNI.MDBX_commit_latency rc) {
		preparation = rc.preparation;
		gc_wallclock = rc.gc_wallclock;
		audit = rc.audit;
		write = rc.write;
		sync = rc.sync;
		ending = rc.ending;
		whole = rc.whole;
		gc_cputime = rc.gc_cputime;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "Stat{" +
			"preparation=" + preparation +
			", gc_wallclock=" + gc_wallclock +
			", audit=" + audit +
			", write=" + write +
			", sync=" + sync +
			", ending=" + ending +
			", whole=" + whole +
			", gc_cputime=" + gc_cputime +
			'}';
	}
}