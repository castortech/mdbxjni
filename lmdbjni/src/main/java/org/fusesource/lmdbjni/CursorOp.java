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
package org.fusesource.lmdbjni;

import static org.fusesource.lmdbjni.JNI.*;

/**
 * Cursor Get operations.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public enum CursorOp {
	/** Position at first keydata item */
	FIRST(MDB_FIRST),

	/**
	 * Position at first data item of current key. Only for
	 * {@link org.fusesource.lmdbjni.Constants#DUPSORT}
	 */
	FIRST_DUP(MDB_FIRST_DUP),

	/**
	 * Position at key/data pair. Only for
	 * {@link org.fusesource.lmdbjni.Constants#DUPSORT}
	 */
	GET_BOTH(MDB_GET_BOTH),

	/**
	 * position at key, nearest data. Only for
	 * {@link org.fusesource.lmdbjni.Constants#DUPSORT}
	 */
	GET_BOTH_RANGE(MDB_GET_BOTH_RANGE),

	/** Return key/data at current cursor position */
	GET_CURRENT(MDB_GET_CURRENT),

	/**
	 * Return key and up to a page of duplicate data items from current cursor
	 * position. Move cursor to prepare for
	 * {@link org.fusesource.lmdbjni.Constants#NEXT_MULTIPLE}. Only for
	 * {@link org.fusesource.lmdbjni.Constants#DUPFIXED}
	 */
	GET_MULTIPLE(MDB_GET_MULTIPLE),

	/** Position at last key/data item */
	LAST(MDB_LAST),

	/**
	 * Position at last data item of current key. Only for
	 * {@link org.fusesource.lmdbjni.Constants#DUPSORT}
	 */
	LAST_DUP(MDB_LAST_DUP),
	
	/** Position at next data item */
	NEXT(MDB_NEXT),
	
	/**
	 * Position at next data item of current key. Only for
	 * {@link org.fusesource.lmdbjni.Constants#DUPSORT}
	 */
	NEXT_DUP(MDB_NEXT_DUP),
	
	/**
	 * Return key and up to a page of duplicate data items from next cursor
	 * position. Move cursor to prepare for
	 * {@link org.fusesource.lmdbjni.Constants#NEXT_MULTIPLE}. Only for
	 * {@link org.fusesource.lmdbjni.Constants#DUPFIXED}
	 */
	NEXT_MULTIPLE(MDB_NEXT_MULTIPLE),
	
	/** Position at first data item of next key */
	NEXT_NODUP(MDB_NEXT_NODUP),
	
	/** Position at previous data item */
	PREV(MDB_PREV),
	
	/**
	 * Position at previous data item of current key. Only for
	 * {@link org.fusesource.lmdbjni.Constants#DUPSORT}
	 */
	PREV_DUP(MDB_PREV_DUP),
	
	/** Position at last data item of previous key */
	PREV_NODUP(MDB_PREV_NODUP),
	
	/** Position at specified key */
    SET(MDB_SET),
    
    /** Position at specified key, return key + data */
    SET_KEY(MDB_SET_KEY),

    /** Position at first key greater than or equal to specified key. */
    SET_RANGE(MDB_SET_RANGE);

	private final int value;

	CursorOp(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
