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
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ctg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.ContigDataStore;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreIterator;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIteratorAdapter;

public class DefaultContigFileDataStore extends AbstractContigFileVisitorBuilder implements ContigDataStore<PlacedRead, Contig<PlacedRead>>{
    private final Map<String,Contig<PlacedRead>> contigs = new TreeMap<String, Contig<PlacedRead>>();

    private boolean isClosed = false;

    /**
     * Construct a ContigFileDataStore containing the contig
     * data from the given contig file.
     * @param contigFile the contig file containing the desired contig data.
     * @throws FileNotFoundException if the given contig file does not exist.
     */
    public DefaultContigFileDataStore(File contigFile) throws FileNotFoundException{
        ContigFileParser.parse(contigFile, this);
    }
    /**
     * Construct a ContigFileDataStore containing the contig
     * data from the given {@link InputStream} of a contig file.
     * @param inputStream an {@link InputStream} of contig file data.
     */
    public DefaultContigFileDataStore(InputStream inputStream) {
        ContigFileParser.parse(inputStream, this);
    }
    @Override
    protected void addContig(Contig<PlacedRead> contig) {
        contigs.put(contig.getId(), contig);
        
    }

    @Override
    public boolean contains(String contigId)throws DataStoreException {
        throwExceptionIfClosed();
        throwExceptionIfNotInitialized();
        return contigs.containsKey(contigId);
    }
    
    private void throwExceptionIfNotInitialized() throws DataStoreException {
        if(!isInitialized()){
            throw new DataStoreException("DataStore not yet initialized");
        }
    }
    
    
    private void throwExceptionIfClosed() throws DataStoreException {
        if(isClosed){
            throw new DataStoreException("DataStore is closed");
        }
    }

    
    @Override
    public Contig<PlacedRead> get(String contigId)
            throws DataStoreException {
        throwExceptionIfClosed();
        throwExceptionIfNotInitialized();
        return contigs.get(contigId);
    }

    @Override
    public void close() throws IOException {        
        isClosed = true;
        contigs.clear();
    }


    @Override
    public CloseableIterator<String> idIterator() {
        return CloseableIteratorAdapter.adapt(contigs.keySet().iterator());
    }


    @Override
    public long getNumberOfRecords() {
        return contigs.size();
    }


    @Override
    public boolean isClosed() throws DataStoreException {
        return isClosed;
    }
    @Override
    public CloseableIterator<Contig<PlacedRead>> iterator() {
        return new DataStoreIterator<Contig<PlacedRead>>(this);
    }
}
