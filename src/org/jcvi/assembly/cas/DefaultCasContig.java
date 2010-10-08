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

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.DefaultContig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.assembly.cas.read.CasPlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public class DefaultCasContig implements CasContig{

    private final Contig<PlacedRead> delegate;
    
    
    /**
     * @param delegate
     */
    private DefaultCasContig(Contig<PlacedRead> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean containsPlacedRead(String placedReadId) {
        return delegate.containsPlacedRead(placedReadId);
    }

    @Override
    public NucleotideEncodedGlyphs getConsensus() {
        return delegate.getConsensus();
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public int getNumberOfReads() {
        return delegate.getNumberOfReads();
    }

    @Override
    public VirtualPlacedRead<PlacedRead> getPlacedReadById(String id) {
        return delegate.getPlacedReadById(id);
    }

    @Override
    public Set<PlacedRead> getPlacedReads() {
        return delegate.getPlacedReads();
    }

    @Override
    public Set<VirtualPlacedRead<PlacedRead>> getVirtualPlacedReads() {
        return delegate.getVirtualPlacedReads();
    }

    @Override
    public boolean isCircular() {
        return delegate.isCircular();
    }

    public static class Builder implements org.jcvi.Builder<DefaultCasContig>{

        private final String id;
        private final Set<PlacedRead> placedReads = new HashSet<PlacedRead>();
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
            CoverageMap<CoverageRegion<PlacedRead>> coverageMap = DefaultCoverageMap.buildCoverageMap(placedReads);
            
            System.out.printf("\tbuilding consensus%n");
            List<NucleotideGlyph> consensus = new ArrayList<NucleotideGlyph>();
            if(coverageMap.getNumberOfRegions()>0){
                long endCoordinate = coverageMap.getLength()-1;
                for(long i=0; i<= endCoordinate; i++){
                    consensus.add(findMostOccuringBase(consensusMap.get(i)));
                }
            }
            System.out.printf("\tbuilding placedReads%n");
            DefaultContig.Builder builder = new DefaultContig.Builder(id, new DefaultNucleotideEncodedGlyphs(consensus));
            for(PlacedRead read : placedReads){
                List<NucleotideGlyph> bases = read.getEncodedGlyphs().decode();           
                builder.addRead(read.getId(), (int)(read.getStart()), read.getValidRange(), 
                        NucleotideGlyph.convertToString(bases), read.getSequenceDirection());
            }
            return new DefaultCasContig(builder.build());
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
}
