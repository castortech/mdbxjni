package com.castortech.mdbxjni.pool;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.castortech.mdbxjni.Cursor;
import com.castortech.mdbxjni.Database;
import com.castortech.mdbxjni.Env;
import com.castortech.mdbxjni.SecondaryDatabase;
import com.castortech.mdbxjni.Transaction;

public class CursorPoolFactory extends BaseKeyedPooledObjectFactory<CursorKey, Cursor> {
	private static final Logger log = LoggerFactory.getLogger(CursorPoolFactory.class);

	private Env env;

	public CursorPoolFactory(Env env) {
		this.env = env;
	}

	@Override
	public Cursor create(CursorKey key) throws Exception {
		Cursor cursor;
		Database db = key.getDb();
		Transaction txn = key.getTxn();

		if (!txn.isAllocated()) {  //if it tries to create to maintain some minimums
			return null;
		}

		if (db instanceof SecondaryDatabase) {
			cursor = env.createSecondaryCursor();
		}
		else {
			cursor = env.createCursor();
		}

		if (log.isDebugEnabled()) {
			log.debug("Create called with key:{}, created cursor:{}", key, cursor); //$NON-NLS-1$
		}
		return cursor;
	}

	@Override
	public PooledObject<Cursor> wrap(Cursor cursor) {
		if (log.isTraceEnabled()) {
			log.trace("Wrap called with cursor:{}", cursor); //$NON-NLS-1$
		}
		return new DefaultPooledObject<>(cursor);
	}

	@Override
	public boolean validateObject(CursorKey key, PooledObject<Cursor> p) {
		return p.getObject() != null;  //can be null if the txn was freed
	}

	@Override
	public void activateObject(CursorKey key, PooledObject<Cursor> p) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Binding cursor:{}", p.getObject()); //$NON-NLS-1$
		}
		p.getObject().bind(key.getDb(), key.getTxn());
		if (log.isTraceEnabled()) {
			log.trace("Bound cursor:{}", p.getObject()); //$NON-NLS-1$
		}
	}

	@Override
	public void destroyObject(CursorKey key, PooledObject<Cursor> p) throws Exception {
		if (key.getThreadId() != Thread.currentThread().getId()) {  //probably the evictor thread
			Cursor cursor = p.getObject();
			log.info("Destroy cursor thread mismatch, wrapping in fake transaction"); //$NON-NLS-1$

			//we have to open a read only transaction and bind the cursor to it and then close it.
			Transaction txn = env.createTransaction(true);
			try {
				cursor.bind(cursor.getDatabase(), txn);
				cursor.mdbxClose();
			}
			finally {
				txn.close();
			}
		}
		else {
			if (log.isDebugEnabled()) {
				log.debug("Closing cursor:{}", p.getObject()); //$NON-NLS-1$
			}
			p.getObject().mdbxClose();
		}
	}
}