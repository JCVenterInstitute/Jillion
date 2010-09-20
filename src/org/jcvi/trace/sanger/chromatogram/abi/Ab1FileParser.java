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
package org.jcvi.trace.sanger.chromatogram.abi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.NucleotideGlyphFactory;
import org.jcvi.io.IOUtil;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ChromatogramFileVisitor;
import org.jcvi.trace.sanger.chromatogram.abi.tag.AsciiTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.ByteArrayTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DateTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DefaultAsciiTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DefaultDateTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DefaultFloatTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DefaultIntegerArrayTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DefaultPascalStringTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DefaultShortArrayTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DefaultTimeTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.FloatArrayTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.IntArrayTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.PascalStringTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.ShortArrayTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.StringTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.TaggedDataName;
import org.jcvi.trace.sanger.chromatogram.abi.tag.TaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.TaggedDataRecordBuilder;
import org.jcvi.trace.sanger.chromatogram.abi.tag.TaggedDataType;
import org.jcvi.trace.sanger.chromatogram.abi.tag.TimeTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.UserDefinedTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.rate.ScanRateTaggedDataType;
import org.jcvi.trace.sanger.chromatogram.abi.tag.rate.ScanRateUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class Ab1FileParser {

	private Ab1FileParser() {
	}
	
	private static final byte[] MAGIC_NUMBER = new byte[]{(char)'A',(char)'B',(char)'I',(char)'F'};
	
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("EEE dd MMM HH:mm:ss YYYY");
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
	
	
	public static void parseAb1File(File ab1File, ChromatogramFileVisitor visitor) throws FileNotFoundException, TraceDecoderException{
		InputStream in = null;
		try{
			in = new FileInputStream(ab1File);
			parseAb1File(in, visitor);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	
	public static void parseAb1File(InputStream in, ChromatogramFileVisitor visitor) throws TraceDecoderException{
		
			verifyMagicNumber(in);
			visitor.visitFile();
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
	
			List<NucleotideGlyph> channelOrder =parseChannelOrder(groupedDataRecordMap);
			visitChannelOrderIfAble(visitor, channelOrder);			
			List<String> basecalls =parseBasecallsFrom(groupedDataRecordMap,traceData,visitor);	
			String signalScale =parseSignalScalingFactor(groupedDataRecordMap, channelOrder, traceData,visitor);
			Map<String,String> comments =parseDataChannels(groupedDataRecordMap,channelOrder,traceData,visitor);
			parsePeakData(groupedDataRecordMap,traceData,visitor);
			parseQualityData(groupedDataRecordMap,traceData,basecalls,visitor);
			parseCommentsFrom(comments,groupedDataRecordMap,channelOrder,traceData,signalScale,basecalls,visitor);
            
			visitor.visitEndOfFile();
	}

	private static String parseSignalScalingFactor(
			GroupedTaggedRecords groupedDataRecordMap,
			List<NucleotideGlyph> channelOrder, byte[] traceData,
			ChromatogramFileVisitor visitor) {
		
		
			ShortArrayTaggedDataRecord scalingFactors =groupedDataRecordMap.shortArrayDataRecords.get(TaggedDataName.SCALE_FACTOR).get(0);
			short aScale=-1,cScale=-1,gScale=-1,tScale =-1;
			List<Short> list = new ArrayList<Short>();
			for(short s: scalingFactors.parseDataRecordFrom(traceData)){
				list.add(Short.valueOf(s));
			}
			Iterator<Short> scaleIterator = list.iterator();
			for(NucleotideGlyph channel : channelOrder){
				short scale = scaleIterator.next();
				switch(channel){
					case Adenine:
						aScale = scale;
						break;
					case Cytosine:
						cScale = scale;
						break;
					case Guanine:
						gScale = scale;
						break;
					default:
						tScale = scale;
						break;
				}
			}
			if(visitor instanceof AbiChromatogramFileVisitor){
			    ((AbiChromatogramFileVisitor) visitor).visitScaleFactors(aScale,cScale,gScale,tScale);
			}
			return String.format("A:%d,C:%d,G:%d,T:%d", aScale,cScale,gScale,tScale);
		
	}

	private static void parseQualityData(
			GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
			List<String> basecallsList,
			ChromatogramFileVisitor visitor) {
		
		List<ByteArrayTaggedDataRecord> qualityRecords =groupedDataRecordMap.byteArrayRecords.get(TaggedDataName.JTC_QUALITY_VALUES);
		for(int i=0; i<qualityRecords.size(); i++){
		    ByteArrayTaggedDataRecord qualityRecord = qualityRecords.get(i);
			List<NucleotideGlyph> basecalls = NucleotideGlyph.getGlyphsFor(basecallsList.get(i));
			byte[][] qualities = splitQualityDataByChannel(basecalls, qualityRecord.parseDataRecordFrom(traceData));
			if(i == ORIGINAL_VERSION && visitor instanceof AbiChromatogramFileVisitor){
				AbiChromatogramFileVisitor ab1Visitor = (AbiChromatogramFileVisitor)visitor;
				ab1Visitor.visitOriginalAConfidence(qualities[0]);
				ab1Visitor.visitOriginalCConfidence(qualities[1]);
				ab1Visitor.visitOriginalGConfidence(qualities[2]);
				ab1Visitor.visitOriginalTConfidence(qualities[3]);
			}
			if(i == CURRENT_VERSION){
				visitor.visitAConfidence(qualities[0]);
				visitor.visitCConfidence(qualities[1]);
				visitor.visitGConfidence(qualities[2]);
				visitor.visitTConfidence(qualities[3]);
			}
		}
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
	private static byte[][] splitQualityDataByChannel(List<NucleotideGlyph> basecalls,byte[] qualities ){
		//The channel of the given basecall gets that
		// quality value, the other channels get zero
		int size = basecalls.size();
		ByteBuffer aQualities = ByteBuffer.allocate(size);
		ByteBuffer cQualities = ByteBuffer.allocate(size);
		ByteBuffer gQualities = ByteBuffer.allocate(size);
		ByteBuffer tQualities = ByteBuffer.allocate(size);
		byte zero = (byte)0;
		for(int i=0; i<qualities.length; i++){
			byte quality = qualities[i];
			switch(basecalls.get(i)){
			case Adenine:
				aQualities.put(quality);
				cQualities.put(zero);
				gQualities.put(zero);
				tQualities.put(zero);
				break;
				
			case Cytosine:
				aQualities.put(zero);
				cQualities.put(quality);
				gQualities.put(zero);
				tQualities.put(zero);
				break;
			case Guanine:
				aQualities.put(zero);
				cQualities.put(zero);
				gQualities.put(quality);
				tQualities.put(zero);
				break;
			//anything else is automatically a T
			default:
				aQualities.put(zero);
				cQualities.put(zero);
				gQualities.put(zero);
				tQualities.put(quality);				
				break;
			}
		}
		return new byte[][]{aQualities.array(),cQualities.array(),gQualities.array(),tQualities.array()};
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
			List<NucleotideGlyph> channelOrder,
			byte[] traceData,
			ChromatogramFileVisitor visitor) {
		List<ShortArrayTaggedDataRecord> dataRecords =groupedDataRecordMap.shortArrayDataRecords.get(TaggedDataName.DATA);
		if(visitor instanceof AbiChromatogramFileVisitor){
			AbiChromatogramFileVisitor ab1Visitor = (AbiChromatogramFileVisitor) visitor;
			//parse extra ab1 data
			for(int i=0; i< 4; i++){
				short[] rawTraceData =dataRecords.get(i).parseDataRecordFrom(traceData);
				ab1Visitor.visitPhotometricData(rawTraceData,i);
			}
			ab1Visitor.visitGelVoltageData(dataRecords.get(4).parseDataRecordFrom(traceData));
			ab1Visitor.visitGelCurrentData(dataRecords.get(5).parseDataRecordFrom(traceData));
			ab1Visitor.visitElectrophoreticPower(dataRecords.get(6).parseDataRecordFrom(traceData));
			ab1Visitor.visitGelTemperatureData(dataRecords.get(7).parseDataRecordFrom(traceData));
			
		}
		Map<String,String> props = new HashMap<String, String>();
		for(int i=0; i<4; i++){
			NucleotideGlyph channel = channelOrder.get(i);
			short[] channelData =dataRecords.get(i+8).parseDataRecordFrom(traceData);
			switch(channel){
				case Adenine:
					visitor.visitAPositions(channelData);
					props.put("NPTS", ""+channelData.length);
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
		return props;
	}

	private static void visitChannelOrderIfAble(
			ChromatogramFileVisitor visitor, List<NucleotideGlyph> channelOrder) {
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
			List<NucleotideGlyph> channelOrder,byte[] traceData,
			String signalScale, List<String> basecalls,
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
/*		
		for(Entry<TaggedDataName, List<PascalStringTaggedDataRecord>> entry :groupedDataRecordMap.pascalStringDataRecords.entrySet()){
			props = addAsComments(entry.getValue(),traceData,props);
		}
		
		for(Entry<TaggedDataName, List<DateTaggedDataRecord>> entry :groupedDataRecordMap.dateDataRecords.entrySet()){
			props = addAsComments(entry.getValue(),traceData,props);						
		}
		for(Entry<TaggedDataName, List<TimeTaggedDataRecord>> entry :groupedDataRecordMap.timeDataRecords.entrySet()){
			props = addAsComments(entry.getValue(),traceData,props);						
		}
		*/
		System.out.println(props);
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
            List<String> basecalls,
            Map<String,String> props) {
        props.put("NBAS", ""+basecalls.get(ORIGINAL_VERSION).length());
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
            List<NucleotideGlyph> channelOrder,
            byte[] traceData,
            Map<String,String> props) {
        Map<TaggedDataName, List<FloatArrayTaggedDataRecord>>map= groupedDataRecordMap.floatDataRecords;
        if(map.containsKey(TaggedDataName.JTC_NOISE)){
            float[] noise = map.get(TaggedDataName.JTC_NOISE).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData);
            float aNoise=0F,cNoise=0F,gNoise=0F,tNoise=0F;
            int i=0;
            for(NucleotideGlyph channel:channelOrder){
                switch(channel){
                    case Adenine:   aNoise= noise[i];
                                    break;
                    case Cytosine:   cNoise= noise[i];
                                    break;
                    case Guanine:   gNoise= noise[i];
                                    break;
                    default:        tNoise= noise[i];
                                    break;
                }
                i++;
            }
            props.put("NOIS",String.format("A:%f,C:%f,G:%f,T:%f", aNoise,cNoise,gNoise,tNoise)); 
        }
        return props;
    }

    /**
     * @param groupedDataRecordMap
     * @param traceData
     * @param props
     * @return
     */
    private static Map<String,String> addTimeStampComment(
            GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
            Map<String,String> props) {
        Map<TaggedDataName, List<DateTaggedDataRecord>> dates= groupedDataRecordMap.dateDataRecords;
        Map<TaggedDataName, List<TimeTaggedDataRecord>> times= groupedDataRecordMap.timeDataRecords;
        if(dates.containsKey(TaggedDataName.RUN_DATE) && times.containsKey(TaggedDataName.RUN_TIME)){
            LocalDate startDate =dates.get(TaggedDataName.RUN_DATE).get(0).parseDataRecordFrom(traceData);
            LocalDate endDate =dates.get(TaggedDataName.RUN_DATE).get(1).parseDataRecordFrom(traceData);
            
            LocalTime startTime = times.get(TaggedDataName.RUN_TIME).get(0).parseDataRecordFrom(traceData);
            LocalTime endTime = times.get(TaggedDataName.RUN_TIME).get(1).parseDataRecordFrom(traceData);
            final DateTime startDateTime = startDate.toDateTime(startTime);
            final DateTime endDateTime = endDate.toDateTime(endTime);
            props.put("DATE", String.format("%s to %s",
            		DATE_FORMATTER.print(startDateTime),
            		DATE_FORMATTER.print(endDateTime)
            		));
            System.out.println(new Period(startDateTime,endDateTime));
            props.put("RUND", String.format("%04d%02d%02d.%02d%02d%02d - %04d%02d%02d.%02d%02d%02d",
                    startDateTime.getYear(), startDateTime.getMonthOfYear(), startDateTime.getDayOfMonth(),
                    startDateTime.getHourOfDay(),startDateTime.getMinuteOfHour(), startDateTime.getSecondOfMinute(),
                    endDateTime.getYear(), endDateTime.getMonthOfYear(), endDateTime.getDayOfMonth(),
                    endDateTime.getHourOfDay(),endDateTime.getMinuteOfHour(), endDateTime.getSecondOfMinute()
                   
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
            List<NucleotideGlyph> channelOrder, Map<String,String> props) {
        StringBuilder order = new StringBuilder();
        for(NucleotideGlyph channel: channelOrder){
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
        if(map.containsKey(TaggedDataName.LANE)){
            props.put("LANE", ""+map.get(TaggedDataName.LANE).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData)[0]);
        }
        if(map.containsKey(TaggedDataName.LASER_POWER)){
            props.put("LsrP", ""+map.get(TaggedDataName.LASER_POWER).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData)[0]);
        }
        if(map.containsKey(TaggedDataName.B1Pt)){
            props.put("B1Pt", ""+map.get(TaggedDataName.B1Pt).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData)[0]);
        }
        if(map.containsKey(TaggedDataName.Scan)){
            props.put("Scan", ""+map.get(TaggedDataName.Scan).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData)[0]);
        }
        if(map.containsKey(TaggedDataName.LNTD)){
            props.put("LNTD",""+ map.get(TaggedDataName.LNTD).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData)[0]);
        }
        if(map.containsKey(TaggedDataName.JTC_START_POINT)){
            final short value = map.get(TaggedDataName.JTC_START_POINT).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData)[0];
            props.put("ASPT", ""+value);
        }
        if(map.containsKey(TaggedDataName.JTC_END_POINT)){
            final short value = map.get(TaggedDataName.JTC_END_POINT).get(ORIGINAL_VERSION).parseDataRecordFrom(traceData)[0];
            props.put("AEPT", ""+value);
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

	
	private static List<String> parseBasecallsFrom(
			GroupedTaggedRecords groupedDataRecordMap, byte[] ab1DataBlock,
			ChromatogramFileVisitor visitor) {
		List<String> basecallsList = new ArrayList<String>(2);
		for(AsciiTaggedDataRecord basecallRecord : groupedDataRecordMap.asciiDataRecords.get(TaggedDataName.BASECALLS)){
			String basecalls = basecallRecord.parseDataRecordFrom(ab1DataBlock);
			basecallsList.add(basecalls);
			if(basecallRecord.getTagNumber()==CURRENT_VERSION){
				visitor.visitBasecalls(basecalls);
			}else if(visitor instanceof AbiChromatogramFileVisitor){
				((AbiChromatogramFileVisitor) visitor).visitOriginalBasecalls(basecalls);
				
			}
		}
		
		return basecallsList;
		
	}

	private static List<NucleotideGlyph> parseChannelOrder(GroupedTaggedRecords dataRecordMap ){
		AsciiTaggedDataRecord order = dataRecordMap.asciiDataRecords.get(TaggedDataName.FILTER_WHEEL_ORDER).get(0);
		
		return NucleotideGlyphFactory.getInstance().getGlyphsFor(order.parseDataRecordFrom(null));

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
				String rawTagName = new String(IOUtil.readByteArray(in, 4),"UTF-8");
				TaggedDataName tagName = TaggedDataName.parseTaggedDataName(
						rawTagName);
				
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
				TaggedDataRecord record = builder.build();
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
			return IOUtil.readByteArray(in, lengthOfDataBlock);
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
			byte[] actual = IOUtil.readByteArray(in, 4);
			if(!Arrays.equals(MAGIC_NUMBER, actual)){
				throw new TraceDecoderException("magic number does not match AB1 format "+ Arrays.toString(actual));
			}
		} catch (IOException e) {
			throw new TraceDecoderException("could not read magic number", e);
		}
		
		
	}
	
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
        
		public void add(TaggedDataRecord record){
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
		
		private <T> void add(TaggedDataRecord record, Map<TaggedDataName,List<T>> map){
			TaggedDataName name = record.getTagName();
			if(!map.containsKey(name)){
				map.put(name, new ArrayList<T>());
			}
			map.get(name).add((T)record);
		}
	}
}
