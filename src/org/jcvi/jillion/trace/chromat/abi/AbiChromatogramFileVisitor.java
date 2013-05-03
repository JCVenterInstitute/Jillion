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
package org.jcvi.jillion.trace.chromat.abi;

import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.Ab1LocalDate;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.Ab1LocalTime;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.ByteArrayTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.DateTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.FloatArrayTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.IntArrayTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.ShortArrayTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.StringTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.TimeTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.UserDefinedTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.rate.ScanRate;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.rate.ScanRateTaggedDataType;
import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;
/**
 * {@code AbiChromatogramFileVisitor} is an ABI
 * Chromatogram specific implementation of 
 * {@link ChromatogramFileVisitor}.
 * @author dkatzel
 *
 */
public interface AbiChromatogramFileVisitor extends ChromatogramFileVisitor{
	/**
	 * Visit the original, unedited basecalls
	 * that were called off the sequencer.
	 * @param originalBasecalls the original basecalls (not null).
	 */
	void visitOriginalBasecalls(NucleotideSequence originalBasecalls);
	/**
	 * Visit the order of the A,C,G and T channels
	 * can vary depending on the machine setup;
	 * this is the order of the channels on the
	 * machine during the run.
	 * @param order the order of A,C,G and T
	 * channels during the run (never null or empty).
	 */
	void visitChannelOrder(List<Nucleotide> order);
	/**
	 * Visit a raw data traces, representing photometric
	 * data as recorded through a single optical filter.
	 * @param rawTraceData the raw photometric data.
	 * @param opticalFilterId which optical filter recorded
	 * this data (0 -4)
	 */
	void visitPhotometricData(short[] rawTraceData, int opticalFilterId);
	/**
	 * Visit the voltage (volts/10) of the gel
	 * during the run at every time point.
	 * @param gelVoltage a series of voltages
	 * one for every time point in the run.
	 */
	void visitGelVoltageData(short[] gelVoltage);
	
	/**
	 * Visit the current running thru the gel
	 * during the run at every time point.
	 * @param gelCurrent a series of current readings,
	 * one for every time point in the run.
	 */
	void visitGelCurrentData(short[] gelCurrent);

	void visitElectrophoreticPower(short[] electrophoreticPowerData);
	/**
	 * Visit the temperature data of the gel
	 * during the run at every time point.
	 * @param gelTemp a series of current readings,
	 * one for every time point in the run.
	 */
	void visitGelTemperatureData(short[] gelTemp);
	/**
	 * Visit the original ABI called peaks.
	 * @param originalTraceData the original
	 * called peaks as shorts, never null.
	 */
	void visitOriginalPeaks(short[] originalPeaks);
	/**
	 * Visit the original ABI called confidence
	 * (quality) values for the A Channel.
	 * @param originalConfidence the original
	 * called A confidences, never null.
	 */
	void visitOriginalAConfidence(byte[] originalConfidence);
	/**
	 * Visit the original ABI called confidence
	 * (quality) values for the C Channel.
	 * @param originalConfidence the original
	 * called C confidences, never null.
	 */
	void visitOriginalCConfidence(byte[] originalConfidence);
	/**
	 * Visit the original ABI called confidence
	 * (quality) values for the G Channel.
	 * @param originalConfidence the original
	 * called G confidences, never null.
	 */
	void visitOriginalGConfidence(byte[] originalConfidence);
	/**
	 * Visit the original ABI called confidence
	 * (quality) values for the T Channel.
	 * @param originalConfidence the original
	 * called T confidences, never null.
	 */
	void visitOriginalTConfidence(byte[] originalConfidence);
	/**
	 * Visit the scaling factors 
	 * for the average signals recovered from the
	 * signals.
	 * @param aScale the scale used for the A channel.
	 * @param cScale the scale used for the C channel.
	 * @param gScale the scale used for the G channel.
	 * @param tScale the scale used for the T channel.
	 */
	void visitScaleFactors(short aScale, short cScale, short gScale,
			short tScale);
	
	void visitTaggedDataRecord(ByteArrayTaggedDataRecord record, byte[] data);
	void visitTaggedDataRecord(UserDefinedTaggedDataRecord record, byte[] data);
	
	void visitTaggedDataRecord(ScanRateTaggedDataType record, ScanRate scanRate);
	void visitTaggedDataRecord(ShortArrayTaggedDataRecord record, short[] data);
	
	void visitTaggedDataRecord(IntArrayTaggedDataRecord record, int[] data);
	
	void visitTaggedDataRecord(FloatArrayTaggedDataRecord record, float[] data);
    
	void visitTaggedDataRecord(StringTaggedDataRecord record, String data);
	
	void visitTaggedDataRecord(TimeTaggedDataRecord record, Ab1LocalTime time);
    
    void visitTaggedDataRecord(DateTaggedDataRecord record, Ab1LocalDate date);
    
}
