/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.sam.index;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.util.JillionUtil;
import org.jcvi.jillion.internal.sam.SamUtil;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamReferenceSequence;
import org.jcvi.jillion.sam.index.BamIndex;
import org.jcvi.jillion.sam.index.Bin;
import org.jcvi.jillion.sam.index.Chunk;
import org.jcvi.jillion.sam.index.ReferenceIndex;

public final class IndexUtil {

	private static final int METADATA_BIN_ID = 37450;

	private static final int INTERVAL_SHIFT = 14; //shift by 0x40_000 which is 14 bits or 16384
	private static final byte[] BAM_INDEX_MAGIC = new byte[]{'B','A','I',1};
	private IndexUtil(){
		//can not instantiate
	}
	
	public static int getIntervalOffsetFor(int genomicOffset){
		return genomicOffset >>INTERVAL_SHIFT;
		
	}
	
	public static BamIndex parseIndex(InputStream in, SamHeader header) throws IOException{
		byte[] magicNumber = IOUtil.readByteArray(in, 4);
		if(!Arrays.equals(BAM_INDEX_MAGIC, magicNumber)){
			throw new IOException("invalid magic number : " + Arrays.toString(magicNumber));
		}
		int numRefs = IOUtil.readSignedInt(in, ByteOrder.LITTLE_ENDIAN);
		List<ReferenceIndex> refIndexes = new ArrayList<ReferenceIndex>(numRefs);
		Iterator<SamReferenceSequence> refSeqIer = header.getReferenceSequences().iterator();
		for(int i=0; i<numRefs; i++){
			//don't need to use builders
			//since the file has everything
			//"prebuilt" for us.
			int numBins = IOUtil.readSignedInt(in, ByteOrder.LITTLE_ENDIAN);
			if(!refSeqIer.hasNext()){
				throw new NullPointerException("no ref " + i);
			}
			SamReferenceSequence refSeq = refSeqIer.next();
                        
			int maxBin = SamUtil.computeBinFor(new Range.Builder(1)
											.shift(refSeq.getLength())
											.build());
			Bin[] bins = new Bin[numBins];
			int numOfBinsUsed=0;
			VirtualFileOffset lowestStart=null, highestEnd =null;
			Long alignedCount=null, unAlignedCount=null;
			for(int j=0; j<numBins; j++){
				int binId = IOUtil.readSignedInt(in, ByteOrder.LITTLE_ENDIAN);
				int numChunks = IOUtil.readSignedInt(in, ByteOrder.LITTLE_ENDIAN);
				Chunk[] chunks = new Chunk[numChunks];
				
				for(int k =0; k<numChunks; k++){
					VirtualFileOffset begin = readVirtualFileOffset(in);					
					VirtualFileOffset end = readVirtualFileOffset(in);
					chunks[k] =new Chunk(begin, end);
				}
				if(binId==METADATA_BIN_ID && maxBin< METADATA_BIN_ID){
					//picard and samtools violate their
					//spec and put additional meta data in the
					//the last bin
					lowestStart = chunks[0].getBegin();
					highestEnd = chunks[0].getEnd();
					
					alignedCount = chunks[1].getBegin().getEncodedValue();
					unAlignedCount = chunks[1].getEnd().getEncodedValue();
					
				}else{
					bins[j] = new BaiBin(binId, chunks);
					numOfBinsUsed++;
				}
				
			}
			int numIntervals = IOUtil.readSignedInt(in, ByteOrder.LITTLE_ENDIAN);
			VirtualFileOffset intervals[] = new VirtualFileOffset[numIntervals];
			for(int j=0; j< numIntervals; j++){
				intervals[j] =readVirtualFileOffset(in);
			}
			BaiRefIndex ref = new BaiRefIndex(Arrays.copyOf(bins, numOfBinsUsed),
													intervals);

			//set metadata if any
			ref.setLowestStartOffset(lowestStart);
			ref.setHighestEndOffset(highestEnd);
			
			ref.setAlignedCount(alignedCount);
			ref.setUnalignedCount(unAlignedCount);
			
			refIndexes.add(ref);
		}
		//see if there is any more data
		//which is the # of unmapped reads
		PushbackInputStream in2 = new PushbackInputStream(in, 1);
		int value = in.read();
		if(value == -1){
			//EOF
			return new BamIndex(header, refIndexes);
		}
		in2.unread(value);
		long numUnMapped = IOUtil.readSignedLong(in2, ByteOrder.LITTLE_ENDIAN);
	
		return new BamIndex(header, refIndexes, numUnMapped);
	}

	private static VirtualFileOffset readVirtualFileOffset(InputStream in)
			throws IOException {
		return new VirtualFileOffset(
				//technically, the spec says unsigned but 
				//our bit shifting does casts so it should be ok
				IOUtil.readSignedLong(in, ByteOrder.LITTLE_ENDIAN)
				);
	}
	public static void writeIndex(OutputStream out, BamIndex indexes) throws IOException{
		writeIndex(out, indexes, false);
	}
	public static void writeIndex(OutputStream out, BamIndex indexes, boolean includeMetaData) throws IOException{
		out.write(BAM_INDEX_MAGIC);
		//assume little endian like BAM
		int numberOfIndexes = indexes.getNumberOfReferenceIndexes();
		IOUtil.putInt(out,numberOfIndexes, ByteOrder.LITTLE_ENDIAN);
		for(int i =0; i<numberOfIndexes; i++){
			ReferenceIndex refIndex = indexes.getReferenceIndex(i);
			List<Bin> bins;
			if(includeMetaData){
				bins = new ArrayList<Bin>(refIndex.getBins());
				Bin metaDataBin = createFakeMetaDataBin(refIndex);
				//only write metadata if we have bins
				if(metaDataBin !=null){
					bins.add(metaDataBin);
				}
			}else{
				bins = refIndex.getBins();
			}
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
			for(int j=0; j<intervals.length; j++){
				VirtualFileOffset current = intervals[j];
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
		if(includeMetaData){
			Long count =indexes.getTotalNumberOfUnmappedReads();
			IOUtil.putLong(out, count ==null? 0: count.longValue(), ByteOrder.LITTLE_ENDIAN);
		}
		out.close();
		
		
		
	}

	private static Bin createFakeMetaDataBin(ReferenceIndex refIndex) {
		if(!refIndex.hasMetaData() || refIndex.getNumberOfBins()==0){
			//no meta data
			return null;
		}
		//metadata is 2 chunks
		//chunk 1 = firstOffset - last offset
		//chunk 2 = # aligned vs #unaligned
		Chunk[] chunks = new Chunk[2];
		chunks[0] =new Chunk(refIndex.getLowestStartOffset(), refIndex.getHighestEndOffset());
		//chunk doesn't do any validation to make sure start < end
		//so this should be safe...
		chunks[1] =new Chunk(new VirtualFileOffset(refIndex.getNumberOfAlignedReads()), 
							new VirtualFileOffset(refIndex.getNumberOfUnAlignedReads()));
		return new BaiBin(METADATA_BIN_ID, chunks);
	}

	/**
	 * Sorts Bins by bin number in increasing order.
	 * @author dkatzel
	 *
	 */
	public static enum BinSorter implements Comparator<Bin>{
		
		INSTANCE;

		@Override
		public int compare(Bin o1, Bin o2) {
			return JillionUtil.compare(o1.getBinNumber(), o2.getBinNumber());
		}
		
	}
	
}
