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

package org.jcvi.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.assembly.ace.consed.ConsedUtil;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.DefaultReferencedEncodedNucleotideGlyph;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.DefaultQualityEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityEncodedGlyphs;
import org.jcvi.sequence.SequenceDirection;
import org.jcvi.trace.sanger.phd.ArtificialPhd;
import org.jcvi.trace.sanger.phd.Phd;
import org.jcvi.trace.sanger.phd.PhdDataStore;
import org.jcvi.trace.sanger.phd.PhdDataStoreAdapter;
import org.jcvi.util.CloseableIterator;

/**
 * {@code HiLowAceContigPhdDatastore} is a PhdDataStore
 * that only sets the quality values for each basecall
 * to either a high quality or a low quality. The threshold
 * for determining if the value is high or low depends on if the 
 * provided basecall in the ace file is upper or lower case.
 * This implementation is helpful if only approximate quality 
 * values are needed and speed and memory usage demands
 * that the actual phd file can not be parsed.
 * @author dkatzel
 */
public class HiLowAceContigPhdDatastore implements PhdDataStore{
    static final PhredQuality DEFAULT_LOW_QUALITY = PhredQuality.valueOf(15);
    static final PhredQuality DEFAULT_HIGH_QUALITY = AceFileUtil.ACE_DEFAULT_HIGH_QUALITY_THRESHOLD;
    private final PhdDataStore delegate;
    private Map<String, Phd> phds;
    public HiLowAceContigPhdDatastore(File aceContigFile, final String contigId) throws IOException{
        this(aceContigFile,contigId,DEFAULT_LOW_QUALITY,DEFAULT_HIGH_QUALITY);
    }
    public HiLowAceContigPhdDatastore(File aceContigFile, final String contigId, 
            final PhredQuality lowQuality, final PhredQuality highQuality) throws IOException{
        AbstractAceFileVisitor visitor = new AbstractAceFileVisitor() {
            boolean contigOfInterest=false;
            NucleotideEncodedGlyphs consensusBasecalls;
            List<PhredQuality> currentHiLowQualities;
            @Override
            protected void visitNewContig(String aceContigId, String consensus) {
                if(contigOfInterest){
                    consensusBasecalls = new DefaultNucleotideEncodedGlyphs(
                        ConsedUtil.convertAceGapsToContigGaps(consensus));
                }                
            }
            
            /**
            * {@inheritDoc}
            */
            @Override
            public boolean visitEndOfContig() {
                //keep parsing until we finish 
                //our contig of interest
                return !contigOfInterest;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public synchronized void visitContigHeader(String aceContigId,
                    int numberOfBases, int numberOfReads,
                    int numberOfBaseSegments, boolean reverseComplimented) {
                contigOfInterest =aceContigId.equals(contigId);
                if(contigOfInterest){
                    phds = new HashMap<String, Phd>(numberOfReads);
                }
                super.visitContigHeader(aceContigId, numberOfBases, numberOfReads,
                        numberOfBaseSegments, reverseComplimented);
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public synchronized void visitReadHeader(String readId,
                    int gappedLength) {
                if(contigOfInterest){
                    currentHiLowQualities = new ArrayList<PhredQuality>(gappedLength);
                }
                super.visitReadHeader(readId, gappedLength);
            }
            /**
            * {@inheritDoc}
            */
            @Override
            public synchronized void visitBasesLine(String bases) {
                if(contigOfInterest && currentHiLowQualities !=null){
                    currentHiLowQualities.addAll(parseQualities(bases));
                }
                super.visitBasesLine(bases);
            }

            private List<PhredQuality> parseQualities(String bases){
                List<PhredQuality> qualities = new ArrayList<PhredQuality>(bases.length());
                String gappedBases =ConsedUtil.convertAceGapsToContigGaps(bases);
                char[] chars = gappedBases.toCharArray();
                for(int i=0; i<chars.length; i++){
                    if(chars[i] =='-'){
                        continue;
                    }
                    if(Character.isUpperCase(chars[i])){
                        qualities.add(highQuality);
                    }else{
                        qualities.add(lowQuality);
                    }
                }
                return qualities;
            }
            @Override
            protected void visitAceRead(String readId, String validBasecalls,
                    int offset, SequenceDirection dir, Range validRange,
                    PhdInfo phdInfo, int ungappedFullLength) {
                if(contigOfInterest){
                    NucleotideEncodedGlyphs fullLengthBasecalls = new DefaultNucleotideEncodedGlyphs(
                                            ConsedUtil.convertAceGapsToContigGaps(getCurrentFullLengthBasecalls())
                                                                .replaceAll("-", ""));
                    
                    if(dir==SequenceDirection.REVERSE){
                        Collections.reverse(currentHiLowQualities);
                        fullLengthBasecalls = new DefaultNucleotideEncodedGlyphs(NucleotideGlyph.reverseCompliment(fullLengthBasecalls.decode()));
                    }
                    QualityEncodedGlyphs qualities = new DefaultQualityEncodedGlyphs(
                                                RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                                                currentHiLowQualities); 
                    
                     
                     Phd phd = new ArtificialPhd(readId, 
                             fullLengthBasecalls,
                                         qualities,19);
                     phds.put(readId,phd);
                     currentHiLowQualities=null;
                }
            }
        };
        
        AceFileParser.parseAceFile(aceContigFile, visitor);
        delegate = new PhdDataStoreAdapter(new SimpleDataStore<Phd>(phds));
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return delegate.getIds();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Phd get(String id) throws DataStoreException {
        return delegate.get(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        return delegate.contains(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int size() throws DataStoreException {
        return delegate.size();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return delegate.isClosed();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        delegate.close();
        phds.clear();
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<Phd> iterator() {
        return delegate.iterator();
    }
    
    public static void main(String[] args) throws IOException, DataStoreException{
        File aceFile = new File("/usr/local/projects/GSTEC/GSTEC002/CLOSURE/CONSED/stec002_COMBINED/edit_dir/stec002_COMBINED.merged.ace.39");
        String contigId = "1127147154511";
        HiLowAceContigPhdDatastore phdDatastore = new HiLowAceContigPhdDatastore(aceFile, contigId,
                PhredQuality.valueOf(15), PhredQuality.valueOf(30));
        //should have 526 or 3779
        System.out.println("# reads = "+phdDatastore.size());
        Phd phd = phdDatastore.get("GON1IHO01ALDWA");
        System.out.println(phd.getBasecalls().decode());
        System.out.println(phd.getQualities().decode());
        phdDatastore.close();
    }
    
}
