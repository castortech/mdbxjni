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

import static com.castortech.mdbxjni.JNI.*;

/**
 * @author Alain Picard
 */
public class EnvOptions {
	//====================================================//
	// Environment Options
	//====================================================//
	public static final int OPT_MAX_DBS                      = MDBX_opt_max_db                         ;
	public static final int OPT_MAX_READERS                  = MDBX_opt_max_readers                    ;
	public static final int OPT_SYNC_BYTES                   = MDBX_opt_sync_bytes                     ;
	public static final int OPT_SYNC_PERIOD                  = MDBX_opt_sync_period                    ;
	public static final int OPT_RP_ARG_LIMIT                 = MDBX_opt_rp_augment_limit               ;
	public static final int OPT_LOOSE_LIMIT                  = MDBX_opt_loose_limit                    ;
	public static final int OPT_DB_RESERVE_LIMIT             = MDBX_opt_dp_reserve_limit               ;
	public static final int OPT_TXN_DP_LIMIT                 = MDBX_opt_txn_dp_limit                   ;
	public static final int OPT_TXN_DP_INITIAL               = MDBX_opt_txn_dp_initial                 ;
	public static final int OPT_SPILL_MAX_DENOM              = MDBX_opt_spill_max_denominator          ;
	public static final int OPT_SPILL_MIN_DENOM              = MDBX_opt_spill_min_denominator          ;
	public static final int OPT_SPILL_P4C_DENOM              = MDBX_opt_spill_parent4child_denominator ;
	public static final int OPT_MERGE_THRESH_PERC            = MDBX_opt_merge_threshold_16dot16_percent;
	public static final int OPT_WRTIE_THRU_THRESH            = MDBX_opt_writethrough_threshold         ;
	public static final int OPT_PREFAULT_WRITE_ENABLE        = MDBX_opt_prefault_write_enable          ;
	public static final int OPT_GC_TIME_LIMIT                = MDBX_opt_gc_time_limit                  ;
	public static final int OPT_PREFER_WAF_INSTEADOF_BALANCE = MDBX_opt_prefer_waf_insteadof_balance   ;
	public static final int OPT_SUBPAGE_LIMIT                = MDBX_opt_subpage_limit                  ;
	public static final int OPT_SUBPAGE_ROOM_THRESHOLD       = MDBX_opt_subpage_room_threshold         ;
	public static final int OPT_SUBPAGE_RESERVE_PREREQ       = MDBX_opt_subpage_reserve_prereq         ;
	public static final int OPT_SUBPAGE_RESERVE_LIMIT        = MDBX_opt_subpage_reserve_limit          ;
}