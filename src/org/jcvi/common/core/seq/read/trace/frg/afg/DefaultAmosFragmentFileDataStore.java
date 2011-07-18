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

package org.jcvi.common.core.seq.read.trace.frg.afg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.common.core.datastore.AbstractDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.common.core.seq.nuc.NucleotideSequence;
import org.jcvi.common.core.seq.qual.EncodedQualitySequence;
import org.jcvi.common.core.seq.qual.PhredQuality;
import org.jcvi.common.core.seq.qual.QualitySequence;
import org.jcvi.common.core.util.CloseableIterator;
import org.jcvi.common.core.util.CloseableIteratorAdapter;

/**
 * @author dkatzel
 *
 *
 */
public class DefaultAmosFragmentFileDataStore extends AbstractDataStore<AmosFragment> implements AmosFragmentDataStore, AmosFragmentVisitor{

    Map<String, AmosFragment> map = new HashMap<String, AmosFragment>();

    private String currentId;
    private int currentIndex;
    private NucleotideSequence currentBasecalls;
    private QualitySequence currentQualities;
    private Range validRange, vectorRange, qualityRange;
    
    public DefaultAmosFragmentFileDataStore(File afgFile) throws FileNotFoundException{
        AmosFragmentFileParser.parse(afgFile, this);
    }
    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        super.contains(id);
        return map.containsKey(id);
    }

    @Override
    public synchronized AmosFragment get(String id) throws DataStoreException {
        super.get(id);
        return map.get(id);
    }

    @Override
    public synchronized CloseableIterator<String> getIds() throws DataStoreException {
        super.getIds();
        return CloseableIteratorAdapter.adapt(map.keySet().iterator());
    }

  

    @Override
    public synchronized int size() throws DataStoreException {
        super.size();
        return map.size();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitBasecalls(NucleotideSequence basecalls) {
        this.currentBasecalls = basecalls;
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitClearRange(Range clearRange) {
        this.validRange = clearRange;
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitQualities(List<PhredQuality> qualities) {
        this.currentQualities = new EncodedQualitySequence(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,qualities);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitQualityRange(Range qualityClearRange) {
        qualityRange =qualityClearRange;
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean visitRead(int index, String id) {
        if(currentId !=null){
            addCurrentRecord();
        }
        currentId =id;
        currentIndex = index;
        return true;
    }

    private void addCurrentRecord() {
        map.put(currentId, new DefaultAmosFragment(currentId, currentIndex, 
                this.currentBasecalls, this.currentQualities, validRange, vectorRange, qualityRange));
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitVectorRange(Range vectorClearRange) {
        this.vectorRange = vectorClearRange;
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitLine(String line) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfFile() {
        addCurrentRecord();
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitFile() {
        // TODO Auto-generated method stub
        
    }

}
