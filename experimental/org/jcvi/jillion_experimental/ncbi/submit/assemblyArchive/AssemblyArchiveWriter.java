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
package org.jcvi.jillion_experimental.ncbi.submit.assemblyArchive;

import java.io.Closeable;
import java.io.IOException;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;

public interface AssemblyArchiveWriter extends Closeable{

	public interface TraceNameLookup{
		/**
		 * Get the trace name to use in the Assembly archive.
		 * This trace name must match the corresponding 
		 * name in the Trace Archive.  Very often this trace name
		 * is different than the read id that is commonly used in the 
		 * contig assembly.
		 * @param readId the read id; can not be null and must
		 * have a corresponding trace name.
		 * @return a String representation of the trace name.
		 * @throws IllegalArgumentException if the lookup 
		 * can not find a valid trace name for the given read id.
		 * @throws NullPointerException if readId is null.
		 */
		String getTraceNameByContigReadId (String readId);
	}
	
	void write(
			String submitterReference,
			AssemblyArchiveConformation conformation,
			AssemblyArchiveType type, 
			Contig<? extends AssembledRead> superContig,
			TraceNameLookup lookup
			) throws IOException;
	
}
