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
package org.jcvi.jillion.assembly.consed;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class TestConsedUtilGetAcePrefix {

	@Test
	public void versionedAce(){
		assertEquals("prefix", ConsedUtil.getAcePrefixFor(new File("prefix.ace.1")));
	}
	
	@Test
	public void unversionedAce(){
		assertEquals("prefix", ConsedUtil.getAcePrefixFor(new File("prefix.ace")));
	}
	@Test
	public void versionedAceWithDotPrefix(){
		assertEquals("foo.prefix", ConsedUtil.getAcePrefixFor(new File("foo.prefix.ace.1")));
	}
	
	@Test
	public void unversionedAceWithDotPrefix(){
		assertEquals("foo.prefix", ConsedUtil.getAcePrefixFor(new File("foo.prefix.ace")));
	}
}
