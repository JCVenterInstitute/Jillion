/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestSangerFastQQualityCodec.class,
        TestSolexaFastQQualityCodec.class,
        TestSangerFastQQualityCodecActual.class,
        TestDefaultFastQFileDataStore.class,
        TestLargeFastQFileDataStore.class
    }
    )
public class AllFastqUnitTests {

}
