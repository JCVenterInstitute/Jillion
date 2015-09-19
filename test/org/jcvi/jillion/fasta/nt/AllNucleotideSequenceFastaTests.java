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
package org.jcvi.jillion.fasta.nt;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author dkatzel
 *
 *
 */
@RunWith(Suite.class)
@SuiteClasses(
    {
     TestNucleotideSequenceFastaRecordFactory.class,
     TestDefaultSequenceFastaDataStore.class,
     TestDefaultSequenceFastaDataStoreWithNoComment.class,
     TestDefaultNucleotideFastaDataStoreAsStream.class,
     
     TestLargeNucleotideFastaFileDataStore.class,
     TestIndexedNucleotideFastaFileDataStore.class,
     TestLargeSequenceFastaMapWithNoComment.class,
     TestNucleotideDataStoreFastaAdatper.class,
     TestFastaSequenceDataStoreAdapter.class,
     TestStreamingDefaultSequenceDataStore.class,
     
     TestDefaultUnixAndDosDataStoresParsedCorrectly.class,
     TestIndexedUnixAndDosDataStoresParsedCorrectly.class,
     TestLargeUnixAndDosDataStoresParsedCorrectly.class,
     
     
     TestNucleotideFastaFileDataStoreBuilder.class,
     TestNucleotideFastaDataStoreBuilderWithLambdaRecordFilter.class,
     
     TestNucleotideFastaRecordWriter.class,
     
     TestParseNonRedundantFastaFile.class,
     
     TestNonRedundantNucleotideFastaWriter.class
    }
    )
public class AllNucleotideSequenceFastaTests {

}
