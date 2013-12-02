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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.align.blast;

import java.io.IOException;
/**
 * {@code BlastParser}
 * is an interface for parsing
 * Blast output using a {@link BlastVisitor}.
 * @author dkatzel
 *
 */
public interface BlastParser {
	/**
	 * Can the parser (re)parse the blast output.
	 * Some implementations may only be able to 
	 * parse the output a few times (or even only once).
	 * This method allows clients to programatically
	 * check if a call to {@link #parse(BlastVisitor)}
	 * will fail. 
	 * @return {@code true} if {@link #parse(BlastVisitor)}
	 * can be called without throwing an {@link IllegalStateException};
	 * {@code false} otherwise.
	 */
	boolean canParse();
	/**
	 * Parse the Blast output and call the appropriate
	 * visitXXX methods on the given visitor.
	 * @param visitor the visitor to call the visit 
	 * methods on; can not be null.
	 * @throws IOException if there is a problem parsing
	 * the blast output.
	 * @throws NullPointerException if the visitor
	 * is null.
	 * @throws IllegalStateException if {@link #canParse()}
	 * returns {@code false}.
	 */
	void parse(BlastVisitor visitor) throws IOException;
}
