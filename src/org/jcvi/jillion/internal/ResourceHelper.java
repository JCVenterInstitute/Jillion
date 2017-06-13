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
 * Created on Jul 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
/**
 * {@code ResourceHelper}  wraps a Class's classLoader's getResource methods.
 * @author dkatzel
 *
 */
public class ResourceHelper{

    
    private final Class<?> clazz;
    /**
     * Construct a new {@link ResourceHelper} using
     * the given class's ClassLoader to as the file server.
     * @param clazz the class all paths will be relative from.
     */
    public ResourceHelper(Class<?> clazz){
    	this.clazz = clazz;
    }
    /**
     * Construct a new instance using the given's object's class.
     * 
     * @apiNote this is the same as {@code new ResourceHelper( classInstance.getClass()); }.
     * 
     * @param classInstance the object whose class to use; can not be null.
     * 
     * @throws NullPointerException if classInstance is null.
     */
    public ResourceHelper(Object classInstance){
        this(classInstance.getClass());
    }
   
  
    /**
     * Get the File associated with the given path
     * @param relativePath the path to the file to get relative to the base class
     * of this Helper object.
     * @return a {@link File} if it exists, or {@code null} otherwise.
     * @throws IOException if there is a problem decoding the path into a File object.
     */
    public File getFile(String relativePath) throws IOException{

			URL url = clazz.getResource(relativePath);
			
			if(url==null){
				return null;
			}
			return new File(urlDecode(url.getFile()));
        
    }
    /**
     * Get the InputStream associated with the given path.
     * @param relativePath the path to the file to get relative to the base class
     * of this Helper object.
     * @return an {@link InputStream} if it exists, or {@code null} otherwise.
     * 
     */
    public InputStream getFileAsStream(String relativePath) {
        return clazz.getResourceAsStream(relativePath);
    }

    /**
     * Replace any URL encoding in the file path with the UTF-8 equivalent.
     * This is needed so paths like in Windows "{@code Documents%20and%20Settings}"
     * becomes "Documents and Settings"
     * @param path
     * @return
     * @throws UnsupportedEncodingException
     */
    private String urlDecode(String path) throws UnsupportedEncodingException{
    	return URLDecoder.decode(path, "UTF-8");
    }
    public File getRootDir(){
        return new File(clazz.getName().replaceAll("\\.", "/")).getParentFile();
    }
}
