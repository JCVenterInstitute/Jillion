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
package org.jcvi.jillion.fasta.aa;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.internal.fasta.AbstractResidueFastaWriter;
/**
 * {@code ProteinFastaWriterBuilder} is a Builder
 * class that will create a new instance of 
 * {@link ProteinFastaWriter}
 * that will write amino acid (protein)
 * data to the given File or OutputStream.
 * @author dkatzel
 *
 */
public final class ProteinFastaWriterBuilder extends AbstractResidueFastaWriter.Builder<AminoAcid, ProteinSequence, ProteinFastaRecord,ProteinFastaWriter> {
	
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
	public ProteinFastaWriterBuilder(File outputFile) throws IOException {
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
	public ProteinFastaWriterBuilder(OutputStream out) {
		super(out);
	}

	@Override
	protected ProteinFastaWriter create(
			OutputStream out, int numberOfResiduesPerLine, Charset charSet, String eol) {
		return new ProteinFastaWriterImpl(out, numberOfResiduesPerLine, charSet,eol);
	}
	
	private static final class ProteinFastaWriterImpl extends AbstractResidueFastaWriter<AminoAcid, ProteinSequence, ProteinFastaRecord> implements ProteinFastaWriter{

		private ProteinFastaWriterImpl(OutputStream out,
				int numberOfResiduesPerLine, Charset charSet, String eol) {
			super(out, numberOfResiduesPerLine, charSet,eol);
		}
	}
}
