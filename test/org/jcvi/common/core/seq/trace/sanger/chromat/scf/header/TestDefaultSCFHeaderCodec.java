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
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.chromat.scf.header;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.common.core.seq.trace.sanger.chromat.scf.header.DefaultSCFHeader;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.header.DefaultSCFHeaderCodec;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.header.SCFHeader;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.header.SCFHeaderCodec;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.header.SCFHeaderDecoderException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultSCFHeaderCodec {
    private int numberOfSamples =100;
    private int sampleOffset=128;
    private int numberOfBases=10;
    private int basesOffset=400;
    private int commentSize=200;
    private int commentOffset=520;
    private float version=3.0F;
    private byte sampleSize=1;
    private int privateDataSize=0;
    private int privateDataOffset=720;

    private DefaultSCFHeader header;

    private SCFHeaderCodec sut = DefaultSCFHeaderCodec.INSTANCE;
    @Before
    public void createHeader(){
        header = new DefaultSCFHeader();
        header.setNumberOfSamples(numberOfSamples);
        header.setSampleOffset(sampleOffset);
        header.setNumberOfBases(numberOfBases);
        header.setBasesOffset(basesOffset);
        header.setCommentOffset(commentOffset);
        header.setCommentSize(commentSize);
        header.setVersion(version);
        header.setSampleSize(sampleSize);
        header.setPrivateDataSize(privateDataSize);
        header.setPrivateDataOffset(privateDataOffset);
    }

    @Test
    public void encode(){
        ByteBuffer expected = createExpectedFrom(header);
        ByteBuffer actual =sut.encode(header);
        assertArrayEquals(expected.array(), actual.array());
    }

    @Test
    public void encodeVersion2(){
        header.setVersion(2.4F);
        ByteBuffer expected = createExpectedFrom(header);
        ByteBuffer actual =sut.encode(header);
        assertArrayEquals(expected.array(), actual.array());
    }

    @Test
    public void decode() throws SCFHeaderDecoderException{
        DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(createExpectedFrom(header).array()));
        SCFHeader actual = sut.decode(in);
        assertEquals(actual,header);
    }
    @Test
    public void decodeVersion2() throws SCFHeaderDecoderException{
        header.setVersion(2.4F);
        DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(createExpectedFrom(header).array()));
        SCFHeader actual = sut.decode(in);
        assertEquals(actual,header);
    }

    @Test
    public void decodeNullInputStreamShouldThrowIllegalArgumentException() throws SCFHeaderDecoderException{
        try {
            sut.decode(null);
            fail("should throw illegal arugment exception when passed in null");
        } catch (IllegalArgumentException e) {
            assertEquals("input stream can not be null", e.getMessage());
        }
    }

    @Test
    public void decodeInputTooSmall(){
        DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(new byte[2]));
        try {
            sut.decode(in);
            fail("if input is too small for magic number should throw SCFHeaderDecoderException");
        } catch(IOException e){
            SCFHeaderDecoderException decoderException = (SCFHeaderDecoderException)e.getCause();
            
            assertEquals("File does not have magic number", decoderException.getMessage());
        }
    }
    @Test
    public void decodeInvaildMagicNumber(){
        DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(".ZTR".getBytes()));
        try {
            sut.decode(in);
            fail("wrong magic number should throw SCFHeaderDecoderException");
        } catch(IOException e){
            SCFHeaderDecoderException decoderException = (SCFHeaderDecoderException)e.getCause();
            
            assertEquals("Magic number .ZTR does not match expected", decoderException.getMessage());
        }
    }
    @Test
    public void invalidFormattedVersionShouldThrowSCFHeaderDecoderException() {
        final byte[] invalidVersion = createExpectedFrom(header).array();
        invalidVersion[36] = (byte)'X';
        DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(invalidVersion));
       try {
        sut.decode(in);
        fail("invalid formatted version should throw SCFHeaderDecoderException");
        } catch (SCFHeaderDecoderException e) {
            assertEquals("Error parsing Header", e.getMessage());
            Throwable cause = e.getCause();
            assertTrue(cause instanceof IOException);
            final byte[] invalidVersonBytes = Arrays.copyOfRange(invalidVersion, 36, 40);
            assertEquals("could not parse version"+
                    Arrays.toString(invalidVersonBytes),
                    cause.getMessage());

            Throwable rootCause = cause.getCause();
            assertTrue(rootCause instanceof NumberFormatException);
            assertEquals("For input string: \""+
                    new String(invalidVersonBytes) + "\"",
                    rootCause.getMessage());
        }
    }

    @Test
    public void truncatedVersionShouldThrowSCFHeaderDecoderException() {
        final byte[] truncatedVersion = Arrays.copyOfRange(createExpectedFrom(header).array(),0,38);

        DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(truncatedVersion));
       try {
        sut.decode(in);
        fail("truncated version should throw SCFHeaderDecoderException");
        } catch (SCFHeaderDecoderException e) {
            assertEquals("Error parsing Header", e.getMessage());
            Throwable cause = e.getCause();
            assertTrue(cause instanceof IOException);
            assertEquals("Stream truncated mid version",
                    cause.getMessage());
           assertNull(cause.getCause());
        }
    }

    private ByteBuffer createExpectedFrom(DefaultSCFHeader h) {
        ByteBuffer result = ByteBuffer.wrap(new byte[128]);
        result.put(".scf".getBytes());
        result.putInt(h.getNumberOfSamples());
        result.putInt(h.getSampleOffset());
        result.putInt(h.getNumberOfBases());
        //obsolete stuff
        result.put(new byte[8]);
        result.putInt(h.getBasesOffset());
        result.putInt(h.getCommentSize());
        result.putInt(h.getCommentOffset());
        result.put(String.format("%.2f", h.getVersion()).getBytes());
        result.putInt(h.getSampleSize());
        //codeset ignored
        result.putInt(0);
        result.putInt(h.getPrivateDataSize());
        result.putInt(h.getPrivateDataOffset());
        //unused left blank
        return result;
    }
}
