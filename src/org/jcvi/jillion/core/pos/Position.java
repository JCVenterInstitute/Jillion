/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.pos;


public final class Position{
	//most sanger traces only go to 15k or so
	private static final int INITIAL_CACHE_SIZE = 20000;
	private static final Position[] CACHE;

	private final int value;
	
	static{
		CACHE = new Position[INITIAL_CACHE_SIZE];
		for(int i=0; i< INITIAL_CACHE_SIZE; i++){
			CACHE[i] = new Position(i);
		}
	}
	
	public static Position valueOf(int value){
		if(value <0){
			throw new IllegalArgumentException("position value can not be negative");
		}
		if(value < CACHE.length){
			return CACHE[value];
		}
		return  new Position(value);
	}
	
	@Override
	public String toString() {
		return Integer.toString(value);
	}

	private Position(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Position)) {
			return false;
		}
		Position other = (Position) obj;
		if (value != other.value) {
			return false;
		}
		return true;
	}

}
