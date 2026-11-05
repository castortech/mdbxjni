package com.castortech.mdbxjni.ffm;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.castortech.mdbxjni.Env;
import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/**
 * Loads every generated downcall. jextract resolves the DLL symbol when the holder class initializes.
 */
public class FfmSymbolTest {
	static {
		MdbxLibrary.load();
	}

	@Test
	public void functionsResolve() throws Exception {
		int resolved = 0;
		for (Class<?> nested : mdbx_h.class.getDeclaredClasses()) {
			if (!nested.getSimpleName().startsWith("mdbx_")) {
				continue;
			}
			Class.forName(nested.getName(), true, nested.getClassLoader());
			resolved++;
		}
		assertTrue(resolved > 50);
	}

	@Test
	public void versionGlobal() {
		String version = Env.version();
		assertTrue(version, version.startsWith("0.14."));
	}
}
