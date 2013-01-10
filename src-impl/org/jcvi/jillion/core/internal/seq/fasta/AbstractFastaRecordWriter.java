package org.jcvi.jillion.core.internal.seq.fasta;

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
import org.jcvi.common.core.seq.fasta.FastaRecord;
import org.jcvi.common.core.seq.fasta.FastaRecordWriter;
import org.jcvi.common.core.seq.fasta.FastaUtil;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;


public  abstract class AbstractFastaRecordWriter<S extends Symbol, T extends Sequence<S>, F extends FastaRecord<S,T>> implements FastaRecordWriter<S, T, F>{

	private final Writer writer;
	private final int numberOfResiduesPerLine;
	private final boolean hasSymbolSeparator;
	
	protected AbstractFastaRecordWriter(OutputStream out,
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
		this.hasSymbolSeparator = hasSymbolSeparator();
	}

	@Override
	public final void close() throws IOException {
		//just incase the implementation of
		//OutputStream is buffering we need to explicitly
		//call flush
		writer.flush();
		writer.close();		
	}

	@Override
	public final void write(F record) throws IOException {
		write(record.getId(),record.getSequence(),record.getComment());
		
	}

	@Override
	public final void write(String id, T sequence)
			throws IOException {
		write(id,sequence,null);		
	}

	@Override
	public final void write(String id, T sequence,
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
		Iterator<S> iter = sequence.iterator();
        
        if(iter.hasNext()){
        	record.append(getStringRepresentationFor(iter.next()));
        }
        int count=1;
        while(iter.hasNext()){
        	if(count%numberOfResiduesPerLine==0){
        		record.append(FastaUtil.LINE_SEPARATOR);
        	}else if(hasSymbolSeparator){
        		record.append(getSymbolSeparator());
        	}
        	record.append(getStringRepresentationFor(iter.next()));
        	count++;
        }
        record.append(FastaUtil.LINE_SEPARATOR);
	}

	protected abstract boolean hasSymbolSeparator();

	protected abstract String getSymbolSeparator();

	protected abstract String getStringRepresentationFor(S symbol);
	
	private void appendDefline(String id, String comment,
			final StringBuilder record) {
		record.append(FastaUtil.HEADER_PREFIX).append(id);
        if (comment != null) {
        	record.append(' ').append(comment);
        }
        record.append(FastaUtil.LINE_SEPARATOR);
	}
    
    private int computeFormattedBufferSize(String id, T sequence, String comment) {
    	//2 extra bytes for '>' and '\n'
		int defLineSize = 2 + id.length();
		if(comment!=null){
			//extra byte for the space
			defLineSize +=1 + comment.length();
		}
		int seqLength=(int)sequence.getLength();
		int numberOfLines = seqLength/numberOfResiduesPerLine +1;
		return defLineSize + numberOfCharsFor(seqLength)+numberOfLines;
	}
   
    protected abstract int numberOfCharsFor(int numberOfSymbols);
    
    public abstract static class AbstractBuilder<S extends Symbol, T extends Sequence<S>,F extends FastaRecord<S,T>, W extends FastaRecordWriter<S, T, F>> implements org.jcvi.common.core.util.Builder<W>{
		
		private static final Charset DEFAULT_CHARSET = IOUtil.UTF_8;
		private final OutputStream out;
		private int numberOfSymbolsPerLine;
		
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
			numberPerLine(getDefaultNumberOfSymbolsPerLine());
		}
		/**
		 * Get the number of symbols
		 * that should be printed on each line
		 * of the fasta record body.
		 * @return a number >=1.
		 */
		protected abstract int getDefaultNumberOfSymbolsPerLine();
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
		public final AbstractBuilder<S,T,F,W> charset(Charset charset){
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
		 * then the a default value will be used.
		 * @param numberPerLine the basesPerLine to use
		 * must be >=1.
		 * @return this.
		 * @throws IllegalArgumentException if basesPerLine <1.
		 */
		public final AbstractBuilder<S,T,F,W> numberPerLine(int numberPerLine){
			if(numberPerLine<1){
				throw new IllegalArgumentException("number per line must be >=1");
			}
			numberOfSymbolsPerLine = numberPerLine;
			return this;
		}
		
		/**
		 * Create a new instance of {@link NucleotideSequenceFastaRecordWriter}
		 * which uses the parameters supplied to this builder.
		 * @return a new instance of {@link NucleotideSequenceFastaRecordWriter}. 
		 */
		@Override
		public final W build() {
			return create(out, numberOfSymbolsPerLine, charSet);
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
