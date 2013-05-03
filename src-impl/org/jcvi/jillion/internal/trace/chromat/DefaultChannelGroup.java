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
 * Created on Oct 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.util.ObjectsUtil;
import org.jcvi.jillion.trace.chromat.Channel;
import org.jcvi.jillion.trace.chromat.ChannelGroup;
/**
 * {@code DefaultChannelGroup} is a default implementation
 * of {@link ChannelGroup}.
 * 
 * For internal use only.
 * @author dkatzel
 *
 */
public class DefaultChannelGroup implements ChannelGroup {

    private final Channel aChannel;
    private final Channel cChannel;
    private final Channel gChannel;
    private final Channel tChannel;


    /**
     * Create a new DefaultChannelGroup instance with the given Channels.
     * @param aChannel the {@link Channel} representing the "A" track.
     * @param cChannel the {@link Channel} representing the "C" track.
     * @param gChannel the {@link Channel} representing the "G" track.
     * @param tChannel the {@link Channel} representing the "T" track.
     * @throws NullPointerException if any channel is null.
     * @throws IllegalArgumentException if all the qualities in all the tracks
     * are not the same length, or if all the positions in the all the tracks
     * are not the same length.
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
        ObjectsUtil.checkNotNull(aChannel, errorMessage);
        ObjectsUtil.checkNotNull(cChannel, errorMessage);
        ObjectsUtil.checkNotNull(gChannel, errorMessage);
        ObjectsUtil.checkNotNull(tChannel, errorMessage);
    }
    private void positionsMustHaveSameLength() {
        long posLength = aChannel.getPositions().getLength();
        if(posLength !=cChannel.getPositions().getLength()
        		 || posLength !=gChannel.getPositions().getLength()
        		|| posLength !=tChannel.getPositions().getLength() ){
                throw new IllegalArgumentException("positions must all have the same length");
            }
    }
    private void confidencesMustHaveSameLength() {
        long confidenceLength = aChannel.getConfidence().getLength();
        if(confidenceLength !=cChannel.getConfidence().getLength() 
        	|| confidenceLength !=gChannel.getConfidence().getLength()
        	|| confidenceLength !=tChannel.getConfidence().getLength() ){
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
        if (this == obj){
            return true;
        }
        if (!(obj instanceof DefaultChannelGroup)){
            return false;
        }
        final ChannelGroup other = (ChannelGroup) obj;
        return ObjectsUtil.nullSafeEquals(getAChannel(), other.getAChannel())
        && ObjectsUtil.nullSafeEquals(getCChannel(), other.getCChannel())
        && ObjectsUtil.nullSafeEquals(getGChannel(), other.getGChannel())
        && ObjectsUtil.nullSafeEquals(getTChannel(), other.getTChannel());

    }
    @Override
    public Channel getChannel(Nucleotide channelToGet) {
    	if(channelToGet==null){
    		throw new NullPointerException("channel to get can not be null");
    	}
        if(channelToGet == Nucleotide.Adenine){
           return getAChannel();
        }
        if(channelToGet == Nucleotide.Cytosine){
            return getCChannel();
         }
        if(channelToGet == Nucleotide.Guanine){
            return getGChannel();
         }
        //anything else is considered a T
        return getTChannel();
        
    }

}
