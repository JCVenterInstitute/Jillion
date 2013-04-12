/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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

        assertFalse(obj2 + " vs " + obj1, obj2.equals(obj1));
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
    
    public static boolean contentsAreEqual(File file1, File file2) throws IOException{
		if(file1.length() != file2.length()){
			return false;
		}
		
		InputStream in1 =null;
		InputStream in2 =null;
		
		try{
			in1 = new BufferedInputStream(new FileInputStream(file1));
			in2 = new BufferedInputStream(new FileInputStream(file2));
			int value1,value2;
			do{
				value1 = in1.read();
				value2 = in2.read();
				if(value1 !=value2){
					return false;
				}
			}while(value1 >=0);
			return true;
		}finally{
			IOUtil.closeAndIgnoreErrors(in1,in2);
		}
		
	}

}
