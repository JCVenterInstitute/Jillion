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
 * Created on Oct 8, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.math.BigInteger;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.trace.sff.DefaultSffCommonHeader;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultSFFCommonHeader {
    private BigInteger indexOffset=BigInteger.valueOf(100000L);
    private int indexLength = 2000;
    private int numberOfReads = 2000000;
    private short numberOfFlowsPerRead = 400;
    private NucleotideSequence flow = new NucleotideSequenceBuilder("TCAGTCAGTCAG").build();
    private NucleotideSequence keySequence = new NucleotideSequenceBuilder("TCAG").build();

    DefaultSffCommonHeader sut = new DefaultSffCommonHeader(indexOffset,  indexLength,
             numberOfReads,  numberOfFlowsPerRead,  flow,
             keySequence);

    @Test
    public void constructor(){
        assertEquals(indexOffset, sut.getIndexOffset());
        assertEquals(indexLength, sut.getIndexLength() );
        assertEquals(numberOfReads, sut.getNumberOfReads());
        assertEquals(numberOfFlowsPerRead, sut.getNumberOfFlowsPerRead());
        assertEquals(flow , sut.getFlowSequence());
        assertEquals(keySequence , sut.getKeySequence());
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
    public void notEqualsWrongClass(){
        assertFalse(sut.equals("not a DefaultSFFCommonHeader"));
    }

    @Test
    public void equalsSameValues(){
        DefaultSffCommonHeader sameValues = new DefaultSffCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads,  numberOfFlowsPerRead,  flow,
                                    keySequence);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void notEqualsDifferentIndexOffset(){
        DefaultSffCommonHeader differentValues = new DefaultSffCommonHeader(
                                    indexOffset.add(BigInteger.valueOf(1)),  indexLength,
                                    numberOfReads,  numberOfFlowsPerRead,  flow,
                                    keySequence);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsDifferentIndexLength(){
        DefaultSffCommonHeader differentValues = new DefaultSffCommonHeader(
                                    indexOffset,  indexLength+1,
                                    numberOfReads,  numberOfFlowsPerRead,  flow,
                                    keySequence);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsDifferentNumberOfReads(){
        DefaultSffCommonHeader differentValues = new DefaultSffCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads+1,  numberOfFlowsPerRead,  flow,
                                    keySequence);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsDifferentNumberOfFlowsPerReads(){
        DefaultSffCommonHeader differentValues = new DefaultSffCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads,  (short)(numberOfFlowsPerRead+1),  flow,
                                    keySequence);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsDifferentFlow(){
        DefaultSffCommonHeader differentValues = new DefaultSffCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads,  numberOfFlowsPerRead,  new NucleotideSequenceBuilder(flow).append("A").build(),
                                    keySequence);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsNullFlow(){
        DefaultSffCommonHeader differentValues = new DefaultSffCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads,  numberOfFlowsPerRead,  null,
                                    keySequence);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsNullKeySequence(){
        DefaultSffCommonHeader differentValues = new DefaultSffCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads,  numberOfFlowsPerRead,  flow,
                                    null);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsDifferentKeySequence(){
        DefaultSffCommonHeader differentValues = new DefaultSffCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads,  numberOfFlowsPerRead,  flow,
                                    new NucleotideSequenceBuilder(keySequence).append("A").build());
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
}
