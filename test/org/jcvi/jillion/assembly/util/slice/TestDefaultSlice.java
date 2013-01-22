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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util.slice;

import java.util.List;

import org.jcvi.jillion.assembly.util.slice.DefaultSlice;
import org.jcvi.jillion.assembly.util.slice.IdedSlice;
import org.jcvi.jillion.assembly.util.slice.IdedSliceElement;

/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultSlice extends AbstractTestSlice{

    /**
    * {@inheritDoc}
    */
    @Override
    protected IdedSlice createNew(List<IdedSliceElement> elements) {
        return new DefaultSlice.Builder().addAll(elements).build();
    }

}
