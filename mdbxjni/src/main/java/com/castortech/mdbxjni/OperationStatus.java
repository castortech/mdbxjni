package com.castortech.mdbxjni;

import static com.castortech.mdbxjni.JNI.*;

@SuppressWarnings("nls")
public final class OperationStatus {
	public static final OperationStatus SUCCESS = new OperationStatus("SUCCESS", 0);
	public static final OperationStatus KEYEXIST = new OperationStatus("KEYEXIST", MDBX_KEYEXIST);
	public static final OperationStatus NOTFOUND = new OperationStatus("NOTFOUND", MDBX_NOTFOUND);

	/* package */static OperationStatus fromInt(final int errCode) {
		if (errCode == 0)
			return SUCCESS;
		if (errCode == MDBX_KEYEXIST)
			return KEYEXIST;
		if (errCode == MDBX_NOTFOUND)
			return NOTFOUND;
		else
			throw new IllegalArgumentException("Unknown error code: " + errCode);
	}

	/* For toString */
	private String statusName;
	private int errCode;

	private OperationStatus(final String statusName, int errCode) {
		this.statusName = statusName;
		this.errCode = errCode;
	}

	@Override
	public String toString() {
		return "OperationStatus." + statusName;
	}
}
