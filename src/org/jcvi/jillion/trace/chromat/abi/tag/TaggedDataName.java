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
package org.jcvi.jillion.trace.chromat.abi.tag;

import java.nio.charset.StandardCharsets;
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
	 * The number of capillaries.
	 */
	NUM_CAPILLARIES("NLNE"),
	/**
	 * The ABI software's basecalls.
	 * <ol>
	 * <li>Basecalls edited by user</li>
	 * <li>Basecalls called by basecaller</li>
	 * </ol>
	 */
	BASECALLS("PBAS",true),
	
	/**
	 * external primer-dye mobility
	 * correction file
	 */
	DYE_PRIMER_CORRECTION_FILE("PDMF"),
	
	/**
	 * <ol>
	 * <li>Peak Locations edited by user</li>
	 * <li>Peak Locations called by basecaller</li>
	 * <ol>
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
     * ABI 3500 additional peak information. There are 25 records of this tag
     * with different value datatypes:
     * <ol>
     * <li>Peak 1 short[] The peak dye indices</li>
     * <li>Peak 2 int[] The peak data points</li>
     * <li>Peak 3 int[] The peak begin data points</li>
     * <li>Peak 4 int[] The peak end data points</li>
     * <li>Peak 5 short[] FWHM of peak</li>
     * <li>Peak 6 double[] Corrected FWHM of peak</li>
     * <li>Peak 7 int[] The peak heights</li>
     * <li>Peak 8 int[] The peak begin heights</li>
     * <li>Peak 9 int[] The peak end heights</li>
     * <li>Peak 10 int[] The peak area (in data points)</li>
     * <li>Peak 11 double[] The corrected peak area (in data points)</li>
     * <li>Peak 12 double[] The peak positions (in bases)</li>
     * <li>Peak 13 double[] The peak begin positions (in bases)</li>
     * <li>Peak 14 double[] The peak end positions (in base)</li>
     * <li>Peak 15 double[] FWHM (in base) of peak</li>
     * <li>Peak 16 double[] Corrected FWHM (in base) of peak</li>
     * <li>Peak 17 double[] The peak area (in base)</li>
     * <li>Peak 18 double[] The corrected peak area (in base)</li>
     * <li>Peak 19 cString The peak labels separated by comma</li>
     * <li>Peak 20 cString The flag indicating whether peak is size matched
     * separated by comma</li>
     * <li>Peak 21 double[] The base when peak is size matched</li>
     * <li>Peak 22 cString The flag indicating whether peak is off-scale
     * separated by comma</li>
     * <li>Peak 23 cString The flag indicating whether peak is user created
     * separated by comma</li>
     * <li>Peak 24 cString The flag indicating whether peak is broaded</li>
     * <li>Peak 25 cString The flag indicating whether peak is pullup</li>
     * </ol>
     */
	PEAK_3500("Peak"),
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
	
	/**
	 * First occurrence for initial analysis, 2nd for occurrence for
	 * last analysis.
	 */
	ANALYSIS_ENDING_SCAN_POINT("AEPt"),
	/**
	 * Optional
	 */
	ANALYSIS_PARAMETERS_FILE_NAME("APFN"),
	
	ANALYSIS_PROTOCOL_XML_SCHEMA_VERSION("APXV",true),
	
	ANALYSIS_PROTOCOL_SETTING_NAME("APrN",true),
	
	ANALYSIS_PROTOCOL_SETTING_VERSION("APrV"),
	ANALYSIS_PROTOTCOL_SETTING_XML("APrX"),
	/**
	 * Analysis Return code. Produced only by 5 Prime basecaller {@code 1.0b3}.
	 */
	ANALYSIS_RETURN_CODE("ARTN"),
	/**
	 * Flag to indicate whether adaptive processing worked or not
	 */
	ADAPTIVE_PROCESSING_FLAG("ASPF"),
	/**
         * First occurrence for initial analysis, 2nd for occurrence for
         * last analysis.
         */
	ANALYSIS_START_SCAN_POINT("ASPt"),
	
	AUDIT_LOG("AUDT"),
	/**
	 * Reference scan number for mobility and spacing curves. first occurrence
	 * for initial analysis, second for last analysis.
	 */
	B1Pt("B1Pt"),
	/**
	 * Time of completion of most recent analysis
	 */
	BASECALLER_TIMESTAMP("BCTS"),
	
	COMMENT("CMNT"),
	/**
	 * Container Identifier, a.k.a. plate barcode.
	 */
	CONTAINER_ID("CTID",true),
	
	CONTAINER_NAME("CTNM",true),
	
	CONTAINER_OWNER("CTOw",true),
	
	COMMENT_TITLE("CTTL"),
	/**
	 * Capillary type electrophoresis. 1 for a capillary based machine. 
	 * 0 for a slab gel based machine.
	 */
	CAPILLARY_TYPE_ELECTROPHORESIS("CpEP"),
	/**
	 * The detection cell heater temperature setting from the Run Module.
	 * Reserved for backward compatibility. ; Not used for 3500.
	 */
	DETECTION_CELL_HEATER_TEMP("DCHT"),
	
	DOWNSAMPLING_RATE("DSam"),
	
	DYE_SET_NAME("DySN"),
	/**
	 * Number of dyes.
	 */
	DYE_NUMBER("Dye#"),
	
	DYE_NAME("DyeN"),
	
	DYE_WAVELENGTH("DyeW"),
	
	ELECTROPHERSIS_VOLTAGE("EPVt"),
	/**
	 * <ol>
	 * <li>Start Run Event</li>
	 * <li>Stop Run Event</li>
	 * <li>Start Collection Event</li>
	 * <li>Stop Collection Event</li>
	 * </ol>
	 */
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
	/**
	 * Injection time setting in Seconds.
	 */
	INJECTION_TIME_SETTINGS("InSc"),
	/**
	 * Injection Voltage setting in Volts
	 */
	INJECTION_VOLTAGE_SETTINGS("InVt"),
	
	SAMPLE_TRACKING_ID_FOR_LIMS("LIMS"),
	/**
	 * Length To Detector (capillary length)
	 */
	LENGTH_TO_DETECTOR("LNTD"),
	/**
	 * Laser power setting in micro Watts
	 */
	LASER_POWER("LsrP"),
	
	MACHINE_NAME("MCHN"),
	/**
	 * Run Module filename. This is redundant with the new tag #RmDn
	 */
	RUN_MODULE_FILENAME("MODF"),
	/**
	 * The ABI sequencing machine model number.
	 */
	MODEL("MODL",true),
	/**
	 * The estimate of rms baseline noise (S/N ratio) for each dye for a
successfully analyzed sample. Corresponds in order to the raw data in
tags DATA 1-4. KB basecaller only.
	 */
	NOISE("NOIS"),
	/**
	 * array of Quality values:
	 * <ol>
	 * <li>Array of quality Values (0-255) as edited by user</li>
	 * <li>Array of quality values (0-255) as called by Basecaller</li>
	 * </ol>
	 */
	QUALITY_VALUES("PCON"),
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
	/**
	 * Number of Scans.
	 */
	SCAN("SCAN"),
	/**
	 * Separation Medium Lot Exp Date
	 */
	SEPARATION_MEDIUM_EXPIRATION_DATE("SMED"),
	/**
	 * Separation Medium Lot Number (polymer lot number)
	 */
	SEPARATION_MEDIUM_LOT_NUMBER("SMLt"),
	/**
	 * SVER 1 pString Data Collection version number
SVER 2 cString Sizecaller version
SVER 3 pString Firmware version number
SVER 4 pString Sample File Format Version String
	 */
	SOFTWARE_VERSION("SVER"),
	/**
	 * Rescaling Divisor for reducing the dynamic range of the color data.
	 */
	Scal("Scal"),
	/**
	 * Number of scans. Redundant with {@link #SCAN}, but lower range. (legacy)
	 */
	Scan("Scan"),
	/**
	 * The Tube/ Well this trace came from.
	 */
	TUBE("TUBE"),
	/**
	 * Oven Temperature setting in degrees C.
	 */
	OVEN_TEMPERATURE("Tmpr"),
	USER("User"),
	/**
	 * The flag indicating whether size match results have been edited by the
user. Unused for 3500. Reserved for backward compatibility.
	 */
	USER_EDITED_SIZE_MATCH_RESULTS("UsrE"),
	/**
	 * Trace peak aria ratio.
	 */
	PEAK_ARIA_RATIO("phAR"),
	/**
	 * Chemistry type ("term", "prim", "unknown"), based on DYE_1
information
	 */
	CHEMISTRY_TYPE("phCH"),
	/**
	 * Dye ("big", "d-rhod", "unknown"), based on mob file information
	 */
	DYE_TYPE("phDY"),
	/**
	 * Maximum Quality Value.
	 */
	MAX_QUAL_VALUE("phQL"),
	/**
	 * phTR 1 short Set Trim region
phTR 2 float Trim probability
	 */
	phTR("phTR"),
	
	MATRIX_FILE_NAME("MTFX"),
	/**
	 * Feature Table.
	 */
	FTab("FTab"),
	/**
	 * Feature Vocabulary.
	 */
	FVoc("FVoc"),
	/**
	 * Features.
	 */
	Feat("Feat"),
	/**
	 * The off-scale data separated by semi-colon(;). Each data consists of five
numbers separated by comma: start data point, end data point, start
base, end base, and validity
	 */
	OFF_SCALE("OffS", true),
	/**
	 * Raw data start point. Set to 0 for 3500.data collection.
	 */
	RAW_DATA_START_POINT("ScSt"),
	/**
	 * List of scan numbers that are offscale (optional)
	 */
	OFF_SCALE_LIST("OfSc"),
	/**
	 * List of scan indexes which were saturated in the camera.
	 */
	SATURATED_SCAN_INDEXES("Satd"),
	
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
    RGOw("RGOw"),
    /**
     * Primary Analysis Audit Active indication. True if system auditing was
enabled during the last write of this file, false if system auditing was
disabled.
     */
    PRIMARY_ANALYSIS_AUDIT_ACTIVE_INDICATOR("AAct"),
    /**
     * Anode Buffer expiration Date in the format YYYY-MM-DDTHH:MM:SS.ss+/-HH:MM
     */
    ANODE_BUFFER_EXPIRATION_DATE("ABED"),
	/**
	 * Date the Anode Buffer was first installed.
	 */
    ANODE_BUFFER_INSTALL_DATE("ABID"),
    
    ANODE_BUFFER_LOT_NUMBER("ABLt"),
    /**
     * Number of Runs process by this anode buffer so far.
     */
    ANODE_BUFFER_RUN_NUMBER("ABRn"),
    
    ANODE_BUFFER_TYPE("ABTp"),
    
    ASSAY_VALIDATION_FLAG("AVld"),
    /**
     * Assay content as XML
     */
    ASSAY_XML("AsyC"),
    /**
     * list of ambient temperature readings.
     */
    AMBIENT_TEMP("AmbT"),
    
    ASSAY_NAME("AsyN"),
    
    ASSAY_VERSION("AsyV"),
    
    BASECALL_QC_CODE("BcRn"),
    /**
     * Comma Separated List of Basecall warnings or errors depending on the type.
     */
    BASECAL_WARNINGS_OR_ERRORS("BcRs"),
    
    CAPILLARY_ARRAY_EXPIRATION("CAED"),
    /**
     * Capillary array ID ?
     */
    CAID("CAID"),
    
    CAPILLARY_ARRAY_LOT_NUM("CALt"),
    /**
     * Number of injections so far through the capillary
     */
    CAPILLAY_INJECTION_COUNT("CARn"),
    
    CAPILLARY_ARRAY_SERIAL_NUM("CASN"),
    
    CATHODE_BUFFER_EXPIRATION_DATE("CBED"),
    
    CATHODE_BUFFER_INSTALL_DATE("CBID"),
    CATHODE_BUFFER_LOT_NUMBER("CBLt"),
    
    CATHODE_BUFFER_RUN_NUMBER("CBRn"),
    
    CATHODE_BUFFER_TYPE("CBTp"),
    /**
     * The Start of the Clear Range (inclusive) or the Length depending on the number.
     */
    CLEAR_RANGE_START_or_LENGTH("CLRG"),
    
    CONTINIOUS_READ_LENGTH_or_PASS_FAIL_CHECK("CRLn"),
    
    FILE_CHECKSUM("CkSm"),
    
    DCMD("DCMD"),
    
    INJECTION_NAME("InjN"),
    
    MEDIUM_PUPSCORE("PuSc"),
    
    QV20("QV20"),
    
    QC_PARAMS("QcPa"),
    
    QC_TRIMCODE("QcRn"),
    
    QC_WARNINGS_OR_ERRORS("QcRs"),
    
    RAMAN_NORMALIZATION_FACTOR("RNmF"),
    
    POLYMER_INSTALL_DATE("SMID"),
    
    POLYMER_RUN_NUMBER("SMRn"),
    
    ACTIVE_SPECTRAL_CALIBRATION_NAME("SpeN"),
    
    TRIMMING_PARAMETERS("TrPa"),
    
    /**
     * The root directory entry in the ab1 file.
     */
    DIRECTORY("tdir"),
    
    TRACE_SCORE("TrSc")
	;
	
	
	public static TaggedDataName getTagFromIntCode(int code){
	    return INT_MAP.get(code);
	}
	public static TaggedDataName parseTaggedDataName(String taggedDataName){
		if(MAP.containsKey(taggedDataName)){
		    return MAP.get(taggedDataName);
		}
		throw new IllegalArgumentException("unknown tag data name '"+ taggedDataName + "'");
		
	}
	private final String name;
	private final boolean isNullTerminated;
	
	private static final Map<Integer, TaggedDataName> INT_MAP;
	private static final Map<String, TaggedDataName> MAP;
        
	static{
	    TaggedDataName[] values = TaggedDataName.values();
	    INT_MAP = new HashMap<>(values.length);
	    
	    MAP = new HashMap<String, TaggedDataName>(values.length);
	    
            for(TaggedDataName tag : values){
                    MAP.put(tag.name, tag);
                    byte[] bytes = tag.name.getBytes(StandardCharsets.US_ASCII);
                    int value=0;
                    for(int i=bytes.length-1; i>=0; i--){
                        value<<=8;
                        value |= bytes[i];
                        
                    }
                    
                    INT_MAP.put(value, tag);
                    
            }
	}
	
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
