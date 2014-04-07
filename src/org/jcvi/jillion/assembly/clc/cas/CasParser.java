/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
package org.jcvi.jillion.assembly.clc.cas;

import java.io.IOException;
/**
 * {@code CasParser} is an interface that will
 * parse a CLC cas formatted structures and call the appropriate 
 * visit methods on the given {@link CasFileVisitor}.
 * @author dkatzel
 *
 */
public interface CasParser {

	/**
	 * Can this handler accept new visit requests
	 * via parse() calls.
	 * 
	 * Some implementations of {@link CasParser}
	 * may only allow one parse call in its lifetime 
	 * (for example, if the phd structure is being parsed via
	 * an InputStream).
	 * @return {@code true} if this handler can handle 
	 * new parse requests; {@code false} otherwise.
	 */
	boolean canParse();
	/**
	 * Parse the cas structure starting from the beginning 
	 * and call the appropriate
	 * visit methods on the given {@link CasFileVisitor}.
	 * @param visitor the {@link CasFileVisitor} instance to call
	 * visit methods on; can not be null;
	 * @throws IOException if there is a problem parsing the cas.
	 * @throws NullPointerException if visitor is null.
	 * @throws IllegalStateException if this handler can not accept
	 * any new parse requests.
	 * @see #canParse()
	 */
	void parse(CasFileVisitor visitor) throws IOException;	
	
}
