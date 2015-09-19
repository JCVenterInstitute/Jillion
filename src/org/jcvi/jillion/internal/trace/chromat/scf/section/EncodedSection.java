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
 * Created on Sep 15, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.section;

import java.nio.ByteBuffer;

public class EncodedSection {
    private final ByteBuffer data;
    private final Section section;
    
    public EncodedSection(ByteBuffer data, Section section) {
        this.data = data;
        this.section = section;
    }
    /**
     * @return the data
     */
    public ByteBuffer getData() {
        return data;
    }
    /**
     * @return the section
     */
    public Section getSection() {
        return section;
    }


}
