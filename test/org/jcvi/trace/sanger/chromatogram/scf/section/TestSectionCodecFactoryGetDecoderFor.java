/*
 * Created on Sep 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.section.CommentSectionCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.DefaultSectionCodecFactory;
import org.jcvi.trace.sanger.chromatogram.scf.section.PrivateDataCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.Section;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionCodecFactory;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionDecoder;
import org.jcvi.trace.sanger.chromatogram.scf.section.Version2BasesSectionCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.Version2SampleSectionCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.Version3BasesSectionCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.Version3SampleSectionCodec;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestSectionCodecFactoryGetDecoderFor {
    SCFHeader mockSCFHeader;
    SCFHeader version3Header;
    SCFHeader version2Header;

    private SectionCodecFactory sut = new DefaultSectionCodecFactory();
    @Before
    public void setupMockHeader(){
        mockSCFHeader = createMock(SCFHeader.class);
        version3Header =  createMock(SCFHeader.class);
        version2Header =  createMock(SCFHeader.class);
        expect(version3Header.getVersion()).andReturn(3F);
        expect(version2Header.getVersion()).andReturn(2F);

    }
    @Test
    public void nullSectionShouldThrowIllegalArgumentException(){
        try{
            sut.getSectionParserFor(null, mockSCFHeader);
            fail("should throw Illegal argument exception when giving null param");
        }
        catch(IllegalArgumentException expected){
            assertEquals(expected.getMessage(),"Section can not be null");
        }
    }
    @Test
    public void nullSCFHeaderShouldThrowIllegalArgumentException(){
        try{
            sut.getSectionParserFor(Section.BASES, null);
            fail("should throw Illegal argument exception when giving null param");
        }
        catch(IllegalArgumentException expected){
            assertEquals(expected.getMessage(),"header can not be null");
        }
    }

    @Test
    public void commentShouldBeSameForAnyVersion(){

        replay(version2Header,version3Header);
        SectionDecoder version3Parser =sut.getSectionParserFor(Section.COMMENTS, version3Header);
        SectionDecoder version2Parser =sut.getSectionParserFor(Section.COMMENTS, version2Header);

        verify(version2Header,version3Header);
        assertTrue(version3Parser instanceof CommentSectionCodec);
        assertSame(version3Parser, version2Parser);
    }
    @Test
    public void privateDataShouldReturnPrivateDataCodecForAnyVersion(){
        replay(version2Header,version3Header);
        SectionDecoder version3Parser =sut.getSectionParserFor(Section.PRIVATE_DATA, version3Header);
        SectionDecoder version2Parser =sut.getSectionParserFor(Section.PRIVATE_DATA, version2Header);

        verify(version2Header,version3Header);
        assertTrue(version3Parser instanceof PrivateDataCodec);
        assertSame(version3Parser, version2Parser);
    }

    @Test
    public void basesVersion2(){
        replay(version2Header);
        SectionDecoder version2Parser =sut.getSectionParserFor(Section.BASES, version2Header);
        verify(version2Header);
        assertTrue(version2Parser instanceof Version2BasesSectionCodec);
    }

    @Test
    public void basesVersion3(){
        replay(version3Header);
        SectionDecoder version3Parser =sut.getSectionParserFor(Section.BASES, version3Header);
        verify(version3Header);
        assertTrue(version3Parser instanceof Version3BasesSectionCodec);
    }

    @Test
    public void samplesVersion2(){
        replay(version2Header);
        SectionDecoder version2Parser =sut.getSectionParserFor(Section.SAMPLES, version2Header);
        verify(version2Header);
        assertTrue(version2Parser instanceof Version2SampleSectionCodec);
    }

    @Test
    public void samplesVersion3(){
        replay(version3Header);
        SectionDecoder version3Parser =sut.getSectionParserFor(Section.SAMPLES, version3Header);
        verify(version3Header);
        assertTrue(version3Parser instanceof Version3SampleSectionCodec);
    }
}
