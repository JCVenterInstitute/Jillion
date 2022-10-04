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
/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.internal.fasta.nt.AdaptedNucleotideFastaDataStore;
/**
 * {@code DefaultNucleotideFastaFileDataStore} is the default implementation
 * of {@link NucleotideSequenceFastaDataStore} which stores
 * all fasta records in memory.  This is only recommended for small fasta
 * files that won't take up too much memory.
 * @author dkatzel
 *
 *
 */
final class DefaultNucleotideFastaFileDataStore{
	
	private DefaultNucleotideFastaFileDataStore(){
		//can not instantiate.
	}

	private static NucleotideFastaDataStoreBuilderVisitorImpl2 createBuilder(Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter, Nucleotide.InvalidCharacterHandler invalidCharacterHandler){
		return new NucleotideFastaDataStoreBuilderVisitorImpl2(filter, recordFilter, invalidCharacterHandler);
	}
	
	public static NucleotideFastaFileDataStore create(File fastaFile) throws IOException{
		return create(fastaFile,DataStoreFilters.alwaysAccept(), null, null);
	}
	public static NucleotideFastaFileDataStore create(File fastaFile, Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter,  Nucleotide.InvalidCharacterHandler invalidCharacterHandler) throws IOException{
		
		FastaParser parser = FastaFileParser.create(fastaFile);
		
		return create(parser, filter, recordFilter, invalidCharacterHandler);
	}

	public static NucleotideFastaFileDataStore create(FastaParser parser,Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter,  Nucleotide.InvalidCharacterHandler invalidCharacterHandler) throws IOException {
		NucleotideFastaDataStoreBuilderVisitorImpl2 builder = createBuilder(filter, recordFilter, invalidCharacterHandler);
		if(parser instanceof FastaFileParser){
		    ((FastaFileParser)parser).getFile().ifPresent(file -> builder.setSourceFile(file));
		}
		parser.parse(builder);
		return builder.build();
	}
	
	public static NucleotideFastaFileDataStore create(InputStream in) throws IOException{
		return create(in,DataStoreFilters.alwaysAccept(), null, null);
	}
	public static NucleotideFastaFileDataStore create(InputStream in, DataStoreFilter filter, Predicate<NucleotideFastaRecord> recordFilter,  Nucleotide.InvalidCharacterHandler invalidCharacterHandler) throws IOException{
		try{
			NucleotideFastaDataStoreBuilderVisitorImpl2 builder = createBuilder(filter, recordFilter, invalidCharacterHandler);
			FastaFileParser.create(in).parse(builder);
			return builder.build();
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
    

    
    private static final class NucleotideFastaDataStoreBuilderVisitorImpl2 implements FastaVisitor, Builder<NucleotideFastaFileDataStore>{

		private final Map<String, NucleotideFastaRecord> fastaRecords = new LinkedHashMap<String, NucleotideFastaRecord>();
		
		private final Predicate<String> filter;		
		private final ReusableNucleotideFastaRecordVisitor currentVisitor;
		
		private File fastaFile =null;
		public NucleotideFastaDataStoreBuilderVisitorImpl2(Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter, Nucleotide.InvalidCharacterHandler invalidCharacterHandler){
			this.filter = filter;
			this.currentVisitor = new ReusableNucleotideFastaRecordVisitor(recordFilter, invalidCharacterHandler);
		}
		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				final String id, String optionalComment) {
			if(!filter.test(id)){
				return null;
			}
			currentVisitor.initialize(id, optionalComment);
			return currentVisitor;
			
		}
		
		public void setSourceFile(File fastaFile){
		    this.fastaFile = fastaFile;
		}

		@Override
		public void visitEnd() {
			//no-op			
		}
		@Override
		public void halted() {
			//no-op			
		}
		@Override
		public NucleotideFastaFileDataStore build() {
			return new AdaptedNucleotideFastaDataStore(fastaRecords, fastaFile);
		}
		/**
		 * {@code ReusableNucleotideFastaRecordVisitor}
		 * is a {@link FastaRecordVisitor} that can be "reset"
		 * and used multiple times.  This should cut down
		 * on object creation and garbage collection
		 * since we expect there could be hundreds of thousands
		 * or millions of records to visit.
		 * 
		 * Before each new record to visit, call {@link #initialize(String, String)}.
		 * @author dkatzel
		 *
		 */
		 private final class ReusableNucleotideFastaRecordVisitor implements FastaRecordVisitor{
			private String currentId;
			private String currentComment;
			private NucleotideSequenceBuilder builder =new NucleotideSequenceBuilder();
			
			private final Predicate<NucleotideFastaRecord> recordFilter;
			/**
			 * Default constructor needs to have it's data
			 * initialized.
			 */
			public ReusableNucleotideFastaRecordVisitor(Predicate<NucleotideFastaRecord> recordFilter, Nucleotide.InvalidCharacterHandler invalidCharacterHandler){
				this.recordFilter = recordFilter;
				builder.setInvalidCharacterHandler(invalidCharacterHandler);
			}
			/**
			 * Prepare this visitor to visit a new record.
			 * @param id the id of the record to be visited.
			 * @param optionalComment the optional comment of the record
			 * to be visited.
			 */
			public void initialize(String id, String optionalComment){
				this.currentId = id;
				this.currentComment = optionalComment;
				builder.clear(); 
			}
			@Override
			public void visitBodyLine(String line) {
				builder.append(line);
				
			}

			@Override
			public void visitEnd() {
				NucleotideFastaRecord record = new NucleotideFastaRecordBuilder(currentId,builder.build())
														.comment(currentComment)
														.build();
				
				if(recordFilter==null || recordFilter.test(record)){
				    fastaRecords.put(currentId, record);
				}
				
			}
			@Override
			public void halted() {
				//no-op				
			}
			
		    	
	    }
	}
    
   
}
