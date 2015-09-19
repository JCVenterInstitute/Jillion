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
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.util.Date;

import org.jcvi.jillion.core.Rangeable;
/**
 * {@code ReadAceTag} is an {@link AceTag}
 * that maps to a particular location on a specific
 * read of a contig in an ace file.
 * @author dkatzel
 */
public final class ReadAceTag extends AbstractDefaultPlacedAceTag{

    public ReadAceTag(String id, String type, String creator,
            Date creationDate, Rangeable location, boolean isTransient) {
        super(id, type, creator, creationDate, location, null, isTransient);
    }
    /**
     * Get the read id that this tag references.
     */
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return super.getId();
	}

	@Override
	public String toString() {
		return "ReadAceTag [getId()=" + getId() + ", getType()="
				+ getType() + ", getCreator()=" + getCreator()
				+ ", getCreationDate()=" + getCreationDate() + ", asRange()="
				+ asRange() + ", getData()=" + getData() + ", isTransient()="
				+ isTransient() + "]";
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj)){
			return false;
		}
		return obj instanceof ReadAceTag;
	}
    
    

}
