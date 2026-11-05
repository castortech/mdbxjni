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
 * This class holds all the native constant, structure and function mappings.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@SuppressWarnings( {"java:S1444", "java:S3008", "java:S101", "java:S116", "java:S117", "squid:S00100" })
public class JNIIntern {
	public static int CURSOR_STACK_SIZE;

//	//====================================================//
//	// Db
//	//====================================================//
//	@JniClass(flags = {STRUCT, TYPEDEF})
//	public static class MDBX_db {
//		@JniField(cast = "uint16_t")
//		public int md_flags;
//		@JniField(cast = "uint16_t")
//		public int md_depth;
//		@JniField(cast = "uint32_t")
//		public int md_xsize;
//		@JniField(cast = "uint32_t")  //coded as pgno_t
//		public int md_root;
//		@JniField(cast = "uint32_t")  //coded as pgno_t
//		public int md_branch_pages;
//		@JniField(cast = "uint32_t")  //coded as pgno_t
//		public int md_leaf_pages;
//		@JniField(cast = "uint32_t")  //coded as pgno_t
//		public int md_overflow_pages;
//		@JniField(cast = "uint64_t")
//		public long md_seq;
//		@JniField(cast = "uint64_t")
//		public long md_entries;
//		@JniField(cast = "uint64_t")
//		public long md_mod_txnid;
//	}

	//====================================================//
	// Auxiliary Db
	//====================================================//
//	/**
//	 * Auxiliary DB info.
//	 * The information here is mostly static/read-only. There is
//	 * only a single copy of this record in the environment.
//	 */
//	@JniClass(flags = {STRUCT, TYPEDEF})
//	public static class MDBX_dbx {
//		/** name of the database */
//		@JniField(cast = "MDBX_val")
//		public JNI.MDBX_val md_name;
//		/** function for comparing keys */
//		@JniField(cast = "MDBX_cmp_func *")
//		long md_cmp;
//		/** function for comparing data items */
//		@JniField(cast = "MDBX_cmp_func *")
//		long md_dcmp;
//		/** min key length for the database */
//		@JniField(cast = "size_t")
//		public long md_klen_min;
//		/** max key length for the database */
//		@JniField(cast = "size_t")
//		/** min value/data length for the database */
//		public long md_klen_max;
//		/** max value/data length for the database */
//		@JniField(cast = "size_t")
//		public long md_vlen_min;
//		@JniField(cast = "size_t")
//		public long md_vlen_max;
//	}

	//====================================================//
	// Cursor
	//====================================================//
	/**
	 * Cursors are used for all DB operations.
	 * A cursor holds a path of (page pointer, key index) from the DB
	 * root to a position in the DB, plus other state. MDBX_DUPSORT
	 * cursors include an xcursor to the current data item. Write txns
	 * track their cursors and keep them up to date when data moves.
	 * Exception: An xcursor's pointer to a P_SUBP page can be stale.
	 * (A node with F_DUPDATA but no F_SUBDATA contains a subpage). */
	public static class MDBX_cursor {
		public int signature;
		public short top;
		public short flags;
		/** Cursor checking flags. */
		public short checking;
		/** The mt_dbistate for this database */
		public short dbi_state;
		/** The transaction that owns this cursor */
		public long txn;
		/* Points to tree->dbs[] for this cursor's DBI. */
		public long tree;
		/* Points to env->kvs[] for this cursor's DBI. */
		public long clc;
		public long subcur;
		/** stack of pushed pages */
		public long[] pg;
		/** stack of page indices */
		public int[] ki;
		/** Next cursor on this DB in this txn */
		public long next;
		/** Backup of the original cursor if this cursor is a shadow */
		public long backup;
	}

	public static int SIZEOF_CURSOR;
}