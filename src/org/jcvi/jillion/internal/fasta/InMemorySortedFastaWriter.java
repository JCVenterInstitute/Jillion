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
package org.jcvi.jillion.internal.fasta;

import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.FastaRecord;
import org.jcvi.jillion.fasta.FastaWriter;

public abstract class InMemorySortedFastaWriter<S, T extends Sequence<S>, F extends FastaRecord<S,T>> implements FastaWriter<S,T,F>{

	private final FastaWriter<S,T,F> writer;
	
	private final Set<F> sorted;
	private volatile boolean closed;
	
	public InMemorySortedFastaWriter(FastaWriter<S,T,F> writer, Comparator<F> comparator){
		Objects.requireNonNull(writer);
		Objects.requireNonNull(comparator);
		this.writer = writer;
		//TODO make performance better
		//is it worth just using a list unsorted
		//and then make a copy to a sorted array to write out?
		//would be faster than using concurrentskip list but take 2x the memory
		sorted = new TreeSet<F>(comparator);
	}
	@Override
	public synchronized void close() throws IOException {
		if(closed){
			return;
		}
		closed=true;
		try{
			for(F record : sorted){
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
	public synchronized void write(F record) throws IOException {
		checkNotClosed();
		sorted.add(record);
		
	}

	@Override
	public void write(String id, T sequence) throws IOException {
		write(id, sequence, null);
		
	}

	@Override
	public void write(String id, T sequence,String optionalComment)
			throws IOException {
		write(createRecord(id, sequence, optionalComment));
	}
	
	protected abstract F createRecord(String id, T sequence,String optionalComment);

}
