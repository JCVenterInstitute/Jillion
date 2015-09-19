/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.experimental.ncbi.submit.assemblyArchive;

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
