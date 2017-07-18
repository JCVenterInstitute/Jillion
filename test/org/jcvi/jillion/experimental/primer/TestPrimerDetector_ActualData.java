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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStore;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestPrimerDetector_ActualData {
    private static final ResourceHelper RESOURCES = new ResourceHelper(TestPrimerDetector_ActualData.class);
    
    private NucleotideSequenceDataStore primerDataStore;
    private NucleotideSequence sequence;
    
    @Before
    public void setup() throws IOException, DataStoreException{
        primerDataStore =  NucleotideFastaFileDataStore.fromFile(RESOURCES.getFile("files/primers.fasta")).asSequenceDataStore();
        sequence =  NucleotideFastaFileDataStore.fromFile(RESOURCES.getFile("files/fullLength.fasta"))
        					.get("SAJJA07T27G07MP1F").getSequence();
    }    
  
    @Test
    public void detect(){
    	PrimerDetector detector = new PrimerDetector(13, .9F);
    	List<DirectedRange> hits = detector.detect(sequence, primerDataStore);
    	assertEquals(1, hits.size());
    	assertEquals(Range.of(643,680), hits.get(0).asRange());
    	assertEquals(Direction.REVERSE, hits.get(0).getDirection());
    }
    
}
