package com.castortech.mdbxjni.ffm;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Loads libmdbx before any downcall. The generated binding looks the library up
 * from the class loader, so {@link #load()} has to run first.
 */
public final class MdbxLibrary {
	/** Full path to {@code mdbx.dll}, {@code libmdbx.so}, or {@code libmdbx.dylib}. */
	public static final String PATH_PROPERTY = "mdbx.lib";

	/** Directory containing the libmdbx shared library. */
	public static final String DIR_PROPERTY = "mdbx.lib.dir";

	private static final String WINDOWS_DEFAULT = "C:\\ALPIC\\git\\libmdbx\\bin\\Release\\mdbx.dll";

	private static boolean loaded;

	private MdbxLibrary() {
	}

	public static void load() {
		if (loaded) {
			return;
		}
		synchronized (MdbxLibrary.class) {
			if (loaded) {
				return;
			}
			System.load(resolve().getAbsolutePath());
			loaded = true;
		}
	}

	static File resolve() {
		String explicit = System.getProperty(PATH_PROPERTY);
		if (explicit != null && !explicit.isBlank()) {
			return requireFile(toLibraryFile(explicit));
		}

		String dir = System.getProperty(DIR_PROPERTY);
		if (dir != null && !dir.isBlank()) {
			return requireFile(new File(dir, libraryFileName()));
		}

		File packaged = packagedLibrary();
		if (packaged != null) {
			return packaged;
		}

		File cwd = new File(System.getProperty("user.dir"));
		String name = libraryFileName();
		File[] candidates = {
				new File(cwd, "../../../libmdbx/bin/Release/" + name),
				new File(cwd, "../../libmdbx/bin/Release/" + name),
				new File(WINDOWS_DEFAULT)
		};
		for (File candidate : candidates) {
			if (candidate.isFile()) {
				return candidate;
			}
		}
		throw new IllegalStateException("libmdbx was not found. Set -D" + PATH_PROPERTY
				+ " to the shared library. Looked for " + name + " from " + cwd.getAbsolutePath());
	}

	private static File toLibraryFile(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			return new File(file, libraryFileName());
		}
		return file;
	}

	private static File requireFile(File file) {
		if (!file.isFile()) {
			throw new IllegalStateException("libmdbx was not found: " + file.getAbsolutePath());
		}
		return file;
	}

	private static File packagedLibrary() {
		String resource = "META-INF/native/" + nativeDirectory() + "/" + libraryFileName();
		try (InputStream input = MdbxLibrary.class.getClassLoader().getResourceAsStream(resource)) {
			if (input == null) {
				return null;
			}
			File directory = Files.createTempDirectory("mdbxjni").toFile();
			directory.deleteOnExit();
			File library = new File(directory, libraryFileName());
			Files.copy(input, library.toPath());
			library.deleteOnExit();
			return library;
		}
		catch (java.io.IOException failure) {
			throw new IllegalStateException("libmdbx could not be unpacked from " + resource, failure);
		}
	}

	private static String nativeDirectory() {
		String os = System.getProperty("os.name", "").toLowerCase();
		if (os.contains("win")) {
			return "windows64";
		}
		if (os.contains("mac")) {
			return "osx64";
		}
		return "linux64";
	}

	private static String libraryFileName() {
		String os = System.getProperty("os.name", "").toLowerCase();
		if (os.contains("win")) {
			return "mdbx.dll";
		}
		if (os.contains("mac")) {
			return "libmdbx.dylib";
		}
		return "libmdbx.so";
	}
}
