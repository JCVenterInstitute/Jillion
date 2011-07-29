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
 * Created on Sep 10, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger;

import org.jcvi.common.core.seq.read.trace.Trace;
import org.jcvi.common.core.symbol.pos.Peaks;
/**
 * A {@code SangerTrace} is a Trace
 * that was created via Sanger
 * sequencing and therefore has 
 * accompanying {@link Peaks}.
 * @author dkatzel
 *
 *
 */
public interface SangerTrace extends Trace {

    Peaks getPeaks();
    /**
     * Get the number of actual trace scan positions
     * in the trace file.  Most Sanger Trace files
     * have about 15,000 trace points.
     * @return 
     */
    int getNumberOfTracePositions();
}
