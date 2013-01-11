/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.seq.trace.fastq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreFilters;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.internal.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.core.internal.util.VariableWidthInteger;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * {@code IndexedFastqFileDataStore} is an implementation of 
 * {@link FastqDataStore} that only stores an index containing
 * file offsets to the various {@link FastqRecord}s contained
 * inside the fastq file.  This implementation provides random access
 * to large files taking up much memory.  The downside is each fastq record
 * must be seeked to and then re-parsed each time and the fastq file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
final class IndexedFastqFileDataStore implements FastqDataStore{

    private final Map<String, VariableWidthInteger> indexFileRange;
    private final FastqQualityCodec qualityCodec;
    private final File file;
    private final DataStoreFilter filter;
    private volatile boolean closed;
    /**
	 * Creates a new {@link FastqFileDataStoreBuilderVisitor}
	 * instance that will build an {@link IndexedFastqFileDataStore}
	 * using the given fastq file.  In order to build a {@link FastqDataStore},
	 * the returned instance must be passed to the fastq parser and once
	 * the entire fastq file has been visited, the {@link FastqDataStore}
	 * can be built using the {@link FastqFileDataStoreBuilderVisitor#build()}
	 * method like this:
	 * <pre>
	 * FastqFileDataStoreBuilderVisitor builder = createBuilder(fastqFile, qualityCodec);
	 * FastqFileParser.parse(fastqFile, builder);
	 * FastqDataStore datastore = builder.build();
	 * </pre>
	 * This implementation of {@link FastqFileDataStoreBuilderVisitor}
	 * can only be used to parse a single fastq file (the one given)
	 * This builder visitor will construct the datastore based
	 * on various visitXXX methods in the {@link FastqFileVisitor}
	 * interface.  In this case, the indexed implementation will combine
	 * callbacks for parts of each fastq record along with the 
	 * corresponding calls to {@link FastqFileVisitor#visitLine(String)} 
	 * to compute the file offsets for each record.
	 * @param file the fastq to create an {@link FastqFileDataStoreBuilderVisitor}
	 * for using an indexed implementation.
	 * @param qualityCodec the {@link FastqQualityCodec} that should
	 * be used to decode the encoded qualities of each record in the file.
	 * @return a new instance of {@link FastqFileDataStoreBuilderVisitor};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fastq file or the {@link FastqQualityCodec} is null.
	 */
    public static FastqFileDataStoreBuilderVisitor createBuilder(File file,FastqQualityCodec qualityCodec){
    	return new IndexedFastqFileDataStoreBuilderVisitor(qualityCodec, file, DataStoreFilters.alwaysAccept());
    }
    /**
	 * Creates a new {@link IndexedFastqFileDataStore}
	 * instance using the given fastqFile which uses has its quality
	 * values encoded in a manner that can be decoded by the given
	 * {@link FastqQualityCodec}.
	 * @param file the fastq file to create an {@link IndexedFastqFileDataStore}
	 * for.
	 * @param qualityCodec the {@link FastqQualityCodec} that should
	 * be used to decode the encoded qualities of each record in the file.
	 * @return a new instance of {@link FastqDataStore};
	 * never null.
	 * @throws IOException if the input fastq file does not exist or 
	 * if there is a problem parsing the file.
	 * @throws NullPointerException if the input fastq file or the {@link FastqQualityCodec} is null.
	 */
    public static FastqDataStore create(File file,FastqQualityCodec qualityCodec) throws IOException{
    	FastqFileDataStoreBuilderVisitor builderVisitor = createBuilder(file, qualityCodec);
    	FastqFileParser.parse(file, builderVisitor);
    	return builderVisitor.build();
    }
    /**
   	 * Creates a new {@link FastqFileDataStoreBuilderVisitor}
   	 * instance that will build an {@link IndexedFastqFileDataStore}
   	 * using the given fastq file which only contains the records
   	 * in the file that are accepted by the given filter.  In order to build a {@link FastqDataStore},
   	 * the returned instance must be passed to the fastq parser and once
   	 * the entire fastq file has been visited, the {@link FastqDataStore}
   	 * can be built using the {@link FastqFileDataStoreBuilderVisitor#build()}
   	 * method like this:
   	 * <pre>
   	 * FastqFileDataStoreBuilderVisitor builder = createBuilder(fastqFile, qualityCodec, filter);
   	 * FastqFileParser.parse(fastqFile, builder);
   	 * FastqDataStore datastore = builder.build();
   	 * </pre>
   	 * This implementation of {@link FastqFileDataStoreBuilderVisitor}
   	 * can only be used to parse a single fastq file (the one given)
   	 * This builder visitor will construct the datastore based
   	 * on various visitXXX methods in the {@link FastqFileVisitor}
   	 * interface.  In this case, the indexed implementation will combine
   	 * callbacks for parts of each fastq record along with the 
   	 * corresponding calls to {@link FastqFileVisitor#visitLine(String)} 
   	 * to compute the file offsets for each record.
   	 * @param file the fastq to create an {@link FastqFileDataStoreBuilderVisitor}
   	 * for using an indexed implementation.
   	 * @param qualityCodec the {@link FastqQualityCodec} that should
	 * be used to decode the encoded qualities of each record in the file.
   	 * @param filter a {@link FastXFilter} that will be used
	 * to filter out some (possibly all or none) of the records from
	 * the fastq file so they will not be included in the {@link FastqDataStore}.
	 * Only records which cause {@link FastXFilter#accept(String, String)}
	 * to return {@code true} will be added to this datastore.
   	 * @return a new instance of {@link FastqFileDataStoreBuilderVisitor};
   	 * never null.
   	 * @throws FileNotFoundException if the input fasta file does not exist.
   	 * @throws NullPointerException if the input fastq file or the {@link FastqQualityCodec} is null.
   	 */
    public static FastqFileDataStoreBuilderVisitor createBuilder(File file,FastqQualityCodec qualityCodec, DataStoreFilter filter){
    	return new IndexedFastqFileDataStoreBuilderVisitor(qualityCodec, file, filter);
    }
    
    /**
   	 * Creates a new {@link IndexedFastqFileDataStore}
   	 * instance using the given fastqFile which uses has its quality
   	 * values encoded in a manner that can be decoded by the given
   	 * {@link FastqQualityCodec} which only contains the records
   	 * in the file that are accepted by the given filter.
   	 * @param file the fastq file to create an {@link IndexedFastqFileDataStore}
   	 * for.
   	 * @param qualityCodec the {@link FastqQualityCodec} that should
	 * be used to decode the encoded qualities of each record in the file.
	 * @param filter a {@link DataStoreFilter} that will be used
	 * to filter out some (possibly all or none) of the records from
	 * the fastq file so they will not be included in the {@link FastqDataStore}.
	 * Only records which cause {@link FastXFilter#accept(String, String)}
	 * to return {@code true} will be added to this datastore.
   	 * @return a new instance of {@link FastqDataStore};
   	 * never null.
   	 * @throws IOException if the input fastq file does not exist or 
   	 * if there is a problem parsing the file.
   	 * @throws NullPointerException if the input fastq file or the {@link FastqQualityCodec} is null.
   	 */
    public static FastqDataStore create(File file,FastqQualityCodec qualityCodec,DataStoreFilter filter) throws IOException{
    	FastqFileDataStoreBuilderVisitor builderVisitor = createBuilder(file, qualityCodec,filter);
    	FastqFileParser.parse(file, builderVisitor);
    	return builderVisitor.build();
    }
    /**
     * @param file
     * @throws FileNotFoundException 
     */
    private IndexedFastqFileDataStore(File file,FastqQualityCodec qualityCodec,Map<String,VariableWidthInteger> indexFileRange,DataStoreFilter filter){
        this.file = file;
        this.qualityCodec = qualityCodec;
        this.indexFileRange = indexFileRange;
        this.filter = filter;
    }
   
   
    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
    	throwExceptionIfClosed();
        return DataStoreStreamingIterator.create(this,indexFileRange.keySet().iterator());
    }
    @Override
    public FastqRecord get(String id) throws DataStoreException {
    	throwExceptionIfClosed();
    	VariableWidthInteger range =indexFileRange.get(id);
        if(range ==null){
        	throw new DataStoreException(id +" does not exist in datastore");
        }
        InputStream in =null;
        try {
        	in = new FileInputStream(file);
        	IOUtil.blockingSkip(in, range.getValue());
        	SingleFastqRecordVisitor visitor = new SingleFastqRecordVisitor();
        	FastqFileParser.parse(in, visitor);
        	return visitor.getRecord();
        } catch (IOException e) {
            throw new DataStoreException("error reading fastq file",e);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
    	throwExceptionIfClosed();
        return indexFileRange.containsKey(id);
    }
    @Override
    public long getNumberOfRecords() throws DataStoreException {
    	throwExceptionIfClosed();
        return indexFileRange.size();
    }
    @Override
    public void close(){
    	closed=true;
        
    }
    
    private void throwExceptionIfClosed(){
    	if(closed){
    		throw new IllegalStateException("datastore is closed");
    	}
    }
    @Override
    public StreamingIterator<FastqRecord> iterator() throws DataStoreException {
    	throwExceptionIfClosed();
    	try {
    		StreamingIterator<FastqRecord> iter = LargeFastqFileDataStore.create(file, filter, qualityCodec)
					.iterator();
    		//iter has a different lifecylce than this datastore
    		//so wrap it
    		return DataStoreStreamingIterator.create(this,iter);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("fastq file no longer exists! : "+ file.getAbsolutePath());
		}
       
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() {
        return closed;
    }
    
    private final class SingleFastqRecordVisitor implements FastqFileVisitor{
    	private String currentId;
    	private String currentComment;
    	private NucleotideSequence currentNucleotideSequence;
    	private QualitySequence currentQualitySequence;
    	
    	private FastqRecord record;
    	
    	
		public final FastqRecord getRecord() {
			return record;
		}

		@Override
		public void visitLine(String line) {
			//no-op			
		}

		@Override
		public void visitFile() {
			//no-op			
		}

		@Override
		public void visitEndOfFile() {
			//no-op
		}

		@Override
		public EndOfBodyReturnCode visitEndOfBody() {
			//only parse one record
			record = new FastqRecordBuilder(currentId, currentNucleotideSequence, currentQualitySequence)
								.comment(currentComment)
								.build();
			return EndOfBodyReturnCode.STOP_PARSING;
		}

		@Override
		public DeflineReturnCode visitDefline(String id, String optionalComment) {
			this.currentId = id;
			this.currentComment = optionalComment;
			return DeflineReturnCode.VISIT_CURRENT_RECORD;
		}

		@Override
		public void visitNucleotides(NucleotideSequence nucleotides) {
			currentNucleotideSequence = nucleotides;
			
		}

		@Override
		public void visitEncodedQualities(String encodedQualities) {
			currentQualitySequence = qualityCodec.decode(encodedQualities);
			
		}
    	
    }
    /**
     * Implementation of {@link FastqFileDataStoreBuilderVisitor}
     * that only stores the file offsets for each record.
     * @author dkatzel
     *
     */
    private static final class IndexedFastqFileDataStoreBuilderVisitor implements FastqFileDataStoreBuilderVisitor{
    	private final Map<String,VariableWidthInteger> indexFileRange=new LinkedHashMap<String, VariableWidthInteger>();
        private final FastqQualityCodec qualityCodec;
        private final File file;
        private long currentStartOffset=0;
        private long currentEndOffset=-1L;
        private String currentId;
        private final DataStoreFilter filter;
        private boolean includeCurrentRecord;
        private volatile boolean finishedVisitingFile=false;
		private IndexedFastqFileDataStoreBuilderVisitor(
				FastqQualityCodec qualityCodec, File file, DataStoreFilter filter) {
			
			if(qualityCodec ==null){
				throw new NullPointerException("quality codec can not be null");
			}
			this.qualityCodec = qualityCodec;
			this.file = file;
			this.filter = filter;
		}

		@Override
		public DeflineReturnCode visitDefline(String id, String optionalComment) {
			checkNotFinished();
			currentId = id;
			includeCurrentRecord= filter.accept(id);
			
			if(includeCurrentRecord){
				return DeflineReturnCode.VISIT_CURRENT_RECORD;
			}
			return DeflineReturnCode.SKIP_CURRENT_RECORD;
		}

		@Override
		public void visitNucleotides(NucleotideSequence nucleotides) {
			checkNotFinished();
			
		}

		@Override
		public void visitEncodedQualities(String encodedQualities) {
			checkNotFinished();
			
		}

		@Override
		public EndOfBodyReturnCode visitEndOfBody() {
			checkNotFinished();
			if(includeCurrentRecord){
				indexFileRange.put(currentId, VariableWidthInteger.valueOf(currentStartOffset));
			}
			currentStartOffset=currentEndOffset+1;
			return EndOfBodyReturnCode.KEEP_PARSING;
		}

		@Override
		public void visitLine(String line) {
			checkNotFinished();
			currentEndOffset+=line.length();
		}
		private void checkNotFinished(){
			if(finishedVisitingFile){
				throw new IllegalStateException("already visitied entire file");
			}
		}
		@Override
		public void visitFile() {
			checkNotFinished();
			
		}

		@Override
		public void visitEndOfFile() {
			checkNotFinished();
			finishedVisitingFile=true;
		}

		@Override
		public FastqDataStore build() {
			if(!finishedVisitingFile){
				throw new IllegalStateException("not yet finished visiting file");
			}
			return new IndexedFastqFileDataStore(file, qualityCodec, indexFileRange,filter);
		}
    	
    }
    
}
