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
package org.jcvi.jillion.core.qual;

import java.util.Collection;
import java.util.Optional;
import java.util.OptionalDouble;

import org.jcvi.jillion.internal.core.GlyphCodec;

/**
 * @author dkatzel
 *
 *
 */
interface QualitySymbolCodec extends GlyphCodec<PhredQuality>{

	 byte[] encode(Collection<PhredQuality> glyphs);
	 
	 byte[] toQualityValueArray(byte[] encodedData);
	 
	 OptionalDouble getAvgQuality(byte[] encodedData);
	    
    Optional<PhredQuality> getMinQuality(byte[] encodedData);
    
    Optional<PhredQuality> getMaxQuality(byte[] encodedData);
}
