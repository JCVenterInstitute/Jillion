/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly;

import java.io.IOException;

/**
 * {@code AssemblyTransformationService}
 * is an interface that will parse some
 * kind of Assembly data into messages
 * that an {@link AssemblyTransformer}
 * can use. 
 * @author dkatzel
 *
 */
public interface AssemblyTransformationService {
	/**
	 * Parse the Assembly and call the appropriate
	 * method calls on the given {@link AssemblyTransformer}.
	 * @param transformer the {@link AssemblyTransformer}
	 * to use; can not be null.
	 * @throws IOException if there is a problem parsing 
	 * the assembly or converting it into 
	 * something the {@link AssemblyTransformer}
	 * can use.
	 * @throws NullPointerException if transformer is null.
	 */
	void transform(AssemblyTransformer transformer) throws IOException;
}
