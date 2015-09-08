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
 * Created on Sep 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.internal.trace.chromat.scf.section.SectionDecoderException;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractTestSamplesSectionDecoder {
    private SCFHeader mockHeader;
    private AbstractTestSamplesSection sut;

    @Before
    public void createMockHeader(){
        mockHeader = createMock(SCFHeader.class);
        sut = createSut();
    }
    protected abstract AbstractTestSamplesSection createSut();
    protected abstract float getVersion();
    @Test
    public void parseShorts() throws SectionDecoderException{
        Integer currentOffset = 0;
        byte[] encodedBytes = sut.encodeShortPositions();
        ScfChromatogramBuilder c = sut.setUpData(currentOffset, (byte)2, mockHeader, getVersion());
        sut.getHandler().decode(new DataInputStream(new ByteArrayInputStream(encodedBytes)), currentOffset, mockHeader, c);
        verify(mockHeader);
        sut.assertChromatogramShortPositions(c);
    }



    @Test
    public void parseBytes() throws SectionDecoderException{
        Integer currentOffset = 0;

        byte[] encodedBytes = sut.encodeBytePositions();
        ScfChromatogramBuilder c = sut.setUpData(currentOffset, (byte)1, mockHeader, getVersion());
        sut.getHandler().decode(new DataInputStream(new ByteArrayInputStream(encodedBytes)), currentOffset, mockHeader, c);
        verify(mockHeader);
        sut.assertChromatogramBytePositions(c);
    }

    @Test
    public void parseThrowsIOExceptionShouldWrapInSectionParserException() throws IOException{
        InputStream in = createMock(InputStream.class);
        ScfChromatogramBuilder c = sut.setUpData(0, (byte)1, mockHeader, getVersion());
        IOException expectedIOException = new IOException("expected");
        expect(in.read()).andThrow(expectedIOException);
        replay(in);
        try{
            sut.getHandler().decode(new DataInputStream(in), 0, mockHeader, c);
            fail("should throw exception on inputstream read exception");
        }
        catch(SectionDecoderException e){
            assertEquals( expectedIOException,e.getCause());
            assertEquals("error reading version "+getVersion()+" samples",e.getMessage());
        }
        verify(in);
    }
}
