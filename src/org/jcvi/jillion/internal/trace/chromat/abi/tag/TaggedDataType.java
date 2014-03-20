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
package org.jcvi.jillion.internal.trace.chromat.abi.tag;

import java.util.HashMap;
import java.util.Map;

public enum TaggedDataType {

	CHAR(2),
	INTEGER(4),
	FLOAT(7),
	DATE(10),
	TIME(11),
	PASCAL_STRING(18),
	TYPE_19(19),
	TYPE_5(5),
	TYPE_1(1),
	/**
	 * User defined type whose data representations are up 
	 * to the creators of this type to know how to parse.
	 */
	USER_DEFINED(1024),
	;
	
	private static final Map<Integer, TaggedDataType> MAP;
	
	static{
		MAP = new HashMap<Integer, TaggedDataType>();
		for(TaggedDataType type : values()){
			MAP.put(Integer.valueOf(type.getValue()), type);
		}
	}
	
	public static TaggedDataType parseTaggedDataName(int dataTypeValue){
		Integer key = Integer.valueOf(dataTypeValue);
		if(!MAP.containsKey(key)){
			throw new IllegalArgumentException("Unknown TaggedDataType "+dataTypeValue);
		}
		return MAP.get(key);
		
	}
	
	private final int value;

	private TaggedDataType(int type) {
		this.value = type;
	}

	/**
	 * @return the value.
	 */
	public int getValue() {
		return value;
	}
	
	
}
