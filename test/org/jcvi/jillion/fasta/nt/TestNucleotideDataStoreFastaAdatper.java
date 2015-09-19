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
/*
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.jillion.fasta.nt.DefaultNucleotideFastaFileDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestNucleotideDataStoreFastaAdatper extends AbstractTestSequenceFastaDataStoreWithNoComment{

    @Override
    protected DataStore<NucleotideFastaRecord> createDataStore(
            File file) throws IOException {
        return DefaultNucleotideFastaFileDataStore.create(file);
    }

    @Test
    public void adaptFasta() throws IOException, DataStoreException{
        NucleotideSequenceDataStore sut=
        		FastaRecordDataStoreAdapter.adapt(NucleotideSequenceDataStore.class, createDataStore(
        		RESOURCES.getFile(FASTA_FILE_PATH)));
    
        assertEquals(
                sut.get("hrv-61"), hrv_61.getSequence());
    }
}
