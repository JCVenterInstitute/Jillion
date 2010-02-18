/*
 * Created on Jan 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;

import org.jcvi.assembly.Placed;

public abstract class AbstractDefaultPlacedAceTag extends AbstractDefaultAceTag implements PlacedAceTag{
    private final String id;
    
    private final Placed location;
    private final boolean isTransient;
    
    /**
     * @param id
     * @param type
     * @param creator
     * @param creationDate
     * @param location
     * @param data
     */
    public AbstractDefaultPlacedAceTag(String id, String type, String creator,
            Date creationDate, Placed location, String data,boolean isTransient) {
        super(type, creator, creationDate, data);
        this.id = id;
        this.location = location;
        this.isTransient = isTransient;
    }

   

    @Override
    public String getId() {
        return id;
    }


    @Override
    public long getEnd() {
        return location.getEnd();
    }

    @Override
    public long getLength() {
        return location.getLength();
    }

    @Override
    public long getStart() {
        return location.getStart();
    }

    @Override
    public boolean isTransient() {
        return isTransient;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (isTransient ? 1231 : 1237);
        result = prime * result
                + ((location == null) ? 0 : location.hashCode());
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof AbstractDefaultPlacedAceTag)) {
            return false;
        }
        AbstractDefaultPlacedAceTag other = (AbstractDefaultPlacedAceTag) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (isTransient != other.isTransient) {
            return false;
        }
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        return true;
    }
    

}
