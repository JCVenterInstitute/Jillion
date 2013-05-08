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
package org.jcvi.jillion.fasta.aa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.internal.fasta.AbstractResidueSequenceFastaRecordWriter;
/**
 * {@code AminoAcidFastaRecordWriterBuilder} is a Builder
 * class that will create a new instance of 
 * {@link AminoAcidFastaRecordWriter}.
 * @author dkatzel
 *
 */
public final class AminoAcidFastaRecordWriterBuilder extends AbstractResidueSequenceFastaRecordWriter.Builder<AminoAcid, AminoAcidSequence, AminoAcidFastaRecord,AminoAcidFastaRecordWriter> {
	
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
	public AminoAcidFastaRecordWriterBuilder(File outputFile) throws FileNotFoundException {
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
	public AminoAcidFastaRecordWriterBuilder(OutputStream out) {
		super(out);
	}

	@Override
	protected AminoAcidFastaRecordWriter create(
			OutputStream out, int numberOfResiduesPerLine, Charset charSet) {
		return new AminoAcidSequenceFastaRecordWriterImpl(out, numberOfResiduesPerLine, charSet);
	}
	
	private static final class AminoAcidSequenceFastaRecordWriterImpl extends AbstractResidueSequenceFastaRecordWriter<AminoAcid, AminoAcidSequence, AminoAcidFastaRecord> implements AminoAcidFastaRecordWriter{

		private AminoAcidSequenceFastaRecordWriterImpl(OutputStream out,
				int numberOfResiduesPerLine, Charset charSet) {
			super(out, numberOfResiduesPerLine, charSet);
		}
	}
}
