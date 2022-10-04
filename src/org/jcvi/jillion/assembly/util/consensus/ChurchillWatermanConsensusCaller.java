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
 * Created on Jun 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import java.util.Set;


import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * {@code ChurchillWatermanConsensusCaller} is a {@link ConsensusCaller}
 * that uses calculates  a {@link ConsensusResult}
 *  for a slice using the Bayes formula and the procedure from
 * <pre>
 * Churchill, G.A. and Waterman, M.S.
 * "The accuracy of DNA sequences: Estimating sequence quality."
 * Genomics 14, pp.89-98 (1992)
 * </pre>
 * @author dkatzel
 *
 *
 */
public final class ChurchillWatermanConsensusCaller extends AbstractChurchillWatermanConsensusCaller{

    private static final int MAX_NUMBER_OF_BASES_TOWARDS_AMBIGUITY = 5;


    public ChurchillWatermanConsensusCaller(
            PhredQuality highQualityThreshold) {
        super(highQualityThreshold);
    }

    

    @Override
    protected Nucleotide getConsensus(
            ConsensusProbabilities normalizedConsensusProbabilities,
            Slice slice) {
        final Set<Nucleotide> basesUsedTowardsAmbiguity = getBasesUsedTowardsAmbiguity(normalizedConsensusProbabilities,
                        MAX_NUMBER_OF_BASES_TOWARDS_AMBIGUITY);
        return Nucleotide.getAmbiguityFor(basesUsedTowardsAmbiguity);
        
    }
    
    

    


    
}
