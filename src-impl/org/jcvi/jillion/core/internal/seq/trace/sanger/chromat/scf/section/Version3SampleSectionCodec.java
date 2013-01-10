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
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.scf.section;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.jcvi.common.core.seq.trace.sanger.PositionSequence;
import org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.scf.SCFUtils;
import org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.scf.header.pos.PositionStrategy;
/**
 * SCF Version 3 implementation of Sample Section Parser.  In order to
 * allow for better compression, SCF Version3
 * stores the position data as "delta delta" values. An algorithm taking into
 * account the previous 2 values are required to compute the actual position values.
 * @author dkatzel
 *
 *
 */
public class Version3SampleSectionCodec extends AbstractSampleSectionCodec{
    @Override
    protected void extractActualPositions(PositionStrategy positionStrategy,
            short[][] positions) {

        for(int channel=0; channel<4; channel++){
            SCFUtils.deltaDeltaDecode(positions[channel]);
        }
    }

    @Override
    protected short[][] parseRawPositions(DataInputStream in,
            int numberOfSamples, PositionStrategy positionStrategy)
            throws IOException {
            short[][] positions = new short[4][numberOfSamples];
            for(int channel=0; channel<4; channel++){
            for(int i=0; i< numberOfSamples; i++){
                try{
                    positions[channel][i]=positionStrategy.getPosition(in);
                }catch(EOFException e){
                    //end of file means no positions?
                    return positions;
                }
                }
            }
            return positions;

    }
    @Override
    protected void writePositionsToBuffer(PositionStrategy positionStrategy,
    		PositionSequence aPositions, PositionSequence cPositions,
    		PositionSequence gPositions, PositionSequence tPositions, ByteBuffer buffer)
            {
        bulkPut(positionStrategy, SCFUtils.deltaDeltaEncode(aPositions), buffer);
        bulkPut(positionStrategy, SCFUtils.deltaDeltaEncode(cPositions), buffer);
        bulkPut(positionStrategy, SCFUtils.deltaDeltaEncode(gPositions), buffer);
        bulkPut(positionStrategy, SCFUtils.deltaDeltaEncode(tPositions), buffer);

    }


    private void bulkPut(PositionStrategy positionStrategy,
            ShortBuffer positions, ByteBuffer buffer){
        while(positions.hasRemaining()){
            positionStrategy.setPosition(positions.get(), buffer);
        }
    }




}
