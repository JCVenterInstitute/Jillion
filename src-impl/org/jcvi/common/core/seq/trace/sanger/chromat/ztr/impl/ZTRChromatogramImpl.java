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
 * Created on Sep 12, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.chromat.ztr.impl;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.seq.trace.sanger.chromat.impl.BasicChromatogram;
import org.jcvi.common.core.seq.trace.sanger.chromat.ztr.ZtrChromatogram;

public class ZTRChromatogramImpl extends BasicChromatogram implements ZtrChromatogram{

    /**
     * Hints for valid range of this sequence.
     */
    private Range clip;
    /**
     * Constructor that takes a Chromatogram without
     * ztr specific paramters.
     * @param c
     */
    public ZTRChromatogramImpl(Chromatogram c){
        this(c, null);
    }
    /**
     * @param c
     */
    public ZTRChromatogramImpl(Chromatogram c, Range clip) {
        super(c);
        this.clip = clip;
    }
    /**
     * Gets the ZTR Specific clip.
     * @return a clip, may be null.
     */
    public Range getClip() {
        return clip;
    }

    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        //keep superclass'es equals
        return super.equals(obj);
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        //keep superclass hascode
        return super.hashCode();
    }
    
    
}
