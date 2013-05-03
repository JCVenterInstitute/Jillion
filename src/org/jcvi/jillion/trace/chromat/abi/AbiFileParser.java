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
package org.jcvi.jillion.trace.chromat.abi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.trace.chromat.abi.AbiUtil;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.Ab1LocalDate;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.Ab1LocalTime;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.AsciiTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.ByteArrayTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.DateTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.DefaultShortArrayTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.FloatArrayTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.IntArrayTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.PascalStringTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.ShortArrayTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.StringTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.TaggedDataName;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.TaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.TaggedDataRecordBuilder;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.TaggedDataType;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.TimeTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.UserDefinedTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.rate.ScanRateTaggedDataType;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.rate.ScanRateUtils;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;
/**
 * {@code Ab1FileParser} can parse an
 * Applied BioSystems "ab1" formatted chromatogram
 * file.
 * @author dkatzel
 *
 */
public final class AbiFileParser {

	
	
	private static final byte ZERO_QUALITY = (byte)0;
	private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("EEE dd MMM HH:mm:ss yyyy", Locale.US);
	/**
	 * ABI files store both the original and current
	 * (possibly edited) data.  This is the index
	 * order the original version
	 * of the tag when both are present.
	 */
	private static final int ORIGINAL_VERSION = 0;
	/**
	 * ABI files store both the original and current
	 * (possibly edited) data.  This is the index
	 * order the current version
	 * of the tag when both are present.
	 */
	private static final int CURRENT_VERSION =1;
	
	private AbiFileParser() {
		//can not instantiate
	}
	/**
	 * Parse the given Applied BioSystems 
	 * "ab1" formatted chromatogram file
	 * and call the appropriate visitXXX
	 * methods on the given {@link ChromatogramFileVisitor}.
	 * @param ab1File the ab1 file to be parsed.
	 * @param visitor  {@link ChromatogramFileVisitor} to call visitXXX
	 * methods on.
	 * @throws FileNotFoundException if the given File does not exist.
	 * @throws TraceDecoderException if there are problems 
	 * parsing the chromatogram.
	 */
	public static void parse(File ab1File, ChromatogramFileVisitor visitor) throws FileNotFoundException, TraceDecoderException{
		InputStream in = null;
		try{
			in = new FileInputStream(ab1File);
			parse(in, visitor);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	/**
	 * Parse the given Applied BioSystems 
	 * "ab1" formatted chromatogram InputStream.
	 * and call the appropriate visitXXX
	 * methods on the given {@link ChromatogramFileVisitor}.
	 * @param in the ab1 formatted InputStream to be parsed.
	 * @param visitor  {@link ChromatogramFileVisitor} to call visitXXX
	 * methods on.
	 * @throws TraceDecoderException if there are problems 
	 * parsing the chromatogram.
	 */
	public static void parse(InputStream in, ChromatogramFileVisitor visitor) throws TraceDecoderException{
			verifyMagicNumber(in);
			visitor.visitNewTrace();
			long numberOfTaggedRecords = parseNumTaggedRecords(in);
			int datablockOffset = parseTaggedRecordOffset(in);
			//All the record info is actually stored
			//AFTER the raw data.
			//In order to avoid re-parsing the stream
			//(we can't guarantee being able to seek backwards
			//from all inputstream implementations)
			//we have to cache the raw data into a byte array for
			//later handling.
			byte[] traceData = parseTraceDataBlock(in, datablockOffset-AbiUtil.HEADER_SIZE);
			GroupedTaggedRecords groupedDataRecordMap = parseTaggedDataRecords(in,numberOfTaggedRecords,traceData,visitor);
	
			List<Nucleotide> channelOrder =parseChannelOrder(groupedDataRecordMap);
			visitChannelOrderIfAble(visitor, channelOrder);			
			List<NucleotideSequence> basecalls =parseBasecallsFrom(groupedDataRecordMap,traceData,visitor);	
			String signalScale =parseSignalScalingFactor(groupedDataRecordMap, channelOrder, traceData,visitor);
			Map<String,String> comments =parseDataChannels(groupedDataRecordMap,channelOrder,traceData,visitor);
			parsePeakData(groupedDataRecordMap,traceData,visitor);
			parseQualityData(groupedDataRecordMap,traceData,basecalls,visitor);
			parseCommentsFrom(comments,groupedDataRecordMap,channelOrder,traceData,signalScale,basecalls,visitor);
            visitor.visitEndOfTrace();
	}

	private static String parseSignalScalingFactor(
			GroupedTaggedRecords groupedDataRecordMap,
			List<Nucleotide> channelOrder, byte[] traceData,
			ChromatogramFileVisitor visitor) {		
		
			ShortArrayTaggedDataRecord scalingFactors =groupedDataRecordMap.shortArrayDataRecords.get(TaggedDataName.SCALE_FACTOR).get(0);
			List<Short> list = convertToShortList(traceData, scalingFactors);
			SignalScalingFactor scalingFactor = SignalScalingFactor.create(channelOrder, list);
			
			if(visitor instanceof AbiChromatogramFileVisitor){
			    ((AbiChromatogramFileVisitor) visitor).visitScaleFactors(
			            scalingFactor.aScale,scalingFactor.cScale,scalingFactor.gScale,scalingFactor.tScale);
			}
			return scalingFactor.toString();
		
	}
    private static List<Short> convertToShortList(byte[] traceData,
            ShortArrayTaggedDataRecord scalingFactors) {
        List<Short> list = new ArrayList<Short>();
        for(short s: scalingFactors.parseDataRecordFrom(traceData)){
        	list.add(Short.valueOf(s));
        }
        return list;
    }

	private static void parseQualityData(
			GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
			List<NucleotideSequence> basecallsList,
			ChromatogramFileVisitor visitor) {
		
		List<ByteArrayTaggedDataRecord> qualityRecords =groupedDataRecordMap.byteArrayRecords.get(TaggedDataName.JTC_QUALITY_VALUES);
		for(int i=0; i<qualityRecords.size(); i++){
		    ByteArrayTaggedDataRecord qualityRecord = qualityRecords.get(i);
		    NucleotideSequence basecalls = basecallsList.get(i);
			byte[][] qualities = splitQualityDataByChannel(basecalls, qualityRecord.parseDataRecordFrom(traceData));
			if(i == ORIGINAL_VERSION && visitor instanceof AbiChromatogramFileVisitor){
				AbiChromatogramFileVisitor ab1Visitor = (AbiChromatogramFileVisitor)visitor;
				handleOriginalConfidenceValues(qualities, ab1Visitor);
			}
			if(i == CURRENT_VERSION){
				handleCurrentConfidenceValues(visitor, qualities);
			}
		}
	}
    private static void handleCurrentConfidenceValues(
            ChromatogramFileVisitor visitor, byte[][] qualities) {
        visitor.visitAConfidence(qualities[0]);
        visitor.visitCConfidence(qualities[1]);
        visitor.visitGConfidence(qualities[2]);
        visitor.visitTConfidence(qualities[3]);
    }
    private static void handleOriginalConfidenceValues(byte[][] qualities,
            AbiChromatogramFileVisitor ab1Visitor) {
        ab1Visitor.visitOriginalAConfidence(qualities[0]);
        ab1Visitor.visitOriginalCConfidence(qualities[1]);
        ab1Visitor.visitOriginalGConfidence(qualities[2]);
        ab1Visitor.visitOriginalTConfidence(qualities[3]);
    }
	/**
	 * To conform with {@link ChromatogramFileVisitor},
	 * each Channel must have its own quality data.
	 * ABI traces don't have that information,
	 * so we must create it based on the basecalls.
	 * Any called base that is not an A,C or G is put in the T
	 * quality channel.
	 * @param basecalls the basecalls to use to split the qualities.
	 * @param qualities the quality values of the called base.
	 * @return a byte matrix containing the quality channel
	 * data for A,C,G,T in that order.
	 */
	private static byte[][] splitQualityDataByChannel(NucleotideSequence basecalls,byte[] qualities ){
		//The channel of the given basecall gets that
		// quality value, the other channels get zero
		int size = (int)basecalls.getLength();
		ByteBuffer aQualities = ByteBuffer.allocate(size);
		ByteBuffer cQualities = ByteBuffer.allocate(size);
		ByteBuffer gQualities = ByteBuffer.allocate(size);
		ByteBuffer tQualities = ByteBuffer.allocate(size);
		
		populateQualities(basecalls, qualities, aQualities, cQualities, gQualities, tQualities);
		return new byte[][]{aQualities.array(),cQualities.array(),gQualities.array(),tQualities.array()};
	}
    private static void populateQualities(NucleotideSequence basecalls,
            byte[] qualities, ByteBuffer aQualities, ByteBuffer cQualities,
            ByteBuffer gQualities, ByteBuffer tQualities) {
    	Iterator<Nucleotide> basecallIterator = basecalls.iterator();
        for(int i=0; i<qualities.length; i++){
			populateQualities(aQualities, cQualities, gQualities, tQualities, basecallIterator.next(), qualities[i]);
		}
    }
    private static void populateQualities(ByteBuffer aQualities, ByteBuffer cQualities,
            ByteBuffer gQualities, ByteBuffer tQualities, Nucleotide basecall, byte quality) {
        switch(basecall){
        	case Adenine:
        		handleAQuality(aQualities, cQualities, gQualities, tQualities, quality);
        		break;
        		
        	case Cytosine:
        		handleCQuality(aQualities, cQualities, gQualities, tQualities, quality);
        		break;
        	case Guanine:
        		handleGQuality(aQualities, cQualities, gQualities, tQualities, quality);
        		break;
        	//anything else is automatically a T
        	default:
        		handleTQuality(aQualities, cQualities, gQualities, tQualities, quality);				
        		break;
        }
    }
    private static void handleTQuality(ByteBuffer aQualities,
            ByteBuffer cQualities, ByteBuffer gQualities,
            ByteBuffer tQualities, byte quality) {
        aQualities.put(ZERO_QUALITY);
        cQualities.put(ZERO_QUALITY);
        gQualities.put(ZERO_QUALITY);
        tQualities.put(quality);
    }
    private static void handleGQuality(ByteBuffer aQualities,
            ByteBuffer cQualities, ByteBuffer gQualities,
            ByteBuffer tQualities, byte quality) {
        aQualities.put(ZERO_QUALITY);
        cQualities.put(ZERO_QUALITY);
        gQualities.put(quality);
        tQualities.put(ZERO_QUALITY);
    }
    private static void handleCQuality(ByteBuffer aQualities,
            ByteBuffer cQualities, ByteBuffer gQualities,
            ByteBuffer tQualities, byte quality) {
        aQualities.put(ZERO_QUALITY);
        cQualities.put(quality);
        gQualities.put(ZERO_QUALITY);
        tQualities.put(ZERO_QUALITY);
    }
    private static void handleAQuality(ByteBuffer aQualities,
            ByteBuffer cQualities, ByteBuffer gQualities,
            ByteBuffer tQualities, byte quality) {
        aQualities.put(quality);
        cQualities.put(ZERO_QUALITY);
        gQualities.put(ZERO_QUALITY);
        tQualities.put(ZERO_QUALITY);
    }
	private static void parsePeakData(
			GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
			ChromatogramFileVisitor visitor) {
		List<ShortArrayTaggedDataRecord> peakRecords =groupedDataRecordMap.shortArrayDataRecords.get(TaggedDataName.PEAK_LOCATIONS);
		
		if(visitor instanceof AbiChromatogramFileVisitor){
			short[] originalPeakData =peakRecords.get(ORIGINAL_VERSION).parseDataRecordFrom(traceData);
			
			((AbiChromatogramFileVisitor) visitor).visitOriginalPeaks(originalPeakData);
		}
		short[] peakData =peakRecords.get(CURRENT_VERSION).parseDataRecordFrom(traceData);
		visitor.visitPeaks(peakData);
	}

	private static Map<String,String> parseDataChannels(
			GroupedTaggedRecords groupedDataRecordMap,
			List<Nucleotide> channelOrder,
			byte[] traceData,
			ChromatogramFileVisitor visitor) {
		List<ShortArrayTaggedDataRecord> dataRecords =groupedDataRecordMap.shortArrayDataRecords.get(TaggedDataName.DATA);
		if(visitor instanceof AbiChromatogramFileVisitor){
			AbiChromatogramFileVisitor ab1Visitor = (AbiChromatogramFileVisitor) visitor;
			//parse extra ab1 data
			visitAb1ExtraChannels(traceData, dataRecords, ab1Visitor);			
		}
		Map<String,String> props = new HashMap<String, String>();
		for(int i=0; i<4; i++){
			Nucleotide channel = channelOrder.get(i);
			short[] channelData =dataRecords.get(i+8).parseDataRecordFrom(traceData);
			props.put("NPTS", ""+channelData.length);
			visitChannel(visitor, channel, channelData);
		}
		return props;
	}
    private static void visitChannel(ChromatogramFileVisitor visitor,
            Nucleotide channel, short[] channelData) {
        switch(channel){
        	case Adenine:
        		visitor.visitAPositions(channelData);					
        		break;
        	case Thymine:
        		visitor.visitTPositions(channelData);
        		break;
        	case Guanine:
        		visitor.visitGPositions(channelData);
        		break;
        	case Cytosine:
        		visitor.visitCPositions(channelData);
        		break;
        	default:
        		throw new IllegalStateException("invalid channel "+ channel);	
        }
    }
    private static void visitAb1ExtraChannels(byte[] traceData,
            List<ShortArrayTaggedDataRecord> dataRecords,
            AbiChromatogramFileVisitor ab1Visitor) {
        for(int i=0; i< 4; i++){
        	short[] rawTraceData =dataRecords.get(i).parseDataRecordFrom(traceData);
        	ab1Visitor.visitPhotometricData(rawTraceData,i);
        }
        ab1Visitor.visitGelVoltageData(dataRecords.get(4).parseDataRecordFrom(traceData));
        ab1Visitor.visitGelCurrentData(dataRecords.get(5).parseDataRecordFrom(traceData));
        ab1Visitor.visitElectrophoreticPower(dataRecords.get(6).parseDataRecordFrom(traceData));
        ab1Visitor.visitGelTemperatureData(dataRecords.get(7).parseDataRecordFrom(traceData));
    }

	private static void visitChannelOrderIfAble(
			ChromatogramFileVisitor visitor, List<Nucleotide> channelOrder) {
		if(visitor instanceof AbiChromatogramFileVisitor){
			((AbiChromatogramFileVisitor) visitor).visitChannelOrder(channelOrder);
		}
	}
	/**
	 * create comments to match IO_LIb implementation for 100%
	 * compatibility.
	 * @param groupedDataRecordMap
	 * @param traceData
	 * @param visitor
	 */
	private static void parseCommentsFrom(
	        Map<String,String> props,
			GroupedTaggedRecords groupedDataRecordMap, 
			List<Nucleotide> channelOrder,byte[] traceData,
			String signalScale, List<NucleotideSequence> basecalls,
			ChromatogramFileVisitor visitor) {
		props.put("SIGN", signalScale);
		props = addStringComments(groupedDataRecordMap, traceData, props);
		props = addSingleShortValueComments(groupedDataRecordMap, traceData, props);
	//	props = extractSingleIntValueComments(groupedDataRecordMap, traceData, props);
		props = addChannelOrderComment(channelOrder,props);
		props = addSpacingComment(groupedDataRecordMap, traceData, props);
		props = addTimeStampComment(groupedDataRecordMap, traceData, props);
		props = addNoiseComment(groupedDataRecordMap, channelOrder,traceData,props);
		props = addNumberOfBases(basecalls,props);
		props = parseSamplingRateFrom(groupedDataRecordMap, traceData, props);
		visitor.visitComments(props);
	}


    /**
     * @param groupedDataRecordMap
     * @param traceData
     * @param props
     * @return
     */
    private static Map<String,String> parseSamplingRateFrom(
            GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
            Map<String,String> props) {
        Map<TaggedDataName, List<UserDefinedTaggedDataRecord>>map= groupedDataRecordMap.userDefinedDataRecords;
        if(map.containsKey(TaggedDataName.Rate)){
            ScanRateTaggedDataType scanRate = (ScanRateTaggedDataType)map.get(TaggedDataName.Rate).get(0);
            props.put("SamplingRate", String.format("%.3f",
                    ScanRateUtils.getSamplingRateFor(scanRate.parseDataRecordFrom(traceData))));
        }
        
        return props;
    }

    /**
     * @param groupedDataRecordMap
     * @param traceData
     * @param props
     * @return
     */
    private static Map<String,String> addNumberOfBases(
            List<NucleotideSequence> basecalls,
            Map<String,String> props) {
        props.put("NBAS", ""+basecalls.get(ORIGINAL_VERSION).getLength());
        return props;
    }

    /**
     * @param groupedDataRecordMap
     * @param traceData
     * @param props
     * @return
     */
    private static Map<String,String> addNoiseComment(
            GroupedTaggedRecords groupedDataRecordMap,
            List<Nucleotide> channelOrder,
            byte[] traceData,
            Map<String,String> props) {
        Map<TaggedDataName, List<FloatArrayTaggedDataRecord>> map= groupedDataRecordMap.floatDataRecords;
        if(map.containsKey(TaggedDataName.JTC_NOISE)){
            float[] noiseData = map.get(TaggedDataName.JTC_NOISE).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData);
            Noise noise = Noise.create(channelOrder, noiseData);
            
            props.put("NOIS",noise.toString()); 
        }
        return props;
    }

    /**
     * @param groupedDataRecordMap
     * @param traceData
     * @param props
     * @return
     */
    private static synchronized Map<String,String> addTimeStampComment(
            GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
            Map<String,String> props) {
        Map<TaggedDataName, List<DateTaggedDataRecord>> dates= groupedDataRecordMap.dateDataRecords;
        Map<TaggedDataName, List<TimeTaggedDataRecord>> times= groupedDataRecordMap.timeDataRecords;
        if(dates.containsKey(TaggedDataName.RUN_DATE) && times.containsKey(TaggedDataName.RUN_TIME)){
        	Ab1LocalDate startDate =dates.get(TaggedDataName.RUN_DATE).get(0).parseDataRecordFrom(traceData);
        	Ab1LocalDate endDate =dates.get(TaggedDataName.RUN_DATE).get(1).parseDataRecordFrom(traceData);
            
            Ab1LocalTime startTime = times.get(TaggedDataName.RUN_TIME).get(0).parseDataRecordFrom(traceData);
            Ab1LocalTime endTime = times.get(TaggedDataName.RUN_TIME).get(1).parseDataRecordFrom(traceData);
            
            final Date startDateTime = startDate.toDate(startTime);
            final Date endDateTime = endDate.toDate(endTime);
            props.put("DATE", String.format("%s to %s",
            		DATE_FORMATTER.format(startDateTime),
            		DATE_FORMATTER.format(endDateTime)
            		));
            
            
            props.put("RUND", String.format("%04d%02d%02d.%02d%02d%02d - %04d%02d%02d.%02d%02d%02d",
            		startDate.getYear(), startDate.getMonth()+1, startDate.getDay(),
            		startTime.getHour(), startTime.getMin(), startTime.getSec(),
            		
            		endDate.getYear(), endDate.getMonth()+1, endDate.getDay(),
            		endTime.getHour(), endTime.getMin(), endTime.getSec()
            		
            ));
        }
        return props;
    }

    /**
     * @param groupedDataRecordMap
     * @param traceData
     * @param props
     * @return
     */
    private static Map<String,String> addSpacingComment(
            GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
            Map<String,String> props) {
        Map<TaggedDataName, List<FloatArrayTaggedDataRecord>> map= groupedDataRecordMap.floatDataRecords;
        if(map.containsKey(TaggedDataName.SPACING)){
            props.put("SPAC", String.format("%-6.2f",map.get(TaggedDataName.SPACING).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData)[0]));
        }
        
        return props;

    }

    /**
     * @param channelOrder
     * @param props
     * @return
     */
    private static Map<String,String> addChannelOrderComment(
            List<Nucleotide> channelOrder, Map<String,String> props) {
        StringBuilder order = new StringBuilder();
        for(Nucleotide channel: channelOrder){
            order.append(channel.getCharacter());
        }
        props.put("FWO_", order.toString() );
        return props;
    }

    protected static Properties extractSingleIntValueComments(
            GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
            Properties props) {
        Map<TaggedDataName, List<IntArrayTaggedDataRecord>> map= groupedDataRecordMap.intArrayDataRecords;
        if(map.containsKey(TaggedDataName.JTC_TEMPERATURE)){
            props.put("Tmpr", map.get(TaggedDataName.JTC_TEMPERATURE).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData)[0]);
        }
        if(map.containsKey(TaggedDataName.ELECTROPHERSIS_VOLTAGE)){
            props.put("EPVt", map.get(TaggedDataName.ELECTROPHERSIS_VOLTAGE).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData)[0]);
        }
        return props;
    }
    protected static Map<String,String> addSingleShortValueComments(
            GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
            Map<String,String> props) {
        Map<TaggedDataName, List<ShortArrayTaggedDataRecord>> map= groupedDataRecordMap.shortArrayDataRecords;
        
        for(ShortTaggedDataRecordPropertyHandler handler : ShortTaggedDataRecordPropertyHandler.values()){
            handler.handle(map, traceData, props);
        }
        
        return props;
    }

    protected static Map<String,String> addStringComments(
            GroupedTaggedRecords groupedDataRecordMap,byte[] traceData, Map<String,String> props) {
        Map<TaggedDataName, List<PascalStringTaggedDataRecord>> pascalStrings= groupedDataRecordMap.pascalStringDataRecords;
		//asciiStrings
        Map<TaggedDataName, List<AsciiTaggedDataRecord>> asciiStrings= groupedDataRecordMap.asciiDataRecords;
        
        
        if(pascalStrings.containsKey(TaggedDataName.COMMENT)){
		    props.put("COMM", pascalStrings.get(TaggedDataName.COMMENT).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData).trim());
		}
		if(pascalStrings.containsKey(TaggedDataName.SAMPLE_NAME)){
            props.put("NAME", pascalStrings.get(TaggedDataName.SAMPLE_NAME).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData).trim());
        }
		if(pascalStrings.containsKey(TaggedDataName.DYE_PRIMER_CORRECTION_FILE)){
            props.put("DYEP", pascalStrings.get(TaggedDataName.DYE_PRIMER_CORRECTION_FILE).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData).trim());
        }
		if(pascalStrings.containsKey(TaggedDataName.MACHINE_NAME)){
            props.put("MCHN", pascalStrings.get(TaggedDataName.MACHINE_NAME).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData).trim());
        }
		if(asciiStrings.containsKey(TaggedDataName.MODEL)){
            props.put("MODL", asciiStrings.get(TaggedDataName.MODEL).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData).trim());
        }
		if(pascalStrings.containsKey(TaggedDataName.MODF)){
            props.put("MODF", pascalStrings.get(TaggedDataName.MODF).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData).trim());
        }
		if(pascalStrings.containsKey(TaggedDataName.MATRIX_FILE_NAME)){
            props.put("MTFX", pascalStrings.get(TaggedDataName.MATRIX_FILE_NAME).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData).trim());
        }
		if(pascalStrings.containsKey(TaggedDataName.SPACING)){
            props.put("BCAL", pascalStrings.get(TaggedDataName.SPACING).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData).trim());
        }
		if(pascalStrings.containsKey(TaggedDataName.SMLt)){
            props.put("SMLt", pascalStrings.get(TaggedDataName.SMLt).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData).trim());
        }
		if(pascalStrings.containsKey(TaggedDataName.SMED)){
            props.put("SMED", pascalStrings.get(TaggedDataName.SMED).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData).trim());
        }
		if(pascalStrings.containsKey(TaggedDataName.SOFTWARE_VERSION)){
            final List<PascalStringTaggedDataRecord> versions = pascalStrings.get(TaggedDataName.SOFTWARE_VERSION);
            //match IO_Lib and only get the first 2 software version records...
            for(int i=0; i<versions.size() && i<2;i++){
                props.put("VER"+(i+1), versions.get(i).parseDataRecordFrom(traceData).trim());
             }
        }
		if(pascalStrings.containsKey(TaggedDataName.JTC_PROTOCOL_NAME)){
            props.put("PRON", pascalStrings.get(TaggedDataName.JTC_PROTOCOL_NAME).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData).trim());
        }
		
		
		if(pascalStrings.containsKey(TaggedDataName.JTC_TUBE)){
            props.put("TUBE", pascalStrings.get(TaggedDataName.JTC_TUBE).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData).trim());
        }
		if(asciiStrings.containsKey(TaggedDataName.JTC_RUN_NAME)){
            props.put("RUNN", asciiStrings.get(TaggedDataName.JTC_RUN_NAME).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData).trim());
        }
		if(asciiStrings.containsKey(TaggedDataName.JTC_PROTOCOL_VERSION)){
            props.put("PROV", asciiStrings.get(TaggedDataName.JTC_PROTOCOL_VERSION).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData).trim());
        }
		return props;
    }

	
	private static List<NucleotideSequence> parseBasecallsFrom(
			GroupedTaggedRecords groupedDataRecordMap, byte[] ab1DataBlock,
			ChromatogramFileVisitor visitor) {
		List<NucleotideSequence> basecallsList = new ArrayList<NucleotideSequence>(2);
		for(AsciiTaggedDataRecord basecallRecord : groupedDataRecordMap.asciiDataRecords.get(TaggedDataName.BASECALLS)){
			NucleotideSequence basecalls = new NucleotideSequenceBuilder( basecallRecord.parseDataRecordFrom(ab1DataBlock))
											.build();
			basecallsList.add(basecalls);
			if(basecallRecord.getTagNumber()==CURRENT_VERSION){
				visitor.visitBasecalls(basecalls);
			}else if(visitor instanceof AbiChromatogramFileVisitor){
				((AbiChromatogramFileVisitor) visitor).visitOriginalBasecalls(basecalls);
				
			}
		}
		
		return basecallsList;
		
	}

	private static List<Nucleotide> parseChannelOrder(GroupedTaggedRecords dataRecordMap ){
		AsciiTaggedDataRecord order = dataRecordMap.asciiDataRecords.get(TaggedDataName.FILTER_WHEEL_ORDER).get(0);
		
		return asList(new NucleotideSequenceBuilder(order.parseDataRecordFrom(null)));

	}
	private static List<Nucleotide> asList(NucleotideSequenceBuilder builder){
    	List<Nucleotide> list = new ArrayList<Nucleotide>((int)builder.getLength());
    	for(Nucleotide n : builder){
    		list.add(n);
    	}
    	return list;
    }
	private static GroupedTaggedRecords parseTaggedDataRecords(
			InputStream in,
			long numberOfTaggedRecords,
			byte[] abiDataBlock,
			ChromatogramFileVisitor visitor) throws TraceDecoderException {
		GroupedTaggedRecords map = new GroupedTaggedRecords();
		boolean isAb1ChromatogramVisitor = visitor instanceof AbiChromatogramFileVisitor;
		try{
			for(long i=0; i<numberOfTaggedRecords; i++){
				String rawTagName = new String(IOUtil.toByteArray(in, 4),"UTF-8");
				
				TaggedDataName tagName = TaggedDataName.parseTaggedDataName(rawTagName);
				
				long tagNumber = IOUtil.readUnsignedInt(in);
				TaggedDataRecordBuilder builder = 
					new TaggedDataRecordBuilder(tagName, tagNumber)
						.setDataType(
							TaggedDataType.parseTaggedDataName(IOUtil.readUnsignedShort(in)), 
							IOUtil.readUnsignedShort(in))
						.setNumberOfElements(IOUtil.readUnsignedInt(in))
						.setRecordLength(IOUtil.readUnsignedInt(in))
						.setDataRecord(IOUtil.readUnsignedInt(in))
						.setCrypticValue(IOUtil.readUnsignedInt(in));
				TaggedDataRecord<?,?> record = builder.build();
				if(isAb1ChromatogramVisitor){
				    visitCorrectTaggedDataRecordViaReflection((AbiChromatogramFileVisitor) visitor,record, abiDataBlock);
				}
				map.add(record);
			}
		}catch(IOException e){
			throw new TraceDecoderException("could parse not tagged data record", e);
		}
		return map;
	}

	private static void visitCorrectTaggedDataRecordViaReflection(AbiChromatogramFileVisitor visitor, TaggedDataRecord record, byte[] abiDataBlock){
	    try {
            Method method =visitor.getClass().getMethod("visitTaggedDataRecord", record.getType(),record.getParsedDataType());
            
            method.invoke(visitor, record, record.parseDataRecordFrom(abiDataBlock));
	    } catch (Exception e) {
            throw new IllegalArgumentException("could not visit tagged data record "+ record,e);
        }
	}
	private static byte[] parseTraceDataBlock(InputStream in, int lengthOfDataBlock) throws TraceDecoderException{
		
		try {
			return IOUtil.toByteArray(in, lengthOfDataBlock);
		} catch (IOException e) {
			throw new TraceDecoderException("could not parse trace data block", e);
		}
	}
	private static int parseTaggedRecordOffset(InputStream in) throws TraceDecoderException {
		try{
			IOUtil.blockingSkip(in, 4);
			return (int)IOUtil.readUnsignedInt(in);
			}catch(IOException e){
				throw new TraceDecoderException("could not parse number of tagged records", e);
			}
	}

	private static long parseNumTaggedRecords(InputStream in) throws TraceDecoderException{
		try{
		IOUtil.blockingSkip(in, 14);
		return IOUtil.readUnsignedInt(in);
		}catch(IOException e){
			throw new TraceDecoderException("could not parse number of tagged records", e);
		}
	}

	private static void verifyMagicNumber(InputStream in) throws TraceDecoderException {
		try {
			byte[] magicNumber = IOUtil.toByteArray(in, 4);
			if(!AbiUtil.isABIMagicNumber(magicNumber)){
				throw new TraceDecoderException("magic number does not match AB1 format "+ Arrays.toString(magicNumber));
			}
		} catch (IOException e) {
			throw new TraceDecoderException("could not read magic number", e);
		}
		
		
	}
	/**
	 * {@code GroupedTaggedRecords} groups all the different types
	 * of {@link TaggedDataRecord}s by class and provides mappings
	 * for each type by TaggedDataName.  This simplifies searching for data
	 * and allows the same taggedDataName to return differnt TaggedDataRecord
	 * types.
	 * @author dkatzel
	 *
	 *
	 */
	private static class GroupedTaggedRecords{
		private final Map<TaggedDataName,List<AsciiTaggedDataRecord>> asciiDataRecords = new EnumMap<TaggedDataName, List<AsciiTaggedDataRecord>>(TaggedDataName.class);
	
		private final Map<TaggedDataName,List<FloatArrayTaggedDataRecord>> floatDataRecords = new EnumMap<TaggedDataName, List<FloatArrayTaggedDataRecord>>(TaggedDataName.class);
		private final Map<TaggedDataName,List<ByteArrayTaggedDataRecord>> byteArrayRecords = new EnumMap<TaggedDataName, List<ByteArrayTaggedDataRecord>>(TaggedDataName.class);
	    
		private final Map<TaggedDataName,List<ShortArrayTaggedDataRecord>> shortArrayDataRecords = new EnumMap<TaggedDataName, List<ShortArrayTaggedDataRecord>>(TaggedDataName.class);
		
		private final Map<TaggedDataName,List<IntArrayTaggedDataRecord>> intArrayDataRecords = new EnumMap<TaggedDataName, List<IntArrayTaggedDataRecord>>(TaggedDataName.class);
		
		private final Map<TaggedDataName,List<PascalStringTaggedDataRecord>> pascalStringDataRecords = new EnumMap<TaggedDataName, List<PascalStringTaggedDataRecord>>(TaggedDataName.class);

		private final Map<TaggedDataName,List<DateTaggedDataRecord>> dateDataRecords = new EnumMap<TaggedDataName, List<DateTaggedDataRecord>>(TaggedDataName.class);
		
		private final Map<TaggedDataName,List<TimeTaggedDataRecord>> timeDataRecords = new EnumMap<TaggedDataName, List<TimeTaggedDataRecord>>(TaggedDataName.class);
		private final Map<TaggedDataName,List<UserDefinedTaggedDataRecord>> userDefinedDataRecords = new EnumMap<TaggedDataName, List<UserDefinedTaggedDataRecord>>(TaggedDataName.class);
        
		public void add(TaggedDataRecord<?,?> record){
			switch(record.getDataType()){
							
			case DATE:
				add(record, dateDataRecords);
				break;
			case FLOAT:
				add(record, floatDataRecords);
				break;
			case INTEGER:
				if(record instanceof DefaultShortArrayTaggedDataRecord){
					add(record, shortArrayDataRecords);
				}else{
					add(record, intArrayDataRecords);
				}
				break;
			case PASCAL_STRING:
				add(record, pascalStringDataRecords);
				break;
			case TIME:
				add(record, timeDataRecords);
				break;
			case USER_DEFINED:
			    add(record, userDefinedDataRecords);
			    break;
			default:
			    if(record instanceof StringTaggedDataRecord){
			        add(record, asciiDataRecords);
			    }else{
			        add(record, byteArrayRecords);
			    }
				break;
			}
		}
		
		@SuppressWarnings("unchecked")
		private <T extends TaggedDataRecord<?,?>> void add(TaggedDataRecord<?,?> record, Map<TaggedDataName,List<T>> map){
			TaggedDataName name = record.getTagName();
			if(!map.containsKey(name)){
				map.put(name, new ArrayList<T>());
			}
			map.get(name).add((T)record);
		}
	}
	/**
	 * {@code Noise} contains the noise factor for
	 * each channel.
	 * @author dkatzel
	 * @see SignalScalingFactor
	 *
	 */
	private static class Noise{
	    private float aNoise,cNoise,gNoise,tNoise;
	    
	    static Noise create(List<Nucleotide> channelOrder, float[] noise){
	        Noise n = new Noise();
            int i=0;
            for(Nucleotide channel:channelOrder){
                switch(channel){
                    case Adenine:   n.aNoise= noise[i];
                                    break;
                    case Cytosine:   n.cNoise= noise[i];
                                    break;
                    case Guanine:   n.gNoise= noise[i];
                                    break;
                    default:        n.tNoise= noise[i];
                                    break;
                }
                i++;
            }
            return n;
	    }
	    
	    @Override
	    public String toString(){
	        return String.format("A:%f,C:%f,G:%f,T:%f", aNoise,cNoise,gNoise,tNoise);
	    }
         
	}
	/**
	 * {@code SignalScalingFactor} contains the scaling factor
	 * for each of the 4 channels.  This metric can be useful
	 * to determine if there is too much or too little DNA
	 * being sequenced.
	 * @author dkatzel
	 *
	 *
	 */
	private static class SignalScalingFactor{
	    
	    private short aScale=-1,cScale=-1,gScale=-1,tScale =-1;
	    
	    static SignalScalingFactor create(List<Nucleotide> channelOrder, List<Short> scalingFactors){
	        SignalScalingFactor sf= new SignalScalingFactor();	         
    	    Iterator<Short> scaleIterator = scalingFactors.iterator();
    	    for(Nucleotide channel : channelOrder){
    	        short scale = scaleIterator.next();
    	        switch(channel){
    	            case Adenine:
    	                sf.aScale = scale;
    	                break;
    	            case Cytosine:
    	                sf.cScale = scale;
    	                break;
    	            case Guanine:
    	                sf.gScale = scale;
    	                break;
    	            default:
    	                sf.tScale = scale;
    	                break;
    	        }
    	    }
    	    return sf;
	}
	    
	    @Override
	    public String toString(){
	        return String.format("A:%d,C:%d,G:%d,T:%d", 
                    aScale,cScale,gScale,tScale);
	    }
	}
	/**
	 * {@code ShortTaggedDataRecordPropertyHandler} sets
	 * the appropriate chromatogram properites (comments)
	 * based on the Ab1 ShortTaggedDataRecord encountered.
	 * @author dkatzel
	 *
	 *
	 */
	private enum ShortTaggedDataRecordPropertyHandler{
	    
	    LANE(TaggedDataName.LANE,"LANE"),
	    LASER_POWER(TaggedDataName.LASER_POWER,"LsrP"),
	    B1Pt(TaggedDataName.B1Pt,"B1Pt"),
	    Scan(TaggedDataName.Scan,"Scan"),
	    LENGTH_OF_DETECTOR(TaggedDataName.LENGTH_OF_DETECTOR,"LNTD"),
	    JTC_START_POINT(TaggedDataName.JTC_START_POINT,"ASPT"),
	    JTC_END_POINT(TaggedDataName.JTC_END_POINT,"AEPT"),
	    ;
	    private final TaggedDataName dataName;
	    private final String propertyKey;

        private ShortTaggedDataRecordPropertyHandler(TaggedDataName dataName,
                String propertyKey) {
            this.dataName = dataName;
            this.propertyKey = propertyKey;
        }


        /**
         * If the given map of TaggedData contains the appropriate record,
         * then generate a key, value comment and add it to the given property map.
         * @param map the map of TaggedDataName to {@link ShortArrayTaggedDataRecord}s.
         * @param traceData the ab1 trace data which may need to be parsed to generate the comment.
         * @param props the key value pair map of comments which is to be modified.
         */
        void handle(Map<TaggedDataName, List<ShortArrayTaggedDataRecord>> map,byte[] traceData, Map<String,String> props){
	        if(map.containsKey(dataName)){
	           props.put(propertyKey, ""+map.get(dataName).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData)[0]);
	        }
	    }
	}
	
	
}
