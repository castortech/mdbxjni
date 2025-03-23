package com.castortech.mdbxjni.pool;

import com.castortech.mdbxjni.Cursor;
import com.castortech.mdbxjni.SecondaryCursor;
import com.castortech.mdbxjni.Transaction;

/**
 * Cursor Pool interface
 *
 * @author Alain Picard
 */
public interface CursorPool {
	/**
	 * Borrow a cursor from the pool
	 *
	 * @param key cursor key
	 * @return borrowed cursor
	 * @throws Exception exceptions from pool library
	 */
	Cursor borrow(CursorKey key) throws Exception;

	/**
	 * Borrow a secondary database cursor from the pool
	 *
	 * @param key cursor key
	 * @return borrowed secondary cursor
	 * @throws Exception exceptions from pool library
	 */
	SecondaryCursor borrowSecondary(CursorKey key) throws Exception;

	/**
	 * Release a borrowed cursor from the pool
	 * @param cursor the cursor to release
	 * @throws Exception exceptions from pool library
	 */
	void release(Cursor cursor) throws Exception;

	/**
	 * Close transaction
	 * @param txn transaction
	 * @throws Exception exceptions from pool library
	 */
	default void closeTransaction(Transaction txn) throws Exception {
		//do nothing
	}

	/**
	 * Close cursor pool
	 */
	void close();

	/**
	 * Get statistics from the pool
	 * @return pool statistics
	 */
	String getStats();
}