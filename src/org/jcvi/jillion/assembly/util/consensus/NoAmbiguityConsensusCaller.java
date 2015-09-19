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
 * Created on Jun 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import static org.jcvi.jillion.core.residue.nt.Nucleotide.Adenine;
import static org.jcvi.jillion.core.residue.nt.Nucleotide.Cytosine;
import static org.jcvi.jillion.core.residue.nt.Nucleotide.Gap;
import static org.jcvi.jillion.core.residue.nt.Nucleotide.Guanine;
import static org.jcvi.jillion.core.residue.nt.Nucleotide.Thymine;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * <code>NoAmbiguityConsensusCaller</code>
 * will always return the either A,C,G, T or gap
 * as the consensus, whichever has the lowest error probability.
 * @author dkatzel
 *
 *
 */
public class NoAmbiguityConsensusCaller extends AbstractChurchillWatermanConsensusCaller{

    public NoAmbiguityConsensusCaller(PhredQuality highQualityThreshold) {
        super(highQualityThreshold);
    }

    @Override
    protected Nucleotide getConsensus(
            ConsensusProbabilities normalizedConsensusProbabilities, Slice slice) {
      //assume A is the answer initially
        Nucleotide result = Adenine;
        double lowestErrorProbability = normalizedConsensusProbabilities.getProbabilityFor(Adenine);
        
        if(normalizedConsensusProbabilities.getProbabilityFor(Cytosine).compareTo(lowestErrorProbability) <0){
            result = Cytosine;
            lowestErrorProbability = normalizedConsensusProbabilities.getProbabilityFor(Cytosine);
        }
        if(normalizedConsensusProbabilities.getProbabilityFor(Guanine).compareTo(lowestErrorProbability) <0){
            result = Guanine;
            lowestErrorProbability = normalizedConsensusProbabilities.getProbabilityFor(Guanine);
        }
        if(normalizedConsensusProbabilities.getProbabilityFor(Thymine).compareTo(lowestErrorProbability) <0){
            result = Thymine;
            lowestErrorProbability = normalizedConsensusProbabilities.getProbabilityFor(Thymine);
        }
        if(normalizedConsensusProbabilities.getProbabilityFor(Gap).compareTo(lowestErrorProbability) <0){
            result = Gap;
            lowestErrorProbability = normalizedConsensusProbabilities.getProbabilityFor(Gap);
        }
        return result;
    }
}
