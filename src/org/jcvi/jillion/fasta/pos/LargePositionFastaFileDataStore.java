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
package org.jcvi.jillion.fasta.pos;

import java.io.IOException;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.pos.Position;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.fasta.AbstractLargeFastaFileDataStore;

/**
 * {@code LargeNucleotideSequenceFastaFileDataStore} is an implementation
 * of {@link NucleotideSequenceFastaDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fasta file
 * which can take some time.  It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.jillion.core.datastore.DataStore, int)}.
 * @author dkatzel
 */
final class LargePositionFastaFileDataStore extends AbstractLargeFastaFileDataStore<Position, PositionSequence, PositionFastaRecord> implements PositionFastaDataStore{
    /**
     * Construct a {@link PositionFastaDataStore}
     * for the given Fasta file.
     * @param parser the FastaParser to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static PositionFastaDataStore create(FastaParser parser){
		return create(parser, DataStoreFilters.alwaysAccept(), null);
	}
	 /**
     * Construct a {@link PositionFastaDataStore}
     * for the given Fasta file using the provided filters.
     * @param parser the FastaParser to use, can not be null.
     * @param filter the {@link Predicate} to use to filter by fasta ID, can not be null.
     * @param recordFilter the {@link Predicate} to use to filter by entire {@link PositionFastaRecord} 
     * if null, then no additional record filtering is used.
     * @throws NullPointerException if fastaFile is null.
     */
	public static PositionFastaDataStore create(FastaParser parser, Predicate<String> filter, Predicate<PositionFastaRecord> recordFilter){
		return new LargePositionFastaFileDataStore(parser, filter, recordFilter);
	}
   
    
    public LargePositionFastaFileDataStore(FastaParser parser,
            Predicate<String> filter, Predicate<PositionFastaRecord> recordFilter) {
		super(parser, filter, recordFilter);
	}
	
	@Override
	protected StreamingIterator<PositionFastaRecord> createNewIterator(
			FastaParser parser, Predicate<String> filter, Predicate<PositionFastaRecord> recordIterator) throws DataStoreException {
		 try {
			return DataStoreStreamingIterator.create(this,
			    		LargePositionFastaIterator.createNewIteratorFor(parser,filter,recordIterator));
		} catch (IOException e) {
			throw new DataStoreException("error iterating over fasta file", e);
		}
	}
}

