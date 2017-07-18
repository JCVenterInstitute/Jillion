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
package org.jcvi.jillion.experimental.primer;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;

/**
 * @author dkatzel
 *
 *
 */
public final class TestPrimerTrimmerUtil {

    public static NucleotideSequenceDataStore createDataStoreFor(NucleotideSequence...primers){
        Map<String, NucleotideSequence> map = new HashMap<String, NucleotideSequence>();
        for(int i=0; i<primers.length; i++){
            map.put("primer_"+i, primers[i]);
        }
        return DataStore.of(map, NucleotideSequenceDataStore.class);
    }
}
