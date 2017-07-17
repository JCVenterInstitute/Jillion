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

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceDataStore;
import org.jcvi.jillion.fasta.FastaDataStore;

/**
 * {@code ProteinFastaDataStore} is a 
 * marker interface for {@link FastaDataStore}s
 * of {@link ProteinFastaRecord}s.
 * @author dkatzel
 *
 */
public interface ProteinFastaDataStore extends FastaDataStore<AminoAcid, ProteinSequence, ProteinFastaRecord, ProteinSequenceDataStore> {

    @Override
    default ProteinSequenceDataStore asSequenceDataStore(){
        return DataStore.adapt(ProteinSequenceDataStore.class, this, ProteinFastaRecord::getSequence);
    }
    /**
     * Create a {@link ProteinFastaDataStore} of all the records
     * in the given fasta file.  Warning! This usually stores all the records in memory
     * use {@link #fromFile(File, DataStoreProviderHint)} or {@link ProteinFastaFileDataStoreBuilder}
     * to handle the file or datastore implementation differently.
     * 
     * @param fastaFile the fasta file make a {@link ProteinFastaDataStore} with;
     * can not be null and must exist.
     * 
     * 
     * @throws IOException if the fasta file does not exist, or can not be read.
     * @throws NullPointerException if fastaFile is null.
     * @return a new ProteinFastaDataStore; will never be null.
     * @since 5.3
     * 
     * @see ProteinFastaFileDataStoreBuilder
     * @see #fromFile(File, DataStoreProviderHint)
     */
    public static ProteinFastaDataStore fromFile(File fastaFile) throws IOException{
        return new ProteinFastaFileDataStoreBuilder(fastaFile).build();
    }
    /**
     * Create a {@link ProteinFastaDataStore} of all the records
     * in the given fasta file with the given {@link DataStoreProviderHint}
     * to help jillion choose a datastore implementation.  If filtering records
     * is desired, pleas use {@link ProteinFastaFileDataStoreBuilder}.
     * 
     * @param fastaFile the fasta file make a {@link ProteinFastaDataStore} with;
     * can not be null and must exist.
     * 
     * @param hint the {@link DataStoreProviderHint} to use; can not be null.
     *
     * 
     * @throws IOException if the fasta file does not exist, or can not be read.
     * @throws NullPointerException if any parameter is null.
     * @return a new ProteinFastaDataStore; will never be null.
     * @since 5.3
     * 
     * @see ProteinFastaFileDataStoreBuilder
     */
    public static ProteinFastaDataStore fromFile(File fastaFile, DataStoreProviderHint hint) throws IOException{
        return new ProteinFastaFileDataStoreBuilder(fastaFile)
                            .hint(hint)
                            .build();
    }
}
