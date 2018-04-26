package com.castortech.mdbxjni;

/**
 * Information about the LMDB environment.
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

  EnvInfo(JNI.MDBX_envinfo rc) {
  	this.geo = new Geo(rc.mi_geo_lower, rc.mi_geo_upper, rc.mi_geo_current, rc.mi_geo_shrink, rc.mi_geo_grow);
    this.mapSize = rc.mi_mapsize;
    this.lastPgNo = rc.mi_last_pgno;
    this.recentTxnId = rc.mi_recent_txnid;
    this.latterReaderTxnId = rc.mi_latter_reader_txnid;
    this.selfLatterReaderTxnId = rc.mi_self_latter_reader_txnid;
    this.meta0TxnId = rc.mi_meta0_txnid;
    this.meta0Sign = rc.mi_meta0_sign;
    this.meta1TxnId = rc.mi_meta1_txnid;
    this.meta1Sign = rc.mi_meta1_sign;
    this.meta2TxnId = rc.mi_meta2_txnid;
    this.meta2Sign = rc.mi_meta2_sign;
    this.maxReaders = rc.mi_maxreaders;
    this.numReaders = rc.mi_numreaders;
    this.dxbPageSize = rc.mi_dxb_pagesize;
    this.sysPageSize = rc.mi_sys_pagesize;
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

  @SuppressWarnings("nls")
	@Override
	public String toString() {
		return "EnvInfo [=" + 
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
				"]";
	}

	public class Geo {
		private final long lower;		/* lower limit for datafile size */
		private final long upper;  	/* upper limit for datafile size */
		private final long current; 	/* current datafile size */
		private final long shrink;  	/* shrink treshold for datafile */
	  private final long grow;    	/* growth step for datafile */
	  
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
}
