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
		gc_prof_wloops = rc.gc_prof_wloops;
		gc_prof_coalescences = rc.gc_prof_coalescences;
		gc_prof_wipes= rc.gc_prof_wipes;
		gc_prof_flushes = rc.gc_prof_flushes;
		gc_prof_kicks = rc.gc_prof_kicks;
		gc_prof_work_counter = rc.gc_prof_work_counter;
		gc_prof_work_rtime_monotonic = rc.gc_prof_work_rtime_monotonic;
		gc_prof_work_xtime_cpu = rc.gc_prof_work_xtime_cpu;
		gc_prof_work_rsteps = rc.gc_prof_work_rsteps;
		gc_prof_work_xpages = rc.gc_prof_work_xpages;
		gc_prof_work_majflt = rc.gc_prof_work_majflt;
		gc_prof_self_counter = rc.gc_prof_self_counter;
		gc_prof_self_rtime_monotonic = rc.gc_prof_self_rtime_monotonic;
		gc_prof_self_xtime_cpu = rc.gc_prof_self_xtime_cpu;
		gc_prof_self_rsteps= rc.gc_prof_self_rsteps;
		gc_prof_self_xpages = rc.gc_prof_self_xpages;
		gc_prof_self_majflt = rc.gc_prof_self_majflt;
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
			", gc_prof_wloops=" + gc_prof_wloops +
			", gc_prof_coalescences=" + gc_prof_coalescences +
			", gc_prof_wipes=" + gc_prof_wipes +
			", gc_prof_flushes=" + gc_prof_flushes +
			", gc_prof_kicks=" + gc_prof_kicks +
			", gc_prof_work_counter=" + gc_prof_work_counter +
			", gc_prof_work_rtime_monotonic=" + gc_prof_work_rtime_monotonic +
			", gc_prof_work_xtime_cpu=" + gc_prof_work_xtime_cpu +
			", gc_prof_work_rsteps=" + gc_prof_work_rsteps +
			", gc_prof_work_xpages=" + gc_prof_work_xpages +
			", gc_prof_work_majflt=" + gc_prof_work_majflt +
			", gc_prof_self_counter=" + gc_prof_self_counter +
			", gc_prof_self_rtime_monotonic=" + gc_prof_self_rtime_monotonic +
			", gc_prof_self_xtime_cpu=" + gc_prof_self_xtime_cpu +
			", gc_prof_self_rsteps=" + gc_prof_self_rsteps +
			", gc_prof_self_xpages=" + gc_prof_self_xpages +
			", gc_prof_self_majflt=" + gc_prof_self_majflt +
			'}';
	}
}