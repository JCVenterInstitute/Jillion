/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.assembly.tasm;

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
public enum TasmReadAttribute {
	NAME("seq_name"),
	CONTIG_LEFT("asm_lend"),
	CONTIG_RIGHT("asm_rend"),
	SEQUENCE_LEFT("seq_lend"),
	SEQUENCE_RIGHT("seq_rend"),
	BEST("best"),
	COMMENT("comment"),
	DB("db"),
	CONTIG_START_OFFSET("offset"),
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
