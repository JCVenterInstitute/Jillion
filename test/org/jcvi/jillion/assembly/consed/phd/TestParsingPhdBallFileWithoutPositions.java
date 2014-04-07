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
package org.jcvi.jillion.assembly.consed.phd;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
public class TestParsingPhdBallFileWithoutPositions {

	@Test
	public void parseFileWithoutPositions() throws IOException{
		ResourceHelper helper = new ResourceHelper(TestParsingPhdBallFileWithoutPositions.class);
		
		File phdBallFile = helper.getFile("files/noPositions.phd.ball.1");
		
		PhdBallParser parser = PhdBallFileParser.create(phdBallFile);
		
		final List<Phd> actualList = new ArrayList<Phd>();
		
		PhdBallVisitor visitor = new AbstractPhdBallVisitor() {

			@Override
			public PhdVisitor visitPhd(PhdBallVisitorCallback callback,
					String id, Integer version) {
				//all versions in this file is version 1
				assertEquals(1, version.intValue());
				return new AbstractPhdVisitor(id, version) {
					
					@Override
					protected void visitPhd(String id, Integer version,
							NucleotideSequence basescalls, QualitySequence qualities,
							PositionSequence positions, Map<String, String> comments,
							List<PhdWholeReadItem> wholeReadItems, List<PhdReadTag> readTags) {
						actualList.add(new PhdBuilder(id, basescalls,qualities)
											.comments(comments)
											.peaks(positions)
											.wholeReadItems(wholeReadItems)
											.readTags(readTags)
											.build()
									);
						
					}
				};
			}
		};
		
		parser.accept(visitor);
		
		List<Phd> expected = createExpectedPhds();
		assertEquals("different number of phds visited", expected.size(), actualList.size());
		assertEquals(expected, actualList);
	}
	
	private List<Phd> createExpectedPhds(){
		return Arrays.asList(
				createFirstPhd(),
				createSecondPhd()
				);
	}

	private Phd createFirstPhd() {
		Map<String, String> comments = new LinkedHashMap<String, String>();
		comments.put("TIME", "Wed Dec 24 11:21:50 2008");
		comments.put("CHEM", "solexa");
		return new PhdBuilder("HWI-EAS94_4_1_1_537_446", 
					new NucleotideSequenceBuilder("gccaatcaggtttctctgcaagcccctttagcagctgagc").build(),
					new QualitySequenceBuilder(new byte[]{30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 28, 23, 30, 30, 30, 30, 30, 30, 28, 22, 8, 22, 7, 15, 15, 15, 10, 10, 11, 15}).build())
				.comments(comments)
				.build();

	}
	
	private Phd createSecondPhd() {
		Map<String, String> comments = new LinkedHashMap<String, String>();
		comments.put("TIME", "Wed Dec 24 11:21:50 2008");
		comments.put("CHEM", "solexa");
		return new PhdBuilder("HWI-EAS94_4_1_1_602_99", 
					new NucleotideSequenceBuilder("gccatggcacatatatgaaggtcagaggacaacttgctgt").build(),
					new QualitySequenceBuilder(new byte[]{30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 16, 30, 28, 22, 22, 22, 14, 15, 15, 5, 10, 15, 10, 5}).build())
				.comments(comments)
				.build();

	}
}
