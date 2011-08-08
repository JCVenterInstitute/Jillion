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

package org.jcvi.fasta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideFastaDataStore;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestTrimFasta {

    private final ResourceFileServer resources = new ResourceFileServer(TestTrimFasta.class);
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    File outputFile;
    File fastaFile;
    File left, right;
    @Before
    public void setup() throws IOException{
        outputFile = folder.newFile("trimmed.seq");
        
        fastaFile = resources.getFile("files/untrimmed.seq");
        left = resources.getFile("files/left.trimpoint");
        right = resources.getFile("files/right.trimpoint");
    }
    
    @Test
    public void shouldTrimBasedOnLeftAndRightTrimFiles() throws FileNotFoundException, DataStoreException{
        TrimFasta.main(new String[]{
                "-f", fastaFile.getAbsolutePath(),
                "-l", left.getAbsolutePath(),
                "-r", right.getAbsolutePath(),
                "-o", outputFile.getAbsolutePath()
        });
        
        NucleotideFastaDataStore actualDataStore = new DefaultNucleotideFastaFileDataStore(outputFile);
        assertEquals(Nucleotides.convertToString(
                    actualDataStore.get("read1").getSequence().asList()),
                    "CCCTTT");
        assertEquals(Nucleotides.convertToString(
                actualDataStore.get("read2").getSequence().asList()),
                "AAACCCTTTGGGG");
    }
}
