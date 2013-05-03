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
package org.jcvi.jillion.trace.chromat;

import java.util.Map;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * {@code ChromatogramFileVisitor} is a {@link FileVisitor}
 * used for visiting chromatogram files.
 * @author dkatzel
 *
 *
 */
public interface ChromatogramFileVisitor{

	void visitNewTrace();
    
    void visitEndOfTrace();
	/**
     * Visit the basecalls in the chromatogram file
     * being visited.
     * @param basecalls the basecalls as a {@link NucleotideSequence},
     * although unlikely, it is possible there are 
     * gaps.
     */
    void visitBasecalls(NucleotideSequence basecalls);

    /**
     * Visit the raw peak values of the
     * chromatogram file being visited.
     * @param peaks the raw peaks as shorts,
     * may be null.
     */
    void visitPeaks(short[] peaks);

    
    /**
     * Visit any comments associated with 
     * this chromatogram. 
     * @param comments the comments associated
     * with this chromatogram file stored
     * as key-value pairs.
     */
    void visitComments(Map<String,String> comments);
   
    /**
     * Visit the raw positions of the A channel of the
     * chromatogram file being visited.
     * @param positions the raw positions as shorts,
     * may be null.
     */
    void visitAPositions(short[] positions);

    /**
     * Visit the raw positions of the C channel of the
     * chromatogram file being visited.
     * @param positions the raw positions as shorts,
     * may be null.
     */
    void visitCPositions(short[] positions);
    /**
     * Visit the raw positions of the G channel of the
     * chromatogram file being visited.
     * @param positions the raw positions as shorts,
     * may be null.
     */
    void visitGPositions(short[] positions);
    /**
     * Visit the raw positions of the T channel of the
     * chromatogram file being visited.
     * @param positions the raw positions as shorts,
     * may be null.
     */
    void visitTPositions(short[] positions);

    /**
     * Visit the raw confidence (quality) of the A channel of the
     * chromatogram file being visited.
     * @param confidence the raw confidence as bytes,
     * may be null.
     */
    void visitAConfidence(byte[] confidence);

    /**
     * Visit the raw confidence (quality) of the C channel of the
     * chromatogram file being visited.
     * @param confidence the raw confidence as bytes,
     * may be null.
     */
    void visitCConfidence(byte[] confidence);

    /**
     * Visit the raw confidence (quality) of the G channel of the
     * chromatogram file being visited.
     * @param confidence the raw confidence as bytes,
     * may be null.
     */
    void visitGConfidence(byte[] confidence);

    /**
     * Visit the raw confidence (quality) of the T channel of the
     * chromatogram file being visited.
     * @param confidence the raw confidence as bytes,
     * may be null.
     */
    void visitTConfidence(byte[] confidence);

}
