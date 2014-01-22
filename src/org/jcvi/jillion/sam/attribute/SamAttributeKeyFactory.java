package org.jcvi.jillion.sam.attribute;

public final class SamAttributeKeyFactory {

	private static final SamAttributeKey[][] CACHE = new SamAttributeKey[122][122];
	
	private SamAttributeKeyFactory(){
		//can not instantiate
	}
	public static SamAttributeKey getKey(String key){
		if(key.length() !=2){
			throw new IllegalArgumentException("key string must be 2 chars long " + key);
		}
		return getKey(key.charAt(0), key.charAt(1));
	}
	public static SamAttributeKey getKey(char first, char second){
		assertValidRange(first);
		assertValidRange(second);
		SamAttributeKey key = CACHE[first][second];
		if(key ==null){
			key = new SamAttributeKey(first, second);
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
