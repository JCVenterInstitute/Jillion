package org.jcvi.jillion.assembly.clc.cas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.jcvi.jillion.testutils.assembly.cas.CasParserTestDouble;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestFilteredCasGappedReferenceDataStoreBuilderVisitor {

	private CasParser parser;
	
	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();
	
	@Before
	public void setup() throws IOException{
		parser = new CasParserTestDouble.Builder(tmp.getRoot())
					.addReference("id_1", "ACGTACGT")
					.addReference("id_2", "TTTTTTTT")
					
					.forwardMatch("id_2", 0)
							.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 2)
							.addAlignmentRegion(CasAlignmentRegionType.INSERT, 1)
							.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 6)
							.build()
							
					.forwardMatch("id_1", 0)
							.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
							.addAlignmentRegion(CasAlignmentRegionType.INSERT, 1)
							.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
							.build()
					.reverseMatch("id_1", 0)
							.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 8)
							.build()
					.reverseMatch("id_1", 0)
							.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 8)
							.build()
					.reverseMatch("id_1", 0)
							.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 8)
							.build()
					.reverseMatch("id_2", 0)
							.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 8)
							.build()
					.build();
	}
	
	
	@Test
	public void unfiltered() throws IOException, DataStoreException{
		CasGappedReferenceDataStoreBuilderVisitor sut = new CasGappedReferenceDataStoreBuilderVisitor(parser.getWorkingDir());
		
		parser.parse(sut);
		 new ExpectedGappedReferenceDataStoreBuilder()
						.addRef("id_1", "ACGT-ACGT")
						.addRef("id_2", "TT-TTTTTT")
						.assertMatches(sut.build());
		
	}
	
	@Test
	public void filterFirstRef() throws IOException, DataStoreException{
		CasGappedReferenceDataStoreBuilderVisitor sut = new CasGappedReferenceDataStoreBuilderVisitor(
																parser.getWorkingDir(),
																id-> "id_2".equals(id));
		
		parser.parse(sut);
		 new ExpectedGappedReferenceDataStoreBuilder()
						.skip("id_1")
						.addRef("id_2", "TT-TTTTTT")
						.assertMatches(sut.build());
	}

	@Test
	public void filterSecondRef() throws IOException, DataStoreException{
		CasGappedReferenceDataStoreBuilderVisitor sut = new CasGappedReferenceDataStoreBuilderVisitor(
																parser.getWorkingDir(),
																id-> "id_1".equals(id));
		
		parser.parse(sut);
		 new ExpectedGappedReferenceDataStoreBuilder()
						 .addRef("id_1", "ACGT-ACGT")
						 .skip("id_2")						
						.assertMatches(sut.build());
	}
	
	@Test
	public void filterEverything() throws IOException, DataStoreException{
		CasGappedReferenceDataStoreBuilderVisitor sut = new CasGappedReferenceDataStoreBuilderVisitor(
																parser.getWorkingDir(),
																id-> false);
		
		parser.parse(sut);
		 new ExpectedGappedReferenceDataStoreBuilder()
						 .skip("id_1")
						 .skip("id_2")						
						.assertMatches(sut.build());
	}
	
	
	private static final class ExpectedGappedReferenceDataStoreBuilder{
		
		List<String> idOrder = new ArrayList<String>();
		Map<String, NucleotideSequence> map = new LinkedHashMap<>();
		
		public ExpectedGappedReferenceDataStoreBuilder addRef(String id, String gappedSeq){
			map.put(id, NucleotideSequenceTestUtil.create(gappedSeq));
			idOrder.add(id);
			
			return this;
		}
		
		public ExpectedGappedReferenceDataStoreBuilder skip(String id){			
			idOrder.add(id);
			
			return this;
		}
		
		public void assertMatches(CasGappedReferenceDataStore actual) throws DataStoreException{
			assertEquals(map.size(), actual.getNumberOfRecords());
			for(long i=0; i<idOrder.size(); i++){
				String id = idOrder.get((int)i);
				NucleotideSequence expectedSequence = map.get(id);
				assertEquals(id, actual.getIdByIndex(i));
				if(expectedSequence==null){
					assertFalse(actual.contains(id));
					assertNull(actual.get(id));
				}else{
					assertEquals(expectedSequence, actual.get(id));
				}
			}
		}
		
	}
	
	
}
