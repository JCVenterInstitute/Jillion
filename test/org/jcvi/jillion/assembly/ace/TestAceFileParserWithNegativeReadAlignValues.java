package org.jcvi.jillion.assembly.ace;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

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
		AceFileParser.create(in).accept(aceVisitor);
	}
}
