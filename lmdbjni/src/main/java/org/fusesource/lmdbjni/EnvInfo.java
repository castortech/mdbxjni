package org.fusesource.lmdbjni;

/**
 * Information about the LMDB environment.
 */
public class EnvInfo {
  private final long mapAddr;
  private final long mapSize;
  private final long lastPgNo;
  private final long lastTxnId;
  private final long maxReaders;
  private final long numReaders;

  EnvInfo(JNI.MDB_envinfo rc) {
    this.mapAddr = rc.me_mapaddr;
    this.mapSize = rc.me_mapsize;
    this.lastPgNo = rc.me_last_pgno;
    this.lastTxnId = rc.me_last_txnid;
    this.maxReaders = rc.me_maxreaders;
    this.numReaders = rc.me_numreaders;
  }

  /**
   * @return Address of map, if fixed.
   */
  public long getMapAddr() {
    return mapAddr;
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
    return lastTxnId;
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

  @Override
  public String toString() {
    return "EnvInfo{" +
      "mapAddr=" + mapAddr +
      ", mapSize=" + mapSize +
      ", lastPgNo=" + lastPgNo +
      ", lastTxnId=" + lastTxnId +
      ", maxReaders=" + maxReaders +
      ", numReaders=" + numReaders +
      '}';
  }
}
