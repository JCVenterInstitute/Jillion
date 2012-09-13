/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Dec 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * an {@code AceContig} is a {@link Contig}
 * from an ace file.  Each {@link AceContig}
 * is made up of {@link AceAssembledRead}s.
 * @author dkatzel
 */
public interface AceContig extends Contig<AceAssembledRead>{
    /**
     * Is this contig complemented?
     * @return {@code true} if this contig
     * is complemented; {@code false} otherwise.
     */
    boolean isComplemented();
    
    /**
     * The order of the reads returned by this
     * iterator is ordered by {@link AceAssembledRead#getGappedStartOffset()}.
     * If two reads have the same start offset, then the shorter read determined
     * by {@link AceAssembledRead#getGappedLength()} is returned.
     * If two reads have the same start offset AND gapped length,
     * the the read with the lexigraphically lower read id is returned.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    StreamingIterator<AceAssembledRead> getReadIterator();
    
}
