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
package org.jcvi.common.core.seq.trace.frg;

import java.util.List;

import org.jcvi.common.core.io.TextFileVisitor;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface Frg2Visitor extends TextFileVisitor{
    
    public enum FrgAction {
        ADD,
        MODIFY,
        DELETE,
        IGNORE;
        
        public static FrgAction parseAction(char action){
            switch(action){
                case 'A': return ADD;
                case 'M': return MODIFY;
                case 'D': return DELETE;
                case 'I': return IGNORE;
                default:
                    throw new IllegalArgumentException("not a Frg action : "+ action);
            }
        }
    }
    
    void visitLibrary(FrgAction action, 
                        String id,
                        MateOrientation orientation,
                        Distance distance);
    
    void visitFragment(FrgAction action,
                String fragmentId, 
                String libraryId,
                NucleotideSequence bases,
                QualitySequence qualities ,
                Range validRange,
                Range vectorClearRange,
                String source);
    
    void visitLink(FrgAction action, List<String> fragIds);
}
