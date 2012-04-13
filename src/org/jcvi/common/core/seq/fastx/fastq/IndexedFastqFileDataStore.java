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

package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.AcceptingFastXFilter;
import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.DefaultIndexedFileRange;
import org.jcvi.common.core.util.IndexedFileRange;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
public final class IndexedFastqFileDataStore implements FastqDataStore{

    private final IndexedFileRange indexFileRange;
    private final FastqQualityCodec qualityCodec;
    private final File file;
    
    public static FastqFileDataStoreBuilderVisitor createBuilder(File file,FastqQualityCodec qualityCodec){
    	return new IndexedFastqFileDataStoreBuilderVisitor(new DefaultIndexedFileRange(), qualityCodec, file, AcceptingFastXFilter.INSTANCE);
    }
    public static FastqDataStore create(File file,FastqQualityCodec qualityCodec) throws IOException{
    	FastqFileDataStoreBuilderVisitor builderVisitor = createBuilder(file, qualityCodec);
    	FastqFileParser.parse(file, builderVisitor);
    	return builderVisitor.build();
    }
    
    public static FastqFileDataStoreBuilderVisitor createBuilder(File file,FastqQualityCodec qualityCodec, FastXFilter filter){
    	return new IndexedFastqFileDataStoreBuilderVisitor(new DefaultIndexedFileRange(), qualityCodec, file, filter);
    }
    public static FastqDataStore create(File file,FastqQualityCodec qualityCodec,FastXFilter filter) throws IOException{
    	FastqFileDataStoreBuilderVisitor builderVisitor = createBuilder(file, qualityCodec,filter);
    	FastqFileParser.parse(file, builderVisitor);
    	return builderVisitor.build();
    }
    /**
     * @param file
     * @throws FileNotFoundException 
     */
    private IndexedFastqFileDataStore(File file,FastqQualityCodec qualityCodec,IndexedFileRange indexFileRange){
        this.file = file;
        this.qualityCodec = qualityCodec;
        this.indexFileRange = indexFileRange;
    }
   
   
    @Override
    public CloseableIterator<String> idIterator() throws DataStoreException {
        return indexFileRange.getIds();
    }
    @Override
    public FastqRecord get(String id) throws DataStoreException {
        if(!contains(id)){
            throw new DataStoreException(id +" does not exist in datastore");
        }
        Range range =indexFileRange.getRangeFor(id);
        InputStream in =null;
        try {
            in = IOUtil.createInputStreamFromFile(file,range);
            FastqDataStore datastore = DefaultFastqFileDataStore.create(in, qualityCodec);
            return datastore.get(id);
        } catch (IOException e) {
            throw new DataStoreException("error reading fastq file",e);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        try{
        return indexFileRange.contains(id);
        }catch(IllegalStateException e){
            throw new DataStoreException("error quering index", e);
        }
    }
    @Override
    public long getNumberOfRecords() throws DataStoreException {
        return indexFileRange.size();
    }
    @Override
    public void close() throws IOException {
        indexFileRange.close();
        
    }
    @Override
    public CloseableIterator<FastqRecord> iterator() throws DataStoreException {
    	try {
			return LargeFastqFileDataStore.create(file, qualityCodec)
					.iterator();
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("fastq file no longer exists! : "+ file.getAbsolutePath());
		}
       
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return indexFileRange.isClosed();
    }
    /**
     * Implementation of {@link FastqFileDataStoreBuilderVisitor}
     * that only stores the file offsets for each record.
     * @author dkatzel
     *
     */
    private static final class IndexedFastqFileDataStoreBuilderVisitor implements FastqFileDataStoreBuilderVisitor{
    	private final IndexedFileRange indexFileRange;
        private final FastqQualityCodec qualityCodec;
        private final File file;
        private long currentStartOffset=0;
        private long currentEndOffset=-1L;
        private String currentId;
        private final FastXFilter filter;
        private boolean includeCurrentRecord;
        private boolean finishedVisitingFile=false;
		private IndexedFastqFileDataStoreBuilderVisitor(
				IndexedFileRange indexFileRange,
				FastqQualityCodec qualityCodec, File file, FastXFilter filter) {
			this.indexFileRange = indexFileRange;
			this.qualityCodec = qualityCodec;
			this.file = file;
			this.filter = filter;
		}

		@Override
		public DeflineReturnCode visitDefline(String id, String optionalComment) {
			checkNotFinished();
			currentId = id;
			includeCurrentRecord = filter.accept(id, optionalComment);
			
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
				indexFileRange.put(currentId, Range.create(currentStartOffset, currentEndOffset));
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
			
		}

		@Override
		public FastqDataStore build() {
			checkNotFinished();
			return new IndexedFastqFileDataStore(file, qualityCodec, indexFileRange);
		}
    	
    }
    
}
