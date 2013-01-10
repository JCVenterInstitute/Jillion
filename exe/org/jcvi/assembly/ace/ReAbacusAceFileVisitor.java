package org.jcvi.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Set;

import org.jcvi.common.core.assembly.ace.AceFileVisitor;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;

class ReAbacusAceFileVisitor implements AceFileVisitor{
	private final File tempOutputDir;
	private final String tempFilePrefix;
	private final Set<String> idsToReAbacus;
	
	private boolean streamThruCurrentContig=false;
	private PrintWriter currentWriter;
	private String currentLine;
	public ReAbacusAceFileVisitor(
			Set<String> idsToReAbacus,
			File tempOutputDir, String tempFilePrefix) {
		this.tempOutputDir = tempOutputDir;
		this.tempFilePrefix = tempFilePrefix;
		this.idsToReAbacus = idsToReAbacus;
	}

	@Override
	public void visitLine(String line) {
		if(streamThruCurrentContig){
			currentWriter.print(line);
		}
		currentLine=line;
	}

	@Override
	public void visitFile() {
		
	}

	@Override
	public void visitEndOfFile() {
		
	}

	@Override
	public void visitHeader(int numberOfContigs, int totalNumberOfReads) {
		//ignore
		
	}


	@Override
	public BeginContigReturnCode visitBeginContig(String contigId, int numberOfBases,
			int numberOfReads, int numberOfBaseSegments,
			boolean reverseComplemented) {
		streamThruCurrentContig = !idsToReAbacus.contains(contigId);
		
		if(streamThruCurrentContig){
			File tempFile = new File(tempOutputDir, tempFilePrefix+".contig"+contigId);
			try {
				currentWriter = new PrintWriter(tempFile);
				currentWriter.print(currentLine);
			} catch (IOException e) {
				throw new IllegalStateException("error writing temp file");
			}			
		}
		return BeginContigReturnCode.SKIP_CURRENT_CONTIG;
	}

	@Override
	public void visitConsensusQualities(QualitySequence ungappedConsensusQualities) {
		
	}

	@Override
	public void visitAlignedReadInfo(String readId, Direction dir,
			int gappedStartOffset) {
		
	}

	@Override
	public void visitBaseSegment(Range gappedConsensusRange, String readId) {
		
	}

	@Override
	public BeginReadReturnCode visitBeginRead(String readId, int gappedLength) {
		return BeginReadReturnCode.VISIT_CURRENT_READ;
	}

	@Override
	public void visitQualityLine(int qualLeft, int qualRight, int alignLeft,
			int alignRight) {
		
	}

	@Override
	public void visitTraceDescriptionLine(String traceName, String phdName,
			Date date) {
		
	}

	@Override
	public void visitBasesLine(String mixedCaseBasecalls) {
		
	}

	@Override
	public void visitReadTag(String id, String type, String creator,
			long gappedStart, long gappedEnd, Date creationDate,
			boolean isTransient) {
	}

	@Override
	public EndContigReturnCode visitEndOfContig() {
		if(streamThruCurrentContig){
			currentWriter.close();
		}
		
		return EndContigReturnCode.KEEP_PARSING;
	}

	@Override
	public void visitBeginConsensusTag(String id, String type, String creator,
			long gappedStart, long gappedEnd, Date creationDate,
			boolean isTransient) {
	
	}

	@Override
	public void visitConsensusTagComment(String comment) {
		
	}

	@Override
	public void visitConsensusTagData(String data) {		
	}

	@Override
	public void visitEndConsensusTag() {
		
	}

	@Override
	public void visitWholeAssemblyTag(String type, String creator,
			Date creationDate, String data) {		
	}

}
