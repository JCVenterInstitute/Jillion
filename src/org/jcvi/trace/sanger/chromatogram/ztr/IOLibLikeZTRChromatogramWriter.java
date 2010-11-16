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

import java.io.OutputStream;

import org.jcvi.trace.TraceEncoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.DefaultZTRChromatogramWriter.DefaultZTRChromatogramWriterBuilder;
import org.jcvi.trace.sanger.chromatogram.ztr.data.DeltaEncodedData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.FollowData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.ShrinkToEightBitData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.ZLibData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.DeltaEncodedData.Level;
/**
 * {@code IOLibLikeZTRChromatogramWriter} is a {@link ZTRChromatogramWriter}
 * implementation that performs the same encoding operations in the same order
 * as the staden IO_Lib C module.  Experiments have shown that 
 *  IOLibLikeZTRChromatogramWriter
 * will encode valid ZTR files that have about a 5% larger filesize.
 * This is probably due to the standard Java implementation of zip does not allow
 * changing the "windowbits" size which could result in better
 * compression.
 * @author dkatzel
 *
 */
public enum IOLibLikeZTRChromatogramWriter implements ZTRChromatogramWriter{
	/**
	 * Singleton instance of {@link IOLibLikeZTRChromatogramWriter}.
	 */
	INSTANCE;

	private final DefaultZTRChromatogramWriter writer;
	
	{
		//these are the same encoders with the same parameters
		//with in the same order
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
			
		writer = builder.build();
	}
	@Override
	public void write(ZTRChromatogram chromatogram, OutputStream out)
			throws TraceEncoderException {
		writer.write(chromatogram, out);
		
	}

}
