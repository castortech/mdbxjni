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
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Constants {
	//====================================================//
	// Environment Flags
	//====================================================//
	public static final int ENVDEFAULTS = MDBX_ENV_DEFAULTS;
	public static final int NOSUBDIR    = MDBX_NOSUBDIR    ;
	public static final int RDONLY      = MDBX_RDONLY      ;
	public static final int EXCLUSIVE   = MDBX_EXCLUSIVE   ;
	public static final int ACCEDE      = MDBX_ACCEDE      ;
	public static final int WRITEMAP    = MDBX_WRITEMAP    ;
	public static final int NOTLS       = MDBX_NOTLS       ;
	public static final int NORDAHEAD 	= MDBX_NORDAHEAD	 ;
	public static final int NOMEMINIT   = MDBX_NOMEMINIT   ;
	public static final int COALESCE 		= MDBX_COALESCE		 ;
	public static final int LIFORECLAIM = MDBX_LIFORECLAIM ;
	public static final int PAGEPERTURB = MDBX_PAGEPERTURB ;
	public static final int SYNCDURABLE = MDBX_SYNC_DURABLE;
	public static final int NOMETASYNC  = MDBX_NOMETASYNC  ;
	public static final int SAFENOSYNC  = MDBX_SAFE_NOSYNC ;
	public static final int MAPASYNC    = MDBX_MAPASYNC    ;
	public static final int UTTERLY_NOSYNC = MDBX_UTTERLY_NOSYNC;

	//====================================================//
	// Database Flags
	//====================================================//
	public static final int DBDEFAULTS  = MDBX_DB_DEFAULTS ;
	public static final int REVERSEKEY  = MDBX_REVERSEKEY  ;
	public static final int DUPSORT     = MDBX_DUPSORT     ;
	public static final int INTEGERKEY  = MDBX_INTEGERKEY  ;
	public static final int DUPFIXED    = MDBX_DUPFIXED    ;
	public static final int INTEGERDUP  = MDBX_INTEGERDUP  ;
	public static final int REVERSEDUP  = MDBX_REVERSEDUP  ;
	public static final int CREATE      = MDBX_CREATE      ;
	public static final int DBACCEDE    = MDBX_DB_ACCEDE   ;

	//====================================================//
	// Write Flags
	//====================================================//
	public static final int UPSERT      = MDBX_UPSERT      ;
	public static final int NOOVERWRITE = MDBX_NOOVERWRITE ;
	public static final int NODUPDATA   = MDBX_NODUPDATA   ;
	public static final int CURRENT     = MDBX_CURRENT     ;
	public static final int RESERVE     = MDBX_RESERVE     ;
	public static final int APPEND      = MDBX_APPEND      ;
	public static final int APPENDDUP   = MDBX_APPENDDUP   ;
	public static final int MULTIPLE    = MDBX_MULTIPLE    ;

	// ====================================================//
	// Cursor Operations
	// ====================================================//
	public static final CursorOp FIRST          = CursorOp.FIRST          ;
	public static final CursorOp FIRST_DUP      = CursorOp.FIRST_DUP      ;
	public static final CursorOp GET_BOTH       = CursorOp.GET_BOTH       ;
	public static final CursorOp GET_BOTH_RANGE = CursorOp.GET_BOTH_RANGE ;
	public static final CursorOp GET_CURRENT    = CursorOp.GET_CURRENT    ;
	public static final CursorOp GET_MULTIPLE   = CursorOp.GET_MULTIPLE   ;
	public static final CursorOp LAST           = CursorOp.LAST           ;
	public static final CursorOp LAST_DUP       = CursorOp.LAST_DUP       ;
	public static final CursorOp NEXT           = CursorOp.NEXT           ;
	public static final CursorOp NEXT_DUP       = CursorOp.NEXT_DUP       ;
	public static final CursorOp NEXT_MULTIPLE  = CursorOp.NEXT_MULTIPLE  ;
	public static final CursorOp NEXT_NODUP     = CursorOp.NEXT_NODUP     ;
	public static final CursorOp PREV           = CursorOp.PREV           ;
	public static final CursorOp PREV_DUP       = CursorOp.PREV_DUP       ;
	public static final CursorOp PREV_NODUP     = CursorOp.PREV_NODUP     ;
	public static final CursorOp SET	          = CursorOp.SET	          ;
	public static final CursorOp KEY            = CursorOp.SET_KEY        ;
	public static final CursorOp RANGE          = CursorOp.SET_RANGE      ;
	public static final CursorOp LOWERBOUND     = CursorOp.SET_LOWERBOUND ;

	public static final int IMMUTABLE_KEY = 0x2;  //used for secondary db

//public static final MDBX_build_info mdbx_build;

	public static byte[] bytes(String value) {
		if (value == null) {
			return null;
		}
		return value.getBytes(StandardCharsets.UTF_8);
	}

	public static String string(byte[] value) {
		if (value == null) {
			return null;
		}
		return new String(value, StandardCharsets.UTF_8);
	}
}