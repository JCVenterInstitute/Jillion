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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.InputStreamSupplier;
/**
 * {@code FastqFileDataStoreBuilder}
 * is a Builder that can create new instances
 * of {@link FastqDataStore}s
 * using data from a given input fastq file.
 * 
 * @author dkatzel
 *
 */
public final class FastqFileDataStoreBuilder{
	private FastqParser parser;
	private InputStreamSupplier inputStreamSupplier;
	
	private Predicate<String> idFilter = (id)-> true;
	//default to null which we can use
	//in the datastore implementations as a short circuit
	//to skip building records if we don't need to 
	//(for example for id  iterator or memento only filtering)
	private Predicate<FastqRecord> recordFilter = null;
	
	//by default store everything in memory
	private DataStoreProviderHint hint = DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED;
	
	private FastqQualityCodec codec=null;
	
	private boolean hasComments;
	private boolean isMultiLine;
	
	/**
	 * Create a new instance of {@code FastqFileDataStoreBuilder}
	 * which will build a {@link FastqDataStore} for the given
	 * fastq file.
	 * 
	 * <p>
	 * Since Jillion 5, the file may be any file encoding that is supported 
	 * by {@link InputStreamSupplier#forFile(File)} so files may be 
	 * compressed.
	 * </p>
	 * 
	 * @implNote As of Jillion 5, this is the same as
	 * {@code new FastqFileDataStoreBuilder( InputStreamSupplier.forFile(fastqFile) );}
	 * 
	 * @param fastqFile the fastq file make a {@link FastqDataStore} with. 
	 * @throws IOException if the fastq file does not exist, or can not be read.
	 * @throws NullPointerException if fastqFile is null.
	 */
	public FastqFileDataStoreBuilder(File fastqFile) throws IOException{
		this( InputStreamSupplier.forFile(fastqFile));
	}
	
	/**
         * Create a new instance of {@code FastqFileDataStoreBuilder}
         * which will build a {@link FastqDataStore} for the given
         * {@link InputStreamSupplier}.
         * @param inputStreamSupplier the {@link InputStreamSupplier} that will
         * return {@link InputStream}s of fastq file data to make a {@link FastqDataStore} with. 
         * 
         * @throws NullPointerException if inputStreamSupplier is null.
         */
        public FastqFileDataStoreBuilder(InputStreamSupplier inputStreamSupplier){
                Objects.requireNonNull(inputStreamSupplier);
                this.inputStreamSupplier = inputStreamSupplier;
                this.parser = null;
        }
	
	
	
	/**
         * Create a new instance of {@code FastqFileDataStoreBuilder}
         * which will build a {@link FastqDataStore} for the given
         * fastq encoded {@link InputStream}.
         * 
         * If using this constructor, a {@link FastqQualityCodec}
         * must be provided using {@link #qualityCodec(FastqQualityCodec)}
         * since the inputStream can not be parsed multiple times
         * to determine the qualiy encoding.
         * 
         * @param inputStream the {@link InputStream} of the fastq file make a {@link FastqDataStore} with. 
         * @throws IOException if the fastq file does not exist, or can not be read.
         * @throws NullPointerException if inputstream is null.
         */
        public FastqFileDataStoreBuilder(InputStream inputStream) throws IOException{
                
                this.parser = FastqFileParser.create(inputStream);
        }
	
	/**
	 * Create a new instance of {@code FastqFileDataStoreBuilder}
	 * which will build a {@link FastqDataStore} for contents of the given
	 * {@link FastqParser}.
	 * @param parser the {@link FastqParser} instance to make a {@link FastqDataStore} with. 
	 * @throws NullPointerException if parser is null.
	 */
	public FastqFileDataStoreBuilder(FastqParser parser) throws IOException{
		if(parser ==null){
			throw new NullPointerException("parser can not be null");
		}
		this.parser = parser;
	}
	/**
	 * Explicitly specify the {@link FastqQualityCodec} that 
	 * is used to encode the quality values in the given fastq file.
	 * If the given {@link FastqQualityCodec} is not the one used to encode
	 * the quality data in the file, then incorrect
	 * quality values might silently decode 
	 * the wrong (possibly higher or lower) quality values or
	 * cause an {@link IllegalArgumentException}
	 * to be thrown during the {@link #build()} if the incorrectly decoded quality values are not
	 * valid phred scores.
	 * <p>
	 * If a quality codec is not given to this builder,
	 * then during the actual datastore construction in {@link #build()},
	 * the codec will be automatically determined by parsing a portion of the 
	 * file an additional time.  This causes extra I/O and increases CPU and execution time
	 * to create a new {@link FastqDataStore} so it is recommended that the {@link FastqQualityCodec} 
	 * is given if it is known to avoid this performance penalty.
	 * </p>
	 * The quality codec MUST be set if the input fastq data source
	 * is an inputStream, since the {@link java.io.InputStream} may be be able to be fully
	 * read twice.  Not setting this method with an {@link java.io.InputStream}
	 * source may cause an {@link IllegalStateException} to be thrown in {@link #build()}.
	 * <p>
	 * @param codec the {@link FastqQualityCodec} to use to parse the file; can not be null.
	 * @return this.
	 * @throws NullPointerException if codec is null.
	 */
	public FastqFileDataStoreBuilder qualityCodec(FastqQualityCodec codec){
		if(codec==null){
			throw new NullPointerException("quality codec can not be null");
		}
		this.codec = codec;
		return this;
	}
	
	 /**
         * Does this fastq file contain comments on the deflines.
         * If not called, then by default this builder uses hasComments = false.
         * 
         * @param hasComments
         *            do the deflines of the sequences contain comments. If set to
         *            {@code true} then a more computationally intensive parsing is
         *            performed to try to distinguish the id from the comment.
         *            Remember some Fastq id's can have spaces which makes comment
         *            detection difficult and complex.
         * @return this
         */
    public FastqFileDataStoreBuilder hasComments(boolean hasComments){
        this.hasComments = hasComments;
        return this;
    }
    /**
     * Does this fastq file contain records that may have multiple lines for the sequence
     * and qualities sequences.
     * Most fastq files define each record in 4 lines:
     * <ol>
     * <li>The defline</li>
     * <li>nucleotide sequence</li>
     * <li>The quality defline</li>
     * <li>quality sequence</li>
     * </ol>
     * 
     * However, some fastq files split the nucleotide and quality sequences
     * over multiple lines.  For performance reasons,
     * checking for multi-lines has been turned off by default
     * and must be explicitly turned on by calling this method.
     * 
     * @param multiline {@code true} if this file may contain multilines;
     * {@code false} otherwise (defaults to {@code false}.
     * 
     * @return this
     */
    public FastqFileDataStoreBuilder hasMultilineSequences(boolean multiline){
        this.isMultiLine = multiline;
        return this;
    }
        
	
	/**
	 * Only include the {@link FastqRecord}s which pass
	 * the given {@link Predicate} for the ID.  If a filter
	 * is not given to this builder, then all records
	 * in the fastq file will be included in the built
	 * {@link FastqDataStore}.
	 * <p>
     * If both this method and {@link #filter(Predicate)}
     * are used, then the ID filter is applied first
     * and then any remaining records are filtered with this
     * filter.
     * <p>
     * If this method is called multiple times, then the previous
     * filters are overwritten and only the last filter is used.
     * 
	 * @param filter a {@link Predicate} instance that can be
	 * used to filter out specified fastq records BY ID; can not be null. 
	 * @return this.
	 * 
	 * @throws NullPointerException if filter is null.
	 * 
	 * @apiNote This is different than {@link #filterRecords(Predicate)}
     * because the latter needs to parse the entire record before
     * filtering can be determined while this filter only needs the ID. If you are only filtering
     * by ID, use this method which may have better
     * performance since the nucleotide and quality values don't have to be parsed
     * on reads that aren't accepted by the id filter.
	 * 
	 * @see #filterRecords(Predicate)
	 */
	public FastqFileDataStoreBuilder filter(Predicate<String> filter){
		if(filter==null){
			throw new NullPointerException("filter can not be null");
		}
		this.idFilter = filter;
		return this;
	}
	
	/**
     * Only include the {@link FastqRecord}s which pass
     * the given {@link Predicate}.  If no predicates
     * are given to this builder, then all records
     * in the fastq file will be included in the built
     * {@link FastqDataStore}.
     * <p>
     * If both this method and {@link #filter(Predicate)} to filter by ID
     * are used, then the ID filter is applied first
     * and then any remaining records are filtered with this
     * filter.
     * <p>
     * If this method is called multiple times, then the previous
     * filters are overwritten and only the last filter is used.
     * 
     * @param filter a {@link Predicate} instance that can be
     * used to filter out specified fastq records; can not be null. 
     * 
     * @return this.
     * @throws NullPointerException if filter is null.
     * 
     * @apiNote This is different than {@link #filter(Predicate)}
     * because the latter can only filter by ID. If you are only filtering
     * by ID, use {@link #filter(Predicate)} which may have better
     * performance since the nucleotide and quality values don't have to be parsed
     * on reads that aren't accepted by the id filter.
     * <p>
     * Also, we had to keep the
     * old filter method to maintain compatibility with old versions of Jillion
     * 
     * @since 5.0
     * @see #filter(Predicate)
     */
    public FastqFileDataStoreBuilder filterRecords(Predicate<FastqRecord> filter){
            if(filter==null){
                    throw new NullPointerException("filter can not be null");
            }
            this.recordFilter = filter;
            return this;
    }
	/**
	 * Provide a {@link DataStoreProviderHint} to this builder
	 * to let it know the implementation preferences of the client.
	 * If no hint is given, then this builder will
	 * try to store all the fastq records in memory
	 * which may cause an Exception if there isn't enough memory.
	 * The {@link DataStoreProviderHint}  is just a guideline 
	 * and may be ignored by this builder when determining
	 * which {@link FastqDataStore} implementation to chose
	 * to build in {@link #build()}.
	 * @param hint an instance of {@link DataStoreProviderHint};
	 * can not be null.
	 * @return this
	 * @throws NullPointerException if hint is null.
	 * @see DataStoreProviderHint
	 */
	public FastqFileDataStoreBuilder hint(DataStoreProviderHint hint){
		if(hint==null){
			throw new NullPointerException("hint can not be null");
		}
		this.hint = hint;
		return this;
	}
	
	
	/**
	 * Parse the given fastq file and return
	 * a new instance of a {@link FastqDataStore}
	 * using all the input parameters given so far.  
	 * If not all optional parameters are set then default
	 * values will be used:
	 * <ul>
	 * <li>
	 * If no {@link FastqQualityCodec} has been specified
	 * by {@link #qualityCodec(FastqQualityCodec)},
	 * then it will be auto-detected for a performance
	 * penalty.
	 * </li>
	 * <li>
	 * If no filter has been specified
	 * by {@link #filter(Predicate)} or {@link #filterRecords(Predicate)},
	 * then all {@link FastqRecord}s will be included in this {@link FastqDataStore}.
	 * </li>
	 * <li>
	 * If no {@link DataStoreProviderHint} has been specified
	 * by {@link #hint(DataStoreProviderHint)},
	 * then this builder will try to store all the 
	 * {@link FastqRecord}s that meet the {@link DataStoreFilter}
	 * requirements in memory.  This may cause out of memory errors
	 * if there is not enough memory available.
	 * </li>
	 * </ul>
	 * @return a new {@link FastqFileDataStore} instance;
	 * never null.
	 * @throws IOException if there is a problem parsing the 
	 * fastq file.
	 * @throws IllegalStateException if quality codec is not set
	 * and input fastq data source is an inputStream which might
	 * prevent us from parsing the data twice (once to determine codec, 
	 * the other to actual decode the data).
	 * @throws IllegalArgumentException if the quality values
	 * are not valid for the specified {@link FastqQualityCodec}
	 * (can be thrown even if the quality codec is auto-detected).
	 * @see #qualityCodec(FastqQualityCodec)
	 * @see #hint(DataStoreProviderHint)
	 * @see #filter(Predicate)
	 * @see #filterRecords(Predicate)
	 */
	public FastqFileDataStore build() throws IOException {
	    
	    
	        if(parser ==null){
	        	parser = new FastqFileParserBuilder(inputStreamSupplier,hint ==DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)
	        	
                        .hasComments(hasComments)
                        .hasMultilineSequences(isMultiLine)
                        .build();
	                                                    
	        }
		if(codec ==null){
			if(parser.isReadOnceOnly()){
				//can't parse this twice
				//to guess codec
				//THEN re-parse to decode
				throw new IllegalStateException("must set quality codec if parsing inputStream");
			}
			codec = FastqUtil.guessQualityCodecUsed(parser);
		}
		switch(hint){
			case RANDOM_ACCESS_OPTIMIZE_SPEED:
				return DefaultFastqFileDataStore.create(parser, codec, idFilter, recordFilter);
			case RANDOM_ACCESS_OPTIMIZE_MEMORY:
			        
				return parser.canCreateMemento()?
				        IndexedFastqFileDataStore.create(parser,  codec, idFilter, recordFilter)
				        : DefaultFastqFileDataStore.create(parser, codec, idFilter, recordFilter);
			case ITERATION_ONLY:
				return LargeFastqFileDataStore.create(parser, codec, idFilter, recordFilter);
			default:
				//can not happen
				throw new IllegalArgumentException("unknown provider hint : "+ hint);
		}
	}


	
}
