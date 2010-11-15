/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.trace.sanger.chromatogram.ztr;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.Builder;
import org.jcvi.trace.TraceEncoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.Chunk;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.ChunkType;
import org.jcvi.trace.sanger.chromatogram.ztr.data.Data;
import org.jcvi.trace.sanger.chromatogram.ztr.data.DeltaEncodedData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.FollowData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.RunLengthEncodedData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.ShrinkToEightBitData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.ZLibData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.DeltaEncodedData.Level;

public class DefaultZTRChromatogramWriter implements ZTRChromatogramWriter{
	
	public static final ZTRChromatogramWriter IO_LIB_ZTR_WRITER;
	
	static{
		DefaultZTRChromatogramWriterBuilder builder = new DefaultZTRChromatogramWriterBuilder();
		builder.forBasecallChunkEncoder()
		        .addDataEncoder(ZLibData.INSTANCE);
		builder.forPositionsChunkEncoder()
			.addDeltaEncoder(DeltaEncodedData.SHORT, Level.DELTA_LEVEL_3)
			.addDataEncoder(ShrinkToEightBitData.SHORT_TO_BYTE)
			.addDataEncoder(FollowData.INSTANCE)
			.addRunLengthEncoder()
			.addDataEncoder(ZLibData.INSTANCE);
		builder.forConfidenceChunkEncoder()
				.addDeltaEncoder(DeltaEncodedData.BYTE, Level.DELTA_LEVEL_1)
				.addRunLengthEncoder((byte)77)
				.addDataEncoder(ZLibData.INSTANCE);
		builder.forPeaksChunkEncoder()
				.addDeltaEncoder(DeltaEncodedData.INTEGER, Level.DELTA_LEVEL_1)
				.addDataEncoder(ShrinkToEightBitData.INTEGER_TO_BYTE)
				.addDataEncoder(ZLibData.INSTANCE);
		builder.forCommentsChunkEncoder().addDataEncoder(ZLibData.INSTANCE);
			
		IO_LIB_ZTR_WRITER = builder.build();
	}
	
	
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
	
	
	public DefaultZTRChromatogramWriter(ChunkEncoder basecallEncoder,
			ChunkEncoder positionsEncoder, ChunkEncoder confidenceEncoder,
			ChunkEncoder commentsEncoder, ChunkEncoder clipEncoder, ChunkEncoder peaksEncoder) {
		super();
		this.basecallEncoder = basecallEncoder;
		this.positionsEncoder = positionsEncoder;
		this.confidenceEncoder = confidenceEncoder;
		this.commentsEncoder = commentsEncoder;
		this.clipEncoder = clipEncoder;
		this.peaksEncoder = peaksEncoder;
	}

	@Override
	public void write(ZTRChromatogram chromatogram, OutputStream out)
			throws TraceEncoderException {
		try {
			out.write(ZTRUtil.ZTR_MAGIC_NUMBER);
			out.write(ZTR_VERSION);
			
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

	private static class DataEncoder{
		private Data data;
		private byte optionalParameter;
		
		private DataEncoder(Data data) {
			this(data,(byte)0);
		}
		private DataEncoder(Data data, byte optionalParameter) {
			this.data = data;
			this.optionalParameter = optionalParameter;
		}


		private byte[] encode(byte[] data) throws TraceEncoderException{
			return this.data.encodeData(data, optionalParameter);
		}
	}
	
	private static class ChunkEncoder{
		private ChunkType type;
		private Chunk chunk;
		private List<DataEncoder> dataEncoders;
		
		private ChunkEncoder(ChunkType type, Chunk chunk,
				List<DataEncoder> dataEncoders) {
			super();
			this.type = type;
			this.chunk = chunk;
			this.dataEncoders = dataEncoders;
		}
		
		public byte[] encode(ZTRChromatogram chromatogram) throws TraceEncoderException{
			
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
	
	public static class ChunkEncoderBuilder implements Builder<ChunkEncoder>{

		private final Chunk chunk;
		private final ChunkType chunkType;
		private final List<DataEncoder> encoders = new ArrayList<DataEncoder>();
		
		public ChunkEncoderBuilder(Chunk chunk, ChunkType chunkType) {
			super();
			this.chunk = chunk;
			this.chunkType = chunkType;
		}

		public ChunkEncoderBuilder addDataEncoder(Data data){
			encoders.add(new DataEncoder(data));
			return this;
		}
		/**
		 * Adds a runLength Encoder using the default
		 * guard value.  This is the same as
		 * {@link #addRunLengthEncoder(byte), addRunLengthEncoder(RunLengthEncodedData.DEFAULT_GUARD)}
		 * @return this
		 * @see RunLengthEncodedData#DEFAULT_GUARD
		 * 
		 */
		public ChunkEncoderBuilder addRunLengthEncoder(){
			return addRunLengthEncoder(RunLengthEncodedData.DEFAULT_GUARD);
		}
		public ChunkEncoderBuilder addRunLengthEncoder(byte guard){
			encoders.add(new DataEncoder(RunLengthEncodedData.INSTANCE,guard));
			return this;
		}
		public ChunkEncoderBuilder addDeltaEncoder(DeltaEncodedData deltaEncoder,Level deltaLevel){
			encoders.add(new DataEncoder(deltaEncoder,deltaLevel.getLevel()));
			return this;
		}
		
		@Override
		public ChunkEncoder build() {			
			return new ChunkEncoder(chunkType, chunk, encoders);
		}
	}
	
	
	public static class DefaultZTRChromatogramWriterBuilder implements Builder<DefaultZTRChromatogramWriter>{

		private final ChunkEncoderBuilder basecallEncoder = new ChunkEncoderBuilder(Chunk.BASE, ChunkType.BASECALLS);
		private final ChunkEncoderBuilder positionsEncoder= new ChunkEncoderBuilder(Chunk.SMP4, ChunkType.SAMPLES);
		private final ChunkEncoderBuilder confidenceEncoder= new ChunkEncoderBuilder(Chunk.CONFIDENCES, ChunkType.CONFIDENCE);
		private final ChunkEncoderBuilder commentsEncoder= new ChunkEncoderBuilder(Chunk.COMMENTS, ChunkType.COMMENTS);
		private final ChunkEncoderBuilder clipEncoder= new ChunkEncoderBuilder(Chunk.CLIP, ChunkType.CLIP);
		private final ChunkEncoderBuilder peaksEncoder= new ChunkEncoderBuilder(Chunk.POSITIONS, ChunkType.POSITIONS);
		
		public ChunkEncoderBuilder forBasecallChunkEncoder(){
			return basecallEncoder;
		}
		public ChunkEncoderBuilder forPositionsChunkEncoder(){
			return positionsEncoder;
		}
		public ChunkEncoderBuilder forConfidenceChunkEncoder(){
			return confidenceEncoder;
		}
		public ChunkEncoderBuilder forCommentsChunkEncoder(){
			return commentsEncoder;
		}
		public ChunkEncoderBuilder forClipPointsChunkEncoder(){
			return clipEncoder;
		}
		public ChunkEncoderBuilder forPeaksChunkEncoder(){
			return peaksEncoder;
		}
		@Override
		public DefaultZTRChromatogramWriter build() {
			return new DefaultZTRChromatogramWriter(
					basecallEncoder.build(), 
					positionsEncoder.build(), 
					confidenceEncoder.build(), 
					commentsEncoder.build(), 
					clipEncoder.build(),
					peaksEncoder.build());
		}
		
	}
}
