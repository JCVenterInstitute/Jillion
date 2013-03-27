/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.primer;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion_experimental.primer.SaltCorrectionStrategy;
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
