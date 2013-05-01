/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.nav;

import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.Range;

/**
 * 
 * {@code ConsensusNavigationElement} is a {@link NavigationElement}
 * that tells consed how to navigate to a particular feature 
 * of a specific contig.
 * @author dkatzel
 *
 *
 */
public final class ConsensusNavigationElement extends AbstractNavigationElement{

    /**
     * Constructs a new {@link ConsensusNavigationElement}.
     * @param contigId the id of the contig of that is to be navigated.
     * @param ungappedPositionRange the ungapped position
     * range of this element; cannot be null.
     * @param comment a comment that describes why this element exists
     * (may be null).
     * @throws NullPointerException if type, targetId or 
     * ungappedPositionRange are null.
     */
    public ConsensusNavigationElement(String contigId,
            Range ungappedPositionRange, String comment) {
        super(Type.CONSENSUS, contigId, ungappedPositionRange, comment);
    }
    /**
     * Constructs a new {@link ConsensusNavigationElement}.
     * @param contigId the id of the contig of that is to be navigated.
     * @param ungappedPositionRange the ungapped position
     * range of this element; cannot be null.
     * @throws NullPointerException if type, targetId or 
     * ungappedPositionRange are null.
     */
    public ConsensusNavigationElement(String contigId,
            Range ungappedPositionRange){
        super(Type.CONSENSUS, contigId, ungappedPositionRange);
    }
    /**
     * Build a new {@link ConsensusNavigationElement} for the given
     * contig, that will navigate to the given GAPPED range.  This
     * is a convenience method that handles converting the gapped
     * range into an ungapped range required by the consed.
     * This is the same as {@link #buildConsensusNavigationElement(Contig, Range, String)
     * buildConsensusNavigationElement(contig, gappedFeatureRange,null)}
     * @param <C> a Contig object.
     * @param contig the contig to make a {@link ConsensusNavigationElement}
     * for; cannot be null.
     * @param gappedFeatureRange the gapped feature range coordinates; cannot be null.
     * @return a new ConsensusNavigationElement.
     * @see #buildConsensusNavigationElement(Contig, Range, String)
     */
    public static <C extends Contig<?>> ConsensusNavigationElement buildConsensusNavigationElement(C contig,
            Range gappedFeatureRange){
        return buildConsensusNavigationElement(contig, gappedFeatureRange,null);
    }
    /**
     * Build a new {@link ConsensusNavigationElement} for the given
     * contig, that will navigate to the given GAPPED range.  This
     * is a convenience method that handles converting the gapped
     * range into an ungapped range required by the consed.
     * @param <C> a Contig object.
     * @param contig the contig to make a {@link ConsensusNavigationElement}
     * for; cannot be null.
     * @param gappedFeatureRange the gapped feature range coordinates; cannot be null.
     * @param comment a comment that describes why this element exists
     * (may be null).
     * @return a new ConsensusNavigationElement.
     */
    public static <C extends Contig<?>> ConsensusNavigationElement buildConsensusNavigationElement(C contig,
            Range gappedFeatureRange,String comment){
        if(gappedFeatureRange ==null){
            throw new NullPointerException("feature range can not be null");
        }
       
        Range ungappedRange = AssemblyUtil.toUngappedRange(contig.getConsensusSequence(), gappedFeatureRange);
        return new ConsensusNavigationElement(contig.getId(), ungappedRange,comment);
    }
    
    
}
