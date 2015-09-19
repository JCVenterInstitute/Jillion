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
package org.jcvi.jillion.assembly.clc.cas.consed;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.jcvi.jillion.assembly.consed.ConsedUtil;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.sff.SffFlowgram;

class FlowgramConsedPhdAdaptedIterator implements StreamingIterator<PhdReadRecord>{
	private final StreamingIterator<? extends SffFlowgram> flowgramIterator;
	private final Map<String,String> requiredComments;
	private final Date phdDate;
	private final File sffFile;
	public FlowgramConsedPhdAdaptedIterator(StreamingIterator<? extends SffFlowgram> flowgramIterator, File sffFile, Date phdDate ){
		this.requiredComments = PhdUtil.createPhdTimeStampCommentFor(phdDate);
		this.flowgramIterator = flowgramIterator;	
		this.phdDate = new Date(phdDate.getTime());
		this.sffFile = sffFile;
	}
	@Override
	public boolean hasNext() {
		return flowgramIterator.hasNext();
	}

	@Override
	public PhdReadRecord next() {
		SffFlowgram nextFlowgram = flowgramIterator.next();
		String id = nextFlowgram.getId();
		Phd phd= new PhdBuilder(id, nextFlowgram.getNucleotideSequence(), nextFlowgram.getQualitySequence())
						.comments(requiredComments)
						.fakePeaks()
						.build();
		
		PhdInfo phdInfo = ConsedUtil.generateDefaultPhdInfoFor(sffFile, id, phdDate);
		return new PhdReadRecord(phd,phdInfo);
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
