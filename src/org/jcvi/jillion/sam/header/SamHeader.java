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
package org.jcvi.jillion.sam.header;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamValidationException;
import org.jcvi.jillion.sam.SortOrder;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
/**
 * {@code SamHeader}
 * is an object representation of
 * the header of a SAM or BAM file.
 * @author dkatzel
 *
 */
public final class SamHeader {

	private final SortOrder sortOrder;
	private final SamVersion version;
	
	private final Map<String, ReferenceSequence> referenceSequences;
	private final Map<String, Integer> referenceIndexMap;
	
	private final Map<Integer, ReferenceSequence> indexReferenceMap;
	
	private final Map<String, ReadGroup> readGroups;
	
	private final Map<String, SamProgram> programs;
	
	private final List<String> comments;
	
	
	private SamHeader(Builder builder){
		this.sortOrder = builder.sortOrder;
		this.version =  builder.version;
		this.referenceSequences =  Collections.unmodifiableMap(new LinkedHashMap<String, ReferenceSequence>(builder.referenceSequences));
	
		this.readGroups =  Collections.unmodifiableMap(new HashMap<String, ReadGroup>(builder.readGroups));
		this.programs =  Collections.unmodifiableMap(new HashMap<String, SamProgram>(builder.programs));
		
		this.comments = Collections.unmodifiableList(new ArrayList<String>(builder.comments));
		referenceIndexMap = new LinkedHashMap<String, Integer>();
		indexReferenceMap = new LinkedHashMap<Integer, ReferenceSequence>();
		
		int i=0;
		for(ReferenceSequence refSeq : referenceSequences.values()){
			Integer index = Integer.valueOf(i);
			referenceIndexMap.put(refSeq.getName(), index);
			indexReferenceMap.put(index, refSeq);
			i++;
		}
	}
	
	
	
	@Override
	public String toString() {
		return "SamHeader [sortOrder=" + sortOrder + ", version=" + version
				+ ", referenceSequences=" + referenceSequences
				+ ", referenceIndexMap=" + referenceIndexMap + ", readGroups="
				+ readGroups + ", programs=" + programs + ", comments="
				+ comments + "]";
	}



	public SortOrder getSortOrder() {
		return sortOrder;
	}



	public SamVersion getVersion() {
		return version;
	}

	public boolean hasReferenceSequence(String name){
		return referenceSequences.containsKey(name);
	}

	public ReferenceSequence getReferenceSequence(String name){
		return referenceSequences.get(name);
	}
	
	public Iterator<ReferenceSequence> getReferenceSequencesIterator(){
		return referenceSequences.values().iterator();
	}
	

	public boolean hasSamProgram(String id){
		return programs.containsKey(id);
	}

	public SamProgram getSamProgram(String id){
		return programs.get(id);
	}

	public boolean hasReadGroup(String id){
		return readGroups.containsKey(id);
	}

	public ReadGroup getReadGroup(String id){
		return readGroups.get(id);
	}
	public Collection<ReadGroup> getReadGroups(){
		return readGroups.values();
	}
	public Collection<SamProgram> getPrograms(){
		return programs.values();
	}
	public Collection<ReferenceSequence> getReferenceSequences(){
		return referenceSequences.values();
	}

	



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((comments == null) ? 0 : comments.hashCode());
		result = prime * result
				+ ((programs == null) ? 0 : programs.hashCode());
		result = prime * result
				+ ((readGroups == null) ? 0 : readGroups.hashCode());
		result = prime
				* result
				+ ((referenceSequences == null) ? 0 : referenceSequences
						.hashCode());
		result = prime * result
				+ ((sortOrder == null) ? 0 : sortOrder.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SamHeader)) {
			return false;
		}
		SamHeader other = (SamHeader) obj;
		if (comments == null) {
			if (other.comments != null) {
				return false;
			}
		} else if (!comments.equals(other.comments)) {
			return false;
		}
		if (programs == null) {
			if (other.programs != null) {
				return false;
			}
		} else if (!programs.equals(other.programs)) {
			return false;
		}
		if (readGroups == null) {
			if (other.readGroups != null) {
				return false;
			}
		} else if (!readGroups.equals(other.readGroups)) {
			return false;
		}
		if (referenceSequences == null) {
			if (other.referenceSequences != null) {
				return false;
			}
		} else if (!referenceSequences.equals(other.referenceSequences)) {
			return false;
		}
		if (sortOrder != other.sortOrder) {
			return false;
		}
		if (version == null) {
			if (other.version != null) {
				return false;
			}
		} else if (!version.equals(other.version)) {
			return false;
		}
		return true;
	}



	public List<String> getComments() {
		return comments;
	}


	/**
	 * {@code Builder}
	 * is the builder class used to construct
	 * a {@link SamHeader}
	 * @author dkatzel
	 *
	 */
	public static final class Builder{
		//default to unknown if not specified
		private SortOrder sortOrder = SortOrder.UNKNOWN;
		private SamVersion version;
		
		private final Map<String, ReferenceSequence> referenceSequences = new LinkedHashMap<String, ReferenceSequence>();
		
		private final Map<String, ReadGroup> readGroups = new HashMap<String, ReadGroup>();
		
		private final Map<String, SamProgram> programs = new HashMap<String, SamProgram>();
		
		private final List<String> comments = new ArrayList<String>();
		
		/**
		 * Create a new Builder instance with 
		 * the default values
		 * of "unknown" sort order
		 * and nothing or empty for everything else.
		 * 
		 * Use the methods on the builder
		 * to add/set values for the header.
		 */
		public Builder(){
			//use defaults
		}
		
		public Builder(SamHeader copy) {
			this.sortOrder = copy.getSortOrder();
			this.version = copy.getVersion();
			for(ReferenceSequence ref :copy.getReferenceSequences()){
				referenceSequences.put(ref.getName(), ref);
			}
			for(ReadGroup readGroup :copy.getReadGroups()){
				readGroups.put(readGroup.getId(), readGroup);
			}
			for(SamProgram prog :copy.getPrograms()){
				programs.put(prog.getId(), prog);
			}
			comments.addAll(copy.getComments());
		}

		public Builder setVersion(SamVersion version){
			this.version = version;
			return this;
		}
		/**
		 * Add a {@link ReferenceSequence}
		 * to this header.  The order that the references are added
		 * defines the alignment sorting order
		 * for some {@link SortOrder} implementations.
		 * @param refSeq the {@link ReferenceSequence} to add;
		 * can not be null.
		 * @return this
		 * @throws NullPointerException if refSeq is null.
		 * @throws IllegalArgumentException if a {@link ReferenceSequence}
		 * with the same name has already been added to this builder.
		 */
		public Builder addReferenceSequence(ReferenceSequence refSeq){
			if(refSeq ==null){
				throw new NullPointerException("reference sequence can not be null");
			}
			if(referenceSequences.containsKey(refSeq.getName())){
				throw new IllegalArgumentException("reference sequence with same name already in header : "+ refSeq.getName());
			}
			referenceSequences.put(refSeq.getName(), refSeq);
			
			return this;
		}
		
		/**
		 * Add a {@link ReadGroup}
		 * to this header. Each record added
		 * must have a unique Id.
		 * @param readGroup the {@link ReadGroup} to add;
		 * can not be null.
		 * @return this
		 * @throws NullPointerException if readGroup is null.
		 * @throws IllegalArgumentException if a {@link ReadGroup}
		 * with the same id has already been added to this builder.
		 */
		public Builder addReadGroup(ReadGroup readGroup){
			if(readGroup ==null){
				throw new NullPointerException("read group can not be null");
			}
			if(readGroups.containsKey(readGroup.getId())){
				throw new IllegalArgumentException("read group with same id already in header : "+ readGroup.getId());
			}
			readGroups.put(readGroup.getId(), readGroup);
			
			return this;
		}
		/**
		 * Add a {@link SamProgram}
		 * to this header. Each record added
		 * must have a unique Id.
		 * @param program the {@link SamProgram} to add;
		 * can not be null.
		 * @return this
		 * @throws NullPointerException if program is null.
		 * @throws IllegalArgumentException if a {@link SamProgram}
		 * with the same id has already been added to this builder.
		 */
		public Builder addProgram(SamProgram program){
			if(program ==null){
				throw new NullPointerException("program can not be null");
			}
			if(programs.containsKey(program.getId())){
				throw new IllegalArgumentException("program with same id already in header : "+ program.getId());
			}
			programs.put(program.getId(), program);
			
			return this;
		}
		/**
		 * Add a comment line to the list of comments.
		 * @param commentLine the comment line to add;
		 * can not be null.
		 * @return this.
		 * @throws NullPointerException if commentLine is null.
		 */
		public Builder addComment(String commentLine){
			if(commentLine ==null){
				throw new NullPointerException("commentLine can not be null");
			}
			comments.add(commentLine);
			return this;
		}
		/**
		 * Set the {@link SortOrder}.
		 * @param order the SortOrder;
		 * if this value is set to {@code null}
		 * then the value will be changed to
		 * {@link SortOrder#UNKNOWN}.
		 * @return this.
		 */
		public Builder setSortOrder(SortOrder order){
			if(order ==null){
				this.sortOrder = SortOrder.UNKNOWN;
			}else{
				this.sortOrder = order;
			}
			return this;
			
		}
		/**
		 * Build a new SamHeader.
		 * @return
		 * @throws IllegalStateException if a {@link SamProgram}'s
		 * previous program id is not also included in this header.
		 */
		public SamHeader build(){
			//confirm all program's previous are known?
			for(SamProgram program : programs.values()){
				String prevId = program.getPreviousProgramId();
				if(prevId !=null && !programs.containsKey(prevId)){						
					throw new IllegalStateException("known previous program " + prevId + "referenced in " + program.getId());
				}
			}
			return new SamHeader(this);
		}

		public boolean hasReferenceSequence(String name) {
			return referenceSequences.containsKey(name);
		}

		public SortOrder getSortOrder() {
			return sortOrder;
		}
	}


	public int getReferenceIndexFor(String referenceName) {
		Integer ret= referenceIndexMap.get(referenceName);
		if(ret==null){
			return -1;
		}
		return ret.intValue();
		
	}
	

	public ReferenceSequence getReferenceSequence(int i) {
		return indexReferenceMap.get(Integer.valueOf(i));
		
	}
	
	public void validRecord(SamRecord record, SamAttributeValidator attributeValidator) throws SamValidationException{
		//reference names must be present in a SQ-SN tag
		String refName =record.getReferenceName();
		if(refName !=null && !this.hasReferenceSequence(refName)){
			throw new SamValidationException("unknown reference "+ refName);
		}
		String nextRefName = record.getNextName();
		if(nextRefName !=null &&  !"=".equals(nextRefName) && !this.hasReferenceSequence(nextRefName)){
			throw new SamValidationException("unknown next reference "+ nextRefName);
		}
		for(SamAttribute attribute : record.getAttributes()){			
				attributeValidator.validate(this, attribute);
			
		}
		
	}
	
	public Comparator<SamRecord> createRecordComparator(){
		if(sortOrder ==null){
			return null;
		}
		return sortOrder.createComparator(this);
	}



}
