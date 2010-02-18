/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.jcvi.trace.sanger.chromatogram.scf.position.PositionStrategy;


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
