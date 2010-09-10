package org.jcvi.trace.sanger.chromatogram.ab1;

import java.util.List;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.trace.sanger.chromatogram.ChromatogramFileVisitor;

public interface Ab1ChromatogramFileVisitor extends ChromatogramFileVisitor{

	void visitOriginalBasecalls(String originalBasecalls);
	
	void visitChannelOrder(List<NucleotideGlyph> order);
}
