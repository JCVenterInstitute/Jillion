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
 * Created on Oct 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.sanger.chromat;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.util.ObjectsUtil;
import org.jcvi.jillion.trace.sanger.chromat.Channel;
import org.jcvi.jillion.trace.sanger.chromat.ChannelGroup;

public class DefaultChannelGroup implements ChannelGroup {

    private final DefaultChannel aChannel;
    private final DefaultChannel cChannel;
    private final DefaultChannel gChannel;
    private final DefaultChannel tChannel;


    /**
     * @param channel
     * @param channel2
     * @param channel3
     * @param channel4
     */
    public DefaultChannelGroup(DefaultChannel aChannel, DefaultChannel cChannel, DefaultChannel gChannel,
            DefaultChannel tChannel) {


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
    public DefaultChannel getAChannel() {
        return aChannel;
    }
    /**
     * @return the cChannel
     */
    public DefaultChannel getCChannel() {
        return cChannel;
    }
    /**
     * @return the gChannel
     */
    public DefaultChannel getGChannel() {
        return gChannel;
    }
    /**
     * @return the tChannel
     */
    public DefaultChannel getTChannel() {
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
