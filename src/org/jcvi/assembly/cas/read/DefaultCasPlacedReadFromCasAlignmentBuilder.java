/*
 * Created on Oct 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.Builder;
import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.assembly.cas.alignment.CasAlignmentRegion;
import org.jcvi.assembly.cas.alignment.CasAlignmentRegionType;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.DefaultRead;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;

public class DefaultCasPlacedReadFromCasAlignmentBuilder implements Builder<DefaultCasPlacedRead>{
    private final String readId;
    private long startOffset;
    private long validRangeStart=0;
    private long currentOffset=0;
    private boolean outsideValidRange=true;
    private final List<NucleotideGlyph> allBases;
    private List<NucleotideGlyph> validBases = new ArrayList<NucleotideGlyph>();
    private final SequenceDirection dir;
    private int numberOfGaps=0;
    private long referenceOffset;
    public DefaultCasPlacedReadFromCasAlignmentBuilder(String readId,EncodedGlyphs<NucleotideGlyph> fullRangeSequence, boolean isReversed, long startOffset){
        this.readId = readId;
        this.startOffset = startOffset;
        this.referenceOffset = startOffset;
        if(fullRangeSequence ==null){
            throw new NullPointerException("null fullRangeSequence for id "+ readId);
        }
        if(isReversed){
            allBases = NucleotideGlyph.reverseCompliment(fullRangeSequence.decode());
        }
        else{
            allBases = fullRangeSequence.decode();
        }
        dir = isReversed? SequenceDirection.REVERSE: SequenceDirection.FORWARD;
       // dir= SequenceDirection.FORWARD;
        
    }
    public DefaultCasPlacedReadFromCasAlignmentBuilder startOfset(long newStartOffset){
        this.startOffset = newStartOffset;
        
        return this;
    }
    public long startOffset(){
        return startOffset;
    }
    public DefaultCasPlacedReadFromCasAlignmentBuilder addAlignmentRegions(List<CasAlignmentRegion> regions,EncodedGlyphs<NucleotideGlyph> referenceBases){
        
        for(CasAlignmentRegion region : regions){
            addAlignmentRegion(region,referenceBases);
        }
        //validBases = NucleotideGlyph.convertToUngapped(validBases);
        return this;
    }
    private void addAlignmentRegion(CasAlignmentRegion region,EncodedGlyphs<NucleotideGlyph> referenceBases){
        CasAlignmentRegionType type =region.getType();
        
        if(outsideValidRange){
            if(type ==CasAlignmentRegionType.INSERT){
                validRangeStart+=region.getLength();
             
              
                currentOffset+=region.getLength();
                return;
            }           
            outsideValidRange=false;
        }
        
        
        for(long i=0; i< region.getLength();i++){
            if(type != CasAlignmentRegionType.INSERT){
                
                while(referenceOffset < referenceBases.getLength() && referenceBases.get((int)(referenceOffset)).isGap()){
                    validBases.add(NucleotideGlyph.Gap);
                    referenceOffset++;
                    numberOfGaps++;
                }
            }
            if(type == CasAlignmentRegionType.DELETION){
                validBases.add(NucleotideGlyph.Gap);
                numberOfGaps++;
                referenceOffset++;
            }
            else{      
                validBases.add(allBases.get((int)(currentOffset+i)));
                
                referenceOffset++;
            }
            
        }
        if(type != CasAlignmentRegionType.DELETION){
            currentOffset+=region.getLength();
        }
        //referenceOffset +=region.getLength();
    }
  
    public String validBases(){
        return NucleotideGlyph.convertToString(validBases);
    }
    @Override
    public DefaultCasPlacedRead build() {
        Range validRange = Range.buildRangeOfLength(0, validBases.size()-numberOfGaps).shiftRight(validRangeStart).convertRange(CoordinateSystem.RESIDUE_BASED);
        if(dir==SequenceDirection.REVERSE){
            validRange = AssemblyUtil.reverseComplimentValidRange(validRange, allBases.size());
        }
        Read<NucleotideEncodedGlyphs> read = new DefaultRead(readId,
                        new DefaultNucleotideEncodedGlyphs(validBases,validRange));
        return new DefaultCasPlacedRead(read, startOffset, validRange, dir);
    }

}
