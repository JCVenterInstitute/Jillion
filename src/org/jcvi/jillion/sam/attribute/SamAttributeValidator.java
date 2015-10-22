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
package org.jcvi.jillion.sam.attribute;

import java.util.Objects;

import org.jcvi.jillion.sam.SamAttributed;
import org.jcvi.jillion.sam.header.SamHeader;
/**
 * Validates {@link SamAttribute}s using different criteria.
 * 
 * @author dkatzel
 *
 */
@FunctionalInterface
public interface SamAttributeValidator {
	/**
	 * Validates that the given {@link SamAttribute} is valid and throws 
	 * an {@link InvalidAttributeException} if the validation fails.
	 * 
	 * @param header the header for this SamAttribute which may define this attribute.
	 * @param source where this attribute came from; usually a {@link org.jcvi.jillion.sam.SamRecord}; will never be null.
	 * @param attribute the {@link SamAttribute} to validate; will never be null.
	 * 
	 * @throws InvalidAttributeException if the validation fails.
	 * @throws NullPointerException if any parameter is null.
	 */
	void validate(SamHeader header, SamAttributed source, SamAttribute attribute) throws InvalidAttributeException;
	/**
	 * Creates a new {@link SamAttributeValidator} that
	 * is a two validators chained together.  "this" validator 
	 * is called, first and if it
	 * "passes" an attribute, then the other validator
	 * in the chain gets called.
	 * <p>
	 * The attribute only passes the chained validator
	 * if all the validators in the chain also pass the attribute.
	 * If any validator throws an exception, then that exception is thrown
	 * by the chain and no other validators in the chain are called.
	 * 
	 * @param other the other {@link SamAttributeValidator} to call next in the chain;
	 * can not be null.
	 * @return a new {@link SamAttributeValidator} that chains "this" validator
	 * and the other validator.
	 * 
	 * @throws NullPointerException if other is null.
	 * 
	 * @since 5.0
	 */
	default SamAttributeValidator thenValidating(SamAttributeValidator other){
		Objects.requireNonNull(other);
		return (header, record, attribute) ->{
			validate(header, record, attribute);
			other.validate(header, record, attribute);
		};
	}
}
