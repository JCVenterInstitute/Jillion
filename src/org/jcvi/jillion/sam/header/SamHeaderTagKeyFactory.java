package org.jcvi.jillion.sam.header;

public final class SamHeaderTagKeyFactory {

	private static final SamHeaderTagKey[][] CACHE = new SamHeaderTagKey[122][122];
	
	private SamHeaderTagKeyFactory(){
		//can not instantiate
	}
	public static SamHeaderTagKey getKey(String key){
		if(key.length() !=2){
			throw new IllegalArgumentException("key string must be 2 chars long " + key);
		}
		return getKey(key.charAt(0), key.charAt(1));
	}
	public static SamHeaderTagKey getKey(char first, char second){
		assertValidRange(first);
		assertValidRange(second);
		SamHeaderTagKey key = CACHE[first][second];
		if(key ==null){
			key = new SamHeaderTagKey(first, second);
			CACHE[first][second] = key;
		}
		return key;
		
	}

	private static void assertValidRange(char c) {
		//char is unsigned so no need to check if < 0
		if(c >= CACHE.length){
			throw new IllegalArgumentException("invalid char codepoint: " + (int) c);
		}
		
	}
}
