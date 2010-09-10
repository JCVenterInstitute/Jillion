package org.jcvi.trace.sanger.chromatogram.ab1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

public final class Ab1FileParser {

	private Ab1FileParser() {
	}
	
	private static final byte[] MAGIC_NUMBER = new byte[]{(char)'A',(char)'B',(char)'I',(char)'F'};
	
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
			long numberOfTaggedRecords = parseNumTaggedRecords(in);
			int datablockOffset = parseTaggedRecordOffset(in);
			byte[] traceData = parseTraceDataBlock(in, datablockOffset-Ab1Util.HEADER_SIZE);
			GroupedTaggedRecords groupedDataRecordMap = parseTaggedDataRecords(in,numberOfTaggedRecords);
	
			List<NucleotideGlyph> channelOrder =getChannelOrder(groupedDataRecordMap);
			if(visitor instanceof Ab1ChromatogramFileVisitor){
				((Ab1ChromatogramFileVisitor) visitor).visitChannelOrder(channelOrder);
			}
			
			parseBasecallsFrom(groupedDataRecordMap,traceData,visitor);
	
			parseCommentsFrom(groupedDataRecordMap,traceData,visitor);
		
	}
	
	private static void parseCommentsFrom(
			GroupedTaggedRecords groupedDataRecordMap, byte[] traceData,
			ChromatogramFileVisitor visitor) {
		Properties props = new Properties();
		for(Entry<TaggedDataName, List<PascalStringTaggedDataRecord>> entry :groupedDataRecordMap.pascalStringDataRecords.entrySet()){
			for(PascalStringTaggedDataRecord  record : entry.getValue()){
				String key = String.format("%s_%d",record.getTagName(),record.getTagNumber());
				props.put(key, record.parseDataRecordFrom(traceData));
			}			
		}
		visitor.visitComments(props);
		//groupedDataRecordMap.dateDataRecords
		
	}

	private static void parseBasecallsFrom(
			GroupedTaggedRecords groupedDataRecordMap, byte[] ab1DataBlock,
			ChromatogramFileVisitor visitor) {
		for(ASCIITaggedDataRecord basecallRecord : groupedDataRecordMap.asciiDataRecords.get(TaggedDataName.BASECALLS)){
			if(basecallRecord.getTagNumber()==1L){
				visitor.visitBasecalls(basecallRecord.parseDataRecordFrom(ab1DataBlock));
			}else if(visitor instanceof Ab1ChromatogramFileVisitor){
				((Ab1ChromatogramFileVisitor) visitor).visitOriginalBasecalls(basecallRecord.parseDataRecordFrom(ab1DataBlock));
				
			}
		}
		
	}

	private static List<NucleotideGlyph> getChannelOrder(GroupedTaggedRecords dataRecordMap ){
		ASCIITaggedDataRecord order = dataRecordMap.asciiDataRecords.get(TaggedDataName.FILTER_WHEEL_ORDER).get(0);
		
		return NucleotideGlyphFactory.getInstance().getGlyphsFor(order.parseDataRecordFrom(null));

	}

	private static GroupedTaggedRecords parseTaggedDataRecords(
			InputStream in,
			long numberOfTaggedRecords) throws TraceDecoderException {
		GroupedTaggedRecords map = new GroupedTaggedRecords();
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
				
				System.out.println(record);
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
