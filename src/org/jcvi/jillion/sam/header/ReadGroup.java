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

import java.util.Date;
import java.util.Locale;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * {@code ReadGroup} is a way
 * to group reads as belonging to a particular
 * set.  Often Read Groups are used to note
 * which reads came from which sequencing run
 * or pool.
 * @author dkatzel
 *
 */
public class ReadGroup {

	private final String id;
	private final String sequencingCenter;
	private final String description;
	private final String library;
	private final String programs;
	private final String platformUnit;
	private final String sampleOrPoolName;
	
	private final Integer predictedMedianInsertSize;
	private final PlatformTechnology platform;
	
	private final Long datetime;
	
	private final NucleotideSequence keySequence, flowOrder;
	
	private ReadGroup(Builder builder){
		this.id = builder.id;
		this.sequencingCenter = builder.sequencingCenter;
		this.description = builder.description;
		this.library = builder.library;
		this.programs = builder.programsUsed;
		this.platformUnit = builder.platformUnit;
		this.sampleOrPoolName = builder.sampleOrPoolName;
		this.predictedMedianInsertSize = builder.predictedInsertSize;
		this.platform = builder.platform;
		this.datetime = builder.datetime;
		this.keySequence = builder.keySequence;
		this.flowOrder = builder.flowOrder;
				
	}
	/**
	 * The predicted median insert size.
	 * @return the median insert size as an Integer;
	 * or {@code null} if not specified.
	 */
	public Integer getPredictedInsertSize() {
		return predictedMedianInsertSize;
	}
	/**
	 * Get the platform/technology used to produce the reads
	 * in this read group.
	 * @return a {@link PlatformTechnology} or {@code null}
	 * if not specified.
	 */
	public PlatformTechnology getPlatform() {
		return platform;
	}
	/**
	 * Get the unique ID. The value of ID is used in the
	 * {@link org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys#READ_GROUP}
	 * tag.
	 * 
	 * @return a String; will never be null.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get the unique Platform unit 
	 * (e.g. flowcell-barcode.lane for Illumina or slide for SOLiD).
	 * @return the the platform unit as a String;
	 * may be {@code null} if not provided.
	 */
	public String getPlatformUnit() {
		return platformUnit;
	}

	/**
	 * Get the key sequence for each read group
	 * (454 and iontorrent only).
	 * @return the {@link NucleotideSequence}
	 * representing the keysequence or
	 * {@code null} if the keysequence is not specified
	 * or is not used by this read group.
	 */
	public NucleotideSequence getKeySequence(){
		return keySequence;
	}
	
	/**
	 * Get the flow order for each read group
	 * (454 and iontorrent only).
	 * @return the {@link NucleotideSequence}
	 * representing the floworder or
	 * {@code null} if the floworder is not specified
	 * or is not used by this read group.
	 */
	public NucleotideSequence getFlowOrder(){
		return flowOrder;
	}

	/**
	 * Get the sequencingCenter that produced
	 * this read group.
	 * @return the sequencingCenter of this read group as a String;
	 * may be {@code null} if this information is not provided.
	 */
	public String getSequencingCenter() {
		return sequencingCenter;
	}


	


	/**
	 * Description of the program.
	 * @return the description of what this program
	 * does as a String;
	 * may be {@code null} if not provided.
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * Get the Library name of this read group.
	 * @return the library name used to construct
	 * this read group as a String;
	 * may be {@code null} if not provided.
	 */
	public String getLibrary() {
		return library;
	}


	/**
	 * Get the programs used for processing
	 * this read group.
	 * @return an String or
	 * {@code null} if not specified.
	 */
	public String getPrograms() {
		return programs;
	}

	/**
	 * Get the name of the pool being sequenced
	 * or the name of the sample of being sequenced
	 * if there is no pool.
	 * @return a String; may be {@code null}
	 * if not specified.
	 */
	public String getSampleOrPoolName() {
		return sampleOrPoolName;
	}

	/**
	 * Date the run was produced.
	 * @return a new {@link Date}
	 * instance; or {@code null}
	 * if the run date is not specified.
	 */
	public Date getRunDate(){
		if(datetime == null){
			return null;
		}
		return new Date(datetime);
	}
	
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id.hashCode();
		result = prime * result
				+ ((datetime == null) ? 0 : datetime.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((flowOrder == null) ? 0 : flowOrder.hashCode());
		
		result = prime * result
				+ ((keySequence == null) ? 0 : keySequence.hashCode());
		result = prime * result + ((library == null) ? 0 : library.hashCode());
		result = prime * result
				+ ((platform == null) ? 0 : platform.hashCode());
		result = prime * result
				+ ((platformUnit == null) ? 0 : platformUnit.hashCode());
		result = prime
				* result
				+ ((predictedMedianInsertSize == null) ? 0
						: predictedMedianInsertSize.hashCode());
		result = prime * result
				+ ((programs == null) ? 0 : programs.hashCode());
		result = prime
				* result
				+ ((sampleOrPoolName == null) ? 0 : sampleOrPoolName.hashCode());
		result = prime
				* result
				+ ((sequencingCenter == null) ? 0 : sequencingCenter.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ReadGroup)) {
			return false;
		}
		ReadGroup other = (ReadGroup) obj;
		if (!id.equals(other.id)) {
			return false;
		}
		if (datetime == null) {
			if (other.datetime != null) {
				return false;
			}
		} else if (!datetime.equals(other.datetime)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (flowOrder == null) {
			if (other.flowOrder != null) {
				return false;
			}
		} else if (!flowOrder.equals(other.flowOrder)) {
			return false;
		}
		
		if (keySequence == null) {
			if (other.keySequence != null) {
				return false;
			}
		} else if (!keySequence.equals(other.keySequence)) {
			return false;
		}
		if (library == null) {
			if (other.library != null) {
				return false;
			}
		} else if (!library.equals(other.library)) {
			return false;
		}
		if (platform != other.platform) {
			return false;
		}
		if (platformUnit == null) {
			if (other.platformUnit != null) {
				return false;
			}
		} else if (!platformUnit.equals(other.platformUnit)) {
			return false;
		}
		if (predictedMedianInsertSize == null) {
			if (other.predictedMedianInsertSize != null) {
				return false;
			}
		} else if (!predictedMedianInsertSize
				.equals(other.predictedMedianInsertSize)) {
			return false;
		}
		if (programs == null) {
			if (other.programs != null) {
				return false;
			}
		} else if (!programs.equals(other.programs)) {
			return false;
		}
		if (sampleOrPoolName == null) {
			if (other.sampleOrPoolName != null) {
				return false;
			}
		} else if (!sampleOrPoolName.equals(other.sampleOrPoolName)) {
			return false;
		}
		if (sequencingCenter == null) {
			if (other.sequencingCenter != null) {
				return false;
			}
		} else if (!sequencingCenter.equals(other.sequencingCenter)) {
			return false;
		}
		return true;
	}




	public static class Builder{
		private String id;

		private String sequencingCenter;		
		private String description;
		
		private String library;
		private String programsUsed;
		private String platformUnit;
		private String sampleOrPoolName;
		
		private Integer predictedInsertSize;
		
		private PlatformTechnology platform;
		
		private Long datetime;
		
		private NucleotideSequence keySequence, flowOrder;
		
		
		/**
		 * Create a new Builder object
		 * that will be used to create a new 
		 * {@link ReadGroup} instance.
		 * @param id the ReadGroup id to use;
		 * can not be null.
		 * @throws NullPointerException if id is null.
		 */
		public Builder(String id) {
			if(id ==null){
				throw new NullPointerException("id can not be null");
			}
			this.id = id;
		}
		
		public Builder(ReadGroup copy){
			this.id = copy.id;
			this.sequencingCenter = copy.sequencingCenter;
			this.description = copy.description;
			this.library = copy.library;
			this.programsUsed = copy.programs;
			this.platformUnit = copy.platformUnit;
			this.sampleOrPoolName = copy.sampleOrPoolName;
			this.predictedInsertSize = copy.getPredictedInsertSize();
			this.platform = copy.platform;
			this.datetime = copy.datetime;
			this.keySequence = copy.keySequence;
			this.flowOrder = copy.flowOrder;
		}
		/**
		 * Get the key sequence for each read group
		 * (454 and iontorrent only).
		 * @return the {@link NucleotideSequence}
		 * representing the keysequence or
		 * {@code null} if the keysequence is not specified
		 * or is not used by this read group.
		 */
		public NucleotideSequence getKeySequence(){
			return keySequence;
		}
		/**
		 * Set the key sequence for each read in this
		 * read group.
		 * @param keySequence the {@link NucleotideSequence}
		 * representing the keysequence or
		 * {@code null} if the keysequence is not specified
		 * or is not used by this read group.
		 * @return this.
		 */
		public Builder setKeySequence(NucleotideSequence keySequence){
			this.keySequence = keySequence;
			return this;
		}
		
		/**
		 * Get the flow order for each read group
		 * (454 and iontorrent only).
		 * @return the {@link NucleotideSequence}
		 * representing the floworder or
		 * {@code null} if the floworder is not specified
		 * or is not used by this read group.
		 */
		public NucleotideSequence getFlowOrder(){
			return flowOrder;
		}
		/**
		 * Set the flow order for each read in this
		 * read group.
		 * @param flowOrder the {@link NucleotideSequence}
		 * representing the floworder or
		 * {@code null} if the floworder is not specified
		 * or is not used by this read group.
		 * @return this.
		 */
		public Builder setFlowOrder(NucleotideSequence flowOrder){
			this.flowOrder = flowOrder;
			return this;
		}
		
		

		/**
		 * The predicted median insert size.
		 * @return the median insert size as an Integer;
		 * or {@code null} if not specified.
		 */
		public Integer getPredictedInsertSize() {
			return predictedInsertSize;
		}
		/**
		 * Set the predicted median insert size.
		 * @param predictedInsertSize the insert size;
		 * may be {@code null} if unknown or unspecified.
		 */
		public Builder setPredictedInsertSize(Integer predictedInsertSize) {
			this.predictedInsertSize = predictedInsertSize;
			return this;
		}
		/**
		 * Get the platform/technology used to produce the reads
		 * in this read group.
		 * @return a {@link PlatformTechnology} or {@code null}
		 * if not specified.
		 */
		public PlatformTechnology getPlatform() {
			return platform;
		}
		/**
		 * Set the platform/technology used to produce the reads
		 * in this read group.
		 * @param platform the {@link PlatformTechnology} or {@code null}
		 * if not specified.
		 * @return this.
		 */
		public Builder setPlatform(PlatformTechnology platform) {
			this.platform = platform;
			return this;
		}

		/**
		 * Get the name of the pool being sequenced
		 * or the name of the sample of being sequenced
		 * if there is no pool.
		 * @return a String; may be {@code null}
		 * if not specified.
		 */
		public String getSampleOrPoolName() {
			return sampleOrPoolName;
		}
		/**
		 * Change the name of the pool being sequenced
		 * or the name of the sample of being sequenced
		 * if there is no pool.
		 * @param sampleOrPoolName the name to set;
		 * may be null.
		 * @return this;
		 */
		public Builder setSampleOrPoolName(String sampleOrPoolName) {
			this.sampleOrPoolName = sampleOrPoolName;
			return this;
		}

		/**
		 * Get the sequencingCenter of the program.
		 * @return the sequencingCenter of this program as a String;
		 * may be null if this information is not provided.
		 */
		public String getSequencingCenter() {
			return sequencingCenter;
		}

		/**
		 * Change the sequencingCenter that produced the read group,
		 * if not called, then the built {@link ReadGroup#getSequencingCenter()}
		 * will return null.
		 * @param name the name of the sequencingCenter to set;
		 * may be null.
		 * @return this
		 */
		public Builder setSequencingCenter(String name) {
			this.sequencingCenter = name;
			return this;
		}

		
		/**
		 * Date the run was produced.
		 * @return a new {@link Date}
		 * instance; or {@code null}
		 * if the run date is not specified.
		 */
		public Date getRunDate(){
			if(datetime == null){
				return null;
			}
			return new Date(datetime);
		}
		
		public Builder setRunDate(Date date){
			if(date ==null){
				datetime =null;
			}else{
				datetime = date.getTime();
			}
			return this;
		}
		/**
		 * Description of the program.
		 * @return the description of what this program
		 * does as a String;
		 * may be {@code null} if not provided.
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * Change the description of the program,
		 * if not called, then the built {@link ReadGroup#getDescription()}
		 * will return null.
		 * @param description the description to set;
		 * may be null.
		 * @return this
		 */
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		/**
		 * Get the Library name of this read group.
		 * @return the library name used to construct
		 * this read group as a String;
		 * may be {@code null} if not provided.
		 */
		public String getLibrary() {
			return library;
		}

		/**
		 * Change the Library name of this read group
		 * if not called, then the built {@link ReadGroup#getLibrary()}
		 * will return null.
		 * @param library the library to set;
		 * may be null.
		 * @return this
		 */
		public Builder setLibrary(String library) {
			this.library = library;
			return this;
		}

		
		
		/**
		 * Get the programs used for processing
		 * this read group.
		 * @return an String or
		 * {@code null} if not specified.
		 */
		public String getPrograms() {
			return programsUsed;
		}
		/**
		 * Change the previous program id of the program,
		 * if not called, then the built {@link ReadGroup#getPrograms()}
		 * will return null.
		 * @param programs the  previous program id to set;
		 * may be null.
		 * @return this
		 */
		public Builder setPrograms(String programs) {
			this.programsUsed = programs;
			return this;
		}


		/**
		 * Get the unique ID. The value of ID is used in the
		 * {@link org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys#READ_GROUP}
		 * tag. IDs may be modified when merging SAM files in order to handle
		 * collisions.
		 * 
		 * @return a String; will never be null.
		 */
		public String getId() {
			return id;
		}
		/**
		 * Change the unique ID.
		 * IDs may be modified when merging SAM files in order to handle
		 * collisions.
		 * @param newId the new ReadGroup id to use;
		 * can not be null.
		 * @throws NullPointerException if newId is null.
		 * @return this
		 */
		public Builder setId(String newId){
			if(newId ==null){
				throw new NullPointerException("new id can not be null");
			}
			this.id = newId;
			return this;
		}
		
		
		/**
		 * Get the unique Platform unit 
		 * (e.g. flowcell-barcode.lane for Illumina or slide for SOLiD).
		 * @return the the platform unit as a String;
		 * may be {@code null} if not provided.
		 */
		public String getPlatformUnit() {
			return platformUnit;
		}

		/**
		 * Change Get the unique Platform unit
		 * if not called, then the built {@link ReadGroup#getDescription()}
		 * will return null.
		 * @param description the description to set;
		 * may be null.
		 * @return this
		 */
		public Builder setPlatformUnit(String platformUnit) {
			this.platformUnit = platformUnit;
			return this;
		}
		
		
		/**
		 * Create a new {@link ReadGroup}
		 * instance using the fields set so far.
		 * @return a new {@link ReadGroup} instance;
		 * will never be null.
		 */
		public ReadGroup build(){
			return new ReadGroup(this);
		}
	}
	
	public enum PlatformTechnology{
		/**
		 * Sanger.
		 */
		CAPILLARY,
		LS454,
		ILLUMINA,
		SOLID,
		HELICOS,
		IONTORRENT,
		PACBIO
		;
		
		public static PlatformTechnology parse(String value){
			return valueOf(value.toUpperCase(Locale.US));
		}
	}
}
