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
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly;

import java.io.Serializable;
import java.util.Comparator;


public class PlacedEndComparator<T extends Placed> implements Comparator<T>, Serializable {       
    /**
     * 
     */
    private static final long serialVersionUID = 5135449151100427846L;

    @Override
    public int compare(T o1, T o2) {           
        return Long.valueOf(o1.getEnd()).compareTo(o2.getEnd());
    }
        
}
