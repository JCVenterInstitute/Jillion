/*
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSFFUtil_numberOfIntensities {

    
    @Test
    public void noHomoPolymerRunsshouldReturnNumberOfBases(){
        final List<NucleotideGlyph> oneOfEachBasecall = Arrays.asList(NucleotideGlyph.values());
        assertEquals(oneOfEachBasecall.size(),SFFUtil.numberOfIntensities(oneOfEachBasecall));
    }
    
    @Test
    public void emptyListShouldReturnZero(){
        assertEquals(0, SFFUtil.numberOfIntensities(Collections.<NucleotideGlyph>emptyList()));
    }
    
    @Test
    public void onlyOneHomopolymerRunShouldReturn1(){
        List<NucleotideGlyph> oneHomopolymer = Arrays.asList(NucleotideGlyph.Adenine,NucleotideGlyph.Adenine,NucleotideGlyph.Adenine);
        assertEquals(1,SFFUtil.numberOfIntensities(oneHomopolymer));
    }
    @Test
    public void twoHomopolymerRunsShouldReturn2(){
        List<NucleotideGlyph> twoDifferentBases = Arrays.asList(NucleotideGlyph.Adenine,NucleotideGlyph.Adenine,
                NucleotideGlyph.Cytosine);
        assertEquals(2,SFFUtil.numberOfIntensities(twoDifferentBases));
    }
    
    @Test
    public void threeRunsShouldReturn3(){
        List<NucleotideGlyph> threeRuns = Arrays.asList(NucleotideGlyph.Adenine,NucleotideGlyph.Adenine,
                NucleotideGlyph.Cytosine,
                NucleotideGlyph.Adenine,NucleotideGlyph.Adenine);
        assertEquals(3,SFFUtil.numberOfIntensities(threeRuns));
    }
}
