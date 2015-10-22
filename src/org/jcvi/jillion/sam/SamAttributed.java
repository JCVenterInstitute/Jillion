package org.jcvi.jillion.sam;

import org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeKey;
/**
 * A class that implements this interface has queryable
 * {@link SamAttribute}s.
 * @author dkatzel
 *
 */
public interface SamAttributed {
	/**
	 * Does this object have an attribute with the given key.
	 * @param key the Key to look for; can not be null.
	 * 
	 * @return {@code true} if the key is present, {@code false} otherwise.
	 * 
	 * @throws NullPointerException if key is null.
	 */
	boolean hasAttribute(SamAttributeKey key);
	/**
	 * Get the {@link SamAttribute} with the given key.
	 * @param key the Key to look for; can not be null.
	 * @return a {@link SamAttribute} if the attribute with the given key
	 * is present; of {@code null} if there is no attribute with that key
	 * for this object.
	 * 
	 *  @throws NullPointerException if key is null.
	 */
	SamAttribute getAttribute(SamAttributeKey key);
	/**
	 * Does this object have an attribute with the given key.
	 * @param key the Key to look for; can not be null.
	 * 
	 * @return {@code true} if the key is present, {@code false} otherwise.
	 * 
	 * @throws NullPointerException if key is null.
	 */
	boolean hasAttribute(ReservedSamAttributeKeys key);
	/**
	 * Get the {@link SamAttribute} with the given key.
	 * @param key the Key to look for; can not be null.
	 * @return a {@link SamAttribute} if the attribute with the given key
	 * is present; of {@code null} if there is no attribute with that key
	 * for this object.
	 * 
	 *  @throws NullPointerException if key is null.
	 */
	SamAttribute getAttribute(ReservedSamAttributeKeys key);

}