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
 * Created on Sep 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section;

import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.header.SCFHeader;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.CommentSectionCodec;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.DefaultSectionCodecFactory;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.PrivateDataCodec;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.Section;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.SectionCodecFactory;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.SectionDecoder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.Version2BasesSectionCodec;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.Version2SampleSectionCodec;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.Version3BasesSectionCodec;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.Version3SampleSectionCodec;
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
