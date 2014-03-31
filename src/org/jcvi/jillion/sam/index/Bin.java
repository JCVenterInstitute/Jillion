package org.jcvi.jillion.sam.index;

import java.util.List;

public interface Bin {

	
	int getBinNumber();

	List<Chunk> getChunks();
	
	@Override
	int hashCode();
	
	@Override
	boolean equals(Object o);

}
