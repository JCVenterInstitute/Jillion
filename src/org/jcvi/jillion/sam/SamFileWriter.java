package org.jcvi.jillion.sam;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;

import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeKey;
import org.jcvi.jillion.sam.header.ReadGroup;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamProgram;

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
		writeHeader();
	}

	private void writeHeader() {
		if(header.getVersion() != null){
			out.printf("@HN\tVN:%s\tSO:%s%n", 
					header.getVersion(), 
					header.getSortOrder().toString().toLowerCase(Locale.US));
		}
		for(ReferenceSequence seq : header.getReferenceSequences()){
			StringBuilder builder = new StringBuilder(300);
			
			builder.append("@SQ\tSN:").append(seq.getName())
					.append("\tLN:").append(seq.getLength());
			
			appendIfNotNull(builder, "AS", seq.getGenomeAssemblyId());
			appendIfNotNull(builder, "M5", seq.getMd5());
			appendIfNotNull(builder, "SP", seq.getSpecies());
			appendIfNotNull(builder, "UR", seq.getMd5());
			out.printf("%s%n",builder.toString());
		}
		
		for(ReadGroup readGroup : header.getReadGroups()){
			StringBuilder builder = new StringBuilder(1024);
			
			builder.append("@RG\tID:").append(readGroup.getId());
			
			appendIfNotNull(builder, "CN", readGroup.getSequencingCenter());
			appendIfNotNull(builder, "DS", readGroup.getDescription());
			
			appendIsoDateIfNotNull(builder, "DT",readGroup.getRunDate());
			
			appendIfNotNull(builder, "FO", readGroup.getFlowOrder());
			appendIfNotNull(builder, "KS", readGroup.getKeySequence());
			appendIfNotNull(builder, "LB", readGroup.getLibrary());
			appendIfNotNull(builder, "PG", readGroup.getPrograms());
			appendIfNotNull(builder, "PI", readGroup.getPredictedInsertSize());
			appendIfNotNull(builder, "PL", readGroup.getPlatform());
			appendIfNotNull(builder, "PU", readGroup.getPlatformUnit());
			appendIfNotNull(builder, "SM", readGroup.getSampleOrPoolName());
			out.printf("%s%n",builder.toString());
		}
	
		for(SamProgram program : header.getPrograms()){
			StringBuilder builder = new StringBuilder(1024);
			builder.append(String.format("%@PG\tID:%s", program.getId()));
			appendIfNotNull(builder, "PN", program.getName());
			appendIfNotNull(builder, "CL",program.getCommandLine());
			appendIfNotNull(builder, "PP", program.getPreviousProgramId());
			appendIfNotNull(builder, "DS", program.getDescription());
			appendIfNotNull(builder, "VN", program.getVersion());
			
			out.printf("%s%n", builder.toString());
		}
		
		for(String comment : header.getComments()){
			out.printf("@CO\t%s%n", comment);
		}
		
	}
	private void appendIsoDateIfNotNull(StringBuilder builder, String key, Date value){
		if(value !=null){
			builder.append("\t").append(key).append(":").append(SamUtil.formatIsoDate(value));
		}
	}
	private void appendIfNotNull(StringBuilder builder, String key, Object value){
		if(value !=null){
			builder.append("\t").append(key).append(":").append(value);
		}
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
