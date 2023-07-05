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

import org.slf4j.Logger;

import com.castortech.mdbxjni.logging.LogLevel;
import com.castortech.mdbxjni.logging.LogLevel.Level;;

/**
 * Log levelsCursor Get operations.
 *
 * @author Alain Picard
 */
public enum MdbxLogLevel {
	/** Critical conditions, i.e. assertion failures */
	FATAL(MDBX_LOG_FATAL, Level.ERROR),

	/**
	 * Enables logging for error conditions
	 * and MDBX_LOG_FATAL
	 */
	ERROR(MDBX_LOG_ERROR, Level.ERROR),

	/**
	 * Enables logging for warning conditions
	 * and MDBX_LOG_ERROR ... MDBX_LOG_FATAL
	 */
	WARN(MDBX_LOG_WARN, Level.WARN),

	/**
	 * Enables logging for normal but significant condition
	 * and MDBX_LOG_WARN ... MDBX_LOG_FATAL
	 */
	NOTICE(MDBX_LOG_NOTICE, Level.INFO),

	/**
	 * Enables logging for verbose informational
   * and MDBX_LOG_NOTICE ... MDBX_LOG_FATAL
	 */
	VERBOSE(MDBX_LOG_VERBOSE, Level.INFO),

	/**
	 * Enables logging for debug-level messages
   * and MDBX_LOG_VERBOSE ... MDBX_LOG_FATAL.
	 */
	DEBUG(MDBX_LOG_DEBUG, Level.DEBUG),

	/**
	 * Enables logging for trace debug-level messages
   * and MDBX_LOG_DEBUG ... MDBX_LOG_FATAL.
	 */
	TRACE(MDBX_LOG_TRACE, Level.TRACE),

	/**
	 * Enables extra debug-level messages (dump pgno lists)
   * and all other log-messages.
	 */
	EXTRA(MDBX_LOG_EXTRA, Level.TRACE),

	/**
	 * for mdbx_setup_debug() only: Don't change current settings (= -1)
	 */
	DONT_CHANGE(MDBX_LOG_DONTCHANGE, Level.TRACE),
	;

	private final int value;
	private final Level level;

	MdbxLogLevel(int value, Level level) {
		this.value = value;
		this.level = level;
	}

	public int getValue() {
		return value;
	}

	public static MdbxLogLevel getByValue(int value) {
		for (MdbxLogLevel level : values()) {
			if (value == level.getValue()) {
				return level;
			}
		}

		throw new IllegalStateException("Invalid Log level:" + value); //$NON-NLS-1$
	}

	public static void log(int value, Logger logger, String format, Object... params) {
		MdbxLogLevel mdbxLogLevel = MdbxLogLevel.getByValue(value);
		if (LogLevel.isEnabledFor(logger, mdbxLogLevel.level)) {
			LogLevel.log(logger, mdbxLogLevel.level, format, params);
		}
	}

	public static int getMaxValue() {
		return 7; //from mdbx source represents MDBX_LOG_EXTRA
	}
}