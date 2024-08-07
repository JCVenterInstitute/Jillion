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
 * Created on Mar 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;

final class DefaultPlacedContig implements PlacedContig{
    private final String contigId;
    private final Range range;
    private final Direction direction;
    /**
     * Convenience constructor, defaults direction to {@link Direction#FORWARD}
     * @param id the id of the contig to be placed.
     * @param range the range this contig should be placed on the scaffold.
     */
    public DefaultPlacedContig(String id, Range range){
        this(id,range,Direction.FORWARD);
    }
    /**
     * Constructs a new DefaultPlacedContig.
     * @param id the id of the contig to be placed.
     * @param range the range this contig should be placed on the scaffold.
     * @param direction the direction this contig faces.
     */
    public DefaultPlacedContig(String id, Range range,Direction direction){
    	if(id ==null){
    		throw new NullPointerException("id can not be null");
    	}
    	if(range ==null){
    		throw new NullPointerException("range can not be null");
    	}
    	if(direction ==null){
    		throw new NullPointerException("direction can not be null");
    	}
        contigId = id;
        this.range = range;
        this.direction = direction;
    }
    @Override
    public String getContigId() {
        return contigId;
    }

    @Override
    public long getEnd() {
        return range.getEnd();
    }

    @Override
    public long getLength() {
        return range.getLength();
    }

    @Override
    public long getBegin() {
        return range.getBegin();
    }
    @Override
    public Direction getDirection() {
        return direction;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + contigId.hashCode();
        result = prime * result
                + direction.hashCode();
        result = prime * result + range.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (!(obj instanceof DefaultPlacedContig)){
            return false;
        }
        DefaultPlacedContig other = (DefaultPlacedContig) obj;
        if (!contigId.equals(other.contigId)){
            return false;            
        }
        if (!direction.equals(other.direction)){
            return false;            
        }
        if (!range.equals(other.range)){
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return "DefaultPlacedContig [contigId=" + contigId + ", direction="
                + direction + ", range=" + range + "]";
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return range;
    }

}
