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
package org.jcvi.jillion.internal.trace.chromat.scf.section;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.pos.Position;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.internal.trace.chromat.scf.header.pos.PositionStrategy;


public class Version2SampleSectionCodec extends AbstractSampleSectionCodec {

    @Override
    protected void extractActualPositions(PositionStrategy positionStrategy,
            short[][] positions) {
        // no-op; version 2 positions are stored uncompressed.

    }

    @Override
    protected void writePositionsToBuffer(PositionStrategy positionStrategy,
            PositionSequence aPositions, PositionSequence cPositions,
            PositionSequence gPositions, PositionSequence tPositions, ByteBuffer buffer)
            {
    	Iterator<Position> aIterator = aPositions.iterator();
    	Iterator<Position> cIterator = cPositions.iterator();
    	Iterator<Position> gIterator = gPositions.iterator();
    	Iterator<Position> tIterator = tPositions.iterator();
    	while(aIterator.hasNext()){
    		  positionStrategy.setPosition(IOUtil.toSignedShort(aIterator.next().getValue()), buffer);
    		  positionStrategy.setPosition(IOUtil.toSignedShort(cIterator.next().getValue()), buffer);
    		  positionStrategy.setPosition(IOUtil.toSignedShort(gIterator.next().getValue()), buffer);
    		  positionStrategy.setPosition(IOUtil.toSignedShort(tIterator.next().getValue()), buffer);
             
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
