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
 * Created on Feb 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jcvi.Range;
import org.jcvi.assembly.ace.consed.ConsedUtil;
import org.jcvi.assembly.contig.AbstractContig;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.DefaultNucleotideGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.SequenceDirection;

public final class  DefaultAceContig extends AbstractContig<AcePlacedRead> implements AceContig{

    

    private DefaultAceContig(String id, NucleotideEncodedGlyphs consensus,
            Set<AcePlacedRead> reads, boolean circular) {
        super(id, consensus, reads, circular);
    }

    public static class Builder{
        static int counter=0;
        private static final DefaultNucleotideGlyphCodec DEFAULT_CODEC =DefaultNucleotideGlyphCodec.getInstance();
        private EncodedGlyphs<NucleotideGlyph> fullConsensus;
        private String contigId;
        private boolean circular;
        private Logger logger = Logger.getRootLogger();
        
        private List<DefaultAcePlacedRead.Builder> aceReadBuilders = new ArrayList<DefaultAcePlacedRead.Builder>();
        private int contigLeft= -1;
        private int contigRight = -1;
        
        public Builder(String contigId, String fullConsensus){
            this.fullConsensus = new DefaultEncodedGlyphs<NucleotideGlyph>(
                    DEFAULT_CODEC, 
                    NucleotideGlyph.getGlyphsFor(ConsedUtil.convertAceGapsToContigGaps(fullConsensus)));
            this.contigId = contigId;
        }
        
        public Builder setContigId(String contigId){
            this.contigId = contigId;
            return this;
        }
        public String getContigId() {
            return contigId;
        }

        public Builder logger(Logger logger){
            this.logger = logger;
            return this;
        }
        public int numberOfReads(){
            return aceReadBuilders.size();
        }
        public Builder addRead(String readId, String validBases, int offset,
                SequenceDirection dir, Range clearRange,PhdInfo phdInfo) {
           
            adjustContigLeftAndRight(validBases, offset);
            try{
                DefaultAcePlacedRead.Builder aceReadBuilder = createNewAceReadBuilder(readId, validBases, offset, dir, clearRange,phdInfo);
                
                
                aceReadBuilders.add(aceReadBuilder);
            }catch(Exception e){
                logger.error("could not add read "+ readId, e);               
            }
            return this;
        }
        private DefaultAcePlacedRead.Builder createNewAceReadBuilder(
                String readId, String validBases, int offset,
                SequenceDirection dir, Range clearRange, PhdInfo phdInfo) {
            return new DefaultAcePlacedRead.Builder(
                    fullConsensus,readId,
                    ConsedUtil.convertAceGapsToContigGaps(validBases),
                    offset,dir,clearRange,phdInfo);
        }
        private void adjustContigLeftAndRight(String validBases, int offset) {
            adjustContigLeft(offset);
            adjustContigRight(validBases, offset);
        }
        private void adjustContigRight(String validBases, int offset) {
            final int endOfNewRead = offset+ validBases.length();
            if(endOfNewRead <= fullConsensus.getLength() && (contigRight ==-1 || endOfNewRead > contigRight)){
                contigRight = endOfNewRead ;
            }
        }
        private void adjustContigLeft(int offset) {
            if(contigLeft ==-1 || offset <contigLeft){
                contigLeft = offset;
            }
        }
        public DefaultAceContig build(){
            Set<AcePlacedRead> placedReads = new HashSet<AcePlacedRead>(aceReadBuilders.size());
            
            if(numberOfReads()==0){
                //force empty contig if no reads...
                return new DefaultAceContig(contigId, new DefaultNucleotideEncodedGlyphs(""),placedReads,circular);
            }
            final List<NucleotideGlyph> validConsensusGlyphs = new ArrayList<NucleotideGlyph>(fullConsensus.decode().subList(contigLeft, contigRight));
            NucleotideEncodedGlyphs validConsensus = new DefaultNucleotideEncodedGlyphs(
                    validConsensusGlyphs, Range.buildRange(0, validConsensusGlyphs.size()));
            for(DefaultAcePlacedRead.Builder aceReadBuilder : aceReadBuilders){
                int newOffset = aceReadBuilder.offset() - contigLeft;
                aceReadBuilder.reference(validConsensus,newOffset);
                placedReads.add(aceReadBuilder.build());                
            }
            return new DefaultAceContig(contigId, validConsensus,placedReads,circular);
        }
    }
    
    
}
