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
package org.jcvi.jillion.assembly.consed;

import java.io.IOException;
import java.util.Date;

import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.ace.WholeAssemblyAceTag;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;

interface PhdConsedTransformerHelper {

	PhdInfo writePhd(Phd phd, Date phdDate) throws IOException;
	
	PhdDataStore createDataStore()  throws IOException ;

	WholeAssemblyAceTag createPhdBallWholeAssemblyTag();
}
