/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
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
        
        TestTriplet.class,
        
        TestSerializeDefaultNucleotideSequence.class,
        TestSerializeReferenceEncodedNucleotideSequence.class,
        
        
        TestDefaultNucleotideSequenceIteratorSubRange.class,
        TestReferenceEncodedSequenceIteratorSubRange.class,
        
        TestNucleotideSequencePermuter.class
    }
)
public class AllNucleotideUnitTests {

}
