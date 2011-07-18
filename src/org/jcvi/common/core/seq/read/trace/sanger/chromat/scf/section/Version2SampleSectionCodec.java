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
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.header.pos.PositionStrategy;


public class Version2SampleSectionCodec extends AbstractSampleSectionCodec {

    @Override
    protected void extractActualPositions(PositionStrategy positionStrategy,
            short[][] positions) {
        // no-op; version 2 positions are stored uncompressed.

    }


    @Override
    protected void writePositionsToBuffer(PositionStrategy positionStrategy,
            ShortBuffer aPositions, ShortBuffer cPositions,
            ShortBuffer gPositions, ShortBuffer tPositions, ByteBuffer buffer)
            {
        //all positions should have same length
        while(aPositions.hasRemaining()){
            positionStrategy.setPosition(aPositions.get(), buffer);
            positionStrategy.setPosition(cPositions.get(), buffer);
            positionStrategy.setPosition(gPositions.get(), buffer);
            positionStrategy.setPosition(tPositions.get(), buffer);
        }
    }


    /**
     * Positions are arrayed A[0]C[0]G[0]T[0]A[1]C[1]G[1]T[1] ...
     */
    @Override
    protected short[][] parseRawPositions(DataInputStream in,
            int numberOfSamples, PositionStrategy positionStrategy)
            throws IOException {
        short[][] positions = new short[4][numberOfSamples];

        for(int i=0; i< numberOfSamples; i++){
            for(int channel=0; channel<4; channel++){
                positions[channel][i]=positionStrategy.getPosition(in);
            }
        }
        return positions;
    }





}
