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
package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.util.Builder;
/**
 * {@code FastqFileDataStoreBuilderVisitor} is a {@link FastqFileVisitor}
 * that will build a {@link FastqDataStore}
 * during the visit callbacks.  After the entire
 * file has been visited,
 * the  {@link #build()} method can be called to 
 * return a new instance of a {@link FastqDataStore}.
 * Instances of {@link FastqFileDataStoreBuilderVisitor}
 * are not thread-safe unless otherwise specified.
 * @author dkatzel
 *
 */
interface FastqFileDataStoreBuilderVisitor extends FastqFileVisitor, Builder<FastqDataStore> {

}
