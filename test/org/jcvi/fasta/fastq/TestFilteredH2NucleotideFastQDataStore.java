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

package org.jcvi.fasta.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.fasta.ExcludeFastXIdFilter;
import org.jcvi.glyph.nuc.datastore.H2NucleotideDataStore;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestFilteredH2NucleotideFastQDataStore extends TestH2NucleotideFastQDataStore{

    @Override
    protected H2NucleotideFastQDataStore createSUT(File fastQFile,
            H2NucleotideDataStore datastore) throws FileNotFoundException {
        return new H2NucleotideFastQDataStore(fastQFile,datastore, new ExcludeFastXIdFilter(Arrays.asList("SOLEXA1:4:1:12:1692#0/1")));
    }

    @Test
    public void filteredRead() throws DataStoreException{
        assertFalse(getSut().contains("SOLEXA1:4:1:12:1692#0/1"));
    }
}
