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

import java.util.Objects;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.fasta.FastaDataStore;

/**
 * {@code NucleotideFastaDataStore} is a FastaDataStore
 * for storing {@link Nucleotide}s.
 * @author dkatzel
 *
 *
 */
public interface NucleotideFastaDataStore extends FastaDataStore<Nucleotide, NucleotideSequence, NucleotideFastaRecord>{

       
	@Override
	default NucleotideSequence getSubSequence(String id, long startOffset) throws DataStoreException {
		
		if(startOffset < 0){
			throw new IllegalArgumentException("start offset can not be negative");
		}
		//this is faster than the super's default method
		NucleotideFastaRecord fullSequence = get(id);
		if(fullSequence ==null){
			return null;
		}
		long fullLength = fullSequence.getLength();
		
		if(fullLength-1 < startOffset){
			throw new IllegalArgumentException("start offset is beyond sequence length : " + startOffset);
		}
		Range range = new Range.Builder(fullLength)
								.contractBegin(startOffset)
								.build();
		return new NucleotideSequenceBuilder(fullSequence.getSequence(), range)
						.build();
	}

	@Override
	default NucleotideSequence getSubSequence(String id, Range includeRange) throws DataStoreException {
		Objects.requireNonNull(includeRange);
		//this is faster than the super's default method
		NucleotideFastaRecord fullSequence = get(id);
		if(fullSequence ==null){
			return null;
		}
		return new NucleotideSequenceBuilder(fullSequence.getSequence(), includeRange)
						.build();
	}

	
	
}
