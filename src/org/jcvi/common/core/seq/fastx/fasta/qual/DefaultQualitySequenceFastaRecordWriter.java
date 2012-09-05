package org.jcvi.common.core.seq.fastx.fasta.qual;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaRecordWriter;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;

public final class DefaultQualitySequenceFastaRecordWriter extends AbstractFastaRecordWriter<PhredQuality, QualitySequence, QualitySequenceFastaRecord> implements QualitySequenceFastaRecordWriter{

	private DefaultQualitySequenceFastaRecordWriter(OutputStream out,
			int numberOfResiduesPerLine, Charset charSet) {
		super(out, numberOfResiduesPerLine, charSet);
	}

	@Override
	protected String getStringRepresentationFor(PhredQuality symbol) {
		return String.format("%02d", symbol.getQualityScore());
	}

	@Override
	protected boolean hasSymbolSeparator() {
		return true;
	}

	@Override
	protected String getSymbolSeparator() {
		return " ";
	}

	@Override
	protected int numberOfCharsFor(int numberOfSymbols) {
		return 3*numberOfSymbols;
	}

	
	public static class Builder extends AbstractBuilder<PhredQuality, QualitySequence, QualitySequenceFastaRecord,QualitySequenceFastaRecordWriter> {
		/**
		 * Create a new Builder that will use
		 * the given {@link OutputStream} to write
		 * out the fasta records.
		 * @param out the {@link OutputStream} to use;
		 * can not be null.
		 * @throws NullPointerException if out is null.
		 */
		public Builder(File outputFile) throws FileNotFoundException {
			super(outputFile);
		}
		/**
		 * Create a new Builder that will use
		 * the given File to write
		 * out the fasta records.  Any contents
		 * that previously existed in this file
		 * will be overwritten.
		 * @param outputFile the File to use;
		 * can not be null.
		 * @throws NullPointerException if outputFile is null.
		 * @throws FileNotFoundException if the file exists but 
		 * is a directory rather than a regular file, 
		 * does not exist but cannot be created, 
		 * or cannot be opened for any other reason.
		 */
		public Builder(OutputStream out) {
			super(out);
		}

		@Override
		protected QualitySequenceFastaRecordWriter create(
				OutputStream out, int numberOfResiduesPerLine, Charset charSet) {
			return new DefaultQualitySequenceFastaRecordWriter(out, numberOfResiduesPerLine, charSet);
		}
		@Override
		protected int getDefaultNumberOfSymbolsPerLine() {
			return 17;
		}
		
	}
}