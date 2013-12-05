package org.fusesource.lmdbjni;

public class DefaultSecondaryKeyCreator implements SecondaryKeyCreator {
    private static final DefaultSecondaryKeyCreator instance = new DefaultSecondaryKeyCreator();
    
    private DefaultSecondaryKeyCreator() { }
    
    public static DefaultSecondaryKeyCreator getInstance() {
        return instance;
    }
    
    public byte[] createSecondaryKey(SecondaryDatabase secondary, byte[] key, byte[] data) {
    	byte[] result = data;
        return result;
    }
}
