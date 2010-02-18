/*
 * Created on Apr 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.jcvi.assembly.Location;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.slice.ContigSlice;
import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceElement;
import org.jcvi.glyph.nuc.NucleotideGlyph;

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
