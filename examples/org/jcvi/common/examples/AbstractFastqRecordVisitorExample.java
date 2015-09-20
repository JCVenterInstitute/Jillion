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
package org.jcvi.common.examples;

import org.jcvi.jillion.trace.fastq.AbstractFastqRecordVisitor;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqRecordVisitor;
import org.jcvi.jillion.trace.fastq.FastqVisitor;

public class AbstractFastqRecordVisitorExample implements FastqVisitor{

	private FastqQualityCodec qualityCodec;

	@Override
	public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
			String id, String optionalComment) {

		return new AbstractFastqRecordVisitor(id, optionalComment, qualityCodec) {
			
			@Override
			protected void visitRecord(FastqRecord record) {
				//do something
				
			}
		};
	}

	@Override
	public void visitEnd() { }

	@Override
	public void halted() { }
	
	
}
