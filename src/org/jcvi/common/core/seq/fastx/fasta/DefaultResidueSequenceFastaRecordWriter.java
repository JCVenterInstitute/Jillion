package org.jcvi.common.core.seq.fastx.fasta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.common.core.symbol.residue.Residue;
import org.jcvi.common.core.symbol.residue.ResidueSequence;


public  class DefaultResidueSequenceFastaRecordWriter<R extends Residue, T extends ResidueSequence<R>, F extends FastaRecord<R,T>> extends  AbstractFastaRecordWriter<R, T, F>{

	
	protected DefaultResidueSequenceFastaRecordWriter(OutputStream out,
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


	public static abstract class AbstractResidueBuilder<R extends Residue, T extends ResidueSequence<R>,F extends FastaRecord<R,T>, W extends FastaRecordWriter<R, T, F>> extends AbstractBuilder<R,T,F,W>{
		protected static final int DEFAULT_RESIDUES_PER_LINE = 60;
		   
		public AbstractResidueBuilder(File outputFile)
				throws FileNotFoundException {
			super(outputFile);
		}

		public AbstractResidueBuilder(OutputStream out) {
			super(out);
		}

		@Override
		protected int getDefaultNumberOfSymbolsPerLine() {
			return DEFAULT_RESIDUES_PER_LINE;
		}
		
		
	}

	
}
