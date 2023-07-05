package com.castortech.mdbxjni.logging;

import org.slf4j.Logger;

/**
 * Taken from https://stackoverflow.com/questions/2621701/setting-log-level-of-message-at-runtime-in-slf4j
 *
 * @author  David Tonhofer (original author)
 * 					Alain Picard
 *
 */
public class LogLevel {
	/**
	 * Allowed levels, as an enum. Import using "import [package].LogLevel.Level" Every logging implementation
	 * has something like this except SLF4J.
	 */
	public enum Level {
		TRACE,
		DEBUG,
		INFO,
		WARN,
		ERROR
	}

	/**
	 * This class cannot be instantiated, why would you want to?
	 */
	private LogLevel() {
		// Unreachable
	}

	/**
	 * Log at the specified level. If the "logger" is null, nothing is logged. If the "level" is null, nothing
	 * is logged. If the "txt" is null, behavior depends on the SLF4J implementation.
	 */
	public static void log(Logger logger, Level level, String txt) {
		if (logger != null && level != null) {
			switch (level) {
			case TRACE:
				logger.trace(txt);
				break;
			case DEBUG:
				logger.debug(txt);
				break;
			case INFO:
				logger.info(txt);
				break;
			case WARN:
				logger.warn(txt);
				break;
			case ERROR:
				logger.error(txt);
				break;
			}
		}
	}

	/**
	 * Log at the specified level. If the "logger" is null, nothing is logged. If the "level" is null, nothing
	 * is logged. If the "format" or the "argArray" are null, behavior depends on the SLF4J-backing
	 * implementation.
	 */
	public static void log(Logger logger, Level level, String format, Object... params) {
		if (logger != null && level != null) {
			switch (level) {
			case TRACE:
				logger.trace(format, params);
				break;
			case DEBUG:
				logger.debug(format, params);
				break;
			case INFO:
				logger.info(format, params);
				break;
			case WARN:
				logger.warn(format, params);
				break;
			case ERROR:
				logger.error(format, params);
				break;
			}
		}
	}

	/**
	 * Log at the specified level, with a Throwable on top. If the "logger" is null, nothing is logged. If the
	 * "level" is null, nothing is logged. If the "format" or the "argArray" or the "throwable" are null,
	 * behavior depends on the SLF4J-backing implementation.
	 */
	public static void log(Logger logger, Level level, String txt, Throwable throwable) {
		if (logger != null && level != null) {
			switch (level) {
			case TRACE:
				logger.trace(txt, throwable);
				break;
			case DEBUG:
				logger.debug(txt, throwable);
				break;
			case INFO:
				logger.info(txt, throwable);
				break;
			case WARN:
				logger.warn(txt, throwable);
				break;
			case ERROR:
				logger.error(txt, throwable);
				break;
			}
		}
	}

	/**
	 * Check whether a SLF4J logger is enabled for a certain loglevel. If the "logger" or the "level" is null,
	 * false is returned.
	 */
	public static boolean isEnabledFor(Logger logger, Level level) {
		boolean res = false;
		if (logger != null && level != null) {
			switch (level) {
			case TRACE:
				res = logger.isTraceEnabled();
				break;
			case DEBUG:
				res = logger.isDebugEnabled();
				break;
			case INFO:
				res = logger.isInfoEnabled();
				break;
			case WARN:
				res = logger.isWarnEnabled();
				break;
			case ERROR:
				res = logger.isErrorEnabled();
				break;
			}
		}
		return res;
	}
}