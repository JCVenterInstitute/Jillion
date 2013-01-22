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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tasm;

import java.util.HashMap;
import java.util.Map;

public enum TasmContigAttribute {
	//ordinal order is the order in .tasm file
	//not sure if we need to keep this order
	//for bcp to work in Project database
	UNGAPPED_CONSENSUS("sequence"),
	GAPPED_CONSENSUS("lsequence"),
	ASMBL_ID("asmbl_id"),
	CA_CONTIG_ID("ca_contig_id"),	
	COMMENT("comment"),	
	SEQ_ID("seq_id"),
	COM_NAME("com_name"),	
	TYPE("type"),
	METHOD("method"),
	EDIT_STATUS("ed_status"),
	AVG_COVERAGE("redundancy"),
	PERCENT_N("perc_N"),
	NUMBER_OF_READS("seq#"),
	FULL_CDS("full_cds"),
	CDS_START("cds_start"),
	CDS_END("cds_end"),
	EDIT_PERSON("ed_pn"),
	EDIT_DATE("ed_date"),
	FRAME_SHIFT("frameshift"),
	IS_CIRCULAR("is_circular"),
	BAC_ID("bac_id")
	;
	
	private final String assemblyTableColumn;
	private static final Map<String, TasmContigAttribute> MAP = new HashMap<String, TasmContigAttribute>();
	static{
		for(TasmContigAttribute attribute : values()){
			MAP.put(attribute.getAssemblyTableColumn(), attribute);
		}
	}
	TasmContigAttribute(String assemblyTableColumn){
		this.assemblyTableColumn = assemblyTableColumn;
	}

	/**
	 * @return the assemblyTableColumn
	 */
	public String getAssemblyTableColumn() {
		return assemblyTableColumn;
	}
	
	public static TasmContigAttribute getAttributeFor(String attribute){
		if(MAP.containsKey(attribute)){
			return MAP.get(attribute);
		}
		throw new IllegalArgumentException("unknown attribute "+attribute);
	}
}
