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

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;


/**
 * {@code NoAmbiguitiesNucleotideCodec} is a {@link NucleotideCodec}
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


	@Override
	public List<Range> getNRanges(byte[] encodedData) {
		// no Ns
		return Collections.emptyList();
	}


	@Override
	public double getPercentN(byte[] encodedData) {
		//no Ns
		return 0D;
	}


	@Override
	public List<Integer> getGapOffsets(byte[] encodedData) {
		GrowableIntArray array = this.getSentinelOffsets(encodedData);
		
		return array.toBoxedList();
	}
    
	@Override
	public IntStream getGapOffsetsAsStream(byte[] encodedData) {
		GrowableIntArray array = this.getSentinelOffsets(encodedData);
		
		return array.stream();
	}
    
    
    
}
