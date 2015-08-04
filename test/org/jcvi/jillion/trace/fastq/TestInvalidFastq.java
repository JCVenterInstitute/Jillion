package org.jcvi.jillion.trace.fastq;

import static org.easymock.EasyMock.*;
import static org.hamcrest.core.StringStartsWith.startsWith;

import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback;
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
		
		expect(visitor.visitDefline(isA(FastqVisitorCallback.class), eq("SRR001666.1"), eq("071112_SLXA-EAS1_s_7:5:1:817:345 length=36"))
				).andReturn(recordVisitor);
		
		recordVisitor.visitNucleotides(seq("GGGTGATGGCCGCTGCCGATGGCGTCAAATCCCACC"));
		recordVisitor.visitEncodedQualities("IIIIIIIIIIIIIIIIIIIIIIIIIIIIII9IG9IC");
		recordVisitor.visitEnd();
		
		visitor.visitEnd();
		
		replay(recordVisitor, visitor);
		
		FastqFileParser.create(stream(input))
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
		
		expect(visitor.visitDefline(isA(FastqVisitorCallback.class), eq("SRR001666.1"), eq("071112_SLXA-EAS1_s_7:5:1:817:345 length=36"))
				).andReturn(recordVisitor);
		
		recordVisitor.visitNucleotides(seq("GGGTGATGGCCGCTGCCGATGGCGTCAAATCCCACC"));
		
		
		replay(recordVisitor, visitor);
		
		thrown.expect(IOException.class);
		thrown.expectMessage(startsWith("incorrect number of quality values"));
		
		
		FastqFileParser.create(stream(input))
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
		
		expect(visitor.visitDefline(isA(FastqVisitorCallback.class), eq("SRR001666.1"), eq("071112_SLXA-EAS1_s_7:5:1:817:345 length=36"))
				).andReturn(recordVisitor);
		
		recordVisitor.visitNucleotides(seq("GGGTGATGGCCGCTGCCGATGGCGTCAAATCCCACC"));
		
		
		replay(recordVisitor, visitor);
		
		thrown.expect(IOException.class);
		thrown.expectMessage(startsWith("too few quality values"));
		
		
		FastqFileParser.create(stream(input))
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
		
		expect(visitor.visitDefline(isA(FastqVisitorCallback.class), eq("SRR001666.1"), eq("071112_SLXA-EAS1_s_7:5:1:817:345 length=36"))
				).andReturn(recordVisitor);
		
		recordVisitor.visitNucleotides(seq("GGGTGATGGCCGCTGCCGATGGCGTCAAATCCCACC"));
		
		
		replay(recordVisitor, visitor);
		
		thrown.expect(IOException.class);
		thrown.expectMessage(startsWith("incorrect number of quality values"));
		
		
		FastqFileParser.create(stream(input))
							.parse(visitor);
		
		
		
		
	}
	
	
	private NucleotideSequence seq(String s){
		return new NucleotideSequenceBuilder(s).build();
	}
	private InputStream stream(String s){
		return IOUtil.toInputStream(s);
	}
}
