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
package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.internal.core.GlyphCodec;


/**
 * {@code NoAmbiguitiesNucleotideCodec} is a {@link GlyphCodec}
 * of {@link Nucleotide}s that can encode a list of {@link Nucleotide}s
 * that only contain A,C,G,T and gaps (no ambiguities) in as little as 2 bits per base
 * plus some extra bytes for storing the gaps. This should 
 * greatly reduce the memory footprint of most kinds of read data.
 * @author dkatzel
 */
final class AcgtGapNucleotideCodec extends AbstractTwoBitEncodedNucleotideCodec{
    public static final AcgtGapNucleotideCodec INSTANCE = new AcgtGapNucleotideCodec();
    
    
    private AcgtGapNucleotideCodec(){
        super(Nucleotide.Gap);
    }
    
   
    
    
    
}
