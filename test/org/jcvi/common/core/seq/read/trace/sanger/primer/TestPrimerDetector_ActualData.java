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

package org.jcvi.common.core.seq.read.trace.sanger.primer;

import java.io.IOException;
import java.util.List;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaFileDataStoreFactory;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileDataStoreType;
import org.jcvi.common.core.seq.read.trace.sanger.primer.PrimerDetector;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author dkatzel
 *
 *
 */
public class TestPrimerDetector_ActualData {
    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestPrimerDetector_ActualData.class);
    
    private NucleotideSequenceDataStore primerDataStore;
    private NucleotideSequence sequence;
    
    @Before
    public void setup() throws IOException, DataStoreException{
        primerDataStore = 
        
        		FastaRecordDataStoreAdapter.adapt(NucleotideSequenceDataStore.class,
        				NucleotideSequenceFastaFileDataStoreFactory.create(
                        RESOURCES.getFile("files/primers.fasta"),
                        FastaFileDataStoreType.MAP_BACKED));
        sequence = NucleotideSequenceFastaFileDataStoreFactory.create(
                                RESOURCES.getFile("files/fullLength.fasta"),
                                FastaFileDataStoreType.MAP_BACKED)
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
