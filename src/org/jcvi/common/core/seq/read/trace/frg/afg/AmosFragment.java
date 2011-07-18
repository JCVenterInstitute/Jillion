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

package org.jcvi.common.core.seq.read.trace.frg.afg;

import org.jcvi.Range;
import org.jcvi.common.core.seq.nuc.NucleotideSequence;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.seq.read.trace.Trace;

/**
 * @author dkatzel
 *
 *
 */
public interface AmosFragment extends Trace, Read<NucleotideSequence>{

    String getId();
    int getIndex();
    Range getValidRange();
    Range getVectorClearRange();
    Range getQualityClearRange();

}
