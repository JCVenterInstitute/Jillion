/*
 * Created on Nov 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.IOUtil;
import org.jcvi.sequence.SequenceDirection;
import org.jcvi.trace.sanger.SangerTraceCodec;
import org.jcvi.trace.sanger.phd.DefaultPhd;
import org.jcvi.trace.sanger.phd.Phd;

public class AcePhdFolderDataStore implements AceFileVisitor,DataStore<Phd>{

    
    private Map<String, Phd> map;
    private StringBuilder currentBasecalls;
    private String currentReadId;
    private boolean initialized = false;
    private boolean closed = false;
    private Map<String, SequenceDirection> directionMap;
    private final File phdDir;
    private final SangerTraceCodec<Phd> codec;
    
    /**
     * @param phdDir
     */
    public AcePhdFolderDataStore(File phdDir, SangerTraceCodec<Phd> codec) {
        if(!phdDir.isDirectory()){
            throw new IllegalArgumentException("phdDir must be a directory");
        }
        if(codec ==null){
            throw new NullPointerException("phd codec can not be null");
        }
        this.phdDir=phdDir;
        this.codec = codec;
    }
    private void throwExceptionIfAlreadyInitialized(){
        if(initialized){
            throw new IllegalStateException("already initialized");
        }
    }
    private void throwExceptionIfNotYetInitialized(){
        if(!initialized){
            throw new IllegalStateException("not initialized");
        }
    }
    private void throwExceptionIfAlreadyClosed(){
        if(closed){
            throw new IllegalStateException("already closed");
        }
    }
    private void checkInitializedAndOpen(){
        throwExceptionIfNotYetInitialized();
        throwExceptionIfAlreadyClosed();
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        checkInitializedAndOpen();        
        return map.containsKey(id);
    }

    @Override
    public Phd get(String id) throws DataStoreException {
        checkInitializedAndOpen();
        return map.get(id);
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        checkInitializedAndOpen();
        return map.keySet().iterator();
    }

    @Override
    public int size() throws DataStoreException {
        checkInitializedAndOpen();
        return map.size();
    }

    @Override
    public void close() throws IOException {
        throwExceptionIfNotYetInitialized();
        closed =true;
        
    }

    @Override
    public Iterator<Phd> iterator() {
        checkInitializedAndOpen();
        return new DataStoreIterator<Phd>(this);
    }

    @Override
    public void visitAssembledFromLine(String readId, SequenceDirection dir,
            int gappedStartOffset) {
        throwExceptionIfAlreadyInitialized();
        directionMap.put(readId, dir);
    }

    @Override
    public void visitBaseSegment(Range gappedConsensusRange, String readId) { 
        throwExceptionIfAlreadyInitialized();
    }

    @Override
    public void visitBasesLine(String bases) {
        throwExceptionIfAlreadyInitialized();
        if(currentReadId!=null){
            currentBasecalls.append(bases);
        }
    }

    @Override
    public void visitConsensusQualities() {
        throwExceptionIfAlreadyInitialized();
    }

    @Override
    public void visitContigHeader(String contigId, int numberOfBases,
            int numberOfReads, int numberOfBaseSegments,
            boolean reverseComplimented) { 
        throwExceptionIfAlreadyInitialized();
        directionMap = new HashMap<String, SequenceDirection>(numberOfReads, 1F);
    }

    @Override
    public void visitHeader(int numberOfContigs, int totalNumberOfReads) {
        throwExceptionIfAlreadyInitialized();
        map = new HashMap<String, Phd>(totalNumberOfReads, 1F);
    }

    @Override
    public void visitQualityLine(int clearLeft, int clearRight, int alignLeft,
            int alignRight) {  
        throwExceptionIfAlreadyInitialized();
    }

    @Override
    public void visitReadHeader(String readId, int gappedLength) {
        throwExceptionIfAlreadyInitialized();
        currentReadId = readId;
        currentBasecalls = new StringBuilder();
        
    }

    @Override
    public void visitTraceDescriptionLine(String traceName, String phdName,
            Date date) {
        throwExceptionIfAlreadyInitialized();
        
        List<NucleotideGlyph> glyphs = NucleotideGlyph.getGlyphsFor(
                currentBasecalls.toString().replaceAll("\\*", ""));
        if(directionMap.get(currentReadId) == SequenceDirection.REVERSE){

            glyphs = NucleotideGlyph.reverseCompliment(glyphs);
        }
       
        
        InputStream phdStream=null;
        try {
            phdStream = new FileInputStream(phdDir+File.separator+phdName);
            Phd originalPhd =codec.decode(phdStream);

            map.put(currentReadId, new DefaultPhd(
                    new DefaultNucleotideEncodedGlyphs(glyphs),
                    originalPhd.getQualities(),
                    originalPhd.getPeaks(),
                    originalPhd.getComments(),
                    originalPhd.getTags()));
            currentReadId=null;
        } catch (Exception e) {
            throw new IllegalStateException("could not parse phd file", e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(phdStream);
        }
        
        
    }

    @Override
    public void visitLine(String line) {
        throwExceptionIfAlreadyInitialized();
    }

    @Override
    public void visitEndOfFile() {
        throwExceptionIfAlreadyInitialized();
        initialized = true;
        
    }

    @Override
    public void visitFile() { 
        throwExceptionIfAlreadyInitialized();
    }
    @Override
    public void visitBeginConsensusTag(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient) {
        throwExceptionIfAlreadyInitialized();
        
    }
    @Override
    public void visitWholeAssemblyTag(String type, String creator,
            Date creationDate, String data) {
        throwExceptionIfAlreadyInitialized();
        
    }
    @Override
    public void visitConsensusTagComment(String comment) {
        throwExceptionIfAlreadyInitialized();
        
    }
    @Override
    public void visitConsensusTagData(String data) {
        throwExceptionIfAlreadyInitialized();
        
    }
    @Override
    public void visitEndConsensusTag() {
        throwExceptionIfAlreadyInitialized();
        
    }
    @Override
    public void visitReadTag(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient) {
        throwExceptionIfAlreadyInitialized();
        
    }

}
