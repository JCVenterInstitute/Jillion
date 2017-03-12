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
import java.util.Objects;
import java.util.function.Function;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.internal.trace.fastq.ParsedFastqRecord;
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
	
	private static final int  DEFAULT_CACHE_SIZE = 10_000;
	
	private final OutputStream out;
	private int numberOfBasesPerLine=ALL_ON_ONE_LINE;
	private boolean writeIdOnQualityLine=false;
	private FastqQualityCodec codec = DEFAULT_CODEC;
	private Charset charSet = DEFAULT_CHARSET;
	
	
	private Comparator<FastqRecord> comparator=null;
	private Integer inMemoryCacheSize;
	private File tmpDir;
	
	private Function<FastqRecord, FastqRecord> adapterFunction= null;
	
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
         * Wrap the built FastqWriter wrap the given fastqWriter and intercept any calls
         * to write() to allow the record to be transformed in some way.  For example,
         * to change the id or modify or trim the sequences; or even skip the record entirely.
         * Calling this method multiple times will overwrite the adaptation not nest additional
         * adapters.  If you need to do that make several calls to 
         * {@link FastqWriter#adapt(FastqWriter, Function)}
         * 
         * @param adapterFunction a Function that is given the input FastqRecord to be written
         * and will return a possibly new FastqRecord to actually write.  If the function
         * returns {@code null} then the record is skipped.  If this parameter is null,
         * then no adapter will be used as if this method was never called at all.
         * @return this
         * 
         * @since 5.3
         * @see FastqWriter#adapt(FastqWriter, Function)
         */
	public FastqWriterBuilder adapt( Function<FastqRecord, FastqRecord> adapterFunction) {
	        this.adapterFunction = adapterFunction;
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
         * in memory sorting and writing out sorted temporary files.  With a default
         * number of records to keep in memory at any time.  (Currently 10,000 records).
         * 
         * <p/>
         * This is the same as {@link #sort(Comparator, int) sort(comparator, 10_000)}
         * which uses the default temp area to make temp files.
         * 
         * @param comparator the {@link Comparator} to use to sort the {@link FastqRecord}s;
         * can not be null.
         * 
         * @return this.
         * 
         * @throws NullPointerException if comparator is null.
         * 
         * @see #sort(Comparator, int)
         * @see #sort(Comparator, int, File)
         * @see #sortInMemoryOnly(Comparator)
         * 
         * @since 5.3
         */
        public FastqWriterBuilder sort(Comparator<FastqRecord> comparator){
            return sort(comparator, DEFAULT_CACHE_SIZE);
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
	    FastqWriter innerWriter = buildInnerWriter();
	    if(adapterFunction ==null){
	        return innerWriter;
	    }
	    return FastqWriter.adapt(innerWriter, adapterFunction);
	}
	
	private FastqWriter buildInnerWriter(){
            FastqWriter writer = new FastqRecordWriterImpl(out, charSet, codec,
                    writeIdOnQualityLine, numberOfBasesPerLine);
            if (comparator == null) {
                return writer;
            }
            if (inMemoryCacheSize == null) {
                return new InMemorySortedFastqWriter(writer, comparator);
            }
            return new TmpDirSortedFastqWriter(writer, comparator, codec, tmpDir, inMemoryCacheSize);
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
                public void write(FastqRecord record) throws IOException{
		    write(record, null);
		}
		
		@Override
		public void write(FastqRecord record, Range trimRange) throws IOException {
		    //performance improvement:
		    //ParsedFastqRecord is a special implementation of FastqRecord
		    //that delays converting the encoded sequence and qualities
		    //from Strings into Jillion Sequence objects.
		    //
		    //This is actually the implementation returned by FastqParser
		    //visit methods since Jillion 5.0.
		    //
		    //Profiling code to find performance bottlenecks
		    //revealed all the CPU cycles wasted from
		    //parsing a fastq file, and then rewriting
		    //those records practically unaltered
		    //back out (possibly filtering out records).
		    //
		    //It seemed silly to decode the encoded strings
		    //into Jillion objects only to re-encode them
		    //back into the same strings again right away during writing.
		    //
		    //This code is much uglier, but improves performance by 25%
		    if(record instanceof ParsedFastqRecord){
		        ParsedFastqRecord parsedRecord = (ParsedFastqRecord) record;
		        FastqQualityCodec recordCodec = parsedRecord.getQualityCodec();
		        String formattedString;
		        if(codec == recordCodec){
		            //same quality encoding can use encoded qualities as is
		            formattedString=  toFormattedString(parsedRecord.getId(),
		                    encodeNucleotides(parsedRecord.getNucleotideString(), trimRange),
		                    formatEncodedQualities(parsedRecord.getEncodedQualities(), trimRange), parsedRecord.getComment());
		        }else{
		            //not same quality encoding		            
		            String reEncodedQualities = reEncodeTrimmedQualities(parsedRecord, recordCodec, trimRange);
		            
		            formattedString = toFormattedString(parsedRecord.getId(), encodeNucleotides(parsedRecord.getNucleotideString(), trimRange),
		                    formatEncodedQualities(reEncodedQualities, null), parsedRecord.getComment());
		        }
		        
		        writer.write(formattedString);
		    }else{
		        if(trimRange ==null){
		            write(record.getId(), record.getNucleotideSequence(), record.getQualitySequence(), record.getComment());
		        }else{
		            write(record.getId(), 
		                    record.getNucleotideSequence()
		                            .toBuilder()
		                            .trim(trimRange)
		                            .turnOffDataCompression(true)
		                            .build(), 
		                    record.getQualitySequence()
		                            .toBuilder()
		                            .trim(trimRange)
		                            .turnOffDataCompression(true)
		                            .build(),
		                 record.getComment());
		        }
		    }
			
		}


        private String reEncodeTrimmedQualities(ParsedFastqRecord parsedRecord,
                FastqQualityCodec recordCodec, Range trimRange) {
            int offsetCorrection = codec.getOffset() - recordCodec.getOffset();
            char[] chars = trimRange ==null?  parsedRecord.getEncodedQualities().toCharArray()
                                : parsedRecord.getEncodedQualities().substring((int)trimRange.getBegin(), (int) trimRange.getEnd()+1).toCharArray();
            for(int i=0; i< chars.length; i++){
                chars[i] = (char)(chars[i] + offsetCorrection);
            }
            return new String(chars);
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
			final String formattedString =toFormattedString(id, encodeNucleotides(sequence.toString(), null), encodeQualities(qualities), optionalComment);
			
			writer.write(formattedString);
	
		}
	
		private CharSequence encodeNucleotides(String sequence, Range trimRange){
		    
		    String nucleotidesToWrite = trimRange==null? sequence : sequence.substring((int)trimRange.getBegin(), (int) trimRange.getEnd()+1);
	            
			if(numberOfBasesPerLine==ALL_ON_ONE_LINE){
				return nucleotidesToWrite;
			}
			
			int numBases = nucleotidesToWrite.length();
			int numberOfLines = numBases/numberOfBasesPerLine +1;
			StringBuilder builder = new StringBuilder(numBases+numberOfLines);
			if(numBases ==0){
				return builder;
			}
			
			char[] charArray = nucleotidesToWrite.toCharArray();
			builder.append(charArray[0]);
			
			for(int i=1; i< numBases; i++){
				if(i%numberOfBasesPerLine==0){
					builder.append(CR);
				}
				builder.append(charArray[i]);
			}
			return builder;
		}
		private CharSequence encodeQualities(QualitySequence qualities){
			String encodedQualities = codec.encode(qualities);
			return formatEncodedQualities(encodedQualities, null);
		}


        private CharSequence formatEncodedQualities(String encodedQualities, Range trimRange) {
            String qualitiesToWrite = trimRange==null? encodedQualities : encodedQualities.substring((int)trimRange.getBegin(), (int) trimRange.getEnd()+1);
            
            if(numberOfBasesPerLine==ALL_ON_ONE_LINE){
				return qualitiesToWrite;
			}
			int numberOfLines = qualitiesToWrite.length()/numberOfBasesPerLine +1;
			StringBuilder builder = new StringBuilder(qualitiesToWrite.length()+numberOfLines);
			for(int i=0; i<qualitiesToWrite.length();i++){
				if(i>0 && i%numberOfBasesPerLine ==0){
					builder.append(CR);
				}
				builder.append(qualitiesToWrite.charAt(i));
			}
			
			return builder;
        }
		private String toFormattedString(String id, CharSequence sequence,
				CharSequence encodedQualities, String optionalComment) {
			boolean hasComment = optionalComment != null;
			int CRlength = CR.length();
//			int numChars = 2 + (hasComment? optionalComment.length():0) + (writeIdOnQualityLine? 2*id.length() : id.length())  + sequence.length() *2 + CRlength*4;
			
			StringBuilder builder = new StringBuilder(sequence.length()*8).append("@").append(id);
			if (hasComment) {
				builder.append(' ').append(optionalComment);
			}
			builder.append(CR).append(sequence).append(CR).append('+');
			if (writeIdOnQualityLine) {
				builder.append(id);
			}
			builder.append(CR).append(encodedQualities).append(CR);
			return builder.toString();
		}
	}



    
}
