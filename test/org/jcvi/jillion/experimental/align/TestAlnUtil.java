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
package org.jcvi.jillion.experimental.align;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestAlnUtil {

	@Test
	public void validHeader(){
		assertTrue(AlnUtil.validHeader("CLUSTAL"));
		assertTrue(AlnUtil.validHeader("CLUSTAL W (1.82) multiple sequence alignment"));
		assertTrue(AlnUtil.validHeader("CLUSTALW (1.82) multiple sequence alignment"));
		
		assertTrue(AlnUtil.validHeader("CLUSTAL 2.0.11 multiple sequence alignment"));
		
	}
	
	@Test
	public void invalidHeader(){
		assertFalse(AlnUtil.validHeader("FOSB_MOUSE      MFQAFPGDYDSGSRCSSSPSAESQYLSSVDSFGSPPTAAASQECAGLGEMPGSFVPTVTA 60"));
		assertFalse(AlnUtil.validHeader(""));
	}
	@Test(expected = NullPointerException.class)
	public void nullHeaderShouldThrowNPE(){
		AlnUtil.validHeader(null);
	}
}
