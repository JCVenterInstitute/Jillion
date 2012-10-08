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
 * Created on Oct 10, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.pyro.sff.DefaultSffReadData;
import org.jcvi.common.core.seq.read.trace.pyro.sff.DefaultSffReadDataDecoder;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffDecoderException;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffReadData;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffUtil;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.testUtil.EasyMockUtil;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestDefaultSffReadDataDecoder {

    protected int numberOfFlows = 5;
    protected int numberOfBases=4;

    protected QualitySequence qualities = new QualitySequenceBuilder(new byte[]{20,30,40,35}).build();
    protected short[] values = new short[]{100,8,97,4,200};
    protected byte[] indexes = new byte[]{1,2,2,0};
    protected NucleotideSequence bases = new NucleotideSequenceBuilder("TATT").build();


    protected DefaultSffReadDataDecoder sut = DefaultSffReadDataDecoder.INSTANCE;

    protected DefaultSffReadData expectedReadData = new DefaultSffReadData(bases, indexes,  values,
                                            qualities);


    @Test
    public void valid() throws SffDecoderException, IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        encode(mockInputStream,expectedReadData);


        replay(mockInputStream);
       SffReadData actualReadData= sut.decode(new DataInputStream(mockInputStream),
                numberOfFlows, numberOfBases);
       assertEquals(expectedReadData, actualReadData);
       verify(mockInputStream);
    }
    @Test
    public void readThrowsIOExceptionShouldWrapInSFFDecoderException() throws IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        IOException ioException = new IOException("expected");
       expect(mockInputStream.read()).andThrow(ioException);


        replay(mockInputStream);
        try{
            sut.decode(new DataInputStream(mockInputStream),numberOfFlows, numberOfBases);
            fail("IOException should be wrapped in SFFDecoderException");
        }catch(SffDecoderException e){
            assertEquals("error decoding read data",e.getMessage());
            assertEquals(ioException, e.getCause());
        }
       verify(mockInputStream);
    }


    void encode(InputStream mockInputStream, SffReadData readData) throws IOException{
        int basesLength =(int)readData.getNucleotideSequence().getLength();
        int numberOfFlows = readData.getFlowgramValues().length;
        int readDataLength = numberOfFlows * 2 + 3*numberOfBases;
        long padding =SffUtil.caclulatePaddedBytes(readDataLength);
        for(int i=0; i<numberOfFlows; i++){
            EasyMockUtil.putShort(mockInputStream, readData.getFlowgramValues()[i]);
        }
        expect(mockInputStream.read(isA(byte[].class), eq(0),eq(basesLength)))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(readData.getFlowIndexPerBase()));

        expect(mockInputStream.read(isA(byte[].class), eq(0),eq(basesLength)))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(readData.getNucleotideSequence().toString().getBytes(IOUtil.UTF_8)));
        expect(mockInputStream.read(isA(byte[].class), eq(0),eq(basesLength)))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(PhredQuality.toArray(readData.getQualitySequence())));
        expect(mockInputStream.skip(padding)).andReturn(padding);

    }

}
