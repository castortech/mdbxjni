/**
 * Copyright (C) 2013, Benchmark Consulting Canada, Inc.
 *
 *    http://www.benchmarkconsulting.com/
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
 * Cursor state flags.
 *
 * @author Alain Picard
 */
public enum CursorStateFlags {
	/** None */
	NONE(0),

//	/** cursor has been initialized and is valid */
//	INITIALIZED(C_INITIALIZED),
//
//	/** No more data */
//	EOF(C_EOF),
//
//	/** Cursor is a sub-cursor */
//	SUB(C_SUB),
//
//	/** last op was a cursor_del */
//	DEL(C_DEL),
//
//	/** Un-track cursor when closing */
//	UNTRACK(C_UNTRACK),
//
//	/** Preparing for a GC update is in progress, so you can take pages from GC even for FREE_DBI */
//	GCU(C_GCU),
	;

	private final int value;

	CursorStateFlags(int value) {
		this.value = value;
	}

	/**
	 * Get value
	 * @return value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Get enum by value
	 * @param value the value associate with the enum
	 * @return the related CursorStateFlags
	 */
	public static CursorStateFlags getByValue(int value) {
		for (CursorStateFlags level : values()) {
			if (value == level.getValue()) {
				return level;
			}
		}

		throw new IllegalStateException("Invalid curor state:" + value); //$NON-NLS-1$
	}
}