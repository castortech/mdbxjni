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
import java.util.ArrayList;
import java.util.List;

import static org.fusesource.lmdbjni.JNI.*;
import static org.fusesource.lmdbjni.Util.checkArgNotNull;
import static org.fusesource.lmdbjni.Util.checkErrorCode;
import static org.fusesource.lmdbjni.Util.checkSize;

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Database extends NativeObject implements Closeable {
    private final Env env;
    private final String name;
    private List<SecondaryDatabase> secondaries = null;

    Database(Env env, long self, String name) {
        super(self);
        this.env = env;
        this.name = name;
    }

    public void close() {
        if( self!=0 ) {
            mdb_dbi_close(env.pointer(), self);
            self=0;
        }
    }

    public MDB_stat stat() {
        Transaction tx = env.createTransaction();
        try {
            return stat(tx);
        } finally {
            tx.commit();
        }
    }

    public MDB_stat stat(Transaction tx) {
        checkArgNotNull(tx, "tx");
        MDB_stat rc = new MDB_stat();
        mdb_stat(tx.pointer(), pointer(), rc);
        return rc;
    }

    public void drop(boolean delete) {
        Transaction tx = env.createTransaction();
        try {
            drop(tx, delete);
        } finally {
            tx.commit();
        }
    }

    public void drop(Transaction tx, boolean delete) {
        checkArgNotNull(tx, "tx");
        mdb_drop(tx.pointer(), pointer(), delete ? 1 : 0);
        if( delete ) {
            self=0;
        }
    }

    public String getName() {
    	return name;
    }
    
    public byte[] get(byte[] key) {
        checkArgNotNull(key, "key");
        Transaction tx = env.createTransaction();
        try {
            return get(tx, key);
        } finally {
            tx.commit();
        }
    }

    public byte[] get(Transaction tx, byte[] key) {
        checkArgNotNull(key, "key");
        if (tx == null) {
            return get(key);
        }
        
        checkArgNotNull(tx, "tx");
        NativeBuffer keyBuffer = NativeBuffer.create(key);
        try {
            return get(tx, keyBuffer);
        } finally {
            keyBuffer.delete();
        }
    }

    private byte[] get(Transaction tx, NativeBuffer keyBuffer) {
        return get(tx, new Value(keyBuffer));
    }

    /*package*/byte[] get(Transaction tx, Value key) {
        Value value = new Value();
        int rc = mdb_get(tx.pointer(), pointer(), key, value);
        if( rc == MDB_NOTFOUND ) {
            return null;
        }
        checkErrorCode(rc);
        return value.toByteArray();
    }

    public byte[] put(byte[] key, byte[] value) {
        return put(key, value, 0);
    }

    public byte[] put(byte[] key, byte[] value, int flags) {
        checkArgNotNull(key, "key");
        Transaction tx = env.createTransaction();
        try {
            return put(tx, key, value, flags);
        } finally {
            tx.commit();
        }
    }

    public byte[] put(Transaction tx, byte[] key, byte[] value) {
        return put(tx, key, value, 0);
    }

    public byte[] put(Transaction tx, byte[] key, byte[] value, int flags) {
        checkArgNotNull(key, "key");
        checkArgNotNull(value, "value");
        
        if (tx == null) {
            return put(key, value, flags);
        }
        
        checkArgNotNull(tx, "tx");
        NativeBuffer keyBuffer = NativeBuffer.create(key);
        try {
            NativeBuffer valueBuffer = NativeBuffer.create(value);
            try {
                return put(tx, keyBuffer, valueBuffer, flags);
            } finally {
                valueBuffer.delete();
            }
        } finally {
            keyBuffer.delete();
        }
    }

    private byte[] put(Transaction tx, NativeBuffer keyBuffer, NativeBuffer valueBuffer, int flags) {
        return put(tx, new Value(keyBuffer), new Value(valueBuffer), flags);
    }

    private byte[] put(Transaction tx, Value keySlice, Value valueSlice, int flags) {
    	checkSize(env, keySlice);
    	if ((flags & MDB_DUPSORT) != 0) {
        	checkSize(env, valueSlice);
    	}
    	
        int rc = mdb_put(tx.pointer(), pointer(), keySlice, valueSlice, flags);
        if ( ((flags & MDB_NOOVERWRITE)!=0 || (flags & MDB_NODUPDATA)!=0) && rc == MDB_KEYEXIST ) {
            // Return the existing value if it was a dup insert attempt.
            return valueSlice.toByteArray();
        } 
        else {
            // If the put failed, throw an exception..
            if (rc != 0) {
                throw new LMDBException("put failed", rc);
            }
            
            if (secondaries != null) {
            	for (SecondaryDatabase secDb : secondaries) {
            		SecondaryDbConfig secConfig = (SecondaryDbConfig) secDb.getConfig();
            		byte[] pKey = keySlice.toByteArray();
            		byte[] secKey = secConfig.getKeyCreator().createSecondaryKey(secDb, pKey, valueSlice.toByteArray());
            		secDb.put(tx, secKey, pKey);
            	}
            }
            
            return null;
        }
    }


    public boolean delete(byte[] key) {
        return delete(key, null);
    }

    public boolean delete(byte[] key, byte[] value) {
        checkArgNotNull(key, "key");
        Transaction tx = env.createTransaction();
        try {
            return delete(tx, key, value);
        } finally {
            tx.commit();
        }
    }

    public boolean delete(Transaction tx, byte[] key) {
        return delete(tx, key, null);
    }

    public boolean delete(Transaction tx, byte[] key, byte[] value) {
        checkArgNotNull(key, "key");
        
        if (tx == null) {
            return delete(key, value);
        }
        
        checkArgNotNull(tx, "tx");
        NativeBuffer keyBuffer = NativeBuffer.create(key);
        try {
            NativeBuffer valueBuffer = NativeBuffer.create(value);
            try {
                return delete(tx, keyBuffer, valueBuffer);
            } finally {
                if( valueBuffer!=null ) {
                    valueBuffer.delete();
                }
            }
        } finally {
            keyBuffer.delete();
        }
    }

    private boolean delete(Transaction tx, NativeBuffer keyBuffer, NativeBuffer valueBuffer) {
        return delete(tx, new Value(keyBuffer), Value.create(valueBuffer));
    }

    private boolean delete(Transaction tx, Value keySlice, Value valueSlice) {
    	checkSize(env, keySlice);
        int rc = mdb_del(tx.pointer(), pointer(), keySlice, valueSlice);
        if( rc == MDB_NOTFOUND ) {
            return false;
        }
        checkErrorCode(rc);
        
        if (secondaries != null) {
        	for (SecondaryDatabase secDb : secondaries) {
        		SecondaryDbConfig secConfig = (SecondaryDbConfig) secDb.getConfig();
        		byte[] pKey = keySlice.toByteArray();
        		byte[] secKey = secConfig.getKeyCreator().createSecondaryKey(secDb, pKey, valueSlice.toByteArray());
        		secDb.delete(tx, secKey, pKey);
        	}
        }
        
        return true;
    }

    public Cursor openCursor(Transaction tx) {
        checkArgNotNull(tx, "tx");
    	
        long cursor[] = new long[1];
        checkErrorCode(mdb_cursor_open(tx.pointer(), pointer(), cursor));
        return new Cursor(env, cursor[0], tx, this);
    }
    
    public SecondaryCursor openSecondaryCursor(Transaction tx) {
        checkArgNotNull(tx, "tx");
    	
        long cursor[] = new long[1];
        checkErrorCode(mdb_cursor_open(tx.pointer(), pointer(), cursor));
        return new SecondaryCursor(env, cursor[0], tx, this);
    }
    
    public int getFlags() {
        Transaction tx = env.createTransaction();
        try {
            return getFlags(tx);
        } finally {
            tx.commit();
        }
    }

    public int getFlags(Transaction tx) {
        long[] flags = new long[1];
        checkErrorCode(mdb_dbi_flags(tx.pointer(), pointer(), flags));
        return (int) flags[0];
    }
    
    public DatabaseConfig getConfig() {
        Transaction tx = env.createTransaction();
        try {
            return getConfig(tx);
        } finally {
            tx.commit();
        }
    }
    
    public DatabaseConfig getConfig(Transaction tx) {
    	int flags = getFlags(tx);
    	return new DatabaseConfig(flags);
    }
    
    
    /*package*/void associate(Transaction tx, SecondaryDatabase secondary) {
    	if (secondaries == null) {
    		secondaries = new ArrayList<SecondaryDatabase>();
    	}
    	
    	secondaries.add(secondary);
    }
    
	
//	int associateFlags = 0;
//	associateFlags |= config.getAllowPopulate() ? Constants.CREATE : 0;
//	if (config.getImmutableSecondaryKey())
//		associateFlags |= Constants.IMMUTABLE_KEY;
//
//	config.getKeyCreator()
//	
//	}

}
