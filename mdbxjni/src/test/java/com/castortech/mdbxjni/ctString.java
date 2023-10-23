package com.castortech.mdbxjni;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.StringCharacterIterator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 * @author alpic
 *
 *         Created on Sep 29, 2006
 */
public class ctString {
	private static final Logger log = LoggerFactory.getLogger(ctString.class);

	public static final String EMPTY_STRING = ""; //$NON-NLS-1$
	public static final String HTML_BR_TAG = "<br/>"; //$NON-NLS-1$
	public static final String HTML_BR_TAG_SP = "<br />"; //$NON-NLS-1$
	public static final String SPACE = " "; //$NON-NLS-1$
	public static final String DASH = "-"; //$NON-NLS-1$
	public static final String DOT = "."; //$NON-NLS-1$
	public static final String COMMA = ","; //$NON-NLS-1$
	public static final String SLASH = "/"; //$NON-NLS-1$
	public static final String ZERO = "0"; //$NON-NLS-1$
	public static final String ELLIPSIS = "..."; //$NON-NLS-1$
	public static final String DBL_QUOTES = "\"\""; //$NON-NLS-1$
	public static final String CHAR_ESCAPE = "(?<!\\\\)"; //$NON-NLS-1$
	public static final String LF = System.getProperty("line.separator"); //$NON-NLS-1$
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss.SSS", Locale.US);  //$NON-NLS-1$

	public static final String TAB = "\t"; //$NON-NLS-1$

	private static Pattern compiledRegExp = null;
	private static final Pattern spacesRegExp = Pattern.compile("\\s+"); //$NON-NLS-1$
	private static final Pattern lfRegExp = Pattern.compile("\\n+"); //$NON-NLS-1$
	private static final Pattern validNameRegExp = Pattern.compile("[a-zA-Z][a-zA-Z0-9-_]+"); //$NON-NLS-1$
	private static final Pattern uriRegExp = Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?"); //$NON-NLS-1$

	// **********************************************************************************
	// 1st section covers essential string manipulation functions that
	// should absolutely be part of the standard java classes. No if ands or but.
	// **********************************************************************************
	public static int pos(final String subStr, final String inStr) {
		if (inStr != null) {
			return inStr.indexOf(subStr);
		}
		return -1;
	}

	public static int pos(final char separator, final String inStr) {
		if (inStr != null) {
			return inStr.indexOf(separator);
		}
		return -1;
	}

	public static int lastPos(final String subStr, final String inStr) {
		if (inStr != null) {
			return inStr.lastIndexOf(subStr);
		}
		return -1;
	}

	public static int indexOf(final String inStr, final char del) {
		if (inStr != null) {
			return inStr.indexOf(del);
		}
		return -1;
	}

	public static int indexOf(final String inStr, final String del) {
		if (inStr != null) {
			return inStr.indexOf(del);
		}
		return -1;
	}

	public static int lastIndexOf(final String inStr, final char del) {
		if (inStr != null) {
			return inStr.lastIndexOf(del);
		}
		return -1;
	}

	public static int lastIndexOf(final String inStr, final char[] cArr, final int offset) {
		if (inStr != null) {
			int lastIdx = -1;

			for (final char element : cArr) {
				final int idx = inStr.lastIndexOf(element, offset);

				if (idx > lastIdx) {
					lastIdx = idx;
				}
			}

			return lastIdx;
		}
		return -1;
	}

	public static String lastSectionOf(final String inStr2, final char del) {
		String inStr = inStr2;
		final int index = lastIndexOf(inStr, del);

		if (inStr != null && index != -1) {
			inStr = inStr.substring(index + 1);
		}

		return inStr;
	}

	public static int indexOf(final String inStr, final char[] cArr, final int offset) {
		if (inStr != null) {
			int firstIdx = -1;

			for (final char element : cArr) {
				final int idx = inStr.indexOf(element, offset);

				if (firstIdx == -1 || idx < firstIdx) {
					firstIdx = idx;
				}
			}

			return firstIdx;
		}
		return -1;
	}

	/**
	 * Trim string to the specified length + adds ellipsis if trimmed
	 */
	public static String trimTo(final String str, final int lgth) {
		if (length(str) > lgth) {
			return addMissingQuote(substring(str, 0, lgth) + ELLIPSIS, '"');
		}
		return substring(str, 0, lgth);
	}

	public static String left(final String str, final int lgth) {
		return substring(str, 0, lgth);
	}

	public static String right(final String str, final int lgth) {
		if (str != null) {
			final int strLgth = str.length();
			return substring(str, strLgth - lgth, strLgth);
		}
		return EMPTY_STRING;
	}

	public static String mid(final String str, final int beginIndex) {
		if (str != null) {
			final int strLgth = str.length();
			return substring(str, beginIndex, strLgth);
		}
		return EMPTY_STRING;
	}

	public static String mid(final String str, final int beginIndex, final int len) {
		return substring(str, beginIndex, beginIndex + len);
	}

	public static char charAt(final String str, final int index) {
		if (str != null && str.length() > index) {
			return str.charAt(index);
		}
		return ' ';
	}

	public static int length(final String val) {
		if (val != null) {
			return val.length();
		}
		return 0;
	}

	//**********************************************************************************************/
	//**  ALL THE TRIMMINGS                                                                        */
	//**********************************************************************************************/
	/**
	 * Trims the given string, and if <code>null</code> will return {@link #EMPTY_STRING}.
	 *
	 * @param val
	 *          the string to trim; can be <code>null</code>
	 * @return the trimmed string, or {@link #EMPTY_STRING} if val is <code>null</code>
	 */
	public static String trim(final String val) {
		if (val != null) {
			return val.trim();
		}
		return EMPTY_STRING;
	}

	/**
	 * Unlike {@link #trim(String)}, will return <code>null</code> if the argument is <code>null</code>.
	 *
	 * @param val
	 *          the string to trim; can be <code>null</code>
	 * @return the trimmed string, or <code>null</code> if val is <code>null</code>
	 */
	public static String trimNull(final String val) {
		return val == null ? null : val.trim();
	}

	// Special version of trim that will always at least return a space
	// instead of a zero length string. To be used to circumvent a bug
	// in DB2 6.1 UDB for OS390.
	public static String trimOne(final String val) {
		String nVal;

		if (val != null) {
			nVal = val.trim();

			if (nVal.length() == 0) {
				nVal = " "; //$NON-NLS-1$
			}
			return nVal;
		}
		return val;
	}

	/**
	 * Function to remove the trailing white space characters (@see
	 * {@link java.lang.Character#isWhitespace(char)}
	 *
	 * @param str
	 *          The string to process
	 * @return The input string with all trailing white spaces removed.
	 */
	public static String trimTrailingSpaces(final String stringToTrim) {
		StringBuilder sb = new StringBuilder(stringToTrim);
		sb = new StringBuilder(trimLeadingWhiteSpaces(sb.reverse().toString()));

		return sb.reverse().toString();
	}

	/**
	 * Function to remove the leading white space characters (@see
	 * {@link java.lang.Character#isWhitespace(char)}
	 *
	 * @param str
	 *          The string to process
	 * @return The input string with all leading white spaces removed.
	 */
	public static String trimLeadingWhiteSpaces(final String str) {
		return trimLeadingWhiteSpaces(str, true);
	}

	/**
	 * Function to remove the leading white space characters (@see
	 * {@link java.lang.Character#isWhitespace(char)} and allowing to control if tabs are considered as
	 * whitespace.
	 *
	 * @param str
	 *          The string to process
	 * @param trimTabs
	 *          True if we want to trim tabs (i.e. consider them as whitespace) or False if we don't want to
	 *          treat them as whitespace.
	 * @return The input string with all leading white spaces removed.
	 */
	public static String trimLeadingWhiteSpaces(final String str, final boolean trimTabs) {
		if (str == null) {
			return EMPTY_STRING;
		}

		final int lgth = str.length();

		for (int i = 0; i < lgth; i++) {
			final char c = str.charAt(i);

			if (!Character.isWhitespace(c) || (!trimTabs && c == '\t')) {
				return substring(str, i);
			}
		}

		return EMPTY_STRING; // if we get here we scanned the string and only found whitespace
	}

	/**
	 * Function to remove the trailing characters to trim
	 *
	 * @param stringToTrim
	 *          The string to process
	 * @param charsToTrim
	 *          String made up of all the chars to trim from the end of the string
	 *          treat them as whitespace.
	 * @return The input string with all trailing characters to trim removed.
	 */
	public static String trimTrailingChars(final String stringToTrim, final String charsToTrim) {
		StringBuilder sb = new StringBuilder(stringToTrim);
		sb = new StringBuilder(trimLeadingChars(sb.reverse().toString(), charsToTrim));

		return sb.reverse().toString();
	}

	/**
	 * Function to remove the leading characters to trim.
	 *
	 * @param str
	 *          The string to process
	 * @param charsToTrim
	 *          String made up of all the chars to trim from the beginning of the string
	 *          treat them as whitespace.
	 * @return The input string with all leading characters to trim removed.
	 */
	public static String trimLeadingChars(final String str, final String charsToTrim) {
		if (str == null) {
			return EMPTY_STRING;
		}

		final int lgth = str.length();

		for (int i = 0; i < lgth; i++) {
			final char c = str.charAt(i);

			if (charsToTrim.indexOf(c) == -1) {
				return substring(str, i);
			}
		}

		return EMPTY_STRING; // if we get here we scanned the string and only found withespace
	}

	/**
	 * Scans a string for the trailing spaces and linefeed that are contained within it
	 * @param str
	 * 				The string to scan
	 * @return
	 * 				The trailing spaces and linefeed if any are present or an empty string otherwise
	 */
	public static String getTrailingSpacesLineFeed(final String str) {
		final StringBuilder sb = new StringBuilder();

		final char[] cbuf = str.toCharArray();
		final int cnt = cbuf.length;

		if (cnt > 0) {
			for (int i = cnt - 1; i >= 0; i--) {
				final char c = cbuf[i];

				if (c == '\n' || c == '\r' || c == ' ') {
					sb.append(c);
				}
				else {
					break;
				}
			}
			return sb.reverse().toString();
		}

		return EMPTY_STRING;
	}

	/**
	 * Scans a string and trim any leading and trailing whitespace characters including the HTML <br/> tag
	 * @param str
	 * 				The string to scan
	 * @return
	 * 				The string with leading and trailing white space characters (including HTML <br/> tag) removed
	 */
	public static String trimWithBr(String str) {
		str = trimLeadingWhiteSpacesAndBr(str);
		str = trimTrailingWhiteSpacesAndBr(str);
		return str;

	}

	/**
	 * Function to remove the leading white space characters (@see
	 * {@link java.lang.Character#isWhitespace(char)} including the HTML <br/> tag.
	 *
	 * @param str
	 *          The string to process
	 * @return The input string with all leading white spaces (including HTML <br/> tag) removed.
	 */
	public static String trimLeadingWhiteSpacesAndBr(String str) {
		if (str == null) {
			return EMPTY_STRING;
		}

		int discardCnt;

		while ((discardCnt = leadingCharsToDiscard(str)) > 0) {
			str = substring(str, discardCnt);
		}

		return str;
	}

	private static int leadingCharsToDiscard(String str) {
		if (isEmpty(str)) {
			return 0;
		}

		if (Character.isWhitespace(str.charAt(0))) {
			return 1;
		}
		else if (str.startsWith(HTML_BR_TAG)) {
			return HTML_BR_TAG.length();
		}
		else if (str.startsWith(HTML_BR_TAG_SP)) {
			return HTML_BR_TAG_SP.length();
		}
		return 0;
	}

	/**
	 * Function to remove the trailing white space characters (@see
	 * {@link java.lang.Character#isWhitespace(char)} including the HTML <br/> tag.
	 *
	 * @param str
	 *          The string to process
	 * @return The input string with all trailing white spaces (including HTML <br/> tag) removed.
	 */
	public static String trimTrailingWhiteSpacesAndBr(String str) {
		if (str == null) {
			return EMPTY_STRING;
		}

		int discardCnt;

		while ((discardCnt = trailingCharsToDiscard(str)) > 0) {
			str = left(str, str.length() - discardCnt);
		}

		return str;
	}

	private static int trailingCharsToDiscard(String str) {
		if (isEmpty(str)) {
			return 0;
		}

		if (Character.isWhitespace(str.charAt(str.length() -1))) {
			return 1;
		}
		else if (str.endsWith(HTML_BR_TAG)) {
			return HTML_BR_TAG.length();
		}
		else if (str.endsWith(HTML_BR_TAG_SP)) {
			return HTML_BR_TAG_SP.length();
		}
		return 0;
	}

	/**********************************************************************************************/
	/** END OF ALL THE TRIMMINGS                                                                  */
	/**********************************************************************************************/

	/**
	 * Returns true if the two Strings passed in are equal, or if they are both null. In particular, note that
	 * equals("", null) will return false.
	 *
	 * @param val1
	 *          one of the two strings to compare.
	 * @param val2
	 *          one of the two strings to compare.
	 * @return true if the two Strings passed in are equal, or if they are both null.
	 *
	 */
	public static boolean equals(final String val1, final String val2) {
		if (val1 == null && val2 == null) {
			return true;
		}
		else if (val1 == null || val2 == null) {
			return false;
		}
		else {
			return val1.equals(val2);
		}
	}

	/**
	 * Returns true if the two Strings passed in are equal, or if they are both null or empty. In particular, note that
	 * equals("", null) will return true as null and empty are considered to be equivalent.
	 *
	 * @param val1
	 *          one of the two strings to compare.
	 * @param val2
	 *          one of the two strings to compare.
	 * @return true if the two Strings passed in are equal, or if they are both null.
	 *
	 */
	public static boolean equalsNullSafe(final String val1, final String val2) {
		if (val1 == null && val2 == null) {
			return true;
		}
		else if (val1 == null || val2 == null) {
			return nullSafe(val1).equals(nullSafe(val2));
		}
		else {
			return val1.equals(val2);
		}
	}

	// More intelligent version of equals that understands nulls.
	public static boolean equalsIgnoreCase(final String val1, final String val2) {
		if (val1 == null && val2 == null) {
			return true;
		}
		else if (val1 == null || val2 == null) {
			return false;
		}
		else {
			return val1.equalsIgnoreCase(val2);
		}
	}

	// More intelligent version of equals that understands nulls.
	public static boolean equalsIgnoreCaseTrim(final String val1, final String val2) {
		if (val1 == null && val2 == null) {
			return true;
		}
		else if (val1 == null || val2 == null) {
			return false;
		}
		else {
			return val1.trim().equalsIgnoreCase(val2.trim());
		}
	}

	/**
	 * Returns true if the two Strings passed in are equal, or if they are both null or empty. In particular, note that
	 * equals("", null) will return true as null and empty are considered to be equivalent.
	 *
	 * @param val1
	 *          one of the two strings to compare.
	 * @param val2
	 *          one of the two strings to compare.
	 * @return true if the two Strings passed in are equal, or if they are both null.
	 *
	 */
	public static boolean matchesUptoEllipsis(final String val1, final String val2) {
		if (equals(val1,val2)) {
			return true;
		}
		else {
			boolean ellipsis1 = endsWithEllipsis(val1);
			boolean ellipsis2 = endsWithEllipsis(val2);

			if (ellipsis1 && ellipsis2) {  //they both end in ellipsis and were not equal to start with
				return false;
			}

			if (ellipsis1 || ellipsis2) {
				Pair<Integer, Integer> common = getCommonAreas(val1, val2);
				int commonLgth = common.getA().intValue() + 1;
				int expectedCommonLgth = ellipsis1 ? length(val1) - 3 : length(val2) - 3;
				return commonLgth == expectedCommonLgth;
			}
			else { //not a case of have one string with an ellipsis
				return false;
			}
		}
	}

	public static boolean equalsIgnoreEndOfLine(final String val1, final String val2) {
		if (val1 == null && val2 == null) {
			return true;
		}
		else if (val1 == null || val2 == null) {
			return false;
		}
		else {
			final BufferedReader br1 = new BufferedReader(new StringReader(val1));
			final BufferedReader br2 = new BufferedReader(new StringReader(val2));

			try {
				boolean equals = false;
				String line1;
				String line2;

				do {
					line1 = br1.readLine();
					line2 = br2.readLine();
					equals = equals(line1, line2);
				}
				while (equals && line1 != null && line2 != null);

				return equals;
			}
			catch (IOException e) {
				return false;
			}
		}
	}

	/**
	 * Returns true if the two Strings passed in are equal, after trimming if necessary, or if they are both null.
	 * In particular, note that equals("", null) will return false.
	 *
	 * @param val1
	 *          one of the two strings to compare.
	 * @param val2
	 *          one of the two strings to compare.
	 * @return true if the two Strings passed in are equal, or if they are both null.
	 *
	 */
	public static boolean equalsTrim(final String val1, final String val2) {
		if (val1 == null && val2 == null) {
			return true;
		}
		else if (val1 == null || val2 == null) {
			return false;
		}
		else {
			return trim(val1).equals(trim(val2));
		}
	}

	/**
	 * Returns true if and only if val is null or val is the empty string "", returns false otherwise.
	 *
	 * @param val
	 *          the string to test.
	 * @return true if and only if val is null or val is the empty string "".
	 */
	public static boolean isEmpty(final String val) {
		return val == null || val.equals(EMPTY_STRING);
	}

	public static boolean isEmptyOrSpaces(final String val) {
		return val == null || (trim(val).equals(EMPTY_STRING));
	}

	public static String nullSafe(final String val) {
		if (val == null) {
			return EMPTY_STRING;
		}
		return val;
	}

	/**
	 * Replace the first occurrence of replStr in str by replByStr
	 *
	 * @param replStr
	 *          the substring to search for and replace.
	 * @param inStr
	 *          the string to search within.
	 * @param replByStr
	 *          the string replace replStr with.
	 * @return
	 */
	public static String replace(final String replStr, final String inStr2, final String replByStr) {
		String inStr = inStr2;
		if (inStr == null || replStr == null || replByStr == null) {
			return null;
		}

		final int strPos = inStr.indexOf(replStr);

		if (strPos != -1) {
			inStr = left(inStr, strPos) + replByStr + inStr.substring(strPos + replStr.length());
		}

		return inStr;
	}

	/**
	 * Replace the last occurrence of replStr in str by replByStr
	 *
	 * @param replStr
	 *          the substring to search for and replace.
	 * @param inStr
	 *          the string to search within.
	 * @param replByStr
	 *          the string replace replStr with.
	 * @return
	 */
	public static String replaceLast(final String replStr, final String inStr2, final String replByStr) {
		String inStr = inStr2;
		if (inStr == null || replStr == null || replByStr == null) {
			return null;
		}

		final int strPos = inStr.lastIndexOf(replStr);

		if (strPos != -1) {
			inStr = left(inStr, strPos) + replByStr + inStr.substring(strPos + replStr.length());
		}

		return inStr;
	}

	/**
	 * Replace the first occurrence of replStr in str by replByStr
	 * @param replStr
	 * @param inStr2
	 * @param replByStr
	 * @return
	 */
	public static String replaceNoCase(final String replStr, final String inStr2, final String replByStr) {
		String inStr = inStr2;
		final int strPos = inStr.toLowerCase().indexOf(replStr.toLowerCase());

		if (strPos != -1) {
			inStr = left(inStr, strPos) + replByStr + inStr.substring(strPos + replStr.length());
		}

		return inStr;
	}

	/**
	 * Replace a specific set of characters starting at strPos in inStr by replByStr
	 *
	 * @param inStr2
	 * @param strPos
	 * @param repLgth
	 * @param replByStr
	 * @return
	 */
	public static String replace(final String inStr2, final int strPos, final int repLgth, final String replByStr) {
		String inStr = inStr2;
		if (strPos != -1) {
			inStr = left(inStr, strPos) + replByStr + substring(inStr, strPos + repLgth);
		}

		return inStr;
	}

	/**
	 * Overstrike or Replace a specific set of characters of a specific length starting at strPos in inStr by
	 * replByStr
	 *
	 * @param inStr
	 *
	 * @param strPos
	 * @param repLgth
	 * @param replByStr
	 * @return
	 */
	public static String overStrike(final String inStr2, final int strPos, final int repLgth, final String replByStr) {
		String inStr = inStr2;
		if (inStr == null || replByStr == null) {
			return inStr;
		}

		if (strPos != -1 && strPos < length(inStr)) {
			inStr = left(inStr, strPos) + left(replByStr, repLgth) + substring(inStr, strPos + repLgth);
		}

		return inStr;
	}

	/**
	 * Replace all occurrences of replStr in inStr by replByStr
	 * @param replStr
	 * @param inStr2
	 * @param replByStr
	 * @return
	 */
	public static String replaces(final String replStr, final String inStr2, final String replByStr) {
		String inStr = inStr2;
		if (inStr == null || replStr == null) {
			return inStr;
		}

		int strPos = inStr.indexOf(replStr);

		while (strPos != -1) {
			String tmpStr = left(inStr, strPos) + replByStr;
			inStr = tmpStr + inStr.substring(strPos + replStr.length());
			strPos = inStr.indexOf(replStr, tmpStr.length());
		}

		return inStr;
	}

	public static String replaceNewLineWithBreak(String str) {
		if (str != null) {
			return str.replace("\n", HTML_BR_TAG); //$NON-NLS-1$
		}
		return EMPTY_STRING;
	}

	public static String pad(final String str, final int padTo, final boolean padRight) {
		return pad(str, padTo, padRight, 1, ' ');
	}

	public static String pad(final String str, final int padTo, final boolean padRight, final char padChar) {
		return pad(str, padTo, padRight, 1, padChar);
	}

	public static String pad(final String str, final int padTo, final boolean padRight, final int tabSize) {
		return pad(str, padTo, padRight, tabSize, ' ');
	}

	public static String pad(final String str2, final int padTo, final boolean padRight, final int tabSize,
			final char padChar) {
		String str = str2;
		if (str == null) {
			str = EMPTY_STRING;
		}

		final Pair<Integer, Integer> pair = getColLgth(str, 0, tabSize);
		final int lgth = pair.b.intValue();

		if (lgth >= padTo) {
			return substring(str, 0, padTo);
			// TODO: Replace with this when the new method is tested. For now won't work with tabs.
			// return substring(str, 1, padTo +1, tabSize);
		}
		final StringBuilder sb = padRight ? new StringBuilder() : new StringBuilder(str);

		for (int x = 0; x < padTo - lgth; x++) {
			sb.append(padChar);
		}

		if (padRight) {
			sb.append(str);
		}

		return sb.toString();
	}

	// **********************************************************************************
	// The next section covers extensions to the basic string manipulation functions that
	// facilitate and enhance programmers productivity
	// **********************************************************************************
	public static String substring(final String str, final int beginIndex) {
		return substring(str, beginIndex, str == null ? 0 : str.length());
	}

	public static String substring(final String str, final int beginIndex2, final int endIndex2) {
		if (str != null) {
			int beginIndex = beginIndex2;
			int endIndex = endIndex2;
			String sstr = EMPTY_STRING;

			if (beginIndex == -1) {  //would happen if pos not found, so move to 0
				beginIndex = 0;
			}

			if (endIndex == -1) {  //would happen if pos not found, so move to str.length()
				endIndex = str.length();
			}

			if (beginIndex < str.length() && beginIndex >= 0 && endIndex >= 0) {
				sstr = str.substring(beginIndex, Math.min(endIndex, str.length()));
			}

			return sstr;
		}
		return str;
	}

	/**
	 * This special version of substring supports dealing with column positions (1 based) contained in a string
	 * that is allowed to contain tabs which are set to tabSize. If any of the column position falls within a
	 * tab expansion, the preceding tab character will be replaced by spaces to allow for an accurate substring.
	 *
	 * @param str
	 *          The String to substring
	 * @param beginCol
	 *          The beginning column (not index, 1 based) to start from
	 * @param endCol
	 *          The ending column (not index, 1 based) to go to
	 * @param tabSize
	 *          The tab increment size. i.e where the tab markers will be located at (col 1, then 1 + tabSize +
	 *          tabSize, etc) This is different that a pure expansion size if "tab area" already contains
	 *          characters.
	 * @return
	 */
	public static String substring(final String str2, final int beginCol, final int endCol, final int tabSize) {
		String str = str2;
		if (str != null) {
			String sstr = EMPTY_STRING;
			Pair<Integer, Integer> beginPair = getColumnOffset(str, beginCol, tabSize);

			if (beginPair.a.intValue() != -1) { // otherwise nowhere in string
				if ((beginPair.a.intValue() + 1) != beginPair.b.intValue()) { // requested beginIndex is within a tab expansion
					str = replace(str, beginPair.a.intValue(), 1, repeatChar(' ',
							beginPair.a.intValue() % tabSize == 0 ? tabSize : beginPair.a.intValue() % tabSize));
					beginPair = getColumnOffset(str, beginCol, tabSize);
				}

				Pair<Integer, Integer> endPair = getColumnOffset(str, endCol, tabSize);

				if (endPair.a.intValue() != -1) { // otherwise nowhere in string
					if ((endPair.a.intValue() + 1) != endPair.b.intValue()) { // requested endIndex is within a tab expansion
						str = replace(str, endPair.a.intValue(), 1, repeatChar(' ',
								endPair.a.intValue() % tabSize == 0 ? tabSize : endPair.a.intValue()
								% tabSize));
						endPair = getColumnOffset(str, endCol, tabSize);
					}

					sstr = str.substring(beginPair.a.intValue(), endPair.a.intValue());
				}
				else if (endPair.b.intValue() == -1) { // we are overreaching, simply return til the end of the string
					sstr = str.substring(beginPair.a.intValue());
				}
			}

			return sstr;
		}
		return str;
	}

	public static String repeatChar(final char rChar, final int repeatCnt) {
		if (repeatCnt <= 0) {
			return EMPTY_STRING;
		}

		final char[] chars = new char[repeatCnt];
		Arrays.fill(chars, rChar);

		return String.valueOf(chars);
	}

	/**
	 * Method that determines the number of repetition of rChar in inStr starting with the leftmost character
	 * and stopping on the first non-occurrence character.
	 *
	 * @param inStr		The string where to look for the repeating character
	 * @param rChar		The character that we want to count the repetition of
	 * @return				The number of occurrence of rChar in inStr
	 */
	public static int repeatCharCnt(final String inStr, final char rChar) {
		int repeatCnt = 0;

		if (inStr != null) {
			while (repeatCnt < inStr.length() && inStr.charAt(repeatCnt) == rChar) {
				repeatCnt++;
			}
		}

		return repeatCnt;
	}

	public static String szString(final String val) {
		if (val == null) {
			return null;
		}
		return (val + (Character.valueOf('\u0000')).toString());
	}

	public static byte[] getBytes(final String str) {
		final char[] buffer = new char[str.length()];
		final int length = str.length();
		str.getChars(0, length, buffer, 0);
		byte[] b = new byte[length];

		for (int j = 0; j < length; j++) {
			if (buffer[j] > 256) { // If fast is a no go, then lets slow down
				log.info("At char {} we detected a non 8 bit character. Switching to slow convert.", j); //$NON-NLS-1$
				b = str.getBytes();
				break;
			}

			b[j] = (byte)buffer[j];
		}

		return b;
	}

	// public static String createFromBytes(byte[] b) {
	// int length = b.length;
	// char buffer[] = new char[length];

	// for (int j = 0; j < length; j += 2) {
	// if ((int)buffer[j] > 256) { //If fast is a no go, then lets slow down
	// System.out.println("At char " + j + " we detected a non 8 bit character. Switching to slow convert.");
	// b = str.getBytes();
	// break;
	// }

	// buffer[j] = (byte) buffer[j];
	// buffer[j + 1] = (byte) buffer[j + 1];
	// }

	// str.getChars(0, length, buffer, 0);

	// return b;
	// }

	public static String toHexString(final String str) {
		final int lgth = str.length();
		final StringBuilder result = new StringBuilder(lgth);

		for (int i = 0; i < lgth; i++) {
			final char c = str.charAt(i);
			final int numVal = c;

			if (numVal < 16) {
				result.append('0');
			}

			result.append(Integer.toHexString(numVal));
			result.append(' ');
		}

		return result.toString();
	}

	public static String fromHexString(final String str2) {
		String str = str2;
		final char[] carr = new char[str.length()];
		int length = str.length();
		int j = 0;
		String charVal;

		if (left(str, 1).equals("<") && right(str, 1).equals(">")) { //$NON-NLS-1$ //$NON-NLS-2$
			str = mid(str, 1, length - 2);
			length = str.length();

			for (int i = 0; i < length - 1; i = i + 2) {
				charVal = mid(str, i, 2);

				if (charVal.length() == 1) {
					charVal = charVal + "0"; //$NON-NLS-1$
				}

				carr[j++] = (char)Integer.parseInt(charVal, 16);
			}

			return new String(carr);
		}

		return EMPTY_STRING;
	}

	public static String toHexFormat(final String str) {
		final int lgth = str.length();
		final StringBuilder fresult = new StringBuilder(lgth);
		StringBuilder tchars = new StringBuilder(16);
		StringBuilder tresult = new StringBuilder(64);
		DecimalFormat df = null;
		String rnStr = null;

		for (int i = 0; i < lgth; i++) {
			final char c = str.charAt(i);
			final int numVal = c;

			if (Character.isISOControl(c)) {
				tchars.append('.');
			}
			else {
				tchars.append(c);
			}

			if (numVal < 16) {
				tresult.append('0');
			}

			tresult.append(Integer.toHexString(numVal));
			tresult.append(' ');

			if (((i + 1) % 16 == 0) || ((i + 1) == lgth)) {
				fresult.append(LF);

				df = new DecimalFormat("000000"); //$NON-NLS-1$
				rnStr = df.format(i - 15L);
				fresult.append(rnStr);

				fresult.append('-');

				df = new DecimalFormat("000000"); //$NON-NLS-1$
				rnStr = df.format(i);
				fresult.append(rnStr);

				fresult.append(":"); //$NON-NLS-1$
				fresult.append(pad(tresult.toString(), 48, true));
				fresult.append(" "); //$NON-NLS-1$
				fresult.append(tchars.toString());

				tchars = new StringBuilder(16);
				tresult = new StringBuilder(64);
			}
		}

		return fresult.toString();
	}

	public static boolean hasLetterOrDigit(final String str) {
		final int lgth = str.length();

		for (int i = 0; i < lgth; i++) {
			final char c = str.charAt(i);

			if (Character.isLetterOrDigit(c)) {
				return true;
			}
		}

		return false;
	}

	public static String quoteIfSpaces(final String str) {
		if (indexOf(str, ' ') != -1) {
			return quoted(str, '"');
		}

		return str;
	}

	/**
	 * @param str
	 * @return str surrounded by double quote (")
	 */
	public static String quoted(final String str) {
		return quoted(str, '"');
	}

	/**
	 * @param str
	 * @return str surrounded by quoteChar
	 */
	public static String quoted(final String str, final char quoteChar) {
		final StringBuilder sb = new StringBuilder(str);

		if (str.length() > 0) {
			if (sb.charAt(0) != quoteChar) {
				sb.insert(0, quoteChar);
			}

			if (str.charAt(str.length() - 1) != quoteChar ||
					// fix to see a trailing \" which is part of the string
					//fix to account for an escape \\" at the end of a string
					(str.length() >= 4 && str.charAt(str.length() - 2) == '\\' &&
					str.charAt(str.length() - 3) != '\\')) {
				sb.append(quoteChar);
			}

			return sb.toString();
		}
		sb.append(quoteChar);
		sb.append(quoteChar);

		return sb.toString();
	}

	/** Removed either single or double quote from string argument */
	public static String unQuoted(final String str) {
		final String str2 = unQuoted(str, '"', '"');
		return unQuoted(str2, '\'', '\'');
	}

	/** Removed single quote from string argument */
	public static String unSingleQuoted(final String str) {
		return unQuoted(str, '\'', '\'');
	}

	/** Removed double quote from string argument */
	public static String unDoubleQuoted(final String str) {
		return unQuoted(str, '"', '"');
	}

	/**
	 * Checks if a string is "quoted", as defined by {@link #isQuoted(String, char, char)}. If so, returns a new
	 * String with the first and last characters (the so-called "quotes") removed, otherwise returns the
	 * original string as-is. Therefore, if a null string is passed in, null will be returned.
	 *
	 * @param str
	 *          the string to unquote.
	 * @param startQuoteChar
	 *          the potential first character of the string.
	 * @param endQuoteChar
	 *          the potential last character of the string.
	 * @return the results of "unquoting".
	 */
	public static String unQuoted(final String str, final char startQuoteChar, final char endQuoteChar) {
		if (isQuoted(str, startQuoteChar, endQuoteChar, true)) {
			final StringBuilder sb = new StringBuilder(str);
			sb.deleteCharAt(0);
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}
		return str;
	}

	public static boolean isQuoted(final String str) {
		return isQuoted(str, '"', '"', true) || isQuoted(str, '\'', '\'', true);
	}

	public static boolean isSingleQuoted(final String str) {
		return isQuoted(str, '\'', '\'', true);
	}

	public static boolean isDoubleQuoted(final String str) {
		return isQuoted(str, '"', '"', true);
	}

	/**
	 * Makes sure that a string containing a quoteChar has a terminating quote character and that it balances
	 * out.
	 *
	 * @param str
	 * @return string with any missing end quote added if necessary
	 */
	public static String addMissingQuote(final String str, final char quoteChar) {
		if (isEmpty(str)) {
			return null;
		}

		int lgth = str.length();
		int quoteCnt = 0;

		for (int i = 0; i < lgth; i++) {
			final char c = str.charAt(i);
			if (c == quoteChar) {
				quoteCnt++;
			}
		}

		if (quoteCnt != 0 && quoteCnt % 2 != 0) {
			return str + quoteChar;
		}
		return str;
	}

	public static boolean isUUID(String str) {
		return getUUIDCompiledRegexep().matcher(str).matches();
	}

	public static boolean isURI(String str) {
		return uriRegExp.matcher(str).matches();
	}

	public static boolean isValidName(String str) {
		return validNameRegExp.matcher(str).matches();
	}

	/**
	 * Tests if the String possibly represents a valid JSON String.<br>
	 * Valid JSON strings are:
	 * <ul>
	 * <li>"null"</li>
	 * <li>starts with "[" and ends with "]"</li>
	 * <li>starts with "{" and ends with "}"</li>
	 * </ul>
	 */
	@SuppressWarnings("nls")
	public static boolean mayBeJSON(String string) {
		return string != null && ("null".equals(string) || (string.startsWith("[") && string.endsWith("]")) ||
				(string.startsWith("{") && string.endsWith("}")));
	}

	public static Pattern getUUIDCompiledRegexep() {
		if (compiledRegExp == null) {
			final String regexp = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"; //$NON-NLS-1$
			compiledRegExp = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		}

		return compiledRegExp;
	}

	public static boolean isAllDigits(final String str) {
		if (isEmpty(str)) {
			return false;
		}

		for (int i = 0; i < str.length(); i++) {
			//If we find a non-digit character we return false.
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static String stripNonDigits(final String str) {
		if (isEmpty(str) || isAllDigits(str)) {
			return str;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			//If we find a non-digit character we return false.
			char c = str.charAt(i);
			if (Character.isDigit(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * Returns true if the string str starts with startQuoteChar and ends with endQuoteChar, false otherwise.
	 * Note that null does not start or end with any char, and so a null string will always return false.
	 *
	 * @param str
	 *          the string to check.
	 * @param startQuoteChar
	 *          the potential first character of the string.
	 * @param endQuoteChar
	 *          the potential last character of the string.
	 * @return true if the string str starts with startQuoteChar and ends with endQuoteChar.
	 */
	public static boolean isQuoted(final String str, final char startQuoteChar, final char endQuoteChar) {
		return isQuoted(str, startQuoteChar, endQuoteChar, false);
	}

	/**
	 * Returns true if the string str starts with startQuoteChar and ends with endQuoteChar, false otherwise.
	 * Note that null does not start or end with any char, and so a null string will always return false.
	 *
	 * This method assumes that other than possible double end quote characters used to represent a quote
	 * character, that the string passed in doesn't contain any other end quote character. The method is not
	 * designed to check where a quoted string ends or if the result is a single valid quoted string.
	 *
	 * @param str
	 *          the string to check.
	 * @param startQuoteChar
	 *          the potential first character of the string.
	 * @param endQuoteChar
	 *          the potential last character of the string.
	 * @param dblQuoteChar
	 *          true if the end quote character can be doubled to allow for its presence in the string. This is
	 *          very standard for quote and double quotes.
	 * @return true if the string str is quoted by starting with startQuoteChar and ending with endQuoteChar. If
	 *         dblQuoteChar is true, the presence of additional endQuoteChar will be tested to make sure they
	 *         are doubled.
	 */
	public static boolean isQuoted(final String str, final char startQuoteChar, final char endQuoteChar,
			boolean dblQuoteChar) {
		boolean quoted = false;
		int lgth = str.length();

		if (!isEmpty(str) && lgth >= 2) { // minimum of the 2 quote characters
			quoted = (str.charAt(0) == startQuoteChar && str.charAt(str.length() - 1) == endQuoteChar);  //most basic test

			if (!dblQuoteChar || !quoted) {  //we don't want to test further or we don't even have a match
				return quoted;
			}
			else if (lgth >= 3) {
				boolean inEscape = false;
				boolean withinQuotedString = true;  //starts true since we know char 1 is start quote char

				//we have a case of the last 2 characters representing the end quote character, so we are possibly
				//not having an end quote.
				//ex: 'bla''' is quoted. but: 'bla'' is not quoted.
				//but beware of: "\"" which is quoted because of the escape
				for (int i = 1; i < lgth; i++) {
					final char c = str.charAt(i);

					if (!inEscape && c == '\\') {  //!inEscape to correctly process escaped backslash itself
						inEscape = true;
					}
					else {
						if (inEscape) {  //this indicates a stand-alone char (and possibly quote char)
							//nothing to do
						}
						else if (c == endQuoteChar) {
							withinQuotedString = !withinQuotedString;
						}
						inEscape = false;
					}
				}
				quoted = !withinQuotedString;
			}
		}

		return quoted;
	}

	public static boolean quoteBalanced(final String str, final char quoteChar) {
		boolean balanced = false;
		int lgth = str.length();

		if (!isEmpty(str) && lgth >= 2) { // minimum of the 2 quote characters
			boolean inEscape = false;
			boolean withinQuotedString = false;

			//but beware of: "\"" which is quoted because of the escape
			for (int i = 0; i < lgth; i++) {
				final char c = str.charAt(i);

				if (!inEscape && c == '\\') {  //!inEscape to correctly process escaped backslash itself
					inEscape = true;
				}
				else {
					if (inEscape) {  //this indicates a stand-alone char (and possibly quote char)
						//nothing to do
					}
					else if (c == quoteChar) {
						withinQuotedString = !withinQuotedString;
					}
					inEscape = false;
				}
			}
			balanced = !withinQuotedString;
		}

		return balanced;
	}

	public static boolean containsIncompleteQuoted(final String str) {
		return containsIncompleteQuoted(str, '"', '"');
	}

	public static boolean containsIncompleteQuoted(final String str, final char startQuoteChar,
			final char endQuoteChar) {
		if (isEmpty(str)) {
			return false;
		}

		final int startPos = str.lastIndexOf(startQuoteChar);

		if (startPos != -1) {
			final int endPos = str.lastIndexOf(endQuoteChar);

			if (endPos == -1 || endPos <= startPos) {
				return true;
			}
		}

		return false;
	}

	public static String escapeSpecialChars(final String in) {
		return escapeSpecialChars(in, false);
	}

	public static String escapeSpecialChars(final String in, final boolean forceOneLine) {
		final StringBuilder resultString = new StringBuilder();
		char prevChar = ' ';

		if (isEmpty(in)) {
			return in;
		}

		for (int i = 0; i < in.length(); i++) {
			final char aChar = in.charAt(i);

			if (aChar == '\'') {
				resultString.append("'"); //$NON-NLS-1$
			}
			else if ((aChar == '"') && prevChar != '\\') {
				resultString.append("\\\""); //$NON-NLS-1$
			}
			else if (aChar == '\n' && forceOneLine) {
				resultString.append("\\n"); //$NON-NLS-1$
			}
			else if (aChar != '\r') { // watch out drops carriage return
				resultString.append(aChar);
			}

			prevChar = aChar;
		}

		return (resultString.toString());
	}

	public static String escapeQuotes(final String in) {
		final StringBuilder resultString = new StringBuilder();

		if (isEmpty(in)) {
			return in;
		}

		for (int i = 0; i < in.length(); i++) {
			final char aChar = in.charAt(i);
			if (aChar == '"') {
				resultString.append("'"); //$NON-NLS-1$
			}
			else if (aChar != '\r') { // watch out drops carriage return
				resultString.append(aChar);
			}
		}
		return (resultString.toString());
	}

	/**
	 * Converts all instances of <code>"'"</code> into <code>"\'"</code>.
	 * <p>
	 * Removes all CR characters (<code>'\r'</code>).
	 */
	public static String escapeSingleQuotes(final String in) {
		final StringBuilder resultString = new StringBuilder();

		if (isEmpty(in)) {
			return in;
		}

		for (int i = 0; i < in.length(); i++) {
			final char aChar = in.charAt(i);
			if (aChar == '\'') {
				resultString.append("\\'"); //$NON-NLS-1$
			}
			else if (aChar != '\r') { // watch out drops carriage return
				resultString.append(aChar);
			}
		}
		return (resultString.toString());
	}

	public static String escapeSpecialCharsForDot(final String in, final boolean forceOneLine) {
		final StringBuilder resultString = new StringBuilder();
		if (isEmpty(in)) {
			return in;
		}

		for (int i = 0; i < in.length(); i++) {
			final char aChar = in.charAt(i);

			if (aChar == '\'') {
				resultString.append("'"); //$NON-NLS-1$
			}
			else if (aChar == '\n' && forceOneLine) {
				resultString.append("\\n"); //$NON-NLS-1$
			}
			else if (aChar != '\r') { // watch out drops carriage return
				resultString.append(aChar);
			}
		}

		return resultString.toString();
	}

	/**
	 * This method takes in a string to replace special characters in.
	 *
	 * @param in
	 *          The string to make replacement in.
	 * @param charsToReplace
	 *          String representing each of the special characters to look for and to replace.
	 * @param replacementChars
	 *          String representing each of the matching characters with which we will replace identified
	 *          special character in charsToReplace.
	 *
	 * @return String with special characters replaced.
	 */
	public static String replaceSpecialChars(final String in, final String charsToReplace,
			final String replacementChars) {
		checkArgument(length(charsToReplace) == length(replacementChars),
				"Length of charsToReplace doesn't match replacementChars"); //$NON-NLS-1$
		final StringBuilder resultString = new StringBuilder();

		if (isEmpty(in) || isEmpty(charsToReplace) || isEmpty(replacementChars)) {
			return in;
		}

		for (int i = 0; i < in.length(); i++) {
			final char aChar = in.charAt(i);
			final int idx = charsToReplace.indexOf(aChar);

			if (idx != -1) {
				resultString.append(replacementChars.charAt(idx));
			}
			else {
				resultString.append(aChar);
			}
		}

		return resultString.toString();
	}

	public static String stripSpecialChars(final String in, final char[] charsToStrip) {
		final StringBuilder resultString = new StringBuilder();

		if (isEmpty(in) || charsToStrip == null || charsToStrip.length == 0) {
			return in;
		}

		for (int i = 0; i < in.length(); i++) {
			final char aChar = in.charAt(i);

			if (!isInArray(aChar, charsToStrip)) {
				resultString.append(aChar);
			}
		}

		return resultString.toString();
	}

	public static String escapeLineDelimiterChars(final String in) {
		final StringBuilder resultString = new StringBuilder();
		for (int i = 0; i < in.length(); i++) {
			final char aChar = in.charAt(i);

			if (aChar == '\n') {
				resultString.append("\\n"); //$NON-NLS-1$
			}
			else if (aChar == '\r') {
				resultString.append("\\r"); //$NON-NLS-1$
			}
			else { // watch out drops carriage return
				resultString.append(aChar);
			}
		}

		return resultString.toString();
	}

	public static String endWithDelimiter(final String text, final String del) {
		if (isEmpty(text)) {
			return del;
		}
		else if (text.endsWith(del)) {
			return text;
		}
		return text + del;
	}

	public static String endWithEllipsis(final String text) {
		return endWithDelimiter(text, ELLIPSIS);
	}

	public static boolean endsWithEllipsis(final String text) {
		if (isEmpty(text)) {
			return false;
		}

		return right(text, 3).equals(ELLIPSIS);
	}

	public static String trimEllipsis(final String text) {
		if (endsWithEllipsis(text)) {
			return trimTrailingChars(text, ELLIPSIS);
		}
		return text;
	}

	/**
	 * This method takes in a string with optional prefix and postfix length and converts special whitespace
	 * characters to their escape equivalents for display. The display is in the form of:
	 * [p,r,e],c,o,n,t,e,n,t,\n,[p,o,s,t] where preCnt = 3 and postCnt = 4
	 *
	 * @param in
	 *          The string to display
	 * @param preCnt
	 *          The number of preceding characters to put content in perspective
	 * @param postCnt
	 *          The number of following characters to put content in perspective
	 * @return Formatted string as shown above
	 */
	public static String debugShowAllChars(final String in, final int preCnt, final int postCnt) {
		final StringBuilder resultString = new StringBuilder();
		char prevChar = ' ';
		final int postStart = in.length() - postCnt;

		if (preCnt > 0) {
			resultString.append("["); //$NON-NLS-1$
		}

		for (int i = 0; i < in.length(); i++) {
			final char aChar = in.charAt(i);

			if (i > 0) {
				resultString.append(","); //$NON-NLS-1$
			}

			if (i == postStart) {
				resultString.append("["); //$NON-NLS-1$
			}

			if (aChar == '\'') {
				resultString.append("'"); //$NON-NLS-1$
			}
			else if ((aChar == '"') && prevChar != '\\') {
				resultString.append("\\\""); //$NON-NLS-1$
			}
			else if (aChar == '\t') {
				resultString.append("\\t"); //$NON-NLS-1$
			}
			else if (aChar == '\f') {
				resultString.append("\\f"); //$NON-NLS-1$
			}
			else if (aChar == '\n') {
				resultString.append("\\n"); //$NON-NLS-1$
			}
			else if (aChar == '\r') {
				resultString.append("\\r"); //$NON-NLS-1$
			}
			else {
				resultString.append(aChar);
			}

			if ((i + 1) == preCnt) {
				resultString.append("]"); //$NON-NLS-1$
			}

			prevChar = aChar;
		}

		if (postCnt > 0) {
			resultString.append("]"); //$NON-NLS-1$
		}

		return (resultString.toString());
	}

	/**
	 * This method is used to escape all special characters and to create a Java string compatible for
	 * processing by the Java RegEx package. It is designed to be used when the COMMENT mode is on to allow for
	 * correct processing of pound (#) characters.
	 *
	 * @param in
	 *          The string to escape
	 * @return The escaped string
	 */
	public static String escapeRegExSpecialChars(final String in) {
		// # is because we default to COMMENTS mode to allow whitespace
		// 1/18/07 AP: updated with []{}& as per http://www.javapractices.com/Topic96.cjp
		final char[] specialChars = { '^', '$', '*', '+', '?', '.', '(', ')', '|', '{', '}', '#', '\\', '[', ']',
				'{', '}', '&' };
		return escapeRegExSpecialCharsImpl(in, specialChars);
	}

	/**
	 * This method is a special version that also escapes literal spaces. It is used when the COMMENT mode is on
	 * to allow for correct processing of space characters contained in string.
	 *
	 * @param in
	 *          The string to escape
	 * @return The escaped string
	 */
	public static String escapeRegExSpecialCharsSpace(final String in) {
		// # is because we default to COMMENTS mode to allow whitespace
		// 1/18/07 AP: updated with []{}& as per http://www.javapractices.com/Topic96.cjp
		final char[] specialChars = { '^', '$', '*', '+', '?', '.', '(', ')', '|', '{', '}', '#', ' ', '\\', '[',
				']', '{', '}', '&' };
		return escapeRegExSpecialCharsImpl(in, specialChars);
	}

	private static String escapeRegExSpecialCharsImpl(final String in, final char[] specialChars) {
		final StringBuilder resultString = new StringBuilder();
		char prevChar = ' ';

		for (int i = 0; i < in.length(); i++) {
			final char aChar = in.charAt(i);
			final char nextChar = i + 1 < in.length() ? in.charAt(i + 1) : ' ';

			if (prevChar != '\\' && isInArray(aChar, specialChars)) {
				// these are equally special to both Java and Regex. Accordingly Java should see 3 \\\, taking
				// the 1st 2 as representing a real \ char and then sending the other character on its own.
				// this results in the string: "\\\b" for example.
				if (aChar == '\\' && "btnfru".indexOf(nextChar) != -1) { //$NON-NLS-1$
					resultString.append("\\"); //$NON-NLS-1$
					resultString.append(aChar);
				}
				else if (aChar == '\\' && "\'\"".indexOf(nextChar) != -1) { //$NON-NLS-1$
					// do nothing special, special for Java but not for Regex
					resultString.append(aChar);
				}
				else {
					// we have a RegEx special character. It needs to result in 2\\ (one left after Java processing)
					// followed by the character. It also handles the special case of an input of: \\ where each has to
					// be doubled.
					resultString.append("\\\\"); //$NON-NLS-1$
					resultString.append(aChar);
				}
			}
			else {
				resultString.append(aChar);
			}

			prevChar = aChar;
		}

		return (resultString.toString());
	}

	public static boolean isInArray(final char aChar, final char[] cArr) {
		for (final char element : cArr) {
			if (element == aChar) {
				return true;
			}
		}

		return false;
	}

	public static String escapeSpecialSqlChars(final String in) {
		final StringBuilder resultString = new StringBuilder();

		for (int i = 0; i < in.length(); i++) {
			final char aChar = in.charAt(i);

			if (aChar == '\'') {
				resultString.append("''"); //$NON-NLS-1$
			}
			else if (aChar != '\r') {
				resultString.append(aChar);
			}
		}

		return (resultString.toString());
	}

	/**
	 * Returns the passed String with characters escaped for use in a XQuery.
	 *
	 * @param in
	 *          the String to escape
	 * @return the escaped String or "" if null was passed.
	 */
	public static String escapeForXQuery(final String in) {
		final StringBuilder retBuffer = new StringBuilder();
		final StringCharacterIterator it = new StringCharacterIterator(in);

		for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
			if (c == '\"') {
				retBuffer.append("&quot;"); //$NON-NLS-1$
			}
			else if (c == '&') {
				retBuffer.append("&amp;"); //$NON-NLS-1$
			}
			else if (c == '<') {
				retBuffer.append("&lt;"); //$NON-NLS-1$
			}
			else if (c == '>') {
				retBuffer.append("&gt;"); //$NON-NLS-1$
			}
			else if (c == '\'') {
				retBuffer.append("&apos;"); //$NON-NLS-1$
			}
			else if (c == '$') {
				retBuffer.append("&#36;"); //$NON-NLS-1$
			}
			else {
				retBuffer.append(c);
			}
		}

		return retBuffer.toString();
	}

	/**
	 * Returns the passed String with XML characters escaped.
	 *
	 * @param in
	 *          the String to escape
	 * @return the escaped String or "" if null was passed.
	 */
	public static String escapeForXML(final String in) {
		final StringBuilder retBuffer = new StringBuilder();
		final StringCharacterIterator it = new StringCharacterIterator(in);

		for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
			if (c == '\"') {
				retBuffer.append("&quot;"); //$NON-NLS-1$
			}
			else if (c == '&') {
				retBuffer.append("&amp;"); //$NON-NLS-1$
			}
			else if (c == '<') {
				retBuffer.append("&lt;"); //$NON-NLS-1$
			}
			else if (c == '>') {
				retBuffer.append("&gt;"); //$NON-NLS-1$
			}
			else if (c == '\'') {
				retBuffer.append("&apos;"); //$NON-NLS-1$
			}
			else {
				retBuffer.append(c);
			}
		}

		return retBuffer.toString();
	}

	/**
	 * Returns the passed String with XML characters escaped.
	 *
	 * Doesn't escape the quote and apostrophe in accordance with www.w3.org/TR/xml/#syntax
	 *
	 * @param in
	 *          the String to escape
	 * @return the escaped String or "" if null was passed.
	 */
	public static String escapeForXMLElement(final String in) {
		final StringBuilder retBuffer = new StringBuilder();
		final StringCharacterIterator it = new StringCharacterIterator(in);

		for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
			if (c == '&') {
				retBuffer.append("&amp;"); //$NON-NLS-1$
			}
			else if (c == '<') {
				retBuffer.append("&lt;"); //$NON-NLS-1$
			}
			else if (c == '>') {
				retBuffer.append("&gt;"); //$NON-NLS-1$
			}
			else {
				retBuffer.append(c);
			}
		}

		return retBuffer.toString();
	}

	/**
	 * Returns the passed String with any occurrence of " replaced by \" and any occurrence of \ replaced by \\.
	 *
	 * Courtesy of : (www.bastian-bergerhoff.com) Bastian Bergerhoff - initial API and implementation Georg
	 * Sendt - fixed a potential NPE
	 *
	 * @param p_text
	 *          the String to escape
	 * @return the escaped String or "" if null was passed.
	 */
	public static String escapeForJava(final String p_text) {
		final StringBuilder retBuffer = new StringBuilder();
		final StringCharacterIterator it = new StringCharacterIterator(p_text);

		for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
			if (c == '\\') {
				retBuffer.append("\\\\"); //$NON-NLS-1$
			}
			else if (c == '"') {
				retBuffer.append("\\\""); //$NON-NLS-1$
			}
			else {
				retBuffer.append(c);
			}
		}

		return retBuffer.toString();
	}

	public static String getSQLString(final String val) {
		if (isQuoted(val)) {
			String str = unQuoted(val);
			str = escapeSpecialSqlChars(str);
			str = quoted(str, '\'');
			return str;
		}
		return val;
	}

	public static int parseInt(final String str2) {
		String str = str2;
		if (isEmpty(str)) {
			return 0;
		}

		if (equals(left(str, 1), "+")) { //$NON-NLS-1$
			str = mid(str, 1);
		}

		return Integer.parseInt(str);
	}

	public static String intToStringWithSign(final int i) {
		String str = Integer.toString(i);

		if (i > 0) {
			str = "+" + str; //$NON-NLS-1$
		}

		return str;
	}

	/**
	 * Method that uses Java's StringTokenizer to return a specific token in a string using all the defaults
	 *
	 * @param str
	 * 	The string to tokenize
	 * @param idx
	 *  The zero based index of the token to return
	 * @return
	 *  The string for the requested token or an empty string if not found
	 */
	public static String getToken(final String str, final int idx) {
		return getToken(str, idx, null);
	}

	/**
	 * Method that uses Java's StringTokenizer to return a specific token in a string using all the defaults
	 *
	 * @param str
	 * 	The string to tokenize
	 * @param idx
	 *  The zero based index of the token to return
	 * @param delim
	 *  The delimiter to pass to StringTokenizer
	 * @return
	 *  The string for the requested token or an empty string if not found
	 */
	public static String getToken(final String str, final int idx, final String delim) {
		int cnt = 0;
		StringTokenizer st = null;

		if (delim != null) {
			st = new StringTokenizer(str, delim);
		}
		else {
			st = new StringTokenizer(str);
		}

		while (st.hasMoreTokens()) {
			if (++cnt == idx) {
				return st.nextToken();
			}
			st.nextToken(); // consume the token
		}

		return EMPTY_STRING;
	}

	public static String toSQLIdentifier(final String str2) {
		if (isEmpty(str2)) {
			return EMPTY_STRING;
		}

		String str = str2;
		str = replaces("-", str, "_"); //$NON-NLS-1$ //$NON-NLS-2$
		final int lgth = str.length();
		final StringBuilder result = new StringBuilder(lgth);

		for (int i = 0; i < lgth; i++) {
			final char c = str.charAt(i);

			if (Character.isLetterOrDigit(c) || c == '_') {
				result.append(c);
			}
		}

		return result.toString();
	}

	public static String toIdentifier(final String str2) {
		if (isEmpty(str2)) {
			return EMPTY_STRING;
		}

		String str = str2;
		str = replaces("-", str, "_"); //$NON-NLS-1$ //$NON-NLS-2$
		final int lgth = str.length();  //NOSONAR protected by isEmpty
		final StringBuilder result = new StringBuilder(lgth);

		for (int i = 0; i < lgth; i++) {
			final char c = str.charAt(i);

			if (Character.isLetterOrDigit(c) || c == '_') {
				result.append(c);
			}
			else {
				result.append('_');
			}
		}

		return result.toString();
	}

	/**Function to return a positional argument in a string based on a supplied separator.
	 *
	 * @param inStr2
	 * 					The string where to look
	 * @param sep
	 * 					The separator string (can be a single character or a long string)
	 * @param argNbr
	 * 					The number of the argument to return. Arguments are 1-based.
	 * @return
	 * 					The extracted argument if found, an empty string otherwise
	 */
	public static String getPosArg(final String inStr2, final String sep, final int argNbr) {
		String inStr = inStr2;
		if (isEmpty(inStr)) {
			return EMPTY_STRING;
		}

		int pos = 1;
		int nextSep = inStr.indexOf(sep);

		while (pos < argNbr && nextSep != -1) {
			inStr = inStr.substring(nextSep + sep.length());
			nextSep = inStr.indexOf(sep);
			pos += 1;
		}

		if (pos == argNbr) {
			if (nextSep != -1) {
				return inStr.substring(0, nextSep);
			}
			return inStr;
		}
		return EMPTY_STRING;
	}

	public static List<String> splitToList(final String inStr2, final String sep, final boolean noEmpty) {
		String inStr = inStr2;
		if (isEmpty(inStr)) {
			return Collections.emptyList();
		}

		if (noEmpty && left(inStr, 1).equals(sep)) {
			inStr = inStr.substring(sep.length());
		}

		final String[] elements = inStr.split(sep);

		return Arrays.asList(elements);
	}

	public static String splitCamelCase(final String inStr2) {
		String inStr = inStr2;
		if (isEmpty(inStr)) {
			return inStr;
		}

		final int lgth = inStr.length();
		StringBuilder result = new StringBuilder(lgth);

		for (int i = 0; i < lgth; i++) {
			final char c = inStr.charAt(i);

			if (i > 0 && Character.isUpperCase(c)) {
				result.append(' ');
			}

			result.append(c);
		}

		return result.toString();
	}

	public static List<String> splitToList(final String inStr2, final int length) {
		String inStr = inStr2;
		if (isEmpty(inStr)) {
			return Collections.emptyList();
		}

		final List<String> list = new ArrayList<>();
		final int lgth = inStr.length();
		StringBuilder result = new StringBuilder(lgth);

		for (int i = 0; i < lgth; i++) {
			final char c = inStr.charAt(i);
			result.append(c);

			if ((i+1) % length == 0) {
				list.add(result.toString());
				result = new StringBuilder(lgth);
			}
		}

		if (result.toString().length() > 0) {
			list.add(result.toString());
		}

		return list;
	}

	public static boolean contains(final String[] strings, final String string) {
		if (strings.length == 0 || string == null) {
			return false;
		}

		for (final String element : strings) {
			if (element.equals(string)) {
				return true;
			}
		}

		return false;
	}

	public static boolean containsIgnoreCase(final String string, final String contained) {
		if (isEmpty(string)) {
			return false;
		}
		String upperString = string.toUpperCase();
		String upperContained = contained.toUpperCase();
		return upperString.contains(upperContained);
	}

	/**
	 * @param string The string to search in
	 * @param strings The array of string to see if they are contained in string
	 * @return	true if any of the strings instance is contained in search string
	 */
	public static boolean containsOneOf(final String string, final String... strings) {
		if (string == null || strings.length == 0) {
			return false;
		}

		for (final String s : strings) {
			if (string.contains(s)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param string The string to search in
	 * @param strings The array of string to see if they are not contained in string
	 * @return	true if none of the strings instance is contained in search string
	 */
	public static boolean containsNoneOf(final String string, final String... strings) {
		return !containsOneOf(string, strings);
	}

	/**
	 * @param string The string to search in
	 * @param strings The array of string to test if they are "startsWith" in string
	 * @return	true if any of the strings instance is the start of search string
	 */
	public static boolean startsWithOneOf(final String string, final String... strings) {
		if (string == null || strings.length == 0) {
			return false;
		}

		for (final String s : strings) {
			if (string.startsWith(s)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param string The string to search in
	 * @param strings The array of string to test if they are "endsWith" in string
	 * @return	true if any of the strings instance is the end of search string
	 */
	public static boolean endsWithOneOf(final String string, final String... strings) {
		if (string == null || strings.length == 0) {
			return false;
		}

		for (final String s : strings) {
			if (string.endsWith(s)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param string The string to search in
	 * @param strings The array of string to test if they match the search string
	 * @return	true if any of the strings instance matches the search string
	 */
	public static boolean isOneOf(final String string, final String... strings) {
		if (string == null || strings.length == 0) {
			return false;
		}

		for (final String s : strings) {
			if (equals(string, s)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Indent a line or a number of lines by a specific indentation level. The indentation is performed by
	 * insuring that every line has the required number of tabs or spaces depending on the useTabs settings.
	 *
	 * @param inStr2
	 *          The string to indent. This string can be more than 1 line long.
	 * @param indentLevel
	 *          The indentation level required
	 * @param indentSize
	 *          The size of each indent level in space character (required for useTabs false)
	 * @param useTabs
	 *          Flags indicating if the indentation should be performed with tabs or spaces
	 * @return The indented string where each line is indented
	 */
	public static String indent(final String inStr2, final int indentLevel, final int indentSize,
			final boolean useTabs) {
		return indent(inStr2, indentLevel, indentSize, useTabs, true);
	}

	/**
	 * Indent a line or a number of lines by a specific indentation level. The indentation is performed by
	 * insuring that every line has the required number of tabs or spaces depending on the useTabs settings.
	 *
	 * @param inStr2
	 *          The string to indent. This string can be more than 1 line long.
	 * @param indentLevel
	 *          The indentation level required
	 * @param indentSize
	 *          The size of each indent level in space character (required for useTabs false)
	 * @param useTabs
	 *          Flags indicating if the indentation should be performed with tabs or spaces
	 * @param reformat
	 *          Flags indicating if the line should be cleared of leading whitespace or left alone
	 * @return The indented string where each line is indented
	 */
	public static String indent(final String inStr2, final int indentLevel, final int indentSize,
			final boolean useTabs, final boolean reformat) {
		String inStr = inStr2;
		if (isEmpty(inStr)) {
			return EMPTY_STRING;
		}

		final StringBuilder sb = new StringBuilder();
		final Pair<Integer, Integer> pair = getLineFeedPosSize(inStr);
		int nextSep = pair.a.intValue();
		String sep = EMPTY_STRING;

		if (nextSep != -1) {
			sep = inStr.substring(nextSep, nextSep + pair.b.intValue());
		}

		while (nextSep != -1) {
			if (useTabs) {
				sb.append(repeatChar('\t', indentLevel));
			}
			else {
				sb.append(repeatChar(' ', indentLevel * indentSize));
			}

			if (reformat) {
				sb.append(trimLeadingWhiteSpaces(inStr.substring(0, nextSep + sep.length()), false));
			}
			else {
				sb.append(inStr.substring(0, nextSep + sep.length()));
			}

			inStr = inStr.substring(nextSep + sep.length());
			nextSep = inStr.indexOf(sep);
		}

		if (!isEmpty(inStr)) {
			if (useTabs) {
				sb.append(repeatChar('\t', indentLevel));
			}
			else {
				sb.append(repeatChar(' ', indentLevel * indentSize));
			}

			if (reformat) {
				sb.append(trimLeadingWhiteSpaces(inStr));
			}
			else {
				sb.append(inStr);
			}
		}

		return sb.toString();
	}

	/**
	 * Indent a line or a number of lines by a specific indent. The indentation is performed by prefixing every
	 * line with the required number of spaces.
	 *
	 * @param inStr
	 *          The string to indent
	 * @param indent
	 *          The number of spaces to indent by
	 * @return The indented string where each line is indented
	 */
	public static String indent(final String inStr, final int indent) {
		return indent(inStr, indent, 1, false);
	}

	/**
	 * Indent a line or a number of lines by a specific tab indent. The indentation is performed by prefixing every
	 * line with the required number of tabs.
	 *
	 * @param inStr
	 *          The string to indent
	 * @param indent
	 *          The number of tab to indent by
	 * @return The indented string where each line is indented
	 */
	public static String tab(final String inStr, final int indent) {
		return indent(inStr, indent, 0, true, false);
	}

	/**
	 * Indents a line or a number of lines around a title. The first line contains the title and first line.
	 * Each subsequent line is indented to match where the content started on the first line.
	 *
	 * @param title
	 *          The title to show on the first line
	 * @param content
	 *          The content to display
	 * @return The formatted content
	 */
	public static String indentToTitle(final String title, final String content2) {
		String content = content2;
		if (isEmpty(title)) {
			return EMPTY_STRING;
		}

		final StringBuilder sb = new StringBuilder();
		sb.append(title);

		final Pair<Integer, Integer> pair = getLineFeedPosSize(content);
		final int nextSep = pair.a.intValue();

		if (nextSep != -1) {
			String sep = content.substring(nextSep, nextSep + pair.b.intValue());
			sb.append(content.substring(0, nextSep + sep.length()));
			content = content.substring(nextSep + sep.length());
			sb.append(indent(content, title.length(), 1, false, false));
		}
		else {
			sb.append(content);
		}

		return sb.toString();
	}

	/**
	 * This method is used to help create a visual tree representation by creating vertical bars that together
	 * with all the lines produces a tree vertical level indicator.
	 *
	 * @param indentLevel
	 *          The current indentation level of the tree
	 * @param indentSize
	 *          The size of the indentation or spacing between each level (or |)
	 * @return A prefix string of all relevant vertical bars to be placed in the tree
	 */
	public static String indentTreePrefix(final int indentLevel, final int indentSize) {
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < indentLevel; i++) {
			sb.append('|');
			sb.append(repeatChar(' ', indentSize - 1));
		}

		return sb.toString();
	}

	/**
	 * Convenience method calling {@link #subLines(String, int, int, boolean, String)}
	 */
	public static String subLines(final String inStr, final int firstLine) {
		return subLines(inStr, firstLine, Integer.MAX_VALUE, false);
	}

	/**
	 * Convenience method calling {@link #subLines(String, int, int, boolean, String)}
	 */
	public static String subLines(final String inStr, final int firstLine, final boolean showTrailingDots) {
		return subLines(inStr, firstLine, Integer.MAX_VALUE, showTrailingDots);
	}

	/**
	 * Convenience method calling {@link #subLines(String, int, int, boolean, String)}
	 */
	public static String subLines(final String inStr, final int firstLine, final int lastLine) {
		return subLines(inStr, firstLine, lastLine, false);
	}

	/**
	 * Convenience method calling {@link #subLines(String, int, int, boolean, String)}
	 */
	public static String subLines(final String inStr, final int firstLine, final int lastLine,
			final boolean showTrailingDots) {
		return subLines(inStr, firstLine, lastLine, showTrailingDots, EMPTY_STRING);
	}

	/**
	 * This method takes a multi-line string and allows to extract a certain number of lines from it based on
	 * the supplied arguments. It is also possible to show some trailing dots if the last line returned is not
	 * the last line of the input string
	 *
	 * @param inStr
	 *          The multi-line string to extract the content from
	 * @param firstLine
	 *          The first line to return (1-based)
	 * @param lastLine
	 *          The last line to return (1-based)
	 * @param showTrailingDots
	 *          Boolean indicating if we want to show trailing dots if the last line is not the end of the input
	 *          string
	 * @param linePrefix
	 *          A prefix to add to each line of output, like for commenting using line comments (// or ')
	 * @return The matching extracted ranges of lines
	 */
	public static String subLines(final String inStr, final int firstLine2, final int lastLine,
			final boolean showTrailingDots, final String linePrefix) {
		int cnt = 0;
		final StringBuilder sb = new StringBuilder();
		final BufferedReader br = new BufferedReader(new StringReader(inStr));
		int firstLine = firstLine2;
		if (isEmpty(inStr)) {
			return EMPTY_STRING;
		}

		if (firstLine < 1) {
			firstLine = 1;
		}

		if (lastLine < firstLine) {
			return EMPTY_STRING;
		}

		try {
			String lineContent = br.readLine();

			while (lineContent != null) {
				cnt++;

				if (cnt >= firstLine && cnt <= lastLine) {
					if (!isEmpty(linePrefix)) {
						sb.append(linePrefix);
					}

					sb.append(lineContent);

					if (firstLine != lastLine) {
						sb.append(LF);
					}
				}
				else if (cnt > lastLine) {
					/*
					 * One last usability check. If this is the last line in the original string, we suppress printing
					 * the ellipses.
					 *
					 * E.g. subLines("Hello World\nGoodbye World\n", 2, 2, true) should yield "Goodbye World" as
					 * opposed to "Goodbye World..."
					 */
					if (showTrailingDots && br.readLine() != null) {
						sb.append(ELLIPSIS);

						if (firstLine != lastLine) {
							sb.append(LF);
						}
					}
					break;
				}

				lineContent = br.readLine();
			}
		}
		catch (final IOException ex) {
			return EMPTY_STRING;
		}

		return sb.toString();
	}

	public static String doXMLEscaping(final String unescapedString) {
		String returnVal = unescapedString;

		if (!isEmpty(returnVal)) {
			// Note: Order matters! & -> &amp; should be first.
			returnVal = returnVal.replace("&", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$
			returnVal = returnVal.replace("\"", "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$
			returnVal = returnVal.replace("<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
			returnVal = returnVal.replace(">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$
			returnVal = returnVal.replace("\r", getUnicodeEntityReference("\r")); // &#x00D //$NON-NLS-1$ //$NON-NLS-2$
			returnVal = returnVal.replace(LF, getUnicodeEntityReference(LF)); // &#x00A
			returnVal = returnVal.replace("\t", getUnicodeEntityReference("\t")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return returnVal;
	}

	public static String getUnicodeEntityReference(final String c) {
		final int codepoint = Character.codePointAt(c, 0);
		String hexString = Integer.toHexString(codepoint);

		while (hexString.length() < 4) {
			hexString = "0" + hexString; //$NON-NLS-1$
		}

		return "&#x" + hexString + ";"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static int getWordCount(final String inStr2) {
		if (isEmpty(inStr2)) {
			return 0;
		}
		String inStr = inStr2.trim();

		if (inStr.isEmpty()) {
			return 0;
		}

		return inStr.split("\\s+").length; //$NON-NLS-1$
	}

	/**
	 * This method returns the number of lines occupied by a String. It will automatically determine the line
	 * delimiter and do its calculation. It doesn't support mixed line delimiters at this point. A last line of
	 * characters not ended with a line delimiter is counted as a line. Also a line ending with a new line
	 * delimiters counts that new line as an extra line
	 *
	 * @param inStr
	 *          The string to get the line count from
	 * @return The number of lines occupied by the argument string (not the number of line delimiters)
	 */
	public static int getLineCount(final String inStr2) {
		String inStr = inStr2;
		if (isEmpty(inStr)) {
			return 0;
		}

		int lnCnt = 1;
		final Pair<Integer, Integer> pair = getLineFeedPosSize(inStr);
		int nextSep = pair.a.intValue();
		String sep = EMPTY_STRING;

		if (nextSep != -1) {
			sep = inStr.substring(nextSep, nextSep + pair.b.intValue());
		}

		while (nextSep != -1) {
			lnCnt++;
			inStr = inStr.substring(nextSep + sep.length());
			nextSep = inStr.indexOf(sep);
		}

		return lnCnt;
	}

	/**
	 * This method returns the count of a specific character in a string.
	 *
	 * @param inStr
	 *          The string to check for the character
	 * @param rptChar
	 * 					The character to find the count for in the string
	 * @return
	 * 					The number of lines occupied by the argument string (not the number of line delimiters)
	 */
	public static int getCharRepeatCount(final String inStr, final char rptChar) {
		if (isEmpty(inStr)) {
			return 0;
		}

		final int lgth = inStr.length();
		int cnt = 0;

		for (int i = 0; i < lgth; i++) {
			final char c = inStr.charAt(i);

			if (c == rptChar) {
				cnt++;
			}
		}

		return cnt;
	}

	/**
	 * This method returns the position of the first linefeed along with its size. It has support for the 3 most
	 * popular formats out here. It supports Windows (CR (0d), LF (0a)), Unix (LF) and Mac (CR). Only Windows
	 * has a size of 2, instead of 1.
	 *
	 * a is position, b is size.
	 *
	 * if str does not contain any linefeeds, position will be -1 and size will be 0.
	 */
	public static Pair<Integer, Integer> getLineFeedPosSize(final String str) {
		int pos = -1;
		int size = 0;

		if (!isEmpty(str)) {
			final char[] cbuf = str.toCharArray();
			final int cnt = cbuf.length;

			if (cnt > 0) {
				for (int i = 0; i < cnt; i++) {
					final char c = cbuf[i];

					if (c == '\r') {
						pos = i;
						size = 1;

						if ((i + 1) < cnt && cbuf[i + 1] == '\n') {
							size = 2;
						}

						break;
					}
					else if (c == '\n') {
						pos = i;
						size = 1;
						break;
					}
				}
			}
		}

		return new Pair<>(Integer.valueOf(pos), Integer.valueOf(size));
	}

	/**
	 * This method returns the position of the last linefeed along with its size. It has support for the 3 most
	 * popular formats out here. It supports Windows (CR (0d), LF (0a)), Unix (LF) and Mac (CR). Only Windows
	 * has a size of 2, instead of 1.
	 */
	public static Pair<Integer, Integer> getLastLineFeedPosSize(final String str) {
		int pos = -1;
		int size = 0;

		final char[] cbuf = str.toCharArray();
		final int cnt = cbuf.length;

		if (cnt > 0) {
			for (int i = cnt - 1; i >= 0; i--) {
				final char c = cbuf[i];

				if (c == '\n') {
					pos = i;
					size = 1;

					if ((i - 1) >= 0 && cbuf[i - 1] == '\r') {
						pos--;
						size = 2;
					}

					break;
				}
				else if (c == '\r') {
					pos = i;
					size = 1;
					break;
				}
			}
		}

		return new Pair<>(Integer.valueOf(pos), Integer.valueOf(size));
	}

	/**
	 * This method takes a string and an offset and determines the column at the offset and the string length
	 * based on the supplied tabSize with the tabs expanded.
	 *
	 * Returned columns are 1-based.
	 *
	 */
	public static Pair<Integer, Integer> getColLgth(final String str, final int offset, final int tabSize) {
		int col = 0;
		int lgth = 0;

		final char[] cbuf = str.toCharArray();
		final int cnt = cbuf.length;

		if (cnt > 0) {
			for (int i = 0; i < cnt; i++) {
				final char c = cbuf[i];

				if (c == '\t') {
					if (lgth % tabSize == 0) {
						lgth += tabSize;
					}
					else {
						lgth += lgth % tabSize;
					}
				}
				else {
					lgth++;
				}

				if (i == offset) {
					col = lgth;
				}
			}
		}

		return new Pair<>(Integer.valueOf(col), Integer.valueOf(lgth));
	}

	/**
	 * This method takes a string and an offset and determines the line and column at the offset
	 * based on the supplied tabSize with the tabs expanded.
	 *
	 * Returned lines and columns are 1-based.
	 *
	 */
	public static Pair<Integer, Integer> getLineCol(String str, int offset, final int tabSize) {
		int line = 0;
		int scanned = 0;

		Pair<Integer, Integer> pair = ctString.getLineFeedPosSize(str);

		while (true) {
			final String lnContent = pair.a == -1 ? substring(str, 0) : substring(str, 0, pair.a);
			final char[] cbuf = lnContent.toCharArray();
			final int cnt = cbuf.length;

			if (cnt > 0) {
				int lgth = 0;
				line++;

				for (int i = 0; i < cnt; i++) {
					final char c = cbuf[i];

					if (c == '\t') {
						if (lgth % tabSize == 0) {
							lgth += tabSize;
						}
						else {
							lgth += lgth % tabSize;
						}
					}
					else {
						lgth++;
					}

					if ((scanned + i) == offset) {
						return new Pair<>(Integer.valueOf(line), Integer.valueOf(lgth));
					}
				}
			}

			if (pair.a == -1) {
				break;
			}

			scanned += cnt + pair.b;
			str = substring(str, pair.a + pair.b);
			pair = ctString.getLineFeedPosSize(str);
		}

		return null;
	}

	/**
	 * This method takes a string and a column and determines the offset at which this column occurs based on
	 * the supplied tabSize with the tabs expanded.
	 *
	 * Note that if the requested column is within a tab expansion, the returned value will be the next "usable"
	 * column as identified in the 2nd part of the pair value.
	 *
	 * Column argument is 1-based.
	 *
	 * An offset (pair.a) value of -1 indicates that the col doesn't exist in the supplied string. In that case,
	 * a column (pair.b) value 0f 0 indicates an empty string, a value of < 0 indicates the number of characters
	 * by which the string was not long enough to reach the requested column.
	 *
	 */
	public static Pair<Integer, Integer> getColumnOffset(final String str, final int col, final int tabSize) {
		boolean done = false;
		int offset = -1;
		int lgth = 0;

		if (!isEmpty(str)) {
			final char[] cbuf = str.toCharArray();
			final int cnt = cbuf.length;

			if (cnt > 0) {
				for (int i = 0; i < cnt; i++) {
					final char c = cbuf[i];
					offset = i;

					if (c == '\t') {
						if (lgth % tabSize == 0) {
							lgth += tabSize;
						}
						else {
							lgth += lgth % tabSize;
						}
					}
					else {
						lgth++;
					}

					if (lgth >= col) {
						done = true;
						break;
					}
				}

				if (!done) {
					offset = -1; // not found
					lgth -= col; // scan past the end
				}
			}
		}

		return new Pair<>(Integer.valueOf(offset), Integer.valueOf(lgth));
	}

	/**
	 * This method produces a string with tabs and spaces of the size that will expand to the end column value
	 * based on the supplied startCol value (i.e. what it takes to cover the "space" between the start column
	 * and the end column).
	 *
	 * @param startCol
	 *          1-based column number of the current start column position. This is used in order to correctly
	 *          handle cases where the start position doesn't fall on proper tab boundaries.
	 * @param endCol
	 *          1-based column number of the end column represented by the returned string
	 * @param tabSize
	 *          The size of the tabs to include in the string. A value of zero will disable tabs and only use
	 *          spaces to produce the string.
	 * @return
	 * 					The string to provide the correct indentation
	 */
	public static String getIndentationString(final int startCol, final int endCol, final int tabSize) {
		final StringBuilder sb = new StringBuilder();
		int colsToAdd = endCol - startCol;

		if (tabSize == 0) {
			sb.append(repeatChar(' ', colsToAdd));
		}
		else {
			if ((startCol - 1) % tabSize != 0) {
				int s = tabSize - ((startCol - 1) % tabSize);
				s = Math.min(s, colsToAdd);
				sb.append(repeatChar(' ', s));
				colsToAdd -= s;
			}

			sb.append(repeatChar('\t', colsToAdd/tabSize));
			sb.append(repeatChar(' ', colsToAdd % tabSize));
		}

		return sb.toString();
	}

	public static int getIndentLevel(final String str, final int tabSize) {
		int indentLevel = 0;
		int spaces = 0;
		final char[] cbuf = str.toCharArray();
		final int cnt = cbuf.length;

		if (cnt <= 0) {
			return 0;
		}

		for (int i = 0; i < cnt; i++) {
			final char c = cbuf[i];

			if (c == '\t') {
				indentLevel++;
				spaces = 0;
			}
			else if (c == ' ') { // spaces that are completed by a tab loose all meaning (i.e. tabSize of 4, SpSpTb
														// is one indent)
				if (++spaces == tabSize) {
					indentLevel++;
					spaces = 0;
				}
			}
			else { // we have some text, this is the end of the left whitespace area
				break;
			}
		}

		return indentLevel;
	}

	/**
	 * Scans a pair of string forward and backward to identify common areas on both the left and right hand
	 * side.
	 *
	 * @param str1
	 *          First string to be compared
	 * @param str2
	 *          Second string to be compared
	 * @return A Pair value with the 1st item giving the index of common characters on the left hand side and
	 *         the 2nd value giving the index of common characters on the right hand side. Either value will
	 *         return -1 if no common area found.
	 */
	public static Pair<Integer, Integer> getCommonAreas(final String str1, final String str2) {
		int leftCommon = -1;
		int rightCommon = -1;

		if (!isEmpty(str1) && !isEmpty(str2)) {
			final char[] cbuf1 = str1.toCharArray();
			final char[] cbuf2 = str2.toCharArray();
			final int cnt1 = cbuf1.length;
			final int cnt2 = cbuf2.length;

			if (cnt1 <= 0 || cnt2 <= 0) {
				return new Pair<>(Integer.valueOf(-1), Integer.valueOf(-1));
			}

			for (int i = 0; i < Math.min(cnt1, cnt2); i++) {
				if (cbuf1[i] != cbuf2[i]) {
					break;
				}

				leftCommon = i;
			}

			for (int i = 0; i < Math.min(cnt1, cnt2); i++) {
				if (cbuf1[cnt1 -i -1] != cbuf2[cnt2 -i -1]) {
					break;
				}

				rightCommon = i;
			}
		}

		return new Pair<>(Integer.valueOf(leftCommon), Integer.valueOf(rightCommon));
	}

	/**
	 * This method is useful for ensuring a string is not too long, e.g. when it needs to be displayed to the
	 * user. If the string is longer than the maximum length, it is truncated, and the string "..." is appended
	 * at the end. The returned string is guaranteed to be no longer than maxLength, as long as maxLength is
	 * greater than 3.
	 *
	 * @param input
	 *          the string to truncate.
	 * @param maxLength
	 *          the maximum length of the string to return. Should be at least 3.
	 * @return the original string, if it is shorter than the maxLength provided, otherwise a string which
	 *         contains the beginning of the original string, ending in "...", and no longer than the maxLength.
	 */
	/**
	 * @param input
	 * @param maxLength
	 * @return
	 */
	public static String humanFriendlyTruncate(final String input2, final int maxLength) {
		String input = input2;
		final String suffix = ELLIPSIS;
		final int suffixLength = suffix.length();
		final int realMaxLength = Math.max(maxLength, suffixLength);

		if (isEmpty(input)) {
			return EMPTY_STRING;
		}

		input = unwrapLine(input, false);

		if (input.length() < realMaxLength) {
			return input;
		}

		String truncated = input.substring(0, realMaxLength - suffixLength) + suffix;

		//make sure to balance double quotes (") and not truncate the last one which will break editor parsing later
		if (!quoteBalanced(truncated, '"')) {
			truncated += '"';
		}

		return truncated;
	}

	/**
	 * This method takes a string and breaks it up in an array of String, each representing a line from the
	 * original long string.
	 *
	 * @param line
	 *          The long line that we want to wrap/split into many different lines
	 * @param wrapColumn
	 *          The column at which to wrap the line and produce new lines
	 * @param tabSize
	 *          The tabsize in effect
	 * @param breakWords
	 *          Boolean that determines if portion of string longer than the line length should be broken at
	 *          non-word boundaries to fit in the wrapColumn or if we can exceed the width if necessary. This is
	 *          like saying the wrapColumn is a strict value or not.
	 * @return
	 */
	public static List<String> wrapLine(final String line2, final int wrapColumn, final int tabSize,
			final boolean breakWords) {
		String line = line2;
		final ArrayList<String> wrappedLines = new ArrayList<>();

		Pair<Integer, Integer> pair = getColLgth(line, wrapColumn, tabSize);
		int lgth = pair.b.intValue();

		while (lgth > wrapColumn) {
			int spaceToWrapAt = lastIndexOf(line, new char[] { ' ', '\t' }, wrapColumn);

			if (spaceToWrapAt >= 0) {
				wrappedLines.add(line.substring(0, spaceToWrapAt));
				line = line.substring(spaceToWrapAt + 1);
			}
			else { // This must be a really long word or URL.
				if (breakWords) {
					wrappedLines.add(line.substring(0, wrapColumn));
					line = line.substring(wrapColumn);
				}
				else {
					spaceToWrapAt = indexOf(line, new char[] { ' ', '\t' }, wrapColumn);

					if (spaceToWrapAt >= 0) {
						wrappedLines.add(line.substring(0, spaceToWrapAt));
						line = line.substring(spaceToWrapAt + 1);
					}
					else {
						wrappedLines.add(line.substring(0, spaceToWrapAt));
						line = EMPTY_STRING;
					}
				}
			}

			pair = getColLgth(line, wrapColumn, tabSize);
			lgth = pair.b.intValue();
		}

		wrappedLines.add(line);

		return wrappedLines;
	}

	/**
	 * This method takes a string and replaces all of the line breaks with either space or with the string
	 * representation of the delimiter character(s) from the original long string.
	 *
	 * @param line
	 *          The long line that we want to unwrap/join into a single line
	 * @param showDelimiters
	 *          Boolean that determines if the delimiters will be shown in the unwrapped string. The default is
	 *          to replace the delimiter(s) by a space.
	 * @return
	 */
	public static String unwrapLine(final String inStr2, final boolean showDelimiters) {
		String inStr = inStr2;
		if (isEmpty(inStr)) {
			return EMPTY_STRING;
		}

		final StringBuilder sb = new StringBuilder();
		final Pair<Integer, Integer> pair = getLineFeedPosSize(inStr);
		int nextSep = pair.a.intValue();
		String sep = EMPTY_STRING;

		if (nextSep != -1) {
			sep = inStr.substring(nextSep, nextSep + pair.b.intValue());
		}

		while (nextSep != -1) {
			sb.append(trimLeadingWhiteSpaces(inStr.substring(0, nextSep)));

			if (showDelimiters) {
				sb.append(escapeLineDelimiterChars(sep));
			}
			else {
				sb.append(' ');
			}

			inStr = inStr.substring(nextSep + sep.length());
			nextSep = inStr.indexOf(sep);
		}

		if (!isEmpty(inStr)) {
			sb.append(trimLeadingWhiteSpaces(inStr));
		}

		return sb.toString();
	}

	public static String printList(final String prefix, final List<String> list, final boolean onOneLine) {
		boolean first = true;
		final StringBuilder sb = new StringBuilder();

		if (prefix != null) {
			sb.append(prefix);
			sb.append(":"); //$NON-NLS-1$
		}

		for (final String str : list) {
			if (!first) {
				if (onOneLine) {
					sb.append(", "); //$NON-NLS-1$
				}
				else {
					sb.append(System.getProperty("line.separator")); //$NON-NLS-1$
				}
			}

			sb.append(str);
			first = false;
		}

		return sb.toString();
	}

	/**
	 * Prints the given {@link Throwable}'s stack trace into a String.
	 *
	 * @param t
	 *          the {@link Throwable} to get the stack trace from
	 * @return the String containing the printed stack trace
	 */
	public static String printStackTrace(final Throwable t) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, false);
		t.printStackTrace(pw);
		pw.flush();
		final String stacktrace = sw.toString();
		pw.close();
		return stacktrace.replace("\r\n", LF); //$NON-NLS-1$
	}

	public static String underScoreToCamelCase(final String str2) {
		String str = str2;
		final StringBuilder sb = new StringBuilder();

		if (!isEmpty(str)) {
			str = str.toLowerCase();

			final int lgth = str.length();

			for (int i = 0; i < lgth; i++) {
				final char c = str.charAt(i);

				if (c == '_') {
					sb.append(Character.toUpperCase(str.charAt(++i)));
				}
				else {
					sb.append(c);
				}
			}
		}

		return sb.toString();
	}

	public static String ensureNotNull(final String input) {
		if (input == null) {
			return EMPTY_STRING;
		}

		return input;
	}

	public static String ensureNotNull(final Object input) {
		return input == null ? EMPTY_STRING : ensureNotNull(input.toString());
	}

	public static String streamToString(final InputStream in) throws IOException {
		final StringBuilder out = new StringBuilder();
		final BufferedInputStream bis = new BufferedInputStream(in);
		final byte[] b = new byte[32768];

		for (int n; (n = bis.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}

		return out.toString();
	}

	/**
	 * Simply removes all duplicate spaces from the incoming string
	 */
	public static String removeDuplicateSpaces(final String inStr) {
		final Matcher m = spacesRegExp.matcher(inStr);

		return m.replaceAll(" "); //$NON-NLS-1$
	}

	/**
	 * Simply removes all duplicate spaces from the incoming string
	 *
	 * @param inStr
	 * @param duplicateCnt	Number of duplicate repeated space character to look for in replacement
	 *
	 * @return
	 */
	public static String removeDuplicateSpaces(final String inStr, int duplicateCnt) {
		final Pattern p = Pattern.compile("\\s{" + duplicateCnt + ",}"); //$NON-NLS-1$ //$NON-NLS-2$
		final Matcher m = p.matcher(inStr);

		return m.replaceAll(ctString.repeatChar(' ', duplicateCnt));
	}

	/**
	 * Does the same as {@link #removeDuplicateSpaces(String)} but keeps the new-line characters.
	 */
	public static String removeDuplicateSpacesKeepLines(final String inStr) {
		if (isEmpty(inStr)) {
			return inStr;
		}

		final StringBuilder sb = new StringBuilder();
		final String[] lines = removeDuplicateSpacesInLines(inStr);

		sb.append(lines[0]);
		for (int i = 1; i < lines.length; i++) {
			sb.append('\n');
			sb.append(lines[i]);
		}

		return sb.toString();
	}

	/**
	 * Builds an array of all the lines in the given String, each getting duplicate spaces removed.
	 */
	public static String[] removeDuplicateSpacesInLines(final String inStr) {
		if (inStr == null) {
			return null;
		}

		final BufferedReader br = new BufferedReader(new StringReader(inStr));

		List<String> resultList = new ArrayList<>();
		String line;

		try {
			while ((line = br.readLine()) != null) {
				final Matcher m = spacesRegExp.matcher(line);
				resultList.add(m.replaceAll(" ")); //$NON-NLS-1$
			}
		}
		catch (IOException e) {
			return new String[] {inStr};
		}

		String[] result = new String[] {};
		return resultList.toArray(result);
	}

	public static String toUpperCase(final String s) {
		if (isEmpty(s)) {
			return EMPTY_STRING;
  	}
		return s.toUpperCase();
  }

	/**
	 * Simply removes all duplicate linefeeds from the incoming string
	 */
	public static String removeDuplicateLinefeeds(final String inStr) {
		final Matcher m = lfRegExp.matcher(inStr);
		return m.replaceAll("\n"); //$NON-NLS-1$
	}

	/**
	 * Returns the provided string with the first character of the string in uppercase. If there is no first
	 * character, the string is returned unchanged. I.e. if the string is null, null is returned, and if the
	 * string is the empty string, the empty string is returned.
	 *
	 * @param s
	 *          the string to convert.
	 * @return the converted string.
	 */
	public static String capitalizeFirstLetter(final String s) {
		if (isEmpty(s)) {
			return EMPTY_STRING;
		}
		final String firstCharacter = s.substring(0, 1);
		return trim(firstCharacter.toUpperCase() + s.substring(1, s.length()));
	}

	public static String lowerCaseFirstLetter(final String name) {
		if (isEmpty(name)) {
			return EMPTY_STRING;
		}
		final String firstCharacter = name.substring(0, 1);
		return firstCharacter.toLowerCase() + name.substring(1, name.length());
	}

	public static String properCase(final String name) {
		if (isEmpty(name)) {
			return EMPTY_STRING;
		}
		final String firstCharacter = name.substring(0, 1);
		final String remaind = name.substring(1, name.length());

		return firstCharacter.toUpperCase() + remaind.toLowerCase();
	}

	public static String properCaseUnderScoreToSpaceCase(final String str2) {
		String str = str2;
		final StringBuilder sb = new StringBuilder();

		if (!isEmpty(str)) {
			str = str.toLowerCase();

			final int lgth = str.length();

			for (int i = 0; i < lgth; i++) {
				final char c = str.charAt(i);
				if (i == 0) {
					sb.append(Character.toUpperCase(str.charAt(i)));
				}
				else if (c == '_') {
					sb.append(' ');
					if (i < lgth -1) {  //make sure string doesn't end with _
						sb.append(Character.toUpperCase(str.charAt(++i)));  //NOSONAR: get real
					}
				}
				else {
					sb.append(c);
				}
			}
		}

		return sb.toString();
	}

	public static String removeSpaces(final String name) {
		if (isEmpty(name)) {
			return EMPTY_STRING;
		}
		return replaces(" ", name, EMPTY_STRING); //$NON-NLS-1$
	}

	/**
	 * @param leftStr		The left part of the code
	 * @param pivotStr	The pivot string usually an assignment operator or TO in Cobol
	 * @param rightStr	The right part of the code
	 * @param pivotPos	The position to align the pivot at
	 * @param maxLgth 	The maximum length of the produced string
	 * @return
	 */
	public static String alignCodeLine(String leftStr, String pivotStr, String rightStr, int pivotPos,
			int maxLgth) {
		return alignCodeLine(leftStr, pivotStr, rightStr, pivotPos, maxLgth, true);
	}

	public static String alignCodeLine(String leftStr, String pivotStr, String rightStr, int pivotPos,
			int maxLgth, boolean whitespaceAfterPivot) {
		final StringBuilder sb = new StringBuilder();
		int currLineStart = 0;
		sb.append(leftStr);
		if (length(leftStr) < pivotPos) {
			sb.append(repeatChar(' ', pivotPos - length(leftStr)));
		}
		else {  //means that the pivot will have to go on next line.
			sb.append(LF);
			currLineStart = sb.length();
			sb.append(repeatChar(' ', pivotPos));
		}
		sb.append(pivotStr);
		if (sb.length() - currLineStart + length(rightStr) >= maxLgth) {
			sb.append(LF);
			sb.append(repeatChar(' ', maxLgth - length(rightStr)));
		}
		else if (whitespaceAfterPivot) {
			sb.append(' ');
		}

		sb.append(rightStr);
		return sb.toString();
	}

	public static String extractQuoted(final String inStr, final char startQuoteChar, final char endQuoteChar) {
		if (isEmpty(inStr)) {
			return EMPTY_STRING;
		}

		int startQuoteCharIndex = inStr.indexOf(startQuoteChar);

		if (startQuoteCharIndex != -1) {
			int endQuoteCharIndex = inStr.indexOf(endQuoteChar, startQuoteCharIndex);

			if (endQuoteCharIndex != -1) {
				return substring(inStr, startQuoteCharIndex, endQuoteCharIndex + 1);
			}
		}
		return EMPTY_STRING;
	}

	/**Method that takes a string and extracts the word that is contained at the specified position
	 *
	 * @param pos  A position within the word to extract (default word chars are letters, numbers, -, _)
	 * @return	The extracted word if any
	 */
	public static String extractWord(final String inStr, final int pos) {
		if (isEmpty(inStr)) {
			return EMPTY_STRING;
		}

		if (pos == -1 || pos >= inStr.length()) {
			return EMPTY_STRING;
		}

		final StringBuilder sb = new StringBuilder();

		for (int i = pos; i >= 0; i--) {
			final char c = inStr.charAt(i);
			if (Character.isLetter(c) || Character.isDigit(c) || c == '-' || c == '_') {
				sb.insert(0, c);
			}
			else {
				break;
			}
		}

		for (int i = pos+1; i < inStr.length(); i++) {
			final char c = inStr.charAt(i);
			if (Character.isLetter(c) || Character.isDigit(c) || c == '-' || c == '_') {
				sb.append(c);
			}
			else {
				break;
			}
		}

		return sb.toString();
	}

	//taken from: https://stackoverflow.com/a/40969865/1331732
	public static String format(String format, Object... params) {
		return MessageFormatter.arrayFormat(format, params).getMessage();
	}

	public static String formatTS(String format, Object... params) {
		return MessageFormatter.arrayFormat(dtf.format(LocalDateTime.now()) + SPACE + format, params).getMessage();
	}

	/**
	 * Format time to string.
	 * <p>
	 * <b>Note that the time argument has to support the required precision, Instant does NOT</b>
	 * </p>
	 * @param time The temporal accessor representing the time to format.
	 * @return Formatted time as HH:mm:ss.SSS
	 */
	public static String formatTS(TemporalAccessor time) {
		return dtf.format(time);
	}

	private static final char[] HEX_TABLE = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };

	/**
	 * Convert a byte array to a human-readable String for debugging purposes.
	 */
	@SuppressWarnings("nls")
	public static String hexDump(byte[] data) {
		byte byteValue;
		StringBuilder str = new StringBuilder(data.length * 3);
		str.append("Hex dump:\n");

		for (int i = 0; i < data.length; i += 16) {
			// dump the header: 00000000:
			String offset = Integer.toHexString(i);

			// "0" left pad offset field so it is always 8 char's long.
			for (int offlen = offset.length(); offlen < 8; offlen++) {
				str.append("0");
			}

			str.append(offset);
			str.append(":");

			// dump hex version of 16 bytes per line.
			for (int j = 0; (j < 16) && ((i + j) < data.length); j++) {
				byteValue = data[i + j];

				// add spaces between every 2 bytes.
				if ((j % 2) == 0) {
					str.append(" ");
				}

				// dump a single byte.
				byte highNibble = (byte)((byteValue & 0xf0) >>> 4);
				byte lowNibble = (byte)(byteValue & 0x0f);

				str.append(HEX_TABLE[highNibble]);
				str.append(HEX_TABLE[lowNibble]);
			}

			// dump ascii version of 16 bytes
			str.append("  ");

			for (int j = 0; (j < 16) && ((i + j) < data.length); j++) {
				char charValue = (char)data[i + j];

				// RESOLVE (really want isAscii() or isPrintable())
				if (Character.isLetterOrDigit(charValue)) {
					str.append(String.valueOf(charValue));
				}
				else {
					str.append(".");
				}
			}

			// new line
			str.append(LF);
		}
		return (str.toString());
	}

	@SuppressWarnings("nls")
	public static boolean isPrimitiveType(String type) {
		return ctString.containsOneOf(type, "int", "double", "float", "boolean", "long", "byte", "char", "short");
	}

	@SuppressWarnings("nls")
	public static void main(String[] args) {
		String trimmed = trimWithBr("  \n   \r\n   <br/><br/>\n <br /><p>MOVE SPACES</p><br/>\n<br/>\n");
		log.info("trimmed is {}{}[end]", LF, trimmed);

		log.info("undblquoted is {}", unDoubleQuoted("\"\\\"\"")); //NOSONAR
		log.info("isquoted is {}", isQuoted("\"\\\"\"", '"', '"', true));

	//		String code = alignCodeLine("MOVE SPACES", "TO", " DC-TBL-DIRECTION-IND.", 28, 65);
	//		System.out.println("code is " + LF + code);
//
	//		code = alignCodeLine("MOVE SPACES", "TO", " DC-TABLENAME-DIRECTION-INDICATOR-TODAY.", 28, 65);
	//		System.out.println("code is " + LF + code);
//
	//		code = alignCodeLine("MOVE LONG-LEFT-VARIABLE-THAT-SPACES", "TO", " DC-TBL-DIRECTION-IND.", 28, 65);
	//		System.out.println("code is " + LF + code);
//
	//		code = alignCodeLine("MOVE LONG-LEFT-VARIABLE-THAT-SPACES", "TO", " DC-TABLENAME-DIRECTION-INDICATOR-TODAY.", 28, 65);
	//		System.out.println("code is " + LF + code);

		String bla1 = "'bla''";
		String bla2 = "'bla'''";
		String bla3 = "\"\\\"\"";
		log.info("bla 3 is {} and it is quoted: {}", bla3, isQuoted(bla3));
		log.info("bla 1 is {}", isQuoted(bla1));
		log.info("bla 2 is {}", isQuoted(bla2));


		String quoteIfSpaces = quoteIfSpaces(null);

//		System.out.println("modulo of 1 %4 is " + (1%4));
//		String indentStr = ctString.getIndentationString(3, 4, 4);
//		System.out.println("indent string is:" + ctString.debugShowAllChars(indentStr, 0, 0));
	//		return 0;
	}
}