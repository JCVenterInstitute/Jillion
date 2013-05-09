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

import org.jcvi.jillion.assembly.Contig;

/**
 * A Unitig is a special kind of contig
 * whose length is "as long as possible" until
 * the assembler found a contradiction.  The final
 * contigs produced by the Celera Assembler
 * are several unitigs that have been extended 
 * to overlap each other.
 * <p/>
 * A Unitig of a repetitive region in the genome
 * will only contain the "perfect repeat"; all underlying reads
 * will be trimmed so any parts of the read that span the non-repetitive
 * parts of the genome will get trimmed off. 
 * This greatly simplifies downstream assembly steps,
 * since we can treat identical unitigs
 * as a a single repeat motif which the downstream
 * assembly steps can use coverage
 * information to figure out how many copies exist.
 * @author dkatzel
 *
 *
 */
public interface AsmUnitig extends Contig<AsmAssembledRead>{

}
