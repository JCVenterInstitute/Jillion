/*
 * Created on Oct 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.alignment;

public class DefaultCasAlignmentRegion implements CasAlignmentRegion{

    private final CasAlignmentRegionType type;
    private final long length;
    
    
    /**
     * @param type
     * @param length
     */
    public DefaultCasAlignmentRegion(CasAlignmentRegionType type, long length) {
        this.type = type;
        this.length = length;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public CasAlignmentRegionType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "DefaultCasAlignmentRegion [type=" + type + ", length=" + length
                + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (length ^ (length >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultCasAlignmentRegion)) {
            return false;
        }
        DefaultCasAlignmentRegion other = (DefaultCasAlignmentRegion) obj;
        if (length != other.length) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

}
