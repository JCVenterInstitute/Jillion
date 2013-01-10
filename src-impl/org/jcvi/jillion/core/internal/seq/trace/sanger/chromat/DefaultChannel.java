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
 * Created on Oct 23, 2007
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.internal.seq.trace.sanger.chromat;


import org.jcvi.common.core.seq.trace.sanger.PositionSequence;
import org.jcvi.common.core.seq.trace.sanger.PositionSequenceBuilder;
import org.jcvi.common.core.seq.trace.sanger.chromat.Channel;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.util.ObjectsUtil;


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
     * <code>new Channel(ByteBuffer.wrap(confidence), ShortBuffer.wrap(positions))</code>
     * </p>
     * @param confidence
     * @param positions
     */
    public DefaultChannel(byte[] confidence, short[] positions){
        this(new QualitySequenceBuilder(confidence).build(),
        		new PositionSequenceBuilder(positions).build());
    }
    /**
     * Constructs a newly allocated {@link DefaultChannel} with the provided
     * values for confidence and positions.
     * @param confidence
     * @param positions
     */
    public DefaultChannel(QualitySequence confidence, PositionSequence positions) {
    	if(confidence ==null){
    		throw new NullPointerException("qualities can not be null");
    	}
    	if(positions ==null){
    		throw new NullPointerException("positions can not be null");
    	}
        this.confidence = confidence;
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
