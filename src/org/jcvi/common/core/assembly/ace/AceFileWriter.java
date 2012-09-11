package org.jcvi.common.core.assembly.ace;

import java.io.Closeable;
import java.io.IOException;

public interface AceFileWriter extends Closeable{

	void write(AceContig contig) throws IOException;
	
	void write(ReadAceTag readTag) throws IOException;
	
	void write(ConsensusAceTag readTag) throws IOException;
	
	void write(WholeAssemblyAceTag readTag) throws IOException;
	
}
