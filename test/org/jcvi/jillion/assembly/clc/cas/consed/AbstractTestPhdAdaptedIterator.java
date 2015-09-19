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
package org.jcvi.jillion.assembly.clc.cas.consed;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.jillion.assembly.consed.ConsedUtil;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class AbstractTestPhdAdaptedIterator {

	protected final PhdReadRecord createExpectedPhdReadRecord(File traceFile,String id, NucleotideSequence basecalls, QualitySequence quals, Date phdDate){
		Phd phd=  new PhdBuilder(id, basecalls, quals)
					.comments(PhdUtil.createPhdTimeStampCommentFor(phdDate))
					.fakePeaks()
					.build();
		PhdInfo info = ConsedUtil.generateDefaultPhdInfoFor(traceFile, id, phdDate);
		
		return new PhdReadRecord(phd, info);
	}
	
	protected final void throwsExceptionWhenNoMoreElements(Iterator<?> iter){
		try{
			iter.next();
			fail("should throw No SuchElementException when no more elements");
		}catch(NoSuchElementException expected){
			//expected
		}
	}
}
