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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import org.jcvi.jillion.assembly.GappedReferenceBuilder;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
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
public final class SamGappedReferenceBuilderVisitor implements SamVisitor{

	private final Map<String, GappedReferenceBuilder> builders = new LinkedHashMap<String, GappedReferenceBuilder>();
	
	public static NucleotideSequenceDataStore createGappedReferencesFrom(SamParser parser, 
			NucleotideFastaDataStore ungappedReferenceDataStore) throws IOException{
		return createGappedReferencesFrom(parser,ungappedReferenceDataStore, null);
	}
	public static NucleotideSequenceDataStore createGappedReferencesFrom(SamParser parser, 
			NucleotideFastaDataStore ungappedReferenceDataStore,
			SamRecordFilter filter) throws IOException{
		SamGappedReferenceBuilderVisitor visitor;
		try {
			visitor = new SamGappedReferenceBuilderVisitor(ungappedReferenceDataStore);
		} catch (DataStoreException e) {
			throw new IOException("error parsing reference datastore", e);
		}
		
		parser.parse(new SamParserOptions().filter(filter), visitor);
		return visitor.buildGappedReferences();
	}
	public static NucleotideSequenceDataStore createGappedReferencesFrom(SamParser parser, 
			NucleotideFastaDataStore ungappedReferenceDataStore,
			SamRecordFilter filter, Map<String, List<Range>> rangesByRefId) throws IOException{
		SamGappedReferenceBuilderVisitor visitor;
		try {
			visitor = new SamGappedReferenceBuilderVisitor(ungappedReferenceDataStore);
		} catch (DataStoreException e) {
			throw new IOException("error parsing reference datastore", e);
		}
		for(Entry<String, List<Range>> entry : rangesByRefId.entrySet()) {
			parser.parse(new SamParserOptions()
						.reference(entry.getKey(), entry.getValue())
						.filter(filter), visitor);
			
		}
		
		return visitor.buildGappedReferences();
	}
	
	private SamGappedReferenceBuilderVisitor(NucleotideFastaDataStore ungappedReferenceDataStore) throws DataStoreException {
		//only load gapped references if we haven't seen them yet
		if(builders.isEmpty()) {
			StreamingIterator<NucleotideFastaRecord> iter =null;
			try{
				iter = ungappedReferenceDataStore.iterator();
				while(iter.hasNext()){
					NucleotideFastaRecord next = iter.next();
					builders.put(next.getId(), new GappedReferenceBuilder(next.getSequence()));
				}
			}finally{
				IOUtil.closeAndIgnoreErrors(iter);
			}
		}
	}

	@Override
	public void visitHeader(SamVisitorCallback callback, SamHeader header) {
		//remove anything not mentioned?
		Set<String> namesUsed = new HashSet<String>();
		for(SamReferenceSequence ref : header.getReferenceSequences()){
			namesUsed.add(ref.getName());
		}
		Iterator<Entry<String, GappedReferenceBuilder>> entryIter = builders.entrySet().iterator();
		while(entryIter.hasNext()){
			Entry<String, GappedReferenceBuilder> entry = entryIter.next();
			if(!namesUsed.contains(entry.getKey())){
				entryIter.remove();
			}
		}
		if(builders.isEmpty()) {
			//no known references?
			throw new IllegalStateException("reference names don't match input fasta, references names are : " + namesUsed);
		}
	}

	

	@Override
	public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
			VirtualFileOffset end) {
		if(record.isPrimary() && record.mapped()){
			
			String refName = record.getReferenceName();
			GappedReferenceBuilder refBuilder = builders.get(refName);
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

	private NucleotideSequenceDataStore buildGappedReferences(){
		Map<String, NucleotideSequence> map = new LinkedHashMap<String, NucleotideSequence>();
		
		for(Entry<String, GappedReferenceBuilder> entry : builders.entrySet()){
			map.put(entry.getKey(), entry.getValue().build());
		}
		return DataStore.of(map, NucleotideSequenceDataStore.class);
	}
}
