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
