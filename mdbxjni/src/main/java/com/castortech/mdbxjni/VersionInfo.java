package com.castortech.mdbxjni;

public class VersionInfo {
	public int major;
	public int minor;
	public int patch;
	public int tweak;
	public String semverPrerelease;
	public String git_datetime;
	public String git_tree;
	public String git_commit;
	public String git_describe;
	public String sourcery;

	VersionInfo(JNI.MDBX_version_info rc) {
		major = rc.major;
		minor = rc.minor;
		patch = rc.patch;
		tweak = rc.tweak;
		semverPrerelease = text(rc.semver_prerelease);
		git_datetime = text(rc.git_datetime);
		git_tree = text(rc.git_tree);
		git_commit = text(rc.git_commit);
		git_describe = text(rc.git_describe);
		sourcery = text(rc.sourcery);
	}

	private static String text(char[] value) {
		return value == null ? "" : new String(value);
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getPatch() {
		return patch;
	}

	public int getTweak() {
		return tweak;
	}

	public String getSemverPrerelease() {
		return semverPrerelease;
	}

	public String getGit_datetime() {
		return git_datetime;
	}

	public String getGit_tree() {
		return git_tree;
	}

	public String getGit_commit() {
		return git_commit;
	}

	public String getGit_describe() {
		return git_describe;
	}

	public String getSourcery() {
		return sourcery;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "{" +
				"major=" + major +
				", minor=" + minor +
				", patch=" + patch +
				", tweak=" + tweak +
				", semverPrerelease=" + semverPrerelease +
				", git_datetime=" + git_datetime +
				", git_tree=" + git_tree +
				", git_commit=" + git_commit +
				", git_describe=" + git_describe +
				", sourcery=" + sourcery +
				"]";
	}
}