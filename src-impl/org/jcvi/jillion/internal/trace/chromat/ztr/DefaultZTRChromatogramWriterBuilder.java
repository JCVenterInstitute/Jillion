/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.trace.chromat.ztr;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.internal.core.seq.trace.sanger.chromat.ztr.data.Data;
import org.jcvi.jillion.internal.trace.chromat.ztr.chunk.Chunk;
import org.jcvi.jillion.internal.trace.chromat.ztr.chunk.ChunkType;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.DeltaEncodedData;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.DeltaEncodedData.Level;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.FollowData;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.RunLengthEncodedData;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.ShrinkToEightBitData;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.ZLibData;
import org.jcvi.jillion.trace.TraceEncoderException;
import org.jcvi.jillion.trace.chromat.Chromatogram;


/**
 * {@code DefaultZTRChromatogramWriterBuilder} is a Builder for
 * {@link ZtrChromatogramWriter}s that allows total control
 * over what encoders are used in which order for every different
 * field in a {@link ZtrChromatogram}.
 * 
 * <p/>
 * For example: here is how to build a ZTRChromatogramWriter
 * that encodes the same as the staden IO_Lib module:
 * <pre/> 
 	DefaultZTRChromatogramWriterBuilder builder = new DefaultZTRChromatogramWriterBuilder();
	builder.forBasecallChunkEncoder()
    			.addEncoder(ZLibData.INSTANCE);
	builder.forPositionsChunkEncoder()
		.addDeltaEncoder(DeltaEncodedData.SHORT, Level.DELTA_LEVEL_3)
		.addEncoder(ShrinkToEightBitData.SHORT_TO_BYTE)
		.addEncoder(FollowData.INSTANCE)
		.addRunLengthEncoder()
		.addEncoder(ZLibData.INSTANCE);
	builder.forConfidenceChunkEncoder()
		.addDeltaEncoder(DeltaEncodedData.BYTE, Level.DELTA_LEVEL_1)
		.addRunLengthEncoder((byte)77)
		.addEncoder(ZLibData.INSTANCE);
	builder.forPeaksChunkEncoder()
		.addDeltaEncoder(DeltaEncodedData.INTEGER, Level.DELTA_LEVEL_1)
		.addEncoder(ShrinkToEightBitData.INTEGER_TO_BYTE)
		.addEncoder(ZLibData.INSTANCE);
	builder.forCommentsChunkEncoder()
		.addEncoder(ZLibData.INSTANCE);
		
	ZTRChromatogramWriter writer = builder.build();
	<pre/>
 * <p/>
 * @author dkatzel
 *
 */
public final class DefaultZTRChromatogramWriterBuilder implements Builder<ZtrChromatogramWriter>{

	private final ChunkEncoderBuilder basecallEncoder = new ChunkEncoderBuilder(Chunk.BASE, ChunkType.BASECALLS);
	private final ChunkEncoderBuilder positionsEncoder= new ChunkEncoderBuilder(Chunk.SMP4, ChunkType.SAMPLES);
	private final ChunkEncoderBuilder confidenceEncoder= new ChunkEncoderBuilder(Chunk.CONFIDENCES, ChunkType.CONFIDENCE);
	private final ChunkEncoderBuilder commentsEncoder= new ChunkEncoderBuilder(Chunk.COMMENTS, ChunkType.COMMENTS);
	private final ChunkEncoderBuilder clipEncoder= new ChunkEncoderBuilder(Chunk.CLIP, ChunkType.CLIP);
	private final ChunkEncoderBuilder peaksEncoder= new ChunkEncoderBuilder(Chunk.POSITIONS, ChunkType.POSITIONS);
	/**
	 * Get the {@link ChunkEncoderBuilder} for the basecalls
	 * chunk.  
	 * @return a {@link ChunkEncoderBuilder} for the basecalls
	 * chunk of the ZTR.
	 */
	public ChunkEncoderBuilder forBasecallChunkEncoder(){
		return basecallEncoder;
	}
	/**
	 * Get the {@link ChunkEncoderBuilder} for the positions
	 * chunk.  
	 * @return a {@link ChunkEncoderBuilder} for the positions
	 * chunk of the ZTR.
	 */
	public ChunkEncoderBuilder forPositionsChunkEncoder(){
		return positionsEncoder;
	}
	/**
	 * Get the {@link ChunkEncoderBuilder} for the confidence
	 * (quality)
	 * chunk.  
	 * @return a {@link ChunkEncoderBuilder} for the confidence
	 * chunk of the ZTR.
	 */
	public ChunkEncoderBuilder forConfidenceChunkEncoder(){
		return confidenceEncoder;
	}
	/**
	 * Get the {@link ChunkEncoderBuilder} for the comments
	 * chunk.  
	 * @return a {@link ChunkEncoderBuilder} for the comments
	 * chunk of the ZTR.
	 */
	public ChunkEncoderBuilder forCommentsChunkEncoder(){
		return commentsEncoder;
	}
	/**
	 * Get the {@link ChunkEncoderBuilder} for the clip points
	 * chunk.  
	 * @return a {@link ChunkEncoderBuilder} for the clip points
	 * chunk of the ZTR.
	 */
	public ChunkEncoderBuilder forClipPointsChunkEncoder(){
		return clipEncoder;
	}
	/**
	 * Get the {@link ChunkEncoderBuilder} for the peaks
	 * chunk.  
	 * @return a {@link ChunkEncoderBuilder} for the peaks
	 * chunk of the ZTR.
	 */
	public ChunkEncoderBuilder forPeaksChunkEncoder(){
		return peaksEncoder;
	}
	/**
	 * Creates a new ZTRChromatogramWriter
	 * using the encoding settings that have been given.
	 */
	@Override
	public ZtrChromatogramWriter build() {
		return new DefaultZTRChromatogramWriter(
				basecallEncoder.build(), 
				positionsEncoder.build(), 
				confidenceEncoder.build(), 
				commentsEncoder.build(), 
				clipEncoder.build(),
				peaksEncoder.build());
	}

	private static final class DataEncoder{
		private final Data data;
		private final byte optionalParameter;
		
		private DataEncoder(Data data) {
			this(data,(byte)0);
		}
		private DataEncoder(Data data, byte optionalParameter) {
			if(data==null){
				throw new NullPointerException("data can not be null");
			}
			this.data = data;
			this.optionalParameter = optionalParameter;
		}


		private byte[] encode(byte[] data) throws TraceEncoderException{
			return this.data.encodeData(data, optionalParameter);
		}
	}
	
	private static final class ChunkEncoder{
		private final ChunkType type;
		private final Chunk chunk;
		private final List<DataEncoder> dataEncoders;
		
		private ChunkEncoder(ChunkType type, Chunk chunk,
				List<DataEncoder> dataEncoders) {
			super();
			this.type = type;
			this.chunk = chunk;
			this.dataEncoders = dataEncoders;
		}
		
		public byte[] encode(Chromatogram chromatogram) throws TraceEncoderException{
			
			byte[] currentData = chunk.encodeChunk(chromatogram);
			for(DataEncoder encoder : dataEncoders){
				currentData =encoder.encode(currentData);
			}
			ByteBuffer encodedData = ByteBuffer.allocate(12+currentData.length);
			try {
				encodedData.put(type.getTypeName().getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new TraceEncoderException("could not encode chunk type "+type,e);
			}
			//never put metadata, so metadata length is always zero
			encodedData.putInt(0);
			encodedData.putInt(currentData.length);
			encodedData.put(currentData);
			return encodedData.array();
		}
	}
	/**
	 * {@code ChunkEncoderBuilder} is a Builder which describes
	 * the order of the different encoding schemes used
	 * to encode each ZTR Chunk.  Encodings can be chained
	 * together to improve compression.  For example,
	 * it might be a good idea to encode a chunk with a Delta encoding
	 * and then with a {@link ShrinkToEightBitData} encoding
	 * since most of the delta values should fit into a single byte each. 
	 * 
	 * @author dkatzel
	 *
	 */
	public static class ChunkEncoderBuilder implements Builder<ChunkEncoder>{

		private final Chunk chunk;
		private final ChunkType chunkType;
		private final List<DataEncoder> encoders = new ArrayList<DataEncoder>();
		/**
		 * Create a new ChunkEncoderBuilder for the given {@link Chunk} and {@link ChunkType}.
		 * @param chunk the chunk that this will encode; may not be null.
		 * @param chunkType the chunk type that this will encode; may not be null.
		 * @throws NullPointerException if chunk or chunkType are null.
		 */
		public ChunkEncoderBuilder(Chunk chunk, ChunkType chunkType) {
			if(chunk==null){
				throw new NullPointerException("chunk can not be null");
			}
			if(chunkType==null){
				throw new NullPointerException("chunkType can not be null");
			}
			this.chunk = chunk;
			this.chunkType = chunkType;
		}		
		/**
		 * Adds a runLength Encoder using the default
		 * guard value.  This is the same as
		 * {@link #addRunLengthEncoder(byte) addRunLengthEncoder(RunLengthEncodedData.DEFAULT_GUARD)}
		 * @return this
		 * @see RunLengthEncodedData#DEFAULT_GUARD
		 * @see #addRunLengthEncoder()
		 */
		public ChunkEncoderBuilder addRunLengthEncoder(){
			return addRunLengthEncoder(RunLengthEncodedData.DEFAULT_GUARD);
		}
		/**
		 * Adds a ZLIB Encoder (for
		 * compressing data via ZIP).
		 * @return this.
		 */
		public ChunkEncoderBuilder addZLibEncoder(){
			encoders.add(new DataEncoder(ZLibData.INSTANCE));
			return this;
		}
		/**
		 * Adds a FollowData Encoder.
		 * @see FollowData
		 * @return this.
		 */
		public ChunkEncoderBuilder addFollowEncoder(){
			encoders.add(new DataEncoder(FollowData.INSTANCE));
			return this;
		}
		/**
		 * Adds a {@link ShrinkToEightBitData} encoder.
		 * @param shrinker the {@link ShrinkToEightBitData} encoder
		 * to use.
		 * @return this.
		 */
		public ChunkEncoderBuilder addShrinkEncoder(ShrinkToEightBitData shrinker){
			encoders.add(new DataEncoder(shrinker));
			return this;
		}
		/**
		 * Adds a runLength Encoder using the given
		 * guard value.  The guard value is used to run length blocks 
		 * and must be handled specially if it occurs in the un-encoded data;
		 * therefore it is preferable to use a guard value
		 * that won't occur often or at all in the un-encoded input data. 
		 * @param guard the value of the guard.
		 * @return this.
		 */
		public ChunkEncoderBuilder addRunLengthEncoder(byte guard){
			encoders.add(new DataEncoder(RunLengthEncodedData.INSTANCE,guard));
			return this;
		}
		/**
		 * Add a Delta Encoder of the given type with the given level.
		 * It has been found experimentally that {@link Level#DELTA_LEVEL_3}
		 * works better for int size values and {@link Level#DELTA_LEVEL_1}
		 * works better for byte and short sized values.
		 * @param deltaEncoder the deltaEncoder instance to use.
		 * @param deltaLevel the the delta level to use.  
		 * @return this
		 * @throws NullPointerException if deltaEncoder or deltaLevel are null.
		 */
		public ChunkEncoderBuilder addDeltaEncoder(DeltaEncodedData deltaEncoder,Level deltaLevel){
			encoders.add(new DataEncoder(deltaEncoder,deltaLevel.getLevel()));
			return this;
		}
		/**
		 * Constructs a new ChunkEncoder with the specified
		 * encoder chain for the given chunk and chunkType.
		 * @return a new ChunkEncoder.
		 */
		@Override
		public ChunkEncoder build() {			
			return new ChunkEncoder(chunkType, chunk, encoders);
		}
	}
	
	
	/**
	 * {@code DefaultZTRChromatogramWriter} is an implementation
	 * of ZTRChromatogramWriter.  Use {@link DefaultZTRChromatogramWriterBuilder}
	 * to customize the encoding options.
	 * @author dkatzel
	 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR 1.2 Spec</a>
	 */
	private static final class DefaultZTRChromatogramWriter implements ZtrChromatogramWriter{
		
		/**
		 * Specifies that this is chromatogram
		 * is encoded using ZTR 1.2 spec.
		 */
		private static final byte[] ZTR_VERSION =new byte[]{1,2};
		private final ChunkEncoder basecallEncoder;
		private final ChunkEncoder peaksEncoder;
		private final ChunkEncoder positionsEncoder;
		private final ChunkEncoder confidenceEncoder;
		private final ChunkEncoder commentsEncoder;
		private final ChunkEncoder clipEncoder;
		
		
		private DefaultZTRChromatogramWriter(ChunkEncoder basecallEncoder,
				ChunkEncoder positionsEncoder, ChunkEncoder confidenceEncoder,
				ChunkEncoder commentsEncoder, ChunkEncoder clipEncoder, ChunkEncoder peaksEncoder) {
			this.basecallEncoder = basecallEncoder;
			this.positionsEncoder = positionsEncoder;
			this.confidenceEncoder = confidenceEncoder;
			this.commentsEncoder = commentsEncoder;
			this.clipEncoder = clipEncoder;
			this.peaksEncoder = peaksEncoder;
		}
		/**
		 * Encode the given chromatogram and write it
		 * to the given outputStream.  The stream
		 * WILL NOT be closed when this method completes.
		 * @param chromatogram the ZTR chromatogram to 
		 * encode and write; may not be null.
		 * @param out the OutputStream to write the encoded
		 * ZTR to.
		 * @throws TraceEncoderException if there is a problem
		 * encoding the ZTR chromatogram.
		 * @throws NullPointerException if chromatogram or out
		 * are null.
		 */
		public void write(Chromatogram chromatogram, OutputStream out)
				throws TraceEncoderException {
			if(chromatogram ==null){
				throw new NullPointerException("chromatogram can not be null");
			}
			
			try {
				out.write(ZTRUtil.getMagicNumber());
				out.write(ZTR_VERSION);
				//this is the order that staden IO_Lib uses
				//some chunks are required before
				//other chunks can be parsed
				//(ex basecalls) so the order
				//should not be changed.
				out.write(positionsEncoder.encode(chromatogram));
				out.write(basecallEncoder.encode(chromatogram));
				out.write(peaksEncoder.encode(chromatogram));
				out.write(confidenceEncoder.encode(chromatogram));			
				out.write(commentsEncoder.encode(chromatogram));
				out.write(clipEncoder.encode(chromatogram));
				
			} catch (IOException e) {
				throw new TraceEncoderException("error writing ZTR", e);
			}
			
		}
	}
	
}
