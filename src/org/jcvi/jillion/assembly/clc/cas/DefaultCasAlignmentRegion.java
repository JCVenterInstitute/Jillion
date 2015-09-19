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
 * Created on Oct 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

final class DefaultCasAlignmentRegion implements CasAlignmentRegion{

    private final CasAlignmentRegionType type;
    private final long length;
    
    
    /**
     * @param type
     * @param length
     */
    public DefaultCasAlignmentRegion(CasAlignmentRegionType type, long length) {
        if(type ==null){
            throw new NullPointerException("type can not be null");
        }
        if(length <0){
            throw new IllegalArgumentException("length can not < 0 : "+ length);
        }
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
        result = prime * result + type.hashCode();
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
       return type.equals(other.type);
    }

}
