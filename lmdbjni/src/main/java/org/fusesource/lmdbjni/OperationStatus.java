package org.fusesource.lmdbjni;

import static org.fusesource.lmdbjni.JNI.*;

public final class OperationStatus {
    public static final OperationStatus SUCCESS =
        new OperationStatus("SUCCESS", 0);

    public static final OperationStatus KEYEXIST =
        new OperationStatus("KEYEXIST", MDB_KEYEXIST);

    public static final OperationStatus NOTFOUND =
        new OperationStatus("NOTFOUND", MDB_NOTFOUND);

    /* package */static OperationStatus fromInt(final int errCode) {
        if (errCode == 0)
            return SUCCESS;
        if (errCode == MDB_KEYEXIST)
            return KEYEXIST;
        if (errCode == MDB_NOTFOUND)
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

    /** {@inheritDoc} */
    public String toString() {
        return "OperationStatus." + statusName;
    }
}
