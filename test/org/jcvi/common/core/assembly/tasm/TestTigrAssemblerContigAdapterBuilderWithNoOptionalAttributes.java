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

package org.jcvi.common.core.assembly.tasm;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.ctg.DefaultContigFileDataStore;
import org.jcvi.common.core.assembly.tasm.DefaultTigrAssemblerFileContigDataStore;
import org.jcvi.common.core.assembly.tasm.TigrAssemblerContig;
import org.jcvi.common.core.assembly.tasm.TigrAssemblerContigAdapter;
import org.jcvi.common.core.assembly.tasm.TigrAssemblerContigAttribute;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.io.fileServer.FileServer;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestTigrAssemblerContigAdapterBuilderWithNoOptionalAttributes {

	 private static final FileServer RESOURCES = new ResourceFileServer(TestTigrAssemblerContigDataStore.class);
	    
	    private static final DefaultContigFileDataStore contigDataStore;
	    private static final DefaultTigrAssemblerFileContigDataStore tasmDataStore;
	    static{
	        try {
	            contigDataStore= new DefaultContigFileDataStore(RESOURCES.getFile("files/giv-15050.contig"));
	        } catch (Exception e) {
	            throw new IllegalStateException("could not parse contig file",e);
	        } 
	        try {
	            tasmDataStore= new DefaultTigrAssemblerFileContigDataStore(RESOURCES.getFile("files/giv-15050.tasm"));
	        } catch (Exception e) {
	            throw new IllegalStateException("could not parse contig file",e);
	        } 
	    }
	    
	    @Test
	    public void adaptPB2() throws DataStoreException{
	    	Contig<AssembledRead> contig =contigDataStore.get("15044");
	    	TigrAssemblerContig tasm =tasmDataStore.get("1122071329926");
	    	
	    	TigrAssemblerContigAdapter sut = new TigrAssemblerContigAdapter.Builder(contig)
	    									.build();
	    	assertEquals(contig.getId(), sut.getId());
	    	assertEquals(contig.getConsensus().asList(), sut.getConsensus().asList());
	    	assertEquals(contig.getNumberOfReads(), contig.getNumberOfReads());
	    	TigrAssemblerTestUtil.assertAllReadsCorrectlyPlaced(contig, tasm);
	    	assertRequiredAttributesAreEqual(tasm, sut);
	    }
	    
	    private void assertRequiredAttributesAreEqual(TigrAssemblerContig expected, TigrAssemblerContigAdapter actual){
	    	//apparently pull_contig sets the asmb_id to the ca_contig_id if present which will throw off our
	    	//asmbl_id check
	    	//	assertAttributeValueEquals(TigrAssemblerContigAttribute.ASMBL_ID,expected, actual);
	    	assertAttributeValueEquals(TigrAssemblerContigAttribute.UNGAPPED_CONSENSUS,expected, actual);
	    	assertAttributeValueEquals(TigrAssemblerContigAttribute.GAPPED_CONSENSUS,expected, actual);
	    	assertAttributeValueEquals(TigrAssemblerContigAttribute.PERCENT_N,expected, actual);
	    	assertAttributeValueEquals(TigrAssemblerContigAttribute.NUMBER_OF_READS,expected, actual);
	    	assertAttributeValueEquals(TigrAssemblerContigAttribute.IS_CIRCULAR,expected, actual);
	    	
	    	//avg coverage is actually computed by java common and estimated by legacy TIGR tools
	    	//so be flexible with rounding errors
	    	assertEquals(Float.parseFloat(expected.getAttributeValue(TigrAssemblerContigAttribute.AVG_COVERAGE)), 
	    			Float.parseFloat(actual.getAttributeValue(TigrAssemblerContigAttribute.AVG_COVERAGE)),
	    			.1F);
	    }
	    private void assertAttributeValueEquals(TigrAssemblerContigAttribute attribute,TigrAssemblerContig expected,TigrAssemblerContig actual ){
	    	assertEquals(expected.getAttributeValue(attribute), actual.getAttributeValue(attribute));
	    }
}
