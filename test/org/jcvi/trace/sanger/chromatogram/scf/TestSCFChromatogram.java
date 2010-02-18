/*
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import java.util.Properties;

import org.jcvi.TestUtil;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Confidence;
import org.jcvi.sequence.DefaultConfidence;
import org.jcvi.sequence.Peaks;
import org.jcvi.trace.sanger.chromatogram.BasicChromatogram;
import org.jcvi.trace.sanger.chromatogram.ChannelGroup;
import org.jcvi.trace.sanger.chromatogram.scf.PrivateData;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramImpl;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;
public class TestSCFChromatogram {


    ChannelGroup mockChannelGroup = createMock(ChannelGroup.class);
    Peaks mockPeaks= createMock(Peaks.class);
    NucleotideEncodedGlyphs basecalls = createMock(NucleotideEncodedGlyphs.class);
    EncodedGlyphs<PhredQuality> qualities = createMock(EncodedGlyphs.class);
    Properties expectedProperties = new Properties();
    Confidence mockInsertionConfidence= createMock(DefaultConfidence.class);
    Confidence mockDeletionConfidence= createMock(DefaultConfidence.class);
    Confidence mockSubstitutionConfidence= createMock(DefaultConfidence.class);
    PrivateData mockPrivateData = createMock(PrivateData.class);

    BasicChromatogram basicChromatogram = new BasicChromatogram(basecalls, qualities,mockPeaks, mockChannelGroup,
            expectedProperties);

    SCFChromatogramImpl sut = new SCFChromatogramImpl(basicChromatogram,
            mockSubstitutionConfidence,
            mockInsertionConfidence,
            mockDeletionConfidence,
            mockPrivateData);


    @Test
    public void constructor(){
        assertEquals(basecalls, sut.getBasecalls());
        assertEquals(mockPeaks, sut.getPeaks());
        assertEquals(mockChannelGroup, sut.getChannelGroup());
        assertEquals(expectedProperties, sut.getProperties());
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
