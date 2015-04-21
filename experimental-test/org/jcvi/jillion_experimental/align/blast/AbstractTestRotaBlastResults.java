/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
package org.jcvi.jillion_experimental.align.blast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;

public abstract class AbstractTestRotaBlastResults {

	private final BlastParser parser;

	public AbstractTestRotaBlastResults(BlastParser parser) {
		this.parser = parser;
	}
	
	@Test
	public void parseBlastResults() throws IOException{
		MyBlastVisitor visitor = new MyBlastVisitor();
		
		assertTrue(parser.canParse());
		
		parser.parse(visitor);
		
		Iterator<Hsp<?,?>> iter = visitor.hsps.iterator();
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<20; i++){
			Hsp<?,?> hsp = iter.next();
			builder.append(String.format("%s %s%n",hsp.getSubjectId(), hsp.getSubjectRange()));
		}
		String expected = 
				String.format("R1-RVA/Human-tc/USA/Wa/1974/G1P8 DirectedRange [range=[ 17 .. 3285 ]/0B, direction=FORWARD]%n") +
						String.format("R1-RVA/Human-tc/USA/D/1974/G1P8 DirectedRange [range=[ 0 .. 3263 ]/0B, direction=FORWARD]%n") +
						String.format("R1-RVA/Human-tc/USA/WI61/1983/G9P8 DirectedRange [range=[ 0 .. 3266 ]/0B, direction=FORWARD]%n") +
						String.format("R1-RVA/Human-tc/USA/P/1974/G3P8 DirectedRange [range=[ 0 .. 3263 ]/0B, direction=FORWARD]%n") +
						String.format("R1-RVA/Human-tc/GBR/ST3/1975/G4P6 DirectedRange [range=[ 0 .. 3266 ]/0B, direction=FORWARD]%n") +
						String.format("R1-RVA/Human-tc/BRA/IAL28/1992/G5P8 DirectedRange [range=[ 0 .. 3263 ]/0B, direction=FORWARD]%n") +
						String.format("R1-RVA/Human-wt/BEL/B3458/2003/G9P8 DirectedRange [range=[ 17 .. 3285 ]/0B, direction=FORWARD]%n") +
						String.format("R1-RVA/Human-wt/BGD/Dhaka12/2003/G12P6 DirectedRange [range=[ 17 .. 3285 ]/0B, direction=FORWARD]%n") +
						String.format("R1-RVA/Human-wt/BEL/B4633/2003/G12P8 DirectedRange [range=[ 17 .. 3285 ]/0B, direction=FORWARD]%n") +
						String.format("R1-RVA/Human-wt/BGD/Dhaka25/2002/G12P8 DirectedRange [range=[ 17 .. 3285 ]/0B, direction=FORWARD]%n") +
						String.format("R1-RVA/Human-wt/BGD/Dhaka6/2001/G11P25 DirectedRange [range=[ 17 .. 3285 ]/0B, direction=FORWARD]%n") +
						String.format("R1-RVA/Human-wt/BGD/Matlab13/2003/G12P6 DirectedRange [range=[ 17 .. 3285 ]/0B, direction=FORWARD]%n") +
						String.format("C1-RVA/Human-wt/BEL/B4633/2003/G12P8 DirectedRange [range=[ 176 .. 2730 ]/0B, direction=FORWARD]%n") +
						String.format("C1-RVA/Human-wt/BGD/Dhaka25/2002/G12P8 DirectedRange [range=[ 176 .. 2730 ]/0B, direction=FORWARD]%n") +
						String.format("C1-RVA/Human-wt/BGD/Matlab13/2003/G12P6 DirectedRange [range=[ 182 .. 2736 ]/0B, direction=FORWARD]%n") +
						String.format("C1-RVA/Human-tc/USA/P/1974/G3P8 DirectedRange [range=[ 148 .. 2687 ]/0B, direction=FORWARD]%n") +
						String.format("M1-RVA/Human-wt/BGD/Dhaka25/2002/G12P8 DirectedRange [range=[ 44 .. 2573 ]/0B, direction=FORWARD]%n") +
						String.format("C1-RVA/Human-wt/BGD/Dhaka12/2003/G12P6 DirectedRange [range=[ 158 .. 2712 ]/0B, direction=FORWARD]%n") +
						String.format("C1-RVA/Human-tc/GBR/ST3/1975/G4P6 DirectedRange [range=[ 130 .. 2672 ]/0B, direction=FORWARD]%n") +
						String.format("M1-RVA/Human-wt/BGD/Dhaka12/2003/G12P6 DirectedRange [range=[ 44 .. 2573 ]/0B, direction=FORWARD]%n");
				
		assertEquals(expected, builder.toString());
		//can't check number of hsps because tabular and xml
		//return different number of hsps!  however the top hits (at least) all match
	//	assertEquals(683, visitor.hsps.size());
	}
}
