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

public enum TaggedDataName {
	/**
	 * the comb used for the sequencing gel.
	 */
	GEL_COMB("CMBF"),
	/**
	 * A Tagged block of data.
	 */
	DATA("DATA"),
	/**
	 * The order of the 3' terminal nucleotides
	 * corresponding to these four display trace arrays
	 */
	FILTER_WHEEL_ORDER("FWO_",true),
	/**
	 * 'GEL0' file from which 
	 * this sample's 'ABI1' data file was extracted.
	 */
	GEL_FILE_NAME ("GELN"),
	/**
	 * The path of the location of the 'GEL0' file.
	 */
	GEL_FILE_PATH ("GELP"),
	/**
	 * This sample's lane as loaded onto the gel.
	 */
	LANE("LANE"),
	/**
	 * Transformation of the photometer data 
	 * vector of the filters to the dye vector.
	 */
	TRANSFORM_MATRIX("MTRX"),
	/**
	 * The name of an external matrix file.
	 */
	TRANSFORM_MATRIX_FILE_NAME("MTXF"),
	/**
	 * Theorized to be the lateral pixel averaging of 
	 * photometer readings at the tracked pixel position(s)
	 * and the two flanking pixels at each scan.
	 */
	LAT_PIXEL_AVG("NAVG"),
	/**
	 * The number of sample lanes 
	 * tracked on the original gel file.
	 */
	NLNE("NLNE"),
	/**
	 * The ABI software's basecalls.
	 */
	BASECALLS("PBAS",true),
	
	/**
	 * external primer-dye mobility
	 * correction file
	 */
	DYE_PRIMER_CORRECTION_FILE("PDMF"),
	
	/**
	 * scan numbers of the ABI
	 * processed display traces at which
	 * the basecalls are centered.
	 */
	PEAK_LOCATIONS("PLOC"),
	
	/**
	 * the scan number of the raw data stream at which the
	 * first ABI basecall is reported
	 * (linking the time lines of the raw and 
	 * display trace data streams).
	 */
	PEAK_POSITIONS("PPOS"),
	
	/**
	 * The start and stop dates of the gel run.
	 */
	RUN_DATE("RUND"),
	
	/**
	 * The start and stop times of the gel run.
	 */
	RUN_TIME("RUNT"),
	
	/**
	 * Represents the scaling factors
	 * for the average signals recovered from the 
	 * bases.
	 */
	SCALE_FACTOR("S/N%"),
	
	/**
	 * The name of the sample.
	 */
	SAMPLE_NAME("SMPL"),
	
	/**
	 * The average
	 * spacing between successive oligomer traces.
	 */
	SPACING("SPAC"),
	
	/**
	 * Lane tracking center adjustments 
	 * which determined the path of data
	 * excised from the gel image file 
	 * on creating of this sample data file.
	 */
	TRACKING_CENTER_ADJUSTMENT("TRKC"),
	
	/**
	 * Lane tracking initial adjustments 
	 * which determined the path of data
	 * excised from the gel image file 
	 * on creating of this sample data file.
	 */
	TRACKING_INITIAL_ADJUSTMENT("TRKP"),
	
	
	JTC_END_POINT("AEPt"),
	
	JTC_PROTOCOL_NAME("APFN"),
	
	JTC_PROTOCOL_VERSION("APXV",true),
	
	APrN("APrN",true),
	
	APrV("APrV"),
	APrX("APrX"),
	
	ARTN("ARTN"),
	
	ASPF("ASPF"),
	
	JTC_START_POINT("ASPt"),
	
	AUDT("AUDT"),
	
	B1Pt("B1Pt"),
	
	BCTS("BCTS"),
	
	COMMENT("CMNT"),
	
	CT_ID("CTID",true),
	
	CT_NAME("CTNM",true),
	
	CT_OWNER("CTOw",true),
	
	CTTL("CTTL"),
	
	CpEP("CpEP"),
	
	DCHT("DCHT"),
	
	DSam("DSam"),
	
	DySN("DySN"),
	
	DYE_NUMBER("Dye#"),
	
	DyeN("DyeN"),
	
	DyeW("DyeW"),
	
	ELECTROPHERSIS_VOLTAGE("EPVt"),
	
	EVENT("EVNT"),
	/**
	 * Polymer/Gel Type.
	 */
	GEL_TYPE("GTyp"),
	/**
	 * There are usually several instrument information tags.  Each
	 * succeeding tag is a more specific datum of this particular instrument.
	 * For example: here is the order for some abi 3100 machine:
	 * <ol>
	 * <li>Instrument Class = "CE"</li>
	 * <li>Instrument Family = "31XX"</li>
	 * <li>Instrument Model = "3130xl"</li>
	 * <li>Instrument Parameters = "UnitID=7;CPUBoard=ECPU500;ArraySize=16;SerialNumber=1211-010"</li>
	 * </ol>
	 */
	INSTRUMENT_INFORMATION("HCFG",true),
	
	InSc("InSc"),
	
	InVt("InVt"),
	
	LIMS("LIMS"),
	
	LENGTH_OF_DETECTOR("LNTD"),
	
	LASER_POWER("LsrP"),
	
	MACHINE_NAME("MCHN"),
	
	MODF("MODF"),
	/**
	 * The ABI sequencing machine model number.
	 */
	MODEL("MODL",true),
	
	JTC_NOISE("NOIS"),
	
	JTC_QUALITY_VALUES("PCON"),
	/**
	 * The size of the Sequencing Plate being sequenced.
	 */
	PLATE_SZE("PSZE"),
	/**
	 * The type of plate being sequenced.
	 */
	PLATE_TYPE("PTYP",true),
	
	PXLB("PXLB"),
	/**
	 * The name of the "Results Group"
	 * this data belongs to.
	 */
	RESULTS_GROUP_NAME("RGNm",true),
	
	RMXV("RMXV"),
	RMdN("RMdN",true),
	RMdV("RMdV"),
	
	RMdX("RMdX",true),
	RPrN("RPrN",true),
	RPrV("RPrV"),
	
	Rate("Rate"),
	RevC("RevC"),
	JTC_RUN_NAME("RunN",true),
	SCAN("SCAN"),
	SMED("SMED"),
	SMLt("SMLt"),
	
	SOFTWARE_VERSION("SVER"),
	Scal("Scal"),
	
	Scan("Scan"),
	/**
	 * The Tube/ Well this trace came from.
	 */
	JTC_TUBE("TUBE"),
	JTC_TEMPERATURE("Tmpr"),
	USER("User"),
	
	phAR("phAR"),
	phCH("phCH"),
	phDY("phDY"),
	
	phQL("phQL"),
	phTR("phTR"),
	
	MATRIX_FILE_NAME("MTFX"),
	
	FTab("FTab"),
	
	FVoc("FVoc"),
	
	Feat("Feat"),
	OFFS("OFFS"),
	ScSt("ScSt"),
	OfSc("OfSc"),
	Satd("Satd"),
	
	BufT("BufT"),
	BufG("BufG"),
	BufC("BufC"),
	BufA("BufA"),
	
	LAST("LAST"),
	P1AM("P1AM"),
	P1RL("P1RL"),
	P1WD("P1WD"),
	P2AM("P2AM"),
    P2RL("P2RL"),  
    P2BA("P2BA"),
    RGOw("RGOw")
	
	;
	
	private static final Map<String, TaggedDataName> MAP;
	static{
		MAP = new HashMap<String, TaggedDataName>();
		for(TaggedDataName tag : values()){
			MAP.put(tag.toString(), tag);
		}
	}
	
	public static TaggedDataName parseTaggedDataName(String taggedDataName){
		if(MAP.containsKey(taggedDataName)){
		    return MAP.get(taggedDataName);
		}
		throw new IllegalArgumentException("unknown tag data name "+ taggedDataName);
		
	}
	private final String name;
	private final boolean isNullTerminated;
	
	private TaggedDataName(String name,boolean isNullTerminated){
	    this.name = name;
	    this.isNullTerminated=isNullTerminated;
	}
	private TaggedDataName(String name) {
		this(name,false);
	}

	/**
     * Does this tag use null terminated values.
     * Some tags are known to have data values that are null-terminated.
     * @return {@code true} if this tag uses null terminated values,
     * {@code false} otherwise.
     */
    public boolean usesNullTerminatedStringValues() {
        return isNullTerminated;
    }
    /* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
	   return name;
	}
	
	
}
