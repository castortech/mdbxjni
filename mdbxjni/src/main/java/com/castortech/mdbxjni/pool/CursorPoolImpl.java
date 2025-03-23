package com.castortech.mdbxjni.pool;

import java.io.Closeable;
import java.lang.reflect.Method;

import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.castortech.mdbxjni.Cursor;
import com.castortech.mdbxjni.Env;
import com.castortech.mdbxjni.SecondaryCursor;
import com.castortech.mdbxjni.Transaction;

/**
 * Cursor pool implementation
 *
 * @author Alain Picard
 */
public class CursorPoolImpl implements CursorPool, Closeable {
	private static final Logger log = LoggerFactory.getLogger(CursorPoolImpl.class);

	private Env env;
	private KeyedObjectPool<CursorKey, Cursor> pool;
	private CursorPoolConfig poolConfig;

	/**
	 * Constructor
	 * @param poolConfig pool configuration
	 * @param env environment
	 */
	public CursorPoolImpl(CursorPoolConfig poolConfig, Env env) {
		KeyedPooledObjectFactory<CursorKey, Cursor> factory = new CursorPoolFactory(env);
		pool = new GenericKeyedObjectPool	<>(factory, poolConfig);
		this.env = env;
		this.poolConfig = poolConfig;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cursor borrow(CursorKey key) throws Exception {
		Cursor cursor = pool.borrowObject(key);
		if (log.isDebugEnabled()) {
			log.debug("Borrow called with key:{}, returning:{}", key, cursor); //$NON-NLS-1$
		}
		return cursor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SecondaryCursor borrowSecondary(CursorKey key) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("BorrowSecondary called with key:{}", key); //$NON-NLS-1$
		}
		return (SecondaryCursor)pool.borrowObject(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void release(Cursor cursor) throws Exception {
		if (cursor != null) {
			CursorKey key = new CursorKey(env, cursor.getDatabase(), cursor.getTransaction());
			if (log.isDebugEnabled()) {
				log.debug("Release called for cursor {}, with key:{}", cursor, key); //$NON-NLS-1$
			}
			pool.returnObject(key, cursor);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeTransaction(Transaction txn) throws Exception {
		pool.clear(new CursorKey(env, false, txn));
		pool.clear(new CursorKey(env, true, txn));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		int attempts = poolConfig.getCloseMaxWaitSeconds();
		int numActive = pool.getNumActive();

		if (attempts > 0 && numActive > 0) {
			int tries = 0;
			while(pool.getNumActive() > 0 && tries < attempts) {
				tries++;
				try {
					Thread.sleep(1_000);  //1sec
				}
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
		pool.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getStats() {
		try {
			Method m = pool.getClass().getDeclaredMethod("getStatsString"); //$NON-NLS-1$
			m.setAccessible(true);  //NOSONAR: Apache need to update their API
			return (String)m.invoke(pool);
		}
		catch (Exception e) {
			e.printStackTrace();
			log.error("Error Occurred", e); //$NON-NLS-1$
		}
		return null;
	}
}