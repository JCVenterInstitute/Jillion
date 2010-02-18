/*
 * Created on Sep 12, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Collections;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.sanger.chromatogram.ChannelGroup;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.position.PositionStrategy;
import org.jcvi.trace.sanger.chromatogram.scf.position.PositionStrategyFactory;

public abstract class AbstractSampleSectionCodec implements SectionCodec{

    @Override
    public long decode(DataInputStream in, long currentOffset, SCFHeader header,
            SCFChromatogramBuilder c) throws SectionDecoderException {
        int numberOfSamples = header.getNumberOfSamples();
        PositionStrategy positionStrategy = PositionStrategyFactory.getPositionStrategy(header);
        long bytesToSkip = header.getSampleOffset() - currentOffset;

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

    protected abstract void extractActualPositions(PositionStrategy positionStrategy,
            short[][] positions) ;

    private void setPositions(SCFChromatogramBuilder c, short[][] positions) {
        c.aPositions(positions[0])
        .cPositions(positions[1])
        .gPositions(positions[2])
        .tPositions(positions[3]);
    }

    protected abstract short[][] parseRawPositions(DataInputStream in,
            int numberOfSamples, PositionStrategy positionStrategy)
            throws IOException ;

    protected PositionStrategy getPositionStrategyFor(SCFChromatogram c){
        return PositionStrategyFactory.getPositionStrategy(getMaxPositionsValue(c));
    }

    private int getMaxPositionsValue(SCFChromatogram c) {
       ChannelGroup group= c.getChannelGroup();
        ShortBuffer aPositions =group.getAChannel().getPositions();
        ShortBuffer cPositions =group.getCChannel().getPositions();
        ShortBuffer gPositions =group.getGChannel().getPositions();
        ShortBuffer tPositions =group.getTChannel().getPositions();
        //rewind all buffers
        aPositions.rewind();
        cPositions.rewind();
        gPositions.rewind();
        tPositions.rewind();
        int max =Collections.max(Arrays.asList(
                    getMaxValueFor(aPositions),
                    getMaxValueFor(cPositions),
                    getMaxValueFor(gPositions),
                    getMaxValueFor(tPositions)
                    ));
        //rewind all buffers
        aPositions.rewind();
        cPositions.rewind();
        gPositions.rewind();
        tPositions.rewind();
        return max;
    }

    private int getMaxValueFor(ShortBuffer buffer) {
        int currentMax = Integer.MIN_VALUE;
        while(buffer.hasRemaining()){
            short value = buffer.get();
            if(value > currentMax){
                currentMax = value;
            }
        }
        return currentMax;
    }

    @Override
    public EncodedSection encode(SCFChromatogram c, SCFHeader header)
            throws IOException {

        PositionStrategy positionStrategy =getPositionStrategyFor(c);
        final ChannelGroup channelGroup = c.getChannelGroup();
        ShortBuffer aPositions =channelGroup.getAChannel().getPositions();
        ShortBuffer cPositions =channelGroup.getCChannel().getPositions();
        ShortBuffer gPositions =channelGroup.getGChannel().getPositions();
        ShortBuffer tPositions =channelGroup.getTChannel().getPositions();
        byte sampleSize =positionStrategy.getSampleSize();
        final int numberOfSamples = aPositions.limit();

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
            ShortBuffer aPositions, ShortBuffer cPositions,
            ShortBuffer gPositions, ShortBuffer tPositions, ByteBuffer buffer);

}
