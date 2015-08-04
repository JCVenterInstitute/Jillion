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
package org.jcvi.jillion.sam.transform;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jcvi.jillion.assembly.GappedReferenceBuilder;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.sam.SamParser;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.cigar.CigarElement;
import org.jcvi.jillion.sam.cigar.CigarOperation;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;

final class SamGappedReferenceBuilderVisitor implements SamVisitor{

	Map<String, GappedReferenceBuilder> builders = new LinkedHashMap<String, GappedReferenceBuilder>();
	
	
	public static NucleotideSequenceDataStore createGappedReferencesFrom(SamParser parser, NucleotideFastaDataStore ungappedReferenceDataStore) throws IOException{
		SamGappedReferenceBuilderVisitor visitor;
		try {
			visitor = new SamGappedReferenceBuilderVisitor(ungappedReferenceDataStore);
		} catch (DataStoreException e) {
			throw new IOException("error parsing reference datastore", e);
		}
		
		parser.accept(visitor);
		return visitor.buildGappedReferences();
	}
	
	private SamGappedReferenceBuilderVisitor(NucleotideFastaDataStore ungappedReferenceDataStore) throws DataStoreException {
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

	@Override
	public void visitHeader(SamVisitorCallback callback, SamHeader header) {
		//remove anything not mentioned?
		Set<String> namesUsed = new HashSet<String>();
		for(ReferenceSequence ref : header.getReferenceSequences()){
			namesUsed.add(ref.getName());
		}
		Iterator<Entry<String, GappedReferenceBuilder>> entryIter = builders.entrySet().iterator();
		while(entryIter.hasNext()){
			Entry<String, GappedReferenceBuilder> entry = entryIter.next();
			if(!namesUsed.contains(entry.getKey())){
				entryIter.remove();
			}
		}
	}

	@Override
	public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
			VirtualFileOffset end) {
		visitRecord(callback, record);
		
	}

	@Override
	public void visitRecord(SamVisitorCallback callback, SamRecord record) {
		if(record.isPrimary() && record.mapped()){
			
			String refName = record.getReferenceName();
			GappedReferenceBuilder refBuilder = builders.get(refName);
			
			int offset = record.getStartPosition() - 1;
			int currentOffset = offset;
			Cigar cigar = record.getCigar();
			Iterator<CigarElement> iter =cigar.getElementIterator();
			while(iter.hasNext()){
				CigarElement element = iter.next();
				
				CigarOperation op = element.getOp();
				
				if(op == CigarOperation.HARD_CLIP || op == CigarOperation.SOFT_CLIP || op == CigarOperation.PADDING){
					//ignore gaps and clipping
				}else if(op == CigarOperation.INSERTION){				
						refBuilder.addReadInsertion(currentOffset, element.getLength());
				}else{
					currentOffset+=element.getLength();
				}
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
		return DataStoreUtil.adapt(NucleotideSequenceDataStore.class, map);
	}
}
