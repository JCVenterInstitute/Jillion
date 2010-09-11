package org.jcvi.trace.sanger.chromatogram.ab1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.NucleotideGlyphFactory;
import org.jcvi.io.IOUtil;
import org.jcvi.io.IOUtil.ENDIAN;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ChromatogramFileVisitor;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.ASCIITaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.AbstractTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.DateTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.DefaultTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.FloatTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.IntegerArrayTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.PascalStringTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.ShortArrayTaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.TaggedDataName;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.TaggedDataRecord;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.TaggedDataRecordBuilder;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.TaggedDataType;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.TimeTaggedDataRecord;
import org.joda.time.LocalTime;

public final class Ab1FileParser {

	private Ab1FileParser() {
	}
	
	private static final byte[] MAGIC_NUMBER = new byte[]{(char)'A',(char)'B',(char)'I',(char)'F'};
	private static final int ORIGINAL_VERSION = 0;
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
			byte[] traceData = parseTraceDataBlock(in, datablockOffset-Ab1Util.HEADER_SIZE);
			GroupedTaggedRecords groupedDataRecordMap = parseTaggedDataRecords(in,numberOfTaggedRecords,visitor);
	
			List<NucleotideGlyph> channelOrder =parseChannelOrder(groupedDataRecordMap);
			visitChannelOrderIfAble(visitor, channelOrder);			
			List<String> basecalls =parseBasecallsFrom(groupedDataRecordMap,traceData,visitor);	
			parseCommentsFrom(groupedDataRecordMap,traceData,visitor);
			parseDataChannels(groupedDataRecordMap,channelOrder,traceData,visitor);
			parsePeakData(groupedDataRecordMap,traceData,visitor);
			parseQualityData(groupedDataRecordMap,traceData,basecalls,visitor);
			visitor.visitEndOfFile();
	}

	private static void parseQualityData(
			GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
			List<String> basecallsList,
			ChromatogramFileVisitor visitor) {
		
		List<ASCIITaggedDataRecord> qualityRecords =groupedDataRecordMap.asciiDataRecords.get(TaggedDataName.QUALITY);
		for(int i=0; i<qualityRecords.size(); i++){
			
		
			ASCIITaggedDataRecord qualityRecord = qualityRecords.get(i);
			List<NucleotideGlyph> basecalls = NucleotideGlyph.getGlyphsFor(basecallsList.get(i));
			byte[][] qualities = parseQualityData(basecalls, qualityRecord.parseDataRecordFrom(traceData).getBytes());
			if(i == ORIGINAL_VERSION && visitor instanceof Ab1ChromatogramFileVisitor){
				Ab1ChromatogramFileVisitor ab1Visitor = (Ab1ChromatogramFileVisitor)visitor;
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
		
		System.out.println("# qual records = " + qualityRecords.size());
		String qualData =qualityRecords.get(CURRENT_VERSION).parseDataRecordFrom(traceData);
		System.out.println("QUALITY = " + Arrays.toString(qualData.getBytes()));
		System.out.println(qualData.length());
		//visitor.visit;
	}

	private static byte[][] parseQualityData(List<NucleotideGlyph> basecalls,byte[] qualities ){
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
			case Thymine:
				aQualities.put(zero);
				cQualities.put(zero);
				gQualities.put(zero);
				tQualities.put(quality);				
				break;
			default:
				throw new IllegalStateException("invalid basecall "+basecalls.get(i));
			}
		}
		
		return new byte[][]{aQualities.array(),cQualities.array(),gQualities.array(),tQualities.array()};
	}
	private static void parsePeakData(
			GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
			ChromatogramFileVisitor visitor) {
		List<ShortArrayTaggedDataRecord> peakRecords =groupedDataRecordMap.shortArrayDataRecords.get(TaggedDataName.PEAK_LOCATIONS);
		
		if(visitor instanceof Ab1ChromatogramFileVisitor){
			short[] originalPeakData =peakRecords.get(ORIGINAL_VERSION).parseDataRecordFrom(traceData);
			
			((Ab1ChromatogramFileVisitor) visitor).visitOriginalPeaks(originalPeakData);
		}
		short[] peakData =peakRecords.get(CURRENT_VERSION).parseDataRecordFrom(traceData);
		visitor.visitPeaks(peakData);
	}

	private static void parseDataChannels(
			GroupedTaggedRecords groupedDataRecordMap,
			List<NucleotideGlyph> channelOrder,
			byte[] traceData,
			ChromatogramFileVisitor visitor) {
		List<ShortArrayTaggedDataRecord> dataRecords =groupedDataRecordMap.shortArrayDataRecords.get(TaggedDataName.DATA);
		if(visitor instanceof Ab1ChromatogramFileVisitor){
			Ab1ChromatogramFileVisitor ab1Visitor = (Ab1ChromatogramFileVisitor) visitor;
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
		if(visitor instanceof Ab1ChromatogramFileVisitor){
			((Ab1ChromatogramFileVisitor) visitor).visitChannelOrder(channelOrder);
		}
	}
	
	private static void parseCommentsFrom(
			GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
			ChromatogramFileVisitor visitor) {
		Properties props = new Properties();
		for(Entry<TaggedDataName, List<PascalStringTaggedDataRecord>> entry :groupedDataRecordMap.pascalStringDataRecords.entrySet()){
			props = addAsComments(entry.getValue(),traceData,props);
		}
		
		for(Entry<TaggedDataName, List<DateTaggedDataRecord>> entry :groupedDataRecordMap.dateDataRecords.entrySet()){
			props = addAsComments(entry.getValue(),traceData,props);						
		}
		for(Entry<TaggedDataName, List<TimeTaggedDataRecord>> entry :groupedDataRecordMap.timeDataRecords.entrySet()){
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
		for(ASCIITaggedDataRecord basecallRecord : groupedDataRecordMap.asciiDataRecords.get(TaggedDataName.BASECALLS)){
			String basecalls = basecallRecord.parseDataRecordFrom(ab1DataBlock);
			basecallsList.add(basecalls);
			if(basecallRecord.getTagNumber()==CURRENT_VERSION){
				visitor.visitBasecalls(basecalls);
			}else if(visitor instanceof Ab1ChromatogramFileVisitor){
				((Ab1ChromatogramFileVisitor) visitor).visitOriginalBasecalls(basecalls);
				
			}
		}
		
		return basecallsList;
		
	}

	private static List<NucleotideGlyph> parseChannelOrder(GroupedTaggedRecords dataRecordMap ){
		ASCIITaggedDataRecord order = dataRecordMap.asciiDataRecords.get(TaggedDataName.FILTER_WHEEL_ORDER).get(0);
		
		return NucleotideGlyphFactory.getInstance().getGlyphsFor(order.parseDataRecordFrom(null));

	}

	private static GroupedTaggedRecords parseTaggedDataRecords(
			InputStream in,
			long numberOfTaggedRecords,
			ChromatogramFileVisitor visitor) throws TraceDecoderException {
		GroupedTaggedRecords map = new GroupedTaggedRecords();
		boolean isAb1ChromatogramVisitor = visitor instanceof Ab1ChromatogramFileVisitor;
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
					((Ab1ChromatogramFileVisitor) visitor).visitTaggedDataRecord(record);
				}
				
				map.add(record);
			}
		}catch(IOException e){
			throw new TraceDecoderException("could parse not tagged data record", e);
		}
		return map;
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
		private final Map<TaggedDataName,List<ASCIITaggedDataRecord>> asciiDataRecords = new EnumMap<TaggedDataName, List<ASCIITaggedDataRecord>>(TaggedDataName.class);
	
		private final Map<TaggedDataName,List<FloatTaggedDataRecord>> floatDataRecords = new EnumMap<TaggedDataName, List<FloatTaggedDataRecord>>(TaggedDataName.class);
		
		private final Map<TaggedDataName,List<ShortArrayTaggedDataRecord>> shortArrayDataRecords = new EnumMap<TaggedDataName, List<ShortArrayTaggedDataRecord>>(TaggedDataName.class);
		
		private final Map<TaggedDataName,List<IntegerArrayTaggedDataRecord>> intArrayDataRecords = new EnumMap<TaggedDataName, List<IntegerArrayTaggedDataRecord>>(TaggedDataName.class);
		
		private final Map<TaggedDataName,List<PascalStringTaggedDataRecord>> pascalStringDataRecords = new EnumMap<TaggedDataName, List<PascalStringTaggedDataRecord>>(TaggedDataName.class);
		
		private final Map<TaggedDataName,List<DefaultTaggedDataRecord>> defaultDataRecords = new EnumMap<TaggedDataName, List<DefaultTaggedDataRecord>>(TaggedDataName.class);
		
		private final Map<TaggedDataName,List<DateTaggedDataRecord>> dateDataRecords = new EnumMap<TaggedDataName, List<DateTaggedDataRecord>>(TaggedDataName.class);
		
		private final Map<TaggedDataName,List<TimeTaggedDataRecord>> timeDataRecords = new EnumMap<TaggedDataName, List<TimeTaggedDataRecord>>(TaggedDataName.class);
		
		public void add(TaggedDataRecord record){
			switch(record.getDataType()){
			case CHAR:
				add(record, asciiDataRecords);
				break;				
			case DATE:
				add(record, dateDataRecords);
				break;
			case FLOAT:
				add(record, floatDataRecords);
				break;
			case INTEGER:
				if(record instanceof ShortArrayTaggedDataRecord){
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
				add(record, defaultDataRecords);
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
