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
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.section;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;
import org.jcvi.jillion.trace.chromat.scf.PrivateData;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramFileVisitor;
/**
 * <code>PrivateDataCodec</code> is the SectionCodec implementation
 * that will encode/ decode the {@link PrivateData} of an {@link ScfChromatogram}.
 * @author dkatzel
 *
 *
 */
public class PrivateDataCodec implements SectionCodec{

    private static final byte[] EMPTY = new byte[0];
    @Override
    public EncodedSection encode(ScfChromatogram c, SCFHeader header)
            throws IOException {
        PrivateData privateData=c.getPrivateData();
        if(privateData ==null|| privateData.getBytes()==null){
            header.setPrivateDataSize(0);
            return new EncodedSection( ByteBuffer.wrap(EMPTY),Section.PRIVATE_DATA);
        }

        final byte[] rawArray = privateData.getBytes();
        header.setPrivateDataSize(rawArray.length);
        return new EncodedSection( ByteBuffer.wrap(rawArray),
                Section.PRIVATE_DATA);
    }

    @Override
    public long decode(DataInputStream in, long currentOffset,
            SCFHeader header, ScfChromatogramBuilder c) throws SectionDecoderException {
        
        long bytesToSkip =Math.max(0,  header.getPrivateDataOffset() - currentOffset);
        
        try {
            IOUtil.blockingSkip(in,bytesToSkip);
            final int privateDataSize = header.getPrivateDataSize();
            if(privateDataSize ==0){
                c.privateData(null);
            }
            else{
                byte[] privateData = new byte[privateDataSize];
                int bytesRead = in.read(privateData);
                if(bytesRead != privateDataSize){
                    throw new SectionDecoderException("could not read entire private data section");
                }
                c.privateData(privateData);
            }
            return currentOffset+bytesToSkip+privateDataSize;
        } catch (IOException e) {
           throw new SectionDecoderException("error trying to decode Private Data",e);
        }
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long decode(DataInputStream in, long currentOffset,
            SCFHeader header, ChromatogramFileVisitor c)
            throws SectionDecoderException {
        long bytesToSkip = header.getPrivateDataOffset() - currentOffset;
        try {
            IOUtil.blockingSkip(in,bytesToSkip);
            final int privateDataSize = header.getPrivateDataSize();
            if(privateDataSize !=0){              
                byte[] privateData = new byte[privateDataSize];
                int bytesRead = in.read(privateData);
                if(bytesRead != privateDataSize){
                    throw new SectionDecoderException("could not read entire private data section");
                }
                if(c instanceof ScfChromatogramFileVisitor){
                    ((ScfChromatogramFileVisitor) c).visitPrivateData(privateData);
                }
                
            }
            return currentOffset+bytesToSkip+privateDataSize;
        } catch (IOException e) {
           throw new SectionDecoderException("error trying to decode Private Data",e);
        }
    }

}
