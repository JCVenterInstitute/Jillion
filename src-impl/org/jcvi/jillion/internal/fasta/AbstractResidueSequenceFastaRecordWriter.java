/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.fasta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.fasta.FastaRecord;
import org.jcvi.jillion.fasta.FastaRecordWriter;

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
