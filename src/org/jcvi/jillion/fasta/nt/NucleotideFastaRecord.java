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
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaRecord;
/**
 * {@code NucleotideFastaRecord} is an implementation
 * of {@link FastaRecord} whose sequences are {@link NucleotideSequence}s.
 * @author dkatzel
 *
 */
public interface NucleotideFastaRecord extends FastaRecord<Nucleotide,NucleotideSequence>{
    @Override
    NucleotideFastaRecord trim(Range trimRange);

    /**
     * Create a NucleotideFastaRecord of the FIRST record in the given
     * fasta file (which may be compressed).
     * @param fastaFile the file to parse; can not be null.
     * @return an Optional wrapped NucleotideFastaRecord object; or empty
     * if the fasta file does not contain any sequences.
     * @throws IOException if there is a problem parsing the file.
     * 
     * @since 6.0
     * 
     * @implNote this is the same as
     * <pre>
     * {@code try( NucleotideFastaFileDataStore datastore = new NucleotideFastaFileDataStoreBuilder(fastaFile)
						.hint(DataStoreProviderHint.ITERATION_ONLY)
						.build();
				StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();
				){
			 if(!iter.hasNext()) {
				return Optional.empty();
			 }
			 return Optional.of(iter.next());
		}
		}
     * </pre>
     */
	static Optional<NucleotideFastaRecord> of(File fastaFile) throws IOException {
		
		try( NucleotideFastaFileDataStore datastore = new NucleotideFastaFileDataStoreBuilder(fastaFile)
						.hint(DataStoreProviderHint.ITERATION_ONLY)
						.build();
				StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();
				){
			 if(!iter.hasNext()) {
				return Optional.empty();
			 }
			 return Optional.of(iter.next());
		}
	}
	 /**
     * Create a new {@link StreamingIterator} of  NucleotideFastaRecord for each of the records in the given
     * fasta file (which may be compressed).
     * @param fastaFile the file to parse; can not be null.
     * @return an Optional wrapped NucleotideFastaRecord object; or empty
     * if the fasta file does not contain any sequences.
     * @throws IOException if there is a problem parsing the file.
     * 
     * @since 6.0
     * 
     * @implNote this is the same as
     * <pre>
     * {@code return new NucleotideFastaFileDataStoreBuilder(fastaFile)
						.hint(DataStoreProviderHint.ITERATION_ONLY)
						.build()
						.iterator();
				
		}
     * </pre>
     */
	static StreamingIterator<NucleotideFastaRecord> createNewIteratorFor(File fastaFile) throws DataStoreException, IOException{
		return new NucleotideFastaFileDataStoreBuilder(fastaFile)
		.hint(DataStoreProviderHint.ITERATION_ONLY)
		.build()
		.iterator();
	}
}
