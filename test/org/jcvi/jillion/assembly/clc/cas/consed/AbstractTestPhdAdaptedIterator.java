/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
