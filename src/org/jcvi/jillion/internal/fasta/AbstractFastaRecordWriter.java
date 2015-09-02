/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.fasta;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.FastaRecord;
import org.jcvi.jillion.fasta.FastaUtil;
import org.jcvi.jillion.fasta.FastaWriter;


public  abstract class AbstractFastaRecordWriter<S, T extends Sequence<S>, F extends FastaRecord<S,T>> implements FastaWriter<S, T, F>{

	private final Writer writer;
	private final int numberOfResiduesPerLine;
	private final boolean hasSymbolSeparator;
	private final String eol;
	
	protected AbstractFastaRecordWriter(OutputStream out,
			int numberOfResiduesPerLine, Charset charSet, String eol) {
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
		this.eol = eol;
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
        	if(numberOfResiduesPerLine != AbstractBuilder.ALL_ON_ONE_LINE && count%numberOfResiduesPerLine==0){
        		record.append(eol);
        	}else if(hasSymbolSeparator){
        		record.append(getSymbolSeparator());
        	}
        	record.append(getStringRepresentationFor(iter.next()));
        	count++;
        }
        record.append(eol);
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
        record.append(eol);
	}
    
    private int computeFormattedBufferSize(String id, T sequence, String comment) {
    	//extra bytes for '>' and end of line length
		int defLineSize = 1 + eol.length() + id.length();
		if(comment!=null){
			//extra byte for the space
			defLineSize +=1 + comment.length();
		}
		int seqLength=(int)sequence.getLength();
		int numberOfLines = numberOfResiduesPerLine == AbstractBuilder.ALL_ON_ONE_LINE ? 1 : seqLength/numberOfResiduesPerLine +1;
		return defLineSize + numberOfCharsFor(seqLength)+ numberOfLines*eol.length();
	}
   
    protected abstract int numberOfCharsFor(int numberOfSymbols);
    
    public abstract static class AbstractBuilder<S, T extends Sequence<S>,F extends FastaRecord<S,T>, W extends FastaWriter<S, T, F>> implements org.jcvi.jillion.core.util.Builder<W>{
		
    	public static final int ALL_ON_ONE_LINE =-1;
    	
		private static final Charset DEFAULT_CHARSET = IOUtil.UTF_8;
		private static final String DEFAULT_LINE_SEPARATOR = FastaUtil.LINE_SEPARATOR;
		
		private final OutputStream out;
		private int numberOfSymbolsPerLine;
		private String eol = DEFAULT_LINE_SEPARATOR;
		
		private Comparator<F> comparator = null;
		private Integer inMemoryCacheSize;
		private File tmpDir;
		
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
		public AbstractBuilder(File outputFile) throws IOException{
			//create parent dirs if do not yet exist
			IOUtil.mkdirs(outputFile.getParentFile());
			this.out = new BufferedOutputStream(new FileOutputStream(outputFile));
			numberPerLine(getDefaultNumberOfSymbolsPerLine());
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
		 * Change the end of line separator String.
		 * <br/>
		 * <strong>Note:</strong> This method should be used with caution.
		 * Many fasta parsers will only expect lines to be terminated
		 * with '\n'.
		 * @param eol the end of line string to use to denote
		 * end of lines; can not be empty.  If set to {@code null},
		 * then the default '\n' is used.
		 * @return this.
		 */
		public final AbstractBuilder<S,T,F,W> lineSeparator(String eol){
			if(eol==null){
				this.eol = DEFAULT_LINE_SEPARATOR;
			}else{
				if(eol.isEmpty()){
					throw new IllegalArgumentException("line separator can not be empty");
				}
				this.eol = eol;
			}
			return this;
		}
		/**
		 * Change the number of bases per line
		 * to write for each fasta record.
		 * If this method is not called,
		 * then the a default value will be used.
		 * If {@link #allBasesOnOneLine()}
		 * is also called, then whichever method is called last
		 * "wins".
		 * @param numberPerLine the basesPerLine to use
		 * must be >=1.
		 * @return this.
		 * @throws IllegalArgumentException if basesPerLine <1.
		 * @see #allBasesOnOneLine()
		 */
		public final AbstractBuilder<S,T,F,W> numberPerLine(int numberPerLine){
			if(numberPerLine<1){
				throw new IllegalArgumentException("number per line must be >=1");
			}
			numberOfSymbolsPerLine = numberPerLine;
			return this;
		}
		/**
		 * Write all the bases on one line instead of allowing
		 * the possibility of multiple lines. If {@link #numberPerLine(int)}
		 * is also called, then whichever method is called last
		 * "wins".
		 * @return this.
		 * @see #numberPerLine(int)
		 */
		public final AbstractBuilder<S,T,F,W> allBasesOnOneLine(){
			numberOfSymbolsPerLine = ALL_ON_ONE_LINE;
			return this;
		}
		
		/**
		 * Create a new {@link FastaWriter} instance
		 * which uses the parameters supplied to this builder.
		 * @return a new instance of {@link FastaWriter}. 
		 */
		@Override
		public final W build() {
			W writer= create(out, numberOfSymbolsPerLine, charSet,eol);
			
			if(comparator ==null){
				//no sorting
				return writer;
			}
			if(inMemoryCacheSize ==null){
				//use in memory cache only
				return createInMemorySortedWriterWriter(writer, comparator);
			}
			return createTmpDirSortedWriterWriter(writer, comparator, inMemoryCacheSize, tmpDir);
		}
		
		protected abstract W createTmpDirSortedWriterWriter(FastaWriter<S,T,F> delegate, Comparator<F> comparator, int cacheSize, File tmpDir);
		protected abstract W createInMemorySortedWriterWriter(FastaWriter<S,T,F> delegate, Comparator<F> comparator);
		/**
		 * Create a new instance of a {@link FastaWriter}
		 * with the given non-null parameters.
		 * @param out the OutputStream that the new writer will use
		 * to output the fasta data; will never be null.
		 * @param numberOfResiduesPerLine the number of residues per line 
		 * that should be written to the file.  If a sequence length
		 * is more than this number, then the sequence should be split
		 * over several lines, each line never exceeding this value.
		 * @param charSet the {@link Charset} to encode the output to,
		 * usually UTF-8.
		 * @return a new {@link FastaWriter}; can not be null.
		 */
		protected abstract W create(OutputStream out, int numberOfResiduesPerLine, Charset charSet, String eol);
		
		
		/**
		 * Write out the {@link FastaRecord}s written by this writer
		 * sorted by the specified {@link Comparator} but do all the sorting in memory.
		 * All of the records will be cached in memory so the output can be written sorted
		 * when the {@link FastaWriter#close()} method is called.  It is not recommended
		 * to use this method if a large number of records will be written because
		 * an {@link OutOfMemoryError} may occur.
		 * 
		 * @param comparator the {@link Comparator} to use to sort the {@link FastaRecord}s;
		 * can not be null.
		 * 
		 * @return this
		 * 
		 * @throws NullPointerException if comparator is null.
		 * 
		 * @since 5.0
		 */
		public AbstractBuilder<S,T,F,W> sortInMemoryOnly(Comparator<F> comparator){
		    Objects.requireNonNull(comparator);
		    this.comparator = comparator;
		    this.inMemoryCacheSize = null;
		    this.tmpDir = null;
		    
		    return this;
		}
		/**
		 * Write out the {@link FastaRecord}s written by this writer
		 * sorted by the specified {@link Comparator} using a combination of 
		 * in memory sorting and writing out sorted temporary files.
		 * 
		 * <p/>
		 * This is the same as {@link #sort(Comparator, int, File) sort(comparator, inMemCacheSize, null)}
		 * which uses the default temp area to make temp files.
		 * 
		 * @param comparator the {@link Comparator} to use to sort the {@link FastaRecord}s;
		 * can not be null.
		 * @param inMemoryCacheSize the in memory cache size to use; must be positive.
		 * 
		 * @return this.
		 * 
		 * @throws NullPointerException if comparator is null.
		 * 
		 * @throws IllegalArgumentException if inMemoryCacheSize < 1.
		 * 
		 * @since 5.0
		 */
		public AbstractBuilder<S,T,F,W> sort(Comparator<F> comparator, int inMemoryCacheSize){
		    return sort(comparator, inMemoryCacheSize, null);
		}
		/**
		 * Write out the {@link FastaRecord}s written by this writer
		 * sorted by the specified {@link Comparator} using a combination of 
		 * in memory sorting and writing out sorted temporary files.
		 * 
		 * An in memory cache similar of the specified size will be created
		 * and whenever the cache fills, the sorted cache contents will be written to a temp file
		 * in the specified tmpDir and the cache cleared out to make room for more records to write.
		 * There may be multiple temp files written depending on how many {@link FastaRecord}s are 
		 * passed to the Writer.
		 * 
		 * Once {@link FastaWriter#close()} has been called, the contents of the in memory cache,
		 * and any temp files written out are merged and written sorted to the final output file.
		 * 
		 * <p>
		 * If any files get written to temp files under {@code dir},
		 * they will be deleted when the writer is closed.  However {@code dir}
		 * itself will not be deleted so feel free to provide non-temp directories as well.
		 * </p>
		 * 
		 * @param comparator the {@link Comparator} to use to sort the {@link FastqRecord}s;
		 * can not be null.
		 * @param inMemoryCacheSize the in memory cache size to use; must be positive.
		 * 
		 * @param dir the directory to write files to; if set to {@code null}
		 * then the default system temporary directory is used.  If the value is not null,
		 * then it must be a directory that already exists.
		 * 
		 * @return this.
		 * 
		 * @throws NullPointerException if comparator is null.
		 * 
		 * @throws IllegalArgumentException if inMemoryCacheSize < 1,
		 * 			or if a non-null dir does not exist or is not a directory.
		 * 
		 * @since 5.0
		 */
		public AbstractBuilder<S,T,F,W> sort(Comparator<F> comparator, int inMemoryCacheSize, File dir){
		    Objects.requireNonNull(comparator);
		    if(inMemoryCacheSize <1){
		        throw new IllegalArgumentException("in memory cache size must be positive");
		    }
		    
		    if(dir !=null){
		    	if(!dir.exists()){	    
		    		throw new IllegalArgumentException("tmpDir does not exist: " + dir.getAbsolutePath());
		    	}
		    	if(!dir.isDirectory()){
		    		throw new IllegalArgumentException("tmpDir is not a directory: " + dir.getAbsolutePath());
		    	}
		    }
	            this.comparator = comparator;
	            this.inMemoryCacheSize = inMemoryCacheSize;
	            this.tmpDir = dir;
		    return this;
		}
	}
}
