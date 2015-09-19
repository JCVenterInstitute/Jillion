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
package org.jcvi.jillion.experimental.primer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
public class TestSaltCorrectionStrategy {

	@Test
	public void santaLucia(){
		assertEquals(20.737D, SaltCorrectionStrategy.SANTALUCIA_1996.adjustTemperature(37, 50)
				,0.001D);
		assertEquals(24.5D, SaltCorrectionStrategy.SANTALUCIA_1996.adjustTemperature(37, 100)
				,0.001D);
		assertEquals(37D, SaltCorrectionStrategy.SANTALUCIA_1996.adjustTemperature(37, 1000)
				,0.001D);
	}
	@Test
	public void schildkrautLifson(){
		assertEquals(15.403D, SaltCorrectionStrategy.SCHILDKRAUT_LIFSON.adjustTemperature(37, 50)
				,0.001D);
		assertEquals(20.4D, SaltCorrectionStrategy.SCHILDKRAUT_LIFSON.adjustTemperature(37, 100)
				,0.001D);
		assertEquals(37D, SaltCorrectionStrategy.SCHILDKRAUT_LIFSON.adjustTemperature(37, 1000)
				,0.001D);
	}
	
}
