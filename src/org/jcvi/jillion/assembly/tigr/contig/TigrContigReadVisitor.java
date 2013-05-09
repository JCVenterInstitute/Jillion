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
package org.jcvi.jillion.assembly.tigr.contig;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code TigrContigReadVisitor} is a 
 * visitor interface to visit a single
 * read inside  contig  from a TIGR {@literal .contig}
 * encoded file.  The id, direction and valid range
 *  of the read
 * have already been given by 
 * {@link TigrContigVisitor#visitRead(String, long, org.jcvi.jillion.core.Direction, org.jcvi.jillion.core.Range)}.
 * @author dkatzel
 *
 */
public interface TigrContigReadVisitor {
	/**
	 * Visit the gapped {@link NucleotideSequence}
	 * of the valid range bases that provide coverage
	 * for this contig.  
	 * @param gappedBasecalls a {@link NucleotideSequence},
	 * will never be null.
	 */
	void visitBasecalls(NucleotideSequence gappedBasecalls);
	/**
	 * Visit the end of this read.
	 */
	void visitEnd();
}
