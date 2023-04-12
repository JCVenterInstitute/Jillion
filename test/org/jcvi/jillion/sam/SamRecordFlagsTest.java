package org.jcvi.jillion.sam;

import org.junit.Test;
import static org.junit.Assert.*;

public class SamRecordFlagsTest {

	@Test
	public void removeFlagsThatPreviouslySet() {
		SamRecordFlags sut = SamRecordFlags.valueOf(SamRecordFlag.MATE_UNMAPPED);
		
		SamRecordFlags updated = sut.remove(SamRecordFlag.MATE_UNMAPPED);
		
		assertFalse(updated.contains(SamRecordFlag.MATE_UNMAPPED));
	}
	
	@Test
	public void removeFlagsThatPreviouslyUnSet() {
		SamRecordFlags sut = SamRecordFlags.valueOf();
		
		SamRecordFlags updated = sut.remove(SamRecordFlag.MATE_UNMAPPED);
		
		assertFalse(updated.contains(SamRecordFlag.MATE_UNMAPPED));
	}
	
	@Test
	public void bulkRemove() {
		SamRecordFlags sut = SamRecordFlags.valueOf(SamRecordFlag.MATE_UNMAPPED, SamRecordFlag.DUPLICATE);
		
		SamRecordFlags updated = sut.remove(SamRecordFlag.MATE_UNMAPPED, SamRecordFlag.EACH_SEGMENT_PROPERLY_ALIGNED);
		
		assertEquals(SamRecordFlags.valueOf(SamRecordFlag.DUPLICATE), updated);
	}
}
