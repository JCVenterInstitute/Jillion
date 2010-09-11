package org.jcvi.trace.sanger.chromatogram.ab1;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.trace.sanger.chromatogram.ab1.tag.TaggedDataRecord;

public class Ab1ChromatogramFilePrinter implements Ab1ChromatogramFileVisitor{

	private final PrintStream out;
	
	Ab1ChromatogramFilePrinter(){
		this(System.out);
	}
	
	public Ab1ChromatogramFilePrinter(PrintStream out) {
		super();
		this.out = out;
	}

	@Override
	public void visitChannelOrder(List<NucleotideGlyph> order) {
		out.printf("channel order = %s%n",order);
		
	}

	@Override
	public void visitOriginalBasecalls(String originalBasecalls) {
		out.printf("original basecalls = %s%n",originalBasecalls);
		
	}

	@Override
	public void visitAConfidence(byte[] confidence) {
		byte[] head = new byte[5];
		System.arraycopy(confidence, 0, head, 0, 5);
		out.println("visited A confidence " + confidence.length +
				"  head = "+Arrays.toString(head));
		
	}

	@Override
	public void visitAPositions(short[] positions) {
		out.println("visited A pos " + positions.length);
		
		
	}

	@Override
	public void visitBasecalls(String basecalls) {
		out.printf("current basecalls = %s%n",basecalls);
		
	}

	@Override
	public void visitCConfidence(byte[] confidence) {
		byte[] head = new byte[5];
		System.arraycopy(confidence, 0, head, 0, 5);
		out.println("visited C confidence " + confidence.length +
				"  head = "+Arrays.toString(head));
		
	}

	@Override
	public void visitCPositions(short[] positions) {
		out.println("visited C pos " + positions.length);
		
		
	}

	@Override
	public void visitComments(Properties comments) {
		out.println(comments);
		
	}

	@Override
	public void visitGConfidence(byte[] confidence) {
		byte[] head = new byte[5];
		System.arraycopy(confidence, 0, head, 0, 5);
		out.println("visited G confidence " + confidence.length +
				"  head = "+Arrays.toString(head));
		
	}

	@Override
	public void visitGPositions(short[] positions) {
		out.println("visited G pos " + positions.length);
		
		
	}

	@Override
	public void visitPeaks(short[] peaks) {
		out.println("visited peaks " + peaks.length);
		
		
	}

	@Override
	public void visitTConfidence(byte[] confidence) {
		byte[] head = new byte[5];
		System.arraycopy(confidence, 0, head, 0, 5);
		out.println("visited T confidence " + confidence.length +
				"  head = "+Arrays.toString(head));
		
		
	}

	@Override
	public void visitTPositions(short[] positions) {
		out.println("visited T pos " + positions.length);
		
		
	}

	@Override
	public void visitEndOfFile() {
		out.println("end parsing");
		
	}

	@Override
	public void visitFile() {
		out.println("starting parsing");
		
	}

	@Override
	public void visitTaggedDataRecord(TaggedDataRecord<?> record) {
		out.println("tagged Record = "+ record);
		
	}

	@Override
	public void visitElectrophoreticPower(short[] electrophoreticPowerData) {
		out.println("visited elctroPower" + "  length ="+ electrophoreticPowerData.length);
		
	}

	@Override
	public void visitGelCurrentData(short[] gelCurrent) {
		out.println("visited gelCurrent" + "  length ="+ gelCurrent.length);
		
		
	}

	@Override
	public void visitGelTemperatureData(short[] gelTemp) {
		out.println("visited gelTemp" + "  length ="+ gelTemp.length);
		
		
	}

	@Override
	public void visitGelVoltageData(short[] gelVoltage) {
		out.println("visited gelVoltage" + "  length ="+ gelVoltage.length);
		
		
	}

	@Override
	public void visitPhotometricData(short[] rawTraceData, int opticalFilterId) {
		out.println("visited photometric data for optical #" + opticalFilterId + "  length ="+ rawTraceData.length);
		
	}

	@Override
	public void visitOriginalPeaks(short[] originalPeaks) {
		out.println("visited ORIGINAL peaks " + originalPeaks.length);
	}

	@Override
	public void visitOriginalAConfidence(byte[] originalConfidence) {
		byte[] head = new byte[5];
		System.arraycopy(originalConfidence, 0, head, 0, 5);
		out.println("visited ORIGINAL A confidence " + originalConfidence.length +
				"  head = "+Arrays.toString(head));
		
	}

	@Override
	public void visitOriginalCConfidence(byte[] originalConfidence) {
		byte[] head = new byte[5];
		System.arraycopy(originalConfidence, 0, head, 0, 5);
		out.println("visited ORIGINAL C confidence " + originalConfidence.length +
				"  head = "+Arrays.toString(head));
		
	}

	@Override
	public void visitOriginalGConfidence(byte[] originalConfidence) {
		byte[] head = new byte[5];
		System.arraycopy(originalConfidence, 0, head, 0, 5);
		out.println("visited ORIGINAL G confidence " + originalConfidence.length +
				"  head = "+Arrays.toString(head));
		
	}

	@Override
	public void visitOriginalTConfidence(byte[] originalConfidence) {
		byte[] head = new byte[5];
		System.arraycopy(originalConfidence, 0, head, 0, 5);
		out.println("visited ORIGINAL T confidence " + originalConfidence.length +
				"  head = "+Arrays.toString(head));
		
	}

}
