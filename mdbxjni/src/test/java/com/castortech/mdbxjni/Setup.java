package com.castortech.mdbxjni;

import java.io.File;

public class Setup {
	public static final String RELEASE_MODE = "Release"; //$NON-NLS-1$
	public static final String DEBUG_MODE = "Debug"; //$NON-NLS-1$

	private static final String DEFAULT_MODE = DEBUG_MODE;

	private static String OS = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$

	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0); //$NON-NLS-1$
	}

	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0); //$NON-NLS-1$
	}

	public static void setLibraryPaths() {
		setLibraryPaths(DEFAULT_MODE);
	}

	public static void setLibraryPaths(String mode) {
		setDbLibraryPath(mode);
	}

	@SuppressWarnings("nls")
	public static void setDbLibraryPath(String mode) {
		File path;
		String libPath = System.getProperty("mdbx.lib.dir");

		if (libPath != null) {
			path = new File(libPath);
		}
		else {
			path = new File(System.getProperty("user.dir"));
			boolean inTarget = path.toString().endsWith("target");
			String relative = isWindows() ? "../../libmdbx/bin/" + mode : "../../libmdbx";
			if (inTarget) {
				relative = "../" + relative;
			}
			path = new File(path, relative);
		}

		File library = new File(path, isWindows() ? "mdbx.dll" : isMac() ? "libmdbx.dylib" : "libmdbx.so");
		if (!library.isFile() && isWindows()) {
			library = new File("C:\\ALPIC\\git\\libmdbx\\bin\\Release\\mdbx.dll");
		}
		if (library.isFile()) {
			System.setProperty("mdbx.lib", library.getAbsolutePath());
			System.setProperty("library.mdbx.path", library.getParentFile().getAbsolutePath());
		}
	}
}