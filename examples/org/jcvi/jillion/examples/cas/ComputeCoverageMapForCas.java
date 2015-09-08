package org.jcvi.jillion.examples.cas;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.assembly.AbstractAssemblyTransformer;
import org.jcvi.jillion.assembly.AssemblyTransformationService;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.assembly.clc.cas.transform.CasFileTransformationService;
import org.jcvi.jillion.assembly.util.CoverageMap;
import org.jcvi.jillion.assembly.util.CoverageMapBuilder;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class ComputeCoverageMapForCas {

	public static void main(String[] args) throws IOException {
		File casFile = new File("/path/to/cas");
		
		//if using sam or bam file, only the implementation of transformationService has to change (see next line)
		//AssemblyTransformationService transformationService = new SamTransformationService(samFile, referenceFasta);
		
		AssemblyTransformationService transformationService = new CasFileTransformationService(casFile);
		CoverageMapTransformer coverageMapTransformer = new CoverageMapTransformer();
		
		transformationService.transform(coverageMapTransformer);
		
		for(Entry<String, List<Range>> entry : coverageMapTransformer.coverageMaps.entrySet()){
			String contigId = entry.getKey();
			
			CoverageMap<Range> coverageMap =  new CoverageMapBuilder<>(entry.getValue())
														.build();
			
			//do coverage map stuff here
			double avgCov = coverageMap.getAverageCoverage();
			
			System.out.println(contigId+"\t\t" + avgCov);
		}
		
	}
	
	
	private static class CoverageMapTransformer extends AbstractAssemblyTransformer{
		
		Map<String, List<Range>> coverageMaps = new LinkedHashMap<>();
		Map<String, NucleotideSequence> consensusSequences = new LinkedHashMap<>();
		
		@Override
		public void referenceOrConsensus(String id,	NucleotideSequence gappedReference) {
			consensusSequences.put(id, gappedReference);
			//initialize each list to 1 million elements to
			//reduce the number of resizes (if more than 1 million are added
			//then it will grow automatically
			coverageMaps.put(id, new ArrayList<Range>(1_000_000));
		}

		@Override
		public void aligned(String readId,
				NucleotideSequence nucleotideSequence,
				QualitySequence qualitySequence,
				PositionSequence positions, URI sourceFileUri,
				String referenceId, long gappedStartOffset,
				Direction direction, NucleotideSequence gappedSequence,
				ReadInfo readInfo) {
			
			
			Range range = new Range.Builder(gappedSequence.getLength())
									.shift(gappedStartOffset)
									.build();
			
			coverageMaps.get(referenceId).add(range);
		}
	}

}
