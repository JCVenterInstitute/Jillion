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

import java.util.Map;

import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;

public interface AceAssemblyTransformerPostProcessor {

	Map<String, AceContigBuilder> postProcess(Map<String, AceContigBuilder> builderMap, PhdDataStore phdDataStore);

	void postProcess(AceContig contig);
}
