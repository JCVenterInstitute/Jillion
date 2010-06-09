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
 * Created on Jul 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.fileServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
/**
 * {@code ResourceFileServer} is a {@link FileServer}
 * that wraps a Class's classLoader's getResource methods.
 * @author dkatzel
 *
 */
public class ResourceFileServer extends AbstractFileServer {

    
    private final Class<?> clazz;
    private final String subDirPath;
    /**
     * Construct a new ResourceFileServer using
     * the given class's ClassLoader to as the file server.
     * Same as 
     * <p>
     * {@code new ResourceFileServer(clazz, null)}.
     * </p>
     * @param clazz
     */
    public ResourceFileServer(Class<?> clazz){
        this(clazz,null);
    }
    /**
     * Construct a new ResourceFileServer using
     * the given class's ClassLoader to as the file server.
     * @param clazz
     * @param rootPath the path from this resource to use as the root,
     * may be null
     */
    public ResourceFileServer(Class<?> clazz, String rootPath){
        this.clazz = clazz;
        this.subDirPath=rootPath;
    }
    private String convertFileIdToPath(String fileId){
        if(subDirPath ==null){
            return fileId;
        }
        return subDirPath+ File.separatorChar + fileId;
    }

    @Override
    public File getFile(String fileId) throws IOException {
        verifyNotClosed();
        return new File(clazz.getResource(convertFileIdToPath(fileId)).getFile());
    }

    @Override
    public InputStream getFileAsStream(String fileId) throws IOException {
        return clazz.getResourceAsStream(convertFileIdToPath(fileId));
    }

    
    @Override
    public boolean supportsGettingFileObjects() {
        return true;
    }

    @Override
    public boolean contains(String fileId) throws IOException {
        return clazz.getResource(convertFileIdToPath(fileId)) !=null;
    }

    public File getRootDir(){
        return new File(clazz.getName().replaceAll("\\.", "/")).getParentFile();
    }
}
