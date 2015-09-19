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
package org.jcvi.jillion.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public abstract class AbstractTestRangeSubclasses {

	
	private Range getRange(){
		return Range.of(getBegin(),getEnd());
	}
	
	protected abstract Range getDifferentRange();
	
	protected abstract long getBegin();
	protected abstract long getEnd();
	
	protected abstract long getLength();
	
	@Test
	public void getters(){
		Range range = getRange();
		assertEquals(getBegin(), range.getBegin());
		assertEquals(getEnd(), range.getEnd());
		assertEquals(getLength(), range.getLength());
	}

	@Test
	public void equalsSameRef(){
		TestUtil.assertEqualAndHashcodeSame(getRange(), getRange());
	}
	@Test
	public void notEqualToNoRange(){
		
		assertFalse(getRange().equals("not a range"));
	}
	@Test
	public void notEqualsDifferentValues(){
		TestUtil.assertNotEqualAndHashcodeDifferent(getRange(), getDifferentRange());
	}
	
	@Test
	public void serialze() throws IOException, ClassNotFoundException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		Range range = getRange();
		oos.writeObject(range);
		oos.close();
		
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
	
		Range deserializedRange = (Range)in.readObject();
		
		assertEquals(range, deserializedRange);
		assertEquals(range.getClass(), deserializedRange.getClass());
	}
}
