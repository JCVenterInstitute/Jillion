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
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

enum DefaultSffReadDataDecoder implements SffReadDataDecoder {
	/**
	 * Singleton instance.
	 */
	INSTANCE;
    @Override
    public SffReadData decode(DataInputStream in, int numberOfFlows, int numberOfBases) throws SffDecoderException {
        try{
            short[] values = IOUtil.readShortArray(in, numberOfFlows);
            byte[] indexes = IOUtil.toByteArray(in, numberOfBases);
            NucleotideSequence bases = new NucleotideSequenceBuilder()
            					.append(new String(IOUtil.toByteArray(in, numberOfBases),IOUtil.UTF_8))
            					.build();
            QualitySequence qualities = new QualitySequenceBuilder(IOUtil.toByteArray(in, numberOfBases))
            								.build();

            int readDataLength = SffUtil.getReadDataLength(numberOfFlows, numberOfBases);
            int padding =SffUtil.caclulatePaddedBytes(readDataLength);
            IOUtil.blockingSkip(in, padding);
            return new DefaultSffReadData(bases, indexes, values,qualities);
        }
        catch(IOException e){
            throw new SffDecoderException("error decoding read data", e);
        }

    }
    @Override
    public SffReadData decode(ByteBuffer in, int numberOfFlows, int numberOfBases) throws SffDecoderException {
        try{
            short[] values = new short[numberOfFlows];            
            in.asShortBuffer().get(values);
            //need to increment position
            //because shortBuffer view used above
            //to get flow values has its own
            //independent position field
            in.position(in.position()+numberOfFlows*2);

            byte[] indexes = new byte[numberOfBases];
            in.get(indexes);
            
            byte[] basecallsAsBytes = new byte[numberOfBases];
            in.get(basecallsAsBytes);
            NucleotideSequence bases = new NucleotideSequenceBuilder(new String(basecallsAsBytes,IOUtil.UTF_8))
            								.build();
            byte[] qualitiesAsBytes = new byte[numberOfBases];
            in.get(qualitiesAsBytes);
            QualitySequence qualities = new QualitySequenceBuilder(qualitiesAsBytes).build();

            int readDataLength = SffUtil.getReadDataLength(numberOfFlows, numberOfBases);
            int padding =SffUtil.caclulatePaddedBytes(readDataLength);
            in.position(in.position()+padding);
            return new DefaultSffReadData(bases, indexes, values,qualities);
        }
        catch(BufferUnderflowException e){
            throw new SffDecoderException("error decoding read data", e);
        }

    }
}
