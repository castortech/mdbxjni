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
public class EnvFlags {
	//====================================================//
	// Environment Flags
	//====================================================//
	public static final int ENVDEFAULTS = MDBX_ENV_DEFAULTS;
	public static final int VALIDATION  = MDBX_VALIDATION  ;
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
}