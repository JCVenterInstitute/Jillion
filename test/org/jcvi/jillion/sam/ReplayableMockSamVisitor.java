package org.jcvi.jillion.sam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.header.SamHeader;

public class ReplayableMockSamVisitor implements SamVisitor{
	private boolean expectationMode=true;
	private Iterator<SamRecord> expectedIterator;
	
	private final List<SamRecord> expected = new ArrayList<SamRecord>();

	private SamHeader expectedHeader;
	
	
	private final boolean checkHeaderMatches;
	

	public ReplayableMockSamVisitor(boolean checkHeaderMatches){
		this.checkHeaderMatches = checkHeaderMatches;
	}
	
	@Override
	public void visitHeader(SamVisitorCallback callback, SamHeader header) {
		if(checkHeaderMatches){
			if(expectationMode){
				expectedHeader = header;
			}else{
				assertEquals(expectedHeader, header);
			}
		}
		
	}

	

	@Override
	public void visitRecord(SamVisitorCallback callback, SamRecord record,
			VirtualFileOffset start, VirtualFileOffset end) {
		if(expectationMode){
			expected.add(record);
		}else{
			assertTrue(expectedIterator.hasNext());
			assertEquals(expectedIterator.next(), record);
		}
	}

	@Override
	public void visitEnd() {
		if(expectationMode){
			changeToReplayMode();
		}else{
			assertFalse(expectedIterator.hasNext());
		}
	}

	private void changeToReplayMode() {
		expectationMode=false;
		expectedIterator = expected.iterator();
	}

	@Override
	public void halted() {
		if(expectationMode){
			changeToReplayMode();
		}else{
			assertFalse(expectedIterator.hasNext());
		}
	}
	
	
}