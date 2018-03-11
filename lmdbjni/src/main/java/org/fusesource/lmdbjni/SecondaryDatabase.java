package org.fusesource.lmdbjni;

public class SecondaryDatabase extends Database {
    private final Database primaryDatabase;
    private final SecondaryDbConfig config;

    /* package */SecondaryDatabase(Env env, Database primaryDatabase, long self, String name, SecondaryDbConfig config) {
        super(env, self, name);
        this.primaryDatabase = primaryDatabase;
        this.config = config;
    }
    
    public Cursor openCursor(final Transaction txn) {
        return openSecondaryCursor(txn);
    }

    public SecondaryCursor openSecondaryCursor(final Transaction tx) {
        return super.openSecondaryCursor(tx);
    }

    public Database getPrimaryDatabase() {
        return primaryDatabase;
    }

    public DatabaseConfig getConfig() {
        return config;
    }
    
    public byte[] get(Transaction tx, byte[] key, byte[] pKey) {
    	Cursor cursor = openCursor(tx);
    	try {
	    	Entry entry = cursor.get(CursorOp.GET_BOTH, key, pKey);
			return entry.getValue();
    	}
    	finally {
    		cursor.close();
    	}
    }
    
    public byte[] internalPut(Transaction tx, byte[] key, byte[] value) {
        return super.put(tx, key, value, 0);
    }

    public byte[] getSearchBoth(Transaction tx, byte[] key, byte[] pKey) {
		return null;
    }

    public byte[] put(byte[] key, byte[] value) {
        throw new UnsupportedOperationException();
    }

    public byte[] put(byte[] key, byte[] value, int flags) {
        throw new UnsupportedOperationException();
    }

    public byte[] put(Transaction tx, byte[] key, byte[] value) {
        throw new UnsupportedOperationException();
    }

    public byte[] put(Transaction tx, byte[] key, byte[] value, int flags) {
        throw new UnsupportedOperationException();
    }
}
