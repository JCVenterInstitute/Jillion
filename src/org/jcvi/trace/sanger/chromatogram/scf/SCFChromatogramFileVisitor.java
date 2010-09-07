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

package org.jcvi.trace.sanger.chromatogram.scf;

import org.jcvi.trace.sanger.chromatogram.ChromatogramFileVisitor;

/**
 * @author dkatzel
 *
 *
 */
public interface SCFChromatogramFileVisitor extends ChromatogramFileVisitor{

    void visitPrivateData(byte[] privateData);
    
    void visitSubstitutionConfidence(byte[] confidence);
    
    void visitInsertionConfidence(byte[] confidence);
    
    void visitDeletionConfidence(byte[] confidence);
}
