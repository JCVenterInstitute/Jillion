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

package org.jcvi.common.core.assembly.clc.cas;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceContigBuilder;
import org.jcvi.common.core.assembly.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.ace.AcePlacedReadBuilder;
import org.jcvi.common.core.assembly.ace.DefaultAceContig;
import org.jcvi.common.core.assembly.ace.PhdInfo;
import org.jcvi.common.core.assembly.util.slice.CompactedSlice;
import org.jcvi.common.core.assembly.util.slice.Slice;
import org.jcvi.common.core.assembly.util.slice.consensus.ConsensusCaller;
import org.jcvi.common.core.assembly.util.slice.consensus.MostFrequentBasecallConsensusCaller;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;

/**
 * {@code UpdateConsensusAceContigBuilder} is an {@link AceContigBuilder}
 * implementation that can recompute the consensus using the
 * <strong>initial</strong> underlying read basecalls.  If reads
 * are modified or shifted after they have been added to this builder
 * then the {@link #updateConsensus()} might generate incorrect
 * consensus.
 * 
 * @author dkatzel
 *
 *
 */
public class UpdateConsensusAceContigBuilder implements AceContigBuilder{

    private final Map<Long, Map<Nucleotide, Integer>> consensusMap;
    private final AceContigBuilder builder;
    private ConsensusCaller consensuCaller = MostFrequentBasecallConsensusCaller.INSTANCE;
    /**
     * @param contigId
     * @param fullConsensus
     */
    public UpdateConsensusAceContigBuilder(String contigId,
            NucleotideSequence fullConsensus) {
        builder = DefaultAceContig.createBuilder(contigId, fullConsensus);
        consensusMap = new HashMap<Long,Map<Nucleotide,Integer>>((int)fullConsensus.getLength()+1, 1F);
        
    }
    public synchronized UpdateConsensusAceContigBuilder consensusCaller(ConsensusCaller consensusCaller){
    	if(consensusCaller ==null){
    		throw new NullPointerException("consensus caller can not be null");
    	}
    	this.consensuCaller = consensusCaller;
    	return this;
    }
    public synchronized void updateConsensus() {
    	if(consensusMap.isEmpty()){
    		return;
    	}
        NucleotideSequenceBuilder consensusBuilder = builder.getConsensusBuilder();
        for(int i=0; i<consensusBuilder.getLength(); i++ )   {
            final Map<Nucleotide, Integer> histogramMap = consensusMap.get(Long.valueOf(i));
            Slice<?> slice = createSliceFor(histogramMap);
            
            consensusBuilder.replace(i,consensuCaller.callConsensus(slice).getConsensus());
        }
        consensusMap.clear();
    }

    @Override
    public synchronized UpdateConsensusAceContigBuilder addRead(AcePlacedRead acePlacedRead) {
        addReadToConsensusMap(acePlacedRead);
        builder.addRead(acePlacedRead);
        return this;
    }

    private synchronized void addReadToConsensusMap(PlacedRead casPlacedRead) {
        long startOffset = casPlacedRead.getStart();
        int i=0;
        for(Nucleotide base : casPlacedRead.getNucleotideSequence().asList()){
            long index = startOffset+i;
            if(!consensusMap.containsKey(index)){
                consensusMap.put(index, new EnumMap<Nucleotide, Integer>(Nucleotide.class));
            }
           
            Map<Nucleotide, Integer> histogram =consensusMap.get(index);
            if(!histogram.containsKey(base)){
                histogram.put(base, Integer.valueOf(1));
            }else{
                histogram.put(base,Integer.valueOf( histogram.get(base)+1));
            }
            i++;
        }
    }
    
    private Slice<?> createSliceFor(Map<Nucleotide, Integer> histogram){
    	CompactedSlice.Builder builder = new CompactedSlice.Builder();
    	PhredQuality qual = PhredQuality.valueOf(30);
    	if(histogram !=null){
	    	int count=0;
	    	for(Entry<Nucleotide, Integer> entry : histogram.entrySet()){
	    		Nucleotide base = entry.getKey();
	    		int max=entry.getValue();
	    		for(int i=0; i<max; i++){
		    		String id = Integer.toString(count);
		    		builder.addSliceElement(id, base, qual , Direction.FORWARD);
		    		count++;    
	    		}
	    	}
    	}
    	return builder.build();
    }
    

    /**
    * {@inheritDoc}
    */
    @Override
    public UpdateConsensusAceContigBuilder setContigId(String contigId) {
        builder.setContigId(contigId);
        return this;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public String getContigId() {
        return builder.getContigId();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int numberOfReads() {
        return builder.numberOfReads();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public UpdateConsensusAceContigBuilder addAllReads(
            Iterable<AcePlacedRead> reads) {
    	for(AcePlacedRead read : reads){
    		addRead(read);
    	}
    	return this;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void removeRead(String readId) {
        builder.removeRead(readId);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideSequenceBuilder getConsensusBuilder() {        
        return builder.getConsensusBuilder();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public AceContig build() {
        this.updateConsensus();
        return builder.build();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public UpdateConsensusAceContigBuilder addRead(String readId, String validBases,
            int offset, Direction dir, Range clearRange, PhdInfo phdInfo,
            int ungappedFullLength) {
        builder.addRead(readId, validBases, offset, dir, clearRange, phdInfo, ungappedFullLength);
        return this;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public AcePlacedReadBuilder getPlacedReadBuilder(String readId) {
        return builder.getPlacedReadBuilder(readId);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Collection<AcePlacedReadBuilder> getAllPlacedReadBuilders() {
        return builder.getAllPlacedReadBuilders();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public UpdateConsensusAceContigBuilder setComplimented(boolean complimented) {
        builder.setComplimented(complimented);
        return this;
    }
    
    
}
