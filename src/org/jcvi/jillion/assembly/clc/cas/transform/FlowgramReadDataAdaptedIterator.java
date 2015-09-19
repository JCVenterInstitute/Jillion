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
package org.jcvi.jillion.assembly.clc.cas.transform;

import java.io.File;
import java.net.URI;

import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.sff.SffFlowgram;

class FlowgramReadDataAdaptedIterator implements StreamingIterator<ReadData>{
	private final StreamingIterator<? extends SffFlowgram> flowgramIterator;
	private final URI sffFileUri;
	public FlowgramReadDataAdaptedIterator(StreamingIterator<? extends SffFlowgram> flowgramIterator, File sffFile){
		this.flowgramIterator = flowgramIterator;	
		this.sffFileUri = sffFile.toURI();
	}
	@Override
	public boolean hasNext() {
		return flowgramIterator.hasNext();
	}

	@Override
	public ReadData next() {
		SffFlowgram nextFlowgram = flowgramIterator.next();
		return new ReadData.Builder(nextFlowgram)
										.setUri(sffFileUri)
										.build();
	
	}

	@Override
	public void remove() {
		flowgramIterator.remove();
		
	}
	@Override
	public void close() {
	   flowgramIterator.close();
		
	}

}
