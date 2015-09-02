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
package org.jcvi.jillion.trace.fastq;

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

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.Builder;
/**
 * {@code FastqWriterBuilder}
 * is a {@link Builder} that 
 * builds an instance of {@link FastqWriter}.
 * @author dkatzel
 *
 */
public final class FastqWriterBuilder implements Builder<FastqWriter>{
	
	private static final String CR = "\n";
	private static final int ALL_ON_ONE_LINE =-1;
	
	private static final Charset DEFAULT_CHARSET = IOUtil.UTF_8;
	private  static final FastqQualityCodec DEFAULT_CODEC = FastqQualityCodec.SANGER;
	private final OutputStream out;
	private int numberOfBasesPerLine=ALL_ON_ONE_LINE;
	private boolean writeIdOnQualityLine=false;
	private FastqQualityCodec codec = DEFAULT_CODEC;
	private Charset charSet = DEFAULT_CHARSET;
	
	
	private Comparator<FastqRecord> comparator=null;
	private Integer inMemoryCacheSize;
	private File tmpDir;
	
	/**
	 * Create a new {@link FastqWriterBuilder} that will use
	 * the given {@link OutputStream} to write
	 * out the fastq records.
	 * @param out the {@link OutputStream} to use;
	 * can not be null.
	 * @throws NullPointerException if out is null.
	 */
	public FastqWriterBuilder(OutputStream out){
		if(out==null){
			throw new NullPointerException("outputstream can not be null");
		}
		this.out = out;
	}
	
	/**
	 * Create a new {@link FastqWriterBuilder} that will use
	 * the given File to write
	 * out the fastq records.  Any contents
	 * that previously existed in this file
	 * will be overwritten.  If the path for the given
	 * File does not yet exist, then it will be created.
	 * @param outputFile the File to use;
	 * can not be null.
	 * @throws NullPointerException if outputFile is null.
	 * @throws IOException if there is a problem creating the new file. 
	 * or cannot be opened for any other reason.
	 */
	public FastqWriterBuilder(File outputFile) throws IOException{
		IOUtil.mkdirs(outputFile.getParentFile());
		this.out =new BufferedOutputStream(new FileOutputStream(outputFile));
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
	public FastqWriterBuilder charset(Charset charset){
		if(charset ==null){
			throw new NullPointerException("charset can not be null");
		}
		this.charSet=charset;
		return this;
	}
	/**
	 * Change the method that quality values
	 * are encoded by providing a {@link FastqQualityCodec}
	 * implementation.  If this method is not called,
	 * this writer will default to {@link FastqQualityCodec#SANGER}.
	 * @param codec the {@link FastqQualityCodec} to use
	 * when writing out the encoded quality sequence;
	 * can not be null.
	 * @return this.
	 * Throws {@link NullPointerException} if codec is null.
	 */
	public FastqWriterBuilder qualityCodec(FastqQualityCodec codec){
		if(codec ==null){
			throw new NullPointerException("codec can not be null");
		}
		this.codec=codec;
		return this;
	}
	/**
	 * If this method is called
	 * then the id of the fastq records
	 * will be duplicated on the fastq quality deflines.
	 * This is not recommended since it will usually 
	 * add several megabytes to the file size and only
	 * contain duplicate data.  (The id of the record
	 * will also be on the nucleotide sequence defline).
	 * @return this.
	 */
	public FastqWriterBuilder duplicateIdOnQualityDefLine(){
		this.writeIdOnQualityLine=true;
		return this;
	}
	/**
	 * Change the number of bases per line
	 * to write for each fastq record.
	 * If this method is not called,
	 * then then the each nucleotide and quality sequence will
	 * be written out on one line each.
	 * @param basesPerLine the basesPerLine to use
	 * must be >=1.
	 * @return this.
	 * @throws IllegalArgumentException if basesPerLine <1.
	 */
	public FastqWriterBuilder basesPerLine(int basesPerLine){
		if(basesPerLine<1){
			throw new IllegalArgumentException("number per line must be >=1");
		}
		numberOfBasesPerLine = basesPerLine;
		return this;
	}
	
	
	/**
	 * Write out the {@link FastqRecord}s written by this writer
	 * sorted by the specified {@link Comparator} but do all the sorting in memory.
	 * All of the records will be cached in memory so the output can be written sorted
	 * when the {@link FastqWriter#close()} method is called.  It is not recommended
	 * to use this method if a large number of records will be written because
	 * an {@link OutOfMemoryError} may occur.
	 * 
	 * @param comparator the {@link Comparator} to use to sort the {@link FastqRecord}s;
	 * can not be null.
	 * 
	 * @return this
	 * 
	 * @throws NullPointerException if comparator is null.
	 * 
	 * @since 5.0
	 */
	public FastqWriterBuilder sortInMemoryOnly(Comparator<FastqRecord> comparator){
	    Objects.requireNonNull(comparator);
	    this.comparator = comparator;
	    this.inMemoryCacheSize = null;
	    this.tmpDir = null;
	    
	    return this;
	}
	/**
	 * Write out the {@link FastqRecord}s written by this writer
	 * sorted by the specified {@link Comparator} using a combination of 
	 * in memory sorting and writing out sorted temporary files.
	 * 
	 * <p/>
	 * This is the same as {@link #sort(Comparator, int, File) sort(comparator, inMemCacheSize, null)}
	 * which uses the default temp area to make temp files.
	 * 
	 * @param comparator the {@link Comparator} to use to sort the {@link FastqRecord}s;
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
	public FastqWriterBuilder sort(Comparator<FastqRecord> comparator, int inMemoryCacheSize){
	    return sort(comparator, inMemoryCacheSize, null);
	}
	/**
	 * Write out the {@link FastqRecord}s written by this writer
	 * sorted by the specified {@link Comparator} using a combination of 
	 * in memory sorting and writing out sorted temporary files.
	 * 
	 * An in memory cache similar of the specified size will be created
	 * and whenever the cache fills, the sorted cache contents will be written to a temp file
	 * in the specified tmpDir and the cache cleared out to make room for more records to write.
	 * There may be multiple temp files written depending on how many {@link FastqRecord}s are 
	 * passed to the Writer.
	 * 
	 * Once {@link FastqWriter#close()} has been called, the contents of the in memory cache,
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
	public FastqWriterBuilder sort(Comparator<FastqRecord> comparator, int inMemoryCacheSize, File dir){
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
	
	
	@Override
	public FastqWriter build() {
	    FastqWriter writer = new FastqRecordWriterImpl(out, charSet, 
				codec, writeIdOnQualityLine, numberOfBasesPerLine);
	    if(comparator ==null){
	        return writer;
	    }
	    if(inMemoryCacheSize ==null){
	        return new InMemorySortedFastqWriter(writer, comparator);
	    }
	    return new TmpDirSortedFastqWriter(writer, comparator, codec, tmpDir,inMemoryCacheSize);
	}
	
	
	
	private static final class FastqRecordWriterImpl implements FastqWriter{
		
		
		private final Writer writer;
		private final FastqQualityCodec codec;
		private final boolean writeIdOnQualityLine;
		private final int numberOfBasesPerLine;
		
		private FastqRecordWriterImpl(OutputStream out, Charset charset,
				FastqQualityCodec codec, boolean writeIdOnQualityLine,
				int numberOfBasesPerLine){
			//wrap in OutputStream Writer to do char encodings
			//for us.  If we did String#getBytes(Charset) instead
			//for each write call that could put unwanted
			//char encoding headers in each record
			//which would be incorrect.  This way
			//the char encoding headers (if any) will
			//only appear at the beginning of the inputstream
			this.writer =  new BufferedWriter(new OutputStreamWriter(out,charset));
			this.codec = codec;
			this.writeIdOnQualityLine = writeIdOnQualityLine;
			this.numberOfBasesPerLine = numberOfBasesPerLine;
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
		public void write(FastqRecord record) throws IOException {
			write(record.getId(), record.getNucleotideSequence(), record.getQualitySequence(), record.getComment());
			
		}
	
		@Override
		public void write(String id, NucleotideSequence nucleotides,
				QualitySequence qualities) throws IOException {
			write(id,nucleotides,qualities,null);
			
		}
	
		@Override
		public void write(String id, NucleotideSequence sequence,
				QualitySequence qualities, String optionalComment)
				throws IOException {
			if(id ==null){
				throw new NullPointerException("id can not be null");
			}
			if(sequence ==null){
				throw new NullPointerException("nucleotide sequence can not be null");
			}
			if(qualities ==null){
				throw new NullPointerException("quality sequence can not be null");
			}
			long nucLength = sequence.getLength();
			long qualLength = qualities.getLength();
			if(nucLength != qualLength){
				throw new IllegalArgumentException(
						String.format("nucleotide and quality sequences must be same length: %d vs %d",nucLength, qualLength));
			}
			final String formattedString =toFormattedString(id, sequence, qualities, optionalComment);
			
			writer.write(formattedString);
	
		}
	
		private CharSequence encodeNucleotides(NucleotideSequence sequence){
			if(numberOfBasesPerLine==ALL_ON_ONE_LINE){
				return sequence.toString();
			}
			
			Iterator<Nucleotide> iter = sequence.iterator();
			int numBases = (int)sequence.getLength();
			int numberOfLines = numBases/numberOfBasesPerLine +1;
			StringBuilder builder = new StringBuilder(numBases+numberOfLines);
			if(iter.hasNext()){
				builder.append(iter.next());
			}
			int i=1;
			while(iter.hasNext()){
				if(i%numberOfBasesPerLine==0){
					builder.append(CR);
				}
				builder.append(iter.next());
				i++;
			}
			return builder;
		}
		private CharSequence encodeQualities(QualitySequence qualities){
			String encodedQualities = codec.encode(qualities);
			if(numberOfBasesPerLine==ALL_ON_ONE_LINE){
				return encodedQualities;
			}
			int numberOfLines = encodedQualities.length()/numberOfBasesPerLine +1;
			StringBuilder builder = new StringBuilder(encodedQualities.length()+numberOfLines);
			for(int i=0; i<encodedQualities.length();i++){
				if(i>0 && i%numberOfBasesPerLine ==0){
					builder.append(CR);
				}
				builder.append(encodedQualities.charAt(i));
			}
			
			return builder;
		}
		private String toFormattedString(String id, NucleotideSequence sequence,
				QualitySequence qualities, String optionalComment) {
			boolean hasComment = optionalComment != null;
	
			StringBuilder builder = new StringBuilder("@").append(id);
			if (hasComment) {
				builder.append(' ').append(optionalComment);
			}
			builder.append(CR).append(encodeNucleotides(sequence)).append(CR).append('+');
			if (writeIdOnQualityLine) {
				builder.append(id);
			}
			builder.append(CR).append(encodeQualities(qualities)).append(CR);
			return builder.toString();
		}
	}
}
