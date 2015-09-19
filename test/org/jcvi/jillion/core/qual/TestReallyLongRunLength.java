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
package org.jcvi.jillion.core.qual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
/**
 * Ravi noticed if a quality sequence
 * that is longer than {@link Short#MAX_VALUE}
 * will crash the run length encoder
 * since it will cause a negative count.
 * 
 * I have created a bug on sourceforge's Jillion
 * bug tracker.
 * 
 * 
 * @author dkatzel
 * @see <a href= "https://sourceforge.net/tracker/?func=detail&atid=1278566&aid=3603580&group_id=303297">
 * Jillion Bug Report</a>
 */
public class TestReallyLongRunLength {

	@Test
	public void testLongerThanShortMax(){
		byte[] quals = new byte[Short.MAX_VALUE +1];
		byte value = (byte)60;
		Arrays.fill(quals, value);
		
		QualitySequence seq = new QualitySequenceBuilder(quals).build();
		assertEquals(quals.length,seq.getLength());
		Iterator<PhredQuality> iter = seq.iterator();
		assertTrue(iter.hasNext());
		while(iter.hasNext()){
			assertEquals(value, iter.next().getQualityScore());
		}
	}
}
