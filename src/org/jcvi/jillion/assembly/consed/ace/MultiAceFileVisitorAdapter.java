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
package org.jcvi.jillion.assembly.consed.ace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;

public class MultiAceFileVisitorAdapter implements AceFileVisitor{

	private final List<AceFileVisitor> visitors;
	
	private List<AceContigVisitor> currentContigVisitors;
	private List<AceContigReadVisitor> currentReadVisitors;
	private List<AceConsensusTagVisitor> currentConsensusTagVisitors;
	
	private List<AceFileVisitorCallbackAdapter> currentCallbacks;
	
	private AceFileVisitorCallback ourCallback;
	public MultiAceFileVisitorAdapter(AceFileVisitor...visitors){
		this(Arrays.asList(visitors));
	}
	public MultiAceFileVisitorAdapter(Collection<? extends AceFileVisitor> visitors) {
		if(visitors.isEmpty()){
			throw new IllegalArgumentException("must provide at least one visitor");
		}
		this.visitors = new ArrayList<AceFileVisitor>(visitors.size());
		for(AceFileVisitor visitor : visitors){
			if(visitor ==null){
				throw new NullPointerException("visitor can not be null");
			}
			this.visitors.add(visitor);
		}
	}

	private boolean handleHaltedVisitors(){
		if(ourCallback ==null){
			//no callbacks do nothing
			return false;
		}
		//work backwards to avoid concurrent modification exception
		for(int i=currentCallbacks.size(); i>=0; i--){
			if(currentCallbacks.get(i).isHalted()){
				if(currentReadVisitors !=null){
					//visitor lists should all be the same size
					//so indexes are in sync
					AceContigReadVisitor readVisitor =currentReadVisitors.remove(i);
					if(readVisitor!=null){
						readVisitor.halted();
					}					
				}
				if(currentContigVisitors !=null){
					AceContigVisitor contigVisitor = currentContigVisitors.remove(i);
					if(contigVisitor !=null){
						contigVisitor.halted();
					}
				}
				if(currentConsensusTagVisitors !=null){
					AceConsensusTagVisitor tagVisitor = currentConsensusTagVisitors.remove(i);
					if(tagVisitor !=null){
						tagVisitor.halted();
					}
				}
				
				AceFileVisitor visitor = visitors.remove(i);
				visitor.halted();
				currentCallbacks.remove(i);
			}
		}
		if(currentCallbacks.isEmpty()){
			//all callbacks have been halted
			ourCallback.haltParsing();
			ourCallback=null;
			currentReadVisitors=null;
			currentContigVisitors = null;
			currentConsensusTagVisitors =null;
			return true;
		}
		return false;
	}
	@Override
	public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
		for(AceFileVisitor visitor : visitors){
			visitor.visitHeader(numberOfContigs, totalNumberOfReads);
			
		}
	}

	@Override
	public AceContigVisitor visitContig(AceFileVisitorCallback callback,
			String contigId, int numberOfBases, int numberOfReads,
			int numberOfBaseSegments, boolean reverseComplemented) {
		this.ourCallback = callback;
		currentCallbacks = new ArrayList<AceFileVisitorCallbackAdapter>(visitors.size());
		currentContigVisitors = new ArrayList<AceContigVisitor>(visitors.size());
		boolean skipContig = true;
		for(AceFileVisitor visitor : visitors){
			AceFileVisitorCallbackAdapter adaptedCallback = new AceFileVisitorCallbackAdapter(callback);
			currentCallbacks.add(adaptedCallback);
			AceContigVisitor contigVisitor = visitor.visitContig(adaptedCallback, contigId, numberOfBases, numberOfReads, numberOfBaseSegments, reverseComplemented);
			currentContigVisitors.add(contigVisitor);
			if(contigVisitor !=null){
				skipContig=false;
			}
		}
		if(handleHaltedVisitors()){
			return null;
		}
		//if none of our visitors want to
		//visit, then we can safely skip it too.
		if(skipContig){
			currentContigVisitors=null;
			return null;
		}
		return new MultiAceContigVisitor();
	}

	@Override
	public void visitReadTag(String id, String type, String creator,
			long gappedStart, long gappedEnd, Date creationDate,
			boolean isTransient) {
		for(AceFileVisitor visitor : visitors){
			visitor.visitReadTag(id, type, creator, 
				gappedStart, gappedEnd, creationDate, 
				isTransient);
			
		}
		
	}

	@Override
	public AceConsensusTagVisitor visitConsensusTag(String id, String type,
			String creator, long gappedStart, long gappedEnd,
			Date creationDate, boolean isTransient) {
		currentConsensusTagVisitors = new ArrayList<AceConsensusTagVisitor>(visitors.size());
		boolean skipTag=true;
		for(AceFileVisitor visitor : visitors){
			AceConsensusTagVisitor tagVisitor = visitor.visitConsensusTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
			currentConsensusTagVisitors.add(tagVisitor);
			if(tagVisitor !=null){
				skipTag =true;
			}
		}
		if(handleHaltedVisitors() || skipTag){
			currentConsensusTagVisitors=null;
			return null;
		}
		
		return new MultiAceConsensusTagVisitor();
	}

	@Override
	public void visitWholeAssemblyTag(String type, String creator,
			Date creationDate, String data) {
		for(AceFileVisitor visitor : visitors){
			visitor.visitWholeAssemblyTag(type, creator, creationDate, data);
		}
		
	}

	@Override
	public void visitEnd() {
		for(AceFileVisitor visitor : visitors){
			visitor.visitEnd();
		}
		
	}

	@Override
	public void halted() {
		//all visitors should have been
		//removed by the time we call this
		//but just in case:
		if(currentReadVisitors !=null){
			for(AceContigReadVisitor readVisitor : currentReadVisitors){
				readVisitor.halted();
			}
		}
		if(currentContigVisitors !=null){
			for(AceContigVisitor contigVisitor : currentContigVisitors){
				contigVisitor.halted();
			}
		}
		if(currentConsensusTagVisitors !=null){
			for(AceConsensusTagVisitor tagVisitor : currentConsensusTagVisitors){
				tagVisitor.halted();
			}
		}
		for(AceFileVisitor visitor : visitors){
			visitor.halted();
		}
		
	}
	
	private class MultiAceConsensusTagVisitor implements AceConsensusTagVisitor{

		@Override
		public void visitComment(String comment) {
			for(AceConsensusTagVisitor visitor : currentConsensusTagVisitors){
				if(visitor !=null){
					visitor.visitComment(comment);
				}
			}
			handleHaltedVisitors();
		}

		@Override
		public void visitData(String data) {
			for(AceConsensusTagVisitor visitor : currentConsensusTagVisitors){
				if(visitor !=null){
					visitor.visitData(data);
				}
			}
			handleHaltedVisitors();
			
		}

		@Override
		public void visitEnd() {
			for(AceConsensusTagVisitor visitor : currentConsensusTagVisitors){
				if(visitor !=null){
					visitor.visitEnd();
				}
			}
			handleHaltedVisitors();
			
		}

		@Override
		public void halted() {
			//no-op?
			//handled by outer visitor
			
		}
		
	}
	
	private class MultiAceContigVisitor implements AceContigVisitor{
		
		
		@Override
		public void visitBasesLine(String mixedCaseBasecalls) {
			for(AceContigVisitor visitor : currentContigVisitors){
				if(visitor !=null){
					visitor.visitBasesLine(mixedCaseBasecalls);
				}
			}
			handleHaltedVisitors();
			
		}
		@Override
		public void visitConsensusQualities(
				QualitySequence ungappedConsensusQualities) {
			for(AceContigVisitor visitor : currentContigVisitors){
				if(visitor !=null){
					visitor.visitConsensusQualities(ungappedConsensusQualities);
				}				
			}
			handleHaltedVisitors();
			
		}
		@Override
		public void visitAlignedReadInfo(String readId, Direction dir,
				int gappedStartPosition) {
			for(AceContigVisitor visitor : currentContigVisitors){
				if(visitor !=null){
					visitor.visitAlignedReadInfo(readId, dir, gappedStartPosition);
				}
			}
			handleHaltedVisitors();
			
		}
		@Override
		public void visitBaseSegment(Range gappedConsensusRange, String readId) {
			for(AceContigVisitor visitor : currentContigVisitors){
				if(visitor !=null){
					visitor.visitBaseSegment(gappedConsensusRange, readId);
				}
			}
			handleHaltedVisitors();
			
		}
		@Override
		public AceContigReadVisitor visitBeginRead(String readId,
				int gappedLength) {
			currentReadVisitors = new ArrayList<AceContigReadVisitor>(currentContigVisitors.size());
			boolean skipRead=true;
			for(AceContigVisitor visitor : currentContigVisitors){
				if(visitor ==null){
					currentReadVisitors.add(null);
				}else{
					AceContigReadVisitor readVisitor = visitor.visitBeginRead(readId, gappedLength);
					currentReadVisitors.add(readVisitor);
					if(readVisitor !=null){
						skipRead=false;
					}
				}
			}
			if(handleHaltedVisitors()){
				return null;
			}
			if(skipRead){
				currentReadVisitors= null;
				return null;
			}
			return new MultiAceContigReadVisitor();
		}
		@Override
		public void visitEnd() {			
			for(AceContigVisitor visitor : currentContigVisitors){
				if(visitor !=null){
					visitor.visitEnd();
				}
			}
			//we are done visiting our current contigs
			//so remove them 
			currentContigVisitors = null;
			handleHaltedVisitors();
			
		}
		@Override
		public void halted() {
			//no-op?
			//the outer visitor should handle halting
			
		}		
	}
	
	private class MultiAceContigReadVisitor implements AceContigReadVisitor{

		@Override
		public void visitQualityLine(int qualLeft, int qualRight,
				int alignLeft, int alignRight) {
			for(AceContigReadVisitor visitor : currentReadVisitors){
				if(visitor !=null){
					visitor.visitQualityLine(qualLeft, qualRight, alignLeft, alignRight);
				}
			}
			handleHaltedVisitors();
		}

		@Override
		public void visitTraceDescriptionLine(String traceName, String phdName,
				Date date) {
			for(AceContigReadVisitor visitor : currentReadVisitors){
				if(visitor !=null){
					visitor.visitTraceDescriptionLine(traceName, phdName, date);
				}
			}
			handleHaltedVisitors();
			
		}

		@Override
		public void visitBasesLine(String mixedCaseBasecalls) {
			for(AceContigReadVisitor visitor : currentReadVisitors){
				if(visitor !=null){
					visitor.visitBasesLine(mixedCaseBasecalls);
				}
			}
			handleHaltedVisitors();			
		}

		@Override
		public void visitEnd() {
			for(AceContigReadVisitor visitor : currentReadVisitors){
				if(visitor !=null){
					visitor.visitEnd();
				}
			}
			handleHaltedVisitors();
			currentReadVisitors = null;
		}

		@Override
		public void halted() {
			//no-op?
			//the outer visitor should handle halting
		}
		
	}
	
	
	private static class AceFileVisitorCallbackAdapter implements AceFileVisitorCallback{
		private final AceFileVisitorCallback callback;
		private volatile boolean halt;
		
		public AceFileVisitorCallbackAdapter(
				AceFileVisitorCallback callback) {
			this.callback = callback;
		}

		@Override
		public boolean canCreateMemento() {
			return callback.canCreateMemento();
		}

		@Override
		public AceFileVisitorMemento createMemento() {
			return callback.createMemento();
		}

		@Override
		public void haltParsing() {
			halt = true;			
		}

		public boolean isHalted() {
			return halt;
		}
		
		
	}
}
