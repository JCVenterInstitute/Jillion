package org.jcvi.jillion.sam.header;

import java.util.Date;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.sam.header.SamReadGroup.PlatformTechnology;

/**
 * {@code Builder} class to construct
 * a {@link ReadGroup} using a fluent interface.
 * @author dkatzel
 *
 */
public class SamReadGroupBuilder{
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
	
	private NucleotideSequence keySequence;

	private NucleotideSequence flowOrder;
	
	
	/**
	 * Create a new Builder object
	 * that will be used to create a new 
	 * {@link ReadGroup} instance.
	 * @param id the ReadGroup id to use;
	 * can not be null.
	 * @throws NullPointerException if id is null.
	 */
	public SamReadGroupBuilder(String id) {
		if(id ==null){
			throw new NullPointerException("id can not be null");
		}
		this.id = id;
	}
	/**
	 * Create a new Builder object
	 * whose values are initialized to the
	 * values of the given ReadGroup.
	 * These values can be changed by the using
	 * this Builder's mutator methods.
	 * 
	 * @param copy the ReadGroup whose
	 * values to copy; can not be null.
	 * @throws NullPointerException if copy is null.
	 */
	public SamReadGroupBuilder(SamReadGroup copy){
		this.id = copy.getId();
		this.sequencingCenter = copy.getSequencingCenter();
		this.description = copy.getDescription();
		this.library = copy.getLibrary();
		this.programsUsed = copy.getPrograms();
		this.platformUnit = copy.getPlatformUnit();
		this.sampleOrPoolName = copy.getSampleOrPoolName();
		this.predictedInsertSize = copy.getPredictedInsertSize();
		this.platform = copy.getPlatform();
		this.datetime = copy.getRunDate().getTime();
		this.keySequence = copy.getKeySequence();
		this.flowOrder = copy.getFlowOrder();
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
	public SamReadGroupBuilder setKeySequence(NucleotideSequence keySequence){
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
	public SamReadGroupBuilder setFlowOrder(NucleotideSequence flowOrder){
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
	public SamReadGroupBuilder setPredictedInsertSize(Integer predictedInsertSize) {
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
	public SamReadGroupBuilder setPlatform(PlatformTechnology platform) {
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
	public SamReadGroupBuilder setSampleOrPoolName(String sampleOrPoolName) {
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
	public SamReadGroupBuilder setSequencingCenter(String name) {
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
	
	public SamReadGroupBuilder setRunDate(Date date){
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
	public SamReadGroupBuilder setDescription(String description) {
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
	public SamReadGroupBuilder setLibrary(String library) {
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
	public SamReadGroupBuilder setPrograms(String programs) {
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
	public SamReadGroupBuilder setId(String newId){
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
	public SamReadGroupBuilder setPlatformUnit(String platformUnit) {
		this.platformUnit = platformUnit;
		return this;
	}
	
	
	/**
	 * Create a new {@link ReadGroup}
	 * instance using the fields set so far.
	 * @return a new {@link ReadGroup} instance;
	 * will never be null.
	 */
	public SamReadGroupImpl build(){
		return new SamReadGroupImpl(this);
	}
	
	private static final class SamReadGroupImpl implements SamReadGroup {

	        final String id;
	        final String sequencingCenter;
	        final String description;
	        final String library;
	        final String programs;
	        final String platformUnit;
	        final String sampleOrPoolName;
	        
	        private final Integer predictedMedianInsertSize;
	        final PlatformTechnology platform;
	        
	        final Long datetime;
	        
	        final NucleotideSequence keySequence;
	    final NucleotideSequence flowOrder;
	        
	        SamReadGroupImpl(SamReadGroupBuilder builder){
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
	        @Override
	    public Integer getPredictedInsertSize() {
	                return predictedMedianInsertSize;
	        }
	        /**
	         * Get the platform/technology used to produce the reads
	         * in this read group.
	         * @return a {@link PlatformTechnology} or {@code null}
	         * if not specified.
	         */
	        @Override
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
	        @Override
	    public String getId() {
	                return id;
	        }

	        /**
	         * Get the unique Platform unit 
	         * (e.g. flowcell-barcode.lane for Illumina or slide for SOLiD).
	         * @return the the platform unit as a String;
	         * may be {@code null} if not provided.
	         */
	        @Override
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
	        @Override
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
	        @Override
	    public NucleotideSequence getFlowOrder(){
	                return flowOrder;
	        }

	        /**
	         * Get the sequencingCenter that produced
	         * this read group.
	         * @return the sequencingCenter of this read group as a String;
	         * may be {@code null} if this information is not provided.
	         */
	        @Override
	    public String getSequencingCenter() {
	                return sequencingCenter;
	        }


	        


	        /**
	         * Description of the program.
	         * @return the description of what this program
	         * does as a String;
	         * may be {@code null} if not provided.
	         */
	        @Override
	    public String getDescription() {
	                return description;
	        }


	        /**
	         * Get the Library name of this read group.
	         * @return the library name used to construct
	         * this read group as a String;
	         * may be {@code null} if not provided.
	         */
	        @Override
	    public String getLibrary() {
	                return library;
	        }


	        /**
	         * Get the programs used for processing
	         * this read group.
	         * @return an String or
	         * {@code null} if not specified.
	         */
	        @Override
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
	        @Override
	    public String getSampleOrPoolName() {
	                return sampleOrPoolName;
	        }

	        /**
	         * Date the run was produced.
	         * @return a new {@link Date}
	         * instance; or {@code null}
	         * if the run date is not specified.
	         */
	        @Override
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
	        /**
	         * Two ReadGroups are equal if they have the same values
	         * for all fields.
	         * {@inheritDoc}
	         */
	        @Override
	        public boolean equals(Object obj) {
	                if (this == obj) {
	                        return true;
	                }
	                if (!(obj instanceof SamReadGroup)) {
	                        return false;
	                }
	                SamReadGroup other = (SamReadGroup) obj;
	                if (!id.equals(other.getId())) {
	                        return false;
	                }
	                if (datetime == null) {
	                        if (other.getRunDate() != null) {
	                                return false;
	                        }
	                }else if(other.getRunDate()==null){
	                    return false;
	                }else if (!datetime.equals(other.getRunDate().getTime())) {
	                        return false;
	                }
	                if (description == null) {
	                        if (other.getDescription() != null) {
	                                return false;
	                        }
	                } else if (!description.equals(other.getDescription())) {
	                        return false;
	                }
	                if (flowOrder == null) {
	                        if (other.getFlowOrder() != null) {
	                                return false;
	                        }
	                } else if (!flowOrder.equals(other.getFlowOrder())) {
	                        return false;
	                }
	                
	                if (keySequence == null) {
	                        if (other.getKeySequence() != null) {
	                                return false;
	                        }
	                } else if (!keySequence.equals(other.getKeySequence())) {
	                        return false;
	                }
	                if (library == null) {
	                        if (other.getLibrary() != null) {
	                                return false;
	                        }
	                } else if (!library.equals(other.getLibrary())) {
	                        return false;
	                }
	                if (platform != other.getPlatform()) {
	                        return false;
	                }
	                if (platformUnit == null) {
	                        if (other.getPlatformUnit() != null) {
	                                return false;
	                        }
	                } else if (!platformUnit.equals(other.getPlatformUnit())) {
	                        return false;
	                }
	                if (predictedMedianInsertSize == null) {
	                        if (other.getPredictedInsertSize() != null) {
	                                return false;
	                        }
	                } else if (!predictedMedianInsertSize
	                                .equals(other.getPredictedInsertSize())) {
	                        return false;
	                }
	                if (programs == null) {
	                        if (other.getPrograms() != null) {
	                                return false;
	                        }
	                } else if (!programs.equals(other.getPrograms())) {
	                        return false;
	                }
	                if (sampleOrPoolName == null) {
	                        if (other.getSampleOrPoolName() != null) {
	                                return false;
	                        }
	                } else if (!sampleOrPoolName.equals(other.getSampleOrPoolName())) {
	                        return false;
	                }
	                if (sequencingCenter == null) {
	                        if (other.getSequencingCenter() != null) {
	                                return false;
	                        }
	                } else if (!sequencingCenter.equals(other.getSequencingCenter())) {
	                        return false;
	                }
	                return true;
	        }



	}
}