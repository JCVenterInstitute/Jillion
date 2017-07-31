/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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

/**
 * Builder object to build new instances
 * of {@link SamRecord}s.
 * 
 * @author dkatzel
 *
 */
public class SamRecordBuilder implements SamAttributed{
	
	
	final SamHeader header;
	private final SamAttributeValidator attributeValidator;
	
	final Map<SamAttributeKey, SamAttribute> attributes = new LinkedHashMap<SamAttributeKey, SamAttribute>();
	
	String queryName= SamRecord.UNAVAILABLE;
	String referenceName = null;
	String nextReferenceName = null;
	SamRecordFlags flags;
	int startPosition =0;
	int nextPosition= 0;
	byte mappingQuality= -1;
	Cigar cigar;
	NucleotideSequence sequence;
	QualitySequence qualities;
	
	int observedTemplateLength = 0;
	/**
	 * Create an new Builder object for a SamRecord
	 * that will use the given {@link SamHeader}.
	 * The reference(s) this record maps to as well
	 * as any read groups or custom tags referred to by this record
	 * must be defined by this header.
	 * 
	 * @param header The {@link SamHeader} to use; can not be null.
	 * 
	 * @throws NullPointerException if header is null.
	 */
	public SamRecordBuilder(SamHeader header){
		this(header, ReservedAttributeValidator.INSTANCE);
	}
	/**
	 * Create an new Builder object for a SamRecord
	 * that will use the given {@link SamHeader} and the given
	 * {@link SamAttributeValidator}.
	 * The reference(s) this record maps to as well
	 * as any read groups or custom tags referred to by this record
	 * must be defined by this header.
	 * 
	 * @param header The {@link SamHeader} to use; can not be null.
	 * 
	 * @throws NullPointerException if header is null.
	 */
	public SamRecordBuilder(SamHeader header, SamAttributeValidator attributeValidator){
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
	 * @throws InvalidAttributeException if the attribute fails the given {@link SamAttributeValidator}.
	 * @see #removeAttribute(SamAttributeKey)
	 */
	public SamRecordBuilder addAttribute(SamAttribute attribute) throws InvalidAttributeException{
		if(attribute ==null){
			throw new NullPointerException("attribute can not be null");
		}
		SamAttributeKey key = attribute.getKey();
		/*
		if(attributes.containsKey(key)){
			throw new InvalidAttributeException("attribute with key already exists " + key);
		}
		*/
		attributeValidator.validate(header, this, attribute);
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
	public SamRecordBuilder removeAttribute(SamAttributeKey attributeKey){
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
	public SamRecordBuilder setQueryName(String queryName) {
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
	 * {@link SamHeader#getReferenceSequence(String)}
	 * must return a non-null value.
	 * 
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
	public SamRecordBuilder setReferenceName(String referenceName) {
		if(referenceName ==null){
			throw new NullPointerException("reference name can not be null");
		}
		if(SamRecord.UNAVAILABLE.equals(referenceName)){
			this.referenceName = null;
		}else{
			assertHeaderKnowsAboutReference(referenceName);
			this.referenceName = referenceName;
		}
		
		return this;
	}
	private void assertHeaderKnowsAboutReference(String referenceName) {
		//RNAME must be present in a SQ-SN tag
		if(header.getReferenceSequence(referenceName)==null){
			throw new IllegalArgumentException("reference name is not in sam header '"+ referenceName+"'");
		}
	}
	/**
	 * Set the reference sequence name of the primary alignment
	 * of the NEXT read in the template.
	 * If nextReferenceName is not {@link SamRecord#UNAVAILABLE}
	 * or {@link SamRecord#IDENTICAL} then the reference
	 * {@link SamHeader#getReferenceSequence(String)}
	 * must return a non-null value. 
	 * 
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
	public SamRecordBuilder setNextReferenceName(String nextReferenceName) {
		if(nextReferenceName ==null){
			throw new NullPointerException("next reference name can not be null");
		}
		if(nextReferenceName.equals(SamRecord.UNAVAILABLE)){
			this.nextReferenceName = null;
		}else{
			if(!nextReferenceName.equals(SamRecord.UNAVAILABLE) && !nextReferenceName.equals(SamRecord.IDENTICAL)){
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
        public SamRecordBuilder setFlags(int flags) {
            this.flags = SamRecordFlags.valueOf(flags);
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
	public SamRecordBuilder setFlags(Set<SamRecordFlag> flags) {
		//make defensive copy
		this.flags = SamRecordFlags.valueOf(flags);
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
	public SamRecordBuilder setStartPosition(int startPosition) {
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
	 * This value should match the {@link SamRecord#getStartPosition()}
	 * of the primary line of the next read.
	 * @param nextPosition the start position; must be >=0.
	 * If this method is not called, then the default nextPosition
	 * of 0 is used.
	 * @return this
	 * @throws IllegalArgumentException if startPosition is <0.
	 */
	public SamRecordBuilder setNextPosition(int nextPosition) {
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
	public SamRecordBuilder setMappingQuality(int mappingQuality) {
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
	public SamRecordBuilder setMappingQuality(byte mappingQuality) {
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
	public SamRecordBuilder setCigar(Cigar cigar) {
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
	public SamRecordBuilder setSequence(NucleotideSequence sequence) {
		this.sequence = sequence;
		return this;
	}
	/**
	 * The {@link QualitySequence} of this segment.
	 * If the qualities are not stored, then this value
	 * should be null.  If not null,
	 * then then this segment's {@link NucleotideSequence}
	 * must also not be null and have an equal length.
	 * @param qualities the {@link QualitySequence} for this record; may be null
	 * if the qualities are not known.
	 * 
	 * @see #setSequence(NucleotideSequence)
	 * @return this.
	 */
	public SamRecordBuilder setQualities(QualitySequence qualities) {
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
	public SamRecordBuilder setObservedTemplateLength(int observedTemplateLength) {
		this.observedTemplateLength = observedTemplateLength;
		return this;
	}
	
	
	public SamRecord build(){
		assertSequenceLengthsCorrect();
		//flags must be set
		if(flags ==null){
			throw new IllegalStateException("flags must be set");
		}
		if(SamRecord.IDENTICAL.equals(nextReferenceName)){
			nextReferenceName = referenceName;
		}
		//TODO force unmapped read to have mapping quality of 0?
		
		return new SamRecordImpl(this);
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
	@Override
	public boolean hasAttribute(SamAttributeKey key){
		if(key==null){
			throw new NullPointerException("key can not be null");
		}
		return attributes.containsKey(key);
	}
	
	@Override
	public SamAttribute getAttribute(SamAttributeKey key){
		return attributes.get(key);
	}
	
	@Override
	public boolean hasAttribute(ReservedSamAttributeKeys key){
		if(key==null){
			throw new NullPointerException("key can not be null");
		}
		return hasAttribute(key.getKey());
	}
	
	@Override
	public SamAttribute getAttribute(ReservedSamAttributeKeys key){
		if(key==null){
			return null;
		}
		return getAttribute(key.getKey());
	}
	
}
