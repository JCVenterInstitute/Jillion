/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.primer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.jcvi.common.primer.PrimerDetector;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.internal.ResourceHelper;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
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
        primerDataStore = 
        
        		FastaRecordDataStoreAdapter.adapt(NucleotideSequenceDataStore.class,
        				new NucleotideSequenceFastaFileDataStoreBuilder(
                        RESOURCES.getFile("files/primers.fasta"))
                        .build());
        sequence = new NucleotideSequenceFastaFileDataStoreBuilder(
                                RESOURCES.getFile("files/fullLength.fasta"))
        					.build()
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
