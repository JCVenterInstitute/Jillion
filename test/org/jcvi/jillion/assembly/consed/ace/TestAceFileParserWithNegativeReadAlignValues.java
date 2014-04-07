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
package org.jcvi.jillion.assembly.consed.ace;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import org.jcvi.jillion.assembly.consed.ace.AceContigReadVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceContigVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceFileParser;
import org.jcvi.jillion.assembly.consed.ace.AceFileVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceFileVisitorCallback;
import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;
/**
 * These tests test for a regression
 * where the AceFileParser did not correctly
 * visit reads which had negative values in their QA lines.
 * the Ace File Format Spec says -1s are valid
 * and mean the read is really bad quality. 
 * However the spec does not mention -1s in the alignment range
 * I guess that means nothing aligned.  (then why is it in the ace file?)
 * @author dkatzel
 *
 */
public class TestAceFileParserWithNegativeReadAlignValues {

	@Test
	public void negativeAlignRangeShouldStillGetVisited() throws IOException{
		String readRecord = "RD GRM587L01ARFN0 253 0 0\n"+
							"TGccaTAAatagtCGATCTGTAggaTTacgccaatacccaacgtaaac*G\n"+
							"tttttccAtttC*tGGAGcataacgCTGaacccagCGATaaaTTGTggtg\n"+
							"tttttccAtttC*tGGAGcataacgCTGaacccagCGATaaaTTGTggtg\n"+
							"AGctgAtgtCC*ATAtttaCAATac*ca*aCGAAcagCCCAaa*gaaT*G\n"+
							"ATttcA*CC*TTga*a*agxxx*xxx*xx*xxxxxxxxxx*xxxxxxxxx\n"+
							"xxx\n"+
							"\n"+
							"QA 16 219 -1 -1\n"+
							"DS CHROMAT_FILE: sff:GRM587L01.sff:GRM587L01ARFN0 PHD_FILE: GRM587L01ARFN0.phd.1 TIME: Sun Jun 3 15:26:00 2001\n";
		
		AceContigReadVisitor readVisitor = createMock(AceContigReadVisitor.class);
	
		readVisitor.visitBasesLine("TGccaTAAatagtCGATCTGTAggaTTacgccaatacccaacgtaaac*G");
		readVisitor.visitBasesLine("tttttccAtttC*tGGAGcataacgCTGaacccagCGATaaaTTGTggtg");
		readVisitor.visitBasesLine("tttttccAtttC*tGGAGcataacgCTGaacccagCGATaaaTTGTggtg");
		readVisitor.visitBasesLine("AGctgAtgtCC*ATAtttaCAATac*ca*aCGAAcagCCCAaa*gaaT*G");
		readVisitor.visitBasesLine("ATttcA*CC*TTga*a*agxxx*xxx*xx*xxxxxxxxxx*xxxxxxxxx");
		readVisitor.visitBasesLine("xxx");
		
		readVisitor.visitQualityLine(16, 219,-1, -1);
		
		readVisitor.visitTraceDescriptionLine(isA(String.class), isA(String.class), isA(Date.class));
		readVisitor.visitEnd();
		//CO <contig name> <# of bases> <# of reads in contig> <# of base segments in contig> <U or C>
		String fakeContigHeader ="CO contig 1 1 0 U\n";
		
		ByteArrayInputStream in = new ByteArrayInputStream((fakeContigHeader+readRecord).getBytes(IOUtil.UTF_8));
	
		AceContigVisitor contigVisitor = createMock(AceContigVisitor.class);
		AceFileVisitor aceVisitor = createMock(AceFileVisitor.class);
		expect(aceVisitor.visitContig(isA(AceFileVisitorCallback.class), eq("contig"), eq(1), eq(1), eq(0), eq(false)))
				.andReturn(contigVisitor);
		
		expect(contigVisitor.visitBeginRead("GRM587L01ARFN0", 253)).andReturn(readVisitor);
		contigVisitor.visitEnd();
		aceVisitor.visitEnd();
		replay(aceVisitor, contigVisitor, readVisitor);
		AceFileParser.create(in).parse(aceVisitor);
	}
}
