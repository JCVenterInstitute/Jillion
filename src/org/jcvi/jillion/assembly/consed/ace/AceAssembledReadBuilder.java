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
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.assembly.AssembledReadBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.Builder;

/**
 * {@code AceAssembledReadBuilder} is a {@link Builder}
 * for {@link AceAssembledRead}s for a specific {@link AceContig}.
 * Methods in this interface can modify the {@link NucleotideSequence}
 * of this read or shift where on the contig
 * this read aligns.
 * @author dkatzel
 *
 *
 */
public interface AceAssembledReadBuilder extends AssembledReadBuilder<AceAssembledRead>{
   

    /**
     * Get the {@link PhdInfo}
     * for this read.
     * @return the phdInfo
     * (never null).
     */
    PhdInfo getPhdInfo();
    /**
     * 
     * {@inheritDoc}
     */
    AceAssembledReadBuilder copy();

    
}
