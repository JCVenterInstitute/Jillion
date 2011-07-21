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

package org.jcvi.common.core.assembly.contig.cas.var;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.assembly.contig.cas.var.DefaultVariation;
import org.jcvi.common.core.assembly.contig.cas.var.Variation.Type;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideGlyph;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultVariation {
    long coordinate = 1234;
    DefaultVariation variation =  new DefaultVariation.Builder(coordinate, 
                                    Type.DIFFERENCE, NucleotideGlyph.Adenine, 
                                    Arrays.asList(NucleotideGlyph.Guanine))
                            .addHistogramRecord(Arrays.asList(NucleotideGlyph.Guanine), 100)
                            .addHistogramRecord(Arrays.asList(NucleotideGlyph.Adenine), 20)
                            .build();
    @Test
    public void nullConsensusInBuilderShouldThrowNPE(){
        try{
            new DefaultVariation.Builder(coordinate, 
                    Type.DIFFERENCE, NucleotideGlyph.Adenine, 
                    null);
            fail("should throw NPE if constructor has a null");
        }catch(NullPointerException e){
            assertEquals("consensus can not be null", e.getMessage());
        }
    }
    @Test
    public void negativeCoordinateInBuilderShouldThrowIllegalArgumentException(){
        try{
            new DefaultVariation.Builder(-1, 
                    Type.DIFFERENCE, NucleotideGlyph.Adenine, 
                    Arrays.asList(NucleotideGlyph.Guanine));
            fail("should throw NPE if constructor has a null");
        }catch(IllegalArgumentException e){
            assertEquals("coordinate can not be <0", e.getMessage());
        }
    }
    
    @Test
    public void nullTypeInBuilderShouldThrowNPE(){
        try{
            new DefaultVariation.Builder(coordinate, 
                    null, NucleotideGlyph.Adenine, 
                    Arrays.asList(NucleotideGlyph.Guanine));
            fail("should throw NPE if constructor has a null");
        }catch(NullPointerException e){
            assertEquals("type can not be null", e.getMessage());
        }
    }
    @Test
    public void emptyConsensusInBuilderShouldThrowNPE(){
        try{
            new DefaultVariation.Builder(coordinate, 
                    Type.DIFFERENCE, NucleotideGlyph.Adenine, 
                    Collections.<NucleotideGlyph>emptyList());
            fail("should throw NPE if constructor has a null");
        }catch(NullPointerException e){
            assertEquals("consensus can not be empty", e.getMessage());
        }
    }
    @Test
    public void nullReferenceInBuilderShouldThrowNPE(){
        try{
            new DefaultVariation.Builder(coordinate, 
                    Type.DIFFERENCE, null, 
                    Arrays.asList(NucleotideGlyph.Guanine));
            fail("should throw NPE if constructor has a null");
        }catch(NullPointerException e){
            assertEquals("reference can not be null", e.getMessage());
        }
    }
    
    @Test
    public void builder(){
        
        
        assertEquals(Arrays.asList(NucleotideGlyph.Guanine),variation.getConsensusBase());
        assertEquals(NucleotideGlyph.Adenine,variation.getReferenceBase());
        assertEquals(coordinate,variation.getCoordinate());
        assertEquals(Type.DIFFERENCE,variation.getType());
        Map<List<NucleotideGlyph>, Integer> actualHistogram =variation.getHistogram();
        Map<List<NucleotideGlyph>, Integer> expectedHistorgram = new HashMap<List<NucleotideGlyph>, Integer>();
        expectedHistorgram.put(Arrays.asList(NucleotideGlyph.Guanine), 100);
        expectedHistorgram.put(Arrays.asList(NucleotideGlyph.Adenine), 20);
        assertEquals(expectedHistorgram, actualHistogram);
        
        String expectedToString = coordinate+" "+Type.DIFFERENCE.toString() + " A -> [G]\tA: 20\tG: 100";
        assertEquals(expectedToString, variation.toString());
    }
    @Test
    public void notEqualToNull(){
        assertFalse(variation.equals(null));
    }
    @Test
    public void notEqualToDifferentClass(){
        assertFalse(variation.equals("not a variation"));
    }
    @Test
    public void sameRefIsEqual(){
        TestUtil.assertEqualAndHashcodeSame(variation, variation);
        assertEquals(0, variation.compareTo(variation));
    }
    @Test
    public void sameValuesAreEqual(){
        DefaultVariation sameValues =  new DefaultVariation.Builder(coordinate, 
                Type.DIFFERENCE, NucleotideGlyph.Adenine, 
                Arrays.asList(NucleotideGlyph.Guanine))
        .addHistogramRecord(Arrays.asList(NucleotideGlyph.Guanine), 100)
        .addHistogramRecord(Arrays.asList(NucleotideGlyph.Adenine), 20)
        .build();
        TestUtil.assertEqualAndHashcodeSame(variation, sameValues);
        assertEquals(0, variation.compareTo(sameValues));
    }
    @Test
    public void greaterCoordinateShouldNotBeEqual(){
        DefaultVariation differentCoordinate =  new DefaultVariation.Builder(coordinate+1, 
                Type.DIFFERENCE, NucleotideGlyph.Adenine, 
                Arrays.asList(NucleotideGlyph.Guanine))
        .addHistogramRecord(Arrays.asList(NucleotideGlyph.Guanine), 100)
        .addHistogramRecord(Arrays.asList(NucleotideGlyph.Adenine), 20)
        .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(variation, differentCoordinate);
    }
    @Test
    public void differentReferenceShouldNotBeEqual(){
        DefaultVariation differentReference =  new DefaultVariation.Builder(coordinate, 
                Type.DIFFERENCE, NucleotideGlyph.Guanine, 
                Arrays.asList(NucleotideGlyph.Guanine))
        .addHistogramRecord(Arrays.asList(NucleotideGlyph.Guanine), 100)
        .addHistogramRecord(Arrays.asList(NucleotideGlyph.Adenine), 20)
        .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(variation, differentReference);
    }
    @Test
    public void differentTypeShouldNotBeEqual(){
        DefaultVariation differentType =  new DefaultVariation.Builder(coordinate, 
                Type.INSERT, NucleotideGlyph.Adenine, 
                Arrays.asList(NucleotideGlyph.Guanine))
        .addHistogramRecord(Arrays.asList(NucleotideGlyph.Guanine), 100)
        .addHistogramRecord(Arrays.asList(NucleotideGlyph.Adenine), 20)
        .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(variation, differentType);
    }
    
    @Test
    public void differentConsensusShouldNotBeEqual(){
        DefaultVariation differentConsensus =  new DefaultVariation.Builder(coordinate, 
                Type.INSERT, NucleotideGlyph.Adenine, 
                Arrays.asList(NucleotideGlyph.Thymine))
        .addHistogramRecord(Arrays.asList(NucleotideGlyph.Guanine), 100)
        .addHistogramRecord(Arrays.asList(NucleotideGlyph.Adenine), 20)
        .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(variation, differentConsensus);
    }
    @Test
    public void differentHistogramShouldNotBeEqual(){
        DefaultVariation differentHistogram =  new DefaultVariation.Builder(coordinate, 
                Type.INSERT, NucleotideGlyph.Adenine, 
                Arrays.asList(NucleotideGlyph.Guanine))
        .addHistogramRecord(Arrays.asList(NucleotideGlyph.Guanine), 120)
        .addHistogramRecord(Arrays.asList(NucleotideGlyph.Adenine), 20)
        .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(variation, differentHistogram);
    }
    @Test
    public void greaterCoordinateShouldNotBeGreater(){
        DefaultVariation differentCoordinate =  new DefaultVariation.Builder(coordinate+1, 
                Type.DIFFERENCE, NucleotideGlyph.Adenine, 
                Arrays.asList(NucleotideGlyph.Guanine))
        .addHistogramRecord(Arrays.asList(NucleotideGlyph.Guanine), 100)
        .addHistogramRecord(Arrays.asList(NucleotideGlyph.Adenine), 20)
        .build();

        assertTrue( variation.compareTo(differentCoordinate) <0);
        assertTrue( differentCoordinate.compareTo(variation) >0);
    }
}
