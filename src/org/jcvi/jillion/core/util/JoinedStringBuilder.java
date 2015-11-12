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
package org.jcvi.jillion.core.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

/**
 * {@code JoinedStringBuilder}
 * is a Builder that builds a single String
 * object from a collection of Objects
 * that can optionally be concatenated with 
 * another "glue" Object and/or prefixed and suffixed
 * with other Objects. Objects
 * to be joined use their {@link Object#toString()}
 * method to get their String representation.
 * unless otherwise told to transform it differently 
 * using the {@link #transform(Function)}.
 * 
 * <br/>
 * <strong>This class is not thread-safe.</strong>
 * @author dkatzel
 *
 *
 */
public final class JoinedStringBuilder<T> implements Builder<String>{
    private final Iterable<T> elements;
    private Object glue;
    private Object prefix;
    private Object suffix;
    private boolean includeEmptyStrings=false;
    /**
     * Default transformation function, defaults to toString().
     */
    private Function<T, String> valueTransformer = Object::toString;
    
    /**
     * Create a new {@link JoinedStringBuilder} instance
     * that will join the given elements together
     * in their iteration order.
     * @param elements the Objects to be joined.
     * If an element is null, then it will be skipped
     * during the join.
     * @param <T> The type of object being joined.
     */
    public static <T> JoinedStringBuilder<T> create(Iterable<T> elements){
    	return new JoinedStringBuilder<T>(elements);
    }
    /**
     * Create a new {@link JoinedStringBuilder} instance
     * that will join the given elements together
     * in their iteration order.
     * @param elements the Objects to be joined.
     * If an element is null, then it will be skipped
     * during the join.
     * @param <T> The type of object being joined.
     * 
     * @return a new JoinedStringBuilder object; will never be null.
     */
    @SafeVarargs
    public static <T> JoinedStringBuilder<T> create(T... elements){
    	return new JoinedStringBuilder<T>(elements);
    }
    
    /**
     * Join the given elements together
     * in their iteration order.
     * @param elements the Objects to be joined.
     * If an element is null, then it will be skipped
     * during the join.
     */
    JoinedStringBuilder(Iterable<T> elements){
        this.elements = elements;
    }
    /**
     * Join the given elements together
     * in their iteration order.
     * @param elements the Objects to be joined.
     * If an element is null, then it will be skipped
     * during the join.
     */
    @SafeVarargs
	JoinedStringBuilder(T... elements){
        this(Arrays.asList(elements));
    }
    /**
     * The given glue's {@link Object#toString()} is what is concatenated
     * between the joined the elements.
     * @param glue the String to put between concatenated
     * elements, if glue is set to {@code null}
     * then no glue will be used.
     * @return this
     */
    public JoinedStringBuilder<T> glue(Object glue){
        this.glue = glue;
        return this;
    }
    /**
     * Prefix the objects to be joined with the given
     * prefix.
     * @param prefix the prefix that will
     * be in the resulting String
     * before the objects are joined;
     * or {@code null} if there should be no prefix.
     * @return this
     */
    public JoinedStringBuilder<T> prefix(Object prefix){
        this.prefix = prefix;
        return this;
    }  
    /**
     * Change the behavior of 
     * how inner empty strings are handled,
     * by default, empty strings will be skipped.
     * @param value  {@code true} if empty string should be included in the
     * final built string; {@code false} otherwise.
     * 
     * @return this
     */
    public JoinedStringBuilder<T> includeEmptyStrings(boolean value){
    	includeEmptyStrings = value;
    	return this;
    }
    /**
     * Suffix the objects to be joined with the given
     * suffix.
     * @param suffix the suffix that will
     * be in the resulting String
     * after the objects are joined;
     * or {@code null} if there should be no suffix.
     * @return this
     */
    public JoinedStringBuilder<T> suffix(Object suffix){
        this.suffix = suffix;
        return this;
    }  
    @Override
    public String toString(){
        return build();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public String build() {
        final StringBuilder joined = new StringBuilder();
        if(prefix!=null){
            joined.append(prefix);
        }
        Iterator<T> iter = elements.iterator();
        if(iter.hasNext()){
            T firstElement= iter.next();
            if(firstElement!=null){
                joined.append(valueTransformer.apply(firstElement));
            }
        }
        
        while(iter.hasNext()){
            
            T item = iter.next();
            if (item != null)
            {   
                String itemString = valueTransformer.apply(item);
                if (glue != null && joined.length() > 0 && (includeEmptyStrings || itemString.length() > 0))
                {
                    joined.append(glue.toString());
                }
                joined.append(itemString);
            }
        }
        if(suffix!=null){
            joined.append(suffix);
        }
        return joined.toString();
    }
    /**
     * Apply the given Transformation function to each value
     * in the elements to join. If this method is not called,
     * then the default transformation just calls each element's
     * toString().
     * @param transformer the transformer to use; can not be null.
     * @return this.
     */
	public JoinedStringBuilder<T> transform(Function<T, String> transformer) {
		Objects.requireNonNull(transformer);
		valueTransformer = transformer;
		return this;
	}
    
    
}
