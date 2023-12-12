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
		setJniLibraryPath(mode);
		setDbLibraryPath(mode);
	}

	@SuppressWarnings("nls")
	public static void setJniLibraryPath(String mode) {
		String modeLC = mode.toLowerCase();
		File path;
		String libPath = System.getProperty("jni.lib.dir");

		if (libPath != null) {
			path = new File(libPath);
		}
		else {
			path = new File(System.getProperty("user.dir"));
			System.out.println("User dir:" + path);
			boolean inTarget = path.toString().endsWith("target");

			if (isWindows()) {
				if (!inTarget) {
					path = new File(path, "../mdbxjni-win64/target/native-build/target/x64-" + modeLC + "/lib");
				}
				else {
					path = new File(path, "/native-build/target/x64-" + modeLC + "/lib");
				}
			}
			else if (isMac()) {
				if (!inTarget) {
					path = new File(path, "../mdbxjni-osx64/target/generated-sources/hawtjni/lib/META-INF/native");
//					path = new File(path, "../mdbxjni-osx64/target/native-build/target/lib"); //$NON-NLS-1$
				}
				else {
					path = new File(path, "/native-build/target/lib");
				}
			}
			else {
				if (!inTarget) {
					path = new File(path, "../mdbxjni-linux64/target/native-build/target/lib");
				}
				else {
					path = new File(path, "/native-build/target/lib");
				}
			}
		}

		if (!path.exists()) {
			throw new IllegalStateException("Path(" + path + ") doesn't exist. Please build mdbxjni first with a platform specific profile");
		}
		String absolutePath = path.getAbsolutePath();
		System.setProperty("library.mdbxjni.path", absolutePath);
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

			if (isWindows()) {
				if (!inTarget) {
					path = new File(path, "../../libmdbx/bin/" + mode);
				}
				else {
					path = new File(path, "../../../libmdbx/bin/" + mode);
				}
			}
			//TODO: Adjust for specific platforms
			else if (isMac()) {
				if (!inTarget) {
					path = new File(path, "../mdbxjni-osx64/target/generated-sources/hawtjni/lib/META-INF/native");
				}
				else {
					path = new File(path, "/native-build/target/lib");
				}
			}
			else {
				if (!inTarget) {
					path = new File(path, "../mdbxjni-linux64/target/native-build/target/lib");
				}
				else {
					path = new File(path, "/native-build/target/lib");
				}
			}
		}

		if (!path.exists()) {
			throw new IllegalStateException("Path(" + path + ") doesn't exist. Please build mdbxjni first with a platform specific profile");
		}
		String absolutePath = path.getAbsolutePath();
		System.setProperty("library.mdbx.path", absolutePath);
	}
}