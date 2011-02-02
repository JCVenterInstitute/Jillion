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

package org.jcvi.assembly.trim;

import java.io.IOException;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.fastX.fasta.seq.DefaultNucleotideFastaFileDataStore;
import org.jcvi.fastX.fasta.seq.DefaultNucleotideFastaRecordFactory;
import org.jcvi.fastX.fasta.seq.NucleotideFastaRecordDataStoreAdatper;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.io.fileServer.ResourceFileServer;
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
    private NucleotideEncodedGlyphs sequence;
    
    private final DefaultPrimerTrimmer sut = new DefaultPrimerTrimmer(13, .9f);
    
    @Before
    public void setup() throws IOException, DataStoreException{
        primerDataStore = 
        
            NucleotideFastaRecordDataStoreAdatper.adapt(
                    new DefaultNucleotideFastaFileDataStore(
                        RESOURCES.getFile("files/primers.fasta"),
                        DefaultNucleotideFastaRecordFactory.getInstance()));
        sequence = new DefaultNucleotideFastaFileDataStore(
                                RESOURCES.getFile("files/fullLength.fasta"),
                                DefaultNucleotideFastaRecordFactory.getInstance())
                        .get("SAJJA07T27G07MP1F").getValue();
    }
    
    @Test
    public void trim(){
        Range expectedRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED,1,643);
        Range actualRange = sut.trim(sequence, primerDataStore);
        assertEquals(expectedRange, actualRange);
    }
    
    
}
