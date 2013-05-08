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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta.pos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.pos.Position;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.internal.fasta.AbstractFastaRecordWriter;
import org.jcvi.jillion.internal.fasta.AbstractFastaRecordWriter.AbstractBuilder;

public final class PositionFastaRecordWriterBuilder extends AbstractBuilder<Position, PositionSequence, PositionFastaRecord, PositionFastaRecordWriter> {
		
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
		public PositionFastaRecordWriterBuilder(File outputFile) throws FileNotFoundException {
			super(outputFile);
		}
		/**
		 * Create a new Builder that will use
		 * the given {@link OutputStream} to write
		 * out the fasta records.
		 * @param out the {@link OutputStream} to use;
		 * can not be null.
		 * @throws NullPointerException if out is null.
		 */
		public PositionFastaRecordWriterBuilder(OutputStream out) {
			super(out);
		}

		@Override
		protected PositionFastaRecordWriter create(
				OutputStream out, int numberOfResiduesPerLine, Charset charSet) {
			return new PositionSequenceFastaRecordWriterImpl(out, numberOfResiduesPerLine, charSet);
		}
		@Override
		protected int getDefaultNumberOfSymbolsPerLine() {
			return 12;
		}
	
		private static final class PositionSequenceFastaRecordWriterImpl  extends AbstractFastaRecordWriter<Position, PositionSequence, PositionFastaRecord> implements PositionFastaRecordWriter{

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

