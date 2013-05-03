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
 * Created on Sep 12, 2008
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
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;

public abstract class AbstractBasesSectionCodec implements SectionCodec{
    @Override
    public long decode(DataInputStream in, long currentOffset, SCFHeader header,
            ScfChromatogramBuilder c) throws SectionDecoderException {
        long bytesToSkip = Math.max(0, header.getBasesOffset() - currentOffset);
        int numberOfBases = header.getNumberOfBases();
        try{
            IOUtil.blockingSkip(in,bytesToSkip);
            readBasesData(in, c, numberOfBases);
            return currentOffset+bytesToSkip + numberOfBases*12;
        }
        catch(IOException e){
            throw new SectionDecoderException("error reading bases section",e);
        }
    }

    
    
    
    @Override
    public long decode(DataInputStream in, long currentOffset,
            SCFHeader header, ChromatogramFileVisitor visitor)
            throws SectionDecoderException {
        long bytesToSkip = header.getBasesOffset() - currentOffset;
        int numberOfBases = header.getNumberOfBases();
        try{
            IOUtil.blockingSkip(in,bytesToSkip);
            readBasesData(in, visitor, numberOfBases);
            return currentOffset+bytesToSkip + numberOfBases*12;
        }
        catch(IOException e){
            throw new SectionDecoderException("error reading bases section",e);
        }
    }




    protected abstract void readBasesData(DataInputStream in, ScfChromatogramBuilder c,
            int numberOfBases) throws IOException;
    protected abstract void readBasesData(DataInputStream in, ChromatogramFileVisitor c,
            int numberOfBases) throws IOException;

    protected static ScfChromatogramBuilder setConfidences(ScfChromatogramBuilder c, byte[][] probability) {

        return c.aConfidence(probability[0])
            .cConfidence(probability[1])
            .gConfidence(probability[2])
            .tConfidence(probability[3]);
    }

    @Override
    public EncodedSection encode(ScfChromatogram c, SCFHeader header)
            throws IOException {
        final int numberOfBases = (int)c.getNucleotideSequence().getLength();
        header.setNumberOfBases(numberOfBases);
        ByteBuffer buffer = ByteBuffer.allocate(numberOfBases*12);
        writeBasesDataToBuffer(buffer, c,numberOfBases);
        buffer.flip();
        return new EncodedSection(buffer, Section.BASES);
    }

    protected abstract void writeBasesDataToBuffer(ByteBuffer buffer, ScfChromatogram c,int numberOfBases);
}
