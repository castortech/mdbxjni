package com.castortech.mdbxjni;

import static com.castortech.mdbxjni.JNI.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.castortech.mdbxjni.types.PackedUnsigned1616;
import com.castortech.mdbxjni.types.UnsignedInt16;

public class DebugState {
	private final int packedResult;
	private final PackedUnsigned1616 packed;

	public DebugState(int packedResult) {
		this.packedResult = packedResult;
		packed = new PackedUnsigned1616(packedResult);
	}

	public MdbxLogLevel getPreviousLogLevel() {
		int left = packed.getLeft();
		UnsignedInt16 uint16 = new UnsignedInt16(left);

		if (uint16.intValue() <= MdbxLogLevel.getMaxValue()) {
			return MdbxLogLevel.getByValue(uint16.intValue());
		}
		return MdbxLogLevel.DONT_CHANGE;  // can't do better
	}

	public Collection<DebugFlags> getPreviousDebugFlags() {
		int right = packed.getRight();
		UnsignedInt16 uint16 = new UnsignedInt16(right);
		int flags = uint16.intValue();
		List<DebugFlags> dbgFlags = new ArrayList<>();

		if (flags == MDBX_DBG_NONE) {
			dbgFlags.add(DebugFlags.NONE);
		}
		if ((flags & MDBX_DBG_ASSERT) == MDBX_DBG_ASSERT) {
			dbgFlags.add(DebugFlags.ASSERT);
		}
		if ((flags & MDBX_DBG_AUDIT) == MDBX_DBG_AUDIT) {
			dbgFlags.add(DebugFlags.AUDIT);
		}
		if ((flags & MDBX_DBG_JITTER) == MDBX_DBG_JITTER) {
			dbgFlags.add(DebugFlags.JITTER);
		}
		if ((flags & MDBX_DBG_DUMP) == MDBX_DBG_DUMP) {
			dbgFlags.add(DebugFlags.DUMP);
		}
		if ((flags & MDBX_DBG_LEGACY_MULTIOPEN) == MDBX_DBG_LEGACY_MULTIOPEN) {
			dbgFlags.add(DebugFlags.LEGACY_MULTIOPEN);
		}
		if ((flags & MDBX_DBG_LEGACY_OVERLAP) == MDBX_DBG_LEGACY_OVERLAP) {
			dbgFlags.add(DebugFlags.LEGACY_OVERLAP);
		}
		if ((flags & MDBX_DBG_DONT_UPGRADE) == MDBX_DBG_DONT_UPGRADE) {
			dbgFlags.add(DebugFlags.DONT_UPGRADE);
		}
		if ((flags & MDBX_DBG_DONTCHANGE) == MDBX_DBG_DONTCHANGE) {
			dbgFlags.add(DebugFlags.DONT_CHANGE);
		}
		return dbgFlags;
	}

	public int getResult() {
		return packedResult;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DebugState [previousLogLevel:");
		sb.append(getPreviousLogLevel());
		sb.append(", previousDebugFlags:");
		sb.append(getPreviousDebugFlags().stream().map(val -> val.toString()).collect(Collectors.joining(", ")));
		sb.append("]");

		return sb.toString();
	}
}