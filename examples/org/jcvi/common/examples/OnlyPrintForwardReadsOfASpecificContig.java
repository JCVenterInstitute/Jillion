package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.assembly.ace.AbstractAceFileVisitor;
import org.jcvi.common.core.assembly.ace.AceFileParser;
import org.jcvi.common.core.assembly.ace.AceFileVisitor;
import org.jcvi.common.core.assembly.ace.PhdInfo;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;

public class OnlyPrintForwardReadsOfASpecificContig {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		
		File aceFile = new File("path/to/ace/file");

		AceFileVisitor visitor = new AbstractAceFileVisitor() {
			String contigIdToPrint = "myContigId";
			//we only want to visit the contig with 
			//the id we care about
			@Override
			public boolean shouldParseContig(String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplimented) {
				return contigIdToPrint.equals(contigId);
			}

			
			
			@Override
			protected void visitAceRead(String readId,
					NucleotideSequence validBasecalls, int offset, Direction dir,
					Range validRange, PhdInfo phdInfo, int ungappedFullLength) {
				if(dir == Direction.REVERSE){
					System.out.printf("%s starts at offset %d%n",readId, offset);
				}
				
			}
			
			@Override
			protected void visitNewContig(String contigId,
					NucleotideSequence consensus, int numberOfBases, int numberOfReads,
					boolean isComplimented) {
				//no-op
				
			}
		};
		
		AceFileParser.parse(aceFile, visitor);
	}

}
