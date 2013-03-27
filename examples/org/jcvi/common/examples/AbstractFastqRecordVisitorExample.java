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
