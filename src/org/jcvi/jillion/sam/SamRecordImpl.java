/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeKey;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.header.SamHeader;
/**
 * Package-private implementation class of SamRecord.
 * 
 * @author dkatzel
 *
 */
class SamRecordImpl implements SamRecord {
	private final SamHeader header;
	private final String queryName, referenceName, nextReferenceName;
	private final Set<SamRecordFlags> flags;
	private final int startPosition, nextOffset;
	private final byte mappingQuality;
	private final Cigar cigar;
	private final NucleotideSequence sequence;
	private final QualitySequence qualities;
	
	private final int observedTemplateLength;
	private final Map<SamAttributeKey, SamAttribute> attributes;
	
	SamRecordImpl(SamRecordBuilder builder) {
		this.header = builder.header;
		this.queryName = builder.queryName;
		this.flags = Collections.unmodifiableSet(builder.flags);
		this.referenceName = builder.referenceName;
		this.startPosition = builder.startPosition;
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
	
	@Override
	public boolean isPrimary(){
		return 
				!(
					flags.contains(SamRecordFlags.SECONDARY_ALIGNMENT)
				|| flags.contains(SamRecordFlags.SUPPLEMENTARY_ALIGNMENT )
					);
	}
	
	@Override
	public boolean useForAnalysis(){
		return !flags.contains(SamRecordFlags.SECONDARY_ALIGNMENT);
	}

	protected SamHeader getHeader() {
		return header;
	}

	@Override
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
	@Override
	public String getReferenceName() {
		return referenceName;
	}

	@Override
	public String getNextName() {
		return nextReferenceName;
	}

	@Override
	public Set<SamRecordFlags> getFlags() {
		return flags;
	}

	@Override
	public int getStartPosition() {
		return startPosition;
	}

	@Override
	public int getNextOffset() {
		return nextOffset;
	}

	@Override
	public byte getMappingQuality() {
		return mappingQuality;
	}

	@Override
	public Cigar getCigar() {
		return cigar;
	}

	@Override
	public NucleotideSequence getSequence() {
		return sequence;
	}

	@Override
	public QualitySequence getQualities() {
		return qualities;
	}

	@Override
	public int getObservedTemplateLength() {
		return observedTemplateLength;
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
		Objects.requireNonNull(key);
		return attributes.get(key);
	}
	
	@Override
	public Collection<SamAttribute> getAttributes() {
		return attributes.values();
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
		Objects.requireNonNull(key);
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
		result = prime * result + startPosition;
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
		if (!(obj instanceof SamRecordImpl)) {
			return false;
		}
		SamRecordImpl other = (SamRecordImpl) obj;
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
		if(referenceName ==null){
			if(other.referenceName !=null){
				return false;
			}
		}else if (!referenceName.equals(other.referenceName)) {
			return false;
		}
		if (sequence == null) {
			if (other.sequence != null) {
				return false;
			}
		} else if (!sequence.equals(other.sequence)) {
			return false;
		}
		if (startPosition != other.startPosition) {
			return false;
		}
		return true;
	}


	@Override
	public String toString() {
		return "SamRecord [queryName=" + queryName + ", referenceName="
				+ referenceName + ", nextReferenceName=" + nextReferenceName
				+ ", flags=" + flags + ", startPosition=" + startPosition
				+ ", nextOffset=" + nextOffset + ", mappingQuality="
				+ mappingQuality + ", cigar=" + cigar + ", sequence="
				+ sequence + ", qualities=" + qualities
				+ ", observedTemplateLength=" + observedTemplateLength
				+ ", attributes=" + attributes + "]";
	}


	/**
	 * Did this record map to one of the references.
	 * 
	 * @return {@code true} if the record mapped somewhere;
	 * {@code false} otherwise.
	 */
	@Override
	public boolean mapped() {
		return !flags.contains(SamRecordFlags.READ_UNMAPPED);
	}
	/**
	 * Get the {@link Direction} that this read mapped in.
	 * If the read didn't map, then the direction will be {@link Direction#FORWARD}.
	 * 
	 * @return {@link Direction#REVERSE} if the read mapped in reverse;
	 * {@link Direction#FORWARD} otherwise.
	 */
	@Override
	public Direction getDirection(){
		return flags.contains(SamRecordFlags.REVERSE_COMPLEMENTED) ? Direction.REVERSE : Direction.FORWARD;
				
	}
	/**
	 * Get the alignment {@link Range} that his record
	 * mapped to along the reference.  The returned Range
	 * is the range used to compute the Bin in indexed bai files.
	 * 
	 * @return a {@link Range} for this record's alignment,
	 * or {@code null} if this record didn't map.
	 */
	@Override
	public Range getAlignmentRange() {
		if(mapped()){
			return new Range.Builder(cigar.getNumberOfReferenceBasesAligned())
							.shift(startPosition -1)
							.build();
		}
		return null;
	}


	
}
