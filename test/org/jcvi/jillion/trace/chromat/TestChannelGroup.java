/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.trace.chromat.DefaultChannel;
import org.jcvi.jillion.internal.trace.chromat.DefaultChannelGroup;
import org.jcvi.jillion.trace.chromat.ChannelGroup;
import org.junit.Test;
public class TestChannelGroup {


    private byte[] differentLengthQualities = new byte[]{40,40,40};
    private short[] differentLengthPositions = new short[]{100,200,300};

    private DefaultChannel aChannel = new DefaultChannel(new byte[]{20,20,20,20},
                                            new short[]{10,20,30,40});

    private DefaultChannel cChannel = new DefaultChannel(new byte[]{22,22,22,22},
                                            new short[]{11,21,31,41});

    private DefaultChannel gChannel = new DefaultChannel(new byte[]{30,30,30,30},
                                new short[]{15,25,35,45});

    private DefaultChannel tChannel = new DefaultChannel(new byte[]{40,40,40,40},
                                new short[]{17,27,37,47});

    private ChannelGroup sut = new DefaultChannelGroup(aChannel, cChannel, gChannel, tChannel);

    @Test
    public void constructor(){
        assertEquals(aChannel, sut.getAChannel());
        assertEquals(cChannel, sut.getCChannel());
        assertEquals(gChannel, sut.getGChannel());
        assertEquals(tChannel, sut.getTChannel());
    }

    @Test
    public void getAChannel(){
    	assertEquals(aChannel, sut.getChannel(Nucleotide.Adenine));
    }
    
    @Test
    public void getCChannel(){
    	assertEquals(cChannel, sut.getChannel(Nucleotide.Cytosine));
    }
    @Test
    public void getGChannel(){
    	assertEquals(gChannel, sut.getChannel(Nucleotide.Guanine));
    }
    @Test
    public void getTChannel(){
    	assertEquals(tChannel, sut.getChannel(Nucleotide.Thymine));
    }
    
    @Test(expected = NullPointerException.class)
    public void passingNullToGetChannelShouldThrowNPE(){
    	sut.getChannel(null);
    }
    
    @Test
    public void passingAmbiguousBaseShouldReturnTChannel(){
    	for(Nucleotide n : Nucleotide.values()){
    		if(n.isAmbiguity()){
    			assertEquals(n.name(),tChannel, sut.getChannel(n));
    		}
    	}
    }
    @Test
    public void nullAChannelShouldThrowNullPointerException(){
        try{
            new DefaultChannelGroup(null, cChannel, gChannel, tChannel);
            fail("should throw NullPointerException when a channel is null");
        }
        catch(NullPointerException expected){
            assertEquals("channels can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullCChannelShouldThrowNullPointerException(){
        try{
            new DefaultChannelGroup(aChannel, null, gChannel, tChannel);
            fail("should throw NullPointerException when c channel is null");
        }
        catch(NullPointerException expected){
            assertEquals("channels can not be null", expected.getMessage());
        }
    }

    @Test
    public void nullGChannelShouldThrowNullPointerException(){
        try{
            new DefaultChannelGroup(aChannel, cChannel, null, tChannel);
            fail("should throw NullPointerException when g channel is null");
        }
        catch(NullPointerException expected){
            assertEquals("channels can not be null", expected.getMessage());
        }
    }

    @Test
    public void nullTChannelShouldThrowNullPointerException(){
        try{
            new DefaultChannelGroup(aChannel, cChannel, gChannel, null);
            fail("should throw NullPointerException when t channel is null");
        }
        catch(NullPointerException expected){
            assertEquals("channels can not be null", expected.getMessage());
        }
    }

    @Test
    public void AConfidenceDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            DefaultChannel differentConfidenceLength = new DefaultChannel(new QualitySequenceBuilder(differentLengthQualities).build(),
                                        aChannel.getPositions());
            new DefaultChannelGroup(differentConfidenceLength, cChannel, gChannel, tChannel);
            fail("should throw IllegalArgumentException when a channel confidence is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("confidences must all have the same length", expected.getMessage());
        }
    }

    @Test
    public void CConfidenceDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            DefaultChannel differentConfidenceLength = new DefaultChannel(
            		new QualitySequenceBuilder(differentLengthQualities).build(),
                                        aChannel.getPositions());
            new DefaultChannelGroup(aChannel, differentConfidenceLength, gChannel, tChannel);
            fail("should throw IllegalArgumentException when a channel confidence is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("confidences must all have the same length", expected.getMessage());
        }
    }

    @Test
    public void GConfidenceDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            DefaultChannel differentConfidenceLength = new DefaultChannel(new QualitySequenceBuilder(differentLengthQualities).build(),
                                        aChannel.getPositions());
            new DefaultChannelGroup(aChannel, cChannel, differentConfidenceLength, tChannel);
            fail("should throw IllegalArgumentException when a channel confidence is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("confidences must all have the same length", expected.getMessage());
        }
    }

    @Test
    public void TConfidenceDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            DefaultChannel differentConfidenceLength = new DefaultChannel(new QualitySequenceBuilder(differentLengthQualities).build(),
                                        aChannel.getPositions());
            new DefaultChannelGroup(aChannel, cChannel, gChannel, differentConfidenceLength);
            fail("should throw IllegalArgumentException when a channel confidence is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("confidences must all have the same length", expected.getMessage());
        }
    }
    @Test
    public void APositionDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            DefaultChannel differentConfidenceLength = new DefaultChannel(aChannel.getConfidence(),
                                                new PositionSequenceBuilder(differentLengthPositions).build());
            new DefaultChannelGroup(differentConfidenceLength, cChannel, gChannel, tChannel);
            fail("should throw IllegalArgumentException when a channel position is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("positions must all have the same length", expected.getMessage());
        }
    }
    @Test
    public void CPositionDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            DefaultChannel differentConfidenceLength = new DefaultChannel(aChannel.getConfidence(),
            		 new PositionSequenceBuilder(differentLengthPositions).build());
            new DefaultChannelGroup(aChannel,differentConfidenceLength, gChannel, tChannel);
            fail("should throw IllegalArgumentException when a channel position is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("positions must all have the same length", expected.getMessage());
        }
    }

    @Test
    public void GPositionDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            DefaultChannel differentConfidenceLength = new DefaultChannel(aChannel.getConfidence(),
            		 new PositionSequenceBuilder(differentLengthPositions).build());
            new DefaultChannelGroup(aChannel,gChannel,differentConfidenceLength, tChannel);
            fail("should throw IllegalArgumentException when a channel position is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("positions must all have the same length", expected.getMessage());
        }
    }


    @Test
    public void TPositionDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            DefaultChannel differentConfidenceLength = new DefaultChannel(aChannel.getConfidence(),
            		 new PositionSequenceBuilder(differentLengthPositions).build());
            new DefaultChannelGroup(aChannel, cChannel, gChannel, differentConfidenceLength);
            fail("should throw IllegalArgumentException when a channel position is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("positions must all have the same length", expected.getMessage());
        }
    }


    @Test
    public void testEqualsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }

    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }

    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a ChannelGroup"));
    }

    @Test
    public void equalsSameValues(){
        ChannelGroup sameValues = new DefaultChannelGroup(aChannel, cChannel, gChannel, tChannel);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }



    @Test
    public void notEqualsDifferentAChannel(){
        ChannelGroup differentAChannel = createGroupWithDifferentAChannel();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentAChannel);
    }

    @Test
    public void notEqualsDifferentCChannel(){
        ChannelGroup differentCChannel = createGroupWithDifferentCChannel();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentCChannel);
    }

    @Test
    public void notEqualsDifferentGChannel(){
        ChannelGroup differentGChannel = createGroupWithDifferentGChannel();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentGChannel);
    }

    @Test
    public void notEqualsDifferentTChannel(){
        ChannelGroup differentTChannel = createGroupWithDifferentTChannel();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentTChannel);
    }

    private ChannelGroup createGroupWithDifferentAChannel() {
        return new DefaultChannelGroup(cChannel, cChannel, gChannel, tChannel);
    }

    private ChannelGroup createGroupWithDifferentCChannel() {
        return new DefaultChannelGroup(aChannel, aChannel, gChannel, tChannel);
    }

    private ChannelGroup createGroupWithDifferentGChannel() {
        return new DefaultChannelGroup(aChannel, cChannel, aChannel, tChannel);
    }

    private ChannelGroup createGroupWithDifferentTChannel() {
        return new DefaultChannelGroup(aChannel, cChannel, gChannel, aChannel);
    }
}
