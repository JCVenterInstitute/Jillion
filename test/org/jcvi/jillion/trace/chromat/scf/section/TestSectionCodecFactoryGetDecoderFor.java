/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;

import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.internal.trace.chromat.scf.section.CommentSectionCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.DefaultSectionCodecFactory;
import org.jcvi.jillion.internal.trace.chromat.scf.section.PrivateDataCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Section;
import org.jcvi.jillion.internal.trace.chromat.scf.section.SectionCodecFactory;
import org.jcvi.jillion.internal.trace.chromat.scf.section.SectionDecoder;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Version2BasesSectionCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Version2SampleSectionCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Version3BasesSectionCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Version3SampleSectionCodec;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestSectionCodecFactoryGetDecoderFor {
    SCFHeader mockSCFHeader;
    SCFHeader version3Header;
    SCFHeader version2Header;

    private SectionCodecFactory sut = DefaultSectionCodecFactory.INSTANCE;
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
