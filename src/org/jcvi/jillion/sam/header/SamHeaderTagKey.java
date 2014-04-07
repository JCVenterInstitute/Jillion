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
package org.jcvi.jillion.sam.header;

import org.jcvi.jillion.internal.sam.SamUtil;

/**
 * {@code SamHeaderTagKey}
 * is an object representation
 * of the two letter key 
 * of a Header Tag.
 * @author dkatzel
 *
 */
public final class SamHeaderTagKey {

	private static final SamHeaderTagKey[][] CACHE = new SamHeaderTagKey[122][122];
	
	/**
	 * The two letters of our key
	 * stored as primitives to save memory.
	 */
	private final char key1,key2;
	
	SamHeaderTagKey(char key1, char key2) {
		 if(!SamUtil.isValidKey(key1, key2)){
			 throw new IllegalArgumentException("invalid key " + key1 + key2);
		 
		 }
		
		this.key1 = key1;
		this.key2 = key2;
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
	

	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key1;
		result = prime * result + key2;
		return result;
	}
	/**
	 * Two Keys are equal if they have the same
	 * 2 characters.
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		SamHeaderTagKey other = (SamHeaderTagKey) obj;
		if (key1 != other.key1){
			return false;
		}			
		if (key2 != other.key2){
			return false;
		}
		return true;
	}
	/**
	 * The two letter key as a String.
	 * @return
	 */
	@Override
	public String toString(){
		return new StringBuilder(2).append(key1).append(key2).toString();
	}
	/**
	 * Get the first character of the key.
	 * @return a char
	 */
	public char getFirstChar() {
		return key1;
	}
	/**
	 * Get the second character of the key.
	 * @return a char
	 */
	public char getSecondChar() {
		return key2;
	}
}
