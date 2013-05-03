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
 * Created on Oct 23, 2007
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat;


import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.util.ObjectsUtil;
import org.jcvi.jillion.trace.chromat.Channel;


/**
 * <code>Channel</code> represents the
 * data from a single trace channel (lane).
 * @author dkatzel
 *
 */
public final class DefaultChannel implements Channel{
	 private final QualitySequence confidence;
	    private final PositionSequence positions;
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    /**
	 * {@inheritDoc}
	 */
	@Override
    public boolean equals(Object obj) {
       if(obj == this){
           return true;
       }
       if(!(obj instanceof DefaultChannel)){
           return false;
       }
       Channel other = (Channel) obj;
      return  ObjectsUtil.nullSafeEquals(getConfidence(), other.getConfidence())
               && ObjectsUtil.nullSafeEquals(getPositions(), other.getPositions());
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    /**
	 * {@inheritDoc}
	 */
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (getConfidence() == null? 0: getConfidence().hashCode());
        result = prime * result + ((getPositions() == null) ? 0 :getPositions().hashCode());

        return result;
    }


    /**
     * Constructs a newly allocated {@link DefaultChannel} with the provided
     * values for confidence and positions.
     * <p>
     *  In other words, this method returns an {@link DefaultChannel} object equal to the value of:
     * <br>
     * <code>new Channel(new QualitySequenceBuilder(qualities).build(), 
     * new PositionSequenceBuilder(positions).build())</code>
     * </p>
     * @param qualities the qualities of this channel stored as
     * as one quality value per byte in a byte array; 
     * can not be null.
     * @param positions the sanger positions of this channel stored as
     * as one position value per short in a short array; 
     * can not be null.
     * @throws NullPointerException if either parameter is null.
     */
    public DefaultChannel(byte[] qualities, short[] positions){
        this(new QualitySequenceBuilder(qualities).build(),
        		new PositionSequenceBuilder(positions).build());
    }
    /**
     * Constructs a newly allocated {@link DefaultChannel} with the provided
     * values for confidence and positions.
     * @param qualities the {@link QualitySequence} for this channel,
     * can not be null.
     * @param positions the {@link PositionSequence} for this channel,
     * can not be null.
     * @throws NullPointerException if either parameter is null.
     */
    public DefaultChannel(QualitySequence qualities, PositionSequence positions) {
    	if(qualities ==null){
    		throw new NullPointerException("qualities can not be null");
    	}
    	if(positions ==null){
    		throw new NullPointerException("positions can not be null");
    	}
        this.confidence = qualities;
        this.positions = positions;
    }
    /**
	 * {@inheritDoc}
	 */
    @Override
	public QualitySequence getConfidence() {
        return confidence;
    }
    /**
	 * {@inheritDoc}
	 */
    @Override
	public PositionSequence getPositions() {
        return positions;
    }


}
