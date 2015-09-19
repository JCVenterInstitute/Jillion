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
import org.jcvi.jillion.trace.fastq.FastqRecord;


class FastqReadDatadAdaptedIterator implements StreamingIterator<ReadData>{

	private final StreamingIterator<? extends FastqRecord> fastqIterator;
	private final URI fastqFileUri;
	public FastqReadDatadAdaptedIterator(StreamingIterator<? extends FastqRecord> fastqIterator,  File fastqFile ){
		this.fastqIterator = fastqIterator;	
		this.fastqFileUri = fastqFile.toURI();
	}
	@Override
	public boolean hasNext() {
		return fastqIterator.hasNext();
	}

	@Override
	public ReadData next() {
		FastqRecord nextFastq = fastqIterator.next();
		return new ReadData.Builder(nextFastq)
							.setUri(fastqFileUri)
							.build();
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
