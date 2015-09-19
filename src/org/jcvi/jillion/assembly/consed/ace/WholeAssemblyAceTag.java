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
/**
 * A {@link WholeAssemblyAceTag} is an {@link AceTag}
 * that applies to the entire assembly in the ace file
 * usually these types of tags refer to things like
 * where the phdball is located. Other uses of 
 * Whole Assembly tags are to store version information
 * about how this assembly was made.
 * @author dkatzel
 *
 *
 */
public final class WholeAssemblyAceTag extends AbstractDefaultAceTag {

    
    /**
     * Create a new {@link WholeAssemblyAceTag}.
     * @param type the type of tag.
     * @param creator who (or which program) created this tag.
     * @param creationDate when this tag was created.
     * @param data the data for this tag.
     */
    public WholeAssemblyAceTag(String type, String creator,
            Date creationDate, String data) {
        super(type, creator, creationDate, data);
    }

    


    @Override
    public int hashCode() {
       return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
    	
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof WholeAssemblyAceTag)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "DefaultWholeAssemblyAceTag [creationDate=" + getCreationDate()
                + ", creator=" + getCreator() + ", data=" + getData() + ", type=" + getType()
                + "]";
    }

}
