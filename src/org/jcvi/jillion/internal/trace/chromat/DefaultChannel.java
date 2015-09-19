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
      return  ObjectsUtil.nullSafeEquals(getQualitySequence(), other.getQualitySequence())
               && ObjectsUtil.nullSafeEquals(getPositionSequence(), other.getPositionSequence());
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
        result = prime * result + (getQualitySequence() == null? 0: getQualitySequence().hashCode());
        result = prime * result + ((getPositionSequence() == null) ? 0 :getPositionSequence().hashCode());

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
	public QualitySequence getQualitySequence() {
        return confidence;
    }
    /**
	 * {@inheritDoc}
	 */
    @Override
	public PositionSequence getPositionSequence() {
        return positions;
    }


}
