/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta.pos;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Comparator;

import org.jcvi.jillion.core.pos.Position;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.AbstractFastaRecordWriter;
import org.jcvi.jillion.fasta.FastaWriter;
import org.jcvi.jillion.fasta.AbstractFastaRecordWriter.AbstractBuilder;
import org.jcvi.jillion.internal.fasta.InMemorySortedFastaWriter;
import org.jcvi.jillion.internal.fasta.TmpDirSortedFastaWriter;
/**
 * {@code PositionFastaWriterBuilder} will create
 * new {@link PositionFastaWriter} instance
 * that will write fasta encoded sanger 
 * position information to the given File
 * or OutputStream.
 * @author dkatzel
 *
 */
public final class PositionFastaWriterBuilder extends AbstractBuilder<Position, PositionSequence, PositionFastaRecord, PositionFastaWriter> {
		
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
		public PositionFastaWriterBuilder(File outputFile) throws IOException {
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
		public PositionFastaWriterBuilder(OutputStream out) {
			super(out);
		}

		@Override
		protected PositionFastaWriter create(
				OutputStream out, int numberOfResiduesPerLine, Charset charSet, String eol) {
			return new PositionSequenceFastaRecordWriterImpl(out, numberOfResiduesPerLine, charSet,eol);
		}
		@Override
		protected int getDefaultNumberOfSymbolsPerLine() {
			return 12;
		}
	
		@Override
		protected PositionFastaWriter createTmpDirSortedWriterWriter(
				FastaWriter<Position, PositionSequence, PositionFastaRecord> delegate,
				Comparator<PositionFastaRecord> comparator, int cacheSize,
				File tmpDir) {
			return new TmpDirSortedPositionFastaWriter(delegate, comparator, cacheSize, tmpDir);
		}
		@Override
		protected PositionFastaWriter createInMemorySortedWriterWriter(
				FastaWriter<Position, PositionSequence, PositionFastaRecord> delegate,
				Comparator<PositionFastaRecord> comparator) {
			return new InMemorySortedPositionFastaWriter(delegate, comparator);
		}

		private static final class PositionSequenceFastaRecordWriterImpl  extends AbstractFastaRecordWriter<Position, PositionSequence, PositionFastaRecord> implements PositionFastaWriter{

			private PositionSequenceFastaRecordWriterImpl(OutputStream out,
					int numberOfResiduesPerLine, Charset charSet, String eol) {
				super(out, numberOfResiduesPerLine, charSet,eol);
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
		
		
		private static final class InMemorySortedPositionFastaWriter extends InMemorySortedFastaWriter<Position, PositionSequence, PositionFastaRecord> implements PositionFastaWriter{

			public InMemorySortedPositionFastaWriter(FastaWriter<Position, PositionSequence, PositionFastaRecord> writer,
					Comparator<PositionFastaRecord> comparator) {
				super(writer, comparator);
			}

			@Override
			protected PositionFastaRecord createRecord(String id,
					PositionSequence sequence, String optionalComment) {

				return new PositionFastaRecord(id, optionalComment, sequence);
			}
			
		}
		
		private static final class TmpDirSortedPositionFastaWriter extends TmpDirSortedFastaWriter<Position, PositionSequence, PositionFastaRecord> implements PositionFastaWriter{

			

			public TmpDirSortedPositionFastaWriter(
					FastaWriter<Position, PositionSequence, PositionFastaRecord> finalWriter,
					Comparator<PositionFastaRecord> comparator, int cacheSize,
					File tmpDir) {
				super(finalWriter, comparator, tmpDir, cacheSize);
			}

			@Override
			protected StreamingIterator<PositionFastaRecord> createStreamingIteratorFor(
					File tmpFastaFile) throws IOException {
				return LargePositionFastaIterator.createNewIteratorFor(tmpFastaFile);
			}

			@Override
			protected FastaWriter<Position, PositionSequence, PositionFastaRecord> createNewTmpWriter(
					File tmpFile) throws IOException {
				return new PositionFastaWriterBuilder(tmpFile)
									.build();
			}

			@Override
			protected PositionFastaRecord createFastaRecord(String id,
					PositionSequence sequence, String optionalComment) {

				return new PositionFastaRecord(id, optionalComment, sequence);
			}
			
		}
	}

