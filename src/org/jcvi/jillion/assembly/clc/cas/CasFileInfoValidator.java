package org.jcvi.jillion.assembly.clc.cas;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.sff.SffFileDataStore;
import org.jcvi.jillion.trace.sff.SffFileDataStoreBuilder;
import org.jcvi.jillion.trace.sff.SffFlowgram;

final class CasFileInfoValidator {
	
	private CasFileInfoValidator(){
		//can not instantiate
	}
	
	private static ActualFileInfo getActualFileInfoFor(File file) throws DataStoreException, IOException{
        ReadFileType readType = ReadFileType.getTypeFromFile(file);
           switch(readType){
	            case FASTQ: 
	            	return getFastqInfo(file);
	            case SFF:
	            	return getSffInfo(file);
	            case FASTA:
                       return getFastaInfo(file);
	            default: 
	            	throw new IllegalArgumentException("unsupported type "+ file.getName());
	            }
        
   }
	
	/**
	 * Validate that the information contained in the
	 * given {@link CasFileInfo} is still correct
	 * for the referenced file(s) and throw
	 * an exception if it's not.
	 * @param dirOfCas the directory that the cas file is located.
	 * @param expected the {@link CasFileInfo} of the file(s) to check
	 * and verify; can not be null.
	 * @throws DataStoreException if there is a problem parsing the files.
	 * @throws IOException if the files don't exist.
	 * @throws IllegalStateException if there is a validation error.
	 */
	public static void validateFileInfo(File dirOfCas, CasFileInfo expected) throws DataStoreException, IOException{
		ActualFileInfo actualInfo = new ActualFileInfo();
		for(String fileName : expected.getFileNames()){
			File file = CasUtil.getFileFor(dirOfCas, fileName);
			actualInfo.add(getActualFileInfoFor(file));
		}
		actualInfo.assertMatches(expected);
	}

	private static ActualFileInfo getFastaInfo(File fastaFile) throws IOException, DataStoreException {
		try(NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(fastaFile)
															.hint(DataStoreProviderHint.ITERATION_ONLY)
															.build();
				
			StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();
			){
			ActualFileInfo actual = new ActualFileInfo();
			while(iter.hasNext()){
				actual.add(iter.next().getSequence());
			}
			return actual;
		}

	}
	
	private static ActualFileInfo getFastqInfo(File fastqFile) throws IOException, DataStoreException {
		try(FastqDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
															.hint(DataStoreProviderHint.ITERATION_ONLY)
															.build();
				
			StreamingIterator<FastqRecord> iter = datastore.iterator();
			){
			ActualFileInfo actual = new ActualFileInfo();
			while(iter.hasNext()){
				actual.add(iter.next().getNucleotideSequence());
			}
			return actual;
		}

	}
	
	private static ActualFileInfo getSffInfo(File sffFile) throws IOException, DataStoreException {
		try(SffFileDataStore datastore = new SffFileDataStoreBuilder(sffFile)
															.hint(DataStoreProviderHint.ITERATION_ONLY)
															.build();
				
			StreamingIterator<SffFlowgram> iter = datastore.iterator();
			){
			ActualFileInfo actual = new ActualFileInfo();
			while(iter.hasNext()){
				actual.add(iter.next().getNucleotideSequence());
			}
			return actual;
		}

	}

	
	
	
	private static final class ActualFileInfo{
		long numberOfRecords = 0;
		BigInteger numberOfResidues = BigInteger.valueOf(0);
		
		public void add(NucleotideSequence seq){
			numberOfRecords ++;
			numberOfResidues = numberOfResidues.add(BigInteger.valueOf(seq.getLength()));
		}
		
		public void add(ActualFileInfo other){
			this.numberOfRecords += other.numberOfRecords;
			this.numberOfResidues = this.numberOfResidues.add(other.numberOfResidues);
		}
		
		public void assertMatches(CasFileInfo expected){
			if(numberOfRecords !=expected.getNumberOfSequences()){
	    		throw new IllegalStateException(
	    				"file(s) "+expected.getFileNames() +" have wrong number of sequences" +
	    						numberOfRecords + " instead of " + expected.getNumberOfSequences() + " usually this is caused by the input files getting updated after the cas was created");
	    	}
			
			if(!numberOfResidues.equals(expected.getNumberOfResidues())){
	    		throw new IllegalStateException(
	    				"file(s) "+expected.getFileNames() +" have wrong number of residues" +
	    						numberOfResidues + " instead of " + expected.getNumberOfResidues() + " usually this is caused by the input files getting updated after the cas was created");
	    	}
		}
	}
}
