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
package org.jcvi.jillion.trace.sff;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public abstract class AbstractTestSffWriter {

	ResourceHelper resources = new ResourceHelper(AbstractTestSffWriter.class);
	
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    File outputFile,inputSff;

    private SffFileDataStore expected, actual;
   
    @Before
    public void setup() throws IOException, DataStoreException{
        outputFile = folder.newFile("output.sff");
        
        inputSff = resources.getFile(getPathToFile());
		
		expected = createDataStore(inputSff);
		
		SffWriter writer = createWriter(outputFile, expected.getKeySequence(), expected.getFlowSequence());
		StreamingIterator<SffFlowgram> iter =null;
		try{
			iter = expected.iterator();
			while(iter.hasNext()){
				SffFlowgram next = iter.next();
				writer.write(next);
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		writer.close();
		
		actual = createDataStore(outputFile);
		
    }
    
    @After
    public void closeDataStores(){
    	IOUtil.closeAndIgnoreErrors(expected, actual);
    }
    protected abstract String getPathToFile();
    
    protected abstract SffFileDataStore createDataStore(File inputSff) throws IOException;
    
    protected abstract SffWriter createWriter(File outputFile, NucleotideSequence keySequence, NucleotideSequence flowSequence) throws IOException;
    
    @Test
	public void keyAndFlowSequencesShouldMatch() throws IOException, DataStoreException{
    	assertEquals(expected.getKeySequence(), actual.getKeySequence());
		assertEquals(expected.getFlowSequence(), actual.getFlowSequence());
    }
	@Test
	public void iteratorsShouldMatch() throws IOException, DataStoreException{
		
		
		assertEquals(expected.getNumberOfRecords(), actual.getNumberOfRecords());
		StreamingIterator<SffFlowgram> iter =null;
		StreamingIterator<SffFlowgram> actualIter =null;
		try{
			iter = expected.iterator();
			actualIter = actual.iterator();
			while(iter.hasNext()){
				assertEquals(iter.next(), actualIter.next());
			}
			assertFalse(actualIter.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(iter, actualIter);
		}
	}
	
	@Test
	public void filesMatchExactly() throws IOException{
		InputStream expectedInputStream = new BufferedInputStream(new FileInputStream(inputSff));
		InputStream actualInputStream = new BufferedInputStream(new FileInputStream(outputFile));
		try{
			byte[] expected =IOUtil.toByteArray(expectedInputStream);
			byte[] actual =IOUtil.toByteArray(actualInputStream);
			assertArrayEquals(expected, actual);
		}finally{
			IOUtil.closeAndIgnoreErrors(actualInputStream, expectedInputStream);
		}
	}
}
