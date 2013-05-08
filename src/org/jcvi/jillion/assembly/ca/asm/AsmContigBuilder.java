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
package org.jcvi.jillion.assembly.ca.asm;

import org.jcvi.jillion.assembly.ContigBuilder;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;

/**
 * @author dkatzel
 *
 *
 */
public interface AsmContigBuilder extends ContigBuilder<AsmAssembledRead, AsmContig>{

    void setDegenerate(boolean isDegenerate);
    
    boolean isDegenerate();
    
    /**
     * Add a read to this contig with the given values.  This read
     * can later get modified via the {@link #getAssembledReadBuilder(String)}.
     * @param readId the Id this read should have
     * @param validBases the gapped bases of this read that align (however well/badly)
     * to this contig and will be used as underlying sequence data for this contig.
     * @param offset the gapped start offset of this read into the contig
     * consensus.
     * @param dir the {@link Direction} of this read.
     * @param clearRange the ungapped clear range of the valid bases
     * relative to the full length non-trimmed raw full length
     * read from the sequence machine.
     * @param ungappedFullLength the ungapped full length
     * non-trimmed raw full length
     * read from the sequence machine.
     * @return this.
     */
    AsmContigBuilder addRead(String readId, String validBases, int offset,
            Direction dir, Range clearRange, int ungappedFullLength, boolean isSurrogate);
}
