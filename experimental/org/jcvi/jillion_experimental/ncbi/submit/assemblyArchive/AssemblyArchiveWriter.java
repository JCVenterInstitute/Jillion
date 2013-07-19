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
