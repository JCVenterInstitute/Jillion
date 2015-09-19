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
package org.jcvi.jillion.core.pos;

import org.jcvi.jillion.core.pos.Position;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestPosition {

	@Test
	public void createValidPosition(){
		Position sut = Position.valueOf(1234);
		assertEquals(1234, sut.getValue());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void creatingNegativeValueShouldThrowException(){
		Position.valueOf(-1);
	}
	
	@Test
	public void flyweightReusesSameValues(){
		Position a = Position.valueOf(123);
		Position b = Position.valueOf(123);
		assertSame(a,b);
	}
	
	@Test
	public void valueLargerThanShortMax(){
		Position sut = Position.valueOf(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, sut.getValue());
	}
}
