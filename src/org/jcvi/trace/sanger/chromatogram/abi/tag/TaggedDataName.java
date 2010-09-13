/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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
package org.jcvi.trace.sanger.chromatogram.abi.tag;

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
	FILTER_WHEEL_ORDER("FWO_"),
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
	BASECALLS("PBAS"),
	
	/**
	 * external primer-dye mobility
	 * correction file
	 */
	CORRECTION_FILE("PDMF"),
	
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
	
	
	AEPt("AEPt"),
	
	APFN("APFN"),
	
	APXV("APXV"),
	
	APrN("APrN"),
	
	APrV("APrV"),
	APrX("APrX"),
	
	ARTN("ARTN"),
	
	ASPF("ASPF"),
	
	ASPt("ASPt"),
	
	AUDT("AUDT"),
	
	B1Pt("B1Pt"),
	
	BCTS("BCTS"),
	
	COMMENT("CMNT"),
	
	CTID("CTID"),
	
	CTNM("CTNM"),
	
	CTOw("CTOw"),
	
	CTTL("CTTL"),
	
	CpEP("CpEP"),
	
	DCHT("DCHT"),
	
	DSam("DSam"),
	
	DySN("DySN"),
	
	DYE_NUMBER("Dye#"),
	
	DyeN("DyeN"),
	
	DyeW("DyeW"),
	
	EPVt("EPVt"),
	
	EVENT("EVNT"),
	
	GTyp("GTyp"),
	
	HCFG("HCFG"),
	
	InSc("InSc"),
	
	InVt("InVt"),
	
	LIMS("LIMS"),
	
	LNTD("LNTD"),
	
	LsrP("LsrP"),
	
	MCHN("MCHN"),
	
	MODF("MODF"),
	
	MODEL("MODL"),
	
	NOIS("NOIS"),
	/**
	 * Possibly quality values?
	 */
	QUALITY("PCON"),
	
	PSZE("PSZE"),
	
	PTYP("PTYP"),
	
	PXLB("PXLB"),
	
	RGNm("RGNm"),
	
	RMXV("RMXV"),
	RMdN("RMdN"),
	RMdV("RMdV"),
	
	RMdX("RMdX"),
	RPrN("RPrN"),
	RPrV("RPrV"),
	
	Rate("Rate"),
	RevC("RevC"),
	RunN("RunN"),
	SCAN("SCAN"),
	SMED("SMED"),
	SMLt("SMLt"),
	
	SVER("SVER"),
	Scal("Scal"),
	
	Scan("Scan"),
	TUBE("TUBE"),
	Tmpr("Tmpr"),
	USER("User"),
	
	phAR("phAR"),
	phCH("phCH"),
	phDY("phDY"),
	
	phQL("phQL"),
	phTR("phTR"),
	
	
	
	
	
	
	
	
	
	;
	
	private static final Map<String, TaggedDataName> MAP;
	static{
		MAP = new HashMap<String, TaggedDataName>();
		for(TaggedDataName tag : values()){
			MAP.put(tag.toString(), tag);
		}
	}
	
	public static TaggedDataName parseTaggedDataName(String taggedDataName){
		
		return MAP.get(taggedDataName);
		
	}
	private final String name;

	
	private TaggedDataName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	
	
}
