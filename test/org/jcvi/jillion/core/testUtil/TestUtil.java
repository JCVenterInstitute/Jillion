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
 * Created on Jul 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.testUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Permission;

import org.jcvi.jillion.core.io.IOUtil;


public final class TestUtil {
    public static void assertEqualAndHashcodeSame(Object obj1, Object obj2) {
        assertEquals(obj1, obj2);
        assertTrue(obj1.hashCode()== obj2.hashCode());

        assertEquals(obj2,obj1);
        assertTrue(obj2.hashCode()== obj1.hashCode());
    }

    public static void assertNotEqualAndHashcodeDifferent(Object obj1,Object obj2) {
        assertFalse(obj1.equals(obj2));
        assertFalse(obj1.hashCode()== obj2.hashCode());

        assertFalse(obj2.equals(obj1));
        assertFalse(obj2.hashCode()== obj1.hashCode());
    }
    /**
     * A special implementation of {@link SecurityManager}
     * that will throw a {@link TriedToExitException}
     * if {@link System#exit(int)} is called.
     * This is useful for testing main methods
     * without shutting
     * down the jvm running junit.
     * Tests can catch {@link TriedToExitException}
     * to figure out what exit code was set.
     */
    public static final SecurityManager NON_EXITABLE_MANAGER = new SecurityManager(){

		@Override
		public void checkPermission(Permission perm) {
			//allow everything
		}
		/**
		 * Throws a {@link TriedToExitException} instead of exiting.
		 * <p/>
		 * {@inheritDoc}
		 */
		@Override
		public void checkExit(int status) {
			throw new TriedToExitException(status);
		}
    	
    };
    
    public static final class TriedToExitException extends SecurityException{
		private static final long serialVersionUID = 1L;
		private final int exitCode;
    	
    	public TriedToExitException(int exitCode){
    		this.exitCode=exitCode;
    	}

		@Override
		public String getMessage() {
			return String.format("tried to System.exit(%d)",exitCode);
		}

		public int getExitCode() {
			return exitCode;
		}
    	
    }
    public static String getFileAsString(File f) throws IOException{

    	try(InputStream in = new BufferedInputStream(new FileInputStream(f));
    		ByteArrayOutputStream out = new ByteArrayOutputStream((int)f.length())){
    		IOUtil.copy(in, out);
    		return new String(out.toByteArray(), IOUtil.UTF_8);
    	}
 
    }
    public static boolean contentsAreEqual(File file1, File file2) throws IOException{
		if(file1.length() != file2.length()){
			return false;
		}
		

		
		try(InputStream in1= new BufferedInputStream(new FileInputStream(file1));
				InputStream in2= new BufferedInputStream(new FileInputStream(file2));
			){
			int value1,value2;
			do{
				value1 = in1.read();
				value2 = in2.read();
				if(value1 !=value2){
					return false;
				}
			}while(value1 >=0);
			return true;
		}
		
	}

}
