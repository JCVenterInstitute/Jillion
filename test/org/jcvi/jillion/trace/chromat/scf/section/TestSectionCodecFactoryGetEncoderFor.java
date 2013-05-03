/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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

import static org.junit.Assert.*;


import org.jcvi.jillion.internal.trace.chromat.scf.section.CommentSectionCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.DefaultSectionCodecFactory;
import org.jcvi.jillion.internal.trace.chromat.scf.section.PrivateDataCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Section;
import org.jcvi.jillion.internal.trace.chromat.scf.section.SectionCodecFactory;
import org.jcvi.jillion.internal.trace.chromat.scf.section.SectionEncoder;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Version2BasesSectionCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Version2SampleSectionCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Version3BasesSectionCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Version3SampleSectionCodec;
import org.junit.Test;

public class TestSectionCodecFactoryGetEncoderFor {

    SectionCodecFactory sut = DefaultSectionCodecFactory.INSTANCE;
    @Test
    public void nullSectionShouldThrowIllegalArgumentException(){
        try{
            sut.getSectionEncoderFor(null, 0F);
            fail("should throw Illegal argument exception when giving null param");
        }
        catch(IllegalArgumentException expected){
            assertEquals(expected.getMessage(),"Section can not be null");
        }
    }

    @Test
    public void encoderForVersion1ShouldThrowIllegalArgumentException(){
        try{
            sut.getSectionEncoderFor(Section.BASES, 0F);
            fail("should throw Illegal argument exception when giving version < 2");
        }
        catch(IllegalArgumentException expected){
            assertEquals(expected.getMessage(),"can not encode for version < 2 or >= 4");
        }
    }
    @Test
    public void encoderForVersion4ShouldThrowIllegalArgumentException(){
        try{
            sut.getSectionEncoderFor(Section.BASES, 4F);
            fail("should throw Illegal argument exception when giving version < 4");
        }
        catch(IllegalArgumentException expected){
            assertEquals(expected.getMessage(),"can not encode for version < 2 or >= 4");
        }
    }

    @Test
    public void privateDataAnyVersionShouldReturnPrivateDataCodec(){
        SectionEncoder version2Encoder=sut.getSectionEncoderFor(Section.PRIVATE_DATA, 2F);
        SectionEncoder version3Encoder=sut.getSectionEncoderFor(Section.PRIVATE_DATA, 3F);

        assertSame(version2Encoder, version3Encoder);
        assertTrue(version2Encoder instanceof PrivateDataCodec);
    }

    @Test
    public void commentDataAnyVersionShouldReturnCommentSectionHandler(){
        SectionEncoder version2Encoder=sut.getSectionEncoderFor(Section.COMMENTS, 2F);
        SectionEncoder version3Encoder=sut.getSectionEncoderFor(Section.COMMENTS, 3F);

        assertSame(version2Encoder, version3Encoder);
        assertTrue(version2Encoder instanceof CommentSectionCodec);
    }

    @Test
    public void basesVersion2(){
        SectionEncoder version2Encoder=sut.getSectionEncoderFor(Section.BASES, 2F);
        assertTrue(version2Encoder instanceof Version2BasesSectionCodec);
    }
    @Test
    public void basesVersion3(){
        SectionEncoder version3Encoder=sut.getSectionEncoderFor(Section.BASES, 3F);
        assertTrue(version3Encoder instanceof Version3BasesSectionCodec);
    }
    @Test
    public void samplesVersion2(){
        SectionEncoder version2Encoder=sut.getSectionEncoderFor(Section.SAMPLES, 2F);
        assertTrue(version2Encoder instanceof Version2SampleSectionCodec);
    }
    @Test
    public void samplesVersion3(){
        SectionEncoder version3Encoder=sut.getSectionEncoderFor(Section.SAMPLES, 3F);
        assertTrue(version3Encoder instanceof Version3SampleSectionCodec);
    }
}
