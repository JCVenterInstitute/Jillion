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
 * Created on Sep 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

/**
 * {@code Read} is the abstraction of an raw (usually ungapped)
 * nucleotide sequence.
 * @author dkatzel
 * @param <T> the type of {@link NucleotideEncodedGlyphs}.
 *
 */
public interface Read<T extends NucleotideEncodedGlyphs> {
    /**
     * Get the id of this read.
     * @return the id as a String; will never be null.
     */
    String getId();
    /**
     * Get the ungapped {@link NucleotideEncodedGlyphs} of this read.
     * @return the {@link NucleotideEncodedGlyphs} of this read; will
     * never be null.
     */
    T getEncodedGlyphs();
    /**
     * Get the (ungapped) length of this read.
     * @return the length of this read as a long.
     */
    long getLength();
}
