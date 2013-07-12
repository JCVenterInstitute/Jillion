package org.jcvi.jillion_experimental.ncbi.submit.assemblyArchive;

import java.io.Closeable;
import java.io.IOException;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;

public interface AssemblyArchiveWriter extends Closeable{

	public interface TraceNameLookup{
		String getTraceNameByContigId (String contigReadId);
	}
	
	void write(
			String submitterReference,
			AssemblyArchiveConformation conformation,
			AssemblyArchiveType type, 
			Contig<? extends AssembledRead> superContig,
			TraceNameLookup lookup
			) throws IOException;
	
}
