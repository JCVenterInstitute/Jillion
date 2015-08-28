/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.fasta.FastaRecord;
import org.jcvi.jillion.fasta.FastaWriter;

/**
 * {@code AbstractResidueFastaWriter} is an abstract
 * implementation of a {@link FastaWriter} that 
 * writes {@link ResidueSequence}s.
 * @author dkatzel
 *
 * @param <R> the type of {@link Residue}
 * @param <S> the type of {@link Sequence}
 * @param <F> the Type of {@link FastaRecord}
 */
public abstract class AbstractResidueFastaWriter<R extends Residue, S extends ResidueSequence<R>, F extends FastaRecord<R,S>> extends  AbstractFastaRecordWriter<R, S, F>{

	
	protected AbstractResidueFastaWriter(OutputStream out,
			int numberOfResiduesPerLine, Charset charSet, String eol) {
		super(out, numberOfResiduesPerLine, charSet, eol);
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
	 * Builder creates a builder instance that will build a {@link FastaWriter}
	 * using the given {@link OutputStream}
	 *  and any additional configuration given.
	 * @author dkatzel
	 *
	 * @param <R> the type of {@link Residue}
	 * @param <S> the type of {@link Sequence}
	 * @param <F> the Type of {@link FastaRecord}
	 * @param <W> the Type of {@link FastaWriter} that will be built.
	 */
	public abstract static class Builder<R extends Residue, S extends ResidueSequence<R>,F extends FastaRecord<R,S>, W extends FastaWriter<R, S, F>> extends AbstractBuilder<R,S,F,W>{
		protected static final int DEFAULT_RESIDUES_PER_LINE = 60;
		   
		public Builder(File outputFile)
				throws IOException {
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
