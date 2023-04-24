package com.castortech.mdbxjni;

/**
 * Information about the MDBX environment.
 */
public class EnvInfo {
	private final Geo geo;
	private final long mapSize;
	private final long lastPgNo;
	private final long recentTxnId;
	private final long latterReaderTxnId;
	private final long selfLatterReaderTxnId;
	private final long meta0TxnId;
	private final long meta0Sign;
	private final long meta1TxnId;
	private final long meta1Sign;
	private final long meta2TxnId;
	private final long meta2Sign;
	private final long maxReaders;
	private final long numReaders;
	private final long dxbPageSize;
	private final long sysPageSize;
	private final BootId bootId;
	private final long unsyncVolume;
	private final long autosyncThreshold;
	private final long sinceSyncSeconds16dot16;
	private final long autosyncPeriodSeconds16dot16;
	private final long sinceReaderCheckSeconds16dot16;
	private final long mode;
	private final PgopStat pgopStat;

	EnvInfo(JNI.MDBX_envinfo rc) {
		geo = new Geo(rc.mi_geo_lower, rc.mi_geo_upper, rc.mi_geo_current, rc.mi_geo_shrink, rc.mi_geo_grow);
		mapSize = rc.mi_mapsize;
		lastPgNo = rc.mi_last_pgno;
		recentTxnId = rc.mi_recent_txnid;
		latterReaderTxnId = rc.mi_latter_reader_txnid;
		selfLatterReaderTxnId = rc.mi_self_latter_reader_txnid;
		meta0TxnId = rc.mi_meta0_txnid;
		meta0Sign = rc.mi_meta0_sign;
		meta1TxnId = rc.mi_meta1_txnid;
		meta1Sign = rc.mi_meta1_sign;
		meta2TxnId = rc.mi_meta2_txnid;
		meta2Sign = rc.mi_meta2_sign;
		maxReaders = rc.mi_maxreaders;
		numReaders = rc.mi_numreaders;
		dxbPageSize = rc.mi_dxb_pagesize;
		sysPageSize = rc.mi_sys_pagesize;

		Point current = new Point(rc.mi_bootid_current_x, rc.mi_bootid_current_y);
		Point meta0 = new Point(rc.mi_bootid_meta0_x, rc.mi_bootid_meta0_y);
		Point meta1 = new Point(rc.mi_bootid_meta1_x, rc.mi_bootid_meta1_y);
		Point meta2 = new Point(rc.mi_bootid_meta2_x, rc.mi_bootid_meta2_y);
		bootId = new BootId(current, meta0, meta1, meta2);
		unsyncVolume = rc.mi_unsync_volume;
		autosyncThreshold = rc.mi_autosync_threshold;
		sinceSyncSeconds16dot16 = rc.mi_since_sync_seconds16dot16;
		autosyncPeriodSeconds16dot16 = rc.mi_autosync_period_seconds16dot16;
		sinceReaderCheckSeconds16dot16 = rc.mi_since_reader_check_seconds16dot16;
		mode = rc.mi_mode;

		pgopStat =  new PgopStat(rc.mi_pgop_stat_newly, rc.mi_pgop_stat_cow, rc.mi_pgop_stat_clone,
				rc.mi_pgop_stat_split, rc.mi_pgop_stat_merge, rc.mi_pgop_stat_spill, rc.mi_pgop_stat_unspill,
				rc.mi_pgop_stat_wops, rc.mi_pgop_stat_prefault, rc.mi_pgop_stat_mincore, rc.mi_pgop_stat_msync,
				rc.mi_pgop_stat_fsync);
	}

	public Geo getGeo() {
		return geo;
	}

	/**
	 * @return Size of the data memory map.
	 */
	public long getMapSize() {
		return mapSize;
	}

	/**
	 * @return ID of the last used page.
	 */
	public long getLastPgNo() {
		return lastPgNo;
	}

	/**
	 * @return ID of the last committed transaction.
	 */
	public long getLastTxnId() {
		return recentTxnId;
	}

	public long getLatterReaderTxnId() {
		return latterReaderTxnId;
	}

	public long getSelfLatterReaderTxnId() {
		return selfLatterReaderTxnId;
	}

	public long getMeta0TxnId() {
		return meta0TxnId;
	}

	public long getMeta0Sign() {
		return meta0Sign;
	}

	public long getMeta1TxnId() {
		return meta1TxnId;
	}

	public long getMeta1Sign() {
		return meta1Sign;
	}

	public long getMeta2TxnId() {
		return meta2TxnId;
	}

	public long getMeta2Sign() {
		return meta2Sign;
	}

	public long getDxbPageSize() {
		return dxbPageSize;
	}

	public long getSysPageSize() {
		return sysPageSize;
	}

	/**
	 * @return max reader slots in the environment.
	 */
	public long getMaxReaders() {
		return maxReaders;
	}

	/**
	 * @return max reader slots used in the environment.
	 */
	public long getNumReaders() {
		return numReaders;
	}

	public long getRecentTxnId() {
		return recentTxnId;
	}

	public BootId getBootId() {
		return bootId;
	}

	public long getUnsyncVolume() {
		return unsyncVolume;
	}

	public long getAutosyncThreshold() {
		return autosyncThreshold;
	}

	public long getSinceSyncSeconds16dot16() {
		return sinceSyncSeconds16dot16;
	}

	public long getAutosyncPeriodSeconds16dot16() {
		return autosyncPeriodSeconds16dot16;
	}

	public long getSinceReaderCheckSeconds16dot16() {
		return sinceReaderCheckSeconds16dot16;
	}

	public long getMode() {
		return mode;
	}

	public PgopStat getPgopStat() {
		return pgopStat;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "EnvInfo [" +
				"geo=" + geo +
				", mapSize=" + mapSize +
				", lastPgNo=" + lastPgNo +
				", lastTxnId=" + recentTxnId +
				", latterReaderTxnId=" + latterReaderTxnId +
				", SelfLatterReaderTxnId=" + selfLatterReaderTxnId +
				", meta0TxnId=" + meta0TxnId +
				", meta0Sign=" + meta0Sign +
				", meta1TxnId=" + meta1TxnId +
				", meta1Sign=" + meta1Sign +
				", meta2TxnId=" + meta2TxnId +
				", meta2Sign=" + meta2Sign +
				", maxReaders=" + maxReaders +
				", numReaders=" + numReaders +
				", dxbPageSize=" + dxbPageSize +
				", sysPageSize=" + sysPageSize +
				", bootId=" + bootId +
				", unsyncVolume=" + unsyncVolume +
				", autosyncThreshold=" + autosyncThreshold +
				", sinceSyncSeconds16dot16=" + sinceSyncSeconds16dot16 +
				", autosyncPeriodSeconds16dot16=" + autosyncPeriodSeconds16dot16 +
				", sinceReaderCheckSeconds16dot16=" + sinceReaderCheckSeconds16dot16 +
				", mode=" + mode +
				", pgopStat=" + pgopStat +
				"]";
	}

	public class Geo {
		private final long lower;			/* lower limit for datafile size */
		private final long upper;			/* upper limit for datafile size */
		private final long current;		/* current datafile size */
		private final long shrink;		/* shrink treshold for datafile */
		private final long grow;			/* growth step for datafile */

		public Geo(long lower, long upper, long current, long shrink, long grow) {
			super();
			this.lower = lower;
			this.upper = upper;
			this.current = current;
			this.shrink = shrink;
			this.grow = grow;
		}

		public long getLower() {
			return lower;
		}

		public long getUpper() {
			return upper;
		}

		public long getCurrent() {
			return current;
		}

		public long getShrink() {
			return shrink;
		}

		public long getGrow() {
			return grow;
		}

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
					"lower=" + lower +
					", upper=" + upper +
					", current=" + current +
					", shrink=" + shrink +
					", grow=" + grow +
					'}';
		}
	}

	public class Point {
		private final long x;
		private final long y;

		public Point(long x, long y) {
			super();
			this.x = x;
			this.y = y;
		}

		public long getX() {
			return x;
		}

		public long getY() {
			return y;
		}

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
					"x=" + x +
					", y=" + y +
					'}';
		}
	}

	public class BootId {
		private final Point current;
		private final Point meta0;
		private final Point meta1;
		private final Point meta2;

		public BootId(Point current, Point meta0, Point meta1, Point meta2) {
			super();
			this.current = current;
			this.meta0 = meta0;
			this.meta1 = meta1;
			this.meta2 = meta2;
		}

		public Point getCurrent() {
			return current;
		}

		public Point getMeta0() {
			return meta0;
		}

		public Point getMeta1() {
			return meta1;
		}

		public Point getMeta2() {
			return meta2;
		}

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
					"current=" + current +
					", meta0=" + meta0 +
					", meta1=" + meta1 +
					", meta2=" + meta2 +
					'}';
		}
	}

	public class PgopStat {
		private final long newly;
		private final long cow;
		private final long clone;
		private final long split;
		private final long merge;
		private final long spill;
		private final long unspill;
		private final long wops;
		private final long prefault;
		private final long mincore;
		private final long msync;
		private final long fsync;

		public PgopStat(long newly, long cow, long clone, long split, long merge, long spill, long unspill,
				long wops, long prefault, long mincore, long msync, long fsync) {
			super();
			this.newly = newly;
			this.cow = cow;
			this.clone = clone;
			this.split = split;
			this.merge = merge;
			this.spill = spill;
			this.unspill = unspill;
			this.wops = wops;
			this.prefault = prefault;
			this.mincore = mincore;
			this.msync = msync;
			this.fsync = fsync;
		}

		public long getNewly() {
			return newly;
		}

		public long getCow() {
			return cow;
		}

		public long getClone() {
			return clone;
		}

		public long getSplit() {
			return split;
		}

		public long getMerge() {
			return merge;
		}

		public long getSpill() {
			return spill;
		}

		public long getUnspill() {
			return unspill;
		}

		public long getWops() {
			return wops;
		}

		public long getPrefault() {
			return prefault;
		}

		public long getMincore() {
			return mincore;
		}

		public long getMsync() {
			return msync;
		}

		public long getFsync() {
			return fsync;
		}

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
					"newly=" + newly +
					", cow=" + cow +
					", clone=" + clone +
					", split=" + split +
					", merge=" + merge +
					", spill=" + spill +
					", unspill=" + unspill +
					", wops=" + wops +
					", prefault=" + prefault +
					", mincore=" + mincore +
					", msync=" + msync +
					", fsync=" + fsync +
					"}";
		}
	}
}