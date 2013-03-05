package org.jcvi.jillion.assembly.ace;

import java.util.Collections;
import java.util.Date;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestConsedConsensusQualityComputer {

	private Date phdDate = new Date(123456789);
	@Test
	public void oneXCoverageShouldJustUseThatRead() throws DataStoreException{
		AceContig contig = new AceContigBuilder("contigId", "ACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read1", "read1.phd", phdDate), 4)
							.build();
		QualitySequence read1Qualities = new QualitySequenceBuilder(new byte[]{20,30,40,50}).build();
		QualitySequenceDataStore qualityDataStore = DataStoreUtil.adapt(QualitySequenceDataStore.class, Collections.singletonMap("read1",read1Qualities ));
		
		QualitySequence actualConsensusQualities = AceFileUtil.computeConsensusQualities(contig, qualityDataStore);
		
		assertEquals(read1Qualities, actualConsensusQualities);
	}

}
