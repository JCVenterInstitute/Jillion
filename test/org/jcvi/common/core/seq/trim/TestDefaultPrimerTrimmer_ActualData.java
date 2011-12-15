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

package org.jcvi.common.core.seq.trim;

import java.io.IOException;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideFastaRecordFactory;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideFastaRecordDataStoreAdatper;
import org.jcvi.common.core.seq.trim.DefaultPrimerTrimmer;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideDataStore;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultPrimerTrimmer_ActualData {
    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestDefaultPrimerTrimmer_ActualData.class);
    
    private NucleotideDataStore primerDataStore;
    private NucleotideSequence sequence;
    
    private final DefaultPrimerTrimmer sut = new DefaultPrimerTrimmer(13, .9f);
    
    @Before
    public void setup() throws IOException, DataStoreException{
        primerDataStore = 
        
            NucleotideFastaRecordDataStoreAdatper.adapt(
                    DefaultNucleotideFastaFileDataStore.create(
                        RESOURCES.getFile("files/primers.fasta")));
        sequence = DefaultNucleotideFastaFileDataStore.create(
                                RESOURCES.getFile("files/fullLength.fasta"))
                        .get("SAJJA07T27G07MP1F").getSequence();
    }
    
    @Test
    public void trim(){
        Range expectedRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED,1,643);
        Range actualRange = sut.trim(sequence, primerDataStore);
        assertEquals(expectedRange, actualRange);
    }
    
    
}
