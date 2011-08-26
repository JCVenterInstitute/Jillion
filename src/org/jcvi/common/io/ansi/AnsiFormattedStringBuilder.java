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
 * @author dkatzel
 *
 *
 */
public class AnsiFormattedStringBuilder {
    /** 
     * The ANSI code which causes text formatting to be reset to the default. 
     */
    private static final EscapeCode RESET = new EscapeCode(0);
    
    private final StringBuilder builder;
    
    public AnsiFormattedStringBuilder(){
        builder = new StringBuilder();
    }
    
    public AnsiFormattedStringBuilder append(String string){
        return privateAppend(string, Collections.<AnsiAttribute>emptySet());
    }
    public AnsiFormattedStringBuilder append(Object object){
        return append(object.toString());
    }
    public AnsiFormattedStringBuilder append(String string, Set<GraphicRenditions> graphics){       
        return privateAppend(string,graphics);
    }
    public AnsiFormattedStringBuilder append(String string, GraphicRenditions graphic){       
        return privateAppend(string,EnumSet.of(graphic));
    }
    public AnsiFormattedStringBuilder append(String string, ForegroundColors foreground){
        return append(string, foreground, Collections.<GraphicRenditions>emptySet());
    }
    public AnsiFormattedStringBuilder append(String string, BackgroundColors background){
        return append(string, background, Collections.<GraphicRenditions>emptySet());
    }
    public AnsiFormattedStringBuilder append(String string, ForegroundColors foreground, Set<GraphicRenditions> graphics){
        if(!graphics.isEmpty()){
            List<AnsiAttribute> attrs = new ArrayList<AnsiAttribute>(graphics.size() +1);
            attrs.add(foreground);
            for(GraphicRenditions g : graphics){
                attrs.add(g);
            }
            return privateAppend(string,attrs);
        }
        return privateAppend(string,Collections.<AnsiAttribute>singleton(foreground));
    }
    public AnsiFormattedStringBuilder append(String string, BackgroundColors background, Set<GraphicRenditions> graphics){
        if(!graphics.isEmpty()){
            List<AnsiAttribute> attrs = new ArrayList<AnsiAttribute>(graphics.size() +1);
            attrs.add(background);
            for(GraphicRenditions g : graphics){
                attrs.add(g);
            }
            return privateAppend(string,attrs);
        }
        return privateAppend(string,Collections.<AnsiAttribute>singleton(background));
    }
    public AnsiFormattedStringBuilder append(String string, ForegroundColors foreground,BackgroundColors background, GraphicRenditions...graphics){
        List<AnsiAttribute> attrs = new ArrayList<AnsiAttribute>(graphics.length +2);
        attrs.add(foreground);
        attrs.add(background);
        for(GraphicRenditions g : graphics){
            attrs.add(g);
        }
        privateAppend(string,attrs);
        
       return this;
    }
    private <A extends AnsiAttribute> AnsiFormattedStringBuilder privateAppend(String string, Collection<A>ansiAttributes){
        Iterator<A> iter = ansiAttributes.iterator();
        final boolean hasFormat = iter.hasNext();
        while(iter.hasNext()){
            this.builder.append(iter.next());
        }        
        this.builder.append(string);
        if (hasFormat){
            this.builder.append(RESET);
        }
        return this;
    }
    
    public String toString(){
        return builder.toString();
    }
    
}
