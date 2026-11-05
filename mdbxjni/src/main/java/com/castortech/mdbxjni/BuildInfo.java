package com.castortech.mdbxjni;

/** libmdbx build information
 * \attention Some strings could be NULL in case no corresponding information
 *            was provided at build time (i.e. flags).
 *
 * @author Alain Picard
 */
public class BuildInfo {
	private final String datetime;
	private final String target;
	private final String options;
	private final String compiler;
	private final String flags;
	private final String metadata;

	BuildInfo(JNI.MDBX_build_info rc) {
		datetime = text(rc.datetime);
		target= text(rc.target);
		options= text(rc.options);
		compiler= text(rc.compiler);
		flags= text(rc.flags);
		metadata = text(rc.metadata);
	}

	private static String text(char[] value) {
		return value == null ? "" : new String(value);
	}

	/**
	 * build timestamp (ISO-8601 or __DATE__ __TIME__)
	 * @return build date time
	 */
	public String getDatetime() {
		return datetime;
	}

	/**
	 * cpu/arch-system-config triplet
	 * @return build target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * mdbx-related options
	 * @return build options
	 */
	public String getOptions() {
		return options;
	}

	/**
	 * compiler
	 * @return build compiler
	 */
	public String getCompiler() {
		return compiler;
	}

	/**
	 * CFLAGS and CXXFLAGS
	 * @return build compile flags
	 */
	public String getFlags() {
		return flags;
	}

	/**
	 * extra/custom information provided via the MDBX_BUILD_METADATA definition during library build
	 * @return build metadata
	 */
	public String getMetadata() {
		return metadata;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "{" +
				"datetime=" + datetime +
				", target=" + target +
				", options=" + options +
				", compiler=" + compiler +
				", flags=" + flags +
				"}";
	}
}