package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.EnumSet;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeKey;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.header.SamHeader;
/**
 * {@code PresortedSamFileWriter} is a {@link SamWriter}
 * that writes out SAM files whose {@link SamRecord}s
 * are written out in the order that they are given.
 * Only use this class if the records are either not sorted
 * or if the records are already sorted when they are given to
 * this writer.  The {@link SamHeader#getSortOrder()}
 * is also written out to the SAM so make sure it is correct.
 * @author dkatzel
 *
 */
class PresortedSamFileWriter implements SamWriter {

	private static final char UNUSED_FIELD = '*';
	private static final char NULL_CHAR = '0';
	private static final char TAB = '\t';
	private final PrintStream out;
	private final SamHeader header;
	private final SamAttributeValidator attributeValidator;
	
	public PresortedSamFileWriter(File out, SamHeader header, SamAttributeValidator attributeValidator) throws IOException {
		if(out ==null){
			throw new NullPointerException("output stream can not be null");
		}
		if(header ==null){
			throw new NullPointerException("header can not be null");
		}
		if(attributeValidator ==null){
			throw new NullPointerException("header can not be null");
		}
		IOUtil.mkdirs(out.getParentFile());
		this.out = new PrintStream(out, IOUtil.UTF_8_NAME);
		this.header = header;
		this.attributeValidator = attributeValidator;
		this.out.print(SamUtil.encodeHeader(this.header).toString());
		
	}

	
	private void appendMandatoryField(StringBuilder builder, Integer value){
		builder.append(TAB);
		if(value ==null){
			builder.append(NULL_CHAR);
		}else{
			builder.append(value);
		}
	}
	private void appendMandatoryField(StringBuilder builder, Object value){
		appendMandatoryField(builder, value, false);
	}
	private void appendMandatoryField(StringBuilder builder, Object value, boolean firstField){
		if(!firstField){
			builder.append(TAB);
		}
		if(value ==null){
			builder.append(UNUSED_FIELD);
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
		try{
			header.validRecord(record, attributeValidator);
		}catch(SamValidationException e){
			throw new IOException("can not write record due to validation error(s)",e);
		}
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
			builder.append(TAB)
					.append(key.toString()).append(':')		
					.append(attr.getType().getTextTypeCode())
					.append(attr.getType().textEncode(attr.getValue()));
		}
		
		out.printf("%s%n", builder.toString());

	}

}
