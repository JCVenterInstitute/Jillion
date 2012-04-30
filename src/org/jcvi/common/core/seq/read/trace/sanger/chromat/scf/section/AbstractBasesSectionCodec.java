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
 * Created on Sep 12, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChromatogramFileVisitor;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.header.SCFHeader;

public abstract class AbstractBasesSectionCodec implements SectionCodec{
    @Override
    public long decode(DataInputStream in, long currentOffset, SCFHeader header,
            SCFChromatogramBuilder c) throws SectionDecoderException {
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




    protected abstract void readBasesData(DataInputStream in, SCFChromatogramBuilder c,
            int numberOfBases) throws IOException;
    protected abstract void readBasesData(DataInputStream in, ChromatogramFileVisitor c,
            int numberOfBases) throws IOException;

    protected static SCFChromatogramBuilder setConfidences(SCFChromatogramBuilder c, byte[][] probability) {

        return (SCFChromatogramBuilder) c.aConfidence(probability[0])
            .cConfidence(probability[1])
            .gConfidence(probability[2])
            .tConfidence(probability[3]);
    }

    @Override
    public EncodedSection encode(SCFChromatogram c, SCFHeader header)
            throws IOException {
        final int numberOfBases = (int)c.getNucleotideSequence().getLength();
        header.setNumberOfBases(numberOfBases);
        ByteBuffer buffer = ByteBuffer.allocate(numberOfBases*12);
        writeBasesDataToBuffer(buffer, c,numberOfBases);
        buffer.flip();
        return new EncodedSection(buffer, Section.BASES);
    }

    protected abstract void writeBasesDataToBuffer(ByteBuffer buffer, SCFChromatogram c,int numberOfBases);
}
