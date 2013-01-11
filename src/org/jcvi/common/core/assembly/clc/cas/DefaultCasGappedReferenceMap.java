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
package org.jcvi.common.core.assembly.clc.cas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.jcvi.common.core.assembly.clc.cas.align.CasAlignment;
import org.jcvi.common.core.assembly.clc.cas.align.CasAlignmentRegion;
import org.jcvi.common.core.assembly.clc.cas.align.CasAlignmentRegionType;
import org.jcvi.common.core.assembly.clc.cas.read.CasNucleotideDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.util.MathUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

/**
 * {@code CasGappedReference} scans a cas file and puts gaps
 * into the reference to align to the underlying sequences 
 * (the reference in the cas file is ungapped). 
 * @author dkatzel
 *
 *
 */
public class DefaultCasGappedReferenceMap extends AbstractOnePassCasFileVisitor implements CasGappedReferenceMap{

   
    private final SortedMap<Long, SortedMap<Long,Insertion>> gapsByReferenceId = new TreeMap<Long, SortedMap<Long,Insertion>>();
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
            
            
            CasAlignment alignment =match.getChosenAlignment();
            Long referenceId = alignment.contigSequenceId();
            if(!gapsByReferenceId.containsKey(referenceId)){
                gapsByReferenceId.put(referenceId, new TreeMap<Long,Insertion>());
            }
            
            List<CasAlignmentRegion> regionsToConsider = getAlignmentRegionsToConsider(alignment);
            boolean outsideValidRange=true;
            long currentOffset = alignment.getStartOfMatch();
            for(CasAlignmentRegion region: regionsToConsider){
            	//1st non insertion type is beginning of where we map
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


	private List<CasAlignmentRegion> getAlignmentRegionsToConsider(
			CasAlignment alignment) {
		List<CasAlignmentRegion> regionsToConsider = new ArrayList<CasAlignmentRegion>(alignment.getAlignmentRegions());
		int lastIndex = regionsToConsider.size()-1;
		//CLC puts 3' unmapped portion of read as an insertion
		if(regionsToConsider.get(lastIndex).getType()==CasAlignmentRegionType.INSERT){
		    regionsToConsider.remove(lastIndex);
		}
		return regionsToConsider;
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
    
                    NucleotideSequence gappedBasecalls = buildGappedReferenceSequence(contigName, gapsByReferenceId.get(i));
                    gappedReferences.put(i, gappedBasecalls);
                    
                } catch (DataStoreException e) {
                    throw new IllegalStateException("could not generate gapped reference for reference " + i,e);
                }
            }        
        }
        gapsByReferenceId.clear();
    }
    
    private NucleotideSequence buildGappedReferenceSequence(String contigName, SortedMap<Long, Insertion> insertions) throws DataStoreException{
        if(insertions ==null){
            return new NucleotideSequenceBuilder().build();
        }
         NucleotideSequence ungappedSequence = referenceNucleotideDataStore.get(contigName);
         if(ungappedSequence==null){
             //invalid contig name?
             throw new IllegalStateException("could not find reference for "+ contigName);
         }
         Iterator<Entry<Long, Insertion>> entryIterator = insertions.entrySet().iterator();
         Entry<Long, Insertion> entry;
         if(entryIterator.hasNext()){
             entry = entryIterator.next();
         }
         else{
             entry =null;
         }
         NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder();
         for(long currentOffset=0; currentOffset<ungappedSequence.getLength(); currentOffset++){
             if(entry !=null && entry.getKey() ==currentOffset){
                 Insertion insertion = entry.getValue();
                 for(int i=0; i< insertion.getSize(); i++){
                     builder.append(Nucleotide.Gap);
                 }
                 
                 if(entryIterator.hasNext()){
                     entry = entryIterator.next();
                 }
                 else{
                     entry =null;
                 }
             }
             builder.append(ungappedSequence.get((int)currentOffset));
         }
         return builder.build();
     }
    
    @Override
    public NucleotideSequence getGappedReferenceFor(long referenceId){
        return gappedReferences.get(referenceId);
    }
    
    public List<NucleotideSequence> getOrderedList(){
    	int size = gappedReferences.size();
    	List<NucleotideSequence> list = new ArrayList<NucleotideSequence>(size);
    	for(long i = 0; i<size; i++){
    		list.add(gappedReferences.get(Long.valueOf(i)));
    	}
    	return Collections.unmodifiableList(list);
    }
    
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

}
