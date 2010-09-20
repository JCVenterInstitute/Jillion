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
package org.jcvi.trace.sanger.chromatogram.scf;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.jcvi.trace.sanger.chromatogram.scf.AbstractSCFCodec;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeaderCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.EncodedSection;
import org.jcvi.trace.sanger.chromatogram.scf.section.Section;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionCodecFactory;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionEncoder;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public abstract class AbstractTestVersionSCFCodecEncoder {

    SCFChromatogram chromo;
    AbstractSCFCodec sut;
    private SCFHeaderCodec mockHeaderCodec;
    private SectionCodecFactory mockSectionCodecFactory;

    byte[] encodedHeader = new byte[]{20,30,4,50};
    byte[] encodedBases = new byte[]{0,2,3,4,5,6};
    byte[] encodedSamples = new byte[]{20,30,40,50,60};
    byte[] encodedComments = new byte[]{1,3,4,5,6,8};
    byte[] privateData = new byte[]{2,3};
    EncodedSection encodedBasesSection = new EncodedSection( ByteBuffer.wrap(encodedBases),Section.BASES);
    EncodedSection encodedSamplesSection = new EncodedSection( ByteBuffer.wrap(encodedSamples),Section.SAMPLES);
    EncodedSection encodedCommentSection = new EncodedSection( ByteBuffer.wrap(encodedComments),Section.COMMENTS);
    EncodedSection encodedPrivateDataSection = new EncodedSection( ByteBuffer.wrap(privateData),Section.PRIVATE_DATA);


    @Before
    public void setup(){
        chromo = createMock(SCFChromatogram.class);
        mockHeaderCodec = createMock(SCFHeaderCodec.class);
        mockSectionCodecFactory = createMock(SectionCodecFactory.class);
        sut = createSCFCodec(mockHeaderCodec,mockSectionCodecFactory);
    }


    protected abstract AbstractSCFCodec createSCFCodec(SCFHeaderCodec headerCodec,SectionCodecFactory sectionCodecFactory);


    protected abstract int getVersion();
    @Test
    public void valid() throws IOException{
        OutputStream mockOut = createMock(OutputStream.class);
        SectionEncoder mockBasesEncoder = createMock(SectionEncoder.class);
        SectionEncoder mockSamplesEncoder = createMock(SectionEncoder.class);
        SectionEncoder mockCommentEncoder = createMock(SectionEncoder.class);
        SectionEncoder mockPrivateDataEncoder = createMock(SectionEncoder.class);
        int version = getVersion();

        expect(mockSectionCodecFactory.getSectionEncoderFor(Section.BASES, version)).andReturn(mockBasesEncoder);
        expect(mockSectionCodecFactory.getSectionEncoderFor(Section.SAMPLES, version)).andReturn(mockSamplesEncoder);
        expect(mockSectionCodecFactory.getSectionEncoderFor(Section.COMMENTS, version)).andReturn(mockCommentEncoder);
        expect(mockSectionCodecFactory.getSectionEncoderFor(Section.PRIVATE_DATA, version)).andReturn(mockPrivateDataEncoder);

        expect(mockBasesEncoder.encode(eq(chromo), isA(SCFHeader.class))).andReturn(encodedBasesSection);
        expect(mockSamplesEncoder.encode(eq(chromo), isA(SCFHeader.class))).andReturn(encodedSamplesSection);
        expect(mockCommentEncoder.encode(eq(chromo), isA(SCFHeader.class))).andReturn(encodedCommentSection);
        expect(mockPrivateDataEncoder.encode(eq(chromo), isA(SCFHeader.class))).andReturn(encodedPrivateDataSection);

        expect(mockHeaderCodec.encode(isA(SCFHeader.class))).andReturn(ByteBuffer.wrap(encodedHeader));
        int size = 128+encodedBases.length+encodedSamples.length+encodedComments.length+privateData.length;
        ByteBuffer expectedBytes = createExpectedEncodedBytes(size);
        mockOut.write(aryEq(expectedBytes.array()));

        replay(mockOut,chromo,mockSectionCodecFactory,mockHeaderCodec,mockBasesEncoder,mockSamplesEncoder,mockCommentEncoder,mockPrivateDataEncoder);
        sut.encode(chromo, mockOut);
        verify(mockOut,chromo,mockSectionCodecFactory,mockHeaderCodec,mockBasesEncoder,mockSamplesEncoder,mockCommentEncoder,mockPrivateDataEncoder);
    }

    @Test
    public void writeThrowsIOException() throws IOException{
        OutputStream mockOut = createMock(OutputStream.class);
        SectionEncoder mockBasesEncoder = createMock(SectionEncoder.class);
        SectionEncoder mockSamplesEncoder = createMock(SectionEncoder.class);
        SectionEncoder mockCommentEncoder = createMock(SectionEncoder.class);
        SectionEncoder mockPrivateDataEncoder = createMock(SectionEncoder.class);
        int version = getVersion();

        expect(mockSectionCodecFactory.getSectionEncoderFor(Section.BASES, version)).andReturn(mockBasesEncoder);
        expect(mockSectionCodecFactory.getSectionEncoderFor(Section.SAMPLES, version)).andReturn(mockSamplesEncoder);
        expect(mockSectionCodecFactory.getSectionEncoderFor(Section.COMMENTS, version)).andReturn(mockCommentEncoder);
        expect(mockSectionCodecFactory.getSectionEncoderFor(Section.PRIVATE_DATA, version)).andReturn(mockPrivateDataEncoder);

        expect(mockBasesEncoder.encode(eq(chromo), isA(SCFHeader.class))).andReturn(encodedBasesSection);
        expect(mockSamplesEncoder.encode(eq(chromo), isA(SCFHeader.class))).andReturn(encodedSamplesSection);
        expect(mockCommentEncoder.encode(eq(chromo), isA(SCFHeader.class))).andReturn(encodedCommentSection);
        expect(mockPrivateDataEncoder.encode(eq(chromo), isA(SCFHeader.class))).andReturn(encodedPrivateDataSection);

        expect(mockHeaderCodec.encode(isA(SCFHeader.class))).andReturn(ByteBuffer.wrap(encodedHeader));
        int size = 128+encodedBases.length+encodedSamples.length+encodedComments.length+privateData.length;
        ByteBuffer expectedBytes = createExpectedEncodedBytes(size);
        mockOut.write(aryEq(expectedBytes.array()));
        IOException expectedIOException = new IOException("expected");
        expectLastCall().andThrow(expectedIOException);
        replay(mockOut,chromo,mockSectionCodecFactory,mockHeaderCodec,mockBasesEncoder,mockSamplesEncoder,mockCommentEncoder,mockPrivateDataEncoder);
        try{
            sut.encode(chromo, mockOut);
            fail("should throw IOException on write failure");
        }catch(IOException e){
            assertEquals(expectedIOException, e);
        }
        verify(mockOut,chromo,mockSectionCodecFactory,mockHeaderCodec,mockBasesEncoder,mockSamplesEncoder,mockCommentEncoder,mockPrivateDataEncoder);
    }

    @Test
    public void encodeThrowsIOException() throws IOException{
        OutputStream mockOut = createMock(OutputStream.class);
        IOException expectedIOException = new IOException("expected");
        SectionEncoder mockSamplesEncoder = createMock(SectionEncoder.class);

        int version = getVersion();

        expect(mockSectionCodecFactory.getSectionEncoderFor(Section.SAMPLES, version)).andReturn(mockSamplesEncoder);

        expect(mockSamplesEncoder.encode(eq(chromo), isA(SCFHeader.class))).andThrow(expectedIOException);


        replay(mockOut,chromo,mockSectionCodecFactory,mockHeaderCodec,mockSamplesEncoder);
        try{
            sut.encode(chromo, mockOut);
            fail("should throw IOException on encode error");
        }catch(IOException e){
            assertEquals(expectedIOException, e);
        }
        verify(mockOut,chromo,mockSectionCodecFactory,mockHeaderCodec,mockSamplesEncoder);
    }


    private ByteBuffer createExpectedEncodedBytes(int size) {
        ByteBuffer expectedBytes = ByteBuffer.allocate(size);
        expectedBytes.put(encodedHeader);
        expectedBytes.put(encodedSamples);
        expectedBytes.put(encodedBases);
        expectedBytes.put(encodedComments);
        expectedBytes.put(privateData);
        expectedBytes.rewind();
        return expectedBytes;
    }
}
