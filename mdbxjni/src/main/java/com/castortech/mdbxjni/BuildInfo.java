package com.castortech.mdbxjni;

public class BuildInfo {
	private final String datetime;
	private final String target;
	private final String options;
	private final String compiler;
	private final String flags;

	BuildInfo(JNI.MDBX_build_info rc) {
		datetime = new String(rc.datetime);
		target= new String(rc.target);
		options= new String(rc.options);
		compiler= new String(rc.compiler);
		flags= new String(rc.flags);
	}

	public String getDatetime() {
		return datetime;
	}

	public String getTarget() {
		return target;
	}

	public String getOptions() {
		return options;
	}

	public String getCompiler() {
		return compiler;
	}

	public String getFlags() {
		return flags;
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