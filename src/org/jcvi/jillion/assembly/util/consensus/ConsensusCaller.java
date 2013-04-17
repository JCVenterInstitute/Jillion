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
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import org.jcvi.jillion.assembly.util.Slice;
/**
 * <code>ConsensusCaller</code> compute the
 * {@link ConsensusResult} for the given Slice.
 * @author dkatzel
 *
 *
 */
public interface ConsensusCaller {
    /**
     * compute the consensus
     * {@link Nucleotide} for the given Slice.
     * @param slice the Slice to compute the consensus for.
     * @return a {@link ConsensusResult} will never be <code>null</code>
     * @throws NullPointerException if slice is null.
     */
    ConsensusResult callConsensus(Slice slice);
}
