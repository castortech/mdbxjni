package com.castortech.mdbxjni;

public interface SecondaryKeyCreator {
    byte[] createSecondaryKey(SecondaryDatabase secondary, byte[] key, byte[] data);
}