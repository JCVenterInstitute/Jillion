package org.jcvi.trace.sanger.chromatogram.abi.tag;

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
	TYPE_1024(1024),
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
