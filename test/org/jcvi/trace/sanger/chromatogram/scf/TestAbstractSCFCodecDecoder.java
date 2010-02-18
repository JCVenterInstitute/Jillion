/*
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jcvi.trace.sanger.SangerTrace;
import org.jcvi.trace.sanger.chromatogram.scf.AbstractSCFCodec;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.scf.SCFDecoderException;
import org.jcvi.trace.sanger.chromatogram.scf.header.DefaultSCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeaderCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.Section;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionCodecFactory;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionDecoder;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionDecoderException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;
public class TestAbstractSCFCodecDecoder {
    private static class TestDouble extends AbstractSCFCodec{
        private SCFChromatogramBuilder struct;
        public TestDouble(SCFHeaderCodec headerCodec,
                SectionCodecFactory sectionCodecFactory,SCFChromatogramBuilder struct) {
            super(headerCodec, sectionCodecFactory);
            this.struct =struct;
        }

        @Override
        public void encode(SangerTrace c, OutputStream out)
                throws IOException {
        }

        /**
        * {@inheritDoc}
        */
        @Override
        protected SCFChromatogramBuilder createSCFChromatogramStruct() {
            return struct;
        }



    }
    private SCFHeaderCodec mockHeaderCodec;
    private SectionCodecFactory mockSectionCodecFactory;
    private InputStream mockInputStream;
    TestDouble sut;
    SCFHeader expectedSCFHeader;
    SCFChromatogramBuilder mockChromoStruct;
    @Before
    public void setupMocks(){
        mockHeaderCodec = createMock(SCFHeaderCodec.class);
        mockSectionCodecFactory = createMock(SectionCodecFactory.class);
        mockInputStream = createMock(InputStream.class);
        mockChromoStruct = createMock(SCFChromatogramBuilder.class);
        sut = new TestDouble(mockHeaderCodec,mockSectionCodecFactory,mockChromoStruct);

        expectedSCFHeader = new DefaultSCFHeader();
        expectedSCFHeader.setBasesOffset(128);
        expectedSCFHeader.setCommentOffset(200);
        expectedSCFHeader.setPrivateDataOffset(300);
        expectedSCFHeader.setSampleOffset(400);

    }

    @Test
    public void decode() throws SCFDecoderException{
        SectionDecoder mockDecoder = createMock(SectionDecoder.class);
        SCFChromatogram expectedChromo = createMock(SCFChromatogram.class);
        expect(mockHeaderCodec.decode(isA(DataInputStream.class))).andReturn(expectedSCFHeader);
        expect(mockSectionCodecFactory.getSectionParserFor(Section.BASES, expectedSCFHeader))
                                    .andReturn(mockDecoder);
        expect(mockSectionCodecFactory.getSectionParserFor(Section.COMMENTS, expectedSCFHeader))
                                .andReturn(mockDecoder);
        expect(mockSectionCodecFactory.getSectionParserFor(Section.PRIVATE_DATA, expectedSCFHeader))
                                    .andReturn(mockDecoder);

        expect(mockSectionCodecFactory.getSectionParserFor(Section.SAMPLES, expectedSCFHeader))
                                .andReturn(mockDecoder);

        expect(mockDecoder.decode(isA(DataInputStream.class), eq(128L), eq(expectedSCFHeader), eq(mockChromoStruct))).andReturn(200L);
        expect(mockDecoder.decode(isA(DataInputStream.class), eq(200L), eq(expectedSCFHeader), eq(mockChromoStruct))).andReturn(300L);
        expect(mockDecoder.decode(isA(DataInputStream.class), eq(300L), eq(expectedSCFHeader), eq(mockChromoStruct))).andReturn(400L);
        expect(mockDecoder.decode(isA(DataInputStream.class), eq(400L), eq(expectedSCFHeader), eq(mockChromoStruct))).andReturn(500L);

        expect(mockChromoStruct.getChromatogram()).andReturn(expectedChromo);
        replay(mockInputStream, mockHeaderCodec, mockSectionCodecFactory,mockChromoStruct,mockDecoder);
        assertEquals(expectedChromo,sut.decode(mockInputStream));
        verify(mockInputStream, mockHeaderCodec, mockSectionCodecFactory,mockChromoStruct,mockDecoder);
    }

    
    @Test
    public void decodeThrowsSCFDecoderExceptionShouldCloseStreamAndRethrow() throws SCFDecoderException{
        SectionDecoderException expectedException = new SectionDecoderException("expected");
        SectionDecoder mockDecoder = createMock(SectionDecoder.class);
        expect(mockHeaderCodec.decode(isA(DataInputStream.class))).andReturn(expectedSCFHeader);
        expect(mockSectionCodecFactory.getSectionParserFor(Section.BASES, expectedSCFHeader))
                                    .andReturn(mockDecoder);

        expect(mockDecoder.decode(isA(DataInputStream.class), eq(128L), eq(expectedSCFHeader), eq(mockChromoStruct))).andThrow(expectedException);
        replay(mockInputStream, mockHeaderCodec, mockSectionCodecFactory,mockChromoStruct,mockDecoder);
        try{
            sut.decode(mockInputStream);
            fail("should throw SCFDecoderException");
        }catch(SCFDecoderException actualException){
            assertEquals(expectedException.getMessage(),actualException.getMessage() );
        }
        verify(mockInputStream, mockHeaderCodec, mockSectionCodecFactory,mockChromoStruct,mockDecoder);
    }




}
