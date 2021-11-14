/**
 * Copyright (C) 2018, Castor Technologies Inc.
 *
 *		http://www.castortech.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Created on Jun 5, 2007
 *
 */
package com.castortech.mdbxjni;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.Callable;

public class TimeUtils {
	public static final String DFLT_MESSAGE = "Operation completed in:"; //$NON-NLS-1$

	public enum HrsOption {
		NEVER,
		IFNOTZERO,
		ALWAYS
	}

	public static String elapsedSince(Date sinceDate) {
		return elapsedSince(sinceDate, HrsOption.NEVER);
	}

	public static String elapsedSince(Date sinceDate, HrsOption showHrs) {
		Date dt = new Date(System.currentTimeMillis());
		long elapsed = dt.getTime() - sinceDate.getTime();
		return timeAsString(elapsed, showHrs);
	}

	public static String elapsedSince(long sinceMillis) {
		return elapsedSince(new Date(sinceMillis));
	}

	public static String elapsedSince(long sinceMillis, HrsOption showHrs) {
		return elapsedSince(new Date(sinceMillis), showHrs);
	}

	public static String elapsedSinceNano(long sinceNanos) {
		return elapsedSinceNano(sinceNanos, HrsOption.NEVER);
	}

	public static long elapsedSinceNanos(long sinceNanos) {
		long currentNanos = System.nanoTime();
		long elapsed = currentNanos - sinceNanos;
		return elapsed;
	}

	public static String elapsedSinceNano(long sinceNanos, HrsOption showHrs) {
		return nanoTimeAsString(elapsedSinceNanos(sinceNanos), showHrs);
	}

	public static String timeAsString(long millis, HrsOption showHrs) {
		Date elapsedDate = new Date(millis);
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
		calendar.setTime(elapsedDate);

		int days = calendar.get(Calendar.DAY_OF_YEAR);
		int hrs = ((days -1) * 24) + calendar.get(Calendar.HOUR_OF_DAY);

		if (showHrs == HrsOption.ALWAYS || (showHrs == HrsOption.IFNOTZERO && hrs > 0)) {
			return (hrs + "h:" + calendar.get(Calendar.MINUTE) + "m:" + calendar.get(Calendar.SECOND) + "s:" + //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					calendar.get(Calendar.MILLISECOND) + "ms"); //$NON-NLS-1$
		}
		int min = ((hrs * 60) + calendar.get(Calendar.MINUTE));
		return (min + "m:" + calendar.get(Calendar.SECOND) + "s:" + calendar.get(Calendar.MILLISECOND) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public static String nanoTimeAsString(long nanos, HrsOption showHrs) {
		long ms = nanos / 1000000;
		long remainder = nanos % 1000000;

		if (ms > 0) {
			return timeAsString(ms, showHrs) + ':' + remainder + "ns";	//$NON-NLS-1$
		}
		else {
			return (nanos + "ns"); //$NON-NLS-1$
		}
	}

	public static <T> T runWithTiming(Callable<T> callable) {
		return runWithTiming(DFLT_MESSAGE, null, callable);
	}

	public static <T> T runWithTiming(HrsOption showHrs, Callable<T> callable) {
		return runWithTiming(DFLT_MESSAGE, showHrs, callable);
	}

	public static <T> T runWithTiming(String message, Callable<T> callable) {
		return runWithTiming(message, null, callable);
	}

	public static <T> T runWithTiming(String message, HrsOption showHrs, Callable<T> callable) {
		long startTime = System.currentTimeMillis();

		try {
			return callable.call();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if (showHrs != null) {
				System.out.println(message + elapsedSince(startTime, showHrs));
			}
			else {
				System.out.println(message + elapsedSince(startTime));
			}
		}
	}

	public static <T> T runWithNanoTiming(Callable<T> callable) {
		return runWithNanoTiming(DFLT_MESSAGE, null, callable);
	}

	public static <T> T runWithNanoTiming(HrsOption showHrs, Callable<T> callable) {
		return runWithNanoTiming(DFLT_MESSAGE, showHrs, callable);
	}

	public static <T> T runWithNanoTiming(String message, Callable<T> callable) {
		return runWithNanoTiming(message, null, callable);
	}

	public static <T> T runWithNanoTiming(String message, HrsOption showHrs, Callable<T> callable) {
		long startNano = System.nanoTime();

		try {
			return callable.call();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if (showHrs != null) {
				System.out.println(message + elapsedSinceNano(startNano, showHrs));
			}
			else {
				System.out.println(message + elapsedSinceNano(startNano));
			}
		}
	}
}