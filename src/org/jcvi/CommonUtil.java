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
/*
 * Created on Jun 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi;


/**
 * <code>CommonUtil</code> contains common
 * utility functions that can be used by all projects.
 * @author dkatzel
 *
 *
 */
public final class CommonUtil {
    /**
     * private constructor.
     */
    private CommonUtil(){}
    /**
     * Checks to see if the two given objects of the same type are
     * similar.  Similar is defined as :
     * <br/>
     * <ol>
     * <li>if first is <code>null</code>;
     *  then second must also be <code>null</code>
     * </li>
     * <li>if first and second are both not <code>null</code>, then
     * <code>first.equals(second)</code> must be <code>true</code>
     * </ol>
     * @param <T> The Type.
     * @param first the first object to compare.
     * @param second the second object to compare.
     * @return <code>true</code> if the given objects are similar;
     * <code>false</code> otherwise.
     */
    public static <T> boolean similarTo(T first, T second){
        if (first == null) {
            if (second != null){
                return false;
            }
        } else if (!first.equals(second)){
            return false;
        }
        return true;
    }
    /**
     * Checks to see if both parameters are null.
     * @param first
     * @param second
     * @return
     */
    public static boolean bothNull(Object first, Object second){
        return first == null && second ==null;
    }
    /**
     * Checks to see if one and only one of the parameters
     * is null.
     * @param first
     * @param second
     * @return
     */
    public static boolean onlyOneIsNull(Object first, Object second){
        return (first ==null && second!=null)
                || (first !=null && second==null);
    }
    
    public static int hashCode(Object obj){
        if(obj ==null){
            return 0;
        }
        return obj.hashCode();
    }
    
    public static void cannotBeNull(Object obj, String errorMessage){
        if(obj ==null){
            throw new NullPointerException(errorMessage);
        }
    }
}
