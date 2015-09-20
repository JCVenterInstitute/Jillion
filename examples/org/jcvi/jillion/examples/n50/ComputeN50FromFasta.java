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
package org.jcvi.jillion.examples.n50;

import java.io.File;
import java.io.IOException;
import java.util.OptionalInt;
import java.util.stream.Stream;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.GenomeStatistics;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;

public class ComputeN50FromFasta {

	public void computeN50(File fastaFile) throws IOException, DataStoreException{
		try(NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(fastaFile)
									.hint(DataStoreProviderHint.ITERATION_ONLY)
									.build();
			
		        Stream<NucleotideFastaRecord> stream = datastore.iterator().toStream();
		){
			OptionalInt n50Value = stream
						.map(fasta -> fasta.getLength())
						.collect(GenomeStatistics.n50Collector());
			
			//return value is optional because there might not be any records!
			if(n50Value.isPresent()){
				System.out.println("N50 = " + n50Value.getAsInt());
			}
		}
	}
	
	public void computeFilteredNG50(File fastaFile, long extimatedGenomeSize) throws IOException, DataStoreException{
		try(NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(fastaFile)
								.hint(DataStoreProviderHint.ITERATION_ONLY)
								//filter out small contigs < 2000bp
								.filterRecords(fasta -> fasta.getLength() >= 2000)
								.build();
			
			Stream<NucleotideFastaRecord> stream = datastore.iterator().toStream();
		){
			OptionalInt ng50Value = stream
						.map(fasta -> fasta.getLength())
						.collect(GenomeStatistics.ng50Collector(extimatedGenomeSize));
			
			//return value is optional because there might not be any records!
			if(ng50Value.isPresent()){
				System.out.println("NG50 = " + ng50Value.getAsInt());
			}
		}
	}
}
