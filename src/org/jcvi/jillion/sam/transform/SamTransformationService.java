package org.jcvi.jillion.sam.transform;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.jcvi.jillion.assembly.AssemblyTransformer;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamRecordFlags;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;

public class SamTransformationService {

	private final File samFile;
	private final NucleotideFastaDataStore referenceDataStore;

	public SamTransformationService(File samFile, File referenceFasta) throws IOException {
		this.samFile = samFile;
		referenceDataStore = new NucleotideFastaFileDataStoreBuilder(referenceFasta)
									.build();
	}
	
	public void transform(final AssemblyTransformer transformer){
		
	}
	
	
	private static final class SamTransformerVisitor implements SamVisitor{

		private final AssemblyTransformer transformer;
		private SamHeader header;
		private final NucleotideFastaDataStore referenceDataStore;
		
		public SamTransformerVisitor(NucleotideFastaDataStore referenceDataStore, AssemblyTransformer transformer) {
			this.referenceDataStore = referenceDataStore;
			this.transformer = transformer;
		}

		@Override
		public void visitHeader(SamHeader header) {
			this.header = header;
			
			for(ReferenceSequence refSeq : header.getReferenceSequences()){
				String id = refSeq.getName();
				try {
					NucleotideFastaRecord fasta = referenceDataStore.get(id);
					if(fasta ==null){
						throw new IllegalStateException("error could not find reference sequence in fasta file with id " + id);
					}
					//TODO need to gap reference similar to cas2consed
					transformer.referenceOrConsensus(id, fasta.getSequence());
				} catch (DataStoreException e) {
					throw new IllegalStateException("error getting reference sequence from fasta file", e);
				}
				
			}
		}
			
		

		@Override
		public void visitRecord(SamRecord record) {
			if(record.isPrimary()){
				
				if(record.getFlags().contains(SamRecordFlags.UNMAPPED)){
					transformer.notAligned(record.getQueryName(), record.getSequence(), record.getQualities(), null, null);
				}else{
					String refName = record.getReferenceName();
					Direction dir = record.getFlags().contains(SamRecordFlags.REVERSE_COMPLEMENTED) ? Direction.REVERSE : Direction.FORWARD;
					Cigar cigar = record.getCigar();
					int rawLength = cigar.getRawUnPaddedReadLength();
					Range validRange = cigar.getValidRange();
					transformer.aligned(record.getQueryName(), record.getSequence(), record.getQualities(), null, null, 
							refName, 
							record.getStartOffset()-1, 
							dir, 
							cigar.toGappedTrimmedSequence(record.getSequence()), 
							
							new ReadInfo(validRange, rawLength));
					
				}
			}
				
		}

		@Override
		public void visitEnd() {
			transformer.endAssembly();
			
		}
			
		}

		
	
	
}
