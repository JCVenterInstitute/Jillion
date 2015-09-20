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
package org.jcvi.jillion.examples.sam;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.sam.index.BamIndexFileWriterBuilder;

public class CreateBamIndexFromBam {

	public static void main(String[] args) throws IOException {
		File bamFile = new File("/path/to/input/example.bam");
		File baiFile = new File("/path/to/output/example.bam.bai");
		
		File outputBaiFile = new BamIndexFileWriterBuilder(bamFile, baiFile)
				.includeMetaData(true) //includes metadata that Picard and samtools use
				.assumeSorted(true)
				.build();
		
		//output bai file == baiFile
				

	}

}
