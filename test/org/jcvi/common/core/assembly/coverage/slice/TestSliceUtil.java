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
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.coverage.slice;

import static org.jcvi.common.core.seq.read.SequenceDirection.FORWARD;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.assembly.contig.slice.DefaultSlice;
import org.jcvi.common.core.assembly.contig.slice.Slice;
import org.jcvi.common.core.seq.read.SequenceDirection;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideGlyph;

public final class TestSliceUtil {
    private TestSliceUtil(){}
    
    public static Slice createIsolatedSliceFrom(String bases, int... qualities){
        DefaultSlice.Builder builder = new DefaultSlice.Builder();
        for(int i =0; i< qualities.length; i++){
            builder.add("read_"+i,NucleotideGlyph.getGlyphFor(bases.charAt(i)), PhredQuality.valueOf(qualities[i]), FORWARD);
        }
        return builder.build();
    }
    public static Slice createSliceFrom(List<NucleotideGlyph> nucleotides, List<PhredQuality> qualities, List<SequenceDirection> directions){
        DefaultSlice.Builder builder = new DefaultSlice.Builder();
        for(int i=0; i<nucleotides.size(); i++){
            builder.add("read_"+i,nucleotides.get(i), qualities.get(i), directions.get(i));
        }
        return builder.build();
    }
    public static Slice createSliceFrom(String nucleotides, byte[] qualities, List<SequenceDirection> directions){
        
        return createSliceFrom(NucleotideGlyph.getGlyphsFor(nucleotides),
                PhredQuality.valueOf(qualities), directions);
    }
    
    public static List<Slice> createSlicesFrom(List<String> nucleotides, byte[][] qualities, List<SequenceDirection> directions){
        List<Slice> slices = new ArrayList<Slice>();
        for(int j=0; j< nucleotides.get(0).length(); j++){
            StringBuilder sliceBases = new StringBuilder();
            byte[] sliceQualities = new byte[nucleotides.size()];
            for(int i=0; i< nucleotides.size(); i++){
                sliceBases.append(nucleotides.get(i).charAt(j));
                sliceQualities[i] = qualities[i][j];                
            }
            slices.add(createSliceFrom(sliceBases.toString(), sliceQualities, directions));
        }
           
        return slices;
    }
}
