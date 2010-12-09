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
 * Created on Dec 31, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcvi.assembly.cas.AbstractOnePassCasFileVisitor;
import org.jcvi.assembly.cas.CasFileInfo;
import org.jcvi.assembly.cas.CasMatch;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.MultipleDataStoreWrapper;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.util.CloseableIterator;

public class DefaultCasFileQualityDataStore extends AbstractOnePassCasFileVisitor implements CasQualityDataStore {

    private List<DataStore<EncodedGlyphs<PhredQuality>>> qualityDataStores = new ArrayList<DataStore<EncodedGlyphs<PhredQuality>>>();
    
    private final CasDataStoreFactory casDataStoreFactory;
    private DataStore<EncodedGlyphs<PhredQuality>> delegate;
   
    /**
     * @param casDataStoreFactory
     * @param cacheSize
     */
    public DefaultCasFileQualityDataStore(CasDataStoreFactory casDataStoreFactory) {
        this.casDataStoreFactory = casDataStoreFactory;
    }

    @Override
    public void visitReadFileInfo(CasFileInfo readFileInfo) {
        super.visitReadFileInfo(readFileInfo);
        handleFileInfo(readFileInfo);
    }

    private void handleFileInfo(CasFileInfo readFileInfo) {
        for(String filePath: readFileInfo.getFileNames()){
            try {
                qualityDataStores.add(casDataStoreFactory.getQualityDataStoreFor(filePath));
            } catch (Exception e) {
                //something like a fasta file doesn't have qualities
                //so fake them (CLC assembler doesn't use qualities 
                //anyway (dkatzel March 2010) 
                try {
                    qualityDataStores.add(createArtificalDataStoreFor(filePath));
                } catch (Exception e2) {
                   throw new IllegalStateException("could not load read file: "+ filePath,e2);
                }
            }
        }
    }
    
    @Override
    public synchronized void visitContigFileInfo(CasFileInfo contigFileInfo) {
        super.visitContigFileInfo(contigFileInfo);
        for(String filePath: contigFileInfo.getFileNames()){
            try {
                qualityDataStores.add(createArtificalDataStoreFor(filePath));
            } catch (Exception e) {
               throw new IllegalStateException("could not load read file: "+ filePath,e);
            }
        }
    }
    
    private DataStore<EncodedGlyphs<PhredQuality>> createArtificalDataStoreFor(String pathtoReferenceFile) throws CasDataStoreFactoryException, DataStoreException{
        Map<String, EncodedGlyphs<PhredQuality>> map = new HashMap<String, EncodedGlyphs<PhredQuality>>();
        DataStore<NucleotideEncodedGlyphs> nucleotideDataStore =casDataStoreFactory.getNucleotideDataStoreFor(pathtoReferenceFile);
        Iterator<String> idIterator = nucleotideDataStore.getIds();
        while(idIterator.hasNext()){
            String id = idIterator.next();
            byte[] buf = new byte[(int)nucleotideDataStore.get(id).getLength()];
            Arrays.fill(buf, (byte)20);
             map.put(id,new DefaultEncodedGlyphs<PhredQuality>(
                     RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE, PhredQuality.valueOf(buf)));
        }
        return new SimpleDataStore<EncodedGlyphs<PhredQuality>>(map);
    }

    @Override
    public synchronized void visitEndOfFile() {
        super.visitEndOfFile();
        delegate = 
                MultipleDataStoreWrapper.createMultipleDataStoreWrapper(DataStore.class, qualityDataStores.toArray(new DataStore[0]));
    }

    @Override
    public synchronized void close() throws IOException {
        delegate.close();
        qualityDataStores.clear();
        
    }

    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        checkIsInitialized();
        return delegate.contains(id);
    }

    @Override
    public synchronized EncodedGlyphs<PhredQuality> get(String id) throws DataStoreException {
        checkIsInitialized();
        return delegate.get(id);
    }

    @Override
    public synchronized CloseableIterator<String> getIds() throws DataStoreException {
        checkIsInitialized();
        return delegate.getIds();
    }

    @Override
    public synchronized int size() throws DataStoreException {
        checkIsInitialized();
        return delegate.size();
    }

    @Override
    public synchronized CloseableIterator<EncodedGlyphs<PhredQuality>> iterator() {
        checkIsInitialized();
        return delegate.iterator();
    }

    @Override
    protected void visitMatch(CasMatch match, long readCounter) {
        // no-op
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return delegate.isClosed();
    }
    
    
}
