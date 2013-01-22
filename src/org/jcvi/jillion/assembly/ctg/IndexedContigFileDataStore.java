/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
/*
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ctg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.ContigDataStore;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.jillion.internal.core.datastore.DataStoreIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
/**
 * {@code IndexedContigFileDataStore} is an implementation of 
 * {@link ContigDataStore} that only stores an index containing
 * file offsets to the various contigs contained
 * inside a contig file.  This allows large files to provide random 
 * access without taking up much memory.  The downside is each contig
 * must be re-parsed each time.
 * @author dkatzel
 *
 *
 */
public class IndexedContigFileDataStore implements CtgContigDataStore{

    private final File file;
    private final Map<String, Range> mappedRanges;
    private volatile boolean closed;
    private final NucleotideSequenceFastaDataStore fullLengthSequenceDataStore;
    /**
     * Construct an new instance of IndexedContigFileDataStore and create
     * the internal index.
     * @param file the contig file to create the datastore for.
     * @throws FileNotFoundException if the contig file does not exist.
     */
    public IndexedContigFileDataStore(NucleotideSequenceFastaDataStore fullLengthSequenceDataStore, File file) throws FileNotFoundException{
        this.file = file;
        this.mappedRanges = new LinkedHashMap<String, Range>();
        this.fullLengthSequenceDataStore = fullLengthSequenceDataStore;
        ContigFileParser.parse(file,
                                        new IndexedContigFileVisitor(mappedRanges));
        
    }
    
    private void throwExceptionIfClosed(){
    	if(closed){
    		throw new IllegalStateException("datastore is closed");
    	}
    }
    @Override
    public boolean contains(String contigId) throws DataStoreException {
    	throwExceptionIfClosed();
        return mappedRanges.containsKey(contigId);
    }

    @Override
    public Contig<AssembledRead> get(String contigId)
            throws DataStoreException {
    	throwExceptionIfClosed();
        Range range = mappedRanges.get(contigId);
        if(range==null){
        	throw new DataStoreException(contigId + " does not exist");
        }
        InputStream inputStream=null;
        try {
            SingleContigFileVisitor visitor = new SingleContigFileVisitor(fullLengthSequenceDataStore);
            inputStream = IOUtil.createInputStreamFromFile(file,(int)range.getBegin(), (int)range.getLength());
            ContigFileParser.parse(inputStream,visitor);
            return visitor.getContigToReturn();
        } catch (Exception e) {
            throw new DataStoreException("error trying to get contig "+ contigId,e);
        }finally{
            IOUtil.closeAndIgnoreErrors(inputStream);
        }
    }
    

    @Override
    public void close() throws IOException {
        closed=true;       
    }
    
    @Override
    public StreamingIterator<String> idIterator() {
    	throwExceptionIfClosed();
        return DataStoreStreamingIterator.create(this,mappedRanges.keySet().iterator());
    }
    @Override
    public long getNumberOfRecords() {
    	throwExceptionIfClosed();
        return mappedRanges.size();
    }
    
    private static class SingleContigFileVisitor extends AbstractContigFileVisitorBuilder{
        private Contig<AssembledRead> contigToReturn;

        public SingleContigFileVisitor(
				NucleotideSequenceFastaDataStore fullLengthSequenceDataStore) {
			super(fullLengthSequenceDataStore);
		}
		@Override
        protected synchronized void addContig(Contig<AssembledRead>  contig) {
            if(contigToReturn !=null){
                throw new IllegalStateException("can not add more than one contig");
            }
            contigToReturn= contig;
        }
        public Contig<AssembledRead>  getContigToReturn(){
            return contigToReturn;
        }

    }
    
    
    private static class IndexedContigFileVisitor extends AbstractContigFileVisitor{

        private int sizeOfCurrentContig;
        private int currentStartOffset;
        private int currentLineLength;
        private String currentContigId;
        private final Map<String, Range> mappedRanges;
        
        IndexedContigFileVisitor(Map<String, Range> mappedRanges){
            resetCurrentContigSize(0);
            this.mappedRanges = mappedRanges;
        }
        private void resetCurrentContigSize(int defLineSize){
            sizeOfCurrentContig=defLineSize;
        }
        @Override
        public void visitLine(String line) {
            super.visitLine(line);
            currentLineLength= line.length();
            sizeOfCurrentContig+=currentLineLength;
        }

        @Override
		protected void visitRead(String readId, int offset, Range validRange,
				NucleotideSequence basecalls, Direction dir) {
			//no-op
			
		}
		@Override
		protected void visitEndOfContig() {
			 int actualLengthOfContig = sizeOfCurrentContig-currentLineLength;
	            mappedRanges.put(currentContigId, new Range.Builder(actualLengthOfContig)
	            					.shift(currentStartOffset).build());
	            currentStartOffset+=actualLengthOfContig;
	            resetCurrentContigSize(currentLineLength);
			
		}
		@Override
		protected void visitBeginContig(String contigId,
				NucleotideSequence consensus) {
			//no-op
			currentContigId=contigId;
		}
		
        
    }


    @Override
    public StreamingIterator<Contig<AssembledRead>> iterator() {
    	throwExceptionIfClosed();
        return new DataStoreIterator<Contig<AssembledRead>>(this);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() {
        return closed;
    }
    
}
