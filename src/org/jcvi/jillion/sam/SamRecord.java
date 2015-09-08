/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.sam.attribute.InvalidAttributeException;
import org.jcvi.jillion.sam.attribute.ReservedAttributeValidator;
import org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeKey;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.cigar.Cigar.ClipType;
import org.jcvi.jillion.sam.header.SamHeader;

public class SamRecord {
	public static final int NOT_SET = -1;
	public static final String UNAVAILABLE = "*";
	public static final String IDENTICAL = "=";
	
	private final SamHeader header;
	private final String queryName, referenceName, nextReferenceName;
	private final EnumSet<SamRecordFlags> flags;
	private final int startOffset, nextOffset;
	private final byte mappingQuality;
	private final Cigar cigar;
	private final NucleotideSequence sequence;
	private final QualitySequence qualities;
	
	private final int observedTemplateLength;
	private final Map<SamAttributeKey, SamAttribute> attributes;
	
	private SamRecord(Builder builder) {
		this.header = builder.header;
		this.queryName = builder.queryName;
		this.flags = builder.flags;
		this.referenceName = builder.referenceName;
		this.startOffset = builder.startPosition;
		this.mappingQuality = builder.mappingQuality;
		this.cigar = builder.cigar;
		
		this.nextOffset = builder.nextPosition;
		this.observedTemplateLength = builder.observedTemplateLength;
		this.sequence = builder.sequence;
		this.qualities = builder.qualities;
		this.attributes = Collections.unmodifiableMap(builder.attributes);
		
		
		//change = to actual ref name
		if(IDENTICAL.equals(builder.nextReferenceName)){
			this.nextReferenceName = referenceName;
		}else{
			this.nextReferenceName = builder.nextReferenceName;
		}
	}
	
	public boolean isPrimary(){
		return 
				!(
					flags.contains(SamRecordFlags.SECONDARY_ALIGNMENT)
				|| flags.contains(SamRecordFlags.SUPPLEMENTARY_ALIGNMENT )
					);
	}
	
	public boolean useForAnalysis(){
		return !flags.contains(SamRecordFlags.SECONDARY_ALIGNMENT);
	}

	protected SamHeader getHeader() {
		return header;
	}

	public String getQueryName() {
		return queryName;
	}
	/**
	 * Get the reference name that this
	 * record aligns to.
	 * @return The name of the reference
	 * or {@link SamRecord#UNAVAILABLE}
	 * if the record either didn't
	 * align or the reference name wasn't provided.
	 */
	public String getReferenceName() {
		return referenceName;
	}

	public String getNextName() {
		return nextReferenceName;
	}

	public EnumSet<SamRecordFlags> getFlags() {
		return flags;
	}

	public int getStartPosition() {
		return startOffset;
	}

	public int getNextOffset() {
		return nextOffset;
	}

	public byte getMappingQuality() {
		return mappingQuality;
	}

	public Cigar getCigar() {
		return cigar;
	}

	public NucleotideSequence getSequence() {
		return sequence;
	}

	public QualitySequence getQualities() {
		return qualities;
	}

	public int getObservedTemplateLength() {
		return observedTemplateLength;
	}
	
	public boolean hasAttribute(SamAttributeKey key){
		if(key==null){
			throw new NullPointerException("key can not be null");
		}
		return attributes.containsKey(key);
	}
	
	public SamAttribute getAttribute(SamAttributeKey key){
		return attributes.get(key);
	}
	
	public Collection<SamAttribute> getAttributes() {
		return attributes.values();
	}
	
	public boolean hasAttribute(ReservedSamAttributeKeys key){
		if(key==null){
			throw new NullPointerException("key can not be null");
		}
		return hasAttribute(key.getKey());
	}
	
	public SamAttribute getAttribute(ReservedSamAttributeKeys key){
		if(key==null){
			return null;
		}
		return getAttribute(key.getKey());
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((cigar == null) ? 0 : cigar.hashCode());
		result = prime * result + ((flags == null) ? 0 : flags.hashCode());
		result = prime * result + mappingQuality;
		result = prime * result + nextOffset;
		result = prime
				* result
				+ ((nextReferenceName == null) ? 0 : nextReferenceName
						.hashCode());
		result = prime * result + observedTemplateLength;
		result = prime * result
				+ ((qualities == null) ? 0 : qualities.hashCode());
		result = prime * result
				+ ((queryName == null) ? 0 : queryName.hashCode());
		result = prime * result	+ referenceName.hashCode();
		result = prime * result
				+ ((sequence == null) ? 0 : sequence.hashCode());
		result = prime * result + startOffset;
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
		if (!(obj instanceof SamRecord)) {
			return false;
		}
		SamRecord other = (SamRecord) obj;
		if (attributes == null) {
			if (other.attributes != null) {
				return false;
			}
		} else if (!attributes.equals(other.attributes)) {
			return false;
		}
		if (cigar == null) {
			if (other.cigar != null) {
				return false;
			}
		} else if (!cigar.equals(other.cigar)) {
			return false;
		}
		if (flags == null) {
			if (other.flags != null) {
				return false;
			}
		} else if (!flags.equals(other.flags)) {
			return false;
		}
		if (mappingQuality != other.mappingQuality) {
			return false;
		}
		if (nextOffset != other.nextOffset) {
			return false;
		}
		if (nextReferenceName == null) {
			if (other.nextReferenceName != null) {
				return false;
			}
		} else if (!nextReferenceName.equals(other.nextReferenceName)) {
			return false;
		}
		if (observedTemplateLength != other.observedTemplateLength) {
			return false;
		}
		if (qualities == null) {
			if (other.qualities != null) {
				return false;
			}
		} else if (!qualities.equals(other.qualities)) {
			return false;
		}
		if (queryName == null) {
			if (other.queryName != null) {
				return false;
			}
		} else if (!queryName.equals(other.queryName)) {
			return false;
		}
		if (!referenceName.equals(other.referenceName)) {
			return false;
		}
		if (sequence == null) {
			if (other.sequence != null) {
				return false;
			}
		} else if (!sequence.equals(other.sequence)) {
			return false;
		}
		if (startOffset != other.startOffset) {
			return false;
		}
		return true;
	}


	@Override
	public String toString() {
		return "SamRecord [queryName=" + queryName + ", referenceName="
				+ referenceName + ", nextReferenceName=" + nextReferenceName
				+ ", flags=" + flags + ", startOffset=" + startOffset
				+ ", nextOffset=" + nextOffset + ", mappingQuality="
				+ mappingQuality + ", cigar=" + cigar + ", sequence="
				+ sequence + ", qualities=" + qualities
				+ ", observedTemplateLength=" + observedTemplateLength
				+ ", attributes=" + attributes + "]";
	}


	public static class Builder{
		
		
		private final SamHeader header;
		private final SamAttributeValidator attributeValidator;
		
		private final Map<SamAttributeKey, SamAttribute> attributes = new LinkedHashMap<SamAttributeKey, SamAttribute>();
		
		private String queryName= UNAVAILABLE, referenceName = null,
				nextReferenceName = null;
		private EnumSet<SamRecordFlags> flags;
		private int startPosition =0, nextPosition= 0;
		private byte mappingQuality= -1;
		private Cigar cigar;
		private NucleotideSequence sequence;
		private QualitySequence qualities;
		
		private int observedTemplateLength = 0;
		
		public Builder(SamHeader header){
			this(header, ReservedAttributeValidator.INSTANCE);
		}
		public Builder(SamHeader header, SamAttributeValidator attributeValidator){
			if(header ==null){
				throw new NullPointerException("header can not be null");
			}
			if(attributeValidator ==null){
				throw new NullPointerException("attribute Validator can not be null");
			}
			this.header = header;
			this.attributeValidator = attributeValidator;
		}
		/**
		 * Add the given attribute to this record.
		 * @param attribute the attribute to add;
		 * can not be null.
		 * @return this
		 * @throws NullPointerException if attribute is null.
		 * @throws InvalidAttributeException if an attribute with the 
		 * same {@link SamAttributeKey}
		 * already exists in this record,
		 * or if the attribute fails the given {@link SamAttributeValidator}.
		 * @see #removeAttribute(SamAttributeKey)
		 */
		public Builder addAttribute(SamAttribute attribute) throws InvalidAttributeException{
			if(attribute ==null){
				throw new NullPointerException("attribute can not be null");
			}
			SamAttributeKey key = attribute.getKey();
			if(attributes.containsKey(key)){
				throw new InvalidAttributeException("attribute with key already exists " + key);
			}
			attributeValidator.validate(header, attribute);
			attributes.put(key, attribute);
			return this;
		}
		/**
		 * Remove the attribute with the given
		 * {@link SamAttributeKey}.
		 * If this record does not have
		 * an attribute with the attribute key,
		 * then this method does nothing.
		 * 
		 * @param attributeKey the key to remove;
		 * can not be null.
		 * @return this.
		 * @throws NullPointerException if attributeKey is null.
		 */
		public Builder removeAttribute(SamAttributeKey attributeKey){
			if(attributeKey ==null){
				throw new NullPointerException("attribute key can not be null");
			}
			attributes.remove(attributeKey);
			return this;
		}
		/**
		 * Sets the query template name.
		 * Reads/segments having identical query name
		 * are regarded to come from the same template.
		 * If this method is not called,
		 * then the query name will be set to 
		 * {@link SamRecord#UNAVAILABLE}.
		 * 
		 * @param queryName the query name to use;
		 * can not be null.
		 * @return this.
		 * @throws NullPointerException if queryName is null.
		 */
		public Builder setQueryName(String queryName) {
			if(queryName ==null){
				throw new NullPointerException("query name can not be null");
			}
			this.queryName = queryName;
			return this;
		}
		/**
		 * Set the reference name that this segment
		 * aligns to.  If this field is not set to
		 * {@link SamRecord#UNAVAILABLE}, then the reference
		 * {@link SamHeader#hasReferenceSequence(String) header.hasReferenceSequence(referenceName)}
		 * must return {@code true}.
		 * If this method is not called, then the value will default
		 * to null.
		 * @param referenceName the reference name this segment aligns;
		 * can not be null and must either be {@link SamRecord#UNAVAILABLE}
		 * or a reference sequence name that is in the provided {@link SamHeader}.
		 * @return this.
		 * @throws NullPointerException if referenceName is null.
		 * @throws IllegalArgumentException if referenceName is not 
		 *  {@link SamRecord#UNAVAILABLE}
		 * and is not in the provided {@link SamHeader}.
		 */
		public Builder setReferenceName(String referenceName) {
			if(referenceName ==null){
				throw new NullPointerException("reference name can not be null");
			}
			if(UNAVAILABLE.equals(referenceName)){
				this.referenceName = null;
			}else{
				assertHeaderKnowsAboutReference(referenceName);
				this.referenceName = referenceName;
			}
			
			return this;
		}
		private void assertHeaderKnowsAboutReference(String referenceName) {
			//RNAME must be present in a SQ-SN tag
			if(!header.hasReferenceSequence(referenceName)){
				throw new IllegalArgumentException("reference name is not in sam header '"+ referenceName+"'");
			}
		}
		/**
		 * Set the reference sequence name of the primary alignment
		 * of the NEXT read in the template.
		 * If nextReferenceName is not {@link SamRecord#UNAVAILABLE}
		 * or {@link SamRecord#IDENTICAL} then the reference
		 * {@link SamHeader#hasReferenceSequence(String) header.hasReferenceSequence(referenceName)}
		 * must return {@code true}. 
		 * If nextReferenceName is not  {@link SamRecord#IDENTICAL}
		 * and the next read in the template {@link SamRecord#isPrimary()}, then this field is
		 * identical to {@link SamRecord#getReferenceName()} of the next read.
		 * @param nextReferenceName the next reference name;
		 * can not be null.
		 * @returns this
		 * @throws NullPointerException if reference name is null.
		 * @throws  IllegalArgumentException if referenceName is not 
		 *  {@link SamRecord#UNAVAILABLE} or {@link SamRecord#IDENTICAL}
		 * and is not in the provided {@link SamHeader}.
		 */
		public Builder setNextReferenceName(String nextReferenceName) {
			if(nextReferenceName ==null){
				throw new NullPointerException("next reference name can not be null");
			}
			if(nextReferenceName.equals(UNAVAILABLE)){
				this.nextReferenceName = null;
			}else{
				if(!nextReferenceName.equals(UNAVAILABLE) && !nextReferenceName.equals(IDENTICAL)){
					assertHeaderKnowsAboutReference(nextReferenceName);
				}
				this.nextReferenceName = nextReferenceName;
			}
			return this;
		}
		/**
		 * Set the {@link SamRecordFlags} of this 
		 * record This method call is required.  This method call makes a defensive copy
		 * of the input Set.
		 * @param flags the {@link SamRecordFlags} relevant
		 * to this record; can not be null.
		 * @return this;
		 * @throws NullPointerException if flags is null.
		 */
		public Builder setFlags(Set<SamRecordFlags> flags) {
			//make defensive copy
			this.flags = EnumSet.copyOf(flags);
			return this;
		}
		/**
		 * Set the start position on the reference (1-based) of the first
		 * matching base of this segment.  The first valid start position
		 * is therefore 1, a value of 0 means the segment did not map.
		 * @param startPosition the start position; must be >=0.
		 * If this method is not called, then the default start position
		 * of 0 is used.
		 * @return this
		 * @throws IllegalArgumentException if startPosition is <0.
		 */
		public Builder setStartPosition(int startPosition) {
			assertValidPosition(startPosition);
			this.startPosition = startPosition;
			return this;
		}
		private void assertValidPosition(int startPosition) {
			if(startPosition <0){
				throw new IllegalArgumentException("position must be >=0 ");
			}
		}
		/**
		 * Set the start position on the reference (1-based) of the 
		 * matching base of the NEXT
		 * read in the template.
		 * This value should match the {@link SamRecord#getStartOffset()}
		 * of the primary line of the next read.
		 * @param nextPosition the start position; must be >=0.
		 * If this method is not called, then the default nextPosition
		 * of 0 is used.
		 * @return this
		 * @throws IllegalArgumentException if startPosition is <0.
		 */
		public Builder setNextPosition(int nextPosition) {
			assertValidPosition(nextPosition);
			this.nextPosition = nextPosition;
			return this;
		}
		/**
		 * Convenience method to set mapping
		 * quality as an int instead of a byte.
		 * Otherwise same as {@link #setMappingQuality(byte)}.
		 * @throws IllegalArgumentException if mappingQuality is out of 
		 * byte range.
		 */
		public Builder setMappingQuality(int mappingQuality) {
			if(mappingQuality>Byte.MAX_VALUE){
				throw new IllegalArgumentException("invalid mapping quality " + mappingQuality);
			}
			return setMappingQuality((byte)mappingQuality);
			
		}
		/**
		 * The mapping quality.  It equals
		 * -10 log<sub>10</sub> Prob{mapping position is wrong}
		 * rounded to the nearest integer.  If set to -1, then 
		 * the mapping is unavailable. Valid values are -1.. {@link Byte#MAX_VALUE}.
		 * If this method is not called, then the default value of -1 will be used.
		 * @param mappingQuality the mapping quality to set; must be >= -1.
		 * @throws IllegalArgumentException if mapping quality < -1.
		 * @return this
		 */
		public Builder setMappingQuality(byte mappingQuality) {
			if(mappingQuality < -1){
				throw new IllegalArgumentException("invalid mapping quality " + mappingQuality);
			}
			this.mappingQuality = mappingQuality;
			return this;
		}
		/**
		 * Sets the {@link Cigar} value. If the {@link Cigar}
		 * is not available, then this value should be set to null.
		 * @param cigar the {@link Cigar}; or null.
		 * @return this.
		 */
		public Builder setCigar(Cigar cigar) {
			this.cigar = cigar;
			return this;
		}
		/**
		 * The segment {@link NucleotideSequence}.  If the sequence is not stored,
		 * then this value should be null.
		 * If this sequence exists, then the sequence length
		 * must equal the length of the {@link Cigar}.
		 * @param sequence the sequence, may be null.
		 * @return this.
		 */
		public Builder setSequence(NucleotideSequence sequence) {
			this.sequence = sequence;
			return this;
		}
		/**
		 * The {@link QualitySequence} of this segment.
		 * If the qualities are not stored, then this value
		 * should be null.  If not null,
		 * then then this segment's {@link NucleotideSequence}
		 * must also not be null and have an equal length.
		 * @param qualities
		 * @see #setSequence(NucleotideSequence)
		 * @return this.
		 */
		public Builder setQualities(QualitySequence qualities) {
			this.qualities = qualities;
			return this;
		}

		/**
		 * Signed observed template length. If all segments are mapped to the
		 * same reference, the unsigned observed template length equals the
		 * number of bases from the leftmost mapped base to the rightmost mapped
		 * base. The leftmost segment has a plus sign and the rightmost has a
		 * minus sign. The sign of segments in the middle is undefined. It is
		 * set as 0 for single-segment template or when the information is
		 * unavailable. If this method is not called, then the default value is
		 * 0.
		 * 
		 * @param observedTemplateLength the observed template length.
		 * @return this.
		 */
		public Builder setObservedTemplateLength(int observedTemplateLength) {
			this.observedTemplateLength = observedTemplateLength;
			return this;
		}
		
		
		public SamRecord build(){
			assertSequenceLengthsCorrect();
			//flags must be set
			if(flags ==null){
				throw new IllegalStateException("flags must be set");
			}
			if(IDENTICAL.equals(nextReferenceName)){
				nextReferenceName = referenceName;
			}
			//TODO force unmapped read to have mapping quality of 0?
			
			return new SamRecord(this);
		}
		private void assertSequenceLengthsCorrect() {
			if(qualities !=null){
				if(sequence ==null){
					throw new IllegalStateException("sequence must be set if qualities are set");
				}
				if(sequence.getLength() !=qualities.getLength()){
					throw new IllegalStateException("sequence and qualities must have same length");
				}
			}
			if(sequence !=null && cigar !=null
				&& sequence.getUngappedLength() != cigar.getUnpaddedReadLength(ClipType.HARD_CLIPPED)){
				
				throw new IllegalStateException("sequence and cigar must have same unpadded/ ungapped read length");
				
			}
			
		}
		
	}


	public boolean mapped() {
		return !flags.contains(SamRecordFlags.READ_UNMAPPED);
	}
	
	public Direction getDirection(){
		return flags.contains(SamRecordFlags.REVERSE_COMPLEMENTED) ? Direction.REVERSE : Direction.FORWARD;
				
	}


	
}
