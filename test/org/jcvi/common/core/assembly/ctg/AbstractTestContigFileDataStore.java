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
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ctg;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.ContigDataStore;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.junit.After;
import org.junit.Test;
public abstract class AbstractTestContigFileDataStore extends TestAbstractContigFileParser{

	protected final NucleotideSequenceFastaDataStore fullLengthSequences;
	private ContigDataStore<AssembledRead, Contig<AssembledRead>> dataStore;
	
	public AbstractTestContigFileDataStore() throws FileNotFoundException, IOException{
		fullLengthSequences = new NucleotideSequenceFastaFileDataStoreBuilder(RESOURCES.getFile("files/gcv_23918.raw.seq.fasta.fasta")).build();
		dataStore = buildContigFileDataStore(fullLengthSequences, getFile() );
	}
	@After
	public void closeDataStores(){
		IOUtil.closeAndIgnoreErrors(dataStore, fullLengthSequences);
	}
    @Test
    public void thereAre4Contigs() throws DataStoreException, IOException{
        assertEquals(4, dataStore.getNumberOfRecords());
    }
    @Override
    protected Contig getContig925From(File file) throws FileNotFoundException {
        return getContig(dataStore, "925");
    }
    @Override
    protected Contig getContig928From(File file) throws Exception{
        return getContig(dataStore, "928");
    }
    private Contig getContig(
            ContigDataStore<AssembledRead, Contig<AssembledRead>> dataStore, String id) {
        try {
            return dataStore.get(id);
        } catch (DataStoreException e) {
            e.printStackTrace();
            throw new RuntimeException("error getting contig "+id,e);
        }
    }
    protected abstract ContigDataStore<AssembledRead, Contig<AssembledRead>> buildContigFileDataStore(
    		NucleotideSequenceFastaDataStore fullLengthSequences, File contigFile) throws FileNotFoundException;

}
