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
 * Created on Jan 29, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.frg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.qual.TigrQualitiesEncodedGyphCodec;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.jcvi.common.io.fileServer.DirectoryFileServer;
import org.jcvi.common.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;

public class Frg2Writer {
    private static final TigrQualitiesEncodedGyphCodec QUALITY_CODEC = TigrQualitiesEncodedGyphCodec.getINSTANCE();
      private static final String FRG_VERSION_2_TAG = "{VER\nver:2\n}\n";
    private static final String LIBRARY_FORMAT = "{LIB\nact:A\nacc:%s\nori:%s\nmea:%.2f\nstd:%.2f\nsrc:\n.\nnft:0\nfea:\n.\n}\n";
    
    private static final String FRG_2_FORMAT = "{FRG\nact:A\nacc:%s\nrnd:1\nsta:G\nlib:%s\npla:0\nloc:0\nsrc:\n%s\n.\nseq:\n%s\n.\nqlt:\n%s\n.\nhps:\n.\nclv:%d,%d\nclr:%d,%d\n}\n";
    private static final String LKG_MESSAGE = "{LKG\nact:A\nfrg:%s\nfrg:%s\n}\n";

    public void writeFrg2(final Iterable<Fragment> unmatedFrgs,OutputStream out) throws IOException, InterruptedException, ExecutionException{
    	writeFrg2(Collections.<Mated<Fragment>>emptyList(),unmatedFrgs,out);
    }
    /**
     * Create a mated FRG 2 file. 
     * @param matedFrags list of mated fragments to write
     * @param out outputStream to write data to.
     * @throws IOException
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
    public void writeFrg2(final List<Mated<Fragment>> matedFrags, final Iterable<Fragment> unmatedFrgs,OutputStream out) throws IOException, InterruptedException, ExecutionException{
        ExecutorService executor = Executors.newFixedThreadPool(3);
        writeVersion(out);
        final ReadWriteDirectoryFileServer tempDir = DirectoryFileServer.createTemporaryDirectoryFileServer();
        List<Callable<Void>> writers = new ArrayList<Callable<Void>>();
        writers.add( new Callable<Void>(){

            @Override
            public Void call() throws Exception{
                OutputStream temp=null;
                try {
                    temp = new FileOutputStream(tempDir.createNewFile("tmp.lib"));
                    writeLibraries(matedFrags, unmatedFrgs, temp);
                    return null;
                }
                finally{
                    IOUtil.closeAndIgnoreErrors(temp);
                }
                              
            }
        });
        writers.add( new Callable<Void>(){

            @Override
            public Void call() throws Exception{
                OutputStream temp=null;
                try {
                    temp = new FileOutputStream(tempDir.createNewFile("tmp.frag"));
                    writeFragments(matedFrags, unmatedFrgs, temp); 
                    return null;
                }finally{
                    IOUtil.closeAndIgnoreErrors(temp);
                }
                              
            }
        });
        writers.add(  new Callable<Void>(){

            @Override
            public Void call() throws Exception{
                OutputStream temp=null;
                try {
                    temp = new FileOutputStream(tempDir.createNewFile("tmp.link"));
                    writeLinkages(matedFrags,temp);
                    return null;
                }finally{
                    IOUtil.closeAndIgnoreErrors(temp);
                }
                              
            }
        });
        //wait for all to finish...
        for(Future<Void> f :executor.invokeAll(writers)){
            //get blocks until task is finished
            f.get();
        }
       
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        appendTempFile(tempDir.getFile("tmp.lib"), out);
        appendTempFile(tempDir.getFile("tmp.frag"), out);
        appendTempFile(tempDir.getFile("tmp.link"), out);
        IOUtil.closeAndIgnoreErrors(out);
        IOUtil.closeAndIgnoreErrors(tempDir);
    }
    
    private void appendTempFile(File temp, OutputStream out) throws IOException{
        InputStream in = null;
        try{
            in = new FileInputStream(temp);
            IOUtil.copy(in, out);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    private void writeLinkages(List<Mated<Fragment>> matedFragsLists,
            OutputStream out) throws IOException {
        for(Mated<Fragment> matedFrgs: matedFragsLists){
            writeLinkageMessage(matedFrgs.getMates(), out);
        }
        
    }
    private void writeLinkageMessage(List<Fragment> mates,OutputStream out) throws IOException{
        out.write(String.format(LKG_MESSAGE, mates.get(0).getId(),mates.get(1).getId()).getBytes(IOUtil.UTF_8));
    }
    private void writeFragments(Iterable<Mated<Fragment>> matedFragsList, Iterable<Fragment> unmatedFrgs,OutputStream out) throws IOException {
        for(Mated<Fragment> matedFrags: matedFragsList){
            for(Fragment frag: matedFrags.getMates()){
                writeFrag(frag,out);
            }
        }
        for(Fragment unmatedFrag: unmatedFrgs){
            writeFrag(unmatedFrag,out);
        }
        
    }
    private void writeFrag(Fragment frag, OutputStream out) throws IOException {
        Range clearRange = frag.getValidRange();
        Range vectorClearRange = frag.getVectorClearRange();
        Library library = frag.getLibrary();
       out.write(String.format(FRG_2_FORMAT, frag.getId(),
               library.getId(),
               writeSourceComment(frag),
               Nucleotides.asString(frag.getBasecalls().asList()),
               new String(QUALITY_CODEC.encode(frag.getQualities().asList()),IOUtil.UTF_8),
               vectorClearRange.getBegin(CoordinateSystem.SPACE_BASED),vectorClearRange.getEnd(CoordinateSystem.SPACE_BASED),
               clearRange.getBegin(CoordinateSystem.SPACE_BASED),clearRange.getEnd(CoordinateSystem.SPACE_BASED)
               ).getBytes(IOUtil.UTF_8));
        
    }
    private String writeSourceComment(Fragment frag) {
        return frag.getComment();
    }

    private void writeVersion(OutputStream out) throws IOException {
        out.write(FRG_VERSION_2_TAG.getBytes(IOUtil.UTF_8));
    }
    
    private void writeLibraries(Iterable<Mated<Fragment>> matedFrags, Iterable<Fragment> unmatedFrgs, OutputStream out) throws IOException {
        Set<String> seen = new HashSet<String>();
        for(Fragment frag: unmatedFrgs){
            writeLibraryIfNotYetSeen(out, seen, frag);
        }
        for(Mated<Fragment> mated : matedFrags){
            for(Fragment frag: mated.getMates()){
                writeLibraryIfNotYetSeen(out, seen, frag);
            }
        }
        
    }
    private  void writeLibraryIfNotYetSeen(OutputStream out, Set<String> seen,
            Fragment frag) throws IOException {
        if(!seen.contains(frag.getLibraryId())){
            writeLibrary(frag.getLibrary(), out);
            seen.add(frag.getLibraryId());
        }
    }
    private void writeLibrary(Library library, OutputStream out)
            throws IOException {
        final Distance distance = library.getDistance();
        out.write(String.format(LIBRARY_FORMAT,
                library.getId(), library.getMateOrientation().getCharacter(),
                distance.getMean(), distance.getStdDev()).getBytes(IOUtil.UTF_8));
    }
}
