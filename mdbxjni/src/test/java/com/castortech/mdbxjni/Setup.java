package com.castortech.mdbxjni;

import java.io.File;

public class Setup {
  private static String OS = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$

  public static boolean isWindows() {
    return (OS.indexOf("win") >= 0); //$NON-NLS-1$
  }

  public static boolean isMac() {
    return (OS.indexOf("mac") >= 0); //$NON-NLS-1$
  }

  public static void setLmdbLibraryPath() {
    File path = new File(System.getProperty("user.dir")); //$NON-NLS-1$
    if (isWindows()) {
      path = new File(path, "../mdbxjni-win64/target/native-build/target/x64-release/lib"); //$NON-NLS-1$
    } else if (isMac()) {
      path = new File(path, "../mdbxjni-osx64/target/native-build/target/lib"); //$NON-NLS-1$
    } else {
      path = new File(path, "../mdbxjni-linux64/target/native-build/target/lib"); //$NON-NLS-1$
    }
    if (!path.exists()) {
      throw new IllegalStateException("Please build mdbxjni first " +
        "with a platform specific profile");
    }
    String absolutePath = path.getAbsolutePath();
		System.setProperty("library.mdbxjni.path", absolutePath); //$NON-NLS-1$
  }
}
