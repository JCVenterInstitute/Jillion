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

package org.jcvi.common.core.seq.trace.sanger.chromat.ztr;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.trace.sanger.chromat.ChromatogramFileVisitor;

/**
 * {@code ZTRChromatogramFileVisitor} is a {@link ChromatogramFileVisitor}
 * that has additional visitXXX methods for ZTR specific fields.
 * @author dkatzel
 *
 *
 */
public interface ZtrChromatogramFileVisitor extends ChromatogramFileVisitor{

  
    /**
     * Visit the clip points of the ZTR chromatogram.
     * @param clipRange the clip points which describe
     * the valid range of the data (may be null or empty).
     */
    void visitClipRange(Range clipRange);


}
