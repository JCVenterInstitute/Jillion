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
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.core.Range;

class DefaultAceBaseSegment implements AceBaseSegment{

    private final String name;
    private final Range gappedConsensusRange;
    
    /**
     * @param name
     * @param gappedConsensusRange
     * @throws NullPointerException if either parameter is null.
     */
    public DefaultAceBaseSegment(String name, Range gappedConsensusRange) {
        if(name ==null || gappedConsensusRange==null){
            throw new NullPointerException("parameters can not be null");
        }
        this.name = name;
        this.gappedConsensusRange = gappedConsensusRange;
    }

    @Override
    public Range getGappedConsensusRange() {
        return gappedConsensusRange;
    }

    @Override
    public String getReadName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                +  gappedConsensusRange
                        .hashCode();
        result = prime * result + name.hashCode();
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
        if (!(obj instanceof AceBaseSegment)) {
            return false;
        }
        AceBaseSegment other = (AceBaseSegment) obj;
       return getReadName().equals(other.getReadName()) 
       && getGappedConsensusRange().equals(other.getGappedConsensusRange());
    }

    @Override
    public String toString() {
        return "DefaultAceBaseSegment [name=" + name
                + ", gappedConsensusRange=" + gappedConsensusRange + "]";
    }

}
