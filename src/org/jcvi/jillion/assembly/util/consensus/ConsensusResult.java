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
/*
 * Created on Dec 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * {@code ConsensusResult} is the base call
 * and quality value that best represents
 * a particular {@link Slice} in a Contig.
 * 
 * @author dkatzel
 *
 *
 */
public interface ConsensusResult {
    /**
     * The best {@link Nucleotide}
     * represented by the Slice.
     * @return a {@link Nucleotide} will never be null.
     */
    Nucleotide getConsensus();
    /**
     * Return the quality of the consensus.  This number may be
     * in the hundreds or thousands depending on the depth of
     * coverage.
     * @return an int; will always be {@code >= 0}
     */
    int getConsensusQuality();
}
