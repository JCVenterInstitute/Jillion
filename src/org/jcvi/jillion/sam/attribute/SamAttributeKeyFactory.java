/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
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
