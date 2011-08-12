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
 * Created on Dec 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.cas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.jcvi.common.core.assembly.contig.cas.align.CasAlignment;
import org.jcvi.common.core.assembly.contig.cas.align.CasAlignmentRegion;
import org.jcvi.common.core.assembly.contig.cas.align.CasAlignmentRegionType;
import org.jcvi.common.core.assembly.contig.cas.read.CasNucleotideDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.jcvi.common.core.util.MathUtil;

/**
 * {@code CasGappedReference} scans a cas file and puts gaps
 * into the reference to align to the underlying sequences 
 * (the reference in the cas file is ungapped). 
 * @author dkatzel
 *
 *
 */
public class DefaultCasGappedReferenceMap extends AbstractOnePassCasFileVisitor implements CasGappedReferenceMap{

    static class Insertion{
        private long size=0;
        
        public  Insertion(long initialSize){
            this.size = initialSize;
        }
        public void updateSize(long newSize){
            if(newSize > size){
                this.size = newSize;
            }
        }
        public long getSize(){
            return size;
        }
    }
    private final Map<Long, TreeMap<Long,Insertion>> gapsByReferenceId = new TreeMap<Long, TreeMap<Long,Insertion>>();
   private final CasIdLookup contigNameLookup;
    private final CasNucleotideDataStore referenceNucleotideDataStore;
    private final Map<Long, NucleotideSequence> gappedReferences = new TreeMap<Long, NucleotideSequence>();
    /**
     * @param casDataStoreFactory
     */
    public DefaultCasGappedReferenceMap(CasNucleotideDataStore referenceNucleotideDataStore, CasIdLookup contigNameLookup) {
        this.contigNameLookup = contigNameLookup;
        this.referenceNucleotideDataStore = referenceNucleotideDataStore;
    }

   
    @Override
    public synchronized void visitMatch(CasMatch match, long readCounter) {
        if(match.matchReported()){
            boolean outsideValidRange=true;
            
            CasAlignment alignment =match.getChosenAlignment();
            Long referenceId = alignment.contigSequenceId();
            if(!gapsByReferenceId.containsKey(referenceId)){
                gapsByReferenceId.put(referenceId, new TreeMap<Long,Insertion>());
            }
            long currentOffset = alignment.getStartOfMatch();
            
            List<CasAlignmentRegion> regionsToConsider = new ArrayList<CasAlignmentRegion>(alignment.getAlignmentRegions());
            int lastIndex = regionsToConsider.size()-1;
            if(regionsToConsider.get(lastIndex).getType()==CasAlignmentRegionType.INSERT){
                regionsToConsider.remove(lastIndex);
            }
            for(CasAlignmentRegion region: regionsToConsider){
                if(outsideValidRange && region.getType() != CasAlignmentRegionType.INSERT){
                    outsideValidRange=false;
                }
                if(!outsideValidRange){
                    
                    if(region.getType() == CasAlignmentRegionType.INSERT){
                        Map<Long,Insertion> insertions =gapsByReferenceId.get(referenceId);
                        if(insertions.containsKey(currentOffset)){
                            insertions.get(currentOffset).updateSize(region.getLength());
                        }
                        else{
                            insertions.put(currentOffset, new Insertion(region.getLength()));
                        }
                    }else{
                        currentOffset +=region.getLength();
                    }
                }
            }
        }
    }

    @Override
    public synchronized void visitEndOfFile() {   
        super.visitEndOfFile();
        if(!gapsByReferenceId.isEmpty()){
            long maxNumberOfReferences = MathUtil.maxOf(gapsByReferenceId.keySet());
            for(long i=0; i<=maxNumberOfReferences; i++){
                
                String contigName = contigNameLookup.getLookupIdFor(i);
                if(contigName ==null){
                    throw new IllegalStateException(
                            String.format("could not find reference name for reference #%s (max is %d)",i, maxNumberOfReferences));
                }
                try {
    
                    String gappedBasecalls = buildGappedReferenceAsString(contigName, gapsByReferenceId.get(i));
                    gappedReferences.put(i, NucleotideSequenceFactory.create(gappedBasecalls));
                    
                } catch (DataStoreException e) {
                    throw new IllegalStateException("could not generate gapped reference for reference " + i,e);
                }
            }        
        }
        gapsByReferenceId.clear();
    }
    
    private String buildGappedReferenceAsString(String contigName, TreeMap<Long, Insertion> insertions) throws DataStoreException{
       if(insertions ==null){
           return "";
       }
        NucleotideSequence contigBasecalls = referenceNucleotideDataStore.get(contigName);
        if(contigBasecalls==null){
            //invalid contig name?
            throw new IllegalStateException("could not find reference for "+ contigName);
        }
        Iterator<Entry<Long, Insertion>> gapIterator = insertions.entrySet().iterator();
        Entry<Long, Insertion> nextGap;
        if(gapIterator.hasNext()){
            nextGap = gapIterator.next();
        }
        else{
            nextGap =null;
        }
        StringBuilder builder = new StringBuilder();
        for(long currentOffset=0; currentOffset<contigBasecalls.getLength(); currentOffset++){
            if(nextGap !=null && nextGap.getKey() ==currentOffset){
                Insertion insertion = nextGap.getValue();
                for(int i=0; i< insertion.getSize(); i++){
                    builder.append("-");
                }
                
                if(gapIterator.hasNext()){
                    nextGap = gapIterator.next();
                }
                else{
                    nextGap =null;
                }
            }
            builder.append(contigBasecalls.get((int)currentOffset));
        }
        return builder.toString();
    }
    
    
    @Override
    public NucleotideSequence getGappedReferenceFor(long referenceId){
        return gappedReferences.get(referenceId);
    }
    
    public List<NucleotideSequence> asList(){
    	int size = gappedReferences.size();
    	List<NucleotideSequence> list = new ArrayList<NucleotideSequence>(size);
    	for(long i = 0; i<size; i++){
    		list.add(gappedReferences.get(Long.valueOf(i)));
    	}
    	return Collections.unmodifiableList(list);
    }
    
   

}
