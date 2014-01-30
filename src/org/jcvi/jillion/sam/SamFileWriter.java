package org.jcvi.jillion.sam;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Locale;

import org.jcvi.jillion.sam.header.ReadGroup;
import org.jcvi.jillion.sam.header.ReferenceSequence;
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
		//TODO add programs and comments
		
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

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeRecord(SamRecord record) throws IOException {
		// TODO Auto-generated method stub

	}

}
