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
package org.jcvi.jillion.trace.chromat.scf;

import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;

/**
 * {@code ScfChromatogramFileVisitor} is a {@link ChromatogramFileVisitor}
 * that has additional visitXXX methods for SCF specific fields.
 * @author dkatzel
 *
 *
 */
public interface ScfChromatogramFileVisitor extends ChromatogramFileVisitor{
    /**
     * Visit the private data section of an SCF chromatogram 
     * file.
     * @param privateData the private data contained in this
     * SCF chromatogram (may be null).
     */
    void visitPrivateData(byte[] privateData);
    /**
     * Visit the confidence data section of an SCF chromatogram 
     * file that describes how confident the basecaller was
     * that the given basecall is not a substituion.
     * @param confidence the substitution data contained in this
     * SCF chromatogram (may be null or empty).
     */
    void visitSubstitutionConfidence(byte[] confidence);
    /**
     * Visit the confidence data section of an SCF chromatogram 
     * file that describes how confident the basecaller was
     * that the given basecall is not an insertion.
     * @param confidence the insertion data contained in this
     * SCF chromatogram (may be null or empty).
     */
    void visitInsertionConfidence(byte[] confidence);
    /**
     * Visit the confidence data section of an SCF chromatogram 
     * file that describes how confident the basecaller was
     * that the given basecall is not a deletion.
     * @param confidence the deletion data contained in this
     * SCF chromatogram (may be null or empty).
     */
    void visitDeletionConfidence(byte[] confidence);
}
