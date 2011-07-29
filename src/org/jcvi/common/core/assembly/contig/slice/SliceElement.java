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
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.slice;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
/**
 * {@code SliceElement} is 
 * @author dkatzel
 *
 *
 */
public interface SliceElement {
    /**
     * Get the ID of this element.  Each element in a Slice must
     * have a different ID, although there SliceElements from 
     * different Slices can have the same ID.  This ID is usually the 
     * read ID.
     * @return the ID of this slice element.
     */
    String getId();
    /**
     * Get the {@link Nucleotide} of this SliceElement.
     * @return
     */
    Nucleotide getBase();
    /**
     * Get the {@link PhredQuality} of this SliceElement.
     * @return
     */
    PhredQuality getQuality();
    /**
     * Get the {@link Direction} of this SliceElement.
     * @return
     */
    Direction getSequenceDirection();
}
