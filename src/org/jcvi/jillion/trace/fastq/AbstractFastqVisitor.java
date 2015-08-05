package org.jcvi.jillion.trace.fastq;

/**
 * {@code AbstractFastqVisitor} is a {@link FastqVisitor}
 * implementation that implements all methods
 * as no-ops.
 * 
 * @author dkatzel
 * 
 * @since 5.0
 *
 */
public abstract class AbstractFastqVisitor implements FastqVisitor {
	/**
	 * Skips the record with the provided define;
	 * please override if you want to visit {@link FastqRecord}s.
	 * @return null
	 */
	@Override
	public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
			String id, String optionalComment) {
		return null;
	}
	/**
	 * Does nothing by default, please override
	 * if you want to do something when all the fastqs
	 * have been visited.
	 */
	@Override
	public void visitEnd() {
		//no-op

	}
	/**
	 * Does nothing by default, please override
	 * if you want to do something when all the fastqs
	 * have been visited.
	 */
	@Override
	public void halted() {
		//no-op

	}

}
