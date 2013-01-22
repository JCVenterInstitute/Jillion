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
package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.ace.AbstractAceFileVisitor;
import org.jcvi.jillion.assembly.ace.AceFileParser;
import org.jcvi.jillion.assembly.ace.AceFileVisitor;
import org.jcvi.jillion.assembly.ace.PhdInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

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
