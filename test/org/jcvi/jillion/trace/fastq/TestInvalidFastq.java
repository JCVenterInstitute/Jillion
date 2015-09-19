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

import static org.easymock.EasyMock.*;
import static org.hamcrest.core.StringStartsWith.startsWith;

import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestInvalidFastq {

	@Rule
    public ExpectedException thrown= ExpectedException.none();
	
	
	@Test
	public void valid() throws IOException{
		String input = 
				"@SRR001666.1 071112_SLXA-EAS1_s_7:5:1:817:345 length=36\n" +
				"GGGTGATGGCCGCTGCCGATGGCGTCAAATCCCACC\n"+
				"+SRR001666.1 071112_SLXA-EAS1_s_7:5:1:817:345 length=36\n"+
				"IIIIIIIIIIIIIIIIIIIIIIIIIIIIII9IG9IC"
				;
		
		FastqVisitor visitor = createMock(FastqVisitor.class);
		FastqRecordVisitor recordVisitor = createMock(FastqRecordVisitor.class);
		
		expect(visitor.visitDefline(anyObject(), eq("SRR001666.1"), eq("071112_SLXA-EAS1_s_7:5:1:817:345 length=36"))
				).andReturn(recordVisitor);
		
		recordVisitor.visitNucleotides("GGGTGATGGCCGCTGCCGATGGCGTCAAATCCCACC");
		recordVisitor.visitEncodedQualities("IIIIIIIIIIIIIIIIIIIIIIIIIIIIII9IG9IC");
		recordVisitor.visitEnd();
		
		visitor.visitEnd();
		
		replay(recordVisitor, visitor);
		
		FastqFileParser.create(stream(input), true,false)
							.parse(visitor);
		
		verify(recordVisitor, visitor);
		
		
	}
	
	@Test
	public void TooManyQualities() throws IOException{
		String input = 
				"@SRR001666.1 071112_SLXA-EAS1_s_7:5:1:817:345 length=36\n" +
				"GGGTGATGGCCGCTGCCGATGGCGTCAAATCCCACC\n"+
				"+SRR001666.1 071112_SLXA-EAS1_s_7:5:1:817:345 length=36\n"+
				"IIIIIIIIIIIIIIIIIIIIIIIIIIIIII9IG9ICXXXXXXXX"
				;
		
		
		
		FastqVisitor visitor = createMock(FastqVisitor.class);
		FastqRecordVisitor recordVisitor = createMock(FastqRecordVisitor.class);
		
		expect(visitor.visitDefline(anyObject(), eq("SRR001666.1"), eq("071112_SLXA-EAS1_s_7:5:1:817:345 length=36"))
				).andReturn(recordVisitor);
		
		recordVisitor.visitNucleotides("GGGTGATGGCCGCTGCCGATGGCGTCAAATCCCACC");
		
		
		replay(recordVisitor, visitor);
		
		thrown.expect(IOException.class);
		thrown.expectMessage(startsWith("incorrect number of quality values"));
		
		
		FastqFileParser.create(stream(input), true, false)
							.parse(visitor);
		
		
		
		
	}
	
	@Test
	public void TooFewQualitiesInLastRecord() throws IOException{
		String input = 
				"@SRR001666.1 071112_SLXA-EAS1_s_7:5:1:817:345 length=36\n" +
				"GGGTGATGGCCGCTGCCGATGGCGTCAAATCCCACC\n"+
				"+SRR001666.1 071112_SLXA-EAS1_s_7:5:1:817:345 length=36\n"+
				"IIIIIIIIIIIIIIIIIIIIIIIIIIIII"
				;
		
		
		
		FastqVisitor visitor = createMock(FastqVisitor.class);
		FastqRecordVisitor recordVisitor = createMock(FastqRecordVisitor.class);
		
		expect(visitor.visitDefline(anyObject(), eq("SRR001666.1"), eq("071112_SLXA-EAS1_s_7:5:1:817:345 length=36"))
				).andReturn(recordVisitor);
		
		recordVisitor.visitNucleotides("GGGTGATGGCCGCTGCCGATGGCGTCAAATCCCACC");
		
		
		replay(recordVisitor, visitor);
		
		thrown.expect(IOException.class);
		thrown.expectMessage(startsWith("too few quality values"));
		
		
		FastqFileParser.create(stream(input), true, false)
							.parse(visitor);
		
		
		
		
	}
	
	@Test
	public void TooFewQualitiesInFirstRecord() throws IOException{
		String input = 
				"@SRR001666.1 071112_SLXA-EAS1_s_7:5:1:817:345 length=36\n" +
				"GGGTGATGGCCGCTGCCGATGGCGTCAAATCCCACC\n"+
				"+SRR001666.1 071112_SLXA-EAS1_s_7:5:1:817:345 length=36\n"+
				"IIIIIIIIIIIIIIIIIIIIIIIIIIIII\n"+
				
				"@Read2\n" +
				"ACGTACGT\n" +
				"+\n" +
				"IIIIIIII"
				;
		
		
		
		FastqVisitor visitor = createMock(FastqVisitor.class);
		FastqRecordVisitor recordVisitor = createMock(FastqRecordVisitor.class);
		
		expect(visitor.visitDefline(anyObject(), eq("SRR001666.1"), eq("071112_SLXA-EAS1_s_7:5:1:817:345 length=36"))
				).andReturn(recordVisitor);
		
		recordVisitor.visitNucleotides("GGGTGATGGCCGCTGCCGATGGCGTCAAATCCCACC");
		
		
		replay(recordVisitor, visitor);
		
		thrown.expect(IOException.class);
		thrown.expectMessage(startsWith("incorrect number of quality values"));
		
		
		FastqFileParser.create(stream(input),true, false)
							.parse(visitor);
		
		
		
		
	}
	
	
	private InputStream stream(String s){
		return IOUtil.toInputStream(s);
	}
}
