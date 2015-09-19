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

import java.io.IOException;
import java.util.NoSuchElementException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.sff.SffFileIterator;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSffFileIterator {

	ResourceHelper RESOURCES  =new ResourceHelper(TestSffFileIterator.class);
	
	@Test
	public void iterateOverAllRecords() throws IOException{
		SffFileIterator iter = SffFileIterator.createNewIteratorFor(RESOURCES.getFile("files/5readExample.sff"));

		for(int i=0; i<5; i++){
			assertTrue(iter.hasNext());
			iter.next();
		}
		assertFalse(iter.hasNext());
		assertGettingNextThrowsException(iter);
	}

	private void assertGettingNextThrowsException(SffFileIterator iter) {
		try{
			iter.next();
			fail("should throw no such element exception");
		}catch(NoSuchElementException expected){
			
		}
	}
	
	@Test
	public void closeIteratorEarlyShouldStopIterating() throws IOException{
		SffFileIterator iter = SffFileIterator.createNewIteratorFor(RESOURCES.getFile("files/5readExample.sff"));
		assertTrue(iter.hasNext());
		iter.next();
		iter.close();
		assertFalse(iter.hasNext());
		assertGettingNextThrowsException(iter);
	}
	
	
	
}
