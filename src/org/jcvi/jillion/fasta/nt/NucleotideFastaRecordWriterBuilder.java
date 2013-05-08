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
package org.jcvi.jillion.fasta.nt;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.internal.fasta.AbstractResidueSequenceFastaRecordWriter;
/**
 * {@code NucleotideFastaRecordWriterBuilder} is a Builder
 * class that will create a new instance of 
 * {@link NucleotideFastaRecordWriter}.
 * @author dkatzel
 *
 */
public final class NucleotideFastaRecordWriterBuilder extends AbstractResidueSequenceFastaRecordWriter.Builder<Nucleotide, NucleotideSequence, NucleotideFastaRecord,NucleotideFastaRecordWriter> {
		
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
		public NucleotideFastaRecordWriterBuilder(File outputFile) throws FileNotFoundException {
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
		public NucleotideFastaRecordWriterBuilder(OutputStream out) {
			super(out);
		}

		@Override
		protected NucleotideFastaRecordWriter create(
				OutputStream out, int numberOfResiduesPerLine, Charset charSet) {
			return new NucleotideSequenceFastaRecordWriterImpl(out, numberOfResiduesPerLine, charSet);
		}
		
		private static final class NucleotideSequenceFastaRecordWriterImpl extends AbstractResidueSequenceFastaRecordWriter<Nucleotide, NucleotideSequence, NucleotideFastaRecord> implements NucleotideFastaRecordWriter{

			private NucleotideSequenceFastaRecordWriterImpl(OutputStream out,
					int numberOfResiduesPerLine, Charset charSet) {
				super(out, numberOfResiduesPerLine, charSet);
			}
		}
}
