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
 * Created on Jul 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import java.util.List;

import org.jcvi.Distance;
import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.TextFileVisitor;
import org.jcvi.sequence.MateOrientation;

public interface Frg2Visitor extends TextFileVisitor{
    
    void visitLibrary(FrgVisitorAction action, 
                        String id,
                        MateOrientation orientation,
                        Distance distance);
    
    void visitFragment(FrgVisitorAction action,
                String fragmentId, 
                String libraryId,
                NucleotideEncodedGlyphs bases,
                EncodedGlyphs<PhredQuality> qualities ,
                Range validRange,
                Range vectorClearRange,
                String source);
    
    void visitLink(FrgVisitorAction action, List<String> fragIds);
}
