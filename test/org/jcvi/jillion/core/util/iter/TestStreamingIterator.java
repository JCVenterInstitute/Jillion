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
package org.jcvi.jillion.core.util.iter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestStreamingIterator {

	private StreamingIterator<String> sut;
	
	List<String> stooges = Arrays.asList("larry","moe","curly");
	List<String> stooges2 = Arrays.asList("shemp","curly-joe","joe besser");
	@Before
	public void setup(){
		List<StreamingIterator<String>> list = new ArrayList<StreamingIterator<String>>();
		list.add(IteratorUtil.createStreamingIterator(stooges.iterator()));
		list.add(IteratorUtil.createStreamingIterator(stooges2.iterator()));
		sut = IteratorUtil.createChainedStreamingIterator(list);

	}
	
	@Test
	public void whenFirstIteratorFinishedShouldStartIteratingSecond(){
		List<String> expected = new ArrayList<String>();
		expected.addAll(stooges);
		expected.addAll(stooges2);
		assertTrue(sut.hasNext());
		for(int i=0; i< expected.size(); i++){
			assertEquals(expected.get(i), sut.next());
		}
		assertFalse(sut.hasNext());
	}
	@Test
	public void closingIteratorShouldMakeIteratorAppearFinished() throws IOException{
		sut.next(); //larry
		sut.next(); //moe
		sut.close(); //close before we get to curly AND 2nd iterator
		assertFalse(sut.hasNext());
		try{
			sut.next();
			fail("should throw NoSuchElementException when no more elements");
		}catch(NoSuchElementException expected){
			
		}
	}
	@Test
	public void closingMultipleTimesShouldHaveNoEffect() throws IOException{
		sut.close();
		sut.close();
	}
}
