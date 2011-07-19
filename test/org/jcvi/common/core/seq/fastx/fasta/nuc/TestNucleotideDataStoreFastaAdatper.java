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
/*
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta.nuc;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideDataStore;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideDataStoreAdapter;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestNucleotideDataStoreFastaAdatper extends AbstractTestSequenceFastaDataStoreWithNoComment{

    @Override
    protected DataStore<NucleotideSequenceFastaRecord> buildMap(
            File file) throws IOException {
        return new DefaultNucleotideFastaFileDataStore(file);
    }

    @Test
    public void adaptFasta() throws IOException, DataStoreException{
        NucleotideDataStore sut=
        new NucleotideDataStoreAdapter( FastaRecordDataStoreAdapter.adapt(buildMap(
        		RESOURCES.getFile(FASTA_FILE_PATH))));
    
        assertEquals(
                sut.get("hrv-61").decode(), hrv_61.getValue().decode());
    }
}
