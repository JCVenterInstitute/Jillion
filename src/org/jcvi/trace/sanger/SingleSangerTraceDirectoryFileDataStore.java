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

package org.jcvi.trace.sanger;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.AbstractDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.util.FileIterator;

/**
 * {@code SingleSangerTraceDirectoryFileDataStore} is a {@link SangerFileDataStore}
 * implemementation that contains {@link FileSangerTrace} data for all
 * single trace files (.scf, .ztr, phd files that are not phd.balls etc)
 * in the given directory.
 * @author dkatzel
 *
 *
 */
public class SingleSangerTraceDirectoryFileDataStore extends AbstractDataStore<FileSangerTrace> implements SangerFileDataStore<FileSangerTrace>{

    private final DirectoryFileServer fileServer;
    private final SangerTraceCodec traceCodec;
    private final String extension;
    public SingleSangerTraceDirectoryFileDataStore(DirectoryFileServer fileServer,
            SangerTraceCodec traceCodec, String extension) {
        if(fileServer ==null){
            throw new NullPointerException("fileServer can not be null");
        }
        if(traceCodec ==null){
            throw new NullPointerException("SangerTraceCodec can not be null");
        }
        this.extension=extension;
        this.fileServer = fileServer;
        this.traceCodec = traceCodec;
        
    }
    /**
     * @param dir
     * @param traceCodec
     */
    public SingleSangerTraceDirectoryFileDataStore(DirectoryFileServer fileServer,
            SangerTraceCodec traceCodec) {
       this(fileServer, traceCodec, null);
    }
    public SingleSangerTraceDirectoryFileDataStore(DirectoryFileServer fileServer, String extension){
        this(fileServer, SangerTraceParser.getInstance(),extension);
    }
    public SingleSangerTraceDirectoryFileDataStore(DirectoryFileServer fileServer){
        this(fileServer, SangerTraceParser.getInstance());
    }
    private String addExtensionIfNeeded(String id){
        if(extension!=null){
            return id + extension;
        }
        return id;
    }
    /**
     * {@inheritDoc}
     */
     @Override
     public boolean contains(String id) throws DataStoreException {
         super.contains(id);
         try {
            return this.fileServer.contains(addExtensionIfNeeded(id));
        } catch (IOException e) {
            throw new DataStoreException("error when checking if Datastore contains" +id ,e);
        }
     }

     /**
     * {@inheritDoc}
     */
     @Override
     public FileSangerTrace get(String id) throws DataStoreException {
         super.get(id);
         
             try{
                 File file = fileServer.getFile(addExtensionIfNeeded(id));
                 SangerTrace traceData= traceCodec.decode(file);
                 return new DefaultFileSangerTrace(traceData,file);
             } catch (IOException e) {
                throw new DataStoreException("could not get trace for "+id, e);
            } catch (TraceDecoderException e) {
                throw new DataStoreException("could not get trace for "+id, e);
            }

     }
     /**
      * {@inheritDoc}
      */
      @Override
      public Iterator<String> getIds() throws DataStoreException {
          super.getIds();
          return new Iterator<String>(){
              Iterator<File> iter = FileIterator.createNonRecursiveFileIteratorBuilder(fileServer.getRootDir()).build();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public String next() {
                String actualFilename = iter.next().getName();
                if(extension!=null){
                    return actualFilename.substring(0, actualFilename.length()-extension.length());
                }
                return actualFilename;
            }

            @Override
            public void remove() {
                iter.remove();
                
            }
              
          };
      }
}
