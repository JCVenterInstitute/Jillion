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
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.header;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.trace.chromat.scf.SCFUtils;

/**
 * <code>DefaultSCFHeaderHandler</code> can both encode
 * and decode {@link SCFHeader}s.
 * @author dkatzel
 *
 *
 */
public enum DefaultSCFHeaderCodec implements SCFHeaderCodec {
    /**
     * Singleton instance.
     */
	INSTANCE;
    /**
     * There is extra unused space at the end of the header; this is the length
     * of that unused space.
     */
    private static final byte LENGTH_OF_SPARE = (byte)72;
    /**
     * The Base clip data in the header is now obsolete,
     * this is how many bytes in the header to skip so we
     * don't read that data.
     */
    private static final byte LENGTH_OF_BASE_CLIP_DATA = (byte)8;
    @Override
    public SCFHeader decode(DataInputStream in) throws SCFHeaderDecoderException {
        if(in ==null){
            throw new IllegalArgumentException("input stream can not be null");
        }
        try{
            verifyMagicNumber(in);
            return parseSCFHeader(in);
        }
        catch(IOException e){
            throw new SCFHeaderDecoderException("Error parsing Header",e);
        }
    }

    private SCFHeader parseSCFHeader(DataInputStream in) throws IOException {
        SCFHeader header = new DefaultSCFHeader();

        header.setNumberOfSamples(in.readInt());
        header.setSampleOffset(in.readInt());

        header.setNumberOfBases(in.readInt());
        skipBaseClipData(in);
        header.setBasesOffset(in.readInt());

        header.setCommentSize(in.readInt());
        header.setCommentOffset(in.readInt());

        header.setVersion(parseVersion(in));
        header.setSampleSize((byte)in.readInt());
        skipUncertaintyCode(in);
        header.setPrivateDataSize(in.readInt());
        header.setPrivateDataOffset(in.readInt());

        skipSpare(in);
        return header;
    }
    /**
     * The SCF Header has a byte code that specifies
     * the uncertainty type ie: does it use '-' or 'N'
     * etc.  We can ignore it for parsing.
     * @param in
     * @throws IOException
     */
    private void skipUncertaintyCode(DataInputStream in) throws IOException {
        in.readInt();

    }

    /**
     * Verify that the given {@link DataInputStream}
     * is actually an SCF file.
     * @param in
     * @throws IOException
     * @throws SCFHeaderDecoderException
     */
    private void verifyMagicNumber(DataInputStream in) throws IOException,
            SCFHeaderDecoderException {
        byte[] actualMagicNumber = new byte[4];
        int bytesRead=in.read(actualMagicNumber);
        if(bytesRead != actualMagicNumber.length){
            throw new SCFHeaderDecoderException("File does not have magic number");
        }
        if(!SCFUtils.isMagicNumber(actualMagicNumber)){
            throw new SCFHeaderDecoderException("Magic number " + new String(actualMagicNumber,IOUtil.UTF_8)+" does not match expected");
        }
    }
    private void skipSpare(DataInputStream in) throws IOException {
        IOUtil.blockingSkip(in,LENGTH_OF_SPARE);
    }
    /**
     * The version of this SCF is stored as a string
     * such as <code>3.00</code>.  This method parses
     * the correct number of bytes into a <code>float</code>.
     * @param in
     * @return
     * @throws IOException
     */
    private float parseVersion(DataInputStream in) throws IOException {
        byte[] version = new byte[4];
        int bytesRead =in.read(version);
        if(bytesRead != version.length){
            throw new IOException("Stream truncated mid version");
        }
        try{
            return Float.parseFloat(new String(version,IOUtil.UTF_8));
        }
        catch(NumberFormatException e){
            throw new IOException("could not parse version" +Arrays.toString(version),e);
        }
    }
    /**
     * As per the SCF 3 Specification: <code>bases_left_clip</code>
     * and <code>bases_right_clip</code> are now obsolete so we will ignore them.
     * @param in
     * @throws IOException
     */
    private void skipBaseClipData(DataInputStream in) throws IOException {
        //skip bases left and right clip which are deprecated
        IOUtil.blockingSkip(in, LENGTH_OF_BASE_CLIP_DATA);
    }

    @Override
    public ByteBuffer encode(SCFHeader header) {
        //I wrap a byte array so that the array is initialized
        //to all 0's. this is needed so garbage data
        //is not leaked in the spare section
        //because NIO will not zero out the buffer upon creation
        ByteBuffer buffer = ByteBuffer.wrap(new byte[SCFUtils.HEADER_SIZE]);
        buffer.put(SCFUtils.getMagicNumber());
        buffer.putInt(header.getNumberOfSamples());
        buffer.putInt(header.getSampleOffset());
        buffer.putInt(header.getNumberOfBases());
        buffer.putInt(0);//bases left and right are ignored zero them out
        buffer.putInt(0);
        buffer.putInt(header.getBasesOffset());
        buffer.putInt(header.getCommentSize());
        buffer.putInt(header.getCommentOffset());
        buffer.put(String.format("%1.2f",header.getVersion()).getBytes(IOUtil.UTF_8));
        buffer.putInt(header.getSampleSize());
        buffer.putInt(0); // code set is 0 for now
        buffer.putInt(header.getPrivateDataSize());
        buffer.putInt(header.getPrivateDataOffset());
        buffer.rewind();
        return buffer;
    }
}
