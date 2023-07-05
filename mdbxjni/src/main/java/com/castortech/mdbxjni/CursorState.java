package com.castortech.mdbxjni;

import static com.castortech.mdbxjni.JNIIntern.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.castortech.mdbxjni.types.UnsignedInt8;

public class CursorState {
	private final short rawResult;
	private final UnsignedInt8 flags;

	public CursorState(short rawResult) {
		this.rawResult = rawResult;
		flags = new UnsignedInt8(rawResult);
	}

	public Collection<CursorStateFlags> getCursorStateFlags() {
		List<CursorStateFlags> csFlags = new ArrayList<>();
		int flgsInt = flags.intValue();

		if (flgsInt == 0) {
			csFlags.add(CursorStateFlags.NONE);
		}
		if ((flgsInt & C_INITIALIZED) == C_INITIALIZED) {
			csFlags.add(CursorStateFlags.INITIALIZED);
		}
		if ((flgsInt & C_EOF) == C_EOF) {
			csFlags.add(CursorStateFlags.EOF);
		}
		if ((flgsInt & C_SUB) == C_SUB) {
			csFlags.add(CursorStateFlags.SUB);
		}
		if ((flgsInt & C_DEL) == C_DEL) {
			csFlags.add(CursorStateFlags.DEL);
		}
		if ((flgsInt & C_UNTRACK) == C_UNTRACK) {
			csFlags.add(CursorStateFlags.UNTRACK);
		}
		if ((flgsInt & C_GCU) == C_GCU) {
			csFlags.add(CursorStateFlags.GCU);
		}
		return csFlags;
	}

	public short getRawResult() {
		return rawResult;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CursorState [stateFlags:");
		sb.append(getCursorStateFlags().stream().map(val -> val.toString()).collect(Collectors.joining(", ")));
		sb.append("]");

		return sb.toString();
	}
}