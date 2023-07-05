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
 * Log levelsCursor Get operations.
 *
 * @author Alain Picard
 */
public enum DebugFlags {
	/** None */
	NONE(MDBX_DBG_NONE),

	/**
	 * Enable assertion checks.
	 * <p>
	 * <b>Note</b>
	 * </p>
	 * Always enabled for builds with MDBX_FORCE_ASSERTIONS option, otherwise requires build with MDBX_DEBUG > 0
	 */
	ASSERT(MDBX_DBG_ASSERT),

	/**
	 * Enable pages usage audit at commit transactions.
	 * <p>
	 * <b>Note</b>
	 * </p>Requires build with MDBX_DEBUG > 0
	 */
	AUDIT(MDBX_DBG_AUDIT),

	/**
	 * Enable small random delays in critical points.
	 * <p>
	 * <b>Note</b>
	 * </p>Requires build with MDBX_DEBUG > 0
	 */
	JITTER(MDBX_DBG_JITTER),

	/**
	 * Include or not meta-pages in coredump files.
	 * <p>
	 * <b>Note</b>
	 * </p>May affect performance in MDBX_WRITEMAP mode
	 */
	DUMP(MDBX_DBG_DUMP),

	/**
	 * Allow multi-opening environment(s)
	 */
	LEGACY_MULTIOPEN(MDBX_DBG_LEGACY_MULTIOPEN),

	/**
	 * Allow read and write transactions overlapping for the same thread.
	 */
	LEGACY_OVERLAP(MDBX_DBG_LEGACY_OVERLAP),

	/**
	 * Don't auto-upgrade format signature.
	 * <p>
	 * <b>Note</b>
	 * </p>However a new write transactions will use and store the last signature regardless this flag
	 */
	DONT_UPGRADE(MDBX_DBG_DONT_UPGRADE),

	/**
	 * or mdbx_setup_debug() only: Don't change current settings
	 */
	DONT_CHANGE(MDBX_DBG_DONTCHANGE),
	;

	private final int value;

	DebugFlags(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static DebugFlags getByValue(int value) {
		for (DebugFlags level : values()) {
			if (value == level.getValue()) {
				return level;
			}
		}

		throw new IllegalStateException("Invalid Log level:" + value); //$NON-NLS-1$
	}
}