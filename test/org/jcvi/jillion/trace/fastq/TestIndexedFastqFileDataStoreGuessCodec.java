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
package org.jcvi.jillion.trace.fastq;

import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilters;
public class TestIndexedFastqFileDataStoreGuessCodec extends AbstractTestFastQFileDataStore{

    @Override
    protected FastqDataStore createFastQFileDataStore(File file,
            FastqQualityCodec qualityCodec) throws IOException {
    	FastqParser parser = new FastqFileParserBuilder(file, true)
									.hasComments(true)
									.hasMultilineSequences(true)
									.build();
    	
    	FastqQualityCodec codec = FastqUtil.guessQualityCodecUsed(parser);
    	assertSame(codec, qualityCodec);
    	
    	
		return IndexedFastqFileDataStore.create(parser, qualityCodec,
		DataStoreFilters.alwaysAccept(), null);
    }

}
