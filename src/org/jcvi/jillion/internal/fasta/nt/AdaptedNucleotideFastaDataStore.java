/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.internal.fasta.nt;

import java.io.File;
import java.util.Map;
import java.util.Optional;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.internal.fasta.AdaptedFastaDataStore;

public class AdaptedNucleotideFastaDataStore extends AdaptedFastaDataStore<Nucleotide, NucleotideSequence, NucleotideFastaRecord, NucleotideSequenceDataStore> implements NucleotideFastaFileDataStore{

    private final File fastaFile;
	public AdaptedNucleotideFastaDataStore(Map<String, NucleotideFastaRecord> map, File fastaFile) {
		super(map);
		this.fastaFile = fastaFile;
	}
    @Override
    public Optional<File> getFile() {
        return Optional.ofNullable(fastaFile);
    }
    @Override
    public NucleotideSequenceDataStore asSequenceDataStore(){
        return NucleotideFastaFileDataStore.super.asSequenceDataStore();
    }

	

}
