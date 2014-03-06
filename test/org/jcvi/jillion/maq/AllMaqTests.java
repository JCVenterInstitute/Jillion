package org.jcvi.jillion.maq;

import org.jcvi.jillion.maq.bfa.TestBinaryFastaFileParser;
import org.jcvi.jillion.maq.bfa.TestBinaryFastaFileWriter;
import org.jcvi.jillion.maq.bfq.TestBinaryFastqFileWriter;
import org.jcvi.jillion.maq.bfq.TestDefaultBinaryFastqDataStore;
import org.jcvi.jillion.maq.bfq.TestIndexedBinaryFastqDataStore;
import org.jcvi.jillion.maq.bfq.TestLargeBinaryFastqDataStore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestDefaultBinaryFastqDataStore.class,
    	TestLargeBinaryFastqDataStore.class,
    	TestIndexedBinaryFastqDataStore.class,
    	
    	TestBinaryFastqFileWriter.class,
    	
    	TestBinaryFastaFileParser.class,
    	TestBinaryFastaFileWriter.class
    }
    )
public class AllMaqTests {

}
