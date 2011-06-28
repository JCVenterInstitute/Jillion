/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/**
 * StringJoiner.java
 *
 * Created: Sep 30, 2008 - 2:19:15 PM (jsitz)
 *
 * Copyright 2008 J. Craig Venter Institute
 */
package org.jcvi.util;

import java.util.Iterator;

import org.jcvi.Builder;


/**
 * The <code>StringUtilities</code> library contains a set of utility methods
 * for dealing with strings that have been left mostly absent from the 
 * current Java {@link String} API.
 *
 * @author jsitz@jcvi.org
 */
public final class StringUtilities
{
    /**
     * {@code JoinedStringBuilder}
     * is a Builder that builds a single String
     * object from a collection of Objects
     * that can optionally be concatenated with 
     * another "glue" Object and/or prefixed and suffixed
     * with other Objects. Objects
     * to be joined use their {@link Object#toString()}
     * method to get their String representation.
     * @author dkatzel
     *
     *
     */
    public static class JoinedStringBuilder implements Builder<String>{
        private final Iterable<?> elements;
        private Object glue;
        private Object prefix;
        private Object suffix;
        /**
         * Join the given elements together
         * in their iteration order.
         * @param elements the Objects to be joined.
         * If an element is null, then it will be skipped
         * during the join.
         */
        public JoinedStringBuilder(Iterable<?> elements){
            this.elements = elements;
        }
        /**
         * Join the given elements together
         * in their iteration order.
         * @param elements the Objects to be joined.
         * If an element is null, then it will be skipped
         * during the join.
         */
        public JoinedStringBuilder(Object... elements){
            this(new ArrayIterable<Object>(elements));
        }
        /**
         * The given glue's {@link Object#toString()} is what is concatenated
         * between the joined the elements.
         * @param glue the String to put between concatenated
         * elements, if glue is set to {@code null}
         * then no glue will be used.
         * @return this
         */
        public JoinedStringBuilder glue(Object glue){
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
        public JoinedStringBuilder prefix(Object prefix){
            this.prefix = prefix;
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
        public JoinedStringBuilder suffix(Object suffix){
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
            Iterator<?> iter = elements.iterator();
            if(iter.hasNext()){
                Object firstElement= iter.next();
                if(firstElement!=null){
                    joined.append(firstElement.toString());
                }
            }
            
            while(iter.hasNext()){
                
                Object item = iter.next();
                if (item != null)
                {   
                    String itemString = item.toString();
                    if (glue != null && joined.length() > 0 && itemString.length() > 0)
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
        
        
    }
    
    /**
     * Converts the given string to standard "camel case".  This will strip out any 
     * non-alpha-numeric characters and treat them as word boundaries.  The first character
     * after a word boundary will be capitalized and all other letters will be lower-cased.
     * 
     * @param text The text to convert.
     * @param capitalizeFirst A flag signalling whether the very first letter should be 
     * capitalized.
     * @return A {@link CharSequence} containing the converted text.
     */
    public static CharSequence toCamelCase(CharSequence text, boolean capitalizeFirst)
    {
        StringBuilder out = new StringBuilder();
        
        boolean forceUpperNext = capitalizeFirst;
        for (int i = 0; i < text.length(); i++)
        {
            char next = text.charAt(i);
            if (Character.isLetterOrDigit(next))
            {
                if (Character.isDigit(next))
                {
                    out.append(next);
                }
                else if (forceUpperNext)
                {
                    out.append(Character.toUpperCase(next));
                    forceUpperNext = false;
                }
                else
                {
                    out.append(Character.toLowerCase(next));
                }
            }
            else
            {
                if (out.length() > 0){
                    forceUpperNext = true;
                }
            }
        }
        
        return out;
    }
    
    /**
     * Converts the given string to standard "camel case" with an initial lower-case character.
     * This is equivalent to {@link #toCamelCase(CharSequence, boolean))}, providing 
     * <code>false</code> as the second parameter.
     * 
     * @param text The text to convert.
     * @param capitalizeFirst A flag signalling whether the very first letter should be 
     * capitalized.
     * @return A {@link CharSequence} containing the converted text.
     * @see #toCamelCase(CharSequence, boolean)
     */
    public static CharSequence toCamelCase(CharSequence text)
    {
        return toCamelCase(text, false);
    }

    /**
     * Builds a labeled count of items.  If the count is one, then the singular label is used, 
     * otherwise the plural label is used.  In the English language, the plural form is the one
     * use most often for counts of zero ("one apple" vs. "zero apples").  This method still 
     * works with negative numbers, however this is unnatural in most languages, as they tend to
     * be structured around the natural number set and not the integer set.  In the case of 
     * negative numbers, the plural label will be used.  This may sound somewhat confusing, but
     * in most cases, it sounds better to use the plural ("negative one apples" vs. "negative 
     * one apple").  Potentially, both could be considered correct.  
     * 
     * @param count The count of the items.
     * @param singular The label to use for singular items.
     * @param plural The label to use for plural items.
     * @return A {@link CharSequence} containing the rendered count and label.
     */
    public static CharSequence pluralize(int count, CharSequence singular, CharSequence plural)
    {
        StringBuilder result = new StringBuilder().append(count).append(' ');
        if (count == 1){
            result.append(singular);
        }
        else{
            result.append(plural);
        }
        
        return result;
    }
    
    /**
     * Builds a labeled count of items.  This is done using the 
     * {@link #pluralize(int, CharSequence, CharSequence)} method, using some simplistic rules
     * to guess at a correct plural label.
     * <p>
     * <em>Note:</em> The rules are currently <em>very</em> simplistic.  They assume the English
     * language is being used and simply append an "s" to the label.
     * 
     * @param count The count of the items.
     * @param label The label to apply to a singular item.
     * @return A {@link CharSequence} containing the rendered count and label.
     */
    public static CharSequence pluralize(int count, CharSequence label)
    {
        return StringUtilities.pluralize(count, label, label + "s");
    }
    
    /**
     * This class is a utility method library and should not be instatiated.
     */
    private StringUtilities()
    {
    }
    
    
    public static boolean isNumber(String s){
        return s.matches("^\\d*\\.?\\d+");
    }
}
