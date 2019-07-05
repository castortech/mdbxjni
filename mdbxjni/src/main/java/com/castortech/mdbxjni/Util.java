/**
 * Copyright (C) 2013, RedHat, Inc.
 *
 *    http://www.redhat.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.castortech.mdbxjni;

import java.nio.charset.Charset;

import static com.castortech.mdbxjni.JNI.mdbx_strerror;
import static com.castortech.mdbxjni.JNI.strlen;

/**
 * Some miscellaneous utility functions.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
class Util {
	public static final boolean isAndroid = isAndroid();

	public static int errno() {
		return errno();
	}

	public static String strerror() {
		return string(JNI.strerror(errno()));
	}

	public static String string(long ptr) {
		if (ptr == 0)
			return null;
		return new String(NativeBuffer.create(ptr, strlen(ptr)).toByteArray(), Charset.defaultCharset());
	}

	public static void checkErrorCode(Env env, int rc) {
		if (rc != 0) {
			String msg = string(mdbx_strerror(rc));
			if (env != null) {
				System.err.println("MDBX Exception. Env:" + env.info().toString());
			}
			throw new MDBXException(msg, rc);
		}
	}

	public static void checkSize(final Env env, final Value val) {
		long size = val.getOffendingSize(env.getMaxKeySize());

		if (size >= 0) {
			String msg = "Key size (" + size + ") is too short or too long.";
			throw new MDBXException(msg, JNI.MDBX_BAD_VALSIZE);
		}
	}

	public static void checkArgNotNull(Object value, String name) {
		if (value == null) {
			throw new IllegalArgumentException("The " + name + " argument cannot be null");
		}
	}

	static boolean isAndroid() {
		try {
			Class.forName("android.os.Process"); //$NON-NLS-1$
			return true;
		}
		catch (Throwable ignored) {
			return false;
		}
	}
}
