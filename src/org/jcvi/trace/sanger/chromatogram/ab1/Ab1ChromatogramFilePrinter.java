package org.jcvi.trace.sanger.chromatogram.ab1;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import org.jcvi.glyph.nuc.NucleotideGlyph;

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitAPositions(short[] positions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitBasecalls(String basecalls) {
		out.printf("current basecalls = %s%n",basecalls);
		
	}

	@Override
	public void visitCConfidence(byte[] confidence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitCPositions(short[] positions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitComments(Properties comments) {
		out.println(comments);
		
	}

	@Override
	public void visitGConfidence(byte[] confidence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitGPositions(short[] positions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitPeaks(short[] peaks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTConfidence(byte[] confidence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTPositions(short[] positions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitEndOfFile() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitFile() {
		// TODO Auto-generated method stub
		
	}

}
