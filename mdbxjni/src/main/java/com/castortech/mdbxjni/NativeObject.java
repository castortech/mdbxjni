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

/**
 * A helper base class which is used to track a pointer to a native
 * structure or class.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
class NativeObject {
	/** self pointer to native object */
	protected long self;

	/**
	 * @param self pointer to native object
	 */
	protected NativeObject(long self) {
		this.self = self;
		if (self == 0) {
			throw new OutOfMemoryError("Failure allocating native heap memory"); //$NON-NLS-1$
		}
	}

	/**
	 * Pointer for native object
	 * @return pointer
	 */
	long pointer() {
		checkAllocated();
		return self;
	}

	/**
	 * Is pointer allocated ?
	 * @return true if allocated
	 */
	public boolean isAllocated() {
		return self != 0;
	}

	/**
	 * Check if pointer is allocated and throws an MDXException if not.
	 */
	protected void checkAllocated() {
		if (!isAllocated()) {
			throw new MDBXException("Native object has been freed."); //$NON-NLS-1$
		}
	}

	/**
	 * Converts native object pointer to hex
	 * @return pointer in hex string format
	 */
	public String pointerHex() {
		return Long.toHexString(self & 0xFFFFFFFFFFFFFFFFL);
	}
}