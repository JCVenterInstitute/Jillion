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
 * Created on Dec 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr;


import org.jcvi.Range;
import org.jcvi.trace.sanger.chromatogram.BasicChromatogramBuilder;

/**
 * <code>ZTRChromatogramBuilder</code> uses the Builder Pattern
 * to build a {@link ZTRChromatogramImpl}
 * @author dkatzel
 *
 *
 */
public class ZTRChromatogramBuilder extends BasicChromatogramBuilder{
    
    /**
     * Hints for valid range of this sequence.
     */
    private Range clip;

    /**
     * Comment on this chromatogram, may be null.
     */
    private String comment;

   /**
    * Gets the ZTR's clip points..
    * @return a Clip, may be null.
    */
    public final Range clip() {
        return clip;
    }
    /**
     * Sets the clip.
     * @param clip the clip to set.
     * @return this.
     */
    public final ZTRChromatogramBuilder clip(Range clip) {
        this.clip = clip;
        return this;
    }
    /**
     * Gets the comment.
     * @return the comment may be null.
     */
    public final String comment() {
        return comment;
    }
    /**
     * Sets the comment.
     * @param clip the comment to set.
     * @return this.
     */
    public final ZTRChromatogramBuilder comment(String comment) {
        this.comment = comment;
        return this;
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public ZTRChromatogramImpl build() {
        return new ZTRChromatogramImpl(super.build(),
                clip(), comment());
    }
    
}
