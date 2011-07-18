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
 * Created on Apr 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceElement;
import org.jcvi.common.core.seq.nuc.NucleotideGlyph;

public class DefaultBasecallCountHistogram implements BasecallCountHistogram {
    private final Slice contigSlice;
    private final Map<NucleotideGlyph, Integer> histogram;
    
    public DefaultBasecallCountHistogram(Slice slice){
        this.contigSlice = slice;
        histogram = generateHistogram(slice);
    }
    private Map<NucleotideGlyph, Integer> generateHistogram(Slice slice) {
        Map<NucleotideGlyph, Integer> histogram = new EnumMap<NucleotideGlyph, Integer>(NucleotideGlyph.class);
        initalizeHistogram(histogram);
        for(SliceElement sliceElement : slice){
            NucleotideGlyph base =sliceElement.getBase();
            histogram.put(base, Integer.valueOf(histogram.get(base) + 1));
        }
        
        return Collections.unmodifiableMap(histogram);
        
    }
    private void initalizeHistogram(Map<NucleotideGlyph, Integer> histogram) {
        final Integer zero = Integer.valueOf(0);
        for(NucleotideGlyph nucelotide :NucleotideGlyph.values()){            
            histogram.put(nucelotide, zero);
        }
        
    }
    @Override
    public Map<NucleotideGlyph, Integer> getHistogram() {
        return histogram;
    }
    @Override
    public Slice getContigSlice() {
        return contigSlice;
    }



}
