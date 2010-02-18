/*
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;



import java.io.IOException;

import org.jcvi.trace.sanger.chromatogram.Channel;
import org.jcvi.trace.sanger.chromatogram.ChannelGroup;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.section.AbstractSampleSectionCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.EncodedSection;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

public abstract class AbstractTestSamplesSection {
    protected  short[] aSamplesAsShorts = new short[]{200,-199,0,0};
    protected short[] aSamplesAsBytes = new short[]{13,14,15,16};
    protected short[] cSamples = new short[]{0,10,30,50};
    protected short[] gSamples = new short[]{50,40,30,50};
    protected short[] tSamples = new short[]{0,5,0,3};
    protected static final byte[] EMPTY_CONFIDENCE = new byte[]{};
    SCFChromatogram chromatogram;
    AbstractSampleSectionCodec sut;
    ChannelGroup mockChannelGroup;
    protected abstract AbstractSampleSectionCodec createSectionHandler();

    protected abstract byte[] encodeBytePositions();
    protected abstract byte[] encodeShortPositions();

    public AbstractTestSamplesSection(){
        chromatogram = createMock(SCFChromatogram.class);
        mockChannelGroup = createMock(ChannelGroup.class);
        expect(mockChannelGroup.getCChannel()).andStubReturn(new Channel(EMPTY_CONFIDENCE, cSamples));
        expect(mockChannelGroup.getGChannel()).andStubReturn(new Channel(EMPTY_CONFIDENCE, gSamples));
        expect(mockChannelGroup.getTChannel()).andStubReturn(new Channel(EMPTY_CONFIDENCE, tSamples));
        expect(chromatogram.getChannelGroup()).andStubReturn(mockChannelGroup);
        sut = createSectionHandler();
    }

    protected void makeChromatogramsHaveShorts() {
        expect(mockChannelGroup.getAChannel()).andStubReturn(new Channel(EMPTY_CONFIDENCE, aSamplesAsShorts));
        replay(mockChannelGroup);
    }

    protected void makeChromatogramsHaveBytes() {
        expect(mockChannelGroup.getAChannel()).andStubReturn(new Channel(EMPTY_CONFIDENCE, aSamplesAsBytes));
        replay(mockChannelGroup);
    }

    protected EncodedSection encode(final byte size) throws IOException {
        SCFHeader mockHeader = createMock(SCFHeader.class);
        mockHeader.setSampleSize(size);
        mockHeader.setNumberOfSamples(aSamplesAsBytes.length);
        replay(mockHeader,chromatogram);
        EncodedSection actualEncodedSection=sut.encode(chromatogram, mockHeader);
        verify(mockHeader,chromatogram);
        return actualEncodedSection;
    }

    protected SCFChromatogramBuilder setUpData(int currentOffset, byte size,
            SCFHeader mockHeader, float version) {
        expect(mockHeader.getSampleSize()).andReturn(size).times(2);
        expect(mockHeader.getSampleOffset()).andReturn(currentOffset);
        expect(mockHeader.getNumberOfSamples()).andReturn(aSamplesAsBytes.length);
        expect(mockHeader.getVersion()).andStubReturn(version);
        SCFChromatogramBuilder c = new SCFChromatogramBuilder();
        replay(mockHeader);
        return c;
    }

    protected void assertChromatogramBytePositions(SCFChromatogramBuilder c) {
        assertArrayEquals(aSamplesAsBytes, c.aPositions());
        assertOtherChannelPositionsCorrect(c);
    }
    protected void assertChromatogramShortPositions(SCFChromatogramBuilder c) {
        assertArrayEquals(aSamplesAsShorts, c.aPositions());
        assertOtherChannelPositionsCorrect(c);
    }

    protected void assertOtherChannelPositionsCorrect(SCFChromatogramBuilder c) {
        assertArrayEquals(cSamples, c.cPositions());
        assertArrayEquals(gSamples, c.gPositions());
        assertArrayEquals(tSamples, c.tPositions());
    }

    /**
     * @return the sut
     */
    public AbstractSampleSectionCodec getHandler() {
        return sut;
    }
}
