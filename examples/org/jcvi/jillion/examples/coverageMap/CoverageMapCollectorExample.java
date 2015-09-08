package org.jcvi.jillion.examples.coverageMap;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceFileDataStore;
import org.jcvi.jillion.assembly.consed.ace.AceFileDataStoreBuilder;
import org.jcvi.jillion.assembly.util.CoverageMap;
import org.jcvi.jillion.assembly.util.CoverageMapCollectors;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

public class CoverageMapCollectorExample {

	public static void main(String[] args) throws IOException, DataStoreException{
		File aceFile = new File("/path/to/ace");
		
		try(AceFileDataStore datastore = new AceFileDataStoreBuilder(aceFile)
												.hint(DataStoreProviderHint.ITERATION_ONLY)
												.build();
			StreamingIterator<AceContig> iter = datastore.iterator();
		){
			
			while(iter.hasNext()){
				AceContig contig = iter.next();
				System.out.println(contig.getId());
				
				CoverageMap<Range> coverageMap = contig.reads()
														.filter(read -> read.getDirection() == Direction.FORWARD)
														.map(AssembledRead::asRange)
														.collect(CoverageMapCollectors.toCoverageMap(200));
				
				System.out.println("\tnum cov regions : " +coverageMap.getNumberOfRegions() );
				System.out.println("\t"+coverageMap.getStats());	
				
				
						
			}
		}
	}
}
