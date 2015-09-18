package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestFastqFileBuilderWithLambdaFilter {

    private static File inputFastq;
    
    
    @Parameters
    public static List<Object[]> data(){
        //we have to use temp variables for the type inferencing to work
        Consumer<FastqFileDataStoreBuilder> allInMemory = (builder) -> builder.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED);
        Consumer<FastqFileDataStoreBuilder> mementos = (builder) -> builder.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY);
        Consumer<FastqFileDataStoreBuilder> iterationOnly = (builder) -> builder.hint(DataStoreProviderHint.ITERATION_ONLY);
        
        
        return Arrays.asList(
                new Object[]{allInMemory},
                new Object[]{mementos},
                new Object[]{iterationOnly}
                );
    }
    
    @BeforeClass
    public static void setup() throws IOException{
        ResourceHelper resources = new ResourceHelper(TestFastqFileBuilderWithLambdaFilter.class);
        inputFastq = resources.getFile("files/giv_XX_15050.fastq");
    }
    
    @AfterClass
    public static void clearFile(){
        inputFastq = null;
    }
    
    
    private final  Consumer<FastqFileDataStoreBuilder> hint;
    
    
    
    public TestFastqFileBuilderWithLambdaFilter(
            Consumer<FastqFileDataStoreBuilder> hint) {
        this.hint = hint;
    }

    @Test
    public void filterOutReadsThatEndInR() throws IOException, DataStoreException{
        FastqFileDataStoreBuilder builder = new FastqFileDataStoreBuilder(inputFastq);
        
        hint.accept(builder);
        
        try(FastqFileDataStore datastore = builder
                                                    .hasComments()
                                                    .filter(id-> id.endsWith("F"))
                                                    .build();
                
            StreamingIterator<FastqRecord> actualIter = datastore.iterator();
            ){
            assertEquals(138, datastore.getNumberOfRecords());
            assertTrue(actualIter.hasNext());
            while(actualIter.hasNext()){
                assertTrue(actualIter.next().getId().endsWith("F"));
            }
        }
    }
    
    @Test
    public void onlyIncludeLongSequenceLengths() throws IOException, DataStoreException{
        FastqFileDataStoreBuilder builder = new FastqFileDataStoreBuilder(inputFastq);
        
        hint.accept(builder);
        
        try(FastqFileDataStore datastore = builder
                                                .filterRecords(record-> record.getNucleotideSequence().getLength() > 1000)
                                                .build();
                
            StreamingIterator<FastqRecord> actualIter = datastore.iterator();
            ){
            assertEquals(33, datastore.getNumberOfRecords());
            assertTrue(actualIter.hasNext());
            while(actualIter.hasNext()){
                assertTrue(actualIter.next().getNucleotideSequence().getLength() > 1000);
            }
        }
    }
}
