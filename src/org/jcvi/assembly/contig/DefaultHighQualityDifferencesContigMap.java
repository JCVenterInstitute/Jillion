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
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.DefaultLocation;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.Sequence;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;


public class DefaultHighQualityDifferencesContigMap implements HighQualityDifferencesContigMap{
    private final PhredQuality qualityThreshold;
    private final Contig<? extends PlacedRead> contig;
    private final QualityDataStore qualityDataStore;
    private final Map<PlacedRead, List<DefaultQualityDifference>> highQualityDifferenceMap;
    private final QualityValueStrategy qualityValueStrategy;
    
    public DefaultHighQualityDifferencesContigMap(Contig<? extends PlacedRead> contig, QualityDataStore qualityDataStore, QualityValueStrategy qualityValueStrategy,PhredQuality qualityThreshold) throws DataStoreException{
        this.qualityThreshold = qualityThreshold;
        this.contig = contig;
        this.qualityDataStore = qualityDataStore;
        this.qualityValueStrategy = qualityValueStrategy;
        highQualityDifferenceMap = new HashMap<PlacedRead, List<DefaultQualityDifference>>();
        populateHightQualityDifferenceMap();
    }



    private void populateHightQualityDifferenceMap() throws DataStoreException {
        for(PlacedRead placedRead : contig.getPlacedReads()){
            List<DefaultQualityDifference> qualityDifferences = buildHighQualityDifferenceList(placedRead);
            if(!qualityDifferences.isEmpty()){
                highQualityDifferenceMap.put(placedRead, qualityDifferences);
            }
        }
    }



    private List<DefaultQualityDifference> buildHighQualityDifferenceList(PlacedRead placedRead) throws DataStoreException {
        List<DefaultQualityDifference> qualityDifferences =new ArrayList<DefaultQualityDifference>();  
        Sequence<PhredQuality> fullQualities =qualityDataStore.get(placedRead.getId());
            if(fullQualities !=null){
            for(Entry<Integer, NucleotideGlyph> snp : placedRead.getSnps().entrySet()){            
                Integer gappedIndex =snp.getKey();
                PhredQuality qualityValue =qualityValueStrategy.getQualityFor(placedRead, fullQualities, gappedIndex);
                
               
                    if(qualityValue.compareTo(qualityThreshold) >= 0){
                        DefaultQualityDifference diff = createNewQualityDifference(placedRead, gappedIndex, qualityValue);
                        qualityDifferences.add(diff);                    
                    }
            }
        }
        return Collections.unmodifiableList(qualityDifferences);
    }



    



    private DefaultQualityDifference createNewQualityDifference(PlacedRead placedRead,
            int gappedIndex, PhredQuality qual) {
        int consensusOffset = (int)(placedRead.getStart()+gappedIndex);
        DefaultQualityDifference diff= new DefaultQualityDifference(
                            new DefaultLocation<Sequence<NucleotideGlyph>>(contig.getConsensus(), consensusOffset),
                            new DefaultLocation<PlacedRead>(placedRead, gappedIndex),
                            qual);
        return diff;
    }
    
    
    @Override
    public final PhredQuality getQualityThreshold() {
        return qualityThreshold;
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public List<DefaultQualityDifference> getHighQualityDifferencesFor(PlacedRead placedRead){
        if(highQualityDifferenceMap.containsKey(placedRead)){
            return highQualityDifferenceMap.get(placedRead);
        }
        return Collections.<DefaultQualityDifference>emptyList();
    }
    @Override
    public Iterator<List<DefaultQualityDifference>> iterator(){
        return highQualityDifferenceMap.values().iterator();
    }

    @Override
    public int getNumberOfReadsWithHighQualityDifferences(){
        return highQualityDifferenceMap.size();
    }
    @Override
    public Set<Entry<PlacedRead,List<DefaultQualityDifference>>> entrySet(){
        return highQualityDifferenceMap.entrySet();
    }

   
}
