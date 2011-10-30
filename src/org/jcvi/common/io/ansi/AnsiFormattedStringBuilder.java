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

package org.jcvi.common.io.ansi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * {@code AnsiFormattedStringBuilder} is a 
 * wrapper around a {@link StringBuilder}
 * but allows inserting ANSI terminal formatted
 * Strings.  Since the ANSI formatting is complex,
 * only appending to the builder is allowed.
 * @author dkatzel
 *
 *
 */
public final class AnsiFormattedStringBuilder {
    /** 
     * The ANSI code which causes text formatting to be reset to the default. 
     */
    private static final EscapeCode RESET = new EscapeCode(0);
    
    private final StringBuilder builder;
    
    public AnsiFormattedStringBuilder(){
        builder = new StringBuilder();
    }
    /**
     * Append the given unformatted string
     * to the builder.
     * @param unformattedText the unformatted text to append.  
     * If string is null, then the four
     * letters "null" are appended.
     * @return this
     */
    public AnsiFormattedStringBuilder append(String unformattedText){
        return privateAppend(unformattedText, Collections.<AnsiAttribute>emptySet());
    }
    /**
     * Append the given string to the builder but formatted using the given
     * {@link AnsiFont}.
     * @param string the text to append.  If string is null, then the four
     * letters "null" are appended.
     * @param font the {@link AnsiFont} to format the text to append; can
     * not be null.
     * @return this.
     * @throws NullPointerException if font is null.
     */
    public AnsiFormattedStringBuilder append(String string, AnsiFont font){
        if(font ==null){
            return privateAppend(string, Collections.<AnsiAttribute>emptySet());
        }
        return privateAppend(string, Collections.<AnsiAttribute>singleton(font));
    }
    /**
     * Appends the given object's toString() representation
     * to the builder as unformatted text.  This
     * is the same as {@link #append(String) append(object.toString()}.
     * @param object the object whose toString() is to be appended;
     * can not be null.
     * @return this
     * @throws NullPointerException if object is null.
     */
    public AnsiFormattedStringBuilder append(Object object){
        return append(object.toString());
    }
    /**
     * Appends the given object's toString() representation
     * to the builder as text but formatted using the given
     * {@link AnsiFont}.
     * is the same as {@link #append(String, AnsiFont) append(object.toString(),font}.
     * @param object the object whose toString() is to be appended;
     * can not be null.
     * @param font the {@link AnsiFont} to format the text to append; can
     * not be null.
     * @return this
     * @throws NullPointerException if object is null or font are null.
     */
    public AnsiFormattedStringBuilder append(Object object, AnsiFont font){
        return append(object.toString(),font);
    }
    public AnsiFormattedStringBuilder append(String string, Set<TextAttributes> textAttributes){       
        return privateAppend(string,textAttributes);
    }
    
    public AnsiFormattedStringBuilder append(String string, TextAttributes textAttribute){       
        return privateAppend(string,EnumSet.of(textAttribute));
    }
    public AnsiFormattedStringBuilder append(String string, AnsiFont font, TextAttributes textAttribute){
        if(textAttribute==null){
            throw new NullPointerException("textAttribute can not be null");
        }
        return append(string,font,(ForegroundColors)null,EnumSet.of(textAttribute));
    }
    public AnsiFormattedStringBuilder append(String string, ForegroundColors foreground){
        return append(string, foreground, Collections.<TextAttributes>emptySet());
    }
    public AnsiFormattedStringBuilder append(String string, BackgroundColors background){
        return append(string, background, Collections.<TextAttributes>emptySet());
    }
    public AnsiFormattedStringBuilder append(String string, ForegroundColors foreground, Set<TextAttributes> textAttributes){
        return append(string,null, foreground,textAttributes);
    }
    public AnsiFormattedStringBuilder append(String string,AnsiFont font, ForegroundColors foreground, Set<TextAttributes> textAttributes){
        List<AnsiAttribute> attrs = new ArrayList<AnsiAttribute>(textAttributes.size() +2);
        if(font!=null){
            attrs.add(font);
        }
        if(foreground!=null){
            attrs.add(foreground);
        }
        for(TextAttributes g : textAttributes){
            attrs.add(g);
        }
        return privateAppend(string,attrs);

    }
    /**
     * Append the given string to the builder but formatted using the given
     * AnsiFont, background color and any additional
     * {@link TextAttributes}.  The default font, foreground color will be used.
     * @param string the text to append using the given formatting.
     * @param font the {@link AnsiFont} to format the text.
     * @param background the background color to format the text.
     * @param attributes the {@link TextAttributes} to append;
     * can not be null.
     * @return this
     * @throws NullPointerException attributes is null.
     */
    public AnsiFormattedStringBuilder append(String string, BackgroundColors background, Set<TextAttributes> textAttributes){
        return append(string,null,background,textAttributes);
    }
    /**
     * Append the given string to the builder but formatted using the given
     * AnsiFont, background color and any additional
     * {@link TextAttributes}.  The default foreground color will be used.
     * @param string the text to append using the given formatting.
     * @param font the {@link AnsiFont} to format the text.
     * @param background the background color to format the text.
     * @param attributes the {@link TextAttributes} to append;
     * can not be null.
     * @return this
     * @throws NullPointerException if attributes is null.
     */
    public AnsiFormattedStringBuilder append(String string, AnsiFont font,BackgroundColors background, Set<TextAttributes> textAttributes){
        List<AnsiAttribute> attrs = new ArrayList<AnsiAttribute>(textAttributes.size() +2);
        if(font!=null){
            attrs.add(font);
        }
        if(background!=null){
            attrs.add(background);
        }
        for(TextAttributes g : textAttributes){
        	if(g !=null){
        		attrs.add(g);
        	}
        }
        return privateAppend(string,attrs);
       
    }
    /**
     * Append the given string to the builder but formatted using the given
     * AnsiFont, foreground color, background color and any additional
     * {@link TextAttributes}.
     * @param string the text to append using the given formatting.
     * @param font the {@link AnsiFont} to format the text; if
     * null, then the default font will be used.
     * @param foreground the foreground color to format the text; if
     * null, then the default foreground color will be used.
     * @param background the background color to format the text; if
     * null, then the default background color will be used.
     * @param attributes the {@link TextAttributes} to append;
     * can not be null.
     * @return this
     * @throws NullPointerException if attributes is null.
     */
    public AnsiFormattedStringBuilder append(String string, AnsiFont font, ForegroundColors foreground,BackgroundColors background, TextAttributes...attributes){
        List<AnsiAttribute> attrs = new ArrayList<AnsiAttribute>(attributes.length +3);
        if(font !=null){
            attrs.add(font);
        }
        if(foreground !=null){
        	attrs.add(foreground);
        }
        if(background !=null){
        	attrs.add(background);
        }
        
        for(TextAttributes g : attributes){
        	if(g !=null){
        		attrs.add(g);
        	}
        }
        privateAppend(string,attrs);
        
       return this;
    }
    private <A extends AnsiAttribute> AnsiFormattedStringBuilder privateAppend(String string, Collection<A>ansiAttributes){
        Iterator<A> iter = ansiAttributes.iterator();
        final boolean hasFormat = iter.hasNext();
        while(iter.hasNext()){
            A next = iter.next();
            if(next==null){
            	throw new NullPointerException("ansi attributes can not null");
            }
			this.builder.append(next);
        }        
        this.builder.append(string);
        if (hasFormat){
            this.builder.append(RESET);
        }
        return this;
    }
    /**
     * Returns the current String representation
     * of all the text appended so far including
     * ANSI escaped text.  This string will contain
     * the escape codes that tell an ANSI compliant terminal
     * how to format the text.  If this String is displayed
     * on a non-compliant terminal it may appear garbled.
     */
    public String toString(){
        return builder.toString();
    }
    
}
