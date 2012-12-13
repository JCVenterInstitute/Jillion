package org.jcvi.common.core.seq.fasta.aa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.common.core.seq.fasta.impl.AbstractResidueSequenceFastaRecordWriter;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;
/**
 * {@code AminoAcidSequenceFastaRecordWriterBuilder} is a Builder
 * class that will create a new instance of 
 * {@link AminoAcidSequenceFastaRecordWriter}.
 * @author dkatzel
 *
 */
public final class AminoAcidSequenceFastaRecordWriterBuilder extends AbstractResidueSequenceFastaRecordWriter.Builder<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord,AminoAcidSequenceFastaRecordWriter> {
	/**
	 * Create a new Builder that will use
	 * the given {@link OutputStream} to write
	 * out the fasta records.
	 * @param out the {@link OutputStream} to use;
	 * can not be null.
	 * @throws NullPointerException if out is null.
	 */
	public AminoAcidSequenceFastaRecordWriterBuilder(File outputFile) throws FileNotFoundException {
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
	public AminoAcidSequenceFastaRecordWriterBuilder(OutputStream out) {
		super(out);
	}

	@Override
	protected AminoAcidSequenceFastaRecordWriter create(
			OutputStream out, int numberOfResiduesPerLine, Charset charSet) {
		return new AminoAcidSequenceFastaRecordWriterImpl(out, numberOfResiduesPerLine, charSet);
	}
	
	private static final class AminoAcidSequenceFastaRecordWriterImpl extends AbstractResidueSequenceFastaRecordWriter<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord> implements AminoAcidSequenceFastaRecordWriter{

		private AminoAcidSequenceFastaRecordWriterImpl(OutputStream out,
				int numberOfResiduesPerLine, Charset charSet) {
			super(out, numberOfResiduesPerLine, charSet);
		}
	}
}
