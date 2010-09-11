package org.jcvi.trace.sanger.chromatogram.ab1;

import java.util.List;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.trace.sanger.chromatogram.ChromatogramFileVisitor;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.TaggedDataRecord;

public interface Ab1ChromatogramFileVisitor extends ChromatogramFileVisitor{

	void visitOriginalBasecalls(String originalBasecalls);
	
	void visitChannelOrder(List<NucleotideGlyph> order);
	
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
	 * @param originalTraceData
	 */
	void visitOriginalPeaks(short[] originalPeaks);

	void visitOriginalAConfidence(byte[] originalConfidence);
	
	void visitOriginalCConfidence(byte[] originalConfidence);
	
	void visitOriginalGConfidence(byte[] originalConfidence);
	
	void visitOriginalTConfidence(byte[] originalConfidence);
}
