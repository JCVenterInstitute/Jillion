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
 * Created on Feb 20, "2009" +
 *
 * @author "dkatzel" +
 */
package org.jcvi.jillion.fasta.nt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.junit.Test;

public abstract class AbstractTestSequenceFastaMapWithNoComment extends AbstractTestSequenceFastaDataStoreWithNoComment{
    @Test
    public void parseStream() throws IOException, DataStoreException{
        DataStore<NucleotideFastaRecord> sut = createDataStore(
        		RESOURCES.getFile(FASTA_FILE_PATH));
        assertEquals(1, sut.getNumberOfRecords());
        assertEquals(hrv_61, sut.get("hrv-61"));
    }

}
