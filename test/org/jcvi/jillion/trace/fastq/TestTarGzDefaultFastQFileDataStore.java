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
/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

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
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class TestTarGzDefaultFastQFileDataStore extends AbstractTestFastQFileDataStore{
	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	
    @Override
    protected FastqDataStore createFastQFileDataStore(File file, FastqQualityCodec qualityCodec) throws IOException {
    	
    	File t = tmpDir.newFile("fastq.tar.gz");
    	try(InputStream in = new BufferedInputStream(new FileInputStream(file));
    		ArchiveOutputStream o = new TarArchiveOutputStream(new GzipCompressorOutputStream(Files.newOutputStream(t.toPath())));
    			){
    		ArchiveEntry entry =o.createArchiveEntry(file, file.getName());
    		o.putArchiveEntry(entry);
    		IOUtils.copy(in, o);
    		o.closeArchiveEntry();
    	}
    	
    	FastqParser parser = new FastqFileParserBuilder(t)
    								.hasComments(true)
    								.hasMultilineSequences(true)
    								.build();
        return DefaultFastqFileDataStore.create(parser,qualityCodec,DataStoreFilters.alwaysAccept(), null);
    }
    
}
