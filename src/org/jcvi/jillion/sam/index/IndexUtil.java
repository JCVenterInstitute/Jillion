package org.jcvi.jillion.sam.index;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.sam.VirtualFileOffset;

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
	
	public static List<ReferenceIndex> parseIndex(InputStream in) throws IOException{
		byte[] magicNumber = IOUtil.readByteArray(in, 4);
		if(!BAM_INDEX_MAGIC.equals(magicNumber)){
			throw new IOException("invalid magic number : " + Arrays.toString(magicNumber));
		}
		int numRefs = IOUtil.readSignedInt(in, ByteOrder.LITTLE_ENDIAN);
		List<ReferenceIndex> refIndexes = new ArrayList<ReferenceIndex>(numRefs);
		
		for(int i=0; i<numRefs; i++){
			//don't need to use builders
			//since the file has everything
			//"prebuilt" for us.
			int numBins = IOUtil.readSignedInt(in, ByteOrder.LITTLE_ENDIAN);
			Bin[] bins = new Bin[numBins];
			for(int j=0; j<numBins; j++){
				int binId = IOUtil.readSignedInt(in, ByteOrder.LITTLE_ENDIAN);
				int numChunks = IOUtil.readSignedInt(in, ByteOrder.LITTLE_ENDIAN);
				Chunk[] chunks = new Chunk[numChunks];
				
				for(int k =0; k<numChunks; k++){
					VirtualFileOffset begin = readVirtualFileOffset(in);					
					VirtualFileOffset end = readVirtualFileOffset(in);
					chunks[k] =new Chunk(begin, end);
				}
				bins[j] = new BaiBin(binId, chunks);
			}
			int numIntervals = IOUtil.readSignedInt(in, ByteOrder.LITTLE_ENDIAN);
			VirtualFileOffset intervals[] = new VirtualFileOffset[numIntervals];
			for(int j=0; j< numIntervals; j++){
				intervals[j] =readVirtualFileOffset(in);
			}
			refIndexes.add(new BaiRefIndex(bins,intervals));
		}
		
		return refIndexes;
	}

	private static VirtualFileOffset readVirtualFileOffset(InputStream in)
			throws IOException {
		return new VirtualFileOffset(
				//technically, the spec says unsigned but 
				//our bit shifting does casts so it should be ok
				IOUtil.readSignedLong(in, ByteOrder.LITTLE_ENDIAN)
				);
	}
	
	public static void writeIndex(OutputStream out, List<ReferenceIndex> indexes) throws IOException{
		out.write(BAM_INDEX_MAGIC);
		//assume little endian like BAM
		IOUtil.putInt(out,indexes.size(), ByteOrder.LITTLE_ENDIAN);
		for(ReferenceIndex refIndex : indexes){
			List<Bin> bins = refIndex.getBins();
			IOUtil.putInt(out,bins.size(), ByteOrder.LITTLE_ENDIAN);
			for(Bin bin : bins){
				IOUtil.putInt(out,bin.getBinNumber(), ByteOrder.LITTLE_ENDIAN);
				List<Chunk> chunks = bin.getChunks();
				IOUtil.putInt(out,chunks.size(), ByteOrder.LITTLE_ENDIAN);
				for(Chunk chunk : chunks){
					IOUtil.putLong(out,chunk.getBegin().getEncodedValue(), ByteOrder.LITTLE_ENDIAN);
					IOUtil.putLong(out,chunk.getEnd().getEncodedValue(), ByteOrder.LITTLE_ENDIAN);
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
					long encodedValue = current.getEncodedValue();
					IOUtil.putLong(out, encodedValue, ByteOrder.LITTLE_ENDIAN);
					prev = encodedValue;
				}
			}
		}
		
		out.close();
		
		
		
	}
	
	
	
}
