package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.FastaUtil;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;

public final class DefaultNucleotideSequenceFastaRecordWriter implements NucleotideSequenceFastaRecordWriter{

	private final OutputStream out;
	private final int numberOfBasesPerLine;
	private final Charset charSet;
	
	
	private DefaultNucleotideSequenceFastaRecordWriter(OutputStream out,
			int numberOfBasesPerLine, Charset charSet) {
		this.out = out;
		this.numberOfBasesPerLine = numberOfBasesPerLine;
		this.charSet = charSet;
	}

	@Override
	public void close() throws IOException {
		//just incase the implementation of
		//OutputStream is buffering we need to explicitly
		//call flush
		out.flush();
		out.close();		
	}

	@Override
	public void write(NucleotideSequenceFastaRecord record) throws IOException {
		write(record.getId(),record.getSequence(),record.getComment());
		
	}

	@Override
	public void write(String id, NucleotideSequence sequence)
			throws IOException {
		write(id,sequence,null);		
	}

	@Override
	public void write(String id, NucleotideSequence sequence,
			String optionalComment) throws IOException {
		String formattedString = toFormattedString(id, sequence, optionalComment);
		out.write(formattedString.getBytes(charSet));
		
	}

	private String toFormattedString(String id, NucleotideSequence sequence, String comment)
    {
    	int bufferSize = computeFormattedBufferSize(id,sequence,comment);
        final StringBuilder record = new StringBuilder(bufferSize);
        appendDefline(id, comment, record);
        appendRecordBody(sequence, record);
        
        return record.toString();
    }

	private void appendRecordBody(NucleotideSequence sequence,
			final StringBuilder record) {
		Iterator<Nucleotide> iter = sequence.iterator();
        
        if(iter.hasNext()){
        	record.append(iter.next());
        }
        int count=1;
        while(iter.hasNext()){
        	if(count%numberOfBasesPerLine==0){
        		record.append(FastaUtil.LINE_SEPARATOR);
        	}
        	record.append(iter.next());
        	count++;
        }
        record.append(FastaUtil.LINE_SEPARATOR);
	}

	private void appendDefline(String id, String comment,
			final StringBuilder record) {
		record.append(FastaUtil.HEADER_PREFIX).append(
                id);
        if (comment != null) {
        	record.append(' ').append(comment);
        }
        record.append(FastaUtil.LINE_SEPARATOR);
	}
    
    private int computeFormattedBufferSize(String id, NucleotideSequence sequence, String comment) {
    	//2 extra bytes for '>' and '\n'
		int size = 2 + id.length();
		if(comment!=null){
			//extra byte for the space
			size +=1 + comment.length();
		}
		int seqLength=(int)sequence.getLength();
		int numberOfLines = seqLength/numberOfBasesPerLine +1;
		return size + seqLength+numberOfLines;
	}
   
	public static final class Builder implements org.jcvi.common.core.util.Builder<NucleotideSequenceFastaRecordWriter>{
		private static final int DEFAULT_BASES_PER_LINE = 60;
		private static final Charset DEFAULT_CHARSET = IOUtil.UTF_8;
		private final OutputStream out;
		private int numberOfBasesPerLine= DEFAULT_BASES_PER_LINE;
		
		private Charset charSet = DEFAULT_CHARSET;
		public Builder(OutputStream out){
			if(out==null){
				throw new NullPointerException("outputstream can not be null");
			}
			this.out = out;
		}
		
		public Builder(File outputFile) throws FileNotFoundException{
			this(new BufferedOutputStream(new FileOutputStream(outputFile)));
		}
		
		public Builder basesPerLine(int basesPerLine){
			if(basesPerLine<1){
				throw new IllegalArgumentException("bases per line must be >=1");
			}
			numberOfBasesPerLine = basesPerLine;
			return this;
		}
		
		
		@Override
		public NucleotideSequenceFastaRecordWriter build() {
			return new DefaultNucleotideSequenceFastaRecordWriter(out, numberOfBasesPerLine, charSet);
		}
		
	}
}
