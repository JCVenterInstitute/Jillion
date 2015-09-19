/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.internal.trace.chromat.scf.SCFUtils;
import org.jcvi.jillion.internal.trace.chromat.scf.header.pos.PositionStrategy;
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
