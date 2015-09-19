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

import java.util.EnumSet;
import java.util.Set;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * <code>AnnotationConsensusCaller</code> is the Consensus
 * caller used by the TIGR Annotation Group.  It is much
 * more sensitive to conflicting basecalls in a slice
 * than the Conic model.  Any high quality conflict
 * will contribute towards the consensus call.
 * @author dkatzel
 *
 *
 */
public class TigrAnnotationConsensusCaller extends AbstractChurchillWatermanConsensusCaller{

    public TigrAnnotationConsensusCaller(PhredQuality highQualityThreshold) {
        super(highQualityThreshold);
    }

    /**
     * Adds all high quality conflicts to standard Chruchill
     * Waterman algorithm of computing which bases
     * to consider towards the ambiguity consensus.
     */
    @Override
    protected Nucleotide getConsensus(
            ConsensusProbabilities normalizedConsensusProbabilities, Slice slice) {
        Set<Nucleotide> basesTowardsAmbiguity = getCWBasesTowardsAmbiguity(normalizedConsensusProbabilities, slice);
        basesTowardsAmbiguity.addAll(findAllHighQualityBases(slice));
        return Nucleotide.getAmbiguityFor(basesTowardsAmbiguity);
        
    }

    private Set<Nucleotide> getCWBasesTowardsAmbiguity(
            ConsensusProbabilities normalizedErrorProbabilityStruct,
            Slice slice) {
    	int numberOfDifferentBasesInSlice=0;
    	for(int counts :slice.getNucleotideCounts().values()){
    		if(counts >0){
    			numberOfDifferentBasesInSlice++;
    		}
    	}      
        return getBasesUsedTowardsAmbiguity(normalizedErrorProbabilityStruct, numberOfDifferentBasesInSlice);
    }

    private Set<Nucleotide> findAllHighQualityBases(Slice slice) {
        Set<Nucleotide> highQualityDiffs = EnumSet.noneOf(Nucleotide.class);
        for(SliceElement sliceElement : slice){
            if(sliceElement.getQuality().compareTo(getHighQualityThreshold()) >=0){
                highQualityDiffs.add(sliceElement.getBase());
            }
        }
        return highQualityDiffs;
    }

}
