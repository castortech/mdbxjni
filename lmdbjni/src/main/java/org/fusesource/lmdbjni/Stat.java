package org.fusesource.lmdbjni;


/**
 * Statistics about the LMDB environment.
 */
public class Stat extends JNI.MDB_stat {

  Stat(JNI.MDB_stat rc) {
    this.ms_branch_pages = rc.ms_branch_pages;
    this.ms_leaf_pages = rc.ms_leaf_pages;
    this.ms_depth = rc.ms_depth;
    this.ms_entries = rc.ms_entries;
    this.ms_overflow_pages = rc.ms_overflow_pages;
    this.ms_psize = rc.ms_psize;
  }

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
