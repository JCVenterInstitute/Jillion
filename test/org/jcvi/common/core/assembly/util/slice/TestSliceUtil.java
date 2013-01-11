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
package org.jcvi.common.core.assembly.util.slice;

import static org.jcvi.jillion.core.Direction.FORWARD;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.assembly.util.slice.DefaultSlice;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public final class TestSliceUtil {
    private TestSliceUtil(){}
    
    public static IdedSlice createIsolatedSliceFrom(String bases, int... qualities){
        DefaultSlice.Builder builder = new DefaultSlice.Builder();
        for(int i =0; i< qualities.length; i++){
            builder.add("read_"+i,Nucleotide.parse(bases.charAt(i)), PhredQuality.valueOf(qualities[i]), FORWARD);
        }
        return builder.build();
    }
    public static IdedSlice createSliceFrom(List<Nucleotide> nucleotides, List<PhredQuality> qualities, List<Direction> directions){
        DefaultSlice.Builder builder = new DefaultSlice.Builder();
        for(int i=0; i<nucleotides.size(); i++){
            builder.add("read_"+i,nucleotides.get(i), qualities.get(i), directions.get(i));
        }
        return builder.build();
    }
    public static IdedSlice createSliceFrom(String nucleotides, byte[] qualities, List<Direction> directions){
        
        return createSliceFrom(asList(new NucleotideSequenceBuilder(nucleotides)),
                PhredQuality.valueOf(qualities), directions);
    }
    private static List<Nucleotide> asList(NucleotideSequenceBuilder builder){
    	List<Nucleotide> list = new ArrayList<Nucleotide>((int)builder.getLength());
    	for(Nucleotide n : builder){
    		list.add(n);
    	}
    	return list;
    }
    public static List<IdedSlice> createSlicesFrom(List<String> nucleotides, byte[][] qualities, List<Direction> directions){
        List<IdedSlice> slices = new ArrayList<IdedSlice>();
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
