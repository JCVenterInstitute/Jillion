/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.assembly.contig;

import org.easymock.EasyMockSupport;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.slice.consensus.ConsensusCaller;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestContigCreator extends EasyMockSupport{
	ConsensusCaller consensusCaller;
	ContigCreator<PlacedRead> sut;
	
	@Before
	public void setup(){
		consensusCaller = createMock(ConsensusCaller.class);
		sut = new ContigCreator<PlacedRead>(consensusCaller);
	}
	
	@Test
	public void noReadsShouldCreateEmptyContig(){
		Contig<PlacedRead> contig = sut.create();
		assertEquals(0, contig.getNumberOfReads());
		assertEquals(0, contig.getConsensus().getLength());
	}
}
