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
import java.util.Arrays;
import java.util.Collections;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.pos.Position;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.internal.trace.chromat.scf.header.pos.PositionStrategy;
import org.jcvi.jillion.internal.trace.chromat.scf.header.pos.PositionStrategyFactory;
import org.jcvi.jillion.trace.chromat.ChannelGroup;
import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;

public abstract class AbstractSampleSectionCodec implements SectionCodec{

    @Override
    public long decode(DataInputStream in, long currentOffset, SCFHeader header,
            ScfChromatogramBuilder c) throws SectionDecoderException {
        int numberOfSamples = header.getNumberOfSamples();
        PositionStrategy positionStrategy = PositionStrategyFactory.getPositionStrategy(header);
        long bytesToSkip = Math.max(0, header.getSampleOffset() - currentOffset);

        try{
            IOUtil.blockingSkip(in,bytesToSkip);
            short[][] positions = parseRawPositions(in, numberOfSamples,
                    positionStrategy);
            if(positions!=null){
                extractActualPositions(positionStrategy, positions);
                setPositions(c, positions);
    
                return currentOffset+bytesToSkip + numberOfSamples*header.getSampleSize()*4;
            }
            return currentOffset+bytesToSkip;
        }
        catch(IOException e){
            throw new SectionDecoderException("error reading version "+header.getVersion() + " samples",e);
        }

    }
    
    @Override
    public long decode(DataInputStream in, long currentOffset, SCFHeader header,
            ChromatogramFileVisitor visitor) throws SectionDecoderException {
        int numberOfSamples = header.getNumberOfSamples();
        PositionStrategy positionStrategy = PositionStrategyFactory.getPositionStrategy(header);
        long bytesToSkip = header.getSampleOffset() - currentOffset;

        try{
            IOUtil.blockingSkip(in,bytesToSkip);
            short[][] positions = parseRawPositions(in, numberOfSamples,
                    positionStrategy);
            if(positions!=null){
                extractActualPositions(positionStrategy, positions);
                visitor.visitAPositions(positions[0]);
                visitor.visitCPositions(positions[1]);
                visitor.visitGPositions(positions[2]);
                visitor.visitTPositions(positions[3]);
    
                return currentOffset+bytesToSkip + numberOfSamples*header.getSampleSize()*4;
            }
            return currentOffset+bytesToSkip;
        }
        catch(IOException e){
            throw new SectionDecoderException("error reading version "+header.getVersion() + " samples",e);
        }

    }

    protected abstract void extractActualPositions(PositionStrategy positionStrategy,
            short[][] positions) ;

    private void setPositions(ScfChromatogramBuilder c, short[][] positions) {
        c.aPositions(positions[0])
        .cPositions(positions[1])
        .gPositions(positions[2])
        .tPositions(positions[3]);
    }

    protected abstract short[][] parseRawPositions(DataInputStream in,
            int numberOfSamples, PositionStrategy positionStrategy)
            throws IOException ;

    protected PositionStrategy getPositionStrategyFor(ScfChromatogram c){
        return PositionStrategyFactory.getPositionStrategy(getMaxPositionsValue(c));
    }

    private int getMaxPositionsValue(ScfChromatogram c) {
       ChannelGroup group= c.getChannelGroup();
        PositionSequence aPositions =group.getAChannel().getPositions();
        PositionSequence cPositions =group.getCChannel().getPositions();
        PositionSequence gPositions =group.getGChannel().getPositions();
        PositionSequence tPositions =group.getTChannel().getPositions();       
        int max =Collections.max(Arrays.asList(
                    getMaxValueFor(aPositions),
                    getMaxValueFor(cPositions),
                    getMaxValueFor(gPositions),
                    getMaxValueFor(tPositions)
                    ));       
        return max;
    }
    private int getMaxValueFor(PositionSequence positions) {
        int currentMax = Integer.MIN_VALUE;
        for(Position position : positions){        	
            int value = position.getValue();
            if(value > currentMax){
                currentMax = value;
            }
        }
        return currentMax;
    }
   

    @Override
    public EncodedSection encode(ScfChromatogram c, SCFHeader header)
            throws IOException {

        PositionStrategy positionStrategy =getPositionStrategyFor(c);
        final ChannelGroup channelGroup = c.getChannelGroup();
        PositionSequence aPositions =channelGroup.getAChannel().getPositions();
        PositionSequence cPositions =channelGroup.getCChannel().getPositions();
        PositionSequence gPositions =channelGroup.getGChannel().getPositions();
        PositionSequence tPositions =channelGroup.getTChannel().getPositions();
        byte sampleSize =positionStrategy.getSampleSize();
        final int numberOfSamples = (int)aPositions.getLength();

        final int bufferLength = numberOfSamples*4*sampleSize;

        ByteBuffer buffer = ByteBuffer.allocate(bufferLength);
        writePositionsToBuffer(positionStrategy, aPositions, cPositions,
                gPositions, tPositions, buffer);

        buffer.flip();
        header.setNumberOfSamples(numberOfSamples);
        header.setSampleSize(sampleSize);
        return new EncodedSection(buffer, Section.SAMPLES);
    }

    protected abstract void writePositionsToBuffer(PositionStrategy positionStrategy,
    		PositionSequence aPositions, PositionSequence cPositions,
    		PositionSequence gPositions, PositionSequence tPositions, ByteBuffer buffer);

}
