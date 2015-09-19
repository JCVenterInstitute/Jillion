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
 * Created on Aug 3, 2007
 * Originally created for Flim, 
 * moved to JavaCommon Nov 2008.
 * @author dkatzel
 */
package org.jcvi.jillion.core.testUtil;


import static org.junit.Assert.*;
import org.junit.Test;
/**
 * This is an abstract TestCase
 * which will test the constuctors
 * of an {@link Throwable} object.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractThrowableTest<T extends Throwable>{

    private String message = "test message";
    private Throwable cause = new Exception("testCause");

    protected abstract T createThrowable(String message);

    protected abstract T createThrowable(String message, Throwable cause);

    @Test
    public void testConstructor(){

        Throwable ex = createThrowable(message);
        assertEquals(message,ex.getMessage());
        assertNull(ex.getCause());
    }
    @Test
    public void testConstructorWithCause(){
        Throwable ex = createThrowable(message,cause);
        assertEquals(message,ex.getMessage());
        assertEquals(cause,ex.getCause());
    }

}
