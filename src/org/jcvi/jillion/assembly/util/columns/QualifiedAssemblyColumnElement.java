package org.jcvi.jillion.assembly.util.columns;

import org.jcvi.jillion.core.qual.PhredQuality;

/**
 * An {@link AssemblyColumnElement} that has a quality value.
 * @author dkatzel
 *
 * @since 6.0
 */
public interface QualifiedAssemblyColumnElement extends AssemblyColumnElement{

	/**
     * Get the {@link PhredQuality} of this AssemblyColumnElement.
     * @return the quality value.
     */
    PhredQuality getQuality();
    
    /**
     * Get the {@link PhredQuality} of this AssemblyColumnElement.
     * @return the quality score.
     * @implNote by default this is the same as {@code getQuality().getQualityScore()}
     * but some implementations might have a more efficient way of computing it and should
     * therefore override this method.
     * 
     * @since 6.0
     */
    default byte getQualityScore() {
    	return getQuality().getQualityScore();
    }
}
