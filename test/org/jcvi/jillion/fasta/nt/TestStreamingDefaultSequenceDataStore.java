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
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiFunction;

import org.jcvi.jillion.core.Defline;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.DecodingOptions;

/**
 * @author dkatzel
 *
 *
 */
public class TestStreamingDefaultSequenceDataStore extends TestDefaultSequenceFastaDataStore{

    @Override
    protected NucleotideFastaDataStore parseFile(File file, DecodingOptions decodingOptions, BiFunction<String,String, Defline> idConverter)
            throws IOException {
        InputStream in =null;
        try{
            in = new FileInputStream(file);
            return DefaultNucleotideFastaFileDataStore.create(in, DataStoreFilters.alwaysAccept(), null, decodingOptions,idConverter);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }

}
