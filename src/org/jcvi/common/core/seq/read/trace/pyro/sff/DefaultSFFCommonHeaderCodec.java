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
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.common.core.io.IOUtil;

public class DefaultSFFCommonHeaderCodec implements SFFCommonHeaderCodec {

    private static final byte[] MAGIC_NUMBER = ".sff".getBytes(IOUtil.UTF_8);
    private static final byte[] ACCEPTED_VERSION = new byte[]{0,0,0,1};
    /**
     * Currently SFF only has 1 format code which has a value of <code>1</code>.
     */
    private static final byte FORMAT_CODE = 1;
    @Override
    public SFFCommonHeader decodeHeader(DataInputStream in) throws SFFDecoderException{

        try{
            verifyMagicNumber(in);
            verifyVersion1(in);
            BigInteger indexOffset = IOUtil.readUnsignedLong(in);
            long indexLength = IOUtil.readUnsignedInt(in);
            long numReads = IOUtil.readUnsignedInt(in);
            //skip header length
            IOUtil.readUnsignedShort(in);
            int keyLength = IOUtil.readUnsignedShort(in);
            int flowsPerRead = IOUtil.readUnsignedShort(in);
            verifyFlowgramFormatCode(in);
            String flow = readFlow(in,flowsPerRead);
            String keySequence = readKeySequence(in, keyLength);
            int bytesReadSoFar = 31+flowsPerRead+keyLength;
            int padding =SFFUtil.caclulatePaddedBytes(bytesReadSoFar);
            IOUtil.blockingSkip(in, padding);

            return new DefaultSFFCommonHeader(indexOffset, indexLength,
            numReads, flowsPerRead, flow,
            keySequence);

        }
        catch(IOException e){
            throw new SFFDecoderException("error decoding sff file",e);
        }

    }
    private String readFlow(DataInputStream in, int flowsPerRead) throws IOException, SFFDecoderException {
        byte[] flow = new byte[flowsPerRead];
        int bytesRead = in.read(flow);
        if(bytesRead != flowsPerRead){
            throw new SFFDecoderException("error decoding flow");
        }
        return new String(flow,IOUtil.UTF_8);
    }
    private String readKeySequence(DataInputStream in, int keyLength) throws IOException, SFFDecoderException {
        byte[] keySequence = new byte[keyLength];
        int bytesRead = in.read(keySequence);
        if(bytesRead != keyLength){
            throw new SFFDecoderException("error decoding keySequence");
        }
        return new String(keySequence,IOUtil.UTF_8);
    }
    private void verifyFlowgramFormatCode(DataInputStream in) throws IOException, SFFDecoderException {
        //currently only 1 format code
        if(in.readByte() != FORMAT_CODE){
            throw new SFFDecoderException("unknown flowgram format code");
        }

    }
    private void verifyVersion1(DataInputStream in) throws IOException, SFFDecoderException {
        byte[] versionArray = new byte[4];
        int bytesRead =in.read(versionArray);
        if(bytesRead != 4 || !Arrays.equals(versionArray, ACCEPTED_VERSION)){
            throw new SFFDecoderException("version not compatible with decoder");
        }

    }
    private void verifyMagicNumber(DataInputStream in) throws IOException,
            SFFDecoderException {
        byte[] actualMagicNumber = new byte[4];
        int bytesRead =in.read(actualMagicNumber);
        if(bytesRead != 4 || !Arrays.equals(actualMagicNumber, MAGIC_NUMBER)){
            throw new SFFDecoderException("magic number does not match expected");
        }
    }


    public byte[] encodeHeader(SFFCommonHeader header){
        final short keyLength =(short) (header.getKeySequence().length());
        int size = 31+header.getNumberOfFlowsPerRead()+ keyLength;
        int padding =SFFUtil.caclulatePaddedBytes(size);
        ByteBuffer buf = ByteBuffer.wrap(new byte[size+padding]);
        buf.put(MAGIC_NUMBER);
        buf.put(ACCEPTED_VERSION);
        buf.put(IOUtil.convertUnsignedLongToByteArray(header.getIndexOffset()));
        buf.put(IOUtil.convertUnsignedIntToByteArray(header.getIndexLength()));
        buf.put(IOUtil.convertUnsignedIntToByteArray(header.getNumberOfReads()));
        buf.putShort((short)(size+padding));
        buf.putShort(keyLength);
        buf.put(IOUtil.convertUnsignedShortToByteArray(header.getNumberOfFlowsPerRead()));
        buf.put(FORMAT_CODE);
        buf.put(header.getFlow().getBytes(IOUtil.UTF_8));
        buf.put(header.getKeySequence().getBytes(IOUtil.UTF_8));
        return buf.array();
    }


}
