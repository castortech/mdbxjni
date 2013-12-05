/**
 * Copyright (C) 2013, RedHat, Inc.
 *
 *    http://www.redhat.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.lmdbjni;


import java.io.Closeable;

import static org.fusesource.lmdbjni.JNI.*;
import static org.fusesource.lmdbjni.Util.checkArgNotNull;
import static org.fusesource.lmdbjni.Util.checkErrorCode;

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Cursor extends NativeObject implements Closeable {
    private final Env env;
    private final Transaction tx;
    private final Database db;

    Cursor(Env env, long self, Transaction tx, Database db) {
        super(self);
        this.env = env;
        this.tx = tx;
        this.db = db;
    }

    public void close() {
        if( self!=0 ) {
            mdb_cursor_close(self);
            self=0;
        }
    }

    public void renew(Transaction tx) {
        checkErrorCode(mdb_cursor_renew(tx.pointer(), pointer()));
    }

    public Entry get(CursorOp op) {
        checkArgNotNull(op, "op");

        Value key = new Value();
        Value value = new Value();
        int rc = mdb_cursor_get(pointer(), key, value, op.getValue());
        if( rc == MDB_NOTFOUND ) {
            return null;
        }
        checkErrorCode(rc);
        return new Entry(key.toByteArray(), value.toByteArray());
    }

    public Entry get(CursorOp op, byte[] key) {
        checkArgNotNull(op, "op");
        NativeBuffer keyBuffer = NativeBuffer.create(key);
        try {
            Value keyValue = new Value(keyBuffer);
            Value value = new Value();
            int rc = mdb_cursor_get(pointer(), keyValue, value, op.getValue());
            if( rc == MDB_NOTFOUND ) {
                return null;
            }
            checkErrorCode(rc);
            return new Entry(keyValue.toByteArray(), value.toByteArray());
        } finally {
        	if (keyBuffer != null)
        		keyBuffer.delete();
        }
    }

    public Entry get(CursorOp op, byte[] key, byte[] value) {
        checkArgNotNull(op, "op");
        NativeBuffer keyBuffer = NativeBuffer.create(key);
        NativeBuffer valBuffer = NativeBuffer.create(value);
        
        try {
            Value keyValue = keyBuffer != null ? new Value(keyBuffer) : new Value();
            Value valValue = valBuffer != null ? new Value(valBuffer) : new Value();
            int rc = mdb_cursor_get(pointer(), keyValue, valValue, op.getValue());
            if( rc == MDB_NOTFOUND ) {
                return null;
            }
            checkErrorCode(rc);
            return new Entry(keyValue.toByteArray(), valValue.toByteArray());
        } finally {
        	if (keyBuffer != null)
        		keyBuffer.delete();
        	if (valBuffer != null)
        		valBuffer.delete();
        }
    }

    public OperationStatus get(CursorOp op, DatabaseEntry key, DatabaseEntry value) {
        checkArgNotNull(op, "op");
        NativeBuffer keyBuffer = NativeBuffer.create(key.getData());
        NativeBuffer valBuffer = NativeBuffer.create(value.getData());
        
        try {
            Value keyValue = keyBuffer != null ? new Value(keyBuffer) : new Value();
            Value valValue = valBuffer != null ? new Value(valBuffer) : new Value();
            int rc = mdb_cursor_get(pointer(), keyValue, valValue, op.getValue());
            if (rc == MDB_NOTFOUND) {
                return OperationStatus.NOTFOUND;
            }
            checkErrorCode(rc);
            key.setData(keyValue.toByteArray());
            value.setData(valValue.toByteArray());
            return OperationStatus.SUCCESS;
        } finally {
        	if (keyBuffer != null)
        		keyBuffer.delete();
        	if (valBuffer != null)
        		valBuffer.delete();
        }
    }

    public byte[] put(byte[] key, byte[] value, int flags) {
        checkArgNotNull(key, "key");
        checkArgNotNull(value, "value");
        NativeBuffer keyBuffer = NativeBuffer.create(key);

        try {
            NativeBuffer valBuffer = NativeBuffer.create(value);
            try {
                return put(keyBuffer, valBuffer, flags);
            } finally {
            	if (valBuffer != null)
            		valBuffer.delete();
            }
        } finally {
        	if (keyBuffer != null)
        		keyBuffer.delete();
        }
    }

    private byte[] put(NativeBuffer keyBuffer, NativeBuffer valueBuffer, int flags) {
        return put(new Value(keyBuffer), new Value(valueBuffer), flags);
    }
    private byte[] put(Value keySlice, Value valueSlice, int flags) {
        mdb_cursor_put(pointer(), keySlice, valueSlice, flags);
        return valueSlice.toByteArray();
    }

    public void delete() {
        checkErrorCode(mdb_cursor_del(pointer(), 0));
    }

    public void deleteIncludingDups() {
        checkErrorCode(mdb_cursor_del(pointer(), MDB_NODUPDATA));
    }

    public long count() {
        long rc[] = new long[1];
        checkErrorCode(mdb_cursor_count(pointer(), rc));
        return rc[0];
    }
    
    public Database getDatabase() {
//        long dbi = mdb_cursor_dbi(pointer());
        return db;
    }

    public Transaction getTransaction() {
//      long txn = mdb_cursor_txn(pointer());
      return tx;
  }
}
