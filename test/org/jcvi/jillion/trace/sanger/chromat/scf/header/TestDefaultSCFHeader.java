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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.chromat.scf.header;
import static org.junit.Assert.*;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.trace.sanger.chromat.scf.header.DefaultSCFHeader;
import org.jcvi.jillion.internal.trace.sanger.chromat.scf.header.SCFHeader;
import org.junit.Before;
import org.junit.Test;
public class TestDefaultSCFHeader {

    private int numberOfSamples =100;
    private int sampleOffset=128;
    private int numberOfBases=10;
    private int basesOffset=400;
    private int commentSize=200;
    private int commentOffset=520;
    private float version=3.0F;
    private byte sampleSize=1;
    private int privateDataSize=0;
    private int privateDataOffset=720;
    private DefaultSCFHeader sut;

    @Before
    public void createHeader(){
        sut = new DefaultSCFHeader();
        sut.setNumberOfSamples(numberOfSamples);
        sut.setSampleOffset(sampleOffset);
        sut.setNumberOfBases(numberOfBases);
        sut.setBasesOffset(basesOffset);
        sut.setCommentOffset(commentOffset);
        sut.setCommentSize(commentSize);
        sut.setVersion(version);
        sut.setSampleSize(sampleSize);
        sut.setPrivateDataSize(privateDataSize);
        sut.setPrivateDataOffset(privateDataOffset);
    }

    private SCFHeader copy(SCFHeader headerToBeCopied){
        DefaultSCFHeader result = new DefaultSCFHeader();
        result.setNumberOfSamples(headerToBeCopied.getNumberOfSamples());
        result.setSampleOffset(headerToBeCopied.getSampleOffset());
        result.setNumberOfBases(headerToBeCopied.getNumberOfBases());
        result.setBasesOffset(headerToBeCopied.getBasesOffset());
        result.setCommentOffset(headerToBeCopied.getCommentOffset());
        result.setCommentSize(headerToBeCopied.getCommentSize());
        result.setVersion(headerToBeCopied.getVersion());
        result.setSampleSize(headerToBeCopied.getSampleSize());
        result.setPrivateDataSize(headerToBeCopied.getPrivateDataSize());
        result.setPrivateDataOffset(headerToBeCopied.getPrivateDataOffset());

        return result;
    }
    @Test
    public void emptyConstructor(){
        DefaultSCFHeader empty = new DefaultSCFHeader();
        assertEquals(0, empty.getNumberOfSamples());
        assertEquals(0,empty.getSampleOffset());
        assertEquals(0,empty.getNumberOfBases());
        assertEquals(0,empty.getBasesOffset());
        assertEquals(0,empty.getCommentOffset());
        assertEquals(0,empty.getCommentSize());
        assertEquals(0F, empty.getVersion(),0);
        assertEquals(0,empty.getSampleSize());
        assertEquals(0,empty.getPrivateDataSize());
        assertEquals(0,empty.getPrivateDataOffset());
    }

    @Test
    public void setters(){
        assertEquals(numberOfSamples, sut.getNumberOfSamples());
        assertEquals(sampleOffset,sut.getSampleOffset());
        assertEquals(numberOfBases,sut.getNumberOfBases());
        assertEquals(basesOffset,sut.getBasesOffset());
        assertEquals(commentOffset,sut.getCommentOffset());
        assertEquals(commentSize,sut.getCommentSize());
        assertEquals(version, sut.getVersion(),0);
        assertEquals(sampleSize,sut.getSampleSize());
        assertEquals(privateDataSize,sut.getPrivateDataSize());
        assertEquals(privateDataOffset,sut.getPrivateDataOffset());
    }

    @Test
    public void testCopy(){
        SCFHeader copy = copy(sut);
        assertEquals(numberOfSamples, copy.getNumberOfSamples());
        assertEquals(sampleOffset,copy.getSampleOffset());
        assertEquals(numberOfBases,copy.getNumberOfBases());
        assertEquals(basesOffset,copy.getBasesOffset());
        assertEquals(commentOffset,copy.getCommentOffset());
        assertEquals(commentSize,copy.getCommentSize());
        assertEquals(version, copy.getVersion(),0);
        assertEquals(sampleSize,copy.getSampleSize());
        assertEquals(privateDataSize,copy.getPrivateDataSize());
        assertEquals(privateDataOffset,copy.getPrivateDataOffset());

    }
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }

    @Test
    public void equalsSameValues(){
        TestUtil.assertEqualAndHashcodeSame(sut, copy(sut));
    }

    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a DefaultSCFHeader"));
    }

    @Test
    public void notEqualsDifferentNumberOfSamples(){
        SCFHeader differentNumberOfSamples = copy(sut);
        differentNumberOfSamples.setNumberOfSamples(numberOfSamples-1);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentNumberOfSamples);
    }

    @Test
    public void notEqualsDifferentSampleOffset(){
        SCFHeader differentSampleOffset = copy(sut);
        differentSampleOffset.setSampleOffset(sampleOffset-1);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentSampleOffset);
    }

    @Test
    public void notEqualsDifferentSampleSize(){
        SCFHeader differentSampleSize = copy(sut);
        differentSampleSize.setSampleSize((byte)(sampleSize-1));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentSampleSize);
    }

    @Test
    public void notEqualsDifferentNumberOfBases(){
        SCFHeader differentNumberOfBases = copy(sut);
        differentNumberOfBases.setNumberOfBases(numberOfBases-1);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentNumberOfBases);
    }

    @Test
    public void notEqualsDifferentBasesOffset(){
        SCFHeader differentBasesOffset = copy(sut);
        differentBasesOffset.setBasesOffset(basesOffset-1);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentBasesOffset);
    }

    @Test
    public void notEqualsDifferentCommentOffset(){
        SCFHeader differentCommentOffset = copy(sut);
        differentCommentOffset.setCommentOffset(commentOffset-1);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentCommentOffset);
    }

    @Test
    public void notEqualsDifferentCommentSize(){
        SCFHeader differentCommentSize = copy(sut);
        differentCommentSize.setCommentSize(commentSize-1);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentCommentSize);
    }
    @Test
    public void notEqualsDifferentVersion(){
        SCFHeader differentVersion = copy(sut);
        differentVersion.setVersion(version- 0.2F);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentVersion);
    }
    @Test
    public void notEqualsDifferentPrivateDataOffset(){
        SCFHeader differentPrivateDataOffset = copy(sut);
        differentPrivateDataOffset.setPrivateDataOffset(privateDataOffset-1);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentPrivateDataOffset);
    }

    @Test
    public void notEqualsDifferentPrivateDataSize(){
        SCFHeader differentPrivateDataSize = copy(sut);
        differentPrivateDataSize.setPrivateDataSize(privateDataSize-1);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentPrivateDataSize);
    }
}
