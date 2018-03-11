package org.fusesource.lmdbjni;

import org.junit.Test;

public class NativeBufferTest {
  static {
    Setup.setLmdbLibraryPath();
  }

  @Test
  public void testPool() {
    NativeBuffer.Pool pool = new NativeBuffer.Pool(10, null);
    pool.create(1);
    pool.create(20);
    pool.delete();

  }
}
