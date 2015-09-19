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
/*
 * Created on Jun 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;


/**
 * <code>ObjectsUtil</code> contains common
 * utility functions on Objects.
 * @author dkatzel
 *
 *
 */
public final class ObjectsUtil {
    /**
     * private constructor.
     */
    private ObjectsUtil(){}
    /**
     * Checks to see if the two given objects of the equal
     * without throwing a NullPointerException
     * if the given parameters are null.
     * <br/>
     * The two objects are considered equal:
     * <ol>
     * <li>if a is <code>null</code>;
     *  then b must also be <code>null</code>
     * </li>
     * <li>if a and b are both not <code>null</code>, then
     * <code>a.equals(b)</code> must be <code>true</code>
     * </ol>
     * @param a the first object to compare.
     * @param b the second object to compare.
     * @return <code>true</code> if the given objects are similar;
     * <code>false</code> otherwise.
     */
    public static boolean nullSafeEquals(Object a, Object b){
        if (a == null) {
            return b == null;
        } 
        return a.equals(b);
     
    }
    /**
     * Checks to see if all the given
     * objects are null.
     * @param objects vararg of objects to check
     * @return {@code true} if all given objects
     * are null; {@code false} otherwise.
     */
    public static boolean allNull(Object ...objects){
        for(Object o : objects){
        	if(o !=null){
        		return false;
        	}
        }
        return true;
    }
    /**
     * Checks to see if all the given
     * objects are <strong>not</strong> null.
     * @param objects vararg of objects to check
     * @return {@code true} if any of the given objects
     * are null; {@code false} otherwise.
     */
    public static boolean noneNull(Object ...objects){
        for(Object o : objects){
        	if(o ==null){
        		return false;
        	}
        }
        return true;
    }
    
    /**
     * Computes the hashcode of the given object with support for null objects.
     * @param obj (can be null) the object to compute the hashcode for.
     * @return the hashcode of the object; or {@code 0} if obj is null.
     */
    public static int nullSafeHashCode(Object obj){
        if(obj ==null){
            return 0;
        }
        return obj.hashCode();
    }
    /**
     * Checks to make sure the given obj is not null.
     * @param obj the object to check.
     * @param errorMessage the error message to report if the object  
     * <strong>is</strong> null.
     * @throws NullPointerException if obj is null, the error message
     * is the given message.
     */
    public static void checkNotNull(Object obj, String errorMessage){
        if(obj ==null){
            throw new NullPointerException(errorMessage);
        }
    }
}
