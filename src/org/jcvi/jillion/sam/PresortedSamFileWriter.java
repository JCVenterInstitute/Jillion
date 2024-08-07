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
package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.internal.sam.SamUtil;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeKey;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
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
			header.validateRecord(record, attributeValidator);
		}catch(SamValidationException e){
			throw new IOException("can not write record due to validation error(s)",e);
		}
		StringBuilder builder = new StringBuilder(4096);
		appendMandatoryField(builder, record.getQueryName(),true);
		SamRecordFlags flags = record.getFlags();
		if(flags ==null){
			appendMandatoryField(builder, (Integer)null);
		}else{
			appendMandatoryField(builder, flags.asInt());
		}
		appendMandatoryField(builder, record.getReferenceName());
		appendMandatoryField(builder, record.getStartPosition());
		//convert negative numbers into unsigned values
		//so -1 (mapping quality not available) becomes 255 as per SAM spec
		//appendMandatoryField(builder, IOUtil.toUnsignedByte(record.getMappingQuality()));
		appendMandatoryField(builder, Math.max(0, record.getMappingQuality()));
		
		appendMandatoryField(builder, record.getCigar());
		appendMandatoryField(builder, record.getNextName());
		appendMandatoryField(builder, record.getNextPosition());
		appendMandatoryField(builder, record.getObservedTemplateLength());
		appendMandatoryField(builder, record.getSequence());
		//always encode qualities in SANGER ?
		QualitySequence quals =record.getQualities();
		appendMandatoryField(builder, quals ==null? null : FastqQualityCodec.SANGER.encode(quals));
		
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
