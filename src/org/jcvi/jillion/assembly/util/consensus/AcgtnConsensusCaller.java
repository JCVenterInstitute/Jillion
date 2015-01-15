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
