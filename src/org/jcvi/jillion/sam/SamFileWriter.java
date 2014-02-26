package org.jcvi.jillion.sam;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.EnumSet;

import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeKey;
import org.jcvi.jillion.sam.header.SamHeader;

public class SamFileWriter implements SamWriter {

	private final PrintStream out;
	private final SamHeader header;
	
	
	
	public SamFileWriter(OutputStream out, SamHeader header) {
		if(out ==null){
			throw new NullPointerException("output stream can not be null");
		}
		if(header ==null){
			throw new NullPointerException("header can not be null");
		}
		this.out = new PrintStream(out);
		this.header = header;
		StringBuilder sb = new StringBuilder();
		SamUtil.writeHeader(this.header, sb);
		this.out.print(sb.toString());
	}

	
	private void appendMandatoryField(StringBuilder builder, Integer value){
		builder.append("\t");
		if(value ==null){
			builder.append("0");
		}else{
			builder.append(value);
		}
	}
	private void appendMandatoryField(StringBuilder builder, Object value){
		appendMandatoryField(builder, value, false);
	}
	private void appendMandatoryField(StringBuilder builder, Object value, boolean firstField){
		if(!firstField){
			builder.append("\t");
		}
		if(value ==null){
			builder.append("*");
		}else{
			builder.append(value);
		}
	}

	@Override
	public void close() throws IOException {
		out.close();

	}

	@Override
	public void writeRecord(SamRecord record) throws IOException {
		//TODO validate record against header?
		//can't do equals this header vs record.getHeader()
		//because we might change header by modifying sort order
		//or adding program to chain
		//we only care that it is similar enough
		//that the record is still valid
		//(reference and programs still known etc)
		StringBuilder builder = new StringBuilder(4096);
		appendMandatoryField(builder, record.getQueryName(),true);
		EnumSet<SamRecordFlags> flags = record.getFlags();
		if(flags ==null){
			appendMandatoryField(builder, (Integer)null);
		}else{
			appendMandatoryField(builder, SamRecordFlags.asBits(flags));
		}
		appendMandatoryField(builder, record.getReferenceName());
		appendMandatoryField(builder, record.getStartPosition());
		appendMandatoryField(builder, record.getMappingQuality());
		appendMandatoryField(builder, record.getCigar());
		appendMandatoryField(builder, record.getNextName());
		appendMandatoryField(builder, record.getNextOffset());
		appendMandatoryField(builder, record.getObservedTemplateLength());
		appendMandatoryField(builder, record.getSequence());
		appendMandatoryField(builder, record.getQualities());
		
		for(SamAttribute attr : record.getAttributes()){
			SamAttributeKey key = attr.getKey();
			//format = key:TYPE:value
			//where TYPE may have multiple chars
			//the getTypeCode includes the 2nd :
			//since it might need to also include
			//the first char in the value.
			builder.append("\t")
					.append(key.toString()).append(':')		
					.append(attr.getType().getTextTypeCode())
					.append(attr.getType().textEncode(attr.getValue()));
		}
		
		out.printf("%s%n", builder.toString());

	}

}
