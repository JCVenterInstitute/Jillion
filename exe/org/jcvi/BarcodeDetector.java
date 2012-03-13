package org.jcvi;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jcvi.common.core.align.pairwise.DefaultNucleotideScoringMatrix;
import org.jcvi.common.core.align.pairwise.NucleotideScoringMatrix;
import org.jcvi.common.core.align.pairwise.NucleotideSmithWatermanAligner;
import org.jcvi.common.core.align.pairwise.PairwiseSequenceAlignment;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideSequenceFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fastq.FastQQualityCodec;
import org.jcvi.common.core.seq.fastx.fastq.FastQRecord;
import org.jcvi.common.core.seq.fastx.fastq.LargeFastQFileDataStore;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.util.iter.CloseableIterator;

public class BarcodeDetector {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws DataStoreException 
	 */
	public static void main(String[] args) throws FileNotFoundException, DataStoreException {
		File barcodeFasta = new File("/home/dkatzel/iotorrent_data/barcode_metadata_from_GLK.txt.pat");
		File fastq = new File("/home/dkatzel/iotorrent_data/R_2012_01_13_11_34_59_user_1IO-5_Auto_1IO-5_6.fastq");

		final NucleotideScoringMatrix matrix;
		DefaultNucleotideScoringMatrix.Builder builder = new DefaultNucleotideScoringMatrix.Builder(-1F);
		
		for(Nucleotide n : Nucleotide.values()){
			builder.set(n, n, 2);
		}
		matrix = builder.build();
		
		
		NucleotideSequenceFastaDataStore barcodes = DefaultNucleotideSequenceFastaFileDataStore.create(barcodeFasta);
		CloseableIterator<FastQRecord> fastqIterator = new LargeFastQFileDataStore(fastq, FastQQualityCodec.SANGER)
														.iterator();
		Set<String> unmatched = new HashSet<String>();
		Map<String, Set<String>> mappedReads = new TreeMap<String, Set<String>>();
		
		Map<String, Set<String>> multiMatchReads = new TreeMap<String, Set<String>>();
		int sequencesSeen =0;
		try{
		while(fastqIterator.hasNext()){
			sequencesSeen++;
			FastQRecord fastqRecord = fastqIterator.next();
			List<String> matchedBarcodes = new ArrayList<String>();
			List<PairwiseSequenceAlignment<Nucleotide, NucleotideSequence>> validAlignments=new ArrayList<PairwiseSequenceAlignment<Nucleotide, NucleotideSequence>>();
			CloseableIterator<NucleotideSequenceFastaRecord> barcodeIterator = barcodes.iterator();
			try{
				while(barcodeIterator.hasNext()){
					NucleotideSequenceFastaRecord barcode = barcodeIterator.next();
					NucleotideSequence barcodeSequence = barcode.getSequence();
					PairwiseSequenceAlignment<Nucleotide, NucleotideSequence> alignment =NucleotideSmithWatermanAligner.align(barcodeSequence, 
																fastqRecord.getSequence(), matrix, -2, 0);
					
					if(alignment.getAlignmentLength() == barcodeSequence.getLength() 
							&& alignment.getNumberOfMismatches() <=2 && alignment.getPercentIdentity() > .9F){
						validAlignments.add(alignment);
						matchedBarcodes.add(barcode.getId());
					}
				}
			}finally{
				IOUtil.closeAndIgnoreErrors(barcodeIterator);
			}
			if(matchedBarcodes.isEmpty()){
				unmatched.add(fastqRecord.getId());
			}else if(matchedBarcodes.size()==1){
				String barcodeName = matchedBarcodes.get(0);
				if(!mappedReads.containsKey(barcodeName)){
					mappedReads.put(barcodeName, new TreeSet<String>());
				}
				mappedReads.get(barcodeName).add(fastqRecord.getId());
			}else{
				multiMatchReads.put(fastqRecord.getId(), new TreeSet<String>(matchedBarcodes));
				System.out.println("multi match for " + fastqRecord.getId());
				for(int i=0; i<matchedBarcodes.size(); i++){
					String barcodeName = matchedBarcodes.get(i);
					PairwiseSequenceAlignment<Nucleotide, NucleotideSequence> alignment = validAlignments.get(i);
					System.out.printf("barcode : %s : %s%n", barcodeName, alignment);
					
				}
			}
			
			
		}
		}finally{
			IOUtil.closeAndIgnoreErrors(fastqIterator);
		}
		
		System.out.println("total number of sequences checked : "+ sequencesSeen);
		System.out.println("unmatched : " + unmatched.size());
		System.out.println("singly matched : ");
		for(Entry<String, Set<String>> barcodeEntry : mappedReads.entrySet()){
			System.out.printf("%s : %d%n", barcodeEntry.getKey(), barcodeEntry.getValue().size());
		}
		System.out.println("multi matched : " + multiMatchReads.size());
		for(Entry<String, Set<String>> multiMatched : multiMatchReads.entrySet()){
			System.out.printf("%s : %s%n", multiMatched.getKey(), multiMatched.getValue());
		}
		
	}
	
	

}
