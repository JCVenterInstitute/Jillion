package org.jcvi.jillion.sam;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class StreamingModifiedMatePairedWriter implements SamWriter{

	private final SamWriter delegate;
	
	private Map<String, SamRecord> mateCache = new HashMap<String, SamRecord>(200_00);
	
	public StreamingModifiedMatePairedWriter(SamWriter delegate) {
		this.delegate = delegate;
	}

	@Override
	public void close() throws IOException {
		for(SamRecord r : mateCache.values()) {
			delegate.writeRecord(r);
		}
		delegate.close();
		
	}

	@Override
	public void writeRecord(SamRecord record) throws IOException {
		if(record.getFlags().contains(SamRecordFlag.HAS_MATE_PAIR)) {
			//does this mate pair already seen
			SamRecord mate = mateCache.remove(record.getQueryName());
			if(mate !=null) {
				//found the mate!
				SamRecord mateToWrite = mate;
				if(record.mapped()) {
					if(mate.getFlags().contains(SamRecordFlag.MATE_UNMAPPED)) {
						mateToWrite = mate.toBuilder().setFlags( mate.getFlags().remove(SamRecordFlag.MATE_UNMAPPED))
											.build();
					}
				}else {
					//record did NOT map
					if(!mate.getFlags().contains(SamRecordFlag.MATE_UNMAPPED)) {
						//make mate unmapped
						SamRecordFlags updatedFlags = mate.getFlags().add(SamRecordFlag.MATE_UNMAPPED)
															.remove(SamRecordFlag.EACH_SEGMENT_PROPERLY_ALIGNED);
						mateToWrite = mate.toBuilder().setFlags(updatedFlags)
											.build();
					}
				}
				delegate.writeRecord(mateToWrite);
				delegate.writeRecord(record);
			}else {
				//no mate yet add it to cache
				mateCache.put(record.getQueryName(), record);
			}
		}else {
			delegate.writeRecord(record);
		}
		
	}

}
