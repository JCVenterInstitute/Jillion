/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tasm;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.ctg.TigrContigDataStore;
import org.jcvi.jillion.assembly.ctg.TigrContigFileDataStoreBuilder;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestTigrAssemblerContigDataStore {

    private static final ResourceHelper RESOURCES = new ResourceHelper(TestTigrAssemblerContigDataStore.class);
    
    private static final TigrContigDataStore contigDataStore;
    private static final TasmContigDataStore tasmDataStore;
    static{
    	
        try {
        	NucleotideSequenceFastaDataStore fullLengthFastas = new NucleotideSequenceFastaFileDataStoreBuilder(RESOURCES.getFile("files/giv-15050.fasta"))
														.hint(DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_MEMORY)
														.build();
            contigDataStore= new TigrContigFileDataStoreBuilder(RESOURCES.getFile("files/giv-15050.contig"),fullLengthFastas)
            				.build();
            tasmDataStore= DefaultTasmFileContigDataStore.create(RESOURCES.getFile("files/giv-15050.tasm"),fullLengthFastas);
            
        } catch (Exception e) {
            throw new IllegalStateException("could not parse contig file",e);
        } 
       
    }
    
    @Test
    public void PB2() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("15044"), tasmDataStore.get("1122071329926"));
    }
    @Test
    public void PB1() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("15045"), tasmDataStore.get("1122071329927"));
    }
    @Test
    public void PA() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("15046"), tasmDataStore.get("1122071329928"));
    }
    @Test
    public void NP() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("15047"), tasmDataStore.get("1122071329929"));
    }
    @Test
    public void MP() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("15048"), tasmDataStore.get("1122071329930"));
    }
    @Test
    public void NS() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("15057"), tasmDataStore.get("1122071329931"));
    }
    @Test
    public void HA() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("26303"), tasmDataStore.get("1122071329932"));
    }
    @Test
    public void NA() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("27233"), tasmDataStore.get("1122071329933"));
    }
    @Test
    public void lastContig() throws DataStoreException{
        assertContigDataMatches(contigDataStore.get("27235"), tasmDataStore.get("1122071329934"));
    }

    private void assertContigDataMatches(Contig<? extends AssembledRead> contig, TasmContig tasm){
        assertEquals("consensus",contig.getConsensusSequence(), tasm.getConsensusSequence());
        assertEquals("#reads",contig.getNumberOfReads(), tasm.getNumberOfReads());
        
        TigrAssemblerTestUtil.assertAllReadsCorrectlyPlaced(contig, tasm);

    }
}
