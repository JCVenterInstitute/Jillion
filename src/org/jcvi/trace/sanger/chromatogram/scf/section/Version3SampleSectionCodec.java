/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.jcvi.trace.sanger.chromatogram.scf.SCFUtils;
import org.jcvi.trace.sanger.chromatogram.scf.position.PositionStrategy;
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
            ShortBuffer aPositions, ShortBuffer cPositions,
            ShortBuffer gPositions, ShortBuffer tPositions, ByteBuffer buffer)
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
