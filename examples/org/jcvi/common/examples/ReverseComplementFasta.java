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
package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.nt.*;

public class ReverseComplementFasta {

	public static void main(String[] args) throws DataStoreException, IOException {
		File inputFasta = new File("path/to/input/fasta");
		File reverseComplimentOutputFasta = new File("/path/to/output.sorted.fasta");

//		jillion4Way(inputFasta, reverseComplimentOutputFasta);

		 jillion5Way(inputFasta, reverseComplimentOutputFasta);


	}

	protected static void jillion4Way(File inputFasta, File reverseComplimentOutputFasta) throws IOException {
		NucleotideFastaDataStore dataStore = new NucleotideFastaFileDataStoreBuilder(inputFasta)
														.hint(DataStoreProviderHint.ITERATION_ONLY)
														.build();
		NucleotideFastaWriter out = new NucleotideFastaWriterBuilder(reverseComplimentOutputFasta)
															.build();

		StreamingIterator<NucleotideFastaRecord> iter=null;
		try {
			iter =dataStore.iterator();
			while(iter.hasNext()){
				NucleotideFastaRecord record =iter.next();
				NucleotideSequence reverseSequence = new NucleotideSequenceBuilder(record.getSequence())
															.reverseComplement()
															.build();
				out.write(record.getId(), reverseSequence, record.getComment());
			}
		} finally{
			IOUtil.closeAndIgnoreErrors(iter,out);
		}
	}

	protected static void jillion5Way(File inputFasta, File reverseComplimentOutputFasta) throws IOException {


		try(NucleotideFastaWriter writer = new NucleotideFastaWriterBuilder(reverseComplimentOutputFasta)
										.createIndex(true)
										.build()
		){
			NucleotideFastaFileReader.records(inputFasta)
					.throwingForEach(record ->

							writer.write(record.getId(),
									record.getSequence().toBuilder().reverseComplement().build(),
									record.getComment())
			);

		}
	}
}
