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
package org.jcvi.jillion.assembly.ca.asm;

import org.jcvi.jillion.assembly.ca.asm.AsmVisitor.AsmVisitorCallback;
import org.jcvi.jillion.assembly.ca.asm.AsmVisitor.LinkOrientation;
/**
 * {@code AsmScaffoldVisitor} is a visitor interface
 * to visit a single scaffold in an ASM file
 * that contains more than one contig.
 * 
 * @author dkatzel
 * @see AsmVisitor#visitScaffold(AsmVisitorCallback, String, long, int)
 * 
 */
public interface AsmScaffoldVisitor {

	 /**
     * A contig pair message defines a pair of contigs that belong to a scaffold.
     * A scaffold with three contigs {1,2,3} would be represented by two pair message
     * (1,2) and (2,3).  Note that the first contig of a pair can have a reverse orientation
     * to preserve its orientation in the previous contig pair message.
     * @param externalContigId1 the external id of one of the contigs in this pair.
     * @param externalContigId2 the external id of the other contig in this pair.
     * @param meanDistance the the predicted number of bases in the gap between contigs.  A negative
     * distance indicates that the contigs overlap (according to mate pairs)
     * but their consensus sequences do not align.
     * @param stddev standard deviation of the distance distribution of the mates that span the contigs.
     * @param orientation the relative {@link LinkOrientation} of the two contigs.
     */
    void visitContigPair(String externalContigId1,String externalContigId2, 
            float meanDistance, float stddev, LinkOrientation orientation);
    
    /**
     * Visiting this scaffold has been halted
     * by a call to {@link AsmVisitorCallback#haltParsing()}.
     */
	void halted();
	/**
	 * The current scaffold  has been completely visited.
	 */
	void visitEnd();
}
