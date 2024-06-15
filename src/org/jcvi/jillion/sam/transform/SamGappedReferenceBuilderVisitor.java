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
package org.jcvi.jillion.sam.transform;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jcvi.jillion.assembly.GappedReferenceBuilder;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.*;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.internal.core.util.Sneak;
import org.jcvi.jillion.sam.SamParser;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamRecordFilter;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.cigar.CigarElement;
import org.jcvi.jillion.sam.cigar.CigarOperation;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamReferenceSequence;
import org.jcvi.jillion.sam.SamParser.SamParserOptions;
public final class SamGappedReferenceBuilderVisitor<S extends INucleotideSequence<S, B>, B extends INucleotideSequenceBuilder<S,B>> implements SamVisitor{

	private final Map<String, GappedReferenceBuilder<S,B>> builders = new LinkedHashMap<>();
	private final DataStore<S> ungappedReferenceDataStore;

	private boolean validateReferences=true;

	public boolean validateReferences() {
		return validateReferences;
	}

	public SamGappedReferenceBuilderVisitor<S,B> validateReferences(boolean validateReferences) {
		this.validateReferences = validateReferences;
		return this;
	}

	public static DataStore<NucleotideSequence> createGappedReferencesFrom(SamParser parser,
																		 NucleotideFastaDataStore ungappedReferenceDataStore) throws IOException{
		return createGappedReferencesFrom(parser,ungappedReferenceDataStore, null);
	}
	public static DataStore<NucleotideSequence> createGappedReferencesFrom(SamParser parser,
																		 NucleotideFastaDataStore ungappedReferenceDataStore,
																		 SamRecordFilter filter) throws IOException{
		return createGappedReferencesFrom(parser, ungappedReferenceDataStore, filter, true);
	}
	public static DataStore<NucleotideSequence> createGappedReferencesFrom(SamParser parser,
			NucleotideFastaDataStore ungappedReferenceDataStore,
			SamRecordFilter filter, boolean validateReferences) throws IOException{
		SamGappedReferenceBuilderVisitor<NucleotideSequence, NucleotideSequenceBuilder> visitor;
		try {
			visitor = new SamGappedReferenceBuilderVisitor<>(ungappedReferenceDataStore.asSequenceDataStore())
								.validateReferences(validateReferences);
		} catch (DataStoreException e) {
			throw new IOException("error parsing reference datastore", e);
		}
		
		parser.parse(SamParserOptions.builder().filter(filter).build(), visitor);
		return visitor.buildGappedReferences();
	}
	public static DataStore<NucleotideSequence> createGappedReferencesFrom(SamParser parser,
																		 NucleotideFastaDataStore ungappedReferenceDataStore,
																		 SamRecordFilter filter, Map<String, List<Range>> rangesByRefId
																		 ) throws IOException {
		return createGappedReferencesFrom(parser, ungappedReferenceDataStore, filter, rangesByRefId, true);
	}
	public static NucleotideSequenceDataStore createGappedReferencesFrom(SamParser parser, 
			NucleotideFastaDataStore ungappedReferenceDataStore,
			SamRecordFilter filter, Map<String, List<Range>> rangesByRefId,
			boolean validateReferences) throws IOException{

		SamGappedReferenceBuilderVisitor<NucleotideSequence, NucleotideSequenceBuilder> visitor;
		try {
			visitor = new SamGappedReferenceBuilderVisitor<>(ungappedReferenceDataStore.asSequenceDataStore())
					.validateReferences(validateReferences);
		} catch (DataStoreException e) {
			throw new IOException("error parsing reference datastore", e);
		}
		for(Entry<String, List<Range>> entry : rangesByRefId.entrySet()) {
			parser.parse(SamParserOptions.builder()
						.reference(entry.getKey(), entry.getValue())
						.filter(filter)
						.build(), visitor);
			
		}
		
		return DataStoreUtil.adapt(NucleotideSequenceDataStore.class, visitor.buildGappedReferences());
	}
	public static <S extends INucleotideSequence<S, B>, B extends INucleotideSequenceBuilder<S,B>>  DataStore<S> createGappedReferencesFrom(SamParser parser,
																		 DataStore<S> ungappedReferenceDataStore,
																		 SamRecordFilter filter, Map<String, List<Range>> rangesByRefId,
																		 boolean validateReferences) throws IOException{

		SamGappedReferenceBuilderVisitor<S, B> visitor;
		try {
			visitor = new SamGappedReferenceBuilderVisitor<>(ungappedReferenceDataStore)
					.validateReferences(validateReferences);
		} catch (DataStoreException e) {
			throw new IOException("error parsing reference datastore", e);
		}
		for(Entry<String, List<Range>> entry : rangesByRefId.entrySet()) {
			parser.parse(SamParserOptions.builder()
					.reference(entry.getKey(), entry.getValue())
					.filter(filter)
					.build(), visitor);

		}

		return visitor.buildGappedReferences();
	}
	private SamGappedReferenceBuilderVisitor(DataStore<S> ungappedReferenceDataStore) throws DataStoreException {

		this.ungappedReferenceDataStore = Objects.requireNonNull(ungappedReferenceDataStore);

	}

	@Override
	public void visitHeader(SamVisitorCallback callback, SamHeader header) {
		if(validateReferences) {
			//remove anything not mentioned?
			Set<String> namesUsed = new HashSet<String>();
			for (SamReferenceSequence ref : header.getReferenceSequences()) {
				namesUsed.add(ref.getName());
			}
			try (StreamingIterator<String> idIterator = ungappedReferenceDataStore.idIterator()) {
				Set<String> idsInReferenceFasta = idIterator.toThrowingStream()
						.collect(Collectors.toSet());
				namesUsed.removeAll(idsInReferenceFasta);
			} catch (DataStoreException e) {
				Sneak.sneakyThrow(e);
			}

			if (!namesUsed.isEmpty()) {
				//no known references?
				throw new IllegalStateException("reference names don't match input fasta, references names are : " + namesUsed);
			}
		}
	}

	

	@Override
	public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
			VirtualFileOffset end) {
		if(record.isPrimary() && record.mapped()){
			
			String refName = record.getReferenceName();
			GappedReferenceBuilder<S,B> refBuilder = builders.computeIfAbsent(refName,
							k-> {
                                try {
                                    return new GappedReferenceBuilder<>(ungappedReferenceDataStore.get(k));
                                } catch (DataStoreException e) {
									//shouldn't happen as we already checked the reference names
									//but to make compiler happy it's here
                                    throw new RuntimeException(e);
                                }
                            });
			try {
			refBuilder.addReadByCigar(record.getStartPosition() - 1, record.getCigar());
			}catch(NullPointerException e) {
				throw e;
			}
			
			
		}
		
	}

	@Override
	public void halted() {
		//no-op
		
	}

	@Override
	public void visitEnd() {
		//no-op
		
	}

	private DataStore<S> buildGappedReferences(){
		Map<String, S> map = new LinkedHashMap<>();
		
		for(Entry<String, GappedReferenceBuilder<S,B>> entry : builders.entrySet()){
			map.put(entry.getKey(), entry.getValue().build());
		}
		return DataStore.of(map);
	}
}
