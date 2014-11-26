/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.transform;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.AssemblyTransformationService;
import org.jcvi.jillion.assembly.AssemblyTransformer;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.assembly.consed.ConsedUtil;
import org.jcvi.jillion.assembly.consed.ConsedUtil.ClipPointsType;
import org.jcvi.jillion.assembly.consed.ace.AbstractAceFileVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceContigReadVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceContigVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceFileParser;
import org.jcvi.jillion.assembly.consed.ace.AceFileVisitorCallback;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public class ConsedTransformationService implements AssemblyTransformationService{

	File consedDir;
	File aceFile;
	private final PhdDataStore phdDataStore;
	
	private ConsedTransformationService(Builder builder) throws IOException{
		consedDir = builder.consedDir;
		if(builder.includeQualities){
			
			phdDataStore = ConsedUtil.createPhdDataStoreFor(consedDir);
		}else{
			phdDataStore = null;
		}
		File editDir = ConsedUtil.getEditDirFor(consedDir);
		if(builder.aceVersion ==null){
			//get latest ace			
			aceFile = ConsedUtil.getLatestAceFile(editDir, builder.acePrefix);
		}else{
			aceFile = ConsedUtil.getAceFile(editDir, builder.acePrefix, builder.aceVersion.intValue());
		}
		if(aceFile == null){
			throw new IOException("specified ace file not found in edit_dir : " + editDir.getAbsolutePath());
		}
	}
	
	@Override
	public void transform(final AssemblyTransformer transformer) throws IOException {
		if(transformer ==null){
			throw new NullPointerException("transformer can not be null");
		}
		//TODO implement!!
		AceFileParser.create(aceFile)
						.parse(new AbstractAceFileVisitor() {

							@Override
							public AceContigVisitor visitContig(
									AceFileVisitorCallback callback,
									String contigId, int consensusLength,
									int numberOfReads,
									int numberOfBaseSegments,
									boolean reverseComplemented) {
								return new AceContigVisitor() {
									NucleotideSequenceBuilder consensusBuilder = new NucleotideSequenceBuilder();
									boolean madeReferenceCallYet = false;
									
									Map<String, Integer> readStarts = new HashMap<>();
									Map<String, Direction> readDirs = new HashMap<>();
									
									@Override
									public void visitEnd() {
										if(!madeReferenceCallYet){
											makeReferenceCall();
										}
										
									}
									
									@Override
									public void visitConsensusQualities(
											QualitySequence ungappedConsensusQualities) {
										//no-op
										
									}
									
									@Override
									public AceContigReadVisitor visitBeginRead(String readId, int gappedLength) {
										if(!madeReferenceCallYet){
											makeReferenceCall();
										}
										return new AceContigReadVisitor() {
											NucleotideSequenceBuilder fullLengthReadBuilder = new NucleotideSequenceBuilder();
											Range gappedValidRange = null;
											
											@Override
											public void visitTraceDescriptionLine(String traceName, String phdName,
													Date date) {
												//no-op
												
											}
											
											@Override
											public void visitQualityLine(int qualLeft, int qualRight, int alignLeft,
													int alignRight) {
												ClipPointsType clipPointsType = ConsedUtil.ClipPointsType.getType(qualLeft, qualRight, alignLeft, alignRight);
												if(clipPointsType != ClipPointsType.VALID){
													return;
												}
												//dkatzel 4/2011 - There have been cases when qual coords and align coords
										        //do not match; usually qual is a sub set of align
										        //but occasionally, qual goes beyond the align coords.
										        //I guess this happens in a referenced based alignment for
										        //reads at the edges when the reads have good quality 
										        //beyond the reference.
										        //It might also be possible that the read has been 
										        //edited and that could have changed the coordinates.
										        //Therefore intersect the qual and align coords
										        //to find the region we are interested in
										        Range qualityRange = Range.of(CoordinateSystem.RESIDUE_BASED, qualLeft,qualRight);
										        Range alignmentRange = Range.of(CoordinateSystem.RESIDUE_BASED, alignLeft,alignRight);
										        gappedValidRange =qualityRange.intersection(alignmentRange);
										        
										       
											}
											
											@Override
											public void visitEnd() {
												
												NucleotideSequence gappedValidRangeSequence = fullLengthReadBuilder.copy()
																								.trim(gappedValidRange)
																								.build();
												//TODO make this more efficient.
												//currently have to create NucleteotideSequence just
												//to convert from gapped to ungapped coords
												NucleotideSequence gappedFullLengthSequence = fullLengthReadBuilder.build();
												
												NucleotideSequence ungappedFullLengthSequence = fullLengthReadBuilder.ungap().build();
												
												
												transformer.aligned(readId, 
														//TODO add support for phd ball?
														ungappedFullLengthSequence, null, null, 
														null, contigId, 
														readStarts.get(readId), readDirs.get(readId), 
														gappedValidRangeSequence, 
														new ReadInfo(AssemblyUtil.toUngappedRange(gappedFullLengthSequence, gappedValidRange),
																(int)ungappedFullLengthSequence.getLength()));
												
											}
											
											@Override
											public void visitBasesLine(String mixedCaseBasecalls) {
												fullLengthReadBuilder.append(mixedCaseBasecalls);
												
											}
											
											@Override
											public void halted() {
												//no-op
												
											}
										};
									}
									
									private void makeReferenceCall() {
										madeReferenceCallYet =true;
										//TODO do we have to worry about contigLeft and right?
										transformer.referenceOrConsensus(contigId, consensusBuilder.build());
										
									}

									@Override
									public void visitBasesLine(String mixedCaseBasecalls) {
										consensusBuilder.append(mixedCaseBasecalls);
										
									}
									
									@Override
									public void visitBaseSegment(Range gappedConsensusRange, String readId) {
										//no-op
										
									}
									
									@Override
									public void visitAlignedReadInfo(String readId, Direction dir,
											int gappedStartPosition) {
										//no-op
										readStarts.put(readId, gappedStartPosition -1);
										readDirs.put(readId, dir);
									}
									
									@Override
									public void halted() {
										//no-op
										
									}
								};
							}

							@Override
							public void visitEnd() {
								transformer.endAssembly();
							}
							
						});
	}

	public static class Builder{
		private final File consedDir;
		private final File editDir;
		private final String acePrefix;
		
		private Integer aceVersion =null;
		private boolean includeQualities = false;
		
		
		public Builder(File consedDir, String acePrefix) {
			if(!consedDir.exists()){
				throw new IllegalArgumentException("consed directory does not exist : "+ consedDir.getAbsolutePath());
			}
			if(acePrefix == null){
				throw new NullPointerException("ace prefix can not be null");
			}
			editDir = ConsedUtil.getEditDirFor(consedDir);
			if(!editDir.exists()){
				throw new IllegalStateException("edit_dir does not exist : " + consedDir.getAbsolutePath());
			}
			this.consedDir = consedDir;
			this.acePrefix = acePrefix;
		}
		
		public Builder includeQualities(boolean includeQualities){
			this.includeQualities = includeQualities;
			return this;
		}
		
		public Builder useAceVersion(int version){			
			File ace = ConsedUtil.getAceFile(editDir, acePrefix, version);
			if(ace ==null){
				throw new IllegalArgumentException("ace file with version " + version + " does not exist in " + editDir.getAbsolutePath());
			}
			this.aceVersion = version;
			return this;
		}
		
		public ConsedTransformationService build() throws IOException{
			return new ConsedTransformationService(this);
		}
		
		
	}



}
