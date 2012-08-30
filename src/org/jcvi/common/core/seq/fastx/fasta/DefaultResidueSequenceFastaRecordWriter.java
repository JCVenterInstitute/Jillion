package org.jcvi.common.core.seq.fastx.fasta;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.FastaUtil;
import org.jcvi.common.core.symbol.residue.Residue;
import org.jcvi.common.core.symbol.residue.ResidueSequence;


public  class DefaultResidueSequenceFastaRecordWriter<R extends Residue, T extends ResidueSequence<R>, F extends FastaRecord<R,T>> implements FastaRecordWriter<R, T, F>{

	private final Writer writer;
	private final int numberOfResiduesPerLine;
	
	
	protected DefaultResidueSequenceFastaRecordWriter(OutputStream out,
			int numberOfResiduesPerLine, Charset charSet) {
		//wrap in OutputStream Writer to do char encodings
		//for us.  If we did String#getBytes(Charset) instead
		//for each write call that could put unwanted
		//char encoding headers in each record
		//which would be incorrect.  This way
		//the char encoding headers (if any) will
		//only appear at the beginning of the inputstream
		this.writer =  new BufferedWriter(new OutputStreamWriter(out,charSet));
		this.numberOfResiduesPerLine = numberOfResiduesPerLine;
	}

	@Override
	public void close() throws IOException {
		//just incase the implementation of
		//OutputStream is buffering we need to explicitly
		//call flush
		writer.flush();
		writer.close();		
	}

	@Override
	public void write(F record) throws IOException {
		write(record.getId(),record.getSequence(),record.getComment());
		
	}

	@Override
	public void write(String id, T sequence)
			throws IOException {
		write(id,sequence,null);		
	}

	@Override
	public void write(String id, T sequence,
			String optionalComment) throws IOException {
		String formattedString = toFormattedString(id, sequence, optionalComment);
		writer.write(formattedString);
		
	}

	private String toFormattedString(String id, T sequence, String comment)
    {
    	int bufferSize = computeFormattedBufferSize(id,sequence,comment);
        final StringBuilder record = new StringBuilder(bufferSize);
        appendDefline(id, comment, record);
        appendRecordBody(sequence, record);
        
        return record.toString();
    }

	private void appendRecordBody(T sequence,
			final StringBuilder record) {
		Iterator<R> iter = sequence.iterator();
        
        if(iter.hasNext()){
        	record.append(iter.next());
        }
        int count=1;
        while(iter.hasNext()){
        	if(count%numberOfResiduesPerLine==0){
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
    
    private int computeFormattedBufferSize(String id, T sequence, String comment) {
    	//2 extra bytes for '>' and '\n'
		int size = 2 + id.length();
		if(comment!=null){
			//extra byte for the space
			size +=1 + comment.length();
		}
		int seqLength=(int)sequence.getLength();
		int numberOfLines = seqLength/numberOfResiduesPerLine +1;
		return size + seqLength+numberOfLines;
	}
   
	public static abstract class AbstractBuilder<R extends Residue, T extends ResidueSequence<R>,F extends FastaRecord<R,T>, W extends FastaRecordWriter<R, T, F>> implements org.jcvi.common.core.util.Builder<FastaRecordWriter<R, T,F>>{
		private static final int DEFAULT_RESIDUES_PER_LINE = 60;
		private static final Charset DEFAULT_CHARSET = IOUtil.UTF_8;
		private final OutputStream out;
		private int numberOfResiduesPerLine= DEFAULT_RESIDUES_PER_LINE;
		
		private Charset charSet = DEFAULT_CHARSET;
		/**
		 * Create a new Builder that will use
		 * the given {@link OutputStream} to write
		 * out the fasta records.
		 * @param out the {@link OutputStream} to use;
		 * can not be null.
		 * @throws NullPointerException if out is null.
		 */
		public AbstractBuilder(OutputStream out){
			if(out==null){
				throw new NullPointerException("outputstream can not be null");
			}
			this.out = out;
		}
		/**
		 * Create a new Builder that will use
		 * the given File to write
		 * out the fasta records.  Any contents
		 * that previously existed in this file
		 * will be overwritten.
		 * @param outputFile the File to use;
		 * can not be null.
		 * @throws NullPointerException if outputFile is null.
		 * @throws FileNotFoundException if the file exists but 
		 * is a directory rather than a regular file, 
		 * does not exist but cannot be created, 
		 * or cannot be opened for any other reason.
		 */
		public AbstractBuilder(File outputFile) throws FileNotFoundException{
			this(new BufferedOutputStream(new FileOutputStream(outputFile)));
		}
		/**
		 * Change the {@link Charset} used
		 * to write out the fasta record.
		 * If this method is not called,
		 * then the CharSet will default to
		 * UTF-8.
		 * @param charset the {@link Charset} to use;
		 * can not be null.
		 * @return this.
		 * @throws NullPointerException if charset is null.
		 */
		public AbstractBuilder<R,T,F,W> charset(Charset charset){
			if(charset ==null){
				throw new NullPointerException("charset can not be null");
			}
			this.charSet=charset;
			return this;
		}
		/**
		 * Change the number of bases per line
		 * to write for each fasta record.
		 * If this method is not called,
		 * then the default bases per line of
		 * {@value #DEFAULT_RESIDUES_PER_LINE} will be used.
		 * @param residuesPerLine the basesPerLine to use
		 * must be >=1.
		 * @return this.
		 * @throws IllegalArgumentException if basesPerLine <1.
		 */
		public AbstractBuilder<R,T,F,W> residuesPerLine(int residuesPerLine){
			if(residuesPerLine<1){
				throw new IllegalArgumentException("bases per line must be >=1");
			}
			numberOfResiduesPerLine = residuesPerLine;
			return this;
		}
		
		/**
		 * Create a new instance of {@link NucleotideSequenceFastaRecordWriter}
		 * which uses the parameters supplied to this builder.
		 * @return a new instance of {@link NucleotideSequenceFastaRecordWriter}. 
		 */
		@Override
		public W build() {
			return create(out, numberOfResiduesPerLine, charSet);
		}
		/**
		 * Create a new instance of a {@link FastaRecordWriter}
		 * with the given non-null parameters.
		 * @param out
		 * @param numberOfResiduesPerLine
		 * @param charSet
		 * @return
		 */
		protected abstract W create(OutputStream out, int numberOfResiduesPerLine, Charset charSet);
	}
}
