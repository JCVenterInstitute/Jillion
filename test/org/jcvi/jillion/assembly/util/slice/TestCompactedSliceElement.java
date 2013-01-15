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

package org.jcvi.jillion.assembly.util.slice;

import org.jcvi.jillion.assembly.util.slice.CompactedSliceElement;
import org.jcvi.jillion.assembly.util.slice.IdedSliceElement;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

/**
 * @author dkatzel
 *
 *
 */
public class TestCompactedSliceElement extends AbstractTestIdedSliceElement{

    /**
    * {@inheritDoc}
    */
    @Override
    protected IdedSliceElement create(String id, Nucleotide base,
            PhredQuality qual, Direction dir) {
        return new CompactedSliceElement(id, base, qual, dir);
    }

}
