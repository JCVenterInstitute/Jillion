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
 * Created on Sep 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.residue.nt;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
        TestNucleotide.class,
        TestNucleotideMatches.class,    
        
        TestBasicNucleotideCodec.class,
        TestAcgtnNucleotideCodec.class,
        TestAcgtGapNucleotideCodec.class,
        
        
        TestDefaultNucleotideSequence.class,
        TestNucleotideGlyphGetAmbiguity.class,
        TestNucleotideGetBasesFor.class,
        TestReferenceEncodedNucleotideSequence.class,
        TestReferenceEncodedNucleotideSequence_gappedtoUngapped.class,
        TestVHTNGS_365.class,
        TestNucleotideGlyph_GetGlyphsFor.class,
        
        TestNucleotideSequenceBuilder.class,
        TestNucleotideSequenceBuilderIsEqualToIgnoringGaps.class,
        
        TestNucleotideSequenceIsEqualToIgnoringGaps.class,
        
        TestTriplet.class,
        
        TestSerializeDefaultNucleotideSequence.class,
        TestSerializeReferenceEncodedNucleotideSequence.class,
        
        
        TestDefaultNucleotideSequenceIteratorSubRange.class,
        TestReferenceEncodedSequenceIteratorSubRange.class,
        
        TestNucleotideSequencePermuter.class,
        
        TestNucleotideKmers.class,
        TestNucleotideRangesOfNs.class,
        TestNucleotideSequenceMatches.class,
        TestNucleotideForEach.class
    }
)
public class AllNucleotideUnitTests {

}
