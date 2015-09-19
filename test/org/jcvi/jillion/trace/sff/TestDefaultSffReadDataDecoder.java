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
 * Created on Oct 10, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.testUtil.EasyMockUtil;
import org.junit.Test;
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
        
        expect(mockInputStream.read()).andReturn(1);
        expect(mockInputStream.skip(padding-1)).andReturn(padding-1);

    }

}
