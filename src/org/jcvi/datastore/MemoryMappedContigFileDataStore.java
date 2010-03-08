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
/*
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.jcvi.Range;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.AbstractContigFileDataStore;
import org.jcvi.assembly.contig.DefaultContigFileParser;
import org.jcvi.util.DefaultMemoryMappedFileRange;
import org.jcvi.util.MemoryMappedFileRange;

public class MemoryMappedContigFileDataStore implements ContigDataStore<PlacedRead, Contig<PlacedRead>>{

    private final File file;
    private final MemoryMappedFileRange mappedRanges;
    public MemoryMappedContigFileDataStore(File file) throws FileNotFoundException{
        this.file = file;
        this.mappedRanges = new DefaultMemoryMappedFileRange();
        DefaultContigFileParser.parse(new FileInputStream(file),
                                        new MemoryMappedContigFileVisitor(mappedRanges));
        
    }
    @Override
    public boolean contains(String contigId) throws DataStoreException {
        return mappedRanges.contains(contigId);
    }

    @Override
    public Contig<PlacedRead> get(String contigId)
            throws DataStoreException {
        Range range = mappedRanges.getRangeFor(contigId);
        
        try {
            SingleContigFileVisitor visitor = new SingleContigFileVisitor();
            final InputStream inputStream = MemoryMappedUtil.createInputStreamFromFile(file,range);
            DefaultContigFileParser.parse(inputStream,visitor);
            return visitor.getContigToReturn();
        } catch (Exception e) {
            throw new DataStoreException("error trying to get contig "+ contigId,e);
        } 
    }
    

    @Override
    public void close() throws IOException {
        mappedRanges.close();        
    }
    
    @Override
    public Iterator<String> getIds() {
        return mappedRanges.getIds();
    }
    @Override
    public int size() {
        return mappedRanges.size();
    }
    
    private static class SingleContigFileVisitor extends AbstractContigFileDataStore{
        private Contig contigToReturn;

        @Override
        protected synchronized void addContig(Contig contig) {
            if(contigToReturn !=null){
                throw new IllegalStateException("can not add more than one contig");
            }
            contigToReturn= contig;
        }
        public Contig getContigToReturn(){
            return contigToReturn;
        }

    }
    
    
    private static class MemoryMappedContigFileVisitor extends AbstractContigFileDataStore{

        private int sizeOfCurrentContig;
        private int currentStartOffset;
        private int currentLineLength;
        private final MemoryMappedFileRange mappedRanges;
        
        MemoryMappedContigFileVisitor(MemoryMappedFileRange mappedRanges){
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
        protected void addContig(Contig contig) {
            int actualLengthOfContig = sizeOfCurrentContig-currentLineLength;
            mappedRanges.put(contig.getId(), Range.buildRangeOfLength(currentStartOffset, actualLengthOfContig));
            currentStartOffset+=actualLengthOfContig;
            resetCurrentContigSize(currentLineLength);
        }
        
    }


    @Override
    public Iterator<Contig<PlacedRead>> iterator() {
        return new DataStoreIterator<Contig<PlacedRead>>(this);
    }
    
}
