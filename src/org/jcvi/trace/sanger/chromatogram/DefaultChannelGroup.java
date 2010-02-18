/*
 * Created on Oct 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram;

import org.jcvi.CommonUtil;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public class DefaultChannelGroup implements ChannelGroup {

    private Channel aChannel;
    private Channel cChannel;
    private Channel gChannel;
    private Channel tChannel;


    /**
     * @param channel
     * @param channel2
     * @param channel3
     * @param channel4
     */
    public DefaultChannelGroup(Channel aChannel, Channel cChannel, Channel gChannel,
            Channel tChannel) {


        this.aChannel = aChannel;
        this.cChannel = cChannel;
        this.gChannel = gChannel;
        this.tChannel = tChannel;
        validate();
    }
    private void validate() {
        channelsCannotBeNull();
        confidencesMustHaveSameLength();
        positionsMustHaveSameLength();

    }
    private void channelsCannotBeNull() {
        String errorMessage = "channels can not be null";
        CommonUtil.cannotBeNull(aChannel, errorMessage);
        CommonUtil.cannotBeNull(cChannel, errorMessage);
        CommonUtil.cannotBeNull(gChannel, errorMessage);
        CommonUtil.cannotBeNull(tChannel, errorMessage);
    }
    private void positionsMustHaveSameLength() {
        int posLength = aChannel.getPositions().array().length;
        if(posLength !=cChannel.getPositions().array().length ||
                posLength !=gChannel.getPositions().array().length ||
                posLength !=tChannel.getPositions().array().length ){
                throw new IllegalArgumentException("positions must all have the same length");
            }
    }
    private void confidencesMustHaveSameLength() {
        int confidenceLength = aChannel.getConfidence().getData().length;
        if(confidenceLength !=cChannel.getConfidence().getData().length ||
            confidenceLength !=gChannel.getConfidence().getData().length ||
            confidenceLength !=tChannel.getConfidence().getData().length ){
            throw new IllegalArgumentException("confidences must all have the same length");
        }
    }
    /**
     * @return the aChannel
     */
    public Channel getAChannel() {
        return aChannel;
    }
    /**
     * @return the cChannel
     */
    public Channel getCChannel() {
        return cChannel;
    }
    /**
     * @return the gChannel
     */
    public Channel getGChannel() {
        return gChannel;
    }
    /**
     * @return the tChannel
     */
    public Channel getTChannel() {
        return tChannel;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result +  aChannel.hashCode();
        result = prime * result +  cChannel.hashCode();
        result = prime * result + gChannel.hashCode();
        result = prime * result + tChannel.hashCode();
        return result;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof DefaultChannelGroup)){
            return false;
        }
        final ChannelGroup other = (ChannelGroup) obj;
        return CommonUtil.similarTo(getAChannel(), other.getAChannel())
        && CommonUtil.similarTo(getCChannel(), other.getCChannel())
        && CommonUtil.similarTo(getGChannel(), other.getGChannel())
        && CommonUtil.similarTo(getTChannel(), other.getTChannel());

    }
    @Override
    public Channel getChannel(NucleotideGlyph nucleotide) {
        if(nucleotide == NucleotideGlyph.Adenine){
           return getAChannel();
        }
        if(nucleotide == NucleotideGlyph.Cytosine){
            return getCChannel();
         }
        if(nucleotide == NucleotideGlyph.Guanine){
            return getGChannel();
         }
        //anything else is considered a T
        return getTChannel();
        
    }

}
