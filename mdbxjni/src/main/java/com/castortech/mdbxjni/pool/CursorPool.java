package com.castortech.mdbxjni.pool;

import com.castortech.mdbxjni.Cursor;
import com.castortech.mdbxjni.SecondaryCursor;
import com.castortech.mdbxjni.Transaction;

public interface CursorPool {
	Cursor borrow(CursorKey key) throws Exception;

	SecondaryCursor borrowSecondary(CursorKey key) throws Exception;

	void release(Cursor cursor) throws Exception;

	default void closeTransaction(Transaction txn) throws Exception {
		//do nothing
	}

	void close();

	String getStats();
}