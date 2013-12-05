package org.fusesource.lmdbjni;

public interface SecondaryKeyCreator {
    byte[] createSecondaryKey(SecondaryDatabase secondary, byte[] key, byte[] data);
}