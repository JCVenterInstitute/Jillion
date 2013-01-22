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
/*
 * Created on Sep 10, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger;

import org.jcvi.jillion.trace.Trace;
/**
 * A {@code SangerTrace} is a Trace
 * that was created via Sanger
 * sequencing and therefore has 
 * accompanying {@link PositionSequence}.
 * @author dkatzel
 *
 *
 */
public interface SangerTrace extends Trace {
    
    PositionSequence getPositionSequence();
    /**
     * Get the number of actual trace scan positions
     * in the trace file.  Most Sanger Trace files
     * have about 15,000 trace points.
     * @return 
     */
    int getNumberOfTracePositions();
}
