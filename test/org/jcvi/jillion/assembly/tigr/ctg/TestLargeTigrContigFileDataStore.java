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
package org.jcvi.jillion.assembly.tigr.ctg;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.tigr.contig.TigrContig;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigDataStore;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileDataStoreBuilder;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigRead;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;

public class TestLargeTigrContigFileDataStore extends AbstractTestContigFileDataStore<TigrContigRead, TigrContig, TigrContigDataStore>{
   
    public TestLargeTigrContigFileDataStore() throws IOException {
		super();
	}

	@Override
    protected TigrContigDataStore buildContigFileDataStore(
    		NucleotideFastaDataStore fullLengthSequences, File file) throws IOException {
        return new TigrContigFileDataStoreBuilder(file, fullLengthSequences)
        	.hint(DataStoreProviderHint.ITERATION_ONLY)
        		.build();
    }
}
