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
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.io.InputStreamSupplier;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;

/**
 * Casava 1.8 changes the fastq mated read names
 * to have the mate pairs have the same read name
 * and the optional comment to be the mate info.
 * Since the mates are in different files this usually isn't 
 * a problem unless you are combining reads from many files (like an assembler).
 * 
 * @author dkatzel
 *
 *
 */
public class TestCasava18 {
    
    @Test
    public void parseIndexedRead() throws FileNotFoundException, IOException{
    	
    	
        FastqVisitor visitor = new FastqVisitor() {
			
			@Override
			public void visitEnd() {
				//no-op				
			}
			@Override
			public void halted(){
				//no-op
	    	}
			@Override
			public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
					String id, String optionalComment) {
				assertEquals("EAS139:136:FC706VJ:2:5:1000:12850 1:Y:18:ATCACG", id);
				return null;
			}
		};

        ResourceHelper resources = new ResourceHelper(TestCasava18.class);
        FastqFileParser.create(
                InputStreamSupplier.forFile(resources.getFile("files/casava1.8.fastq")), false, false, false).parse(visitor);
    }
    
    @Test
    public void parseNotIndexedRead() throws FileNotFoundException, IOException{
    	
    	
        FastqVisitor visitor = new FastqVisitor() {
			
			@Override
			public void visitEnd() {
				//no-op				
			}
			@Override
			public void halted(){
				//no-op
	    	}
			@Override
			public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
					String id, String optionalComment) {
				assertEquals("EAS139:136:FC706VJ:2:5:1000:12850 1:Y:18:", id);
				return null;
			}
		};

        ResourceHelper resources = new ResourceHelper(TestCasava18.class);
        FastqFileParser.create(InputStreamSupplier.forFile(resources.getFile("files/casava1.8.miseq.fastq")), false, false, false).parse(visitor);
    }
}
