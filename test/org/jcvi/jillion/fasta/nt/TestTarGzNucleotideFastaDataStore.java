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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder.DecodingOptions;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class TestTarGzNucleotideFastaDataStore extends AbstractTestSequenceFastaDataStore {

   

    @Override
    protected NucleotideFastaDataStore parseFile(File file,  DecodingOptions decodingOptions)
            throws IOException {
 
    	File t = tmpDir.newFile("fasta.tar.gz");
    	try(InputStream in = new BufferedInputStream(new FileInputStream(file));
    		ArchiveOutputStream o = new TarArchiveOutputStream(new GzipCompressorOutputStream(Files.newOutputStream(t.toPath())));
    			){
    		ArchiveEntry entry =o.createArchiveEntry(file, file.getName());
    		o.putArchiveEntry(entry);
    		IOUtils.copy(in, o);
    		o.closeArchiveEntry();
    	}
    	
    	return DefaultNucleotideFastaFileDataStore.create(t, DataStoreFilters.alwaysAccept(), null, decodingOptions);
    	
        
    }

}
