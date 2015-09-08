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
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;



import java.io.IOException;

import org.jcvi.jillion.internal.trace.chromat.DefaultChannel;
import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.internal.trace.chromat.scf.section.AbstractSampleSectionCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.EncodedSection;
import org.jcvi.jillion.trace.chromat.ChannelGroup;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

public abstract class AbstractTestSamplesSection {
    protected  short[] aSamplesAsShorts = new short[]{200,300,0,0};
    protected short[] aSamplesAsBytes = new short[]{13,14,15,16};
    protected short[] cSamples = new short[]{0,10,30,50};
    protected short[] gSamples = new short[]{50,40,30,50};
    protected short[] tSamples = new short[]{0,5,0,3};
    protected static final byte[] EMPTY_CONFIDENCE = new byte[]{};
    ScfChromatogram chromatogram;
    AbstractSampleSectionCodec sut;
    ChannelGroup mockChannelGroup;
    protected abstract AbstractSampleSectionCodec createSectionHandler();

    protected abstract byte[] encodeBytePositions();
    protected abstract byte[] encodeShortPositions();

    public AbstractTestSamplesSection(){
        chromatogram = createMock(ScfChromatogram.class);
        mockChannelGroup = createMock(ChannelGroup.class);
        expect(mockChannelGroup.getCChannel()).andStubReturn(new DefaultChannel(EMPTY_CONFIDENCE, cSamples));
        expect(mockChannelGroup.getGChannel()).andStubReturn(new DefaultChannel(EMPTY_CONFIDENCE, gSamples));
        expect(mockChannelGroup.getTChannel()).andStubReturn(new DefaultChannel(EMPTY_CONFIDENCE, tSamples));
        expect(chromatogram.getChannelGroup()).andStubReturn(mockChannelGroup);
        sut = createSectionHandler();
    }

    protected void makeChromatogramsHaveShorts() {
        expect(mockChannelGroup.getAChannel()).andStubReturn(new DefaultChannel(EMPTY_CONFIDENCE, aSamplesAsShorts));
        replay(mockChannelGroup);
    }

    protected void makeChromatogramsHaveBytes() {
        expect(mockChannelGroup.getAChannel()).andStubReturn(new DefaultChannel(EMPTY_CONFIDENCE, aSamplesAsBytes));
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

    protected ScfChromatogramBuilder setUpData(int currentOffset, byte size,
            SCFHeader mockHeader, float version) {
        expect(mockHeader.getSampleSize()).andReturn(size).times(2);
        expect(mockHeader.getSampleOffset()).andReturn(currentOffset);
        expect(mockHeader.getNumberOfSamples()).andReturn(aSamplesAsBytes.length);
        expect(mockHeader.getVersion()).andStubReturn(version);
        ScfChromatogramBuilder c = new ScfChromatogramBuilder("id");
        replay(mockHeader);
        return c;
    }

    protected void assertChromatogramBytePositions(ScfChromatogramBuilder c) {
        assertArrayEquals(aSamplesAsBytes, c.aPositions());
        assertOtherChannelPositionsCorrect(c);
    }
    protected void assertChromatogramShortPositions(ScfChromatogramBuilder c) {
        assertArrayEquals(aSamplesAsShorts, c.aPositions());
        assertOtherChannelPositionsCorrect(c);
    }

    protected void assertOtherChannelPositionsCorrect(ScfChromatogramBuilder c) {
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
