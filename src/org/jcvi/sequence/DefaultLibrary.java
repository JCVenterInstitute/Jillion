/*
 * Created on Mar 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import org.jcvi.Distance;

public class DefaultLibrary implements Library{

    private final String id;
    private final MateOrientation mateOrientation;
    private final Distance distance;
    
    
    /**
     * @param id
     * @param distance
     * @param mateOrientation
     */
    public DefaultLibrary(String id, Distance distance,
            MateOrientation mateOrientation) {
        if(id==null || distance == null || mateOrientation ==null){
            throw new IllegalArgumentException("can not have null fields");
        }
        this.id = id;
        this.distance = distance;
        this.mateOrientation = mateOrientation;
    }

    @Override
    public Distance getDistance() {
        return distance;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public MateOrientation getMateOrientation() {
        return mateOrientation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof DefaultLibrary))
            return false;
        DefaultLibrary other = (DefaultLibrary) obj;
        return id.equals(other.id);
    }

    
}
