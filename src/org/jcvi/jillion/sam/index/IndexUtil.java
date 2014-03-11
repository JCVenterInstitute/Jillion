package org.jcvi.jillion.sam.index;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;

public final class IndexUtil {

	private static final int INTERVAL_LENGTH = 16384; //16kbp
	
	private static final byte[] BAM_INDEX_MAGIC = new byte[]{'B','A','I',1};
	private IndexUtil(){
		//can not instantiate
	}
	
	public static int getIntervalOffsetFor(int startPosition){
		int numIntervals =startPosition/INTERVAL_LENGTH;
		if(startPosition % INTERVAL_LENGTH !=0){
			numIntervals++;
		}
		return numIntervals;
	}
	
	public static void writeIndex(OutputStream out, List<ReferenceIndex> indexes) throws IOException{
		out.write(BAM_INDEX_MAGIC);
		//assume little endian like BAM?
		IOUtil.putInt(out,indexes.size(), ByteOrder.LITTLE_ENDIAN);
		for(ReferenceIndex refIndex : indexes){
			List<Bin> bins = refIndex.getBins();
			IOUtil.putInt(out,bins.size(), ByteOrder.LITTLE_ENDIAN);
			for(Bin bin : bins){
				IOUtil.putInt(out,bin.getBinNumber(), ByteOrder.LITTLE_ENDIAN);
				List<Chunk> chunks = bin.getChunks();
				IOUtil.putInt(out,chunks.size(), ByteOrder.LITTLE_ENDIAN);
				for(Chunk chunk : chunks){
					IOUtil.putLong(out,encode(chunk.getBegin()), ByteOrder.LITTLE_ENDIAN);
					IOUtil.putLong(out,encode(chunk.getEnd()), ByteOrder.LITTLE_ENDIAN);
				}
			}
			//intervals
			VirtualFileOffset[] intervals =refIndex.getIntervals();
			IOUtil.putInt(out,intervals.length, ByteOrder.LITTLE_ENDIAN);
			long prev =0;
			for(int i=0; i<intervals.length; i++){
				VirtualFileOffset current = intervals[i];
				if(current ==null){
					//no offset for this interval
					//use previous?
					IOUtil.putLong(out, prev, ByteOrder.LITTLE_ENDIAN);
				}else{
					long encodedOffset = encode(current);
					IOUtil.putLong(out, encodedOffset, ByteOrder.LITTLE_ENDIAN);
					prev = encodedOffset;
				}
			}
		}
		
		out.close();
		
		
		
	}
	
	private static long encode(VirtualFileOffset vfo){
		long ret = vfo.getCompressedBamBlockOffset()<<16;
		ret |= vfo.getUncompressedOffset();
		return ret;
	}
	
}
