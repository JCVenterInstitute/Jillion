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
import java.util.EnumMap;
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
import org.jcvi.trace.sanger.chromatogram.abi.tag.ByteArrayTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DefaultAsciiTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DefaultDateTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DefaultFloatTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DefaultIntegerArrayTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DefaultPascalStringTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DefaultShortArrayTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.DefaultTimeTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.StringTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.TaggedDataName;
import org.jcvi.trace.sanger.chromatogram.abi.tag.TaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.abi.tag.TaggedDataRecordBuilder;
import org.jcvi.trace.sanger.chromatogram.abi.tag.TaggedDataType;

public final class Ab1FileParser {

	private Ab1FileParser() {
	}
	
	private static final byte[] MAGIC_NUMBER = new byte[]{(char)'A',(char)'B',(char)'I',(char)'F'};
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
			parseSignalScalingFactor(groupedDataRecordMap, channelOrder, traceData,visitor);
			parseCommentsFrom(groupedDataRecordMap,traceData,visitor);
			parseDataChannels(groupedDataRecordMap,channelOrder,traceData,visitor);
			parsePeakData(groupedDataRecordMap,traceData,visitor);
			parseQualityData(groupedDataRecordMap,traceData,basecalls,visitor);
			visitor.visitEndOfFile();
	}

	private static void parseSignalScalingFactor(
			GroupedTaggedRecords groupedDataRecordMap,
			List<NucleotideGlyph> channelOrder, byte[] traceData,
			ChromatogramFileVisitor visitor) {
		
		if(visitor instanceof AbiChromatogramFileVisitor){
			DefaultShortArrayTaggedDataRecord scalingFactors =groupedDataRecordMap.shortArrayDataRecords.get(TaggedDataName.SCALE_FACTOR).get(0);
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
			
			((AbiChromatogramFileVisitor) visitor).visitScaleFactors(aScale,cScale,gScale,tScale);
		}
		
	}

	private static void parseQualityData(
			GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
			List<String> basecallsList,
			ChromatogramFileVisitor visitor) {
		
		List<ByteArrayTaggedDataRecord> qualityRecords =groupedDataRecordMap.byteArrayRecords.get(TaggedDataName.QUALITY);
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
		List<DefaultShortArrayTaggedDataRecord> peakRecords =groupedDataRecordMap.shortArrayDataRecords.get(TaggedDataName.PEAK_LOCATIONS);
		
		if(visitor instanceof AbiChromatogramFileVisitor){
			short[] originalPeakData =peakRecords.get(ORIGINAL_VERSION).parseDataRecordFrom(traceData);
			
			((AbiChromatogramFileVisitor) visitor).visitOriginalPeaks(originalPeakData);
		}
		short[] peakData =peakRecords.get(CURRENT_VERSION).parseDataRecordFrom(traceData);
		visitor.visitPeaks(peakData);
	}

	private static void parseDataChannels(
			GroupedTaggedRecords groupedDataRecordMap,
			List<NucleotideGlyph> channelOrder,
			byte[] traceData,
			ChromatogramFileVisitor visitor) {
		List<DefaultShortArrayTaggedDataRecord> dataRecords =groupedDataRecordMap.shortArrayDataRecords.get(TaggedDataName.DATA);
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
		for(int i=0; i<4; i++){
			NucleotideGlyph channel = channelOrder.get(i);
			short[] channelData =dataRecords.get(i+8).parseDataRecordFrom(traceData);
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
	}

	private static void visitChannelOrderIfAble(
			ChromatogramFileVisitor visitor, List<NucleotideGlyph> channelOrder) {
		if(visitor instanceof AbiChromatogramFileVisitor){
			((AbiChromatogramFileVisitor) visitor).visitChannelOrder(channelOrder);
		}
	}
	
	private static void parseCommentsFrom(
			GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
			ChromatogramFileVisitor visitor) {
		Properties props = new Properties();
		for(Entry<TaggedDataName, List<DefaultPascalStringTaggedDataRecord>> entry :groupedDataRecordMap.pascalStringDataRecords.entrySet()){
			props = addAsComments(entry.getValue(),traceData,props);
		}
		
		for(Entry<TaggedDataName, List<DefaultDateTaggedDataRecord>> entry :groupedDataRecordMap.dateDataRecords.entrySet()){
			props = addAsComments(entry.getValue(),traceData,props);						
		}
		for(Entry<TaggedDataName, List<DefaultTimeTaggedDataRecord>> entry :groupedDataRecordMap.timeDataRecords.entrySet()){
			props = addAsComments(entry.getValue(),traceData,props);						
		}
		visitor.visitComments(props);
	}

	private static Properties addAsComments(List<? extends TaggedDataRecord> records,byte[] traceData, Properties comments){
		boolean shouldNumber = records.size()>1;
		for(TaggedDataRecord  record : records){
			//if more than one record with that
			//name, then append the tag number to it 
			//to make it unique
			final String key;
			if(shouldNumber){
				key= String.format("%s_%d",record.getTagName(),record.getTagNumber());
			}else{
				key = record.getTagName().toString();
			}
			
			comments.put(key, record.parseDataRecordFrom(traceData));
			
		}
		return comments;
	}
	private static List<String> parseBasecallsFrom(
			GroupedTaggedRecords groupedDataRecordMap, byte[] ab1DataBlock,
			ChromatogramFileVisitor visitor) {
		List<String> basecallsList = new ArrayList<String>(2);
		for(DefaultAsciiTaggedDataRecord basecallRecord : groupedDataRecordMap.asciiDataRecords.get(TaggedDataName.BASECALLS)){
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
		DefaultAsciiTaggedDataRecord order = dataRecordMap.asciiDataRecords.get(TaggedDataName.FILTER_WHEEL_ORDER).get(0);
		
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
		private final Map<TaggedDataName,List<DefaultAsciiTaggedDataRecord>> asciiDataRecords = new EnumMap<TaggedDataName, List<DefaultAsciiTaggedDataRecord>>(TaggedDataName.class);
	
		private final Map<TaggedDataName,List<DefaultFloatTaggedDataRecord>> floatDataRecords = new EnumMap<TaggedDataName, List<DefaultFloatTaggedDataRecord>>(TaggedDataName.class);
		private final Map<TaggedDataName,List<ByteArrayTaggedDataRecord>> byteArrayRecords = new EnumMap<TaggedDataName, List<ByteArrayTaggedDataRecord>>(TaggedDataName.class);
	    
		private final Map<TaggedDataName,List<DefaultShortArrayTaggedDataRecord>> shortArrayDataRecords = new EnumMap<TaggedDataName, List<DefaultShortArrayTaggedDataRecord>>(TaggedDataName.class);
		
		private final Map<TaggedDataName,List<DefaultIntegerArrayTaggedDataRecord>> intArrayDataRecords = new EnumMap<TaggedDataName, List<DefaultIntegerArrayTaggedDataRecord>>(TaggedDataName.class);
		
		private final Map<TaggedDataName,List<DefaultPascalStringTaggedDataRecord>> pascalStringDataRecords = new EnumMap<TaggedDataName, List<DefaultPascalStringTaggedDataRecord>>(TaggedDataName.class);

		private final Map<TaggedDataName,List<DefaultDateTaggedDataRecord>> dateDataRecords = new EnumMap<TaggedDataName, List<DefaultDateTaggedDataRecord>>(TaggedDataName.class);
		
		private final Map<TaggedDataName,List<DefaultTimeTaggedDataRecord>> timeDataRecords = new EnumMap<TaggedDataName, List<DefaultTimeTaggedDataRecord>>(TaggedDataName.class);
		
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
