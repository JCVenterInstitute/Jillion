/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 26, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat;
import static org.junit.Assert.*;

import org.jcvi.jillion.core.pos.Position;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.trace.chromat.DefaultChannel;
import org.jcvi.jillion.trace.chromat.Channel;
import org.junit.Test;

public class TestChannel {

    private short[] positions = new short[]{13,14,15,18,20,15,11,4,0};

    private byte[] qualities = new byte[]{10,12,14,15,20,20,20,20,20};
    QualitySequence qualitySequence = new QualitySequenceBuilder(qualities).build();
    PositionSequence positionSequence = new PositionSequenceBuilder(positions).build();
    Channel sut = new DefaultChannel(qualities, positions);


    @Test
    public void arrayConstructor(){
        assertEquals(qualitySequence, sut.getQualitySequence());
        assertEquals(positionSequence, sut.getPositionSequence());
    }

    @Test
    public void sequenceConstructor(){
    	 Channel channel = new DefaultChannel(qualitySequence, positionSequence);
    	 assertEquals(qualitySequence, channel.getQualitySequence());
         assertEquals(positionSequence, channel.getPositionSequence());
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
        assertFalse(sut.equals("not a channel"));
    }
    @Test
    public void equalsSameValues(){
        Channel sameValues = new DefaultChannel(
        		new QualitySequenceBuilder(qualitySequence).build(), 
        		new PositionSequenceBuilder(positions).build());
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void notEqualsDifferentConfidence(){
        Channel hasDifferentConfidence = new DefaultChannel(
        		new QualitySequenceBuilder(qualitySequence)
        				.replace(2, PhredQuality.valueOf(99))
        				.build(),
        		 positionSequence);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentConfidence);
    }
    @Test
    public void notEqualsDifferentPositions(){
        Channel hasDifferentConfidence = new DefaultChannel(
        		qualitySequence,
        		
        		new PositionSequenceBuilder(positionSequence)
        		.replace(3, Position.valueOf(Short.MAX_VALUE))
        		.build());
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentConfidence);
    }
    @Test(expected = NullPointerException.class)
    public void nullQualitySequenceShouldthrowNPE(){
        new DefaultChannel(null, positionSequence);
    }
    @Test(expected = NullPointerException.class)
    public void nullPositionSequenceShouldthrowNPE(){
        new DefaultChannel(qualitySequence, null);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullQualityArrayShouldthrowNPE(){
        new DefaultChannel(null, positions);
    }
    @Test(expected = NullPointerException.class)
    public void nullPositionArrayShouldthrowNPE(){
        new DefaultChannel(qualities, null);
    }
   
   
}
