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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.util.HashMap;
import java.util.Map;
/**
 * {@code TasmReadAttribute} 
 * of all possible attribute keys
 * that can be attributed to 
 * a {@link TasmAssembledRead}.
 * These values are fixed since the attributes
 * correspond to the legacy TIGR
 * internal database columns that used
 * to store this information.
 * @author dkatzel
 *
 */
enum TasmReadAttribute {
	//dkatzel- these java doc comments
	//explaining what each tasm attribute means
	//comes from javadoc from cloe's TDBAssemblySequence object so
	//I assume it's correct...
	/**
	 * The name of the sequence
	 */
	NAME("seq_name"),
	/**
	 * The assembly left end (1-based position of the ungapped
     * consensus at which this sequence begins to provide coverage)
	 */
	CONTIG_LEFT("asm_lend"),
	/**
	 * The assembly right end (1-based final position of the ungapped
     * consensus for which this sequence provides coverage)
	 */
	CONTIG_RIGHT("asm_rend"),
	/**
	 * 1-based position of the left end of the valid range of this
     * ungapped sequence.  The value returned will be greater than
     * what would be returned by {@link #SEQUENCE_RIGHT} if this
     * sequence has been reverse complemented in the assembly.
	 */
	SEQUENCE_LEFT("seq_lend"),
	/**
	 * 1-based position of the right end of the valid range of this
     * ungapped sequence.  The value returned will be less than
     * what would be returned by {@link #SEQUENCE_LEFT} if this
     * sequence has been reverse complemented in the assembly.
	 */
	SEQUENCE_RIGHT("seq_rend"),
	/**
	 * A 1 bit flag denoting if this is the best
	 * read (?). 
	 * 
	 * @deprecated This field isn't used anymore
	 * and defaults to 0.  
	 */
	@Deprecated
	BEST("best"),
	/**
	 * Comment about this read. 
	 * 
	 * @deprecated This field isn't
	 * used anymore and defaults to NULL.
	 */
	@Deprecated
	COMMENT("comment"),
	/**
	 * What database this read is in.  
	 * 
	 * @deprecated This field
	 * isn't used anymore since the TIGR
	 * Project database separated out reads in different
	 * databases so this field should always be NULL.
	 */
	@Deprecated
	DB("db"),
	/**
	 * The <b>zero-based</b> offset of this sequence within the
     * <b>gapped</b> consensus
	 */
	CONTIG_START_OFFSET("offset"),
	/**
	 * The gapped VALID sequence of this read.  Only the trimmed portion
	 * of this read that is used in the contig is included.
	 */
	GAPPED_SEQUENCE("lsequence"),
	
;
	
	private final String assemblyTableColumn;
	private static final Map<String, TasmReadAttribute> MAP = new HashMap<String, TasmReadAttribute>();
	static{
		for(TasmReadAttribute attribute : values()){
			MAP.put(attribute.getAssemblyTableColumn(), attribute);
		}
	}
	TasmReadAttribute(String assemblyTableColumn){
		this.assemblyTableColumn = assemblyTableColumn;
	}

	/**
	 * @return the assemblyTableColumn
	 */
	public String getAssemblyTableColumn() {
		return assemblyTableColumn;
	}
	
	public static TasmReadAttribute getAttributeFor(String attribute){
		if(MAP.containsKey(attribute)){
			return MAP.get(attribute);
		}
		throw new IllegalArgumentException("unknown attribute "+attribute);
	}
}
