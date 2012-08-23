package org.jcvi.assembly.ace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Set;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.AceContigDataStoreBuilder;
import org.jcvi.common.core.assembly.ace.AceFileVisitor;

class ReAbacusAceFileVisitor implements AceFileVisitor{

	private final AceContigDataStoreBuilder reAbacusDataStoreBuilder;
	private final File tempOutputDir;
	private final String tempFilePrefix;
	private final Set<String> idsToReAbacus;
	
	private boolean streamThruCurrentContig=false;
	private PrintWriter currentWriter;
	private String currentLine;
	public ReAbacusAceFileVisitor(
			AceContigDataStoreBuilder reAbacusDataStoreBuilder,
			Set<String> idsToReAbacus,
			File tempOutputDir, String tempFilePrefix) {
		this.reAbacusDataStoreBuilder = reAbacusDataStoreBuilder;
		this.tempOutputDir = tempOutputDir;
		this.tempFilePrefix = tempFilePrefix;
		this.idsToReAbacus = idsToReAbacus;
	}

	@Override
	public void visitLine(String line) {
		//always visit lines in case
		//the builder needs it (like for indexed implementations)
		reAbacusDataStoreBuilder.visitLine(line);
		if(streamThruCurrentContig){
			currentWriter.print(line);
		}
		currentLine=line;
	}

	@Override
	public void visitFile() {
		reAbacusDataStoreBuilder.visitFile();
		
	}

	@Override
	public void visitEndOfFile() {
		reAbacusDataStoreBuilder.visitEndOfFile();
		
	}

	@Override
	public void visitHeader(int numberOfContigs, int totalNumberOfReads) {
		//ignore
		
	}

	@Override
	public boolean shouldVisitContig(String contigId, int numberOfBases,
			int numberOfReads, int numberOfBaseSegments, boolean isComplemented) {
		streamThruCurrentContig = !idsToReAbacus.contains(contigId);
		
		if(streamThruCurrentContig){
			File tempFile = new File(tempOutputDir, tempFilePrefix+".contig"+contigId);
			try {
				currentWriter = new PrintWriter(tempFile);
				currentWriter.print(currentLine);
			} catch (IOException e) {
				throw new IllegalStateException("error writing temp file");
			}
			return false;
		}
		//we aren't streaming so delegate to our builder
		return reAbacusDataStoreBuilder.shouldVisitContig(contigId,
															numberOfBases, 
															numberOfReads, 
															numberOfBaseSegments, 
															isComplemented);
	}

	@Override
	public void visitBeginContig(String contigId, int numberOfBases,
			int numberOfReads, int numberOfBaseSegments,
			boolean reverseComplemented) {
		//can only be called if our builder cares
		reAbacusDataStoreBuilder.visitBeginContig(contigId, numberOfBases, numberOfReads, numberOfBaseSegments, reverseComplemented);
		
	}

	@Override
	public void visitConsensusQualities() {
		//only called if our builder cares
		reAbacusDataStoreBuilder.visitConsensusQualities();
		
	}

	@Override
	public void visitAssembledFromLine(String readId, Direction dir,
			int gappedStartOffset) {
		reAbacusDataStoreBuilder.visitAssembledFromLine(readId, dir, gappedStartOffset);
		
	}

	@Override
	public void visitBaseSegment(Range gappedConsensusRange, String readId) {
		reAbacusDataStoreBuilder.visitBaseSegment(gappedConsensusRange, readId);
		
	}

	@Override
	public void visitReadHeader(String readId, int gappedLength) {
		reAbacusDataStoreBuilder.visitReadHeader(readId, gappedLength);		
	}

	@Override
	public void visitQualityLine(int qualLeft, int qualRight, int alignLeft,
			int alignRight) {
		reAbacusDataStoreBuilder.visitQualityLine(qualLeft, qualRight, alignLeft, alignRight);
		
	}

	@Override
	public void visitTraceDescriptionLine(String traceName, String phdName,
			Date date) {
		reAbacusDataStoreBuilder.visitTraceDescriptionLine(traceName, phdName, date);
		
	}

	@Override
	public void visitBasesLine(String mixedCaseBasecalls) {
		reAbacusDataStoreBuilder.visitBasesLine(mixedCaseBasecalls);
		
	}

	@Override
	public void visitReadTag(String id, String type, String creator,
			long gappedStart, long gappedEnd, Date creationDate,
			boolean isTransient) {
		reAbacusDataStoreBuilder.visitReadTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
		
	}

	@Override
	public boolean visitEndOfContig() {
		if(streamThruCurrentContig){
			currentWriter.close();
		}
		reAbacusDataStoreBuilder.visitEndOfContig();
		
		return true;
	}

	@Override
	public void visitBeginConsensusTag(String id, String type, String creator,
			long gappedStart, long gappedEnd, Date creationDate,
			boolean isTransient) {
		reAbacusDataStoreBuilder.visitBeginConsensusTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
		
	}

	@Override
	public void visitConsensusTagComment(String comment) {
		reAbacusDataStoreBuilder.visitConsensusTagComment(comment);
		
	}

	@Override
	public void visitConsensusTagData(String data) {
		reAbacusDataStoreBuilder.visitConsensusTagData(data);		
	}

	@Override
	public void visitEndConsensusTag() {
		reAbacusDataStoreBuilder.visitEndConsensusTag();
		
	}

	@Override
	public void visitWholeAssemblyTag(String type, String creator,
			Date creationDate, String data) {
		reAbacusDataStoreBuilder.visitWholeAssemblyTag(type, creator, creationDate, data);		
	}

}
