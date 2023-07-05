package com.castortech.mdbxjni;

import static com.castortech.mdbxjni.JNI.*;

import static com.castortech.mdbxjni.Util.checkArgNotNull;
import static com.castortech.mdbxjni.Util.checkErrorCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecondaryCursor extends Cursor {
	private static final Logger log = LoggerFactory.getLogger(SecondaryCursor.class);

	/* package */ SecondaryCursor(Env env, long self, Transaction tx, Database db) {
		super(env, self, tx, db);
	}

	@Override
	public Database getDatabase() {
		return getSecondaryDatabase();
	}

	public SecondaryDatabase getSecondaryDatabase() {
		return (SecondaryDatabase)super.getDatabase();
	}

	public Database getPrimaryDatabase() {
		return getSecondaryDatabase().getPrimaryDatabase();
	}

	public OperationStatus get(CursorOp op, DatabaseEntry key, DatabaseEntry pKey, DatabaseEntry value) {
		checkArgNotNull(op, "op"); //$NON-NLS-1$
		NativeBuffer keyBuffer = NativeBuffer.create(key.getData());
		NativeBuffer pkeyBuffer = NativeBuffer.create(pKey.getData());

		try {
			Value keyValue = keyBuffer != null ? new Value(keyBuffer) : new Value();
			Value pkeyValue = pkeyBuffer != null ? new Value(pkeyBuffer) : new Value();
			if (log.isTraceEnabled())
				log.trace("Calling sec cursor get op/de key/pkey/val for {}", this); //$NON-NLS-1$
			int rc = mdbx_cursor_get(pointer(), keyValue, pkeyValue, op.getValue());
			if (rc == MDBX_NOTFOUND) {
				return OperationStatus.NOTFOUND;
			}
			checkErrorCode(null, rc);

			key.setData(keyValue.toByteArray());
			pKey.setData(pkeyValue.toByteArray());
			value.setData(getSecondaryDatabase().getPrimaryDatabase()
					.get(getTransaction(), pkeyValue));
			return OperationStatus.SUCCESS;
		}
		finally {
			if (keyBuffer != null)
				keyBuffer.delete();
			if (pkeyBuffer != null)
				pkeyBuffer.delete();
		}
	}

	@Override
	public byte[] put(byte[] key, byte[] value, int flags) {
		throw new UnsupportedOperationException();
	}
}