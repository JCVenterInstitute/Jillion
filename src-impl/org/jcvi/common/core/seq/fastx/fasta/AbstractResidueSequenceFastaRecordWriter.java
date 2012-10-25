package org.jcvi.common.core.seq.fastx.fasta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.common.core.symbol.residue.Residue;
import org.jcvi.common.core.symbol.residue.ResidueSequence;

/**
 * {@code AbstractResidueSequenceFastaRecordWriter} is an abstract
 * implementation of a {@link FastaRecordWriter} that 
 * writes {@link ResidueSequence}s.
 * @author dkatzel
 *
 * @param <R>
 * @param <T>
 * @param <F>
 */
public abstract class AbstractResidueSequenceFastaRecordWriter<R extends Residue, T extends ResidueSequence<R>, F extends FastaRecord<R,T>> extends  AbstractFastaRecordWriter<R, T, F>{

	
	protected AbstractResidueSequenceFastaRecordWriter(OutputStream out,
			int numberOfResiduesPerLine, Charset charSet) {
		super(out, numberOfResiduesPerLine, charSet);
	}
	@Override
	protected String getStringRepresentationFor(R symbol) {
		return symbol.getCharacter().toString();
	}

	@Override
	protected int numberOfCharsFor(int numberOfSymbols) {
		return numberOfSymbols;
	}
	
	
	@Override
	protected boolean hasSymbolSeparator() {
		return false;
	}
	@Override
	protected String getSymbolSeparator() {
		return null;
	}

	/**
	 * 
	 * @author dkatzel
	 *
	 * @param <R>
	 * @param <T>
	 * @param <F>
	 * @param <W>
	 */
	public abstract static class Builder<R extends Residue, T extends ResidueSequence<R>,F extends FastaRecord<R,T>, W extends FastaRecordWriter<R, T, F>> extends AbstractBuilder<R,T,F,W>{
		protected static final int DEFAULT_RESIDUES_PER_LINE = 60;
		   
		public Builder(File outputFile)
				throws FileNotFoundException {
			super(outputFile);
		}

		public Builder(OutputStream out) {
			super(out);
		}

		@Override
		protected int getDefaultNumberOfSymbolsPerLine() {
			return DEFAULT_RESIDUES_PER_LINE;
		}
		
		
	}

	
}
