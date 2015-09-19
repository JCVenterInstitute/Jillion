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
package org.jcvi.jillion.assembly.util.slice;

import java.util.List;
import java.util.stream.Stream;

import org.jcvi.jillion.core.Sequence;

public interface VariableWidthSlice<T, S extends Sequence<T>> {

	Stream<? extends VariableWidthSliceElement<T>> elements();
	
	int getCoverageDepth();
	
	int getSliceLength();
	
	int getCountFor(List<T> sliceElementSeq);
	
	S getGappedReferenceSequence();
}
