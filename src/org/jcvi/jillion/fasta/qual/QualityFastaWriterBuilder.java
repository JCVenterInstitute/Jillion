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
package org.jcvi.jillion.fasta.qual;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.internal.fasta.AbstractFastaRecordWriter;
import org.jcvi.jillion.internal.fasta.AbstractFastaRecordWriter.AbstractBuilder;
/**
 * {@code QualityFastaWriterBuilder}
 * builds a new instance of {@link QualityFastaWriter}
 * that will write fasta encoded quality data
 * to the provided File or {@link OutputStream}. 
 * @author dkatzel
 *
 */
public final class QualityFastaWriterBuilder extends AbstractBuilder<PhredQuality, QualitySequence, QualityFastaRecord,QualityFastaWriter> {

	/**
	 * Create a new Builder that will use
	 * the given File to write
	 * out the fasta records.  Any contents
	 * that previously existed in this file
	 * will be overwritten.  If this file or
	 * any parent directories do not exist,
	 * then they will be created.
	 * @param outputFile the File to use;
	 * can not be null.
	 * @throws NullPointerException if outputFile is null.
	 * @throws IOException if the file exists but 
	 * is a directory rather than a regular file, 
	 * does not exist but cannot be created, 
	 * or cannot be opened for any other reason.
	 */
	public QualityFastaWriterBuilder(File outputFile) throws IOException {
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
	public QualityFastaWriterBuilder(OutputStream out) {
		super(out);
	}

	@Override
	protected QualityFastaWriter create(
			OutputStream out, int numberOfResiduesPerLine, Charset charSet, String eol) {
		return new QualitySequenceFastaRecordWriterImpl(out, numberOfResiduesPerLine, charSet, eol);
	}
	@Override
	protected int getDefaultNumberOfSymbolsPerLine() {
		return 17;
	}
	
	private static final class QualitySequenceFastaRecordWriterImpl extends AbstractFastaRecordWriter<PhredQuality, QualitySequence, QualityFastaRecord> implements QualityFastaWriter{

		private QualitySequenceFastaRecordWriterImpl(OutputStream out,
				int numberOfResiduesPerLine, Charset charSet, String eol) {
			super(out, numberOfResiduesPerLine, charSet, eol);
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
	}
}
