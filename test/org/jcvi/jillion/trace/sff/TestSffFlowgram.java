/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Nov 4, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Before;
import org.junit.Test;
public class TestSffFlowgram {

    Range qualitiesClip = Range.of(10,90);
    Range adapterClip= Range.of(5,95);
    QualitySequence confidence =  new QualitySequenceBuilder(new byte[]{20,15,30,15}).build();
    short[] values = new short[]{202, 310,1,232,7};
    NucleotideSequence basecalls = new NucleotideSequenceBuilder("ACGT").build();
    String id = "readId";
    SffFlowgramImpl sut;
    
    byte[] fakeActualFlows = new byte[0];
    short[] fakeActualFlowValues = new short[0];
    @Before
    public void setup(){
         sut = new SffFlowgramImpl(id,basecalls,confidence,values,qualitiesClip, adapterClip, fakeActualFlows, fakeActualFlowValues);
        
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
                            sut.getCalledFlowValue(i),0);
        }
    }
    @Test(expected = NullPointerException.class)
    public void nullRawFlowsShouldThrowNPE(){
        new SffFlowgramImpl(id,basecalls,confidence,values,qualitiesClip, adapterClip, null, fakeActualFlowValues);
       
   }
    
    @Test(expected = NullPointerException.class)
    public void nullRawFlowValuesShouldThrowNPE(){
        new SffFlowgramImpl(id,basecalls,confidence,values,qualitiesClip, adapterClip, fakeActualFlows, null);
       
   }
    
    @Test
    public void nullIdShouldthrowNullPointerException(){
        try{
            new SffFlowgramImpl(null,basecalls,confidence,values,qualitiesClip, adapterClip, fakeActualFlows, fakeActualFlowValues);
            fail("should throw nullPointerException when id is null");
        }
        catch(NullPointerException expected){
            assertEquals("id can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullBasecallsShouldthrowNullPointerException(){
        try{
            new SffFlowgramImpl(id,null,confidence,values,qualitiesClip, adapterClip, fakeActualFlows, fakeActualFlowValues);
            fail("should throw nullPointerException when basecalls is null");
        }
        catch(NullPointerException expected){
            assertEquals("basecalls can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullQualitiesShouldthrowNullPointerException(){
        try{
            new SffFlowgramImpl(id,basecalls,null,values,qualitiesClip, adapterClip, fakeActualFlows, fakeActualFlowValues);
            fail("should throw nullPointerException when qualities is null");
        }
        catch(NullPointerException expected){
            assertEquals("qualities can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullValuesShouldthrowNullPointerException(){
        try{
            new SffFlowgramImpl(id,basecalls,confidence,null,qualitiesClip, adapterClip, fakeActualFlows, fakeActualFlowValues);
            fail("should throw nullPointerException when values is null");
        }
        catch(NullPointerException expected){
            assertEquals("values can not be null", expected.getMessage());
        }
    }
    @Test
    public void emptyValuesShouldthrowIllegalArgumentException(){
        try{
            new SffFlowgramImpl(id,basecalls,confidence,new short[0],qualitiesClip, adapterClip, fakeActualFlows, fakeActualFlowValues);
            fail("should throw IllegalArgumentException when values is empty");
        }
        catch(IllegalArgumentException expected){
            assertEquals("values can not be empty", expected.getMessage());
        }
    }
    @Test
    public void nullQualitiesClipShouldthrowNullPointerException(){
        try{
            new SffFlowgramImpl(id,basecalls,confidence,values,null, adapterClip, fakeActualFlows, fakeActualFlowValues);
            fail("should throw nullPointerException when qualitiesClip is null");
        }
        catch(NullPointerException expected){
            assertEquals("qualitiesClip can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullAdapterClipShouldthrowNullPointerException(){
        try{
            new SffFlowgramImpl(id,basecalls,confidence,values,qualitiesClip, null, fakeActualFlows, fakeActualFlowValues);
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
        SffFlowgramImpl sameData = new SffFlowgramImpl(id,basecalls,confidence,values,qualitiesClip, adapterClip, fakeActualFlows, fakeActualFlowValues);
        TestUtil.assertEqualAndHashcodeSame(sut, sameData);
    }
    @Test
    public void notEqualsDifferentValues(){
        SffFlowgramImpl differentValues = new SffFlowgramImpl(id,basecalls,confidence,
                new short[]{1,2,3,4,5,6,7},
                    qualitiesClip, adapterClip, fakeActualFlows, fakeActualFlowValues);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsValues(){
        SffFlowgramImpl differentValues = new SffFlowgramImpl(id,basecalls,confidence,
                new short[]{1,2,3,4,5,6,7},
                    qualitiesClip, adapterClip, fakeActualFlows, fakeActualFlowValues);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
}
