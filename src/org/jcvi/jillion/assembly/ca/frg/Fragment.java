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
 * Created on Mar 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ca.frg;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.trace.Trace;

public interface Fragment extends Trace{

    Range getValidRange();
    Range getVectorClearRange();
    String getComment();
    Library getLibrary();
}
