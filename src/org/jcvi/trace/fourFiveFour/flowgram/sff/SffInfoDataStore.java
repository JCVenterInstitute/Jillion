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

package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.IOUtil;

/**
 * {@code SffInfoDataStore} is an {@link SffDataStore}
 * implementation that invokes 454's sffinfo
 * command and parses its STDOUT output to get
 * Sffflowgram information from an sff file.
 * This may be faster than the other JavaCommon parsed
 * implementations of SffDataStore because sffinfo
 * may use undocumented indexing functions to speed up lookups.
 * @author dkatzel
 *
 *
 */
public class SffInfoDataStore implements SffDataStore {

    private static final Pattern QUAL_LEFT_PATTERN = Pattern.compile("Clip Qual Left:\\s+(\\d+)");
    private static final Pattern QUAL_RIGHT_PATTERN = Pattern.compile("Clip Qual Right:\\s+(\\d+)");
    private static final Pattern ADAPTER_LEFT_PATTERN = Pattern.compile("Clip Adap Left:\\s+(\\d+)");
    private static final Pattern ADAPTER_RIGHT_PATTERN = Pattern.compile("Clip Adap Right:\\s+(\\d+)");
    
    
    private static final Pattern BASECALLS_PATTERN = Pattern.compile("^\\s*Bases:\\s+(\\S+)\\s*$");
    private final File sffFile;
    private final String pathToSffInfo;
    
    /**
     * @param sffFile
     */
    public SffInfoDataStore(String pathToSffInfo,File sffFile) {
        this.pathToSffInfo = pathToSffInfo;
        this.sffFile = sffFile;
    }
    public SffInfoDataStore(File sffFile){
        this("sffinfo", sffFile);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<String> getIds() throws DataStoreException {
       return new IdIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SFFFlowgram get(String id) throws DataStoreException {
        ProcessBuilder builder = new ProcessBuilder(
                pathToSffInfo,
                "-n", //no trim
                sffFile.getAbsolutePath(),
                id
                
        );
        Process proc=null;
        Scanner scanner=null;
        try {
            proc = builder.start();
            
            InputStream in = new BufferedInputStream(proc.getInputStream());
            scanner = new Scanner(in);
            long qualLeft=0, qualRight=0, adapterLeft=0,adapterRight=0;
            //initial size is 400 which is # of flows usually
            List<Short> flows = new ArrayList<Short>(400);
            List<Short> usedFlowValues=new ArrayList<Short>(400);
            NucleotideEncodedGlyphs basecalls=null;
            EncodedGlyphs<PhredQuality> qualities=null;
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                Matcher qualClipLeftMatcher = QUAL_LEFT_PATTERN.matcher(line);
                if(qualClipLeftMatcher.find()){
                    qualLeft = Long.parseLong(qualClipLeftMatcher.group(1));
                }else{
                    Matcher qualClipRightMatcher = QUAL_RIGHT_PATTERN.matcher(line);
                    if(qualClipRightMatcher.find()){
                        qualRight = Long.parseLong(qualClipRightMatcher.group(1));
                    }else{
                        Matcher adapterClipLeftMatcher = ADAPTER_LEFT_PATTERN.matcher(line);
                        if(adapterClipLeftMatcher.find()){
                            adapterLeft = Long.parseLong(adapterClipLeftMatcher.group(1));
                        }else{
                            Matcher adapterClipRightMatcher = ADAPTER_RIGHT_PATTERN.matcher(line);
                            if(adapterClipRightMatcher.find()){
                                adapterRight = Long.parseLong(adapterClipRightMatcher.group(1));
                            }else{
                               
                                if(line.startsWith("Flowgram:")){
                                    Scanner scanner2 = new Scanner(line);
                                    scanner2.next(); // Flowgram:
                                    while(scanner2.hasNext()){
                                        flows.add(SFFUtil.parseSffInfoEncodedFlowgram(scanner2.next()));
                                    }
                                    scanner2.close();
                                }else{
                                    if(line.startsWith("Flow Indexes:")){
                                        Scanner scanner2 = new Scanner(line);
                                        scanner2.next(); //Flow
                                        scanner2.next(); //Indexes:
                                        int previousIndex=0;
                                        while(scanner2.hasNextInt()){
                                            int index = scanner2.nextInt();
                                            if(index > previousIndex){
                                                usedFlowValues.add(flows.get(index-1));
                                                previousIndex = index;
                                            }
                                        }
                                        scanner2.close();
                                    }else{
                                        Matcher basesMatcher = BASECALLS_PATTERN.matcher(line);
                                        if(basesMatcher.find()){
                                            basecalls = new DefaultNucleotideEncodedGlyphs(basesMatcher.group(1).trim());
                                        }else{
                                            
                                            if(line.startsWith("Quality Scores:")){
                                                Scanner scanner2 = new Scanner(line);
                                                scanner2.next(); //Quality
                                                scanner2.next(); //Scores:
                                                //basecalls shouldn't be null here as long
                                                //as 454 doesn't change the order of output
                                                List<PhredQuality> qualitiesList = new ArrayList<PhredQuality>((int)basecalls.getLength());
                                                while(scanner2.hasNextByte()){
                                                    qualitiesList.add(PhredQuality.valueOf(scanner2.nextByte()));
                                                }
                                                scanner2.close();
                                                qualities = new DefaultEncodedGlyphs<PhredQuality>(
                                                        RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE, qualitiesList);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return new SFFFlowgram(basecalls, qualities,
                    usedFlowValues, Range.buildRange(CoordinateSystem.RESIDUE_BASED,qualLeft, qualRight), adapterLeft ==0? Range.buildRange(CoordinateSystem.RESIDUE_BASED, 0,0) :
                        Range.buildRange(CoordinateSystem.RESIDUE_BASED,adapterLeft, adapterRight));
            
        } catch (IOException e) {
            throw new DataStoreException("could not get size", e);
        }finally{
            if(proc !=null){
                proc.destroy();
            }
            IOUtil.closeAndIgnoreErrors(scanner);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(String id) throws DataStoreException {
        ProcessBuilder builder = new ProcessBuilder(
                pathToSffInfo,
                "-a",
                sffFile.getAbsolutePath(),
                id
                
        );
        Process proc=null;
        try {
            proc = builder.start();
            
            InputStream in = proc.getInputStream();
            Scanner scanner = new Scanner(in);
            if(!scanner.hasNextLine()){
                return false;
            }
            if(id.equals(scanner.nextLine())){
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new DataStoreException("could not get size", e);
        } finally{
            if(proc !=null){
                proc.destroy();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() throws DataStoreException {
        ProcessBuilder builder = new ProcessBuilder(
                pathToSffInfo,
                "-a",
                sffFile.getAbsolutePath()
                
        );
        Process proc=null;
        try {
            proc = builder.start();
            
            InputStream in = new BufferedInputStream(proc.getInputStream());
            Scanner scanner = new Scanner(in);
            int size=0;
            while(scanner.hasNextLine()){
                scanner.nextLine();
                size++;
            }
            return size;
        } catch (IOException e) {
            throw new DataStoreException("could not get size", e);
        }finally{
            if(proc !=null){
                proc.destroy();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        //no-op

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<SFFFlowgram> iterator() {
        return new DataStoreIterator<SFFFlowgram>(this);
    }

    private class IdIterator implements Iterator{
        private final Scanner scanner;
        private final Process process;
        IdIterator() throws DataStoreException{
            ProcessBuilder builder = new ProcessBuilder(
                    pathToSffInfo,
                    "-a",
                    sffFile.getAbsolutePath()
                    
            );
            
            try {
                process = builder.start();
                
                InputStream in = process.getInputStream();
                scanner = new Scanner(in);
            } catch (Exception e) {
                throw new DataStoreException("could not get id iterator", e);
            }
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public boolean hasNext() {
            try{
                return scanner.hasNext();
            }catch(IllegalStateException e){
                //scanner has been closed
                return false;
            }
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public Object next() {
            
            String id= scanner.next();
            if(!scanner.hasNext()){
                scanner.close();
                process.destroy();
            }
            return id;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void remove() {
            // TODO Auto-generated method stub
            
        }
        
    }
}
