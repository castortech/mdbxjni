package com.castortech.mdbxjni.ffm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.castortech.mdbxjni.JNI;
import com.castortech.mdbxjni.MDBXException;
import com.castortech.mdbxjni.ffm.gen.mdbx_h;

/** The errno aliases have to match the OS that loaded libmdbx, not the Windows jextract host. */
public class FfmErrorCodeTest {
	@Test
	public void accessCodeMatchesThisOs() {
		int expected = System.getProperty("os.name", "").toLowerCase().contains("win") ? 5 : 13;
		assertEquals(expected, JNI.MDBX_EACCESS);
		assertEquals(expected, MDBXException.Status.EACCES.getStatusCode());
		assertEquals(MDBXException.Status.EACCES, MDBXException.Status.fromStatusCode(expected));
	}

	@Test
	public void readOnlyFlagStaysOnTheHeaderLiteral() {
		assertEquals(mdbx_h.MDBX_RDONLY(), JNI.MDBX_RDONLY);
	}
}
