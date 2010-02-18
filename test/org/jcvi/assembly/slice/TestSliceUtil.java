/*
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import static org.jcvi.sequence.SequenceDirection.FORWARD;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.SequenceDirection;

public final class TestSliceUtil {
    private TestSliceUtil(){}
    
    public static Slice createIsolatedSliceFrom(String bases, int... qualities){
        List<SliceElement> sliceElements = new ArrayList<SliceElement>();
        for(int i =0; i< qualities.length; i++){
            sliceElements.add(new DefaultSliceElement("read_"+i,NucleotideGlyph.getGlyphFor(bases.charAt(i)), PhredQuality.valueOf(qualities[i]), FORWARD));
        }
        return new DefaultSlice(sliceElements);
    }
    public static Slice createSliceFrom(List<NucleotideGlyph> nucleotides, List<PhredQuality> qualities, List<SequenceDirection> directions){
        List<SliceElement> sliceElements = new ArrayList<SliceElement>();
        for(int i=0; i<nucleotides.size(); i++){
            sliceElements.add(new DefaultSliceElement("read_"+i,nucleotides.get(i), qualities.get(i), directions.get(i)));
        }
        return new DefaultSlice(sliceElements);
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
