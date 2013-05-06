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

import java.io.OutputStream;

import org.jcvi.jillion.internal.trace.chromat.ztr.data.DeltaEncodedData;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.DeltaEncodedData.Level;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.ShrinkToEightBitData;
import org.jcvi.jillion.trace.TraceEncoderException;
import org.jcvi.jillion.trace.chromat.Chromatogram;
/**
 * {@code IOLibLikeZtrChromatogramWriter} is a {@link ZtrChromatogramWriter}
 * implementation that performs the same encoding operations in the same order
 * as the staden IO_Lib C module.  Experiments have shown that 
 *  {@link IOLibLikeZtrChromatogramWriter}
 * will encode valid ZTR files that have about a 5% larger file size.
 * This is probably due to the standard Java implementation of zip does not allow
 * changing the "windowbits" size which could result in better
 * compression.  Adding a 3rd party library that allows more configuration
 * of encoding zipped data might enable smaller output file sizes
 * but that would cause an unnecessary dependency.
 * @author dkatzel
 * @see <a href ="http://staden.sourceforge.net/"> Staden Package Website</a>
 *
 */
public enum IOLibLikeZtrChromatogramWriter implements ZtrChromatogramWriter{
	/**
	 * Singleton instance of {@link IOLibLikeZtrChromatogramWriter}.
	 */
	INSTANCE;
	/**
	 * This is the guard value that IO_Lib uses for run length
	 * encoding its confidence values.
	 */
	public static final byte IO_LIB_CONFIDENCE_RUN_LENGTH_GUARD_VALUE = (byte)77;
	private final ZtrChromatogramWriter writer;
	
	{
		//these are the same encoders with the same parameters
		//in the same order as the staden IO_Lib C library's
		//ZTR 1.2 writer.
		DefaultZTRChromatogramWriterBuilder builder = new DefaultZTRChromatogramWriterBuilder();
		builder.forBasecallChunkEncoder()
	        .addZLibEncoder();
		builder.forPositionsChunkEncoder()
			.addDeltaEncoder(DeltaEncodedData.SHORT, Level.DELTA_LEVEL_3)
			.addShrinkEncoder(ShrinkToEightBitData.SHORT_TO_BYTE)
			.addFollowEncoder()
			.addRunLengthEncoder()
			.addZLibEncoder();
		builder.forConfidenceChunkEncoder()
			.addDeltaEncoder(DeltaEncodedData.BYTE, Level.DELTA_LEVEL_1)
			.addRunLengthEncoder(IO_LIB_CONFIDENCE_RUN_LENGTH_GUARD_VALUE)
			.addZLibEncoder();
		builder.forPeaksChunkEncoder()
			.addDeltaEncoder(DeltaEncodedData.INTEGER, Level.DELTA_LEVEL_1)
			.addShrinkEncoder(ShrinkToEightBitData.INTEGER_TO_BYTE)
			.addZLibEncoder();
		builder.forCommentsChunkEncoder()
			.addZLibEncoder();
			
		writer = builder.build();
	}
	@Override
	public void write(Chromatogram chromatogram, OutputStream out)
			throws TraceEncoderException {
		writer.write(chromatogram, out);
		
	}

}
