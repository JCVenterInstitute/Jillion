/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.orf;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.Frame;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.Codon;
import org.jcvi.jillion.core.residue.aa.IupacTranslationTables;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.core.residue.aa.TranslationTable;
import org.jcvi.jillion.core.residue.aa.TranslationVisitor;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class OrfFinder {
    
    public enum FinderOptions{
        SEARCH_FORWARD,
        SEARCH_REVERSE
    }
    
    public List<Orf> find(NucleotideSequence seq){
        return find(seq, IupacTranslationTables.STANDARD,FinderOptions.SEARCH_FORWARD, FinderOptions.SEARCH_REVERSE);
    }
    public List<Orf> find(NucleotideSequence seq, TranslationTable translationTable, FinderOptions...finderOptions){
        List<Orf> orfs = new ArrayList<>();
        
        Set<FinderOptions> options = EnumSet.noneOf(FinderOptions.class);
        for(FinderOptions o : finderOptions){
            options.add(o);
        }
        
        if(options.contains(FinderOptions.SEARCH_FORWARD)){
            for(Frame f : Frame.forwardFrames()){
                OrfVisitor visitor = new OrfVisitor(f);
                translationTable.translate(seq, f, visitor);
                visitor.getOrf().ifPresent(orf -> orfs.add(orf));
            }
        }
        if(options.contains(FinderOptions.SEARCH_REVERSE)){
            NucleotideSequence reverseSeq = seq.toBuilder().reverseComplement().build();
            for(Frame f : Frame.reverseFrames()){
                OrfVisitor visitor = new OrfVisitor(f);
                translationTable.translate(reverseSeq, f.getOppositeFrame(), visitor);
                visitor.getOrf().ifPresent(orf -> orfs.add(orf));
            }
        }
        
        return orfs;
    }
    
    private static class OrfVisitor implements TranslationVisitor{

        ProteinSequenceBuilder builder = new ProteinSequenceBuilder();
        Frame frame;
        
        public OrfVisitor(Frame frame) {
            this.frame = frame;
        }

        long startCoord =-1; long stopCoord =-1;
        boolean hasStart=false;
        boolean hasStop=false;
        
        Orf orf=null;
        @Override
        public void visitCodon(long nucleotideStartCoordinate, long nucleotideEndCoordinate, Codon codon) {
        	if(hasStart) {
        		builder.append(codon.getAminoAcid());
        	}
        }
        
        

        @Override
        public FoundStartResult foundStart(long nucleotideStartCoordinate,
                long nucleotideEndCoordinate, Codon codon) {
            if(hasStart){
                //already seen a start
                builder.append(codon.getAminoAcid());
            }else{
            hasStart=true;
            startCoord = nucleotideStartCoordinate;
          //hardcode an M if this is our first start
            //which may 
            //not be the amino acid returned by 
            //#getAminoAcid() depending on the translation table
            builder.append(AminoAcid.Methionine);
            }
            return FoundStartResult.CONTINUE;
        }

        @Override
        public FoundStopResult foundStop(long nucleotideStartCoordinate,
                long nucleotideEndCoordinate, Codon codon) {
            hasStop = true;
            stopCoord = nucleotideEndCoordinate;
            builder.append(codon.getAminoAcid());
            return FoundStopResult.STOP;
        }

        @Override
        public void end() {
            if(hasStart && hasStop){
                orf = new Orf(frame, builder.build(), Range.of(startCoord, stopCoord));
            }
            
        }
        
        public Optional<Orf> getOrf(){
            return Optional.ofNullable(orf);
        }
        
    }
}
