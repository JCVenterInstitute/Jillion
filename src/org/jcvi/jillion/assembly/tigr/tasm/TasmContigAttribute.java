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
 * {@code TasmContigAttribute} 
 * of all possible attribute keys
 * that can be attributed to 
 * a {@link TasmContig}.
 * These values are fixed since the attributes
 * correspond to the legacy TIGR
 * internal database columns that used
 * to store this information.
 * <p/>
 * The ordinal order of these attributes
 * is the order they are listed
 * in a TIGR Assembler {@literal .tasm}
 * file.
 * @author dkatzel
 *
 */
enum TasmContigAttribute {
	//ordinal order is the order in .tasm file
	//not sure if we need to keep this order
	//for bcp and aloader to work in TIGR Project database
	/**
	 * The ungapped consensus sequence as a string.
	 */
	UNGAPPED_CONSENSUS("sequence"),
	/**
	 * The gapped consensus sequence
	 * as a string. Gaps are denoted by '-'s.
	 */
	GAPPED_CONSENSUS("lsequence"),
	/**
	 * The primary key (an int) of this contig
	 * in the TIGR project database.
	 */
	ASMBL_ID("asmbl_id"),
	/**
	 * A GUID of the corresponding
	 * id from Celera Assembler.
	 * If Celera Assembler was not 
	 * used, then this field is either 
	 * set to NULL or a new GUID should be generated
	 * for it.
	 */
	CA_CONTIG_ID("ca_contig_id"),
	/**
	 * Comment explaining what this contig is
	 * might include the name of the chromosome or
	 * segment this contig belongs.
	 */
	COMMENT("comment"),	
	/**
	 * @deprecated No longer used.  This value
	 * is always NULL.
	 */
	@Deprecated
	SEQ_ID("seq_id"),
	/**
	 * Common name for this contig.
	 * Often this value is a combination 
	 * of the comment and bac id.
	 */
	COM_NAME("com_name"),	
	/**
	 * @deprecated No longer used.  This value
	 * is always NULL.
	 */
	@Deprecated
	TYPE("type"),
	/**
	 * The method used to create this
	 * contig.  This value is often
	 * the name of the assembler used
	 * or the software program
	 * that generated this contig.
	 */
	METHOD("method"),
	/**
	 * @deprecated No longer used.  This value
	 * is always 0.
	 */
	@Deprecated
	EDIT_STATUS("ed_status"),
	/**
	 * Average coverage of this contig 
	 * as a floating point number,
	 * usually to two decimal places.
	 */
	AVG_COVERAGE("redundancy"),
	/**
	 *  The number of Ns
	 *  rounded to the nearest hundredth percent.
	 *  This is usually not calculated and set to 0.0
	 *  since most TIGR tools hardly ever call N's.
	 */
	PERCENT_N("perc_N"),
	/**
	 * Number of reads in this contig.
	 */
	NUMBER_OF_READS("seq#"),
	/**
	 * @deprecated Not used anymore seems to default to {@code 2}.
	 */
	@Deprecated
	FULL_CDS("full_cds"),
	/**
	 * @deprecated Not used anymore seems to default to NULL.
	 */
	@Deprecated
	CDS_START("cds_start"),
	/**
	 * @deprecated Not used anymore seems to default to NULL.
	 */
	@Deprecated
	CDS_END("cds_end"),
	/**
	 * The username of the last person to edit 
	 * this contig.
	 */
	EDIT_PERSON("ed_pn"),
	/**
	 * The date of the last edit to this contig.
	 */
	EDIT_DATE("ed_date"),
	/**
	 * @deprecated Not used anymore seems to default to NULL.
	 */
	@Deprecated
	FRAME_SHIFT("frameshift"),
	/**
	 * Is this contig circular.
	 */
	IS_CIRCULAR("is_circular"),
	/**
	 * The bac id (sample id) that this contig belongs.
	 * Since the TIGR project database was created 
	 * projects moved away from being BAC based so
	 * even though this id is named "bac id" 
	 * it usually doesn't refer to a Bacterial Artificial
	 * Chromosome, but just a sample id.
	 */
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
