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

import java.nio.charset.StandardCharsets;

import static com.castortech.mdbxjni.JNI.*;

/**
 * Constants from libmdbx
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Constants {
	private Constants() { }

	//====================================================//
	// Database Flags
	//====================================================//
	/** See {@link JNI#MDBX_DB_DEFAULTS}. */
	public static final int DBDEFAULTS  = MDBX_DB_DEFAULTS ;
	/** See {@link JNI#MDBX_REVERSEKEY}. */
	public static final int REVERSEKEY  = MDBX_REVERSEKEY  ;
	/** See {@link JNI#MDBX_DUPSORT}. */
	public static final int DUPSORT     = MDBX_DUPSORT     ;
	/** See {@link JNI#MDBX_INTEGERKEY}. */
	public static final int INTEGERKEY  = MDBX_INTEGERKEY  ;
	/** See {@link JNI#MDBX_DUPFIXED}. */
	public static final int DUPFIXED    = MDBX_DUPFIXED    ;
	/** See {@link JNI#MDBX_INTEGERDUP}. */
	public static final int INTEGERDUP  = MDBX_INTEGERDUP  ;
	/** See {@link JNI#MDBX_REVERSEDUP}. */
	public static final int REVERSEDUP  = MDBX_REVERSEDUP  ;
	/** See {@link JNI#MDBX_CREATE}. */
	public static final int CREATE      = MDBX_CREATE      ;
	/** See {@link JNI#MDBX_DB_ACCEDE}. */
	public static final int DBACCEDE    = MDBX_DB_ACCEDE   ;

	//====================================================//
	// Write Flags
	//====================================================//
	/** See {@link JNI#MDBX_UPSERT}. */
	public static final int UPSERT      = MDBX_UPSERT      ;
	/** See {@link JNI#MDBX_NOOVERWRITE}. */
	public static final int NOOVERWRITE = MDBX_NOOVERWRITE ;
	/** See {@link JNI#MDBX_NODUPDATA}. */
	public static final int NODUPDATA   = MDBX_NODUPDATA   ;
	/** See {@link JNI#MDBX_CURRENT}. */
	public static final int CURRENT     = MDBX_CURRENT     ;
	/** See {@link JNI#MDBX_ALLDUPS}. */
	public static final int ALLDUPS     = MDBX_ALLDUPS     ;
	/** See {@link JNI#MDBX_RESERVE}. */
	public static final int RESERVE     = MDBX_RESERVE     ;
	/** See {@link JNI#MDBX_APPEND}. */
	public static final int APPEND      = MDBX_APPEND      ;
	/** See {@link JNI#MDBX_APPENDDUP}. */
	public static final int APPENDDUP   = MDBX_APPENDDUP   ;
	/** See {@link JNI#MDBX_MULTIPLE}. */
	public static final int MULTIPLE    = MDBX_MULTIPLE    ;

	// ====================================================//
	// Cursor Operations
	// ====================================================//
	/** See {@link CursorOp#FIRST}. */
	public static final CursorOp FIRST          = CursorOp.FIRST          ;
	/** See {@link CursorOp#FIRST_DUP}. */
	public static final CursorOp FIRST_DUP      = CursorOp.FIRST_DUP      ;
	/** See {@link CursorOp#GET_BOTH}. */
	public static final CursorOp GET_BOTH       = CursorOp.GET_BOTH       ;
	/** See {@link CursorOp#GET_BOTH_RANGE}. */
	public static final CursorOp GET_BOTH_RANGE = CursorOp.GET_BOTH_RANGE ;
	/** See {@link CursorOp#GET_CURRENT}. */
	public static final CursorOp GET_CURRENT    = CursorOp.GET_CURRENT    ;
	/** See {@link CursorOp#GET_MULTIPLE}. */
	public static final CursorOp GET_MULTIPLE   = CursorOp.GET_MULTIPLE   ;
	/** See {@link CursorOp#LAST}. */
	public static final CursorOp LAST           = CursorOp.LAST           ;
	/** See {@link CursorOp#LAST_DUP}. */
	public static final CursorOp LAST_DUP       = CursorOp.LAST_DUP       ;
	/** See {@link CursorOp#NEXT}. */
	public static final CursorOp NEXT           = CursorOp.NEXT           ;
	/** See {@link CursorOp#NEXT_DUP}. */
	public static final CursorOp NEXT_DUP       = CursorOp.NEXT_DUP       ;
	/** See {@link CursorOp#NEXT_MULTIPLE}. */
	public static final CursorOp NEXT_MULTIPLE  = CursorOp.NEXT_MULTIPLE  ;
	/** See {@link CursorOp#NEXT_NODUP}. */
	public static final CursorOp NEXT_NODUP     = CursorOp.NEXT_NODUP     ;
	/** See {@link CursorOp#PREV}. */
	public static final CursorOp PREV           = CursorOp.PREV           ;
	/** See {@link CursorOp#PREV_DUP}. */
	public static final CursorOp PREV_DUP       = CursorOp.PREV_DUP       ;
	/** See {@link CursorOp#PREV_NODUP}. */
	public static final CursorOp PREV_NODUP     = CursorOp.PREV_NODUP     ;
	/** See {@link CursorOp#PREV_MULTIPLE}. */
	public static final CursorOp PREV_MULTIPLE  = CursorOp.PREV_MULTIPLE  ;
	/** See {@link CursorOp#SET}. */
	public static final CursorOp SET            = CursorOp.SET	          ;
	/** See {@link CursorOp#SET_KEY}. */
	public static final CursorOp KEY            = CursorOp.SET_KEY        ;
	/** See {@link CursorOp#SET_RANGE}. */
	public static final CursorOp RANGE          = CursorOp.SET_RANGE      ;
	/** See {@link CursorOp#SET_LOWERBOUND}. */
	public static final CursorOp LOWERBOUND     = CursorOp.SET_LOWERBOUND ;
	/** See {@link CursorOp#SET_UPPERBOUND}. */
	public static final CursorOp UPPERBOUND     = CursorOp.SET_UPPERBOUND ;

	/** used for secondary db */
	public static final int IMMUTABLE_KEY = 0x2;

//public static final MDBX_build_info mdbx_build;

	/**
	 * Converts a string into a byte array for UTF-8
	 * @param value string to convert
	 * @return converted byte array
	 */
	public static byte[] bytes(String value) {
		if (value == null) {
			return null;
		}
		return value.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Convert a byte array into the corresponding string for UTF-8
	 * @param value byte array to convert
	 * @return converted string
	 */
	public static String string(byte[] value) {
		if (value == null) {
			return null;
		}
		return new String(value, StandardCharsets.UTF_8);
	}
}