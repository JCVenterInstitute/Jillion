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
 * Created on Nov 4, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sff;

import org.jcvi.common.core.seq.trace.sff.SffFlowgram;
import org.jcvi.common.core.seq.trace.sff.SffUtil;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.testUtil.TestUtil;
import org.jcvi.jillion.core.Range;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestSffFlowgram {

    Range qualitiesClip = Range.of(10,90);
    Range adapterClip= Range.of(5,95);
    QualitySequence confidence =  new QualitySequenceBuilder(new byte[]{20,15,30,15}).build();
    short[] values = new short[]{202, 310,1,232,7};
    NucleotideSequence basecalls = new NucleotideSequenceBuilder("ACGT").build();
    String id = "readId";
    SffFlowgram sut;
    @Before
    public void setup(){
         sut = new SffFlowgram(id,basecalls,confidence,values,qualitiesClip, adapterClip);
        
    }

   
    
    @Test
    public void constructor(){
        assertEquals(id, sut.getId());
        assertEquals(basecalls, sut.getNucleotideSequence());
        assertEquals(confidence, sut.getQualitySequence());
        assertEquals(qualitiesClip, sut.getQualityClip());
        assertEquals(adapterClip, sut.getAdapterClip());
        assertEquals(values.length, sut.getNumberOfFlows());
        for(int i=0; i< values.length; i++){
            assertEquals(SffUtil.convertFlowgramValue(values[i]), 
                            sut.getFlowValue(i),0);
        }
    }
    @Test
    public void nullIdShouldthrowNullPointerException(){
        try{
            new SffFlowgram(null,basecalls,confidence,values,qualitiesClip, adapterClip);
            fail("should throw nullPointerException when id is null");
        }
        catch(NullPointerException expected){
            assertEquals("id can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullBasecallsShouldthrowNullPointerException(){
        try{
            new SffFlowgram(id,null,confidence,values,qualitiesClip, adapterClip);
            fail("should throw nullPointerException when basecalls is null");
        }
        catch(NullPointerException expected){
            assertEquals("basecalls can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullQualitiesShouldthrowNullPointerException(){
        try{
            new SffFlowgram(id,basecalls,null,values,qualitiesClip, adapterClip);
            fail("should throw nullPointerException when qualities is null");
        }
        catch(NullPointerException expected){
            assertEquals("qualities can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullValuesShouldthrowNullPointerException(){
        try{
            new SffFlowgram(id,basecalls,confidence,null,qualitiesClip, adapterClip);
            fail("should throw nullPointerException when values is null");
        }
        catch(NullPointerException expected){
            assertEquals("values can not be null", expected.getMessage());
        }
    }
    @Test
    public void emptyValuesShouldthrowIllegalArgumentException(){
        try{
            new SffFlowgram(id,basecalls,confidence,new short[0],qualitiesClip, adapterClip);
            fail("should throw IllegalArgumentException when values is empty");
        }
        catch(IllegalArgumentException expected){
            assertEquals("values can not be empty", expected.getMessage());
        }
    }
    @Test
    public void nullQualitiesClipShouldthrowNullPointerException(){
        try{
            new SffFlowgram(id,basecalls,confidence,values,null, adapterClip);
            fail("should throw nullPointerException when qualitiesClip is null");
        }
        catch(NullPointerException expected){
            assertEquals("qualitiesClip can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullAdapterClipShouldthrowNullPointerException(){
        try{
            new SffFlowgram(id,basecalls,confidence,values,qualitiesClip, null);
            fail("should throw nullPointerException when adapterClip is null");
        }
        catch(NullPointerException expected){
            assertEquals("adapterClip can not be null", expected.getMessage());
        }
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a SFFFlowgram"));
    }
    @Test
    public void equalsSameData(){
        SffFlowgram sameData = new SffFlowgram(id,basecalls,confidence,values,qualitiesClip, adapterClip);
        TestUtil.assertEqualAndHashcodeSame(sut, sameData);
    }
    @Test
    public void notEqualsDifferentValues(){
        SffFlowgram differentValues = new SffFlowgram(id,basecalls,confidence,
                new short[]{1,2,3,4,5,6,7},
                    qualitiesClip, adapterClip);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsValues(){
        SffFlowgram differentValues = new SffFlowgram(id,basecalls,confidence,
                new short[]{1,2,3,4,5,6,7},
                    qualitiesClip, adapterClip);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
}
