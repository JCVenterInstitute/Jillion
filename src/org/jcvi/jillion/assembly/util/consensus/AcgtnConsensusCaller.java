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
package org.jcvi.jillion.assembly.util.consensus;


import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

/**
 * {@code AcgtnConsensusCaller} is just like
 * {@link NoAmbiguityConsensusCaller}
 * except if the slice does not have any A,C,G or Ts
 * then it will call an N.
 * @author dkatzel
 *
 */
public class AcgtnConsensusCaller extends NoAmbiguityConsensusCaller{

    public AcgtnConsensusCaller(PhredQuality highQualityThreshold) {
        super(highQualityThreshold);
    }

    @Override
    protected Nucleotide getConsensus(
            ConsensusProbabilities normalizedConsensusProbabilities, Slice slice) {
      Nucleotide acgt = super.getConsensus(normalizedConsensusProbabilities, slice);
      if(slice.getNucleotideCounts().get(acgt) >0){
    	  return acgt;
      }
      return Nucleotide.Unknown;
    }

}
