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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 12, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.trace.chromat.BasicChromatogram;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;

public class ZTRChromatogramImpl extends BasicChromatogram implements ZtrChromatogram{

    /**
     * Hints for valid range of this sequence.
     */
    private Range clip;
    /**
     * Constructor that takes a {@link Chromatogram} without
     * ztr specific parameters.
     * @param c the {@link Chromatogram} instance to wrap;
     * can not be null.
     * @throws NullPointerException if c is null.
     */
    public ZTRChromatogramImpl(Chromatogram c){
        this(c, null);
    }
    /**
     * Constructor that takes a {@link Chromatogram} and a ZTR clip
     * point as a Range.
     * @param c the {@link Chromatogram} instance to wrap;
     * can not be null.
     * @param clip the ZTR clip points as  {@link Range};
     * may be null (which means no clip points have been set).
     * @throws NullPointerException if c is null.
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
