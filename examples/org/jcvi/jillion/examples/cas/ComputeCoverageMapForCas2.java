package org.jcvi.jillion.examples.cas;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jcvi.jillion.assembly.clc.cas.AbstractCasFileVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasAlignment;
import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegion;
import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegionType;
import org.jcvi.jillion.assembly.clc.cas.CasFileInfo;
import org.jcvi.jillion.assembly.clc.cas.CasFileParser;
import org.jcvi.jillion.assembly.clc.cas.CasMatch;
import org.jcvi.jillion.assembly.clc.cas.CasMatchVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasParser;
import org.jcvi.jillion.assembly.clc.cas.CasUtil;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.joda.time.Period;

public class ComputeCoverageMapForCas2 {

	public static void main(String[] args) throws IOException {
		File casFile = new File("/path/to/cas");
		
		long start = System.currentTimeMillis();
		CasParser parser = CasFileParser.create(casFile);
		
		CoverageMapVisitor visitor = new CoverageMapVisitor(parser);
		parser.parse(visitor);
		long end = System.currentTimeMillis();
		
		System.out.println(new Period(end- start));
	}
	
	
	private static class CoverageMapVisitor extends AbstractCasFileVisitor{

		private int[] contigLengths;
		private String[] contigIds;
		
		private long[] alignedBasesPerContig;
		
		
		private CasMatchVisitor alignmentVisitor =  new CasMatchVisitor() {
			
			@Override
			public void visitMatch(CasMatch match) {
				if(!match.matchReported()){
					//only care about reads that aligned
					return;
				}
				
				CasAlignment alignment = match.getChosenAlignment();
				
				int refIndex = (int) alignment.getReferenceIndex();
				
				long alignmentLength = computeAlignmentLength(alignment);
				
				alignedBasesPerContig[refIndex] +=alignmentLength;
			}
			
			private long computeAlignmentLength(CasAlignment alignment) {
				//cas files may have leading and trailing INSERTs
				//which CLC uses to show the unmapped edges
				//we want to ignore those
				
				
				List<CasAlignmentRegion> allRegions = alignment.getAlignmentRegions();
				
				int endOffset = findEndOffset(allRegions);
				//nothing aligned?, shouldn't happen
				if(endOffset ==-1){
					return 0;
				}
				
				long length=0;
				for(int i =findStartOffset(allRegions); i<= endOffset; i++){
					CasAlignmentRegion region = allRegions.get(i);
					//the following switch statement makes
					//the avg coverage computation match clc's assembly_info
					//although I disagree with the choices
					//shouldn't INSERTION be used instead of deletion ? (or both)
					//the difference in the final answer
					//is usually only a fractional number
					//302.06x vs 302.03x for example (first number is assembly_info vs using INSERTION instead )
					switch(region.getType()){
						
						case DELETION: //fall through
						case MATCH_MISMATCH: length+=region.getLength();
												break;
						default: break;
					}
				}
				
				return length;
			}
			private int findStartOffset(List<CasAlignmentRegion> allRegions) {
				for(int i= 0; i< allRegions.size(); i++){
					if(allRegions.get(i).getType() != CasAlignmentRegionType.INSERT){
						return i;
					}
				}
				return allRegions.size();
			}
			private int findEndOffset(List<CasAlignmentRegion> allRegions) {
				for(int i= allRegions.size() -1; i>=0; i--){
					if(allRegions.get(i).getType() != CasAlignmentRegionType.INSERT){
						return i;
					}
				}
				return -1;
			}

			@Override
			public void visitEnd() {
				//do nothing
				
			}
			
			@Override
			public void halted() {
				//do nothing
			}
		};
		
		private final File workingDir;
		
		public CoverageMapVisitor(CasParser parser){
			workingDir = parser.getWorkingDir();
		}
		
		@Override
		public void visitReferenceFileInfo(CasFileInfo referenceFileInfo) {
			int numContigs = (int) referenceFileInfo.getNumberOfSequences();
			
			contigLengths = new int[numContigs];
			contigIds = new String[numContigs];
			//initializes each cell to 0
			alignedBasesPerContig = new long[numContigs];
			
			//cas files refer to references by a counter, not by their name
			//the order is the order in the input reference file(s)
			int currentRefIndex=0;
			for(String filePath : referenceFileInfo.getFileNames()){
				//could be relative so we need to use 
				//utility method to convert to full path
				try {
					File refFasta = CasUtil.getFileFor(workingDir, filePath);
					//TODO assume fasta file - change as appropriate
					try(NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(refFasta)
																	.hint(DataStoreProviderHint.ITERATION_ONLY)
																	.build();
							
							StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();
					){
						while(iter.hasNext()){
							NucleotideFastaRecord record = iter.next();
							
							contigIds[currentRefIndex] = record.getId();
							contigLengths[currentRefIndex] = (int) record.getSequence().getLength();
							
							//increment ref index each time
							currentRefIndex++;
						}
					}
					
				} catch (IOException | DataStoreException e) {
					throw new IllegalStateException("error parsing reference file(s)", e);
				}
			}
		}

		

		@Override
		public CasMatchVisitor visitMatches(CasVisitorCallback callback) {
			return alignmentVisitor;
		}
		
		@Override
		public void visitEnd() {
			//compute avg coverages
			for(int i=0; i< contigIds.length; i++){
				double avgCoverage =  alignedBasesPerContig[i] / (double)contigLengths[i];
				
				System.out.printf("%40s :\t%.2f%n",contigIds[i] , avgCoverage);
			}
		}
	}

}
