/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.fasta.fastq.AllFastqUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
     TestQualityFastaRecord.class ,
     TestDefaultEncodedNuclotideFastaRecord.class,
     
     TestDefaultQualityFastaMap.class,
     
     TestDefaultSequenceFastaMap.class,
     TestLargeSequenceFastaMap.class,
     
     TestDefaultSequenceFastaMapWithNoComment.class,
     TestLargeSequenceFastaMapWithNoComment.class,
     
     TestFlowgramQualityFastaMap.class,
     
     TestPositionFastaRecord.class,
     
     TestDefaultPositionsFastaMap.class,
     TestLargePositionsFastaMap.class,
     TestLargeQualityFastaMap.class,
     
     TestNucleotideDataStoreFastaAdatper.class,
     
     AllFastqUnitTests.class
    }
    )
public class AllFastaUnitTests {

}
