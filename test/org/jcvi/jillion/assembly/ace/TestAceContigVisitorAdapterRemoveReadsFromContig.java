package org.jcvi.jillion.assembly.ace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
public class TestAceContigVisitorAdapterRemoveReadsFromContig {

	private final Date phdDate = new Date(123456789); 
	
	@Test
	public void removeSingleReadFromContig() throws IOException{
		final AceContig contig = new AceContigBuilder("contig1", "ACGTACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGTACGT").build(), 0, Direction.FORWARD, Range.of(10,17), new PhdInfo("read1", "read1.phd", phdDate), 25)
							.addRead("read2", new NucleotideSequenceBuilder("ACGTACGT").build(), 0, Direction.FORWARD, Range.of(10,17), new PhdInfo("read2", "read2.phd", phdDate), 25)
							.addRead("read3", new NucleotideSequenceBuilder("ACGT").build(), 4, Direction.FORWARD, Range.of(14,17), new PhdInfo("read3", "read3.phd", phdDate), 25)
							
							.build();
		
		AceHandler handler = AceTestUtil.createAceHandlerFor(contig);
		final AtomicBoolean visitedContig = new AtomicBoolean(false);
		handler.accept(new AbstractAceFileVisitor() {

			@Override
			public AceContigVisitor visitContig(
					AceFileVisitorCallback callback, String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplemented) {
				AceContigVisitor delegate = new AbstractAceContigBuilderVisitor(contigId, numberOfBases, numberOfReads) {
					
					@Override
					protected void visitContig(AceContigBuilder builder) {
						AceContig actual = builder.build();
						AceContig expected = new AceContigBuilder(contig)
													.removeRead("read2")
													.build();
						
						assertEquals(actual, expected);
						visitedContig.set(true);
					}
				};
				
				return new AceContigVisitorAdapter(delegate){

					@Override
					public AceContigReadVisitor visitBeginRead(String readId,
							int gappedLength) {
						if(readId.equals("read2")){
							//skip read2
							return null;
						}
						return getDelegate().visitBeginRead(readId, gappedLength);
					}
					
				};
			}
			
		});
		
		assertTrue(visitedContig.get());
	}
}
