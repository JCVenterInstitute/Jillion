package org.jcvi.jillion.trace.sanger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.jillion.internal.fasta.AbstractFastaRecordWriter;
import org.jcvi.jillion.internal.fasta.AbstractFastaRecordWriter.AbstractBuilder;

public final class PositionSequenceFastaRecordWriterBuilder extends AbstractBuilder<Position, PositionSequence, PositionSequenceFastaRecord, PositionSequenceFastaRecordWriter> {
		/**
		 * Create a new Builder that will use
		 * the given {@link OutputStream} to write
		 * out the fasta records.
		 * @param out the {@link OutputStream} to use;
		 * can not be null.
		 * @throws NullPointerException if out is null.
		 */
		public PositionSequenceFastaRecordWriterBuilder(File outputFile) throws FileNotFoundException {
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
		public PositionSequenceFastaRecordWriterBuilder(OutputStream out) {
			super(out);
		}

		@Override
		protected PositionSequenceFastaRecordWriter create(
				OutputStream out, int numberOfResiduesPerLine, Charset charSet) {
			return new PositionSequenceFastaRecordWriterImpl(out, numberOfResiduesPerLine, charSet);
		}
		@Override
		protected int getDefaultNumberOfSymbolsPerLine() {
			return 12;
		}
	
		private static final class PositionSequenceFastaRecordWriterImpl  extends AbstractFastaRecordWriter<Position, PositionSequence, PositionSequenceFastaRecord> implements PositionSequenceFastaRecordWriter{

			private PositionSequenceFastaRecordWriterImpl(OutputStream out,
					int numberOfResiduesPerLine, Charset charSet) {
				super(out, numberOfResiduesPerLine, charSet);
			}

			@Override
			protected String getStringRepresentationFor(Position symbol) {
				return String.format("%04d", symbol.getValue());
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
				return 5*numberOfSymbols;
			}
		}
	}

