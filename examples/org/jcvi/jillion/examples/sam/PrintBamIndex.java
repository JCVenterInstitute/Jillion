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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.jcvi.jillion.internal.sam.index.IndexUtil;
import org.jcvi.jillion.sam.SamParser;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.index.BamIndex;
import org.jcvi.jillion.sam.index.Bin;
import org.jcvi.jillion.sam.index.ReferenceIndex;

public class PrintBamIndex {

	public static void main(String[] args) throws IOException {
		
		//File bai = new File(args[0]);
		/*File jillion = new File("/usr/local/scratch/dkatzel/jillion.UHR10pgD93_tophat.sorted.bam.bai");
		File picard = new File("/usr/local/scratch/dkatzel/picard.jillion.UHR10pgD93_tophat.sorted.bam.bai");

		File bai = jillion;
		File bam = new File(jillion.getParentFile(), FileUtil.getBaseName(jillion));
		*/
		
		//File bai = new File("/usr/local/scratch/dkatzel/failed.test.bai");
		File bai = new File("/usr/local/scratch/dkatzel/jillion.index_test.bam.bai");
		
		File bam = new File("/usr/local/scratch/dkatzel/index_test.bam");
		
		SamParser parser = SamParserFactory.create(bam);
		try(InputStream in = new FileInputStream(bai);
				PrintWriter out = new PrintWriter(new File(bai.getParentFile(), "printBai." + bai.getName()))){
			
			BamIndex index = IndexUtil.parseIndex(in, parser.getHeader());
			int refNum = index.getNumberOfReferenceIndexes();
			out.println("num refs = " + refNum);
			for(int i =0; i< refNum; i++){
				ReferenceIndex r= index.getReferenceIndex(i);
				out.println("ref " + i);
				for(Bin bin : r.getBins()){
					out.println(bin);
				}
				VirtualFileOffset[] intervals = r.getIntervals();
				out.println("# of intervals = " + intervals.length);
				for(VirtualFileOffset vfo : intervals){
					out.println(vfo);
				}
			}
			
			
		}
		
	}

}
