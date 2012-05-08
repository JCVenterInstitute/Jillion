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
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.common.core.seq.read.trace.sanger.chromat.BasicChromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChannelGroup;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.Confidence;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.DefaultConfidence;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramImpl;
import org.jcvi.common.core.symbol.pos.SangerPeak;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestSCFChromatogram {


    ChannelGroup mockChannelGroup = createMock(ChannelGroup.class);
    SangerPeak mockPeaks= createMock(SangerPeak.class);
    NucleotideSequence basecalls = createMock(NucleotideSequence.class);
    QualitySequence qualities = createMock(QualitySequence.class);
    Map<String,String> expectedProperties = new HashMap<String, String>();
    Confidence mockInsertionConfidence= createMock(DefaultConfidence.class);
    Confidence mockDeletionConfidence= createMock(DefaultConfidence.class);
    Confidence mockSubstitutionConfidence= createMock(DefaultConfidence.class);
    PrivateData mockPrivateData = createMock(PrivateData.class);

    BasicChromatogram basicChromatogram = new BasicChromatogram("id",basecalls, qualities,mockPeaks, mockChannelGroup,
            expectedProperties);

    SCFChromatogramImpl sut = new SCFChromatogramImpl(basicChromatogram,
            mockSubstitutionConfidence,
            mockInsertionConfidence,
            mockDeletionConfidence,
            mockPrivateData);


    @Test
    public void constructor(){
        assertEquals(basecalls, sut.getNucleotideSequence());
        assertEquals(mockPeaks, sut.getPeaks());
        assertEquals(mockChannelGroup, sut.getChannelGroup());
        assertEquals(expectedProperties, sut.getComments());
        assertEquals(mockInsertionConfidence,sut.getInsertionConfidence());
        assertEquals(mockDeletionConfidence, sut.getDeletionConfidence());
        assertEquals(mockSubstitutionConfidence, sut.getSubstitutionConfidence());
        assertEquals(mockPrivateData, sut.getPrivateData());
        assertEquals(qualities, sut.getQualities());
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
        assertFalse(sut.equals("not a chromatogram"));
    }

    @Test
    public void notEqualsBasicChromatogram(){
        assertFalse(sut.equals(basicChromatogram));
        assertTrue(sut.hashCode() != basicChromatogram.hashCode());
    }

    @Test
    public void equalsSameValues(){
        SCFChromatogramImpl sameValues = new SCFChromatogramImpl(basicChromatogram,
                mockSubstitutionConfidence,
                mockInsertionConfidence,
                mockDeletionConfidence,
                mockPrivateData);

        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void notEqualsNullSubstitution(){
        SCFChromatogramImpl hasNullSubstitution = new SCFChromatogramImpl(basicChromatogram,
                null,
                mockInsertionConfidence,
                mockDeletionConfidence,
                mockPrivateData);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasNullSubstitution);
    }

    @Test
    public void notEqualsDifferentSubstitution(){
        Confidence differentSub = createMock(DefaultConfidence.class);
        SCFChromatogramImpl hasDifferentSubstitution = new SCFChromatogramImpl(basicChromatogram,
                differentSub,
                mockInsertionConfidence,
                mockDeletionConfidence,
                mockPrivateData);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentSubstitution);
    }

    @Test
    public void notEqualsNullInsertion(){
        SCFChromatogramImpl hasNullInsertion = new SCFChromatogramImpl(basicChromatogram,
                mockSubstitutionConfidence,
                null,
                mockDeletionConfidence,
                mockPrivateData);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasNullInsertion);
    }

    @Test
    public void notEqualsDifferentInsertion(){
        Confidence differentInsertion = createMock(DefaultConfidence.class);
        SCFChromatogramImpl hasDifferentInsertion = new SCFChromatogramImpl(basicChromatogram,
                mockSubstitutionConfidence,
                differentInsertion,
                mockDeletionConfidence,
                mockPrivateData);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentInsertion);
    }

    @Test
    public void notEqualsNullDeletion(){
        SCFChromatogramImpl hasNullDeletion = new SCFChromatogramImpl(basicChromatogram,
                mockSubstitutionConfidence,
                mockInsertionConfidence,
                null,
                mockPrivateData);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasNullDeletion);
    }

    @Test
    public void notEqualsDifferentDeletion(){
        Confidence differentDeletion = createMock(DefaultConfidence.class);
        SCFChromatogramImpl hasDifferentDeletion = new SCFChromatogramImpl(basicChromatogram,
                mockSubstitutionConfidence,
                mockInsertionConfidence,
                differentDeletion,
                mockPrivateData);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentDeletion);
    }

    @Test
    public void notEqualsNullPrivateData(){
        SCFChromatogramImpl hasNullPrivateData = new SCFChromatogramImpl(basicChromatogram,
                mockSubstitutionConfidence,
                mockInsertionConfidence,
                mockDeletionConfidence,
                null);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasNullPrivateData);
    }

    @Test
    public void notEqualsDifferentPrivateData(){
    	PrivateData differentPrivateData = createMock(PrivateData.class);
        SCFChromatogramImpl hasDifferentPrivateData = new SCFChromatogramImpl(basicChromatogram,
                mockSubstitutionConfidence,
                mockInsertionConfidence,
                mockDeletionConfidence,
                differentPrivateData);

        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentPrivateData);
    }

}
