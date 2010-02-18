/*
 * Created on Nov 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestSFFUtil_getTrimRange {

    private static final Range NOT_SET = Range.buildRange(CoordinateSystem.RESIDUE_BASED,0,0);

    Flowgram flowgram;
    long numberOfBases = 100L;
    NucleotideEncodedGlyphs mockBasecalls;
    @Before
    public void setup(){
        flowgram = createMock(Flowgram.class);
        mockBasecalls = createMock(NucleotideEncodedGlyphs.class);
        expect(mockBasecalls.getLength()).andStubReturn(numberOfBases);
        expect(flowgram.getBasecalls()).andReturn(mockBasecalls);
        replay(mockBasecalls);
    }
    
    @Test
    public void noClipsSetShouldReturn1_NumBases(){
        expect(flowgram.getQualitiesClip()).andReturn(NOT_SET);
        expect(flowgram.getAdapterClip()).andReturn(NOT_SET);
        replay(flowgram);
        Range expectedRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 1,numberOfBases);
        assertEquals(expectedRange, SFFUtil.getTrimRangeFor(flowgram));
        verify(flowgram);
    }
    @Test
    public void onlyQualityClipSetShouldReturnQualityClip(){
        Range qualityClip = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 5,80);
        expect(flowgram.getQualitiesClip()).andReturn(qualityClip);
        expect(flowgram.getAdapterClip()).andReturn(NOT_SET);
        replay(flowgram);
       assertEquals(qualityClip, SFFUtil.getTrimRangeFor(flowgram));
        verify(flowgram);
    }
    @Test
    public void onlyAdapterClipSetShouldReturnAdapterClip(){
        Range adapterClip = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 5,80);
        expect(flowgram.getQualitiesClip()).andReturn(NOT_SET);
        expect(flowgram.getAdapterClip()).andReturn(adapterClip);
        replay(flowgram);
       assertEquals(adapterClip, SFFUtil.getTrimRangeFor(flowgram));
        verify(flowgram);
    }
    @Test
    public void bothClipsSetShouldReturnIntersection(){
        Range qualityClip = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 5,80);
        Range adapterClip = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 30,75);
        expect(flowgram.getQualitiesClip()).andReturn(qualityClip);
        expect(flowgram.getAdapterClip()).andReturn(adapterClip);
        replay(flowgram);
        Range expectedRange = qualityClip.intersection(adapterClip);
       assertEquals(expectedRange, SFFUtil.getTrimRangeFor(flowgram));
        verify(flowgram);
    }
    @Test
    public void adapterOutsideOfQualityClip(){
        Range qualityClip = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 5,80);
        Range adapterClip = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 3,85);
        expect(flowgram.getQualitiesClip()).andReturn(qualityClip);
        expect(flowgram.getAdapterClip()).andReturn(adapterClip);
        replay(flowgram);
        Range expectedRange = qualityClip.intersection(adapterClip);
       assertEquals(expectedRange, SFFUtil.getTrimRangeFor(flowgram));
        verify(flowgram);
    }
}
