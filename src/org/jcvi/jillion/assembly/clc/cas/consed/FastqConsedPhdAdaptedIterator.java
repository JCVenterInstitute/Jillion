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
import org.jcvi.jillion.trace.fastq.FastqRecord;


class FastqConsedPhdAdaptedIterator implements StreamingIterator<PhdReadRecord>{

	private final StreamingIterator<? extends FastqRecord> fastqIterator;
	private final Map<String,String> requiredComments;
	private final Date phdDate;
	private final File fastqFile;
	public FastqConsedPhdAdaptedIterator(StreamingIterator<? extends FastqRecord> fastqIterator,  File fastqFile,Date phdDate ){
		this.requiredComments = PhdUtil.createPhdTimeStampCommentFor(phdDate);
		this.fastqIterator = fastqIterator;	
		this.phdDate = new Date(phdDate.getTime());
		this.fastqFile = fastqFile;
	}
	@Override
	public boolean hasNext() {
		return fastqIterator.hasNext();
	}

	@Override
	public PhdReadRecord next() {
		FastqRecord nextFastq = fastqIterator.next();
		String id = nextFastq.getId();
		Phd phd = new PhdBuilder(id, nextFastq.getNucleotideSequence(),nextFastq.getQualitySequence())
							.comments(requiredComments)
							.fakePeaks()
							.build();
		
		PhdInfo info = ConsedUtil.generateDefaultPhdInfoFor(fastqFile, id, phdDate);
		return new PhdReadRecord(phd, info);
	}

	@Override
	public void remove() {
		fastqIterator.remove();
		
	}
	@Override
	public void close(){
		fastqIterator.close();
		
	}
}
