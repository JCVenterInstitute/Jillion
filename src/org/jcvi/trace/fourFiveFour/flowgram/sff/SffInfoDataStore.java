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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideSequence;
import org.jcvi.glyph.phredQuality.EncodedQualitySequence;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualitySequence;
import org.jcvi.io.IOUtil;
import org.jcvi.util.CloseableIterator;

/**
 * {@code SffInfoDataStore} is an {@link SffDataStore}
 * implementation that invokes 454's sffinfo
 * command and parses its STDOUT output to get
 * Sffflowgram information from an sff file.
 * This may be faster than the other JavaCommon parsed
 * implementations of SffDataStore (for fetching a few reads
 * from a large sff file) because sffinfo
 * may use undocumented indexing functions to speed up lookups.
 * However, it is not recommended to use this implementation
 * where lots of ids have to retrieved because of the overhead
 * of making system calls to sffinfo makes the time to fetch data
 * take a long time.
 * @author dkatzel
 *
 *
 */
public class SffInfoDataStore implements SffDataStore {

    private static final Pattern QUAL_LEFT_PATTERN = Pattern.compile("Clip Qual Left:\\s+(\\d+)");
    private static final Pattern QUAL_RIGHT_PATTERN = Pattern.compile("Clip Qual Right:\\s+(\\d+)");
    private static final Pattern ADAPTER_LEFT_PATTERN = Pattern.compile("Clip Adap Left:\\s+(\\d+)");
    private static final Pattern ADAPTER_RIGHT_PATTERN = Pattern.compile("Clip Adap Right:\\s+(\\d+)");
    private static final Pattern ID_PATTERN = Pattern.compile("^>(\\S+)");
    
    
    private static final Pattern BASECALLS_PATTERN = Pattern.compile("^\\s*Bases:\\s+(\\S+)\\s*$");
    private final File sffFile;
    private final String pathToSffInfo;
    private SFFCommonHeader sffHeader;
    /**
     * @param sffFile
     * @throws FileNotFoundException 
     * @throws SFFDecoderException 
     */
    public SffInfoDataStore(String pathToSffInfo,File sffFile) throws SFFDecoderException, FileNotFoundException {
        this.pathToSffInfo = pathToSffInfo;
        this.sffFile = sffFile;
        //need to read the header so we get the # of flows
        //sffinfo only puts that data for the 1st record
        //returned so things like the iterator will need to
        //get it from somewhere other than STDOUT.
        SffFileVisitor headerVisitor = new AbstractSffFileVisitor() {
            
           
            @Override
            public boolean visitCommonHeader(SFFCommonHeader commonHeader) {
                sffHeader = commonHeader;
                return false;
            }
        };
        SffParser.parseSFF(sffFile, headerVisitor);
    }
    public SffInfoDataStore(File sffFile) throws SFFDecoderException, FileNotFoundException{
        this("sffinfo", sffFile);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
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
            InputStream in = proc.getInputStream();
            scanner = new Scanner(in);
            return parseSingleSffRecordFrom(scanner);
            
        } catch (IOException e) {
            throw new DataStoreException("could not get size", e);
        }finally{
            if(proc !=null){
                proc.destroy();
            }
            IOUtil.closeAndIgnoreErrors(scanner);
        }
    }
    protected SFFFlowgram parseSingleSffRecordFrom(Scanner scanner) {
        String id = parseIdFrom(scanner);
        Range qualityRange = parseQualityClip(scanner);
        Range adapterRange = parseAdapterClip(scanner);
        short[] flows = parseAllFlows(scanner,sffHeader.getNumberOfFlowsPerRead());            
        List<Short> usedFlowValues= parseUsedFlows(scanner.nextLine(), flows);             
        NucleotideSequence basecalls=parseBasecalls(scanner.nextLine());
        QualitySequence qualities=parseQualities(scanner.nextLine(),(int)basecalls.getLength());
        return new SFFFlowgram(id,basecalls, qualities,
                usedFlowValues, qualityRange, adapterRange);
    }

    private String parseIdFrom(Scanner scanner){
        String line = scanner.nextLine();

        Matcher idMatcher = ID_PATTERN.matcher(line);
        while(!idMatcher.find()){
            line = scanner.nextLine();
            idMatcher = ID_PATTERN.matcher(line);
        }
        return idMatcher.group(1);
    }
    
    /**
     * @param nextLine
     * @return
     */
    private QualitySequence parseQualities(String nextLine, int numOfQualities) {
        if(nextLine.startsWith("Quality Scores:")){
            Scanner scanner2 = new Scanner(nextLine);
            scanner2.next(); //Quality
            scanner2.next(); //Scores:
            List<PhredQuality> qualitiesList = new ArrayList<PhredQuality>(numOfQualities);
            while(scanner2.hasNextByte()){
                qualitiesList.add(PhredQuality.valueOf(scanner2.nextByte()));
            }
            scanner2.close();
            return new EncodedQualitySequence(
                    RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE, qualitiesList);
        }
        throw new IllegalStateException("could not parse qualities from "+ nextLine);
    }
    /**
     * @param nextLine
     * @return
     */
    private NucleotideSequence parseBasecalls(String line) {
        Matcher basesMatcher = BASECALLS_PATTERN.matcher(line);
        if(basesMatcher.find()){
           return new DefaultNucleotideSequence(basesMatcher.group(1).trim());
        }
        throw new IllegalStateException("could not parse basecalls from line: "+ line   );
    }
    /**
     * @param scanner
     * @param flows
     * @return
     */
    private List<Short> parseUsedFlows(String usedFlowLine, short[] flows) {
        List<Short> usedFlowValues= new ArrayList<Short>(flows.length);
        Scanner scanner = new Scanner(usedFlowLine);
        scanner.next(); //Flow
        scanner.next(); //Indexes:
        int previousIndex=0;
        while(scanner.hasNextInt()){
            int index = scanner.nextInt();
            if(index > previousIndex){
                usedFlowValues.add(flows[index-1]);
                previousIndex = index;
            }
        }
        scanner.close();
        return usedFlowValues;
    }
    
    /**
     * @param scanner
     * @return
     */
    private short[] parseAllFlows(Scanner scanner, int numFlows) {
        ShortBuffer flows = ShortBuffer.allocate(numFlows);
        String line = scanner.nextLine();
        while(!line.startsWith("Flowgram:")){
            line =scanner.nextLine();
        }
        Scanner scanner2 = new Scanner(line);
        scanner2.next(); // Flowgram:
        while(scanner2.hasNext()){
            flows.put(SFFUtil.parseSffInfoEncodedFlowgram(scanner2.next()));
        }
        scanner2.close();
        return flows.array();
    }
    /**
     * @param scanner
     * @return
     */
    private Range parseAdapterClip(Scanner scanner) {
        String line = scanner.nextLine();

        Matcher clipLeftMatcher = ADAPTER_LEFT_PATTERN.matcher(line);
        while(!clipLeftMatcher.find()){
            line = scanner.nextLine();
            clipLeftMatcher = ADAPTER_LEFT_PATTERN.matcher(line);
        }
        int left=Integer.parseInt(clipLeftMatcher.group(1));
        if(left==0){
            return Range.buildRange(CoordinateSystem.RESIDUE_BASED, 0,0);
        }
        line =scanner.nextLine();
        Matcher clipRightMatcher = ADAPTER_RIGHT_PATTERN.matcher(line);
        clipRightMatcher.find();
        int right=Integer.parseInt(clipRightMatcher.group(1));
        return Range.buildRange(CoordinateSystem.RESIDUE_BASED,left,right);
    }
    /**
     * @param scanner
     * @return
     */
    private Range parseQualityClip(Scanner scanner) {
        String line = scanner.nextLine();        
        Matcher qualClipLeftMatcher = QUAL_LEFT_PATTERN.matcher(line);
        while(!qualClipLeftMatcher.find()){
            line = scanner.nextLine();  
            qualClipLeftMatcher = QUAL_LEFT_PATTERN.matcher(line);
        }
        int left=Integer.parseInt(qualClipLeftMatcher.group(1));
        line =scanner.nextLine();
        Matcher qualClipRightMatcher = QUAL_RIGHT_PATTERN.matcher(line);
        qualClipRightMatcher.find();
        int right=Integer.parseInt(qualClipRightMatcher.group(1));
        return Range.buildRange(CoordinateSystem.RESIDUE_BASED,left,right);
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
    public CloseableIterator<SFFFlowgram> iterator() {
        try {
            return new SffIterator();
        } catch (DataStoreException e) {
           throw new IllegalStateException("error creating iterator", e);
        }
    }

    private class SffIterator implements CloseableIterator<SFFFlowgram>{
        private final Scanner scanner;
        private final Process process;
        private Object endOfStream = new Object();
        private Object next;
        SffIterator() throws DataStoreException{
            ProcessBuilder builder = new ProcessBuilder(
                    pathToSffInfo,
                    "-n",
                    sffFile.getAbsolutePath()
                    
            );
            
            try {
                process = builder.start();
                
                InputStream in = new BufferedInputStream(process.getInputStream());
                scanner = new Scanner(in);
                updateIterator();
            } catch (Exception e) {
                throw new DataStoreException("could not get id iterator", e);
            }
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public boolean hasNext() {
            return endOfStream != next;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public SFFFlowgram next() {
            if(!hasNext()){
                throw new NoSuchElementException("iterator does not have any flowgrams left to iterate");
            }
            SFFFlowgram ret = (SFFFlowgram)next;
            updateIterator();
            return ret;
        }

        private void updateIterator(){
            try{
                next = parseSingleSffRecordFrom(scanner);
            }catch(Exception e){                
                IOUtil.closeAndIgnoreErrors(this);
            }
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void remove() {
            
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            next= endOfStream;
            scanner.close();
            process.destroy();
            
        }
        
    }
    private class IdIterator implements CloseableIterator<String>{
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
        public String next() {
            
            String id= scanner.next();
            if(!scanner.hasNext()){
                IOUtil.closeAndIgnoreErrors(this);
            }
            return id;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void remove() {
            
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            scanner.close();
            process.destroy();
            
        }
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return false;
    }
}
