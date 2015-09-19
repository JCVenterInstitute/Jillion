/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceFileDataStore;
import org.jcvi.jillion.assembly.tigr.contig.TigrContig;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigDataStore;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileDataStoreBuilder;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTestFilteredAceDataStore{

    ResourceHelper resources = new ResourceHelper(AbstractTestFilteredAceDataStore.class);
    @Test
    public void skipSelectedContig() throws IOException, DataStoreException{
        String contigIdToSkip = "22934-PB1";
        File aceFile = resources.getFile("files/fluSample.ace");
        DataStoreFilter filter = DataStoreFilters.newExcludeFilter(Collections.singleton(contigIdToSkip));
        AceFileDataStore datastore = create(aceFile, filter);
        assertEquals(7, datastore.getNumberOfRecords());
        assertEquals(499 , datastore.getNumberOfTotalReads());
        TigrContigDataStore contigDataStore = new TigrContigFileDataStoreBuilder(resources.getFile("files/fluSample.contig"),
        									AceContigTestUtil.createFullLengthSeqDataStoreFrom(aceFile))
																				.build();
        assertEquals(8,contigDataStore.getNumberOfRecords());
        StreamingIterator<AceContig> iter = datastore.iterator();
        try{
	    	 while(iter.hasNext()){
	        	AceContig aceContig = iter.next();
	            TigrContig contig = contigDataStore.get(aceContig.getId());
	            AceContigTestUtil.assertContigsEqual(contig, aceContig);
	    	 }
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        
    }
    
    protected abstract AceFileDataStore create(File aceFile, DataStoreFilter filter) throws IOException;
    
}
