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
/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta;

import org.jcvi.jillion.fasta.aa.AllProteinFastaRecordUnitTests;
import org.jcvi.jillion.fasta.nt.AllNucleotideSequenceFastaTests;
import org.jcvi.jillion.fasta.pos.AllPositionFastaUnitTests;
import org.jcvi.jillion.fasta.qual.AllFastaQualityTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestFastaParser.class,

        AllFastaQualityTests.class,
        AllNucleotideSequenceFastaTests.class,
        AllProteinFastaRecordUnitTests.class,
        AllPositionFastaUnitTests.class,
        
        TestRolloverSplitFastaWriter.class,
        TestRoundRobinSplitFastaWriter.class,
        TestDeconvolveSplitFastaWriter.class
    }
    )
public class AllFastaUnitTests {

}
