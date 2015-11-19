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
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

final class DefaultCasReferenceDescription implements CasReferenceDescription {
    private final boolean isCircular;
    private final long contigLength;
    
    
    public DefaultCasReferenceDescription(long contigLength, boolean isCircular) {
        this.contigLength = contigLength;
        this.isCircular = isCircular;
    }

    @Override
    public long getContigLength() {
        return contigLength;
    }

    @Override
    public boolean isCircular() {
        return isCircular;
    }

    @Override
    public String toString() {
        return "DefaultCasContigDescription [contigLength=" + contigLength
                + ", isCircular=" + isCircular + "]";
    }

}
