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

package org.jcvi.common.core.assembly.contig.cas;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.ace.AceContig;
import org.jcvi.common.core.assembly.contig.ace.AceContigBuilder;
import org.jcvi.common.core.assembly.contig.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.contig.ace.AcePlacedReadBuilder;
import org.jcvi.common.core.assembly.contig.ace.DefaultAceContig;
import org.jcvi.common.core.assembly.contig.ace.PhdInfo;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;

/**
 * @author dkatzel
 *
 *
 */
public class UpdateConsensusAceContigBuilder implements AceContigBuilder{

    private final Map<Long, Map<Nucleotide, Integer>> consensusMap;
    private final AceContigBuilder builder;
    /**
     * @param contigId
     * @param fullConsensus
     */
    public UpdateConsensusAceContigBuilder(String contigId,
            NucleotideSequence fullConsensus) {
        builder = DefaultAceContig.createBuilder(contigId, fullConsensus);
        consensusMap = new HashMap<Long,Map<Nucleotide,Integer>>((int)fullConsensus.getLength()+1, 1F);
        
    }

    public void updateConsensus() {
        NucleotideSequenceBuilder consensusBuilder = builder.getConsensusBuilder();
        for(int i=0; i<consensusBuilder.getLength(); i++ )   {
            final Map<Nucleotide, Integer> histogramMap = consensusMap.get(Long.valueOf(i));
            consensusBuilder.replace(i,findMostOccuringBase(histogramMap));
        }
        consensusMap.clear();
    }

    @Override
    public UpdateConsensusAceContigBuilder addRead(AcePlacedRead acePlacedRead) {
        addReadToConsensusMap(acePlacedRead);
        builder.addRead(acePlacedRead);
        return this;
    }

    private void addReadToConsensusMap(PlacedRead casPlacedRead) {
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
    private Nucleotide findMostOccuringBase(Map<Nucleotide, Integer> histogramMap){
        int max=-1;
        Nucleotide mostOccuringBase = Nucleotide.Unknown;
        if(histogramMap !=null){
            for(Entry<Nucleotide, Integer> entry : histogramMap.entrySet()){
                int value = entry.getValue();
                if(value > max){
                    max = value;
                    mostOccuringBase = entry.getKey();
                }
            }
        }
        return mostOccuringBase;
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
        builder.addAllReads(reads);
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
