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

import java.util.Arrays;

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
class Value extends JNI.MDBX_val {
	public Value() {
	}

	public Value(long data, long length) {
		this.iov_base = data;
		this.iov_len = length;
	}

	public Value(NativeBuffer buffer) {
		this(buffer.pointer(), buffer.capacity());
	}

	public static Value create(NativeBuffer buffer) {
		if (buffer == null) {
			return null;
		} else {
			return new Value(buffer);
		}
	}

	public byte[] toByteArray() {
		if (iov_base == 0) {
			return null;
		}
		if (iov_len > Integer.MAX_VALUE) {
			throw new ArrayIndexOutOfBoundsException("Native slice is larger than the maximum Java array");
		}
		byte[] rc = new byte[(int)iov_len];
		JNI.buffer_copy(iov_base, 0, rc, 0, rc.length);
		return rc;
	}

	public long getOffendingSize(long maxSize) {
		if (iov_base == 0) {
			return 0;
		}
		if (iov_len > maxSize) {
			return iov_len;
		}
		return -1;
	}

	@Override
	public boolean equals(final Object object) {
		if (this == object)
			return true;
		if (object == null || getClass() != object.getClass())
			return false;
		final Value other = (Value)object;
		return Arrays.equals(toByteArray(), other.toByteArray());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(toByteArray());
	}
}
