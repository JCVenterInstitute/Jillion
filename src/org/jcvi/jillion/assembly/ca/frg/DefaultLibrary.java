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
 * Created on Mar 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ca.frg;


public class DefaultLibrary implements Library{

    private final String id;
    private final MateOrientation mateOrientation;
    private final Distance distance;
    
    
    /**
     * Create a new Library instance.
     * 
     * @param id the library's Id.
     * @param distance the Library's {@link Distance};
     * can not be null.
     * @param mateOrientation the {@link MateOrientation};
     * can not be null.
     * @throws NullPointerException if any parameters are null.
     */
    public DefaultLibrary(String id, Distance distance,
            MateOrientation mateOrientation) {
        if(id==null || distance == null || mateOrientation ==null){
            throw new NullPointerException("can not have null fields");
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
        if (this == obj){
            return true;
        }
        if (!(obj instanceof DefaultLibrary)){
            return false;
        }
        DefaultLibrary other = (DefaultLibrary) obj;
        return id.equals(other.id);
    }

    
}
