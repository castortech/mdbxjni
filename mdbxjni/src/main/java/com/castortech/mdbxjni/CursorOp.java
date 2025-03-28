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

import static com.castortech.mdbxjni.JNI.*;

/**
 * Cursor Get operations.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public enum CursorOp {
	/** Position at first keydata item */
	FIRST(MDBX_FIRST),

	/**
	 * Position at first data item of current key. Only for
	 * {@link com.castortech.mdbxjni.Constants#DUPSORT}
	 */
	FIRST_DUP(MDBX_FIRST_DUP),

	/**
	 * Position at key/data pair. Only for
	 * {@link com.castortech.mdbxjni.Constants#DUPSORT}
	 */
	GET_BOTH(MDBX_GET_BOTH),

	/**
	 * position at key, nearest data. Only for
	 * {@link com.castortech.mdbxjni.Constants#DUPSORT}
	 */
	GET_BOTH_RANGE(MDBX_GET_BOTH_RANGE),

	/** Return key/data at current cursor position */
	GET_CURRENT(MDBX_GET_CURRENT),

	/**
	 * Return key and up to a page of duplicate data items from current cursor
	 * position. Move cursor to prepare for
	 * {@link com.castortech.mdbxjni.Constants#NEXT_MULTIPLE}. Only for
	 * {@link com.castortech.mdbxjni.Constants#DUPFIXED}
	 */
	GET_MULTIPLE(MDBX_GET_MULTIPLE),

	/** Position at last key/data item */
	LAST(MDBX_LAST),

	/**
	 * Position at last data item of current key. Only for
	 * {@link com.castortech.mdbxjni.Constants#DUPSORT}
	 */
	LAST_DUP(MDBX_LAST_DUP),

	/** Position at next data item */
	NEXT(MDBX_NEXT),

	/**
	 * Position at next data item of current key. Only for
	 * {@link com.castortech.mdbxjni.Constants#DUPSORT}
	 */
	NEXT_DUP(MDBX_NEXT_DUP),

	/**
	 * Return key and up to a page of duplicate data items from next cursor
	 * position. Move cursor to prepare for
	 * {@link com.castortech.mdbxjni.Constants#NEXT_MULTIPLE}. Only for
	 * {@link com.castortech.mdbxjni.Constants#DUPFIXED}
	 */
	NEXT_MULTIPLE(MDBX_NEXT_MULTIPLE),

	/** Position at first data item of next key */
	NEXT_NODUP(MDBX_NEXT_NODUP),

	/** Position at previous data item */
	PREV(MDBX_PREV),

	/**
	 * Position at previous data item of current key. Only for
	 * {@link com.castortech.mdbxjni.Constants#DUPSORT}
	 */
	PREV_DUP(MDBX_PREV_DUP),

	/** Position at last data item of previous key */
	PREV_NODUP(MDBX_PREV_NODUP),

	/** Position at specified key */
	SET(MDBX_SET),

	/** Position at specified key, return key + data */
	SET_KEY(MDBX_SET_KEY),

	/** Position at first key greater than or equal to specified key. */
	SET_RANGE(MDBX_SET_RANGE),

	/** MDBX_DUPFIXED-only: Position at previous page and
   * return key and up to a page of duplicate data items.
   */
	PREV_MULTIPLE(MDBX_PREV_MULTIPLE),

	/** Position at first key-value pair greater than or equal to specified,
   * return both key and data, and the return code depends on a exact match.
   *
   * For non DUPSORT-ed collections this work the same to \ref MDBX_SET_RANGE,
   * but returns \ref MDBX_SUCCESS if key found exactly or
   * \ref MDBX_RESULT_TRUE if greater key was found.
   *
   * For DUPSORT-ed a data value is taken into account for duplicates,
   * i.e. for a pairs/tuples of a key and an each data value of duplicates.
   * Returns \ref MDBX_SUCCESS if key-value pair found exactly or
   * \ref MDBX_RESULT_TRUE if the next pair was returned.
   */
	SET_LOWERBOUND(MDBX_SET_LOWERBOUND),

	/** Positions cursor at first key-value pair greater than specified,
   * return both key and data, and the return code depends on whether a
   * upper-bound was found.
   *
   * For non DUPSORT-ed collections this work like \ref MDBX_SET_RANGE,
   * but returns \ref MDBX_SUCCESS if the greater key was found or
   * \ref MDBX_NOTFOUND otherwise.
   *
   * For DUPSORT-ed a data value is taken into account for duplicates,
   * i.e. for a pairs/tuples of a key and an each data value of duplicates.
   * Returns \ref MDBX_SUCCESS if the greater pair was returned or
   * \ref MDBX_NOTFOUND otherwise.
   */
	SET_UPPERBOUND(MDBX_SET_UPPERBOUND),

	/** Doubtless cursor positioning at a specified key. */
	TO_KEY_LESSER_THAN(MDBX_TO_KEY_LESSER_THAN),
	/** See {@link CursorOp#TO_KEY_LESSER_THAN}. */
	TO_KEY_LESSER_OR_EQUAL(MDBX_TO_KEY_LESSER_OR_EQUAL),
	/** See {@link CursorOp#TO_KEY_LESSER_THAN}. */
	TO_KEY_EQUAL(MDBX_TO_KEY_EQUAL),
	/** See {@link CursorOp#TO_KEY_LESSER_THAN}. */
	TO_KEY_GREATER_OR_EQUAL(MDBX_TO_KEY_GREATER_OR_EQUAL),
	/** See {@link CursorOp#TO_KEY_LESSER_THAN}. */
	TO_KEY_GREATER_THAN(MDBX_TO_KEY_GREATER_THAN),

	/** Doubtless cursor positioning at a specified key-value pair for dupsort/multi-value hives. */
	TO_EXACT_KEY_VALUE_LESSER_THAN(MDBX_TO_EXACT_KEY_VALUE_LESSER_THAN),
	/** See {@link CursorOp#TO_EXACT_KEY_VALUE_LESSER_THAN}. */
	TO_EXACT_KEY_VALUE_LESSER_OR_EQUAL(MDBX_TO_EXACT_KEY_VALUE_LESSER_OR_EQUAL),
	/** See {@link CursorOp#TO_EXACT_KEY_VALUE_LESSER_THAN}. */
	TO_EXACT_KEY_VALUE_EQUAL(MDBX_TO_EXACT_KEY_VALUE_EQUAL),
	/** See {@link CursorOp#TO_EXACT_KEY_VALUE_LESSER_THAN}. */
	TO_EXACT_KEY_VALUE_GREATER_OR_EQUAL(MDBX_TO_EXACT_KEY_VALUE_GREATER_OR_EQUAL),
	/** See {@link CursorOp#TO_EXACT_KEY_VALUE_LESSER_THAN}. */
	TO_EXACT_KEY_VALUE_GREATER_THAN(MDBX_TO_EXACT_KEY_VALUE_GREATER_THAN),

	/** Doubtless cursor positioning at a specified key-value pair for dupsort/multi-value hives. */
	TO_PAIR_LESSER_THAN(MDBX_TO_PAIR_LESSER_THAN),
	/** See {@link CursorOp#TO_PAIR_LESSER_THAN}. */
	TO_PAIR_LESSER_OR_EQUAL(MDBX_TO_PAIR_LESSER_OR_EQUAL),
	/** See {@link CursorOp#TO_PAIR_LESSER_THAN}. */
	TO_PAIR_EQUAL(MDBX_TO_PAIR_EQUAL),
	/** See {@link CursorOp#TO_PAIR_LESSER_THAN}. */
	TO_PAIR_GREATER_OR_EQUAL(MDBX_TO_PAIR_GREATER_OR_EQUAL),
	/** See {@link CursorOp#TO_PAIR_LESSER_THAN}. */
	TO_PAIR_GREATER_THAN(MDBX_TO_PAIR_GREATER_THAN),

	/** \ref MDBX_DUPFIXED -only: Seek to given key and return up to a page of
	 * duplicate data items from current cursor position. Move cursor to prepare
	 * for \ref MDBX_NEXT_MULTIPLE. \see MDBX_GET_MULTIPLE
	 */
	SEEK_AND_GET_MULTIPLE(MDBX_SEEK_AND_GET_MULTIPLE),
	;

	private final int value;

	CursorOp(int value) {
		this.value = value;
	}

	/**
	 * Return the constant value associated with the cursor operation
	 * @return associated value
	 */
	public int getValue() {
		return value;
	}
}
