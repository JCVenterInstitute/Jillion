/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
