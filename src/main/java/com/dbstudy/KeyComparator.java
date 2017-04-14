package com.dbstudy;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Comparator;

public class KeyComparator implements Comparator<byte[]>, Serializable {

	private static final long serialVersionUID = 6401742512177447575L;

	/**
     * Compares two DB keys.
     *
     * @param key1 first key
     * @param key2 second key
     *
     * @return comparison result
     */
    public int compare(byte[] key1, byte[] key2) {
        return new BigInteger(key1).compareTo(new BigInteger(key2));
    }
}