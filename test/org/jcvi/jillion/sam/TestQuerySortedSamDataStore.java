/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.sam.SamVisitor.SamVisitorCallback;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;
public class TestQuerySortedSamDataStore {

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();
    
    @Test
    public void shortCircuitWhenIteratorGetsPastName() throws IOException, DataStoreException{
        ResourceHelper resources = new ResourceHelper(this.getClass());
        File originalSam = resources.getFile("example.bam");
        
        File querySorted = tmpDir.newFile("sorted.sam");
        try(SamFileDataStore originalDs = new SamFileDataStoreBuilder(originalSam).build()){
            try(SamWriter writer = new SamFileWriterBuilder(querySorted, originalDs.getHeader())
                                            .reSortBy(SortOrder.QUERY_NAME)
                                            .build();
                    
                    StreamingIterator<SamRecord> iter = originalDs.iterator();
                    ){
                while(iter.hasNext()){
                    writer.writeRecord(iter.next());
                }
            }
        
            try(QueryDataStoreTestDouble sut = new QueryDataStoreTestDouble(SamParserFactory.create(querySorted), null)){
            
            assertEquals(originalDs.getAllRecordsFor("r001"), sut.getAllRecordsFor("r001"));
            
            assertTrue(sut.shortCircuited);
            }
        }
        
    }
    
    @Test
    public void returnedInstanceIfHeaderSaysQuerySorted() throws IOException, DataStoreException{
        ResourceHelper resources = new ResourceHelper(this.getClass());
        File originalSam = resources.getFile("example.bam");
        
        File querySorted = tmpDir.newFile("sorted.sam");
        try(SamFileDataStore originalDs = new SamFileDataStoreBuilder(originalSam).build()){
            try(SamWriter writer = new SamFileWriterBuilder(querySorted, originalDs.getHeader())
                                            .reSortBy(SortOrder.QUERY_NAME)
                                            .build();
                    
                    StreamingIterator<SamRecord> iter = originalDs.iterator();
                    ){
                while(iter.hasNext()){
                    writer.writeRecord(iter.next());
                }
            }
            
            assertTrue(new SamFileDataStoreBuilder(querySorted).build() instanceof QuerySortedSamFileDataStore);
        }     
    }
    
    
    private static class QueryDataStoreTestDouble extends QuerySortedSamFileDataStore{

        QueryDataStoreTestDouble(SamParser parser, Predicate<SamRecord> filter) {
            super(parser, filter);
        }
        
        private boolean shortCircuited=false;

        @Override
        protected void haltParsing(SamVisitorCallback callback) {
            shortCircuited = true;
            super.haltParsing(callback);
        }
        
        
    }
}
