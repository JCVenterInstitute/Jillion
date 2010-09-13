/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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
package org.jcvi.trace.sanger.chromatogram.abi;

import java.util.List;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.trace.sanger.chromatogram.ChromatogramFileVisitor;
import org.jcvi.trace.sanger.chromatogram.abi.tag.TaggedDataRecord;
/**
 * {@code Ab1ChromatogramFileVisitor} is an ABI
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
	void visitOriginalBasecalls(String originalBasecalls);
	/**
	 * Visit the order of the A,C,G and T channels
	 * can vary depending on the machine setup;
	 * this is the order of the channels on the
	 * machine during the run.
	 * @param order the order of A,C,G and T
	 * channels during the run (never null or empty).
	 */
	void visitChannelOrder(List<NucleotideGlyph> order);
	/**
	 * Visit a single {@link TaggedDataRecord}
	 * that describes the format, type, length
	 * and file offset of a  particular ABI data
	 * block.
	 * @param record a TaggedDataRecord, never null.
	 */
	void  visitTaggedDataRecord(TaggedDataRecord<?> record);
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
}
