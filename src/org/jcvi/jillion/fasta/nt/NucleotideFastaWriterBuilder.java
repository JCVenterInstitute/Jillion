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
package org.jcvi.jillion.fasta.nt;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.JoinedStringBuilder;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaWriter;
import org.jcvi.jillion.internal.fasta.AbstractResidueFastaWriter;
import org.jcvi.jillion.internal.fasta.InMemorySortedFastaWriter;
import org.jcvi.jillion.internal.fasta.TmpDirSortedFastaWriter;
/**
 * {@code NucleotideFastaWriterBuilder} is a Builder
 * class that will create a new instance of 
 * {@link NucleotideFastaWriter}
 * that will write fasta encoded data
 * to the given File or {@link OutputStream}.
 * @author dkatzel
 *
 */
public final class NucleotideFastaWriterBuilder extends AbstractResidueFastaWriter.Builder<Nucleotide, NucleotideSequence, NucleotideFastaRecord,NucleotideFastaWriter> {
		private boolean nonRedundant;
		private Integer expectedCapacity;
		
		
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
		public NucleotideFastaWriterBuilder(File outputFile) throws IOException {
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
		public NucleotideFastaWriterBuilder(OutputStream out) {
			super(out);
		}
		/**
		 * Write out the Fasta as a
		 * non-redundant database (like BLAST's nr).
		 * Identical sequences will have their deflines merged
		 * into a single defline where each record is separated by control-A characters
		 * invisible to most programs.
		 * <br/>
		 * In the example below both entries gi|1469284 and gi|1477453 
		 * have the same sequence, in every respect:
		 * <pre>
		 * >gi|3023276|sp|Q57293|AFUC_ACTPL   Ferric transport ATP-binding protein afuC
		 * ^Agi|1469284|gb|AAB05030.1|   afuC gene product ^Agi|1477453|gb|AAB17216.1|
		 * afuC [Actinobacillus pleuropneumoniae]
		 * ATGAACAACGATTTTCTGGTGCTGAAAAACATTACCAAAAGCTTTGGCAAAGCGACCGTG
		 * ATTGATAACCTGGATCTGGTGATTAAACGCGGCACCATGGTGACCCTGCTGGGCCCGAGC
		 * GGCTGCGGCAAAACCACCGTGCTGCGCCTGGTGGCGGGCCTGGAAAACCCGACCAGCGGC
		 * CAGATTTTTATTGATGGCGAAGATGTGACCAAAAGCAGCATTCAGAACCGCGATATTTGC
		 * ATTGTGTTTCAGAGCTATGCGCTGTTTCCGCATATGAGCATTGGCGATAACGTGGGCTAT
		 * GGCCTGCGCATGCAGGGCGTGAGCAACGAAGAACGCAAACAGCGCGTGAAAGAAGCGCTG
		 * GAACTGGTGGATCTGGCGGGCTTTGCGGATCGCTTTGTGGATCAGATTAGCGGCGGCCAG
		 * CAGCAGCGCGTGGCGCTGGCGCGCGCGCTGGTGCTGAAACCGAAAGTGCTGATTCTGGAT
		 * GAACCGCTGAGCAACCTGGATGCGAACCTGCGCCGCAGCATGCGCGAAAAAATTCGCGAA
		 * CTGCAGCAGCGCCTGGGCATTACCAGCCTGTATGTGACCCATGATCAGACCGAAGCGTTT
		 * GCGGTGAGCGATGAAGTGATTGTGATGAACAAAGGCACCATTATGCAGAAAGCGCGCCAG
		 * AAAATTTTTATTTATGATCGCATTCTGTATAGCCTGCGCAACTTTATGGGCGAAAGCACC
		 * ATTTGCGATGGCAACCTGAACCAGGGCACCGTGAGCATTGGCGATTATCGCTTTCCGCTG
		 * CATAACGCGGCGGATTTTAGCGTGGCGGATGGCGCGTGCCTGGTGGGCGTGCGCCCGGAA
		 * GCGATTCGCCTGACCGCGACCGGCGAAACCAGCCAGCGCTGCCAGATTAAAAGCGCGGTG
		 * TATATGGGCAACCATTGGGAAATTGTGGCGAACTGGAACGGCAAAGATGTGCTGATTAAC
		 * GCGAACCCGGATCAGTTTGATCCGGATGCGACCAAAGCGTTTATTCATTTTACCGAACAG
		 * GGCATTTTTCTGCTGAACAAAGAA
		 * </pre>
		 * 
		 * @return this
		 * 
		 * @since 5.0
		 */
		public NucleotideFastaWriterBuilder makeNonRedundant(){
			this.nonRedundant = true;
			this.expectedCapacity = null;
			return this;
		}
		/**
		 * Write out the Fasta as a
		 * non-redundant database (like BLAST's nr).
		 * Identical sequences will have their deflines merged
		 * into a single defline where each record is separated by control-A characters
		 * invisible to most programs.
		 * <br/>
		 * In the example below both entries gi|1469284 and gi|1477453 
		 * have the same sequence, in every respect:
		 * <pre>
		 * >gi|3023276|sp|Q57293|AFUC_ACTPL   Ferric transport ATP-binding protein afuC
		 * ^Agi|1469284|gb|AAB05030.1|   afuC gene product ^Agi|1477453|gb|AAB17216.1|
		 * afuC [Actinobacillus pleuropneumoniae]
		 * ATGAACAACGATTTTCTGGTGCTGAAAAACATTACCAAAAGCTTTGGCAAAGCGACCGTG
		 * ATTGATAACCTGGATCTGGTGATTAAACGCGGCACCATGGTGACCCTGCTGGGCCCGAGC
		 * GGCTGCGGCAAAACCACCGTGCTGCGCCTGGTGGCGGGCCTGGAAAACCCGACCAGCGGC
		 * CAGATTTTTATTGATGGCGAAGATGTGACCAAAAGCAGCATTCAGAACCGCGATATTTGC
		 * ATTGTGTTTCAGAGCTATGCGCTGTTTCCGCATATGAGCATTGGCGATAACGTGGGCTAT
		 * GGCCTGCGCATGCAGGGCGTGAGCAACGAAGAACGCAAACAGCGCGTGAAAGAAGCGCTG
		 * GAACTGGTGGATCTGGCGGGCTTTGCGGATCGCTTTGTGGATCAGATTAGCGGCGGCCAG
		 * CAGCAGCGCGTGGCGCTGGCGCGCGCGCTGGTGCTGAAACCGAAAGTGCTGATTCTGGAT
		 * GAACCGCTGAGCAACCTGGATGCGAACCTGCGCCGCAGCATGCGCGAAAAAATTCGCGAA
		 * CTGCAGCAGCGCCTGGGCATTACCAGCCTGTATGTGACCCATGATCAGACCGAAGCGTTT
		 * GCGGTGAGCGATGAAGTGATTGTGATGAACAAAGGCACCATTATGCAGAAAGCGCGCCAG
		 * AAAATTTTTATTTATGATCGCATTCTGTATAGCCTGCGCAACTTTATGGGCGAAAGCACC
		 * ATTTGCGATGGCAACCTGAACCAGGGCACCGTGAGCATTGGCGATTATCGCTTTCCGCTG
		 * CATAACGCGGCGGATTTTAGCGTGGCGGATGGCGCGTGCCTGGTGGGCGTGCGCCCGGAA
		 * GCGATTCGCCTGACCGCGACCGGCGAAACCAGCCAGCGCTGCCAGATTAAAAGCGCGGTG
		 * TATATGGGCAACCATTGGGAAATTGTGGCGAACTGGAACGGCAAAGATGTGCTGATTAAC
		 * GCGAACCCGGATCAGTTTGATCCGGATGCGACCAAAGCGTTTATTCATTTTACCGAACAG
		 * GGCATTTTTCTGCTGAACAAAGAA
		 * </pre>
		 * If this method is called multiple times, then only the last
		 * method call will be respected. (Last call wins)
		 * 
		 * @param expectedSize the expected number of non-redundant records.
		 * This is only uses as a performance optimization to initialize
		 * internal datastructures.  Must be >0.
		 * 
		 * @return this
		 * 
		 * @throws IllegalArgumentException if expectedSize < 1
		 * 
		 * @since 5.0
		 */
		public NucleotideFastaWriterBuilder makeNonRedundant(int expectedSize){

			if(expectedSize <1){
				throw new IllegalArgumentException("expected size must be >= 1");					
			}
			this.expectedCapacity = expectedSize;			
			this.nonRedundant = true;
			return this;
		}

		@Override
		protected NucleotideFastaWriter create(
				OutputStream out, int numberOfResiduesPerLine, Charset charSet, String eol) {
			
			if(nonRedundant){
				if(expectedCapacity ==null){
					return new NonRedundantNucleotideSequenceFastaWriter(out, numberOfResiduesPerLine, charSet, eol);
				}
				return new NonRedundantNucleotideSequenceFastaWriter(out, numberOfResiduesPerLine, charSet, eol, expectedCapacity);
			}
			return new NucleotideSequenceFastaRecordWriterImpl(out, numberOfResiduesPerLine, charSet,eol);
		}
		
		
		
		
		@Override
		protected NucleotideFastaWriter createTmpDirSortedWriterWriter(
				FastaWriter<Nucleotide, NucleotideSequence, NucleotideFastaRecord> delegate,
				Comparator<NucleotideFastaRecord> comparator, int cacheSize,
				File tmpDir) {
			return new TmpDirSortedNucleotideFastaWriter(delegate, comparator, cacheSize, tmpDir);
		}
		@Override
		protected NucleotideFastaWriter createInMemorySortedWriterWriter(
				FastaWriter<Nucleotide, NucleotideSequence, NucleotideFastaRecord> delegate,
				Comparator<NucleotideFastaRecord> comparator) {
			return new InMemorySortedNucleotideFastaWriter(delegate, comparator);
		}




		private static final class NucleotideSequenceFastaRecordWriterImpl extends AbstractResidueFastaWriter<Nucleotide, NucleotideSequence, NucleotideFastaRecord> implements NucleotideFastaWriter{

			private NucleotideSequenceFastaRecordWriterImpl(OutputStream out,
					int numberOfResiduesPerLine, Charset charSet, String eol) {
				super(out, numberOfResiduesPerLine, charSet,eol);
			}
		}
		
		private static interface NonRedundantEntry{
			String getId();
			String getComment();
			
			default String getNonRedundantId(){
				String id = getId();
				String comment = getComment();
				StringBuilder builder = new StringBuilder(id.length() + (comment ==null?0 : comment.length()+1));
				builder.append(id);
				if(comment !=null){
					builder.append(' ').append(comment);
				}
				return builder.toString();
			}
			
		}
		
		private static final class CommentedNonRedundantEntry implements NonRedundantEntry{
			private final String id, comment;			
			
			public CommentedNonRedundantEntry(String id, String comment) {
				this.id = id;
				this.comment = comment;
			}

			@Override
			public String getId() {
				return id;
			}

			@Override
			public String getComment() {
				return comment;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result +  id.hashCode();
				result = prime * result	+  comment.hashCode();
				
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj) {
					return true;
				}
				if (obj == null) {
					return false;
				}
				if (!(obj instanceof CommentedNonRedundantEntry)) {
					return false;
				}
				CommentedNonRedundantEntry other = (CommentedNonRedundantEntry) obj;
				if (!getComment().equals(other.getComment())) {
					return false;
				}
				if (!getId().equals(other.getId())) {
					return false;
				}
				return true;
			}
			
			
			
		}
		
		private static final class NoCommentedNonRedundantEntry implements NonRedundantEntry{
			private final String id;			
			
			public NoCommentedNonRedundantEntry(String id) {
				this.id = id;
			}

			@Override
			public String getId() {
				return id;
			}

			@Override
			public String getComment() {
				return null;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result +  id.hashCode();
				result = prime * result	; //null comment 
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj) {
					return true;
				}
				if (obj == null) {
					return false;
				}
				if (!(obj instanceof CommentedNonRedundantEntry)) {
					return false;
				}
				CommentedNonRedundantEntry other = (CommentedNonRedundantEntry) obj;
				if (other.getComment() !=null) {
					return false;
				}
				if (!getId().equals(other.getId())) {
					return false;
				}
				return true;
			}
			
			
			
		}
		
		private static final class NonRedundantNucleotideSequenceFastaWriter implements NucleotideFastaWriter{

			private static final char CONTROL_A = 0x1;
			private final Map<NucleotideSequence, Set<NonRedundantEntry>> nonRedundantMap;
			
			private final NucleotideSequenceFastaRecordWriterImpl delegateWriter;
			
			public NonRedundantNucleotideSequenceFastaWriter(OutputStream out,
					int numberOfResiduesPerLine, Charset charSet, String eol){
				this(out, numberOfResiduesPerLine, charSet, eol, 1000);
			}
			public NonRedundantNucleotideSequenceFastaWriter(OutputStream out,
					int numberOfResiduesPerLine, Charset charSet, String eol, int expectedSize){
				nonRedundantMap = new LinkedHashMap<>(MapUtil.computeMinHashMapSizeWithoutRehashing(expectedSize));
				
				delegateWriter = new NucleotideSequenceFastaRecordWriterImpl(out, numberOfResiduesPerLine, charSet, eol);
			}
			
			@Override
			public void close() throws IOException {
				try{
					for(Entry<NucleotideSequence, Set<NonRedundantEntry>> entry : nonRedundantMap.entrySet()){
						NucleotideSequence sequence = entry.getKey();
						
						String ids =JoinedStringBuilder.create(entry.getValue())
											.glue(CONTROL_A)
											.transform(NonRedundantEntry::getNonRedundantId)
											.build();
						
						delegateWriter.write(ids, sequence);
					}
				}finally{
					delegateWriter.close();
				}
				
			}

			@Override
			public void write(NucleotideFastaRecord record) throws IOException {
				write(record.getId(), record.getSequence(), record.getComment());
				
			}

			@Override
			public void write(String id, NucleotideSequence sequence)
					throws IOException {
				write(id, sequence, null);
				
			}

			@Override
			public void write(String id, NucleotideSequence sequence,
					String optionalComment) throws IOException {
				NonRedundantEntry entry;
				if(optionalComment ==null){
					entry = new NoCommentedNonRedundantEntry(id);
				}else{
					entry = new CommentedNonRedundantEntry(id, optionalComment);
				}
				
				nonRedundantMap.computeIfAbsent(sequence, k-> new LinkedHashSet<>())
									.add(entry);
				
				
			}
			
			
		}
		
		
		private static final class InMemorySortedNucleotideFastaWriter extends InMemorySortedFastaWriter<Nucleotide, NucleotideSequence, NucleotideFastaRecord> implements NucleotideFastaWriter{

			public InMemorySortedNucleotideFastaWriter(FastaWriter<Nucleotide, NucleotideSequence, NucleotideFastaRecord> writer,
					Comparator<NucleotideFastaRecord> comparator) {
				super(writer, comparator);
			}

			@Override
			protected NucleotideFastaRecord createRecord(String id, NucleotideSequence sequence, String optionalComment) {
				return new NucleotideFastaRecordBuilder(id, sequence)
								.comment(optionalComment)
								.build();
			}
			
		}
		
		private static final class TmpDirSortedNucleotideFastaWriter extends TmpDirSortedFastaWriter<Nucleotide, NucleotideSequence, NucleotideFastaRecord> implements NucleotideFastaWriter{

			

			public TmpDirSortedNucleotideFastaWriter(
					FastaWriter<Nucleotide, NucleotideSequence, NucleotideFastaRecord> finalWriter,
					Comparator<NucleotideFastaRecord> comparator, int cacheSize,
					File tmpDir) {
				super(finalWriter, comparator, tmpDir, cacheSize);
			}

			@Override
			protected StreamingIterator<NucleotideFastaRecord> createStreamingIteratorFor(
					File tmpFastaFile) throws IOException, DataStoreException {
				return LargeNucleotideSequenceFastaIterator.createNewIteratorFor(tmpFastaFile, DataStoreFilters.alwaysAccept());
			}

			@Override
			protected FastaWriter<Nucleotide, NucleotideSequence, NucleotideFastaRecord> createNewTmpWriter(
					File tmpFile) throws IOException {
				return new NucleotideFastaWriterBuilder(tmpFile)
									.build();
			}

			@Override
			protected NucleotideFastaRecord createFastaRecord(String id,
					NucleotideSequence sequence, String optionalComment) {

				return new NucleotideFastaRecordBuilder(id, sequence)
									.comment(optionalComment)
									.build();
			}
			
		}
}
