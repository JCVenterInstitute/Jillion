package org.jcvi.jillion.sam.transform;

import java.io.File;
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
import org.jcvi.jillion.sam.SamFileParser;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.cigar.CigarElement;
import org.jcvi.jillion.sam.cigar.CigarOperation;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;

public class SamGappedReferenceBuilderVisitor implements SamVisitor{

	Map<String, GappedReferenceBuilder> builders = new LinkedHashMap<String, GappedReferenceBuilder>();
	
	
	public static NucleotideSequenceDataStore createGappedReferencesFrom(File samFile, NucleotideFastaDataStore ungappedReferenceDataStore) throws IOException{
		SamGappedReferenceBuilderVisitor visitor;
		try {
			visitor = new SamGappedReferenceBuilderVisitor(ungappedReferenceDataStore);
		} catch (DataStoreException e) {
			throw new IOException("error parsing reference datastore", e);
		}
		
		new SamFileParser(samFile).accept(visitor);
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
	public void visitHeader(SamHeader header) {
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
	public void visitRecord(SamRecord record) {
		if(record.isPrimary() && record.mapped()){
			/*
			NucleotideSequence sequence;
			if(record.getDirection() == Direction.REVERSE){
				//reverse complement before alignment?
				sequence = new NucleotideSequenceBuilder(record.getSequence())
									.reverseComplement()
									.build();
			}else{
				sequence = record.getSequence();
			}
			
			
			
			*/
			String refName = record.getReferenceName();
			GappedReferenceBuilder refBuilder = builders.get(refName);
			
			int offset = record.getStartPosition() - 1;
			int currentOffset = offset;
			Cigar cigar = record.getCigar();
			Iterator<CigarElement> iter =cigar.getElementIterator();
			while(iter.hasNext()){
				CigarElement element = iter.next();
				
				CigarOperation op = element.getOp();
				
				if(op == CigarOperation.HARD_CLIP || op == CigarOperation.SOFT_CLIP){
					//ignore gaps and clipping
				}else if(op == CigarOperation.INSERTION || op ==CigarOperation.PADDING){				
						refBuilder.addReadInsertion(currentOffset, element.getLength());
				}else{
					currentOffset+=element.getLength();
				}
			}
			
		}
		
	}

	@Override
	public void visitEnd() {
		// TODO no-op
		
	}

	private NucleotideSequenceDataStore buildGappedReferences(){
		Map<String, NucleotideSequence> map = new LinkedHashMap<String, NucleotideSequence>();
		
		for(Entry<String, GappedReferenceBuilder> entry : builders.entrySet()){
			map.put(entry.getKey(), entry.getValue().build());
		}
		return DataStoreUtil.adapt(NucleotideSequenceDataStore.class, map);
	}
}
