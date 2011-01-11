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
package org.jcvi.assembly.cas;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jcvi.assembly.AbstractContigBuilder;
import org.jcvi.assembly.cas.read.CasPlacedRead;
import org.jcvi.assembly.cas.read.DefaultCasPlacedRead;
import org.jcvi.assembly.contig.AbstractContig;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.ReferencedEncodedNucleotideGlyphs;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;

public class DefaultCasContig extends AbstractContig<CasPlacedRead> implements CasContig{

    
    
    /**
     * @param id
     * @param consensus
     * @param placedReads
     */
    public DefaultCasContig(String id, NucleotideEncodedGlyphs consensus,
            Set<CasPlacedRead> placedReads) {
        super(id, consensus, placedReads);
    }

   


    public static class Builder implements org.jcvi.Builder<DefaultCasContig>{

        private final String id;
        private final Set<CasPlacedRead> placedReads = new HashSet<CasPlacedRead>();
        Map<Long, Map<NucleotideGlyph, Integer>> consensusMap = new HashMap<Long,Map<NucleotideGlyph,Integer>>();
        /**
         * @param id
         * @param qualityDataStore
         * @param nucleotideDataStore
         * @param consensusCaller
         */
        public Builder(String id) {
            this.id = id;
        }

      
        public Builder addCasPlacedRead(CasPlacedRead read){
            placedReads.add(read);
            addReadToConsensusMap(read);
            return this;
        }
        private void addReadToConsensusMap(CasPlacedRead casPlacedRead) {
            long startOffset = casPlacedRead.getStart();
            int i=0;
            for(NucleotideGlyph base : casPlacedRead.getEncodedGlyphs().decode()){
                long index = startOffset+i;
                if(!consensusMap.containsKey(index)){
                    consensusMap.put(index, new EnumMap<NucleotideGlyph, Integer>(NucleotideGlyph.class));
                }
                Map<NucleotideGlyph, Integer> histogram =consensusMap.get(index);
                if(!histogram.containsKey(base)){
                    histogram.put(base, Integer.valueOf(1));
                }else{
                    histogram.put(base,Integer.valueOf( histogram.get(base)+1));
                }
                i++;
            }
        }
        @Override
        public DefaultCasContig build() {
            System.out.printf("building cas contig %s%n",id);
            CoverageMap<CoverageRegion<CasPlacedRead>> coverageMap = DefaultCoverageMap.buildCoverageMap(placedReads);
            
            System.out.printf("\tbuilding consensus%n");
            List<NucleotideGlyph> consensus = new ArrayList<NucleotideGlyph>();
            if(coverageMap.getNumberOfRegions()>0){
                long endCoordinate = coverageMap.getLength()-1;
                for(long i=0; i<= endCoordinate; i++){
                    consensus.add(findMostOccuringBase(consensusMap.get(i)));
                }
            }
            System.out.printf("\tbuilding placedReads%n");
            DefaultCasContigBuilder builder = new DefaultCasContigBuilder(id, new DefaultNucleotideEncodedGlyphs(consensus));
            for(CasPlacedRead read : placedReads){
                builder.setCurrentUngappedFullLength(read.getUngappedFullLength());
                
                List<NucleotideGlyph> bases = read.getEncodedGlyphs().decode();           
                builder.addRead(read.getId(), (int)(read.getStart()), read.getValidRange(), 
                        NucleotideGlyph.convertToString(bases), read.getSequenceDirection());
            }
            return builder.build();
        }


        private NucleotideGlyph findMostOccuringBase(Map<NucleotideGlyph, Integer> histogramMap){
            int max=-1;
            NucleotideGlyph mostOccuringBase = NucleotideGlyph.Unknown;
            if(histogramMap !=null){
                for(Entry<NucleotideGlyph, Integer> entry : histogramMap.entrySet()){
                    int value = entry.getValue();
                    if(value > max){
                        max = value;
                        mostOccuringBase = entry.getKey();
                    }
                }
            }
            return mostOccuringBase;
        }
        
    }
    
    private static class DefaultCasContigBuilder extends AbstractContigBuilder<CasPlacedRead, DefaultCasContig>{
        
        
        /**
         * @param id
         * @param consensus
         */
        public DefaultCasContigBuilder(String id,
                NucleotideEncodedGlyphs consensus) {
            super(id, consensus);
        }

        private int currentUngappedFullLength=0;
        
        public void setCurrentUngappedFullLength(int currentUngappedFullLength){
            this.currentUngappedFullLength = currentUngappedFullLength;
        }
        @Override
        protected CasPlacedRead createPlacedRead(
                Read<ReferencedEncodedNucleotideGlyphs> read,
                long offset, SequenceDirection dir) {
            return new DefaultCasPlacedRead(read, offset, 
                    read.getEncodedGlyphs().getValidRange(), 
                    dir, currentUngappedFullLength);
        }

        @Override
        public DefaultCasContig build() {
            return new DefaultCasContig(getId(),getConsensus(),this.getPlacedReads());
        }
    }
}
