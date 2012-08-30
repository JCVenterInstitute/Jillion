package org.jcvi.common.core.seq.fastx.fasta.aa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.common.core.seq.fastx.fasta.DefaultResidueSequenceFastaRecordWriter;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;

public final class DefaultAminoAcidSequenceFastaRecordWriter extends DefaultResidueSequenceFastaRecordWriter<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord> implements AminoAcidSequenceFastaRecordWriter{

	private DefaultAminoAcidSequenceFastaRecordWriter(OutputStream out,
			int numberOfResiduesPerLine, Charset charSet) {
		super(out, numberOfResiduesPerLine, charSet);
	}

	public static class Builder extends DefaultResidueSequenceFastaRecordWriter.AbstractResidueBuilder<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord,AminoAcidSequenceFastaRecordWriter> {
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
		protected AminoAcidSequenceFastaRecordWriter create(
				OutputStream out, int numberOfResiduesPerLine, Charset charSet) {
			return new DefaultAminoAcidSequenceFastaRecordWriter(out, numberOfResiduesPerLine, charSet);
		}
		
	}
}
