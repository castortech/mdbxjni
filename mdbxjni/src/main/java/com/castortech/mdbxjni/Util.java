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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import static com.castortech.mdbxjni.JNI.mdbx_strerror;
import static com.castortech.mdbxjni.JNI.strlen;

/**
 * Some miscellaneous utility functions.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@SuppressWarnings("nls")
class Util {
	private static final Logger log = LoggerFactory.getLogger(Util.class);

	public static final boolean isAndroid = isAndroid();

	private Util() { }

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
		if (rc != JNI.MDBX_SUCCESS && rc != JNI.MDBX_RESULT_TRUE) {
			String msg = string(mdbx_strerror(rc));
			if (env != null) {
				log.info("MDBX Exception. Msg:{}, Env:{}", msg, env.info().toString());
			}
			throw new MDBXException(msg, rc);
		}
	}

	public static void checkErrorCode(Env env, Transaction txn, int rc) {
		if (rc != JNI.MDBX_SUCCESS && rc != JNI.MDBX_RESULT_TRUE) {
			String msg = string(mdbx_strerror(rc));
			if (env != null) {
				log.info("MDBX Exception. Msg:{}, Env:{}", msg, env.info().toString());
			}
			throw new MDBXException(msg, rc);
		}
	}

	public static void checkSize(final Env env, final Value val) {
		long size = val.getOffendingSize(env.getMaxKeySize());

		if (size >= 0) {
			String msg = format("Key size ({}) is too short or too long.", size);
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
		catch (Exception ignored) {
			return false;
		}
	}

	public static String format(String format, Object... params) {
		return MessageFormatter.arrayFormat(format, params).getMessage();
	}
}