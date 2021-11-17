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

import java.util.Map;

/**
 * Key and value.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Entry implements Map.Entry<byte[], byte[]> {
	private final byte[] key;
	private final byte[] value;

	public Entry(byte[] key, byte[] value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public byte[] getKey() {
		return key;
	}

	@Override
	public byte[] getValue() {
		return value;
	}

	@Override
	public byte[] setValue(byte[] value) {
		throw new UnsupportedOperationException();
	}
}