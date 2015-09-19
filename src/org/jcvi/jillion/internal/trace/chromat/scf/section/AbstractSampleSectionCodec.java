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
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;
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

    protected PositionStrategy getPositionStrategyFor(Chromatogram c){
        return PositionStrategyFactory.getPositionStrategy(getMaxPositionsValue(c));
    }

    private int getMaxPositionsValue(Chromatogram c) {
       ChannelGroup group= c.getChannelGroup();
        PositionSequence aPositions =group.getAChannel().getPositionSequence();
        PositionSequence cPositions =group.getCChannel().getPositionSequence();
        PositionSequence gPositions =group.getGChannel().getPositionSequence();
        PositionSequence tPositions =group.getTChannel().getPositionSequence();       
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
    public EncodedSection encode(Chromatogram c, SCFHeader header)
            throws IOException {

        PositionStrategy positionStrategy =getPositionStrategyFor(c);
        final ChannelGroup channelGroup = c.getChannelGroup();
        PositionSequence aPositions =channelGroup.getAChannel().getPositionSequence();
        PositionSequence cPositions =channelGroup.getCChannel().getPositionSequence();
        PositionSequence gPositions =channelGroup.getGChannel().getPositionSequence();
        PositionSequence tPositions =channelGroup.getTChannel().getPositionSequence();
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
