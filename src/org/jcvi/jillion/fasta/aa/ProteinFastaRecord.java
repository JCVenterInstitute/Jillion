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
package org.jcvi.jillion.fasta.aa;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaRecord;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;

/**
 * {@code AminoAcidSequenceFastaRecord} is an interface for {@link FastaRecord} objects
 * using the {@link ProteinSequence}.

 * @author naxelrod
 * @author dkatzel
 */
public interface ProteinFastaRecord extends FastaRecord<AminoAcid,ProteinSequence> {

	ProteinSequence getSequence();

	@Override
	ProteinFastaRecord trim(Range trimRange);

	/**
     * Create a ProteinFastaRecord of the FIRST record in the given
     * fasta file (which may be compressed).
     * @param refFile the file to parse; can not be null.
     * @return an Optional wrapped ProteinFastaRecord object; or empty
     * if the fasta file does not contain any sequences.
     * @throws IOException if there is a problem parsing the file.
     * 
     * @sinece 6.0
     * 
     * @implNote this is the same as
     * <pre>
     * {@code try( ProteinFastaFileDataStore datastore = new ProteinFastaFileDataStoreBuilder(refFile)
						.hint(DataStoreProviderHint.ITERATION_ONLY)
						.build();
				StreamingIterator<ProteinFastaRecord> iter = datastore.iterator();
				){
			 if(!iter.hasNext()) {
				return Optional.empty();
			 }
			 return Optional.of(iter.next());
		}
		}
     * </pre>
     */
	static Optional<ProteinFastaRecord> of(File refFile) throws IOException {
		
		try( ProteinFastaFileDataStore datastore = new ProteinFastaFileDataStoreBuilder(refFile)
						.hint(DataStoreProviderHint.ITERATION_ONLY)
						.build();
				StreamingIterator<ProteinFastaRecord> iter = datastore.iterator();
				){
			 if(!iter.hasNext()) {
				return Optional.empty();
			 }
			 return Optional.of(iter.next());
		}
	}
}
