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
package org.jcvi.jillion.fasta.qual;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Comparator;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaWriter;
import org.jcvi.jillion.internal.fasta.InMemorySortedFastaWriter;
import org.jcvi.jillion.internal.fasta.TmpDirSortedFastaWriter;
import org.jcvi.jillion.internal.fasta.qual.LargeQualityFastaFileDataStore;
import org.jcvi.jillion.shared.fasta.AbstractFastaRecordWriter;
import org.jcvi.jillion.shared.fasta.AbstractFastaRecordWriter.AbstractBuilder;
/**
 * {@code QualityFastaWriterBuilder}
 * builds a new instance of {@link QualityFastaWriter}
 * that will write fasta encoded quality data
 * to the provided File or {@link OutputStream}. 
 * @author dkatzel
 *
 */
public final class QualityFastaWriterBuilder extends AbstractBuilder<PhredQuality, QualitySequence, QualityFastaRecord,QualityFastaWriter, QualityFastaWriterBuilder> {

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
    protected QualityFastaWriterBuilder getThis() {
        return this;
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
	
	
	
	@Override
	protected QualityFastaWriter createTmpDirSortedWriterWriter(
			FastaWriter<PhredQuality, QualitySequence, QualityFastaRecord> delegate,
			Comparator<QualityFastaRecord> comparator, int cacheSize,
			File tmpDir) {
		return new TmpDirSortedQualityFastaWriter(delegate, comparator, cacheSize, tmpDir);
	}
	@Override
	protected QualityFastaWriter createInMemorySortedWriterWriter(
			FastaWriter<PhredQuality, QualitySequence, QualityFastaRecord> delegate,
			Comparator<QualityFastaRecord> comparator) {
		return new InMemorySortedPositionFastaWriter(delegate, comparator);
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
	}
	
	private static final class InMemorySortedPositionFastaWriter extends InMemorySortedFastaWriter<PhredQuality, QualitySequence, QualityFastaRecord> implements QualityFastaWriter{

		public InMemorySortedPositionFastaWriter(FastaWriter<PhredQuality, QualitySequence, QualityFastaRecord> writer,
				Comparator<QualityFastaRecord> comparator) {
			super(writer, comparator);
		}

		@Override
		protected QualityFastaRecord createRecord(String id, QualitySequence sequence, String optionalComment) {
			return new QualityFastaRecordBuilder(id, sequence)
							.comment(optionalComment)
							.build();
		}
		
	}
	
	private static final class TmpDirSortedQualityFastaWriter extends TmpDirSortedFastaWriter<PhredQuality, QualitySequence, QualityFastaRecord> implements QualityFastaWriter{

		

		public TmpDirSortedQualityFastaWriter(
				FastaWriter<PhredQuality, QualitySequence, QualityFastaRecord> finalWriter,
				Comparator<QualityFastaRecord> comparator, int cacheSize,
				File tmpDir) {
			super(finalWriter, comparator, tmpDir, cacheSize);
		}

		@Override
		protected StreamingIterator<QualityFastaRecord> createStreamingIteratorFor(
				File tmpFastaFile) throws IOException, DataStoreException {
			return LargeQualityFastaFileDataStore.create(tmpFastaFile).iterator();
		}

		@Override
		protected FastaWriter<PhredQuality, QualitySequence, QualityFastaRecord> createNewTmpWriter(
				File tmpFile) throws IOException {
			return new QualityFastaWriterBuilder(tmpFile)
								.build();
		}

		@Override
		protected QualityFastaRecord createFastaRecord(String id,
				QualitySequence sequence, String optionalComment) {

			return new QualityFastaRecordBuilder(id, sequence)
								.comment(optionalComment)
								.build();
		}
		
	}
}
