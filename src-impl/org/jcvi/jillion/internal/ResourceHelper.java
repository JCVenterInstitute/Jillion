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

import org.jcvi.jillion.core.io.FileUtil;
/**
 * {@code ResourceFileServer}  wraps a Class's classLoader's getResource methods.
 * @author dkatzel
 *
 */
public class ResourceHelper{

    
    private final Class<?> clazz;
    private final String relativeStartPath;
    
    public ResourceHelper(Class<?> clazz){
    	this.clazz = clazz;
    	relativeStartPath=null;
    }
    /**
     * Construct a new ResourceFileServer using
     * the given class's ClassLoader to as the file server.
     * @param clazz
     * @param rootDir the path from this resource to use as the root,
     * may be null
     * @throws IOException 
     */
    public ResourceHelper(Class<?> clazz, File rootDir) throws IOException{
        this.clazz = clazz;
        if(rootDir==null){
        	relativeStartPath=null;
        }else{
        	relativeStartPath = FileUtil.createRelavitePathFrom(
        			getClassRootDir(), rootDir).replace(File.separator, "/");
        }
    }
   
    private String getRelativePath(String fileId){
    	return String.format("%s%s",
    			relativeStartPath==null?"":relativeStartPath+"/",
    					fileId);
    }

    public File getFile(String fileId) throws IOException {

	        String relativePath = getRelativePath(fileId);
			URL url = clazz.getResource(relativePath);
			
			if(url==null){
				return null;
			}
			return new File(urlDecode(url.getFile()));
        
    }

    public InputStream getFileAsStream(String fileId) throws IOException {
        return clazz.getResourceAsStream(getRelativePath(fileId));
    }

  
    public boolean contains(String fileId) throws IOException {
        return getFile(fileId) !=null;
    }
    /**
     * Replace any URL encoding in the file path with the UTF-8 equivalent.
     * This is needed so paths like in Windows "{@code Docuements%20and%20Settings}"
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
    private final File getClassRootDir() throws UnsupportedEncodingException{
    	return new File(urlDecode(clazz.getResource(".").getFile()));
        //return new File(clazz.getName().replaceAll("\\.", "/")).getParentFile();
    }
}
