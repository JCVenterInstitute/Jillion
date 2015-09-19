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
 * Created on Oct 8, 2008
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
import static org.jcvi.jillion.core.testUtil.EasyMockUtil.putInt;
import static org.jcvi.jillion.core.testUtil.EasyMockUtil.putShort;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.testUtil.EasyMockUtil;
import org.junit.Test;
public class TestSffeadHeaderDecoder extends AbstractTestSFFReadHeaderCodec{

    @Test
    public void valid() throws SffDecoderException, IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        encodeHeader(mockInputStream, expectedReadHeader);
        replay(mockInputStream);
        SffReadHeader actualReadHeader =sut.decodeReadHeader(new DataInputStream(mockInputStream));
        
       
        assertEquals(actualReadHeader, expectedReadHeader);
        verify(mockInputStream);
    }
    
    @Test
    public void noClipPointsShouldSetAdapterLengthToNumBases() throws SffDecoderException, IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        encodeHeaderWithNoClipPoints(mockInputStream, expectedReadHeader);
        replay(mockInputStream);
        SffReadHeader actualReadHeader =sut.decodeReadHeader(new DataInputStream(mockInputStream));
        DefaultSffReadHeader expectedReadHeader = new DefaultSffReadHeader(numberOfBases,
                new Range.Builder(numberOfBases).contractBegin(qual_left-1).build(),
        			new Range.Builder(numberOfBases).contractBegin(adapter_left-1).build(), name);
        
        
        assertEquals(actualReadHeader, expectedReadHeader);
        verify(mockInputStream);
    }
    

	@Test
    public void sequenceNameLengthEncodedIncorrectlyShouldThrowIOException() throws  IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        encodeHeaderWithWrongSequenceLength(mockInputStream, expectedReadHeader);
        replay(mockInputStream);
        try{
            sut.decodeReadHeader(new DataInputStream(mockInputStream));
            fail("should throw SFFDecoderException if name length encoded wrong");
        }catch(IOException e){
            Throwable cause = e.getCause();
            assertEquals("error decoding seq name", cause.getMessage());
        }


        verify(mockInputStream);
    }

    @Test
    public void readThrowsIOExceptionShouldWrapInSFFDecoderException() throws IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        IOException expectedIOException = new IOException("expected");
        expect(mockInputStream.read()).andThrow(expectedIOException);
        replay(mockInputStream);
        try {
            sut.decodeReadHeader(new DataInputStream(mockInputStream));
            fail("should wrap IOException in SFFDecoderException");
        } catch (SffDecoderException e) {
            assertEquals("error trying to decode read header", e.getMessage());
            assertEquals(expectedIOException, e.getCause());
        }

        verify(mockInputStream);
    }

    private void encodeHeaderWithNoClipPoints(InputStream mockInputStream,
			DefaultSffReadHeader readHeader) throws IOException {
    	 final String seqName = readHeader.getId();
         final int nameLength = seqName.length();
         int unpaddedLength = 16+nameLength;
         final long padds = SffUtil.caclulatePaddedBytes(unpaddedLength);
         putShort(mockInputStream,(short)(padds+unpaddedLength));
         putShort(mockInputStream,(short)nameLength);
         putInt(mockInputStream,readHeader.getNumberOfBases());
         putShort(mockInputStream,(short)readHeader.getQualityClip().getBegin(CoordinateSystem.RESIDUE_BASED));
         putShort(mockInputStream,(short)0);
         putShort(mockInputStream,(short)readHeader.getAdapterClip().getBegin(CoordinateSystem.RESIDUE_BASED));
         putShort(mockInputStream,(short)0);
         expect(mockInputStream.read(isA(byte[].class), eq(0),eq(nameLength)))
             .andAnswer(EasyMockUtil.writeArrayToInputStream(seqName.getBytes()));
         expect(mockInputStream.read()).andReturn(1);
         expect(mockInputStream.skip(padds-1)).andReturn(padds-1);
		
	}
    
    void encodeHeader(InputStream mockInputStream, SffReadHeader readHeader) throws IOException{
        final String seqName = readHeader.getId();
        final int nameLength = seqName.length();
        int unpaddedLength = 16+nameLength;
        final long padds = SffUtil.caclulatePaddedBytes(unpaddedLength);
        putShort(mockInputStream,(short)(padds+unpaddedLength));
        putShort(mockInputStream,(short)nameLength);
        putInt(mockInputStream,readHeader.getNumberOfBases());
        putShort(mockInputStream,(short)readHeader.getQualityClip().getBegin(CoordinateSystem.RESIDUE_BASED));
        putShort(mockInputStream,(short)readHeader.getQualityClip().getEnd(CoordinateSystem.RESIDUE_BASED));
        putShort(mockInputStream,(short)readHeader.getAdapterClip().getBegin(CoordinateSystem.RESIDUE_BASED));
        putShort(mockInputStream,(short)readHeader.getAdapterClip().getEnd(CoordinateSystem.RESIDUE_BASED));
        expect(mockInputStream.read(isA(byte[].class), eq(0),eq(nameLength)))
            .andAnswer(EasyMockUtil.writeArrayToInputStream(seqName.getBytes()));
        expect(mockInputStream.read()).andReturn(1);
        expect(mockInputStream.skip(padds-1)).andReturn(padds-1);
    }
    void encodeHeaderWithWrongSequenceLength(InputStream mockInputStream, SffReadHeader readHeader) throws IOException{
        final String seqName = readHeader.getId();
        final int nameLength = seqName.length();
        int unpaddedLength = 16+nameLength;
        final long padds = SffUtil.caclulatePaddedBytes(unpaddedLength);
        putShort(mockInputStream,(short)(padds+unpaddedLength));
        putShort(mockInputStream,(short)(nameLength+1));
        putInt(mockInputStream,readHeader.getNumberOfBases());
        putShort(mockInputStream,(short)readHeader.getQualityClip().getBegin());
        putShort(mockInputStream,(short)readHeader.getQualityClip().getEnd());
        putShort(mockInputStream,(short)readHeader.getAdapterClip().getBegin());
        putShort(mockInputStream,(short)readHeader.getAdapterClip().getEnd());
        expect(mockInputStream.read(isA(byte[].class), eq(0),eq(nameLength+1)))
            .andAnswer(EasyMockUtil.writeArrayToInputStream(seqName.getBytes()));
        
        expect(mockInputStream.read(isA(byte[].class), eq(13), eq(1))).andReturn(-1); //EOF
    }


}
