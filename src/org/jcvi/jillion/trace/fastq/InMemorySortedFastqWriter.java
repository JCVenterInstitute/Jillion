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
package org.jcvi.jillion.trace.fastq;

import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

class InMemorySortedFastqWriter implements FastqWriter{

	private final FastqWriter writer;
	
	private final Set<FastqRecord> sorted;
	private volatile boolean closed;
	
	public InMemorySortedFastqWriter(FastqWriter writer, Comparator<FastqRecord> comparator){
		Objects.requireNonNull(writer);
		Objects.requireNonNull(comparator);
		this.writer = writer;
		sorted = new ConcurrentSkipListSet<FastqRecord>(comparator);
	}
	@Override
	public synchronized void close() throws IOException {
		if(closed){
			return;
		}
		closed=true;
		try{
			for(FastqRecord record : sorted){
				writer.write(record);
			}
		}finally{
			sorted.clear();
			IOUtil.closeAndIgnoreErrors(writer);
		}
	}
	
	private void checkNotClosed() throws IOException{
		if(closed){
			throw new IOException("writer is closed");
		}
	}

	@Override
	public void write(FastqRecord record) throws IOException {
		checkNotClosed();
		sorted.add(record);
		
	}

	@Override
	public void write(String id, NucleotideSequence nucleotides,
			QualitySequence qualities) throws IOException {
		write(FastqRecordBuilder.create(id, nucleotides, qualities).build());
		
	}

	@Override
	public void write(String id, NucleotideSequence sequence,
			QualitySequence qualities, String optionalComment)
			throws IOException {
		write(FastqRecordBuilder.create(id, sequence, qualities, optionalComment).build());
	}

}
