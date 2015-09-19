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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestFastqQualityCodecOffsets {

	@Test
	public void sangerIs33(){
		assertEquals(33, FastqQualityCodec.SANGER.getOffset());
	}
	@Test
	public void solexaIs64(){
		assertEquals(64, FastqQualityCodec.SOLEXA.getOffset());
	}
	@Test
	public void illuminaIs64(){
		assertEquals(64, FastqQualityCodec.ILLUMINA.getOffset());
	}
}
