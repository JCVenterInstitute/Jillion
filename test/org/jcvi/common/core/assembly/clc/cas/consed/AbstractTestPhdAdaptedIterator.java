package org.jcvi.common.core.assembly.clc.cas.consed;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.common.core.assembly.ace.PhdInfo;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.seq.read.trace.sanger.phd.ArtificialPhd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdUtil;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;

public class AbstractTestPhdAdaptedIterator {

	protected final PhdReadRecord createExpectedPhdReadRecord(File traceFile,String id, NucleotideSequence basecalls, QualitySequence quals, Date phdDate){
		Phd phd=  ArtificialPhd.createNewbler454Phd(
				id, 
				basecalls, 
				quals,
				PhdUtil.createPhdTimeStampCommentFor(phdDate));
		PhdInfo info = ConsedUtil.generateDefaultPhdInfoFor(traceFile, id, phdDate);
		
		return new DefaultPhdReadRecord(phd, info);
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
