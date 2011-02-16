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
 * Created on May 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.Range;
import org.jcvi.assembly.ace.AbstractAceContigBuilder;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceContigDataStore;
import org.jcvi.assembly.ace.AceFileParser;
import org.jcvi.io.IOUtil;
import org.jcvi.util.CloseableIterator;
import org.jcvi.util.DefaultIndexedFileRange;
import org.jcvi.util.IndexedFileRange;
/**
 * {@code IndexedAceFileDataStore} is an implementation of 
 * {@link AceContigDataStore} that only stores an index containing
 * file offsets to the various contigs contained
 * inside the ace file.  This allows large files to provide random 
 * access without taking up much memory.  The downside is each contig
 * must be re-parsed each time.
 * @author dkatzel
 */
public class IndexedAceFileDataStore extends AbstractAceContigBuilder implements AceContigDataStore{
    private final IndexedFileRange indexFileRange;
    private final File file;
    private int currentStartOffset;
    private int currentLineLength;
    private int currentFileOffset;
    
    public IndexedAceFileDataStore(File file, IndexedFileRange indexFileRange ) throws IOException{
        this.indexFileRange = indexFileRange;
        this.file = file;
        AceFileParser.parseAceFile(file, this);
    }
    public IndexedAceFileDataStore(File file) throws IOException{
        this(file, new DefaultIndexedFileRange());
    }

    @Override
    public synchronized void visitLine(String line) {        
        super.visitLine(line);
        final int length = line.length();
        currentLineLength = length;
        currentFileOffset+=length;
        
    }

    @Override
    public synchronized void visitContigHeader(String contigId, int numberOfBases,
            int numberOfReads, int numberOfBaseSegments,
            boolean reverseComplimented) {
        super.visitContigHeader(contigId, numberOfBases, numberOfReads,
                numberOfBaseSegments, reverseComplimented);
        currentStartOffset=currentFileOffset-currentLineLength;
    }

    @Override
    protected synchronized void visitContig(AceContig contig) {
        indexFileRange.put(contig.getId(), Range.buildRange(currentStartOffset, currentFileOffset));
        currentStartOffset=currentFileOffset+1;
    }

    @Override
    public boolean contains(String contigId) throws DataStoreException {
        return indexFileRange.contains(contigId);
    }

    @Override
    public AceContig get(String contigId) throws DataStoreException {
        Range range = indexFileRange.getRangeFor(contigId);
        InputStream inputStream=null;
        try {
            DefaultAceFileDataStore visitor = new DefaultAceFileDataStore();
            inputStream = IOUtil.createInputStreamFromFile(file,range);
            AceFileParser.parseAceFile(inputStream,visitor);
            return visitor.get(contigId);
        } catch (Exception e) {
            throw new DataStoreException("error trying to get contig "+ contigId,e);
        }finally{
            IOUtil.closeAndIgnoreErrors(inputStream);
        }
    }

    @Override
    public CloseableIterator<String> getIds() {
        return indexFileRange.getIds();
    }

    @Override
    public int size() {
        return indexFileRange.size();
    }

    @Override
    public void close() throws IOException {
        indexFileRange.close();
        
    }

    

    @Override
    public CloseableIterator<AceContig> iterator() {
        return new DataStoreIterator<AceContig>(this);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return indexFileRange.isClosed();
    }
}
