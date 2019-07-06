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

import org.fusesource.hawtjni.runtime.*;

import static org.fusesource.hawtjni.runtime.ClassFlag.STRUCT;
import static org.fusesource.hawtjni.runtime.ClassFlag.TYPEDEF;
import static org.fusesource.hawtjni.runtime.FieldFlag.CONSTANT;
import static org.fusesource.hawtjni.runtime.MethodFlag.CONSTANT_GETTER;
import static org.fusesource.hawtjni.runtime.MethodFlag.CONSTANT_INITIALIZER;
import static org.fusesource.hawtjni.runtime.ArgFlag.*;

/**
 * This class holds all the native constant, structure and function mappings.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@JniClass
public class JNI {
	public static final Library DB_LIB = new Library("mdbx", JNI.class); //$NON-NLS-1$
	public static final Library JNI_LIB = new Library("mdbxjni", JNI.class); //$NON-NLS-1$

	static {
		JNI.DB_LIB.load();
		JNI.JNI_LIB.load();
		init();
	}

	@JniMethod(flags = {CONSTANT_INITIALIZER})
	private static final native void init();

	///////////////////////////////////////////////////////////////////////
	//
	// Posix APIs:
	//
	///////////////////////////////////////////////////////////////////////

	@JniMethod(flags={CONSTANT_GETTER})
	public static final native int errno();

	@JniMethod(cast = "char *")
	public static final native long strerror(int errnum);

	public static final native int strlen(@JniArg(cast = "const char *")long s);

	@JniMethod(cast = "void *")
	public static final native long malloc(@JniArg(cast = "size_t") long size);

	public static final native void free(@JniArg(cast = "void *") long self);

	///////////////////////////////////////////////////////////////////////
	//
	// Additional Helpers
	//
	///////////////////////////////////////////////////////////////////////
	public static final native void buffer_copy (
			@JniArg(cast = "const void *", flags={NO_OUT, CRITICAL}) byte[] src,
			@JniArg(cast = "size_t") long srcPos,
			@JniArg(cast = "void *") long dest,
			@JniArg(cast = "size_t") long destPos,
			@JniArg(cast = "size_t") long length);

	public static final native void buffer_copy (
			@JniArg(cast = "const void *") long src,
			@JniArg(cast = "size_t") long srcPos,
			@JniArg(cast = "void *", flags={NO_IN, CRITICAL}) byte[] dest,
			@JniArg(cast = "size_t") long destPos,
			@JniArg(cast = "size_t") long length);

	///////////////////////////////////////////////////////////////////////
	//
	// The mdbx API
	//
	///////////////////////////////////////////////////////////////////////

	//====================================================//
	// Version Info
	//====================================================//
	@JniField(flags = { CONSTANT })
	static public int MDBX_VERSION_MAJOR;
	@JniField(flags = { CONSTANT })
	static public int MDBX_VERSION_MINOR;

//	@JniClass(flags = {STRUCT})
//	public static class git {
//		public String datetime;
//		public String tree;
//		public String commit;
//		public String describe;
//
//		@SuppressWarnings("nls")
//		@Override
//		public String toString() {
//			return "{" +
//					"datetime=" + datetime +
//					", tree=" + tree +
//					", commit=" + commit +
//					", describe=" + describe +
//					'}';
//		}
//	}
    
	@JniClass(flags = {STRUCT, TYPEDEF})
	public static class mdbx_version_info {
		@JniField(cast = "uint8_t")
		public short major;
		@JniField(cast = "uint8_t")
		public short minor;
		@JniField(cast = "uint16_t")
		public int release;
		@JniField(cast = "uint16_t")
		public int revision;

		@JniField(accessor="git.datetime", cast = "char *") 
		public char[] git_datetime;
		@JniField(accessor="git.tree", cast = "char *") 
		public char[] git_tree;
		@JniField(accessor="git.commit", cast = "char *") 
		public char[] git_commit;
		@JniField(accessor="git.describe", cast = "char *") 
		public char[] git_describe;
		
//		@JniField(cast = "mdbx_version_info.git *")
//		public git git = new git(); 

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
					"major=" + major +
					", minor=" + minor +
					", release=" + release +
					", revision=" + revision +
//					", git=" + git +
					'}';
		}
		
		@SuppressWarnings("nls")
		public String getVersionString() {
			return "" + major + '.' + minor + '.' + release + '.' + revision;
		}
	}
	
//	@JniField(flags = { CONSTANT })
//	static public mdbx_version_info mdbx_version;
	
	//====================================================//
	// Environment Flags
	//====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_NOSUBDIR;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NOSYNC;
	@JniField(flags = { CONSTANT })
	public static int MDBX_RDONLY;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NOMETASYNC;
	@JniField(flags = { CONSTANT })
	public static int MDBX_WRITEMAP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_MAPASYNC;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NOTLS;
//	@JniField(flags = { CONSTANT })
//	public static int MDBX_EXCLUSIVE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NORDAHEAD;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NOMEMINIT;
	@JniField(flags = { CONSTANT })
	public static int MDBX_COALESCE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LIFORECLAIM;
	@JniField(flags = { CONSTANT })
	public static int MDBX_UTTERLY_NOSYNC;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PAGEPERTURB;

	// ====================================================//
	// Database Flags
	// ====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_REVERSEKEY;
	@JniField(flags = { CONSTANT })
	public static int MDBX_DUPSORT;
	@JniField(flags = { CONSTANT })
	public static int MDBX_INTEGERKEY;
	@JniField(flags = { CONSTANT })
	public static int MDBX_DUPFIXED;
	@JniField(flags = { CONSTANT })
	public static int MDBX_INTEGERDUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_REVERSEDUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_CREATE;

	// ====================================================//
	// Transaction Flags
	// ====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_TRYTXN;
    
	// ====================================================//
	// Copy Flags
	// ====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_CP_COMPACT;

	// ====================================================//
	// Write Flags
	// ====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_NOOVERWRITE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NODUPDATA;
	@JniField(flags = { CONSTANT })
	public static int MDBX_CURRENT;
	@JniField(flags = { CONSTANT })
	public static int MDBX_RESERVE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_APPEND;
	@JniField(flags = { CONSTANT })
	public static int MDBX_APPENDDUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_MULTIPLE;

	// ====================================================//
	// enum MDBX_cursor_op:
	// ====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_FIRST;
	@JniField(flags = { CONSTANT })
	public static int MDBX_FIRST_DUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_GET_BOTH;
	@JniField(flags = { CONSTANT })
	public static int MDBX_GET_BOTH_RANGE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_GET_CURRENT;
	@JniField(flags = { CONSTANT })
	public static int MDBX_GET_MULTIPLE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LAST;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LAST_DUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NEXT;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NEXT_DUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NEXT_MULTIPLE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NEXT_NODUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PREV;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PREV_DUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PREV_NODUP;
	@JniField(flags = { CONSTANT })
	public static int MDBX_SET;
	@JniField(flags = { CONSTANT })
	public static int MDBX_SET_KEY;
	@JniField(flags = { CONSTANT })
	public static int MDBX_SET_RANGE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PREV_MULTIPLE;

	// ====================================================//
	// Return Codes
	// ====================================================//
	@JniField(flags = { CONSTANT })
	public static int MDBX_EINVAL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EACCESS;
	@JniField(flags = { CONSTANT })
	public static int MDBX_ENODATA;
	@JniField(flags = { CONSTANT })
	public static int MDBX_ENOMEM;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EROFS;
	@JniField(flags = { CONSTANT })
	public static int MDBX_ENOSYS;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EIO;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EPERM;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EINTR;
	@JniField(flags = { CONSTANT })
	public static int MDBX_SUCCESS;
	@JniField(flags = { CONSTANT })
	public static int MDBX_KEYEXIST;
	@JniField(flags = { CONSTANT })
	public static int MDBX_NOTFOUND;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PAGE_NOTFOUND;
	@JniField(flags = { CONSTANT })
	public static int MDBX_CORRUPTED;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PANIC;
	@JniField(flags = { CONSTANT })
	public static int MDBX_VERSION_MISMATCH;
	@JniField(flags = { CONSTANT })
	public static int MDBX_INVALID;
	@JniField(flags = { CONSTANT })
	public static int MDBX_MAP_FULL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_DBS_FULL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_READERS_FULL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TXN_FULL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_CURSOR_FULL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PAGE_FULL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_MAP_RESIZED;
	@JniField(flags = { CONSTANT })
	public static int MDBX_INCOMPATIBLE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_BAD_RSLOT;
	@JniField(flags = { CONSTANT })
	public static int MDBX_BAD_TXN;
	@JniField(flags = { CONSTANT })
	public static int MDBX_BAD_VALSIZE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_BAD_DBI;
	@JniField(flags = { CONSTANT })
	public static int MDBX_PROBLEM;
	@JniField(flags = { CONSTANT })
	public static int MDBX_BUSY;
	@JniField(flags = { CONSTANT })
	public static int MDBX_LAST_ERRCODE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EMULTIVAL;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EBADSIGN;
	@JniField(flags = { CONSTANT })
	public static int MDBX_WANNA_RECOVERY;
	@JniField(flags = { CONSTANT })
	public static int MDBX_EKEYMISMATCH;
	@JniField(flags = { CONSTANT })
	public static int MDBX_TOO_LARGE;
	@JniField(flags = { CONSTANT })
	public static int MDBX_THREAD_MISMATCH;
    
//	@JniClass(flags = {STRUCT})
//	public static class MiGeo {
//		@JniField(cast = "uint64_t")
//		public long lower;		/* lower limit for datafile size */
//		@JniField(cast = "uint64_t")
//		public long upper;  	/* upper limit for datafile size */
//		@JniField(cast = "uint64_t")
//		public long current; 	/* current datafile size */
//		@JniField(cast = "uint64_t")
//		public long shrink;  	/* shrink treshold for datafile */
//		@JniField(cast = "uint64_t")
//		public long grow;    	/* growth step for datafile */
//
//		@SuppressWarnings("nls")
//		@Override
//		public String toString() {
//			return "{" +
//					"lower=" + lower +
//					", upper=" + upper +
//					", current=" + current +
//					", shrink=" + shrink +
//					", grow=" + grow +
//					'}';
//		}
//	}
    
	@JniClass(flags = {STRUCT, TYPEDEF})
	public static class MDBX_envinfo {
//		public MiGeo mi_geo = new MiGeo(); 
		@JniField(accessor="mi_geo.lower", cast = "uint64_t")
		public long mi_geo_lower;		/* lower limit for datafile size */
		@JniField(accessor="mi_geo.upper", cast = "uint64_t")
		public long mi_geo_upper;  	/* upper limit for datafile size */
		@JniField(accessor="mi_geo.current", cast = "uint64_t")
		public long mi_geo_current; 	/* current datafile size */
		@JniField(accessor="mi_geo.shrink", cast = "uint64_t")
		public long mi_geo_shrink;  	/* shrink treshold for datafile */
		@JniField(accessor="mi_geo.grow", cast = "uint64_t")
		public long mi_geo_grow;    	/* growth step for datafile */
		
		@JniField(cast = "uint64_t")
		public long mi_mapsize;							/* Size of the data memory map */	
		@JniField(cast = "uint64_t")
		public long mi_last_pgno;						/* ID of the last used page */
		@JniField(cast = "uint64_t")
		public long mi_recent_txnid;					/* ID of the last committed transaction */
		@JniField(cast = "uint64_t")
		public long mi_latter_reader_txnid; 	/* ID of the last reader transaction */
		@JniField(cast = "uint64_t")
		public long mi_self_latter_reader_txnid; /* ID of the last reader transaction of caller process */
		@JniField(cast = "uint64_t")
		public long mi_meta0_txnid;
		@JniField(cast = "uint64_t")
		public long mi_meta0_sign;
		@JniField(cast = "uint64_t")
		public long mi_meta1_txnid;
		@JniField(cast = "uint64_t")
		public long mi_meta1_sign;
		@JniField(cast = "uint64_t")
		public long mi_meta2_txnid;
		@JniField(cast = "uint64_t")
		public long mi_meta2_sign;
		@JniField(cast = "uint32_t")
		public long mi_maxreaders; /* max reader slots in the environment */
		@JniField(cast = "uint32_t")
		public long mi_numreaders; /* max reader slots used in the environment */
		@JniField(cast = "uint32_t")
		public long mi_dxb_pagesize; /* database pagesize */
		@JniField(cast = "uint32_t")
		public long mi_sys_pagesize; /* system pagesize */

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
//					"mi_geo=" + mi_geo +
					", mi_mapsize=" + mi_mapsize +
					", mi_last_pgno=" + mi_last_pgno +
					", mi_recent_txnid=" + mi_recent_txnid +
					", mi_latter_reader_txnid=" + mi_latter_reader_txnid +
					", mi_self_latter_reader_txnid=" + mi_self_latter_reader_txnid +
					", mi_meta0_txnid=" + mi_meta0_txnid +
					", mi_meta0_sign=" + mi_meta0_sign +
					", mi_meta1_txnid=" + mi_meta1_txnid +
					", mi_meta1_sign=" + mi_meta1_sign +
					", mi_meta2_txnid=" + mi_meta2_txnid +
					", mi_meta2_sign=" + mi_meta2_sign +
					", mi_maxreaders=" + mi_maxreaders +
					", mi_numreaders=" + mi_numreaders +
					", mi_dxb_pagesize=" + mi_dxb_pagesize +
					", mi_sys_pagesize=" + mi_sys_pagesize +
					'}';
		}
	}

	@JniField(accessor="sizeof(struct MDBX_envinfo)", flags={CONSTANT})
  public static int SIZEOF_ENVINFO;
	
	@JniClass(flags = {STRUCT, TYPEDEF})
	public static class MDBX_stat {
		@JniField(cast = "uint32_t")
		public long ms_psize;
		@JniField(cast = "uint32_t")
		public long ms_depth;
		@JniField(cast = "uint64_t")
		public long ms_branch_pages;
		@JniField(cast = "uint64_t")
		public long ms_leaf_pages;
		@JniField(cast = "uint64_t")
		public long ms_overflow_pages;
		@JniField(cast = "uint64_t")
		public long ms_entries;
  	 
		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "{" +
					"ms_branch_pages=" + ms_branch_pages +
					", ms_psize=" + ms_psize +
					", ms_depth=" + ms_depth +
					", ms_leaf_pages=" + ms_leaf_pages +
					", ms_overflow_pages=" + ms_overflow_pages +
					", ms_entries=" + ms_entries +
					'}';
		}
	}
	
	@JniField(accessor="sizeof(struct MDBX_stat)", flags={CONSTANT})
  public static int SIZEOF_STAT;

	@JniClass(flags = { STRUCT, TYPEDEF })
	public static class MDBX_val {
		@JniField(cast = "void *")
		public long iov_base;
		@JniField(cast = "size_t")
		public long iov_len;
	}
	
  @JniMethod
  public static final native void map_val(
  		@JniArg(cast = "MDBX_val *", flags={NO_OUT}) long in,
  		@JniArg(cast = "MDBX_val *", flags={NO_IN}) MDBX_val out);

	@JniMethod(cast = "char *")
	public static final native long mdbx_strerror(int err);

	@JniMethod
	public static final native int mdbx_env_create(
			@JniArg(cast = "MDBX_env **", flags={NO_IN}) long[] env);

	@JniMethod
	public static final native int mdbx_env_open(
			@JniArg(cast = "MDBX_env *") long env,
			@JniArg(cast = "const char *") String path,
			@JniArg(cast = "unsigned") int flags,
			@JniArg(cast = "mode_t") int mode);

	@JniMethod
	public static final native int mdbx_env_copy(
			@JniArg(cast = "MDBX_env *") long env,
			@JniArg(cast = "const char *") String path,
			@JniArg(cast = "unsigned") int flags);
	
  @JniMethod
  public static final native int mdbx_env_copy2fd(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "mdbx_filehandle_t") long fd,
  		@JniArg(cast = "unsigned int") int flags);

  @JniMethod
  public static final native int mdbx_env_stat(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "MDBX_stat *", flags = {NO_IN}) MDBX_stat stat,
			@JniArg(cast = "size_t") long bytes);

  @JniMethod
  public static final native int mdbx_env_info(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "MDBX_envinfo *", flags = {NO_IN}) MDBX_envinfo stat,
			@JniArg(cast = "size_t") long bytes);

  @JniMethod
  public static final native int mdbx_env_sync(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "int") int force);

  @JniMethod
  public static final native int mdbx_env_close(
  		@JniArg(cast = "MDBX_env *") long env);

  @JniMethod
  public static final native int mdbx_env_set_flags(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "unsigned") int flags,
  		@JniArg(cast = "int") int onoff);

  @JniMethod
  public static final native int mdbx_env_get_flags(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "unsigned *") long[] flags);

  @JniMethod
  public static final native int mdbx_env_get_path(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "const char **", flags={NO_IN}) long[] path);

  @JniMethod
  public static final native int mdbx_env_get_fd(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "mdbx_filehandle_t *") long[] fd);
  
  @JniMethod
  public static final native int mdbx_env_set_mapsize(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "size_t") long size);

  @JniMethod
  public static final native int mdbx_env_set_geometry(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "intptr_t") long size_lower,
  		@JniArg(cast = "intptr_t") long size_now,
  		@JniArg(cast = "intptr_t") long size_upper,
  		@JniArg(cast = "intptr_t") long growth_step,
  		@JniArg(cast = "intptr_t") long shrink_threshold,
  		@JniArg(cast = "intptr_t") long pagesize);

  @JniMethod
  public static final native int mdbx_env_set_maxreaders(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "unsigned") long readers);

  @JniMethod
  public static final native int mdbx_env_get_maxreaders(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "unsigned *") long[] readers);

  @JniMethod
  public static final native int mdbx_env_set_maxdbs(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "uint32_t") long dbs);

  @JniMethod
  public static final native int mdbx_env_get_maxkeysize(
  		@JniArg(cast = "MDBX_env *") long env);

  @JniMethod
  public static final native int mdbx_env_set_userctx(
  		@JniArg(cast = "MDBX_env *") long env,
			@JniArg(cast = "void *") long ctx);

  @JniMethod(cast = "void *")
  public static final native long mdbx_env_get_userctx(
  		@JniArg(cast = "MDBX_env *") long env);

  //TODO: MDBX_assert_func
  
  //TODO: mdbx_env_set_assert
  
  @JniMethod
  public static final native int mdbx_txn_begin(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "MDBX_txn *") long parent,
  		@JniArg(cast = "unsigned") long flags,
  		@JniArg(cast = "MDBX_txn **", flags={NO_IN}) long[] txn);

  @JniMethod(cast = "MDBX_env *")
  public static final native long mdbx_txn_env(
  		@JniArg(cast = "MDBX_txn *") long txn);

  @JniMethod
  public static final native int mdbx_txn_flags(
  		@JniArg(cast = "MDBX_txn *") long txn);

  @JniMethod(cast = "uint64_t")
  public static final native long mdbx_txn_id(
  		@JniArg(cast = "MDBX_txn *") long txn);

  @JniMethod
  public static final native int mdbx_txn_commit(
  		@JniArg(cast = "MDBX_txn *") long txn);

  @JniMethod
  public static final native void mdbx_txn_abort(
  		@JniArg(cast = "MDBX_txn *") long txn);

  @JniMethod
  public static final native void mdbx_txn_reset(
  		@JniArg(cast = "MDBX_txn *") long txn);

  @JniMethod
  public static final native int mdbx_txn_renew(
  		@JniArg(cast = "MDBX_txn *") long txn);

  @JniMethod
  public static final native int mdbx_dbi_open_ex(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "const char *") String name,
  		@JniArg(cast = "unsigned") long flags,
  		@JniArg(cast = "uint32_t *") long[] dbi,
  		@JniArg(cast = "int(*)(const MDBX_val *, const MDBX_val *)", flags = ArgFlag.POINTER_ARG) long keycmp,
  		@JniArg(cast = "int(*)(const MDBX_val *, const MDBX_val *)", flags = ArgFlag.POINTER_ARG) long datacmp);

  @JniMethod
  public static final native int mdbx_dbi_open(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "const char *") String name,
  		@JniArg(cast = "unsigned") long flags,
  		@JniArg(cast = "uint32_t *") long[] dbi);

  @JniMethod
  public static final native int mdbx_dbi_stat(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "uint32_t") long dbi,
  		@JniArg(cast = "MDBX_stat *", flags = {NO_IN}) MDBX_stat stat,
  		@JniArg(cast = "size_t") long bytes);

  @JniMethod
  public static final native int mdbx_dbi_flags_ex(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "uint32_t") long dbi,
  		@JniArg(cast = "unsigned *") long[] flags,
  		@JniArg(cast = "unsigned *") long[] state);

  @JniMethod
  public static final native int mdbx_dbi_flags(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "uint32_t") long dbi,
  		@JniArg(cast = "unsigned *") long[] flags);

  @JniMethod
  public static final native void mdbx_dbi_close(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "uint32_t") long dbi);

  @JniMethod
  public static final native int mdbx_drop(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "uint32_t") long dbi,
  		int del);

  @JniMethod
  public static final native int mdbx_get(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "uint32_t") long dbi,
  		@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
  		@JniArg(cast = "MDBX_val *", flags={NO_IN}) MDBX_val data);

  @JniMethod
  public static final native int mdbx_put(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "uint32_t") long dbi,
  		@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
  		@JniArg(cast = "MDBX_val *") MDBX_val data,
  		@JniArg(cast = "unsigned") int flags);

  @JniMethod
  public static final native int mdbx_del(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "uint32_t") long dbi,
  		@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
  		@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val data);

  @JniMethod
  public static final native int mdbx_cursor_open(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "uint32_t") long dbi,
  		@JniArg(cast = "MDBX_cursor **", flags={NO_IN}) long[] cursor);

  @JniMethod
  public static final native void mdbx_cursor_close(
  		@JniArg(cast = "MDBX_cursor *") long cursor);

  @JniMethod
  public static final native int mdbx_cursor_renew(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "MDBX_cursor *") long cursor);

  @JniMethod(cast = "MDBX_txn *")
  public static final native long mdbx_cursor_txn(
  		@JniArg(cast = "MDBX_cursor *") long cursor);

  @JniMethod(cast = "uint32_t")
  public static final native long mdbx_cursor_dbi(
  		@JniArg(cast = "MDBX_cursor *") long cursor);

  @JniMethod
  public static final native int mdbx_cursor_get(
  		@JniArg(cast = "MDBX_cursor *") long cursor,
  		@JniArg(cast = "MDBX_val *") MDBX_val key,
  		@JniArg(cast = "MDBX_val *") MDBX_val data,
  		@JniArg(cast = "MDBX_cursor_op") int op);

  @JniMethod
  public static final native int mdbx_cursor_put(
  		@JniArg(cast = "MDBX_cursor *") long cursor,
  		@JniArg(cast = "MDBX_val *", flags = {NO_OUT}) MDBX_val key,
  		@JniArg(cast = "MDBX_val *", flags = {NO_OUT}) MDBX_val data,
  		@JniArg(cast = "unsigned") int flags);

  @JniMethod
  public static final native int mdbx_cursor_del(
  		@JniArg(cast = "MDBX_cursor *") long cursor,
  		@JniArg(cast = "unsigned") int flags);

  @JniMethod
  public static final native int mdbx_cursor_count(
  		@JniArg(cast = "MDBX_cursor *") long cursor,
  		@JniArg(cast = "size_t *") long[] countp);

  @JniMethod
  public static final native int mdbx_cmp(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "uint32_t") long dbi,
  		@JniArg(cast = "MDBX_val *", flags = {NO_OUT}) MDBX_val a,
  		@JniArg(cast = "MDBX_val *", flags = {NO_OUT}) MDBX_val b);

  @JniMethod
  public static final native int mdbx_dcmp(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "uint32_t") long dbi,
  		@JniArg(cast = "MDBX_val *", flags = {NO_OUT}) MDBX_val a,
  		@JniArg(cast = "MDBX_val *", flags = {NO_OUT}) MDBX_val b);

  //TODO: MDBX_msg_func
  
  //TODO: mdbx_reader_list
  
	@JniMethod
	public static final native int mdbx_reader_check(
			@JniArg(cast = "MDBX_env *") long env,
			@JniArg(cast = "int *") int[] dead);

  //TODO: mdbx_dkey
  
	@JniMethod
	public static final native int mdbx_env_close_ex(
			@JniArg(cast = "MDBX_env *") long env,
			int dont_sync);

  @JniMethod
  public static final native int mdbx_env_set_syncbytes(
  		@JniArg(cast = "MDBX_env *") long env,
  		@JniArg(cast = "size_t") long bytes);

	@JniMethod
	public static final native int mdbx_txn_straggler(
  		@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "int *") int[] percent);

	//TODO: MDBX_oom_func
	
	//TODO: mdbx_env_set_oomfunc
	
	//TODO: MDBX_oom_func

	//TODO: debug and page visit functions

  @JniMethod
  public static final native int mdbx_cursor_eof(
  		@JniArg(cast = "MDBX_cursor *") long cursor);

  @JniMethod
  public static final native int mdbx_cursor_on_first(
  		@JniArg(cast = "MDBX_cursor *") long cursor);

  @JniMethod
  public static final native int mdbx_cursor_on_last(
  		@JniArg(cast = "MDBX_cursor *") long cursor);

  @JniMethod
  public static final native int mdbx_replace(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "uint32_t") long dbi,
  		@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
  		@JniArg(cast = "MDBX_val *") MDBX_val new_data,
  		@JniArg(cast = "MDBX_val *") MDBX_val old_data,
  		@JniArg(cast = "unsigned") int flags);

  @JniMethod
  public static final native int mdbx_get_ex(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "uint32_t") long dbi,
  		@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
  		@JniArg(cast = "MDBX_val *", flags={NO_IN}) MDBX_val data,
  		@JniArg(cast = "size_t *") long values_count);

  @JniMethod
  public static final native int mdbx_is_dirty(
  		@JniArg(cast = "MDBX_txn *") long txn,
			@JniArg(cast = "const void *") long ptr);

  @JniMethod
  public static final native int mdbx_dbi_sequence(
  		@JniArg(cast = "MDBX_txn *") long txn,
  		@JniArg(cast = "uint32_t") long dbi,
  		@JniArg(cast = "uint64_t *") long result,
  		@JniArg(cast = "uint64_t") long increment);

//  @JniMethod
//  public static final native int mdbx_set_compare(
//  		@JniArg(cast = "MDBX_txn *") long txn,
//  		@JniArg(cast = "uint32_t") long dbi,
//  		@JniArg(cast = "MDBX_cmp_func *") long cmp);
//
//  @JniMethod
//  public static final native int mdbx_set_dupsort(
//  		@JniArg(cast = "MDBX_txn *") long txn,
//  		@JniArg(cast = "uint32_t") long dbi,
//  		@JniArg(cast = "MDBX_cmp_func *") long cmp);

}