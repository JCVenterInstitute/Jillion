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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.jcvi.datastore.AbstractDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.io.IOUtil;
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
    /**
     * @param dir
     * @param traceCodec
     */
    public SingleSangerTraceDirectoryFileDataStore(DirectoryFileServer fileServer,
            SangerTraceCodec traceCodec) {
        if(fileServer ==null){
            throw new NullPointerException("fileServer can not be null");
        }
        if(traceCodec ==null){
            throw new NullPointerException("SangerTraceCodec can not be null");
        }
        this.fileServer = fileServer;
        this.traceCodec = traceCodec;
    }
    public SingleSangerTraceDirectoryFileDataStore(DirectoryFileServer fileServer){
        this(fileServer, SangerTraceParser.getInstance());
    }
    
    /**
     * {@inheritDoc}
     */
     @Override
     public boolean contains(String id) throws DataStoreException {
         super.contains(id);
         try {
            return this.fileServer.contains(id);
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
         
             InputStream inputStream =null;
             try{
                 File file = fileServer.getFile(id);
                 inputStream= new FileInputStream(file);
                 SangerTrace traceData= traceCodec.decode(inputStream);
                 return new DefaultFileSangerTrace(traceData,file);
             } catch (IOException e) {
                throw new DataStoreException("could not get trace for "+id, e);
            } catch (TraceDecoderException e) {
                throw new DataStoreException("could not get trace for "+id, e);
            }
             finally{
                 IOUtil.closeAndIgnoreErrors(inputStream);
             }

     }
     /**
      * {@inheritDoc}
      */
      @Override
      public Iterator<String> getIds() throws DataStoreException {
          super.getIds();
          return new Iterator<String>(){
              Iterator<File> iter = FileIterator.createFileIterator(fileServer.getRootDir());

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public String next() {
                return iter.next().getName();
            }

            @Override
            public void remove() {
                iter.remove();
                
            }
              
          };
      }
}
