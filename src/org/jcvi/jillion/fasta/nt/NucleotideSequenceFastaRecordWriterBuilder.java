package org.jcvi.jillion.fasta.nt;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.internal.fasta.AbstractResidueSequenceFastaRecordWriter;
/**
 * {@code NucleotideSequenceFastaRecordWriterBuilder} is a Builder
 * class that will create a new instance of 
 * {@link NucleotideSequenceFastaRecordWriter}.
 * @author dkatzel
 *
 */
public final class NucleotideSequenceFastaRecordWriterBuilder extends AbstractResidueSequenceFastaRecordWriter.Builder<Nucleotide, NucleotideSequence, NucleotideSequenceFastaRecord,NucleotideSequenceFastaRecordWriter> {
		/**
		 * Create a new Builder that will use
		 * the given {@link OutputStream} to write
		 * out the fasta records.
		 * @param out the {@link OutputStream} to use;
		 * can not be null.
		 * @throws NullPointerException if out is null.
		 */
		public NucleotideSequenceFastaRecordWriterBuilder(File outputFile) throws FileNotFoundException {
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
		public NucleotideSequenceFastaRecordWriterBuilder(OutputStream out) {
			super(out);
		}

		@Override
		protected NucleotideSequenceFastaRecordWriter create(
				OutputStream out, int numberOfResiduesPerLine, Charset charSet) {
			return new NucleotideSequenceFastaRecordWriterImpl(out, numberOfResiduesPerLine, charSet);
		}
		
		private static final class NucleotideSequenceFastaRecordWriterImpl extends AbstractResidueSequenceFastaRecordWriter<Nucleotide, NucleotideSequence, NucleotideSequenceFastaRecord> implements NucleotideSequenceFastaRecordWriter{

			private NucleotideSequenceFastaRecordWriterImpl(OutputStream out,
					int numberOfResiduesPerLine, Charset charSet) {
				super(out, numberOfResiduesPerLine, charSet);
			}
		}
}
